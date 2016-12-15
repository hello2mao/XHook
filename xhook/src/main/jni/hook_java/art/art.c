#include "art.h"
#include "art_helper.h"

static void hook_method(JNIEnv* env, jobject thiz, jobject method_origin, jobject method_proxy) {
    jmethodID meth_ori = (*env)->FromReflectedMethod(env, method_origin);
    jmethodID meth_pro = (*env)->FromReflectedMethod(env, method_proxy);
    if (meth_ori == NULL || meth_pro == NULL || meth_ori == meth_pro) {
        LOGE("FromReflectedMethod failed");
    }
    LOGD("meth_ori=0x%x, meth_pro=0x%x\n", (int)meth_ori, (int)meth_pro);
    switchQuickToInterpret(meth_ori);
    switchInterpretToInterpret(meth_ori);
    switchInterpretToInterpret(meth_pro);
    doHook(meth_ori, meth_pro);
}

static JNINativeMethod gMethods[] = {
    { "hookMethod", "(Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)V", (void *) hook_method },
};


static int registerNativeMethods(JNIEnv *env, const char *className,
        JNINativeMethod *gMethods, int numMethods) {
    jclass clazz = (*env)->FindClass(env, className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if ((*env)->RegisterNatives(env, clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

void art_jni_onload(JNIEnv *env, int version) {
    if (registerNativeMethods(env, JNIHOOK_CLASS, gMethods,
                              sizeof(gMethods) / sizeof(gMethods[0])) == JNI_FALSE) {
        LOGE("registerNativeMethods failed: %s", JNIHOOK_CLASS);
    }
    init_parameter(version);
}
