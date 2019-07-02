package com.jackzheng.ourgame.demonadshowlib;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.vivo.ad.model.AdError;
import com.vivo.ad.splash.SplashADListener;
import com.vivo.mobilead.splash.SplashAdParams;
import com.vivo.mobilead.splash.VivoSplashAd;
import com.vivo.mobilead.util.VADLog;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends BaseActivity implements SplashADListener {

    private static final String TAG = "jackzheng";
    public boolean canJump = false;

    private String VIVO_SPLASH_ID = MainActivity.SPLASH_ID;

    @Override
    protected void doInit() {
        // 如果targetSDKVersion >= 23，就要申请好权限。如果您的App没有适配到Android6.0（即targetSDKVersion <
        // 23），那么只需要在这里直接调用fetchSplashAD接口。
        if (Build.VERSION.SDK_INT >= 23) {
            checkAndRequestPermission();
        } else {
            fetchSplashAD(this, VIVO_SPLASH_ID, this);
        }

    }

    /**
     * ----------非常重要----------
     * <p>
     * Android6.0以上的权限适配简单示例：
     * <p>
     * 如果targetSDKVersion >= 23，那么必须要申请到所需要的权限，再调用广告SDK，否则不会有广告返回。
     * <p>
     * Demo代码里是一个基本的权限申请示例，请开发者根据自己的场景合理地编写这部分代码来实现权限申请。
     * 注意：下面的`checkSelfPermission`和`requestPermissions`方法都是在Android6.0的SDK中增加的API，如果您的App还没有适配到Android6.0以上，则不需要调用这些方法，直接调用广点通SDK即可。
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermission() {
        List<String> lackedPermission = new ArrayList<String>();
        if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        // 权限都已经有了，那么直接调用SDK
        if (lackedPermission.size() == 0) {
            fetchSplashAD(this, VIVO_SPLASH_ID, this);
        } else {
            // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1024 && hasAllPermissionsGranted(grantResults)) {
            fetchSplashAD(this, VIVO_SPLASH_ID, this);
        } else {
            // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
            Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            finish();
        }
    }

    /**
     * 获取广告
     *
     * @param activity
     * @param posId
     * @param listener
     */
    private void fetchSplashAD(Activity activity, String posId, SplashADListener listener) {
        try {
            SplashAdParams.Builder builder = new SplashAdParams.Builder();
            // 拉取广告的超时时长：即开屏广告从请求到展示所花的最大时长（并不是指广告曝光时长）取值范围[3000, 5000]
            builder.setFetchTimeout(3000);
            // 广告下面半屏的应用标题+应用描述:应用标题和应用描述是必传字段，不传将抛出异常
            // 标题最长5个中文字符 描述最长8个中文字符
            builder.setTitle("卡牌世界的勇士");
            builder.setDesc("娱乐休闲首选游戏");
            new VivoSplashAd(activity, posId, listener, builder.build());
        } catch (Exception e) {
            Log.e("----------", "error", e);
            e.printStackTrace();
            toNextActivity();
        }
    }

    @Override
    public void onADDismissed() {
        Log.d(TAG, "onADDismissed");
        toNextActivity();
    }

    @Override
    public void onNoAD(AdError error) {
        Log.d(TAG, "onNoAD:" + error.getErrorMsg());
        toNextActivity();
    }

    @Override
    public void onADPresent() {
        Log.d(TAG, "onADPresent");
    }

    @Override
    public void onADClicked() {

        Log.d(TAG, "onADClicked");
    }

    /**
     * 设置一个变量来控制当前开屏页面是否可以跳转，当广告被点击，会跳转其他页面，此时开发者还不能打开自己的App主页。当从其他页面返回以后， 才可以跳转到开发者自己的App主页；
     */
    private void next() {
        if (canJump) {
            //开屏与游戏页面共存 可以用下面的方法
            // vivoSplashAd.removeSplashView();
            toNextActivity();
        } else {
            canJump = true;
        }
    }

    private void toNextActivity() {
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        canJump = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJump) {
            next();
        }
        canJump = true;
    }

    /**
     * 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
