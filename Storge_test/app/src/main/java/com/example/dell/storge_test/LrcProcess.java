package com.example.dell.storge_test;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LrcProcess {
    private List<LrcContent> lrcList;
    private LrcContent mLrcContent;

    public LrcProcess(){
        mLrcContent = new LrcContent();
        lrcList = new ArrayList<LrcContent>();
    }

    public String readLRC(String path){//path是音乐的名字
        StringBuilder stringBuilder = new StringBuilder();
        String abpath = "/storage/emulated/0/Music/Musiclrc/";
        path = path.replace(" - ","-");
        path = path.replace("[mqms2].",".");
        path = path.replace(".mp3",".lrc");
        path = abpath + path;
        File file = new File(path);
      // File file =new File("/storage/emulated/0/Music/Musiclrc/林俊杰-一千年以后.lrc");
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis,"utf-8");
            BufferedReader br = new BufferedReader(isr);
                    String s = "";
            while ((s = br.readLine()) !=null ) {
                s = s.replace("[","");
                s = s.replace("]","@");
                String splitLrcData[] = s.split("@");
                if (splitLrcData.length>1){
                    mLrcContent.setLrcStr(splitLrcData[1]);
                    int lrcTime  = timeToStr(splitLrcData[0]);
                    mLrcContent.setLrcTime(lrcTime);
                    lrcList.add(mLrcContent);
                    mLrcContent = new LrcContent();
                }



            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
            stringBuilder.append("没有读到歌词，赶紧去下载！");
        }catch (IOException e){
            e.printStackTrace();
            stringBuilder.append("没有读到歌词哦！");
        }
        return stringBuilder.toString();
    }
    public int timeToStr(String timeStr){
        timeStr = timeStr.replace(":",".");
        timeStr = timeStr.replace(".","@");
        String timeData[] = timeStr.split("@");

        int minute = Integer.parseInt(timeData[0]);
        int second = Integer.parseInt(timeData[1]);
        int millisecond = Integer.parseInt(timeData[2]);

        int currentTime = (minute*60 + second)*1000 + millisecond;
        return currentTime;
    }
    public List<LrcContent> getLrcList(){
        return lrcList;
    }

}
