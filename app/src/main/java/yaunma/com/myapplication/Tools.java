package yaunma.com.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

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


    public static void sendImgToRoom(Context context, ArrayList imgList, boolean defaultTrue, int defaultZero, int defaultZero2, String id ){

        ClassLoader classLoader = context.getClassLoader();
        Class as = null;
        try {
            as = classLoader.loadClass("com.tencent.mm.ui.chatting.SendImgProxyUI");
            Intent intent = new Intent(Tools.mActivity, as);
            ArrayList list1 = new ArrayList();
            intent.putStringArrayListExtra("key_select_video_list",list1);
            intent.putExtra("CropImage_limit_Img_Size",new Integer(26214400));
            intent.putExtra("GalleryUI_FromUser","zsbin001");
            intent.putExtra("GalleryUI_ToUser",id);
            intent.putExtra("KSelectImgUseTime",4626L);
            intent.putStringArrayListExtra("CropImage_OutputPath_List",imgList);

            intent.putExtra("CropImage_Compress_Img",false);
           // intent.puex("key_select_video_list",);
          //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Tools.mActivity.startActivity(intent);

            Class j1 = classLoader.loadClass("com.tencent.mm.as.l$4");
            Object req1 = XposedHelpers.newInstance(j1);

            Class j2 = classLoader.loadClass("ssc:com.tencent.mm.i.c");
            Object req2 = XposedHelpers.newInstance(j2);

            Class j3 = classLoader.loadClass("ssc:com.tencent.mm.i.d");
            Object req3 = XposedHelpers.newInstance(j3);



        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
