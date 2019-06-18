package jackzheng.study.com.wechat.sscManager;

import android.os.Debug;
import android.text.TextUtils;
import android.util.ArrayMap;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import de.robv.android.xposed.XposedBridge;
import jackzheng.study.com.wechat.CommonDeal;
import jackzheng.study.com.wechat.HtmlParse;
import jackzheng.study.com.wechat.MessageDeal;
import jackzheng.study.com.wechat.SscControl;
import jackzheng.study.com.wechat.net.Kaijiangback;
import jackzheng.study.com.wechat.net.NetServerControl;
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

    public final static int OPEN_INDEX = 38;
    public int mOpenIndex = -1;

    public String mMysId = "";

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
        if(!groupDate.isIntime){
            return;
        }
        if(!TextUtils.isEmpty(groupDate.toGroup) && DateSaveManager.getIntance().getGroup(groupDate.toGroup).isEnable ){
            xiazuGroup = groupDate.toGroup;

            //SscControl.getIntance().sendMessage(data.message,DateSaveManager.getIntance().getGroup(data.groupID).toGroup,false);
            //return;
        }
        if(! DateSaveManager.getIntance().getGroup(xiazuGroup).isEnable ){
            return;
        }

        xiazhu(data.message,xiazuGroup,msgId,-1,null);
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
                DateSaveManager.getIntance().getGroup(goupID).isIntime = false;
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

    public class ZhongDate{
        public float  zhong2Count = 0;
        public float  zhong3Count = 0;
        public float  zhong4Count = 0;

        public float  zhong2Money = 0;
        public float  zhong3Money = 0;
        public float  zhong4Money = 0;

        public  void toPoint2(){
            if(zhong2Count != 0){
                zhong2Count = dealPoint(zhong2Count);
                zhong2Money = dealPoint(zhong2Money);
            }
            if(zhong3Count != 0){
                zhong3Count = dealPoint(zhong3Count);
                zhong3Money = dealPoint(zhong3Money);
            }
            if(zhong4Count != 0){
                zhong4Count = dealPoint(zhong4Count);
                zhong4Money = dealPoint(zhong4Money);
            }
        }
        private float dealPoint(float a){
            
            BigDecimal bd = new BigDecimal((float) a);
            bd = bd.setScale(2, 4);
            a = bd.floatValue();

            return a;
        }

    }

    public void kaijiangEnd(Kaijiangback data){

        String indexNumber = data.index<10?("00"+ data.index):data.index<100?("0"+data.index):""+data.index;
        String msgRoot="\uE12D\uE12D 第["+indexNumber+"]届      开："+data.openNumber+"\n";


        ArrayMap<String, DateSaveManager.GroupDate> allGroup = DateSaveManager.getIntance().getAllGroup();
        ZhongDate count;
        StringBuilder str ;
        if(data != null && data.date != null && data.date.size() > 0){
            for(Kaijiangback.KaijiangGroupBackData group :data.date){
                str = new StringBuilder();
                ArrayList<SscBean> xiazu = mXiazuMap.get(group.group);
                mXiazuMap.remove(group.group);
                count = new ZhongDate();
                for(Kaijiangback.KaijiangGroupBackDataItem bean :group.date){
                    SscBean sscHave = null;
                    for(SscBean ssc : xiazu){
                        ssc.mId = bean.id;
                        sscHave = ssc;
                        break;
                    }
                    bean.date.toPoint2();


                    String zhongTmpStr = "";
                    if(bean.date.zhong2Count != 0){
                        zhongTmpStr+=("②星 "+sscHave.msg+" 中"+bean.date.zhong2Count+" 共"+bean.date.zhong2Money);
                    }
                    if(bean.date.zhong3Count != 0){
                        zhongTmpStr+=("③星 "+sscHave.msg+" 中"+bean.date.zhong3Count+" 共"+bean.date.zhong3Money);
                    }
                    if(bean.date.zhong4Count != 0){
                        zhongTmpStr+=("④星 "+sscHave.msg+" 中"+bean.date.zhong4Count+" 共"+bean.date.zhong4Money);
                    }

                    count.zhong2Money += bean.date.zhong2Money;
                    count.zhong2Count += bean.date.zhong2Count;
                    count.zhong3Money += bean.date.zhong3Money;
                    count.zhong3Count += bean.date.zhong3Count;
                    count.zhong4Money += bean.date.zhong4Money;
                    count.zhong4Count += bean.date.zhong4Count;
                }
                str.append("\n");
                float a = count.zhong2Money+count.zhong3Money+count.zhong4Money;
                if(a > 0){
                    DateSaveManager.getIntance().changeGroupFenOrLiang(group.group,true,1,a);
                }

                String zhongTmpStr= "";
                if(count.zhong2Count != 0){
                    zhongTmpStr+=("②星 "+count.zhong2Count+" 共"+count.zhong2Money+"\n");
                }
                if(count.zhong3Count != 0){
                    zhongTmpStr+=("③星 "+count.zhong3Count+" 共"+count.zhong3Money+"\n");
                }
                if(count.zhong4Count != 0){
                    zhongTmpStr+=("④星 "+count.zhong4Count+" 共"+count.zhong4Money+"\n");
                }
                str.append(zhongTmpStr);

                str.append("共计 "+(count.zhong2Money+count.zhong3Money+count.zhong4Money)+"\n");
                str.append(" 剩余"+DateSaveManager.getIntance().getGroup(group.group).fen+" 量"+DateSaveManager.getIntance().getGroup(group.group).liang);
                if(DateSaveManager.getIntance().getGroup(group.group).isEnable){
                    if(!TextUtils.isEmpty( DateSaveManager.getIntance().mBaobiao)){
                        SscControl.getIntance().sendMessage(  DateSaveManager.getIntance().getGroup(group.group).groupName+"\n"+
                                msgRoot+"-------------------\n"+str.toString(), DateSaveManager.getIntance().mBaobiao,false);

                    }else{
                        SscControl.getIntance().sendMessage(  msgRoot+"-------------------\n"+str.toString(), group.group,false);
                        SscControl.getIntance().sendMessage(  "共中上"+(count.zhong2Money+count.zhong3Money+count.zhong4Money)+",剩余 "+DateSaveManager.getIntance().getGroup(group.group).fen,
                                group.group,false);
                    }
                }
                if(data.index == 38  ){
                    DateSaveManager.GroupDate g = DateSaveManager.getIntance().getGroup(group.group);
                    g.isIntime = false;
                    if(DateSaveManager.getIntance().getGroup(group.group).isEnable){
                        SscControl.getIntance().sendMessage(  "当前分"+g.fen+"亮"+g.liang+"\n今日停潘", group.group,false);
                    }

                    DateSaveManager.getIntance().clearAllGroupFenAndLiang();

                }else if(data.index == OPEN_INDEX && DateSaveManager.getIntance().getGroup(group.group).isEnable){
                    DateSaveManager.GroupDate g = DateSaveManager.getIntance().getGroup(group.group);
                    g.isIntime = true;
                    if(DateSaveManager.getIntance().getGroup(group.group).isEnable){
                        SscControl.getIntance().sendMessage(  "当前分"+g.fen+"亮"+g.liang+"\n开始下驻", group.group,false);
                    }
                }

            }
        }
        for(String goupID : allGroup.keySet()){
            if(!TextUtils.isEmpty(DateSaveManager.getIntance().getGroup(goupID).toGroup)){
                continue;
            }
            str = new StringBuilder();
            str.append(" 剩余"+DateSaveManager.getIntance().getGroup(goupID).fen+" 量"+DateSaveManager.getIntance().getGroup(goupID).liang);

            if(DateSaveManager.getIntance().getGroup(goupID).isEnable){
                if(!TextUtils.isEmpty( DateSaveManager.getIntance().mBaobiao)){
                    SscControl.getIntance().sendMessage(  DateSaveManager.getIntance().getGroup(goupID).groupName+"\n"+
                            msgRoot+"-------------------\n"+str.toString(), DateSaveManager.getIntance().mBaobiao,false);

                }else{
                    SscControl.getIntance().sendMessage(  msgRoot+"-------------------\n"+str.toString(), goupID,false);
                    SscControl.getIntance().sendMessage(  "共中上 0,剩余 "+DateSaveManager.getIntance().getGroup(goupID).fen,
                            goupID,false);
                }
            }
            if(data.index == 38  ){
                DateSaveManager.GroupDate g = DateSaveManager.getIntance().getGroup(goupID);
                g.isIntime = false;
                if(DateSaveManager.getIntance().getGroup(goupID).isEnable){
                    SscControl.getIntance().sendMessage(  "当前分"+g.fen+"亮"+g.liang+"\n今日停潘", goupID,false);
                }

                DateSaveManager.getIntance().clearAllGroupFenAndLiang();

            }else if(data.index == OPEN_INDEX && DateSaveManager.getIntance().getGroup(goupID).isEnable){
                DateSaveManager.GroupDate g = DateSaveManager.getIntance().getGroup(goupID);
                g.isIntime = true;
                if(DateSaveManager.getIntance().getGroup(goupID).isEnable){
                    SscControl.getIntance().sendMessage(  "当前分"+g.fen+"亮"+g.liang+"\n开始下驻", goupID,false);
                }
            }
        }
        mXiazuMap.clear();
        mAllMessage.clear();
        mAllXiazu.clear();
        mId = 1;
        if(data.index != 38){
            for(String g : allGroup.keySet()){
                allGroup.get(g).isIntime = true;
            }
        }else{
            SscControl.getIntance().sendMessage(  "总共"+(SscControl.getIntance().mMessageCount+1), DateSaveManager.getIntance().mGuanliQun,false);
        }

    }

    public void kaijiangDeal(HtmlParse.MaxIndexResult parse){
        ServerManager2.getmIntance().mOpenIndex = parse.index;
        ArrayMap<String, DateSaveManager.GroupDate> all = DateSaveManager.getIntance().getAllGroup();
        if(parse.index  > 38 && parse.index < OPEN_INDEX){

            for(String g : all.keySet()){
                all.get(g).isIntime = false;
            }
            return;
        }
       NetServerControl.getIntance().getKaijiang(mXiazuMap,parse.index,parse.str);
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
       if(! TextUtils.isEmpty(data.groupID) &&
                DateSaveManager.getIntance().isHaveGroup(data.groupID)&&
                !TextUtils.isEmpty(DateSaveManager.getIntance().getGroup(data.groupID).toGroup)){
            return;
        }/*
        if(!D ateSaveManager.getIntance().getGroup(data.groupID).isIntime){
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

        }else if(!TextUtils.isEmpty(data.groupID) &&! data.groupID.equals(DateSaveManager.getIntance().mZongQun)){
            SscControl.getIntance().sendMessage("\uE333["+id+" ]退失败\uE333",data.groupID,false);
        }

    }

    private void editDeal(MessageDeal.MessagerDealDate data){
        if(! TextUtils.isEmpty(data.groupID) &&
                DateSaveManager.getIntance().isHaveGroup(data.groupID)&&
                !TextUtils.isEmpty(DateSaveManager.getIntance().getGroup(data.groupID).toGroup)){
            return;
        }
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
        }else if(!TextUtils.isEmpty(data.groupID) && !data.groupID.equals(DateSaveManager.getIntance().mZongQun)){
            SscControl.getIntance().sendMessage(  "\uE333改"+id+" 失败\uE333", data.groupID,false);
        }
    }



    public void xiazhuFault(long netId){
        AllXiazuBean xiazuBeanByNetBean = getXiazuBeanByNetBean(netId);

        if(TextUtils.isEmpty(xiazuBeanByNetBean.sendGroup)){
            SscControl.getIntance().sendMessage(  xiazuBeanByNetBean.date.mId+"改"+xiazuBeanByNetBean.date.msg+"\n\uE333解析失败\uE333", xiazuBeanByNetBean.group,false);
        }else if(!xiazuBeanByNetBean.sendGroup.equals(DateSaveManager.getIntance().mZongQun)){
            SscControl.getIntance().sendMessage(  xiazuBeanByNetBean.date.mId+"改"+xiazuBeanByNetBean.date.msg+"\n\uE333解析失败\uE333", xiazuBeanByNetBean.sendGroup,false);
        }

        if(!TextUtils.isEmpty( DateSaveManager.getIntance().mZongQun)){
            SscControl.getIntance().sendMessage(  xiazuBeanByNetBean.date.mId+"改"+xiazuBeanByNetBean.date.msg+"\n\uE333解析失败\uE333", DateSaveManager.getIntance().mZongQun,false);
        }


    }
    public void xiazhuSuccess(long netId,SscBean bean){
        AllXiazuBean xiazuBeanByNetBean = getXiazuBeanByNetBean(netId);
        xiazuBeanByNetBean.date.list = bean.list;
        if( DateSaveManager.getIntance().getGroup(xiazuBeanByNetBean.group).xianer  && xiazuBeanByNetBean.date.count> DateSaveManager.getIntance().getGroup(xiazuBeanByNetBean.group).fen ){
            xiazuBeanByNetBean.date.list= null;
            if(TextUtils.isEmpty(xiazuBeanByNetBean.sendGroup)){
                SscControl.getIntance().sendMessage( xiazuBeanByNetBean.date.msg+"\n\uE333操作失败-积分不足\uE333",xiazuBeanByNetBean.group,false);
            }else if(!xiazuBeanByNetBean.sendGroup.equals(DateSaveManager.getIntance().mZongQun)){
                SscControl.getIntance().sendMessage( xiazuBeanByNetBean.date.msg+"\n\uE333操作失败-积分不足\uE333",xiazuBeanByNetBean.sendGroup,false);
            }

            return;
        }
        String str ="";
        for(DateBean2 tmp : xiazuBeanByNetBean.date.list){
            xiazuBeanByNetBean.date.count += tmp.allCount;
            str = str+ tmp.toString()+"\n";
        }

        DateSaveManager.getIntance().changeGroupFenOrLiang(xiazuBeanByNetBean.group,true,2,xiazuBeanByNetBean.date.count);
        DateSaveManager.getIntance().changeGroupFenOrLiang(xiazuBeanByNetBean.group,false,1,xiazuBeanByNetBean.date.count);
        String end = "-------------------\n扣"+bean.count+"   剩余"+DateSaveManager.getIntance().getGroup(xiazuBeanByNetBean.group).fen+"\n退单撤回下注信息或者发送：退"+bean.mId;

       /* if(!result.isTrue && !TextUtils.isEmpty(DateSaveManager.getIntance().mTixing)){
            //          if(xiazuBean != null){
            SscControl.getIntance().sendMessage(DateSaveManager.getIntance().getGroup(xiazuBeanByNetBean.group).groupName+"\n"+ xiazuBeanByNetBean.date.msg+"\n\n--------------\n"+str+end,DateSaveManager.getIntance().mTixing,false);
            //          }
        }*/
        if(!TextUtils.isEmpty(DateSaveManager.getIntance().mZhengque)){
            SscControl.getIntance().sendMessage(xiazuBeanByNetBean.date.msg+"\n\n--------------\n"+str+end,DateSaveManager.getIntance().mZhengque,false);
        }
        if(TextUtils.isEmpty(xiazuBeanByNetBean.sendGroup)){
            SscControl.getIntance().sendMessage(xiazuBeanByNetBean.date.msg+"\n\n--------------\n"+str+end,xiazuBeanByNetBean.group,false);
        }else{
            SscControl.getIntance().sendMessage(xiazuBeanByNetBean.date.msg+"\n\n--------------\n"+str+end,xiazuBeanByNetBean.sendGroup,false);
        }

    }



    private AllXiazuBean getXiazuBeanByNetBean(long netId){
        Set<Long> longs = mAllXiazu.keySet();
        for(long key : longs){
            if(mAllXiazu.get(key).netId == netId){
                return mAllXiazu.get(key);
            }
        }
        return null;
    }
    private void xiazhu(String message,String groupID,long msgId,long id,String sendGroup){
        XposedBridge.log("xiazhu ");
        boolean isEdit = false;
        AllXiazuBean xiazuBean = null;
        if(DateSaveManager.getIntance().getGroup(groupID).isIntime ||
                (!DateSaveManager.getIntance().getGroup(groupID).isIntime && id != -1 )  ){
            SscBean bean;
            if(id == -1){
                bean = new SscBean();
                bean.msg =message;
                bean.mId = mId+DateSaveManager.getIntance().mRobatIndex*150;
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
                xiazuBean = new AllXiazuBean();
                xiazuBean.date = bean;
                xiazuBean.group = groupID;
                xiazuBean.netId = NetServerControl.getIntance().getNetId();
                xiazuBean.sendGroup =sendGroup;
                xiazuBean.isEdit = false;
                mAllXiazu.put(bean.mId,xiazuBean);
            }else{
                if(mAllXiazu.containsKey(id)){
                    isEdit = true;
                    xiazuBean = mAllXiazu.get(id);
                    xiazuBean.netId = NetServerControl.getIntance().getNetId();
                    xiazuBean.isEdit = true;
                    xiazuBean.sendGroup =sendGroup;
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
            NetServerControl.getIntance().getRegular(xiazuBean.netId,message,DateSaveManager.getIntance().isThird);

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