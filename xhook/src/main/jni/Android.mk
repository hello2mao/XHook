LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# TODO: hook_native
HOOKS += hook_native/hooks/hook_dns.c
HOOKS += hook_native/hooks/hook_socket.c
HOOKS += hook_native/hooks/hook_connect.c
HOOKS += hook_native/hooks/hook_poll.c
HOOKS += hook_native/hooks/hook_sendto.c
HOOKS += hook_native/hooks/hook_recvfrom.c
HOOKS += hook_native/hooks/hook_ssl_do_handshake.c
LOCAL_SRC_FILES += \
    hook_native/base/hook.c\
    hook_native/base/util.c\
    hook_native/report_data/report.c\
    $(HOOKS)\

# TODO: hook_java
LOCAL_SRC_FILES += \
    hook_java/art/art.c\
    hook_java/art/art_helper.c\
    hook_java/dvm/dvm.c\
    hook_java/dvm/dvm_helper.c\

# TODO: module setting
LOCAL_SRC_FILES += main.c
#LOCAL_ARM_MODE := arm
LOCAL_MODULE := xhooknative
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog -lcrypto -lssl
LOCAL_CFLAGS := -g
LOCAL_SHARED_LIBRARIES := dl

include $(BUILD_SHARED_LIBRARY)





