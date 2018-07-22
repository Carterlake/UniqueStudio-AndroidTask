package com.example.dell.storge_test;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class

DownloadService extends IntentService {
    private Notification.Builder builder;
    private NotificationManager nfManager;

    public DownloadService(){
        super("");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String id = "Music_player_notification_channel_02";
        CharSequence name =getString(R.string.channel_name2);
        String description = getString(R.string.channel_description2);
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(id,name,importance);
        mChannel.setDescription(description);

        nfManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nfManager.createNotificationChannel(mChannel);
        int notifyID = 2;
        String CHANNEL_ID = "Music_player_notification_channel_02";
         builder = new Notification.Builder(this,CHANNEL_ID);

                builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("正在下载...")
                .setContentText("已下载：")
                .setChannelId(CHANNEL_ID)
                .build();

        nfManager.notify(notifyID,builder.build());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String downloadUri  = intent.getStringExtra("downloadUri");
        final String songid = intent.getStringExtra("songid");
       // String dlU = "http://dl.stream.qqmusic.qq.com/000guIPQ4ZZ7Ky.m4a";
        Request request = new Request.Builder().url(downloadUri).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    File myMusic = new File(Environment.getExternalStorageDirectory()+"/downloadTest","MyMusic");
                    if (!myMusic.exists()){
                        myMusic.mkdirs();
                    }
                    FileOutputStream fos = new FileOutputStream(new File(myMusic,songid+".mp3")) ;
                    InputStream is = response.body().byteStream();
                    byte[] buf = new byte[1024];
                    int len = 0;
                    int currentLenth = 0;
                    int totalLength = (int)response.body().contentLength();
                    while ((len = is.read(buf))!= -1){
                        fos.write(buf,0,len);
                        fos.flush();
                        currentLenth += len;
                        builder.setProgress(totalLength,currentLenth,false);
                        builder.setContentText("已下载："+(int)((currentLenth*1.0f/totalLength)*100)+"%");
                        nfManager.notify(2,builder.build());
                    }
                }
            }
        });
    }
}
