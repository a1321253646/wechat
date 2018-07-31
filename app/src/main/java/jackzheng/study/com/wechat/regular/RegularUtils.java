package jackzheng.study.com.wechat.regular;

import android.text.TextUtils;
import android.util.Log;
//import android.util.Log;

import java.util.ArrayList;

public class RegularUtils {

    private static final char[] NUMBER_SIGN_LIST = {'—','_','～','+','=','一','＝'};
    private static final char[] LINE_SIGN_LIST = {',','，','.','。','\n'};
    private static final char[] LOCAL_SIGN_LIST= {'前','后','万','千','百','十','个'};
    private static final char LOCAL_SIGN ='蕉';
    private static final char WEI_SIGN_LIST = '位';
    private static final char EACH_SIGN = '各';
    private static final char[]  NOREPEAT_SIGN_LIST = {'排','无','去','不'};
    private static final char  NOREPEAT_SIGN= '排';
    private static final char[] GROUP_SIGN_LIST = {'全','单','双','大','小'};
    private static String[] GROUP_VALUE_LIST = {"@0123456789@","@13579@","@02468@","@56789@","@01234@"};
    private static char KILL_SIGN = '杀';
    private static final char[] UNUSER_SIGN_LIST = {'头','尾','重','从'};

    private static final char VIRGULE_SIGN = '/';

    private static char NUMBER_SPLITE_REPLACE = '-';
    private static String NUMBER_SPLITE_REPLACE_S = "-";
    private static String NUMBER_SPLITE_REPLACE_R = "(-)+";

    private static char NEW_NUMBER_SPILE = '@';
    private static String NEW_NUMBER_SPILE_S = "@";
    private static String NEW_NUMBER_SPILE_S_R = "(@)+";

    private static int[] ALL_NUMBER_LIST = {0,1,2,3,4,5,6,7,8,9};

    public static ArrayList<DateBean> regularStr(String str){
        Log.d("zsbin","regularStr = "+str);
        ArrayList<DateBean> resoult = new ArrayList<DateBean>() ;
        ArrayList<String> strs = spileString(str);
        boolean priStr = true;
        for(String sigle : strs){
            String pri = sigle;
            if(TextUtils.isEmpty(sigle)){
                continue;
            }
            DateBean bean = new DateBean();
            sigle = getWei(sigle,bean);
            sigle = loopReplace(sigle,bean);

            if(bean.local.size() <1){
                Integer[] local = new Integer[]{0,0};
                local[0]=4;
                local[1]=5;
                bean.local.add(local);
            }else{
                bean.isHavaLocal = true;
            }

            sigle = replaceDeleteNumberSign(sigle);
            sigle = cleanUpString(sigle,bean);
            findLatData(sigle,bean);
            if(bean.mNumberCount == 2 ){
                if( bean.isHavaEach || bean.isHavaFristVirgule||bean.isHavaFristNosame || bean.isHavaFristLocal){
                    ArrayList<Integer> list = new ArrayList<Integer>(bean.mDataList.get(0));
                    ArrayList<Integer> list2 = new ArrayList<Integer>(bean.mDataList.get(0));
                    ArrayList<Integer> list3 = new ArrayList<Integer>(bean.mDataList.get(1));
                    bean.mDataList.clear();
                    bean.mDataList.add(list);
                    bean.mDataList.add(list2);
                    bean.mDataList.add(list3);
                    bean.mNumberCount = 3;
                    bean.isHavaSecondkill = bean.isHavaFristKill;
                }else if(bean.isHavaFristNumberSpile){
                    bean.mCount = 0;
                    resoult.add(bean);
                    continue;
                }
            }

            if(bean.mNumberCount == 3 || bean.mLastData.size()>0){
                int count = 0;
                if(bean.mNumberCount== 3){
                    ArrayList<Integer> mCountList = bean.mDataList.get(2);
                    for(Integer i :mCountList){
                        count = count*10 + i;
                    }
                    bean.mCount = count;
                }

                resoult.add(bean);
                continue;
            }

            if(priStr){
                priStr = false;
         //       Log.d("zsbin","str = "+str+" false"+"");
            }
          //  Log.d("zsbin","spile str= "+pri+"----"+sigle);
            StringBuilder b = new StringBuilder();
            b.append("bean: Local = ");
            for(Integer[] local : bean.local){
                b.append("["+local[0]+","+local[1]+"]  ");
            }
            for(ArrayList<Integer> list :bean.mDataList){
                b.append("[");
                for(Integer i:list){
                    b.append("["+i+"],");
                }
                b.append("]");
            }
         //   Log.d("zsbin",b.toString());
            resoult.clear();
            return null;
        }
        boolean isSuccess = false;
        if(setArrayBeanCount(resoult)){
            isSuccess = true;
            printfArrayBean(resoult);
        }else{
            isSuccess = false;
          //  Log.d("zsbin","格式不对");
        }

      //  Log.d("zsbin","-------------------------------------------");
        if(isSuccess){
            return resoult;
        }else{
            return null;
        }
    }
    private static  void printfArrayBean(ArrayList<DateBean> list){

        for(DateBean bean : list){
            StringBuilder b = new StringBuilder();
            b.append("bean: Local = ");
            for(Integer[] local : bean.local){
                b.append("["+local[0]+","+local[1]+"]  ");
            }
            if(bean.mLastData.size() > 0){
                b.append("lastDat:");
                for(Integer[] ints :bean.mLastData){
//            StringBuilder b = new StringBuilder();
                    b.append("[["+ints[0]+"]["+ints[1]+"]] ");
                }
            }else if(bean.mDataList.size() > 0){
                for(ArrayList<Integer> list2 :bean.mDataList){
                    b.append("[");
                    for(Integer i:list2){
                        b.append("["+i+"],");
                    }
                    b.append("] ");
                }
            }
            b.append("count = "+bean.mCount+"\n");
            b.append("LastData:");
            for(int i = 0;i<bean.mLastData.size();i++){
                if(i%5 ==0 ){
                    b.append("\n    ");
                }
                b.append("["+bean.mLastData.get(i)[0]+"]["+bean.mLastData.get(i)[1]+"] ");
            }
            b.append("\n    NoSome="+bean.isNoSame+" Count = "+bean.mNumberCount+" Each "+bean.isHavaEach
                    +" Loc "+bean.isHavaLocal+" FriSpile "+bean.isHavaFristNumberSpile+" SecSpile "+bean.isHavaSecondNumberSpile+
                    " FriKill "+bean.isHavaFristKill+" Seckill\n "+bean.isHavaSecondkill+"     FriVir "+
                    bean.isHavaFristVirgule+" SecVir "+bean.isHavaSecondVirgule+
                    " FriNosame "+bean.isHavaFristNosame+" SecNoSame "+bean.isHavaSecondNosame+
                    " FriLoc "+bean.isHavaFristLocal+" SecLoc "+bean.isHavaSecondLocal);
      //      Log.d("zsbin",b.toString());
        }

    }
    private static  boolean setArrayBeanCount(ArrayList<DateBean> list){
        for(int i = 0; i < list.size();i++){
            if(list.get(i).mCount == 0){
                int count = setCount(i,list);
                if(count <= 0){
                    return false;
                }
            }
            if(list.get(i).mDataList.size() >1){
                creatLastData(list.get(i));
            }
            if(list.get(i).mLastData.size() <1){
 //               Log.d("zsbin","return true");
                return false;
            }
        }
   //     Log.d("zsbin","return false");
        return true;
    }

    private static boolean creatLastData(DateBean bean){
        ArrayList<Integer> frist ;
        ArrayList<Integer> second;
        boolean isNoSame = bean.isNoSame;
        if(bean.isHavaFristKill){
            frist = new ArrayList<Integer>();
            boolean tmp ;
            for(int i=0;i<ALL_NUMBER_LIST.length;i++){
                tmp = false;
                for(int ii : bean.mDataList.get(0)){
                    if(ii == i){
                        tmp = true;
                        break;
                    }
                }
                if(!tmp){
                    frist.add(i);
                }
            }
        }else{
            frist = bean.mDataList.get(0);
        }
        if(bean.isHavaSecondkill){
            second = new ArrayList<Integer>();
            boolean tmp;
            for(int i=0;i<ALL_NUMBER_LIST.length;i++){
                tmp = false;
                for(int ii : bean.mDataList.get(1)){
                    if(ii == i){
                        tmp = true;
                        break;
                    }
                }
                if(!tmp){
                    second.add(i);
                }
            }
        }else{
            second = bean.mDataList.get(1);
        }
        for(int i :frist){
            for(int ii : second){
                if(i == ii && isNoSame){
                       continue;
                }
                Integer[] tar = new Integer[]{i,ii};
                bean.mLastData.add(tar);
            }
        }
        if(bean.mLastData.size() >0){
   //         Log.d("zsbin","return true");
            return true;
        }
 //       Log.d("zsbin","return false");
        return false;
    }

    private static int setCount(int index ,ArrayList<DateBean> list){
        if(index < list.size()-1){
            if(list.get(index+1).mCount >0){
                list.get(index).mCount = list.get(index+1).mCount;
            }else{
                int count = setCount(index+1,list);
                if(count >0){
                    list.get(index).mCount = count;
                }
            }
            return list.get(index).mCount;
        }else{
            return -1;
        }
    }

    private static boolean findLatData(String str, DateBean bean){
        //Log.d("zsbin","findLatData str= "+str);
        int numberCount =0;
        int index = 0;
        Integer[] friNum =null;
        boolean findSplite = false;
        int count = 0;
        char[] chars = str.toCharArray();
        while (index <chars.length && chars[index] == ' '){
            index++;
        }
        //Log.d("zsbin","findLatData start index= "+index);
        for(;index< chars.length;index++){
          //  Log.d("zsbin","chars["+index+"]= "+chars[index]);
            if(chars[index] >= '0' && chars[index] <= '9'){
                numberCount++;
                if(findSplite){
                    count = count*10 + chars[index] - '0';
                }else if(numberCount < 3){
                    if(friNum ==null){
                        friNum = new Integer[]{-1,-1};
                    }
                    friNum[numberCount -1] = chars[index] - '0';
                }
            }else  if(chars[index] ==NEW_NUMBER_SPILE && !findSplite){
                findSplite = true;
                if( bean.mLastData.size() > 0 && numberCount== 2 && friNum[0] != -1 && friNum[1] != -1){
                    bean.mLastData.add(friNum);
                    friNum = null;
                    numberCount = 0;
                }
            }else if(chars[index] != ' '){
                bean.mLastData.clear();
                return false;
            }
            if(!findSplite && chars[index] == ' ' && numberCount== 2 && friNum[0] != -1 && friNum[1] != -1){
                bean.mLastData.add(friNum);
                friNum = null;
                numberCount = 0;
            }
        }
        bean.mCount = count;
        if(bean.mLastData.size() > 0 ){
//            StringBuilder b = new StringBuilder();
//            b.append(str+":");
//            for(Integer[] ints : bean.mLastData){
//                b.append("[["+ints[0]+"]["+ints[1]+"]]");
//            }
//            b.append("count = "+bean.mCount);
//            Log.d("zsbin","findLatData:"+b.toString());
            return true;
        }
        return false;
    }

    private static String cleanUpString(String str, DateBean bean){
        StringBuilder builder = new StringBuilder();
        char[] chars = str.toCharArray();
        int numberCount = 0;
        boolean isNumber = false;
        for(int i= 0;i< chars.length;i++){
            if(chars[i]>= '0' && chars[i] <='9'){
                if(!isNumber){
                    numberCount++;
                    bean.mNumberCount = numberCount;
                }
                isNumber = true;
                ArrayList<Integer> list ;
                if(bean.mDataList.size() <numberCount){
                    list = new ArrayList<>();
                    bean.mDataList.add(list);
                }else{
                    list = bean.mDataList.get(numberCount-1);
                }
                list.add(chars[i]-'0');
                builder.append(chars[i]);
                continue;
            }
            isNumber = false;
            if(chars[i] == KILL_SIGN){
                if(i > 0 && chars[i-1] >= '0' && chars[i-1] <='9'&&
                        i < chars.length-1 && chars[i+1] >= '0' && chars[i+1] <='9'){
                    builder.append(NEW_NUMBER_SPILE);
                }
                if(numberCount == 0){
                    bean.isHavaFristKill = true;
                }else if(numberCount == 1){
                    bean.isHavaSecondkill = true;
                }
                continue;
            }
            if(chars[i] == NUMBER_SPLITE_REPLACE){
                if(numberCount == 1){
                    bean.isHavaFristNumberSpile = true;
                }else if(numberCount == 2){
                    bean.isHavaSecondNumberSpile = true;
                }
                if(i == 0 || chars[i-1] < '0' || chars[i-1] >'9'||
                        i == chars.length-1 || chars[i+1] < '0' || chars[i+1] > '9'){

                }else{
                    builder.append(NEW_NUMBER_SPILE);
                }
                continue;
            }
            if(chars[i] == VIRGULE_SIGN){
                if(numberCount == 1){
                    bean.isHavaFristVirgule = true;
                }else if(numberCount == 2){
                    bean.isHavaSecondVirgule = true;
                }
                if(i == 0 || chars[i-1] < '0' || chars[i-1] >'9'||
                        i == chars.length-1 || chars[i+1] < '0' || chars[i+1] > '9'){

                }else{
                    builder.append(NEW_NUMBER_SPILE);
                }
                continue;
            }
            if(chars[i] == NOREPEAT_SIGN){
                if(numberCount == 1){
                    bean.isHavaFristNosame = true;
                }else if(numberCount == 2){
                    bean.isHavaSecondNosame = true;
                }
                if(i == 0 || chars[i-1] < '0' || chars[i-1] >'9'||
                        i == chars.length-1 || chars[i+1] < '0' || chars[i+1] > '9'){
                }else{
                    builder.append(NEW_NUMBER_SPILE);
                }
                continue;
            }
            if(chars[i] == LOCAL_SIGN){
                if(numberCount == 1){
                    bean.isHavaFristLocal = true;
                }else if(numberCount == 2){
                    bean.isHavaSecondLocal = true;
                }
                if(i == 0 || chars[i-1] < '0' || chars[i-1] >'9'||
                        i == chars.length-1 || chars[i+1] < '0' || chars[i+1] > '9'){
                }else{
                    builder.append(NEW_NUMBER_SPILE);
                }
                continue;
            }
            builder.append(chars[i]);
        }

        return builder.toString();
    }

    private static String replaceDeleteNumberSign(String str){
        str = str.replaceAll(NEW_NUMBER_SPILE_S_R,NEW_NUMBER_SPILE_S);
        str = str.replaceAll(NUMBER_SPLITE_REPLACE_R,NUMBER_SPLITE_REPLACE_S);

        if(str.startsWith(NEW_NUMBER_SPILE_S)){
            str = str.substring(1);
        }
        if(str.endsWith(NEW_NUMBER_SPILE_S)){
            str = str.substring(0,str.length()-1);
        }

        return str;
    }

    private static String getWei(String str , DateBean data){
        StringBuilder builder = new StringBuilder();
        int index = 0;
        char[] chars = str.toCharArray();
        for(int i= 0;i< chars.length;i++){
            if(chars[i] >'0'&& chars[i]<'6'){
                index++;
                if(index ==2 && i <chars.length-1 && chars[i+1]== WEI_SIGN_LIST && chars[i] != chars[i-1]){
                    Integer[] local = new Integer[]{0,0};
                    local[0]=chars[i-1] - '0';
                    local[1]=chars[i] - '0';
                    data.local.add(local);
                    i++;
                    builder.append(NEW_NUMBER_SPILE);
                    index = 0;
                }else if(i == chars.length-1 ){
                    for(;index > 0;index --){
                        builder.append(chars[i-index+1]);
                    }
                }
            }else{
                for(;index > 0;index --){
                    builder.append(chars[i-index]);
                }
                index = 0;
                builder.append(chars[i]);
            }
        }
        return builder.toString();
    }

    private static String loopReplace(String str, DateBean bean){
        StringBuilder builder = new StringBuilder();
        char[] chars = str.toCharArray();
        for(char c : chars ){
            if(replaceLocal(c,bean,builder)){
                continue;
            }
            if(replaceNorepeat(c,bean,builder)){
                continue;
            }
            if(replaceGroup(c,builder)){
                continue;
            }
            if(replaceNumberSign(c,builder)){
                continue;
            }
            if(replaceEachSign(c,builder)){
                bean.isHavaEach = true;
                continue;
            }
            if(replaceUnuserSign(c,builder)){
                continue;
            }
            builder.append(c);
        }
        return builder.toString();
    }

    private static boolean replaceUnuserSign(char c,StringBuilder builder){
        for(char c2: UNUSER_SIGN_LIST){
            if(c == c2){
                builder.append(NEW_NUMBER_SPILE);
                return true;
            }
        }
        return  false;
    }

    private static boolean replaceNumberSign(char c,StringBuilder builder){
        for(char c2: NUMBER_SIGN_LIST){
            if(c == c2){
                builder.append(NUMBER_SPLITE_REPLACE);
                return true;
            }
        }
        return  false;
    }

    private static boolean replaceEachSign(char c,StringBuilder builder){
        if(c == EACH_SIGN){
            builder.append(NEW_NUMBER_SPILE);
            return true;
        }
        return false;
    }

    private static boolean replaceGroup(char c,StringBuilder builder){
        for(int i= 0;i< GROUP_SIGN_LIST.length;i++){
            if(c == GROUP_SIGN_LIST[i]){
                builder.append(GROUP_VALUE_LIST[i]);
                return true;
            }
        }
        return  false;
    }

    private static boolean replaceNorepeat(char c, DateBean bean, StringBuilder builder){
        for(char c2: NOREPEAT_SIGN_LIST){
            if(c2 == c){
                builder.append(NOREPEAT_SIGN);
                bean.isNoSame = true;
                return  true;
            }
        }
        return false;
    }
    private static boolean replaceLocal(char c, DateBean bean, StringBuilder builder){
         Integer[] local ;
        for(int i= 0;i< LOCAL_SIGN_LIST.length;i++){
            if(c == LOCAL_SIGN_LIST[i]){
                switch (i){
                    case 0:{
                        local = new Integer[]{0,0};
                        local[0]=1;
                        local[1]=2;
                        bean.local.add(local);
                        builder.append(LOCAL_SIGN);
                    }
                    break;
                    case 1:{
                        local = new Integer[]{0,0};
                        local[0]=4;
                        local[1]=5;
                        bean.local.add(local);
                        builder.append(LOCAL_SIGN);
                    }
                    break;
                    case 2: case 3: case 4: case 5:case 6:{
                        if(bean.local.size() >0){
                            local = bean.local.get(bean.local.size() - 1);
                        }else {
                            local = new Integer[]{0,0};
                            bean.local.add(local);
                        }
                        if(local[1] != 0){
                            local =  new Integer[]{0,0};
                            bean.local.add(local);
                        }
                        if( local[0]==0){
                            local[0]= i-1;
                            builder.append(LOCAL_SIGN);
                        }else{
                            local[1] = i-1;
                            builder.append(LOCAL_SIGN);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static ArrayList<String> spileString(String str){
        char[] cs = str.toCharArray();
        boolean isHavaSpoleSign ;
        StringBuilder build = new StringBuilder();
        ArrayList<String> list = new ArrayList<>();
        for(char c :cs){
            isHavaSpoleSign = false;
            for(char c2 : LINE_SIGN_LIST){
                if(c2 == c){
                    isHavaSpoleSign = true;
                    break;
                }
            }
            if(isHavaSpoleSign){
                if(build.length() >0){
                    list.add(build.toString());
                }
                build = new StringBuilder();
            }else{
                build.append(c);
            }
        }
        if(build.length() >0){
            list.add(build.toString());
        }
        return  list;
    }

}
