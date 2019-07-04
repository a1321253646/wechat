package com.jackzheng.ourgame.demonadshowlib;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;
import com.vivo.mobilead.banner.VivoBannerAd;
import com.vivo.mobilead.interstitial.VivoInterstialAd;
import com.vivo.mobilead.listener.IAdListener;
import com.vivo.mobilead.manager.VivoAdManager;
import com.vivo.mobilead.model.VivoAdError;
import com.vivo.unionsdk.open.VivoExitCallback;
import com.vivo.unionsdk.open.VivoUnionSDK;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends MainActivityBase {

    public final static String  APP_ID = "30085137";
    public final static String  SPLASH_ID = "8f6f587a9fce40fd8c24d0d8e0a4ba36";
    public final static String[]  BANNER_ID = {"132ac4d9a26e4fb0abd89beafd46db47","819d80a7e6b54d11b22c3bf2f6c628bd"};
    public final static String[]  INSERT_ID = {"9f547e4cb3984d608a71a88689c90feb","873b0939908e46be8a92c7601fb63bfd"};

    public  boolean isAdInit = false;
    @Override
    public boolean isAdInit(String str) {
        return true;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Map<String,String> allParams = new HashMap<String,String>();
        allParams.put("channel","vivo");
        try {
            AdControlServer.getmIntance().getInitDate("http://milihuyu.com/game/ads.php",allParams,this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VivoAdManager.getInstance().init(this, "db54b8ec86db46e9b7b985eba119628f");
    }



    @Override
    public boolean isInserAdReady(String str) {
        return true;
    }

    private boolean  isReadyInsert = false;
    private long mPreShowInsertTime = -1;
    VivoInterstialAd mInterAd = null;
    @Override
    public void playInerAdDeal(boolean isMust) {
        Log.d("jackzheng","playInerAdDeal" );

        if(!AdControlServer.getmIntance().chaping){
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

        Log.d("jackzheng","mPreShowBannerTime="+mPreShowInsertTime);
        Log.d("jackzheng","bntime="+AdControlServer.getmIntance().bntime );
        mPreShowInsertTime = System.currentTimeMillis();
        if(mInterAd == null){
            mInterAd = new VivoInterstialAd(this, INSERT_ID[0], new IAdListener() {
                @Override
                public void onAdShow() {
                    Log.d("jackzheng","mInterAd onAdShow" );
                    startGameOrPause(false);
                    mInterAd.load();
                }

                @Override
                public void onAdFailed(VivoAdError vivoAdError) {
                    Log.d("jackzheng","mInterAd onAdFailed "+vivoAdError.mErrorMsg+"  code="+vivoAdError.mErrorCode );
                    startGameOrPause(true);
                }

                @Override
                public void onAdReady() {
                    Log.d("jackzheng","mInterAd onAdReady" );
                    isReadyInsert = true;
                }

                @Override
                public void onAdClick() {
                    Log.d("jackzheng","mInterAd onAdClick" );
                }

                @Override
                public void onAdClosed() {
                    Log.d("jackzheng","mInterAd onAdClosed" );
                    mPreShowInsertTime = System.currentTimeMillis();
                    startGameOrPause(true);
                }
            });

        }
        if(!isReadyInsert){
            mInterAd.load();
        }else{
            mInterAd.showAd();
            isReadyInsert = false;
        }


    }

    @Override
    public void playSplashAdDeal() {
        Log.d("jackzheng","playSplashAdDeal" );
        startActivity(new Intent(MainActivity.this,SplashActivity.class));
    }


    private VivoBannerAd mVivoBanner;
    private int mBannerIndex = 0;
    private int mBannerViewHight = 0;
    private int mBannerViewWight = 0;
    private long mBannerPreShow = -1;
    private FrameLayout mAdContainer;
    private boolean isShowInsertAuto = true;

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
        if(mBannerPreShow != -1 && AdControlServer.getmIntance().bntime != -1){
            long time = System.currentTimeMillis();
            if(mBannerPreShow + AdControlServer.getmIntance().bntime*1000 > time ){
                return;
            }
        }
        mBannerIndex++;
        if(mAdContainer == null) {
            String[] strs = mBannerPoint.split(",");
            float weight = Float.parseFloat(strs[0]);
            float y = Float.parseFloat(strs[1]);
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            float density = dm.density;
            mBannerViewHight = (int) (57 * density);
            mBannerViewWight = (int) (weight);

            mAdContainer = new FrameLayout(this);//创建帧布局对象layout
            FrameLayout.LayoutParams frameLayout = new FrameLayout.LayoutParams(
                    mBannerViewWight,
                    mBannerViewHight
            );//设置帧布局的高宽属性
            frameLayout.bottomMargin = 0;
            frameLayout.gravity = Gravity.BOTTOM | Gravity.CENTER;
            frameLayout.bottomMargin = (int) y - mBannerViewHight;
            addContentView(mAdContainer, frameLayout);
        }
        mAdContainer.setVisibility(View.GONE);
        mBannerPreShow = System.currentTimeMillis();

        if(mVivoBanner == null){

            mVivoBanner = new VivoBannerAd(this, BANNER_ID[mBannerIndex / 2], new IAdListener() {
                @Override
                public void onAdShow() {
                    Log.d("jackzheng","startShowBannerDeal onAdShow" );
                }

                @Override
                public void onAdFailed(VivoAdError vivoAdError) {
                    Log.d("jackzheng","startShowBannerDeal onAdFailed" );
                    mAdContainer.setVisibility(View.GONE);
                }

                @Override
                public void onAdReady() {
                    Log.d("jackzheng","startShowBannerDeal onAdReady" );
                    mAdContainer.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdClick() {
                    Log.d("jackzheng","startShowBannerDeal onAdClick" );
                }

                @Override
                public void onAdClosed() {
                    Log.d("jackzheng","startShowBannerDeal onAdClosed" );
                    mAdContainer.setVisibility(View.GONE);
                    mBannerPreShow = System.currentTimeMillis();
                    mVivoBanner = null;
                }
            });

            mVivoBanner.setShowClose(true);

            mVivoBanner.setRefresh(30);

            View adView = mVivoBanner.getAdView();

            if (null != adView) {

                mAdContainer.addView(adView);
            }
        }

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

    /*
    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermission() {
        List<String> lackedPermission = new ArrayList<String>();
        if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) ==
                PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
// 权限都已经有了，那么直接调用SDK
        if (lackedPermission.size() == 0) {
// 开始调用广告组件请求广告
        } else {
// 请求所缺少的权限，在onRequestPermissionsResult 中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            requestPermissions(requestPermissions, 1024);
        }
    }

    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1024 && hasAllPermissionsGranted(grantResults)) {
// 开始调用广告组件请求广告
        } else {
// 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
            Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。",
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            finish();
        }
    }*/

}
