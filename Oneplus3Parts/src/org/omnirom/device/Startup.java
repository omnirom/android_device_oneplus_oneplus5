/*
* Copyright (C) 2013 The OmniROM Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package org.omnirom.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Startup extends BroadcastReceiver {

    public static void restore(Context context, String file, boolean enabled) {
        if (file == null) {
            return;
        }
        Utils.writeValue(file, enabled ? "1" : "0");
    }

    public static void restore(Context context, String file, String value) {
        if (file == null) {
            return;
        }
        Utils.writeValue(file, value);
    }

    @Override
    public void onReceive(final Context context, final Intent bootintent) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enabled = sharedPrefs.getBoolean(GestureSettings.KEY_TORCH_SWITCH, false);
        restore(context, TorchGestureSwitch.getFile(), enabled);
        enabled = sharedPrefs.getBoolean(GestureSettings.KEY_UP_ARROW_SWITCH, false);
        restore(context, UpArrowGestureSwitch.getFile(), enabled);
        enabled = sharedPrefs.getBoolean(GestureSettings.KEY_CAMERA_SWITCH, false);
        restore(context, CameraGestureSwitch.getFile(), enabled);
        enabled = sharedPrefs.getBoolean(GestureSettings.KEY_MUSIC_SWITCH, false);
        restore(context, MusicGestureSwitch.getFile(), enabled);
        enabled = sharedPrefs.getBoolean(DeviceSettings.KEY_SRGB_SWITCH, false);
        restore(context, SRGBModeSwitch.getFile(), enabled);
        enabled = sharedPrefs.getBoolean(DeviceSettings.KEY_HBM_SWITCH, false);
        restore(context, HBMModeSwitch.getFile(), enabled ? "2" : "0");
        enabled = sharedPrefs.getBoolean(GestureSettings.KEY_LEFT_ARROW_SWITCH, false);
        restore(context, LeftArrowGestureSwitch.getFile(), enabled);
        enabled = sharedPrefs.getBoolean(GestureSettings.KEY_RIGHT_ARROW_SWITCH, false);
        restore(context, RightArrowGestureSwitch.getFile(), enabled);
        enabled = sharedPrefs.getBoolean(DeviceSettings.KEY_DCI_SWITCH, false);
        restore(context, DCIModeSwitch.getFile(), enabled);

        KeyHandler.setButtonDisable(context);
        VibratorStrengthPreference.restore(context);
    }
}
