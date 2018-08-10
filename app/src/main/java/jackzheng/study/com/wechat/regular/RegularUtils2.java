package jackzheng.study.com.wechat.regular;

import android.text.TextUtils;

import java.util.ArrayList;

public class RegularUtils2 {

    public static ArrayList<DateBean2> regularStr(String str){

        ArrayList<String> list = StringDealFactory.stringDeal(str);
        if(list == null || list.size() <1){
            return null;
        }
        int stringCount = list.size();
        ArrayList<DateBean2> value = new ArrayList<>();
        for(String data : list){
            if(TextUtils.isEmpty(data)){
                continue;
            }
            int numberCount = StringDealFactory.haveNumCount(data);
            DateBean2 date = new DateBean2();
            SingleStrDealBean bean2 = new SingleStrDealBean();
            getLocalAnOther(data,numberCount,bean2);
         //   Log.d("zsbin",data +" getLocalAnOther =\n"+bean2);
            if(bean2.numberList.size() >3 ){
                for(int i = 0; i<bean2.numberList.size()-1 ;i++){
                    if(bean2.numberCountList.get(i) != 2){
                        return null;
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
                 //   Log.d("zsbin","spile1 ="+spile1+" spile2"+spile2);
                    boolean have = false;
                    String[] chars = {",", "，", ".", "。", "、", " ","/"};
                    for(String c: chars){
                        if(spile1.contains(c) && spile2.contains(c) ){
                            return null;
                        }else if(spile1.contains(c) ){
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
                        if((stringCount >1 && spile1.equals("/") || spile1.equals("，") ) ||spile1.equals("-") || spile1.equals("—") || spile1.equals("一") || spile1.equals("。") /*|| spile1.equals(".")*/){
                            date.mDataList.add(getIntFormString(bean2.numberList.get(0)));
                            date.mDataList.add(getIntFormString(bean2.numberList.get(1)));
                            bean2.mLocalCount.add(0);
                        }else{
                            date.mDataList.add(getIntFormString(bean2.numberList.get(0)));
                            date.mDataList.add(getIntFormString(bean2.numberList.get(0)));
                            bean2.mLocalCount.add(Integer.parseInt(bean2.numberList.get(1)));
                        }
                    }else{
                        if( (stringCount >1 && spile1.equals("/") || spile1.equals("，") ) || spile1.equals("-") || spile1.equals("—")|| spile1.equals("一") ){
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
                    return null;
                }else{
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
            value.add(date);

        }
        return dealDate(value);
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
                    if(count > 900){
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
        boolean isBack = true;
        for(int i = 0 ; i<list.size();i++){
            if(list.get(i).mLastData.size() == 0 && list.get(i).mDataList.size() == 0 && list.get(i).local.size() != 0){
                isBack = false;
                continue;
            }else if(list.get(i).local.size() >0 ){
                isBack = true;
            }else if(list.get(i).local.size() == 0){
                int index = i;
                if(!isBack){
                    list.get(i).local.addAll(list.get(i-1).local);
                }else{
                    index++;
                    while(index < list.size() && list.get(index).local.size() == 0){
                        index++;
                    }
                    if(index == list.size()){
                        isBack = false;
                        if(i > 0){
                            list.get(i).local.addAll(list.get(i-1).local);
                        }else{
                            list.get(i).local.add(new Integer[]{4,5});
                        }
                    }else{
                        if(list.get(index).mDataList.size() == 0 && list.get(index).mLastData.size() ==0){
                            if(i > 0 && list.get(i-1).local.size() != 0){
                                isBack = false;
                                list.get(i).local.addAll(list.get(i-1).local);
                                continue;
                            }
                        }
                        list.get(i).local.addAll(list.get(index).local);
                    }
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
        ArrayList<Integer> second =  bean.mDataList.get(1);;
        boolean isNoSame = bean.isNoSame;
        for(int i :frist){
            for(int ii : second){
                if(i == ii && isNoSame){
                    continue;
                }else if(bean.isHe != null){
                    if(bean.isHe && i+ii != bean.heNumber){
                        continue;
                    }else if(!bean.isHe && i+ii == bean.heNumber){
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
    private static void getLocalAnOther(String str,int numberCount, SingleStrDealBean deal){
        StringBuilder builder = new StringBuilder();
        int numCount = 0;
        char[] cs = str.toCharArray();
        for(int i = 0; i< cs.length;){
            if(cs[i] == StringDealFactory.NEW_LOCAL_CHAR){      //提取位置信息
                deal.haveLoacl = true;
                if(i < cs.length -3 && StringDealFactory.isLocal(cs[i+1]) && StringDealFactory.isLocal(cs[i+2]) && StringDealFactory.chinaToNumber(cs[i+3]) != -1) {
                    Integer[] local = new Integer[]{StringDealFactory.getLocalData(cs[i + 1]), StringDealFactory.getLocalData(cs[i + 2])};
                    if (local[0] != -1 && local[1] != -1) {
                        i = i + 3;
                        deal.mLocal.add(local);
                    }
                    int count = 0;
                    while (i < cs.length && StringDealFactory.chinaToNumber(cs[i]) != -1) {
                        count = count * 10 + StringDealFactory.chinaToNumber(cs[i]);
                        i++;
                    }
                    if (count != 0) {
                        deal.mLocalCount.add(count);
                    }
                }else if( i < cs.length -1 && StringDealFactory.isLocal(cs[i+1])) {
                    i++;
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
                            }
                        }
                        i++;
                    }
                    builder.append(StringDealFactory.NEW_SPLIE_CHAR);
                }else{
                    builder.append(cs[i]);
                    i++;
                }

            }else if(cs[i]==StringDealFactory.ALL_NOSUM_CHAR || cs[i] == StringDealFactory.ALL_SUM_CHAR){//提取合数
                builder.append(StringDealFactory.NEW_SPLIE_CHAR);
                char tmp = cs[i];
                i++;
                int sum = 0;
                deal.haveGroup = true;
                while ( i < cs.length && StringDealFactory.chinaToNumber(cs[i]) != -1){
                    sum = sum*10 + StringDealFactory.chinaToNumber(cs[i]);
                    i++;
                }
                deal.heNumber = sum;
                if(sum != 0){
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
                    }
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
    }
}
