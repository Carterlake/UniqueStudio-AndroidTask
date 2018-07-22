package com.example.dell.storge_test;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by DELL on 2018/7/9.
 */

public class MusicAdapter  extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    public static List<Music> musicList;
    private Context context;
    public static int position ;
    public static Music music;
    public static int position1;//记录longClick时间得到的position；
    //private AdapterView.OnItemClickListener onClickListener;
    public MusicAdapter(Context  context,List<Music> musicList1){
        this.context = context;
        musicList = musicList1;
    }
    public void setListItem(List<Music> music){
        musicList = music;
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    /*@Override
    public Object getItem(int position) {
        return musicList.get(position);
    }*/

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView Order;
        TextView MusicName;
        TextView MusicianName;
        TextView Music_time;


        public ViewHolder(View view){
            super(view);
            Order = (TextView) view.findViewById(R.id.order);
            MusicName = (TextView)view.findViewById(R.id.music_item_name);
            MusicianName = (TextView)view.findViewById(R.id.music_item_singer);
            Music_time = view.findViewById(R.id.music_item_time);

        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.music_item_singer,parent,false);
        final ViewHolder holder = new ViewHolder(view);
       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               position = holder.getAdapterPosition();
               Music music = musicList.get(position);
               Toast.makeText(v.getContext(),music.getName(),Toast.LENGTH_SHORT).show();
               Intent intent = new Intent(context,MusicService.class);
               intent.putExtra("MusicName",music.getUrl());
               Main2Activity.MusicName.setText(music.getName());
               Main2Activity.MusicianName.setText(music.getSinger());
               Main2Activity.duration_text.setText(toTime(music.getTime()));
               int PLAY = 1;
               intent.putExtra("operation",PLAY);
               context.startService(intent);

               Main2Activity. play_button.setBackgroundResource(R.drawable.ic_pause);


           }
       });

       holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View v) {
               int position1 = holder.getAdapterPosition();
               show_musicLongClickPopUpWindow(position1);
               return false;
           }
       });
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Music music = musicList.get(position);
        holder.Order.setText(""+(position+1));
        holder.MusicName.setText(music.getName());
        holder.MusicianName.setText(music.getSinger());
        holder.Music_time.setText(toTime(music.getTime()));
        holder.itemView.setTag(position);


    }
  /*  @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.music_item_singer,null);
        }
        Music m = musicList.get(position);
        TextView textOrder =(TextView)convertView.findViewById(R.id.order);
        TextView textMusicName = (TextView)convertView.findViewById(R.id.music_item_name);
        TextView textMusicSinger =(TextView)convertView.findViewById(R.id.music_item_singer);
        TextView textMusicTime =(TextView)convertView.findViewById(R.id.music_item_time);
        textOrder.setText(""+ (position+1));
        textMusicSinger.setText(m.getSinger());
        textMusicName.setText(m.getName());
        textMusicTime.setText(toTime(m.getTime()));
        //convertView.setOnClickListener(this);
        return convertView;
    }*/

    public  static String toTime(Long time){
        time /= 1000;
        Long minute = time / 60;
        Long hour = minute /60;
        Long second = time%60;
        return String.format("%02d:%02d",minute,second);
    }
    public void show_musicLongClickPopUpWindow(final int position1){
        View contentView =LayoutInflater.from(context).inflate(R.layout.music_item_long_click_popupwindow_layout,null);
        Main2Activity.MusicItemLongClick_popupWindow = new PopupWindow(contentView,
                WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,true);
        Main2Activity.MusicItemLongClick_popupWindow.setContentView(contentView);
        Main2Activity.showMusicList_button = contentView.findViewById(R.id.add_list_button);
        Main2Activity.showMusicList_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main2Activity.MusicItemLongClick_popupWindow.dismiss();
                show_addList_popupWindow(position1);

            }
        });

        Main2Activity.MusicItemLongClick_popupWindow.showAsDropDown(Main2Activity.current_text);

    }
    public void show_addList_popupWindow(int position1){
        this.position1 =position1;
        View contentView1 =LayoutInflater.from(context).inflate(R.layout.add_list_popupwindow_layout,null);
        Main2Activity.addList_popupWindow = new PopupWindow(contentView1,
                WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,true);
        Main2Activity.addList_popupWindow.setContentView(contentView1);
        Main2Activity.addList_recyclerView = contentView1.findViewById(R.id.addList_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        Main2Activity.addList_recyclerView.setLayoutManager(linearLayoutManager);
        addListAdapter adapter = new addListAdapter(context,MainActivity.MyList_List);
        Main2Activity.addList_recyclerView.setAdapter(adapter);
        Main2Activity.addList_popupWindow.showAsDropDown(Main2Activity.current_text);
    }


    /*public void setOnItemClickListener(OnItemClickListener listener){
        this.onClickListener = listener;
    }
    protected static interface OnItemClickListener{
        void onItemClick(View view,int position);
    }*/
}
