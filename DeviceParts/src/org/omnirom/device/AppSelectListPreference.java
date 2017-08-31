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

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AppSelectListPreference extends DialogPreference implements DialogInterface.OnDismissListener {

    private static String TAG = "AppSelectListPreference";

    public static final String TORCH_ENTRY = "torch";
    public static final String DISABLED_ENTRY = "disabled";
    public static final String CAMERA_ENTRY = "camera";
    public static final String MUSIC_PLAY_ENTRY = "music_play";
    public static final String MUSIC_PREV_ENTRY = "music_prev";
    public static final String MUSIC_NEXT_ENTRY = "music_next";

    private ImageView mAppIcon;
    private AppSelectListAdapter mAdapter;
    private Drawable mAppIconDrawable;
    private int mAppIconResourceId;
    private CharSequence mTitle;
    private String mValue;
    private PackageManager mPm;

    public class PackageItem implements Comparable<PackageItem> {
        public final CharSequence mTitle;
        public final int mAppIconResourceId;
        public final ComponentName mComponentName;
        public final String mValue;

        PackageItem(CharSequence title, int iconResourceId, ComponentName componentName) {
            mTitle = title;
            mAppIconResourceId = iconResourceId;
            mComponentName = componentName;
            mValue = componentName.flattenToString();
        }

        PackageItem(CharSequence title, int iconResourceId, String value) {
            mTitle = title;
            mAppIconResourceId = iconResourceId;
            mComponentName = null;
            mValue = value;
        }

        @Override
        public int compareTo(PackageItem another) {
            return mTitle.toString().toUpperCase().compareTo(another.mTitle.toString().toUpperCase());
        }

        @Override
        public int hashCode() {
            return mValue.hashCode();
        }

        @Override
        public boolean equals(Object another) {
            if (another == null || !(another instanceof PackageItem)) {
                return false;
            }
            return mValue.equals(((PackageItem) another).mValue);
        }
    }

    public class AppSelectListAdapter extends BaseAdapter implements Runnable {
        private LayoutInflater mInflater;
        private List<PackageItem> mInstalledPackages = new LinkedList<PackageItem>();

        private final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // now add the special actions on top
                PackageItem cameraItem = new PackageItem(getContext().getResources().getString(R.string.camera_entry),
                        R.drawable.ic_camera, CAMERA_ENTRY);
                mInstalledPackages.add(0, cameraItem);

                PackageItem torchItem = new PackageItem(getContext().getResources().getString(R.string.torch_entry),
                        R.drawable.ic_flashlight, TORCH_ENTRY);
                mInstalledPackages.add(0, torchItem);

                PackageItem musicNextItem = new PackageItem(getContext().getResources().getString(R.string.music_next_entry),
                        R.drawable.ic_music_next, MUSIC_NEXT_ENTRY);
                mInstalledPackages.add(0, musicNextItem);

                PackageItem musicPrevItem = new PackageItem(getContext().getResources().getString(R.string.music_prev_entry),
                        R.drawable.ic_music_prev, MUSIC_PREV_ENTRY);
                mInstalledPackages.add(0, musicPrevItem);

                PackageItem musicPlayItem = new PackageItem(getContext().getResources().getString(R.string.music_play_entry),
                        R.drawable.ic_music_play, MUSIC_PLAY_ENTRY);
                mInstalledPackages.add(0, musicPlayItem);

                PackageItem disabledItem = new PackageItem(getContext().getResources().getString(R.string.disabled_entry),
                        R.drawable.ic_disabled, DISABLED_ENTRY);
                mInstalledPackages.add(0, disabledItem);

                notifyDataSetChanged();
                updatePreferenceViews();
            }
        };

        public AppSelectListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            reloadList();
        }

        @Override
        public int getCount() {
            return mInstalledPackages.size();
        }

        @Override
        public PackageItem getItem(int position) {
            return mInstalledPackages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mInstalledPackages.get(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = mInflater.inflate(R.layout.applist_preference_icon, null, false);
                holder = new ViewHolder();
                convertView.setTag(holder);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            }

            PackageItem applicationInfo = getItem(position);
            holder.title.setText(applicationInfo.mTitle);
            if (applicationInfo.mAppIconResourceId != 0) {
                holder.icon.setImageResource(applicationInfo.mAppIconResourceId);
            } else {
                Drawable d = resolveAppIcon(applicationInfo);
                holder.icon.setImageDrawable(d);
            }
            return convertView;
        }

        private void reloadList() {
            mInstalledPackages.clear();
            new Thread(this).start();
        }

        @Override
        public void run() {
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
                    final PackageItem item = new PackageItem(activity.loadLabel(mPm), 0, componentName);
                    mInstalledPackages.add(item);
                }
            }
            Collections.sort(mInstalledPackages);
            mHandler.obtainMessage(0).sendToTarget();
        }

        private PackageItem resolveApplication(ComponentName componentName) {
            for (PackageItem item : mInstalledPackages) {
                if (item.mComponentName != null && item.mComponentName.equals(componentName)) {
                    return item;
                }
            }
            return null;
        }

        private class ViewHolder {
            TextView title;
            TextView summary;
            ImageView icon;
        }
    }

    public AppSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppSelectListPreference(Context context, int color) {
        super(context, null);
        init();
    }

    private void init() {
        mPm = getContext().getPackageManager();
        setWidgetLayoutResource(R.layout.applist_preference);
        setNegativeButtonText(android.R.string.cancel);
        setPositiveButtonText(null);
        setDialogTitle(R.string.choose_app);
        setDialogIcon(null);
        mAdapter = new AppSelectListAdapter(getContext());
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View v = super.onCreateView(parent);
        mAppIcon = (ImageView) v.findViewById(R.id.app_icon);
        if (mTitle != null) {
            setSummary(mTitle);
        } else {
            setSummary(getContext().getResources().getString(R.string.not_ready_summary));
        }
        if (mAppIconDrawable != null) {
            mAppIcon.setImageDrawable(mAppIconDrawable);
        } else {
            mAppIcon.setImageResource(mAppIconResourceId);
        }
        return v;
    }

    private void updatePreferenceViews() {
        String name = null;
        if (shouldPersist()) {
            name = getPersistedString(null);
        } else {
            name = mValue;
        }

        if (name != null) {
            if (mTitle == null) {
                mAppIconDrawable = null;
                mAppIconResourceId = 0;
                if (name.equals(DISABLED_ENTRY)) {
                    mTitle = getContext().getResources().getString(R.string.disabled_entry);
                    mAppIconResourceId = R.drawable.ic_disabled;
                } else if (name.equals(TORCH_ENTRY)) {
                    mTitle = getContext().getResources().getString(R.string.torch_entry);
                    mAppIconResourceId = R.drawable.ic_flashlight;
                } else if (name.equals(CAMERA_ENTRY)) {
                    mTitle = getContext().getResources().getString(R.string.camera_entry);
                    mAppIconResourceId = R.drawable.ic_camera;
                } else if (name.equals(MUSIC_PLAY_ENTRY)) {
                    mTitle = getContext().getResources().getString(R.string.music_play_entry);
                    mAppIconResourceId = R.drawable.ic_music_play;
                } else if (name.equals(MUSIC_NEXT_ENTRY)) {
                    mTitle = getContext().getResources().getString(R.string.music_next_entry);
                    mAppIconResourceId = R.drawable.ic_music_next;
                } else if (name.equals(MUSIC_PREV_ENTRY)) {
                    mTitle = getContext().getResources().getString(R.string.music_prev_entry);
                    mAppIconResourceId = R.drawable.ic_music_prev;
                } else {
                    ComponentName componentName = ComponentName.unflattenFromString(name);
                    PackageItem item = mAdapter.resolveApplication(componentName);
                    if (item != null) {
                        mTitle = item.mTitle;
                        mAppIconDrawable = resolveAppIcon(item);
                    } else {
                        mTitle = getContext().getResources().getString(R.string.resolve_failed_summary);
                    }
                }
            }
        } else {
            mTitle = getContext().getResources().getString(R.string.disabled_entry);
            mAppIconResourceId = R.drawable.ic_disabled;
        }

        if (mAppIcon != null) {
            setSummary(mTitle);
            if (mAppIconDrawable != null) {
                mAppIcon.setImageDrawable(mAppIconDrawable);
            } else {
                mAppIcon.setImageResource(mAppIconResourceId);
            }
        }
    }

    private Drawable getDefaultActivityIcon() {
        return getContext().getResources().getDrawable(android.R.drawable.sym_def_app_icon);
    }

    @Override
    protected View onCreateDialogView() {
        final ListView list = new ListView(getContext());
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PackageItem info = (PackageItem) parent.getItemAtPosition(position);
                mValue = info.mValue;
                if (shouldPersist()) {
                    persistString(mValue);
                }
                mTitle = info.mTitle;
                mAppIconDrawable = null;
                mAppIconResourceId = 0;
                if (info.mComponentName != null) {
                    mAppIconDrawable = resolveAppIcon(info);
                } else {
                    mAppIconResourceId = info.mAppIconResourceId;
                }

                updatePreferenceViews();
                callChangeListener(mValue);
                getDialog().dismiss();
            }
        });
        return list;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    private Drawable resolveAppIcon(PackageItem item) {
        Drawable appIcon = null;
        try {
            appIcon = mPm.getActivityIcon(item.mComponentName);
            if (appIcon == null) {
                appIcon = getDefaultActivityIcon();
            }
        } catch (Exception e) {
        }
        return appIcon;
    }
}
