package com.jackzheng.ourgame.demonadshowlib;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.vivo.unionsdk.open.VivoUnionSDK;

import org.apache.commons.logging.Log;

public class MainApplication extends Application {

    public static String[] mAppKeyUmeng = {"5cd62f530cafb2cc4c0007df","5cd62f633fc1957973000f46","5cd62f783fc195f2680002ef","5cd62f884ca3570403000919","5cd62f9b570df34c47000fde",
            "5cd62fbc0cafb254a00004ab","5cd62fce570df31d670001d5","5cd62fdd3fc19537dd000660","5cd62fe73fc195f268000303"};

    public static String appKey = "670bd90cdccb094ae6a4d2753f6396c5";

    @Override
    public void onCreate() {
        super.onCreate();
        PackageManager manager = getPackageManager();
        String name = "";
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        android.util.Log.d("jackzhng","versionName  = "+name);
        if(TextUtils.isEmpty(name)){
            int i = 1/0;

        }else{
            String[] strs = name.split("\\.");
            name = strs[strs.length-1];

        }
        //
        android.util.Log.d("jackzhng","mAppKeyUmeng id = "+mAppKeyUmeng[Integer.parseInt(name)]);
        UMConfigure.setLogEnabled(true);
        UMConfigure.init(this, mAppKeyUmeng[Integer.parseInt(name)], "Umeng", UMConfigure.DEVICE_TYPE_PHONE,null);


        VivoUnionSDK.initSdk(this, appKey, false);
    }

}
