package com.jackzheng.ourgame.demonadshowlib;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.oppo.mobad.api.InitParams;
import com.oppo.mobad.api.MobAdManager;
import com.oppo.mobad.api.ad.SplashAd;
import com.oppo.mobad.api.listener.ISplashAdListener;
import com.oppo.mobad.api.params.SplashAdParams;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends MainActivityBase {

    public final static String  APP_ID = "30085137";
    public final static String  SPLASH_ID = "60939";


    @Override
    public boolean isAdInit(String str) {
        return true;
    }

    @Override
    public boolean isInserAdReady(String str) {
        return false;
    }

    @Override
    public void playSplashAdDeal() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            checkAndRequestPermissions();
        } else {
            fetchSplashAd();
        }
    }

    private void fetchSplashAd() {
           Intent intent = new Intent(this, SplashActivity.class);
           startActivity(intent);
    }


    @Override
    public void playInerAdDeal() {

    }

    @Override
    public void startShowBannerDeal() {

    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Map<String,String> allParams = new HashMap<String,String>();
        allParams.put("channel","oppo");
        try {
            AdControlServer.getmIntance().getInitDate("http://milihuyu.com/game/ads.php",allParams,this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        InitParams initParams = new InitParams.Builder()
                .setDebug(true)
                .build();
        MobAdManager.getInstance().init(this,APP_ID, initParams);
        checkAndRequestPermissions();
    }
    ArrayList<String> mNeedRequestPMSList = new ArrayList<>();
    private void checkAndRequestPermissions() {


        if (PackageManager.PERMISSION_GRANTED !=
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            mNeedRequestPMSList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (PackageManager.PERMISSION_GRANTED !=
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            mNeedRequestPMSList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (PackageManager.PERMISSION_GRANTED !=
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)) {
            mNeedRequestPMSList.add(Manifest.permission.WRITE_CALENDAR);
        }
        if (PackageManager.PERMISSION_GRANTED !=
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            mNeedRequestPMSList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (0 == mNeedRequestPMSList.size()) {
            fetchSplashAd();
        } else {
            String[] temp = new String[mNeedRequestPMSList.size()];
            mNeedRequestPMSList.toArray(temp);
            ActivityCompat.requestPermissions(this, temp, REQUEST_PERMISSIONS_CODE);
        }
    }
    

    private static  final  long FETCH_TIME_OUT = 5000;
    private static  final  String APP_TITLE = "oppo游戏开屏广告";
    private static  final  String APP_DESC = "测试测试";
    private SplashAd mSplashAd = null;

    public static final int REQUEST_PERMISSIONS_CODE = 10001;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case REQUEST_PERMISSIONS_CODE:
                if (hasNecessaryPMSGranted()) {

                   // fetchSplashAd();
                } else {

                    Toast.makeText(this, "应用缺少SDK必须的 EAD_PHONE_STATE WRITE_EXTERNAL_STORAGE 两个权限 请点击\"应用权限\"打开所需要的权限",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                    finish();
                }
                break;
            default:
                break;
        }
    }
    private boolean hasNecessaryPMSGranted() {
        if (PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            if (PackageManager.PERMISSION_GRANTED ==
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String exitGame(String str) {
        MobAdManager.getInstance().exit(this);
        return super.exitGame(str);
    }
}
