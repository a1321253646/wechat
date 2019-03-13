package yaunma.com.myapplication;

import android.app.Activity;
import android.os.Handler;

import java.net.Socket;
import java.util.concurrent.ExecutorService;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import jackzheng.study.com.wechat.SscControl;


/**
 * Created by junyan_mac on 17/10/8.
 */

public enum Hook {
    INSTANCE;
    Socket socket = null;
    public static ExecutorService mExecutorService;
    public static Boolean ishook=false;
    Handler mHandler ;
    public void doHook(Activity activity) {
        try {

            if (ishook) {
                return;
            }
            ishook = true;
            Tools.mActivity=activity;
            SscControl.getIntance().init();
            ClassLoader classLoader = activity.getClassLoader();

            Class cd = classLoader.loadClass(Constance.className_cg);//672
            XposedHelpers.findAndHookMethod(cd, Constance.methodName_cg_vP, new Av_P());//672



        } catch (Exception e) {

            XposedBridge.log(e);
        }

    }
}
