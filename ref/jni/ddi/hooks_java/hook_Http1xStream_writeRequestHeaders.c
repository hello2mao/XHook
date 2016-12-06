#include <jni.h>
#include "base/hook.h"
#include "../dalvikhook/dexstuff.h"
#include "../dalvikhook/dalvik_hook.h"
#include "hooks_java_init.h"


static struct dalvik_hook_t tt;

// patches
static void *writeRequestHeaders(JNIEnv *env, jobject obj, jobject request) {

    jvalue args[1];
    args[0].l = request;
    dalvik_prepare(&dex_stuff, &tt, env);
    (*env)->CallObjectMethodA(env, obj, tt.mid, args);
    LOGD("success calling : %s\n", tt.method_name);
    dalvik_postcall(&dex_stuff, &tt);

    jclass Request = (*env)->FindClass(env,"okhttp3/Request");
    jmethodID toString = (*env)->GetMethodID(env, Request, "toString","()Ljava/lang/String;");
    void *res = (*env)->CallObjectMethodA(env, Request, toString, args);
    const char *s = (*env)->GetStringUTFChars(env, res, 0);
    if (s) {
        LOGD("Request.toString() = %s\n", s);
        (*env)->ReleaseStringUTFChars(env, res, s);
    }
}

void hook_writeRequestHeaders_setup() {

    dalvik_hook_setup(&tt, "Lokhttp3/internal/http/Http1xStream;", "writeRequestHeaders",
                      "(Lokhttp3/Request;)V", 2, writeRequestHeaders);
    dalvik_hook(&dex_stuff, &tt);

}

