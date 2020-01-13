ifeq ($(TARGET_DEVICE),$(filter $(TARGET_DEVICE),oneplus5 oneplus5t))
LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := fstab.qcom
LOCAL_MODULE_CLASS := ETC
LOCAL_SRC_FILES := $(LOCAL_MODULE)
ifeq ($(BOARD_USES_RECOVERY_AS_BOOT),true)
LOCAL_MODULE_PATH := $(TARGET_RECOVERY_ROOT_OUT)/first_stage_ramdisk
else
LOCAL_MODULE_PATH := $(TARGET_RAMDISK_OUT)
endif

include $(BUILD_PREBUILT)
endif
