#include "../config.h"

struct hook_t {
    unsigned int jump[3];     /* 要修改的hook指令（Arm） */
    unsigned int store[3];    /* 被修改的原指令（Arm） */
    unsigned char jumpt[20];  /* 要修改的hook指令（Thumb） */
    unsigned char storet[20]; /* 被修改的源指令（Thumb） */
    unsigned int orig;        /* 被hook的函数地址 */
    unsigned int patch;       /* hook的函数地址 */
    unsigned char thumb;      /* 表明要hook函数使用的指令集，1为Thumb，0为Arm */
    unsigned char name[128];  /* 被hook的函数名 */
    void *data;
};

int start_coms(int *coms, char *ptsn);

void hook_cacheflush(unsigned int begin, unsigned int end);
void hook_precall(struct hook_t *h);
void hook_postcall(struct hook_t *h);
int hook(struct hook_t *h, int pid, char *libname, char *funcname, void *hook_arm, void *hook_thumb);
void unhook(struct hook_t *h);
