package yaunma.com.myapplication;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import jackzheng.study.com.wechat.HtmlParse;
import jackzheng.study.com.wechat.SscControl;
import jackzheng.study.com.wechat.regular.StringDealFactory;
import jackzheng.study.com.wechat.sscManager.DateSaveManager;
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
        if(TextUtils.isEmpty(userId) || TextUtils.isEmpty(msg)){
            return;
        }

        if(ServerManager2.getmIntance().mAllMessage.containsKey(field_msgSvrId)){
            return;
        }
        ServerManager2.getmIntance().mAllMessage.put(field_msgSvrId,true);
        if (msg.equals("上线")){
            ServerManager2.getmIntance().isInit = true;
            Tools.sendTextToRoom(Tools.mActivity,"已上线",roomId);
            int index = SscControl.getIntance().getIndex();
            if(index  > 38 && index < 102){
                ServerManager2.getmIntance().isTime = false;
                DateSaveManager.getIntance().clearAllGroupFenAndLiang();
            }
            return;
        }
        if(!ServerManager2.getmIntance().isInit){
            return;
        }

        if(!ServerManager2.getmIntance().isTime && userId.equals(DateSaveManager.getIntance().mKaikaikai) && msg.contains("期开") && msg.contains("第")){
            HtmlParse.MaxIndexResult parse = new HtmlParse.MaxIndexResult();
            char[] cs = msg.toCharArray();
            int csIndex = 0;
            String str = "";
            while (csIndex < cs.length &&!StringDealFactory.isNumber(cs[csIndex])){
                csIndex++;
            }
            while (csIndex < cs.length &&StringDealFactory.isNumber(cs[csIndex])){
                str=str+cs[csIndex];
                csIndex++;
            }
            parse.index = Integer.parseInt(str);

            str = "";
            while (csIndex < cs.length &&!StringDealFactory.isNumber(cs[csIndex])){
                csIndex++;
            }
            while (csIndex < cs.length &&StringDealFactory.isNumber(cs[csIndex])){
                str=str+cs[csIndex];
                csIndex++;
            }
            parse.str = str;
            if(parse.index >0 && parse.str.length() == 5){
                SscControl.getIntance().kaijaingEnd(parse);
            }
        }
        try{
            ServerManager2.getmIntance().receiveMessage(msg,userId,room,msgId);
        }catch (Exception e){
            XposedBridge.log(e.toString());
        }

    }

}
