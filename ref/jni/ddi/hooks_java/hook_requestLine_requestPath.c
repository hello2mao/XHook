#include <jni.h>
#include "base/hook.h"
#include "../dalvikhook/dexstuff.h"
#include "../dalvikhook/dalvik_hook.h"
#include "hooks_java_init.h"


static struct dalvik_hook_t requestPath;

// patches
static void *requestLine_requestPath(JNIEnv *env, jobject obj, jobject url) {

    jvalue args[1];
    args[0].l = url;
    dalvik_prepare(&dex_stuff, &requestPath, env);
    void *res = (*env)->CallStaticObjectMethodA(env, obj, requestPath.mid, args);
    LOGD("success calling : %s\n", requestPath.method_name);
    dalvik_postcall(&dex_stuff, &requestPath);

    const char *s = (*env)->GetStringUTFChars(env, res, 0);
    if (s) {
        LOGD("requestLine_requestPath = %s\n", s);
        (*env)->ReleaseStringUTFChars(env, res, s);
    }

    return (void *)res;
}

void hook_requestLine_requestPath_setup() {

    dalvik_hook_setup(&requestPath, "Lokhttp3/internal/http/RequestLine;", "requestPath",
                      "(Lokhttp3/HttpUrl;)Ljava/lang/String;", 2, requestLine_requestPath);
    dalvik_hook(&dex_stuff, &requestPath);

}
