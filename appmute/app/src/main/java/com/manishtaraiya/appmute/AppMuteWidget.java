package com.manishtaraiya.appmute;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Implementation of App Widget functionality.
 */
public class AppMuteWidget extends AppWidgetProvider {

    static String CLICK_ACTION = "CLICKED";
    static MySharePreference sharePreference = new MySharePreference();
    private static final String TAG = "AppMuteWidget";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Log.v(TAG, "***** onUpdate");

        Intent intent = new Intent(context, AppMuteWidget.class);
        intent.setAction(CLICK_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_mute_widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);
        //boolean isManualMute = sharePreference.get_data_boolean(context,Utils.statusManualMuteButtonKey,false);

//        if(!isManualMute){
//            new SetMasterMute().setMasterMute(true, context);
//            views.setViewVisibility(R.id.widget_image_on_layout, View.GONE);
//            views.setViewVisibility(R.id.widget_image_off_layout, View.VISIBLE);
//        }else {
//            new SetMasterMute().setMasterMute(false, context);
//            views.setViewVisibility(R.id.widget_image_on_layout, View.VISIBLE);
//            views.setViewVisibility(R.id.widget_image_off_layout, View.GONE);
//        }

        boolean isWidgetMute = sharePreference.get_data_boolean(context,Utils.statusWidgetMuteButtonKey,false);
        if(isWidgetMute){
            new SetMasterMute().setMasterMute(true, context);
            views.setViewVisibility(R.id.widget_image_on_layout, View.GONE);
            views.setViewVisibility(R.id.widget_image_off_layout, View.VISIBLE);
        }else {
            new SetMasterMute().setMasterMute(false, context);
            views.setViewVisibility(R.id.widget_image_on_layout, View.VISIBLE);
            views.setViewVisibility(R.id.widget_image_off_layout, View.GONE);
        }
        sharePreference.set_data_boolean(context,Utils.statusManualMuteButtonKey,isWidgetMute);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();

        if (TextUtils.equals(action,CLICK_ACTION)) {
            //Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();
            boolean isWidgetMute = sharePreference.get_data_boolean(context,Utils.statusWidgetMuteButtonKey,false);
            changeWidgetState(context,!isWidgetMute);
            sharePreference.set_data_boolean(context,Utils.statusWidgetMuteButtonKey,!isWidgetMute);

        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.v(TAG, "***** onEnable");
        boolean isWidgetMute = sharePreference.get_data_boolean(context,Utils.statusWidgetMuteButtonKey,false);
        changeWidgetState(context,isWidgetMute);

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        Log.v(TAG, "***** onDisable");
    }

    private void changeWidgetState(Context context ,boolean state ){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_mute_widget);


        if(state){
            new SetMasterMute().setMasterMute(true, context);
            views.setViewVisibility(R.id.widget_image_on_layout, View.GONE);
            views.setViewVisibility(R.id.widget_image_off_layout, View.VISIBLE);
        }else {
            new SetMasterMute().setMasterMute(false, context);
            views.setViewVisibility(R.id.widget_image_on_layout, View.VISIBLE);
            views.setViewVisibility(R.id.widget_image_off_layout, View.GONE);
        }

        sharePreference.set_data_boolean(context,Utils.statusManualMuteButtonKey,state);

        int[] appWidgetId = AppWidgetManager.getInstance(context).getAppWidgetIds(
                new ComponentName(context, AppMuteWidget.class));
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

}

