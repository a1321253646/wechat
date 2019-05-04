package jackzheng.study.com.wechat.sscManager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.ArrayMap;

import java.util.Set;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import yaunma.com.myapplication.Tools;

public class DateSaveManager {

    ArrayMap<String , GroupDate> mGroupDate = new ArrayMap<String , GroupDate> ();
    ArrayMap<String , Integer>  mGuangli = new ArrayMap<String , Integer>();
    public  String mGuanliQun ;
    public  String mZongQun ;
    public  String mTixing;
    public  String mFengLiang;
    public  String mZhengque;
    public  String mBaobiao;
    public  String mKaikaikai;
    public static int mIndex = 0;
    public static int mGuanLiIndex = 0;
//    public boolean isJustShou = false;
    public boolean isBeiyong = false;
    public boolean isFangqun= false;
    public int mMaxJieId = 2;
    public boolean isHaveGroup (String group){
        if(mGroupDate.containsKey(group)){
            return true;
        }else{
            return false;
        }
    }

    public void saveGroup (String group){
        XposedBridge.log("saveGroup = "+group);
        GroupDate date = new GroupDate();
        mIndex++;
        date.index = mIndex;
        date.groupID = group;
        saveGroupId(date);
        mGroupDate.put(group,date);

        saveGroupEnable(group,true);
    }
    public GroupDate getGroup(String group){
        return mGroupDate.get(group);
    }
    public ArrayMap<String , GroupDate> getAllGroup(){
        return mGroupDate;
    }

    public void clearAllGroupFenAndLiang(){
        for(String id : mGroupDate.keySet()){
            GroupDate date = mGroupDate.get(id);
            date.fen = 0;
            date.liang = 0;
            saveStringDate(date.groupID+"fen",0 );
            saveStringDate(date.groupID+"liang",0 );
        }
    }

    public boolean isGuanliQun(String guanliqun){
        if(TextUtils.isEmpty(guanliqun)){
            return false;
        }
        return guanliqun.equals(mGuanliQun);
    }

    public boolean isGuanli(String user){
        return mGuangli.containsKey(user);
    }


    public String saveTo(String group,long toGroup){
        Set<String> dateList = mGroupDate.keySet();
        for(String str : dateList){
            XposedBridge.log("saveTo str="+str+" mGroupDate.get(str).getGroup="+mGroupDate.get(str).getGroup+" toGroup="+toGroup);
            if(mGroupDate.get(str).getGroup == toGroup){
                mGroupDate.get(group).toGroup = str;
                saveStringDate(group+"to",str);
                return mGroupDate.get(group).toGroup;
            }
        }
        return  null;
    }
    public void saveGet(String group,int get){
        mGroupDate.get(group).getGroup = get;
        saveStringDate(group+"get",get);
    }

    public void saveMaxJieId(){

        saveStringDate("maxJie",mMaxJieId);
    }

    public void saveGuanLiYuan(String user){
        if(mGuangli.containsKey(mGuangli)){
            return;
        }else{
            mGuanLiIndex++;
            mGuangli.put(user,mGuanLiIndex);
            saveStringDate("guanli"+mGuanLiIndex,user);
        }
    }

    public void saveYin(String group,int yin){
        mGroupDate.get(group).yin = yin;
        saveStringDate(+mGroupDate.get(group).index+"yin",mGroupDate.get(group).yin);
    }
    public void saveXiane(String group,boolean  xiane){
        mGroupDate.get(group).xianer = xiane;
        saveStringDate(mGroupDate.get(group).groupID+"xiane",mGroupDate.get(group).xianer);
    }

    public void saveGuanliQun(String guanliqun){
        mGuanliQun = guanliqun;
        saveStringDate("guanliqun",mGuanliQun);
    }
    public void saveFengLiang(String guanliqun){
        mFengLiang = guanliqun;
        saveStringDate("fengliang",guanliqun);
    }
    public void saveZhengque(String guanliqun){
        mZhengque = guanliqun;
        saveStringDate("zhengque",guanliqun);
    }
    public void saveBaobiao(String guanliqun){
        mBaobiao = guanliqun;
        saveStringDate("baobiao",guanliqun);
    }
    public void saveKaikaikai(String guanliqun){
        mKaikaikai = guanliqun;
        saveStringDate("kaikaikai",guanliqun);
    }
    private void saveGroupId(GroupDate group){

        saveStringDate("groupId"+group.index,group.groupID);
    }

    public void saveGroupName(String group,String name){
        XposedBridge.log("saveGroupName group= "+group+" name="+name);
        mGroupDate.get(group).groupName = name;
        saveStringDate(mGroupDate.get(group).groupID+"name",name);
    }
    public void saveZong(String group){
        mZongQun = group;
        saveStringDate("zong",mZongQun);
    }
    public void saveTiXing(String group){
        mTixing = group;
        saveStringDate("tixing",mTixing);
    }


    public void saveGroupEnable(String group,boolean isOpen){
        mGroupDate.get(group).isEnable = isOpen;
        saveStringDate(mGroupDate.get(group).groupID+"enable",isOpen);
    }

    public void saveJustShou(){
//        isJustShou = true;
//        saveStringDate("justShou",true);
    }
    public void saveBeiyong(){
        isBeiyong = true;
        saveStringDate("beiyong",true);
    }
    public void saveFangqun(){
        isFangqun = true;
        saveStringDate("fangqun",true);
    }
    public void changeGroupFenOrLiang(String group,boolean isFen,int action,int value){
        if(isFen){
            mGroupDate.get(group).fen = saveFenOrLiangeActionValue(mGroupDate.get(group).fen,action,value);
            saveStringDate(mGroupDate.get(group).groupID+"fen",mGroupDate.get(group).fen );
        }else{
            mGroupDate.get(group).liang = saveFenOrLiangeActionValue(mGroupDate.get(group).liang,action,value);
            saveStringDate(mGroupDate.get(group).groupID+"liang",mGroupDate.get(group).liang );
        }
    }
    private int saveFenOrLiangeActionValue(int old,int action,int value){
        if(action == 3){
            return value;
        }else if(action == 1){
            return old+value;
        }else if(action == 2){
            return old-value;
        }
        return old;
    }

    public void clearAll(){
        if(!TextUtils.isEmpty(mGuanliQun)){
            editor.remove("guanliqun").commit();;
            mGuanliQun = null;
        }
        for(String gl : mGuangli.keySet()){
            editor.remove("guanli"+mGuangli.get(gl)).commit();
        }
        mGuangli.clear();
        mGuanLiIndex = 0;
        for(String g : mGroupDate.keySet()){
            GroupDate group = mGroupDate.get(g);
            editor.remove("groupId"+group.index).commit();
            editor.remove(group.groupID+"name").commit();
            editor.remove(group.groupID+"to").commit();
            editor.remove(group.groupID+"get").commit();
            editor.remove(group.groupID+"fen").commit();
            editor.remove(group.groupID+"liang").commit();
            editor.remove(group.groupID+"yin").commit();
            editor.remove(group.groupID+"enable").commit();
            editor.remove(group.groupID+"xiane").commit();
        }
        mGroupDate.clear();
        mIndex = 0;
        editor.remove("justShou").commit();
        editor.remove("fangqun").commit();
        editor.remove("zong").commit();
        editor.remove("maxJie").commit();
        editor.remove("tixing").commit();
        editor.remove("fengliang").commit();
        editor.remove("baobiao").commit();
        editor.remove("kaikaikai").commit();
        editor.remove("zhengque").commit();
        editor.remove("beiyong").commit();


        mZongQun = null;
        mTixing = null;
//        isJustShou = false;
        isBeiyong = false;
        isFangqun = false;
        mFengLiang = null;
        mZhengque = null;
        mBaobiao = null;
        mKaikaikai = null;
        mMaxJieId = 2;
    }


    public void getAllDate(){
        mGuanliQun = getStringDate("guanliqun");
        XposedBridge.log("getAllDate mGuanliQun="+mGuanliQun);
        if(TextUtils.isEmpty(mGuanliQun)){
            return;
        }
        mMaxJieId = getIntDate("maxJie",2);
        int index = 1;
        while (true){
            String guanli = getStringDate("guanli"+index);
            XposedBridge.log("getAllDate guanli="+guanli);
            if(TextUtils.isEmpty(guanli)){
                index -- ;
                mGuanLiIndex = index;
                break;
            }else{
                mGuangli.put(guanli,index);
            }
            index ++;
        }
        if(mGuangli.size() == 0){
            return;
        }
        index = 1;
        while (true){
            String group = getStringDate("groupId"+index);
            XposedBridge.log("getAllDate group="+group);
            if(TextUtils.isEmpty(group)){
                index -- ;
                mIndex = index;
                break;
            }else{
                GroupDate date = new GroupDate();
                date.groupID = group;
                date.index = index;
                date.groupName = getStringDate(date.groupID+"name");
                date.toGroup = getStringDate(date.groupID+"to");
                date.getGroup = getIntDate(date.groupID+"get",1);
                date.fen = getIntDate(date.groupID+"fen",0);
                date.liang = getIntDate(date.groupID+"liang",0);
                date.yin = getIntDate(date.groupID+"yin",97);
                date.isEnable = getBooleanDate(date.groupID+"enable");
                date.xianer = getBooleanDate(date.groupID+"xiane");
                mGroupDate.put(group,date);
            }
            index ++;
        }
//        isJustShou = getBooleanDate("justShou");
        isBeiyong = getBooleanDate("justShou");
        mZongQun = getStringDate("zong");
        mTixing = getStringDate("tixing");
        mFengLiang = getStringDate("fengliang");
        isFangqun = getBooleanDate("fangqun");
        mZhengque = getStringDate("zhengque");
        mBaobiao = getStringDate("baobiao");
        mKaikaikai = getStringDate("kaikaikai");

    }



    public static class GroupDate{
        public String groupID;
        public String groupName;
        public boolean isEnable = true;
        public int fen = 0;
        public int liang = 0;
        public int yin = 97;
        public int index;
        public String toGroup;
        public int getGroup = 1;
        public boolean xianer = false;
        public boolean isIntime = false;
    }




    public static DateSaveManager getIntance(){
        return mIntance;
    }
    private static DateSaveManager mIntance = new DateSaveManager();
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    XSharedPreferences intance;
    private DateSaveManager(){
         sp = Tools.mActivity
                .getSharedPreferences(Tools.mActivity.getPackageName() + "_preferences", Activity.MODE_WORLD_READABLE);
        editor  = sp.edit();
        intance = new XSharedPreferences(Tools.mActivity.getPackageName());
// 保存数据到共享配置文件中
        editor.putString("test_put", "test_put").commit();
        getAllDate();
    }
    private void saveStringDate(String key,String value){
        XposedBridge.log("saveStringDate key= "+key+" value="+value);
        editor.putString(key, value).commit();
    }
    private void saveStringDate(String key,boolean value){
        XposedBridge.log("saveStringDate key= "+key+" value="+value);
        editor.putBoolean(key, value).commit();
    }
    private void saveStringDate(String key,int value){
        XposedBridge.log("saveStringDate key= "+key+" value="+value);
        editor.putInt(key, value).commit();
    }
    private boolean getBooleanDate(String key){
        boolean value = intance.getBoolean(key, false);
        XposedBridge.log("getBooleanDate key= "+key+" value= "+value);
        return  value;

    }
    private String getStringDate(String key){

        String value = intance.getString(key, "");
        XposedBridge.log("getBooleanDate key= "+key+" value= "+value);
        if(TextUtils.isEmpty(value)){
            return null;
        }
        return value;
    }
    private int getIntDate(String key,int defaultValue){

        int value = defaultValue;
        try {
           value =  intance.getInt(key,defaultValue);
        }catch (Exception e){

        }
        XposedBridge.log("getBooleanDate key= "+key+" value= "+value);
        return value;

    }
}