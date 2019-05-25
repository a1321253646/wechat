package com.jackzheng.ourgame.demonadshowlib;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.oppo.mobad.api.InitParams;
import com.oppo.mobad.api.MobAdManager;
import com.oppo.mobad.api.ad.BannerAd;
import com.oppo.mobad.api.ad.InterstitialAd;
import com.oppo.mobad.api.ad.NativeTempletAd;
import com.oppo.mobad.api.ad.SplashAd;
import com.oppo.mobad.api.listener.IBannerAdListener;
import com.oppo.mobad.api.listener.IInterstitialAdListener;
import com.oppo.mobad.api.listener.INativeTempletAdListener;
import com.oppo.mobad.api.listener.ISplashAdListener;
import com.oppo.mobad.api.listener.b;
import com.oppo.mobad.api.params.INativeTempletAdView;
import com.oppo.mobad.api.params.NativeAdError;
import com.oppo.mobad.api.params.NativeAdSize;
import com.oppo.mobad.api.params.SplashAdParams;
import com.qsnmz.qslib.QsAd;
import com.qsnmz.qslib.QsAdListener;
import com.qsnmz.qslib.QsAdResult;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends MainActivityBase  {

    public final static String  APP_ID = "30085137";
    public final static String  SPLASH_ID = "60939";
    public final static String[]  BANNER_ID = {"60970","60972"};
    public final static String[]  INSERT_ID = {"60969","60973","60950","60945"};
    private BannerAd mBannerAd;


    public  boolean isAdInit = false;
    @Override
    public boolean isAdInit(String str) {
        return isAdInit;
    }

    @Override
    public boolean isInserAdReady(String str) {
        return true;
    }

    @Override
    public void playSplashAdDeal() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            checkAndRequestPermissions(true);
        } else {
            fetchSplashAd();
        }
    }

    private void fetchSplashAd() {
           Intent intent = new Intent(this, LandSplashActivity.class);
           startActivity(intent);
    }

    private InterstitialAd mInterstitialAd;
    private int mInsertIndex = 0;
    private long mPreShowInsertTime = -1;
    @Override
    public void playInerAdDeal() {
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
        QsAd.showInsert(this,INSERT_ID[mInsertIndex%4],new QsAdListener(){

            @Override
            public void result(QsAdResult qsAdResult) {
                Log.d("jackzheng", "playInerAdDeal QsAdListener qsAdResult="+qsAdResult);
            /*    mPreShowInsertTime = System.currentTimeMillis();
                if(qsAdResult == QsAdResult.OPEN){
                    startGameOrPause(false);
                }else if(qsAdResult == QsAdResult.CLOSE){
                    startGameOrPause(true);
                }*/

            }
        });
        
    }
    private int mBannerIndex = 0;
    private int mBannerViewHight;
    private int mBannerViewWight;
    private long mBannerPreShow = -1;
    private FrameLayout mAdContainer;
    private boolean isShowInsertAuto = true;
    @Override
    public void startShowBannerDeal() {
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

/*
            mAdContainer = new FrameLayout(this);


            WindowManager mWindowManager = (WindowManager) MainActivity.this.getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams mWmParams = new WindowManager.LayoutParams();
            mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mWmParams.height =mBannerViewHight;
            mWmParams.width = mBannerViewWight;
            mWmParams.gravity = Gravity.BOTTOM|Gravity.CENTER;
            mWmParams.y = (int) y - mWmParams.height;
            mWindowManager.addView(mAdContainer, mWmParams);*/
        }

        Log.d("jackzheng","BANNER_ID[mBannerIndex%2]="+BANNER_ID[mBannerIndex%2]);
        mBannerPreShow = System.currentTimeMillis();
        QsAd.showBanner(this,BANNER_ID[mBannerIndex%2],mAdContainer,new QsAdListener(){
            @Override
            public void result(QsAdResult qsAdResult) {
                Log.d("jackzheng", "startShowBannerDeal QsAdListener qsAdResult="+qsAdResult);
               /* if(qsAdResult == QsAdResult.OPEN){
                    mAdContainer.setVisibility(View.VISIBLE);
                }else if(qsAdResult == QsAdResult.CLOSE){
                    mAdContainer.setVisibility(View.GONE);
                }*/

            }
        });

    }


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Map<String,String> allParams = new HashMap<String,String>();
        allParams.put("channel","oppo");
        try {
            AdControlServer.getmIntance().getInitDate("http://milihuyu.com/game/ads.php",allParams,this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //初始化 appid
        InitParams initParams = new InitParams.Builder()
                .setDebug(true)
//true打开SDK日志，当应用发布Release版本时，必须注释掉这行代码的调用，或者设为false
                .build();
        MobAdManager.getInstance().init(this,MainActivity.APP_ID, initParams, new b() {
            @Override
            public void onSuccess() {
                android.util.Log.d("jackzhng","bAdManager.getInstance().init onSuccess = ");
                isAdInit = true;
            }

            @Override
            public void onFailed(String s) {
                android.util.Log.d("jackzhng","bAdManager.getInstance().init onFailed = ");
                isAdInit = false;
            }
        });
        checkAndRequestPermissions(false);
    }
    ArrayList<String> mNeedRequestPMSList = new ArrayList<>();
    private void checkAndRequestPermissions(boolean isSplash) {


        if (PackageManager.PERMISSION_GRANTED !=
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            mNeedRequestPMSList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (PackageManager.PERMISSION_GRANTED !=
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            mNeedRequestPMSList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (PackageManager.PERMISSION_GRANTED !=
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)) {
            mNeedRequestPMSList.add(Manifest.permission.WRITE_CALENDAR);
        }
        if (PackageManager.PERMISSION_GRANTED !=
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            mNeedRequestPMSList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (0 == mNeedRequestPMSList.size()) {
            if(isSplash){
                fetchSplashAd();
            }

        } else {
            String[] temp = new String[mNeedRequestPMSList.size()];
            mNeedRequestPMSList.toArray(temp);
            ActivityCompat.requestPermissions(this, temp, REQUEST_PERMISSIONS_CODE);
        }
    }
    

    private static  final  long FETCH_TIME_OUT = 5000;
    private static  final  String APP_TITLE = "oppo游戏开屏广告";
    private static  final  String APP_DESC = "测试测试";
    private SplashAd mSplashAd = null;

    public static final int REQUEST_PERMISSIONS_CODE = 10001;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case REQUEST_PERMISSIONS_CODE:
                if (hasNecessaryPMSGranted()) {

                   // fetchSplashAd();
                } else {

                    Toast.makeText(this, "应用缺少SDK必须的 EAD_PHONE_STATE WRITE_EXTERNAL_STORAGE 两个权限 请点击\"应用权限\"打开所需要的权限",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                    finish();
                }
                break;
            default:
                break;
        }
    }
    private boolean hasNecessaryPMSGranted() {
        if (PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            if (PackageManager.PERMISSION_GRANTED ==
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String exitGame(String str) {
        MobAdManager.getInstance().exit(this);
        return super.exitGame(str);
    }

}
