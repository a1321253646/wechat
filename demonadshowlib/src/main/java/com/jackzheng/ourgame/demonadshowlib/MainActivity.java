package com.jackzheng.ourgame.demonadshowlib;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;
import com.zqhy.sdk.callback.ExitCallBack;
import com.zqhy.sdk.callback.GameDataReFreshCallBack;
import com.zqhy.sdk.callback.InitCallBack;
import com.zqhy.sdk.callback.LocalExitCallBack;
import com.zqhy.sdk.callback.LoginCallBack;
import com.zqhy.sdk.callback.PayCallBack;
import com.zqhy.sdk.callback.ReLoginCallBack;
import com.zqhy.sdk.model.GameDataParams;
import com.zqhy.sdk.model.PayParams;
import com.zqhy.sdk.platform.BTGameTWSDKApi;
import com.zqhy.sdk.ui.FloatWindowManager;

public class MainActivity extends UnityPlayerActivity {

    private static String TAG = "MainActivity";
    public void onBuySuccess(String skus){

        UnityPlayer.UnitySendMessage("Main Camera", "onBuySuccess", skus);
    }

    public void onBuyFault(String skus){

        UnityPlayer.UnitySendMessage("Main Camera", "onBuyFault", skus);
    }

    public void onLoginSuccessBack(){

        UnityPlayer.UnitySendMessage("Main Camera", "onLoginSuccess", "");
    }
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        BTGameTWSDKApi.getInstance().init(this,3154,"4db29607556f3637ae8c00e6623ffe1a",new InitCallBack(){


            @Override
            public void onInitSuccess() {
                Log.d(TAG,"onInitSuccess");
                Toast.makeText(MainActivity.this, "初始化成功", Toast.LENGTH_SHORT).show();
                login();
                BTGameTWSDKApi.getInstance().registerReLoginCallBack(reLoginCallBack);

            }

            @Override
            public void onInitFailure(String s) {
                Log.d(TAG,"onInitFailure s ="+s);
                Toast.makeText(MainActivity.this, "初始化失败\n 失败原因：" + s, Toast.LENGTH_SHORT).show();
            }
        });
    }
    ReLoginCallBack reLoginCallBack = new ReLoginCallBack() {
        @Override
        public void onReLogin() {//切换账号 cp 需要在这里再次调用登录接口
            Log.e(TAG, "RELOGIN");
            login();
        }
    };
    private String strUsername ;
    private String strToken ;
    public void login() {
        BTGameTWSDKApi.getInstance().login(this, new LoginCallBack() {
            @Override
            public void onLoginSuccess(String username, String token) {
                Log.d(TAG," onLoginSuccess");
                strUsername = username;
                strToken = token;
                onLoginSuccessBack();
            }
            @Override
            public void onLoginFailure(String message) {
                Log.d(TAG," onLoginFailure message ="+message);
            }
            @Override
            public void onLoginCancel() {
                Log.d(TAG," onLoginFailure message onLoginCancel");
            }
        });
    }

    public String pay(String userID,String userName,final String productSku,String productName,String money) {
        PayParams payParams = new PayParams();
        payParams.extendsinfo = productSku+"_"+ System.currentTimeMillis();
        final String  info = payParams.extendsinfo;
        payParams.username = strUsername;
        payParams.token = strToken;
        payParams.serverid = "1";
        payParams.amount = Float.parseFloat(money);
        payParams.role_id = userID;
        payParams.role_name = userName;
        payParams.product_name = productName;
        payParams.servername = "逃出深渊BtGame服";
        Log.d(TAG," pay payParams.username="+payParams.username);
        Log.d(TAG," pay  payParams.token="+ payParams.token);
        Log.d(TAG," pay payParams.serverid="+payParams.serverid);
        Log.d(TAG," pay payParams.amount="+payParams.amount);
        Log.d(TAG," pay payParams.role_id="+payParams.role_id);
        Log.d(TAG," pay payParams.role_name="+payParams.role_name);
        Log.d(TAG," pay payParams.product_name="+payParams.product_name);
        Log.d(TAG," pay payParams.servername="+payParams.servername);


        BTGameTWSDKApi.getInstance().pay(this, payParams, new PayCallBack() {
            @Override
            public void onPaySuccess(String message) {
                Log.d(TAG," onPaySuccess message="+message);
                onBuySuccess(info);
            }
            @Override
            public void onPayFailure(String message) {
                Log.d(TAG," onPayFailure message ="+message);
                onBuyFault(info);
            }
            @Override
            public void onPayCancel() {
                Log.d(TAG," onPayCancel message onLoginCancel");
                onBuyFault(info);
            }
        });
        return "";
    }
    public String reFreshGameData(String userID,String userName,String eventType,String level) {
        GameDataParams gdp = new GameDataParams();
        gdp.setUsername(strUsername);
        gdp.setToken(strToken);
        gdp.setServerid(1);
        gdp.setServername("逃出深渊BtGame服");
        gdp.setRole_id(userID);
        gdp.setRole_name(userName);
        gdp.setOp(Integer.parseInt(eventType));
        gdp.setGame_level(Integer.parseInt(level));
        Log.d(TAG," reFreshGameData getUsername="+gdp.getUsername());
        Log.d(TAG," reFreshGameData getToken="+gdp.getToken());
        Log.d(TAG," reFreshGameData getServerid="+gdp.getServerid());
        Log.d(TAG," reFreshGameData getServername="+gdp.getServername());
        Log.d(TAG," reFreshGameData getRole_id="+gdp.getRole_id());
        Log.d(TAG," reFreshGameData getRole_name="+gdp.getRole_name());
        Log.d(TAG," reFreshGameData getOp="+gdp.getOp());
        Log.d(TAG," reFreshGameData getGame_level="+gdp.getGame_level());


        BTGameTWSDKApi.getInstance().reFreshGameData(this, gdp, new GameDataReFreshCallBack() {
            @Override
            public void reFreshOk() {
                Log.d(TAG," reFreshGameData reFreshOk");
            }
            @Override
            public void reFreshFailure(String message) {
                Log.d(TAG," reFreshGameData reFreshFailure message="+message);
            }
        });
        return "";
    }

    public void exit() {
        int orientation = 0; // 横屏 = 0 竖屏 = 1
        BTGameTWSDKApi.getInstance().exit(this, orientation, new ExitCallBack() {
            @Override
            public void onExit() {
                Log.d(TAG," exit onExit");
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            @Override
            public void onContinueGame() {
                Log.d(TAG," exit onContinueGame");
            }
            @Override
            public void onCancel() {
                Log.d(TAG," exit onCancel");
            }
        }, new LocalExitCallBack() {
            @Override
            public void onLocalExit() {
                Log.d(TAG," exit onLocalExit");
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FloatWindowManager.getInstance(this.getApplicationContext()).showFloat();
    }
    @Override
    protected void onStop() {
        super.onStop();
        FloatWindowManager.getInstance(this.getApplicationContext()).hideFloat();
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        FloatWindowManager.getInstance(this.getApplicationContext()).destroyFloat();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public String showTaptap(String str){
        return "";
    }

}
