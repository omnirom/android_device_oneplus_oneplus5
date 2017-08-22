/*
 *
 *  Copyright (c) 2013, The Linux Foundation. All rights reserved.
 *  Not a Contribution, Apache license notifications and license are retained
 *  for attribution purposes only.
 *
 * Copyright (C) 2012 The Android Open Source Project
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

#ifndef _BDROID_BUILDCFG_H
#define _BDROID_BUILDCFG_H
#define BTM_DEF_LOCAL_NAME         "Oneplus5"

// QCOM Config
#define MAX_L2CAP_CHANNELS         20
#define MAX_L2CAP_CLIENTS          19
#define MAX_ACL_CONNECTIONS        MAX_L2CAP_CHANNELS
#define GATT_MAX_PHY_CHANNEL       MAX_L2CAP_CHANNELS
#define AVDT_NUM_SEPS              9
#define AVDT_CODEC_SIZE            20
#define HID_HOST_MAX_DEVICES       MAX_L2CAP_CHANNELS
#define AVCT_NUM_CONN              5

// AOSP
// Wide-band speech support
#define BTM_WBS_INCLUDED           TRUE
#define BTIF_HF_WBS_PREFERRED      TRUE

// Google VSC spec support
#define BLE_VND_INCLUDED           TRUE

// QCOM power management workaround
#define BT_CLEAN_TURN_ON_DISABLED  TRUE

#endif
