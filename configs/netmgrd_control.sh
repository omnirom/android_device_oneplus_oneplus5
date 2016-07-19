#!/system/bin/sh

# hack a shaq!
# to work around the libc crash in netmgrd which tears down
# mobile data - this will force restarting rild when
# netmgrd crashes which will bring back mobile data too
if [ `getprop sys.boot_completed` -eq 1 ]; then
    for pid in `pidof /system/bin/rild`
    do
        kill $pid
    done
fi
/system/bin/netmgrd
