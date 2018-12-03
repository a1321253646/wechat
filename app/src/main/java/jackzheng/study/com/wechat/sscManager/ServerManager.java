package jackzheng.study.com.wechat.sscManager;

import android.os.Handler;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import de.robv.android.xposed.XposedBridge;
import jackzheng.study.com.wechat.HookUtils;
import jackzheng.study.com.wechat.MessageDeal;
import jackzheng.study.com.wechat.NetManager;

public class ServerManager {

    boolean isTime;

    private boolean qunGuanLiCommon( MessageDeal.MessagerDealDate data){//普通群中的管理员命令
        if(data.type == MessageDeal.QUN_NAME_INT){//注册群：必须是管理员在群里发送命令
            XposedBridge.log("31");
            data.message = data.message.replace(MessageDeal.QUN_NAME_STR,"");
            NetManager.getIntance().addGroup(data.message,data.groupID);
           // sendMessageToGuanli(data.message+"已经进行注册,请留意它的开/关操作");
            return true;
        }else if(data.type == MessageDeal.QUN_KAI_INT ){//使能群:：必须是管理员在群里发送的命令
            XposedBridge.log("32");
            NetManager.getIntance().setGroupEnable(data.groupID,true);
         //   sendMessageToGuanli(data.message+"您已开启"+ NetManager.getIntance().getGroupDatById(data.groupID).name+"");
          //  sendMessage(data.groupID,"已开启\"捅记");
            return true;
        }else if(data.type == MessageDeal.QUN_QIAN_INT ){//关闭群：：必须是管理员在群里发送的命令
            XposedBridge.log("33");
            NetManager.getIntance().setGroupEnable(data.groupID,false);
      //      sendMessageToGuanli(data.message+"您已关闭"+NetManager.getIntance().getGroupDatById(data.groupID).name+"");
        //    sendMessage(data.groupID,"已关闭捅记");
            return true;
        }else if(data.type == MessageDeal.RECEVI_ZU_INT ){
            XposedBridge.log("34");
            String fenStr = data.message.replace(MessageDeal.RECEVI_ZU_STR,"");
            if(!TextUtils.isEmpty(fenStr)){
                NetManager.getIntance().saveGroupKeyDate(fenStr,data.groupID,2);
    //            sendMessage(data.groupID,"该裙为"+fenStr+"接受裙");
            }
            return true;
        }else if(data.type == MessageDeal.SEND_ZU_INT ){
            XposedBridge.log("35");
            String fenStr = data.message.replace(MessageDeal.SEND_ZU_STR,"");
            if(!TextUtils.isEmpty(fenStr)){
                NetManager.getIntance().saveGroupKeyDate(fenStr,data.groupID,1);
       //         sendMessage(data.groupID,"该裙为"+fenStr+"发送裙");
            }
            return true;
        }else if(data.type == MessageDeal.QUAN_ZHUANG_INT ){
            XposedBridge.log("36");
            String fenStr = data.message.replace(MessageDeal.QUAN_ZHUANG_STR,"");
            if(!TextUtils.isEmpty(fenStr)){
                NetManager.getIntance().saveGroupTypeDate(data.groupID,fenStr,1);
     //           sendMessage(data.groupID,"该裙被"+fenStr+"砖马裙");
            }
            return true;
        }else if(data.type == MessageDeal.QUAN_SHOU_INT ){
            XposedBridge.log("37");
            String fenStr = data.message.replace(MessageDeal.QUAN_SHOU_STR,"");
            if(!TextUtils.isEmpty(fenStr)){
                NetManager.getIntance().saveGroupTypeDate(data.groupID,fenStr,3);
      //          sendMessage(data.groupID,"该裙为"+fenStr+"收马裙");
            }
            return true;
        }else if(data.type == MessageDeal.STOP_PARSE_INT){
            XposedBridge.log("38");
            NetManager.getIntance().stopParseGroup(data.groupID,true);
           // sendMessage(data.groupID,"该裙不发送解析信息");
             return true;
        }else if(data.type == MessageDeal.STAART_PARSE_INT){
            XposedBridge.log("39");
            NetManager.getIntance().stopParseGroup(data.groupID,false);
          //  sendMessage(data.groupID,"该裙将重新发送解析信息");
            return true;
        }else if(data.type == MessageDeal.SHE_XIAN_INT){
            XposedBridge.log("340");
            String fenStr = data.message.replace(MessageDeal.SHE_XIAN_STR,"");
            try {
                Integer allInt = Integer.parseInt(fenStr);
                if(allInt !=null){
                    NetManager.getIntance().shexianGroup(data.groupID,allInt);
        //            sendMessage(data.groupID,"该裙每一株最高为"+fenStr);
                }
            }catch (Exception e){

            }
            return true;
        }
        XposedBridge.log("41");
        return  false;
    }
    private boolean qunFuzherenCommon(MessageDeal.MessagerDealDate data){//普通群中的负责员命令
        if(data.type == MessageDeal.SHANG_FEN_INT ){
            String fenStr = data.message.replace(MessageDeal.SHANG_FEN_STR,"");
            try {
                Integer fen = Integer.parseInt(fenStr);
                if(fen !=null){
                    NetManager.getIntance().changeFen(data.groupID,fen);
                    NetManager.GroupData groupData= NetManager.getIntance().getGroupDatById(data.groupID);
            //        sendMessage(data.groupID,"尚芬"+fen+" 剩下共 "+groupData.fen);

                }
            }catch (Exception e){

            }
            return true;
        }else  if(data.type == MessageDeal.SHE_FEN_INT){
            String fenStr = data.message.replace(MessageDeal.SHE_FEN_STR,"");
            try {
                Integer fen = Integer.parseInt(fenStr);
                if(fen !=null){
                    NetManager.getIntance().setFen(data.groupID,fen);
                    NetManager.GroupData groupData= NetManager.getIntance().getGroupDatById(data.groupID);
            //        sendMessage(data.groupID," 剩下共 "+groupData.fen);
                }
            }catch (Exception e){

            }
            return true;
        }else if(data.type == MessageDeal.SET_CHECK_INT){//设量
            String all = data.message.replace(MessageDeal.SET_CHECK_STR,"");
            try {
                Integer allInt = Integer.parseInt(all);
                if(allInt !=null){
                    NetManager.getIntance().setAllForGroup(data.groupID,allInt);
                    NetManager.GroupData groupData= NetManager.getIntance().getGroupDatById(data.groupID);
           //         sendMessage(data.groupID," 今天量设置为 "+groupData.all);
                }
            }catch (Exception e){

            }
            return true;
        }else if(data.type == MessageDeal.YING_INT){
            String pei = data.message.replace(MessageDeal.YING_STR,"");
            try {
                Integer fen = Integer.parseInt(pei);
                if(fen !=null){
                    NetManager.getIntance().setPei(data.groupID,fen);
                    NetManager.GroupData groupData= NetManager.getIntance().getGroupDatById(data.groupID);
             //       sendMessage(data.groupID,"设置为："+fen);
                    // sendMessageToGuanli(groupData.name+"设置为："+fen);
                }
            }catch (Exception e){

            }
            return true;
        }else  if(data.type == MessageDeal.XIA_FEN_INT){
            String fenStr = data.message.replace(MessageDeal.XIA_FEN_STR,"");
            try {
                Integer fen = Integer.parseInt(fenStr);
                if(fen !=null){
                    NetManager.getIntance().changeFen(data.groupID,-fen);
                    NetManager.GroupData groupData= NetManager.getIntance().getGroupDatById(data.groupID);
                //    sendMessage(data.groupID,"夏芬"+fen+" 剩下共 "+groupData.fen);
                    //sendMessageToGuanli(groupData.name+"夏芬"+fen+" 剩下共 "+groupData.fen);
                }
            }catch (Exception e){

            }
            return true;
        }
        return false;
    }

    private boolean commomPersonCommon(MessageDeal.MessagerDealDate data) {
        if (data.type == MessageDeal.CHECK_INT) {//查量
            NetManager.GroupData groupData = NetManager.getIntance().getGroupDatById(data.groupID);
            sendMessage(data.groupID, "今天共吓： " + groupData.all);
            return true;
        } else if (data.type == MessageDeal.CHECK_FEN_INT ) {//查分
            NetManager.GroupData groupData = NetManager.getIntance().getGroupDatById(data.groupID);
            sendMessage(data.groupID, "剩余： " + groupData.fen);
            return true;
        }else if(data.type == MessageDeal.TUI_INT && isTime){
            String message = data.message.replace(MessageDeal.TUI_STR,"");
            try {

                Integer fen = Integer.parseInt(message);
                NetManager.getIntance().deleteXiazu(data.groupID,fen);
            }catch (Exception e){

            }

            return true;
        }
        return false;
    }

    public void receiveMessage(String str,String userId,String group){
        if(!TextUtils.isEmpty(str)
                && str.contains("期开")
                && NetManager.getIntance().getGroupDatById(group) != null
                && NetManager.getIntance().getGroupDatById(group).type ==1){
            return;
        }
        if(TextUtils.isEmpty(NetManager.getIntance().mGuanliQunID)){
            return;
        }
        if( NetManager.getIntance().getGroupDatById(group) == null && !NetManager.getIntance().mGuanliQunID.equals(group)){
            NetManager.getIntance().autoAddGroup(group);
        }
        MessageDeal.MessagerDealDate data = MessageDeal.getMessageDealData(str,userId,group);
        StringBuilder build = new StringBuilder();
        build.append("\n---------------------------------------------------\n"+"message = "+str+" user id = "+userId+" group= "+group);
        if(data == null){
            return;
        }
        if(data.type ==  MessageDeal.ZHUANG_MING_LING_INT && NetManager.getIntance().mGuanliQunID.equals(data.groupID) ){
            data.message = data.message.replaceFirst(MessageDeal.ZHUANG_MING_LING,"");
            String[] strs =  data.message.split("令");
            if(strs.length == 2){
                NetManager.GroupData groupDateByName = NetManager.getIntance().getGroupDateByName(strs[0]);
                if(groupDateByName == null || TextUtils.isEmpty(groupDateByName.name)){
                    sendMessage(NetManager.getIntance().mGuanliQunID,"群名不存在",true,false);
                }else{
                    data = MessageDeal.getMessageDealData(strs[1],userId,groupDateByName.id);
                    if(data == null){
                        sendMessage(NetManager.getIntance().mGuanliQunID,"命令出错",true,false);
                        return;
                    }
                }

            }else{
                sendMessage(NetManager.getIntance().mGuanliQunID,"命令出错",true,false);
            }
        }

       XposedBridge.log("\n---------------------------------------------------\n"+"mType = "+NetManager.getIntance().mType
                +" groupID = "+NetManager.getIntance().getGroupDatById(data.groupID));
        XposedBridge.log("");
        if( NetManager.getIntance().mType != 3
                && NetManager.getIntance().getGroupDatById(data.groupID) != null
                && NetManager.getIntance().getGroupDatById(data.groupID).type !=3
                && NetManager.getIntance().getGroupDatById(data.groupID).type !=NetManager.getIntance().mType){
            XposedBridge.log("1");
            return;
        }
        if(data.groupID != null&& data.groupID.equals(NetManager.getIntance().getGuanliQunId())){
            if(guanLiQunGuanLiCommon(data)){
                XposedBridge.log("2");
                return;
            }
        }
        if(  data.groupID != null&& !data.groupID.equals(NetManager.getIntance().getGuanliQunId())){
            if(NetManager.getIntance().isGuanliYuan(data.TakerId)){
                if(qunGuanLiCommon(data)){
                    XposedBridge.log("3");
                    return;
                }else if(qunFuzherenCommon(data)){
                    XposedBridge.log("4");
                    return;
                }
            }
            if(isInit && commomPersonCommon(data)){
                XposedBridge.log("5");
                return;
            }
        }
        XposedBridge.log("NetManager.getIntance().getGuanliQunId() = "+NetManager.getIntance().getGuanliQunId());
        XposedBridge.log("data.groupID = "+data.groupID);
        XposedBridge.log("NetManager.getIntance().getGuanliQunId() = "+NetManager.getIntance().getGuanliQunId());
        XposedBridge.log("isRobot(data.TakerId) = "+isRobot(data.TakerId));
        XposedBridge.log("isInit  = "+isInit);
        if(data.type == MessageDeal.GUAN_LI_QUN_INT && !TextUtils.isEmpty(data.groupID)){//管理群信息，已经有群记录的进行发自己身份ID，没有的等待5s进行确认身份ID
/*
            String quunId = NetManager.getIntance().getGuanliQunId();
            if(TextUtils.isEmpty(quunId) || mMyId == -1){
                //NetManager.getIntance().setGuanliqunID(data.groupID);
                initShengfen();
            }else{
                final String id = data.groupID;
                final String idstr = MessageDeal.SEND_SHENFEN+mMyId;
            }
*/
            XposedBridge.log("6");
        }else if(data.type == MessageDeal.CHANGE_INT && !TextUtils.isEmpty(data.groupID)){
            XposedBridge.log("7");
            try {
                data.message = data.message.replace(MessageDeal.CHANGE_STR,"");
                String[] spiles =  data.message.split("改");
                String id = spiles[0];
                String newMessage =spiles[1];
                Integer fen = Integer.parseInt(id);
                NetManager.getIntance().changeXiazu(newMessage,fen,data.groupID);
            }catch (Exception e){

            }
        }else if(haveNumber(str) && ! TextUtils.isEmpty(NetManager.getIntance().getGuanliQunId()) &&
                ! TextUtils.isEmpty(data.groupID) &&!data.groupID.equals(NetManager.getIntance().getGuanliQunId()) &&
                !isRobot(data.TakerId) && isInit){
            XposedBridge.log("8");
            NetManager.GroupData groupData= NetManager.getIntance().getGroupDatById(data.groupID);
            if(groupData == null){
                XposedBridge.log("该群未注册");
                return;
            }
            XposedBridge.log("groupData == null");
            if(  !groupData.isEnable){
                XposedBridge.log(" !groupData.isEnable");
                XposedBridge.log(groupData.name+" 下注：isTime ="+isTime+" isenable="+groupData.isEnable);
                return;
            }
            XposedBridge.log("groupData.isEnable");
            if(!isTime){
                XposedBridge.log("!isTime");
                sendMessage(data.groupID,str+" 无效");
                return ;
            }
            XposedBridge.log("isTime");
            if(NetManager.getIntance().mType == 0){
                XposedBridge.log("NetManager.getIntance().mType == 0");
                sendMessage(data.groupID,"未设置身份");
                return;
            }/*else  if(NetManager.getIntance().mType == 1){
                String rec = NetManager.getIntance().getReceviDate(data.groupID);
                if(!TextUtils.isEmpty(rec) && NetManager.getIntance().getGroupDatById(rec).isEnable){
                    sendMessage(rec,data.message,false,false);

                }

                return;
            }*/
            XposedBridge.log("NetManager.getIntance().mType != 0");
            build.append("下注："+str);
            NetManager.getIntance().xiazu(str,group);
        }
        XposedBridge.log(build.toString());

        build.append("\n------------------------------------------------\n");
        //DebugLog.saveLog(build.toString());
    }

    public void setTrueByDayStrartNoNotification(){
        isTime = true;
    }
    public void setFalseByDayEndNoNotification(){
        isTime = false;
    }

    public void setTrueByDayStrart(){
        isTime = true;
        String strBase ="024期下注开始" ;
        Map<String, NetManager.GroupData> groupDate = NetManager.getIntance().getGroupDate();
        if(groupDate.size() > 0){
            Set<String> strings = groupDate.keySet();
            for(String s :strings){
                if(groupDate.get(s).isEnable){
                    sendMessage(s,strBase);
                }
            }
        }

    }
    public void setFalseByDayEnd(){
        isTime = false;
        String strBase ="今天下注结束 ";
        Map<String, NetManager.GroupData> groupDate = NetManager.getIntance().getGroupDate();
        if(groupDate.size() > 0){
            Set<String> strings = groupDate.keySet();
            for(String s :strings){
                if(groupDate.get(s).isEnable){
                    if(groupDate.get(s).isStopParse){
                        sendMessage(s,strBase);
                    }else {
                        sendMessage(s+"\n凌晨3点将进行清芬清量，请提前做好统计",strBase);
                    }

                }
            }
        }
    }

    private void setEnable(boolean isEnable){
        if(!isEnable){
            sendMessageToGuanli("已经手动停止下注");
        }else
        {
            sendMessageToGuanli("已经手动开始下注");
        }

    }

    private void sendEnableInfoToAll(){
        String str;
        if(isTime){
            str = "暂停一小时，不便之处请见谅";
        }else{
            str = "谢谢大家的谅解，现在继续";
        }
        Map<String, NetManager.GroupData> groupDate = NetManager.getIntance().getGroupDate();
        if(groupDate.size() > 0){
            Set<String> strings = groupDate.keySet();
            for(String s :strings){
                if(groupDate.get(s).isEnable){
                    sendMessage(groupDate.get(s).id,str);
                }
            }
        }
    }

    private void sendMessageToGuanli(String str){
        if(!TextUtils.isEmpty(NetManager.getIntance().getGuanliQunId())){
            sendMessage(NetManager.getIntance().getGuanliQunId(),str,true);
        }

    }

    public void sendMessage(String userId,String str,boolean isNow,boolean isCheck){
        if(isCheck && NetManager.getIntance().mType != 3 &&
                NetManager.getIntance().getGroupDatById(userId )!= null &&
                NetManager.getIntance().getGroupDatById(userId ).type != 3 &&
                NetManager.getIntance().getGroupDatById(userId ).type != NetManager.getIntance().mType){
            return;
        }

        XposedBridge.log("userid:"+userId+" str="+str+" isnow"+isNow);
        if(isNow){
            HookUtils.getIntance().sendMeassageBy(userId,str);
        }else{
            addWairMessage(userId,str);
        }
    }

    private void sendMessage(String userId,String str,boolean isNow){
        sendMessage(userId,str,isNow,true);
    }

    private void sendMessage(String userId,String str){
        sendMessage(userId,str,false);
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

    private static ServerManager mIntance = new ServerManager();
    public static ServerManager getIntance(){
        return mIntance;
    }

    private ServerManager(){
        isTime = true;
      //  NetManager.getIntance();
    }
    /*
    * 身份管理相关的业务处理
    * 处理相关的代码直接发送
    * */
 //   private ArrayList<String> mRobotIds = new ArrayList<>();
    private ArrayList<WaitSendMessage> mWaitSend =new  ArrayList<>();

    public void setFalseByAuto() {
        isTime = false;
    }
    public void setTrueByAuto() {
        isTime = true;
    }
    class WaitSendMessage{
        public String sendId;
        public String message;
        public long time;
    }

    private void addWairMessage(String sendId,String message){
        if(!NetManager.getIntance().isSendEableMessageGroup(sendId)){
            return;
        }
        WaitSendMessage a= new WaitSendMessage();
        a.sendId = sendId;
        a.message = message;
        mWaitSend.add(a);
        if(!isWorkLoop){
            startLoop();
        }
    }

    private boolean guanLiQunGuanLiCommon( MessageDeal.MessagerDealDate data){//管理群中的管理命令
        if(data.type == MessageDeal.GUAN_LI_INT) {//注册管理员：管理员私发命令
            int error = NetManager.getIntance().addGuanliId(data.TakerId);
            return true;
        }else if(data.type == MessageDeal.KAISHI_INT ){
            isTime = true;
            setEnable(true);
            return true;
        }
        return  false;
    }

    private boolean  isRobot(String id){
        return NetManager.getIntance().isRobot(id);
    }

    public void init(){
        isInit = true;
       // initShengfen();
    }
    private boolean isInit = false;

    Runnable mWorkRun = new Runnable() {
        @Override
        public void run() {
            if(mWaitSend.size()  > 0){
                WaitSendMessage waitSendMessage = mWaitSend.get(0);
                sendMessage(waitSendMessage.sendId,waitSendMessage.message,true,false);
                mWaitSend.remove(0);
            }
            startLoop();
        }
    };


    public int loopDelay = 400;
    private boolean isWorkLoop = false;
    private void startLoop(){
        isWorkLoop = true;
        Handler handler = HookUtils.getIntance().getHandler();
        if(handler != null){
            Random random = new Random();
            int i = random.nextInt(100);
            i =loopDelay+i;
            handler.removeCallbacks(mWorkRun);
            handler.postDelayed(mWorkRun,i);
        }
    }

}
