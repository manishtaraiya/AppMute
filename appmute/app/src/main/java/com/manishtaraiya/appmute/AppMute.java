package com.manishtaraiya.appmute;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppMute extends AccessibilityService {

    private final ArrayList<String> IGNORE_PACKAGE_NAMES = new ArrayList<>(Arrays.asList("com.android.systemui", "com.samsung.android.app.cocktailbarservice", "com.samsung.android.MtpApplication"));

    private final AccessibilityServiceInfo info = new AccessibilityServiceInfo();
    private static final String TAG = "AppMuteAccessibility";

    MySharePreference sharePreference = new MySharePreference();
    private long lastTimestamp = 0;

    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {

        //Log.v(TAG, "Event :: " + event.toString());

        boolean isManualMute = sharePreference.get_data_boolean(this, Utils.statusManualMuteButtonKey, false);

        String packageName = String.valueOf(event.getPackageName());
        if (isManualMute ||
                TextUtils.equals(packageName, "null") ||
                TextUtils.isEmpty(packageName) ||
                IGNORE_PACKAGE_NAMES.contains(packageName)
        ) {
            return;
        }

        boolean isAutoModeEnabled = sharePreference.get_data_boolean(getApplicationContext(), Utils.isAutoMuteEnableKey, false);
        boolean isToastEnabled = sharePreference.get_data_boolean(getApplicationContext(), Utils.isToastEnableKey, false);
        ApplicationInfo appInfo = null;
        final PackageManager pm = getApplicationContext().getPackageManager();
        try {
            appInfo = pm.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert appInfo != null;


        if (isAutoModeEnabled) {
            Gson gson = new Gson();
            String appList = sharePreference.get_data(this, Utils.selectedAppKey);
            List<ApplicationInfoModel> applicationInfo = null;
            if (!TextUtils.equals(appList, "nothing")) {
                applicationInfo = gson.fromJson(appList, new TypeToken<List<ApplicationInfoModel>>() {
                }.getType());
            }

            boolean isInMuteMode = sharePreference.get_data_boolean(this, Utils.isInMuteModeKey, false);
            boolean allowToast = false;
            long timestamp = 0;
            if (applicationInfo != null && applicationInfo.size() > 0) {

                for (ApplicationInfoModel app : applicationInfo) {
                    if (TextUtils.equals(app.getPackageName(), packageName)) {

                        timestamp = System.currentTimeMillis();
                        if (timestamp - lastTimestamp > 1000) {
                            allowToast = true;
                            lastTimestamp = timestamp;
                        }
                        if (isToastEnabled && !isInMuteMode && allowToast) {
                            Toast.makeText(getApplicationContext(), "Automatic Mute Enabled for " + pm.getApplicationLabel(appInfo), Toast.LENGTH_SHORT).show();
                        }
                        if (!isInMuteMode) {
                            new SetMasterMute().setMasterMute(true, this);
                        }
                        return;
                    }

                }
            }
            if (isInMuteMode) {
                new SetMasterMute().setMasterMute(false, this);
            }
            if (isToastEnabled && isInMuteMode) {

                timestamp = System.currentTimeMillis();
                if (timestamp - lastTimestamp > 1000) {
                    allowToast = true;
                    lastTimestamp = timestamp;
                }
                if (allowToast)
                    Toast.makeText(getApplicationContext(), "Automatic Mute Disabled ", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onInterrupt() {
        Log.v(TAG, "onInterrupt");
    }


    @Override
    public void onServiceConnected() {
        Log.v(TAG, "onServiceConnected");

        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED | AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_WINDOWS_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.notificationTimeout = 100;
        if (Build.VERSION.SDK_INT >= 19) {
            info.flags = 2;
        }
        setServiceInfo(info);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Check if Accessibility Service is enabled.
     *
     * @param mContext context
     * @return <code>true</code> if Accessibility Service is ON, otherwise <code>false</code>
     */
    public static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = "com.manishtaraiya.appmute/com.manishtaraiya.appmute.AppMute";


        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "ACCESSIBILITY IS ENABLED");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String str = mStringColonSplitter.next();
                    if (str.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "ACCESSIBILITY IS DISABLED");
        }

        return false;
    }


}
