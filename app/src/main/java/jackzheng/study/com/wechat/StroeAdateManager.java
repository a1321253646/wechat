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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.robv.android.xposed.XposedBridge;

public class StroeAdateManager {

    private static String FILE_NAME = "group_data.json";
    private static String FOLDER_NAME = "group_data.json";
    private JSONObject mJson;
    private Map<String,GroupData>  mGroupList= new HashMap<>();
    private ArrayList<String> mGuanliList = new ArrayList<>();
    public String getJsonString(){
        return mJson.toString();
    }

    public void setJson(String str){
            getJson(str);
            writeFileToSDCard(mJson.toString().getBytes());
    }

    public  Map<String,GroupData> getGroupDate(){
        return mGroupList;
    }
    public GroupData getGroupDatById(String id){
        return mGroupList.get(id);
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
                js.put("all",date.all);
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
    public String getGuanliQunId(){
        try {
            if(mJson.has("guanliquan")){
                return mJson.getString("guanliquan");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
    public void setGuanliqunID(String id){
        try {
            mJson.put("guanliquan",id);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        writeFileToSDCard(mJson.toString().getBytes());
    }

    private static StroeAdateManager mIntance = new StroeAdateManager();
    public static StroeAdateManager getmIntance(){
        return mIntance;
    }
    private StroeAdateManager(){

        String s = readFile();
        XposedBridge.log("readFile:\n"+s+"\n---------------------------\n");
        getJson(s);
    }

    private void getJson(String s){
        if(TextUtils.isEmpty(s)){
            mJson  = new JSONObject();
        }else{
            try {
                mJson = new JSONObject(s);
                if(mJson.has("guanlis")){
                    JSONArray array = mJson.getJSONArray("guanlis");
                    for(int i = 0;i<array.length();i++){
                        mGuanliList.add(array.getJSONObject(i).getString("id"));
                        XposedBridge.log("guanli id= "+mGuanliList.get(i));
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
                                int fen = 0;
                                int pei = 97;
                                int all = 0;
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
                                if(ob.has("all")){
                                    all = ob.getInt("all");
                                }
                                GroupData data = new GroupData(name,id);
                                data.fen = fen;
                                data.pei = pei;
                                data.isEnable = enable;
                                data.all = all;
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

    public void setAllForGroup(String groupId,int all){
        GroupData groupData;
        if(mGroupList.containsKey(groupId)){
            groupData = mGroupList.get(groupId);
            groupData.all  = all;
        }else{
            return;
        }
        JSONObject jsonByGroupId = findJsonByGroupId(groupId);
        if(jsonByGroupId == null){
            return;
        }
        try {
            jsonByGroupId.put("all",groupData.all);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        writeFileToSDCard(mJson.toString().getBytes());
    }

    public void clearAllForAllGroup(){
        if(mGroupList != null && mGroupList.size() > 0){
            Set<String> strings = mGroupList.keySet();
            for(String s : strings){
                GroupData groupData = mGroupList.get(s);
                groupData.all = 0;
                groupData.fen = 0;
                JSONObject jsonByGroupId = findJsonByGroupId(groupData.id);
                try {
                    jsonByGroupId.put("all",groupData.all);
                    jsonByGroupId.put("fen",groupData.fen);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            writeFileToSDCard(mJson.toString().getBytes());
        }
    }

    public void changeFen(String groupID, int fen , boolean isDown){
        GroupData groupData;
        if(mGroupList.containsKey(groupID)){
            groupData = mGroupList.get(groupID);
            groupData.fen = groupData.fen+fen;
            if(isDown){
                groupData.all = groupData.all-fen;
            }
        }else{
            return;
        }
        JSONObject jsonByGroupId = findJsonByGroupId(groupID);
        if(jsonByGroupId == null){
            return;
        }
        try {
            jsonByGroupId.put("fen",groupData.fen);
            if(isDown){
                jsonByGroupId.put("all",groupData.all);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        writeFileToSDCard(mJson.toString().getBytes());
    }

    public void changeFen(String groupID, int fen) {
        changeFen(groupID,fen,false);
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
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        writeFileToSDCard(mJson.toString().getBytes());
    }

    public void saveReceviDate(String key,String groupID){
        try {
            mJson.put(key,groupID);
            writeFileToSDCard(mJson.toString().getBytes());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void saveSendDate(String groupID,String key){
        try {
            mJson.put(groupID,key);
            writeFileToSDCard(mJson.toString().getBytes());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void saveFuzheDate(String key,String groupID){
        try {
            mJson.put(key+"服",groupID);
            writeFileToSDCard(mJson.toString().getBytes());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void saveBeFuzheDate(String groupID,String key){
        try {
            mJson.put(groupID+"服",key+"服");
            writeFileToSDCard(mJson.toString().getBytes());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String getFuzheData(String group){
        try {
            String key = mJson.getString(group+"服");
            if(TextUtils.isEmpty(key)){
                return  null;
            }
            String send =  mJson.getString(key);
            if(TextUtils.isEmpty(key)){
                return  null;
            }else{
                return send;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  null;
    }

    public String getReceviDate(String group){
        try {
           String key = mJson.getString(group);
           if(TextUtils.isEmpty(key)){
               return  null;
           }
           String send =  mJson.getString(key);
           if(TextUtils.isEmpty(key)){
                return  null;
            }else{
               return send;
           }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  null;
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
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        writeFileToSDCard(mJson.toString().getBytes());
    }

    public boolean isGuanliYuan(String takerId){
        for(String id : mGuanliList){
            if(takerId.equals(id)){
                return true;
            }
        }
        return false;
    }

    public int addGuanliId(String takerId) {
        if(mJson.has("guanlis")){
            try {
                JSONArray array = mJson.getJSONArray("guanlis");
                for(int i = 0;i < array.length();i++){
                    JSONObject js = array.getJSONObject(i);
                    if(takerId.equals(js.getString("id"))){
                        return 1;
                    }
                }
                mGuanliList.add(takerId);
                JSONObject js2 = new JSONObject();
                js2.put("id",takerId);
                array.put(js2);
                writeFileToSDCard(mJson.toString().getBytes());
            } catch (JSONException e) {
                e.printStackTrace();
                return 2;
            }
        }else{
            try {
                JSONObject js2 = new JSONObject();
                js2.put("id",takerId);
                JSONArray array = new JSONArray();
                array.put(js2);
                mGuanliList.add(takerId);
                mJson.put("guanlis",array);
                writeFileToSDCard(mJson.toString().getBytes());
            } catch (JSONException e) {
                e.printStackTrace();
                return 2;
            }
        }
        return 0;
    }

    public static class GroupData{
        public String name;
        public String id;
        public int fen = 0;
        public boolean isEnable = false;
        public int pei =90;
        public int all = 0;
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

