package yaunma.com.myapplication;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import java.sql.DatabaseMetaData;
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

        XposedBridge.log("roomID= "+roomId);
        XposedBridge.log("field_content= "+field_content);

        if(isSend == 1 || field_type != 1){
            if(isSend == 1 && TextUtils.isEmpty(ServerManager2.getmIntance().mMysId)){
                if(roomId.endsWith("@chatroom")){
                    String[] strs =  field_content.split(":\n");
                    ServerManager2.getmIntance().mMysId = strs[0];
                }else{
                    ServerManager2.getmIntance().mMysId = roomId;
                }
            }
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
            String text = "";
        //    if(DateSaveManager.getIntance().isJustShou){
                text = DateSaveManager.getIntance().mRobatIndex+"已上线20190508";
          //  }else{
         //       text = "解已上线";
          //  }
            Tools.sendTextToRoom(Tools.mActivity,text,roomId);
            int index = SscControl.getIntance().getIndex();
            ArrayMap<String, DateSaveManager.GroupDate> all = DateSaveManager.getIntance().getAllGroup();
            if(index  > 38 && index < ServerManager2.OPEN_INDEX){

                for(String g : all.keySet()){
                    all.get(g).isIntime = false;
                }
                DateSaveManager.getIntance().clearAllGroupFenAndLiang();
            }else{
                for(String g : all.keySet()){
                    all.get(g).isIntime = true;
                }
            }
            return;
        }

/*        if(!DateSaveManager.getIntance().isJustShou && msg.equals("解已上线") && !TextUtils.isEmpty(room) && DateSaveManager.getIntance().isGuanliQun(room)){
            if(!ServerManager2.getmIntance().mIgnoeList.containsKey(userId)){
                ServerManager2.getmIntance().mIgnoeList.put(userId,true);
            }
            return;
        }*/

        if(ServerManager2.getmIntance().mIgnoeList.size() > 0 &&  ServerManager2.getmIntance().mIgnoeList.containsKey(userId)){
            return;
        }

        if(!ServerManager2.getmIntance().isInit){
            return;
        }

        if(msg.contains("期开") && msg.contains("第")){
            if(userId.equals(DateSaveManager.getIntance().mKaikaikai)||
                    (!TextUtils.isEmpty(room) && DateSaveManager.getIntance().isHaveGroup(room))){

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
                if(parse.index >0 && parse.index<300 && parse.str.length() == 5 ){
/*                    if(DateSaveManager.getIntance().isJustShou &&
                            DateSaveManager.getIntance().isHaveGroup(room) &&
                            DateSaveManager.getIntance().getGroup(room).isEnable &&
                            !TextUtils.isEmpty(DateSaveManager.getIntance().getGroup(room).toGroup) &&
                            DateSaveManager.getIntance().getGroup(DateSaveManager.getIntance().getGroup(room).toGroup).isEnable
                            ){
                        SscControl.getIntance().sendMessage(msg,DateSaveManager.getIntance().getGroup(room).toGroup,false);
                    }
*/
                    if(ServerManager2.getmIntance().mOpenIndex != parse.index){
                        SscControl.getIntance().kaijaingEnd(parse);

                    }
                }
            }
            return;
        }
        try{
            ServerManager2.getmIntance().receiveMessage(msg,userId,room,msgId);
        }catch (Exception e){
            XposedBridge.log(e.toString());
        }

    }

}
