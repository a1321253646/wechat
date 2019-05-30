package com.jackzheng.ourgame.demonadshowlib;


import android.os.Bundle;

import com.gionee.game.offlinesdk.floatwindow.AppInfo;
import com.gionee.game.offlinesdk.floatwindow.GamePlatform;
import com.gionee.gsp.GnEFloatingBoxPositionModel;

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

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        AppInfo appInfo = new AppInfo();
        appInfo.setApiKey(MainApplication.API_KEY);// apiKey由开发者后台申请得到
        appInfo.setPrivateKey(MainApplication.PRIVATE_KEY); //privateKey由开发者后台申请得到
        GamePlatform.init(this, appInfo, GnEFloatingBoxPositionModel.LEFT_TOP);
    }
}
