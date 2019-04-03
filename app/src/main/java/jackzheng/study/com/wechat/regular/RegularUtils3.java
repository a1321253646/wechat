package jackzheng.study.com.wechat.regular;

import android.text.TextUtils;

import java.util.ArrayList;

import de.robv.android.xposed.XposedBridge;

public class RegularUtils3 {

    public final static String NEW_LOCAL_CHAR = "囲";
    private static String getLocal(String str,DateBean3 date){
        if(str.contains("任二")){
            date.isRenyi = true;
            str = str.replace("任二",NEW_LOCAL_CHAR);
            for(int i = 1 ;i<6 ;i++){
                for(int ii = i+1; ii< 6 ;ii++){
                    date.local.add(new Integer[]{i,ii});
                }
            }
            return str;
        }
        if(str.contains("五个位")){
            date.isWuwei = true;
            str = str.replace("五个位",NEW_LOCAL_CHAR);
            for(int i = 1 ;i<5 ;i++){
                date.local.add(new Integer[]{i,i+1});
            }
            date.local.add(new Integer[]{5,4});
            return str;
        }
        if(str.contains("位")){
            if(str.endsWith("位")){
                String[] strs = str.split(" ");
                String wei = strs[strs.length -1];
                str = str.replace(" "+wei,NEW_LOCAL_CHAR);
                wei = wei.replace("位","");
                String[] weis = wei.split(".");
                for(String s : weis){
                    if(s.length() !=2 || !StringDealFactory2.isAllNumber(s)){
                        date.error = "XX位在后面的正确方法为使用空格与前面隔开如1234/12 12.23.45位";
                        return null;
                    }
                    char[] cs = s.toCharArray();
                    date.local.add(new Integer[]{cs[0]-'0',cs[1]-'0'});
                }
            }else{
                String[] strs = str.split("位");
                String wei = strs[0];
                str = str.replace(wei+"位",NEW_LOCAL_CHAR);
                String[] weis = wei.split(".");
                for(String s : weis){
                    if(s.length() !=2 || !StringDealFactory2.isAllNumber(s)){
                        date.error = "XX位在前面的正确方法如12.23.45位1234/12 ";
                        return null;
                    }
                    char[] cs = s.toCharArray();
                    date.local.add(new Integer[]{cs[0]-'0',cs[1]-'0'});
                }
            }
        }
        if(str.contains("前")){
            str = str.replace("前",NEW_LOCAL_CHAR);
            date.local.add(new Integer[]{1,2});
        }
        if(str.contains("后")){
            str = str.replace("后",NEW_LOCAL_CHAR);
            date.local.add(new Integer[]{4,5});
        }

        if(str.contains("万")||str.contains("千")||str.contains("百")||str.contains("十")||str.contains("个")){
            char[] cs = str.toCharArray();
            StringBuilder build = new StringBuilder();
            Integer[] local  = new Integer[2];
            int count = 0;
            int i = 0;
            while (i< cs.length && !StringDealFactory2.isLocal(cs[i])){
                i++;
            }
            for(;i<cs.length;i++){
                if(cs[i] == '.'){
                    if(count == 1){
                        date.error = "使用万千百十个来指定位置需要是指定两位如 万千.十个 或者 万千十个";
                        return null;
                    }
                    build.append(cs[i]);
                }else if(StringDealFactory2.isLocal(cs[i])){
                    local[count] = StringDealFactory2.getLocalData(cs[i]);
                    count ++;
                    if(count == 2){
                        date.local.add(local);
                        local = new Integer[2];
                        count = 0;
                    }
                    build.append(cs[i]);
                }else{
                    break;
                }
            }
            if(count == 1){
                date.error = "使用万千百十个来指定位置需要是指定两位如 万千.十个 或者 万千十个";
                return null;
            }
            str = str.replace(build.toString(),NEW_LOCAL_CHAR);
        }

        return str;
    }


    public static String getPai(String str,DateBean3 date ){
        if(str.contains("排")){
            str = str.replace("排",NEW_LOCAL_CHAR);
            date.isNoSame = true;
        }
        return str;
    }

    public static String getSha(String str,DateBean3 date){
        if(str.contains("杀")){
            char[] cs = str.toCharArray();
            boolean isHaveNumber = false;
            for(int i = 0 ;i< cs.length ; i++){
                if(StringDealFactory2.isNumber(cs[i])){
                    isHaveNumber = true;
                }else if(cs[i] == '杀') {
                    break;
                }
            }
            if(isHaveNumber){
                date.error = "杀字前面除了位置，不能带数字";
                return null;
            }
            str = str.replace("杀",NEW_LOCAL_CHAR);
            date.isSha = true;
        }
        return str;
    }

    public static String getHe(String str,DateBean3 date){
        if(str.contains("合")){
            StringBuilder build = new StringBuilder();
            boolean isHaveNumber = false;
            char[] cs = str.toCharArray();
            for(int i = 0 ;i< cs.length ; i++){
                if(StringDealFactory2.isNumber(cs[i])){
                    isHaveNumber = true;
                }else if(cs[i] == '合') {
                    if(i > 0){
                        if(cs[i-1] == '不'){
                            build.append("不合");
                            date.isHe = false;
                        }else{
                            build.append("合");
                            date.isHe = true;
                        }
                    }else{
                        build.append("合");
                        date.isHe = true;
                    }
                    if(i < cs.length ){
                        i++;
                        for(;i< cs.length ; i++){
                            if(StringDealFactory2.isNumber(cs[i])){
                                build.append(cs[i]);
                                date.heNumber.add(cs[i]-'0');
                            }else{
                                break;
                            }
                        }
                    }
                    if(date.heNumber.size() <1){
                        date.error = "请在合后面指明合或不合那些数";
                        return null;
                    }else{
                        if(!isHaveNumber){
                            str = str.replace(build.toString(),"全头全尾");
                        }else{
                            str = str.replace(build.toString(),NEW_LOCAL_CHAR);
                        }
                    }
                    break;
                }
            }
        }
        return str;
    }

    private static String getXiaJiang(String str,DateBean3 date){
        if(str.contains("奖")){
            StringBuilder build = new StringBuilder();
            char[] cs  = str.toCharArray();
            int i = 0;
            int leng = 0;
            while (i < cs.length && cs[i] != '奖'){
                i++;
            }
            while (i > 0 && StringDealFactory2.isNumber(cs[i])){
                leng ++;
            }
            if(leng < 1){
                date.error = "不符合下奖玩法";
                return null;
            }
            build.append(cs,i-leng+1,leng);

            for(char c : build.toString().toCharArray()){
                date.xiajiangNumber.add(c-'0');
            }
            date.isXiaJiang = true;
            str = str.replace(build.toString()+"奖",build.toString()+"-0123456789"+NEW_LOCAL_CHAR);
        }
        return str;
    }


    public static String replaceNumber(String s,DateBean3 date){
        if(s.contains("双")){
            s =s.replace("双","囲02468囲");
        }
        if(s.contains("单")){
            s =s.replace("单","囲13579囲");
        }
        if(s.contains("大")){
            s =s.replace("大","囲56789囲");
        }
        if(s.contains("小")){
            s =s.replace("双","囲01234囲");
        }
        if(s.contains("对子")){
            s =s.replace("对子","囲00.11.22.33.44.55.66.77.88.99囲");
        }
        if(s.contains("平重")){
            s =s.replace("平重","囲00.11.22.33.44.55.66.77.88.99囲");
        }

        if(s.contains("全头")){
            s =s.replace("全头","囲0123456789头");
        }
        if(s.contains("全尾")){
            s =s.replace("全尾","囲0123456789尾");
        }

        if(s.contains("头") && s.contains("尾")){
            StringBuilder build1 = new StringBuilder();
            StringBuilder build2 = new StringBuilder();
            int touIndex = 0;
            int weiIndex = 0;

            char[] cs  = s.toCharArray();
            int i = 0;

            int leng = 0;
            while (i < cs.length && cs[i] != '头'){
                i++;
            }
            touIndex = i;
            while (i > 0 && StringDealFactory2.isNumber(cs[i])){
                leng ++;
            }
            if(leng < 1){
                date.error = "不符合头尾玩法";
                return null;
            }
            build1.append(cs,i-leng+1,leng);

            i = 0;
            leng =0;
            while (i < cs.length && cs[i] != '尾'){
                i++;
            }
            weiIndex = i;
            while (i > 0 && StringDealFactory2.isNumber(cs[i])){
                leng ++;
            }
            if(leng < 1){
                date.error = "不符合头尾玩法";
                return null;
            }
            build2.append(cs,i-leng+1,leng);
            if(weiIndex < touIndex){
                s=s.replace(build2+"尾",build1+NEW_LOCAL_CHAR);
                s=s.replace(build1+"头",build2+NEW_LOCAL_CHAR);
            }else{
                s=s.replace("尾",NEW_LOCAL_CHAR);
                s=s.replace("头",NEW_LOCAL_CHAR);
            }
        }

        return s;
    }

    public static ArrayList<DateBean3>  regularStr(String str){

        ArrayList<String>list = StringDealFactory2.stringDeal(str);

        if(list == null || list.size() <1){
            return null;
        }

        int stringCount = list.size();

        ArrayList<DateBean3> value = new ArrayList<>();
        boolean isDuoZhu =stringCount > 1 ? true:false ;


        for(int i = 0 ; i< list.size() ; i++){

            String each = list.get(i);
            if(each.contains("重")){
                each = each.replace("重",NEW_LOCAL_CHAR);
            }
            DateBean3 date = new DateBean3();
            each = getLocal(each,date);
            if(each == null){
                value.clear();
                value.add(date);
                return value;
            }
            each = getPai(each,date);
            each = getHe(each,date);
            if(each == null){
                value.clear();
                value.add(date);
                return value;
            }
            each = getSha(each,date);
            if(each == null){
                value.clear();
                value.add(date);
                return value;
            }
            each = getXiaJiang(each,date);
            if(each == null){
                value.clear();
                value.add(date);
                return value;
            }
            each = replaceNumber(each,date);
            if(each == null){
                value.clear();
                value.add(date);
                return value;
            }

        }
        return null;

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
        ArrayList<Integer[]> next = null;
        ArrayList<Integer[]> pre = null;
        int mLocalCount = 0;
        int start = 0;
        int end = list.size();
        for(int i =0 ; i<list.size();i++){
            if(list.get(i).local !=null && list.get(i).local.size() >0){
                mLocalCount++;
            }
        }
        if(mLocalCount == 1 &&list.get(0).isALlUserFri) {
            next = list.get(0).local;
        }else if(mLocalCount == 1 && list.get(list.size() -1 ).isALlUserLast){
            next =list.get(list.size() -1).local;
        }
        if(mLocalCount == 1 || mLocalCount == 0){
            if(next != null && mLocalCount == 1 ){
                if(mLocalCount == 1 &&list.get(0).isALlUserFri) {
                    start = 1;
                    next = list.get(0).local;
                }else if(mLocalCount == 1 && list.get(list.size() -1 ).isALlUserLast){
                    end = end -1;
                    next =list.get(list.size() -1).local;
                }
            }else if(mLocalCount == 0){
                next = new ArrayList<>();
                next.add(new Integer[]{4,5});
            }else{
                next =null;
            }
            if(next != null){
                for(int i =start ; i<end;i++){
                    // list.get(i).local.clear();
                    if(list.get(i).local.size() == 0){
                        list.get(i).local.addAll(next);
                    }
                }
                return;
            }
        }

        for(int i =0 ; i<list.size();i++){
            if(list.get(i).mCountList != null && list.get(i).mCountList.size() > 0 && list.get(i).mCountList.get(0) > 0) {
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
    private static void getLocalAnOther(String str,int numberCount, SingleStrDealBean deal,DateBean2 date,boolean duozhu){
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
    }
}
