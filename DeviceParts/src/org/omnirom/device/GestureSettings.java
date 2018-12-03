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
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.TwoStatePreference;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.util.Log;
import static android.provider.Settings.Secure.SYSTEM_NAVIGATION_KEYS_ENABLED;
import android.os.UserHandle;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GestureSettings extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static final String KEY_PROXI_SWITCH = "proxi";
    public static final String KEY_DOUBLE_SWIPE_APP = "double_swipe_gesture_app";
    public static final String KEY_CIRCLE_APP = "circle_gesture_app";
    public static final String KEY_DOWN_ARROW_APP = "down_arrow_gesture_app";
    public static final String KEY_UP_ARROW_APP = "up_arrow_gesture_app";
    public static final String KEY_LEFT_ARROW_APP = "left_arrow_gesture_app";
    public static final String KEY_RIGHT_ARROW_APP = "right_arrow_gesture_app";
    public static final String KEY_DOWN_SWIPE_APP = "down_swipe_gesture_app";
    public static final String KEY_UP_SWIPE_APP = "up_swipe_gesture_app";
    public static final String KEY_LEFT_SWIPE_APP = "left_swipe_gesture_app";
    public static final String KEY_RIGHT_SWIPE_APP = "right_swipe_gesture_app";
    public static final String KEY_FP_GESTURE_CATEGORY = "key_fp_gesture_category";
    public static final String KEY_FP_GESTURE_DEFAULT_CATEGORY = "gesture_settings";

    public static final String FP_GESTURE_SWIPE_DOWN_APP = "fp_down_swipe_gesture_app";
    public static final String FP_GESTURE_SWIPE_UP_APP = "fp_up_swipe_gesture_app";
    public static final String FP_GESTURE_SWIPE_RIGHT_APP = "fp_right_swipe_gesture_app";
    public static final String FP_GESTURE_SWIPE_LEFT_APP = "fp_left_swipe_gesture_app";
    public static final String FP_GESTURE_LONG_PRESS_APP = "fp_long_press_gesture_app";

    public static final String DEVICE_GESTURE_MAPPING_0 = "device_gesture_mapping_0_0";
    public static final String DEVICE_GESTURE_MAPPING_1 = "device_gesture_mapping_1_0";
    public static final String DEVICE_GESTURE_MAPPING_2 = "device_gesture_mapping_2_0";
    public static final String DEVICE_GESTURE_MAPPING_3 = "device_gesture_mapping_3_0";
    public static final String DEVICE_GESTURE_MAPPING_4 = "device_gesture_mapping_4_0";
    public static final String DEVICE_GESTURE_MAPPING_5 = "device_gesture_mapping_5_0";
    public static final String DEVICE_GESTURE_MAPPING_6 = "device_gesture_mapping_6_0";
    public static final String DEVICE_GESTURE_MAPPING_7 = "device_gesture_mapping_7_0";
    public static final String DEVICE_GESTURE_MAPPING_8 = "device_gesture_mapping_8_0";
    public static final String DEVICE_GESTURE_MAPPING_9 = "device_gesture_mapping_9_0";
    public static final String DEVICE_GESTURE_MAPPING_10 = "device_gesture_mapping_10_0";
    public static final String DEVICE_GESTURE_MAPPING_11 = "device_gesture_mapping_11_0";
    public static final String DEVICE_GESTURE_MAPPING_12 = "device_gesture_mapping_12_0";
    public static final String DEVICE_GESTURE_MAPPING_13 = "device_gesture_mapping_13_0";
    public static final String DEVICE_GESTURE_MAPPING_14 = "device_gesture_mapping_14_0";

    private TwoStatePreference mProxiSwitch;
    private TwoStatePreference mFpSwipeDownSwitch;
    private TwoStatePreference mOffscreenGestureFeedbackSwitch;
    private AppSelectListPreference mDoubleSwipeApp;
    private AppSelectListPreference mCircleApp;
    private AppSelectListPreference mDownArrowApp;
    private AppSelectListPreference mUpArrowApp;
    private AppSelectListPreference mLeftArrowApp;
    private AppSelectListPreference mRightArrowApp;
    private AppSelectListPreference mDownSwipeApp;
    private AppSelectListPreference mUpSwipeApp;
    private AppSelectListPreference mLeftSwipeApp;
    private AppSelectListPreference mRightSwipeApp;
    private AppSelectListPreference mFPDownSwipeApp;
    private AppSelectListPreference mFPUpSwipeApp;
    private AppSelectListPreference mFPRightSwipeApp;
    private AppSelectListPreference mFPLeftSwipeApp;
    private AppSelectListPreference mFPLongPressApp;
    private PreferenceCategory fpGestures;
    private boolean mFpDownSwipe;
    private static final boolean sIsOnePlus5t = android.os.Build.DEVICE.equals("OnePlus5T");
    private List<AppSelectListPreference.PackageItem> mInstalledPackages = new LinkedList<AppSelectListPreference.PackageItem>();
    private PackageManager mPm;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.gesture_settings, rootKey);
        mPm = getContext().getPackageManager();

        mProxiSwitch = (TwoStatePreference) findPreference(KEY_PROXI_SWITCH);
        mProxiSwitch.setChecked(Settings.System.getInt(getContext().getContentResolver(),
                Settings.System.OMNI_DEVICE_PROXI_CHECK_ENABLED, 1) != 0);

        mDoubleSwipeApp = (AppSelectListPreference) findPreference(KEY_DOUBLE_SWIPE_APP);
        mDoubleSwipeApp.setEnabled(isGestureSupported(KEY_DOUBLE_SWIPE_APP));
        String value = Settings.System.getString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_0);
        mDoubleSwipeApp.setValue(value);
        mDoubleSwipeApp.setOnPreferenceChangeListener(this);

        mCircleApp = (AppSelectListPreference) findPreference(KEY_CIRCLE_APP);
        mCircleApp.setEnabled(isGestureSupported(KEY_CIRCLE_APP));
        value = Settings.System.getString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_1);
        mCircleApp.setValue(value);
        mCircleApp.setOnPreferenceChangeListener(this);

        mDownArrowApp = (AppSelectListPreference) findPreference(KEY_DOWN_ARROW_APP);
        mDownArrowApp.setEnabled(isGestureSupported(KEY_DOWN_ARROW_APP));
        value = Settings.System.getString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_2);
        mDownArrowApp.setValue(value);
        mDownArrowApp.setOnPreferenceChangeListener(this);

        mUpArrowApp = (AppSelectListPreference) findPreference(KEY_UP_ARROW_APP);
        mUpArrowApp.setEnabled(isGestureSupported(KEY_UP_ARROW_APP));
        value = Settings.System.getString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_3);
        mUpArrowApp.setValue(value);
        mUpArrowApp.setOnPreferenceChangeListener(this);

        mLeftArrowApp = (AppSelectListPreference) findPreference(KEY_LEFT_ARROW_APP);
        mLeftArrowApp.setEnabled(isGestureSupported(KEY_LEFT_ARROW_APP));
        value = Settings.System.getString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_4);
        mLeftArrowApp.setValue(value);
        mLeftArrowApp.setOnPreferenceChangeListener(this);

        mRightArrowApp = (AppSelectListPreference) findPreference(KEY_RIGHT_ARROW_APP);
        mRightArrowApp.setEnabled(isGestureSupported(KEY_RIGHT_ARROW_APP));
        value = Settings.System.getString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_5);
        mRightArrowApp.setValue(value);
        mRightArrowApp.setOnPreferenceChangeListener(this);

        mDownSwipeApp = (AppSelectListPreference) findPreference(KEY_DOWN_SWIPE_APP);
        mDownSwipeApp.setEnabled(isGestureSupported(KEY_DOWN_SWIPE_APP));
        value = Settings.System.getString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_6);
        mDownSwipeApp.setValue(value);
        mDownSwipeApp.setOnPreferenceChangeListener(this);

        mUpSwipeApp = (AppSelectListPreference) findPreference(KEY_UP_SWIPE_APP);
        mUpSwipeApp.setEnabled(isGestureSupported(KEY_UP_SWIPE_APP));
        value = Settings.System.getString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_7);
        mUpSwipeApp.setValue(value);
        mUpSwipeApp.setOnPreferenceChangeListener(this);

        mLeftSwipeApp = (AppSelectListPreference) findPreference(KEY_LEFT_SWIPE_APP);
        mLeftSwipeApp.setEnabled(isGestureSupported(KEY_LEFT_SWIPE_APP));
        value = Settings.System.getString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_8);
        mLeftSwipeApp.setValue(value);
        mLeftSwipeApp.setOnPreferenceChangeListener(this);

        mRightSwipeApp = (AppSelectListPreference) findPreference(KEY_RIGHT_SWIPE_APP);
        mRightSwipeApp.setEnabled(isGestureSupported(KEY_RIGHT_SWIPE_APP));
        value = Settings.System.getString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_9);
        mRightSwipeApp.setValue(value);
        mRightSwipeApp.setOnPreferenceChangeListener(this);

        if (sIsOnePlus5t) {
            mFPDownSwipeApp = (AppSelectListPreference) findPreference(FP_GESTURE_SWIPE_DOWN_APP);
            mFPDownSwipeApp.setEnabled(!areSystemNavigationKeysEnabled());
            value = Settings.System.getString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_10);
            mFPDownSwipeApp.setValue(value);
            mFPDownSwipeApp.setOnPreferenceChangeListener(this);

            mFPUpSwipeApp = (AppSelectListPreference) findPreference(FP_GESTURE_SWIPE_UP_APP);
            mFPUpSwipeApp.setEnabled(!areSystemNavigationKeysEnabled());
            value = Settings.System.getString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_11);
            mFPUpSwipeApp.setValue(value);
            mFPUpSwipeApp.setOnPreferenceChangeListener(this);

            mFPRightSwipeApp = (AppSelectListPreference) findPreference(FP_GESTURE_SWIPE_RIGHT_APP);
            mFPRightSwipeApp.setEnabled(true);
            value = Settings.System.getString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_12);
            mFPRightSwipeApp.setValue(value);
            mFPRightSwipeApp.setOnPreferenceChangeListener(this);

            mFPLeftSwipeApp = (AppSelectListPreference) findPreference(FP_GESTURE_SWIPE_LEFT_APP);
            mFPLeftSwipeApp.setEnabled(true);
            value = Settings.System.getString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_13);
            mFPLeftSwipeApp.setValue(value);
            mFPLeftSwipeApp.setOnPreferenceChangeListener(this);

            mFPLongPressApp = (AppSelectListPreference) findPreference(FP_GESTURE_LONG_PRESS_APP);
            mFPLongPressApp.setEnabled(true);
            value = Settings.System.getString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_14);
            mFPLongPressApp.setValue(value);
            mFPLongPressApp.setOnPreferenceChangeListener(this);
        } else {
            Preference fpGestures = findPreference(KEY_FP_GESTURE_CATEGORY);
            Preference fpGesturesDefault = findPreference(KEY_FP_GESTURE_DEFAULT_CATEGORY);
            getPreferenceScreen().removePreference(fpGestures);
            getPreferenceScreen().removePreference(fpGesturesDefault);
        }

        new FetchPackageInformationTask().execute();
    }

    private boolean areSystemNavigationKeysEnabled() {
        return Settings.Secure.getInt(getContext().getContentResolver(),
               Settings.Secure.SYSTEM_NAVIGATION_KEYS_ENABLED, 0) == 1;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == mProxiSwitch) {
            Settings.System.putInt(getContext().getContentResolver(),
                    Settings.System.OMNI_DEVICE_PROXI_CHECK_ENABLED, mProxiSwitch.isChecked() ? 1 : 0);
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mDoubleSwipeApp) {
            String value = (String) newValue;
            boolean gestureDisabled = value.equals(AppSelectListPreference.DISABLED_ENTRY);
            setGestureEnabled(KEY_DOUBLE_SWIPE_APP, !gestureDisabled);
            Settings.System.putString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_0, value);
        } else if (preference == mCircleApp) {
            String value = (String) newValue;
            boolean gestureDisabled = value.equals(AppSelectListPreference.DISABLED_ENTRY);
            setGestureEnabled(KEY_CIRCLE_APP, !gestureDisabled);
            Settings.System.putString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_1, value);
        } else if (preference == mDownArrowApp) {
            String value = (String) newValue;
            boolean gestureDisabled = value.equals(AppSelectListPreference.DISABLED_ENTRY);
            setGestureEnabled(KEY_DOWN_ARROW_APP, !gestureDisabled);
            Settings.System.putString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_2, value);
        } else if (preference == mUpArrowApp) {
            String value = (String) newValue;
            boolean gestureDisabled = value.equals(AppSelectListPreference.DISABLED_ENTRY);
            setGestureEnabled(KEY_UP_ARROW_APP, !gestureDisabled);
            Settings.System.putString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_3, value);
        } else if (preference == mLeftArrowApp) {
            String value = (String) newValue;
            boolean gestureDisabled = value.equals(AppSelectListPreference.DISABLED_ENTRY);
            setGestureEnabled(KEY_LEFT_ARROW_APP, !gestureDisabled);
            Settings.System.putString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_4, value);
        } else if (preference == mRightArrowApp) {
            String value = (String) newValue;
            boolean gestureDisabled = value.equals(AppSelectListPreference.DISABLED_ENTRY);
            setGestureEnabled(KEY_RIGHT_ARROW_APP, !gestureDisabled);
            Settings.System.putString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_5, value);
        } else if (preference == mDownSwipeApp) {
            String value = (String) newValue;
            boolean gestureDisabled = value.equals(AppSelectListPreference.DISABLED_ENTRY);
            setGestureEnabled(KEY_DOWN_SWIPE_APP, !gestureDisabled);
            Settings.System.putString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_6, value);
        } else if (preference == mUpSwipeApp) {
            String value = (String) newValue;
            boolean gestureDisabled = value.equals(AppSelectListPreference.DISABLED_ENTRY);
            setGestureEnabled(KEY_UP_SWIPE_APP, !gestureDisabled);
            Settings.System.putString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_7, value);
        } else if (preference == mLeftSwipeApp) {
            String value = (String) newValue;
            boolean gestureDisabled = value.equals(AppSelectListPreference.DISABLED_ENTRY);
            setGestureEnabled(KEY_LEFT_SWIPE_APP, !gestureDisabled);
            Settings.System.putString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_8, value);
        } else if (preference == mRightSwipeApp) {
            String value = (String) newValue;
            boolean gestureDisabled = value.equals(AppSelectListPreference.DISABLED_ENTRY);
            setGestureEnabled(KEY_RIGHT_SWIPE_APP, !gestureDisabled);
            Settings.System.putString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_9, value);
        } else if (preference == mFPDownSwipeApp) {
            String value = (String) newValue;
            Settings.System.putString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_10, value);
        } else if (preference == mFPUpSwipeApp) {
            String value = (String) newValue;
            Settings.System.putString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_11, value);
        } else if (preference == mFPRightSwipeApp) {
            String value = (String) newValue;
            Settings.System.putString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_12, value);
        } else if (preference == mFPLeftSwipeApp) {
            String value = (String) newValue;
            Settings.System.putString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_13, value);
        } else if (preference == mFPLongPressApp) {
            String value = (String) newValue;
            Settings.System.putString(getContext().getContentResolver(), DEVICE_GESTURE_MAPPING_14, value);
        }
        return true;
    }

    public static String getGestureFile(String key) {
        switch(key) {
            case KEY_CIRCLE_APP:
                return "/proc/touchpanel/letter_o_enable";
            case KEY_DOUBLE_SWIPE_APP:
                return "/proc/touchpanel/double_swipe_enable";
            case KEY_DOWN_ARROW_APP:
                return "/proc/touchpanel/down_arrow_enable";
            case KEY_UP_ARROW_APP:
                return "/proc/touchpanel/up_arrow_enable";
            case KEY_LEFT_ARROW_APP:
                return "/proc/touchpanel/left_arrow_enable";
            case KEY_RIGHT_ARROW_APP:
                return "/proc/touchpanel/right_arrow_enable";
            case KEY_DOWN_SWIPE_APP:
                return "/proc/touchpanel/down_swipe_enable";
            case KEY_UP_SWIPE_APP:
                return "/proc/touchpanel/up_swipe_enable";
            case KEY_LEFT_SWIPE_APP:
                return "/proc/touchpanel/left_swipe_enable";
            case KEY_RIGHT_SWIPE_APP:
                return "/proc/touchpanel/right_swipe_enable";
        }
        return null;
    }

    private boolean isGestureSupported(String key) {
        return Utils.fileWritable(getGestureFile(key));
    }

    private void setGestureEnabled(String key, boolean enabled) {
        Utils.writeValue(getGestureFile(key), enabled ? "1" : "0");
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (!(preference instanceof AppSelectListPreference)) {
            super.onDisplayPreferenceDialog(preference);
            return;
        }
        DialogFragment fragment =
                AppSelectListPreference.AppSelectListPreferenceDialogFragment
                        .newInstance(preference.getKey());
        fragment.setTargetFragment(this, 0);
        fragment.show(getFragmentManager(), "dialog_preference");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFPDownSwipeApp != null) {
            mFPDownSwipeApp.setEnabled(!areSystemNavigationKeysEnabled());
        }
        if (mFPUpSwipeApp != null) {
            mFPUpSwipeApp.setEnabled(!areSystemNavigationKeysEnabled());
        }
    }

    private void loadInstalledPackages() {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> installedAppsInfo = mPm.queryIntentActivities(mainIntent, 0);

        for (ResolveInfo info : installedAppsInfo) {
            ActivityInfo activity = info.activityInfo;
            ApplicationInfo appInfo = activity.applicationInfo;
            ComponentName componentName = new ComponentName(appInfo.packageName, activity.name);
            CharSequence label = null;
            try {
                label = activity.loadLabel(mPm);
            } catch (Exception e) {
            }
            if (label != null) {
                final AppSelectListPreference.PackageItem item = new AppSelectListPreference.PackageItem(activity.loadLabel(mPm), 0, componentName);
                mInstalledPackages.add(item);
            }
        }
        Collections.sort(mInstalledPackages);
    }

    private class FetchPackageInformationTask extends AsyncTask<Void, Void, Void> {
        public FetchPackageInformationTask() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            loadInstalledPackages();
            return null;
        }

        @Override
        protected void onPostExecute(Void feed) {
            mDoubleSwipeApp.setPackageList(mInstalledPackages);
            mCircleApp.setPackageList(mInstalledPackages);
            mDownArrowApp.setPackageList(mInstalledPackages);
            mUpArrowApp.setPackageList(mInstalledPackages);
            mLeftArrowApp.setPackageList(mInstalledPackages);
            mRightArrowApp.setPackageList(mInstalledPackages);
            mDownSwipeApp.setPackageList(mInstalledPackages);
            mUpSwipeApp.setPackageList(mInstalledPackages);
            mLeftSwipeApp.setPackageList(mInstalledPackages);
            mRightSwipeApp.setPackageList(mInstalledPackages);
            if (sIsOnePlus5t) {
                mFPDownSwipeApp.setPackageList(mInstalledPackages);
                mFPUpSwipeApp.setPackageList(mInstalledPackages);
                mFPRightSwipeApp.setPackageList(mInstalledPackages);
                mFPLeftSwipeApp.setPackageList(mInstalledPackages);
                mFPLongPressApp.setPackageList(mInstalledPackages);
            }
        }
    }
}
