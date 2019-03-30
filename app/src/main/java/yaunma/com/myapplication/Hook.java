package yaunma.com.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import org.json.JSONArray;

import java.net.Socket;
import java.util.concurrent.ExecutorService;

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

        } catch (Exception e) {

            XposedBridge.log(e);
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
                                    param.args[1] = "select * from message where type=? and msgId=?";
                                    param.args[2] = newObjArr;
                                    newObjArr[0] = 1;
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
