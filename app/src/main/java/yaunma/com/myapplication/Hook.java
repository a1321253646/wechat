package yaunma.com.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.jsoup.select.Evaluator;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import jackzheng.study.com.wechat.SscControl;
import jackzheng.study.com.wechat.sscManager.DateSaveManager;
import jackzheng.study.com.wechat.sscManager.ServerManager2;


/**
 * Created by junyan_mac on 17/10/8.
 */

public enum Hook {
    INSTANCE;
    Socket socket = null;
    public static ExecutorService mExecutorService;
    public static Boolean ishook=false;
    public static Class mSendImg ;
    Handler mHandler ;
    public void doHook(Activity activity) {
        try {

            if (ishook) {
                return;
            }
            ishook = true;
            Tools.mActivity=activity;
            SscControl.getIntance().init();
            DateSaveManager.getIntance();
            ClassLoader classLoader = activity.getClassLoader();

            Class cd = classLoader.loadClass(Constance.className_cg);//672
            XposedHelpers.findAndHookMethod(cd, Constance.methodName_cg_vP, new Av_P());//672
            hookDB(classLoader);
           // hookImgSend(classLoader);

        } catch (Exception e) {

            XposedBridge.log(e);
        }

    }

    private static void printfParam(XC_MethodHook.MethodHookParam param){
        Object[] os= param.args;
        if(os != null){
            XposedBridge.log("ssc:"+" printfParam size ="+os.length );
            for(int i = 0 ; i< os.length;i++){
                Object ob = os[i];

                if(ob != null){
                    XposedBridge.log("ssc:"+" printfParam os ["+i+"] ob type ="+ob.getClass().getName());
                    if(ob instanceof List){
                        XposedBridge.log("ssc:"+"printfParam ob size ="+((List) ob).size());
                        for(int ii = 0 ;ii< ((List) ob).size();ii++){
                            XposedBridge.log("ssc:"+" printfParam os ["+i+"] param["+ii+"]="+((List) ob).get(ii).toString());
                        }
                    }else if(ob instanceof Intent){
                        XposedBridge.log("ssc:"+" printfParam os ["+i+"] ob="+ob.toString());
                        XposedBridge.log("ssc:"+" printfParam os ["+i+"] ob="+((Intent) ob).getFlags());
                        XposedBridge.log("ssc:"+" printfParam os ["+i+"] ob="+((Intent) ob).getAction());
                        XposedBridge.log("ssc:"+" printfParam os ["+i+"] ob="+((Intent) ob).getData());
                        Intent it = ((Intent)ob);
                        Bundle extras = it.getExtras();
                        if(extras != null){
                            for(String key : extras.keySet()){
                                XposedBridge.log("ssc:"+" printfParam os ["+i+"] Bundle["+key+"]="+extras.get(key).getClass().getName());
                                XposedBridge.log("ssc:"+" printfParam os ["+i+"] Bundle["+key+"]="+extras.get(key));
                            }
                        }else{
                            XposedBridge.log("ssc:"+" printfParam os ["+i+"] Bundle =null");

                        }
                    }else if(ob instanceof Message){
                        Message me = ((Message)ob);
                        XposedBridge.log("ssc:"+"printfParam os ["+i+"] Message="+me.toString());
                        XposedBridge.log("ssc:"+"printfParam os ["+i+"] what="+me.what);
                        XposedBridge.log("ssc:"+"printfParam os ["+i+"] arg1="+me.arg1);
                        XposedBridge.log("ssc:"+"printfParam os ["+i+"] arg2="+me.arg2);
                        XposedBridge.log("ssc:"+"printfParam os ["+i+"] obj="+me.obj);
                        XposedBridge.log("ssc:"+"printfParam os ["+i+"] param="+me.getCallback());
                        Bundle extras = me.getData();
                        if(extras != null){
                            for(String key : extras.keySet()){
                                XposedBridge.log("ssc:"+" printfParam os ["+i+"] Bundle["+key+"]="+extras.get(key).getClass().getName());
                                XposedBridge.log("ssc:"+" printfParam os ["+i+"] Bundle["+key+"]="+extras.get(key));
                            }
                        }else{
                            XposedBridge.log("ssc:"+" printfParam os ["+i+"] Message Bundle =null");

                        }

                    }else{
                        XposedBridge.log("ssc:"+"printfParam os ["+i+"] param="+ob.toString());
                    }

                }else{
                    XposedBridge.log("ssc:"+" printfParam os ["+i+"] param=null");
                }
            }
        }

    }

     public static  void hookImgSend(final ClassLoader classLoader){

         try {
    /*         Class a = classLoader.loadClass("com.tencent.mm.i.c");
             XposedBridge.hookAllConstructors(a, new XC_MethodHook() {
                 @Override
                 protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                     super.beforeHookedMethod(param);
                     XposedBridge.log("ssc:"+"com.tencent.mm.i.c new" );
                     printfParam(param);
                 }
             });
             Class b = classLoader.loadClass("com.tencent.mm.i.d");
             XposedBridge.hookAllConstructors(b, new XC_MethodHook() {
                 @Override
                 protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                     super.beforeHookedMethod(param);
                     XposedBridge.log("ssc:"+"com.tencent.mm.i.d new" );
                     printfParam(param);
                 }
             });
             XposedBridge.hookAllConstructors(classLoader.loadClass("com.tencent.mm.as.l$4"), new XC_MethodHook() {
                 @Override
                 protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                     super.beforeHookedMethod(param);
                     XposedBridge.log("ssc:"+"com.tencent.mm.as.l$4 new" );
                     printfParam(param);
                 }
             });
             XposedBridge.hookAllConstructors(classLoader.loadClass("com.tencent.mm.ak.b"),new XC_MethodHook() {
                 @Override
                 protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                     super.beforeHookedMethod(param);
                     XposedBridge.log("ssc:"+"com.tencent.mm.ak.b new" );
                     printfParam(param);
                 }
             });
             XposedBridge.hookAllConstructors(classLoader.loadClass("com.tencent.mm.i.f"),new XC_MethodHook() {
                 @Override
                 protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                     super.beforeHookedMethod(param);
                     XposedBridge.log("ssc:"+"com.tencent.mm.i.f new" );
                     printfParam(param);

                 }
             });
             XposedBridge.hookAllConstructors(classLoader.loadClass("com.tencent.mm.ak.b$7"),new XC_MethodHook() {
                         @Override
                         protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                             super.beforeHookedMethod(param);
                             XposedBridge.log("ssc:"+"com.tencent.mm.ak.b$7 new" );
                             printfParam(param);

                         }
                     });
             XposedBridge.hookAllConstructors(classLoader.loadClass("com.tencent.mm.as.l"), new XC_MethodHook() {
                 @Override
                 protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                     super.beforeHookedMethod(param);
                     XposedBridge.log("ssc:"+"com.tencent.mm.as.l new" );
                     printfParam(param);
                 }
             });
             XposedHelpers.findAndHookMethod(classLoader.loadClass("com.tencent.mm.as.l$4"),"a",
                   String.class, int.class,a,b, boolean.class,new XC_MethodHook() {
                 @Override
                 protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                     super.beforeHookedMethod(param);
                     XposedBridge.log("ssc:"+"com.tencent.mm.as.l$4" );
                     printfParam(param);

                 }
             });
             XposedHelpers.findAndHookMethod(classLoader.loadClass("com.tencent.mm.sdk.platformtools.ar"),"b",
                     classLoader.loadClass("com.tencent.mm.i.f"),new XC_MethodHook() {
                         @Override
                         protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                             super.beforeHookedMethod(param);
                             XposedBridge.log("ssc:"+"com.tencent.mm.ak.a.b" );
                             printfParam(param);
                         }
                     });

           XposedHelpers.findAndHookMethod(classLoader.loadClass("com.tencent.mm.sdk.platformtools.am"),"dispatchMessage", Message.class, new XC_MethodHook() {
                         @Override
                         protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                             super.beforeHookedMethod(param);
                             XposedBridge.log("ssc:"+"com.tencent.mm.sdk.platformtools.am" );
                             printfParam(param);

                         }
                     });*/
             XposedBridge.hookAllConstructors(classLoader.loadClass("com.tencent.mm.sdk.platformtools.ar"), new XC_MethodHook() {
                 @Override
                 protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                     super.beforeHookedMethod(param);
                     XposedBridge.log("ssc:" + "com.tencent.mm.sdk.platformtools.ar new");
               //      printfParam(param);

                     if(Tools.mSendImgToRoom1 == null){
                         if(param.args != null && param.args.length == 5 && param.args[2] != null){
                             XposedBridge.log("ssc:" + "com.tencent.mm.sdk.platformtools.ar param.args[2 = "+param.args[2].getClass().getName());
                                if( param.args[2].getClass().getName().equals("com.tencent.mm.ui.chatting.SendImgProxyUI$1@c1")){
                                    Tools.mSendImgToRoom1 = param.args[0];
                                    Tools.mSendImgToRoom2 = param.args[1];
                                    Tools.mSendImgToRoom3 = param.args[4];
                                }
                         }
                     }
                 }
             });
             XposedBridge.hookAllConstructors(classLoader.loadClass("com.tencent.mm.ui.chatting.SendImgProxyUI$1"), new XC_MethodHook() {
                 @Override
                 protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                     super.beforeHookedMethod(param);
                     XposedBridge.log("ssc:" + "com.tencent.mm.ui.chatting.SendImgProxyUI$1 new");
                     printfParam(param);

                 }
             });
             XposedBridge.hookAllConstructors(classLoader.loadClass("com.tencent.mm.ui.chatting.SendImgProxyUI"), new XC_MethodHook() {
                 @Override
                 protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                     super.beforeHookedMethod(param);
                     XposedBridge.log("ssc:" + "com.tencent.mm.ui.chatting.SendImgProxyUI new");
                     printfParam(param);
                 }
             });

         } catch (ClassNotFoundException e) {
             e.printStackTrace();
         }


         Class cd0 = null;
         try {
             cd0 = classLoader.loadClass("com.tencent.mm.ui.chatting.SendImgProxyUI");//672

         } catch (Exception e) {
             e.printStackTrace();
         }
         XposedHelpers.findAndHookMethod(cd0,"a",cd0,Intent.class,new XC_MethodHook() {
             @Override
             protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                 super.beforeHookedMethod(param);
                 XposedBridge.log("ssc:"+"com.tencent.mm.ui.chatting.SendImgProxyUI.a" );
                 printfParam(param);
                if(param != null && param.args != null && param.args.length == 2){
                    if(param.args[0] != null  && param.args[0]== Tools.mSendUi ){
                        param.args[1] = Tools.mSendUiInten;
                        Tools.mSendImgToRoom1 = null;
                    }
                }
             }
         });

         Class cd = null;
         try {
              cd = classLoader.loadClass("com.tencent.mm.as.n");//672
             Method[] methods = cd.getMethods();
             for(Method m : methods){
                 XposedBridge.log("ssc:"+" hookImgSend  getMethods name = "+m.getName() );
                 Class<?>[] parameterTypes = m.getParameterTypes();
                 if(parameterTypes != null && parameterTypes.length > 0){
                     for(Class c: parameterTypes){
                         XposedBridge.log("ssc:"+" hookImgSend   "+m.getName()+" param ="+ c.getName());
                     }

                 }
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
         if(cd != null){
             ArrayList<String> a = new ArrayList<>();
             XposedHelpers.findAndHookMethod(cd,"a",ArrayList.class,boolean.class,int.class,int.class,String.class,int.class ,new XC_MethodHook() {
                         @Override
                         protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                             super.beforeHookedMethod(param);
                             XposedBridge.log("ssc:"+"com.tencent.mm.as.n.a" );
                             printfParam(param);
                         }
                     });

         }
         Class cd2 = null;
         try {
             cd2 = classLoader.loadClass("com.tencent.mm.ui.chatting.SendImgProxyUI");//672
             Method[] methods2 = cd2.getMethods();
             for(Method m : methods2){
                 XposedBridge.log("ssc:"+" hookImgSend SendImgProxyUI getMethods name = "+m.getName() );
                 Class<?>[] parameterTypes = m.getParameterTypes();
                 if(parameterTypes != null && parameterTypes.length > 0){
                     for(Class c: parameterTypes){
                         XposedBridge.log("ssc:"+" hookImgSend SendImgProxyUI  "+m.getName()+" param ="+ c.getName());
                     }

                 }
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
         if(cd2 != null){
             ArrayList<String> a = new ArrayList<>();
             XposedHelpers.findAndHookMethod(cd2,"a",ArrayList.class,int.class,ArrayList.class,boolean.class,new XC_MethodHook() {
                 @Override
                 protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                     super.beforeHookedMethod(param);
                     XposedBridge.log("ssc:"+" com.tencent.mm.ui.chatting.SendImgProxyUI.a " );
                     printfParam(param);

                 }
             });

         }

    }

    private void hookDB( final ClassLoader classLoader) {
        final Class<?> sQLiteDatabase = XposedHelpers.findClass("com.tencent.wcdb.database.SQLiteDatabase", classLoader);
        final Class<?> cancellationSignal = XposedHelpers.findClass("com.tencent.wcdb.support.CancellationSignal", classLoader);
        if (sQLiteDatabase == null) return;
        XposedHelpers.findAndHookConstructor("com.tencent.wcdb.database.SQLiteProgram",
                classLoader,
                sQLiteDatabase,
                String.class,
                Object[].class,
                cancellationSignal,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        Object[] objArr = (Object[]) param.args[2];
                        String originalSql = param.args[1].toString();
                        //打印所有调用SQLiteProgram的sql
                   //     XposedBridge.log("hookDB", "sql -> " + param.args[1], "objArr:" + J JSON.toJSONString(objArr));
                        if (objArr != null && originalSql.toUpperCase().startsWith("UPDATE MESSAGE")) {
                            for (Object obj : objArr) {
                                String sqlParam = obj.toString();//自己撤回10002 别人撤回10000
                                if (sqlParam.equals("10000")) {//别人撤回
                                    Object[] newObjArr = new Object[2];
                                    //param.args[1] = "UPDATE message SET type=? WHERE msgId=?";
                                  //  param.args[1] = "select * from message where type=? and msgId=?";
                                  //  param.args[2] = newObjArr;
                                   // newObjArr[0] = 1;
                                    newObjArr[1] = objArr[objArr.length - 1];
                                    //param.args[1] = "UPDATE message SET content=(select (select content from message where msgId = ?)||X'0D'||X'0A'||X'0D'||X'0A'||(\"<sysmsg>wxInvoke卧槽，TA竟然要撤回上面的信息wxInvoke</sysmsg>\")),msgId=?,type=? WHERE msgId=?";
                                  //  XposedBridge.log("撤回的信息是msgid="+ newObjArr[1]);
                                    ServerManager2.getmIntance().tuiDeal(Long.parseLong(newObjArr[1].toString()));
                                   // LogUtils.e("hookDB", "originalSql->" + originalSql, "newSql->" + param.args[1], "sqlParam->" + JSON.toJSONString(newObjArr));
                                    //WxChatInvokeMsg msg = new WxChatInvokeMsg();
                                    //msg.setMsgId(newObjArr[1].toString());
                                   // WxChatInvokeMsgDB.insertData(applicationContext, msg);
                                }
                            }
                        }
                        return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                    }
                });

    }

}
