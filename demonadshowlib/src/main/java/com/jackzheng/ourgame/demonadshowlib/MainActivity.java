package com.jackzheng.ourgame.demonadshowlib;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTBannerAd;
import com.bytedance.sdk.openadsdk.TTInteractionAd;

import static com.bytedance.sdk.openadsdk.AdSlot.Builder;
import org.json.JSONException;

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
         Intent intent = new Intent(MainActivity.this, SplashActivity.class);
         startActivity(intent);
    }

    private TTAdNative mInsertTTAdNative;
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


        loadInteractionAd();
    }
    private void loadInteractionAd() {
        //step4:创建插屏广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new Builder()
                .setCodeId(INSERT_ID)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(900, 900) //根据广告平台选择的尺寸，传入同比例尺寸
                .build();
        //step5:请求广告，调用插屏广告异步请求接口
        mInsertTTAdNative.loadInteractionAd(adSlot, new TTAdNative.InteractionAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.e("jackzheng","insert广告出错 code="+code+" message="+message);
            }

            @Override
            public void onInteractionAdLoad(TTInteractionAd ttInteractionAd) {
                Log.e("jackzheng","insert onInteractionAdLoad type:  " + ttInteractionAd.getInteractionType());
                ttInteractionAd.setAdInteractionListener(new TTInteractionAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked() {
                        Log.d(TAG, "insert 被点击");
                    }

                    @Override
                    public void onAdShow() {
                        Log.d(TAG, "insert  被展示");
                        mInsertPreShow = System.currentTimeMillis();
                        isShowInserAd = true;
                        startGameOrPause(false);
                    }

                    @Override
                    public void onAdDismiss() {
                        Log.d(TAG, "insert  插屏广告消失");
                        mInsertPreShow = System.currentTimeMillis();
                        isShowInserAd = false;
                        startGameOrPause(true);
                    }
                });
                //弹出插屏广告
                ttInteractionAd.showInteractionAd(MainActivity.this);
            }
        });
    }


    private FrameLayout mBannerContainer;
    private boolean isAutoInsert = true;
    private int mBannerViewHight = 0;
    private int mBannerViewWight = 0;
    private TTAdNative mBannerTTAdNative;
    private long mBannerPreShow = -1;
    private boolean isShowBannerIng = false;
    @Override
    public void startShowBannerDeal() {

        if(isAutoInsert){
            mHandler.sendEmptyMessageDelayed(4,1000);
            isAutoInsert = false;
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


        if(mBannerContainer == null){
            mBannerContainer = new FrameLayout(this);

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
            mBannerViewHight = (int)( 54 *density);
            mBannerViewWight = (int)weight;
            // mBannerViewHight = 260;
            // mBannerViewWight = 600;
            mWmParams.height =mBannerViewHight;
            mWmParams.width = mBannerViewWight;
            mWmParams.gravity = Gravity.BOTTOM|Gravity.CENTER;
            mWmParams.y = (int) y - mWmParams.height;
            mWindowManager.addView(mBannerContainer, mWmParams);
            mBannerContainer.setVisibility(View.GONE);
        }
        loadBannerAd();

    }

    private void loadBannerAd() {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(BANNER_ID) //广告位id
                .setSupportDeepLink(true)
                .setImageAcceptedSize(mBannerViewWight, mBannerViewHight)
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        mBannerTTAdNative.loadBannerAd(adSlot, new TTAdNative.BannerAdListener() {

            @Override
            public void onError(int code, String message) {
                Log.e("jackzheng","banner广告出错code="+code+" message="+message);
                mBannerContainer.removeAllViews();
                mBannerContainer.setVisibility(View.GONE);
                isShowBannerIng = false;
            }

            @Override
            public void onBannerAdLoad(final TTBannerAd ad) {
                Log.e("jackzheng","banner onBannerAdLoad start");
                if (ad == null) {
                    Log.e("jackzheng","banner onBannerAdLoad ad == null");
                    return;
                }
                View bannerView = ad.getBannerView();
                if (bannerView == null) {
                    Log.e("jackzheng","banner onBannerAdLoad bannerView == null");
                    return;
                }
                Log.e("jackzheng","banner onBannerAdLoad");
                mBannerContainer.setVisibility(View.VISIBLE);
                isShowBannerIng = true;
                mBannerPreShow = System.currentTimeMillis();
                //设置轮播的时间间隔  间隔在30s到120秒之间的值，不设置默认不轮播
                ad.setSlideIntervalTime(30 * 1000);
                mBannerContainer.removeAllViews();
                mBannerContainer.addView(bannerView);
                //设置广告互动监听回调
                ad.setBannerInteractionListener(new TTBannerAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        Log.e("jackzheng","banner广告被点击");
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                       // mBannerContainer.setVisibility(View.VISIBLE);

                        Log.e("jackzheng","banner广告展示");
                    }
                });

                //在banner中显示网盟提供的dislike icon，有助于广告投放精准度提升
                ad.setShowDislikeIcon(new TTAdDislike.DislikeInteractionCallback() {
                    @Override
                    public void onSelected(int position, String value) {
                        Log.e("jackzheng","banner广告被关闭");
                        //用户选择不喜欢原因后，移除广告展示
                        mBannerContainer.removeAllViews();
                        mBannerContainer.setVisibility(View.GONE);
                        mBannerPreShow = System.currentTimeMillis();
                        isShowBannerIng = false;
                    }

                    @Override
                    public void onCancel() {
                        Log.e("jackzheng","banner广告被取消");
                    }
                });

            }
        });
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ArrayMap<String,String> tmp = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            tmp = new ArrayMap<String,String>();
            tmp.put("channel  ","haoyoubao");
            try {
                AdControlServer.getmIntance().getInitDate("http://milihuyu.com/game/ads.php",tmp,this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mBannerTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        mInsertTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
    }
}
