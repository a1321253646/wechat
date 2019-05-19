package com.jackzheng.ourgame.demonadshowlib;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.miui.zeus.mimo.sdk.MimoSdk;
import com.miui.zeus.mimo.sdk.ad.AdWorkerFactory;
import com.miui.zeus.mimo.sdk.ad.IAdWorker;
import com.miui.zeus.mimo.sdk.listener.MimoAdListener;
import com.umeng.analytics.MobclickAgent;
import com.unity3d.player.UnityPlayerActivity;
import com.xiaomi.ad.common.pojo.AdType;

import miui.os.zeus.Build;

public class MainActivity extends UnityPlayerActivity {

    private IAdWorker mAdWorker;
    public static final String TAG = "HorizonInterstitial";
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            mAdWorker = AdWorkerFactory.getAdWorker(this, (ViewGroup) getWindow().getDecorView(), new MimoAdListener() {
                @Override
                public void onAdPresent() {
                    Log.e(TAG, "onAdPresent");
                }

                @Override
                public void onAdClick() {
                    Log.e(TAG, "onAdClick");
                }

                @Override
                public void onAdDismissed() {
                    Log.e(TAG, "onAdDismissed");
                    try {
                        mAdWorker.load("b90939fae6df77c0b5989fbac3ef810a");
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("jackzhng","onResume===========================");
        MobclickAgent.onResume(this);

    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("jackzhng","onPause===========================");
        MobclickAgent.onPause(this);
    }

    public boolean isAdInit(String str){
        return MimoSdk.isSdkReady();
    }

    public boolean isInserAdReady(String str){
        try {
            boolean ready =  mAdWorker.isReady();
            if(!ready){
                mAdWorker.load("b90939fae6df77c0b5989fbac3ef810a");
            }
            return ready;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void playInerAd(String str){
        try {
            mAdWorker.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            mAdWorker.recycle();
        } catch (Exception e) {
        }
    }

}
