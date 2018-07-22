package com.example.dell.storge_test;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Random;

public class playActivity extends AppCompatActivity {
    public  static SeekBar seekBar;
    private ImageView MusicPicture;
    public static ImageButton button_play_stop;
    private ImageButton previous;
    private ImageButton next;
    private Button button_change_playing_state;
    public static TextView MusicName;
    public static TextView CurrentText;//设为公共的便于在服务里实时改变
    public static  TextView DurationText;
    private MediaPlayer mediaPlayer;
    private Bitmap bitmap;
    private  int album_ID;
    private TextView lyc_text;
    public static boolean exist = false;
    private PopupWindow lrc_popwindow;
    private TextView lrc_text;
    public  static LrcView lrcView;
    public static  boolean lrc_exsit = false;
    public static ObjectAnimator discObjectAnimator;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exist = true;
        setContentView(R.layout.activity_play);
        findViewById();
        setDiscImage();
        setImageAnimator();

        if (MusicService.ISPLAYING){
            button_play_stop.setBackgroundResource(R.drawable.ic_pause);
            discObjectAnimator.start();
        }else {
            button_play_stop.setBackgroundResource(R.drawable.ic_play);
           discObjectAnimator.cancel();
        }

        final Music music = MusicAdapter.musicList.get(MusicAdapter.position);
        MusicName.setText(music.getName());
        setDurationtext(music);
        setChangeButtonText();//为了使APP的生命周期内changebutton显示正确，此前退出此activity再进入时显示错误

        button_play_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(playActivity.this,MusicService.class);
                intent.putExtra("operation",4);
                startService(intent);
                if (MusicService.ISPLAYING){
                    button_play_stop.setBackgroundResource(R.drawable.ic_play);
                    discObjectAnimator.pause();
                }else {
                    button_play_stop.setBackgroundResource(R.drawable.ic_pause);
                   discObjectAnimator.resume();
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous_music();

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               next_music();
            }
        });


        button_change_playing_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               switch (MusicService.PLAYING_STATE){
                   case 0:
                       MusicService.PLAYING_STATE =1;
                       button_change_playing_state.setText("随机");
                       break;
                   case 1:
                       MusicService.PLAYING_STATE =2;
                       button_change_playing_state.setText("循环");
                       break;
                   case 2:
                       MusicService.PLAYING_STATE = 0;
                       button_change_playing_state.setText("顺序");
                       break;
                   default:
                       break;
               }
            }
        });

        seekBar.setMax(MusicService.player.getDuration());
        seekBar.setProgress(MusicService.player.getCurrentPosition());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    MusicService.player.seekTo(seekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        initBroadcast();

        lyc_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Music music1 = MusicAdapter.musicList.get(MusicAdapter.position);
                String ID = music.getName();
                String ID1 = ID.replace(" - ","-");
                String ID2 = ID1.replace("[mqms2].",".");
                String ID3 = ID2.replace(".mp3",".lrc");
                String path = "/storage/emulated/0/Music/Musiclrc/";
                String path2 = path + ID3;
                //"/storage/emulated/0/Music/Musiclrc/林俊杰-一千年以后.lrc"

                String SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath();

                File f = new File(path2);
                if (f.exists()){
                Toast.makeText(getApplicationContext(),"You Cliked LYC !",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(),SDPATH,Toast.LENGTH_SHORT).show();
                }
            }
        });
        MusicPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Music music1 = MusicAdapter.musicList.get(MusicAdapter.position);
                MusicPicture.setVisibility(View.INVISIBLE);

                View contentView = LayoutInflater.from(playActivity.this).inflate(R.layout.lrc_popupwindow_layout,null);
                lrc_popwindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT,true);
                lrc_popwindow.setContentView(contentView);
                lrc_popwindow.setFocusable(false);
                lrcView = contentView.findViewById(R.id.lrc_View);
                lrc_exsit = true;
                MusicService.initLrc();

                lrcView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lrc_popwindow.dismiss();
                        MusicPicture.setVisibility(View.VISIBLE);
                    }
                });

                View rootview = LayoutInflater.from(playActivity.this).inflate(R.layout.activity_play, null);
                lrc_popwindow.showAtLocation(rootview, Gravity.TOP, 0,300);
            }
        });

    }


    public void initBroadcast(){
        MusicBroadcast musicBroadcast = new MusicBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.liuyuchen.musictime");
        registerReceiver(musicBroadcast,filter);
    }
    public static  void setDurationtext(Music music){
        playActivity.DurationText.setText(MusicAdapter.toTime(music.getTime()));
        Main2Activity.duration_text.setText(MusicAdapter.toTime(music.getTime()));
    }

    public void findViewById(){
        button_change_playing_state = findViewById(R.id.buton_change_playing_state);
        button_play_stop = findViewById(R.id.start_stop);
        next = findViewById(R.id.Music_next);
        previous = findViewById(R.id.Music_previous);
        MusicName = findViewById(R.id.PlayingMusicName2);
        seekBar =findViewById(R.id.seekbar2);
        CurrentText = findViewById(R.id.current_time_2);
        DurationText = findViewById(R.id.full_time_2);
        MusicPicture = findViewById(R.id.music_picture);
        lyc_text =findViewById(R.id.playing_music_lyc_text);

    }
    public void setChangeButtonText(){
        String state;
        switch (MusicService.PLAYING_STATE){
            case 0:
                state ="顺序";
                break;
            case 1:
                state ="随机";
                break;
            case 2:
                state ="循环";
                break;
            default:
                state = "无状态";
                break;
        }
        button_change_playing_state.setText(state);
    }


    public  void next_music(){
        int position2; switch (MusicService.PLAYING_STATE){
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
        Intent intent = new Intent(getApplicationContext(),MusicService.class);
        intent.putExtra("MusicName",music_path);
        intent.putExtra("operation",3);//3是播放下一首的操作代码
        startService(intent);

        MusicName.setText(music.getName());
        setDurationtext(music);
    }

    public void previous_music(){
        int position1;
        switch (MusicService.PLAYING_STATE){
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
        Intent intent = new Intent(getApplicationContext(),MusicService.class);
        intent.putExtra("MusicName",music_path);
        intent.putExtra("operation",2);//2是播放上一首的操作代码
        startService(intent);

        MusicName.setText(music.getName());
        setDurationtext(music);
    }

    public   void music_play_stop(){
        Intent intent = new Intent(getApplicationContext(),MusicService.class);
        intent.putExtra("operation",4);
        startService(intent);

    }

    public void setDiscImage(){
        OvalShape ovalShape0 = new OvalShape();
        ShapeDrawable drawable0 = new ShapeDrawable(ovalShape0);
        drawable0.getPaint().setColor(0x10000000);
        drawable0.getPaint().setStyle(Paint.Style.FILL);
        drawable0.getPaint().setAntiAlias(true);

        //黑色唱片边框
        RoundedBitmapDrawable drawable1 = RoundedBitmapDrawableFactory.create(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.ic_disc));
        drawable1.setCircular(true);
        drawable1.setAntiAlias(true);

        //内层黑色边线
        OvalShape ovalShape2 = new OvalShape();
        ShapeDrawable drawable2 = new ShapeDrawable(ovalShape2);
        drawable2.getPaint().setColor(Color.BLACK);
        drawable2.getPaint().setStyle(Paint.Style.FILL);
        drawable2.getPaint().setAntiAlias(true);

        //最里面的图像
        RoundedBitmapDrawable drawable3 = RoundedBitmapDrawableFactory.create(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.circle3));
        drawable3.setCircular(true);
        drawable3.setAntiAlias(true);

        Drawable[] layers = new Drawable[4];
        layers[0] = drawable0;
        layers[1] = drawable1;
        layers[2] = drawable2;
        layers[3] = drawable3;

        LayerDrawable layerDrawable = new LayerDrawable(layers);

        int width = 10;
        //针对每一个图层进行填充，使得各个圆环之间相互有间隔，否则就重合成一个了。
        //layerDrawable.setLayerInset(0, width, width, width, width);
        layerDrawable.setLayerInset(1, width , width, width, width );
        layerDrawable.setLayerInset(2, width * 11, width * 11, width * 11, width * 11);
        layerDrawable.setLayerInset(3, width * 12, width * 12, width * 12, width * 12);
        MusicPicture.setBackground(layerDrawable);
    }
    public void  setImageAnimator(){
        discObjectAnimator =ObjectAnimator.ofFloat(MusicPicture,"rotation",0,360);
        discObjectAnimator.setDuration(20000);
        discObjectAnimator.setInterpolator(new LinearInterpolator());
        discObjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        discObjectAnimator.setRepeatMode(ValueAnimator.RESTART);
    }
}
