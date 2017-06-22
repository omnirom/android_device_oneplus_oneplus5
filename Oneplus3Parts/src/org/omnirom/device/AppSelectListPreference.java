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

    private ImageView mAppIcon;
    private AppSelectListAdapter mAdapter;
    private Drawable mAppIconDrawable;
    private CharSequence mTitle;
    private String mValue;

    public class PackageItem implements Comparable<PackageItem> {
        public final String mPackageName;
        public final CharSequence mTitle;
        public final Drawable mAppIcon;
        public final String mComponentName;

        PackageItem(String packageName, CharSequence title, Drawable icon, String componentName) {
            this.mPackageName = packageName;
            this.mTitle = title;
            this.mAppIcon = icon;
            this.mComponentName = componentName;
        }

        @Override
        public int compareTo(PackageItem another) {
            return mTitle.toString().toUpperCase().compareTo(another.mTitle.toString().toUpperCase());
        }

        @Override
        public int hashCode() {
            return mComponentName.hashCode();
        }

        @Override
        public boolean equals(Object another) {
            if (another == null || !(another instanceof PackageItem)) {
                return false;
            }
            return mComponentName.equalsIgnoreCase(((PackageItem) another).mComponentName);
        }
    }

    public class AppSelectListAdapter extends BaseAdapter implements Runnable {
        private PackageManager mPm;
        private LayoutInflater mInflater;
        private List<PackageItem> mInstalledPackages = new LinkedList<PackageItem>();

        private final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                notifyDataSetChanged();
                updatePreferenceViews();
            }
        };

        public AppSelectListAdapter(Context context) {
            mPm = context.getPackageManager();
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
            holder.icon.setImageDrawable(applicationInfo.mAppIcon);
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
                ComponentName name = new ComponentName(appInfo.packageName, activity.name);
                try {
                    final PackageItem item = new PackageItem(appInfo.packageName,
                            activity.loadLabel(mPm), mPm.getActivityIcon(name), name.flattenToString());
                    mInstalledPackages.add(item);
                } catch (PackageManager.NameNotFoundException e) {
                }
            }
            Collections.sort(mInstalledPackages);
            mHandler.obtainMessage(0).sendToTarget();
        }

        private PackageItem resolveApplication(ComponentName componentName) {
            for (PackageItem item : mInstalledPackages) {
                if (item.mComponentName.equals(componentName.flattenToString())) {
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
        }
        if (mAppIconDrawable != null) {
            mAppIcon.setImageDrawable(mAppIconDrawable);
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
            if (mTitle == null || mAppIconDrawable == null) {
                ComponentName componentName = ComponentName.unflattenFromString(name);
                PackageItem item = mAdapter.resolveApplication(componentName);
                if (item != null) {
                    mTitle = item.mTitle;
                    mAppIconDrawable = item.mAppIcon;
                }
            }
        } else {
            mTitle = null;
            mAppIconDrawable = null;
        }

        if (mAppIcon != null) {
            setSummary(mTitle);
            mAppIcon.setImageDrawable(mAppIconDrawable);
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
                mValue = info.mComponentName;
                if (shouldPersist()) {
                    persistString(mValue);
                }
                mTitle = info.mTitle;
                mAppIconDrawable = info.mAppIcon;
                if (mAppIconDrawable == null) {
                    mAppIconDrawable = getDefaultActivityIcon();
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
}
