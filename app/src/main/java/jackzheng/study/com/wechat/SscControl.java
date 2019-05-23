package jackzheng.study.com.wechat;

import android.os.Handler;
import android.util.ArrayMap;

import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import de.robv.android.xposed.XposedBridge;
import jackzheng.study.com.wechat.sscManager.ServerManager2;
import okhttp3.OkHttpClient;
import yaunma.com.myapplication.Tools;

public class SscControl {

    private Handler mHandler;

    public boolean isInit = false;

    public long mMessageCount = 0;



    public void sendMeassageBy(String id,String str){

        if(ServerManager2.getmIntance().isWork){
            mMessageCount++;
            Tools.sendTextToRoom(Tools.mActivity,str,id);
        }

    }

    public void kaijaingEnd(HtmlParse.MaxIndexResult parse){
        //TODO 开奖处理
        long delay = getStopTime();
      /*  if(delay == -1){
            mHandler.removeCallbacks(mRequitRun);
            mHandler.postDelayed(mRequitRun,getDelayMs());
        }else{*/
 //           mHandler.removeCallbacks(mRequitRun);
            mHandler.postDelayed(mStopRun,delay);
       // }
        ServerManager2.getmIntance().kaijiangDeal(parse);
    }

    HtmlParse.MaxIndexResult mCurrentResult = new HtmlParse.MaxIndexResult();
  /*  Runnable mRequitRun = new Runnable() {
        @Override
        public void run() {
            mCurrentResult.str = null;
            mCurrentResult.number= null;
            Thread thread= new Thread(){
                @Override
                public void run() {
                    while(true){

                        XposedBridge.log("当前期开奖期数 = "+mCurrentResult.index);
                        HtmlParse.MaxIndexResult result = HtmlParse.parseQuite(mCurrentResult.index);
                        if(result == null){
                            result = HtmlParse.parse(mCurrentResult.index);
                        }
                        if(result != null){
                            mCurrentResult = result;
                            kaijaingEnd(mCurrentResult);
                            break;
                        }
                    }
                }
            };
            thread.start();
        }
    };*/
    Runnable mStopRun = new Runnable() {
        @Override
        public void run() {
            //TODO 停止下注处理
            XposedBridge.log("mStopRun = ");
            mHandler.removeCallbacks(mStopRun);
        //    mHandler.postDelayed(mRequitRun,getDelayMs());
            mCurrentResult.index = getIndex();
            ServerManager2.getmIntance().stopDeal(mCurrentResult.index,null);
        }
    };
    Runnable mTimeRun  = new Runnable() {
        @Override
        public void run() {
            XposedBridge.log("mTimeRun isInit= "+isInit);
            if(!isInit){
                mHandler.removeCallbacks(mTimeRun);
                mHandler.postDelayed(mTimeRun,5000);
                return;
            }
            long delay = getStopTime();
           // if(delay == -1){
           //     mCurrentResult.index = getIndex();
           //     mHandler.removeCallbacks(mTimeRun);
           //     mHandler.postDelayed(mRequitRun,getDelayMs());
           // }else{
                mHandler.removeCallbacks(mTimeRun);
                mHandler.postDelayed(mStopRun,delay);
           // }
        }
    };

    public int getIndex(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int fen = hour*60+minute;
        return fen/5 +1;
    }

    private long getTime(int hour , int min){
        int fen = hour*60+min;
        return  (fen/5+1) *5-fen;
    }

    //hh:mm
    private long getDelayMs(){
        Calendar calendar = Calendar.getInstance();

        int currentH = calendar.get(Calendar.HOUR_OF_DAY);
        int currentM = calendar.get(Calendar.MINUTE);
        int currentS = calendar.get(Calendar.SECOND);
        int currentMs =calendar.get(Calendar.MILLISECOND);
        long ms = 0;
        long s = 0;
        long time = getTime(currentH, currentM);
        if(currentS != 0){
            time --;
            s = 60- currentS;
        }
        if(currentMs != 0){
            if(s == 0){
                time --;
                s = 59;
            }else{
                s -- ;
            }
            ms = 1000 - currentMs;
        }
        long delay =  time * 60000 + s * 1000 + ms;
        XposedBridge.log("mymsg time="+time+" s="+s+" ms="+ms);
        XposedBridge.log("mymsg delay="+delay);
        return delay;
    }
    private long getStopTime(){
        Calendar calendar = Calendar.getInstance();

        int currentH = calendar.get(Calendar.HOUR_OF_DAY);
        int currentM = calendar.get(Calendar.MINUTE);
        int currentS = calendar.get(Calendar.SECOND);
        int currentMs =calendar.get(Calendar.MILLISECOND);
        long time = getTime(currentH, currentM);
     //   if(time == 1 && currentS>= 50){
     //       return -1;
     //   }
        long ms = 0;
        long s = 0;
        if(currentS != 0){
            time --;
            s = 60- currentS;
        }
        if(currentMs != 0){
            if(s == 0){
                time --;
                s = 59;
            }else{
                s -- ;
            }
            ms = 1000 - currentMs;
        }
        s = s- 14;
        long delay =  time * 60000 + s * 1000 + ms;
        XposedBridge.log("mymsg time="+time+" s="+s+" ms="+ms);
        XposedBridge.log("mymsg stop = "+delay);
        return delay;
    }


    public void init(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(2000L, TimeUnit.MILLISECONDS)
                .readTimeout(2000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);

        mHandler = new Handler();
        mHandler.post(mTimeRun);
        mHandler.postDelayed(mSendRun,500);
        isInit = true;
    }


    Runnable mSendRun = new Runnable() {
        @Override
        public void run() {
            sendMessage();
            mHandler.removeCallbacks(mSendRun);
            mHandler.postDelayed(mSendRun,3000+mRandom.nextInt(500));
        }
    };
    ArrayList<MessageData> mMessageList = new ArrayList<>();
    public void sendMessage(String message,String id,boolean isNow){
        XposedBridge.log("sendMessage message = "+message+" id="+id+" isNow="+isNow);
        if(isNow){
            sendMeassageBy(id,message);
        }else{
            addMessageList(new MessageData(message,id));
        }
    }
    Object mMessageLock = new Object();
    private void addMessageList(MessageData data){
        synchronized (mMessageLock){
            mMessageList.add(data);
        }
    }


    private void sendMessage(){
        synchronized (mMessageLock){
            if(mMessageList.size() >0){
                MessageData data = mMessageList.get(0);
                sendMeassageBy(data.userid,data.msg);
                mMessageList.remove(0);
            }

        }
    }




    public static SscControl mIntance = new SscControl();
    Random mRandom  = null;
    private SscControl(){
        mRandom = new Random(134);
    }
    public static SscControl getIntance(){
        return mIntance;
    }
    public class MessageData{
        String msg;
        String userid;
        public MessageData(String message,String id){
            msg = message;
            userid = id;
        }
    }
}
