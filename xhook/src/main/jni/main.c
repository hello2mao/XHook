#include <jni.h>
#include <stdio.h>
#include <dlfcn.h>
#include <stdbool.h>
#include <stdlib.h>
#include <unistd.h>
#include "hook_java/dvm/dvm.h"
#include "hook_java/art/art.h"
#include "hook_native/base/hook.h"
#include "config.h"
#include "hook_native/base/util.h"

LIB_HOOK_INFO_NODE* custom_lib_hook_info_root = NULL;
HOOKED_INFO_NODE* hooked_info_root = NULL;
jmethodID mid;
jclass objclass;
jobject mobj;
JavaVM *m_vm;
char hook_func_list[MAX_HOOK_FUNC_LEN][MAX_HOOK_INFO_LEN] = {}; // increase/decrease if necessary

int get_android_version(const char *s);
bool init_hook_func_list(const char *ver);
void print_hook_func_list();

// 初始化的时候会调进来一次，在这个方法里持有jvm的引用
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGD("Lib JNI_OnLoad");
    m_vm=vm;
    JNIEnv *env = NULL;
    jint result = -1;
    if (vm == NULL) {
        LOGE("xhook native init failed due to vm is NULL");
    } else if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        return result;
    }
    return JNI_VERSION_1_4;
}

// 当动态库被卸载时这个函数被系统调用
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved) {
    LOGD("Lib JNI_OnUnload");
}

void hook_java_init(JNIEnv* env, jint version) {
    if ((int) version == NOT_HOOK_JAVA) {
        LOGI("Java hook not enabled");
        return;
    } else if ((int) version == DALVIK_VM) {
        LOGD("Java hook, Dalvik mode");
        dvm_jni_onload(env);
    } else {
        LOGD("Java hook, ART mode");
        // FIXME: not work
        art_jni_onload(env, (int)version);
    }
    LOGD("Init Java Hook sucess!");
}

/**
 * JNI native 接口，初始化Lib
 * path：lib path
 */
JNIEXPORT void JNICALL Java_com_mhb_xhook_nativehook_HookManager_initNativeHook(
    JNIEnv *env, jobject object, jstring path, jstring release, jint version) {

    const char *lib_path = (*env)->GetStringUTFChars(env, path, 0);
    const char *rel = (*env)->GetStringUTFChars(env, release, 0);
    LOGD("Init Lib, path=%s, Android Release=%s\n", lib_path, rel);

    // init Java Hook
    hook_java_init(env, version);

    bool ret = init_hook_func_list(rel);
    if (!ret) {
        LOGE("Recently, not support Android %s\n", rel);
        return;
    }

//    LIB_HOOK_INFO_NODE* lib_hook_info_root = build_hook_info_list_v1(lib_path, "system_hook_info");
    LIB_HOOK_INFO_NODE* lib_hook_info_root = build_hook_info_list_v2(lib_path, hook_func_list);
    if (lib_hook_info_root == NULL) {
        LOGE("Build system hook info list failed");
        return;
    }
    LIB_HOOK_INFO_NODE *lib_hook_info_node = lib_hook_info_root->next;
    void *handler = dlopen(lib_path, RTLD_LAZY);
    if (handler == NULL) {
        LOGE("dlopen %s failed", lib_path);
        return;
    }
    while (lib_hook_info_node != NULL) {
        char *hook_info_name = lib_hook_info_node->hook_info_name;
        // 根据动态链接库操作句柄与符号，返回符号对应的地址
        void *tmp_hook_info = dlsym(handler, hook_info_name);
        HOOK_INFO *hook_info;
        hook_info = (HOOK_INFO *) tmp_hook_info;
        if (hook_info != NULL) {
            // 修改函数来完成hook的目的
            if(hook(&(hook_info->eph),
                    getpid(),
                    hook_info->libname,
                    hook_info->funcname,
                    hook_info->hook_arm,
                    hook_info->hook_thumb)) {
                LOGD("Hooked %s in lib %s", hook_info->funcname, hook_info->libname);
            } else {
                LOGE("Try to hook %s in lib %s failed!", hook_info->funcname, hook_info->libname);
            }
        }
        lib_hook_info_node = lib_hook_info_node->next;
    }

    //这种写法可以用在子线程中
    objclass = (*env)->GetObjectClass(env, object);
    mid = (*env)->GetMethodID(env, objclass,
                              "callback",
                              "(IIDIIILjava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V");

    // JNI函数参数中 jobject或者它的子类，其参数都是 local reference。
    // Local reference 只在这个 JNI函数中有效，JNI函数返回后，引用的对象就被释放，它的生命周期就结束了。
    // 若要留着日后使用，则需根据这个 local reference创建global reference。
    // Global reference不会被系统自动释放，它仅当被程序明确调用DeleteGlobalReference时才被回收。JNI多线程机制）
    mobj=(*env)->NewGlobalRef(env, object);
    LOGI("Lib init success");

}

int get_android_version(const char *s) {
    if (!strncmp(s, "4.4", 3)) {
        return ANDROID_4_4;
    }
    if (!strncmp(s, "4.3", 3)) {
        return ANDROID_4_3;
    }
    return ANDROID_NOT_SUPPORT;
}

bool init_hook_func_list(const char *ver) {
    int num = get_android_version(ver);
    switch (num) {
        case ANDROID_4_3:
        case ANDROID_4_4:
            // TODO:
            strcpy(hook_func_list[0], "hook_info_getaddrinfo");
            strcpy(hook_func_list[1], "hook_info_socket");
            strcpy(hook_func_list[2], "hook_info_connect");
            strcpy(hook_func_list[3], "hook_info_poll");
            strcpy(hook_func_list[4], "hook_info_recvfrom");
            strcpy(hook_func_list[5], "hook_info_sendto");
            strcpy(hook_func_list[6], "hook_info_SSL_do_handshake");
            strcpy(hook_func_list[7], "hook_info_SSL_read");
            strcpy(hook_func_list[8], "hook_info_SSL_write");
            break;
        case ANDROID_NOT_SUPPORT:
            return false;
        default:
            return false;
    }
    return true;
}

void print_hook_func_list() {
    char (*temp)[MAX_HOOK_INFO_LEN];
    temp = hook_func_list;
    while (strlen(temp) != 0) {
        LOGD("hook_func: %s", temp);
        temp++;
    }
}


