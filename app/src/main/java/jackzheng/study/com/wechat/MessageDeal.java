package jackzheng.study.com.wechat;

import android.text.TextUtils;

import de.robv.android.xposed.XposedBridge;
import jackzheng.study.com.wechat.sscManager.ServerManager2;

public class MessageDeal {

    public static final String QUN_KAI_STR = "开";
    public static final String TING_STR = "关";
    public static final String SHANG_FEN_STR = "分上";
    public static final String XIA_FEN_STR = "分下";
    public static final String SHE_FEN_STR = "分设";
    public static final String SHANG_LIANG_STR = "量上";
    public static final String XIA_LIANG__STR = "量下";
    public static final String SHE_LIANG__STR = "量设";
    public static final String GUAN_LI_STR = "馆里";
    public static final String YING_STR = "赢";
    public static final String CHECK_STR = "查";
    public static final String CLEAR_CHECK_STR = "清";
    public static final String RECEVI_ZU_STR  = "收";
    public static final String SEND_ZU_STR  = "解";
    public static final String GUAN_LI_QUN = "管裙";
    public static final String SET_QUN_QUN_NAME = "裙";
    public static final String SET_JUST_SHOU = "只收";
    public static final String SET_ZONG_QUN = "错";
    public static final String SET_TUI = "退";
    public static final String SET_RESET = "重置";
    public static final String SET_EDIT= "改";
    public static final String SET_DETAIL= "码";
    public static final String SET_TIXING= "提";
    public static final String SET_FENGLIANG= "分量";
    public static final String SET_FANG_QUN= "放裙";
    public static final String SET_YUANQUN_CEXIAO= "原裙撤销";


    public static final String[] ORDER_LIST = {QUN_KAI_STR,TING_STR,SHANG_FEN_STR,XIA_FEN_STR,
            SHE_FEN_STR,SHANG_LIANG_STR,XIA_LIANG__STR,SHE_LIANG__STR,GUAN_LI_STR,YING_STR,
            CHECK_STR,CLEAR_CHECK_STR,RECEVI_ZU_STR,SEND_ZU_STR,GUAN_LI_QUN,SET_QUN_QUN_NAME,SET_JUST_SHOU,
            SET_ZONG_QUN,SET_TUI,SET_RESET,SET_DETAIL,SET_TIXING,SET_FENGLIANG,SET_FANG_QUN,SET_YUANQUN_CEXIAO
    };

    public static final int XIA_ZU_INT = 0;
    public static final int QUN_KAI_INT = 1;
    public static final int TING_INT = 2;
    public static final int SHANG_FEN_INT = 3;
    public static final int XIA_FEN_INT = 4;
    public static final int SHE_FEN_INT = 5;
    public static final int SHANG_LIANG_INT = 6;
    public static final int XIA_LIANG_INT = 7;
    public static final int SHE_LIANG_INT = 8;
    public static final int GUAN_LI_INT = 9;
    public static final int YING_INT = 10;
    public static final int CHECK_INT = 11;
    public static final int CLEAR_CHECK_INT = 12;
    public static final int RECEVI_ZU_INT  = 13;
    public static final int SEND_ZU_INT  = 14;
    public static final int GUAN_LI_QUN_INT = 15;
    public static final int SET_QUN_QUN_NAME_INT = 16;
    public static final int SET_JUST_SHOU_INT = 17;
    public static final int SET_ZONG_QUN_INT = 18;
    public static final int SET_TUI_INT = 19;
    public static final int SET_RESET_INT = 20;
    public static final int SET_EDIT_INT = 21;
    public static final int SET_DETAIL_INT = 22;
    public static final int SET_TIXING_INT = 23;
    public static final int SET_FENGLIANG_INT= 24;
    public static final int SET_FANG_QUN_INT= 25;
    public static final int SET_YUANQUN_CEXIAO_NT= 26;

    public static final int[] ORDER_TYPE_LIST = {QUN_KAI_INT,TING_INT,SHANG_FEN_INT,XIA_FEN_INT,SHE_FEN_INT,SHANG_LIANG_INT,
            XIA_LIANG_INT,SHE_LIANG_INT,GUAN_LI_INT,YING_INT,CHECK_INT,CLEAR_CHECK_INT,RECEVI_ZU_INT,SEND_ZU_INT,
            GUAN_LI_QUN_INT,SET_QUN_QUN_NAME_INT,SET_JUST_SHOU_INT,
            SET_ZONG_QUN_INT,SET_TUI_INT,SET_RESET_INT,SET_DETAIL_INT,SET_TIXING_INT,SET_FENGLIANG_INT,SET_FANG_QUN_INT,
            SET_YUANQUN_CEXIAO_NT
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
        if(str.contains(SET_EDIT)){
            String[] strs = str.split(SET_EDIT);
            if(strs.length == 2 && !TextUtils.isEmpty(strs[0]) && ServerManager2.getmIntance().isAllNumber(strs[0])){
                type = SET_EDIT_INT;
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
