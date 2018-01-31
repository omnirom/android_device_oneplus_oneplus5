[AID_QTI_DIAG]
value:2901

[AID_RFS]
value:2951

[AID_RFS_SHARED]
value:2952

[bt_firmware/]
mode: 0771
user: AID_SYSTEM
group: AID_SYSTEM
caps: 0

[dsp/]
mode: 0771
user: AID_MEDIA
group: AID_MEDIA
caps: 0

[firmware/]
mode: 0771
user: AID_SYSTEM
group: AID_SYSTEM
caps: 0

[persist/]
mode: 0771
user: AID_SYSTEM
group: AID_SYSTEM
caps: 0

[system/bin/cnd]
mode: 0755
user: AID_SYSTEM
group: AID_SYSTEM
caps: NET_BIND_SERVICE BLOCK_SUSPEND NET_ADMIN

[system/bin/cnss-daemon]
mode: 0755
user: AID_SYSTEM
group: AID_SYSTEM
caps: NET_BIND_SERVICE

[system/vendor/bin/cnss-daemon]
mode: 0755
user: AID_SYSTEM
group: AID_SYSTEM
caps: NET_BIND_SERVICE

[system/bin/ims_rtp_daemon]
mode: 0755
user: AID_SYSTEM
group: AID_RADIO
caps: NET_BIND_SERVICE

[system/bin/imsdatadaemon]
mode: 0755
user: AID_SYSTEM
group: AID_SYSTEM
caps: NET_BIND_SERVICE

[system/bin/mm-qcamera-daemon]
mode: 0700
user: AID_CAMERA
group: AID_SHELL
caps: SYS_NICE

[system/vendor/bin/pm-service]
mode: 0755
user: AID_SYSTEM
group: AID_SYSTEM
caps: NET_BIND_SERVICE

[system/vendor/bin/pd-mapper]
mode: 0755
user: AID_SYSTEM
group: AID_SYSTEM
caps: NET_BIND_SERVICE

[system/bin/wcnss_filter]
mode: 0755
user: AID_BLUETOOTH
group: AID_BLUETOOTH
caps: BLOCK_SUSPEND

[system/vendor/bin/qvrservice]
mode: 0755
user: AID_SYSTEM
group: AID_SYSTEM
caps: SYS_NICE

[system/vendor/bin/cnd]
mode: 0755
user: AID_SYSTEM
group: AID_SYSTEM
caps: NET_BIND_SERVICE BLOCK_SUSPEND NET_ADMIN

[system/vendor/bin/ims_rtp_daemon]
mode: 0755
user: AID_SYSTEM
group: AID_RADIO
caps: NET_BIND_SERVICE

[system/vendor/bin/imsdatadaemon]
mode: 0755
user: AID_SYSTEM
group: AID_SYSTEM
caps: NET_BIND_SERVICE

[system/vendor/bin/imsrcsd]
mode: 0755
user: AID_SYSTEM
group: AID_RADIO
caps: WAKE_ALARM

[system/vendor/bin/slim_daemon]
mode: 0755
user:  AID_GPS
group: AID_GPS
caps: NET_BIND_SERVICE

[system/vendor/bin/wcnss_filter]
mode: 0755
user: AID_BLUETOOTH
group: AID_BLUETOOTH
caps: BLOCK_SUSPEND

[vendor/bin/xtwifi-client]
mode: 0755
user:  AID_GPS
group: AID_GPS
caps: NET_BIND_SERVICE BLOCK_SUSPEND

[system/vendor/bin/hw/android.hardware.bluetooth@1.0-service-qti]
mode: 0755
user: AID_SYSTEM
group: AID_SYSTEM
caps: BLOCK_SUSPEND NET_ADMIN
