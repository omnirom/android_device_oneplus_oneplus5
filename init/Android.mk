ifeq ($(TARGET_INIT_VENDOR_LIB),libinit_oneplus5)

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_C_INCLUDES := system/core/init
LOCAL_SRC_FILES := init_oneplus5.cpp
LOCAL_MODULE := libinit_oneplus5

include $(BUILD_STATIC_LIBRARY)
endif
