package jackzheng.study.com.wechat.sscManager;

import android.os.Handler;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import de.robv.android.xposed.XposedBridge;
import jackzheng.study.com.wechat.MessageDeal;
import jackzheng.study.com.wechat.NetManager;
import jackzheng.study.com.wechat.SscControl;

public class ServerManager2 {
    public void receiveMessage(String str,String userId,String group){
        if(TextUtils.isEmpty(str)||
                TextUtils.isEmpty(userId)||
                TextUtils.isEmpty(group)){
            return;
        }

    }
}
