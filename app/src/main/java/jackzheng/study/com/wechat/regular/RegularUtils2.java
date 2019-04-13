package jackzheng.study.com.wechat.regular;

import android.text.TextUtils;

import java.util.ArrayList;

import de.robv.android.xposed.XposedBridge;

public class RegularUtils2 {

    public static RegularStrBean regularStr(String str){

        RegularStrBean regu = new RegularStrBean();
        StringDealBean.StringListDealBean spile = new StringDealBean.StringListDealBean();
        spile.list = StringDealFactory.stringDeal(str);

        if(spile == null || spile.list.size() <1){
            return regu;
        }
        ArrayList<StringDealBean.StringSimpleDealBean> list = spile.list;
        int stringCount = list.size();

        ArrayList<DateBean2> value = new ArrayList<>();
        regu.isTrue = spile.isTrue;
        //regu.list = value;

        boolean isDuoZhu =stringCount > 1 ? true:false ;
        for(int iii = 0;iii < list.size();iii++){
            StringDealBean.StringSimpleDealBean data = list.get(iii);

            if(TextUtils.isEmpty(data.str) || StringDealFactory.haveNumCount(data.str) == 0){
                continue;
            }
            int numberCount = StringDealFactory.haveNumCount(data.str);
            DateBean2 date = new DateBean2();
            if(data.dec != null){
                date.dec = data.dec;
            }
            date.message = data.str;
            SingleStrDealBean bean2 = new SingleStrDealBean();
//            if(iii == 0){
            if(!getLocalAnOther(data.str,numberCount,bean2,date,isDuoZhu)){
                return null;
            }
//            }else{
//                getLocalAnOther(data,numberCount,bean2,null);
//            }

            date.mHaveGroup = bean2.haveGroup;
            //      XposedBridge.log(data +" getLocalAnOther =\n"+bean2);
            if(bean2.numberList.size() >3 ){
                for(int i = 0; i<bean2.numberList.size()-1 ;i++){
                    if(bean2.numberCountList.get(i) != 2){
                        return regu;
                    }
                }
                for(int i = 0 ;i < bean2.numberList.size() -1 ;i++){
                    char[] chars = bean2.numberList.get(i).toCharArray();
                    date.mLastData.add(new Integer[]{chars[0]-'0',chars[1]-'0'});
                }
                bean2.mLocalCount.add(Integer.parseInt(bean2.numberList.get(bean2.numberList.size()-1)));
            }if(numberCount == 3){
                if(bean2.numberCountList.get(0) != 2 || bean2.numberCountList.get(1) != 2){
                    date.mDataList.add(getIntFormString(bean2.numberList.get(0)));
                    date.mDataList.add(getIntFormString(bean2.numberList.get(1)));
                    bean2.mLocalCount.add(Integer.parseInt(bean2.numberList.get(2)));
                }else{

                    if(!date.mHaveGroup ){
                        regu.isTrue = false;
                    }
                    String spile1 =null;
                    String spile2 = null;
                    if(bean2.spilStrList.size() == bean2.numberList.size() && bean2.isHaveLatSpile){
                        spile1 = bean2.spilStrList.get(0);
                        spile2 = bean2.spilStrList.get(1);
                    }else if(bean2.spilStrList.size() == bean2.numberList.size() ){
                        spile1 = bean2.spilStrList.get(1);
                        spile2 = bean2.spilStrList.get(2);
                    }else if(bean2.spilStrList.size() == bean2.numberList.size()+1 ){
                        spile1 = bean2.spilStrList.get(1);
                        spile2 = bean2.spilStrList.get(2);
                    }else if(bean2.spilStrList.size() == bean2.numberList.size()-1 ){
                        spile1 = bean2.spilStrList.get(0);
                        spile2 = bean2.spilStrList.get(1);
                    }
                       XposedBridge.log("spile1 ="+spile1+" spile2"+spile2);
                    boolean have = false;
                    String[] chars = {",", "，", ".", "。", "、", " ","/"};
                    for(String c: chars){
                        /*if(spile1.contains(c) && spile2.contains(c) ){
                            return regu;
                        }else*/
                            if(spile1.contains(c) ){
                            have = true;
                            break;
                        }
                    }
                    if(have && bean2.haveGroup){
                        have = false;
                    }
                    if(have){
                        date.mLastData.add(new Integer[]{getIntFormString(bean2.numberList.get(0)).get(0),getIntFormString(bean2.numberList.get(0)).get(1)});
                        date.mLastData.add(new Integer[]{getIntFormString(bean2.numberList.get(1)).get(0),getIntFormString(bean2.numberList.get(1)).get(1)});
                        bean2.mLocalCount.add(Integer.parseInt(bean2.numberList.get(2)));
                    }else{
                        date.mDataList.add(getIntFormString(bean2.numberList.get(0)));
                        date.mDataList.add(getIntFormString(bean2.numberList.get(1)));
                        bean2.mLocalCount.add(Integer.parseInt(bean2.numberList.get(2)));
                    }
                }
            }else if(numberCount == 2){
                //    XposedBridge.log("bean2.haveCount ="+bean2.haveCount);
                if(bean2.haveCount){
                    if(bean2.numberCountList.get(0) !=2){
                        date.mDataList.add(getIntFormString(bean2.numberList.get(0)));
                        date.mDataList.add(getIntFormString(bean2.numberList.get(0)));
                        bean2.mLocalCount.add(Integer.parseInt(bean2.numberList.get(1)));
                    }else if(bean2.haveGroup){
                        date.mDataList.add(getIntFormString(bean2.numberList.get(0)));
                        date.mDataList.add(getIntFormString(bean2.numberList.get(0)));
                        bean2.mLocalCount.add(Integer.parseInt(bean2.numberList.get(1)));
                    }else{
                        date.mLastData.add(new Integer[]{getIntFormString(bean2.numberList.get(0)).get(0),getIntFormString(bean2.numberList.get(0)).get(1)});
                        bean2.mLocalCount.add(Integer.parseInt(bean2.numberList.get(1)));
                    }
                }else{
                    String spile1 =null;
                    if(bean2.spilStrList.size() == bean2.numberList.size() && bean2.isHaveLatSpile){
                        spile1 = bean2.spilStrList.get(0);
                    }else if(bean2.spilStrList.size() == bean2.numberList.size() ){
                        spile1 = bean2.spilStrList.get(1);
                    }else if(bean2.spilStrList.size() == bean2.numberList.size()+1 ){
                        spile1 = bean2.spilStrList.get(1);
                    }else if(bean2.spilStrList.size() == bean2.numberList.size()-1 ){
                        spile1 = bean2.spilStrList.get(0);
                    }

                    if(bean2.numberCountList.get(0) !=2){
                        if((stringCount >1 && spile1.equals("/")/* || spile1.equals("，") */) ||spile1.contains("-") || spile1.contains("—") ||spile1.contains("－") || spile1.equals("一") /*|| spile1.equals("。") *//*|| spile1.equals(".")*/){
                            date.mDataList.add(getIntFormString(bean2.numberList.get(0)));
                            date.mDataList.add(getIntFormString(bean2.numberList.get(1)));
                            bean2.mLocalCount.add(0);
                        }else{
                            date.mDataList.add(getIntFormString(bean2.numberList.get(0)));
                            date.mDataList.add(getIntFormString(bean2.numberList.get(0)));
                            bean2.mLocalCount.add(Integer.parseInt(bean2.numberList.get(1)));
                        }
                    }else{
                        if(!date.mHaveGroup ){
                            regu.isTrue = false;
                        }
                        if( (stringCount >1 && spile1.equals("/")/* || spile1.equals("，") */) || spile1.equals("-") || spile1.equals("—")|| spile1.equals("一") ||spile1.contains("－")){
                            date.mDataList.add(getIntFormString(bean2.numberList.get(0)));
                            date.mDataList.add(getIntFormString(bean2.numberList.get(1)));
                            bean2.mLocalCount.add(0);
                        }else if(bean2.haveGroup) {
                            date.mDataList.add(getIntFormString(bean2.numberList.get(0)));
                            date.mDataList.add(getIntFormString(bean2.numberList.get(0)));
                            bean2.mLocalCount.add(Integer.parseInt(bean2.numberList.get(1)));
                        }else if(bean2.numberCountList.get(0) ==2 && spile1.equals(" ")){
                            date.mLastData.add(new Integer[]{getIntFormString(bean2.numberList.get(0)).get(0),getIntFormString(bean2.numberList.get(0)).get(1)});
                            date.mLastData.add(new Integer[]{getIntFormString(bean2.numberList.get(1)).get(0),getIntFormString(bean2.numberList.get(1)).get(1)});
                            bean2.mLocalCount.add(0);
                        }else{
                            date.mLastData.add(new Integer[]{getIntFormString(bean2.numberList.get(0)).get(0),getIntFormString(bean2.numberList.get(0)).get(1)});
                            bean2.mLocalCount.add(Integer.parseInt(bean2.numberList.get(1)));
                        }
                    }
                }
            }else if(numberCount ==1 ){
                if(stringCount == 1){
                    return regu;
                }else{
                    if(!date.mHaveGroup ){
                        regu.isTrue = false;
                    }
                    if(bean2.numberCountList.get(0) !=2 || bean2.haveGroup) {
                        date.mDataList.add(getIntFormString(bean2.numberList.get(0)));
                        date.mDataList.add(getIntFormString(bean2.numberList.get(0)));
                        bean2.mLocalCount.add(0);
                    }else{
                        date.mLastData.add(new Integer[]{getIntFormString(bean2.numberList.get(0)).get(0),getIntFormString(bean2.numberList.get(0)).get(1)});
                        bean2.mLocalCount.add(0);
                    }
                }
            }
            if(bean2.isPai == null){
                bean2.isPai = false;
            }
            date.isNoSame = bean2.isPai;
//            if(bean2.mLocal.size() ==0){
//                bean2.mLocal.add(new Integer[]{4,5});
//            }
            date.local = bean2.mLocal;
            date.mCountList = bean2.mLocalCount;
            date.isHe = bean2.isHe;
            date.heNumber = bean2.heNumber;

            if(bean2.isKill){
                ArrayList<Integer> data0 = getKillData(date.mDataList.get(0));
                ArrayList<Integer> data1 = getKillData(date.mDataList.get(1));
                date.mDataList.clear();
                date.mDataList.add(data0);
                date.mDataList.add(data1);
            }
            value.add(date);

        }
        getPai(value);
        if(dealDate(value) == null){
            return null;
        }
        regu.list = value;
        return regu;

    }

    private static ArrayList<Integer>  getKillData(ArrayList<Integer> data){
        ArrayList<Integer> value =new ArrayList<>();
        boolean  isKill = false;
        for(int ii = 0 ;ii <10;ii++){
            isKill = false;
            for(int i= 0;i <  data.size();i++){
                if(ii == data.get(i)){
                    isKill = true;
                    break;
                }
            }
            if(isKill){
                continue;
            }else{
                value.add(ii);
            }
        }
        return value;
    }

    //整理位置和注数
    private static ArrayList<DateBean2> dealDate(ArrayList<DateBean2>  list){
        getLocal(list);
        boolean isgetCont = getCount(list);
        if(!isgetCont){
            return null;
        }
        for(DateBean2 date : list){
            if(date.mCountList.size() == 0){
                continue;
            }else{
                for(Integer count : date.mCountList){
                    if(count > 800){
                        return null;
                    }
                }
            }
        }
        duplicateRemove(list);
        return list;
    }
    private static boolean  getCount(ArrayList<DateBean2>  list){
        boolean isBack = true;
        for(int i = 0 ; i<list.size();i++){
            if(list.get(i).mCountList.size() == 0){
                continue;
            }else if( list.get(i).mCountList.get(0) != 0){
                isBack = true;
                continue;
            }else if( list.get(i).mCountList.get(0) == 0){
                if(isBack){
                    int index = i;
                    index++;
                    while (index <list.size() && (list.get(index).mCountList.size()==0 ||  list.get(index).mCountList.get(0) == 0)){
                        index ++;
                    }
                    if(index == list.size()){
                        if(i >0 ){
                            index = i;
                            index --;
                            while (index >= 0 &&  list.get(index).mCountList.size() ==0){
                                index --;
                            }
                            if(index < 0){
                                return false;
                            }else{
                                list.get(i).mCountList = list.get(i-1).mCountList;
                                isBack = false;
                            }
                        }else{
                            return false;
                        }
                    }else{
                        list.get(i).mCountList = list.get(index).mCountList;
                    }
                }else{
                    int index = i;
                    index --;
                    while (index >= 0 &&  list.get(index).mCountList.size() ==0){
                        index --;
                    }
                    if(index < 0){
                        return false;
                    }else{
                        list.get(i).mCountList = list.get(i-1).mCountList;
                    }
                }
            }
        }
        return true;
    }
    private static void getLocal(ArrayList<DateBean2>  list){
        ArrayList<Integer[]> next = null;
        ArrayList<Integer[]> pre = null;
        int mLocalCount = 0;
        int start = 0;
        int end = list.size();
        for(int i =0 ; i<list.size();i++){
            if(list.get(i).local !=null && list.get(i).local.size() >0){
                next = list.get(i).local;
                mLocalCount++;
            }
        }
        /*if(mLocalCount == 1 &&list.get(0).isALlUserFri) {
            next = list.get(0).local;
        }else if(mLocalCount == 1 && list.get(list.size() -1 ).isALlUserLast){
            next =list.get(list.size() -1).local;
        }*/
        if(mLocalCount == 1 || mLocalCount == 0){
            if(next != null && mLocalCount == 1 ){
                if(mLocalCount == 1 &&list.get(0).isALlUserFri) {
                    start = 1;
                    next = list.get(0).local;
                }else if(mLocalCount == 1 && list.get(list.size() -1 ).isALlUserLast){
                    end = end -1;
                    next =list.get(list.size() -1).local;
                }
            }else /*if(mLocalCount == 0)*/{
                next = new ArrayList<>();
                next.add(new Integer[]{4,5});
            }/*else{
                next =null;
            }*/
            if(next != null){
                for(int i =start ; i<end;i++){
                    // list.get(i).local.clear();
                    if(list.get(i).local.size() == 0){
                        list.get(i).local.addAll(next);
                    }
                }

                for(int i =0 ; i<list.size();i++){
                    XposedBridge.log("list.get(i).dec  = "+list.get(i).dec );
                    if(list.get(i).dec !=null ){
                        XposedBridge.log("isXiajiang  = "+list.get(i).dec.isXiajiang+" isWuwei="+ list.get(i).dec.isWuwei+" number="+list.get(i).dec.xiajiangNumber);
                    }
                    if(list.get(i).dec !=null && list.get(i).dec.isXiajiang && !list.get(i).dec.isWuwei){
                        int count = list.get(i).local.size();
                        for(int ii = 0 ; ii< count ;ii++){
                            list.get(i).local.add(new Integer[]{list.get(i).local.get(ii)[1],list.get(i).local.get(ii)[0]});
                        }

                    }
                }

                return;
            }
        }

        for(int i =0 ; i<list.size();){
            int tmp = i;
            if (list.get(i).local == null || list.get(i).local.size() == 0) {
                while(tmp < list.size() && (list.get(tmp).local== null|| list.get(tmp).local.size() == 0)){
                    tmp++;
                }
                if(tmp < list.size()){
                    for (;i<tmp;i++){
                        if(list.get(i).local == null){
                            list.get(i).local = new ArrayList<>();
                        }
                        list.get(i).local.addAll(list.get(tmp).local);
                    }
                    i = tmp+1;
                }else{
                    pre = new ArrayList<>();
                    pre.add(new Integer[]{4, 5});
                    for(;i<list.size();i++){
                        list.get(i).local.addAll(pre);
                    }
                }
            }else{
                i++;
            }




           /* if(list.get(i).mCountList != null && list.get(i).mCountList.size() > 0 && list.get(i).mCountList.get(0) > 0) {
                if (list.get(i).local == null || list.get(i).local.size() == 0) {
                    pre = new ArrayList<>();
                    pre.add(new Integer[]{4, 5});
                    list.get(i).local.addAll(pre);
                } else {
                    pre = list.get(i).local;
                }
            }else if(list.get(i).local != null && list.get(i).local.size() != 0){
                pre = list.get(i).local;
            }else{
                start = i;
                while (i < list.size() && (list.get(i).local ==null  || list.get(i).local.size() == 0 ) &&(list.get(i).mCountList == null ||  list.get(i).mCountList.size() ==0 || list.get(i).mCountList.get(0) == 0)){
                    i++;
                }
                if(i != list.size()){
                    if(list.get(i).local ==null  || list.get(i).local.size() == 0 ){
                        pre = new ArrayList<>();
                        pre.add(new Integer[]{4,5});
                        list.get(i).local.addAll(pre);
                    }
                    pre = list.get(i).local;
                }
                next = pre;
                for(int ii = start ; ii < i ;ii++){
                    list.get(ii).local.addAll(next);
                }
            }*/
        }
        for(int i =0 ; i<list.size();i++){
            XposedBridge.log("list.get(i).dec  = "+list.get(i).dec );
            if(list.get(i).dec !=null ){
                XposedBridge.log("isXiajiang  = "+list.get(i).dec.isXiajiang+" isWuwei="+ list.get(i).dec.isWuwei+" number="+list.get(i).dec.xiajiangNumber);
            }
            if(list.get(i).dec !=null && list.get(i).dec.isXiajiang && !list.get(i).dec.isWuwei){
                int count = list.get(i).local.size();
                for(int ii = 0 ; ii< count ;ii++){
                    list.get(i).local.add(new Integer[]{list.get(i).local.get(ii)[1],list.get(i).local.get(ii)[0]});
                }

            }
        }

    }

    private static void getPai(ArrayList<DateBean2> dates){
        boolean isPai = false;
        DateBean2 tmp = null;
        for(int i = 0 ; i<dates.size();i++){
            XposedBridge.log("getPai i="+i+" isNoSame="+dates.get(i).isNoSame);
            if(dates.get(i).isNoSame){
                isPai = true;
                break;
            }
        }
        if(isPai){
            for(int i = 0 ; i<dates.size();i++){
                tmp = dates.get(i);
                XposedBridge.log("getPai i="+i+" tmp.mHaveGroup="+tmp.mHaveGroup);
                XposedBridge.log("getPai tmp.mCountList = "+tmp.mCountList);
                if(tmp.mCountList != null){
                    XposedBridge.log("getPai tmp.mCountList.size() = "+tmp.mCountList.size());
                }
                if(tmp.mCountList.size() >0){
                    XposedBridge.log("getPai tmp.mCountList.get(0)= "+tmp.mCountList.get(0));
                }

                if((tmp.mCountList ==null || tmp.mCountList.size() == 0 || tmp.mCountList.get(0) == 0)&& !tmp.mHaveGroup){
                    tmp.isNoSame = true;
                }
            }
        }
    }


    private static void duplicateRemove(ArrayList<DateBean2>  list){
        for(DateBean2 date :list){
            if(date.mDataList.size() != 0){
                for(ArrayList<Integer> numlist :date.mDataList){
                    for(int i = 0; i< numlist.size(); i++){
                        for(int ii= i+1;ii < numlist.size();){
                            if(numlist.get(i)==numlist.get(ii)){
                                numlist.remove(ii);
                            }else{
                                ii++;
                            }
                        }
                    }
                }
                creatLastData(date);
            }
            if(date.mCountList.size() == 1 && date.local.size() >1){
                int cha = date.local.size() - date.mCountList.size();
                for(int i = 0; i<cha;i++){
                    date.mCountList.add(date.mCountList.get(0));
                }
            }
            int count =0;
            for(Integer item : date.mCountList){
                count+=item;
            }
            count = count*date.mLastData.size();
            date.allCount = count;
        }
    }
    private static void creatLastData(DateBean2 bean){
        ArrayList<Integer> frist  = bean.mDataList.get(0);
        ArrayList<Integer> second =  bean.mDataList.get(1);
        boolean isNoSame = bean.isNoSame;
        for(int i :frist){
            for(int ii : second){
                if(i == ii && isNoSame){
                    continue;
                }else if(bean.isHe != null){
                    boolean isUser = true;
                    if(bean.isHe){
                        isUser = false;
                    }else{
                        isUser = true;
                    }
                    for(Integer number : bean.heNumber){
                        if(bean.isHe){
                            if((i+ii)%10 == number) {
                                isUser = true;
                                break;
                            }
                        }else{
                            if((i+ii)%10 == number){
                                isUser = false;
                                break;
                            }
                        }
                    }
                    if(!isUser){
                        continue;
                    }

                }
                Integer[] tar = new Integer[]{i,ii};
                bean.mLastData.add(tar);
            }
        }
        return ;
    }

    private static ArrayList<Integer>  getCount(ArrayList<DateBean2>  list,int index){
        if(index >= list.size()){
            return new ArrayList<Integer>();
        }
        if(list.get(index).mCountList.size() == 1 && list.get(index).mCountList.get(0) == 0){
            list.get(index).mCountList = getCount(list,index+1);
        }
        return list.get(index).mCountList;
    }

    private static  ArrayList<Integer> getIntFormString(String str){
        char[] chars = str.toCharArray();
        ArrayList<Integer> mnumlist = new ArrayList<Integer>();
        for(char c :chars){
            mnumlist.add(c-'0');
        }
        return mnumlist;
    }
    private static boolean getLocalAnOther(String str,int numberCount, SingleStrDealBean deal,DateBean2 date,boolean duozhu){
        StringBuilder builder = new StringBuilder();
        int numCount = 0;
        char[] cs = str.toCharArray();
        boolean isLocalSpile = false;
        for(int i = 0; i< cs.length;){
            XposedBridge.log(" cs[i] = "+  cs[i]);
            if(cs[i] == StringDealFactory.NEW_LOCAL_CHAR){      //提取位置信息
                //  XposedBridge.log("numberCount = "+numberCount+" numCount="+numCount+" "+cs[i] );
                if(numCount == 0 && date!= null){
                    date.isALlUserFri = true;
                }else if(numCount == numberCount && date != null){

                    date.isALlUserLast = true;
                }
                // XposedBridge.log("  date.isALlUserLast = "+  date.isALlUserLast );
                builder.append("-");
                deal.haveLoacl = true;
                if(i < cs.length -3 && StringDealFactory.isLocal(cs[i+1]) && StringDealFactory.isLocal(cs[i+2]) && StringDealFactory.chinaToNumber(cs[i+3]) != -1) {
                    Integer[] local = new Integer[]{StringDealFactory.getLocalData(cs[i + 1]), StringDealFactory.getLocalData(cs[i + 2])};
                    if (local[0] != -1 && local[1] != -1) {
                        i = i + 3;
                        deal.mLocal.add(local);
                        // XposedBridge.log("  .add(local) = "+  local[0]+","+local[1] );
                    }
                    int count = 0;
                    while (i < cs.length && StringDealFactory.chinaToNumber(cs[i]) != -1) {
                        count = count * 10 + StringDealFactory.chinaToNumber(cs[i]);
                        i++;
                    }
                    if (count != 0) {
                        deal.mLocalCount.add(count);
                        // XposedBridge.log("  .add(count) = "+  count );
                    }


                    if(numCount == numberCount -1) {
                        deal.haveCount = true;
                    }
                }else if( i < cs.length -1 && StringDealFactory.isLocal(cs[i+1])) {
                    i++;
                    if(i< cs.length -1 &&  StringDealFactory.isLocal(cs[i+1])){
                        isLocalSpile = false;
                    }else if( duozhu &&  numCount == numberCount -1){
                        isLocalSpile = true;
                    }
                    while( i < cs.length  && StringDealFactory.isLocal(cs[i])){
                        Integer[] integers;
                        if (deal.mLocal.size() == 0) {
                            integers = new Integer[]{0, 0};
                            integers[0] = StringDealFactory.getLocalData(cs[i]);
                            deal.mLocal.add(integers);
                        } else {
                            integers = deal.mLocal.get(deal.mLocal.size() - 1);
                            if (integers != null && integers[0] != 0 && integers[1] != 0) {
                                integers = new Integer[]{0, 0};
                                integers[0] = StringDealFactory.getLocalData(cs[i]);
                                deal.mLocal.add(integers);
                            } else {
                                integers[1] = StringDealFactory.getLocalData(cs[i]);
                                if(isLocalSpile && deal.mLocal.size() == 1){
                                    isLocalSpile = true;
                                }else{
                                    isLocalSpile = false;
                                }
                            }
                        }
                        i++;
                    }
                    builder.append(StringDealFactory.NEW_SPLIE_CHAR);
                    if(!isLocalSpile && numCount == numberCount -1) {
                        deal.haveCount = true;
                    }
                }else{
                    builder.append(cs[i]);
                    i++;
                }

            }else if(cs[i]==StringDealFactory.ALL_NOSUM_CHAR || cs[i] == StringDealFactory.ALL_SUM_CHAR){//提取合数

                builder.append(StringDealFactory.NEW_SPLIE_CHAR);
                char tmp = cs[i];
                i++;

                deal.haveGroup = true;

                while ( i < cs.length && StringDealFactory.chinaToNumber(cs[i]) != -1){
                    int henumber = StringDealFactory.chinaToNumber(cs[i]);
                    for(Integer hen : deal.heNumber){
                        if(hen == henumber){
                            henumber = -1;
                            break;
                        }
                    }
                    if(henumber != -1){
                        deal.heNumber.add(henumber);
                    }
                    i++;
                }

                if(deal.heNumber.size() > 0){
                    if(tmp ==StringDealFactory.ALL_NOSUM_CHAR){
                        deal.isHe = false;
                    }else {
                        deal.isHe = true;
                    }
                }
            }else if(cs[i] == StringDealFactory.ALL_PAI_CHAR) {//获取是否为排
                i++;
                deal.isPai = true;
                deal.haveGroup = true;
                if(numCount == numberCount-1 && i < cs.length && StringDealFactory.isNumber(cs[i]) ){
                    builder.append(StringDealFactory.COUNT_SIGN_CHAR);
                    deal.haveCount = true;
                }else{
                    builder.append(StringDealFactory.NEW_SPLIE_CHAR);
                }
            }else if(cs[i] == '各'){
                builder.append(cs[i]);
                i++;
                if(numberCount == 2){
                    deal.haveGroup = true;
                }
            }else if(cs[i]== StringDealFactory.KILL_SIGN_CHAR) {//处理杀
                deal.haveGroup = true;
                if (numCount == numberCount - 1) {
                    deal.isPai = true;
                    i++;
                    if (i < cs.length && StringDealFactory.isNumber(cs[i])) {
                        builder.append(StringDealFactory.COUNT_SIGN_CHAR);
                        deal.haveCount = true;
                    } else {
                        builder.append(StringDealFactory.NEW_SPLIE_CHAR);
                    }
                } else {


                    builder.append(StringDealFactory.NEW_SPLIE_CHAR);
                    deal.isKill = true;
                    i++;
                    /*
                    boolean isKill = false;
                    ArrayList<Character> killChar = new ArrayList<>();
                    while (true) {
                        i++;
                        if (i < cs.length && StringDealFactory.isNumber(cs[i])) {
                            killChar.add(cs[i]);
                        } else {
                            break;
                        }
                    }
                    if (killChar.size() > 0) {
                        deal.spilStrList.add(builder.toString());
                        builder = new StringBuilder();
                        for (char c : StringDealFactory.ALL_NUMBER_REPALCE) {
                            isKill = false;
                            for (char cc : killChar) {
                                if (cc == c) {
                                    isKill = true;
                                    break;
                                }
                            }
                            if (isKill) {
                                continue;
                            }
                            builder.append(c);
                        }
                        if(builder.length() >0){
                            numCount++;
                            deal.numberList.add(builder.toString());
                            builder = new StringBuilder();
                            builder.append(StringDealFactory.NEW_SPLIE_CHAR);
                        }
                    }*/
                }

            }else if(StringDealFactory.isNumber(cs[i])) {
                if(builder.length() > 0){
                    deal.spilStrList.add(builder.toString());
                    builder = new StringBuilder();
                }
                numCount++;
                int wei = 0;
                StringBuilder sumBuilder = new StringBuilder( );
                while (i < cs.length && StringDealFactory.isNumber(cs[i])) {
                    wei++;
                    sumBuilder.append(cs[i]);
                    i++;
                }
                deal.numberList.add(sumBuilder.toString());
                deal.numberCountList.add(wei);
            }else if(cs[i] == '打' || cs[i] == '各' && numCount == numberCount-1 && i < cs.length-1  && StringDealFactory.isNumber(cs[i+1] )) {

                deal.haveCount = true;
                builder.append(StringDealFactory.COUNT_SIGN_CHAR);
                i++;
//            }else if(){}
            }else if(StringDealFactory.isUnuse(cs[i])){
                builder.append(StringDealFactory.NEW_SPLIE_CHAR);
                builder.append("-");
                i++;
            }else{
                builder.append(cs[i]);
                i++;
            }
        }
        if(builder.length() >0){
            deal.spilStrList.add(builder.toString());
            deal.isHaveLatSpile = true;
        }
        boolean other =false;
        if(deal.spilStrList.size() >0){
            ArrayList<String > newStr = new ArrayList<>();
            for(String tmp : deal.spilStrList) {
                char[] newCs = tmp.toCharArray();
                builder = new StringBuilder();
                for(int i = 0 ;i < newCs.length ;i++){
                    if(i ==0) {
                        builder.append(newCs[i]);
                    }else if(newCs[i] == newCs[i-1] && !StringDealFactory.isNumber(newCs[i])){
                        continue;
                    }else{
                        builder.append(newCs[i]);
                    }
                }
                newStr.add(builder.toString());
            }
            deal.spilStrList = newStr;
        }
        return  true;
    }
}
