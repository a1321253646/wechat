package jackzheng.study.com.wechat.sscManager;

import android.os.Handler;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
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
    Map <String, HashMap<String,Integer>> mAllDataCount = new HashMap<>();
   ArrayList<Sscbean> mErrorList = new ArrayList<>();
   ArrayList<MessageDeal.MessagerDealDate> mRecList = new ArrayList<>();
   Map<Integer,Long> mHeartList = new HashMap<>();
   private int mMyId = -1;
   private int mCurrentMaxId = -1;

    boolean isTime;

    private boolean qunGuanLiCommon( MessageDeal.MessagerDealDate data){//普通群中的管理员命令
        if(data.type == MessageDeal.QUN_NAME_INT){//注册群：必须是管理员在群里发送命令
            data.message = data.message.replace(MessageDeal.QUN_NAME_STR,"");
            StroeAdateManager.getmIntance().addGroup(data.message,data.groupID);
            sendMessageToGuanli(data.message+"已经进行注册,请留意它的开/关操作");
            return true;
        }else if(data.type == MessageDeal.QUN_KAI_INT ){//使能群:：必须是管理员在群里发送的命令
            StroeAdateManager.getmIntance().setGroupEnable(data.groupID,true);
            sendMessageToGuanli(data.message+"您已开启"+StroeAdateManager.getmIntance().getGroupDatById(data.groupID).name+"");
            sendMessage(data.groupID,"已开启\"捅记");
            return true;
        }else if(data.type == MessageDeal.QUN_QIAN_INT ){//关闭群：：必须是管理员在群里发送的命令
            StroeAdateManager.getmIntance().setGroupEnable(data.groupID,false);
            sendMessageToGuanli(data.message+"您已关闭"+StroeAdateManager.getmIntance().getGroupDatById(data.groupID).name+"");
            sendMessage(data.groupID,"已关闭捅记");
            return true;
        }else if(data.type == MessageDeal.RECEVI_ZU_INT ){
            String fenStr = data.message.replace(MessageDeal.RECEVI_ZU_STR,"");
            if(!TextUtils.isEmpty(fenStr)){
                StroeAdateManager.getmIntance().saveReceviDate(fenStr,data.groupID);
                sendMessage(data.groupID,"该裙为"+fenStr+"接受裙");
            }
            return true;
        }else if(data.type == MessageDeal.SEND_ZU_INT ){
            String fenStr = data.message.replace(MessageDeal.SEND_ZU_STR,"");
            if(!TextUtils.isEmpty(fenStr)){
                StroeAdateManager.getmIntance().saveSendDate(data.groupID,fenStr);
                sendMessage(data.groupID,"该裙为"+fenStr+"发送裙");
            }
            return true;
        }else if(data.type == MessageDeal.QUN_FUZHE_INT ){
            String fenStr = data.message.replace(MessageDeal.QUN_FUZHE_JSON,"");
            if(!TextUtils.isEmpty(fenStr)){
                StroeAdateManager.getmIntance().saveFuzheDate(fenStr,data.groupID);
                sendMessage(data.groupID,"该裙为"+fenStr+"负责裙");
            }
            return true;
        }else if(data.type == MessageDeal.QUN_BEIFUZHE_INT ){
            String fenStr = data.message.replace(MessageDeal.QUN_BEIFUZHE_JSON,"");
            if(!TextUtils.isEmpty(fenStr)){
                StroeAdateManager.getmIntance().saveBeFuzheDate(data.groupID,fenStr);
                sendMessage(data.groupID,"该裙被"+fenStr+"负责裙");
            }
            return true;
        }else if(data.type == MessageDeal.QUAN_ZHUANG_INT ){
            String fenStr = data.message.replace(MessageDeal.QUAN_ZHUANG_STR,"");
            if(!TextUtils.isEmpty(fenStr)){
                StroeAdateManager.getmIntance().saveBeFuzheDate(data.groupID,fenStr);
                StroeAdateManager.getmIntance().saveSendDate(data.groupID,fenStr);
                StroeAdateManager.getmIntance().stopZhuang(data.groupID,1);
                sendMessage(data.groupID,"该裙被"+fenStr+"砖马裙");
            }
            return true;
        }else if(data.type == MessageDeal.QUAN_SHOU_INT ){
            String fenStr = data.message.replace(MessageDeal.QUAN_SHOU_STR,"");
            if(!TextUtils.isEmpty(fenStr)){
                StroeAdateManager.getmIntance().saveReceviDate(fenStr,data.groupID);
                StroeAdateManager.getmIntance().saveFuzheDate(fenStr,data.groupID);
                StroeAdateManager.getmIntance().stopZhuang(data.groupID,2);
                if(mMyId == 0){
                    StroeAdateManager.getmIntance().saveDate();
                }
                sendMessage(data.groupID,"该裙为"+fenStr+"收马裙");
            }
            return true;
        }else if(data.type == MessageDeal.STOP_PARSE_INT){
            StroeAdateManager.getmIntance().stopParseGroup(data.groupID,true);
            sendMessage(data.groupID,"该裙不发送解析信息");
             return true;
        }else if(data.type == MessageDeal.STAART_PARSE_INT){
            StroeAdateManager.getmIntance().stopParseGroup(data.groupID,false);
            sendMessage(data.groupID,"该裙将重新发送解析信息");
            return true;
        }else if(data.type == MessageDeal.SHE_XIAN_INT){
            String fenStr = data.message.replace(MessageDeal.SHE_XIAN_STR,"");
            try {
                Integer allInt = Integer.parseInt(fenStr);
                if(allInt !=null){
                    StroeAdateManager.getmIntance().shexianGroup(data.groupID,allInt);
                    sendMessage(data.groupID,"该裙每一株最高为"+fenStr);
                }
            }catch (Exception e){

            }
            return true;
        }

        return  false;
    }
    private boolean qunFuzherenCommon(MessageDeal.MessagerDealDate data){//普通群中的负责员命令
        if(data.type == MessageDeal.SHANG_FEN_INT ){
            String fenStr = data.message.replace(MessageDeal.SHANG_FEN_STR,"");
            try {
                Integer fen = Integer.parseInt(fenStr);
                if(fen !=null){
                    StroeAdateManager.getmIntance().changeFen(data.groupID,fen);
                    StroeAdateManager.GroupData groupData= StroeAdateManager.getmIntance().getGroupDatById(data.groupID);
                    sendMessage(data.groupID,"尚芬"+fen+" 剩下共 "+groupData.fen);

                }
            }catch (Exception e){

            }
            return true;
        }else  if(data.type == MessageDeal.SHE_FEN_INT){
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
            return true;
        }else if(data.type == MessageDeal.SET_CHECK_INT){//设量
            String all = data.message.replace(MessageDeal.SET_CHECK_STR,"");
            try {
                Integer allInt = Integer.parseInt(all);
                if(allInt !=null){
                    StroeAdateManager.getmIntance().setAllForGroup(data.groupID,allInt);
                    StroeAdateManager.GroupData groupData= StroeAdateManager.getmIntance().getGroupDatById(data.groupID);
                    sendMessage(data.groupID," 今天量设置为 "+groupData.all);
                }
            }catch (Exception e){

            }
            return true;
        }else if(data.type == MessageDeal.YING_INT){
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
            return true;
        }else  if(data.type == MessageDeal.XIA_FEN_INT){
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
            return true;
        }
        return false;
    }

    private boolean commomPersonCommon(MessageDeal.MessagerDealDate data) {
        if (data.type == MessageDeal.CHECK_INT) {//查量
            StroeAdateManager.GroupData groupData = StroeAdateManager.getmIntance().getGroupDatById(data.groupID);
            sendMessage(data.groupID, "今天共吓： " + groupData.all);
            return true;
        } else if (data.type == MessageDeal.CHECK_FEN_INT ) {//查分
            StroeAdateManager.GroupData groupData = StroeAdateManager.getmIntance().getGroupDatById(data.groupID);
            sendMessage(data.groupID, "剩余： " + groupData.fen);
            return true;
        }else if(data.type == MessageDeal.TUI_INT && isTime){
            String message = data.message.replace(MessageDeal.TUI_STR,"");
            deleteMessage(data.groupID,message);
            return true;
        }else if(data.type == MessageDeal.CHA_TUI_INT && !StroeAdateManager.getmIntance().getGroupDatById(data.groupID).isStopParse){//查退
            if(mAllData.containsKey(data.groupID)){
                ArrayList<Sscbean> userData = mAllData.get(data.groupID);
                if(userData != null && userData.size() > 0){
                    StringBuilder builder = new StringBuilder();
                    for(Sscbean date : userData){
                        builder.append("id "+date.getId()+":"+date.message+"\n");
                    }
                    if(mMyId == 0){
                        sendMessage(data.groupID, builder.toString() ,true);
                    }
                }else{
                    if(mMyId == 0){
                        sendMessage(data.groupID, "没有下注信息" );
                    }
                }
            }else{
                if(mMyId == 0){
                    sendMessage(data.groupID, "没有下注信息" );
                }
            }
            return true;
        }else if(data.type == MessageDeal.CHA_XIA_INT && !StroeAdateManager.getmIntance().getGroupDatById(data.groupID).isStopParse){//查下
            if(mAllDataCount.containsKey(data.groupID)){
                HashMap<String,Integer> userData = mAllDataCount.get(data.groupID);
                if(userData != null && userData.size() > 0){
                    StringBuilder builder = new StringBuilder();
                    Set<String> strings = userData.keySet();
                    for(String s :strings){
                        builder.append(s+"="+userData.get(s)+"\n");
                    }
                    if(mMyId == 0){
                        sendMessage(data.groupID, builder.toString() ,true);
                    }

                }else{
                    if(mMyId == 0){
                        sendMessage(data.groupID, "没有下注信息" );
                    }
                }
            }else{
                if(mMyId == 0){
                    sendMessage(data.groupID, "没有下注信息" );
                }
            }
            return true;
        }
        return false;
    }

    public void receiveMessage(String str,String userId,String group){

        MessageDeal.MessagerDealDate data = MessageDeal.getMessageDealData(str,userId,group);
        StringBuilder build = new StringBuilder();
        build.append("\n---------------------------------------------------\n"+"message = "+str+" user id = "+userId+" group= "+group);
        if(data == null){
            return;
        }
       XposedBridge.log("\n---------------------------------------------------\n"+"mType = "+StroeAdateManager.getmIntance().mType
                +" groupID = "+StroeAdateManager.getmIntance().getGroupDatById(data.groupID));

        if(qunLiMessage(data)){
            if(mMyId == 0){
                StroeAdateManager.getmIntance().saveDate();
            }
            return;
        }
        if( StroeAdateManager.getmIntance().mType != 3
                && StroeAdateManager.getmIntance().getGroupDatById(data.groupID) != null
                && StroeAdateManager.getmIntance().getGroupDatById(data.groupID).type != StroeAdateManager.getmIntance().mType){
            return;
        }
        if(  data.groupID != null&& !data.groupID.equals(StroeAdateManager.getmIntance().getGuanliQunId())){
            if(StroeAdateManager.getmIntance().isGuanliYuan(data.TakerId)){
                if(qunGuanLiCommon(data)){
                    if(mMyId == 0){
                        StroeAdateManager.getmIntance().saveDate();
                    }
                    return;
                }else if(qunFuzherenCommon(data)){
                    if(mMyId == 0){
                        StroeAdateManager.getmIntance().saveDate();
                    }
                    return;
                }
            }
            if(isInit && commomPersonCommon(data)){
                return;
            }
        }

        if(data.type == MessageDeal.GUAN_LI_QUN_INT && !TextUtils.isEmpty(data.groupID)){//管理群信息，已经有群记录的进行发自己身份ID，没有的等待5s进行确认身份ID
            String quunId = StroeAdateManager.getmIntance().getGuanliQunId();
            if(TextUtils.isEmpty(quunId) || mMyId == -1){
                StroeAdateManager.getmIntance().setGuanliqunID(data.groupID);
                initShengfen();
            }else{
                final String id = data.groupID;
                final String idstr = MessageDeal.SEND_SHENFEN+mMyId;
                HookUtils.getIntance().getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendMessage(id,idstr,true);
                        if(mMyId == 0){
                            sendMessage(id,MessageDeal.GENG_XIN_JSON+StroeAdateManager.getmIntance().getJsonString(),true);
                        }
                    }
                },500);
            }

        }else if(data.type == MessageDeal.CHANGE_INT &&StroeAdateManager.getmIntance().isGuanliYuan(data.TakerId) && !TextUtils.isEmpty(data.groupID)){
            try {
                data.message = data.message.replace(MessageDeal.CHANGE_STR,"");
                String[] spiles =  data.message.split("改");
                String id = spiles[0];
                String newMessage =spiles[1];
                Integer fen = Integer.parseInt(id);
                boolean isSuccess = false;
                boolean isHave= false;
                if(mErrorList.size() > 0){
                    for (Sscbean bean: mErrorList){
                        if(bean.getId() == fen && bean.user.equals(data.groupID)){
                            isHave = true;
                            break;
                        }
                    }
                }
                if(fen !=null && isHave ){
                    isSuccess = messageCheck(fen,newMessage);
                    return;
                }
                Sscbean change = null;
                if(mAllData.size() > 0){
                    ArrayList<Sscbean> sscbeans = mAllData.get(data.groupID);

                    for (Sscbean bean: sscbeans){
                        if(bean.getId() == fen){
                            change = bean;
                            break;
                        }
                    }
                }

                if(!isSuccess){
                    isSuccess = deleteMessage(fen,data.groupID,false);
                }
                if(isSuccess){
                    saveMassege(data.groupID,newMessage,change.message,change.getId());
                }
            }catch (Exception e){

            }
//        }else if(data.message.equals("故意出错")&& ! TextUtils.isEmpty(StroeAdateManager.getmIntance().getGuanliQunId()) &&
//                ! TextUtils.isEmpty(data.groupID) &&!data.groupID.equals(StroeAdateManager.getmIntance().getGuanliQunId()) &&
//                !isRobot(data.TakerId)){
//                if(mMyId != 0){
//                    sendMessage(data.groupID,"出错");
//                }
//        }else if(data.message.equals("图片测试")&& ! TextUtils.isEmpty(StroeAdateManager.getmIntance().getGuanliQunId()) &&
//                ! TextUtils.isEmpty(data.groupID) &&!data.groupID.equals(StroeAdateManager.getmIntance().getGuanliQunId()) &&
//                !isRobot(data.TakerId)){
//            sendMessage(data.groupID,"/sdcard/DCIM/Screenshots/Screenshot_2018-10-04-04-02-53-195_com.miui.home.png");
        }else if(haveNumber(str) && ! TextUtils.isEmpty(StroeAdateManager.getmIntance().getGuanliQunId()) &&
                ! TextUtils.isEmpty(data.groupID) &&!data.groupID.equals(StroeAdateManager.getmIntance().getGuanliQunId()) &&
                !isRobot(data.TakerId) && isInit){
            StroeAdateManager.GroupData groupData= StroeAdateManager.getmIntance().getGroupDatById(data.groupID);
            if(groupData == null){
                build.append("该群未注册");
                return;
            }
            if(  !groupData.isEnable){
                build.append(groupData.name+" 下注：isTime ="+isTime+" isenable="+groupData.isEnable);
                return;
            }
            if(!isTime){
                sendMessage(data.groupID,str+" 无效");
                return ;
            }
            if(StroeAdateManager.getmIntance().mType == 0){
                sendMessage(data.groupID,"未设置身份");
                return;
            }else  if(StroeAdateManager.getmIntance().mType == 1){
                String rec = StroeAdateManager.getmIntance().getReceviDate(data.groupID);
                if(!TextUtils.isEmpty(rec) && StroeAdateManager.getmIntance().getGroupDatById(rec).isEnable){
                    sendMessage(rec,data.message,false,false);
                }
                return;
            }
            build.append("下注："+str);
            saveMassege(group,str);
        }
        XposedBridge.log(build.toString());
        build.append("\n------------------------------------------------\n");
        DebugLog.saveLog(build.toString());
    }

    public void clearAllForAllGroup(boolean isAuto){
        clearAllForAllGroup(isAuto,true);
    }

    public void clearAllForAllGroup(boolean isAuto,boolean isSend){
        StroeAdateManager.getmIntance().clearAllForAllGroup();
        String strBase ="当前量为0，芬为0" ;
        Map<String, StroeAdateManager.GroupData> groupDate = StroeAdateManager.getmIntance().getGroupDate();
        if(groupDate.size() > 0){
            Set<String> strings = groupDate.keySet();
            for(String s :strings){
                if(groupDate.get(s).isEnable && !groupDate.get(s).isStopParse && isSend){
                    sendMessage(s,strBase);
                }
            }
        }
        Handler handler = HookUtils.getIntance().getHandler();
        if(isAuto && handler != null){
            stopHeart();
        }
        if(mMyId == 0){
            StroeAdateManager.getmIntance().saveDate();
        }
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
        Sscbean.resetID();
        Map<String, StroeAdateManager.GroupData> groupDate = StroeAdateManager.getmIntance().getGroupDate();
        if(groupDate.size() > 0){
            Set<String> strings = groupDate.keySet();
            for(String s :strings){
                if(groupDate.get(s).isEnable){
                    sendMessage(s,strBase);
                }
            }
        }

        Set<Integer> integers = mHeartList.keySet();
        long timeStamp = System.currentTimeMillis();
        for(Integer id : integers){
            if(mHeartList.get(id) != null  ){
                mHeartList.put(id,timeStamp+15000);
            }
        }
        sendHartInfo();
        XposedBridge.log("userid: 开始心跳");
        startHeart();
    }
    public void setFalseByDayEnd(){
        isTime = false;
        String strBase ="今天下注结束 ";
        Map<String, StroeAdateManager.GroupData> groupDate = StroeAdateManager.getmIntance().getGroupDate();
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
    private long mAutoStopTime  = -1;
    public void setFalseByAuto(int index){
        isTime = false;
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
                    String str =  "[红包][红包] "+index+"期结束 [红包][红包]\n-----------------------------";
                    if(StroeAdateManager.getmIntance().mType == 0){
                        sendMessage(s,"未设置身份");
                        return;
                    }
                   if(!groupDate.get(s).isStopParse && StroeAdateManager.getmIntance().mType == 2){
                        str = index+" 欺共吓注 "+count+"【米"+StroeAdateManager.getmIntance().getGroupDatById(s).fen+"】\n\n"+str;
                    }
                    sendMessage(s,str);
                    if(!groupDate.get(s).isStopParse && StroeAdateManager.getmIntance().mType == 2){
                        StringBuilder builde = new StringBuilder();
                        if(data != null && data.size() > 0 ){
                            for(Sscbean b : data){
                                builde.append(b.message);
                                if(b.getCount() == 0){
                                    builde.append("  无法识别");
                                }else{
                                    builde.append("  【");
                                    builde.append(b.getCount()+"】");
                                }
                                builde.append("\n--------------------\n");
                            }
                        }
                        sendMessage(s,builde.toString());
                    }
                }
            }
            if(mMyId != -1  ){
                startHeart();
                sendHartInfo();
                Set<Integer> integers = mHeartList.keySet();
                mAutoStopTime = System.currentTimeMillis();
                for(Integer id : integers){
                    if(mHeartList.get(id) != null  && !(mHeartList.get(id)-heartLoop-5000 > mAutoStopTime -6000) ){
                        mHeartList.put(id,mAutoStopTime+5000);
                    }
                    XposedBridge.log("id="+id+"next heartTime = "+(mHeartList.get(id)-mAutoStopTime));
                }
            }
            Sscbean.resetID();
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
                int indexNext = index+1;
                if(indexNext == 121){
                    indexNext = 1;
                }
                StroeAdateManager.getmIntance().changeFen(id,menoy);

                String str2 = ""+index+"期： "+str+"\n\n ";
                if(StroeAdateManager.getmIntance().mType == 0){
                    sendMessage(id,"未设置身份");
                    return;
                }
                if(!data.isStopParse && StroeAdateManager.getmIntance().mType == 2){
                    str2 = str2 +"仲"+count+":芬"+menoy+" [米:"+data.fen+"]\n\n";
                }

                if(index != 23){
                    str2 = str2+"[红包][红包] "+indexNext+" 欺开始 [红包][红包]\n-----------------------------";
                }else{
                    str2 = str2+"[红包][红包] 今天下注结束 [红包][红包]\n-----------------------------";
                }
                sendMessage(id,str2);
                if(!data.isStopParse && StroeAdateManager.getmIntance().mType == 2){
                    StringBuilder builde = new StringBuilder();
                    ArrayList<Sscbean> list =  mAllData.get(id);
                    if(list != null && list.size() > 0 ){
                        for(Sscbean b : list){
                            if(b.zhong == 0){
                                continue;
                            }
                            builde.append(b.message);
                            builde.append("  仲【");
                            builde.append(b.zhong);
                            builde.append("】\n--------------------\n");
                        }
                    }
                    sendMessage(id,builde.toString());
                }
            }
        }
        if(mMyId == 0){
            StroeAdateManager.getmIntance().saveDate();
        }
        if(mMyId != -1  ){
            long timeStamp = System.currentTimeMillis();
            startHeart(timeStamp- (mAutoStopTime));
            sendHartInfo();
            Set<Integer> integers = mHeartList.keySet();

            for(Integer id : integers){
                if(mHeartList.get(id) != null && !(mHeartList.get(id) >mAutoStopTime+10000)){
                    mHeartList.put(id,timeStamp+10000);
                }
            }
        }


        mAllData.clear();
        mAllDataCount.clear();
        mErrorList.clear();
        if(index == 23){
            setFalseByDayEnd();
        }
    }

    /**
     * 对外发送格式不符合的下注
     * @param bean 元数据
     * @param error 错误的原因
     * @param id 对应该字符串的id
     */
    private void sendMessageToGroup(String userid,Sscbean bean,String error,int id){
        if(!StroeAdateManager.getmIntance().getGroupDatById(userid).isStopParse){
            sendMessage(userid,"无法识别\n"+bean.message+"    &改"+bean.getId()+"&" );
        }

        String fuzhe = StroeAdateManager.getmIntance().getFuzheData(userid);
        if(!TextUtils.isEmpty( fuzhe) && StroeAdateManager.getmIntance().getGroupDatById(fuzhe).isEnable ){
            sendMessage(fuzhe,StroeAdateManager.getmIntance().getGroupDatById(userid).name+
                    "无法识别\n"+bean.message+"    &改"+bean.getId()+"&" );
        }
    }

    private void sendMessageToCheck(Sscbean bean,String error,int id){
       // sendMessageToGuanli("id:"+bean.getId()+":"+bean.message);
    }

    private void sendMessageToGuanli(String str){
        if(!TextUtils.isEmpty(StroeAdateManager.getmIntance().getGuanliQunId())){
            sendMessage(StroeAdateManager.getmIntance().getGuanliQunId(),str,true);
        }

    }

    private void sendMessage(String userId,String str,boolean isNow,boolean isCheck){
        if(isCheck && StroeAdateManager.getmIntance().mType != 3 &&
                StroeAdateManager.getmIntance().getGroupDatById(userId )!= null &&
                StroeAdateManager.getmIntance().getGroupDatById(userId ).type != StroeAdateManager.getmIntance().mType){
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

    /**
     * 保存下注数据
     * @param userId 用户的id
     * @param message 下注内容
     */
    public void saveMassege(String userId,String message){
        saveMassege(userId,message,null);
    }
    public void saveMassege(String userId,String message,String oldMessage){
        saveMassege(userId,message,null,-1);

    }
    public void saveMassege(String userId,String message,String oldMessage,int id){
        StringBuilder build = new StringBuilder();
        ArrayList<Sscbean> userData ;
        if(mAllData.containsKey(userId)){
            userData = mAllData.get(userId);
        }else{
            userData = new ArrayList<>();
            mAllData.put(userId,userData);
        }
        HashMap<String,Integer> coulist2;
        if(mAllDataCount.containsKey(userId)){
            coulist2 = mAllDataCount.get(userId);
        }else{
            coulist2 = new HashMap<>();
            mAllDataCount.put(userId,coulist2);
        }
        Sscbean bean ;
        if(id != -1){
            bean =new Sscbean(message,id);
        }else{
            bean =new Sscbean(message);
        }
        bean.user = userId;
        if(!TextUtils.isEmpty(oldMessage)){
            bean.message = oldMessage;
        }
        bean.mList = RegularUtils2.regularStr(message);
        if(bean.mList != null){
            HashMap<String,Integer> coulist = new HashMap<>();
            StringBuilder builder2 = new StringBuilder();
           // builder2.append(message+"\n");
            for(int i = 0;i< bean.mList.size();i++){
                DateBean2 date = bean.mList.get(i);
                StringBuilder builder = new StringBuilder();
                if(date.mDataList.size() >0){
                    for(int ii = 0;ii <date.mDataList.size() ;ii++){
                        ArrayList<Integer> ss = date.mDataList.get(ii);
                        ArrayList<Integer> stmp = new ArrayList<>();
                        for(Integer s : ss){
                            //    if(stmp.size() > 0){
                            int stI = 0;
                            for(;stI < stmp.size();stI++){
                                if(s < stmp.get(stI)){
                                    stmp.add(stI,s);
                                    break;
                                }
                            }
                            if(stI == stmp.size()){
                                stmp.add(s);
                            }
                            //    }
                        }
                        date.mDataList.remove(ii);
                        date.mDataList.add(ii,stmp);
                        ss = stmp;
                        for(Integer inte : ss){
                            builder.append(""+inte);
                        }
                        if(ii <date.mDataList.size() -1){
                            builder.append("-");
                        }
                    }
                }else if(date.mLastData.size() >0){
                    for(Integer[] data : date.mLastData){
                        builder.append(""+data[0]+data[1]+".");
                    }
                }
                for(int a = 0;a< date.local.size();a++){
                    Integer[] locs = date.local.get(a);
                    String key ="";
                    for(Integer loc :locs){
                        key += loc;
                    }
                    key = builder.toString()+":"+key;
                    if(coulist.containsKey(key)){
                        int count = coulist.get(key);
                        count = count+date.mCountList.get(a);
                        if(count > StroeAdateManager.getmIntance().getGroupDatById(userId).max){
                            coulist = null;
                            break;
                        }
                        coulist.put(key,count);
                    }else{
                        if(date.mCountList.get(a) > StroeAdateManager.getmIntance().getGroupDatById(userId).max){
                            coulist = null;
                            break;
                        }
                        coulist.put(key,date.mCountList.get(a));
                    }
                }
                if(coulist == null){
                    break;
                }
                builder2.append(date.toString());
                if(i != bean.mList.size() -1){
                    builder2.append("\n-----------------------\n");
                }
            }
            builder2.append("   &退"+bean.getId()+"&");

            String rec = StroeAdateManager.getmIntance().getReceviDate(userId);
            if(!TextUtils.isEmpty(rec) && StroeAdateManager.getmIntance().getGroupDatById(rec).isEnable){

                HashMap<String,Integer> coulist3;
                if(mAllDataCount.containsKey(rec)){
                    coulist3 = mAllDataCount.get(rec);
                }else{
                    coulist3 = new HashMap<>();
                    mAllDataCount.put(rec,coulist3);
                }

                if(coulist == null){
                    sendMessage(rec,bean.message+"\n该下足中有超过最大，下失败");
                    return;
                }else{
                    HashMap<String, Integer> stringIntegerHashMap = addCountList(rec, coulist3, coulist,true);
                    if(stringIntegerHashMap == null){
                        sendMessage(rec,bean.message+"\n该下足中有超过最大，下失败");
                        return;
                    }else{
                        mAllDataCount.remove(rec);
                        mAllDataCount.put(rec,stringIntegerHashMap);
                    }
                }

                sendMessage(rec,builder2.toString());
                xiazjianfen(bean,rec);
                if(!mAllData.containsKey(rec)){
                    ArrayList<Sscbean> recData = new ArrayList<>();
                    mAllData.put(rec,recData);
                }
                ArrayList<Sscbean> sscbeans = mAllData.get(rec);
                if(sscbeans.size() == 0){
                    sscbeans.add(bean);
                }else{
                    boolean isAdd = false;
                    for(int i = 0; i < sscbeans.size() ;i++){
                        if(  bean.getId()< sscbeans.get(i).getId()){
                            sscbeans.add(i,bean);
                            isAdd = true;
                            break;
                        }
                    }
                    if(!isAdd){
                        sscbeans.add(bean);
                    }
                }

            }else{
                if(coulist == null){
                    sendMessage(userId,bean.message+"\n该下足中有超过最大，下失败");
                    return;
                }else{
                    HashMap<String, Integer> stringIntegerHashMap = addCountList(userId, coulist2, coulist,true);
                    if(stringIntegerHashMap == null){
                        sendMessage(userId,bean.message+"\n该下足中有超过最大，下失败");
                        return;
                    }else{
                        mAllDataCount.remove(userId);
                        mAllDataCount.put(userId,stringIntegerHashMap);
                    }
                }
            }

            if(userData.size() == 0){
                userData.add(bean);
            }else{
                boolean isAdd = false;
                for(int i = 0; i < userData.size() ;i++){
                    if(  bean.getId()< userData.get(i).getId()){
                        userData.add(i,bean);
                        isAdd = true;
                        break;
                    }
                }
                if(!isAdd){
                    userData.add(bean);
                }
            }

            xiazjianfen(bean,userId);
            if(!StroeAdateManager.getmIntance().getGroupDatById(userId).isStopParse){
                sendMessage(userId,builder2.toString());
            }
        }else{
            mErrorList.add(bean);
            boolean isAdd = false;
            for(int i = 0; i < userData.size() ;i++){
                if(  bean.getId()< userData.get(i).getId()){
                    userData.add(i,bean);
                    isAdd = true;
                    break;
                }
            }
            if(!isAdd){
                userData.add(bean);
            }
            build.append("格式不对");
            sendMessageToCheck(bean,"格式不对",-1);
            sendMessageToGroup(userId,bean,"",-1);
        }
        XposedBridge.log(build.toString());
        DebugLog.saveLog(build.toString());
    }


    private HashMap<String,Integer> addCountList(String userId, HashMap<String,Integer> coulist1, HashMap<String,Integer> coulist2,boolean isAdd){
        HashMap<String,Integer> coulist = new HashMap<>();
        Set<String> strings = coulist1.keySet();
        Set<String> strings2 = coulist2.keySet();
        for(String s :strings){
            if(coulist2.containsKey(s)){
                int count1 = coulist1.get(s);
                int count2 = coulist2.get(s);
                if(isAdd){
                    count1 = count1+count2;
                }else{
                    count1 = count1-count2;
                }

                if(count1 > StroeAdateManager.getmIntance().getShexianGroup(userId)){
                    return null;
                }
                coulist.put(s,count1);
                coulist2.remove(s);
            }else{
                coulist.put(s,coulist1.get(s));
            }
        }
        for(String s :strings2){
            coulist.put(s,coulist2.get(s));
        }
        return coulist;
    }


    private void xiazjianfen(Sscbean bean,String groupId){
        if(bean.mList !=null && bean.mList.size() >0){
            int count = 0;
            for(DateBean2 data : bean.mList){
                count+= data.allCount;
            }
            StroeAdateManager.getmIntance().changeFen(groupId,-count,true);
        }
    }

    /**
     * 接受矫正后的下注内容
     * @param id 修正的id
     * @param str 修正后的内容
     */
    private Boolean messageCheck(int id,String str){
        for(int i = 0;i <mErrorList.size();i++){
            Sscbean bean = mErrorList.get(i);
            if(bean.getId() == id){
                mErrorList.remove(i);
                saveMassege(bean.user,str,bean.message,bean.getId());
                return true;
            }
        }

        //sendMessageToCheck(null,"id错误",id);

        return  false;
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
                       int zhong = 0;
                       for(Sscbean bean : sscbeans){
                           zhong = 0;
                           if(bean != null && bean.mList != null && bean.mList.size() >0){
                               for(DateBean2 data : bean.mList){
                                   if(data.mLastData != null && data.mLastData.size() >0 && data.local != null && data.local.size() >0){
                                       for(int i = 0; i<data.local.size();i++){
                                           for(Integer[] num : data.mLastData){
                                               if(num[0] == targetNumber[data.local.get(i)[0]-1] && num[1] == targetNumber[data.local.get(i)[1]-1]){
                                                   zhong += data.mCountList.get(i);
                                               }
                                           }
                                       }
                                   }
                               }
                           }
                           count += zhong;
                           if(bean.zhong  == 0){
                               bean.zhong += zhong;
                           }
                       }
                   }
                   successCountUser.put(groupkey,count);
               }else if(groupDate.get(groupkey).isEnable){
                   successCountUser.put(groupkey,0);
               }
           }
       }
       isTime = true;
       sendMessageToSuccess(str,successCountUser,index);

   }
    /**
     * 撤销下注内容
     * @param userId
     * @param str
     * @return
     */
    private boolean deleteMessage(String userId,String str){
        Integer id ;
        try{
            id = Integer.parseInt(str);
        }catch (Exception e){
            return false;
        }
        return deleteMessage(id,userId);
    }
    private boolean deleteMessage(Integer userId,String str){

        return deleteMessage(userId,str,true);
    }
    private boolean deleteMessage(Integer id,String userId,boolean isPrinte){
        if(id == 0){
            return false;
        }
        boolean  isHave = false;
        String mess = null;
        ArrayList<Sscbean> userData;
        if(mAllData.containsKey(userId)){
            userData = mAllData.get(userId);
            for(int i = 0; i < userData.size();i++){
                if(userData.get(i).getId() == id){
                    isHave = true;
                    break;
                }
            }
        }
        if(!isHave){
            mess =  "退 "+id+" 失败";
            if(isPrinte){
                sendMessage(userId,mess);
            }
            return false;
        }
        Set<String> strings = mAllData.keySet();
        for(String s : strings){
            userData = mAllData.get(s);
            for(int i = 0; i < userData.size();i++){
                if( userData.get(i).getId()== id){
                    Sscbean bean= userData.get(i);
                    mess = bean.getMessage();
                    int count = bean.getCount();
                    StroeAdateManager.getmIntance().changeFen(userId,count,true);
                    userData.remove(i);
                    if(!StroeAdateManager.getmIntance().getGroupDatById(userId).isStopParse){
                        HashMap<String, Integer> countList = getCountList(bean);
                        HashMap<String, Integer> stringIntegerHashMap = mAllDataCount.get(userId);
                        HashMap<String, Integer> stringIntegerHashMap1 = addCountList(userId, stringIntegerHashMap, countList, false);
                        mAllDataCount.remove(userId);
                        mAllDataCount.put(userId,stringIntegerHashMap1);
                    }
                    break;
                }
            }
            if(isPrinte){
                mess = "退 "+id+":\n"+mess+"\n成功";
                sendMessage(s,mess);
            }
        }
        return true;
    }


    private HashMap<String,Integer> getCountList(Sscbean bean){
        HashMap<String,Integer> coulist = null;
        if(bean.mList != null){
            coulist = new HashMap<>();
            StringBuilder builder2 = new StringBuilder();
            for(int i = 0;i< bean.mList.size();i++) {
                DateBean2 date = bean.mList.get(i);
                StringBuilder builder = new StringBuilder();
                if (date.mDataList.size() > 0) {
                    for (int ii = 0; ii < date.mDataList.size(); ii++) {
                        ArrayList<Integer> ss = date.mDataList.get(ii);
                        for (Integer inte : ss) {
                            builder.append("" + inte);
                        }
                        if (ii < date.mDataList.size() - 1) {
                            builder.append("-");
                        }
                    }
                } else if (date.mLastData.size() > 0) {
                    for (Integer[] data : date.mLastData) {
                        builder.append("" + data[0] + data[1] + ".");
                    }
                }
                for (int a = 0; a < date.local.size(); a++) {
                    Integer[] locs = date.local.get(a);
                    String key = "";
                    for (Integer loc : locs) {
                        key += loc;
                    }
                    key = builder.toString() + ":" + key;
                    if (coulist.containsKey(key)) {
                        int count = coulist.get(key);
                        count = count + date.mCountList.get(a);
                        coulist.put(key, count);
                    } else {
                        coulist.put(key, date.mCountList.get(a));
                    }
                }
            }
        }
        return coulist;
    }

    private static ServerManager mIntance = new ServerManager();
    public static ServerManager getIntance(){
        return mIntance;
    }

    private ServerManager(){
        isTime = true;
      //  StroeAdateManager.getmIntance();
    }
    /*
    * 身份管理相关的业务处理
    * 处理相关的代码直接发送
    * */
 //   private ArrayList<String> mRobotIds = new ArrayList<>();
    private ArrayList<WaitSendMessage> mWaitSend =new  ArrayList<>();
    class WaitSendMessage{
        public String sendId;
        public String message;
        public long time;
    }

    private void addWairMessage(String sendId,String message){
        String b = message.replaceAll(" ","").replace("\n","").split("\\(")[0];
        for(int i = 0;i<mRecList.size();i++){
            MessageDeal.MessagerDealDate date = mRecList.get(i);
            String a = date.message.replaceAll(" ","").replace("\n","").split("\\(")[0];
            if(b.equals(a) && date.groupID.equals(sendId)){
                mRecList.remove(date);
                return;
            }else if( b.contains("[红包][红包]") && a.contains("[红包][红包]")){
                String[] s1 = a.split("[红包][红包]");
                String[] s2 = a.split("[红包][红包]");
                if(s1.length == 3 &&  s2.length == 3){
                    if(s1[1].equals(s2[1])){
                        mRecList.remove(date);
                        return;
                    }
                }
            }
        }
        XposedBridge.log("huangna: add userid:"+sendId+" message="+message);
        WaitSendMessage a= new WaitSendMessage();
        long timeStamp = System.currentTimeMillis();
        a.sendId = sendId;
        a.message = message;
        mWaitSend.add(a);
        if(mMyId == 0 && !isWorkLoop){
            startLoop();
        }
    }

    private boolean guanLiQunGuanLiCommon( MessageDeal.MessagerDealDate data){//管理群中的管理命令
        if(data.type == MessageDeal.GUAN_LI_INT) {//注册管理员：管理员私发命令
            int error = StroeAdateManager.getmIntance().addGuanliId(data.TakerId);
            if(error == 0){
                sendMessage(data.groupID,"注册管理员成功",true);
            }else if(error == 1){
                sendMessage(data.groupID,"你已经是管理员无需重新注册",true);
            }else if(error == 2){
                sendMessage(data.groupID,"注册失败",true);
            }
            return true;
        }else if(data.type == MessageDeal.TING_INT ){
            HookUtils.getIntance().tingXiaZhu();
            setEnable(false);
            return true;
        }else if(data.type == MessageDeal.KAISHI_INT ){
            isTime = true;
            setEnable(true);
            return true;
        }else if(data.type == MessageDeal.CLEAR_CHECK_INT ){//清量
            sendMessage(data.groupID,"已清空所有量",true);
            clearAllForAllGroup(false);
            return true;
        }else if(data.type == MessageDeal.SHEN_ZHU_INT) {
            String name = data.message.replace(MessageDeal.SHEN_ZHU_STR, "");
            if (!TextUtils.isEmpty(name) && name.equals(StroeAdateManager.getmIntance().mDeviceID)) {
                mMyId = 0;
                sendMessageToGuanli("本号设为注号");
            } else {
                initShengfen();
            }
            return true;
        } else if(data.type == MessageDeal.SHANGXIAN_SHOUDONG_INT ) {//上线命令
            if(mMyId != -1){
             sendMessageToGuanli(MessageDeal.SEND_SHENFEN + mMyId);
            }else{
                queRenShenFen();
            }
            if (mMyId == 0) {
                sendMessageToGuanli( MessageDeal.GENG_XIN_JSON + StroeAdateManager.getmIntance().getJsonString());
            }
            return true;
        }else if(data.type == MessageDeal.GENG_XIN_INT){
            String replace = data.message.replace(MessageDeal.GENG_XIN_JSON, "");
            StroeAdateManager.getmIntance().setJson(replace);
            if(mMyId == 0){
                StroeAdateManager.getmIntance().saveDate();
            }
            return true;
        }else if(data.type == MessageDeal.KAIJIANG_INT ){
            String num = data.message.replace(MessageDeal.KAIJIANG_STR,"");
            if(isFiveNumber(num)){
                int idnex = HookUtils.getIntance().kaijian(num);
                sendMessageToGuanli(idnex+"期开奖"+num+"成功");
            }else{
                sendMessageToGuanli("开奖命令失败,清检查命令");
            }
        }else if(data.type == MessageDeal.LEIXING_INT){
            String replace = data.message.replace(MessageDeal.LEIXIN_STR, "");
            String typStr = null;
            int type = 0;
            if(replace.startsWith("转")){
                type = 1;
                replace = replace.replace("转","");
                typStr = "转";

            }else if(replace.startsWith("解")){
                type = 2;
                replace = replace.replace("解","");
                typStr = "解";
            }else{
                sendMessageToGuanli( "设置类型命令格式出错");
                return true;
            }
            if(replace.equals(StroeAdateManager.getmIntance().mDeviceID)){

                int i = StroeAdateManager.getmIntance().saveDeviceType(type);
                if(i == -1){
                    sendMessageToGuanli( "设置类型命令执行出错");
                }else  if(i == 1){
                    sendMessageToGuanli( "该设备已经为"+typStr+"类型,无需重新设置");
                }else{
                    sendMessageToGuanli( "该设备已经为"+typStr+"类型,请全部设置完成重启设备");
                }
            }
            return true;
        }
        return  false;
    }
    private boolean guanLiRobotCommon(MessageDeal.MessagerDealDate data){
        String quunId = StroeAdateManager.getmIntance().getGuanliQunId();
        if(data.type == MessageDeal.SHANG_XIAN_INT  && mMyId  != -1) {//上线命令
            String sf =  "无身份";
            int type = StroeAdateManager.getmIntance().mType;
            if(type == 0){
                sf = "无身份";
            }else if(type == 1){
                sf = MessageDeal.ZHUAN_SEND_SHENFEN;
            }else if(type == 2){
                sf = MessageDeal.SEND_SHENFEN;
            }
            sendMessage(StroeAdateManager.getmIntance().getGuanliQunId(),sf+mMyId+","+StroeAdateManager.getmIntance().mDeviceID,true);


            //sendMessage(data.groupID,MessageDeal.SEND_SHENFEN+mMyId,true);
            if(mMyId == 0 && StroeAdateManager.getmIntance().mType == 2){
                sendMessage(quunId,MessageDeal.GENG_XIN_JSON+StroeAdateManager.getmIntance().getJsonString(),true);
            }
            return true;
        }else if(data.type == MessageDeal.SHENG_FEN_INT || data.type == MessageDeal.ZHUAN_SEND_SHENFEN_INT) {

            try {
                String str = null;
                Integer id = null;
                String device = null;
                if(StroeAdateManager.getmIntance().mType == 1 && data.type == MessageDeal.ZHUAN_SEND_SHENFEN_INT){
                    str = data.message.replace(MessageDeal.ZHUAN_SEND_SHENFEN, "");
                    id =  Integer.parseInt(str.split(",")[0]);
                    device = str.split(",")[1];
                }else  if(StroeAdateManager.getmIntance().mType == 2 && data.type == MessageDeal.SHENG_FEN_INT ){
                    str = data.message.replace(MessageDeal.SEND_SHENFEN, "");
                    id =  Integer.parseInt(str.split(",")[0]);
                    device = str.split(",")[1];
                }
                if (id != null && id >= mCurrentMaxId) {
                    mCurrentMaxId = id;
                    XposedBridge.log("mCurrentMaxId ="+mCurrentMaxId);
                }
                if(!TextUtils.isEmpty(device)){
                    StroeAdateManager.getmIntance().setDeiveIdByName(data.TakerId,device);
                }
            } catch (Exception e) {

            }
            return true;
        }else if(data.type == MessageDeal.XIN_TIAO_INT && StroeAdateManager.getmIntance().mType == 2){
            try{
                Integer id = Integer.parseInt(data.message.replace(MessageDeal.XIN_TIAO_STR, ""));
                mHeartList.put(id,System.currentTimeMillis()+heartLoop+5000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(data.type == MessageDeal.ZHUAN_XIN_TIAO_INT && StroeAdateManager.getmIntance().mType == 1){
            try{
                Integer id = Integer.parseInt(data.message.replace(MessageDeal.ZHUAN_XIN_TIAO_STR, ""));
                mHeartList.put(id,System.currentTimeMillis()+heartLoop+5000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(data.type == MessageDeal.SHANG_WEI_INT && isInit ){
            String replace = data.message.replace(MessageDeal.SHANG_WEI_QUN, "");
            String[] list = null;
            if(!TextUtils.isEmpty(replace)){
                list = replace.split(",");
            }else{
                return true;
            }
            if(list != null && list.length == 2){
                try {
                    Integer oldWei = Integer.parseInt(list[0]);
                    Integer newWei = Integer.parseInt(list[1]);
                    XposedBridge.log("oldWei ="+oldWei+" newWei="+newWei);
                    if(mMyId >  0){
                        int index = -1;
                        if(mMyId > newWei && mMyId < oldWei ){
                            index = mMyId- newWei;
                        }else  if(mMyId > oldWei ){
                            index = mMyId- newWei -1;
                        }else if(mMyId == newWei ){
                            mMyId = -1;
                        }
                        if(mMyId == -1){
                            index = 0;
                        }
                        if(index != -1){
                            Handler handler = HookUtils.getIntance().getHandler();
                            if(handler != null){
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        initShengfen();
                                    }
                                },5000*index);
                            }
                        }
                    }else{
                        initShengfen();
                    }
                } catch (Exception e) {

                }
            }
            return true;
        }else if(data.type == MessageDeal.GENG_XIN_INT){
            String replace = data.message.replace(MessageDeal.GENG_XIN_JSON, "");
            StroeAdateManager.getmIntance().setJson(replace);
            if(mMyId == 0){
                StroeAdateManager.getmIntance().saveDate();
            }
            return true;
        }
        return false;
    }

    private boolean qunLiMessage( MessageDeal.MessagerDealDate data){
        String quunId = StroeAdateManager.getmIntance().getGuanliQunId();
        String b = data.message.replaceAll(" ","").replace("\n","").split("\\(")[0];
        if(!TextUtils.isEmpty(quunId) && !TextUtils.isEmpty(data.groupID) && !quunId.equals(data.groupID) && isRobot(data.TakerId)){//在非管理裙中接受到其他机器人的信息
            XposedBridge.log("zsbin: add deal:"+data.groupID+" message="+data.message);
            boolean isDeal = false;
            if(mMyId == 0){//主号重新确认身份
                mMyId = -1;
            }
            XposedBridge.log("huangna: add deal rec after :"+b+ "mWaitSend size ="+mWaitSend.size() );
            for(int i= 0; i < mWaitSend.size() ;i++){
                String a = mWaitSend.get(i).message.replaceAll(" ","").replace("\n","").split("\\(")[0];;
                XposedBridge.log("zsbin: add deal my after :"+a  );
                if(a.equals(b) && data.groupID.equals(mWaitSend.get(i).sendId)){
                    XposedBridge.log("zsbin: remove" );
                    mWaitSend.remove(i);
                    isDeal = true;
                    break;
                }else if( b.contains("[红包][红包]") && a.contains("[红包][红包]")){
                    String[] s1 = a.split("[红包][红包]");
                    String[] s2 = b.split("[红包][红包]");
                    if(s1.length == 3 &&  s2.length == 3){
                        if(s1[1].equals(s2[1])){
                            mWaitSend.remove(i);
                            isDeal = true;
                            break;
                        }
                    }
                }
            }
            if(!isDeal){
                mRecList.add(data);
            }
            return true;
        }

        if(!TextUtils.isEmpty(quunId) && !TextUtils.isEmpty(data.groupID) && quunId.equals(data.groupID)){
            if( !guanLiRobotCommon(data)){
                 guanLiQunGuanLiCommon(data);
            }
            return true;
        }
        return false;
    }

    private boolean  isRobot(String id){
        return StroeAdateManager.getmIntance().isRobot(id);
    }

    public void init(){
        initShengfen();
    }
    private boolean isInit = false;
    private void initShengfen(){
        final  String guanliQunId = StroeAdateManager.getmIntance().getGuanliQunId();
        if(!TextUtils.isEmpty(guanliQunId)){
            sendMessage(StroeAdateManager.getmIntance().getGuanliQunId(),MessageDeal.SHANG_XIAN,true);
            queRenShenFen();
        }
    }

    private void queRenShenFen(){

        mCurrentMaxId = -1;
        XposedBridge.log("确定身份 "+mCurrentMaxId);
        final Handler handler = HookUtils.getIntance().getHandler();
        if(handler != null){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {//确定身份
                    XposedBridge.log("确定身份 "+mCurrentMaxId);
                    mMyId = mCurrentMaxId +1;
                    isInit = true;
                    String sf =  "无身份";
                    int type = StroeAdateManager.getmIntance().mType;
                    if(type == 0){
                        sf = "无身份";
                    }else if(type == 1){
                        sf = MessageDeal.ZHUAN_SEND_SHENFEN;
                    }else if(type == 2){
                        sf = MessageDeal.SEND_SHENFEN;
                    }
                    sendMessage(StroeAdateManager.getmIntance().getGuanliQunId(),sf+mMyId+","+StroeAdateManager.getmIntance().mDeviceID,true);
                    if(StroeAdateManager.getmIntance().mStatus != 4 && mMyId ==0){
                        StroeAdateManager.getmIntance().getDate(StroeAdateManager.getmIntance().getGuanliQunId(),false);
                    }
               //     sendMessage(qun,MessageDeal.XIN_TIAO_STR+mMyId,true);
                    startLoop();
                    if(mMyId == 0){
                        startHeart();
                    }else{
                        long timeStamp = System.currentTimeMillis();
                        for(int i = 0;i<mMyId;i++){
                            mHeartList.put(i,timeStamp+heartLoop+5000);
                        }
                        startHeart();
                    }
                }
            },20000);
        }
    }
    public int heartLoop = 240000;

    Runnable mHeartLoop = new Runnable() {
        @Override
        public void run() {
            Handler handler = HookUtils.getIntance().getHandler();
            if(handler != null){
                sendHartInfo();
                startHeart();
            }
        }
    };
    private void sendHartInfo(){
        if(StroeAdateManager.getmIntance().mType == 1){
            sendMessageToGuanli(MessageDeal.ZHUAN_XIN_TIAO_STR+mMyId);
        }else if(StroeAdateManager.getmIntance().mType ==2){
            sendMessageToGuanli(MessageDeal.XIN_TIAO_STR+mMyId);
        }

        if(mMyId == -1){
            initShengfen();
        }
    }

    public boolean isHear = false;
    public void startHeart(){
        startHeart(0);
    }
    public void startHeart(long idle){
        isHear = true;
        Handler handler = HookUtils.getIntance().getHandler();
        if(handler != null){
            handler.removeCallbacks(mHeartLoop);
            handler.postDelayed(mHeartLoop,heartLoop-idle);
        }
    }

    public void openPhoneNoInTime(){
        clearAllForAllGroup(true,false);
    }

    public void stopHeart(){
        isHear = true;
        Handler handler = HookUtils.getIntance().getHandler();
        if(handler != null){
            handler.removeCallbacks(mHeartLoop);
        }
    }
//    Runnable mLoopRun = new Runnable() {
//        @Override
//        public void run() {
//            if(mMyId == 0 && mWaitSend.size() == 0){
//                stopLoop();
//            }else if( mMyId == 0 && mWaitSend.size()  > 0){
//                WaitSendMessage waitSendMessage = mWaitSend.get(0);
//                sendMessage(waitSendMessage.sendId,waitSendMessage.message,true);
//                mWaitSend.remove(0);
//                startLoop();
//            }else if( mMyId != 0 && mWaitSend.size()  > 0){
//                long timeStamp = System.currentTimeMillis();
//                for(int i = 0;i < mWaitSend.size() ;i++){
//                    WaitSendMessage wait = mWaitSend.get(i);
//                    if(timeStamp > wait.time){
//                        toBeZeroId();
//                        sendMessage(wait.sendId,wait.message,true);
//                        mWaitSend.remove(i);
//                        startLoop();
//                        break;
//                    }
//                }
//                startLoop();
//            }else{
//                startLoop();
//            }
//        }
//    };
    boolean mIsShangweiIng = false;
    Runnable mWorkRun = new Runnable() {
        @Override
        public void run() {


            if((mMyId == 0 ||  mMyId ==10000)&& mWaitSend.size()  > 0){
                WaitSendMessage waitSendMessage = mWaitSend.get(0);
                sendMessage(waitSendMessage.sendId,waitSendMessage.message,true,false);
                mWaitSend.remove(0);
                startLoop();
            }else if( mMyId > 0  && isHear  && !mIsShangweiIng){
                long timeStamp = System.currentTimeMillis();
                Set<Integer> integers = mHeartList.keySet();
                boolean isShangwei  = true;
                for(Integer id : integers){
                    if(id < mMyId  && mHeartList.get(id) > timeStamp){
                        isShangwei = false;
                        break;
                    }
                }
                if(isShangwei){
                    mMyId = 10000;
                    mIsShangweiIng = true;
                    Handler handler = HookUtils.getIntance().getHandler();
                    if(handler != null){
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mIsShangweiIng = false;
                                sendMessage(StroeAdateManager.getmIntance().getGuanliQunId(),MessageDeal.SHANG_WEI_QUN+mMyId+","+0,true);
                                mMyId = 0;
                            }
                        },10000);
                    }

                }
                startLoop();
            }else{
                startLoop();
            }
        }
    };
    private void toBeZeroId(){
        mRecList.clear();
    }

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
//    private void stopLoop(){
//        isWorkLoop = false;
//        Handler handler = HookUtils.getIntance().getHandler();
//        if(handler != null){
//            handler.removeCallbacks(mWorkRun);
//        }
//    }
}
