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

DALVIK_HOOK := dalvikhook/dexstuff.c.arm dalvikhook/dalvik_hook.c

#LOCAL_ARM_MODE := arm
LOCAL_MODULE := xhooknative
LOCAL_SRC_FILES := entry.c base/hook.c base/util.c hooks/util.c report_data/report.c $(HOOKS)
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog -lcrypto -lssl
LOCAL_CFLAGS := -g -std=gnu99
LOCAL_SHARED_LIBRARIES := dl

# TODO: dalvik hook
LOCAL_SRC_FILES += $(DALVIK_HOOK)
# LOCAL_LDLIBS += -L./libs -ldl -ldvm
LOCAL_SHARED_LIBRARIES += dvm



include $(BUILD_SHARED_LIBRARY)





