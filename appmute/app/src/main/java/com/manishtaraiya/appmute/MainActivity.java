package com.manishtaraiya.appmute;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static com.manishtaraiya.appmute.AppMuteWidget.CLICK_ACTION;

public class MainActivity extends AppCompatActivity {


    Toolbar toolbar;
    private static final String TAG = "MainActivity";
    CardView manualMuteButton;
    TextView manualMuteText;
    MySharePreference sharePreference = new MySharePreference();
    List<ApplicationInfoModel> applicationInfoModels = new ArrayList<>();
    AppSelectedAdapter adapter;
    private PackageManager pm;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch toastSwitch, autoMuteEnableSwitch;
    CardView appSelectCard;
    //LinearLayout appListInfoLayout;
    RecyclerView appSelectionRecycleView;
    LinearLayout muteNotificationLayout, enableAutoMuteLayout;
    CheckBox ringCheckBox, alarmCheckBox, systemCheckBox, musicCheckBox, notificationCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.appBar);
        ImageView appBarImage = toolbar.findViewById(R.id.appBarImage);
        RequestOptions options = new RequestOptions()
                .fitCenter();
        Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.app_mute_white)).apply(options).into(appBarImage);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        pm = getPackageManager();
        setUpUi();

    }

    @Override
    protected void onResume() {
        super.onResume();

        updateAppList();

        boolean isManualMute = sharePreference.get_data_boolean(this, Utils.statusManualMuteButtonKey, false);
        changeManualMuteButton(isManualMute);

        boolean isToastEnable = sharePreference.get_data_boolean(MainActivity.this, Utils.isToastEnableKey);
        toastSwitch.setChecked(isToastEnable);


        boolean isAutoMuteEnabled = sharePreference.get_data_boolean(MainActivity.this, Utils.isAutoMuteEnableKey, false);
        boolean isAccessibilityOn = AppMute.isAccessibilitySettingsOn(MainActivity.this);
        if (isAutoMuteEnabled && isAccessibilityOn) {
            autoMuteEnableSwitch.setChecked(true);
            appSelectCard.setVisibility(View.VISIBLE);
            //appListInfoLayout.setVisibility(View.VISIBLE);
            appSelectionRecycleView.setVisibility(View.VISIBLE);
        } else {
            autoMuteEnableSwitch.setChecked(false);
            appSelectCard.setVisibility(View.GONE);
            //appListInfoLayout.setVisibility(View.GONE);
            appSelectionRecycleView.setVisibility(View.GONE);
        }
    }

    public void AppSelectionClicked(View view) {
        startActivity(new Intent(MainActivity.this, AppSelectionActivity.class));

    }


    public void manualMuteClicked(View view) {
        boolean isManualMute = sharePreference.get_data_boolean(this, Utils.statusManualMuteButtonKey, false);
        //change state
        isManualMute = !isManualMute;
        sharePreference.set_data_boolean(this, Utils.statusManualMuteButtonKey, isManualMute);
        changeManualMuteButton(isManualMute);
        if (!isManualMute)
            sharePreference.set_data_boolean(this, Utils.isTriggeredFromWidget, false);
        Utils.vibrate(this);
    }

    private void changeManualMuteButton(boolean status) {

        updateWidget(status);
        //new SetMasterMute().setMasterMute(status,this);

        if (status) {
            manualMuteText.setText(Utils.tapToUnMute);
            manualMuteButton.setCardBackgroundColor(Color.RED);

        } else {
            manualMuteText.setText(Utils.tapToMute);
            manualMuteButton.setCardBackgroundColor(ContextCompat.getColor(this, R.color.green));
        }
        //sharePreference.set_data_boolean(this,Utils.statusManualMuteButtonKey,status);
    }


    private void updateWidget(boolean state) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.app_mute_widget);

        new SetMasterMute().setMasterMute(state, this);

        if (state) {
            views.setViewVisibility(R.id.widget_image_on_layout, View.GONE);
            views.setViewVisibility(R.id.widget_image_off_layout, View.VISIBLE);
        } else {
            views.setViewVisibility(R.id.widget_image_on_layout, View.VISIBLE);
            views.setViewVisibility(R.id.widget_image_off_layout, View.GONE);
        }

        sharePreference.set_data_boolean(this, Utils.statusWidgetMuteButtonKey, state);
        Intent intent = new Intent(this, AppMuteWidget.class);

        intent.setAction(CLICK_ACTION);
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

        int[] appWidgetId = AppWidgetManager.getInstance(this).getAppWidgetIds(
                new ComponentName(this, AppMuteWidget.class));
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void updateAppList() {

        Gson gson = new Gson();
        String appList = sharePreference.get_data(this, Utils.selectedAppKey);
        if (!TextUtils.equals(appList, "nothing")) {


            List<ApplicationInfoModel> applicationInfo = gson.fromJson(appList, new TypeToken<List<ApplicationInfoModel>>() {
            }.getType());
            applicationInfoModels.clear();
            for (ApplicationInfoModel model : applicationInfo) {
                if (!Utils.isPackageInstalled(model.getPackageName(), pm)) {
                    applicationInfo.remove(model);
                    Utils.addRemoveSelectedApp(this, model.getPackageName(), model.getAppName(), false);
                }
            }
            applicationInfoModels.addAll(applicationInfo);

            adapter.notifyDataSetChanged();
        }
    }

    private void setUpUi() {
        manualMuteButton = findViewById(R.id.manualMuteButton);
        manualMuteText = findViewById(R.id.manualMuteText);
        toastSwitch = findViewById(R.id.toastSwitch);
        autoMuteEnableSwitch = findViewById(R.id.autoMuteEnableSwitch);
        appSelectCard = findViewById(R.id.appSelectCard);
        //appListInfoLayout = findViewById(R.id.appListInfoLayout);
        appSelectionRecycleView = findViewById(R.id.appSelectedRecycleView);

        muteNotificationLayout = findViewById(R.id.muteNotificationLayout);
        enableAutoMuteLayout = findViewById(R.id.enableAutoMuteLayout);

        ringCheckBox = findViewById(R.id.ringRadio);
        ringCheckBox.setOnCheckedChangeListener(checkedChangeListener);

        systemCheckBox = findViewById(R.id.systemRadio);
        systemCheckBox.setOnCheckedChangeListener(checkedChangeListener);

        notificationCheckBox = findViewById(R.id.notificationRadio);
        notificationCheckBox.setOnCheckedChangeListener(checkedChangeListener);

        musicCheckBox = findViewById(R.id.musicRadio);
        musicCheckBox.setOnCheckedChangeListener(checkedChangeListener);

        alarmCheckBox = findViewById(R.id.alarmRadio);
        alarmCheckBox.setOnCheckedChangeListener(checkedChangeListener);
        updateRadioButton();


        toastSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = ((Switch) v).isChecked();
                sharePreference.set_data_boolean(MainActivity.this, Utils.isToastEnableKey, isChecked);
                //Toast.makeText(MainActivity.this,"Notification status change to "+isChecked,Toast.LENGTH_SHORT).show();
            }
        });

        muteNotificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = (!(toastSwitch.isChecked()));
                toastSwitch.setChecked(isChecked);
                sharePreference.set_data_boolean(MainActivity.this, Utils.isToastEnableKey, isChecked);
            }
        });

        autoMuteEnableSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = ((Switch) v).isChecked();
                setAutoMuteMode(isChecked);
            }
        });

        enableAutoMuteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = (!(autoMuteEnableSwitch.isChecked()));
                autoMuteEnableSwitch.setChecked(isChecked);
                setAutoMuteMode(isChecked);
            }
        });

        adapter = new AppSelectedAdapter(applicationInfoModels, pm);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, calculateNoOfColumns(this, 100));//new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        appSelectionRecycleView.setLayoutManager(layoutManager);
        appSelectionRecycleView.setItemAnimator(new DefaultItemAnimator());
        appSelectionRecycleView.setHasFixedSize(true);
        appSelectionRecycleView.setNestedScrollingEnabled(false);

        appSelectionRecycleView.setAdapter(adapter);
    }

    private void setAutoMuteMode(boolean state) {
        boolean accessibility_state = AppMute.isAccessibilitySettingsOn(MainActivity.this);
        if (state) {
            if (accessibility_state) {
                sharePreference.set_data_boolean(MainActivity.this, Utils.isAutoMuteEnableKey, true);
                appSelectCard.setVisibility(View.VISIBLE);
                //appListInfoLayout.setVisibility(View.VISIBLE);
                appSelectionRecycleView.setVisibility(View.VISIBLE);
            } else {
                AlertDialogForAccessibility();
            }
        } else {
            sharePreference.set_data_boolean(MainActivity.this, Utils.isAutoMuteEnableKey, false);
            appSelectCard.setVisibility(View.GONE);
            //appListInfoLayout.setVisibility(View.GONE);
            appSelectionRecycleView.setVisibility(View.GONE);
        }
    }

    private void AlertDialogForAccessibility() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Accessibility permission required")
                .setMessage("This app need accessibility permission to enable automatic mute")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivity(intent);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        autoMuteEnableSwitch.setChecked(false);
                    }
                })
                .setIcon(R.drawable.ic_info)
                .show();
    }

    public int calculateNoOfColumns(Context context, int width) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / width);
    }

    public void shareAppClicked(View view) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "AppMute- Camera Mute with widget");
            String shareMessage = "Let me recommend you this application.\n\nAppMute- Camera Mute with widget\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            //e.toString();
        }
    }

    private void updateRadioButton() {
        boolean status = false;
        status = sharePreference.get_data_boolean(this, Utils.ringRadioButtonKey, true);
        ringCheckBox.setChecked(status);
        status = sharePreference.get_data_boolean(this, Utils.notificationRadioButtonKey, true);
        notificationCheckBox.setChecked(status);
        status = sharePreference.get_data_boolean(this, Utils.systemRadioButtonKey, true);
        systemCheckBox.setChecked(status);
        status = sharePreference.get_data_boolean(this, Utils.alarmRadioButtonKey, true);
        alarmCheckBox.setChecked(status);
        status = sharePreference.get_data_boolean(this, Utils.musicRadioButtonKey, true);
        musicCheckBox.setChecked(status);
    }

    private int getRadioEnabledCount() {
        boolean status = false;
        int count = 0;
        status = sharePreference.get_data_boolean(this, Utils.ringRadioButtonKey, true);
        if (status) count++;
        status = sharePreference.get_data_boolean(this, Utils.notificationRadioButtonKey, true);
        if (status) count++;
        status = sharePreference.get_data_boolean(this, Utils.systemRadioButtonKey, true);
        if (status) count++;
        status = sharePreference.get_data_boolean(this, Utils.alarmRadioButtonKey, true);
        if (status) count++;
        status = sharePreference.get_data_boolean(this, Utils.musicRadioButtonKey, true);
        if (status) count++;
        return count;

    }



    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean status) {
            boolean changeBack = false;
            if(!status && getRadioEnabledCount() <=1)
            {
                Utils.showAlertDialog(MainActivity.this,"Alert !","Please select at least 1 mute type");
                changeBack = true;
            }
            switch (compoundButton.getId()) {
                case R.id.ringRadio:
                    if(changeBack){
                        ringCheckBox.setChecked(true);
                    }else {
                        sharePreference.set_data_boolean(MainActivity.this, Utils.ringRadioButtonKey, status);
                    }
                    break;

                case R.id.notificationRadio:
                    if(changeBack){
                        notificationCheckBox.setChecked(true);
                    }else {
                        sharePreference.set_data_boolean(MainActivity.this, Utils.notificationRadioButtonKey, status);
                    }
                    break;

                case R.id.systemRadio:
                    if(changeBack){
                        systemCheckBox.setChecked(true);
                    }else {
                        sharePreference.set_data_boolean(MainActivity.this, Utils.systemRadioButtonKey, status);
                    }
                    break;

                case R.id.alarmRadio:
                    if(changeBack){
                        alarmCheckBox.setChecked(true);
                    }else {
                        sharePreference.set_data_boolean(MainActivity.this, Utils.alarmRadioButtonKey, status);
                    }
                    break;
                case R.id.musicRadio:
                    if(changeBack){
                        musicCheckBox.setChecked(true);
                    }else {
                        sharePreference.set_data_boolean(MainActivity.this, Utils.musicRadioButtonKey, status);
                    }
                    break;
            }
        }
    };


}
