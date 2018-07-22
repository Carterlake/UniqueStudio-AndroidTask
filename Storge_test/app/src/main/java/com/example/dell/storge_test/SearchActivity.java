package com.example.dell.storge_test;

import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.dell.storge_test.util.ShowApiRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class SearchActivity extends AppCompatActivity {
private EditText search_exit;
public static Button search_button;
public static Handler mHandler = new Handler();
private MusicOnlineListAdapter adapter;
private RecyclerView search_recylerView;
public static PopupWindow download_popupWindow;
public static Button download_music_button;
public static Button download_lrc_button;
public static List<Music> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        search_button = findViewById(R.id.search_button);
        search_exit = findViewById(R.id.search_exit);
        search_recylerView = findViewById(R.id.search_recyclerView);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String SearchId = search_exit.getText().toString();
                if (SearchId.equals("")){
                    Toast.makeText(SearchActivity.this,"search Id can't be empty!",Toast.LENGTH_SHORT).show();
                }else{
                new Thread(){
                    //在新线程中发送网络请求
                    String SearchId = search_exit.getText().toString();
                    public void run() {
                        String appid="70165";//要替换成自己的
                        String secret="ac7d17f6c73f484ba32c9a4d32297b3b";//要替换成自己的
                        final String res = new ShowApiRequest( "http://route.showapi.com/213-1", appid, secret)
                                .addTextPara("keyword", SearchId)
                                .addTextPara("page", "1")
                                .post();

                        System.out.println(res);
//把返回内容通过handler对象更新到界面
                        mHandler.post(new Thread(){
                            public void run() {
                                LinearLayoutManager layoutManager=new LinearLayoutManager(SearchActivity.this);
                                search_recylerView.setLayoutManager(layoutManager);
                                list = MusicModleUtil.parseJOSNWithGSON(res);
                                adapter = new MusicOnlineListAdapter(SearchActivity.this,list);
                               search_recylerView.setAdapter(adapter);
                            }
                        });


                    }
                }.start();

                }
            }
        });
    }

    public static void downloadFile(String url,final String name){
        Request request = new Request.Builder().url(url).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.i("DOWNLOAD","download failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Sink sink = null;
                BufferedSink bufferedSink = null;
                try {
                    String mSDCardPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/downloadTest";
                    File dest = new File(mSDCardPath,name+".mp3");
                    sink = Okio.sink(dest);
                    bufferedSink = Okio.buffer(sink);
                    bufferedSink.writeAll(response.body().source());

                    bufferedSink.close();
                    Log.i("DOWNLOAD","download success");
                    //Toast.makeText(MainActivity.this)

                }catch (Exception e){
                    e.printStackTrace();
                    Log.i("DOWNLOAD","download failed");
                }finally {
                    if (bufferedSink != null ){
                        bufferedSink.close();
                    }
                }
            }
        });
    }


}
