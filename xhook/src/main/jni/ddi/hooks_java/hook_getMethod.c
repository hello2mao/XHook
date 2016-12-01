#include <jni.h>
#include "../../base/hook.h"
#include "../dalvikhook/dexstuff.h"
#include "../dalvikhook/dalvik_hook.h"
#include "hooks_java_init.h"


static struct dalvik_hook_t sb5;

static void *sb5_getmethod(JNIEnv *env, jobject obj, jobject str, jobject cls) {
/*
    log("getmethod\n")
    log("env = 0x%x\n", env)
    log("obj = 0x%x\n", obj)
    log("str = 0x%x\n", str)
    log("cls = 0x%x\n", cls)
*/

    jvalue args[2];
    args[0].l = str;
    args[1].l = cls;
    dalvik_prepare(&dex_stuff, &sb5, env);
    void *res = (*env)->CallObjectMethodA(env, obj, sb5.mid, args);
    LOGD("success calling : %s\n", sb5.method_name);
    dalvik_postcall(&dex_stuff, &sb5);

    if (str) {
        const char *s = (*env)->GetStringUTFChars(env, str, 0);
        if (s) {
            LOGD("sb5.getmethod = %s\n", s);
            (*env)->ReleaseStringUTFChars(env, str, s);
        }
    }

    return (void *)res;
}

void hook_getmethod_setup() {
    dalvik_hook_setup(&sb5, "Ljava/lang/Class;", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", 3, sb5_getmethod);
    dalvik_hook(&dex_stuff, &sb5);
}