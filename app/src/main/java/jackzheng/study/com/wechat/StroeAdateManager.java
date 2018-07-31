package jackzheng.study.com.wechat;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.robv.android.xposed.XposedBridge;

public class StroeAdateManager {

    private static String FILE_NAME = "group_data.json";
    private static String FOLDER_NAME = "group_data.json";
    private JSONObject mJson;
    private String mGuanliId ;
    private String mSpGuanliId ;
    private Map<String,GroupData>  mGroupList= new HashMap<>();

    public String getmGuanliId() {
        return mGuanliId;
    }
    public String getmSPGuanliId() {
        return mSpGuanliId;
    }

    public  Map<String,GroupData> getGroupDate(){
        return mGroupList;
    }
    public GroupData getGroupDatById(String id){
        return mGroupList.get(id);
    }

    public void setmGuanliId(String mGuanliId) {
        this.mGuanliId = mGuanliId;
        try {
            mJson.put("guanli",mGuanliId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        writeFileToSDCard(mJson.toString().getBytes());
    }
    public void setmSPGuanliId(String mGuanliId) {
        this.mGuanliId = mGuanliId;
        try {
            mJson.put("spguanli",mSpGuanliId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        writeFileToSDCard(mJson.toString().getBytes());
    }
    public void addGroup(String name, String id){

        if(mGroupList.containsKey(id)){
            JSONObject js = findJsonByGroupId(id);
            if(id == null){
                return;
            }
            try {
                js.put("name",name);
                JSONArray list = mJson.getJSONArray("list");
               // list.put(js);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            GroupData groupData = mGroupList.get(id);
            groupData.name = name;
        }else{
            GroupData date = new GroupData(name,id);
            mGroupList.put(id,date);
            JSONObject js = new JSONObject();
            try {
                js.put("name",date.name);
                js.put("id",date.id);
                js.put("fen",date.fen);
                js.put("enable",date.isEnable);
                js.put("pei",date.pei);
                JSONArray array;
                if(mJson.has("list")){
                   array = mJson.getJSONArray("list");
                }else{
                    array = new JSONArray();
                    mJson.put("list",array);
                }
                array.put(js);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }
        writeFileToSDCard(mJson.toString().getBytes());
    }

    private JSONObject findJsonByGroupId(String id){
        if(!mJson.has("list")){
            return null;
        }
        try {
            JSONArray array = mJson.getJSONArray("list");
            if(array == null){
                return null;
            }
            for(int i = 0; i<array.length();i++){
                if(!array.isNull(i)){
                    JSONObject js = array.getJSONObject(i);
                    if(js.getString("id").equals(id)){
                   //     array.remove()
                        return js;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }




    private static StroeAdateManager mIntance = new StroeAdateManager();
    public static StroeAdateManager getmIntance(){
        return mIntance;
    }
    private StroeAdateManager(){

        String s = readFile();
        XposedBridge.log("readFile:\n"+s+"\n---------------------------\n");
        if(TextUtils.isEmpty(s)){
            mJson  = new JSONObject();
        }else{
            try {
                mJson = new JSONObject(s);
                if(mJson.has("guanli")){
                    mGuanliId = mJson.getString("guanli");
                    XposedBridge.log("guanli id = "+mGuanliId);
                }
                if(mJson.has("spguanli")){
                    mSpGuanliId = mJson.getString("spguanli");
                    XposedBridge.log("guanli id = "+mGuanliId);
                }
                if(mJson.has("list")){
                    JSONArray array = mJson.getJSONArray("list");
                    if(array != null && array.length() > 0){
                        for(int i = 0 ;i<array.length() ;i++){
                            if(!array.isNull(i)){
                                JSONObject ob = array.getJSONObject(i);
                                String name = "";
                                String id = "";
                                int fen = 0;
                                int pei = 97;
                                boolean enable = false;
                                if(ob.has("name")){
                                    name = ob.getString("name");
                                }
                                if(ob.has("id")){
                                    id = ob.getString("id");
                                }
                                if(ob.has("pei")){
                                    pei = ob.getInt("pei");
                                }
                                if(ob.has("fen")){
                                    fen = ob.getInt("fen");
                                }
                                if(ob.has("enable")){
                                    enable = ob.getBoolean("enable");
                                }
                                GroupData data = new GroupData(name,id);
                                data.fen = fen;
                                data.pei = pei;
                                data.isEnable = enable;
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

    public void setGroupEnable(String groupID, boolean b) {
        if(mGroupList.containsKey(groupID)){
            if(mGroupList.get(groupID).isEnable != b){
                mGroupList.get(groupID).isEnable = b;
            }else{
                return;
            }
        }else{
            return;
        }
        JSONObject jsonByGroupId = findJsonByGroupId(groupID);
        if(jsonByGroupId == null){
            return;
        }
        try {
            jsonByGroupId.put("enable",b);
            JSONArray list = mJson.getJSONArray("list");
         //   list.put(jsonByGroupId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        writeFileToSDCard(mJson.toString().getBytes());
    }

    public void changeFen(String groupID, int fen) {
        GroupData groupData;
        if(mGroupList.containsKey(groupID)){
            groupData = mGroupList.get(groupID);
            groupData.fen = groupData.fen+fen;
        }else{
            return;
        }
        JSONObject jsonByGroupId = findJsonByGroupId(groupID);
        if(jsonByGroupId == null){
            return;
        }
        try {
            jsonByGroupId.put("fen",groupData.fen);
            JSONArray list = mJson.getJSONArray("list");
       //     list.put(jsonByGroupId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        writeFileToSDCard(mJson.toString().getBytes());
    }
    public void setFen(String groupID, int fen) {
        GroupData groupData;
        if(mGroupList.containsKey(groupID)){
            groupData = mGroupList.get(groupID);
            groupData.fen = fen;
        }else{
            return;
        }
        JSONObject jsonByGroupId = findJsonByGroupId(groupID);
        if(jsonByGroupId == null){
            return;
        }
        try {
            jsonByGroupId.put("fen",groupData.fen);
            JSONArray list = mJson.getJSONArray("list");
            //     list.put(jsonByGroupId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        writeFileToSDCard(mJson.toString().getBytes());
    }
    public void setPei(String groupID, int pei) {
        GroupData groupData;
        if(mGroupList.containsKey(groupID)){
            groupData = mGroupList.get(groupID);
            groupData.pei = pei;
        }else{
            return;
        }
        JSONObject jsonByGroupId = findJsonByGroupId(groupID);
        if(jsonByGroupId == null){
            return;
        }
        try {
            jsonByGroupId.put("pei",pei);
            JSONArray list = mJson.getJSONArray("list");
      //      list.put(jsonByGroupId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        writeFileToSDCard(mJson.toString().getBytes());
    }

    public static class GroupData{
        public String name;
        public String id;
        public int fen = 0;
        public boolean isEnable = false;
        public int pei =90;
        public GroupData(String name,String id){
            this.name = name;
            this.id = id;
        }
    }

    public synchronized static void writeFileToSDCard(@NonNull final byte[] buffer) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String folderPath = Environment.getExternalStorageDirectory()
                        + File.separator + FOLDER_NAME + File.separator;

                File fileDir = new File(folderPath);
                if (!fileDir.exists()) {
                    if (!fileDir.mkdirs()) {
                        return;
                    }
                }
                File file = new File(folderPath + FILE_NAME);
                RandomAccessFile raf = null;
                FileOutputStream out = null;
                try {
                        //重写文件，覆盖掉原来的数据
                        out = new FileOutputStream(file);
                        out.write(buffer);
                        out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (raf != null) {
                            raf.close();
                        }
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    private String  readFile() {
        String folderPath = Environment.getExternalStorageDirectory()
                + File.separator + FOLDER_NAME + File.separator;

        File fileDir = new File(folderPath);
        if (!fileDir.exists()) {
            return null;
        }
        File file = new File(folderPath + FILE_NAME);
        if (!file.exists()) {
            return null;
        }
        try {
            InputStream instream = new FileInputStream(file);
            if (instream != null) {
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                StringBuilder build = new StringBuilder();
                //分行读取
                while ((line = buffreader.readLine()) != null) {
                    build.append(line + "\n");
                }
                buffreader.close();
                inputreader.close();
                instream.close();
                return build.toString();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

