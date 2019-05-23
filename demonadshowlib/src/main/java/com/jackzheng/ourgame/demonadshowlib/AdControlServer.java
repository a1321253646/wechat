package com.jackzheng.ourgame.demonadshowlib;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


import okhttp3.Call;
import okhttp3.OkHttpClient;

import static com.jackzheng.ourgame.demonadshowlib.HttpUtils.sdkversion;

public class AdControlServer {
    public boolean chaping = true;
    public boolean shiping  = true;
    public boolean banner = false;
    public long adtime   = -1;
    public long bntime   = -1;
    public long cntime   = -1;

    private static  AdControlServer mIntance= new AdControlServer();

    OkHttpClient okHttpClient = null;
    private AdControlServer(){

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);

    }
    public static AdControlServer getmIntance(){
        return mIntance;
    }



    private String mapToJson(Map<String,String>  map){
        JSONObject jsonObject = new JSONObject();
        Set<String> strings = map.keySet();
        try {
            for(String str : strings){
                jsonObject.put(str,map.get(str));
            }
            return jsonObject.toString();
        } catch (JSONException e1) {
            e1.printStackTrace();
            return null;
        }
    }

    public void getInitDate(final String path,final Map<String,String> params,final Activity context) throws JSONException {

        Thread t = new Thread(){
            @Override
            public void run() {
                try {

                    HttpUtils.httpPostJson(path,params,context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

}
