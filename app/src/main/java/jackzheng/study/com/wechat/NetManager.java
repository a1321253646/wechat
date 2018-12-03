package jackzheng.study.com.wechat;

import android.text.TextUtils;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import de.robv.android.xposed.XposedBridge;
import jackzheng.study.com.wechat.sscManager.ServerManager;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import jackzheng.study.com.wechat.HookUtils;

public class NetManager {

    public String mDeviceID = null;
    public String mGuanliQunID = "Error";
    public int mType = 0;
    public JSONObject mJson = new JSONObject();

    private Map<String,GroupData>  mGroupList= new HashMap<>();
    private ArrayList<String> mGuanliList = new ArrayList<>();
    private ArrayList<RobatType> mRobatList = new ArrayList<>();

    private static NetManager mIntance = new NetManager();
    public static NetManager getIntance(){
        return mIntance;
    }


    private NetManager(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    public void kaijaing(){
        JSONObject js = new JSONObject();
        try {
            String aaaa = "";
            if(HookUtils.getIntance().mIndexMax < 10){
                aaaa = "00";
            }else if (HookUtils.getIntance().mIndexMax < 100 ){
                aaaa = "0";
            }
            js.put("index",aaaa+HookUtils.getIntance().mIndexMax);
            js.put("groupID",mGuanliQunID);
            postData(js,"http://47.107.161.54:8801/opendata.php",new MyStringCall(null,mGuanliQunID,null){
                @Override
                public void onResponseString(String response, int id) {
                    try {
                        JSONObject js = new JSONObject(response);
                        if(js.has("status")){
                            int status = Integer.parseInt(js.getString("status"));
                            String  index = "";
                            String  result = "";
                            if(status == 1){

                                if(js.has("index")){
                                    index = js.getString("index");
                                }else{
                                    return;
                                }
                                if(js.has("result")){
                                    result = js.getString("result");
                                }else{
                                    return;
                                }
                                if(js.has("data")){
                                    JSONArray data = js.getJSONArray("data");
                                    if(data.length() > 0){
                                        for(int i = 0 ;i< data.length() ;i++){
                                            JSONObject jb = data.getJSONObject(i);
                                            String work_id = null;
                                            if(jb.has("work_id")){
                                                work_id = jb.getString("work_id");
                                            }else{
                                                return;
                                            }
                                            int count = -1;
                                            if(jb.has("count")){
                                                count = jb.getInt("count");
                                            }else{
                                                return;
                                            }
                                            String orderSum = null;
                                            if(jb.has("orderSum")){
                                                orderSum = jb.getString("orderSum");
                                            }else{
                                                return;
                                            }
                                            int fen = -1;
                                            if(jb.has("fen")){
                                                fen = jb.getInt("fen");
                                            }else{
                                                return;
                                            }
                                            String chip_in = null;
                                            if(jb.has("chip_in")){
                                                JSONArray js2 = jb.getJSONArray("chip_in");
                                                if(js2.length() >0){
                                                    StringBuilder b = new StringBuilder();
                                                   for(int ii = 0 ;ii < js2.length() ;ii++){
                                                       JSONObject jb2 = js2.getJSONObject(ii);
                                                       int status2  = jb2.getInt("statu");
                                                       if(status2 == 1){
                                                           b.append(jb2.getString("ms")+"【"+jb2.getString("fen")+"】\n");
                                                       }else if(status2 == 2){
                                                           b.append(jb2.getString("ms")+"【失败】\n");
                                                       }else if(status2 == 3){
                                                           b.append(jb2.getString("ms")+"【退码】\n");
                                                       }
                                                   }
                                                    chip_in = b.toString();
                                                }
                                            }
                                            String winning = null;
                                            if(jb.has("winning")){
                                                JSONArray js2 = jb.getJSONArray("winning");
                                                if(js2.length() >0){
                                                    StringBuilder b = new StringBuilder();
                                                    for(int ii = 0 ;ii < js2.length() ;ii++){
                                                        JSONObject jb2 = js2.getJSONObject(ii);
                                                        b.append(jb2.getString("ms")+"【中"+jb2.getInt("count")+"】\n");
                                                    }
                                                    winning = b.toString();
                                                }
                                            }
                                            StringBuilder b2 = new StringBuilder();
                                            b2.append(index);
                                            b2.append("期 开");
                                            b2.append(result+"\n");
                                            if(!TextUtils.isEmpty(chip_in)){
                                                b2.append("--------吓报表-------\n");
                                                b2.append(chip_in);
                                                b2.append("共"+orderSum+"\n");
                                            }
                                            if(!TextUtils.isEmpty(winning)){
                                                b2.append("\n-----仲报表----\n");
                                                b2.append(winning);
                                                b2.append("共仲"+count+" 计"+fen+"米");
                                            }
                                            String ms2 = b2.toString();
                                            ServerManager.getIntance().sendMessage(work_id, ms2,false,false);
                                            String receviDate = getReceviDate(work_id);
                                            if(!TextUtils.isEmpty(receviDate)){
                                                GroupData groupDatById = getGroupDatById(work_id);
                                                ServerManager.getIntance().sendMessage(receviDate,groupDatById.name+"   "+ ms2,false,false);
                                            }
                                        }
                                    }
                                }
                                HookUtils.getIntance().kaijaingEnd();
                            }else if(status == 2){
                                if(js.has("data")){
                                    JSONArray data = js.getJSONArray("data");
                                    if(data.length() > 0){
                                        for(int i = 0 ;i< data.length() ;i++){
                                            JSONObject jb = data.getJSONObject(i);
                                            String work_id = null;
                                            if(jb.has("work_id")){
                                                work_id = jb.getString("work_id");
                                            }else{
                                                return;
                                            }
                                            int count = -1;
                                            if(jb.has("count")){
                                                count = jb.getInt("count");
                                            }else{
                                                return;
                                            }
                                            String orderSum = null;
                                            if(jb.has("orderSum")){
                                                orderSum = jb.getString("orderSum");
                                            }else{
                                                return;
                                            }
                                            int fen = -1;
                                            if(jb.has("fen")){
                                                fen = jb.getInt("fen");
                                            }else{
                                                return;
                                            }
                                            String chip_in = null;
                                            if(jb.has("chip_in")){
                                                JSONArray js2 = jb.getJSONArray("chip_in");
                                                if(js2.length() >0){
                                                    StringBuilder b = new StringBuilder();
                                                    for(int ii = 0 ;ii < js2.length() ;ii++){
                                                        JSONObject jb2 = js2.getJSONObject(ii);
                                                        int status2  = jb2.getInt("statu");
                                                        if(status2 == 1){
                                                            b.append(jb2.getString("ms")+"【"+jb2.getString("fen")+"】\n");
                                                        }else if(status2 == 2){
                                                            b.append(jb2.getString("ms")+"【失败】\n");
                                                        }else if(status2 == 3){
                                                            b.append(jb2.getString("ms")+"【退码】\n");
                                                        }
                                                    }
                                                    chip_in = b.toString();
                                                }
                                            }


                                            StringBuilder b2 = new StringBuilder();
                                            b2.append(HookUtils.getIntance().mIndexMax);
                                            b2.append( "期 开失败  ");
                                            b2.append( index);
                                            b2.append( "期 开 ");
                                            b2.append(result+"\n");
                                            if(!TextUtils.isEmpty(chip_in)){
                                                b2.append("--------吓报表-------\n");
                                                b2.append(chip_in);
                                                b2.append("共"+orderSum+"\n");
                                            }
                                            String ms2 = b2.toString();
                                            ServerManager.getIntance().sendMessage(work_id, ms2,false,false);
                                            String receviDate = getReceviDate(work_id);
                                            if(!TextUtils.isEmpty(receviDate)){
                                                GroupData groupDatById = getGroupDatById(work_id);
                                                ServerManager.getIntance().sendMessage(work_id,groupDatById.name+"   "+ ms2,false,false);
                                            }
                                        }
                                    }
                                }
                                HookUtils.getIntance().kaijaingEnd();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteXiazu(String workId,final int id2){
        JSONObject js = new JSONObject();
        try {
            String aaaa = "";
            if(HookUtils.getIntance().mIndexMax < 10){
                aaaa = "00";
            }else if (HookUtils.getIntance().mIndexMax < 100 ){
                aaaa = "0";
            }
            js.put("index",aaaa+HookUtils.getIntance().mIndexMax);
            js.put("orderid",id2);
            js.put("groupID",mGuanliQunID);
            js.put("workID",workId);
            postData(js,"http://47.107.161.54:8801/cancelorder.php",new MyStringCall(null,workId,null){
                @Override
                public void onResponseString(String response, int id) {
                    try {
                        JSONObject js = new JSONObject(response);
                        if(js.has("status") && js.getInt("status") == 1){
                            ServerManager.getIntance().sendMessage(workIdCall,id2+" 退成功",false,false);
                            String receviDate = getReceviDate(workIdCall);
                            XposedBridge.log("receviDate = "+receviDate);
                            if(!TextUtils.isEmpty(receviDate)){
                                ServerManager.getIntance().sendMessage(receviDate,id2+" 退成功",false,false);
                            }
                        }else{
                            ServerManager.getIntance().sendMessage(workIdCall,id2+" 退失败",false,false);
                        }
                    } catch (JSONException e) {
                        ServerManager.getIntance().sendMessage(workIdCall,id2+" 退失败",false,false);
                    }
                }

                @Override
                public void onError(Call call, Exception e, int id) {
                    ServerManager.getIntance().sendMessage(workIdCall,id2+" 退失败",false,false);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void xiazu(final String str,String group) {
        XposedBridge.log("xiazu "+str+" group"+group);
        JSONObject js = new JSONObject();
        try {
            String aaaa = "";
            if(HookUtils.getIntance().mIndexMax < 10){
                aaaa = "00";
            }else if (HookUtils.getIntance().mIndexMax < 100 ){
                aaaa = "0";
            }
            js.put("index",aaaa+HookUtils.getIntance().mIndexMax);
            js.put("ms",str);
            js.put("groupID",mGuanliQunID);
            js.put("workID", group );
            postData(js,"http://47.107.161.54:8801/order.php",new MyStringCall(null,group,null){
                public void onResponseString(String response, int id) {
                    String getXiazuXinxi = getXiazuXinxi(str,response);
                    if(TextUtils.isEmpty(getXiazuXinxi)){
                        ServerManager.getIntance().sendMessage(workIdCall,str+" 下失败",false,false);
                    }else{
                        ServerManager.getIntance().sendMessage(workIdCall,getXiazuXinxi,false,false);
                        String receviDate = getReceviDate(workIdCall);
                        XposedBridge.log("receviDate = "+receviDate);
                        if(!TextUtils.isEmpty(receviDate)){
                            ServerManager.getIntance().sendMessage(receviDate,getXiazuXinxi,false,false);
                        }
                    }

                }

                @Override
                public void onError(Call call, Exception e, int id) {
                    ServerManager.getIntance().sendMessage(workIdCall,str+" 下失败",false,false);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private String getXiazuXinxi(String str ,String response){
        try {
            JSONObject js = new JSONObject(response);
            if(js.has("status")){
                int status = js.getInt("status");
                if(status == 1){
                    StringBuilder b = new StringBuilder();
                    b.append(str+"\n------------\n");
                    int xiaId = -1;
                    boolean isPai = false;
                    if(js.has("orderid")){
                        xiaId  = js.getInt("orderid");
                        if(js.has("data")){
                            JSONArray data = js.getJSONArray("data");
                            for(int i = 0 ; i<data.length();i++){
                                JSONObject jb = data.getJSONObject(i);
                                if(jb.has("date_list") && jb.getJSONArray("date_list").length() > 0){
                                    JSONArray date_list=  jb.getJSONArray("date_list");
                                    if(date_list.length() == 2){
                                        JSONArray date_list1 = date_list.getJSONArray(0);
                                        JSONArray date_list2 = date_list.getJSONArray(1);
                                        for(int ii = 0; ii < date_list1.length();ii++){
                                            b.append(date_list1.getInt(ii));
                                        }
                                        b.append("-");
                                        for(int ii = 0; ii < date_list2.length();ii++){
                                            b.append(date_list2.getInt(ii));
                                        }
                                    }else{
                                        return null;
                                    }
                                }else if(jb.has("last_data") && jb.getJSONArray("last_data").length() > 0){
                                    JSONArray last_data=  jb.getJSONArray("last_data");
                                    if(last_data.length() > 0){
                                        for(int ii = 0 ; ii< last_data.length() ;ii++){
                                            JSONArray last_data1 = last_data.getJSONArray(ii);
                                            if(last_data1.length() == 2){
                                                if(ii != 0){
                                                    b.append(",");
                                                }
                                                b.append(last_data1.getInt(0)+""+last_data1.getInt(1));
                                            }else{
                                                return null;
                                            }
                                        }
                                    }else{
                                        return null;
                                    }
                                }else{
                                    return null;
                                }
                                if(jb.has("local") && jb.getJSONArray("local").length() > 0) {
                                    JSONArray local = jb.getJSONArray("local");
                                    if (local.length() > 0) {
                                        b.append("【");
                                        for (int ii = 0; ii < local.length(); ii++) {
                                            JSONArray local1 = local.getJSONArray(ii);
                                            if (local1.length() == 2) {
                                                if (ii != 0) {
                                                    b.append(",");
                                                }
                                                b.append(local1.getInt(0) + "" + local1.getInt(1));
                                            } else {
                                                return null;
                                            }
                                        }
                                        b.append("】");
                                    } else {
                                        return null;
                                    }
                                }
                                if(jb.has("pai")){
                                    isPai = jb.getBoolean("pai");
                                }
                                b.append(isPai?"排":"");
                                if(jb.has("count_list") && jb.getJSONArray("count_list").length() > 0) {
                                   JSONArray count_list =  jb.getJSONArray("count_list");
                                    if (count_list.length() > 0) {
                                        b.append("[");
                                        ArrayList<Integer> list = new ArrayList<>();
                                        for (int ii = 0; ii < count_list.length(); ii++) {
                                            list.add(count_list.getInt(ii));
                                        }
                                        int count = -1;
                                        boolean isHaveOne = true;
                                        StringBuilder b2 =new StringBuilder();
                                        for(Integer iii : list){

                                            if(count == -1){
                                                count = iii;
                                                b2.append(count);
                                            }else{
                                                b2.append(","+iii);
                                                if(count != iii){
                                                    isHaveOne = false;
                                                }
                                            }
                                        }
                                        if(isHaveOne){
                                            b.append(b2.toString());
                                        }else{
                                            b.append(count);
                                        }
                                        b.append("]");
                                    }
                                }
                                b.append("\n----------------------\n");
                            }
                        }
                        b.append("退改ID  "+xiaId);
                        return b.toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
      return null;
    }

    public void changeXiazu(final String newStr,final int idc,String group) {
        JSONObject js = new JSONObject();
        try {
            String aaaa = "";
            if(HookUtils.getIntance().mIndexMax < 10){
                aaaa = "00";
            }else if (HookUtils.getIntance().mIndexMax < 100 ){
                aaaa = "0";
            }
            js.put("index",aaaa+HookUtils.getIntance().mIndexMax);
            js.put("ms",newStr);
            js.put("orderid",""+idc);
            js.put("groupID",mGuanliQunID);
            js.put("workID",group);
            postData(js,"http://47.107.161.54:8801/changeorder.php",new MyStringCall(null,group,null){
                @Override
                public void onResponseString(String response, int id) {
                    String getXiazuXinxi = getXiazuXinxi(newStr,response);
                    if(TextUtils.isEmpty(getXiazuXinxi)){
                        ServerManager.getIntance().sendMessage(workIdCall,idc+" 改失败",false,false);
                    }else{
                        ServerManager.getIntance().sendMessage(workIdCall,idc+" 改成功\n"+getXiazuXinxi,false,false);
                        String receviDate = getReceviDate(workIdCall);
                        XposedBridge.log("receviDate = "+receviDate);
                        if(!TextUtils.isEmpty(receviDate)){
                            ServerManager.getIntance().sendMessage(receviDate,idc+" 改成功\n"+getXiazuXinxi,false,false);
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private  void postData(JSONObject jsonObject,String url,MyStringCall call){
        OkHttpUtils
                .postString()
                .url(url)
                .content(jsonObject.toString())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(call);
    }

    public String getGuanliQunId(){
        try {
            if(mJson.has("guanliquan")){
                return mJson.getString("guanliquan");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(!TextUtils.isEmpty(mGuanliQunID)){
            return mGuanliQunID;
        }
        return "";
    }

    public boolean isRobot(String id){
        for(RobatType type : mRobatList){
            if( type.type == mType && !TextUtils.isEmpty(type.id) &&type.id.equals(id)){
                return true;
            }
        }
        return false;
    }
    public boolean isGuanliYuan(String takerId){
        for(String id : mGuanliList){
            if(takerId.equals(id)){
                return true;
            }
        }
        return false;
    }

    public void stopParseGroup(String groupID,boolean isStop){
        HashMap<String,String > map = new HashMap<>();
        map.put("isStopParse",isStop+"");
        setGroup(map,groupID,mGroupList.get(groupID).name+(isStop?"停解":"开解"));

    }

    public void setPei(String groupID, int pei) {
        HashMap<String,String > map = new HashMap<>();
        map.put("odds",pei+"");
        setGroup(map,groupID,mGroupList.get(groupID).name+"陪绿"+pei);

    }


    public void shexianGroup(String groupID,int max){
        HashMap<String,String > map = new HashMap<>();
        map.put("maxlimit",max+"");
        setGroup(map,groupID,mGroupList.get(groupID).name+"设限"+max);

    }
    public int  getShexianGroup(String groupID){
        if(mGroupList.containsKey(groupID)){
            GroupData groupData = mGroupList.get(groupID);
            return groupData.max;
        }else{
            return 600;
        }
    }


    public void changeFen(String groupID, int fen) {
        changeFen(groupID,fen,false);
    }
    public void changeFen(String groupID, int fen , boolean isDown){
        HashMap<String,String > map = new HashMap<>();
        map.put("sType",1+"");
        map.put("score",(isDown  ? -fen:fen)+"");
        setGroup(map,groupID,mGroupList.get(groupID).name+"上份"+(isDown  ? -fen:fen));
    }
    public void setFen(String groupID, int fen) {
        HashMap<String,String > map = new HashMap<>();
        map.put("sType",0+"");
        map.put("score",fen+"");
        setGroup(map,groupID,mGroupList.get(groupID).name+" 设分"+fen);
    }
    public void saveGroupTypeDate(String key,String groupId,int type){
        HashMap<String,String > map = new HashMap<>();
        map.put("secretkey",key+"");
        if(type == 3 ){
            map.put("orientation",2+"");
        }else{
            map.put("orientation",1+"");
        }
        map.put("type",type+"");
        setGroup(map,groupId,mGroupList.get(groupId).name+"为 "+key+(type == 1 ? " 飞" : " 收"));
    }

    public void saveGroupKeyDate(String key,String groupId,int type){
        HashMap<String,String > map = new HashMap<>();
        map.put("secretkey",key+"");
        map.put("orientation",type+"");
        setGroup(map,groupId,mGroupList.get(groupId).name+"为"+key+(type == 1? " 转":" 收"));
    }


    public void setAllForGroup(String groupId,int all){
        HashMap<String,String > map = new HashMap<>();
        map.put("daysum",all+"");
        setGroup(map,groupId,mGroupList.get(groupId).name+"量改为"+all);

    }

    public void setGroupEnable(String groupID, boolean b) {
        HashMap<String,String > map = new HashMap<>();
        map.put("enable",b+"");

        setGroup(map,groupID,mGroupList.get(groupID).name+(b?"开":"关"));
    }

    private void setGroup(Map<String,String > data,String workId,final String str){
        GetBuilder getBuilder = OkHttpUtils
                .get()
                .url("http://47.107.161.54:8801/setworkgroup.php");
        Set<String> strings = data.keySet();
        for(String s : strings){
            getBuilder.addParams(s,data.get(s));
        }
        getBuilder.addParams("workID", workId);
        getBuilder.addParams("groupID", mGuanliQunID);
        getBuilder.build().execute(new MyStringCall(mGuanliQunID,null ,null){
            @Override
            public void onResponseString(String response, int id) {
                loginIn(mDeviceID,mGuanliQunID,mType+"",false);
                ServerManager.getIntance().sendMessage(mGuanliQunID,str+"设置成功",false,false);
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ServerManager.getIntance().sendMessage(mGuanliQunID,str+"设置失败",false,false);
            }
        });

    }
    public GroupData getGroupDatById(String id){
        return mGroupList.get(id);
    }

    public void autoAddGroup(String str){
        addGroup(str,str,true);

    }
    public void addGroup(final String name,final  String id,final boolean isAuto){
        OkHttpUtils
                .get()
                .url("http://47.107.161.54:8801/addworkgroup.php")
                .addParams("workName", name)
                .addParams("workID", id)
                .addParams("groupID", mGuanliQunID)
                .build()
                .execute(new MyStringCall(mGuanliQunID,null ,null){
                    @Override
                    public void onResponseString(String response, int id) {
                        ServerManager.getIntance().sendMessage(mGuanliQunID,name+"群注册成功",false,false);
                        loginIn(mDeviceID,mGuanliQunID,mType+"",false);
                    }
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        ServerManager.getIntance().sendMessage(mGuanliQunID,name+"群注册失败",false,false);
                    }
                });
    }
    public void addGroup(final String name,final  String id){
        addGroup(name,id,false);
    }

    public int addGuanliId(final String takerId) {
        if(isGuanliYuan(takerId)){
            return 1;
        }

         OkHttpUtils
        .get()
        .url("http://47.107.161.54:8801/setadmin.php")
        .addParams("adminwxID", takerId)
        .addParams("groupID", mGuanliQunID)
        .build()
        .execute(new MyStringCall(mGuanliQunID,null ,null){
            @Override
            public void onResponseString(String response, int id) {
                if(!TextUtils.isEmpty(response) && response.equals("1")){
                    ServerManager.getIntance().sendMessage(mGuanliQunID,"注册管理员成功",false,false);
                    loginIn(mDeviceID,mGuanliQunID,mType+"",false);
                }
            }
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ServerManager.getIntance().sendMessage(mGuanliQunID,"注册管理员失败",false,false);
            }
        });
        return 0;
    }

    public void loginIn(String name,String groupId,String type){
        loginIn(name,groupId,type,true);
    }


    public void loginIn(String name,String groupId,String type,final boolean isPrintf){
        mDeviceID = name;
        mGuanliQunID = groupId;

        GetBuilder getBuilder = OkHttpUtils
                .get()
                .url("http://47.107.161.54:8801/goline.php")
                .addParams("wxName", name)
                .addParams("AdminGroup", groupId);
        if(!TextUtils.isEmpty(type)){
            getBuilder.addParams("wxType",type);

        }
        getBuilder.build().execute(new MyStringCall(groupId,null ,null){

            @Override
            public void onResponseString(String response, int id) {
                XposedBridge.log("onResponseString = "+response);
                getJson(response);
                ServerManager.getIntance().init();
                StringBuilder b = new StringBuilder();
                b.append(mDeviceID);
                b.append("上线成功\n");
                if(mGuanliList.size() >0){
                    b.append("管理员有：\n");
                    for(String s: mGuanliList){
                        b.append(s+"\n");
                    }
                }
                if(mGroupList.size() >0){
                    b.append("工作群有：\n");
                    Set<String> strings = mGroupList.keySet();
                    for(String s: strings){
                        b.append(mGroupList.get(s).name+"\n");
                    }
                }
                if(mRobatList.size() >0){
                    b.append("机器人有：\n");
                    for(RobatType s: mRobatList){
                        b.append(s.name+"  "+s.type+"\n");
                    }
                }

                b.append("上线成功");
                if(isPrintf){
                    ServerManager.getIntance().sendMessage(mGuanliQunID,b.toString(),false,false);
                }

            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ServerManager.getIntance().sendMessage(mGuanliQunID,mDeviceID+"上线失败",false,false);
            }
        });
    }

    private void getJson(String s){
        if(TextUtils.isEmpty(s)){
            return;
        }else{
            try {
                mJson = new JSONObject(s);
                mGuanliList.clear();
                mRobatList.clear();
                mGroupList.clear();
                XposedBridge.log("mJson = "+mJson.toString());
                if(mJson.has("guanlis")){
                    JSONArray array = mJson.getJSONArray("guanlis");
                    for(int i = 0;i<array.length();i++){
                        mGuanliList.add(array.getJSONObject(i).getString("id"));
                        XposedBridge.log("guanli id= "+mGuanliList.get(i));
                    }
                }
                if(mJson.has("robat")){
                    JSONArray array = mJson.getJSONArray("robat");
                    for(int i = 0;i<array.length();i++){
                        JSONObject ob = array.getJSONObject(i);
                        RobatType type = new RobatType();
                        if(ob.has("type")){
                            type.type = Integer.parseInt(ob.getString("type"));
                        }
                        if(ob.has("name")){
                            type.name = ob.getString("name");
                        }
                        if(!TextUtils.isEmpty(type.name) && type.name.equals(mDeviceID)){
                            mType = type.type;
                        }
                        mRobatList.add(type);
                        XposedBridge.log("robat name= "+type.name);
                    }
                }


                if(mJson.has("list")){
                    JSONArray array = mJson.getJSONArray("list");
                    if(array != null && array.length() > 0){
                        for(int i = 0 ;i<array.length() ;i++){
                            if(!array.isNull(i)){
                                JSONObject ob = array.getJSONObject(i);
                                String name = "";
                                String id = "";
                                String secretkey = "";
                                int fen = 0;
                                int pei = 97;
                                int all = 0;
                                int max =600;
                                int type = 3;
                                boolean enable = false;
                                boolean stopParse = false;
                                boolean fufen = true;

                                int orientation = 2;
                                int delay = 20000;

                                if(ob.has("fufen")){
                                    fufen =Integer.parseInt(ob.getString("pei")) == 1 ? true:false;
                                }
                                if(ob.has("secretkey")){
                                    secretkey = ob.getString("secretkey");
                                }
                                if(ob.has("orientation")){
                                    orientation =Integer.parseInt(ob.getString("orientation"));
                                }
                                if(ob.has("delay")){
                                    delay = Integer.parseInt(ob.getString("delay"));
                                }

                                if(ob.has("name")){
                                    name = ob.getString("name");
                                }
                                if(ob.has("id")){
                                    id = ob.getString("id");
                                }
                                if(ob.has("pei")){
                                    pei = Integer.parseInt(ob.getString("pei"));
                                }
                                if(ob.has("fen")){
                                    pei = Integer.parseInt(ob.getString("fen"));
                                }
                                if(ob.has("enable")){
                                    enable =Boolean.parseBoolean( ob.getString("enable"));
                                }
                                if(ob.has("isStopParse")){
                                    stopParse =Boolean.parseBoolean( ob.getString("isStopParse"));
                                }
                                if(ob.has("all")){
                                    pei = Integer.parseInt(ob.getString("all"));
                                }
                                if(ob.has("max")){
                                    pei = Integer.parseInt(ob.getString("max"));
                                }
                                if(ob.has("type")){
                                    pei = Integer.parseInt(ob.getString("type"));
                                }
                                GroupData data = new GroupData(name,id);
                                data.fen = fen;
                                data.pei = pei;
                                data.isEnable = enable;
                                data.isStopParse = stopParse;
                                data.all = all;
                                data.max = max;
                                data.type = type;
                                data.fufen = fufen;
                                data.delay = delay;
                                data.orientation = orientation;
                                data.secretkey = secretkey;
                                mGroupList.put(data.id,data);
                            }
                        }

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String,GroupData> getGroupDate() {
        return mGroupList;
    }

    public GroupData getGroupDateByName(String name){
        if(mGroupList.size() > 0){
            Set<String> strings = mGroupList.keySet();
            for(String s :strings){
                GroupData g= mGroupList.get(s);
                if(!TextUtils.isEmpty(g.name) && g.name.equals(name)){
                    return g;
                }
            }

        }
        return null;
    }

    public String getReceviDate(String groupID) {
        GroupData groupData = mGroupList.get(groupID);
        if(groupData.orientation == 2){
            return null;
        }
        String key = groupData.secretkey;
        if(TextUtils.isEmpty(key)){
            return null;
        }else{
            Set<String> strings = mGroupList.keySet();
            for(String s : strings){
                GroupData groupData1 = mGroupList.get(s);
                XposedBridge.log("groupData1.secretkey = "+groupData1.secretkey+"  groupData1.orientation ="+ groupData1.orientation);
                if(key.equals(groupData1.secretkey) && groupData1.orientation == 2){
                    return groupData1.id;
                }
            }
        }
        return null;
    }


    public static class GroupData{
        public String name;
        public String id;
        public int fen = 0;
        public boolean isEnable = false;
        public int pei =90;
        public int all = 0;
        public boolean isStopParse = false;
        public int max = 600;
        public int type = 3;
        public boolean fufen = true;
        public String secretkey ="";
        public int orientation = 2;
        public int delay = 20000;
        public GroupData(String name,String id){
            this.name = name;
            this.id = id;
        }
    }

    public boolean isSendEableMessageGroup(String goupId){
        if(mGroupList.containsKey(goupId)){
            GroupData g = mGroupList.get(goupId);
            if(!TextUtils.isEmpty(g.secretkey) && g.orientation == 2){
                return true;
            }
        }else if(mGuanliQunID.equals(goupId)){
            return true;
        }
        return false;
    }

    public static class RobatType{
        public String id;
        public String name;
        public int type;
    }
}
