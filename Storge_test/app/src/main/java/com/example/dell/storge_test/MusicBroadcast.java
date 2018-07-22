package com.example.dell.storge_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by DELL on 2018/7/10.
 */

public class MusicBroadcast extends BroadcastReceiver {
    public static final String MUSIC_TIME_ACTION = "com.liuyuchen.musictime";

    @Override
    public void onReceive(Context context, Intent intent) {
        int type = intent.getIntExtra("type",0);
        switch (type){
            case MusicService.DURATION_TYPE:
                int time = intent.getIntExtra("time",0);
                Date dateDuration = new Date(time);
                SimpleDateFormat format = new SimpleDateFormat("mm:ss");
                playActivity.seekBar.setMax(time);
                break;

            case MusicService.CURRENT_TYPE:
                int ctime =intent.getIntExtra("time",0);
                Date dateCurrentTime =new Date(ctime);
                SimpleDateFormat formatCurrent = new SimpleDateFormat("mm:ss");
                Main2Activity.current_text.setText(formatCurrent.format(dateCurrentTime));
                playActivity.CurrentText.setText(formatCurrent.format(dateCurrentTime));
                playActivity.seekBar.setProgress(ctime);
             if (playActivity.lrc_exsit){
                    int i = MusicService.lrcIndex();
                    playActivity.lrcView.setIndex(i);
                    playActivity.lrcView.invalidate();

                }

                break;
            default:
                break;
        }
    }


}
