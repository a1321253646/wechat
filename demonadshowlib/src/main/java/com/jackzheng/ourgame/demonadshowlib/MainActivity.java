package com.jackzheng.ourgame.demonadshowlib;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import cn.gundam.sdk.shell.even.SDKEventKey;
import cn.gundam.sdk.shell.even.SDKEventReceiver;
import cn.gundam.sdk.shell.even.Subscribe;
import cn.gundam.sdk.shell.exception.AliLackActivityException;
import cn.gundam.sdk.shell.exception.AliNotInitException;
import cn.gundam.sdk.shell.open.ParamInfo;
import cn.gundam.sdk.shell.open.UCOrientation;
import cn.gundam.sdk.shell.param.SDKParamKey;
import cn.gundam.sdk.shell.param.SDKParams;
import cn.sirius.nga.NGASDK;
import cn.sirius.nga.NGASDKFactory;
import cn.sirius.nga.properties.NGABannerController;
import cn.sirius.nga.properties.NGABannerListener;
import cn.sirius.nga.properties.NGABannerProperties;
import cn.sirius.nga.properties.NGAInsertController;
import cn.sirius.nga.properties.NGAInsertListener;
import cn.sirius.nga.properties.NGAInsertProperties;
import cn.sirius.nga.properties.NGAWelcomeListener;
import cn.sirius.nga.properties.NGAWelcomeProperties;
import cn.sirius.nga.properties.NGAdController;
import cn.uc.gamesdk.UCGameSdk;
import cn.uc.paysdk.face.commons.Response;

public class MainActivity extends MainActivityBase {

    private static String APP_ID = "1000007329";
    private static String BANNER_ID = "1555316252874";
    private static String SAPLASH_ID = "1554802358263";
    private static String INSERT_ID = "1554893593851";


    @Override
    public boolean isAdInit(String str) {
        return isAdInit;
    }

    @Override
    public boolean isInserAdReady(String str) {
        return true;
    }


    NGAWelcomeListener mWelcomeAdListener;
    ViewGroup mSplashView;
    WindowManager mWindowManager;
    @Override
    public void playSplashAdDeal() {
        mWelcomeAdListener = new NGAWelcomeListener() {
            @Override
            public void onClickAd() {
                Log.e(TAG, "onClickAd");
            }
            @Override
            public void onErrorAd(int code, String message) {
                Log.e(TAG, "onErrorAd errorCode:" + code + ", message:" + message);
                mWindowManager.removeView(mSplashView);
            }
            @Override
            public void onShowAd() {
              //  splashHolder.setVisibility(View.INVISIBLE); // 广告展示后一定要把预设的开屏图片隐藏起来
                Log.e(TAG, "onShowAd");
                startGameOrPause(false);
            }
            @Override
            public void onCloseAd() {
                //无论成功展示成功或失败都回调用该接口，所以开屏结束后的操作在该回调中实现
                Log.e(TAG, "onCloseAd");
                mWindowManager.removeView(mSplashView);
                startGameOrPause(true);
            }
            @Override
            public <T extends NGAdController> void onReadyAd(T controller) {
                // 开屏广告是闪屏过程自动显示不需要NGAdController对象，所以返回controller为null；
                Log.e(TAG, "onReadyAd");
            }
            @Override
            public void onRequestAd() {
                Log.e(TAG, "onRequestAd");
            }
            @Override
            public void onTimeTickAd(long millisUntilFinished) {
                //skipView.setText(String.format(SKIP_TEXT, Math.round(millisUntilFinished / 1000f)));
            }
        };


        // 支持开发者自定义的跳过按钮。SDK要求skipContainer一定在传入后要处于VISIBLE状态，且其宽高都不得小于3x3dp。
        // 如果需要使用SDK默认的跳过按钮，可以选择上面两个构造方法。
        //properties.setSkipView(skipView);
         mSplashView = new FrameLayout(this) ;
        Log.d(TAG,"playSplashAdDeal===========================");
        String[]  strs = mBannerPoint.split(",");
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        mWindowManager = (WindowManager) MainActivity.this.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams mWmParams = new WindowManager.LayoutParams();
        mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        mWmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mWmParams.gravity = Gravity.BOTTOM|Gravity.CENTER;
        mWindowManager.addView(mSplashView, mWmParams);

        NGAWelcomeProperties properties = new NGAWelcomeProperties(this, APP_ID, SAPLASH_ID, mSplashView);
        properties.setListener(mWelcomeAdListener);
        NGASDK ngasdk = NGASDKFactory.getNGASDK();
        ngasdk.loadAd(properties);
    }
    private NGAInsertProperties mPropertiesInsert;
    private NGAInsertController mControllerInsert;
    NGAInsertListener mInsertAdListener;
    private long mInsertPreShow = -1;
    private boolean isShowInserAd = false;
    @Override
    public void playInerAdDeal(boolean isMust) {
        if(isShowInserAd){
            return;
        }
        if(!AdControlServer.getmIntance().chaping){
            return;
        }
        if(!isMust){
            if(mInsertPreShow != -1 && AdControlServer.getmIntance().cntime != -1){
                long time = System.currentTimeMillis();
                if(mInsertPreShow + AdControlServer.getmIntance().cntime*1000  > time ){
                    return;
                }
            }
        }


        if(mInsertAdListener == null){
            mInsertAdListener = new NGAInsertListener() {
                @Override
                public void onShowAd() {
                    Log.e(TAG,"onShowAd");
                    isShowInserAd = true;
                    mInsertPreShow = System.currentTimeMillis();
                    startGameOrPause(false);
                }
                @Override
                public void onRequestAd() {
                    Log.e(TAG, "onRequestAd");
                }
                @Override
                public <T extends NGAdController> void onReadyAd(T controller) {

                    mControllerInsert = (NGAInsertController) controller;
                    mControllerInsert.showAd();
                    Log.e(TAG, "onReadyAd");
                }
                @Override
                public void onCloseAd() {
                    mController = null;
                    startGameOrPause(true);
                    isShowInserAd = false;
                    mInsertPreShow = System.currentTimeMillis();
                    Log.e(TAG, "onCloseAd");
                }
                @Override
                public void onClickAd() {
                    Log.e(TAG, "onClickAd");
                }
                @Override
                public void onErrorAd(int code, String message) {
                    Log.e(TAG, "onErrorAd errorCode:" + code + ", message:" + message);
                }
            };

        }
        mPropertiesInsert = new NGAInsertProperties(this, APP_ID,INSERT_ID, null);
        mPropertiesInsert.setListener(mInsertAdListener);
        NGASDK ngasdk = NGASDKFactory.getNGASDK();
        ngasdk.loadAd(mPropertiesInsert);
    }


    private NGABannerController mController;
    private NGABannerProperties mProperties;
    NGABannerListener mAdListener;
    ViewGroup mBannerView;
    private long mBannerPreShow = -1;
    private boolean isShowBannerIng = false;
    private int mBannerViewHight = 0;
    private int mBannerViewWight = 0;
    private boolean isShowInsertAuto = true;
    public void startShowBannerDeal(){
        if(isShowInsertAuto){
            isShowInsertAuto = false;
            mHandler.sendEmptyMessageDelayed(4,5000);
        }
        if(isShowBannerIng){
            return;
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
        Log.e(TAG, "startShowBannerDeal");
        if(mWindowManager == null){
            mWindowManager = (WindowManager) MainActivity.this.getSystemService(Context.WINDOW_SERVICE);
        }
        if (mBannerView != null && mBannerView.getParent() != null) {
            mWindowManager.removeView(mBannerView);
        }
        mBannerView = new RelativeLayout(this) ;
        String[]  strs = mBannerPoint.split(",");
        float weight = Float.parseFloat(strs[0]);
        float y = Float.parseFloat(strs[1]);
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        WindowManager.LayoutParams mWmParams = new WindowManager.LayoutParams();
        mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mBannerViewHight = (int)( 54 *density);
        mBannerViewWight = (int)weight;
        mWmParams.height =mBannerViewHight;
        mWmParams.width = mBannerViewWight;
        mWmParams.gravity = Gravity.BOTTOM| Gravity.CENTER;
        mWmParams.y = (int) y - mWmParams.height;
        mWindowManager.addView(mBannerView, mWmParams);

        if(mAdListener == null){
            mAdListener = new NGABannerListener() {
                @Override
                public void onRequestAd() {
                    Log.e(TAG,  "onRequestAd");
                }
                @Override
                public <T extends NGAdController> void onReadyAd(T controller) {
                    mController = (NGABannerController) controller;
                    mController.showAd();
                    mBannerView.setVisibility(View.VISIBLE);
                    isShowBannerIng = true;
                    mBannerPreShow = System.currentTimeMillis();
                    Log.e(TAG, "onReadyAd");
                }
                @Override
                public void onShowAd() {
                    Log.e(TAG,  "onShowAd");
                }
                @Override
                public void onCloseAd() {
                    //广告关闭之后mController置null，鼓励加载广告重新调用loadAd，提高广告填充率
                    mController = null;
                    Log.e(TAG, "onCloseAd");
                    mBannerView.setVisibility(View.GONE);
                    isShowBannerIng = false;
                    mBannerPreShow = System.currentTimeMillis();
                }
                @Override
                public void onErrorAd(int code, String message) {
                    Log.e(TAG, "onErrorAd errorCode:" + code + ", message:" + message);
                    mBannerView.setVisibility(View.GONE);
                }
                @Override
                public void onClickAd() {
                    Log.e(TAG,  "onClickAd");
                }
            };


        }
        mProperties = new NGABannerProperties(this,APP_ID, BANNER_ID, mBannerView);
        mProperties.setListener(mAdListener);
        mBannerView.setVisibility(View.GONE);
        NGASDK ngasdk = NGASDKFactory.getNGASDK();
        ngasdk.loadAd(mProperties);
    }

    private boolean isAdInit = false;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initSdk(this, new NGASDK.InitCallback() {
            @Override
            public void success() {
                isAdInit = true;
      //          showAd(WelcomeActivity.this);
            }
            @Override
            public void fail(Throwable throwable) {
                isAdInit = false;
             //   throwable.printStackTrace();
            }
        });
        ArrayMap<String,String> tmp = null;
        //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            tmp = new ArrayMap<String,String>();
            tmp.put("channel  ","jiuyou");
            try {
                AdControlServer.getmIntance().getInitDate("http://milihuyu.com/game/ads.php",tmp,this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
     //   }
        UCGameSdk.defaultSdk().registerSDKEventReceiver(receiver);
        ucSdkInit();
    }
    /**
     *回调事件
     */
    private SDKEventReceiver receiver = new SDKEventReceiver() {

        @Subscribe(event = SDKEventKey.ON_EXIT_SUCC)
        private void onExitSucc() {
        //    Toast.makeText(MainActivity.this, ">> 确认退出游戏", Toast.LENGTH_LONG).show();
            UnityPlayer.UnitySendMessage("Main Camera", "exitGame", "退出");
        }

    };

    private void exitApp() {
        finish();
        //退出程序
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    private void ucSdkInit() {
        ParamInfo gameParamInfo = new ParamInfo();

        gameParamInfo.setGameId(1112585);
        gameParamInfo.setOrientation(UCOrientation.LANDSCAPE);

        SDKParams sdkParams = new SDKParams();
        sdkParams.put(SDKParamKey.GAME_PARAMS, gameParamInfo);

        try {
            //初始化SDK
            UCGameSdk.defaultSdk().initSdk(this, sdkParams);
        } catch (AliLackActivityException e) {
            e.printStackTrace();
        }
    }
    private static void initSdk(Activity activity, final NGASDK.InitCallback initCallback) {
        // 重新初始化sdk
        NGASDK ngasdk = NGASDKFactory.getNGASDK();
        Map<String, Object> args = new HashMap<>();
        args.put(NGASDK.APP_ID, APP_ID);
        //打Release包的时候，需要把DebugMode设置为false
        args.put(NGASDK.DEBUG_MODE, "false");
        ngasdk.init(activity, args, initCallback);
    }
    public String exitGame(String s){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    UCGameSdk.defaultSdk().exit(MainActivity.this,null);
                } catch (AliLackActivityException e) {
                    e.printStackTrace();
                } catch (AliNotInitException e) {
                    e.printStackTrace();
                }
            }
        });
        return "";
    }
}
