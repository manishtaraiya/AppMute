package com.manishtaraiya.appmute;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static final String selectedAppKey = "selected_app";
    public static final String tapToMute = "Mute manually";
    public static final String tapToUnMute = "Unmute";
    public static final String isManualMasterMuteKey  = "is_manual_master_mute";
    public static final String statusManualMuteButtonKey = "status_manual_mute_button";
    public static final String isInMuteModeKey = "is_in_mute_mode";
    public static final String isToastEnableKey = "is_toast_enable";
    public static final String isAutoMuteEnableKey = "is_auto_mute_enable";
    public static final String statusWidgetMuteButtonKey = "status_widget_mute_button";
    public static final String isTriggeredFromWidget= "is_triggered_from_mute_widget";

    public static final String ringRadioButtonKey = "ring_radio_button_key";
    public static final String notificationRadioButtonKey = "notification_radio_button_key";
    public static final String systemRadioButtonKey = "system_radio_button_key";
    public static final String alarmRadioButtonKey = "alarm_radio_button_key";
    public static final String musicRadioButtonKey = "music_radio_button_key";



    private  ProgressDialog mProgressDialog;


    public static void addRemoveSelectedApp(Context context , String packageName, String appName , boolean state){
        Gson gson = new Gson();
        ApplicationInfoModel applicationInfo = new ApplicationInfoModel(packageName,state,appName);
        MySharePreference sharePreference = new MySharePreference();
        String appList = sharePreference.get_data(context,Utils.selectedAppKey);
        List<ApplicationInfoModel> selectedInfoModels;
        if(!TextUtils.equals(appList,"nothing")) {
            selectedInfoModels = gson.fromJson(appList, new TypeToken<List<ApplicationInfoModel>>() {
            }.getType());
        }else {
            selectedInfoModels = new ArrayList<>();
        }
        if(selectedInfoModels.size() == 0){
            if(state) {
                applicationInfo.setSelected(true);
                selectedInfoModels.add(applicationInfo);
            }
        }else {

            for (int i=0 ; i< selectedInfoModels.size() ; i++){
                if(TextUtils.equals(selectedInfoModels.get(i).getPackageName(),applicationInfo.getPackageName())) {
                    if(state) return;
                    else {
                        selectedInfoModels.remove(i);
                        break;
                    }
                }
            }

            if(state){
                applicationInfo.setSelected(true);
                selectedInfoModels.add(applicationInfo);

            }

        }

        appList = gson.toJson(selectedInfoModels);
        sharePreference.set_data(context,Utils.selectedAppKey,appList);

    }



    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    public  void showProgressDialog(Context ctx, String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(ctx);
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    public  void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public static void vibrate(Context context){

        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if(v!=null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(50);
            }
        }
    }

    public static void showAlertDialog(Context context, String title, String message)
    {
        // Create the object of
        // AlertDialog Builder class
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(context);

        // Set the message show for the Alert time
        builder.setMessage(message);

        // Set Alert Title
        builder.setTitle(title);

        // Set Cancelable false
        // for when the user clicks on the outside
        // the Dialog Box then it will remain show
        builder.setCancelable(true);

        // Set the positive button with yes name
        // OnClickListener method is use of
        // DialogInterface interface.

        builder
                .setPositiveButton(
                        "Ok",
                        new DialogInterface
                                .OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {

                                // When the user click yes button
                                // then app will close
                                dialog.cancel();
                            }
                        });

//        // Set the Negative button with No name
//        // OnClickListener method is use
//        // of DialogInterface interface.
//        builder
//                .setNegativeButton(
//                        "No",
//                        new DialogInterface
//                                .OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog,
//                                                int which)
//                            {
//
//                                // If user click no
//                                // then dialog box is canceled.
//                                dialog.cancel();
//                            }
//                        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();

        // Show the Alert Dialog box
        alertDialog.show();
    }
}
