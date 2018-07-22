package com.example.dell.storge_test;

import android.util.Log;

import org.json.JSONObject;

public class MusicLrcUtil {
    public static String parseJOSNWithGSON(String res){
        String lrc = "";
        try {
            JSONObject jsonObject = new JSONObject(res);
            String error = jsonObject.getString("showapi_res_error");
            if (!error.equals(":")){
                String body = jsonObject.getString("showapi_res_body");
                JSONObject jsonObject1 = new JSONObject(body);
                String lyric = jsonObject1.getString("lyric");
                lrc = lyric;
                Log.i("lrc","lrc");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  lrc;
    }
}
