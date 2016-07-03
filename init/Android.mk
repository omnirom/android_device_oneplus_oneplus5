ifeq ($(TARGET_INIT_VENDOR_LIB),libinit_oneplus3)

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_C_INCLUDES := system/core/init
LOCAL_SRC_FILES := init_oneplus3.cpp
LOCAL_MODULE := libinit_oneplus3
LOCAL_CFLAGS := -Wall -DANDROID_TARGET=\"$(TARGET_BOARD_PLATFORM)\"


include $(BUILD_STATIC_LIBRARY)
endif
