/*
* Copyright (C) 2015 The OmniROM Project
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

import android.app.ActivityManagerNative;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.media.IAudioService;
import android.media.AudioManager;
import android.media.session.MediaSessionLegacyHelper;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManagerGlobal;

import com.android.internal.os.DeviceKeyHandler;
import com.android.internal.util.ArrayUtils;

public class KeyHandler implements DeviceKeyHandler {

    private static final String TAG = KeyHandler.class.getSimpleName();
    private static final boolean DEBUG = true;
    protected static final int GESTURE_REQUEST = 1;
    private static final int GESTURE_WAKELOCK_DURATION = 2000;

    // Supported scancodes
    //#define KEY_GESTURE_CIRCLE      250 // draw circle to lunch camera
    //#define KEY_GESTURE_TWO_SWIPE	251 // swipe two finger vertically to play/pause
    //#define KEY_GESTURE_V           252 // draw v to toggle flashlight
    //#define KEY_GESTURE_LEFT_V      253 // draw left arrow for previous track
    //#define KEY_GESTURE_RIGHT_V     254 // draw right arrow for next track
    //#define MODE_TOTAL_SILENCE 600
    //#define MODE_ALARMS_ONLY 601
    //#define MODE_PRIORITY_ONLY 602
    //#define MODE_NONE 603

    private static final int GESTURE_CIRCLE_SCANCODE = 250;
    private static final int GESTURE_V_SCANCODE = 252;
    private static final int GESTURE_II_SCANCODE = 251;
    private static final int GESTURE_LEFT_V_SCANCODE = 253;
    private static final int GESTURE_RIGHT_V_SCANCODE = 254;
    private static final int KEY_DOUBLE_TAP = 143;
    private static final int KEY_HOME = 102;
    private static final int KEY_BACK = 158;
    private static final int KEY_RECENTS = 580;
    private static final int KEY_MODE_TOTAL_SILENCE = 601;
    //private static final int KEY_MODE_ALARMS_ONLY = 601;
    private static final int KEY_MODE_PRIORITY_ONLY = 602;
    private static final int KEY_MODE_NONE = 603;

    private static final int[] sSupportedGestures = new int[]{
        GESTURE_CIRCLE_SCANCODE,
        GESTURE_V_SCANCODE,
        KEY_DOUBLE_TAP,
        GESTURE_II_SCANCODE,
        GESTURE_LEFT_V_SCANCODE,
        GESTURE_RIGHT_V_SCANCODE,
        KEY_MODE_TOTAL_SILENCE,
        //KEY_MODE_ALARMS_ONLY,
        KEY_MODE_PRIORITY_ONLY,
        KEY_MODE_NONE
    };

    private static final int[] sHandledGestures = new int[]{
        GESTURE_V_SCANCODE,
        GESTURE_II_SCANCODE,
        GESTURE_LEFT_V_SCANCODE,
        GESTURE_RIGHT_V_SCANCODE,
        KEY_MODE_TOTAL_SILENCE,
        //KEY_MODE_ALARMS_ONLY,
        KEY_MODE_PRIORITY_ONLY,
        KEY_MODE_NONE
    };

    private static final int[] sDisabledButtons = new int[]{
        KEY_HOME,
        KEY_BACK,
        KEY_RECENTS
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

    private class SettingsObserver extends ContentObserver {
        SettingsObserver(Handler handler) {
            super(handler);
        }

        void observe() {
            mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(
                    Settings.System.HARDWARE_KEYS_DISABLE),
                    false, this);
            update();
        }

        @Override
        public void onChange(boolean selfChange) {
            update();
        }

        public void update() {
            setButtonDisable(mContext);
        }
    }

    public KeyHandler(Context context) {
        mContext = context;
        mEventHandler = new EventHandler();
        mPowerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mGestureWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "GestureWakeLock");
        mSettingsObserver = new SettingsObserver(mHandler);
        mSettingsObserver.observe();
        mNoMan = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    private class EventHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            KeyEvent event = (KeyEvent) msg.obj;
            switch(event.getScanCode()) {
            case GESTURE_V_SCANCODE:
                if (DEBUG) Log.i(TAG, "GESTURE_V_SCANCODE");
                mGestureWakeLock.acquire(GESTURE_WAKELOCK_DURATION);
                Intent torchIntent = new Intent("com.android.systemui.TOGGLE_FLASHLIGHT");
                torchIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                UserHandle user = new UserHandle(UserHandle.USER_CURRENT);
                mContext.sendBroadcastAsUser(torchIntent, user);
                break;
            case GESTURE_II_SCANCODE:
                if (DEBUG) Log.i(TAG, "GESTURE_II_SCANCODE");
                mGestureWakeLock.acquire(GESTURE_WAKELOCK_DURATION);
                dispatchMediaKeyWithWakeLockToAudioService(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                break;
            case GESTURE_LEFT_V_SCANCODE:
                if (isMusicActive()) {
                    if (DEBUG) Log.i(TAG, "GESTURE_LEFT_V_SCANCODE");
                    mGestureWakeLock.acquire(GESTURE_WAKELOCK_DURATION);
                    dispatchMediaKeyWithWakeLockToAudioService(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                }
                break;
            case GESTURE_RIGHT_V_SCANCODE:
                if (isMusicActive()) {
                    if (DEBUG) Log.i(TAG, "GESTURE_RIGHT_V_SCANCODE");
                    mGestureWakeLock.acquire(GESTURE_WAKELOCK_DURATION);
                    dispatchMediaKeyWithWakeLockToAudioService(KeyEvent.KEYCODE_MEDIA_NEXT);
                }
                break;
            case KEY_MODE_TOTAL_SILENCE:
                if (DEBUG) Log.i(TAG, "KEY_MODE_TOTAL_SILENCE");
                mGestureWakeLock.acquire(GESTURE_WAKELOCK_DURATION);
                if (getSliderMode() == 0) {
                    mNoMan.setZenMode(Global.ZEN_MODE_NO_INTERRUPTIONS, null, TAG);
                } else {
                    mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_SILENT);
                }
                break;
            /*case KEY_MODE_ALARMS_ONLY:
                if (DEBUG) Log.i(TAG, "KEY_MODE_ALARMS_ONLY " + Global.ZEN_MODE_ALARMS);
                mGestureWakeLock.acquire(GESTURE_WAKELOCK_DURATION);
                mNoMan.setZenMode(Global.ZEN_MODE_ALARMS, null, TAG);
                break;*/
            case KEY_MODE_PRIORITY_ONLY:
                if (DEBUG) Log.i(TAG, "KEY_MODE_PRIORITY_ONLY");
                mGestureWakeLock.acquire(GESTURE_WAKELOCK_DURATION);
                if (getSliderMode() == 0) {
                    mNoMan.setZenMode(Global.ZEN_MODE_IMPORTANT_INTERRUPTIONS, null, TAG);
                } else {
                    mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_VIBRATE);
                }
                break;
            case KEY_MODE_NONE:
                if (DEBUG) Log.i(TAG, "KEY_MODE_NONE");
                mGestureWakeLock.acquire(GESTURE_WAKELOCK_DURATION);
                if (getSliderMode() == 0) {
                    mNoMan.setZenMode(Global.ZEN_MODE_OFF, null, TAG);
                } else {
                    mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_NORMAL);
                }
                break;
            }
        }
    }

    @Override
    public boolean handleKeyEvent(KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_UP) {
            return false;
        }

        if (mButtonDisabled) {
            if (DEBUG) Log.i(TAG, "scanCode=" + event.getScanCode());
            if (ArrayUtils.contains(sDisabledButtons, event.getScanCode())) {
                return true;
            }
        }
        boolean isKeySupported = ArrayUtils.contains(sHandledGestures, event.getScanCode());
        if (isKeySupported && !mEventHandler.hasMessages(GESTURE_REQUEST)) {
            if (DEBUG) Log.i(TAG, "scanCode=" + event.getScanCode());
            Message msg = getMessageForKeyEvent(event);
            mEventHandler.sendMessage(msg);
        }
        return isKeySupported;
    }

    @Override
    public boolean canHandleKeyEvent(KeyEvent event) {
        return ArrayUtils.contains(sSupportedGestures, event.getScanCode());
    }

    @Override
    public boolean isDisabledKeyEvent(KeyEvent event) {
        if (mButtonDisabled) {
            if (ArrayUtils.contains(sDisabledButtons, event.getScanCode())) {
                return true;
            }
        }
        return false;
    }

    private Message getMessageForKeyEvent(KeyEvent keyEvent) {
        Message msg = mEventHandler.obtainMessage(GESTURE_REQUEST);
        msg.obj = keyEvent;
        return msg;
    }

    public static void setButtonDisable(Context context) {
        mButtonDisabled = Settings.System.getInt(
                context.getContentResolver(), Settings.System.HARDWARE_KEYS_DISABLE, 0) == 1;
        if (DEBUG) Log.i(TAG, "setButtonDisable=" + mButtonDisabled);
    }

    @Override
    public boolean isCameraLaunchEvent(KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_UP) {
            return false;
        }
        return event.getScanCode() == GESTURE_CIRCLE_SCANCODE;
    }

    @Override
    public boolean isWakeEvent(KeyEvent event){
        if (event.getAction() != KeyEvent.ACTION_UP) {
            return false;
        }
        return event.getScanCode() == KEY_DOUBLE_TAP;
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

    private int getSliderMode() {
        return Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.BUTTON_EXTRA_KEY_MAPPING, 0);
    }

    private boolean swapBackAndRecents() {
        return Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.BUTTON_SWAP_BACK_RECENTS, 0) != 0;
    }

    @Override
    public KeyEvent translateKeyEvent(KeyEvent event) {
        // KeyEvent(long downTime, long eventTime, int action, int code, int repeat, int metaState, int deviceId, int scancode, int flags, int source)
        if (event.getScanCode() == KEY_BACK) {
            if (swapBackAndRecents()) {
                KeyEvent newEvent = new KeyEvent(event.getDownTime(), event.getEventTime(), event.getAction(),
                        KeyEvent.KEYCODE_APP_SWITCH, event.getRepeatCount(), event.getMetaState(), event.getDeviceId(),
                        KEY_RECENTS, event.getFlags(), event.getSource());
                return newEvent;
            }
        }
        if (event.getScanCode() == KEY_RECENTS) {
            if (swapBackAndRecents()) {
                KeyEvent newEvent = new KeyEvent(event.getDownTime(), event.getEventTime(), event.getAction(),
                        KeyEvent.KEYCODE_BACK, event.getRepeatCount(), event.getMetaState(), event.getDeviceId(),
                        KEY_BACK, event.getFlags(), event.getSource());
                return newEvent;
            }
        }
        return null;
    }
}

