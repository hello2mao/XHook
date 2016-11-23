LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := hijack
LOCAL_SRC_FILES := hijack.c
LOCAL_ARM_MODE := arm
LOCAL_CFLAGS := -g

include $(BUILD_EXECUTABLE)