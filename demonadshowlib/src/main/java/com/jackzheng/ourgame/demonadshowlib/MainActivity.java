package com.jackzheng.ourgame.demonadshowlib;


public class MainActivity extends MainActivityBase {

    public static String APP_ID = "1246";
    public static String BANNER_ID = "4684";
    public static String INSERT_ID = "4683";
    public static String SPLASH_ID = "4682";


    @Override
    public boolean isAdInit(String str) {
        return MainApplication.isAdinit;
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
}
