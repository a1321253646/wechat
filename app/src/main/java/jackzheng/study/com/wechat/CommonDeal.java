package jackzheng.study.com.wechat;

import android.text.TextUtils;

import jackzheng.study.com.wechat.sscManager.DateSaveManager;
import jackzheng.study.com.wechat.sscManager.ServerManager2;

public class CommonDeal {
    public static boolean deal(MessageDeal.MessagerDealDate data){
        if(data.type == MessageDeal.SET_QUN_QUN_NAME_INT &&
                !TextUtils.isEmpty(data.groupID)&&
                DateSaveManager.getIntance().isGuanli(data.TakerId)&&
                !DateSaveManager.getIntance().isGuanliQun(data.groupID)){//群名设置
            saveGroupName(data);
            return true;
        }else if(data.type == MessageDeal.QUN_KAI_INT &&
                !TextUtils.isEmpty(data.groupID)&&
                DateSaveManager.getIntance().isGuanli(data.TakerId)&&
                DateSaveManager.getIntance().isHaveGroup(data.groupID)){//开群
            DateSaveManager.getIntance().saveGroupEnable(data.groupID,true);
            SscControl.getIntance().sendMessage(DateSaveManager.getIntance().getGroup(data.groupID).groupName+"开启",DateSaveManager.getIntance().mGuanliQun,false);
            return true;
        }else if(data.type == MessageDeal.TING_INT &&
                !TextUtils.isEmpty(data.groupID)&&
                DateSaveManager.getIntance().isGuanli(data.TakerId)&&
                DateSaveManager.getIntance().isHaveGroup(data.groupID)){//关群
            DateSaveManager.getIntance().saveGroupEnable(data.groupID,false);
            SscControl.getIntance().sendMessage(DateSaveManager.getIntance().getGroup(data.groupID).groupName+"关闭",DateSaveManager.getIntance().mGuanliQun,false);
            return true;
        }else if(data.type == MessageDeal.SHANG_FEN_INT||
                data.type == MessageDeal.XIA_FEN_INT||
                data.type == MessageDeal.SHE_FEN_INT||
                data.type == MessageDeal.SHANG_LIANG_INT||
                data.type == MessageDeal.XIA_LIANG_INT||
                data.type == MessageDeal.SHE_LIANG_INT
                ){                                              //修改分量
            changeFenOrLiang(data);
            return true;
        } else if(data.type  == MessageDeal.GUAN_LI_QUN_INT &&
                !TextUtils.isEmpty(data.groupID)){//设置管理群
            DateSaveManager.getIntance().saveGuanliQun(data.groupID);
            SscControl.getIntance().sendMessage("当前群为管理群",data.groupID,false);
            return true;
        } else if(data.type  == MessageDeal.GUAN_LI_INT &&
                !TextUtils.isEmpty(data.groupID) &&
                DateSaveManager.getIntance().isGuanliQun(data.groupID)){//设置管理员
            DateSaveManager.getIntance().saveGuanLiYuan(data.TakerId);
            SscControl.getIntance().sendMessage("添加管理员成功",data.groupID,false);
            return true;
        } else if(data.type  == MessageDeal.YING_INT &&
                !TextUtils.isEmpty(data.groupID) &&
                !DateSaveManager.getIntance().isGuanliQun(data.groupID)&&
                DateSaveManager.getIntance().isHaveGroup(data.groupID)&&
                DateSaveManager.getIntance().isGuanli(data.TakerId)
                ){//设置管理员
            try {
                int fen = Integer.parseInt(data.message.replace(MessageDeal.YING_STR,""));
                DateSaveManager.getIntance().saveYin(data.groupID,fen);
                SscControl.getIntance().sendMessage("该群一吟"+fen,data.groupID,false);
            }catch (Exception e){

            }
            return true;
        }else if(data.type  == MessageDeal.RECEVI_ZU_INT &&
                DateSaveManager.getIntance().isHaveGroup(data.groupID) &&
                DateSaveManager.getIntance().isGuanli(data.TakerId) &&
                DateSaveManager.getIntance().isJustShou){
            String b = DateSaveManager.getIntance().saveTo(data.groupID, ServerManager2.getmIntance().mJieId );
            if(!TextUtils.isEmpty(b)){
                SscControl.getIntance().sendMessage(ServerManager2.getmIntance().mJieId+"设置"+DateSaveManager.getIntance().getGroup(data.groupID).groupName+"发送到本群",b,false);
            }
            return true;
        }else if(data.type  == MessageDeal.SEND_ZU_INT &&
                DateSaveManager.getIntance().isHaveGroup(data.groupID) &&
                DateSaveManager.getIntance().isGuanli(data.TakerId)&&
                DateSaveManager.getIntance().isJustShou){
           // String s = data.message.replaceFirst(MessageDeal.SEND_ZU_STR,"");

            if(DateSaveManager.getIntance().getGroup(data.groupID).getGroup != 1){
                ServerManager2.getmIntance().mJieId = DateSaveManager.getIntance().getGroup(data.groupID).getGroup;
            }else{
                ServerManager2.getmIntance().mJieId =  DateSaveManager.getIntance().mMaxJieId;
                DateSaveManager.getIntance().mMaxJieId++;
                DateSaveManager.getIntance().saveMaxJieId();
                DateSaveManager.getIntance().saveGet(data.groupID, ServerManager2.getmIntance().mJieId);

            }

            SscControl.getIntance().sendMessage(ServerManager2.getmIntance().mJieId+"本群为解收群",data.groupID,false);
            return true;
        }else if(data.type == MessageDeal.CHECK_INT &&
                DateSaveManager.getIntance().isHaveGroup(data.groupID)&&
                !DateSaveManager.getIntance().isJustShou){
            DateSaveManager.GroupDate group = DateSaveManager.getIntance().getGroup(data.groupID);
            SscControl.getIntance().sendMessage("分"+group.fen+"量"+group.liang,data.groupID,false);
            return true;
        }else if(data.type == MessageDeal.CLEAR_CHECK_INT &&
                DateSaveManager.getIntance().isGuanliQun(data.groupID)){
            DateSaveManager.getIntance().clearAllGroupFenAndLiang();
        }else if(data.type == MessageDeal.SET_JUST_SHOU_INT &&
                TextUtils.isEmpty(data.groupID)){
            DateSaveManager.getIntance().saveJustShou();
            return true;
        }else if(data.type == MessageDeal.SET_ZONG_QUN_INT &&
                !TextUtils.isEmpty(data.groupID) &&
                !DateSaveManager.getIntance().isHaveGroup(data.groupID)&&
                !DateSaveManager.getIntance().isGuanliQun(data.groupID)&&
                DateSaveManager.getIntance().isGuanli(data.TakerId)){
            DateSaveManager.getIntance().saveZong(data.groupID);
            return true;
        }else if(data.type == MessageDeal.SET_TIXING_INT &&
                !TextUtils.isEmpty(data.groupID) &&
                !DateSaveManager.getIntance().isHaveGroup(data.groupID)&&
                !DateSaveManager.getIntance().isGuanliQun(data.groupID)&&
                DateSaveManager.getIntance().isGuanli(data.TakerId)){
            DateSaveManager.getIntance().saveTiXing(data.groupID);
            return true;
         }else if(data.type == MessageDeal.SET_RESET_INT &&
                TextUtils.isEmpty(data.groupID) &&
                DateSaveManager.getIntance().isGuanli(data.TakerId)){
            DateSaveManager.getIntance().clearAll();
            return true;
        }
        return false;
    }


    private static void saveGroupName(MessageDeal.MessagerDealDate data){
        String newName = data.message.replace(MessageDeal.SET_QUN_QUN_NAME,"");
        if(DateSaveManager.getIntance().isHaveGroup(data.groupID)){
            DateSaveManager.getIntance().saveGroupName(data.groupID,newName);
        }else{
            DateSaveManager.getIntance().saveGroup(data.groupID);
            DateSaveManager.getIntance().saveGroupName(data.groupID,newName);
        }
        SscControl.getIntance().sendMessage(newName+"注册成功",DateSaveManager.getIntance().mGuanliQun,false);
    }

    private static void changeFenOrLiang(MessageDeal.MessagerDealDate data){
        if(TextUtils.isEmpty(data.groupID)||
                !DateSaveManager.getIntance().isGuanli(data.TakerId)||
                !DateSaveManager.getIntance().isHaveGroup(data.groupID)){
            return;
        }
        boolean isFen = false;
        int isAdd = 1;//1为加2为减3为设
        String friStr = "";
        if(data.type == MessageDeal.SHANG_FEN_INT){
            isFen = true;
            isAdd = 1;
            friStr = MessageDeal.SHANG_FEN_STR;
        }else if(data.type == MessageDeal.XIA_FEN_INT){
            isFen = true;
            isAdd = 2;
            friStr = MessageDeal.XIA_FEN_STR;
        }else if(data.type == MessageDeal.SHE_FEN_INT){
            isFen = true;
            isAdd = 3;
            friStr = MessageDeal.SHE_FEN_STR;
        }else if(data.type == MessageDeal.SHANG_LIANG_INT){
            isFen = false;
            isAdd = 1;
            friStr = MessageDeal.SHANG_LIANG_STR;
        }else if(data.type == MessageDeal.XIA_LIANG_INT){
            isFen = false;
            isAdd = 2;
            friStr = MessageDeal.XIA_LIANG__STR;
        }else if(data.type == MessageDeal.SHE_LIANG_INT){
            isFen = false;
            isAdd = 3;
            friStr = MessageDeal.SHE_LIANG__STR;
        }

        int fen =0;
        try {
            fen = Integer.parseInt(data.message.replace(friStr,""));
        }catch (Exception e){

            return;
        }
        DateSaveManager.getIntance().changeGroupFenOrLiang(data.groupID,isFen,isAdd,fen);
        DateSaveManager.GroupDate date = DateSaveManager.getIntance().getGroup(data.groupID);
        SscControl.getIntance().sendMessage("当前分"+date.fen+" 量"+date.liang,data.groupID,false);
    }
}
