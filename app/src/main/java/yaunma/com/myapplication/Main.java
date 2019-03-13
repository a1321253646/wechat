package yaunma.com.myapplication;

import android.app.Activity;
import android.os.Bundle;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Administrator on 2017/8/25.
 */

public class Main implements IXposedHookLoadPackage {


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lp) {

        if (!lp.packageName.equals("com.tencent.mm")) {
            return;
        }

        try {
            XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class,
                    ActivityOncreate.getInstance());
//            XposedHelpers.findAndHookMethod("com.tencent.tinker.loader.app.TinkerApplication", lp.classLoader, "createDelegate", new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//
//                    Application result = (Application) param.getResult();
//                    LogUtils.d("result:"+result);
//                    OkSocket.initialize(result);
//                    OkSocket.setBackgroundSurvivalTime(-1);
//
//                }
//
//            });


        } catch (Exception e) {
                        XposedBridge.log(e);
        }


    }


}
