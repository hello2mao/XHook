#define _GNU_SOURCE
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <dlfcn.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <sys/select.h>
#include <string.h>
#include <termios.h>
#include <pthread.h>
#include <sys/epoll.h>
#include <jni.h>
#include <stdlib.h>

#include "util.h"
#include "hook.h"

/**
 * 刷新缓存
 * #define __ARM_NR_BASE (__NR_SYSCALL_BASE+0x0f0000)    
 * #define __ARM_NR_cacheflush (__ARM_NR_BASE+2) 
 * 用到了编号为0xf0002的系统调用，这个系统调用是私有的
 * 0xf0002刚好对应到定义为“__ARM_NR_cacheflush”的系统调用。
 *
 * 这个系统调用接受三个参数，分别用寄存器r0、r1和r2传递进来。
 * 第一个参数（r0）表示要刷缓存的指令的起始地址；
 * 第二个参数（r1）表示指令的结束地址；
 * 第三个参数必须为0。
 * “svc”指令用来实现Arm下的软终端，从而触发系统调用，而具体的系统调用号保存在寄存器r7中  
 */
// BUG FIX: Error: cannot honor width suffix -- `mov r2,#0x0'
// http://stackoverflow.com/questions/30980160/
//      arm-assembly-cannot-use-immediate-values-and-adds-adcs-together
void inline hook_cacheflush(unsigned int begin, unsigned int end) {
    const int syscall = 0xf0002;
    __asm __volatile (
        "mov     r0, %0\n"
        "mov     r1, %1\n"
        "mov     r7, %2\n"
        "movs    r2, #0x0\n"
        "svc     0x00000000\n"
        :
        :    "r" (begin), "r" (end), "r" (syscall)
        :    "r0", "r1", "r7"
        );
}

int hook_direct(struct hook_t *h, unsigned int addr, void *hookf) {
    int i;

    LOGD("addr  = %x\n", addr);
    LOGD("hookf = %lx\n", (unsigned long)hookf);

    if ((addr % 4 == 0 && (unsigned int)hookf % 4 != 0)
        || (addr % 4 != 0 && (unsigned int)hookf % 4 == 0)) {
        LOGD("addr 0x%x and hook 0x%lx\n don't match!\n", addr, (unsigned long) hookf);
    }

    //LOGD("ARM\n");
    h->thumb = 0;
    h->patch = (unsigned int)hookf;
    h->orig = addr;
    LOGD("orig = %x\n", h->orig);
    h->jump[0] = 0xe59ff000; // LDR pc, [pc, #0]
    h->jump[1] = h->patch;
    h->jump[2] = h->patch;
    for (i = 0; i < 3; i++) {
        h->store[i] = ((int *) h->orig)[i];
    }
    for (i = 0; i < 3; i++) {
        ((int *) h->orig)[i] = h->jump[i];
    }

    hook_cacheflush((unsigned int)h->orig, (unsigned int)h->orig+sizeof(h->jumpt));
    return 1;
}

// 具体修改函数来完成hook的目的
int hook(struct hook_t *h, int pid, char *libname, char *funcname, void *hook_arm,
         void *hook_thumb) {
    unsigned long int addr;
    int i;

    // 通过find_name()函数找到具体要hook函数的地址
    if (find_name(pid, funcname, libname, &addr) < 0) {
        LOGE("can't find: %s\n", funcname);
        return 0;
    }
    LOGD("hooking func %s, addr in %s = 0x%lx ", funcname, libname, addr);
    strncpy(h->name, funcname, sizeof(h->name) - 1);
    /* 使用Arm指令集的情况 */
    if (addr % 4 == 0) {
        LOGD("Using ARM, hook func addr in libxhooknative.so = 0x%lx\n", (unsigned long)hook_arm);
        // 表明要hook函数使用的指令集，1为Thumb，0为Arm
        h->thumb = 0;
        // hook的函数地址
        h->patch = (unsigned int)hook_arm;
        // 被hook的函数地址
        h->orig = addr;
        /******************************************
         * 要修改的hook指令（Arm）
         * jump[0~3]实际上保存的是跳转到hook函数的指令
         *****************************************/
        h->jump[0] = 0xe59ff000; // LDR pc, [pc, #0]
        h->jump[1] = h->patch;
        h->jump[2] = h->patch;
        // 将被hook函数的前3个4字节保存下来，方便以后恢复
        for (i = 0; i < 3; i++) {
            h->store[i] = ((int *)h->orig)[i];
        }
        /****************************************
         * 将跳转指令写到被hook函数的前12字节
         * 这样，当要调用被hook函数的时候，实际执行的指令就是跳转到hook函数
         ****************************************/
        for (i = 0; i < 3; i++) {
            ((int *) h->orig)[i] = h->jump[i];
        }
    } else { /* 使用Thumb指令集的情况 */
        if ((unsigned long int)hook_thumb % 4 == 0) {
            // TODO:bug need fix, this may not cause crash --MHB
            LOGE("warning: hook is not thumb 0x%lx ==\n", (unsigned long) hook_thumb);
        }
        h->thumb = 1;
        LOGD("Using THUMB, hook func addr in libxhooknative.so = 0x%lx\n", (unsigned long)hook_thumb);
        h->patch = (unsigned int)hook_thumb;
        h->orig = addr;
        // 和Arm的处理不同，这里是通过pop指令来修改PC寄存器的
        h->jumpt[1] = 0xb4;
        h->jumpt[0] = 0x60; // push {r5,r6}
        h->jumpt[3] = 0xa5;
        h->jumpt[2] = 0x03; // add r5, pc, #12
        h->jumpt[5] = 0x68;
        h->jumpt[4] = 0x2d; // ldr r5, [r5]
        h->jumpt[7] = 0xb0;
        h->jumpt[6] = 0x02; // add sp,sp,#8
        h->jumpt[9] = 0xb4;
        h->jumpt[8] = 0x20; // push {r5}
        h->jumpt[11] = 0xb0;
        h->jumpt[10] = 0x81; // sub sp,sp,#4
        h->jumpt[13] = 0xbd;
        h->jumpt[12] = 0x20; // pop {r5, pc}
        h->jumpt[15] = 0x46;
        h->jumpt[14] = 0xaf; // mov pc, r5 ; just to pad to 4 byte boundary
        memcpy(&h->jumpt[16], (unsigned char*)&h->patch, sizeof(unsigned int));
        unsigned int orig = addr - 1; // sub 1 to get real address
        for (i = 0; i < 20; i++) {
            h->storet[i] = ((unsigned char*)orig)[i];
            //LOGD("%0.2x ", h->storet[i]);
        }
        //LOGD("\n");
        for (i = 0; i < 20; i++) {
            ((unsigned char*)orig)[i] = h->jumpt[i];
            //LOGD("%0.2x ", ((unsigned char*)orig)[i]);
        }
    }
    /************************************************************
     * 经过上面的处理，被hook函数的前几条指令已经被修改成跳转到hook函数的指令了，
     * 如果接下来被hook的函数被调用到了，实际上就会跳转到指定的hook函数上去。
     *
     * 现代的处理器都有指令缓存，用来提高执行效率，虽然前面的操作修改了内存中的指令，
     * 但有可能被修改的指令之前已经被缓存起来了，再执行的时候还是优先执行缓存中的指令，使得修改的指令得不到执行。
     * 关于这个问题，解决的方法是刷新缓存。实际的做法是触发一个影藏的系统调用
     ************************************************************/
    // 刷新缓存
    hook_cacheflush((unsigned int)h->orig, (unsigned int)h->orig + sizeof(h->jumpt));
    return 1;
}

// 把前面hook()函数中保存在storet或者store中的被hook函数的原始指令写回去。
// 这样接下来再调用原始函数的话就能完成其原有的功能
void hook_precall(struct hook_t *h) {
    int i;

    if (h->thumb) {
        unsigned int orig = h->orig - 1;
        for (i = 0; i < 20; i++) {
            ((unsigned char*)orig)[i] = h->storet[i];
        }
    } else {
        for (i = 0; i < 3; i++) {
            ((int *) h->orig)[i] = h->store[i];
        }
    }
    hook_cacheflush((unsigned int)h->orig, (unsigned int)h->orig + sizeof(h->jumpt));
}

// 把保存在jumpt或者jump中的跳转指令写到被hook函数开头，从而实现hook的动作。
// 这之后再调用被hook函数，就会跳转到hook函数中去
void hook_postcall(struct hook_t *h) {
    int i;

    if (h->thumb) {
        unsigned int orig = h->orig - 1;
        for (i = 0; i < 20; i++) {
            ((unsigned char *) orig)[i] = h->jumpt[i];
        }
    } else {
        for (i = 0; i < 3; i++) {
            ((int *) h->orig)[i] = h->jump[i];
        }
    }
    hook_cacheflush((unsigned int)h->orig, (unsigned int)h->orig + sizeof(h->jumpt));
}

void unhook(struct hook_t *h) {
    LOGD("unhooking %s = %x  hook = %x ", h->name, h->orig, h->patch);
    hook_precall(h);
}

/*
 *  workaround for blocked socket API when process does not have network
 *  permissions
 *
 *  this code simply opens a pseudo terminal (pty) which gives us a
 *  file descriptor. the pty then can be used by another process to
 *  communicate with our instrumentation code. an example program
 *  would be a simple socket-to-pty-bridge
 *  
 *  this function just creates and configures the pty
 *  communication (read, write, poll/select) has to be implemented by hand
 *
 */
int start_coms(int *coms, char *ptsn) {
    if (!coms) {
        LOGE("coms == null!\n");
        return 0;
    }

    *coms = open("/dev/ptmx", O_RDWR|O_NOCTTY);
    if (*coms <= 0) {
        LOGE("posix_openpt failed\n");
        return 0;
    }
    //else
    //    LOGD("pty created\n")
    if (unlockpt(*coms) < 0) {
        LOGD("unlockpt failed\n");
        return 0;
    }

    if (ptsn) {
        strcpy(ptsn, (char *) ptsname(*coms));
    }

    struct termios  ios;
    tcgetattr(*coms, &ios);
    ios.c_lflag = 0;  // disable ECHO, ICANON, etc...
    tcsetattr(*coms, TCSANOW, &ios);

    return 1;
}
