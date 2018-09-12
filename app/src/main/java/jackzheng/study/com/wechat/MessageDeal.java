package jackzheng.study.com.wechat;

import android.text.TextUtils;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class MessageDeal {

    public static final String KAIJIANG_STR = "开:";
    public static final String CHANGE_STR = "id:";
    public static final String QUN_NAME_STR = "群名";
    public static final String QUN_QIAN_STR = "裙关";
    public static final String QUN_KAI_STR = "裙开";
    public static final String TING_STR = "停:";
    public static final String KAISHI_STR = "开始:";
    public static final String TUI_STR = "退";
    public static final String SHANG_FEN_STR = "尚芬";
    public static final String XIA_FEN_STR = "下芬";
    public static final String GUAN_LI_STR = "馆里";
    public static final String YING_STR = "一赢";
    public static final String SHE_FEN_STR = "设芬";
    public static final String SP_GL_STR = "SP管理:";
    public static final String CHECK_STR = "查量";
    public static final String CLEAR_CHECK_STR = "清所有量";
    public static final String SET_CHECK_STR = "设量";
    public static final String CHECK_FEN_STR  = "查芬";
    public static final String RECEVI_ZU_STR  = "robat接受";
    public static final String SEND_ZU_STR  = "robat发送";
    public static final String SEND_SHENFEN  = "身份";
    public static final String SHANG_XIAN = "上限";
    public static final String GUAN_LI_QUN = "关理裙";
    public static final String SHANG_WEI_QUN = "尚未";
    public static final String GENG_XIN_JSON = "更新数据";
    public static final String[] ORDER_LIST = {KAIJIANG_STR,CHANGE_STR,QUN_NAME_STR,
            QUN_QIAN_STR,QUN_KAI_STR,TING_STR,KAISHI_STR,TUI_STR,SHANG_FEN_STR,
            XIA_FEN_STR,GUAN_LI_STR,YING_STR,SHE_FEN_STR,SP_GL_STR,CHECK_STR,CLEAR_CHECK_STR,SET_CHECK_STR,
            CHECK_FEN_STR,RECEVI_ZU_STR,SEND_ZU_STR,SHANG_XIAN,GUAN_LI_QUN,SEND_SHENFEN,SHANG_WEI_QUN,GENG_XIN_JSON
    };

    public static final int KAIJIANG_INT = 1;
    public static final int CHANGE_INT = 2;
    public static final int QUN_NAME_INT= 3;
    public static final int QUN_QIAN_INT= 4;
    public static final int QUN_KAI_INT= 5;
    public static final int TING_INT = 6;
    public static final int KAISHI_INT = 7;
    public static final int TUI_INT= 8;
    public static final int SHANG_FEN_INT = 9;
    public static final int XIA_FEN_INT= 10;
    public static final int GUAN_LI_INT = 11;
    public static final int XIA_ZU_INT = 12;
    public static final int YING_INT = 13;
    public static final int SHE_FEN_INT = 14;
    public static final int SP_GL_INT = 15;
    public static final int CHECK_INT = 16;
    public static final int CLEAR_CHECK_INT = 17;
    public static final int SET_CHECK_INT = 18;
    public static final int CHECK_FEN_INT = 19;
    public static final int RECEVI_ZU_INT  = 20;
    public static final int SEND_ZU_INT  = 21;
    public static final int SHANG_XIAN_INT  = 22;
    public static final int GUAN_LI_QUN_INT  = 23;
    public static final int SHENG_FEN_INT  = 24;
    public static final int SHANG_WEI_INT  = 25;
    public static final int GENG_XIN_INT  = 26;

    public static final int[] ORDER_TYPE_LIST = {KAIJIANG_INT,CHANGE_INT,QUN_NAME_INT,
            QUN_QIAN_INT,QUN_KAI_INT,TING_INT,KAISHI_INT,TUI_INT,SHANG_FEN_INT,
            XIA_FEN_INT,GUAN_LI_INT,YING_INT,SHE_FEN_INT,SP_GL_INT,CHECK_INT,CLEAR_CHECK_INT,SET_CHECK_INT,
            CHECK_FEN_INT,RECEVI_ZU_INT,SEND_ZU_INT,SHANG_XIAN_INT,GUAN_LI_QUN_INT,SHENG_FEN_INT,SHANG_WEI_INT
            ,GENG_XIN_INT
    };

    public  static final String GROUP_END = "@chatroom";

    public static MessagerDealDate getMessageDealData(String message,String id,String group){
        MessagerDealDate data = new MessagerDealDate();
        data.groupID = group;
        data.TakerId = id;
        data.message = message;
        if(TextUtils.isEmpty(data.TakerId) ){
            return null;
        }
        data.type = getType(data.message);

        if(data.type == XIA_ZU_INT && TextUtils.isEmpty(data.message)){
            return null;
        }
        XposedBridge.log("getMessageDealData :"+data.toString());
        return data;
    }

    private  static int getType(String str){
        int type = -1;
        for(int i = 0;i<ORDER_LIST.length ;i++){
            if(str.startsWith(ORDER_LIST[i])){
                type = ORDER_TYPE_LIST[i];
                break;
            }
        }
        if(type == -1 ){
            type = XIA_ZU_INT;
        }
        return type;
    }

    public static class MessagerDealDate{
        public int type = -1;
        public String TakerId;
        public String groupID ;
        public String message;

        @Override
        public String toString() {
            return "TakerId = "+TakerId+" groupID="+groupID+" message="+message+" type="+type;
        }
    }

}
