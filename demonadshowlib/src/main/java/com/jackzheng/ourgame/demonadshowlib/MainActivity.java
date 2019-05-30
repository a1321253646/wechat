package com.jackzheng.ourgame.demonadshowlib;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.mob4399.adunion.AdUnionBanner;
import com.mob4399.adunion.AdUnionInterstitial;
import com.mob4399.adunion.AdUnionSDK;
import com.mob4399.adunion.listener.OnAuBannerAdListener;
import com.mob4399.adunion.listener.OnAuInitListener;
import com.mob4399.adunion.listener.OnAuInterstitialAdListener;

public class MainActivity extends MainActivityBase {

    public static String APP_ID = "1246";
    public static String BANNER_ID = "4684";
    public static String INSERT_ID = "4683";
    public static String SPLASH_ID = "4682";
    private  boolean isAdinit = false;
    private static int REQ_PERMISSION_CODE = 1001;
    private static final String[] PERMISSIONS = {Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        // Manifest.permission.WRITE_EXTERNAL_STORAGE 和  Manifest.permission.READ_PHONE_STATE是必须权限，允许这两个权限才会显示广告。

        if(isInit){
            return;
        }

        if (hasPermission(Manifest.permission.READ_PHONE_STATE)
                && hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            initSdk();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQ_PERMISSION_CODE);
        }
    }
    private boolean hasPermission(String permissionName) {
        return ActivityCompat.checkSelfPermission(this, permissionName)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isInit = false;
    private void initSdk(){
        if(isInit){
            return;
        }
        isInit = true;

        AdUnionSDK.init(this, MainActivity.APP_ID, new OnAuInitListener() {
            @Override
            public void onSucceed() {
                isAdinit = true;
                android.util.Log.d("jackzhng","AdUnionSDK init onSucceed  ");
            }

            @Override
            public void onFailed(String s) {
                android.util.Log.d("jackzhng","AdUnionSDK init onFailed s=   "+s);
                isAdinit = false;
            }
        });

    }

    @Override
    public boolean isAdInit(String str) {
        return isAdinit;
    }

    @Override
    public boolean isInserAdReady(String str) {
        return true;
    }

    @Override
    public void playSplashAdDeal() {
        this.startActivity(new Intent(this, SplashActivity.class));
    }

    private boolean mIsShowInsert = false;
    private long mPreShowInsertTime = -1;
    AdUnionInterstitial adUnionInterstitial = null;
    @Override
    public void playInerAdDeal(boolean isMust) {
        Log.d("jackzheng","playInerAdDeal" );

        if(!AdControlServer.getmIntance().chaping){
            return;
        }
        if(mIsShowInsert){
            return;
        }
        if(!isMust){
            if(mPreShowInsertTime != -1 && AdControlServer.getmIntance().cntime != -1){
                long time = System.currentTimeMillis();
                if(mPreShowInsertTime + AdControlServer.getmIntance().cntime*1000  > time ){
                    return;
                }
            }
        }

        adUnionInterstitial = new AdUnionInterstitial(this, INSERT_ID,
                new OnAuInterstitialAdListener() {
                    @Override
                    public void onInterstitialLoaded() {
                        Log.e(TAG, "Intertitial loaded and show");
                        startGameOrPause(false);
                        mPreShowInsertTime = System.currentTimeMillis();
                        mIsShowInsert = true;
                    }

                    @Override
                    public void onInterstitialLoadFailed(String message) {
                        Log.e(TAG, "Intertitial onInterstitialLoadFailed");
                        Log.e(TAG, message);
                        startGameOrPause(true);
                        mIsShowInsert = false;
                        mPreShowInsertTime = System.currentTimeMillis();
                    }

                    @Override
                    public void onInterstitialClicked() {
                        Log.e(TAG, "Intertitial clicked");
                    }

                    @Override
                    public void onInterstitialClosed() {
                        Log.e(TAG, "Intertitial closed");
                        startGameOrPause(true);
                        mIsShowInsert = false;
                        mPreShowInsertTime = System.currentTimeMillis();
                    }
                });
        //在需要展示插屏广告的位置调用show方法
        adUnionInterstitial.show();
    }

    private int mBannerViewHight;
    private int mBannerViewWight;
    private boolean isShowInsertAuto = true;
    private long mBannerPreShow = -1;
    private boolean mIsBannerShow = false;
    private FrameLayout mAdContainer;
    @Override
    public void startShowBannerDeal() {
        Log.d("jackzheng","startShowBannerDeal" );
        if(isShowInsertAuto){
            isShowInsertAuto = false;
            mHandler.sendEmptyMessageDelayed(4,5000);
        }
        if(!AdControlServer.getmIntance().banner){
            return;
        }
        if(mIsBannerShow){
            return;
        }
        if(mBannerPreShow != -1 && AdControlServer.getmIntance().bntime != -1){
            long time = System.currentTimeMillis();
            if(mBannerPreShow + AdControlServer.getmIntance().bntime*1000 > time ){
                return;
            }
        }
        if(mAdContainer == null){
            String[]  strs = mBannerPoint.split(",");
            float weight = Float.parseFloat(strs[0]);
            float y = Float.parseFloat(strs[1]);
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            float density = dm.density;
            mBannerViewHight = (int)(57 *density) ;
            mBannerViewWight = (int)(weight);

            mAdContainer = new FrameLayout(this);//创建帧布局对象layout
            FrameLayout.LayoutParams frameLayout = new FrameLayout.LayoutParams(
                    mBannerViewWight,
                    mBannerViewHight
            );//设置帧布局的高宽属性
            frameLayout.bottomMargin = 0;
            frameLayout.gravity = Gravity.BOTTOM | Gravity.CENTER;
            frameLayout.bottomMargin= (int) y - mBannerViewHight;
            addContentView(mAdContainer, frameLayout);
        }
        AdUnionBanner adUnionBanner = new AdUnionBanner();
        adUnionBanner.loadBanner(this, BANNER_ID, new OnAuBannerAdListener() {
            @Override
            public void onBannerLoaded(View bannerView) {
                Log.i("jackzheng","onBannerLoaded广告加载");
                //判断当前广告是否已被加载,若已被加载则进行移除后重新添加
                if (bannerView.getParent() != null) {
                    ((ViewGroup)bannerView.getParent()).removeView(bannerView);
                }
                //添加广告到容器中
                mBannerPreShow = System.currentTimeMillis();
                mIsBannerShow = true;
                mAdContainer.addView(bannerView);
            }

            @Override
            public void onBannerFailed(String s) {
                Log.i("jackzheng","onBannerFailed广告被点击");
                mBannerPreShow = System.currentTimeMillis();
                mIsBannerShow = false;
            }


            @Override
            public void onBannerClicked() {
                Log.i("jackzheng","onBannerClicked广告被点击");
            }

            @Override
            public void onBannerClosed() {
                Log.i("jackzheng","onBannerClosed广告被关闭");
                mBannerPreShow = System.currentTimeMillis();
                mIsBannerShow = false;
            }
        });
    }
}
