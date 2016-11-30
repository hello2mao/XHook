#include <jni.h>
#include "base/hook.h"
#include "dalvikhook/dexstuff.h"
#include "dalvikhook/dalvik_hook.h"
#include "../config.h"


static struct dexstuff_t d;
static struct dalvik_hook_t sb20;
static struct dalvik_hook_t test;
static struct dalvik_hook_t requestline_get;

// patches
static void *sb20_tostring(JNIEnv *env, jobject obj) {

    dalvik_prepare(&d, &sb20, env);
    void *res = (*env)->CallObjectMethod(env, obj, sb20.mid);
    LOGD("success calling : %s\n", sb20.method_name);
    dalvik_postcall(&d, &sb20);

    const char *s = (*env)->GetStringUTFChars(env, res, 0);
    if (s) {
        LOGD("sb20.toString() = %s\n", s);
        (*env)->ReleaseStringUTFChars(env, res, s);
    }

    return res;
}

static void *okhttp_requestline_get(JNIEnv *env, jobject obj) {
    dalvik_prepare(&d, &requestline_get, env);
    void *res = (*env)->CallObjectMethod(env, obj, requestline_get.mid);
    LOGD("success calling requestline_get: %s\n", requestline_get.method_name);
    dalvik_postcall(&d, &requestline_get);

    const char *s = (*env)->GetStringUTFChars(env, res, 0);
    if (s) {
        LOGD("okhttp_requestline_get = %s\n", s);
        (*env)->ReleaseStringUTFChars(env, res, s);
    }

    return res;
}

static void *my_hook(JNIEnv *env, jobject obj) {
    dalvik_prepare(&d, &test, env);
    void *res = (*env)->CallObjectMethod(env, obj, test.mid);
    LOGD("success calling test: %s\n", test.method_name);
    dalvik_postcall(&d, &test);

    const char *s = (*env)->GetStringUTFChars(env, res, 0);
    if (s) {
        LOGD("test = %s\n", s);
        (*env)->ReleaseStringUTFChars(env, res, s);
    }

    return res;
}

void init() {
    // resolve symbols from DVM
    // 获得在libdvm.so动态库中所有hook需要使用的函数和全局变量的地址
    dexstuff_resolv_dvm(&d);
    // insert hooks
    // 初始化后面hook会用到的dalvik_hook_t结构体
    dalvik_hook_setup(&sb20, "Ljava/lang/StringBuilder;", "toString", "()Ljava/lang/String;", 1,
                      sb20_tostring);
    // 完成对要hook函数Method结构体的修改，从而完成hook
    dalvik_hook(&d, &sb20);

}
