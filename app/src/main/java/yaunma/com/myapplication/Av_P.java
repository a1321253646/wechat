package yaunma.com.myapplication;

import android.content.ContentValues;
import android.util.Log;

import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import jackzheng.study.com.wechat.HtmlParse;
import jackzheng.study.com.wechat.sscManager.ServerManager2;


/**
 * Created by Administrator on 2018-05-20.
 */

public class Av_P extends XC_MethodHook {


    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
        final ClassLoader classLoader = param.thisObject.getClass().getClassLoader();
        int field_type = XposedHelpers.getIntField(param.thisObject, "field_type");
        //消息
        final String field_content = (String) XposedHelpers.getObjectField(param.thisObject, "field_content");
        //群的id
        final String roomId = (String) XposedHelpers.getObjectField(param.thisObject, "field_talker");
        final long field_msgSvrId = XposedHelpers.getLongField(param.thisObject, "field_msgSvrId");
        final long isSend = XposedHelpers.getLongField(param.thisObject, "field_isSend");
        final long msgId = XposedHelpers.getLongField(param.thisObject, "field_msgId");


        if(isSend == 1 || field_type != 1){
            return;
        }

        String userId = "";
        String msg = "";
        String room = "";

        if(roomId.endsWith("@chatroom")){
            String[] strs =  field_content.split(":\n");
            userId = strs[0];
            msg = strs[1];
            room = roomId;
        }else{
            userId = roomId;
            msg = field_content;
            room = "";
        }
        if(ServerManager2.getmIntance().mAllMessage.containsKey(field_msgSvrId)){
            return;
        }
        ServerManager2.getmIntance().mAllMessage.put(field_msgSvrId,true);
        if (msg.equals("上线")){
            ServerManager2.getmIntance().isInit = true;
            Tools.sendTextToRoom(Tools.mActivity,"已上线",roomId);
        }
        if(!ServerManager2.getmIntance().isInit){
            return;
        }
        try{
            ServerManager2.getmIntance().receiveMessage(msg,userId,room,msgId);
        }catch (Exception e){
            XposedBridge.log(e.toString());
        }

    }

}
