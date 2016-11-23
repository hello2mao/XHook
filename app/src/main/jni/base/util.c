#define _XOPEN_SOURCE 500
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <fcntl.h>
#include <sys/ptrace.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/stat.h>
#include <dlfcn.h>
#include <elf.h>
#include <unistd.h>
#include <errno.h>       
#include <sys/mman.h>
#include <termios.h>
#include <sys/ioctl.h>

#include "hook.h"
#include "util.h"

/* memory map for libraries */
#define MAX_NAME_LEN 256
#define MEMORY_ONLY  "[memory]"
struct mm {
    char name[MAX_NAME_LEN];
    unsigned long start, end;
};

typedef struct symtab *symtab_t;
struct symlist {
    Elf32_Sym *sym;       /* symbols */
    char *str;            /* symbol strings */
    unsigned num;         /* number of symbols */
};
struct symtab {
    struct symlist *st;    /* "static" symbols */
    struct symlist *dyn;   /* dynamic symbols */
};

static void *xmalloc(size_t size) {
    void *p;
    p = malloc(size);
    if (!p) {
        LOGE("Out of memory\n");
        exit(1);
    }
    return p;
}

static int my_pread(int fd, void *buf, size_t count, off_t offset) {
    lseek(fd, offset, SEEK_SET);
    return read(fd, buf, count);
}

static struct symlist* get_syms(int fd, Elf32_Shdr *symh, Elf32_Shdr *strh) {
    struct symlist *sl, *ret;
    int rv;

    ret = NULL;
    sl = (struct symlist *) xmalloc(sizeof(struct symlist));
    sl->str = NULL;
    sl->sym = NULL;

    /* sanity */
    if (symh->sh_size % sizeof(Elf32_Sym)) {
        //printf("elf_error\n");
        goto out;
    }

    /* symbol table */
    sl->num = symh->sh_size / sizeof(Elf32_Sym);
    sl->sym = (Elf32_Sym *) xmalloc(symh->sh_size);
    rv = my_pread(fd, sl->sym, symh->sh_size, symh->sh_offset);
    if (0 > rv) {
        //perror("read");
        goto out;
    }
    if (rv != symh->sh_size) {
        //printf("elf error\n");
        goto out;
    }

    /* string table */
    sl->str = (char *) xmalloc(strh->sh_size);
    rv = my_pread(fd, sl->str, strh->sh_size, strh->sh_offset);
    if (0 > rv) {
        //perror("read");
        goto out;
    }
    if (rv != strh->sh_size) {
        //printf("elf error");
        goto out;
    }

    ret = sl;
out:
    return ret;
}

// 通过解析指定库ELF文件中的“.symtab”或者“.dynsym”节，就可以获得库中所有的符号信息
static int do_load(int fd, symtab_t symtab) {
    int rv;
    size_t size;
    Elf32_Ehdr ehdr;
    Elf32_Shdr *shdr = NULL, *p;
    Elf32_Shdr *dynsymh, *dynstrh;
    Elf32_Shdr *symh, *strh;
    char *shstrtab = NULL;
    int i;
    int ret = -1;

    /* elf header */
    rv = read(fd, &ehdr, sizeof(ehdr));
    if (0 > rv) {
        LOGE("read error\n");
        goto out;
    }
    if (rv != sizeof(ehdr)) {
        LOGE("elf error 1\n");
        goto out;
    }
    if (strncmp(ELFMAG, ehdr.e_ident, SELFMAG)) { /* sanity */
        LOGE("not an elf\n");
        goto out;
    }
    if (sizeof(Elf32_Shdr) != ehdr.e_shentsize) { /* sanity */
        LOGE("elf error 2\n");
        goto out;
    }

    /* section header table */
    size = ehdr.e_shentsize * ehdr.e_shnum;
    shdr = (Elf32_Shdr *) xmalloc(size);
    rv = my_pread(fd, shdr, size, ehdr.e_shoff);
    if (0 > rv) {
        LOGE("read error\n");
        goto out;
    }
    if (rv != size) {
        LOGE("elf error 3 %d %d\n", rv, size);
        goto out;
    }

    /* section header string table */
    size = shdr[ehdr.e_shstrndx].sh_size;
    shstrtab = (char *) xmalloc(size);
    rv = my_pread(fd, shstrtab, size, shdr[ehdr.e_shstrndx].sh_offset);
    if (0 > rv) {
        LOGE("read error\n");
        goto out;
    }
    if (rv != size) {
        LOGE("elf error 4 %d %d\n", rv, size);
        goto out;
    }

    /* symbol table headers */
    symh = dynsymh = NULL;
    strh = dynstrh = NULL;
    for (i = 0, p = shdr; i < ehdr.e_shnum; i++, p++)
        if (SHT_SYMTAB == p->sh_type) {
            if (symh) {
                LOGE("too many symbol tables\n");
                goto out;
            }
            symh = p;
        } else if (SHT_DYNSYM == p->sh_type) {
            if (dynsymh) {
                LOGE("too many symbol tables\n");
                goto out;
            }
            dynsymh = p;
        } else if (SHT_STRTAB == p->sh_type
               && !strncmp(shstrtab+p->sh_name, ".strtab", 7)) {
            if (strh) {
                LOGE("too many string tables\n");
                goto out;
            }
            strh = p;
        } else if (SHT_STRTAB == p->sh_type
               && !strncmp(shstrtab+p->sh_name, ".dynstr", 7)) {
            if (dynstrh) {
                LOGE("too many string tables\n");
                goto out;
            }
            dynstrh = p;
        }
    /* sanity checks */
    if ((!dynsymh && dynstrh) || (dynsymh && !dynstrh)) {
        LOGE("bad dynamic symbol table\n");
        goto out;
    }
    if ((!symh && strh) || (symh && !strh)) {
        LOGE("bad symbol table\n");
        goto out;
    }
    if (!dynsymh && !symh) {
        LOGE("no symbol table\n");
        goto out;
    }

    /* symbol tables */
    if (dynsymh) {
        symtab->dyn = get_syms(fd, dynsymh, dynstrh);
    }
    if (symh) {
        symtab->st = get_syms(fd, symh, strh);
    }
    ret = 0;
    out:
        free(shstrtab);
        free(shdr);
    return ret;
}

static symtab_t load_symtab(char *filename) {
    int fd;
    symtab_t symtab;

    symtab = (symtab_t) xmalloc(sizeof(*symtab));
    memset(symtab, 0, sizeof(*symtab));
    fd = open(filename, O_RDONLY);
    if (0 > fd) {
        LOGE("%s open failed!\n", __func__);
        return NULL;
    }
    // 通过解析指定库ELF文件中的“.symtab”或者“.dynsym”节，就可以获得库中所有的符号信息
    if (0 > do_load(fd, symtab)) {
        LOGE("Error ELF parsing %s\n", filename);
        free(symtab);
        symtab = NULL;
    }
    close(fd);
    return symtab;
}

static int load_memmap(pid_t pid, struct mm *mm, int *nmmp) {
    // modify by mhb, default char raw[80000]
    // TODO:
    char raw[240000]; // increase this if needed for larger "maps"
    char name[MAX_NAME_LEN];
    char *p;
    unsigned long start, end;
    struct mm *m;
    int nmm = 0;
    int fd, rv;
    int i;

    sprintf(raw, "/proc/%d/maps", pid);
    fd = open(raw, O_RDONLY);
    if (0 > fd) {
        LOGE("Can't open %s for reading\n", raw);
        return -1;
    }

    /* Zero to ensure data is null terminated */
    memset(raw, 0, sizeof(raw));

    p = raw;
    while (1) {
        rv = read(fd, p, sizeof(raw) - (p - raw));
        if (0 > rv) {
            LOGE("read error");
            return -1;
        }
        if (0 == rv) {
            break;
        }
        p += rv;
        if (p - raw >= sizeof(raw)) {
            LOGD("Too many memory mapping\n");
            return -1;
        }
    }
    close(fd);

    p = strtok(raw, "\n");
    m = mm;
    while (p) {
        /* parse current map line */
        rv = sscanf(p, "%08lx-%08lx %*s %*s %*s %*s %s\n", &start, &end, name);
        p = strtok(NULL, "\n");
        if (rv == 2) {
            m = &mm[nmm++];
            m->start = start;
            m->end = end;
            strcpy(m->name, MEMORY_ONLY);
            continue;
        }

        /* search backward for other mapping with same name */
        for (i = nmm-1; i >= 0; i--) {
            m = &mm[i];
            if (!strcmp(m->name, name)) {
                break;
            }
        }

        if (i >= 0) {
            if (start < m->start) {
                m->start = start;
            }
            if (end > m->end) {
                m->end = end;
            }
        } else {
            /* new entry */
            m = &mm[nmm++];
            m->start = start;
            m->end = end;
            strcpy(m->name, name);
        }
    }
    *nmmp = nmm;
    return 0;
}

/* Find libc in MM, storing no more than LEN-1 chars of
   its name in NAME and set START to its starting
   address.  If libc cannot be found return -1 and
   leave NAME and START untouched.  Otherwise return 0
   and null-terminated NAME. */
// why libc: may copy from hijiack.c
static int find_libname(char *libn, char *name, int len, unsigned long *start,
                        struct mm *mm, int nmm) {
    int i;
    struct mm *m;
    char *p;
    for (i = 0, m = mm; i < nmm; i++, m++) {
        if (!strcmp(m->name, MEMORY_ONLY)) {
            continue;
        }
        p = strrchr(m->name, '/');
        if (!p) {
            continue;
        }
        p++;
        if (strncmp(libn, p, strlen(libn))) {
            continue;
        }
        p += strlen(libn);

        /* here comes our crude test -> 'libc.so' or 'libc-[0-9]' */
        // TODO: BUG need fix -- mhb
        if (!strncmp("so", p, 2) || 1) { // || (p[0] == '-' && isdigit(p[1])))
            break;
        }
    }
    if (i >= nmm) {
        /* not found */
        return -1;
    }
    *start = m->start;
    strncpy(name, m->name, len);
    if (strlen(m->name) >= len) {
        name[len-1] = '\0';
    }
    // 用mprotect()函数，修改该库内存段的属性，加上可写（PROT_WRITE）属性
    mprotect((void*)m->start, m->end - m->start, PROT_READ|PROT_WRITE|PROT_EXEC);
    return 0;
}

static int lookup2(struct symlist *sl, unsigned char type,
    char *name, unsigned long *val) {
    Elf32_Sym *p;
    int len;
    int i;

    len = strlen(name);
    for (i = 0, p = sl->sym; i < sl->num; i++, p++) {
        //LOGD("name: %s %x\n", sl->str+p->st_name, p->st_value);
        if (!strncmp(sl->str+p->st_name, name, len) && *(sl->str+p->st_name+len) == 0
            && ELF32_ST_TYPE(p->st_info) == type) {
            //if (p->st_value != 0) {
            *val = p->st_value;
            return 0;
            //}
        }
    }
    return -1;
}

static int lookup_sym(symtab_t s, unsigned char type,
       char *name, unsigned long *val) {
    if (s->dyn && !lookup2(s->dyn, type, name, val)) {
        return 0;
    }
    if (s->st && !lookup2(s->st, type, name, val)) {
        return 0;
    }
    return -1;
}

static int lookup_func_sym(symtab_t s, char *name, unsigned long *val) {
    return lookup_sym(s, STT_FUNC, name, val);
}

static void append_hook_info_node(LIB_HOOK_INFO_NODE *root, char *hook_info_name) {
    LIB_HOOK_INFO_NODE *temp, *right;
    temp = (LIB_HOOK_INFO_NODE *) malloc(sizeof(LIB_HOOK_INFO_NODE));
    temp->hook_info_name = hook_info_name;
    right = root;
    while(right->next != NULL) {
        right = right->next;
    }
    right->next = temp;
    right = temp;
    right->next = NULL;
    LOGD("Hook info node: %s", hook_info_name);
}

// 从符号表中，找到指定名称的符号，并返回其数值
static void lookup_hook_infos_v1(struct symlist *sl, unsigned char type, char *condition,
                              LIB_HOOK_INFO_NODE *root) {
    Elf32_Sym *p;
    int i = 0;
    for (i=0, p = sl->sym; i < sl->num; i++, p++) {
        char *sym_name = sl->str + p->st_name;
        if (ELF32_ST_TYPE(p->st_info) == type && !strncmp(sym_name, condition, strlen(condition))) {
            append_hook_info_node(root, sym_name);
        }
    }
}

// 根据condition匹配 -- v1
LIB_HOOK_INFO_NODE *build_hook_info_list_v1(char *filepath, char *condition) {
    LIB_HOOK_INFO_NODE *root = (LIB_HOOK_INFO_NODE *) malloc(sizeof(LIB_HOOK_INFO_NODE));
    if(root == NULL) {
        LOGE("malloc space for build_hook_info_list failed!");
        return root;
    }
    root->hook_info_name = NULL;
    root->next = NULL;
    // 获得libxhooknative.so库中所有的符号信息
    symtab_t s = load_symtab(filepath);
    if (!s) {
        LOGE("cannot read symbol table\n");
        return root;
    }
    // 从符号表中，找到指定名称的符号，并返回其数值，这里的指定名称就是condition*
    // 这里查到的符号值其实就是要找的那个函数/静态变量的起始地址相对于整个库的起始地址的偏移
    if (s->dyn) {
        lookup_hook_infos_v1(s->dyn, STT_OBJECT, condition, root);
    }
    if (s->st) {
        lookup_hook_infos_v1(s->st, STT_OBJECT, condition, root);
    }
    return root;
}

// 从符号表中，找到指定名称的符号，并返回其数值
static void lookup_hook_infos_v2(struct symlist *sl, unsigned char type, char *condition,
                                 LIB_HOOK_INFO_NODE *root) {
    Elf32_Sym *p;
    int i = 0;
    for (i=0, p = sl->sym; i < sl->num; i++, p++) {
        char *sym_name = sl->str + p->st_name;
        if (ELF32_ST_TYPE(p->st_info) == type && !strncmp(sym_name, condition, strlen(condition))) {
            append_hook_info_node(root, sym_name);
            return;
        }
    }
}

// 精准构造 -- v2
LIB_HOOK_INFO_NODE *build_hook_info_list_v2(const char *filepath, char **condition_list) {
    LIB_HOOK_INFO_NODE *root = (LIB_HOOK_INFO_NODE *) malloc(sizeof(LIB_HOOK_INFO_NODE));
    if(root == NULL) {
        LOGE("malloc space for build_hook_info_list failed!");
        return root;
    }
    root->hook_info_name = NULL;
    root->next = NULL;
    // 获得libxhooknative.so库中所有的符号信息
    symtab_t s = load_symtab(filepath);
    if (!s) {
        LOGE("cannot read symbol table\n");
        return root;
    }
    // 从符号表中，找到指定名称的符号，并返回其数值，这里的指定名称就是condition*
    // 这里查到的符号值其实就是要找的那个函数/静态变量的起始地址相对于整个库的起始地址的偏移
    char (*current_condition)[MAX_HOOK_INFO_LEN];
    current_condition = condition_list;
    while (strlen(current_condition) != 0) {
        if (s->dyn) {
            lookup_hook_infos_v2(s->dyn, STT_OBJECT, current_condition, root);
        }
        if (s->st) {
            lookup_hook_infos_v2(s->st, STT_OBJECT, current_condition, root);
        }
        current_condition++;
    }
    return root;
}

// 找到hook函数在内存中的位置
int find_name(pid_t pid, char *name, char *libn, unsigned long *addr) {
    struct mm mm[1000];
    unsigned long libcaddr;
    int nmm;
    char libc[1024];
    symtab_t s;

    // 读取并解析完宿主进程的内存映射信息
    if (0 > load_memmap(pid, mm, &nmm)) {
        LOGE("cannot read memory map\n");
        return -1;
    }

    // 逐项比对宿主进程的内存映射段，如果内存段没有名字就跳过，
    // 如果有名字则从名字字符串后面往前找第一个“/”，并将这个字符之后的子字符串和要查找的库名进行比较。
    // 如果一样的话，证明找到了，退出循环；如果不一样的话，说明没找到就继续找。
    if (0 > find_libname(libn, libc, sizeof(libc), &libcaddr, mm, nmm)) {
        LOGE("cannot find lib: %s\n", libn);
        return -1;
    }
    LOGD("lib: >%s<\n", libc);
    // 符号表解析
    s = load_symtab(libc);
    if (!s) {
        LOGE("cannot read symbol table\n");
        return -1;
    }
    // 从符号表中，找到指定名称的符号，并返回其数值
    if (0 > lookup_func_sym(s, name, addr)) {
        LOGE("cannot find function: %s\n", name);
        return -1;
    }
    // 函数的地址就是库的起始地址，加上函数名符号的值
    *addr += libcaddr;
    return 0;
}

int find_libbase(pid_t pid, char *libn, unsigned long *addr) {
    struct mm mm[1000];
    unsigned long libcaddr;
    int nmm;
    char libc[1024];
    symtab_t s;

    if (0 > load_memmap(pid, mm, &nmm)) {
        LOGE("cannot read memory map\n");
        return -1;
    }
    if (0 > find_libname(libn, libc, sizeof(libc), &libcaddr, mm, nmm)) {
        LOGE("cannot find lib\n");
        return -1;
    }
    *addr = libcaddr;
    return 0;
}
