package com.example.dell.storge_test;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
    public static ImageButton play_button;
    private RecyclerView listView;
    private LinearLayout turnToPlay_LINNE;
    public static TextView current_text;
    public static TextView duration_text;
    public static TextView MusicName;
    public static TextView MusicianName;
    public static  boolean exist =false;
    public static PopupWindow MusicItemLongClick_popupWindow;
    public static Button showMusicList_button;
    public static PopupWindow addList_popupWindow;
    public static RecyclerView addList_recyclerView;
    private PopupWindow countdown_popupWindow;
    private View contentView;
    private long countdown_time;
    private CountDownTimer timer;
    private Button Countdown_cancel_button;
    private Button Countdown_5min_button;
    private Button Countdown_10min_button;
    private Button Countdown_30min_button;
    private Button Countdown_60min_button;
    private EditText Countdown_input_exit;
    private Button Count_input_button;

    private TextView Countdown_textView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        exist = true;
        setContentView(R.layout.activity_main2);
        play_button =findViewById(R.id.play_button);
        listView = (RecyclerView) this.findViewById(R.id.ListView1);
        turnToPlay_LINNE =findViewById(R.id.turn_to_play);
        MusicName =findViewById(R.id.PlayingMusicName1);
        MusicianName = findViewById(R.id.playing_musician_name);
        current_text =findViewById(R.id.current_text);
        duration_text =findViewById(R.id.duration_text);
        Intent intent =getIntent();
        int position = intent.getIntExtra("musicPostion",0);
        List<Music> listMusic =new ArrayList<Music>();

        listMusic = MusicList.getMusicData(getApplicationContext());
       // DataSupport.saveAll(listMusic);

        List<Music> playMusiclist = new ArrayList<>();
       if (MainActivity.position == -1){
            playMusiclist = DataSupport.findAll(Music.class);
        }
        else{
            List<Long> list = MainActivity.MyList_List.get(MainActivity.position).myMusicList;
            int size=list.size();
            Long[] array = (Long[])list.toArray(new Long[size]);
            long[]array2 =new long[size];
            int t = 0;
            for ( t = 0;t < size;t++){
                array2[t] =array[t]+1;
            }
            playMusiclist =  DataSupport.findAll(Music.class,array2);
        }

        MusicAdapter adapter = new MusicAdapter(this, playMusiclist);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);
        listView.setAdapter(adapter);


        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Main2Activity.exist){
                    Toast.makeText(v.getContext(),"You clicked this button",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Main2Activity.this,MusicService.class);
                    intent.putExtra("operation",4);//4是点击播放停止键的操作代码
                    startService(intent);
                }
                if (MusicService.ISPLAYING){
                       play_button.setBackgroundResource(R.drawable.ic_play);
                }else {
                    play_button.setBackgroundResource(R.drawable.ic_pause);
                }
            }
        });

        turnToPlay_LINNE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this,playActivity.class);
                startActivity(intent);
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.countdown_menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.count_down_item:
                show_countdowm_popupWindow();
                Toast.makeText(this,"you clicked countdown item!",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        exist = true;
       Music music = MusicAdapter.musicList.get(MusicAdapter.position);
        MusicName.setText(music.getName());
        MusicianName.setText(music.getSinger());
        if (MusicService.ISPLAYING){
            play_button.setBackgroundResource(R.drawable.ic_pause);
        }else {
            play_button.setBackgroundResource(R.drawable.ic_play);
        }
    }

    public void show_countdowm_popupWindow(){
         contentView = LayoutInflater.from(this).inflate(R.layout.coundown_popupwindow_layout,null);
        countdown_popupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,true);
        countdown_popupWindow.setContentView(contentView);

        Countdown_cancel_button = contentView.findViewById(R.id.countdown_cancel);
        Countdown_5min_button = contentView.findViewById(R.id.countdown_5_mins);
        Countdown_10min_button =contentView.findViewById(R.id.countdown_10_mins);
        Countdown_30min_button =contentView.findViewById(R.id.countdown_30_mins);
        Countdown_60min_button = contentView.findViewById(R.id.countdown_60_mins);
        Countdown_input_exit =contentView.findViewById(R.id.countdown_input_exit);
        Count_input_button =contentView.findViewById(R.id.countdown_input_button);
        Countdown_textView =contentView.findViewById(R.id.countdown_show_text);
        Countdown_popupwindow_setonClickListener();

        countdown_popupWindow.showAsDropDown(play_button);
    }

    private String toTime(Long time){
        time /= 1000;
        Long minute = time / 60;
        Long hour = minute /60;
        Long second = time%60;
        return String.format("%02d:%02d",minute,second);
    }


    private void init_countdown_timer( final Long time){
        timer = new CountDownTimer(time,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Countdown_textView.setText("倒计时："+ toTime(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                if (Main2Activity.exist) {
                    Intent intent1 = new Intent(Main2Activity.this, MusicService.class);
                    intent1.putExtra("operation", 4);//4是点击播放停止键的操作代码
                    startService(intent1);
                    Toast.makeText(Main2Activity.this, "countdown is over", Toast.LENGTH_SHORT).show();
                }
            }

        };
    }
    public void Countdown_popupwindow_setonClickListener(){
        Countdown_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timer==null){
                    Toast.makeText(Main2Activity.this,"Countdown_time is not init !",Toast.LENGTH_SHORT).show();
                }else {
                    timer.cancel();
                    Countdown_textView.setText("倒计时：00:00");
                    Toast.makeText(Main2Activity.this, "Countdown cancel !", Toast.LENGTH_SHORT).show();

                }
            }
        });
        Countdown_5min_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countdown_time = 5 * 60* 1000;
                init_countdown_timer(countdown_time);
                timer.start();
            }
        });
        Countdown_10min_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countdown_time = 10 * 60* 1000;
                init_countdown_timer(countdown_time);
                timer.start();
            }
        });
        Countdown_30min_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countdown_time = 30 * 60* 1000;
                init_countdown_timer(countdown_time);
                timer.start();
            }
        });

        Countdown_60min_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countdown_time = 60 * 60* 1000;
                init_countdown_timer(countdown_time);
                timer.start();
            }
        });
        Count_input_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String int_put_time_string = Countdown_input_exit.getText().toString();
                if (int_put_time_string.equals("")){
                    Toast.makeText(Main2Activity.this,"please input the countdown time",Toast.LENGTH_SHORT ).show();
                }
                else {
                    long input_time_long = Long.valueOf(int_put_time_string).longValue();
                    countdown_time =input_time_long*60*1000;
                    init_countdown_timer(countdown_time);
                    timer.start();
                    Toast.makeText(Main2Activity.this,"Countdown time is" + toTime(countdown_time),Toast.LENGTH_SHORT).show();

                }
            }
        });



    }
}
