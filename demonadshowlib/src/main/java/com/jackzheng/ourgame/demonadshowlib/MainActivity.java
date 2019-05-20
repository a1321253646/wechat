package com.jackzheng.ourgame.demonadshowlib;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.miui.zeus.mimo.sdk.MimoSdk;
import com.miui.zeus.mimo.sdk.ad.AdWorkerFactory;
import com.miui.zeus.mimo.sdk.ad.IAdWorker;
import com.miui.zeus.mimo.sdk.listener.MimoAdListener;
import com.umeng.analytics.MobclickAgent;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;
import com.xiaomi.ad.common.pojo.AdType;

import miui.os.zeus.Build;

public class MainActivity extends UnityPlayerActivity {

    private IAdWorker mAdWorker;
    IAdWorker mBannerAd;
    IAdWorker mSplashAd;
    public static final String TAG = "jackzhng";
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getmAdWorker();
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
                    playSplashAdDeal();
                }else if(msg.what == 4){
                    mHandler.removeMessages(4);
                    if(isAdInit("")){
                        playSplashAdDeal();
                    }else{
                        mHandler.sendEmptyMessageDelayed(4,500);
                    }
                }
            }
        };
    }


    private Handler mHandler= null;

    private IAdWorker getmAdWorker(){
        if(mAdWorker != null){
            return  mAdWorker;
        }else{
            try {
                mAdWorker = AdWorkerFactory.getAdWorker(this, (ViewGroup) getWindow().getDecorView(), new MimoAdListener() {
                    @Override
                    public void onAdPresent() {

                        Log.e(TAG, "onAdPresent");
                        startGameOrPause(false);
                    }

                    @Override
                    public void onAdClick() {
                        Log.e(TAG, "onAdClick");
                        try {
                            startGameOrPause(true);
                            getmAdWorker().recycle();
                            getmAdWorker().load(MainApplication.INSERT_ID);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onAdDismissed() {
                        Log.e(TAG, "onAdDismissed");
                        try {
                            startGameOrPause(true);
                            getmAdWorker().recycle();
                            getmAdWorker().load(MainApplication.INSERT_ID);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onAdFailed(String s) {
                        Log.e(TAG, "onAdFailed");

                    }

                    @Override
                    public void onAdLoaded(int size) {
                        Log.e(TAG, "ad loaded");

                    }


                    @Override
                    public void onStimulateSuccess() {
                    }
                }, AdType.AD_INTERSTITIAL);
                return mAdWorker;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("jackzhng","onResume===========================");
        MobclickAgent.onResume(this);
    //    mHandler.sendEmptyMessageDelayed(4,500);
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("jackzhng","onPause===========================");
        MobclickAgent.onPause(this);
    }

    public boolean isAdInit(String str){

        boolean isReady  = MimoSdk.isSdkReady();
        Log.d("jackzhng","isAdInit==========================="+isReady);
        return isReady;
    }

    public boolean isInserAdReady(String str){

        try {
            boolean ready =  getmAdWorker().isReady();
            Log.d("jackzhng","isInserAdReady==========================="+ready);
            if(!ready){
                getmAdWorker().load(MainApplication.INSERT_ID);
            }
            return ready;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean playInerAd(String str){
        Log.d("jackzhng","playInerAd===========================");
        mHandler.sendEmptyMessage(1);

        return true;
    }
    public boolean playSplashAd(String str){
        Log.d("jackzhng","playSplashAd===========================");
        mHandler.sendEmptyMessage(3);
        return true;
    }
    ViewGroup mSplashView;
    private void playSplashAdDeal( ){
        mSplashView = new FrameLayout(this) ;
        Log.d("jackzhng","playSplashAdDeal===========================");
        String[]  strs = mBannerPoint.split(",");
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        WindowManager mWindowManager = (WindowManager) MainActivity.this.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams mWmParams = new WindowManager.LayoutParams();
        mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        mWmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mWmParams.gravity = Gravity.BOTTOM|Gravity.CENTER;
        mWindowManager.addView(mSplashView, mWmParams);

        Log.d("jackzhng","playInerAd===========================");
        try {
            mSplashAd = AdWorkerFactory.getAdWorker(this, mSplashView, new MimoAdListener() {
                @Override
                public void onAdPresent() {
                    // 开屏广告展示
                    Log.d(TAG, "onAdPresent");
                }

                @Override
                public void onAdClick() {
                    //用户点击了开屏广告
                    Log.d(TAG, "onAdClick");
                    mSplashView.setVisibility(View.GONE);
                }

                @Override
                public void onAdDismissed() {
                    //这个方法被调用时，表示从开屏广告消失。
                    Log.d(TAG, "onAdDismissed");
                    mSplashView.setVisibility(View.GONE);
                }

                @Override
                public void onAdFailed(String s) {
                    Log.e(TAG, "ad fail message : " + s);
                    mSplashView.setVisibility(View.GONE);
                }

                @Override
                public void onAdLoaded(int size) {
                    //do nothing
                }

                @Override
                public void onStimulateSuccess() {
                }
            }, AdType.AD_SPLASH);

            mSplashAd.loadAndShow(MainApplication.SPLASH_ID);
        } catch (Exception e) {
            e.printStackTrace();
            mSplashView.setVisibility(View.GONE);
        }
    }


    private void playInerAdDeal(){
        try {
            getmAdWorker().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startGameOrPause(boolean isStart){
        String action = "";
        if(isStart){
            action = "start";
        }else{
            action = "pause";
        }
        UnityPlayer.UnitySendMessage("Main Camera", "startOrPause", action);
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

    private boolean isShowBanner = false;
    private String mBannerPoint = "";
    public boolean startShowBanner(String point){
        mBannerPoint = point;
        mHandler.sendEmptyMessage(2);

        return true;
    }
    ViewGroup mBannerView;
    private void startShowBannerDeal(){

        Log.e(TAG, "startShowBanner");
        if(isShowBanner){
            return ;
        }
        if(mBannerView == null){
            mBannerView = new FrameLayout(this) ;
            String[]  strs = mBannerPoint.split(",");
            float weight = Float.parseFloat(strs[0]);
            float y = Float.parseFloat(strs[1]);
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            float density = dm.density;
            float screenHeight = dm.heightPixels;
            WindowManager mWindowManager = (WindowManager) MainActivity.this.getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams mWmParams = new WindowManager.LayoutParams();
            mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mWmParams.height =(int)( 54 *density);
            mWmParams.width = (int)weight;
            mWmParams.gravity = Gravity.TOP|Gravity.CENTER;
            mWmParams.y = (int)(screenHeight- y);
            mWindowManager.addView(mBannerView, mWmParams);
        }

        if(mBannerAd == null){
            try {
                mBannerAd = AdWorkerFactory.getAdWorker(this, mBannerView, new MimoAdListener() {
                    @Override
                    public void onAdPresent() {
                        Log.e(TAG, "startShowBannerDeal onAdPresent");
                    }

                    @Override
                    public void onAdClick() {
                        Log.e(TAG, "startShowBannerDeal onAdClick");
                     /*   try {
                            mBannerAd.loadAndShow("6a5cfe092eb49ba667221ea9189e0cd9");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                    }

                    @Override
                    public void onAdDismissed() {
                        Log.e(TAG, "startShowBannerDeal onAdDismissed");
                     /*   try {
                            mBannerAd.loadAndShow("6a5cfe092eb49ba667221ea9189e0cd9");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                    }

                    @Override
                    public void onAdFailed(String s) {
                        Log.e(TAG, "startShowBannerDeal onAdFailed :"+s);
                        try {
                            isShowBanner = false;
                            mHandler.sendEmptyMessageDelayed(2,500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onAdLoaded(int size) {
                        Log.e(TAG, "startShowBannerDeal onAdLoaded");
                    }

                    @Override
                    public void onStimulateSuccess() {
                        Log.e(TAG, "startShowBannerDeal onStimulateSuccess");
                    }
                }, AdType.AD_BANNER);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try{
            mBannerAd.loadAndShow(MainApplication.BANNER_ID);
            isShowBanner = true;
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            getmAdWorker().recycle();
            mBannerAd.recycle();
        } catch (Exception e) {
        }
    }

}
