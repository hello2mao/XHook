#include "dvm_helper.h"
#include "dvm.h"

static void *(*dvmGetMethodFromReflectObj)(void *);
static void *(*dvmInvokeMethod)(void *, void *, void *, void *, void *, bool);


static void realInvokeOriginalMethodNative(const u4 *args, JValue *pResult, const void *method,
                                           void *self) {
    void *meth = dvmGetMethodFromReflectObj((void *) args[0]);
    void *thisObject = (void *) args[1];
    void *argList = (void *) args[2];
    void *params = (void *) args[3];
    void *returnType = (void *) args[4];

    // 调用原方法
    pResult->l = dvmInvokeMethod(thisObject, meth, argList, params, returnType, true);
}

void init_dvm(JNIEnv* env) {
    void *handle = dlopen("libdvm.so", RTLD_LAZY | RTLD_GLOBAL);
    void (*dvmSetNativeFunc)(void *, void (*)(const u4 *, JValue *, const void *, void *), const u2 *)
            = (void (*)(void *, void (*)(const u4 *, JValue *, const void *, void *), const u2 *))
                    dlsym(handle, "_Z16dvmSetNativeFuncP6MethodPFvPKjP6JValuePKS_P6ThreadEPKt");
    dvmGetMethodFromReflectObj = (void* (*)(void*)) dlsym(handle, "_Z26dvmGetMethodFromReflectObjP6Object");
    dvmInvokeMethod = (void* (*)(void*, void*, void*, void*, void*, bool))
            dlsym(handle, "_Z15dvmInvokeMethodP6ObjectPK6MethodP11ArrayObjectS5_P11ClassObjectb");

    // 在native层中先找到要修复的Java函数对应的Method对象，修改它变为native方法，
    // 把它的nativeFunc指向hookedMethodCallback。
    // 这样对这个java函数的调用就转为调用hookedMethodCallback这个native函数了，
    // 然后再用这个native函数回调java层自己实现的统一接口来处理。
    jclass clazz = (*env)->FindClass(env, JNIHOOK_CLASS);
    // private static native Object invokeDvmMethod(Method method, Object receiver, Object[] args,
    // Class<?>[] typeParameter, Class<?> returnType);
    void *invokeOriginalMethodNative = (void*) (*env)->GetStaticMethodID(env, clazz, "invokeDvmMethod",
        "(Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;[Ljava/lang/Class;Ljava/lang/Class;)Ljava/lang/Object;");
    // 把invokeOriginalMethodNative的nativeFunc指向realInvokeOriginalMethodNative
    dvmSetNativeFunc(invokeOriginalMethodNative, realInvokeOriginalMethodNative, NULL);
}

