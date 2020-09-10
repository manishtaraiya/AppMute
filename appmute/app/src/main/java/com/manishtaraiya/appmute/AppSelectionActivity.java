package com.manishtaraiya.appmute;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppSelectionActivity extends AppCompatActivity {

    private List<ApplicationSelectionModel> applicationSelectionModels = new ArrayList<>();
    private static final String TAG = "MainActivity";
    PackageManager pm;
    MySharePreference sharePreference = new MySharePreference();
    AppSelectionAdapter adapter;
    Utils utils = new Utils();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selection);

        /* Enable back arrow */
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        applicationSelectionModels.clear();

        pm = getPackageManager();

        //loadApps(applicationSelectionModels);
        //updateList();


        utils.showProgressDialog(AppSelectionActivity.this,"Loading Apps...");
        //new Thread(runnable).start();
        new TaskRunner().executeAsync(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                //applicationSelectionModels = getInstalledApps(false);//loadApps();
                updateList();
                return true;
            }
        }, new TaskRunner.Callback<Boolean>() {
            @Override
            public void onComplete(Boolean result) {
                utils.hideProgressDialog();
                adapter.notifyDataSetChanged();
            }
        });


        adapter = new AppSelectionAdapter(applicationSelectionModels);


        RecyclerView appSelectionRecycleView = findViewById(R.id.appSelectionRecycleView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        appSelectionRecycleView.setLayoutManager(layoutManager);

        appSelectionRecycleView.setItemAnimator(new DefaultItemAnimator());
        appSelectionRecycleView.setHasFixedSize(true);
        appSelectionRecycleView.setNestedScrollingEnabled(false);

        appSelectionRecycleView.setAdapter(adapter);


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }




    public static class TaskRunner {
        private final Executor executor = Executors.newSingleThreadExecutor(); // change according to your requirements
        private final Handler handler = new Handler(Looper.getMainLooper());

        public interface Callback<R> {
            void onComplete(R result);
        }

        public <R> void executeAsync(final Callable<R> callable, final Callback<R> callback) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    final R result;
                    try {
                        result = callable.call();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onComplete(result);
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }


    private void updateList() {

        Gson gson = new Gson();
        List<ApplicationInfoModel> applicationInfo = null;
        String appList = sharePreference.get_data(this, Utils.selectedAppKey);
        if (!TextUtils.equals(appList, "nothing")) {

            applicationInfo = gson.fromJson(appList, new TypeToken<List<ApplicationInfoModel>>() {
            }.getType());

        }

        if (applicationInfo != null && applicationInfo.size() > 0) {

            for (ApplicationInfoModel model : applicationInfo) {
                if (Utils.isPackageInstalled(model.getPackageName(), pm)) {
                    Drawable image = ContextCompat.getDrawable(this, R.drawable.app_mute);
                    try {
                        image = pm.getApplicationIcon(model.getPackageName());
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    applicationSelectionModels.add(new ApplicationSelectionModel(model.getPackageName(), model.isSelected(), model.getAppName(), image));
                }
            }
        }

        List<ApplicationSelectionModel> InfoModels = new ArrayList<>();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            if (pm.getLaunchIntentForPackage(packageInfo.packageName) != null) {
                boolean foundInList = false;
                if (applicationInfo != null && applicationInfo.size() > 0) {
                    for (ApplicationInfoModel app : applicationInfo) {
                        if (TextUtils.equals(app.getPackageName(), packageInfo.packageName)) {
                            foundInList = true;
                            break;
                        }
                    }
                }
                if (!foundInList) {
                    String appName = String.valueOf(pm.getApplicationLabel(packageInfo));
                    Drawable appIcon = null; //= pm.getApplicationIcon(packageInfo);
                    try {
                        appIcon = pm.getApplicationIcon(packageInfo.packageName);

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (appIcon != null)
                        InfoModels.add(new ApplicationSelectionModel(packageInfo.packageName, false, appName, appIcon));
                }
                //Log.i(TAG, String.valueOf(pm.getApplicationLabel(packageInfo)));
            }
        }

        Collections.sort(InfoModels, new Comparator<ApplicationSelectionModel>() {
            public int compare(ApplicationSelectionModel s1, ApplicationSelectionModel s2) {
                // notice the cast to (Integer) to invoke compareTo
                return (s1.getAppName()).compareToIgnoreCase(s2.getAppName());
            }
        });

        applicationSelectionModels.addAll(InfoModels);

    }

}
