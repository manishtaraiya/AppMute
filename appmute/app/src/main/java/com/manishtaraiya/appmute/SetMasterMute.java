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
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import static android.content.Context.NOTIFICATION_SERVICE;

public class SetMasterMute {
    private MySharePreference mySharePreference = new MySharePreference();

    @SuppressLint({"UseValueOf"})
    public void setMasterMute(boolean flag, Context context) {
        mySharePreference.set_data_boolean(context, Utils.isInMuteModeKey, flag);
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        appNotification(context, flag);
        MuteAudio(flag,context);
    }


    public void MuteAudio(boolean muteState , Context context){
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        boolean isWidgetMute = mySharePreference.get_data_boolean(context, Utils.isTriggeredFromWidget, false);
        boolean music = mySharePreference.get_data_boolean(context,Utils.musicRadioButtonKey,true);
        boolean notification = mySharePreference.get_data_boolean(context,Utils.notificationRadioButtonKey,true);
        boolean alarm = mySharePreference.get_data_boolean(context,Utils.alarmRadioButtonKey,true);
        boolean ring = mySharePreference.get_data_boolean(context,Utils.ringRadioButtonKey,true);
        boolean system = mySharePreference.get_data_boolean(context,Utils.systemRadioButtonKey,true);

        if(muteState) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(notification || isWidgetMute) mAudioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0);
                if(alarm || isWidgetMute) mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0);
                if(music || isWidgetMute) mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
                if(ring || isWidgetMute) mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
                if(system || isWidgetMute) mAudioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
            } else {
                if(notification || isWidgetMute) mAudioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
                if(alarm || isWidgetMute) mAudioManager.setStreamMute(AudioManager.STREAM_ALARM, true);
                if(music || isWidgetMute) mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                if(ring || isWidgetMute) mAudioManager.setStreamMute(AudioManager.STREAM_RING, true);
                if(system || isWidgetMute) mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
            }
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(notification || isWidgetMute) mAudioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0);
                if(alarm || isWidgetMute) mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, 0);
                if(music || isWidgetMute) mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
                if(ring || isWidgetMute) mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
                if(system || isWidgetMute) mAudioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
            } else {
                if(notification || isWidgetMute) mAudioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
                if(alarm || isWidgetMute) mAudioManager.setStreamMute(AudioManager.STREAM_ALARM, false);
                if(music || isWidgetMute) mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                if(ring || isWidgetMute) mAudioManager.setStreamMute(AudioManager.STREAM_RING, false);
                if(system || isWidgetMute) mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
            }
        }
    }



    private void appNotification(Context context, boolean show) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        int notificationId = 23051990;

        if (show) {

            String channelId = "AppMute";
            String channelName = "AppMuteChannel";


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                assert notificationManager != null;

                mChannel.setDescription("no sound");
                mChannel.setSound(null, null);
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
        } else {
            assert notificationManager != null;
            notificationManager.cancel(notificationId);
        }

    }

}




