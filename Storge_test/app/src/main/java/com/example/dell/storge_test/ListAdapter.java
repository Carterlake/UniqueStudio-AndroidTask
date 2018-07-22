package com.example.dell.storge_test;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static com.example.dell.storge_test.MainActivity.button_list;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{
    private List<MyMusicList> MyMusicList_List;

    private Context context;
    private int position;
    public ListAdapter(Context  context,List<MyMusicList> myMusicList_List){
        this.context = context;
        MyMusicList_List = myMusicList_List;
    }

    @Override
    public int getItemCount() {
        return MyMusicList_List.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView List_ID;
        TextView MusicNum;

        public ViewHolder(View view){
            super(view);
            List_ID = (TextView) view.findViewById(R.id.list_ID);
           MusicNum= (TextView)view.findViewById(R.id.list_music_num);
        }

    }
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.my_list_item_layout,parent,false);
        final ListAdapter.ViewHolder holder = new ListAdapter.ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            position = holder.getAdapterPosition();
           // int Size = MainActivity.MyList_List.get(position).getMyMusicList().size();
            //Toast.makeText(context,""+Size+"首歌",Toast.LENGTH_SHORT).show();
                String id = "Music_player_notification_channel_01";
                CharSequence name =context.getString(R.string.channel_name);
                String description = context.getString(R.string.channel_description);
                int importance = NotificationManager.IMPORTANCE_LOW;
                NotificationChannel mChannel = new NotificationChannel(id,name,importance);
                mChannel.setDescription(description);

                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.createNotificationChannel(mChannel);
                int notifyID = 1;
                String CHANNEL_ID = "Music_player_notification_channel_01";
                Notification.Builder builder = new Notification.Builder(context,CHANNEL_ID);
                RemoteViews notificationViews = new RemoteViews(context.getPackageName(),R.layout.notification_layout);
                notificationViews.setImageViewResource(R.id.notification_musician_img,R.drawable.circle);
                if(MusicService.ISPLAYING){
                    notificationViews.setImageViewResource(R.id.notification_play_button,R.drawable.ic_pause);
                }else{
                    notificationViews.setImageViewResource(R.id.notification_play_button,R.drawable.ic_play);
                }
                Notification notification =new Notification.Builder(context,CHANNEL_ID)
                        .setSmallIcon(R.drawable.circle1)
                        .setCustomBigContentView(notificationViews)
                        .setChannelId(CHANNEL_ID)
                        .build();
                notification.flags = Notification.FLAG_ONGOING_EVENT;
                mNotificationManager.notify(notifyID,notification);

                Intent intent1 = new Intent(context,Main2Activity.class);
                intent1.putExtra("musicPosition",position);
                context.startActivity(intent1);
                MainActivity.position = position;

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position1 = holder.getAdapterPosition();
                show_item_longClick_popupWindow(position1);
                return true;
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ListAdapter.ViewHolder holder, int position) {
        MyMusicList myMusicList = MyMusicList_List.get(position);
        holder.List_ID.setText(myMusicList.getID());
        holder.MusicNum.setText(myMusicList.getMyMusicList().size()+"首歌曲");
        holder.itemView.setTag(position);
    }

    public void show_item_longClick_popupWindow(final int position1){
        final View contentView = LayoutInflater.from(context).inflate(R.layout.list_item_long_click_popupwindow_layout,null);
        MainActivity.listAdapterLongClick_popWindow = new PopupWindow(contentView,
                WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,true);
        MainActivity.listAdapterLongClick_popWindow.setContentView(contentView);
        MainActivity.list_delete_button = contentView.findViewById(R.id.list_delete_button);
        MainActivity.list_delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_delete_list_popupWindow(position1);
                MainActivity.listAdapterLongClick_popWindow.dismiss();
            }
        });
        MainActivity.listAdapterLongClick_popWindow.showAsDropDown(button_list);
    }

    public void  show_delete_list_popupWindow(final int position1){
        View contentView1 =LayoutInflater.from(context).inflate(R.layout.delete_list_popupwindow_layout,null);
        MainActivity.delete_list_popupWindow = new PopupWindow(contentView1,
                WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,true);
        MainActivity.delete_list_popupWindow.setContentView(contentView1);

        MainActivity.delete_list_cancel_button = contentView1.findViewById(R.id.delete_list_cancel_button);
        MainActivity.delete_list_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.delete_list_popupWindow.dismiss();
            }
        });
        MainActivity.delete_list_delete_button = contentView1.findViewById(R.id.delete_list_delete_button);
        MainActivity.delete_list_delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyMusicList myMusicList = MainActivity.MyList_List.get(position1);
                myMusicList.delete();
                MainActivity.MyList_List.remove(position1);
                MainActivity.adapter.notifyItemRemoved(position1);
                MainActivity.adapter.notifyItemRangeChanged(position1,MainActivity.MyList_List.size()-position1+1);
                Toast.makeText(context,"successfully delete" + myMusicList.getID()+" !",Toast.LENGTH_SHORT).show();

                MainActivity.delete_list_popupWindow.dismiss();
            }
        });
        MainActivity.delete_list_popupWindow.showAsDropDown(button_list);
    }


    }



