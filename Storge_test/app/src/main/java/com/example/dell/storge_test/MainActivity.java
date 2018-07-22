package com.example.dell.storge_test;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Binder;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {

private Button button2;
private Button button3;
public static Button button_list;
private Music music;
private RecyclerView recyclerView;
public static List<MyMusicList> MyList_List;
public static ListAdapter adapter;
private PopupWindow create_list_popupWindow;
private PopupWindow input_list_popupWindow;
private Button create_list_button;
private EditText input_list_exit;
private Button  input_list_cancel_button;
private Button  input_list_putUp_button;
private MyMusicList myMusicList;
public static PopupWindow listAdapterLongClick_popWindow;
public static Button list_delete_button;
public static PopupWindow delete_list_popupWindow;
public static Button delete_list_cancel_button;
public static Button delete_list_delete_button;
public static int position;
private Button button_deleteAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myTest();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.My_list_recyclerView);
        button2 =findViewById(R.id.Show);
        button3 =findViewById(R.id.My_love);
        button_list = findViewById(R.id.My_list);
        LitePal.getDatabase();
        MyList_List = DataSupport.findAll(MyMusicList.class);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new ListAdapter(this,MyList_List);
        recyclerView.setAdapter(adapter);
        button_deleteAll = findViewById(R.id.deleteAll);
        button_deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast.makeText(MainActivity.this,"",Toast.LENGTH_SHORT).show();
            }
        });

        button_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(),"you clicked MY_LIST!",Toast.LENGTH_SHORT).show();
                showcreatePopupWindow();
            }
        });





        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTest();
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
                Notification.Builder builder = new Notification.Builder(MainActivity.this,CHANNEL_ID);
                RemoteViews notificationViews = new RemoteViews(getPackageName(),R.layout.notification_layout);
                notificationViews.setImageViewResource(R.id.notification_musician_img,R.drawable.circle);
               if(MusicService.ISPLAYING){
                    notificationViews.setImageViewResource(R.id.notification_play_button,R.drawable.ic_pause);
                }else{
                    notificationViews.setImageViewResource(R.id.notification_play_button,R.drawable.ic_play);
                }
                Notification notification =new Notification.Builder(MainActivity.this,CHANNEL_ID)
                        .setSmallIcon(R.drawable.circle1)
                        .setCustomBigContentView(notificationViews)
                        .setChannelId(CHANNEL_ID)
                        .build();
                notification.flags = Notification.FLAG_ONGOING_EVENT;
                 mNotificationManager.notify(notifyID,notification);

                Intent intent = new Intent(MainActivity.this,Main2Activity.class);
                intent.putExtra("musicPosition",0);
                startActivity(intent);
                position  = -1;
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTest();
            }
        });



    }


    public void showcreatePopupWindow(){
        View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.list_create_popupwindow_layout,null);
        create_list_popupWindow =new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,true);
        create_list_popupWindow.setContentView(contentView);
        create_list_button =contentView.findViewById(R.id.list_create_button);
        create_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Do you want to create a new list",Toast.LENGTH_SHORT).show();
                showInputPopupWindow();
                create_list_popupWindow.dismiss();
            }
        });
        create_list_popupWindow.showAsDropDown(button_list);
    }
    public void showInputPopupWindow(){
        View contentView1 =LayoutInflater.from(MainActivity.this).inflate(R.layout.input_popupwindow_layout,null);
        input_list_popupWindow = new PopupWindow(contentView1,WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,true);
        input_list_popupWindow.setContentView(contentView1);

        input_list_exit =contentView1.findViewById(R.id.input_ID_exit);
        input_list_cancel_button =contentView1.findViewById(R.id.input_cancel_create_button);
        input_list_putUp_button = contentView1.findViewById(R.id.input_put_up_button);

        input_list_cancel_button .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_list_popupWindow.dismiss();
            }
        });

        input_list_putUp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = input_list_exit.getText().toString();
                int position = MyList_List.size();
                myMusicList = new MyMusicList();
                myMusicList.setID(id);
                MyList_List.add(myMusicList);
                myMusicList.save() ;
                adapter.notifyItemChanged(position);
                Toast.makeText(MainActivity.this,"list add succsessfully !",Toast.LENGTH_SHORT).show();
                input_list_popupWindow.dismiss();
            }
        });

        input_list_popupWindow.showAsDropDown(button_list);

    }


    public void myTest(){

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
        }
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this,PERMISSIONS_STORAGE2,REQUEST_PERMISSION_CODE);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,PERMISSIONS_INTERNET, REQUEST_PERMISSION_CODE);
        }

   }
    private static  String[] PERMISSIONS_INTERNET = {
            Manifest.permission.INTERNET
    };
    private static int REQUEST_PERMISSION_CODE = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE={
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static  String[] PERMISSIONS_STORAGE2 = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onStart() {
        recyclerView.setAdapter(adapter);
        super.onStart();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE){
            Toast.makeText(MainActivity.this,"sayYESOOOOOOO!!!!!",Toast.LENGTH_SHORT).show();
            for (int i = 0;i < permissions.length;i++){
                Log.i("MainActivity","申请的权限为"+ permissions[i]+",申请结果为："+grantResults[i]);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.turn_to_search,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.turn_to_search_item:
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }
}
