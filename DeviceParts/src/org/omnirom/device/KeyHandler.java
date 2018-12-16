/*
* Copyright (C) 2016 The OmniROM Project
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

import static android.provider.Settings.Global.ZEN_MODE_OFF;
import static android.provider.Settings.Global.ZEN_MODE_IMPORTANT_INTERRUPTIONS;

import android.app.ActivityManagerNative;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.IAudioService;
import android.media.AudioManager;
import android.media.session.MediaSessionLegacyHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.HapticFeedbackConstants;
import android.view.WindowManagerGlobal;

import com.android.internal.util.omni.DeviceKeyHandler;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.omni.OmniUtils;
import org.omnirom.omnilib.utils.OmniVibe;
import com.android.internal.statusbar.IStatusBarService;

public class KeyHandler implements DeviceKeyHandler {

    private static final String TAG = "KeyHandler";
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_SENSOR = false;

    protected static final int GESTURE_REQUEST = 1;
    private static final int GESTURE_WAKELOCK_DURATION = 2000;
    private static final String KEY_CONTROL_PATH = "/proc/touchpanel/key_disable";
    private static final String FPC_CONTROL_PATH = "/sys/devices/soc/soc:fpc_fpc1020/proximity_state";
    private static final String FPC_KEY_CONTROL_PATH = "/sys/devices/soc/soc:fpc_fpc1020/key_disable";
    private static final String GOODIX_CONTROL_PATH = "/sys/devices/soc/soc:goodix_fp/proximity_state";

    private static final int GESTURE_CIRCLE_SCANCODE = 250;
    private static final int GESTURE_V_SCANCODE = 255;
    private static final int GESTURE_II_SCANCODE = 251;
    private static final int GESTURE_LEFT_V_SCANCODE = 253;
    private static final int GESTURE_RIGHT_V_SCANCODE = 254;
    private static final int GESTURE_A_SCANCODE = 252;
    private static final int GESTURE_RIGHT_SWIPE_SCANCODE = 63;
    private static final int GESTURE_LEFT_SWIPE_SCANCODE = 64;
    private static final int GESTURE_DOWN_SWIPE_SCANCODE = 65;
    private static final int GESTURE_UP_SWIPE_SCANCODE = 66;

    private static final int KEY_DOUBLE_TAP = 143;
    private static final int KEY_HOME = 102;
    private static final int KEY_BACK = 158;
    private static final int KEY_RECENTS = 580;
    private static final int KEY_SLIDER_TOP = 601;
    private static final int KEY_SLIDER_CENTER = 602;
    private static final int KEY_SLIDER_BOTTOM = 603;

    private static final int MIN_PULSE_INTERVAL_MS = 2500;
    private static final String DOZE_INTENT = "com.android.systemui.doze.pulse";
    private static final int HANDWAVE_MAX_DELTA_MS = 1000;
    private static final int POCKET_MIN_DELTA_MS = 5000;
    private static final int FP_GESTURE_SWIPE_DOWN = 108;
    private static final int FP_GESTURE_SWIPE_UP = 103;
    private static final int FP_GESTURE_SWIPE_LEFT = 105;
    private static final int FP_GESTURE_SWIPE_RIGHT = 106;
    private static final int FP_GESTURE_LONG_PRESS = 305;
    private static final boolean sIsOnePlus5t = android.os.Build.DEVICE.equals("OnePlus5T");

    private static final int[] sSupportedGestures5t = new int[]{
        GESTURE_II_SCANCODE,
        GESTURE_CIRCLE_SCANCODE,
        GESTURE_V_SCANCODE,
        GESTURE_A_SCANCODE,
        GESTURE_LEFT_V_SCANCODE,
        GESTURE_RIGHT_V_SCANCODE,
        GESTURE_DOWN_SWIPE_SCANCODE,
        GESTURE_UP_SWIPE_SCANCODE,
        GESTURE_LEFT_SWIPE_SCANCODE,
        GESTURE_RIGHT_SWIPE_SCANCODE,
        KEY_DOUBLE_TAP,
        KEY_SLIDER_TOP,
        KEY_SLIDER_CENTER,
        KEY_SLIDER_BOTTOM,
        FP_GESTURE_SWIPE_DOWN,
        FP_GESTURE_SWIPE_UP,
        FP_GESTURE_SWIPE_LEFT,
        FP_GESTURE_SWIPE_RIGHT,
        FP_GESTURE_LONG_PRESS,
    };

    private static final int[] sSupportedGestures = new int[]{
        GESTURE_II_SCANCODE,
        GESTURE_CIRCLE_SCANCODE,
        GESTURE_V_SCANCODE,
        GESTURE_A_SCANCODE,
        GESTURE_LEFT_V_SCANCODE,
        GESTURE_RIGHT_V_SCANCODE,
        GESTURE_DOWN_SWIPE_SCANCODE,
        GESTURE_UP_SWIPE_SCANCODE,
        GESTURE_LEFT_SWIPE_SCANCODE,
        GESTURE_RIGHT_SWIPE_SCANCODE,
        KEY_DOUBLE_TAP,
        KEY_SLIDER_TOP,
        KEY_SLIDER_CENTER,
        KEY_SLIDER_BOTTOM
    };

    private static final int[] sHandledGestures = new int[]{
        KEY_SLIDER_TOP,
        KEY_SLIDER_CENTER,
        KEY_SLIDER_BOTTOM
    };

    private static final int[] sProxiCheckedGestures = new int[]{
        GESTURE_II_SCANCODE,
        GESTURE_CIRCLE_SCANCODE,
        GESTURE_V_SCANCODE,
        GESTURE_A_SCANCODE,
        GESTURE_LEFT_V_SCANCODE,
        GESTURE_RIGHT_V_SCANCODE,
        GESTURE_DOWN_SWIPE_SCANCODE,
        GESTURE_UP_SWIPE_SCANCODE,
        GESTURE_LEFT_SWIPE_SCANCODE,
        GESTURE_RIGHT_SWIPE_SCANCODE,
        KEY_DOUBLE_TAP
    };

    protected final Context mContext;
    private final PowerManager mPowerManager;
    private EventHandler mEventHandler;
    private WakeLock mGestureWakeLock;
    private Handler mHandler = new Handler();
    private SettingsObserver mSettingsObserver;
    private static boolean mButtonDisabled;
    private final NotificationManager mNoMan;
    private final AudioManager mAudioManager;
    private SensorManager mSensorManager;
    private boolean mProxyIsNear;
    private boolean mUseProxiCheck;
    private Sensor mTiltSensor;
    private boolean mUseTiltCheck;
    private boolean mProxyWasNear;
    private long mProxySensorTimestamp;
    private boolean mUseWaveCheck;
    private Sensor mPocketSensor;
    private boolean mUsePocketCheck;
    private boolean mFPcheck;
    private boolean mDispOn;
    private boolean isFpgesture;

    private SensorEventListener mProximitySensor = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            mProxyIsNear = event.values[0] == 1;
            if (DEBUG_SENSOR) Log.i(TAG, "mProxyIsNear = " + mProxyIsNear + " mProxyWasNear = " + mProxyWasNear);
            if (mUseProxiCheck) {
                if (!sIsOnePlus5t) {
                    if (Utils.fileWritable(FPC_CONTROL_PATH)) {
                        Utils.writeValue(FPC_CONTROL_PATH, mProxyIsNear ? "1" : "0");
                    }
                } else {
                    if (Utils.fileWritable(GOODIX_CONTROL_PATH)) {
                        Utils.writeValue(GOODIX_CONTROL_PATH, mProxyIsNear ? "1" : "0");
                    }
                }
            }
            if (mUseWaveCheck || mUsePocketCheck) {
                if (mProxyWasNear && !mProxyIsNear) {
                    long delta = SystemClock.elapsedRealtime() - mProxySensorTimestamp;
                    if (DEBUG_SENSOR) Log.i(TAG, "delta = " + delta);
                    if (mUseWaveCheck && delta < HANDWAVE_MAX_DELTA_MS) {
                        launchDozePulse();
                    }
                    if (mUsePocketCheck && delta > POCKET_MIN_DELTA_MS) {
                        launchDozePulse();
                    }
                }
                mProxySensorTimestamp = SystemClock.elapsedRealtime();
                mProxyWasNear = mProxyIsNear;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private SensorEventListener mTiltSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.values[0] == 1) {
                launchDozePulse();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private class SettingsObserver extends ContentObserver {
        SettingsObserver(Handler handler) {
            super(handler);
        }

        void observe() {
            mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(
                    Settings.System.OMNI_HARDWARE_KEYS_DISABLE),
                    false, this);
            mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(
                    Settings.System.OMNI_DEVICE_PROXI_CHECK_ENABLED),
                    false, this);
            mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(
                    Settings.System.OMNI_DEVICE_FEATURE_SETTINGS),
                    false, this);
            update();
            updateDozeSettings();
        }

        @Override
        public void onChange(boolean selfChange) {
            update();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (uri.equals(Settings.System.getUriFor(
                    Settings.System.OMNI_DEVICE_FEATURE_SETTINGS))){
                updateDozeSettings();
                return;
            }
            update();
        }

        public void update() {
            setButtonDisable(mContext);
            mUseProxiCheck = Settings.System.getIntForUser(
                    mContext.getContentResolver(), Settings.System.OMNI_DEVICE_PROXI_CHECK_ENABLED, 1,
                    UserHandle.USER_CURRENT) == 1;
        }
    }

    private BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() {
         @Override
         public void onReceive(Context context, Intent intent) {
             if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                 mDispOn = true;
                 onDisplayOn();
             } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                 mDispOn = false;
                 onDisplayOff();
             }
         }
    };

    public KeyHandler(Context context) {
        mContext = context;
        mDispOn = true;
        mEventHandler = new EventHandler();
        mPowerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mGestureWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "GestureWakeLock");
        mSettingsObserver = new SettingsObserver(mHandler);
        mSettingsObserver.observe();
        mNoMan = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mTiltSensor = getSensor(mSensorManager, "com.oneplus.sensor.pickup");
        mPocketSensor = getSensor(mSensorManager, "com.oneplus.sensor.pocket");
        IntentFilter screenStateFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mContext.registerReceiver(mScreenStateReceiver, screenStateFilter);
    }

    private class EventHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
        }
    }

    @Override
    public boolean handleKeyEvent(KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_UP) {
            return false;
        }
        isFpgesture = false;
        boolean isKeySupported = ArrayUtils.contains(sHandledGestures, event.getScanCode());
        if (isKeySupported) {
            if (DEBUG) Log.i(TAG, "scanCode=" + event.getScanCode());
            switch(event.getScanCode()) {
                case KEY_SLIDER_TOP:
                    if (DEBUG) Log.i(TAG, "KEY_SLIDER_TOP");
                    doHandleSliderAction(0);
                    return true;
                case KEY_SLIDER_CENTER:
                    if (DEBUG) Log.i(TAG, "KEY_SLIDER_CENTER");
                    doHandleSliderAction(1);
                    return true;
                case KEY_SLIDER_BOTTOM:
                    if (DEBUG) Log.i(TAG, "KEY_SLIDER_BOTTOM");
                    doHandleSliderAction(2);
                    return true;
            }
        }

        if (DEBUG) Log.i(TAG, "nav_code=" + event.getScanCode());
        int fpcode = event.getScanCode();
        mFPcheck = canHandleKeyEvent(event);
        String value = getGestureValueForFPScanCode(fpcode);
        if (mFPcheck && mDispOn && !TextUtils.isEmpty(value) && !value.equals(AppSelectListPreference.DISABLED_ENTRY)){
            isFpgesture = true;
            if (!launchSpecialActions(value) && !isCameraLaunchEvent(event)) {
                    OmniVibe.performHapticFeedbackLw(HapticFeedbackConstants.LONG_PRESS, false, mContext);
                    Intent intent = createIntent(value);
                    if (DEBUG) Log.i(TAG, "intent = " + intent);
                    mContext.startActivity(intent);
            }
        }
        return isKeySupported;
    }

    @Override
    public boolean canHandleKeyEvent(KeyEvent event) {
        if (sIsOnePlus5t) {
            return ArrayUtils.contains(sSupportedGestures5t, event.getScanCode());
        } else {
            return ArrayUtils.contains(sSupportedGestures, event.getScanCode());
        }
    }

    @Override
    public boolean isDisabledKeyEvent(KeyEvent event) {
        boolean isProxyCheckRequired = mUseProxiCheck &&
                ArrayUtils.contains(sProxiCheckedGestures, event.getScanCode());
        if (mProxyIsNear && isProxyCheckRequired) {
            if (DEBUG) Log.i(TAG, "isDisabledKeyEvent: blocked by proxi sensor - scanCode=" + event.getScanCode());
            return true;
        }
        return false;
    }

    public static void setButtonDisable(Context context) {
        // we should never come here on the 5t but just to be sure
        if (!sIsOnePlus5t) {
            mButtonDisabled = Settings.System.getIntForUser(
                    context.getContentResolver(), Settings.System.OMNI_HARDWARE_KEYS_DISABLE, 0,
                    UserHandle.USER_CURRENT) == 1;
            if (DEBUG) Log.i(TAG, "setButtonDisable=" + mButtonDisabled);
            if(mButtonDisabled) {
                Utils.writeValue(KEY_CONTROL_PATH, "1");
                Utils.writeValue(FPC_KEY_CONTROL_PATH, "1");
            }
            else {
                Utils.writeValue(KEY_CONTROL_PATH, "0");
                Utils.writeValue(FPC_KEY_CONTROL_PATH, "0");
            }
        }
    }

    @Override
    public boolean isCameraLaunchEvent(KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_UP) {
            return false;
        }
        if (mFPcheck) {
            String value = getGestureValueForFPScanCode(event.getScanCode());
            return !TextUtils.isEmpty(value) && value.equals(AppSelectListPreference.CAMERA_ENTRY);
        } else {
            String value = getGestureValueForScanCode(event.getScanCode());
            return !TextUtils.isEmpty(value) && value.equals(AppSelectListPreference.CAMERA_ENTRY);
        }
    }

    @Override
    public boolean isWakeEvent(KeyEvent event){
        if (event.getAction() != KeyEvent.ACTION_UP) {
            return false;
        }
        String value = getGestureValueForScanCode(event.getScanCode());
        if (!TextUtils.isEmpty(value) && value.equals(AppSelectListPreference.WAKE_ENTRY)) {
            if (DEBUG) Log.i(TAG, "isWakeEvent " + event.getScanCode() + value);
            return true;
        }
        return event.getScanCode() == KEY_DOUBLE_TAP;
    }

    @Override
    public Intent isActivityLaunchEvent(KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_UP) {
            return null;
        }
        String value = getGestureValueForScanCode(event.getScanCode());
        if (!TextUtils.isEmpty(value) && !value.equals(AppSelectListPreference.DISABLED_ENTRY)) {
            if (DEBUG) Log.i(TAG, "isActivityLaunchEvent " + event.getScanCode() + value);
            if (!launchSpecialActions(value)) {
                OmniVibe.performHapticFeedbackLw(HapticFeedbackConstants.LONG_PRESS, false, mContext);
                Intent intent = createIntent(value);
                return intent;
            }
        }
        return null;
    }

    private IAudioService getAudioService() {
        IAudioService audioService = IAudioService.Stub
                .asInterface(ServiceManager.checkService(Context.AUDIO_SERVICE));
        if (audioService == null) {
            Log.w(TAG, "Unable to find IAudioService interface.");
        }
        return audioService;
    }

    boolean isMusicActive() {
        return mAudioManager.isMusicActive();
    }

    private void dispatchMediaKeyWithWakeLockToAudioService(int keycode) {
        if (ActivityManagerNative.isSystemReady()) {
            IAudioService audioService = getAudioService();
            if (audioService != null) {
                KeyEvent event = new KeyEvent(SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(), KeyEvent.ACTION_DOWN,
                        keycode, 0);
                dispatchMediaKeyEventUnderWakelock(event);
                event = KeyEvent.changeAction(event, KeyEvent.ACTION_UP);
                dispatchMediaKeyEventUnderWakelock(event);
            }
        }
    }

    private void dispatchMediaKeyEventUnderWakelock(KeyEvent event) {
        if (ActivityManagerNative.isSystemReady()) {
            MediaSessionLegacyHelper.getHelper(mContext).sendMediaButtonEvent(event, true);
        }
    }

    private void onDisplayOn() {
        if (DEBUG) Log.i(TAG, "Display on");
        if (enableProxiSensor()) {
            mSensorManager.unregisterListener(mProximitySensor, mPocketSensor);
            enableGoodix();
        }
        if (mUseTiltCheck) {
            mSensorManager.unregisterListener(mTiltSensorListener, mTiltSensor);
        }
    }

    private void enableGoodix() {
        if (sIsOnePlus5t) {
            if (Utils.fileWritable(GOODIX_CONTROL_PATH)) {
                Utils.writeValue(GOODIX_CONTROL_PATH, "0");
            }
        }
    }

    private void onDisplayOff() {
        if (DEBUG) Log.i(TAG, "Display off");
        if (enableProxiSensor()) {
            mProxyWasNear = false;
            mSensorManager.registerListener(mProximitySensor, mPocketSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
            mProxySensorTimestamp = SystemClock.elapsedRealtime();
        }
        if (mUseTiltCheck) {
            mSensorManager.registerListener(mTiltSensorListener, mTiltSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private int getSliderAction(int position) {
        String value = Settings.System.getStringForUser(mContext.getContentResolver(),
                    Settings.System.OMNI_BUTTON_EXTRA_KEY_MAPPING,
                    UserHandle.USER_CURRENT);
        final String defaultValue = DeviceSettings.SLIDER_DEFAULT_VALUE;

        if (value == null) {
            value = defaultValue;
        } else if (value.indexOf(",") == -1) {
            value = defaultValue;
        }
        try {
            String[] parts = value.split(",");
            return Integer.valueOf(parts[position]);
        } catch (Exception e) {
        }
        return 0;
    }

    private void doHandleSliderAction(int position) {
        int action = getSliderAction(position);
        if ( action == 0) {
            mNoMan.setZenMode(ZEN_MODE_OFF, null, TAG);
            mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_NORMAL);
        } else if (action == 1) {
            mNoMan.setZenMode(ZEN_MODE_OFF, null, TAG);
            mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_VIBRATE);
        } else if (action == 2) {
            mNoMan.setZenMode(ZEN_MODE_IMPORTANT_INTERRUPTIONS, null, TAG);
        }
    }

    private Intent createIntent(String value) {
        ComponentName componentName = ComponentName.unflattenFromString(value);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.setComponent(componentName);
        return intent;
    }

    private boolean launchSpecialActions(String value) {
        if (value.equals(AppSelectListPreference.TORCH_ENTRY)) {
            mGestureWakeLock.acquire(GESTURE_WAKELOCK_DURATION);
            IStatusBarService service = getStatusBarService();
            if (service != null) {
                try {
                    service.toggleCameraFlash();
                    OmniVibe.performHapticFeedbackLw(HapticFeedbackConstants.LONG_PRESS, false, mContext);
                } catch (RemoteException e) {
                    // do nothing.
                }
            }
            return true;
        } else if (value.equals(AppSelectListPreference.MUSIC_PLAY_ENTRY)) {
            mGestureWakeLock.acquire(GESTURE_WAKELOCK_DURATION);
            OmniVibe.performHapticFeedbackLw(HapticFeedbackConstants.LONG_PRESS, false, mContext);
            dispatchMediaKeyWithWakeLockToAudioService(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
            return true;
        } else if (value.equals(AppSelectListPreference.MUSIC_NEXT_ENTRY)) {
            if (isMusicActive()) {
                mGestureWakeLock.acquire(GESTURE_WAKELOCK_DURATION);
                OmniVibe.performHapticFeedbackLw(HapticFeedbackConstants.LONG_PRESS, false, mContext);
                dispatchMediaKeyWithWakeLockToAudioService(KeyEvent.KEYCODE_MEDIA_NEXT);
            }
            return true;
        } else if (value.equals(AppSelectListPreference.MUSIC_PREV_ENTRY)) {
            if (isMusicActive()) {
                mGestureWakeLock.acquire(GESTURE_WAKELOCK_DURATION);
                OmniVibe.performHapticFeedbackLw(HapticFeedbackConstants.LONG_PRESS, false, mContext);
                dispatchMediaKeyWithWakeLockToAudioService(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
            }
            return true;
        } else if (value.equals(AppSelectListPreference.VOLUME_UP_ENTRY)) {
            OmniVibe.performHapticFeedbackLw(HapticFeedbackConstants.LONG_PRESS, false, mContext);
            mAudioManager.adjustSuggestedStreamVolume(AudioManager.ADJUST_RAISE,AudioManager.USE_DEFAULT_STREAM_TYPE,AudioManager.FLAG_SHOW_UI);
            return true;
        } else if (value.equals(AppSelectListPreference.VOLUME_DOWN_ENTRY)) {
            OmniVibe.performHapticFeedbackLw(HapticFeedbackConstants.LONG_PRESS, false, mContext);
            mAudioManager.adjustSuggestedStreamVolume(AudioManager.ADJUST_LOWER,AudioManager.USE_DEFAULT_STREAM_TYPE,AudioManager.FLAG_SHOW_UI);
            return true;
        } else if (value.equals(AppSelectListPreference.BROWSE_SCROLL_DOWN_ENTRY)) {
            OmniVibe.performHapticFeedbackLw(HapticFeedbackConstants.LONG_PRESS, false, mContext);
            OmniUtils.sendKeycode(KeyEvent.KEYCODE_PAGE_DOWN);
            return true;
        } else if (value.equals(AppSelectListPreference.BROWSE_SCROLL_UP_ENTRY)) {
            OmniVibe.performHapticFeedbackLw(HapticFeedbackConstants.LONG_PRESS, false, mContext);
            OmniUtils.sendKeycode(KeyEvent.KEYCODE_PAGE_UP);
            return true;
        } else if (value.equals(AppSelectListPreference.NAVIGATE_BACK_ENTRY)) {
            OmniVibe.performHapticFeedbackLw(HapticFeedbackConstants.LONG_PRESS, false, mContext);
            OmniUtils.sendKeycode(KeyEvent.KEYCODE_BACK);
            return true;
        } else if (value.equals(AppSelectListPreference.NAVIGATE_HOME_ENTRY)) {
            OmniVibe.performHapticFeedbackLw(HapticFeedbackConstants.LONG_PRESS, false, mContext);
            OmniUtils.sendKeycode(KeyEvent.KEYCODE_HOME);
            return true;
        } else if (value.equals(AppSelectListPreference.NAVIGATE_RECENT_ENTRY)) {
            OmniVibe.performHapticFeedbackLw(HapticFeedbackConstants.LONG_PRESS, false, mContext);
            OmniUtils.sendKeycode(KeyEvent.KEYCODE_APP_SWITCH);
            return true;
        }
        return false;
    }

    private String getGestureValueForScanCode(int scanCode) {
        switch(scanCode) {
            case GESTURE_II_SCANCODE:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_0, UserHandle.USER_CURRENT);
            case GESTURE_CIRCLE_SCANCODE:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_1, UserHandle.USER_CURRENT);
            case GESTURE_V_SCANCODE:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_2, UserHandle.USER_CURRENT);
            case GESTURE_A_SCANCODE:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_3, UserHandle.USER_CURRENT);
            case GESTURE_LEFT_V_SCANCODE:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_4, UserHandle.USER_CURRENT);
            case GESTURE_RIGHT_V_SCANCODE:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_5, UserHandle.USER_CURRENT);
            case GESTURE_DOWN_SWIPE_SCANCODE:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_6, UserHandle.USER_CURRENT);
            case GESTURE_UP_SWIPE_SCANCODE:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_7, UserHandle.USER_CURRENT);
            case GESTURE_LEFT_SWIPE_SCANCODE:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_8, UserHandle.USER_CURRENT);
            case GESTURE_RIGHT_SWIPE_SCANCODE:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_9, UserHandle.USER_CURRENT);
        }
        return null;
    }

    private String getGestureValueForFPScanCode(int scanCode) {
        switch(scanCode) {
            case FP_GESTURE_SWIPE_DOWN:
                if (areSystemNavigationKeysEnabled() == false){
                    return Settings.System.getStringForUser(mContext.getContentResolver(),
                       GestureSettings.DEVICE_GESTURE_MAPPING_10, UserHandle.USER_CURRENT);
                }
                break;
            case FP_GESTURE_SWIPE_UP:
                if (areSystemNavigationKeysEnabled() == false){
                    return Settings.System.getStringForUser(mContext.getContentResolver(),
                       GestureSettings.DEVICE_GESTURE_MAPPING_11, UserHandle.USER_CURRENT);
                }
                break;
            case FP_GESTURE_SWIPE_LEFT:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_12, UserHandle.USER_CURRENT);
            case FP_GESTURE_SWIPE_RIGHT:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_13, UserHandle.USER_CURRENT);
            case FP_GESTURE_LONG_PRESS:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_14, UserHandle.USER_CURRENT);
        }
        return null;
    }

    private boolean areSystemNavigationKeysEnabled() {
        return Settings.Secure.getIntForUser(mContext.getContentResolver(),
                Settings.Secure.SYSTEM_NAVIGATION_KEYS_ENABLED, 0, UserHandle.USER_CURRENT) == 1;
    }

    private void launchDozePulse() {
        if (DEBUG) Log.i(TAG, "Doze pulse");
        mContext.sendBroadcastAsUser(new Intent(DOZE_INTENT),
                new UserHandle(UserHandle.USER_CURRENT));
    }

    private boolean enableProxiSensor() {
        return mUsePocketCheck || mUseWaveCheck || mUseProxiCheck;
    }

    private void updateDozeSettings() {
        String value = Settings.System.getStringForUser(mContext.getContentResolver(),
                    Settings.System.OMNI_DEVICE_FEATURE_SETTINGS,
                    UserHandle.USER_CURRENT);
        if (DEBUG) Log.i(TAG, "Doze settings = " + value);
        if (!TextUtils.isEmpty(value)) {
            String[] parts = value.split(":");
            mUseWaveCheck = Boolean.valueOf(parts[0]);
            mUsePocketCheck = Boolean.valueOf(parts[1]);
            mUseTiltCheck = Boolean.valueOf(parts[2]);
        }
    }

    IStatusBarService getStatusBarService() {
        return IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
    }

    protected static Sensor getSensor(SensorManager sm, String type) {
        for (Sensor sensor : sm.getSensorList(Sensor.TYPE_ALL)) {
            if (type.equals(sensor.getStringType())) {
                return sensor;
            }
        }
        return null;
    }

    @Override
    public boolean getCustomProxiIsNear(SensorEvent event) {
        return event.values[0] == 1;
    }

    @Override
    public String getCustomProxiSensor() {
        return "com.oneplus.sensor.pocket";
    }

    /*private void vibe(){
        /*boolean doVibrate = Settings.System.getIntForUser(mContext.getContentResolver(),
                Settings.System.OMNI_DEVICE_GESTURE_FEEDBACK_ENABLED, 0,
                UserHandle.USER_CURRENT) == 1;
        int owningUid;
        String owningPackage;
        
        owningUid = android.os.Process.myUid();
        owningPackage = mContext.getOpPackageName();
        VibrationEffect effect = VibrationEffect.get(VibrationEffect.EFFECT_HEAVY_CLICK);
        //mVibrator.vibrate(owningUid, owningPackage, effect, VIBRATION_ATTRIBUTES);
        //OmniVibe.performHapticFeedback(owningUid, owningPackage, effect, VIBRATION_ATTRIBUTES);
        
        //OmniVibe mOmniVibe = new OmniVibe();
        OmniVibe.performHapticFeedbackLw(HapticFeedbackConstants.LONG_PRESS, false, mContext);
    }*/
    
}
