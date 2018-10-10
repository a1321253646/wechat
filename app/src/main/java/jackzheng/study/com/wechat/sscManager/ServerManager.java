package jackzheng.study.com.wechat.sscManager;

import android.os.Handler;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
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
   ArrayList<SscBeanWithUser> mErrorList = new ArrayList<>();
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
                sendMessage(data.groupID,"该裙被"+fenStr+"砖马裙");
            }
            return true;
        }else if(data.type == MessageDeal.QUAN_SHOU_INT ){
            String fenStr = data.message.replace(MessageDeal.QUAN_SHOU_STR,"");
            if(!TextUtils.isEmpty(fenStr)){
                StroeAdateManager.getmIntance().saveReceviDate(fenStr,data.groupID);
                StroeAdateManager.getmIntance().saveFuzheDate(fenStr,data.groupID);
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
            String mess = deleteMessage(data.groupID,message);
            String mes;
            if(mess == null){
                mes =  "退 "+message+" 失败";
            }else{
                mes = "退 "+message+":\n"+mess+"\n成功";
            }
            sendMessage(data.groupID,mes);
            String rec = StroeAdateManager.getmIntance().getReceviDate(data.groupID);
            if(!TextUtils.isEmpty(rec) && StroeAdateManager.getmIntance().getGroupDatById(rec).isEnable){
                sendMessage(rec,mes);
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

        if(qunLiMessage(data)){
            return;
        }if(  data.groupID != null&& !data.groupID.equals(StroeAdateManager.getmIntance().getGuanliQunId())){
            if(StroeAdateManager.getmIntance().isGuanliYuan(data.TakerId)){
                if(qunGuanLiCommon(data)){
                    return;
                }else if(qunFuzherenCommon(data)){
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
        }else if(data.type == MessageDeal.KAIJIANG_INT &&StroeAdateManager.getmIntance().isGuanliYuan(data.TakerId) && TextUtils.isEmpty(data.groupID)){
            String num = data.message.replace(MessageDeal.KAIJIANG_STR,"");
            build.append("开奖："+str);

        }else if(data.type == MessageDeal.CHANGE_INT &&StroeAdateManager.getmIntance().isGuanliYuan(data.TakerId) && TextUtils.isEmpty(data.groupID)){
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
//        }else if(data.message.equals("故意出错")&& ! TextUtils.isEmpty(StroeAdateManager.getmIntance().getGuanliQunId()) &&
//                ! TextUtils.isEmpty(data.groupID) &&!data.groupID.equals(StroeAdateManager.getmIntance().getGuanliQunId()) &&
//                !isRobot(data.TakerId)){
//                if(mMyId != 0){
//                    sendMessage(data.groupID,"出错");
//                }
        }else if(data.message.equals("图片测试")&& ! TextUtils.isEmpty(StroeAdateManager.getmIntance().getGuanliQunId()) &&
                ! TextUtils.isEmpty(data.groupID) &&!data.groupID.equals(StroeAdateManager.getmIntance().getGuanliQunId()) &&
                !isRobot(data.TakerId)){
            sendMessage(data.groupID,"/sdcard/DCIM/Screenshots/Screenshot_2018-10-04-04-02-53-195_com.miui.home.png");
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
            build.append("下注："+str);
            saveMassege(group,str);
        }
        XposedBridge.log(build.toString());
        build.append("\n------------------------------------------------\n");
        DebugLog.saveLog(build.toString());
    }

    public void clearAllForAllGroup(boolean isAuto){
        StroeAdateManager.getmIntance().clearAllForAllGroup();
        String strBase ="当前量为0，芬为0" ;
        Map<String, StroeAdateManager.GroupData> groupDate = StroeAdateManager.getmIntance().getGroupDate();
        if(groupDate.size() > 0){
            Set<String> strings = groupDate.keySet();
            for(String s :strings){
                if(groupDate.get(s).isEnable){
                    sendMessage(s,strBase);
                }
            }
        }
        Handler handler = HookUtils.getIntance().getHandler();
        if(isAuto && handler != null){
            isHear = false;
            handler.removeCallbacks(mHeartLoop);
            //               handler.postDelayed(mHeartLoop,heartLoop);
        }
    }

    public void setTrueByDayStrartNoNotification(){
        isTime = true;
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
        sendMessage(StroeAdateManager.getmIntance().getGuanliQunId(),MessageDeal.XIN_TIAO_STR+mMyId,true);
        XposedBridge.log("userid: 开始心跳");
        startHeart();
    }
    public void setFalseByDayEndNoNotification(){
        isTime = false;
    }
    public void setFalseByDayEnd(){
        isTime = false;
        String strBase ="今天下注结束\n凌晨3点将进行清芬清量，请提前做好统计" ;
        Map<String, StroeAdateManager.GroupData> groupDate = StroeAdateManager.getmIntance().getGroupDate();
        if(groupDate.size() > 0){
            Set<String> strings = groupDate.keySet();
            for(String s :strings){
                if(groupDate.get(s).isEnable){
                    sendMessage(s,strBase);
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
                    if(!groupDate.get(s).isStopParse){
                        str = index+" 欺共吓注 "+count+"【米"+StroeAdateManager.getmIntance().getGroupDatById(s).fen+"】\n\n"+str;
                    }
                    sendMessage(s,str);
                }
            }
            if(mMyId != -1  ){
                startHeart(5000);
                sendMessage(StroeAdateManager.getmIntance().getGuanliQunId(),MessageDeal.XIN_TIAO_STR+mMyId,true);
                Set<Integer> integers = mHeartList.keySet();
                mAutoStopTime = System.currentTimeMillis();
                for(Integer id : integers){
                    if(mHeartList.get(id) != null  && !(mHeartList.get(id) >mAutoStopTime -6000) ){
                        mHeartList.put(id,mAutoStopTime+15000);
                    }
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
                int indexNext = index+1;
                if(indexNext == 121){
                    indexNext = 1;
                }
                StroeAdateManager.getmIntance().changeFen(id,menoy);
                if(index != 23){
                    String str2 = ""+index+"期： "+str+"\n\n ";
                    if(!data.isStopParse){
                        str2 = str2 +"仲"+count+":芬"+menoy+" [米:"+data.fen+"]\n\n";
                    }
                    str2 = str2+"[红包][红包] "+indexNext+" 欺开始 [红包][红包]\n-----------------------------";
                    sendMessage(id, str2);
                }else{
                    String str2 =+index+" 期 "+str+" \n";
                    if(!data.isStopParse){
                        str2 = str2 +" 重："+count+"，"+" 上 ："+menoy+"余："+data.fen+"\n";
                    }
                    str2 = str2+ "晚安";
                    sendMessage(id,str2) ;
                }

            }
        }
        if(mMyId != -1  ){
            long timeStamp = System.currentTimeMillis();
            startHeart(timeStamp- (mAutoStopTime-5000));
            sendMessage(StroeAdateManager.getmIntance().getGuanliQunId(),MessageDeal.XIN_TIAO_STR+mMyId,true);
            Set<Integer> integers = mHeartList.keySet();

            for(Integer id : integers){
                if(mHeartList.get(id) != null && !(mHeartList.get(id) >mAutoStopTime+10000)){
                    mHeartList.put(id,timeStamp+10000);
                }
            }
        }


        mAllData.clear();
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
            sendMessage(userid,"无法识别\n"+bean.message);
        }

        String fuzhe = StroeAdateManager.getmIntance().getFuzheData(userid);
        if(!TextUtils.isEmpty( fuzhe) && StroeAdateManager.getmIntance().getGroupDatById(fuzhe).isEnable ){
            sendMessage(fuzhe,StroeAdateManager.getmIntance().getGroupDatById(userid).name+" 无法识别\n"+bean.message);
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

    private void sendMessage(String userId,String str,boolean isNow){
        XposedBridge.log("userid:"+userId+" str="+str+" isnow"+isNow);
        if(isNow){
            HookUtils.getIntance().sendMeassageBy(userId,str);
        }else{
            addWairMessage(userId,str);
        }
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

            StringBuilder builder2 = new StringBuilder();

            builder2.append(message+"\n");

            for(int i = 0;i< bean.mList.size();i++){
                DateBean2 date = bean.mList.get(i);
                builder2.append("解"+date.toString());
                if(i != bean.mList.size() -1){
                    builder2.append("\n");
                }
            }
            builder2.append("   (退"+bean.getId()+")");
            if(!StroeAdateManager.getmIntance().getGroupDatById(userId).isStopParse){
                sendMessage(userId,builder2.toString());
            }

            String rec = StroeAdateManager.getmIntance().getReceviDate(userId);
            if(rec!= null && StroeAdateManager.getmIntance().getGroupDatById(rec).isEnable){
                sendMessage(rec,builder2.toString());
                xiazjianfen(bean,rec);
                if(!mAllData.containsKey(rec)){
                    ArrayList<Sscbean> recData = new ArrayList<>();
                    mAllData.put(rec,recData);
                    mAllData.get(rec).add(bean);
                }else{
                    mAllData.get(rec).add(bean);
                }
            }
        }else{
            SscBeanWithUser error = new SscBeanWithUser(bean,userId);
            mErrorList.add(error);
            build.append("格式不对");
            sendMessageToCheck(bean,"格式不对",-1);
            sendMessageToGroup(userId,bean,"",-1);
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
            StroeAdateManager.getmIntance().changeFen(groupId,-count,true);
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
       isTime = true;
       sendMessageToSuccess(str,successCountUser,index);

   }
    /**
     * 撤销下注内容
     * @param userId
     * @param str
     * @return
     */
    private String deleteMessage(String userId,String str){
        Integer id ;
        try{
            id = Integer.parseInt(str);
        }catch (Exception e){
            return null;
        }
        if(id == 0){
            return null;
        }
        ArrayList<Sscbean> userData  =null;
        ArrayList<Sscbean> recData  =null;
        if(mAllData.containsKey(userId)){
            userData = mAllData.get(userId);
        }
        if(userData == null){
            return null;
        }
        String rec = StroeAdateManager.getmIntance().getReceviDate(userId);
        if(rec!= null && StroeAdateManager.getmIntance().getGroupDatById(rec).isEnable){
            recData = mAllData.get(rec);
        }
        String mess = null;
        for(int i = 0; i < userData.size();i++){
            if( userData.get(i).getId()== id){
                Sscbean bean= userData.get(i);
                mess = bean.getMessage();
                int count = bean.getCount();
                StroeAdateManager.getmIntance().changeFen(userId,count,true);
                userData.remove(i);
                break;
            }
        }
        if(recData != null && mess != null){
            for(int i = 0; i < recData.size();i++){
                if( recData.get(i).getId()== id){
                    Sscbean bean= recData.get(i);
                    int count = bean.getCount();
                    StroeAdateManager.getmIntance().changeFen(rec,count,true);
                    recData.remove(i);
                    break;
                }
            }
        }
        return mess;
    }

    private static ServerManager mIntance = new ServerManager();
    public static ServerManager getIntance(){
        return mIntance;
    }

    private ServerManager(){
        isTime = true;
        StroeAdateManager.getmIntance();
    }
    /*
    * 身份管理相关的业务处理
    * 处理相关的代码直接发送
    * */
    private ArrayList<String> mRobotIds = new ArrayList<>();
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
            setEnable(false);
            return true;
        }else if(data.type == MessageDeal.KAISHI_INT ){
            setEnable(true);
            return true;
        }else if(data.type == MessageDeal.CLEAR_CHECK_INT ){//清量
            sendMessage(data.groupID,"已清空所有量",true);
            clearAllForAllGroup(false);

            return true;
        }
        return  false;
    }
    private boolean guanLiRobotCommon(MessageDeal.MessagerDealDate data){
        String quunId = StroeAdateManager.getmIntance().getGuanliQunId();
        if(data.type == MessageDeal.SHANG_XIAN_INT  && mMyId  != -1) {//上线命令
            sendMessage(data.groupID,MessageDeal.SEND_SHENFEN+mMyId,true);
            if(mMyId == 0){
                sendMessage(quunId,MessageDeal.GENG_XIN_JSON+StroeAdateManager.getmIntance().getJsonString(),true);
            }
            return true;
        }else if(data.type == MessageDeal.SHENG_FEN_INT) {

            boolean isHave = false;
            for (String id : mRobotIds) {
                if (id.equals(data.TakerId)) {
                    isHave = true;
                    break;
                }
            }
            if (!isHave) {
                mRobotIds.add(data.TakerId);
            }
            try {
                Integer id = Integer.parseInt(data.message.replace(MessageDeal.SEND_SHENFEN, ""));
                if (id != null && id >= mCurrentMaxId) {
                    mCurrentMaxId = id;
                    XposedBridge.log("mCurrentMaxId ="+mCurrentMaxId);
                }
            } catch (Exception e) {

            }
            return true;
        }else if(data.type == MessageDeal.XIN_TIAO_INT){
            try{
                Integer id = Integer.parseInt(data.message.replace(MessageDeal.XIN_TIAO_STR, ""));
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
        for(String ids : mRobotIds){
            if(ids.equals(id)){
                return true;
            }
        }
        return false;
    }

    public void init(){
        initShengfen();
    }
    private boolean isInit = false;
    private void initShengfen(){
        final  String guanliQunId = StroeAdateManager.getmIntance().getGuanliQunId();
        if(!TextUtils.isEmpty(guanliQunId)){
            sendMessage(guanliQunId,MessageDeal.SHANG_XIAN,true);
            queRenShenFen(guanliQunId);
        }
    }

    private void queRenShenFen(final String qun){
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
                    sendMessage(qun,MessageDeal.SEND_SHENFEN+mMyId,true);
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
            },5000);
        }
    }
    public int heartLoop = 240000;

    Runnable mHeartLoop = new Runnable() {
        @Override
        public void run() {
            Handler handler = HookUtils.getIntance().getHandler();
            if(handler != null){
                sendMessage(StroeAdateManager.getmIntance().getGuanliQunId(),MessageDeal.XIN_TIAO_STR+mMyId,true);
                startHeart();
            }
        }
    };
    boolean isHear = false;
    private void startHeart(){
        startHeart(0);
    }
    private void startHeart(long idle){
        isHear = true;
        Handler handler = HookUtils.getIntance().getHandler();
        if(handler != null){
            handler.removeCallbacks(mHeartLoop);
            handler.postDelayed(mHeartLoop,heartLoop-idle);
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


            if( mMyId == 0 && mWaitSend.size()  > 0){
                WaitSendMessage waitSendMessage = mWaitSend.get(0);
                sendMessage(waitSendMessage.sendId,waitSendMessage.message,true);
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
                        },5000);
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
