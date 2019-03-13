package com.jackzheng.ourgame.demonadshowlib;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.soulgame.sgsdk.tgsdklib.unity.TGSDKUnityActivity;

public class MainActivity extends TGSDKUnityActivity {

    public String showTaptap(String str){
        Log.d("jackzhng","playAd===========================");
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
        }
        return "";
    }

}
