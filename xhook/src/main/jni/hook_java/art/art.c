#include "art.h"
#include "art_helper.h"

static void hook_yposed_method(JNIEnv* env, jobject thiz, jobject method_origin, jobject method_proxy) {
    jmethodID meth_ori = (*env)->FromReflectedMethod(env, method_origin);
    jmethodID meth_pro = (*env)->FromReflectedMethod(env, method_proxy);
    switchQuickToInterpret(meth_ori);
    switchInterpretToInterpret(meth_ori);
    switchInterpretToInterpret(meth_pro);
    doHook(meth_ori, meth_pro);
}

static JNINativeMethod gMethods[] = {
        { "hookYposedMethod", "(Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)V", (void*) hook_yposed_method },
        };


static int registerNativeMethods(JNIEnv* env, const char* className,
        JNINativeMethod* gMethods, int numMethods) {
    jclass clazz = (*env)->FindClass(env, className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if ((*env)->RegisterNatives(env, clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

void art_jni_onload(JNIEnv* env, int version) {
    registerNativeMethods(env, JNIHOOK_CLASS, gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
    init_parameter(version);
}
