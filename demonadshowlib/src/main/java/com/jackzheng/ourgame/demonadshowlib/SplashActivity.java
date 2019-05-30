package com.jackzheng.ourgame.demonadshowlib;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.ViewGroup;

import com.mob4399.adunion.AdUnionParams;
import com.mob4399.adunion.AdUnionSDK;
import com.mob4399.adunion.AdUnionSplash;
import com.mob4399.adunion.listener.OnAuInitListener;
import com.mob4399.adunion.listener.OnAuSplashAdListener;

public class SplashActivity extends Activity implements OnAuSplashAdListener {
    public static final String TAG = "SplashActivity";

    private static final String SPLASH_POS_ID = "2";
    private ViewGroup container;



    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        container = (ViewGroup) findViewById(R.id.layout_splash_container);
        fetchAD();
    }



    AdUnionSplash adUnionSplash;
    /**
     * 加载闪屏广告
     */
    private void fetchAD() {
        //SDK在初始化完成后调用
        adUnionSplash = new AdUnionSplash();
        adUnionSplash.loadSplashAd(this,container, MainActivity.SPLASH_ID,
                this);

    }

    @Override
    public void onSplashExposure() {
        Log.i(TAG,"Splash ad loaded");

    }

    @Override
    public void onSplashDismissed() {
        Log.i(TAG,"Splash ad dismissed");
        this.finish();
    }

    @Override
    public void onSplashLoadFailed(String message) {
        Log.i(TAG,"Splash ad load failed," + message);
        this.finish();
    }

    @Override
    public void onSplashClicked() {
        Log.i(TAG,"Splash ad clicked");
    }

}
