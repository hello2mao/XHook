LOCAL_PATH := $(call my-dir)

#include $(CLEAR_VARS)
#LOCAL_MODULE := libdexposed
#LOCAL_SRC_FILES := third_libs/dexposed_so/dexposed_dalvik/armeabi/libdexposed.a
#include $(PREBUILT_STATIC_LIBRARY)

#include $(CLEAR_VARS)
#LOCAL_MODULE := libdexposed
#LOCAL_SRC_FILES := third_libs/dexposed_so/dexposed_dalvik/armeabi/libdexposed.so
#include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
# TODO:
HOOKS += hooks/hook_dns.c
HOOKS += hooks/hook_socket.c
HOOKS += hooks/hook_connect.c
HOOKS += hooks/hook_poll.c
HOOKS += hooks/hook_sendto.c
HOOKS += hooks/hook_recvfrom.c
HOOKS += hooks/hook_ssl_do_handshake.c

#LOCAL_ARM_MODE := arm
LOCAL_MODULE := xhooknative
LOCAL_SRC_FILES := entry.c base/hook.c base/util.c hooks/util.c report_data/report.c $(HOOKS)
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog -lcrypto -lssl
LOCAL_CFLAGS := -g -std=gnu99
LOCAL_SHARED_LIBRARIES := dl

# TODO: dalvik hook
DALVIK_HOOK := ddi/dalvikhook/dexstuff.c.arm ddi/dalvikhook/dalvik_hook.c ddi/hooks_java/hooks_java_init.c
HOOKS_JAVA := ddi/hooks_java/hook_toString.c ddi/hooks_java/hook_getMethod.c
HOOKS_JAVA += ddi/hooks_java/hook_requestLine_get.c ddi/hooks_java/hook_requestLine_requestPath.c
LOCAL_SRC_FILES += $(DALVIK_HOOK) $(HOOKS_JAVA)
LOCAL_SHARED_LIBRARIES += dvm

# TODO: dexposed
#LOCAL_STATIC_LIBRARIES += dexposed
#LOCAL_SHARED_LIBRARIES += dexposed


include $(BUILD_SHARED_LIBRARY)





