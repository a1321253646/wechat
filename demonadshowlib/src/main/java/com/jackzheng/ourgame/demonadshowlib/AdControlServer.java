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
import okhttp3.Response;

import static com.jackzheng.ourgame.demonadshowlib.HttpUtils.sdkversion;

public class AdControlServer {
    public boolean chaping = true;
    public boolean shiping  = true;
    public boolean banner = true;
    public long adtime   = -1;
    public long bntime   = -1;
    public long cntime   = -1;
    public boolean isGetAdControl = false;
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

    public void getInitDate(String path, Map<String,String> params, Activity context) throws JSONException {
        Map<String,String> allParams = new HashMap<String,String>();
        allParams.putAll(params);
        allParams.put("imsi",TelephoneUtils.getIMSI(context));
        allParams.put("imei",TelephoneUtils.getIMEI(context));
        allParams.put("phoneModel",TelephoneUtils.getSystemModel());
        allParams.put("sysVersion",TelephoneUtils.getSystemVersion());
        allParams.put("packageName",TelephoneUtils.getPackageName(context));
        allParams.put("packageVer",TelephoneUtils.getVersionCode(context));

        final JSONObject jsonObject = TelephoneUtils.getGSMCellLocationInfo(context);
        if (jsonObject != null){

            allParams.put("lac",jsonObject.getString("lac"));
            allParams.put("celid",jsonObject.getString("cid"));
        }
        allParams.put("sdkVersion",sdkversion);
        if (TelephoneUtils.isBackground(context)){
            allParams.put("foreground","0");
            System.out.println("00000================foreground:");
        }else{
            allParams.put("foreground","1");
            System.out.println("11111================foreground:");
        }
        allParams.put("uuid",TelephoneUtils.getUniqueUuid(context));
        String postStr =   mapToJson(allParams);
        final String postString = "dsf=" + URLEncoder.encode(HttpUtils.encryptData(postStr));

        OkHttpUtils
                .post()
                .url(path)
                .addParams("dsf", postString)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("jackzheng","onResponse response="+response);

                        String string = HttpUtils.decryptData(response);
                        if(!TextUtils.isEmpty(string)){
                            try {
                                JSONObject jb = new JSONObject(string);
                                Log.d("jackzheng","onResponse response decryptData="+string);
                                chaping = jb.getInt("chaping")==1;
                                shiping = jb.getInt("shiping")==1;
                                banner = jb.getInt("banner")==1;
                                adtime = jb.getLong("adtime");
                                bntime = jb.getLong("bntime");
                                cntime = jb.getLong("cntime");
                                Log.d("jackzheng","chaping="+chaping+" shiping="+shiping+" banner="+banner+" adtime="+adtime+" bntime="+bntime+" cntime="+cntime);
                                isGetAdControl = true;
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                                isGetAdControl = false;
                            }
                        }
                    }
                });
    }

}
