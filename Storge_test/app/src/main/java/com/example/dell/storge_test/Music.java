package com.example.dell.storge_test;

import org.litepal.crud.DataSupport;

/**
 * Created by DELL on 2018/7/9.
 */

public class Music  extends DataSupport {

    private String title;
    private String singer;
    private String album;
    private String url;
    private Long size;
    private Long time;
    private String name;
    public boolean exist = false;
    private int album_ID;
    private String MusicID;//在QQ音乐的音乐库的ID，通过这个获取歌词
    private String downloadURI;


    public void setDownloadURI(String s){
        downloadURI = s;
    }

    public String getDownloadURI() {
        return downloadURI;
    }


    public void setMusicID(String s){MusicID = s;}
    public String getMusicID(){ return  MusicID; }
    public int getAlbum_ID(){return album_ID;}
    public String getName(){
        return name;
    }
    public String getTitle(){
        return title;
    }
    public String getAlbum(){
        return album;
    }
    public String getUrl(){
        return url;
    }
    public String getSinger(){
        return singer;
    }
    public Long getTime(){
        return time;
    }
    public Long getSize(){
        return size;
    }
    public void setTitle(String t){
        title = t;
    }
    public void setSinger(String s){
        singer = s;
    }
    public void setAlbum(String a){
        album = a;
    }
    public void setUrl(String u){
        url = u;
    }
    public void setAlbum_ID(int I){album_ID = I;}
    public void setName(String n){
        name = n;
    }
    public void setSize(Long s){
        size = s;
    }
    public void setTime(Long t){
        time = t;
    }

}
