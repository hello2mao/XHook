#include "dvm.h"
#include "dvm_helper.h"

static int gInsns = 32;
static int gRegistersSize = 10;
static int gOutsSize = 12;
static int gInsSize = 14;
static int gMethodSize = 60;

static void dvm_doHook(void *origin, void *proxy) {
    LOGD("dvm_doHook");
    int *ori_code_item = (int *)(origin + gInsns);
    int *pro_code_item = (int *)(proxy + gInsns);
    int code_temp = *ori_code_item;
    *ori_code_item = *pro_code_item;

    int *ori_register = (int *)(origin + gRegistersSize);
    int *pro_register = (int *)(proxy + gRegistersSize);
    int register_temp = *ori_register;
    *ori_register = *pro_register;

    int *ori_out = (int *)(origin + gOutsSize);
    int *pro_out = (int *)(proxy + gOutsSize);
    int out_temp = *ori_out;
    *ori_out = *pro_out;

    int *ori_size = (int *)(origin + gInsSize);
    int *pro_size = (int *)(proxy + gInsSize);
    int size_temp = *ori_size;
    *ori_size = *pro_size;

    memcpy(proxy, origin, gMethodSize);
    *pro_code_item = code_temp;
    *pro_register = register_temp;
    *pro_out = out_temp;
    *pro_size = size_temp;
}

static void hook_method(JNIEnv* env, jobject thiz, jobject method_origin, jobject method_proxy) {
    jmethodID meth_ori = (*env)->FromReflectedMethod(env, method_origin);
    jmethodID meth_pro = (*env)->FromReflectedMethod(env, method_proxy);
    if (meth_ori == NULL || meth_pro == NULL || meth_ori == meth_pro) {
        LOGE("FromReflectedMethod failed");
    }
    LOGD("meth_ori=0x%x, meth_pro=0x%x\n", (int)meth_ori, (int)meth_pro);
    dvm_doHook(meth_ori, meth_pro);
}

static JNINativeMethod gMethods[] = {
    { "hookMethod", "(Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;)V", (void *) hook_method }
};


static int registerNativeMethods(JNIEnv* env, const char *className,
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

void dvm_jni_onload(JNIEnv* env) {
    if (registerNativeMethods(env, JNIHOOK_CLASS, gMethods,
                              sizeof(gMethods) / sizeof(gMethods[0])) == JNI_FALSE) {
        LOGE("registerNativeMethods failed: %s", JNIHOOK_CLASS);
    }
    init_dvm(env);
}
