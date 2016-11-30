#include <stdio.h>
#include <stdlib.h>

#include "dexstuff.h"
#include "dalvik_hook.h"

#include "../config.h"

int dalvik_hook_setup(struct dalvik_hook_t *h, char *cls, char *meth, char *sig, int ns, void *func) {
    if (!h) {
        return 0;
    }

    strcpy(h->clname, cls);
    strncpy(h->clnamep, cls + 1, strlen(cls) - 2);
    strcpy(h->method_name, meth);
    strcpy(h->method_sig, sig);
    h->n_iss = ns;
    h->n_rss = ns;
    h->n_oss = 0;
    h->native_func = func;

    h->sm = 0; // set by hand if needed
    h->af = 0x0100; // native, modify by hand if needed
    h->resolvm = 0; // don't resolve method on-the-fly, change by hand if needed
    h->debug_me = DEBUG;

    return 1;
}

void *dalvik_hook(struct dexstuff_t *dex, struct dalvik_hook_t *h) {
    if (h->debug_me) {
        LOGD("dalvik_hook: try to hook class %s\n", h->clname);
    }

    void *target_cls = dex->dvmFindLoadedClass_fnPtr(h->clname);
    if (!target_cls) {
        LOGE("dvmFindLoadedClass error");
        return (void*)0;
    }
    if (h->debug_me) {
        LOGD("class = 0x%x\n", target_cls);
    }

    // print class in logcat
    if (h->dump && dex && target_cls) {
        dex->dvmDumpClass_fnPtr(target_cls, (void *) 1);
    }



    // 试图在你指定的类中找到你指定名字的那个虚函数。这里所谓的虚函数，指的其实是非静态函数，也就是函数名字前没有static关键字
    h->method = dex->dvmFindVirtualMethodHierByDescriptor_fnPtr(target_cls, h->method_name, h->method_sig);
    if (h->method == 0) {
        // 试图在你指定类中找到你指定名字的那个静态函数
        h->method = dex->dvmFindDirectMethodByDescriptor_fnPtr(target_cls, h->method_name, h->method_sig);
    }

    // constrcutor workaround, see "dalvik_prepare" below
    if (!h->resolvm) {
        h->cls = target_cls;
        h->mid = (void*)h->method;
    }

    if (h->debug_me) {
        LOGD("%s(%s) = 0x%x\n", h->method_name, h->method_sig, h->method);
    }

    if (h->method) {
        h->insns = h->method->insns;

        if (h->debug_me) {
            LOGD("nativeFunc %x\n", h->method->nativeFunc);

            LOGD("insSize = 0x%x  registersSize = 0x%x  outsSize = 0x%x\n", h->method->insSize,
                 h->method->registersSize, h->method->outsSize);
        }

        // 先将那个代表你要hook函数的Method结构体中的一些变量的当前值保存下来，这些值在后面恢复的时候是要用到的
        h->iss = h->method->insSize;
        h->rss = h->method->registersSize;
        h->oss = h->method->outsSize;

        // 修改成一个Native函数，并且指向的是你自己写的Native代码
        h->method->insSize = h->n_iss;
        h->method->registersSize = h->n_rss;
        h->method->outsSize = h->n_oss;

        if (h->debug_me) {
            LOGD("shorty %s\n", h->method->shorty);
            LOGD("name %s\n", h->method->name);
            LOGD("arginfo %x\n", h->method->jniArgInfo);
        }
        h->method->jniArgInfo = 0x80000000; // <--- also important
        if (h->debug_me) {
            LOGD("noref %c\n", h->method->noRef);
            LOGD("access %x\n", h->method->a);
        }
        h->access_flags = h->method->a;
        h->method->a = h->method->a | h->af; // make method native
        if (h->debug_me) {
            LOGD("access %x\n", h->method->a);
        }

        dex->dvmUseJNIBridge_fnPtr(h->method, h->native_func);

        if (h->debug_me) {
            LOGD("patched %s to: 0x%x\n", h->method_name, h->native_func);
        }

        return (void *)1;
    } else {
        if (h->debug_me) {
            LOGD("could NOT patch %s\n", h->method_name);
        }
    }
    return (void *)0;
}

// 在自己写的JNI函数中，完成了一些附加的功能之后,继续调用原来的那个函数
int dalvik_prepare(struct dexstuff_t *dex, struct dalvik_hook_t *h, JNIEnv *env) {

    // this seems to crash when hooking "constructors"

    if (h->resolvm) {
        h->cls = (*env)->FindClass(env, h->clnamep);
        if (h->debug_me) {
            LOGD("cls = 0x%x\n", h->cls);
        }
        if (!h->cls) {
            return 0;
        }
        if (h->sm) {
            h->mid = (*env)->GetStaticMethodID(env, h->cls, h->method_name, h->method_sig);
        } else {
            h->mid = (*env)->GetMethodID(env, h->cls, h->method_name, h->method_sig);
        }
        if (h->debug_me) {
            LOGD("mid = 0x%x\n", h->mid);
        }
        if (!h->mid) {
            return 0;
        }
    }

    h->method->insSize = h->iss;
    h->method->registersSize = h->rss;
    h->method->outsSize = h->oss;
    h->method->a = h->access_flags;
    h->method->jniArgInfo = 0;
    h->method->insns = h->insns;
}

void dalvik_postcall(struct dexstuff_t *dex, struct dalvik_hook_t *h)
{
    h->method->insSize = h->n_iss;
    h->method->registersSize = h->n_rss;
    h->method->outsSize = h->n_oss;

    //LOGD("shorty %s\n", h->method->shorty)
    //LOGD("name %s\n", h->method->name)
    //LOGD("arginfo %x\n", h->method->jniArgInfo)
    h->method->jniArgInfo = 0x80000000;
    //LOGD("noref %c\n", h->method->noRef)
    //LOGD("access %x\n", h->method->a)
    h->access_flags = h->method->a;
    h->method->a = h->method->a | h->af;
    //LOGD("access %x\n", h->method->a)

    dex->dvmUseJNIBridge_fnPtr(h->method, h->native_func);

    if (h->debug_me) {
        LOGD("patched BACK %s to: 0x%x\n", h->method_name, h->native_func);
    }
}

static char logfile[] = "/data/local/tmp/log";

static void logmsgtofile(char *msg)
{
    int fp = open(logfile, O_WRONLY|O_APPEND);
    write(fp, msg, strlen(msg));
    close(fp);
}

static void logmsgtostdout(char *msg)
{
    write(1, msg, strlen(msg));
}
