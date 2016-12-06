#ifndef XHOOK_CONFIG_H
#define XHOOK_CONFIG_H

#include <android/log.h>

#include "com_mhb_xhook_nativehook_HookManager.h"

#define LOG_TAG "XHook-native"

#define DEBUG 1
#ifdef DEBUG
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#else
#define LOGI(...) while(0){}
#define LOGD(...) while(0){}
#define LOGE(...) while(0){}
#endif

#define MAX_HOOK_INFO_LEN 80
#define MAX_HOOK_FUNC_LEN 20

#define ANDROID_NOT_SUPPORT 0
#define ANDROID_4_3 1
#define ANDROID_4_4 2
#define ANDROID_5_0 3
#define ANDROID_6_0 4


// Event Type
#define SOCKET_CREATED 1
#define UNKNOWN_TYPE_2 2
#define SOCKET_CONNECTED 3
#define SOCKET_SSL 4
#define SOCKET_POLLOUT 5
#define SOCKET_POLLIN 6
#define DNS_EVENT 7
#define SOCKET_SEND_OVER 8
#define SOCKET_RECEIVED 9

// Unknown callback report value
#define UNKNOWN_FD -1
#define UNKNOWN_TYPE -1
#define UNKNOWN_STARTTIME -1
#define UNKNOWN_TIMEELAPSED -1
#define UNKNOWN_RETURNVALUE -888
#define UNKNOWN_ERRORNUM -1
#define UNKNOWN_HOST ""
#define UNKNOWN_ADDRESS ""
#define UNKNOWN_DESC ""
#define UNKNOWN_PORT -1


#ifndef FRAMEWORK_SYSTEM_API
#define FRAMEWORK_SYSTEM_API 0x00
#endif

#ifndef NATIVE_SYSTEM_API
#define NATIVE_SYSTEM_API 0x10
#endif

#ifndef NATIVE_APP_API
#define NATIVE_APP_API 0x11
#endif

#endif //XHOOK_CONFIG_H
