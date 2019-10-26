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
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

        new loadAppsAsyncTask(applicationSelectionModels).execute();


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

    public void loadApps(List<ApplicationSelectionModel> appModel ) {
        try {
            PackageManager packageManager = getPackageManager();
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);

            for (ResolveInfo resolveInfo : resolveInfoList) {
                ApplicationSelectionModel model = new ApplicationSelectionModel();
                model.setImage(resolveInfo.activityInfo.loadIcon(packageManager));
                model.setAppName(resolveInfo.loadLabel(packageManager).toString());
                model.setPackageName(resolveInfo.activityInfo.packageName);
                appModel.add(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /* renamed from: jp.snowlife01.android.free_mutecamera.AppListActivity$b */
    public class loadAppsAsyncTask extends AsyncTask<String, Integer, String> {
        List<ApplicationSelectionModel> appModel;
        loadAppsAsyncTask(List<ApplicationSelectionModel> appModel) {
            this.appModel = appModel;
        }

        @Override
        protected String doInBackground(String... strings) {
            updateList();
            return "Loaded";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            utils.showProgressDialog(AppSelectionActivity.this,"Loading Apps...");

        }

        @Override
        protected void onPostExecute(String s) {super.onPostExecute(s);
            utils.hideProgressDialog();
            adapter.notifyDataSetChanged();
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

            for(ApplicationInfoModel model:applicationInfo){
                if(Utils.isPackageInstalled(model.getPackageName(),pm)){
                    Drawable image = ContextCompat.getDrawable(this,R.drawable.app_mute);
                    try {
                         image = pm.getApplicationIcon(model.getPackageName());
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    applicationSelectionModels.add(new ApplicationSelectionModel(model.getPackageName(),model.isSelected(),model.getAppName(),image));
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
                    InfoModels.add(new ApplicationSelectionModel(packageInfo.packageName, false,String.valueOf(pm.getApplicationLabel(packageInfo)),pm.getApplicationIcon(packageInfo)));


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
