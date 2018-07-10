# Copyright (C) 2010 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#
# This file is the build configuration for a full Android
# build for grouper hardware. This cleanly combines a set of
# device-specific aspects (drivers) with a device-agnostic
# product configuration (apps).
#
BOARD_PATH := device/oneplus/oneplus5

PRODUCT_FULL_TREBLE := true
BOARD_VNDK_VERSION := current
BOARD_VNDK_RUNTIME_DISABLE := true
PRODUCT_SHIPPING_API_LEVEL := 25
TARGET_NO_BOOTLOADER := true
TARGET_NO_KERNEL := false
TARGET_NO_RECOVERY := false
BOARD_SEPOLICY_VERS := 30

TARGET_NO_BOOTLOADER := true
TARGET_OTA_ASSERT_DEVICE := oneplus5,oneplus5t,cheeseburger,dumpling
TARGET_KERNEL_VERSION := 4.4
TARGET_BOOTLOADER_BOARD_NAME := msm8998
TARGET_KERNEL_CLANG_COMPILE := true
TARGET_USE_SDCLANG := true

# Platform
TARGET_BOARD_PLATFORM := msm8998
TARGET_BOARD_PLATFORM_GPU := qcom-adreno540

# Architecture
TARGET_ARCH := arm64
TARGET_ARCH_VARIANT := armv8-a
TARGET_CPU_ABI := arm64-v8a
TARGET_CPU_ABI2 :=
TARGET_CPU_VARIANT := kryo

TARGET_2ND_ARCH := arm
TARGET_2ND_ARCH_VARIANT := armv7-a-neon
TARGET_2ND_CPU_ABI := armeabi-v7a
TARGET_2ND_CPU_ABI2 := armeabi
TARGET_2ND_CPU_VARIANT := kryo

TARGET_USE_QCOM_BIONIC_OPTIMIZATION := true
TARGET_USES_64_BIT_BINDER := true
TARGET_COMPILE_WITH_MSM_KERNEL := true

ENABLE_CPUSETS := true
ENABLE_SCHEDBOOST := true

BOARD_KERNEL_CMDLINE := androidboot.hardware=qcom user_debug=31 msm_rtb.filter=0x237 ehci-hcd.park=3 lpm_levels.sleep_disabled=1 sched_enable_hmp=1 sched_enable_power_aware=1 service_locator.enable=1 swiotlb=2048 androidboot.usbcontroller=a800000.dwc3
BOARD_KERNEL_CMDLINE += androidboot.configfs=true
#BOARD_KERNEL_CMDLINE += androidboot.selinux=permissive
BOARD_KERNEL_BASE := 0x00000000
BOARD_KERNEL_PAGESIZE := 4096
BOARD_KERNEL_TAGS_OFFSET := 0x01E00000
BOARD_RAMDISK_OFFSET     := 0x02000000
BOARD_ROOT_EXTRA_FOLDERS := bt_firmware firmware firmware/radio persist
TARGET_KERNEL_ARCH := arm64
TARGET_KERNEL_HEADER_ARCH := arm64
TARGET_KERNEL_CROSS_COMPILE_PREFIX := aarch64-linux-android-
BOARD_KERNEL_IMAGE_NAME := Image.gz-dtb
TARGET_KERNEL_APPEND_DTB := true
TARGET_KERNEL_SOURCE := kernel/oneplus/msm8998
TARGET_KERNEL_CONFIG := omni_oneplus5_defconfig

# partitions
BOARD_BOOTIMAGE_PARTITION_SIZE := 67108864
BOARD_CACHEIMAGE_PARTITION_SIZE := 268435456
BOARD_RECOVERYIMAGE_PARTITION_SIZE := 67108864
BOARD_SYSTEMIMAGE_PARTITION_SIZE := 3221225472
BOARD_VENDORIMAGE_PARTITION_SIZE := 1073741824
BOARD_USERDATAIMAGE_PARTITION_SIZE := 56908316672
BOARD_FLASH_BLOCK_SIZE := 262144

# global
TARGET_SPECIFIC_HEADER_PATH := $(BOARD_PATH)/include
BOARD_USES_QCOM_HARDWARE := true
TARGET_USES_QCOM_BSP := false
TARGET_USERIMAGES_USE_EXT4 := true
TARGET_USERIMAGES_USE_F2FS := false
TARGET_USERIMAGES_SPARSE_EXT_DISABLED := false
TARGET_USES_MKE2FS := true

TARGET_COPY_OUT_VENDOR := vendor

# Generic AOSP image does NOT support HWC1
TARGET_USES_HWC2 := true
# Set emulator framebuffer display device buffer count to 3
NUM_FRAMEBUFFER_SURFACE_BUFFERS := 3

# TODO(b/35790399): remove when b/35790399 is fixed.
BOARD_NAND_SPARE_SIZE := 0
BOARD_FLASH_BLOCK_SIZE := 512

# audio effects
TARGET_SYSTEM_AUDIO_EFFECTS := true

# Camera
TARGET_USES_QTI_CAMERA2CLIENT := true
USE_CAMERA_STUB := true
TARGET_USES_MEDIA_EXTENSIONS := false
BOARD_USES_SNAPDRAGONCAMERA_VERSION := 2

#Media
TARGET_ENABLE_QC_AV_ENHANCEMENTS := true

# Disable secure discard because it's SLOW
BOARD_SUPPRESS_SECURE_ERASE := true

# Bluetooth
BOARD_HAVE_BLUETOOTH := true
BOARD_HAVE_BLUETOOTH_QCOM := true
BOARD_BLUETOOTH_BDROID_BUILDCFG_INCLUDE_DIR := $(BOARD_PATH)/bluetooth
QCOM_BT_USE_BTNV := true

# Wifi
TARGET_USES_QCOM_WCNSS_QMI       := false
BOARD_HAS_QCOM_WLAN              := true
BOARD_WLAN_DEVICE                := qcwcn
WPA_SUPPLICANT_VERSION           := VER_0_8_X
BOARD_WPA_SUPPLICANT_DRIVER      := NL80211
BOARD_WPA_SUPPLICANT_PRIVATE_LIB := lib_driver_cmd_$(BOARD_WLAN_DEVICE)
BOARD_HOSTAPD_DRIVER             := NL80211
BOARD_HOSTAPD_PRIVATE_LIB        := lib_driver_cmd_$(BOARD_WLAN_DEVICE)
WIFI_DRIVER_FW_PATH_STA          := "sta"
WIFI_DRIVER_FW_PATH_AP           := "ap"
WIFI_DRIVER_FW_PATH_P2P          := "p2p"
#WIFI_DRIVER_MODULE_PATH          := "/system/lib/modules/wlan.ko"
#WIFI_DRIVER_MODULE_NAME          := "wlan"
#WIFI_DRIVER_MODULE_ARG           := ""
WIFI_DRIVER_STATE_CTRL_PARAM     := "/dev/wlan"
WIFI_DRIVER_STATE_ON             := "ON"
WIFI_DRIVER_STATE_OFF            := "OFF"
WIFI_DRIVER_BUILT                := qca_cld3
WIFI_DRIVER_DEFAULT              := qca_cld3
#WIFI_HIDL_FEATURE_AWARE          := true
#WIFI_DRIVER_LOAD_DELAY           := true

CONFIG_ACS := true
CONFIG_IEEE80211AC := true

# charger
BOARD_CHARGER_ENABLE_SUSPEND := true
BOARD_CHARGER_DISABLE_INIT_BLANK := true
HEALTHD_ENABLE_OP_FASTCHG_CHECK := true

# power hal
TARGET_PROVIDES_POWERHAL := true
TARGET_USES_INTERACTION_BOOST := true

# liblights
TARGET_PROVIDES_LIBLIGHT := true

# NFC
TARGET_USES_NQ_NFC := true
BOARD_NFC_CHIPSET := pn553
NQ3XX_PRESENT := true
BOARD_NFC_HAL_SUFFIX := $(TARGET_BOARD_PLATFORM)

# ANT+
TARGET_USES_PREBUILT_ANT := true
BOARD_ANT_WIRELESS_DEVICE := "qualcomm-hidl"

# GPS
TARGET_NO_RPC := true
USE_DEVICE_SPECIFIC_GPS := true
BOARD_VENDOR_QCOM_GPS_LOC_API_HARDWARE := $(TARGET_BOARD_PLATFORM)
BOARD_VENDOR_QCOM_LOC_PDK_FEATURE_SET := true

# Crypto
TARGET_HW_DISK_ENCRYPTION := true
TARGET_CRYPTFS_HW_PATH := $(BOARD_PATH)/cryptfs_hw

#vold
TARGET_KERNEL_HAVE_NTFS := true
TARGET_EXFAT_DRIVER := exfat

# CNE and DPM
#TARGET_LDPRELOAD := libNimsWrap.so
BOARD_USES_QCNE := true

# selinux
include device/qcom/sepolicy/sepolicy.mk
include vendor/omni/sepolicy/sepolicy.mk
BOARD_PLAT_PUBLIC_SEPOLICY_DIR += $(BOARD_PATH)/sepolicy/public
BOARD_PLAT_PRIVATE_SEPOLICY_DIR += $(BOARD_PATH)/sepolicy/private

BOARD_SECCOMP_POLICY += $(BOARD_PATH)/seccomp_policy

TARGET_SYSTEM_PROP := $(BOARD_PATH)/system.prop
BOARD_PROPERTY_OVERRIDES_SPLIT_ENABLED := true

# for offmode charging
BOARD_USE_CUSTOM_RECOVERY_FONT := \"roboto_23x41.h\"

# Recovery:Start
BOARD_HAS_LARGE_FILESYSTEM := true
BOARD_HAS_NO_SELECT_BUTTON := true
TARGET_RECOVERY_PIXEL_FORMAT := "RGBX_8888"
TARGET_RECOVERY_FSTAB := $(BOARD_PATH)/twrp/fstab.qcom
BOARD_HAS_NO_REAL_SDCARD := true
RECOVERY_SDCARD_ON_DATA := true
TARGET_RECOVERY_QCOM_RTC_FIX := true
TW_BRIGHTNESS_PATH := "/sys/class/leds/lcd-backlight/brightness"
TW_EXCLUDE_DEFAULT_USB_INIT := true
TW_EXCLUDE_SUPERSU := true
TW_EXTRA_LANGUAGES := true
TW_INCLUDE_CRYPTO := true
#TW_CRYPTO_USE_SYSTEM_VOLD := qseecomd hwservicemanager keymaster-3-0
#TW_INCLUDE_NTFS_3G := true
TW_INPUT_BLACKLIST := "hbtp_vm"
TW_MAX_BRIGHTNESS := 255
TW_NO_USB_STORAGE := false
TW_SCREEN_BLANK_ON_BOOT := true
TW_THEME := portrait_hdpi
# Workaround for error copying vendor files to recovery ramdisk
#BOARD_VENDORIMAGE_FILE_SYSTEM_TYPE := ext4
#TARGET_COPY_OUT_VENDOR := vendor
TW_USE_TOOLBOX := true
