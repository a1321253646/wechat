package com.jackzheng.ourgame.demonadshowlib;


public class MainActivity extends MainActivityBase {

    public static final String APP_ID = "5018777";
    public static final String BANNER_ID = "918777504";
    public static final String INSERT_ID = "918777140";
    public static final String SPLASH_ID = "818777900";


    @Override
    public boolean isAdInit(String str) {
        return true;
    }

    @Override
    public boolean isInserAdReady(String str) {
        return true;
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
}
