package com.example.dell.storge_test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class addListAdapter extends RecyclerView.Adapter<addListAdapter.ViewHolder>{
    private List<MyMusicList> MyMusicList_List;

    private Context context;
    private int position;
    public addListAdapter(Context  context,List<MyMusicList> myMusicList_List){
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
        TextView addList_ID;


        public ViewHolder(View view){
            super(view);
            addList_ID = (TextView) view.findViewById(R.id.add_list_item_ID);
        }

    }
    @Override
    public addListAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.add_list_item_layout,parent,false);
        final addListAdapter.ViewHolder holder = new addListAdapter.ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               int position1 = holder.getAdapterPosition();
               MyMusicList myMusicList = MainActivity.MyList_List.get(position1);
               if(!myMusicList.getISInitState()){
                   Long position2 =new Long((MusicAdapter.position1));

                   myMusicList.myMusicList.add(position2);

                   myMusicList.changeInitState();

                   myMusicList.save();
                   Toast.makeText(context,"successfully added!!",Toast.LENGTH_SHORT).show();
               }
              else{
                   Long position2 =new Long((MusicAdapter.position1));

                   myMusicList.myMusicList.add(position2);
                   myMusicList.save();
                   Toast.makeText(context,"successfully added!!",Toast.LENGTH_SHORT).show();
               }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(addListAdapter.ViewHolder holder, int position) {
        MyMusicList myMusicList = MyMusicList_List.get(position);
        holder.addList_ID.setText(myMusicList.getID());
        holder.itemView.setTag(position);
    }
}



