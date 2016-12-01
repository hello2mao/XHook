#include <jni.h>
#include "../../base/hook.h"
#include "../dalvikhook/dexstuff.h"
#include "../dalvikhook/dalvik_hook.h"
#include "hooks_java_init.h"


static struct dalvik_hook_t get;

// patches
static void *requestLine_get(JNIEnv *env, jobject obj, jobject request, jobject type) {

    jvalue args[2];
    args[0].l = request;
    args[1].l = type;
    dalvik_prepare(&dex_stuff, &get, env);
    void *res = (*env)->CallStaticObjectMethodA(env, obj, get.mid, args);
    LOGD("success calling : %s\n", get.method_name);
    dalvik_postcall(&dex_stuff, &get);

    const char *s = (*env)->GetStringUTFChars(env, res, 0);
    if (s) {
        LOGD("requestLine_get = %s\n", s);
        (*env)->ReleaseStringUTFChars(env, res, s);
    }

    return res;
}

void hook_requestLine_get_setup() {

    dalvik_hook_setup(&get, "Lokhttp3/internal/http/RequestLine;", "get",
                      "(Lokhttp3/Request;Ljava/net/Proxy/Type;)Ljava/lang/String;", 3, requestLine_get);
    dalvik_hook(&dex_stuff, &get);

}
