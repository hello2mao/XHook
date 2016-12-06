#include <jni.h>
#include "base/hook.h"
#include "../dalvikhook/dexstuff.h"
#include "../dalvikhook/dalvik_hook.h"
#include "hooks_java_init.h"


static struct dalvik_hook_t tt;

// patches
static void *writeRequest(JNIEnv *env, jobject obj, jobject headers, jobject requestLine) {

    jvalue args[2];
    args[0].l = headers;
    args[1].l = requestLine;
    dalvik_prepare(&dex_stuff, &tt, env);
    (*env)->CallVoidMethodA(env, obj, tt.mid, args);
    LOGD("success calling : %s\n", tt.method_name);
    dalvik_postcall(&dex_stuff, &tt);

    printString(env, requestLine, "requestLine = ");

}

void hook_writeRequest_setup() {

    dalvik_hook_setup(&tt, "Lokhttp3/internal/http/Http1xStream;", "writeRequest",
                      "(Lokhttp3/Headers;Ljava/lang/String;)V", 3, writeRequest);
    dalvik_hook(&dex_stuff, &tt);

}

