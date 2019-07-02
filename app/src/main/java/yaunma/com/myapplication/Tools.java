package yaunma.com.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import de.robv.android.xposed.XposedBridge;
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

    public static Object mSendImgToRoom1 = null;
    public static Object mSendImgToRoom2 = null;
    public static Object mSendImgToRoom3 = null;

    public static Object mSendUi = null;
    public static Intent mSendUiInten = null;


    public static void sendImgToRoom(Context context, ArrayList imgList, boolean defaultTrue, int defaultZero, int defaultZero2, String id ){

        ClassLoader classLoader = context.getClassLoader();
        Class as = null;
        try {
            as = classLoader.loadClass("com.tencent.mm.ui.chatting.SendImgProxyUI");
            mSendUi = XposedHelpers.newInstance(as);

            as = classLoader.loadClass("com.tencent.mm.ui.chatting.SendImgProxyUI$1");
            Object sendUi1Ob = XposedHelpers.newInstance(as,mSendUi,true,-5);

            Object xYx = XposedHelpers.getObjectField(sendUi1Ob, "xYx");
            XposedBridge.log("com.tencent.mm.ui.chatting.SendImgProxyUI$1.xYx="+ xYx.getClass().getName());
            as = classLoader.loadClass("com.tencent.mm.sdk.platformtools.ar");
            Object ar = XposedHelpers.newInstance(as,mSendImgToRoom1,mSendImgToRoom2,sendUi1Ob,null,mSendImgToRoom3);

            mSendUiInten = new Intent(Tools.mActivity, as);
            ArrayList list1 = new ArrayList();
            mSendUiInten.putStringArrayListExtra("key_select_video_list",list1);
            mSendUiInten.putExtra("CropImage_limit_Img_Size",new Integer(26214400));
            mSendUiInten.putExtra("GalleryUI_FromUser","zsbin001");
            mSendUiInten.putExtra("GalleryUI_ToUser",id);
            mSendUiInten.putExtra("KSelectImgUseTime",4626L);
            mSendUiInten.putStringArrayListExtra("CropImage_OutputPath_List",imgList);

            mSendUiInten.putExtra("CropImage_Compress_Img",false);

            XposedHelpers.callMethod(sendUi1Ob,"run");

           // XposedHelpers.callStaticMethod( classLoader.loadClass("com.tencent.mm.ak.a"), "b",ob1);
           // intent.puex("key_select_video_list",);
          //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

         //   Tools.mActivity.startActivity(mSendUiInten);

            //Class as1 = classLoader.loadClass("com.tencent.mm.i.f");
            //Object ob1 = XposedHelpers.newInstance(as1);




        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
