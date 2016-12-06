#include "hooks_java_init.h"

void hooks_java_init() {
    // resolve symbols from DVM
    // 获得在libdvm.so动态库中所有hook需要使用的函数和全局变量的地址
    dexstuff_resolv_dvm(&dex_stuff);
    hook_toString_setup();
    hook_getmethod_setup();
    hook_requestLine_get_setup();
    hook_requestLine_requestPath_setup();
    hook_writeRequestHeaders_setup();
    hook_writeRequest_setup();
}

// helper function
void printString(JNIEnv *env, jobject str, char *l) {
    const char *s = (*env)->GetStringUTFChars(env, str, 0);
    if (s) {
        LOGD("%s%s\n", l, s);
        (*env)->ReleaseStringUTFChars(env, str, s);
    }
}