package com.manishtaraiya.appmute;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class AppSelectionActivity extends AppCompatActivity {

    private List<ApplicationInfoModel> applicationInfoModels = new ArrayList<>();
    private static final String TAG = "MainActivity";
    PackageManager pm;
    MySharePreference sharePreference = new MySharePreference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selection);

        /* Enable back arrow */
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        applicationInfoModels.clear();

        pm = getPackageManager();

        updateList();

        AppSelectionAdapter adapter = new AppSelectionAdapter(applicationInfoModels,pm);
        adapter.setHasStableIds(true);

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


    private void updateList(){

        Gson gson = new Gson();
        List<ApplicationInfoModel> applicationInfo = null;
        String appList = sharePreference.get_data(this,Utils.selectedAppKey);
        if(!TextUtils.equals(appList,"nothing")) {

            applicationInfo = gson.fromJson(appList, new TypeToken<List<ApplicationInfoModel>>() {
            }.getType());

        }

        if(applicationInfo != null){
            applicationInfoModels.addAll(applicationInfo);
        }

        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            if(pm.getLaunchIntentForPackage(packageInfo.packageName) !=null) {
                boolean foundInList =false;
                if(applicationInfo != null) {
                    for (ApplicationInfoModel app : applicationInfo) {
                        if(TextUtils.equals(app.getPackageName(),packageInfo.packageName)){
                            foundInList = true;
                            break;
                        }
                    }
                }
                if(!foundInList){
                    applicationInfoModels.add(new ApplicationInfoModel(packageInfo.packageName, false));

                }
            }
        }


    }

}
