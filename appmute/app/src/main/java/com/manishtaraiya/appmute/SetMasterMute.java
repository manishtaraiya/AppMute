package com.manishtaraiya.appmute;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import static android.content.Context.NOTIFICATION_SERVICE;

public class SetMasterMute {
    private MySharePreference mySharePreference =  new MySharePreference();
    @SuppressLint({"UseValueOf"})
    public void setMasterMute(boolean flag, Context context) {
        mySharePreference.set_data_boolean(context, Utils.isInMuteModeKey, flag);
        boolean isNotificationON = mySharePreference.get_data_boolean(context, Utils.isNotificationOn, false);
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);


        if(flag && !isNotificationON) {
            mySharePreference.set_data_boolean(context,Utils.isNotificationOn,true);
            appNotification(context, true);
        }else if (!flag && isNotificationON){
            mySharePreference.set_data_boolean(context,Utils.isNotificationOn,false);
            appNotification(context, false);
        }

        try {
            assert mAudioManager != null;
            Method obj =mAudioManager.getClass().getMethod("setMasterMute", Boolean.TYPE, Integer.TYPE);
            Boolean obj1 = flag;
            Integer obj2 = 0;
            obj.invoke(mAudioManager, obj1, obj2);
        } catch (NoSuchMethodException nosuchmethodexception) {
            nosuchmethodexception.printStackTrace();
        } catch (InvocationTargetException invocationtargetexception) {
            invocationtargetexception.printStackTrace();
        } catch (IllegalAccessException illegalaccessexception) {
            illegalaccessexception.printStackTrace();
        }
    }


    private void appNotification(Context context, boolean show) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        int notificationId = 23051990;

        if(show) {


            //new Random().nextInt(9999 - 1) + 1;
            String channelId = "techCdo";
            String channelName = "TechCdoClient";


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                assert notificationManager != null;

                mChannel.setDescription("no sound");
                mChannel.setSound(null,null);
                mChannel.enableLights(false);
                mChannel.setLightColor(Color.BLUE);
                mChannel.enableVibration(false);

                notificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_mute)
                    .setContentTitle("AppMute Running")
                    .setContentText("Click on notification to open app")
                    .setSound(null)
                    .setVibrate(null)
                    .setOngoing(true)
                    .setAutoCancel(false);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(new Intent(context, MainActivity.class));
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            mBuilder.setContentIntent(resultPendingIntent);

            assert notificationManager != null;
            notificationManager.notify(notificationId, mBuilder.build());
        }else {
            assert notificationManager != null;
            notificationManager.cancel(notificationId);
        }

    }

}




