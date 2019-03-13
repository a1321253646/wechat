package yaunma.com.myapplication;

import android.app.Activity;

import de.robv.android.xposed.XC_MethodHook;


/**
 * Created by Administrator on 2017/8/25.
 */

public class ActivityOncreate extends XC_MethodHook {


    private int i;

    //在静态内部类中初始化实例对象
    private static class SingletonHolder {
        private static final ActivityOncreate INSTANCE = new ActivityOncreate();
    }


    //对外提供获取实例的静态方法
    public static final ActivityOncreate getInstance() {
        return SingletonHolder.INSTANCE;
    }


    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        String cn = param.thisObject.getClass().getSimpleName();
        Activity activity = (Activity) param.thisObject;
        //        Context context = activity.getApplicationContext();
//        LogUtils.d("cn:"+cn);
        if (cn.equals("LauncherUI")) {
        Hook.INSTANCE.doHook(activity);

//            LogUtils.d("versionCode:"+versionCode);

        }
    }


}
