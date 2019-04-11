package jackzheng.study.com.wechat;

import android.text.TextUtils;
import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import jackzheng.study.com.wechat.regular.AllXiazuBean;
import jackzheng.study.com.wechat.regular.SscBean;
import jackzheng.study.com.wechat.sscManager.DateSaveManager;
import jackzheng.study.com.wechat.sscManager.ServerManager2;
import okhttp3.Call;
import okhttp3.MediaType;

public class NetServer {

    private static String HOST_ROOT = "http://156.236.71.178:10087/";
    private static String REFUND = HOST_ROOT+"refund";
    private static String BEFORE_LOGIN =  HOST_ROOT+"isLogin";
    private static String TXTBET =  HOST_ROOT+"txtBet";
    private static String LOGIN =  HOST_ROOT+"login";

    public static void loginBefore(){
        try {
            OkHttpUtils
                    .post()
                    .url(BEFORE_LOGIN)
                    .build()
                    .execute(new StringCallback()
                    {

                        @Override
                        public void onError(Call call, Exception e, int id) {


                        }

                        @Override
                        public void onResponse(String response, int id) {

                        }
                    });
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    public static void login(final String user,final String pass,final String url,final String taker){
        try {
            JSONObject jb = new JSONObject();
            jb.put("domain",url);
            jb.put("name",user);
            jb.put("password",pass);
            OkHttpUtils
                    .postString()
                    .url(LOGIN)
                    .content(jb.toString())
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .build()
                    .execute(new StringCallback()
                    {

                        @Override
                        public void onError(Call call, Exception e, int id) {
                            SscControl.getIntance().sendMessage("登陆失败",taker,false);
                        }

                        @Override
                        public void onResponse(String response, int id) {

                            try {
                                JSONObject jb = new JSONObject(response);
                                if(jb.has("code")){
                                    int code = jb.getInt("code");
                                    if(code == 0){
                                        if(jb.has("msg")){
                                            jb = jb.getJSONObject("msg");
                                            if(jb.has("token")){
                                                String token = jb.getString("token");
                                                if(!TextUtils.isEmpty(token)){
                                                    DateSaveManager.getIntance().saveNetInfo(token,user,url);
                                                    DateSaveManager.getIntance().saveNet(true);
                                                    SscControl.getIntance().sendMessage("登陆成功",taker,false);
                                                    return;
                                                }
                                            }

                                        }

                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            SscControl.getIntance().sendMessage("登陆失败",taker,false);
                        }
                    });
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    public static void txtBet(final SscBean date){
        try {
            JSONObject jb = new JSONObject();
            jb.put("token",DateSaveManager.getIntance().mToken);
            jb.put("data",date.getNetDate());
            OkHttpUtils
                    .postString()
                    .url(TXTBET)
                    .content(jb.toString())
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .build()
                    .execute(new StringCallback()
                    {

                        @Override
                        public void onError(Call call, Exception e, int id) {
                            SscControl.getIntance().sendMessage( date.msg+"\n\uE333下驻失败\uE333\n",date.group,false);
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            try {
                                JSONObject jb = new JSONObject(response);
                                if(jb.has("code")){
                                    int code = jb.getInt("code");
                                    if(code == 0){
                                        if(jb.has("msg")){
                                            jb = jb.getJSONObject("msg");
                                            if(jb.has("orderNo")){
                                                String order = jb.getString("orderNo");
                                                if(!TextUtils.isEmpty(order)){
                                                    date.orderNo = order;
                                                    ServerManager2.getmIntance().xiazuSuccess(date);
                                                    if(jb.has("money")){
                                                        double m = jb.getDouble("money");
                                                        if(m != date.count){
                                                            SscControl.getIntance().sendMessage(DateSaveManager.getIntance().getGroup(date.group).groupName+":"+date.msg+"计算出错",DateSaveManager.getIntance().mGuanliQun,false);
                                                        }
                                                    }
                                                    return;
                                                }else{
                                                    SscControl.getIntance().sendMessage("后台出问题",DateSaveManager.getIntance().mGuanliQun,false);
                                                }
                                            }

                                        }
                                    }else{
                                        String msg = jb.getString("msg");
                                        ServerManager2.getmIntance().xiazuFailt(date,msg);
                                        return;
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            SscControl.getIntance().sendMessage( date.msg+"\n\uE333下驻失败\uE333\n",date.group,false);
                        }
                    });
        }catch(Exception e) {
            SscControl.getIntance().sendMessage( date.msg+"\n\uE333下驻失败\uE333\n",date.group,false);
            e.printStackTrace();
        }
    }
    public static void refund(final SscBean date){
        try {
            JSONObject jb = new JSONObject();
            jb.put("orderNo",date.orderNo);
            jb.put("token",DateSaveManager.getIntance().mToken);
            jb.put("issue",date.issue);
            OkHttpUtils
                .postString()
                .url(REFUND)
                .content(jb.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback()
                {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        SscControl.getIntance().sendMessage( date.msg+"\n\uE333退失败\uE333\n",date.group,false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jb = new JSONObject(response);
                            if(jb.has("code")){
                                int code = jb.getInt("code");
                                if(code == 0){
                                    ServerManager2.getmIntance().tuiSuccess(date);
                                    return;
                                }else{
                                    String msg = jb.getString("msg");
                                    SscControl.getIntance().sendMessage( date.msg+"\n\uE333退失败\uE333\n"+msg,date.group,false);
                                    return;
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SscControl.getIntance().sendMessage( date.msg+"\n\uE333退失败\uE333\n",date.group,false);
                    }
                });
        }catch(Exception e) {
            SscControl.getIntance().sendMessage( date.msg+"\n\uE333退失败\uE333\n",date.group,false);
            e.printStackTrace();
        }
    }

}
