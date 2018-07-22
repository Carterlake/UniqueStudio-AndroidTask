package com.example.dell.storge_test;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.app.*;



/**
 * Implementation of App Widget functionality.
 */
public class AppWidget2 extends AppWidgetProvider {
    public static String widget_stop_play = "WIDGET_STOP_PLAY";
    public static final String ACTION_UPDATE_ALL = "android.appwidget.action.click";
    public static String widget_next = "WIDGET_NEXT";
    protected static  String widget_previous =  "WIDGET_PREVIOUS";
    public static RemoteViews widgetViews;
    public static  AppWidgetManager appWidgetManager1;
    public static  int id;
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        appWidgetManager1  = appWidgetManager;
        id = appWidgetId;
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        if (Main2Activity.exist){
        Music music = MusicAdapter.musicList.get(MusicAdapter.position);
        widgetViews = new RemoteViews(context.getPackageName(), R.layout.widgit_layout);
        widgetViews.setTextViewText(R.id.widget_music_name,music.getName());
        if (MusicService.ISPLAYING){
            widgetViews.setImageViewResource(R.id.widget_button_stop_play,R.drawable.ic_pause);}
        else{
        widgetViews.setImageViewResource(R.id.widget_button_stop_play,R.drawable.ic_play);
        }

        Intent widget_stop_play_Intent = new Intent();
        widget_stop_play_Intent.setAction(widget_stop_play );
        PendingIntent pendingStopPlayIntent = PendingIntent.getBroadcast(context,0,widget_stop_play_Intent, PendingIntent.FLAG_CANCEL_CURRENT);
        widgetViews.setOnClickPendingIntent(R.id.widget_button_stop_play,pendingStopPlayIntent);

        Intent widget_next_Intent = new Intent();
        widget_next_Intent.setAction(widget_next);
        PendingIntent pendingNextIntent = PendingIntent.getBroadcast(context,0,widget_next_Intent, PendingIntent.FLAG_CANCEL_CURRENT);
        widgetViews.setOnClickPendingIntent(R.id.widget_button_next,pendingNextIntent);

        Intent widget_previous_Intent = new Intent();
        widget_previous_Intent.setAction(widget_previous);
        PendingIntent pendingPreviousIntent = PendingIntent.getBroadcast(context,0,widget_previous_Intent, PendingIntent.FLAG_CANCEL_CURRENT);
        widgetViews.setOnClickPendingIntent(R.id.widget_button_previous,pendingPreviousIntent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, widgetViews);}

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, widgetViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);
        if (Main2Activity.exist){
        Music music = MusicAdapter.musicList.get(MusicAdapter.position);
        widgetViews = new RemoteViews(context.getPackageName(), R.layout.widgit_layout);
        widgetViews.setTextViewText(R.id.widget_music_name,music.getName());
        if (MusicService.ISPLAYING){
            widgetViews.setImageViewResource(R.id.widget_button_stop_play,R.drawable.ic_pause);}
        else{
            widgetViews.setImageViewResource(R.id.widget_button_stop_play,R.drawable.ic_play);
        }

        /*Intent widget_stop_play_Intent = new Intent();
        widget_stop_play_Intent.setAction(widget_stop_play );
        PendingIntent pendingStopPlayIntent = PendingIntent.getBroadcast(context,0,widget_stop_play_Intent, PendingIntent.FLAG_CANCEL_CURRENT);
        widgetViews.setOnClickPendingIntent(R.id.widget_button_stop_play,pendingStopPlayIntent);*/
        }

       /* Intent widget_next_Intent = new Intent();
        widget_next_Intent.setAction(widget_next);
        PendingIntent pendingNextIntent = PendingIntent.getBroadcast(context,0,widget_next_Intent, PendingIntent.FLAG_CANCEL_CURRENT);
        widgetViews.setOnClickPendingIntent(R.id.widget_button_next,pendingNextIntent);

        Intent widget_previous_Intent = new Intent();
        widget_next_Intent.setAction(widget_previous);
        PendingIntent pendingPreviousIntent = PendingIntent.getBroadcast(context,0,widget_previous_Intent, PendingIntent.FLAG_CANCEL_CURRENT);
        widgetViews.setOnClickPendingIntent(R.id.widget_button_previous,pendingPreviousIntent);*/
        // Instruct the widget manager to update the widget
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

