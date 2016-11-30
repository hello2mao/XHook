LOCAL_PATH := $(call my-dir)
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
DALVIK_HOOK := dalvikhook/dexstuff.c.arm dalvikhook/dalvik_hook.c hooks_java/hooks_java_init.c
HOOKS_JAVA := hooks_java/hook_toString.c
LOCAL_SRC_FILES += $(DALVIK_HOOK) $(HOOKS_JAVA)
LOCAL_SHARED_LIBRARIES += dvm

# TODO: dexposed


include $(BUILD_SHARED_LIBRARY)





