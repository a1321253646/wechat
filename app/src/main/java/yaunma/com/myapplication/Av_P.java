package yaunma.com.myapplication;

import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;


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
        Log.d("mylog","userId:"+userId);
        Log.d("mylog","msg:"+msg);
        Log.d("mylog","room:"+room);
        if (msg.equals("发送")){
            Tools.sendTextToRoom(Tools.mActivity,"我回答了",roomId);
        }

    }

}
