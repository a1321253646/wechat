package jackzheng.study.com.wechat.sscManager;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.robv.android.xposed.XposedBridge;
import jackzheng.study.com.wechat.DebugLog;
import jackzheng.study.com.wechat.HookUtils;
import jackzheng.study.com.wechat.HtmlParse;
import jackzheng.study.com.wechat.MessageDeal;
import jackzheng.study.com.wechat.StroeAdateManager;
import jackzheng.study.com.wechat.regular.DateBean;
import jackzheng.study.com.wechat.regular.DateBean2;
import jackzheng.study.com.wechat.regular.RegularUtils;
import jackzheng.study.com.wechat.regular.RegularUtils2;

public class ServerManager {


    Map <String, ArrayList<Sscbean>> mAllData = new HashMap<>();
   ArrayList<SscBeanWithUser> mErrorList = new ArrayList<>();

    boolean isTime;

    public void receiveMessage(String str,String userId,String group){
        MessageDeal.MessagerDealDate data = MessageDeal.getMessageDealData(str,userId,group);
        StringBuilder build = new StringBuilder();
        build.append("\n---------------------------------------------------\n"+"message = "+str+" user id = "+userId);
        if(data == null){
            return;
        }
        if(data.type == MessageDeal.QUN_NAME_INT && !TextUtils.isEmpty(data.groupID)){//注册群：必须是管理员在群里发送命令
            data.message = data.message.replace(MessageDeal.QUN_NAME_STR,"");
            StroeAdateManager.getmIntance().addGroup(data.message,data.groupID);
            sendMessageToGuanli(data.message+"已经进行注册,请留意它的开/关操作");
        }else if(data.type == MessageDeal.QUN_KAI_INT && !TextUtils.isEmpty(StroeAdateManager.getmIntance().getmGuanliId())
                && data.TakerId.equals(StroeAdateManager.getmIntance().getmGuanliId())){//使能群:：必须是管理员在群里发送的命令
            StroeAdateManager.getmIntance().setGroupEnable(data.groupID,true);
            sendMessageToGuanli(data.message+"您已开启"+StroeAdateManager.getmIntance().getGroupDatById(data.groupID).name+"");
            sendMessage(data.groupID,"已开启\"捅记");
        }else if(data.type == MessageDeal.QUN_QIAN_INT && !TextUtils.isEmpty(StroeAdateManager.getmIntance().getmGuanliId())
                && data.TakerId.equals(StroeAdateManager.getmIntance().getmGuanliId())){//关闭群：：必须是管理员在群里发送的命令
            StroeAdateManager.getmIntance().setGroupEnable(data.groupID,false);
            sendMessageToGuanli(data.message+"您已关闭"+StroeAdateManager.getmIntance().getGroupDatById(data.groupID).name+"");
            sendMessage(data.groupID,"已关闭捅记");
        }else if(data.type == MessageDeal.GUAN_LI_INT && TextUtils.isEmpty(data.groupID)){//注册管理员：管理员私发命令
            StroeAdateManager.getmIntance().setmGuanliId(data.TakerId);
            sendMessageToGuanli("你已经是这个的管理员，请查看命令");
        }else if(data.type == MessageDeal.SP_GL_INT && TextUtils.isEmpty(data.groupID)){//注册管理员：管理员私发命令
            StroeAdateManager.getmIntance().setmSPGuanliId(data.TakerId);
            sendMessageToGuanli("你已经是这个的仓管，请查看命令");
        }else  if(data.type == MessageDeal.SHE_FEN_INT && !TextUtils.isEmpty(StroeAdateManager.getmIntance().getmGuanliId())
                && data.TakerId.equals(StroeAdateManager.getmIntance().getmGuanliId()) && !TextUtils.isEmpty(data.groupID)){
            build.append("设分："+str);
            String fenStr = data.message.replace(MessageDeal.SHE_FEN_STR,"");
            try {
                Integer fen = Integer.parseInt(fenStr);
                if(fen !=null){
                    StroeAdateManager.getmIntance().setFen(data.groupID,fen);
                    StroeAdateManager.GroupData groupData= StroeAdateManager.getmIntance().getGroupDatById(data.groupID);
                    sendMessage(data.groupID," 剩下共 "+groupData.fen);
                    //   sendMessageToGuanli(groupData.name+"尚芬"+fen+" 剩下共 "+groupData.fen);
                }
            }catch (Exception e){

            }
        }else  if(data.type == MessageDeal.SHANG_FEN_INT && !TextUtils.isEmpty(StroeAdateManager.getmIntance().getmGuanliId())
                && data.TakerId.equals(StroeAdateManager.getmIntance().getmGuanliId()) && !TextUtils.isEmpty(data.groupID)){
            build.append("上分："+str);
            String fenStr = data.message.replace(MessageDeal.SHANG_FEN_STR,"");
            try {
                Integer fen = Integer.parseInt(fenStr);
                if(fen !=null){
                    StroeAdateManager.getmIntance().changeFen(data.groupID,fen);
                    StroeAdateManager.GroupData groupData= StroeAdateManager.getmIntance().getGroupDatById(data.groupID);
                    sendMessage(data.groupID,"尚芬"+fen+" 剩下共 "+groupData.fen);
                 //   sendMessageToGuanli(groupData.name+"尚芬"+fen+" 剩下共 "+groupData.fen);
                }
            }catch (Exception e){

            }

        }else  if(data.type == MessageDeal.XIA_FEN_INT && !TextUtils.isEmpty(StroeAdateManager.getmIntance().getmGuanliId())
                && data.TakerId.equals(StroeAdateManager.getmIntance().getmGuanliId()) && !TextUtils.isEmpty(data.groupID)){
            build.append("下分："+str);
            String fenStr = data.message.replace(MessageDeal.XIA_FEN_STR,"");
            try {
                Integer fen = Integer.parseInt(fenStr);
                if(fen !=null){
                    StroeAdateManager.getmIntance().changeFen(data.groupID,-fen);
                    StroeAdateManager.GroupData groupData= StroeAdateManager.getmIntance().getGroupDatById(data.groupID);
                    sendMessage(data.groupID,"夏芬"+fen+" 剩下共 "+groupData.fen);
                    //sendMessageToGuanli(groupData.name+"夏芬"+fen+" 剩下共 "+groupData.fen);
                }
            }catch (Exception e){

            }
        }else if(data.type == MessageDeal.TING_INT && !TextUtils.isEmpty(StroeAdateManager.getmIntance().getmSPGuanliId())
                && data.TakerId.equals(StroeAdateManager.getmIntance().getmSPGuanliId()) && TextUtils.isEmpty(data.groupID)){
            build.append("暂停下注"+str);
            setEnable(false);
        }else if(data.type == MessageDeal.KAISHI_INT && !TextUtils.isEmpty(StroeAdateManager.getmIntance().getmGuanliId())
                && data.TakerId.equals(StroeAdateManager.getmIntance().getmGuanliId()) && TextUtils.isEmpty(data.groupID)){
            build.append("开始下注"+str);
            setEnable(true);
        }else if(data.type == MessageDeal.YING_INT && !TextUtils.isEmpty(StroeAdateManager.getmIntance().getmGuanliId())
                && data.TakerId.equals(StroeAdateManager.getmIntance().getmGuanliId()) && !TextUtils.isEmpty(data.groupID)){
            XposedBridge.log("设置赔率："+str);
            build.append("设置赔率："+str);
            String pei = data.message.replace(MessageDeal.YING_STR,"");
            try {
                Integer fen = Integer.parseInt(pei);
                if(fen !=null){
                    StroeAdateManager.getmIntance().setPei(data.groupID,fen);
                    StroeAdateManager.GroupData groupData= StroeAdateManager.getmIntance().getGroupDatById(data.groupID);
                    sendMessage(data.groupID,"设置为："+fen);
                   // sendMessageToGuanli(groupData.name+"设置为："+fen);
                }
            }catch (Exception e){

            }
        }else if(data.type == MessageDeal.KAIJIANG_INT && !TextUtils.isEmpty(StroeAdateManager.getmIntance().getmGuanliId())
                && data.TakerId.equals(StroeAdateManager.getmIntance().getmGuanliId()) && TextUtils.isEmpty(data.groupID)){
            String num = data.message.replace(MessageDeal.KAIJIANG_STR,"");
            build.append("开奖："+str);
//            if(!TextUtils.isEmpty(num)&& isFiveNumber(num)){
//                build.append(" 号码为："+num);
//                announceByMessage(num,999);
//            }else{
//                build.append(" 格式错误");
//                sendMessageToCheck(null,str+" 格式错误",-1);
//            }
        }else if(data.type == MessageDeal.TUI_INT && !TextUtils.isEmpty(StroeAdateManager.getmIntance().getmGuanliId())
               /* && data.TakerId.equals(StroeAdateManager.getmIntance().getmGuanliId()) */&& !TextUtils.isEmpty(data.groupID)){
            build.append("删除下注："+str);
            String message = data.message.replace(MessageDeal.TUI_STR,"");
            boolean suuccess = deleteMessage(data.groupID,message);
            sendMessage(data.groupID,"推："+message+(suuccess?" 成功":" 失败"));
            build.append(" "+suuccess);
        }else if(data.type == MessageDeal.CHANGE_INT && !TextUtils.isEmpty(StroeAdateManager.getmIntance().getmGuanliId())
                && data.TakerId.equals(StroeAdateManager.getmIntance().getmGuanliId()) && TextUtils.isEmpty(data.groupID)){
            build.append("修改下注："+str);
            try {
                data.message = data.message.replace(MessageDeal.CHANGE_STR,"");
                String id = data.message.split(":")[0];
                data.message = data.message.replace(id+":","");
                Integer fen = Integer.parseInt(id);
                if(fen !=null){
                    messageCheck(fen, data.message );
                }
            }catch (Exception e){

            }
        }else if(haveNumber(str) && !TextUtils.isEmpty(StroeAdateManager.getmIntance().getmGuanliId())&& ! TextUtils.isEmpty(data.groupID)){
            StroeAdateManager.GroupData groupData= StroeAdateManager.getmIntance().getGroupDatById(data.groupID);
            if(groupData == null){
                build.append("该群未注册");
                return;
            }
            if(!isTime || !groupData.isEnable){
                build.append(groupData.name+" 下注：isTime ="+isTime+" isenable="+groupData.isEnable);
                return;
            }
            build.append("下注："+str);
            saveMassege(group,str);
        }
        XposedBridge.log(build.toString());
        build.append("\n------------------------------------------------\n");
        DebugLog.saveLog(build.toString());
    }


    public void setFalseByAuto(int index){
        isTime = false;
        String strBase =" "+index+"欺停止瞎足-----------------\n" ;
        Map<String, StroeAdateManager.GroupData> groupDate = StroeAdateManager.getmIntance().getGroupDate();
        if(groupDate.size() > 0){
            Set<String> strings = groupDate.keySet();
            ArrayList<Sscbean> data=null;
            int count = 0;
            for(String s :strings){
                count = 0;
                if(groupDate.get(s).isEnable){
                    data = mAllData.get(s);
                    count = getGroupDeal(data);
                    sendMessage(s,strBase+index+" 欺共瞎足"+count+",剩余"+StroeAdateManager.getmIntance().getGroupDatById(s).fen);
                }
            }
        }
    }

    private int getGroupDeal( ArrayList<Sscbean> data){
        int count= 0;
        if(data != null && data.size() > 0 ){
            for(Sscbean bean : data){
                if(bean.mList != null && bean.mList.size() > 0){
                    for(DateBean2 tmp : bean.mList){
                        count += tmp.allCount;
                    }
                }
            }
        }else{
            count = 0;
        }
        return count;
    }


    private void setEnable(boolean isEnable){
        isTime = isEnable;
        sendEnableInfoToAll();
    }

    private void sendEnableInfoToAll(){
        String str;
        if(isTime){
            str = "暂停一小时，不便之处请见谅";
        }else{
            str = "谢谢大家的谅解，现在继续";
        }
        Map<String, StroeAdateManager.GroupData> groupDate = StroeAdateManager.getmIntance().getGroupDate();
        if(groupDate.size() > 0){
            Set<String> strings = groupDate.keySet();
            for(String s :strings){
                if(groupDate.get(s).isEnable){
                    sendMessage(groupDate.get(s).id,str);
                }
            }
        }
    }


    /**
     * 对外发送中奖结果
     * @param map
     */
    private void sendMessageToSuccess(String str,Map<String,Integer> map,int index){
        //对于的群id中奖的数目
        Set<String> ids = map.keySet();
        for(String id :ids){
            StroeAdateManager.GroupData data = StroeAdateManager.getmIntance().getGroupDatById(id);
            if(data != null && data.isEnable){
                int count = map.get(id);
                int menoy = count * data.pei;
                StroeAdateManager.getmIntance().changeFen(id,menoy);
                sendMessage(id,index+" 欺开 "+str+" 重："+count+"祝"+" 上芬 ："+menoy+" 剩余："+data.fen+
                "\n---------------------------------------\n"+
                        (index+1)+" 欺开始瞎足");
            }
        }
        mAllData.clear();
        mErrorList.clear();
    }

    /**
     * 对外发送格式不符合的下注
     * @param bean 元数据
     * @param error 错误的原因
     * @param id 对应该字符串的id
     */
    private void sendMessageToCheck(Sscbean bean,String error,int id){
        sendMessageToGuanli("id:"+bean.getId()+":"+bean.message);
    }

    private void sendMessageToGuanli(String str){
        sendMessage(StroeAdateManager.getmIntance().getmGuanliId(),str);
    }

    private void sendMessage(String userId,String str){
        if(HookUtils.getIntance() != null){
            XposedBridge.log("userid:"+userId+" str="+str);
            HookUtils.getIntance().sendMeassageBy(userId,str);
        }
    }

    private boolean isFiveNumber(String str){
        char[] chars = str.toCharArray();
        if(chars.length == 5){
            for(char c: chars){
                if(c < '0' || c > '9'){
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    private boolean haveNumber(String str){
        char[] chars = str.toCharArray();
        for(char c: chars){
            if(c >= '0' && c<= '9'){
                return true;
            }
        }
        return false;
    }

    /**
     * 保存下注数据
     * @param userId 用户的id
     * @param message 下注内容
     */
    public void saveMassege(String userId,String message){
        saveMassege(userId,message,null);
    }
    public void saveMassege(String userId,String message,String oldMessage){

        StringBuilder build = new StringBuilder();
        ArrayList<Sscbean> userData ;
        if(mAllData.containsKey(userId)){
            userData = mAllData.get(userId);
        }else{
            userData = new ArrayList<>();
            mAllData.put(userId,userData);
        }
        Sscbean bean =new  Sscbean(message);
        if(!TextUtils.isEmpty(oldMessage)){
            bean.message = oldMessage;
        }
        bean.mList = RegularUtils2.regularStr(message);
        if(bean.mList != null){
            userData.add(bean);
            xiazjianfen(bean,userId);
            build.append("    "+bean.toString());
            StringBuilder builder2 = new StringBuilder();
            builder2.append(message+"\n");
            for(DateBean2 date : bean.mList){
                builder2.append("解析："+date.toString()+"\n");
            }
            sendMessage(userId,builder2.toString());
        }else{
            SscBeanWithUser error = new SscBeanWithUser(bean,userId);
            mErrorList.add(error);
            build.append("格式不对");
            sendMessageToCheck(bean,"格式不对",-1);
        }
        XposedBridge.log(build.toString());
        DebugLog.saveLog(build.toString());
    }


    private void xiazjianfen(Sscbean bean,String groupId){
        if(bean.mList !=null && bean.mList.size() >0){
            int count = 0;
            for(DateBean2 data : bean.mList){
                count+= data.allCount;
            }
            StroeAdateManager.getmIntance().changeFen(groupId,-count);
        }
    }

    /**
     * 接受矫正后的下注内容
     * @param id 修正的id
     * @param str 修正后的内容
     */
    private void messageCheck(int id,String str){
        for(int i = 0;i <mErrorList.size();i++){
            SscBeanWithUser bean = mErrorList.get(i);
            if(bean.bean.getId() == id){
                mErrorList.remove(i);
                saveMassege(bean.user,str,bean.bean.message);
                return;
            }
        }
        sendMessageToCheck(null,"id错误",id);
    }

    public void announceByAuto(String str,int nowIndex, int max){
        XposedBridge.log("announceByAuto str = "+str+" nowIndex"+nowIndex+" max ="+max);
        if(nowIndex - max >1){
            Map<String, StroeAdateManager.GroupData> groupDate = StroeAdateManager.getmIntance().getGroupDate();
            if(groupDate.size() > 0){
                Set<String> strings = groupDate.keySet();
                ArrayList<Sscbean> data=null;
                int count = 0;
                for(String s :strings){
                    count = 0;
                    if(groupDate.get(s).isEnable){
                        data = mAllData.get(s);
                        count = getGroupDeal(data);
                        StroeAdateManager.getmIntance().changeFen(s,count);

                        sendMessage(s,max+"欺开奖失败 下注作废"+
                                "\n-----------------\n"+
                                nowIndex+"欺开奖"+str+
                                "\n-----------------\n"+
                                (nowIndex+1)+"欺开始瞎住");
                    }
                }
            }
            mAllData.clear();
            mErrorList.clear();
            isTime = true;
        }else{
            announceByMessage(str,max );
        }
    }

    /**
     *  处理开奖
     * @param str=为开奖的五位号码字符串
     */
   public void announceByMessage(String str,int index){
        char[] number = str.toCharArray();
        Map<String,Integer>  successCountUser = new HashMap<>();
        int [] targetNumber = new int[5];
        for(int i= 0;i <5;i++){
            targetNumber[i] = number[i]-'0';
        }
        Set<String> users = mAllData.keySet();
        ArrayList<Sscbean> sscbeans = null;

       Map<String, StroeAdateManager.GroupData> groupDate = StroeAdateManager.getmIntance().getGroupDate();
       if(groupDate !=null && groupDate.size() >0){
           Set<String> groupKeys = groupDate.keySet();
           for(String groupkey :groupKeys){
               XposedBridge.log("announceByMessage users = "+users);
               if(mAllData.containsKey(groupkey)){
                   sscbeans = mAllData.get(groupkey);
                   int count =0;
                   if(sscbeans != null && sscbeans.size() > 0){
                       for(Sscbean bean : sscbeans){
                           if(bean != null && bean.mList != null && bean.mList.size() >0){
                               for(DateBean2 data : bean.mList){
                                   if(data.mLastData != null && data.mLastData.size() >0 && data.local != null && data.local.size() >0){
                                       for(int i = 0; i<data.local.size();i++){
                                           for(Integer[] num : data.mLastData){
                                               if(num[0] == targetNumber[data.local.get(i)[0]-1] && num[1] == targetNumber[data.local.get(i)[1]-1]){
                                                   count += data.mCountList.get(i);
                                               }
                                           }
                                       }
                                   }
                               }
                           }
                       }
                   }
                   successCountUser.put(groupkey,count);
               }else if(groupDate.get(groupkey).isEnable){
                   successCountUser.put(groupkey,0);
               }
           }
       }
       sendMessageToSuccess(str,successCountUser,index);
       isTime = true;
   }
    /**
     * 撤销下注内容
     * @param userId
     * @param str
     * @return
     */
    private boolean deleteMessage(String userId,String str){
        ArrayList<Sscbean> userData  =null;
        if(mAllData.containsKey(userId)){
            userData = mAllData.get(userId);
        }
        if(userData == null){
            return false;
        }
        for(int i = 0; i < userData.size();i++){
            if( userData.get(i).message.equals(str)){
                Sscbean bean= userData.get(i);
                int count = bean.getCount();
                StroeAdateManager.getmIntance().changeFen(userId,count);
                userData.remove(i);
                return true;
            }
        }
        return false;
    }

    private static ServerManager mIntance = new ServerManager();
    public static ServerManager getIntance(){
        return mIntance;
    }

    private ServerManager(){
        isTime = true;
        StroeAdateManager.getmIntance();
    }

}
