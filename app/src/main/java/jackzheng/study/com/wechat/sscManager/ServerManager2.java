package jackzheng.study.com.wechat.sscManager;

import android.os.Debug;
import android.text.TextUtils;
import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Date;

import de.robv.android.xposed.XposedBridge;
import jackzheng.study.com.wechat.CommonDeal;
import jackzheng.study.com.wechat.HtmlParse;
import jackzheng.study.com.wechat.MessageDeal;
import jackzheng.study.com.wechat.SscControl;
import jackzheng.study.com.wechat.regular.AllXiazuBean;
import jackzheng.study.com.wechat.regular.DateBean2;
import jackzheng.study.com.wechat.regular.RegularStrBean;
import jackzheng.study.com.wechat.regular.RegularUtils2;
import jackzheng.study.com.wechat.regular.SscBean;
import yaunma.com.myapplication.Av_P;

public class ServerManager2 {
  //  public boolean isTime = true;
    public boolean isInit = false;
    public boolean isWork = false;

    public ArrayMap<String , ArrayList<SscBean> > mXiazuMap = new ArrayMap<>();
    public ArrayMap<String , Boolean > mIgnoeList = new ArrayMap<>();
    public long mId = 1;

    public int mJieId = 1;

    public final static int OPEN_INDEX = 107;
    public int mOpenIndex = -1;


//    public ArrayMap<String ,Long > mIdMap = new ArrayMap<>();
    public ArrayMap<Long , Boolean> mAllMessage = new ArrayMap<>();
    public ArrayMap<Long , AllXiazuBean> mAllXiazu= new ArrayMap<>();
    public void receiveMessage(String str,String userId,String group,long msgId){
        if(TextUtils.isEmpty(str)||
                TextUtils.isEmpty(userId)){
            return;
        }
        MessageDeal.MessagerDealDate data = MessageDeal.getMessageDealData(str,userId,group);
        if(CommonDeal.deal(data)){
            return;
        }
        if(data.type == MessageDeal.SET_TUI_INT &&
            !TextUtils.isEmpty(data.groupID)){
            tuiDeal(data);
            return;
        }else if(data.type == MessageDeal.SET_EDIT_INT &&
                !TextUtils.isEmpty(data.groupID)){
            editDeal(data);
            return;
        }else if(data.type == MessageDeal.SET_DETAIL_INT &&
                !TextUtils.isEmpty(data.groupID) &&
                DateSaveManager.getIntance().isHaveGroup(data.groupID) &&
                DateSaveManager.getIntance().getGroup(data.groupID).isEnable){
            if(!TextUtils.isEmpty(DateSaveManager.getIntance().getGroup(data.groupID).toGroup)){
                return;
            }
            StringBuilder str1 = new StringBuilder();
            long count = 0;
            if(mXiazuMap.containsKey(data.groupID)){

                ArrayList<SscBean> xiazu = mXiazuMap.get(data.groupID);
                for(SscBean bean : xiazu){
                    if(bean.list == null){
                        str1.append(bean.msg+" 失败");
                        str1.append("\n-------------------\n");
                    }else{
                        count += bean.count;
                        str1.append(bean.toString());
                        str1.append("\n--------------------\n");
                    }

                }

            }
            str1.append("共"+count+" 剩余"+DateSaveManager.getIntance().getGroup(data.groupID).fen);
            if(DateSaveManager.getIntance().getGroup(data.groupID).isEnable){
                SscControl.getIntance().sendMessage(  "当前列表\n"+str1.toString(),
                        data.groupID,false);
            }
            return;
        }
        if(data.message.contains("以上全部算有效") &&
                data.message.contains("当期如果撤单当局为庄通杀")&&
                data.message.contains("止停线下无效")&&
                DateSaveManager.getIntance().isHaveGroup(data.groupID)){
            int stopIndex = SscControl.getIntance().getIndex();
            stopDeal(stopIndex,data.groupID);
            return;
        }

/*        if(!DateSaveManager.getIntance().isJustShou&&
                !TextUtils.isEmpty(data.groupID) &&
                DateSaveManager.getIntance().isHaveGroup(data.groupID) &&
                DateSaveManager.getIntance().getGroup(data.groupID).isEnable &&
                DateSaveManager.getIntance().getGroup(data.groupID).isIntime){


        }*/

        if(!haveNumber(data.message)){
            return;
        }
        DateSaveManager.GroupDate groupDate = DateSaveManager.getIntance().getGroup(data.groupID);
        if(groupDate== null){
            return ;
        }
        String str2 = data.message;
        if(str2.contains("期")){
            return;
        }else if(str2.contains("补")){
            //   if(str2.contains("+")|| str2.contains("-")|| str2.contains("一")|| str2.contains("—")){
            return;
            // }
        }else if(str2.contains("量")){
            return;
        }else if(str2.contains("减")){
            return;
        }else if(str2.startsWith("中") || str2.startsWith("仲") ){
            return;
        }else if(str2.contains("剩余") ){
            return;
        }else if(str2.startsWith("下")){
            str2 = str2.replace("下","");
        }else if(str2.startsWith("上")){
            str2 = str2.replace("上","");
        }else if(str2.startsWith("十")){
            str2 = str2.replace("十","");
        }else if(str2.startsWith("+")){
            str2 = str2.replace("+","");
        }else if(str2.startsWith("-")){
            str2 = str2.replace("-","");
        }else if(str2.startsWith("一")){
            str2 = str2.replace("一","");
        }

        if(isAllNumber(str2)){
            return;
        }
        String xiazuGroup = data.groupID;
        if(!TextUtils.isEmpty(groupDate.toGroup) && DateSaveManager.getIntance().getGroup(groupDate.toGroup).isEnable && groupDate.isIntime){
            xiazuGroup = groupDate.toGroup;

            //SscControl.getIntance().sendMessage(data.message,DateSaveManager.getIntance().getGroup(data.groupID).toGroup,false);
            //return;
        }
        if(! DateSaveManager.getIntance().getGroup(xiazuGroup).isEnable){
            return;
        }

        xiazhu(data.message,data.groupID,msgId,-1,null);
    }
    public void stopDeal(int index,String group){
    //    if(DateSaveManager.getIntance().isJustShou ){
    //        return;
    //    }
        ArrayMap<String , DateSaveManager.GroupDate> all = DateSaveManager.getIntance().getAllGroup();
        if(index  > 38 && index <= OPEN_INDEX){
            if(TextUtils.isEmpty(group)){
                for(String g : all.keySet()){
                    all.get(g).isIntime = false;
                }
            }else{
                all.get(group).isIntime = false;
            }
            return;
        }
        String indexNumber =index<10?("00"+index):index<100?("0"+index):""+index;
        String msgRoot="\uE252\uE252 第["+indexNumber+"]届      ：结束\n";

        StringBuilder str;
        int count = 0;

        boolean isAll= false;
        if(TextUtils.isEmpty(group)){
            isAll = true;
        }else{
            isAll = false;
        }

        for(String goupID : all.keySet()){
            if(isAll && !all.get(goupID).isIntime){
                continue;
            }else if(!isAll && !goupID.equals(group)){
                continue;
            }else if(!isAll && group.equals(goupID) && !all.get(goupID).isIntime){
                break;
            }else if( !all.get(goupID).isEnable){
                continue;
            }
            all.get(goupID).isIntime = false;
            if(isAll && !TextUtils.isEmpty(DateSaveManager.getIntance().getGroup(goupID).toGroup)){
                SscControl.getIntance().sendMessage(  "--------以上全部算有效--------\n" +
                                "------当期如果撤单当局为庄通杀------\n" +
                                "--------止停线下无效-----------",
                        goupID,false);
               /* if(){

                }else if(!isAll &&group.equals(goupID)  ){
                    SscControl.getIntance().sendMessage(  "--------以上全部算有效--------\n" +
                                    "------当期如果撤单当局为庄通杀------\n" +
                                    "--------止停线下无效-----------",
                            DateSaveManager.getIntance().getGroup(goupID).toGroup,false);
                }*/
//                continue;
            }
            if( !(isAll &&TextUtils.isEmpty(DateSaveManager.getIntance().getGroup(goupID).toGroup)) &&
                    !(!isAll && !TextUtils.isEmpty(DateSaveManager.getIntance().getGroup(goupID).toGroup))){
                continue;
            }

            if(!isAll){
                goupID = DateSaveManager.getIntance().getGroup(goupID).toGroup;
            }


            count = 0;
            str = new StringBuilder();
            str.append(msgRoot);
            str.append("-------------------\n");
            if(mXiazuMap.containsKey(goupID)){
                ArrayList<SscBean> xiazu = mXiazuMap.get(goupID);
                for(SscBean bean : xiazu){
                    if(bean.list == null){
                        str.append(bean.msg+" 失败");
                        str.append("\n-------------------\n");
                    }else if(bean.count == 0){
                        str.append(bean.msg+"   失败");
                        str.append("\n-------------------\n");
                    }else {
                        count+= bean.count;
                        str.append(bean.toString());
                        str.append("\n-------------------\n");
                    }

                }
            }/*else if(DateSaveManager.getIntance().isBeiyong){
                continue;
            }*/
            str.append("共"+count+" 剩余"+DateSaveManager.getIntance().getGroup(goupID).fen+" 流"+DateSaveManager.getIntance().getGroup(goupID).liang);
            if(DateSaveManager.getIntance().getGroup(goupID).isEnable){
                if(!TextUtils.isEmpty( DateSaveManager.getIntance().mBaobiao)){
                    SscControl.getIntance().sendMessage( DateSaveManager.getIntance().getGroup(goupID).groupName+"\n"+ str.toString(),
                            DateSaveManager.getIntance().mBaobiao,false);

                }else{
                    SscControl.getIntance().sendMessage(  str.toString(),
                            goupID,false);
                    SscControl.getIntance().sendMessage(  "下"+count+" 量"+DateSaveManager.getIntance().getGroup(goupID).liang,
                            goupID,false);
                }
            }
            if(!isAll &&goupID.equals(group)){
                break;
            }
        }
    }

    public void kaijiangDeal(HtmlParse.MaxIndexResult parse){
        ServerManager2.getmIntance().mOpenIndex = parse.index;
        if(parse.index  > 38 && parse.index < OPEN_INDEX){
            ArrayMap<String, DateSaveManager.GroupDate> all = DateSaveManager.getIntance().getAllGroup();
            for(String g : all.keySet()){
                all.get(g).isIntime = false;
            }
            return;
        }

        String indexNumber = parse.index<10?("00"+ parse.index):parse.index<100?("0"+parse.index):""+parse.index;
        String msgRoot="\uE12D\uE12D 第["+indexNumber+"]届      开："+parse.str+"\n";

        StringBuilder str;

        int count;
        int eachCount ;

        ArrayMap<String, DateSaveManager.GroupDate> allGroup = DateSaveManager.getIntance().getAllGroup();
//        if(!DateSaveManager.getIntance().isJustShou ){
            for(String goupID : allGroup.keySet()){
                if(!TextUtils.isEmpty(DateSaveManager.getIntance().getGroup(goupID).toGroup)){
                    continue;
                }
                count = 0;
                str = new StringBuilder();
                int yin = DateSaveManager.getIntance().getGroup(goupID).yin;
                if(mXiazuMap.containsKey(goupID)){
                    ArrayList<SscBean> xiazu = mXiazuMap.get(goupID);

                    for(SscBean bean1 : xiazu){
                        if(bean1.list == null){
                            continue;
                        }
                        eachCount= 0;
                        for(DateBean2 bean :bean1.list){
                            eachCount +=getZhongjianCount(bean,parse.getNumber(),yin);
                        }
                        if(eachCount >0){
                            str.append(bean1.msg);
                            str.append(" 中   "+(eachCount/yin)+"\n-------------------\n");
                            count+=eachCount;
                        }
                    }
                    str.append("\n");
                    if(count > 0){
                        DateSaveManager.getIntance().changeGroupFenOrLiang(goupID,true,1,count);
                    }
                }/*else if(DateSaveManager.getIntance().isBeiyong){
                    continue;
                }*/
                str.append("共中"+(count/yin)+"组     共计 "+count+"\n");
                str.append(" 剩余"+DateSaveManager.getIntance().getGroup(goupID).fen+" 量"+DateSaveManager.getIntance().getGroup(goupID).liang);

                if(DateSaveManager.getIntance().getGroup(goupID).isEnable){
                     if(!TextUtils.isEmpty( DateSaveManager.getIntance().mBaobiao)){
                         SscControl.getIntance().sendMessage(  DateSaveManager.getIntance().getGroup(goupID).groupName+"\n"+
                                 msgRoot+"-------------------\n"+str.toString(), DateSaveManager.getIntance().mBaobiao,false);

                     }else{
                         SscControl.getIntance().sendMessage(  msgRoot+"-------------------\n"+str.toString(), goupID,false);
                         SscControl.getIntance().sendMessage(  "共中"+(count/yin)+",剩余 "+DateSaveManager.getIntance().getGroup(goupID).fen,
                                 goupID,false);
                     }
                }
                if(parse.index == 38  ){
                    DateSaveManager.GroupDate g = DateSaveManager.getIntance().getGroup(goupID);
                    g.isIntime = false;
                    if(DateSaveManager.getIntance().getGroup(goupID).isEnable){
                        SscControl.getIntance().sendMessage(  "当前分"+g.fen+"亮"+g.liang+"\n今日停潘", goupID,false);
                    }

                    DateSaveManager.getIntance().clearAllGroupFenAndLiang();

                }else if(parse.index == OPEN_INDEX && DateSaveManager.getIntance().getGroup(goupID).isEnable){
                    DateSaveManager.GroupDate g = DateSaveManager.getIntance().getGroup(goupID);
                    g.isIntime = true;
                    if(DateSaveManager.getIntance().getGroup(goupID).isEnable){
                        SscControl.getIntance().sendMessage(  "当前分"+g.fen+"亮"+g.liang+"\n开始下驻", goupID,false);
                    }
                }
            }
//        }

        mXiazuMap.clear();
        mAllMessage.clear();
        mAllXiazu.clear();
        mId = 1;
        if(parse.index != 38){
            for(String g : allGroup.keySet()){
                allGroup.get(g).isIntime = true;
            }
        }
    }
    public void tuiDeal(String msg){
        AllXiazuBean bean = null;
        long msgId = -1;
        for(long key :mAllXiazu.keySet()){
            if(!TextUtils.isEmpty(mAllXiazu.get(key).date.msg) &&
                    mAllXiazu.get(key).date.msg.equals(msg)){
                bean = mAllXiazu.get(key);
                msgId = bean.date.msgId;
            }
        }
        //  if(DateSaveManager.getIntance().isJustShou && DateSaveManager.getIntance().getGroup(bean.)){
        //    SscControl.getIntance().sendMessage("\uE333操作失败-封潘中\uE333",bean.group,false);
        // }
 /*       if(!DateSaveManager.getIntance().getGroup(bean.group).isIntime ){
            if(bean != null){
                SscControl.getIntance().sendMessage("\uE333操作失败-封潘中\uE333",bean.group,false);
            }

            return;
        }*/
        if(bean != null){
            ArrayList<SscBean> sscBeans = mXiazuMap.get(bean.group);
            for(int i = 0 ;i < sscBeans.size();i++){
                SscBean bean2 = sscBeans.get(i);
                XposedBridge.log("bean2 id="+bean2.mId);
                if(bean2.msgId ==msgId){
                    DateSaveManager.getIntance().changeGroupFenOrLiang(bean.group,true,1,bean2.count);
                    DateSaveManager.getIntance().changeGroupFenOrLiang(bean.group,false,2,bean2.count);
                    SscControl.getIntance().sendMessage("["+bean2.mId+" ]"+bean2.msg+"\n--------------\n[捂脸][捂脸]退成功    补"+bean2.count+"\n剩余"+
                                    DateSaveManager.getIntance().getGroup( bean.group).fen,
                            bean.group,false);
                    sscBeans.remove(bean2);
                    mAllXiazu.remove(bean2.mId);
                    return;
                }
            }
        }

    }



    public void tuiDeal(long msgId){
        AllXiazuBean bean = null;

/*        if(DateSaveManager.getIntance().isJustShou){
            XposedBridge.log("isJustShou 撤销 "+msgId);
            for(String group:mXiazuMap.keySet()){
                XposedBridge.log("group =  "+group);
                for(int i = 0 ;i < mXiazuMap.get(group).size() ;i++){
                    SscBean bean2 = mXiazuMap.get(group).get(i);
                    if(bean2.msgId == msgId){
                        if(DateSaveManager.getIntance().getGroup(group) != null &&
                             //   DateSaveManager.getIntance().getGroup(group).isIntime &&
                                !TextUtils.isEmpty(DateSaveManager.getIntance().getGroup(group).toGroup)
                                ){
                            SscControl.getIntance().sendMessage(MessageDeal.SET_YUANQUN_CEXIAO+bean2.msg,DateSaveManager.getIntance().getGroup(group).toGroup,false);
                            mXiazuMap.get(group).remove(i);
                        }
                        return;
                    }
                }
                for( SscBean bean2: mXiazuMap.get(group)){
                    XposedBridge.log("bean2.msgId =  "+bean2.msgId);

                }
            }
            return;
        }
*/
        for(long key :mAllXiazu.keySet()){
            if(mAllXiazu.get(key).date.msgId == msgId){
                bean = mAllXiazu.get(key);
            }
        }
      //  if(DateSaveManager.getIntance().isJustShou && DateSaveManager.getIntance().getGroup(bean.)){
        //    SscControl.getIntance().sendMessage("\uE333操作失败-封潘中\uE333",bean.group,false);
       // }
    /*    if(!DateSaveManager.getIntance().getGroup(bean.group).isIntime){
            if(bean != null){
                SscControl.getIntance().sendMessage("\uE333操作失败-封潘中\uE333",bean.group,false);
            }
            return;
        }*/
        if(bean != null){
            ArrayList<SscBean> sscBeans = mXiazuMap.get(bean.group);
            for(int i = 0 ;i < sscBeans.size();i++){
                SscBean bean2 = sscBeans.get(i);
                XposedBridge.log("bean2 id="+bean2.mId);
                if(bean2.msgId ==msgId){
                    DateSaveManager.getIntance().changeGroupFenOrLiang(bean.group,true,1,bean2.count);
                    DateSaveManager.getIntance().changeGroupFenOrLiang(bean.group,false,2,bean2.count);
                    SscControl.getIntance().sendMessage("["+bean2.mId+" ]"+bean2.msg+"\n--------------\n[捂脸][捂脸]退成功    补"+bean2.count+"\n剩余"+
                                    DateSaveManager.getIntance().getGroup( bean.group).fen,
                            bean.group,false);
                    sscBeans.remove(bean2);
                    mAllXiazu.remove(bean2.mId);
                    return;
                }
            }
        }
    }

    private void tuiDeal(MessageDeal.MessagerDealDate data){
   /*     if( TextUtils.isEmpty(data.groupID) ||
                !DateSaveManager.getIntance().isHaveGroup(data.groupID)||
                !TextUtils.isEmpty(DateSaveManager.getIntance().getGroup(data.groupID).toGroup)){
            return;
        }
        if(!DateSaveManager.getIntance().getGroup(data.groupID).isIntime){
            SscControl.getIntance().sendMessage("\uE333操作失败-封潘中\uE333",data.groupID,false);
            return;
        }*/
        long id = -1;
        try{
            id = Integer.parseInt(data.message.replace(MessageDeal.SET_TUI,""));
        }catch (Exception e){
            return;
        }


        if(mAllXiazu.containsKey(id)){
            XposedBridge.log("mAllXiazu.containsKey id="+id);
            AllXiazuBean bean = mAllXiazu.get(id);

            if(TextUtils.isEmpty(bean.group)) {
                XposedBridge.log("群号为空");
                SscControl.getIntance().sendMessage("\uE333["+id+" ]退失败\uE333",data.groupID,false);
                return;
            }else if(!DateSaveManager.getIntance().isHaveGroup(data.groupID)&&!data.groupID.equals(DateSaveManager.getIntance().mZongQun)){
                XposedBridge.log("不是报错群");
                return;
            }else if(DateSaveManager.getIntance().isHaveGroup(data.groupID) && !DateSaveManager.getIntance().getGroup(data.groupID).isEnable){
                XposedBridge.log("没有开群");
                return;
            }else if(DateSaveManager.getIntance().isHaveGroup(data.groupID) && !bean.group.equals(data.groupID)){
                XposedBridge.log("不是下注的群");
                SscControl.getIntance().sendMessage("\uE333["+id+" ]退失败\uE333",data.groupID,false);
                return;

            }

            ArrayList<SscBean> sscBeans = mXiazuMap.get(bean.group);
            if(sscBeans != null){
                for(int i = 0 ;i < sscBeans.size();i++){
                    SscBean bean2 = sscBeans.get(i);
                    XposedBridge.log("bean2 id="+bean2.mId);
                    if(bean2.mId ==id ){
                        DateSaveManager.getIntance().changeGroupFenOrLiang(bean.group,true,1,bean2.count);
                        DateSaveManager.getIntance().changeGroupFenOrLiang(bean.group,false,2,bean2.count);
                        SscControl.getIntance().sendMessage("["+bean2.mId+" ]"+bean2.msg+"\n--------------\n[捂脸][捂脸]退成功    补"+bean2.count+"\n剩余"+
                                DateSaveManager.getIntance().getGroup( bean.group).fen,data.groupID,false);
                        sscBeans.remove(bean2);
                        mAllXiazu.remove(id);
                        return;
                    }
                }
            }

        }else{
            XposedBridge.log("! mAllXiazu.containsKey id="+id);
        }
        SscControl.getIntance().sendMessage("\uE333["+id+" ]退失败\uE333",data.groupID,false);
    }

    private void editDeal(MessageDeal.MessagerDealDate data){
        long id = -1;
        try{
            String[] strs =data.message.split(MessageDeal.SET_EDIT);
            data.message = strs[1];
            id = Integer.parseInt(strs[0]);
            XposedBridge.log("id = "+id);
        }catch (Exception e){
            XposedBridge.log(e.toString());
            return;
        }
        if(mAllXiazu.containsKey(id)){
            AllXiazuBean bean = mAllXiazu.get(id);
            if(TextUtils.isEmpty(bean.group)) {
                SscControl.getIntance().sendMessage(  "\uE333改"+id+" 失败\uE333", data.groupID,false);
                return;
            }else if(!DateSaveManager.getIntance().isHaveGroup(data.groupID)&&!data.groupID.equals(DateSaveManager.getIntance().mZongQun)){
                //XposedBridge.log("不是报错群");
                return;
            }else if(DateSaveManager.getIntance().isHaveGroup(data.groupID) && !DateSaveManager.getIntance().getGroup(data.groupID).isEnable){
                //XposedBridge.log("没有开群");
                return;
            }else if(DateSaveManager.getIntance().isHaveGroup(data.groupID) && !bean.group.equals(data.groupID)){
                //XposedBridge.log("不是下注群");
                SscControl.getIntance().sendMessage(  "\uE333改"+id+" 失败\uE333", data.groupID,false);
                return;
            }
            xiazhu(data.message,bean.group,bean.date.msgId,id,data.groupID);
        }else{
            SscControl.getIntance().sendMessage(  "\uE333改"+id+" 失败\uE333", data.groupID,false);
        }
    }

    private int getZhongjianCount(DateBean2 bean ,int[] numbers,int yin){
        int count= 0;
        for(int i = 0; i< bean.local.size();i++){
            Integer[] local = bean.local.get(i);
            for(int ii= 0;ii<  bean.mLastData.size();ii++){
                Integer[] data = bean.mLastData.get(ii);
                if(numbers[ local[0]-1 ] == data[0] && numbers[ local[1]-1 ] == data[1]){
                    count = count + bean.mCountList.get(i)*yin;
                }
            }
        }
        return count;
    }

    private void xiazhu(String message,String groupID,long msgId,long id,String sendGroup){
        boolean isEdit = false;
        AllXiazuBean xiazuBean = null;
        if(DateSaveManager.getIntance().getGroup(groupID).isIntime ||
                (!DateSaveManager.getIntance().getGroup(groupID).isIntime && id != -1 )  ){
            SscBean bean;
            if(id == -1){
                bean = new SscBean();
                bean.msg =message;
                bean.mId = mId;
                bean.msgId =msgId;
                mId++;
                if(mXiazuMap.containsKey(groupID)){
                    ArrayList<SscBean> mgroupList = mXiazuMap.get(groupID);
                    mgroupList.add(bean);
                }else{
                    ArrayList<SscBean> l = new  ArrayList<>();
                    l.add(bean);
                    mXiazuMap.put(groupID,l);
                }
                AllXiazuBean allXiazuBean = new AllXiazuBean();
                allXiazuBean.date = bean;
                allXiazuBean.group = groupID;
                mAllXiazu.put(bean.mId,allXiazuBean);
            }else{
                if(mAllXiazu.containsKey(id)){
                    isEdit = true;
                    xiazuBean = mAllXiazu.get(id);
                    bean =xiazuBean.date;
                }else{
                    if(TextUtils.isEmpty(sendGroup)){
                        SscControl.getIntance().sendMessage(  "\uE333改"+id+" 失败\uE333", groupID,false);
                    }else{
                        SscControl.getIntance().sendMessage(  "\uE333改"+id+" 失败\uE333", sendGroup,false);
                    }

                    return;
                }

            }
            ArrayList<DateBean2> dateBean2s = null;
            RegularStrBean result = null;
            try {
                result = RegularUtils2.regularStr(message);
                if(result != null){
                    dateBean2s = result.list;
                }

            }catch (Exception e){

            }

            if(dateBean2s == null || dateBean2s.size() < 1){
                    if(TextUtils.isEmpty(sendGroup)){
                        SscControl.getIntance().sendMessage(  "[改"+bean.mId+" ]"+(isEdit?( bean.msg+"->"):"")+message+"\n\uE333下驻失败，请修改格式\uE333",groupID,false);
                    }else if(!sendGroup.equals(DateSaveManager.getIntance().mZongQun)){
                        SscControl.getIntance().sendMessage(  "[改"+bean.mId+" ]"+(isEdit?( bean.msg+"->"):"")+message+"\n\uE333下驻失败，请修改格式\uE333",sendGroup,false);
                    }

                if(!TextUtils.isEmpty( DateSaveManager.getIntance().mZongQun)){
                    SscControl.getIntance().sendMessage(  bean.mId+"改"+message+"\n\uE333解析失败\uE333", DateSaveManager.getIntance().mZongQun,false);
                }

                return;
            }
            for(DateBean2 dateBeanEach : dateBean2s){
                if(dateBeanEach.mCountList== null ||
                        dateBeanEach.mCountList.size() == 0 ||
                        dateBeanEach.local == null ||
                        dateBeanEach.local.size() == 0||
                        dateBeanEach.local.size() != dateBeanEach.mCountList.size() ){

                    if(TextUtils.isEmpty(sendGroup)){
                        SscControl.getIntance().sendMessage( "[改"+bean.mId+" ]"+(isEdit?( bean.msg+"->"):"")+message+"\n\uE333下驻失败，请修改格式\uE333",groupID,false);
                    }else if(!sendGroup.equals(DateSaveManager.getIntance().mZongQun)){
                        SscControl.getIntance().sendMessage( "[改"+bean.mId+" ]"+(isEdit?( bean.msg+"->"):"")+message+"\n\uE333下驻失败，请修改格式\uE333",sendGroup,false);
                    }


                    if(!TextUtils.isEmpty( DateSaveManager.getIntance().mZongQun)){
                        SscControl.getIntance().sendMessage(  bean.mId+"改"+message+"\n\uE333解析失败\uE333", DateSaveManager.getIntance().mZongQun,false);


                    }

                    return;
                }
                for(Integer[] tmp : dateBeanEach.local){
                    if(tmp  == null ||
                            tmp.length != 2||
                            tmp[0] <=0 ||  tmp[1] <=0 ||tmp[0] >5 || tmp[1] >5){
                        if(tmp.length == 2 && tmp[1]== 0 &&  tmp[0] > 0 && tmp[0] <= 5 && dateBeanEach.local.size() == 1 &&
                                dateBeanEach.mDataList != null && dateBeanEach.mDataList.size() == 2 && dateBeanEach.mDataList.get(1).size() == 10){
                            if(tmp[0]== 5){
                                tmp[1] = 4;
                            }else{
                                tmp[1] = 5;
                            }
                            break;
                        }

                        if(TextUtils.isEmpty(sendGroup)){
                            SscControl.getIntance().sendMessage( "[改"+bean.mId+" ]"+(isEdit?( bean.msg+"->"):"")+message+"\n\uE333下驻失败，请修改格式\uE333",groupID,false);
                        }else if(!sendGroup.equals(DateSaveManager.getIntance().mZongQun)){
                            SscControl.getIntance().sendMessage( "[改"+bean.mId+" ]"+(isEdit?( bean.msg+"->"):"")+message+"\n\uE333下驻失败，请修改格式\uE333",sendGroup,false);
                        }

                        if(!TextUtils.isEmpty( DateSaveManager.getIntance().mZongQun)){
                            SscControl.getIntance().sendMessage(  bean.mId+"改"+message+"\n\uE333解析失败\uE333", DateSaveManager.getIntance().mZongQun,false);


                        }

                        return;
                    }
                }
                for(Integer tmp : dateBeanEach.mCountList){

                    if(tmp  == null ||tmp <= 0){
                        if(TextUtils.isEmpty(sendGroup)){
                            SscControl.getIntance().sendMessage( "[改"+bean.mId+" ]"+(isEdit?( bean.msg+"->"):"")+message+"\n\uE333下驻失败，请修改格式\uE333",groupID,false);
                        }else if(!sendGroup.equals(DateSaveManager.getIntance().mZongQun)){
                            SscControl.getIntance().sendMessage( "[改"+bean.mId+" ]"+(isEdit?( bean.msg+"->"):"")+message+"\n\uE333下驻失败，请修改格式\uE333",sendGroup,false);
                        }

                        if(!TextUtils.isEmpty( DateSaveManager.getIntance().mZongQun)){
                            SscControl.getIntance().sendMessage(  bean.mId+"改"+message+"\n\uE333解析失败\uE333", DateSaveManager.getIntance().mZongQun,false);

                        }

                        return;
                    }
                }
            }

            bean.list = dateBean2s;
            String str = "";
            int count  = 0;
            for(DateBean2 tmp : dateBean2s){
                count += tmp.allCount;
                str = str+ tmp.toString()+"\n";
            }
            if( DateSaveManager.getIntance().getGroup(groupID).xianer  && count> DateSaveManager.getIntance().getGroup(groupID).fen ){
                bean.list= null;
                if(TextUtils.isEmpty(sendGroup)){
                    SscControl.getIntance().sendMessage( message+"\n\uE333操作失败-积分不足\uE333",groupID,false);
                }else if(!sendGroup.equals(DateSaveManager.getIntance().mZongQun)){
                    SscControl.getIntance().sendMessage( message+"\n\uE333操作失败-积分不足\uE333",sendGroup,false);
                }

                return;
            }

            bean.count = count;

            DateSaveManager.getIntance().changeGroupFenOrLiang(groupID,true,2,count);
            DateSaveManager.getIntance().changeGroupFenOrLiang(groupID,false,1,count);
            String end = "-------------------\n扣"+bean.count+"   剩余"+DateSaveManager.getIntance().getGroup(groupID).fen+"\n退单撤回下注信息或者发送：退"+bean.mId;

            if(!result.isTrue && !TextUtils.isEmpty(DateSaveManager.getIntance().mTixing)){
      //          if(xiazuBean != null){
                    SscControl.getIntance().sendMessage(DateSaveManager.getIntance().getGroup(groupID).groupName+"\n"+ message+"\n--------------\n"+str+end,DateSaveManager.getIntance().mTixing,false);
      //          }
            }
            if(!TextUtils.isEmpty(DateSaveManager.getIntance().mZhengque)){
                SscControl.getIntance().sendMessage(message+"\n--------------\n"+str+end,DateSaveManager.getIntance().mZhengque,false);
            }
            if(TextUtils.isEmpty(sendGroup)){
                SscControl.getIntance().sendMessage(message+"\n--------------\n"+str+end,groupID,false);
            }else{
                SscControl.getIntance().sendMessage(message+"\n--------------\n"+str+end,sendGroup,false);
            }



        }else{
            if(TextUtils.isEmpty(sendGroup)){
                SscControl.getIntance().sendMessage("\uE333操作失败-封潘中\uE333",groupID,false);
            }else{
                SscControl.getIntance().sendMessage("\uE333操作失败-封潘中\uE333",sendGroup,false);
            }

        }
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
    public boolean isAllNumber(String str){
    //    str = str.replaceAll(" ","");
        char[] chars = str.toCharArray();
        for(char c: chars){
            if(c < '0' || c > '9'){
                return false;
            }
        }
        return true;
    }

    private static ServerManager2 mIntance = new ServerManager2();
    public static ServerManager2 getmIntance(){
        return mIntance;
    }
    private ServerManager2(){

    }
}