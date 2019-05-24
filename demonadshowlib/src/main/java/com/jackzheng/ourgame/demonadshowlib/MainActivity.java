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
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.oppo.mobad.api.InitParams;
import com.oppo.mobad.api.MobAdManager;
import com.oppo.mobad.api.ad.BannerAd;
import com.oppo.mobad.api.ad.InterstitialAd;
import com.oppo.mobad.api.ad.SplashAd;
import com.oppo.mobad.api.listener.IBannerAdListener;
import com.oppo.mobad.api.listener.IInterstitialAdListener;
import com.oppo.mobad.api.listener.ISplashAdListener;
import com.oppo.mobad.api.params.SplashAdParams;
import com.qsnmz.qslib.QsAd;
import com.qsnmz.qslib.QsAdListener;
import com.qsnmz.qslib.QsAdResult;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends MainActivityBase implements IBannerAdListener  {

    public final static String  APP_ID = "30085137";
    public final static String  SPLASH_ID = "60939";
    public final static String[]  BANNER_ID = {"60970","60972"};
    public final static String[]  INSERT_ID = {"60969","60973","60950","60945"};
    private BannerAd mBannerAd;

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
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            checkAndRequestPermissions(true);
        } else {
            fetchSplashAd();
        }
    }

    private void fetchSplashAd() {
           Intent intent = new Intent(this, SplashActivity.class);
           startActivity(intent);
    }

    private InterstitialAd mInterstitialAd;
    private int mInsertIndex = 0;
    private long mPreShowInsertTime = -1;
    @Override
    public void playInerAdDeal() {
        Log.d("jackzheng","playInerAdDeal" );

        if(!AdControlServer.getmIntance().chaping ){
            return;
        }
        Log.d("jackzheng","mPreShowBannerTime="+mPreShowBannerTime);
        Log.d("jackzheng","bntime="+AdControlServer.getmIntance().bntime );
        if(mPreShowInsertTime != -1 && AdControlServer.getmIntance().bntime != -1){
            long time = System.currentTimeMillis();
            Log.d("jackzheng","time="+time );
            if(time - mPreShowInsertTime < AdControlServer.getmIntance().bntime *1000){
                return;
            }
        }
        QsAd.showInsert(this,INSERT_ID[mInsertIndex%4],new QsAdListener(){

            @Override
            public void result(QsAdResult qsAdResult) {
                Log.d("jackzheng", "playInerAdDeal QsAdListener qsAdResult="+qsAdResult);
                mPreShowInsertTime = System.currentTimeMillis();
                if(qsAdResult == QsAdResult.OPEN){
                    startGameOrPause(false);
                }else{
                    startGameOrPause(true);
                }

            }
        });
  /*      mInterstitialAd = new InterstitialAd(this,INSERT_ID[mInsertIndex%4]);
        mInsertIndex++;
        mInterstitialAd.setAdListener( new IInterstitialAdListener(){

            @Override
            public void onAdShow() {
                Log.d("jackzheng", "playInerAdDeal onAdShow ");
                mPreShowInsertTime = System.currentTimeMillis();
            }

            @Override
            public void onAdFailed(String s) {
                Log.d("jackzheng", "playInerAdDeal onAdFailed s="+s);
                mInterstitialAd.destroyAd();
            }

            @Override
            public void onAdClick() {
                Log.d("jackzheng", "playInerAdDeal onAdClick ");
            }

            @Override
            public void onAdReady() {
                Log.d("jackzheng", "playInerAdDeal onAdReady ");
                mInterstitialAd.showAd();
                startGameOrPause(false);
            }

            @Override
            public void onAdClose() {
                Log.d("jackzheng", "playInerAdDeal onAdClose ");
                mPreShowInsertTime = System.currentTimeMillis();
                startGameOrPause(true);
                mInterstitialAd.destroyAd();
            }
        });
        mInterstitialAd.loadAd();*/

        
    }
    private int mBannerIndex = 0;
    FrameLayout mBannerView;
    private int mBannerViewHight;
    private int mBannerViewWight;

    private long mPreShowBannerTime = -1;
    @Override
    public void startShowBannerDeal() {
        Log.d("jackzheng","startShowBannerDeal" );
        if(!AdControlServer.getmIntance().banner ){
            return;
        }
        Log.d("jackzheng","mPreShowBannerTime="+mPreShowBannerTime);
        Log.d("jackzheng","bntime="+AdControlServer.getmIntance().bntime );
        if(mPreShowBannerTime != -1 && AdControlServer.getmIntance().bntime != -1){

            long time = System.currentTimeMillis();
            Log.d("jackzheng","time="+time );
            if(time - mPreShowBannerTime < AdControlServer.getmIntance().bntime *1000){
                return;
            }
        }
        if(mBannerView == null){
            mBannerView = new FrameLayout(this) ;

            String[]  strs = mBannerPoint.split(",");
            float weight = Float.parseFloat(strs[0]);
            float y = Float.parseFloat(strs[1]);
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            float density = dm.density;
            float screenHeight = dm.heightPixels;
            WindowManager mWindowManager = (WindowManager) MainActivity.this.getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams mWmParams = new WindowManager.LayoutParams();
            mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mBannerViewHight = (int)( 57 *density);
            mBannerViewWight = (int)weight;
            mWmParams.height =mBannerViewHight;
            mWmParams.width = mBannerViewWight;
            mWmParams.gravity = Gravity.BOTTOM|Gravity.CENTER;
            mWmParams.y = (int) y - mWmParams.height;
            mWindowManager.addView(mBannerView, mWmParams);
        }
        Log.d("jackzheng","BANNER_ID[mBannerIndex%2]="+BANNER_ID[mBannerIndex%2]);
        QsAd.showBanner(this,BANNER_ID[mBannerIndex%2],mBannerView,new QsAdListener(){

            @Override
            public void result(QsAdResult qsAdResult) {
                Log.d("jackzheng", "startShowBannerDeal QsAdListener qsAdResult="+qsAdResult);
                mPreShowBannerTime = System.currentTimeMillis();
            }
        });

     /*   mBannerAd = new BannerAd(this,BANNER_ID[mBannerIndex%2]);
        mBannerIndex++;
        if(mBannerView == null){
            mBannerView = new FrameLayout(this) ;

            String[]  strs = mBannerPoint.split(",");
            float weight = Float.parseFloat(strs[0]);
            float y = Float.parseFloat(strs[1]);
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            float density = dm.density;
            float screenHeight = dm.heightPixels;
            WindowManager mWindowManager = (WindowManager) MainActivity.this.getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams mWmParams = new WindowManager.LayoutParams();
            mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mBannerViewHight = (int)( 57 *density);
            mBannerViewWight = (int)weight;
            mWmParams.height =mBannerViewHight;
            mWmParams.width = mBannerViewWight;
            mWmParams.gravity = Gravity.BOTTOM|Gravity.CENTER;
            mWmParams.y = (int) y - mWmParams.height;
            mWindowManager.addView(mBannerView, mWmParams);
        }
        mBannerView.setVisibility(View.VISIBLE);
        mBannerAd.setAdListener(this);
        View adView = mBannerAd.getAdView();
        if (null != adView) {
            mBannerView.addView(adView);
        }
        mBannerAd.loadAd();*/
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

    @Override
    public void onAdReady() {
        Log.d("jackzheng", "mBannerView onAdReady");
    }

    @Override
    public void onAdClose() {
        Log.d("jackzheng", "mBannerView onAdClose");
        mPreShowBannerTime = System.currentTimeMillis();
        mBannerView.setVisibility(View.GONE);
        mBannerView.removeAllViews();
        mBannerAd.destroyAd();
    }

    @Override
    public void onAdShow() {
        Log.d("jackzheng", "mBannerView onAdShow");
        mPreShowBannerTime = System.currentTimeMillis();
    }

    @Override
    public void onAdFailed(String s) {
        Log.d("jackzheng", "mBannerView onAdFailed:errMsg=" + (null != s ? s : "原因不明"));
        mBannerView.setVisibility(View.GONE);
        mBannerView.removeAllViews();
        mBannerAd.destroyAd();
    }

    @Override
    public void onAdClick() {
        Log.d("jackzheng", "mBannerView onAdClick");
        mBannerView.setVisibility(View.GONE);
        mBannerView.removeAllViews();
    }
}
