package com.jackzheng.ourgame.demonadshowlib;


import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;
import com.vivo.unionsdk.open.VivoExitCallback;
import com.vivo.unionsdk.open.VivoUnionSDK;

public class MainActivity extends MainActivityBase {


    @Override
    public boolean isAdInit(String str) {
        return false;
    }

    @Override
    public boolean isInserAdReady(String str) {
        return false;
    }

    @Override
    public void playSplashAdDeal() {

    }

    @Override
    public void playInerAdDeal(boolean isMust) {

    }

    @Override
    public void startShowBannerDeal() {

    }

    public String exitGame(String str){
        VivoUnionSDK.exit(this, new VivoExitCallback() {
            @Override
            public void onExitCancel() {
                //取消退出
            }

            @Override
            public void onExitConfirm() {
                UnityPlayer.UnitySendMessage("Main Camera", "exitGame", "param");
                finish();
            }
        });

        return "";
    }
}
