package com.jackzheng.ourgame.demonadshowlib;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;


public class MainApplication extends Application {

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



    }

}
