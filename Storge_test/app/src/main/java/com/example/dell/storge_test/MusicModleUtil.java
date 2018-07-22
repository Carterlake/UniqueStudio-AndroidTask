package com.example.dell.storge_test;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MusicModleUtil {
    public static List<Music> list;
    public static List<Music> parseJOSNWithGSON(String res){
        list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(res);
            String error = jsonObject.getString("showapi_res_error");
            if (!error.equals(":")){
                String body = jsonObject.getString("showapi_res_body");
                JSONObject jsonObject1 = new JSONObject(body);
                String pagebean = jsonObject1.getString("pagebean");
                JSONObject jsonObject2 = new JSONObject(pagebean);
                String songlist = jsonObject2.getString("contentlist");
                JSONArray jsonArray = new JSONArray(songlist);

                for (int i = 0;i < jsonArray.length();i++){
                    Music music = new Music();
                    JSONObject jsonObject3 = jsonArray.getJSONObject(i);
                    music.setUrl(jsonObject3.getString("downUrl"));
                    music.setName(jsonObject3.getString("songname"));
                    music.setSinger(jsonObject3.getString("singername"));
                    music.setAlbum(jsonObject3.getString("albumname"));
                    music.setMusicID(jsonObject3.getString("songmid"));
                    music.setDownloadURI(jsonObject3.getString("downUrl"));
                    list.add(music);
                }


            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }


}
