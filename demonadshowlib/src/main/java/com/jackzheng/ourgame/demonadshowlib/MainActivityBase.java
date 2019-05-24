package com.jackzheng.ourgame.demonadshowlib;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.nearme.game.sdk.GameCenterSDK;
import com.nearme.game.sdk.callback.GameExitCallback;
import com.umeng.analytics.MobclickAgent;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public abstract class MainActivityBase extends UnityPlayerActivity {
    public static final String TAG = "jackzhng";
    public Handler mHandler= null;
    public abstract boolean isAdInit(String str);
    public abstract boolean isInserAdReady(String str);

    public abstract void playSplashAdDeal( );
    public abstract void playInerAdDeal();
    public abstract void startShowBannerDeal();
    public String mBannerPoint = "";

    public static String CHANNEL_VERSION = "";

    public void startGameOrPause(boolean isStart){
        if(!isStart && isPause){
            return;
        }
        String action = "";
        if(isStart){
            action = "start";
        }else{
            action = "pause";
        }
        UnityPlayer.UnitySendMessage("Main Camera", "startOrPause", action);
    }

    private boolean isShowSplash = false;
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
                    playInerAdDeal();
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
                }
            }
        };
    }

    private boolean isPause = false;
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause===========================");
        MobclickAgent.onPause(this);
    }

    public boolean startShowBanner(String point){
        Log.d(TAG,"startShowBanner===========================");
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
    public String exitGame(String str){
        //Log.d("jackzhng","exitGame===========================");
        android.util.Log.d("jackzhng","exitGame===========================");
        GameCenterSDK.getInstance().onExit(this, new GameExitCallback(){

            @Override
            public void exitGame() {
                UnityPlayer.UnitySendMessage("Main Camera", "exitGame", "退出");
            }
        });
        return "";
    }
}
