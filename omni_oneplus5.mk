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

# Sample: This is where we'd set a backup provider if we had one
# $(call inherit-product, device/sample/products/backup_overlay.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/core_64_bit.mk)

# Get the prebuilt list of APNs
$(call inherit-product, vendor/omni/config/gsm.mk)

# Inherit from the common Open Source product configuration
$(call inherit-product, $(SRC_TARGET_DIR)/product/aosp_base_telephony.mk)

# must be before including omni part
TARGET_BOOTANIMATION_SIZE := 1080p

# Inherit from our custom product configuration
$(call inherit-product, vendor/omni/config/common.mk)

# Inherit from hardware-specific part of the product configuration
$(call inherit-product, device/oneplus/oneplus5/device.mk)

PRODUCT_PROPERTY_OVERRIDES += ro.hardware.nfc_nci=nqx.default

ALLOW_MISSING_DEPENDENCIES := true

DEVICE_PACKAGE_OVERLAYS += device/oneplus/oneplus5/overlay/device

PRODUCT_PACKAGES += \
    android.hardware.biometrics.fingerprint@2.1-service

# Discard inherited values and use our own instead.
PRODUCT_NAME := omni_oneplus5
PRODUCT_DEVICE := oneplus5
PRODUCT_BRAND := OnePlus
PRODUCT_MANUFACTURER := OnePlus
PRODUCT_MODEL := ONEPLUS A5000

PRODUCT_BUILD_PROP_OVERRIDES += TARGET_DEVICE=OnePlus5 PRODUCT_NAME=OnePlus5

PRODUCT_BUILD_PROP_OVERRIDES += \
    BUILD_FINGERPRINT=OnePlus/OnePlus5/OnePlus5:8.0.0/OPR6.170623.013/12140945:user/release-keys \
    PRIVATE_BUILD_DESC="OnePlus5-user 8.0.0 OPR6.170623.013 57 release-keys"

TARGET_VENDOR := oneplus
