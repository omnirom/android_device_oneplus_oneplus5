# for HIDL related packages
PRODUCT_PACKAGES += \
    android.hardware.audio@2.0-impl \
    android.hardware.audio@2.0-service \
    android.hardware.audio.effect@2.0-impl \
    android.hardware.audio.soundtrigger@2.0-impl

# Bluetooth
PRODUCT_PACKAGES += \
    android.hardware.bluetooth@1.0-impl \
    android.hardware.bluetooth@1.0-service

# Camera configuration file. Shared by passthrough/binderized camera HAL
#PRODUCT_PACKAGES += camera.device@3.2-impl
#PRODUCT_PACKAGES += android.hardware.camera.provider@2.4-impl
# Enable binderized camera HAL
#PRODUCT_PACKAGES += android.hardware.camera.provider@2.4-service

#PRODUCT_PACKAGES += \
    vendor.qti.hardware.camera.device@1.0

# Display/Graphics
PRODUCT_PACKAGES += \
    android.hardware.graphics.allocator@2.0-impl \
    android.hardware.graphics.allocator@2.0-service \
    android.hardware.graphics.mapper@2.0-impl \
    android.hardware.graphics.composer@2.1-impl \
    android.hardware.graphics.composer@2.1-service \
    android.hardware.memtrack@1.0-impl \
    android.hardware.memtrack@1.0-service \
    android.hardware.light@2.0-impl \
    android.hardware.light@2.0-service \
    android.hardware.configstore@1.0-service

PRODUCT_PACKAGES += \
    vendor.display.config@1.0

# DRM
PRODUCT_PACKAGES += \
    android.hardware.drm@1.0-impl \
    android.hardware.drm@1.0-service

PRODUCT_PACKAGES += android.hidl.manager@1.0

#Enable AOSP KEYMASTER and GATEKEEPER HIDLs
PRODUCT_PACKAGES += \
    android.hardware.gatekeeper@1.0-impl \
    android.hardware.gatekeeper@1.0-service \
    android.hardware.keymaster@3.0-impl \
    android.hardware.keymaster@3.0-service

# GPS
PRODUCT_PACKAGES += \
    android.hardware.gnss@1.0-impl-qti \
    android.hardware.gnss@1.0-service-qti

# Netutils
PRODUCT_PACKAGES += \
    netutils-wrapper-1.0

#Nfc
PRODUCT_PACKAGES += \
    android.hardware.nfc@1.0-impl \
    vendor.nxp.hardware.nfc@1.0-impl \
    vendor.nxp.hardware.nfc@1.0-service
 
#Omx
PRODUCT_PACKAGES += \
    android.hardware.media.omx@1.0

 
# Power
PRODUCT_PACKAGES += \
    android.hardware.power@1.0-impl \
    android.hardware.power@1.1-service.oneplus5

# RenderScript HAL
PRODUCT_PACKAGES += \
    android.hardware.renderscript@1.0-impl

# Sensors
PRODUCT_PACKAGES += \
    android.hardware.sensors@1.0-impl \
    android.hardware.sensors@1.0-service

# USB
PRODUCT_PACKAGES += \
    android.hardware.usb@1.1-service.oneplus5

# Vibrator
PRODUCT_PACKAGES += \
    android.hardware.vibrator@1.0-impl \
    android.hardware.vibrator@1.0-service

#VR
PRODUCT_PACKAGES += \
    android.hardware.vr@1.0-service.oneplus5

# Wifi
PRODUCT_PACKAGES += \
    android.hardware.wifi@1.1 \
    android.hardware.wifi@1.0-service \
    android.hardware.wifi.supplicant@1.1

#RIL
PRODUCT_PACKAGES += \
    android.hardware.radio@1.1

#Healthd packages
PRODUCT_PACKAGES += \
    android.hardware.health@1.0-impl \
    android.hardware.health@1.0-service

# Netd
PRODUCT_PACKAGES += \
    android.system.net.netd@1.0

PRODUCT_PACKAGES += \
    android.hidl.base@1.0

PRODUCT_PACKAGES += \
    android.hidl.manager-V1.0-java
