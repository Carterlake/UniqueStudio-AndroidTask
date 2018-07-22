package com.example.dell.storge_test;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.storge_test.util.ShowApiRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class MusicOnlineListAdapter extends RecyclerView.Adapter<MusicOnlineListAdapter.ViewHolder> {
    private List<Music> onlineMusicList;

    private Context context;
    private int position;
    public MusicOnlineListAdapter(Context context, List<Music> myMusicList){
        this.context = context;
        onlineMusicList = myMusicList;
    }

    @Override
    public int getItemCount() {
        return onlineMusicList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView MusicName;
        TextView MusicSinger;


        public ViewHolder(View view){
            super(view);
            MusicName = (TextView) view.findViewById(R.id.Music_name);
            MusicSinger = view.findViewById(R.id.music_singer);
        }

    }
    @Override
    public MusicOnlineListAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.search_list_item_layout,parent,false);
        final MusicOnlineListAdapter.ViewHolder holder = new MusicOnlineListAdapter.ViewHolder(view);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                show_download_popupWindow();
                return false;
            }
        });
        return holder;
    }


  private void show_download_popupWindow(){
      final View contentView = LayoutInflater.from(context).inflate(R.layout.search_adapter_popupwindowlayout,null);
      SearchActivity.download_popupWindow = new PopupWindow(contentView,
              WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,true);
      SearchActivity.download_popupWindow.setContentView(contentView);
      SearchActivity.download_music_button = contentView.findViewById(R.id.search_download_muisc);
      SearchActivity.download_lrc_button = contentView.findViewById(R.id.search_download_lrc);
      SearchActivity.download_music_button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Music music =SearchActivity.list.get(position);
              String downloadUri = music.getDownloadURI();
              String name = music.getName();
              SearchActivity.downloadFile(downloadUri,name);
          }
      });

      SearchActivity.download_lrc_button .setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              new Thread(){
                  //在新线程中发送网络请求
                  public void run() {
                      String appid="70165";//要替换成自己的
                      String secret="ac7d17f6c73f484ba32c9a4d32297b3b";//要替换成自己的
                      final Music music =SearchActivity.list.get(position);
                      String ID = music.getMusicID();
                      final String res=new ShowApiRequest( "http://route.showapi.com/213-2", appid, secret)
                              .addTextPara("musicid",ID)
                              .post();

                      System.out.println(res);
//把返回内容通过handler对象更新到界面
                      SearchActivity.mHandler.post(new Thread(){
                          public void run() {
                              String  lrc = MusicLrcUtil.parseJOSNWithGSON(res);
                              Toast.makeText(context,lrc,Toast.LENGTH_SHORT).show();
                              byte[] sourceByte = lrc.getBytes();
                              if (null != sourceByte){
                                  try {
                                      String name = music.getName();
                                      File file = new File("/storage/emulated/0"+"/downloadTest/"+name+".lrc");
                                      if (!file.exists()){
                                          File dir = new File(file.getParent());
                                          dir.mkdirs();
                                          file.createNewFile();
                                      }
                                      FileOutputStream outputStream = new FileOutputStream(file);
                                      outputStream.write(sourceByte);
                                      outputStream.close();
                                      Log.d("download","yes");

                                  }catch (Exception e){
                                      e.printStackTrace();
                                      Log.d("download","fail");
                                  }
                              }
                          }
                      });

                  }
              }.start();



          }
      });

      SearchActivity.download_popupWindow.showAsDropDown(SearchActivity.search_button);
  }

    @Override
    public void onBindViewHolder(MusicOnlineListAdapter.ViewHolder holder, int position) {
        Music music = onlineMusicList.get(position);
        holder.MusicName.setText(music.getName());
        holder.MusicSinger.setText(music.getSinger());
        holder.itemView.setTag(position);
    }
}
