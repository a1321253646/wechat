package jackzheng.study.com.wechat;

import android.os.Handler;
import android.text.TextUtils;

import java.util.Calendar;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import jackzheng.study.com.wechat.sscManager.ServerManager;
import yaunma.com.myapplication.Tools;

public class SscControl {

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
    public int mIndexMax = 0;

    private Handler mHandler;

    boolean isInit = false;

    public Handler getHandler(){
        return mHandler;
    }


    private void sendMessageDealBefore(String talkerId,String message){
        if(!TextUtils.isEmpty(message) && message.startsWith("vx机")){
            String id = message.replace("vx机","");
            String[] list = id.split("&");
            if(list.length == 1){
                NetManager.getIntance().loginIn(id, message,1+"");
            }else{
                NetManager.getIntance().loginIn(list[0],message,list[1]);
            }
        }
        Tools.sendTextToRoom(Tools.mActivity,message,talkerId);

    }
    public void getMessage(String talkerId,String groupId,String content){

    }

    public void sendMeassageBy(String id,String str){
        sendMessageDealBefore(id,str);
    }

    public void kaijaingEnd(){
        ServerManager.getIntance().setTrueByAuto();
        mHandler.removeCallbacks(mRequitRun);
        mHandler.postDelayed(mTimeRun,30000);
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        mIndexMax = getIndex(hour, minute);
    }

    Runnable mRequitRun = new Runnable() {
        @Override
        public void run() {
            NetManager.getIntance().kaijaing();
            mHandler.removeCallbacks(mRequitRun);
            mHandler.postDelayed(mRequitRun,10000);
        }
    };

    Runnable mTimeRun  = new Runnable() {
        @Override
        public void run() {
            if(!isInit){
                return;
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
            if(second < 50) {
                dealOther(hour,minute);
                mIndexMax = getIndex(hour, minute);
                if (isOpen ) {
                    ServerManager.getIntance().setFalseByAuto();
                    mHandler.post(mRequitRun);
                    mHandler.removeCallbacks(mTimeRun);
                    return;
                } else {
                    Integer[] tmp;
                    tmp = mTimeLsit[mIndexMax-1];
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
                    delay =delay + 40*1000;
                }
            }else{
                delay =(60-second)*1000;
            }
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
        if(hour == 9 && min == 50){
            ServerManager.getIntance().setTrueByDayStrart();
        }
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
                    return i+1;
                }
            }else if(hour < time[0]){
                isOpen = false;
                return i+1;
            }else{
                continue;
            }
        }
        return 0;
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


    public void init(){
        mHandler = new Handler();
        mHandler.post(mTimeRun);
        isInit = true;
    }

    public static SscControl mIntance = new SscControl();
    private SscControl(){

    }
    public static SscControl getIntance(){
        return mIntance;
    }
}
