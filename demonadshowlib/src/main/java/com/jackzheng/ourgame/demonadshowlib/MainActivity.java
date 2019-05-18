package com.jackzheng.ourgame.demonadshowlib;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.nearme.game.sdk.GameCenterSDK;
import com.nearme.game.sdk.callback.GameExitCallback;
import com.umeng.analytics.MobclickAgent;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public class MainActivity extends UnityPlayerActivity {

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
        Log.d("jackzhng","exitGame===========================");
        GameCenterSDK.getInstance().onExit(this, new GameExitCallback(){

            @Override
            public void exitGame() {
                UnityPlayer.UnitySendMessage("Main Camera", "exitGame", "退出");
            }
        });
        return "";
    }
}
