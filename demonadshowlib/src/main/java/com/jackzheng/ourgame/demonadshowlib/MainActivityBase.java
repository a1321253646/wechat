package com.jackzheng.ourgame.demonadshowlib;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public abstract class MainActivityBase extends UnityPlayerActivity {
    public static final String TAG = "jackzhng";
    public Handler mHandler= null;
    public abstract boolean isAdInit(String str);
    public abstract boolean isInserAdReady(String str);

    public abstract void playSplashAdDeal( );
    public abstract void playInerAdDeal(boolean isMust);
    public abstract void startShowBannerDeal();
    public String mBannerPoint = "";

    public static String CHANNEL_VERSION = "";

    public void startGameOrPause(boolean isStart){
        Log.d("jackzheng","startGameOrPause isStart="+isStart+" isPause="+isPause);
        if(!isStart && isPause){
            return;
        }
        String action = "";
        if(isStart){
            action = "start";
            isPause = false;
        }else{
            action = "pause";
            isPause = true;
        }
        UnityPlayer.UnitySendMessage("Main Camera", "startOrPause", action);
    }

    private boolean isShowSplash = false;
    private boolean isFristAutoInsert = true;
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume===========================");
        MobclickAgent.onResume(this);
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1){
                    playInerAdDeal(false);
                }else if(msg.what == 2){
                    mHandler.removeMessages(2);
                    if(isAdInit("")){
                        startShowBannerDeal();
                    }else{
                        mHandler.sendEmptyMessageDelayed(2,500);
                    }

                }else if(msg.what == 3){
                    mHandler.removeMessages(3);
                    if(isAdInit("")){
                        playSplashAdDeal();
                    }else{
                        mHandler.sendEmptyMessageDelayed(3,500);
                    }
                }else if(msg.what == 4){
                    mHandler.removeMessages(4);
                    if(!AdControlServer.getmIntance().isGet){
                        mHandler.sendEmptyMessageDelayed(4,1000);
                    }else if(AdControlServer.getmIntance().adtime   > 0){
                        if(isFristAutoInsert){
                            isFristAutoInsert = false;
                        }else{
                            playInerAdDeal(true);
                        }
                        mHandler.sendEmptyMessageDelayed(4,AdControlServer.getmIntance().adtime *1000);
                    }
                }
            }
        };
        if(!isShowSplash){
            mHandler.sendEmptyMessageDelayed(3,500);
            isShowSplash = true;
        }


    }


    private boolean isPause = false;
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause===========================");
        MobclickAgent.onPause(this);
    }

    public boolean startShowBanner(String point){
        mBannerPoint = point;
        mHandler.sendEmptyMessage(2);
        return true;
    }
    public boolean playInerAd(String str){
        Log.d(TAG,"playInerAd===========================");
        mHandler.sendEmptyMessage(1);
        return true;
    }
    public boolean playSplashAd(String str){
        Log.d(TAG,"playSplashAd===========================");
        mHandler.sendEmptyMessage(3);
        return true;
    }

    public String showTaptap(String str){
  /*      Log.d("jackzhng","playAd===========================");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("taptap://taptap.com/app?app_id=150760&source=outer"));
        if (intent != null && intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }else{
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("http://d.taptap.com/latest?app_id=150760"));
            startActivity(intent);
        }*/
        return "";
    }
}
