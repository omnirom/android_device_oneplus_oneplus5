/*
* Copyright (C) 2017 The OmniROM Project
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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.util.Log;

public class DozeSettings extends PreferenceActivity  {

    private static final String KEY_WAVE_CHECK = "wave_check";
    private static final String KEY_POCKET_CHECK = "pocket_check";
    private static final String KEY_TILT_CHECK = "tilt_check";
    private static final String KEY_FOOTER = "footer";

    private boolean mUseTiltCheck;
    private boolean mUseWaveCheck;
    private boolean mUsePocketCheck;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        addPreferencesFromResource(R.xml.doze_settings);

        getDozeSettings();

        TwoStatePreference waveSwitch = (TwoStatePreference) findPreference(KEY_WAVE_CHECK);
        waveSwitch.setChecked(mUseWaveCheck);
        waveSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mUseWaveCheck = (Boolean) newValue;
                setDozeSettings();
                return true;
            }
        });
        TwoStatePreference pocketSwitch = (TwoStatePreference) findPreference(KEY_POCKET_CHECK);
        pocketSwitch.setChecked(mUsePocketCheck);
        pocketSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mUsePocketCheck = (Boolean) newValue;
                setDozeSettings();
                return true;
            }
        });
        TwoStatePreference tiltSwitch = (TwoStatePreference) findPreference(KEY_TILT_CHECK);
        tiltSwitch.setChecked(mUseTiltCheck);
        tiltSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mUseTiltCheck = (Boolean) newValue;
                setDozeSettings();
                return true;
            }
        });
        Preference footer = findPreference(KEY_FOOTER);
        if (isAmbientDisplayEnabled()) {
            getPreferenceScreen().removePreference(footer);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getDozeSettings() {
        String value = Settings.System.getString(getContentResolver(),
                    Settings.System.DEVICE_FEATURE_SETTINGS);
        if (!TextUtils.isEmpty(value)) {
            String[] parts = value.split(":");
            mUseWaveCheck = Boolean.valueOf(parts[0]);
            mUsePocketCheck = Boolean.valueOf(parts[1]);
            mUseTiltCheck = Boolean.valueOf(parts[2]);
        }
    }

    private void setDozeSettings() {
        String newValue = String.valueOf(mUseWaveCheck) + ":" + String.valueOf(mUsePocketCheck) + ":" + String.valueOf(mUseTiltCheck);
        Settings.System.putString(getContentResolver(), Settings.System.DEVICE_FEATURE_SETTINGS, newValue);
    }

    private boolean isAmbientDisplayEnabled() {
        return Settings.Secure.getInt(getContentResolver(), Settings.Secure.DOZE_ENABLED, 1) == 1;
    }
}
