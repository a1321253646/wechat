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

public class ServerManager2 {
    public boolean isTime = true;
    public boolean isInit = false;
    public ArrayMap<String , ArrayList<SscBean> > mXiazuMap = new ArrayMap<>();
    public long mId = 1;

    public int mJieId = 1;


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
                isTime&&
                !DateSaveManager.getIntance().isJustShou&&
            !TextUtils.isEmpty(data.groupID) &&
            DateSaveManager.getIntance().isHaveGroup(data.groupID) &&
            DateSaveManager.getIntance().getGroup(data.groupID).isEnable){
            tuiDeal(data);
            return;
        }else if(data.type == MessageDeal.SET_EDIT_INT &&
                isTime&&
                !DateSaveManager.getIntance().isJustShou&&
                !TextUtils.isEmpty(data.groupID) ){
            editDeal(data);
            return;
        }else if(data.type == MessageDeal.SET_DETAIL_INT &&
                !DateSaveManager.getIntance().isJustShou&&
                !TextUtils.isEmpty(data.groupID) &&
                DateSaveManager.getIntance().isHaveGroup(data.groupID) &&
                DateSaveManager.getIntance().getGroup(data.groupID).isEnable){
            StringBuilder str1 = new StringBuilder();
            long count = 0;
            if(mXiazuMap.containsKey(data.groupID)){

                ArrayList<SscBean> xiazu = mXiazuMap.get(data.groupID);
                for(SscBean bean : xiazu){
                    if(bean.list == null){
                        str1.append(bean.msg+" 无法识别");
                        str1.append("\n----------------------------\n");
                    }else{
                        count += bean.count;
                        str1.append(bean.toString());
                        str1.append("\n----------------------------\n");
                    }

                }

            }
            str1.append("共"+count+" 剩余"+DateSaveManager.getIntance().getGroup(data.groupID).fen);
            if(DateSaveManager.getIntance().getGroup(data.groupID).isEnable){
                SscControl.getIntance().sendMessage(  "**************************\n"+str1.toString(),
                        data.groupID,false);
            }
            return;
        }

        if(!haveNumber(data.message)){
            return;
        }
        if(DateSaveManager.getIntance().getGroup(data.groupID) == null){
            return ;
        }
        if(!TextUtils.isEmpty(DateSaveManager.getIntance().getGroup(data.groupID).toGroup)){
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
            }else if(str2.startsWith("中")){
                str2 = str2.replace("中","");
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
            SscControl.getIntance().sendMessage(data.message,DateSaveManager.getIntance().getGroup(data.groupID).toGroup,false);
            return;
        }
        if(DateSaveManager.getIntance().isJustShou || ! DateSaveManager.getIntance().getGroup(data.groupID).isEnable){
            return;
        }

        xiazhu(data.message,data.groupID,msgId,-1);
    }
    public void stopDeal(int index){
        if(DateSaveManager.getIntance().isJustShou ){
            return;
        }
        String msgRoot=index+"停：\n";
        isTime = false;
        StringBuilder str;
        int count = 0;

        ArrayMap<String , DateSaveManager.GroupDate> all = DateSaveManager.getIntance().getAllGroup();
        for(String goupID : all.keySet()){
            count = 0;
            str = new StringBuilder();
            str.append(msgRoot);
            if(mXiazuMap.containsKey(goupID)){
                ArrayList<SscBean> xiazu = mXiazuMap.get(goupID);
                for(SscBean bean : xiazu){
                    if(bean.list == null){
                        str.append(bean.msg+" 无法识别");
                        str.append("\n----------------------------\n");
                    }else{
                        count+= bean.count;
                        str.append(bean.toString());
                        str.append("\n----------------------------\n");
                    }

                }

            }
            str.append("共"+count+" 剩余"+DateSaveManager.getIntance().getGroup(goupID).fen);
            if(DateSaveManager.getIntance().getGroup(goupID).isEnable){
             //   if(!TextUtils.isEmpty( DateSaveManager.getIntance().mZongQun)){
                    SscControl.getIntance().sendMessage(  "**************************\n"+str.toString(),
                            goupID,false);
             //       SscControl.getIntance().sendMessage("**************************\n"+msgRoot,goupID,false);
           //     }else{
             //       SscControl.getIntance().sendMessage("**************************\n"+str.toString(),goupID,false);
         //       }
            }

        }

    }

    public void kaijiangDeal(HtmlParse.MaxIndexResult parse){

        String msgRoot=parse.index+"开："+parse.str+"\n";


        StringBuilder str;

        int count;
        int eachCount ;

        ArrayMap<String, DateSaveManager.GroupDate> allGroup = DateSaveManager.getIntance().getAllGroup();

        for(String goupID : allGroup.keySet()){
            count = 0;
            str = new StringBuilder();
            str.append(msgRoot);
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
                        str.append(" 中 "+(eachCount/yin)+"\n--------------------------------------\n");
                        count+=eachCount;
                    }
                }
                if(count > 0){
                    DateSaveManager.getIntance().changeGroupFenOrLiang(goupID,true,1,count);
                }
            }
            str.append("共中"+(count/yin)+"组 共计 "+count+"\n");
            str.append(" 剩余"+DateSaveManager.getIntance().getGroup(goupID).fen);
            if(!DateSaveManager.getIntance().isJustShou && DateSaveManager.getIntance().getGroup(goupID).isEnable){
               // if(!TextUtils.isEmpty( DateSaveManager.getIntance().mZongQun)){
                    SscControl.getIntance().sendMessage(  "##########################\n"+str.toString(), goupID,false);
                //    SscControl.getIntance().sendMessage("##########################\n"+msgRoot,goupID,false);
               // }else{
                 //   SscControl.getIntance().sendMessage("##########################\n"+str.toString(),goupID,false);
               // }
            }
        }
        mXiazuMap.clear();
        mAllMessage.clear();
        mAllXiazu.clear();
        mId = 1;
        isTime = true;
    }

    public void tuiDeal(long msgId){
        AllXiazuBean bean = null;
        for(long key :mAllXiazu.keySet()){
            if(mAllXiazu.get(key).date.msgId == msgId){
                bean = mAllXiazu.get(key);
            }
        }
        if(bean != null){
            ArrayList<SscBean> sscBeans = mXiazuMap.get(bean.group);
            for(int i = 0 ;i < sscBeans.size();i++){
                SscBean bean2 = sscBeans.get(i);
                XposedBridge.log("bean2 id="+bean2.mId);
                if(bean2.msgId ==msgId){
                    DateSaveManager.getIntance().changeGroupFenOrLiang(bean.group,true,1,bean2.count);
                    DateSaveManager.getIntance().changeGroupFenOrLiang(bean.group,false,2,bean2.count);
                    SscControl.getIntance().sendMessage("["+bean2.mId+" ]"+bean2.msg+"\n退成功",bean.group,false);
                    sscBeans.remove(bean2);
                    mAllXiazu.remove(bean2.mId);
                    return;
                }
            }
        }
    }

    private void tuiDeal(MessageDeal.MessagerDealDate data){
        long id = -1;
        try{
            id = Integer.parseInt(data.message.replace(MessageDeal.SET_TUI,""));
        }catch (Exception e){
            return;
        }


        if(mAllXiazu.containsKey(id)){
            XposedBridge.log("mAllXiazu.containsKey id="+id);
            AllXiazuBean bean = mAllXiazu.get(id);
            ArrayList<SscBean> sscBeans = mXiazuMap.get(bean.group);
            for(int i = 0 ;i < sscBeans.size();i++){
                SscBean bean2 = sscBeans.get(i);
                XposedBridge.log("bean2 id="+bean2.mId);
                if(bean2.mId ==id){
                    DateSaveManager.getIntance().changeGroupFenOrLiang(data.groupID,true,1,bean2.count);
                    DateSaveManager.getIntance().changeGroupFenOrLiang(data.groupID,false,2,bean2.count);
                    SscControl.getIntance().sendMessage("["+bean2.mId+" ]"+bean2.msg+"\n退成功",data.groupID,false);
                    sscBeans.remove(bean2);
                    mAllXiazu.remove(id);
                    return;
                }
            }
        }else{
            XposedBridge.log("! mAllXiazu.containsKey id="+id);
        }
        SscControl.getIntance().sendMessage("["+id+" ]退失败",data.groupID,false);
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
            xiazhu(data.message,bean.group,bean.date.msgId,id);
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

    private void xiazhu(String message,String groupID,long msgId,long id){
        boolean isEdit = false;
        if(isTime){
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
                    bean = mAllXiazu.get(id).date;
                }else{
                    SscControl.getIntance().sendMessage(  "改"+id+" 失败", groupID,false);
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
                if(!TextUtils.isEmpty( DateSaveManager.getIntance().mZongQun)){
                    SscControl.getIntance().sendMessage(  "[改"+bean.mId+" ]"+(isEdit?( bean.msg+"->"):"")+DateSaveManager.getIntance().getGroup(groupID).groupName+":\n"+message+"解析失败", DateSaveManager.getIntance().mZongQun,false);
                }else{
                    SscControl.getIntance().sendMessage(  "[改"+bean.mId+" ]"+(isEdit?( bean.msg+"->"):"")+message+"解析失败",groupID,false);
                }

                return;
            }
            for(DateBean2 dateBeanEach : dateBean2s){
                if(dateBeanEach.mCountList== null ||
                        dateBeanEach.mCountList.size() == 0 ||
                        dateBeanEach.local == null ||
                        dateBeanEach.local.size() == 0||
                        dateBeanEach.local.size() != dateBeanEach.mCountList.size() ){
                    if(!TextUtils.isEmpty( DateSaveManager.getIntance().mZongQun)){
                        SscControl.getIntance().sendMessage(  "[改"+bean.mId+" ]"+(isEdit?( bean.msg+"->"):"")+DateSaveManager.getIntance().getGroup(groupID).groupName+":\n"+message+"解析失败", DateSaveManager.getIntance().mZongQun,false);
                    }else{
                        SscControl.getIntance().sendMessage( "[改"+bean.mId+" ]"+(isEdit?( bean.msg+"->"):"")+message+"解析失败",groupID,false);
                    }

                    return;
                }
                for(Integer[] tmp : dateBeanEach.local){
                    if(tmp  == null ||
                            tmp.length != 2||
                            tmp[0] <=0 ||  tmp[1] <=0 ||tmp[0] >5 || tmp[1] >5){
                        if(!TextUtils.isEmpty( DateSaveManager.getIntance().mZongQun)){
                            SscControl.getIntance().sendMessage(  "[改"+bean.mId+" ]"+(isEdit?( bean.msg+"->"):"")+DateSaveManager.getIntance().getGroup(groupID).groupName+":\n"+message+"解析失败", DateSaveManager.getIntance().mZongQun,false);
                        }else{
                            SscControl.getIntance().sendMessage( "[改"+bean.mId+" ]"+(isEdit?( bean.msg+"->"):"")+message+"解析失败",groupID,false);
                        }

                        return;
                    }
                }
                for(Integer tmp : dateBeanEach.mCountList){
                    if(tmp  == null ||tmp <= 0){
                        if(!TextUtils.isEmpty( DateSaveManager.getIntance().mZongQun)){
                            SscControl.getIntance().sendMessage(  "[改"+bean.mId+" ]"+(isEdit?( bean.msg+"->"):"")+DateSaveManager.getIntance().getGroup(groupID).groupName+":\n"+message+"解析失败", DateSaveManager.getIntance().mZongQun,false);
                        }else{
                            SscControl.getIntance().sendMessage( "[改"+bean.mId+" ]"+(isEdit?( bean.msg+"->"):"")+message+"解析失败",groupID,false);
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
            if(id != -1 && bean.count != 0){
                DateSaveManager.getIntance().changeGroupFenOrLiang(groupID,true,1,bean.count);
                DateSaveManager.getIntance().changeGroupFenOrLiang(groupID,false,2,bean.count);
            }
            bean.count = count;

            DateSaveManager.getIntance().changeGroupFenOrLiang(groupID,true,2,count);
            DateSaveManager.getIntance().changeGroupFenOrLiang(groupID,false,1,count);
            if(!result.isTrue && !TextUtils.isEmpty(DateSaveManager.getIntance().mTixing)){
                SscControl.getIntance().sendMessage("[改"+bean.mId+" ]"+message+"\n"+str,DateSaveManager.getIntance().mTixing,false);

            }else{
                SscControl.getIntance().sendMessage("[改"+bean.mId+" ]"+message+"\n"+str,groupID,false);
            }

        }else{
            SscControl.getIntance().sendMessage("停止中",groupID,false);
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
        str = str.replaceAll(" ","");
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
