package com.manishtaraiya.appmute;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.util.Log;


public class MySharePreference {

    public void set_data(Context context, String key, String data) {
        Editor editor = context.getSharedPreferences(context.getApplicationContext().getPackageName(), 0).edit();
        editor.putString(key, data);
        editor.apply();
        log_i("shareprefrerence ", "set data: " + data);
    }

    public String get_data(Context context, String key) {
        log_i("shareprefrerence title: ", context.getApplicationContext().getPackageName());
        String result = context.getSharedPreferences(context.getApplicationContext().getPackageName(), 0).getString(key, "nothing");
        log_i("shareprefrerence test", "get data: " + result);
        return result;
    }

    public void set_data_boolean(Context context, String key, boolean data) {
        Editor editor = context.getSharedPreferences(context.getApplicationContext().getPackageName(), 0).edit();
        editor.putBoolean(key, data);
        editor.apply();
        log_i("shareprefrerence ", "set data: " + data);
    }

    public boolean get_data_boolean(Context context, String key) {
        log_i("shareprefrerence title: ", context.getApplicationContext().getPackageName());
        boolean result = context.getSharedPreferences(context.getApplicationContext().getPackageName(), 0).getBoolean(key, false);
        log_i("shareprefrerence test", "get data: " + result);
        return result;
    }

    public boolean get_data_boolean(Context context, String key, boolean defaultValue) {
        log_i("shareprefrerence title: ", context.getApplicationContext().getPackageName());
        boolean result = context.getSharedPreferences(context.getApplicationContext().getPackageName(), 0).getBoolean(key, defaultValue);
        log_i("shareprefrerence test", "get data: " + result);
        return result;
    }

    public boolean get_data_boolean_defaultTRUE(Context context, String key) {
        log_i("shareprefrerence title: ", context.getApplicationContext().getPackageName());
        boolean result = context.getSharedPreferences(context.getApplicationContext().getPackageName(), 0).getBoolean(key, true);
        log_i("shareprefrerence test", "get data: " + result);
        return result;
    }

    public void set_data_Integer(Context context, String key, int data) {
        Editor editor = context.getSharedPreferences(context.getApplicationContext().getPackageName(), 0).edit();
        editor.putInt(key, data);
        editor.apply();
        log_i("shareprefrerence ", "set data: " + data);
    }

    public int get_data_Integer(Context context, String key) {
        log_i("shareprefrerence title: ", context.getApplicationContext().getPackageName());
        int result = context.getSharedPreferences(context.getApplicationContext().getPackageName(), 0).getInt(key, -1);
        log_i("shareprefrerence test", "get data: " + result);
        return result;
    }

    public int get_data_Integer_with_defaultVal(Context context, String key, int defaultVal) {
        log_i("shareprefrerence title: ", context.getApplicationContext().getPackageName());
        int result = context.getSharedPreferences(context.getApplicationContext().getPackageName(), 0).getInt(key, defaultVal);
       log_i("shareprefrerence test", "get data: " + result);
        return result;
    }

    private static void log_i(String index, String values) {

            //Log.i("Manish", index + " " + values);

    }
}
