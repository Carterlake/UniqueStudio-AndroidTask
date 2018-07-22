package com.example.dell.storge_test;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MyMusicList extends DataSupport {
    private String musiclist_iD;
    public List<Long> myMusicList = new ArrayList<Long>();
    private int MusicNum = 0;
    private boolean isInit = false;//是否被初始化了


    public void setID(String ID) {
        this.musiclist_iD = ID;
    }
    public void setMyMusicList(List<Long> myMusicList) {
        this.myMusicList = myMusicList;
        MusicNum++;
    }

    public void changeInitState(){
        isInit =true;
    }
    public boolean getISInitState(){
        return isInit;
    }
    public String getID(){
        return musiclist_iD;
    }
    public List<Long> getMyMusicList(){
        return myMusicList;
    }

    public void removeMusic(int position){
        myMusicList.remove(position);
    }
    public String getMusicNum(){
        return (""+MusicNum);
    }



}
