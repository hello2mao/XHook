#include <jni.h>
#include "base/hook.h"
#include "dalvikhook/dexstuff.h"
#include "dalvikhook/dalvik_hook.h"
#include "hooks_java_init.h"
#include "../config.h"


static struct dalvik_hook_t sb20;

// patches
static void *sb20_tostring(JNIEnv *env, jobject obj) {

    dalvik_prepare(&dex_stuff, &sb20, env);
    void *res = (*env)->CallObjectMethod(env, obj, sb20.mid);
    LOGD("success calling : %s\n", sb20.method_name);
    dalvik_postcall(&dex_stuff, &sb20);

    const char *s = (*env)->GetStringUTFChars(env, res, 0);
    if (s) {
        LOGD("sb20.toString() = %s\n", s);
        (*env)->ReleaseStringUTFChars(env, res, s);
    }

    return res;
}

void hook_toString_setup() {
    // insert hooks
    // 初始化后面hook会用到的dalvik_hook_t结构体
    dalvik_hook_setup(&sb20, "Ljava/lang/StringBuilder;", "toString", "()Ljava/lang/String;", 1,
                      sb20_tostring);
    // 完成对要hook函数Method结构体的修改，从而完成hook
    dalvik_hook(&dex_stuff, &sb20);

}
