package com.jackzheng.ourgame.demonadshowlib;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends MainActivityBase {


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
         //   fetchSplashAd();
        } else {
            String[] temp = new String[mNeedRequestPMSList.size()];
            mNeedRequestPMSList.toArray(temp);
            ActivityCompat.requestPermissions(this, temp, REQUEST_PERMISSIONS_CODE);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case REQUEST_PERMISSIONS_CODE:
                if (hasNecessaryPMSGranted()) {

                    fetchSplashAd();
                } else {

                    Toast.makeText(this, "􁓄􂭘􃕪􁉁 SDK 􄘀􃹼􁗵􄺫􂲴 READ_PHONE_STATE􀇃
                            WRITE_EXTERNAL_STORAGE 􀑔􀑚􁵳􄲀􀊽􄈧􂛩􀠫\"􁓄􂭘􁵳􄲀\"􀋈􁢃􁔰􁡰􄴰􃾱􂲴􁵳􄲀􀇄",
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

}
