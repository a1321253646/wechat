package jackzheng.study.com.wechat.net;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import jackzheng.study.com.wechat.HtmlParse;
import jackzheng.study.com.wechat.sscManager.ServerManager2;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class NetServerControl {
    private static String URL_ROOT = "http://120.79.249.55:9010";
    private void init(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);

    }
    private int mNetId = 1;
    public long getNetId(){
        mNetId++;
        return mNetId;
    }

    public void getRegular(final long netid ,final String user,final String str,final boolean isThird){

        try {
            JSONObject jb = new JSONObject();
            jb.put("user",user);
            jb.put("id",netid);
            jb.put("str",str);
            jb.put("isThird",isThird);
            OkHttpUtils
                    .postString()
                    .url(URL_ROOT+"/ssc")
                    .content(jb.toString())
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .build()
                    .execute(new StringCallback()
                    {

                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ServerManager2.getmIntance().xiazhuFault(netid);
                        }

                        @Override
                        public void onResponse(String response, int id) {

                            try {
                                Gson gson = new Gson();
                                ResponseDateRangeBean result = gson.fromJson(response, ResponseDateRangeBean.class);
                                if(result.date != null){
                                    ServerManager2.getmIntance().xiazhuSuccess(result.id,result.date);
                                }else{
                                    ServerManager2.getmIntance().xiazhuFault(result.id);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }catch(Exception e) {
            e.printStackTrace();

        }
    }


    private NetServerControl(){
        init();
    }

    private static NetServerControl mIntance = new NetServerControl();
    public static NetServerControl getIntance(){
        return mIntance;
    }
}
