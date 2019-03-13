package yaunma.com.myapplication;

import android.app.Activity;
import android.content.Context;

import de.robv.android.xposed.XposedHelpers;

public class Tools {

  public static   Activity mActivity;


    //
    public static void sendTextToRoom(Context context, String sendText, String roomId) {

        ClassLoader classLoader = context.getClassLoader();
        Class j = null;
        try {
            j = classLoader.loadClass(Constance.className_j);
            Object req = XposedHelpers.newInstance(j, roomId, sendText, 1, 0, (Object) null);

            Class as = classLoader.loadClass(Constance.className_as);
            Object ys = XposedHelpers.callStaticMethod(as, Constance.method_name_as_CN);
            XposedHelpers.callMethod(ys, Constance.method_name_ad_e_a, req, 1);


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
