package com.example.dell.storge_test;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.app.*;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener{
    public static boolean ISPLAYING = false;
    public static MediaPlayer player;
    private Notification myNotification;
    public static final int DURATION_TYPE = 1;
    public static final int CURRENT_TYPE = 2;
    private MusicThread thread;
    public static int PLAYING_STATE = 0;//顺序播放为0，随机播放为1，单曲循环为2
    public static String notification_action_previous = "NOTIFICATION_PREVIOUS_MUSIC";
    public static String notification_action_next = "NOTIFICATION_NEXT_MUSIC";
    public static String notification_action_stop_play = "NOTIFICATION_STOP_PLAY_MUSIC";
    public static String notification_action_exit = "NOTIFICATION_EXIT";
    private IntentFilter notification_previous_filter;
    private IntentFilter notification_next_filter;
    private IntentFilter notification_stop_play_filter;
    private IntentFilter notification_exit_filter;
    private IntentFilter widget_stop_play_filter;
    private IntentFilter widget_next_filter;
    private IntentFilter widget_previous_filter;
    public static boolean exist  = false;
    public  static  int i = 0;

    public static LrcProcess mLrcProcess;	//歌词处理
    public static List<LrcContent> lrcList = new ArrayList<LrcContent>(); //存放歌词列表对象
    public static int index = 0;//歌词时间检索
    private Handler handler;


    @Override
    public void onCompletion(MediaPlayer mp) {
        int position;
        switch (PLAYING_STATE) {
            case 0:
                 position = MusicAdapter.position + 1;//列表下一首

                break;
            case 1:
                Random random = new Random();//下一首随机
                position = random.nextInt(MusicAdapter.musicList.size()-1)%(MusicAdapter.musicList.size()) +0;
                break;
            case 2:
                position = MusicAdapter.position;//循环播放
                break;
            default:
                position =MusicAdapter.position;//瞎打的
                break;
        }
        MusicAdapter.position = position;
        Music music = MusicAdapter.musicList.get(position);
        startMusic(music.getUrl());
        setNotification(music);
       // setWidget(music);
        //setNotificationFilter();
       // Music music = MusicAdapter.musicList.get(MusicAdapter.position);
        playActivity.MusicName.setText(music.getName());
        playActivity.DurationText.setText(MusicAdapter.toTime(music.getTime()));
        Main2Activity.duration_text.setText(MusicAdapter.toTime(music.getTime()));
        Main2Activity.MusicianName.setText(music.getSinger());
        Main2Activity.MusicName.setText(music.getName());

    }

    public void changePlayState(){
        if (ISPLAYING){
            ISPLAYING = false;
        }
        else {
            ISPLAYING =  true;
        }

    }
    public int onStartCommand(Intent intent,int flags,int startId){
        setNotificationFilter();
        setWidgetFilter();
        exist = true;
        int operation;
        operation = intent.getIntExtra("operation",0);
        switch (operation){//1是从列表点击播放
            case 1 :       //2是上一首
                            //3是下一首
                startNewMusic(intent);//4是播放或暂停键
                break;
            case 2:
                if (player != null) {
                    player.stop();
                    player=null;
                }
                startNewMusic(intent);
                break;
            case 3:
                if (player != null) {
                    player.stop();
                    player = null;
                }
                startNewMusic(intent);
                break;
            case 4:
                startOrPaused();

                break;
                }


        return super.onStartCommand(intent,flags,startId);
    }

    private void startOrPaused(){

        if (ISPLAYING){
            player.pause();
            if (playActivity.exist){
                playActivity.discObjectAnimator.pause();
                playActivity.button_play_stop.setBackgroundResource(R.drawable.ic_play);
            }

        }else {

            player.start();
            if (playActivity.exist){
                playActivity.discObjectAnimator.resume();
                playActivity.button_play_stop.setBackgroundResource(R.drawable.ic_pause);
            }

           thread =new MusicThread();
           thread.start();

        }
        changePlayState();
        Music music = MusicAdapter.musicList.get(MusicAdapter.position);
        setNotification(music);
        //setWidget(music);
    }

    private void startNewMusic(final Intent intent){
        String musicPath = intent.getStringExtra("MusicName");
        startMusic(musicPath);
    }

    private void startMusic(String musicPath){
        if (player == null){
            player = new MediaPlayer();
        }
        player.setOnCompletionListener(this);
        player.reset();
        try{
            player.setDataSource(musicPath);
            player.prepare();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    player.start();
                    ISPLAYING = true;
                   Music music = MusicAdapter.musicList.get(MusicAdapter.position);
                    setNotification(music);

                    int time = player.getDuration();
                    Intent intent1 =new Intent("com.liuyuchen.musictime");
                    intent1.putExtra("time",time);
                    intent1.putExtra("type",DURATION_TYPE);
                    sendBroadcast(intent1);
                    thread =new MusicThread();
                    thread.start();
                    //setWidget(music);
                }
            });
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    class MusicThread extends Thread{
        @Override
        public void run() {
            while (player.isPlaying()){//每隔一秒发送一次广播提供准确时间
                int time = player.getCurrentPosition();
                int time2 = player.getDuration();

                Intent intent = new Intent("com.liuyuchen.musictime");
                intent.putExtra("time",time);
                intent.putExtra("type",CURRENT_TYPE);
                Intent updateIntent = new Intent();
                updateIntent.setAction(AppWidget2.ACTION_UPDATE_ALL);
                sendBroadcast(updateIntent);
                sendBroadcast(intent);

                if (playActivity.lrc_exsit) {
                     i = lrcIndex();

                    playActivity.lrcView.setIndex(i);
                    playActivity.lrcView.invalidate();
                }
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

            }
        }
    }

    BroadcastReceiver widget_onClick_receiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String widgetAction = intent.getAction();
            if (widgetAction.equals(AppWidget2.widget_stop_play)){
                Log.d("111","111");
                if (Main2Activity.exist) startOrPaused();
            }
           if (widgetAction.equals(AppWidget2.widget_previous)){
               if (Main2Activity.exist) previous_music();
            }
            if (widgetAction.equals(AppWidget2.widget_next)){
                if (Main2Activity.exist)  next_music();
            }

        }
    };


    BroadcastReceiver notification_onClick_receiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String notification_action = intent.getAction();
            if (notification_action.equals(notification_action_next)){
                next_music();
            }
            if (notification_action.equals(notification_action_previous)){
                previous_music();
            }
            if (notification_action.equals(notification_action_stop_play)){
              //  Log.d("fuck","fuck");
                startOrPaused();
            }
            if (notification_action.equals(notification_action_exit)){
               // System.exit(0);
            }
        }
    };


    public void setWidget(Music music){
        if (Main2Activity.exist){
        AppWidget2.widgetViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.widgit_layout);
        AppWidget2.widgetViews.setTextViewText(R.id.widget_music_name,music.getName());
        if (MusicService.ISPLAYING){
            AppWidget2.widgetViews.setImageViewResource(R.id.widget_button_stop_play,R.drawable.ic_pause);
        }
        else{
            AppWidget2.widgetViews.setImageViewResource(R.id.widget_button_stop_play,R.drawable.ic_play);
        }
        AppWidget2.appWidgetManager1.updateAppWidget(AppWidget2.id,AppWidget2.widgetViews);
    }
    }

    public void setNotification(Music music){
        String id = "Music_player_notification_channel_01";
        CharSequence name =getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(id,name,importance);
        mChannel.setDescription(description);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(mChannel);
        int notifyID = 1;
        String CHANNEL_ID = "Music_player_notification_channel_01";
        Notification.Builder builder = new Notification.Builder(this,CHANNEL_ID);
        RemoteViews notificationViews = new RemoteViews(getPackageName(),R.layout.notification_layout);
        notificationViews.setImageViewResource(R.id.notification_musician_img,music.getAlbum_ID());

        if(MusicService.ISPLAYING){
            notificationViews.setImageViewResource(R.id.notification_play_button,R.drawable.ic_pause);
        }else{
            notificationViews.setImageViewResource(R.id.notification_play_button,R.drawable.ic_play);
        }

        notificationViews.setTextViewText(R.id.notification_musician,music.getSinger());//改变通知的文字
        notificationViews.setTextViewText(R.id.notification_music_title,music.getName());

        Notification notification =new Notification.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.circle1)
                .setCustomContentView(notificationViews)
                .setChannelId(CHANNEL_ID)
                .build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        Intent notification_previous_Intent = new Intent(notification_action_previous);//为按钮设置点击事件发送Intent
        PendingIntent pendingPreviousIntent = PendingIntent.getBroadcast(getApplicationContext(),0,notification_previous_Intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notificationViews.setOnClickPendingIntent(R.id.notification_previous_button,pendingPreviousIntent);

        Intent notification_next_Intent = new Intent(notification_action_next);
        PendingIntent pendingNextIntent = PendingIntent.getBroadcast(getApplicationContext(),0,notification_next_Intent,PendingIntent.FLAG_CANCEL_CURRENT);
        notificationViews.setOnClickPendingIntent(R.id.notification_next_button,pendingNextIntent);

        Intent notification_stop_play_Intent = new Intent(notification_action_stop_play);
        PendingIntent pendingStopPlayIntent = PendingIntent.getBroadcast(getApplicationContext(),0,notification_stop_play_Intent,PendingIntent.FLAG_CANCEL_CURRENT);
        notificationViews.setOnClickPendingIntent(R.id.notification_play_button,pendingStopPlayIntent);

        Intent notification_exit_Intent = new Intent(notification_action_exit);
        PendingIntent pendingExitIntent = PendingIntent.getBroadcast(getApplicationContext(),0,notification_exit_Intent,0);
        notificationViews.setOnClickPendingIntent(R.id.notification_exit_button,pendingExitIntent);

        mNotificationManager.notify(notifyID,notification);


    }

    public void setWidgetFilter(){

        widget_stop_play_filter = new IntentFilter();
        widget_stop_play_filter.addAction(AppWidget2.widget_stop_play);
        registerReceiver(widget_onClick_receiver,widget_stop_play_filter);

        widget_previous_filter =new IntentFilter();
        widget_previous_filter.addAction(AppWidget2.widget_previous);
        registerReceiver(widget_onClick_receiver,widget_previous_filter);

        widget_next_filter =new IntentFilter();
        widget_next_filter.addAction(AppWidget2.widget_next);
        registerReceiver(widget_onClick_receiver,widget_next_filter);

    }
    @Override
    public void onDestroy() {
        unregisterReceiver(notification_onClick_receiver);
        unregisterReceiver(widget_onClick_receiver);
        super.onDestroy();
    }
    public void setNotificationFilter(){
         notification_previous_filter = new IntentFilter();
        notification_previous_filter.addAction(notification_action_previous);
        registerReceiver(notification_onClick_receiver,notification_previous_filter);

         notification_next_filter = new IntentFilter();
        notification_next_filter.addAction(notification_action_next);
        registerReceiver(notification_onClick_receiver,notification_next_filter);

         notification_exit_filter = new IntentFilter();
        notification_exit_filter.addAction(notification_action_exit);
        registerReceiver(notification_onClick_receiver,notification_exit_filter);

        notification_stop_play_filter = new IntentFilter();
        notification_stop_play_filter.addAction(notification_action_stop_play);
        registerReceiver(notification_onClick_receiver,notification_stop_play_filter);
    }



    public void previous_music(){
        int position1;
        switch (PLAYING_STATE){
            case 2 ://允许循环播放时点击切歌
            case 0 ://顺序播放

                if (MusicAdapter.position==0){//当歌曲是列表中第一首时，跳到最后一首
                    position1 = MusicAdapter.musicList.size() - 1;
                }else {
                    position1 = MusicAdapter.position - 1;
                }
                break;
            case 1://随机播放，设计为点击也是随机
                Random random = new Random();
                position1 = random.nextInt(MusicAdapter.musicList.size()-1)%(MusicAdapter.musicList.size()) +0;
                break;
            default:
                position1 = MusicAdapter.position - 1;
                break;
        }

        MusicAdapter.position = position1;//当前播放歌曲的位置改变

        Music music = MusicAdapter.musicList.get(position1);
        String music_path = music.getUrl();
        setNotification(music);
        setWidget(music);
        startMusic(music_path);//2是播放上一首的操作代码
        Main2Activity.MusicName.setText(music.getName());
        Main2Activity.duration_text.setText(MusicAdapter.toTime(music.getTime()));
        if (playActivity.exist) {//防止在playActivity未被创造时改变引起程序崩溃
            playActivity.MusicName.setText(music.getName());
            playActivity.DurationText.setText(MusicAdapter.toTime(music.getTime()));}
    }

    public  void next_music(){
        int position2;
        switch (MusicService.PLAYING_STATE){
            case 2 ://允许循环播放时点击切歌
            case 0 ://顺序播放
                if (MusicAdapter.position==MusicAdapter.musicList.size() - 1){//当歌曲是列表中第一首时，跳到最后一首
                    position2 = 0;
                }else {
                    position2 = MusicAdapter.position + 1;
                }
                break;
            case 1://随机播放，设计为点击也是随机
                Random random = new Random();
                position2 = random.nextInt(MusicAdapter.musicList.size()-1)%(MusicAdapter.musicList.size()) +0;
                break;
            default:
                position2 = MusicAdapter.position + 1;
                break;
        }

        MusicAdapter.position = position2;//当前播放歌曲的位置改变
        Music music = MusicAdapter.musicList.get(position2);
        String music_path = music.getUrl();
        setNotification(music);
        setWidget(music);
        startMusic(music_path);
        Main2Activity.MusicName.setText(music.getName());
        Main2Activity.duration_text.setText(MusicAdapter.toTime(music.getTime()));
        if (playActivity.exist) {//防止在playActivity未被创造时改变引起程序崩溃
            playActivity.MusicName.setText(music.getName());
            playActivity.DurationText.setText(MusicAdapter.toTime(music.getTime()));
        }

    }

    public static void initLrc(){
        MusicService.mLrcProcess = new LrcProcess();
        //读取歌词文件
        mLrcProcess.readLRC(MusicAdapter.musicList.get(MusicAdapter.position).getName());
        //传回处理后的歌词文件
        lrcList = mLrcProcess.getLrcList();
        playActivity.lrcView.setmLrcList(lrcList);
        //切换带动画显示歌词
        //playActivity.lrcView.setAnimation(AnimationUtils.loadAnimation(MusicService.this,));

    }

    public  static int lrcIndex() {
        int currentTime;
        int duration;
        if(player.isPlaying()) {
             currentTime = player.getCurrentPosition();
             duration = player.getDuration();
        }else {
            currentTime =player.getCurrentPosition();duration = player.getDuration();
        }
        if(currentTime < duration) {
            for (int i = 0; i < lrcList.size(); i++) {
                if (i < lrcList.size() - 1) {
                    if (currentTime < lrcList.get(i).getLrcTime() && i == 0){
                        index = i;
                        break;
                }
                    if (currentTime > lrcList.get(i).getLrcTime()
                            && currentTime < lrcList.get(i + 1).getLrcTime()){
                        index = i;
                        break;
                    }

                }
                if (i == lrcList.size() - 1
                        && currentTime > lrcList.get(i).getLrcTime()){
                   index = i ;
                    break;
                }
            }
        }
        return index;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}