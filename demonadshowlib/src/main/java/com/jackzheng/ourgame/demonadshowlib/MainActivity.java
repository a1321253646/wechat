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

public class MainActivity extends MainActivityBase {

    private IAdWorker mAdWorker;
    IAdWorker mBannerAd;
    IAdWorker mSplashAd;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getmAdWorker();
    }


    private IAdWorker getmAdWorker(){
        if(mAdWorker != null){
            return  mAdWorker;
        }else{
            try {
                mAdWorker = AdWorkerFactory.getAdWorker(this, (ViewGroup) getWindow().getDecorView(), new MimoAdListener() {
                    @Override
                    public void onAdPresent() {

                        Log.e(TAG, "mAdWorker onAdPresent");
                        startGameOrPause(false);
                    }

                    @Override
                    public void onAdClick() {
                        Log.e(TAG, "mAdWorker onAdClick");
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
                        Log.e(TAG, "mAdWorker onAdDismissed");
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
                        Log.e(TAG, "onAdFailed s="+s);

                    }

                    @Override
                    public void onAdLoaded(int size) {
                        Log.e(TAG, "mAdWorker ad loaded");

                    }


                    @Override
                    public void onStimulateSuccess() {
                        Log.e(TAG, "mAdWorker ad onStimulateSuccess");
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
    public boolean isAdInit(String str){
        boolean isReady  = MimoSdk.isSdkReady();
        Log.d(TAG,"isAdInit==========================="+isReady);
        return isReady;
    }
    @Override
    public boolean isInserAdReady(String str){

        try {
            boolean ready =  getmAdWorker().isReady();
            Log.d(TAG,"isInserAdReady==========================="+ready);
            if(!ready){
                getmAdWorker().load(MainApplication.INSERT_ID);
            }
            return ready;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    ViewGroup mSplashView;
    @Override
    public void playSplashAdDeal( ){
        mSplashView = new FrameLayout(this) ;
        Log.d(TAG,"playSplashAdDeal===========================");
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

        Log.d(TAG,"playSplashAdDeal===========================");
        try {
            mSplashAd = AdWorkerFactory.getAdWorker(this, mSplashView, new MimoAdListener() {
                @Override
                public void onAdPresent() {
                    // 开屏广告展示
                    Log.d(TAG, "playSplashAdDeal onAdPresent");
                }

                @Override
                public void onAdClick() {
                    //用户点击了开屏广告
                    Log.d(TAG, "playSplashAdDeal onAdClick");
                    mSplashView.setVisibility(View.GONE);
                }

                @Override
                public void onAdDismissed() {
                    //这个方法被调用时，表示从开屏广告消失。
                    Log.d(TAG, "playSplashAdDeal onAdDismissed");
                    mSplashView.setVisibility(View.GONE);
                }

                @Override
                public void onAdFailed(String s) {
                    Log.e(TAG, "playSplashAdDeal ad fail message : " + s);
                    mSplashView.setVisibility(View.GONE);
                }

                @Override
                public void onAdLoaded(int size) {
                    //do nothing
                    Log.d(TAG, "playSplashAdDeal onAdLoaded");
                }

                @Override
                public void onStimulateSuccess() {
                    Log.d(TAG, "playSplashAdDeal  Log.d(TAG, \"playSplashAdDeal onAdLoaded\");");
                }
            }, AdType.AD_SPLASH);

            mSplashAd.loadAndShow(MainApplication.SPLASH_ID);
        } catch (Exception e) {
            e.printStackTrace();
            mSplashView.setVisibility(View.GONE);
        }
    }

    @Override
    public void playInerAdDeal(){
        Log.d(TAG, "playInerAdDeal");
        try {
            getmAdWorker().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isShowBanner = false;

    ViewGroup mBannerView;
    @Override
    public void startShowBannerDeal(){

        Log.e(TAG, "startShowBannerDeal");
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
            mWmParams.gravity = Gravity.BOTTOM|Gravity.CENTER;
            mWmParams.y = (int) y - mWmParams.height;
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
