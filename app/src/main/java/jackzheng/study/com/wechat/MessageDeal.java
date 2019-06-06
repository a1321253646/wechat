package jackzheng.study.com.wechat;

import android.text.TextUtils;

import de.robv.android.xposed.XposedBridge;
import jackzheng.study.com.wechat.sscManager.ServerManager2;

public class MessageDeal {

    public static final String QUN_KAI_STR = "开";
    public static final String TING_STR = "关";
    public static final String SHANG_FEN_STR = "上分";
    public static final String XIA_FEN_STR = "下分";
    public static final String SHE_FEN_STR = "设分";
    public static final String SHANG_LIANG_STR = "上量";
    public static final String XIA_LIANG__STR = "下量";
    public static final String SHE_LIANG__STR = "设量";
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
    public static final String SET_ZHENGQUE= "正确";
    public static final String SET_BAOBIAO= "报表";
    public static final String SET_XIANE= "限额";
    public static final String SET_NO_XIANER= "不限";
    public static final String SET_KAI_USER= "奖开";
    public static final String SET_BEIYONG= "备用";
    public static final String SET_WORK_START= "工号开";
    public static final String SET_WORK_END= "工号停";
    public static final String SET_ROBAT_NUMBER= "机器人";
    public static final String SET_MODOL_INDEX ="式模";
    public static final String SET_THIRD_OPEN_MODEL ="奖三开";
    public static final String SET_THIRD_CLOSE_MODEL ="奖三关";
    public static final String YING_3_STR = "三赢";
    public static final String YING_4_STR = "四赢";

    public static final String[] ORDER_LIST = {QUN_KAI_STR,TING_STR,SHANG_FEN_STR,XIA_FEN_STR,
            SHE_FEN_STR,SHANG_LIANG_STR,XIA_LIANG__STR,SHE_LIANG__STR,GUAN_LI_STR,YING_STR,
            CHECK_STR,CLEAR_CHECK_STR,RECEVI_ZU_STR,SEND_ZU_STR,GUAN_LI_QUN,SET_QUN_QUN_NAME,SET_JUST_SHOU,
            SET_ZONG_QUN,SET_TUI,SET_RESET,SET_DETAIL,SET_TIXING,SET_FENGLIANG,SET_FANG_QUN,SET_YUANQUN_CEXIAO,
            SET_ZHENGQUE,SET_BAOBIAO,SET_XIANE,SET_NO_XIANER,SET_KAI_USER,SET_BEIYONG,
            SET_WORK_START,SET_WORK_END,SET_ROBAT_NUMBER,SET_MODOL_INDEX,SET_THIRD_OPEN_MODEL,SET_THIRD_CLOSE_MODEL,
            YING_3_STR,YING_4_STR
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
    public static final int SET_ZHENGQUE_INT= 27;
    public static final int SET_BAOBIAO_INT= 28;
    public static final int SET_XIANER_INT= 29;
    public static final int SET_NO_XIANER_INT= 30;
    public static final int SET_KAI_USER_INT= 31;
    public static final int SET_BEIYONG_INT= 32;
    public static final int SET_WORK_START_INT= 33;
    public static final int SET_WORK_END_INT= 34;
    public static final int SET_ROBAT_NUMBER_INT= 35;
    public static final int SET_MODOL_INDEX_INT= 36;
    public static final int SET_THIRD_MODOL_OPEN_INT= 37;
    public static final int SET_THIRD_MODOL_CLOSE_INT= 38;
    public static final int YING_3_INT= 39;
    public static final int YING_4_INT= 40;

    public static final int[] ORDER_TYPE_LIST = {QUN_KAI_INT,TING_INT,SHANG_FEN_INT,XIA_FEN_INT,SHE_FEN_INT,SHANG_LIANG_INT,
            XIA_LIANG_INT,SHE_LIANG_INT,GUAN_LI_INT,YING_INT,CHECK_INT,CLEAR_CHECK_INT,RECEVI_ZU_INT,SEND_ZU_INT,
            GUAN_LI_QUN_INT,SET_QUN_QUN_NAME_INT,SET_JUST_SHOU_INT,
            SET_ZONG_QUN_INT,SET_TUI_INT,SET_RESET_INT,SET_DETAIL_INT,SET_TIXING_INT,SET_FENGLIANG_INT,SET_FANG_QUN_INT,
            SET_YUANQUN_CEXIAO_NT,SET_ZHENGQUE_INT,SET_BAOBIAO_INT,SET_XIANER_INT,SET_NO_XIANER_INT,SET_KAI_USER_INT,SET_BEIYONG_INT,
            SET_WORK_START_INT,SET_WORK_END_INT,SET_ROBAT_NUMBER_INT,SET_MODOL_INDEX_INT,SET_THIRD_MODOL_OPEN_INT,SET_THIRD_MODOL_CLOSE_INT
            ,YING_3_INT,YING_4_INT
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
