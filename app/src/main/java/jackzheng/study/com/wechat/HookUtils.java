package jackzheng.study.com.wechat;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import com.ydscience.fakemomo.utils.f;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import jackzheng.study.com.wechat.sscManager.ServerManager;

public class HookUtils implements IXposedHookLoadPackage {

    public static final Integer[][] mTimeLsit ={
            {0,5},{0,10},{0,15},{0,20},{0,25},{0,30},{0,35},{0,40},{0,45},{0,50},{0,55},{1,0},
            {1,5},{1,10},{1,15},{1,20},{1,25},{1,30},{1,35},{1,40},{1,45},{1,50},{1,55},
            {10,0},{10,10},{10,20},{10,30},{10,40},{10,50},
            {11,0},{11,10},{11,20},{11,30},{11,40},{11,50},
            {12,0},{12,10},{12,20},{12,30},{12,40},{12,50},
            {13,0},{13,10},{13,20},{13,30},{13,40},{13,50},
            {14,0},{14,10},{14,20},{14,30},{14,40},{14,50},
            {15,0},{15,10},{15,20},{15,30},{15,40},{15,50},
            {16,0},{16,10},{16,20},{16,30},{16,40},{16,50},
            {17,0},{17,10},{17,20},{17,30},{17,40},{17,50},
            {18,0},{18,10},{18,20},{18,30},{18,40},{18,50},
            {19,0},{19,10},{19,20},{19,30},{19,40},{19,50},
            {20,0},{20,10},{20,20},{20,30},{20,40},{20,50},
            {21,0},{21,10},{21,20},{21,30},{21,40},{21,50},
            {22,0},{22,5},{22,10},{22,15},{22,20},{22,25},{22,30},{22,35},{22,40},{22,45},{22,50},{22,55},
            {23,0},{23,5},{23,10},{23,15},{23,20},{23,25},{23,30},{23,35},{23,40},{23,45},{23,50},{23,55},{24,0},
    } ;

    private static String a = "";
    private LoadPackageParam mLoad;
    private Object mSendObject;
    private static HookUtils mInstance;
    public  static  HookUtils getIntance(){
        return mInstance;
    }
    private boolean  isHook = false;
    Handler mHandler ;
    int mIndexMax = 0;

    public Handler getHandler(){
        return mHandler;
    }
    Context applicationContext;
    HtmlParse.MaxIndexResult mCurrentResult;
    Runnable mRequitRun = new Runnable() {
        @Override
        public void run() {
            Thread thread= new Thread(){
                @Override
                public void run() {
                    while(true){
                        mCurrentResult = HtmlParse.parse2();
                        if(mCurrentResult  == null){
                            XposedBridge.log("parseQuite2 没数据");
                        }else{
                            XposedBridge.log("parseQuite2 mIndexMax ="+mIndexMax+" parseQuite mCurrentResult index ="+mCurrentResult.index+" mCurrentResult str"+mCurrentResult.str);
                        }
                        if(mCurrentResult != null && ( (mIndexMax >= 10 &&  mCurrentResult.index >= mIndexMax) || (mIndexMax < 10 && mCurrentResult.index!= 120 &&  mCurrentResult.index >= mIndexMax) ) ){
                            mHandler.removeCallbacks(mTimeRun);
                            mHandler.postDelayed(mTimeRun,2000);
                            break;
                        }else{
                            mCurrentResult = HtmlParse.parseQuite();
                            if(mCurrentResult  == null){
                                XposedBridge.log("parseQuite 没数据");
                            }else{
                                XposedBridge.log("parseQuite mIndexMax ="+mIndexMax+" parseQuite mCurrentResult index ="+mCurrentResult.index+" mCurrentResult str"+mCurrentResult.str);
                            }
                            if(mCurrentResult != null && ( (mIndexMax >= 10 &&  mCurrentResult.index >= mIndexMax) || (mIndexMax < 10 && mCurrentResult.index!= 120 &&  mCurrentResult.index >= mIndexMax) ) ){
                                mHandler.removeCallbacks(mTimeRun);
                                mHandler.postDelayed(mTimeRun,2000);
                                break;
                            }else{
                                mCurrentResult = HtmlParse.parse();
                                if(mCurrentResult  == null){
                                    XposedBridge.log("parse 没数据");
                                }else{
                                    XposedBridge.log("parse mIndexMax ="+mIndexMax+" parse mCurrentResult index ="+mCurrentResult.index+" mCurrentResult str"+mCurrentResult.str);
                                }
                                if(mCurrentResult != null && ( (mIndexMax >= 10 && mCurrentResult.index >= mIndexMax) || (mIndexMax < 10 && mCurrentResult.index!= 120 &&  mCurrentResult.index >= mIndexMax) ) ){
                                    mHandler.removeCallbacks(mTimeRun);
                                    mHandler.postDelayed(mTimeRun,2000);
                                    break;
                                }else{
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            };
            thread.start();
        }
    };

    public int  kaijian(String number){
        mCurrentResult = new HtmlParse.MaxIndexResult();
        mCurrentResult.str  = number;

        mCurrentResult.index = mIndexMax + 1;
        if(mIndexMax == 121){
            mCurrentResult.index = 1;
        }

        mHandler.removeCallbacks(mTimeRun);
        mHandler.removeCallbacks(mRequitRun);
        mHandler.postDelayed(mTimeRun,50);
        return mCurrentResult.index;
    }

    public void tingXiaZhu(){
        ServerManager.getIntance().setFalseByAuto(mIndexMax);
        mHandler.removeCallbacks(mTimeRun);
        mHandler.removeCallbacks(mRequitRun);
        mHandler.post(mRequitRun);
    }

    Runnable mTimeRun  = new Runnable() {
        @Override
        public void run() {
            if(applicationContext == null){
                mHandler.removeCallbacks(mTimeRun);
                mHandler.postDelayed(mTimeRun,5000);
                return;
            }
            if(mCurrentResult != null){
                ServerManager.getIntance().announceByAuto(mCurrentResult.str,mCurrentResult.index,mIndexMax);
                mIndexMax = mCurrentResult.index;
                mCurrentResult = null;
            }

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH)+1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            int msecond =calendar.get(Calendar.MILLISECOND);
            XposedBridge.log("Calendar获取当前日期"+year+"年"+month+"月"+day+"日"+hour+":"+minute+":"+second+"."+msecond);
            long delay = 0;
         //   if(second < 50) {
                dealOther(hour,minute);
                mIndexMax = getIndex(hour, minute);
                if (isOpen) {
                    ServerManager.getIntance().setFalseByAuto(mIndexMax);
                   mHandler.post(mRequitRun);
                   return;
                } else {
                    Integer[] tmp;
                    tmp = mTimeLsit[mIndexMax];
                    XposedBridge.log("目标时间"+tmp[0]+"时"+tmp[1]+"分");
                    if((hour > 1 && hour <3)||(hour == 1  && minute > 55)) {
                        delay = getDelayMs(3, 0, hour, minute, second, msecond);//定时3：00 全面清盘
                    } else if((hour >7 && hour < 9 )||(hour == 9  && minute < 50)){
                        delay = getDelayMs(9, 50, hour, minute, second, msecond);//定时9：50 全面开盘
                    }else if (tmp[0] - hour > 1) {
                        delay = 3600000;
                    } else {
                        delay = getDelayMs(tmp[0],tmp[1],hour,minute,second,msecond);
                    }
                    delay =delay + 20*1000;
                }
           // }else{
            //    delay =(60-second)*1000;
            //}
            long delay2 = delay;
            msecond =(int) delay2 % 1000;
            delay2 = delay2/1000;
            second = (int)delay2 % 60;
            delay2 = delay2/60;
             minute = (int)delay2%60;
            delay2 = delay2/60;
             hour =(int) delay2;

            XposedBridge.log("延时为:"+hour+":"+minute+":"+second+"-"+msecond);
            mHandler.removeCallbacks(mTimeRun);
            mHandler.postDelayed(mTimeRun,delay);
        }
    };
    private void dealOther(long hour ,long min){
        if(hour == 3 && min == 0){
            ServerManager.getIntance().clearAllForAllGroup(true);
        }else if(hour == 9 && min == 50){
            ServerManager.getIntance().setTrueByDayStrart();
        }
        if(((hour > 3 && hour < 9) ||(hour == 9 && min <5))){
            if(ServerManager.getIntance().isHear){
                ServerManager.getIntance().openPhoneNoInTime();
            }else{
                String glq = StroeAdateManager.getmIntance().mGuanliQunID;
                if (!TextUtils.isEmpty(glq)) {
                    sendMeassageBy(glq,StroeAdateManager.getmIntance().mDeviceID+" "+hour+" : "+min);
                }

            }

        }
    }

    //hh:mm
    private long getDelayMs(long targetH,long targetM,long currentH,long currentM,long currentS,long currentMs){
        long ms = 0;
        long s = 0;
        long min = targetM;
        long hou = targetH;
        if (currentMs != 0) {
            ms = 1000 - currentMs;
            s = 59;
            if (min == 0) {
                min = 59;
                hou = hou - 1;
            } else {
                min = min - 1;
            }
        }
        if (currentS > s) {
            if (min == 0) {
                min = 59;
                hou = hou - 1;
            } else {
                min = min - 1;
            }
            s = s + 60 - currentS;
        }else{
            s = s-currentS;
        }
        if (currentM > min) {
            hou = hou - 1;
            min = min + 60 - currentM;
        }else{
            min = min - currentM;
        }
        hou = hou - currentH;
        return hou * 3600000 + min * 60000 + s * 1000 + ms;
    }
    private boolean isOpen;
    private int getIndex(int hour , int min){
        Integer[] time;
        if(hour == 0 && min == 0){
            isOpen =true;
            return mTimeLsit.length;
        }
        for(int i = 0 ; i<mTimeLsit.length;i++){
            time = mTimeLsit[i];
            if(time[0]  == hour  || (time[0] ==24 && hour ==0) ){
                if(time[1] <min){
                    continue;
                }else if(time[1] == min){
                    isOpen = true;
                    return i+1;
                }else{
                    isOpen = false;
                    return i;
                }
            }else if(hour < time[0]){
                isOpen = false;
                return i;
            }else{
                continue;
            }
        }
        return 0;
    }
    public void handleLoadPackage(LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals("com.tencent.mm")) {
            mLoad = loadPackageParam;
            XposedBridge.log("wechat version" + loadPackageParam.processName);
            if( loadPackageParam.processName.equals("com.tencent.mm")&&mHandler == null){
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH)+1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);
                int msecond =calendar.get(Calendar.MILLISECOND);

                if((hour <9 && hour >=2) || (hour == 9 && minute <50)||(hour == 1 && minute > 55) ){
                    ServerManager.getIntance().setFalseByDayEndNoNotification();
                    XposedBridge.log("初始设置为关");
                }else{
                    ServerManager.getIntance().setTrueByDayStrartNoNotification();
                    XposedBridge.log("初始设置为开");
                }
                mHandler = new Handler();
                mHandler.post(mTimeRun);
                //StroeAdateManager.getmIntance();
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                },60000);
            }
            mInstance = this;
            Context context = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread", new Object[0]), "getSystemContext", new Object[0]);
            String str = context.getPackageManager().getPackageInfo(loadPackageParam.packageName, 0).versionName;
            XposedBridge.log("wechat version" + str);
            a = str;
            f.a(str);

            try {
                Class<?> ContextClass = XposedHelpers.findClass("android.content.ContextWrapper", loadPackageParam.classLoader);
                XposedHelpers.findAndHookMethod(ContextClass, "getApplicationContext", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        applicationContext = (Context) param.getResult();
                //        XposedBridge.log("得到上下文");
                    }
                });
            } catch (Throwable t) {
              //  XposedBridge.log("获取上下文出错"+t);
            }

            XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase", loadPackageParam.classLoader, "insert", String.class,String.class, ContentValues.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String str = (String) param.args[0];
                   // mSendObject = param.thisObject;
                    XposedBridge.log("com.tencent.wcdb.database.SQLiteDatabase.insert str =   " + str);
                    Object[] args = param.args;
                    if(args == null ){
                        XposedBridge.log("wechat hookAllConstructors args ==null" );

                    }else if(args.length ==0){
                        XposedBridge.log("wechat hookAllConstructors args length==0" );
                    }else{
                        int i = 0;
                        for(Object ob : args){

                            if(ob instanceof  String){
                                XposedBridge.log("wechat hookAllConstructors args ["+i+"] is String :"+(String)ob );
                            }else if(ob instanceof  Integer){
                                XposedBridge.log("wechat hookAllConstructors args ["+i+"] is Integer :"+(Integer)ob );
                            }else if(ob instanceof  Long){
                                XposedBridge.log("wechat hookAllConstructors args ["+i+"] is Integer :"+(Long)ob );
                            }else if(ob != null){
                                XposedBridge.log("wechat hookAllConstructors args ["+i+"] is"+ ob.getClass().getName() );
                            }
                            i++;
                         }
                    }
                    int type = -1;
                    boolean isSend = false;
                    String GroupID = "";
                    String content = "";
                    String userId = "";

                    if(args[2] != null ){
                        ContentValues arg = (ContentValues) args[2];
                        if(arg.containsKey("type")){
                            type = arg.getAsInteger("type");
                        }
                        if(arg.containsKey("talker")){
                            userId = arg.getAsString("talker");
                        }
                        if(arg.containsKey("content")){
                            content = arg.getAsString("content");
                        }
                        if(arg.containsKey("isSend")){
                            Integer isSend1 = arg.getAsInteger("isSend");
                            if(isSend1 != null && isSend1 == 1){
                                isSend = true;
                            }
                        }
                    }
                    if(!TextUtils.isEmpty(userId) && userId.endsWith("@chatroom")){

                        GroupID = userId;
                        String[] split = content.split(":");
                        if(split.length >1){
                            userId = split[0];
                        }
                        content = content.replace(userId+":\n","");
                    }
                    XposedBridge.log("GroupID = "+GroupID+" userId = "+userId+" content = "+content+" isSend = "+isSend+" type ="+type);
                    if(!isSend && type == 1 && !TextUtils.isEmpty(userId)){
                        getMessage(userId,GroupID,content);
                    }
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if(mSendObject == null){
                        appInitHook();
                    }
                }
            });

        }
    }
    public void getMessage(String talkerId,String groupId,String content){
        if(applicationContext == null){
            return;
        }
        XposedBridge.log("getMessage");
        ServerManager.getIntance().receiveMessage(content,talkerId,groupId);
    }

    public void sendMeassageBy(String id,String str){
        XposedBridge.log("mSendObject = "+ mSendObject);
        if(mSendObject != null){
            XposedHelpers.callMethod(mSendObject,"Dl",new Class[]{String.class},"liesi"+id+"&"+str);
        }
    }
    private void appInitHook( ){
        Class aClass1 = XposedHelpers.findClass("com.tencent.mm.ui.chatting.al", mLoad.classLoader);

        XposedBridge.hookAllConstructors(aClass1, new XC_MethodHook() {
            protected final void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                XposedBridge.log("com.tencent.mm.ui.chatting.al hookAllConstructors" );
                Object[] args = methodHookParam.args;
                if(args == null ){
                    XposedBridge.log("com.tencent.mm.ui.chatting.al hookAllConstructors args ==null" );
                }else if(args.length ==0){
                    XposedBridge.log("com.tencent.mm.ui.chatting.al hookAllConstructors args length==0" );
                }else{
                    int i = 0;
                    for(Object ob : args){
                        if(ob == null){
                            XposedBridge.log("com.tencent.mm.ui.chatting.al hookAllConstructors args ["+i+"] is String :"+(String)ob );
                        }else if(ob instanceof  String){
                            XposedBridge.log("com.tencent.mm.ui.chatting.al hookAllConstructors args ["+i+"] is String :"+(String)ob );
                        }else if(ob instanceof  Integer){
                            XposedBridge.log("com.tencent.mm.ui.chatting.al hookAllConstructors args ["+i+"] is Integer :"+(Integer)ob );
                        }else if(ob instanceof  Long){
                            XposedBridge.log("com.tencent.mm.ui.chatting.al hookAllConstructors args ["+i+"] is Integer :"+(Long)ob );
                        }else{
                            XposedBridge.log("com.tencent.mm.ui.chatting.al hookAllConstructors args ["+i+"] is"+ ob.getClass().getName() );
                        }
                        i++;
                    }
                }
                mSendObject = methodHookParam.thisObject;
                XposedBridge.log("mSendObject = "+ mSendObject);
            }
        });

        XposedHelpers.findAndHookMethod(aClass1,"Dl",String.class,new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String str = (String) param.args[0];
                XposedBridge.log("com.tencent.mm.ui.chatting.al.dl str =   " + str);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
        Class aClass2 = XposedHelpers.findClass("com.tencent.mm.modelmulti.j", mLoad.classLoader);

        XposedBridge.hookAllConstructors(aClass2, new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if(param.args != null){
                    XposedBridge.log("com.tencent.mm.mudelmulti.j hookAllConstructors before srgs leng="+param.args.length  );
                    if(param.args.length == 5 && param.args[1] instanceof  String){
                        String message =  (String)param.args[1];
                        if(!TextUtils.isEmpty(message) && message.startsWith("liesi")){
                            message = message.replace("liesi","");
                            param.args[0] = message.split("&")[0];
                            param.args[1] = message.replaceFirst(param.args[0]+"&","");
                        }else if(!TextUtils.isEmpty(message) && message.startsWith("vx机")){
                            String id = message.replace("vx机","");
                            StroeAdateManager.getmIntance().setDeviceID(id, (String)param.args[0]);
                        }/*else if(!TextUtils.isEmpty(message) && message.startsWith("表情测试")){
                            param.args[1] = "wxid_d67dg24okmqp12:1541269558576:0:cf5df8fb5e829e2033277c461675e3fb::0";
                            param.args[2] = 47;
                        }*/

                    }

                }else{
                    XposedBridge.log("com.tencent.mm.mudelmulti.j hookAllConstructors before srgs =null"  );
                }

            }


            protected final void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                XposedBridge.log("com.tencent.mm.mudelmulti.j hookAllConstructors" );
                Object[] args = methodHookParam.args;
                if(args == null ){
                    XposedBridge.log("com.tencent.mm.mudelmulti.j hookAllConstructors args ==null" );
                }else if(args.length ==0){
                    XposedBridge.log("com.tencent.mm.mudelmulti.j hookAllConstructors args length==0" );
                }else{
                    int i = 0;
                    for(Object ob : args){
                        if(ob == null){
                            XposedBridge.log("com.tencent.mm.mudelmulti.j hookAllConstructors args ["+i+"] is String :"+(String)ob );
                        }else if(ob instanceof  String){
                            XposedBridge.log("com.tencent.mm.mudelmulti.j hookAllConstructors args ["+i+"] is String :"+(String)ob );
                        }else if(ob instanceof  Integer){
                            XposedBridge.log("com.tencent.mm.mudelmulti.j hookAllConstructors args ["+i+"] is Integer :"+(Integer)ob );
                        }else if(ob instanceof  Long){
                            XposedBridge.log("com.tencent.mm.mudelmulti.j hookAllConstructors args ["+i+"] is Integer :"+(Long)ob );
                        }else{
                            XposedBridge.log("com.tencent.mm.mudelmulti.j hookAllConstructors args ["+i+"] is"+ ob.getClass().getName() );
                        }
                        i++;
                     }
                }
            }
        });
    }
}
