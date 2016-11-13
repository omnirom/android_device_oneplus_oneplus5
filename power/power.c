/*
 * Copyright (C) 2012 The Android Open Source Project
 * Copyright (C) 2016 The OmniROM Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#include <errno.h>
#include <string.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <fcntl.h>
#include <dlfcn.h>

#define LOG_TAG "PowerHAL"
#include <utils/Log.h>

#include <hardware/hardware.h>
#include <hardware/power.h>

#include "util.h"

// #define LOG_NDEBUG 0

#define DOUBLE_TAP_FILE "/proc/touchpanel/double_tap_enable"

static void power_init(struct power_module __unused *module)
{
    ALOGI("%s", __func__);
}

static void power_set_interactive(struct power_module __unused *module, int on)
{
    ALOGV("%s %s", __func__, (on ? "ON" : "OFF"));
}

static void power_hint(struct power_module __unused *module, power_hint_t hint,
                       void *data) {
    switch (hint) {
        case POWER_HINT_INTERACTION:
            break;
        case POWER_HINT_VIDEO_ENCODE:
            break;
        case POWER_HINT_LOW_POWER:
            break;
        default:
             break;
    }
}

void set_feature(struct power_module __unused *module, feature_t feature, int state) {
    if (feature == POWER_FEATURE_DOUBLE_TAP_TO_WAKE) {
        ALOGI("%s POWER_FEATURE_DOUBLE_TAP_TO_WAKE %s", __func__, (state ? "ON" : "OFF"));
        sysfs_write(DOUBLE_TAP_FILE, state ? "1" : "0");
    }
}

static struct hw_module_methods_t power_module_methods = {
    .open = NULL,
};

struct power_module HAL_MODULE_INFO_SYM = {
    .common = {
        .tag = HARDWARE_MODULE_TAG,
        .module_api_version = POWER_MODULE_API_VERSION_0_3,
        .hal_api_version = HARDWARE_HAL_API_VERSION,
        .id = POWER_HARDWARE_MODULE_ID,
        .name = "Oneplus3 power HAL",
        .author = "The OmniROM Project",
        .methods = &power_module_methods,
    },

    .init = power_init,
    .setInteractive = power_set_interactive,
    .powerHint = power_hint,
    .setFeature = set_feature,
};
