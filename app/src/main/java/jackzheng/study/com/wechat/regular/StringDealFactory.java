package jackzheng.study.com.wechat.regular;

import android.util.Log;

import java.util.ArrayList;

public class StringDealFactory {

    private static final char[] SPILE_SIGN_LIST = {',','，','.','。','/','、',' '};
    public final static char NEW_SPLIE_CHAR = '死';
    public final static char NEW_LOCAL_CHAR = '囲';
    public final static char ALL_PAI_CHAR = '排';
    public final static char ALL_NOSUM_CHAR = '无';
    public final static char ALL_SUM_CHAR = '合';
    public final static char COUNT_SIGN_CHAR = '注';
    public final static char KILL_SIGN_CHAR = '杀';
    private static final char[] ALL_SIGN_LIST = {',','，','.','。','/','、',' ','-','_','—','=','+',':',':','：'};
    private final static String  NEW_SPILE_SIGN = "死";

    //目前使用在单组多个位置多注数的情况，合数
    private static char[] NUMBER_REPALACE = {'拾','壹','贰','叁','肆','伍','陆','柒','捌','玖'};

    private static char[] LOCAL_REPALCE = {'万','千','百','十','个'};
    public static char[] ALL_NUMBER_REPALCE = {'0','1','2','3','4','5','6','7','8','9'};
    public static int[] ALL_NUMBER_REPALCE_INT = {0,1,2,3,4,5,6,7,8,9};

    private static final char[] GROUP_SIGN_LIST = {'全','单','双','大','小'};
    private static String[] GROUP_VALUE_LIST = {"-0123456789-","-13579-","-02468-","-56789-","-01234-"};

    private static final char[]  NOREPEAT_SIGN_LIST = {'排','无','去','不'};
    private static final char[] DUPLICATE_SIGN_LIST = {'重','从'};

    private static final char[] UNUSER_SIGN_LIST = {'头','尾'};
    public static ArrayList<String> stringDeal(String str){
       // Log.d("zsbin","str ="+str+"***************************************************************************************************\n");
        str = rejectNouserChar(str);
 //       Log.d("zsbin","rejectNouserChar:str ="+str);
        str = repalace(str);
  //      Log.d("zsbin","repalace:str ="+str);
        str = weirdReplace(str);
//        Log.d("zsbin","weirdReplace:str ="+str);
        ArrayList<String> list =   spileString(str);
        for(int i= 0;i< list.size() ;i++){
            String s = list.get(i);
            if(s.contains("下奖") || s.contains("上奖") ){
                String str1 = "";
                String str2 = "";
                list.remove(i);
                if(s.contains("下奖")){
                    s = s.replace("下奖",StringDealFactory.NEW_SPILE_SIGN);
                }else if(s.contains("上奖")){
                    s = s.replace("上奖",StringDealFactory.NEW_SPILE_SIGN);
                }
                char[] cs = s.toCharArray();
                StringBuilder builder = new StringBuilder();
                int index = 0;
                while(index < cs.length && !isNumber(cs[index])){
                    builder.append(cs[index]);
                    index++;
                }
                str2 += builder.toString();
                str1 += builder.toString();
                builder = new StringBuilder();
                str1 += "0123456789-";
                while (index <cs.length && isNumber(cs[index])){
                    builder.append(cs[index]);
                    index++;
                }
                str2 += builder.toString();
                str1 += builder.toString();
                str2 += "-0123456789";
                builder = new StringBuilder();
                while (index <cs.length){
                    builder.append(cs[index]);
                    index++;
                }
                str2 += builder.toString();
                str1 += builder.toString();
                Log.d("zsbin","String 1 ="+str1);
                Log.d("zsbin","String 2 ="+str2);
                list.add(i,str1);
                list.add(i,str2);
            }

        }
//        for(int i = 0 ;i <list.size();i++){
//            Log.d("zsbin","siglen:data["+i+"] ="+list.get(i));
//        }
//        Log.d("zsbin","-------------------------------------------------------------------------------------------\n");
        return  list;
    }

    //剔除字符串中连续出现的符号和首尾的空格
    private static String rejectNouserChar(String str){
        char[] list = str.toCharArray();
        int start = 0;
        int end = list.length -1;
        StringBuilder build = new StringBuilder();
        for(;start <= end ;start ++){
            if(list[start] != ' ' && list[start] != '\t'){
                break;
            }
        }
        if(start == end){
            return null;
        }
        for(;start <= end ;end --){
            if(list[end] != ' ' && list[end] != '\t'){
                break;
            }
        }
        if(start == end){
            return null;
        }
        for(;start<=end;){
            if(isSpileChar(list[start])){
                ArrayList<Character> mtmpChar = new ArrayList<>();
                boolean isHave = false;
                while (start <= end && isSpileChar(list[start])){
                    if(mtmpChar.size() == 0 ){
                        mtmpChar.add(list[start]);
                        build.append(list[start]);
                    }else{
                        isHave = false;
                        for(Character c :mtmpChar){
                            if(c == list[start]){
                                isHave = true;
                                break;
                            }
                        }
                        if(!isHave){
                            mtmpChar.add(list[start]);
                            build.append(list[start]);
                        }
                    }
                    start++;
                }
            }else{
                build.append(list[start]);
                start++;
            }
        }
        return build.toString();
    }

    //另类处理,在字符处理之后
    //这一部分为了处理一些比较特殊的情况，便于分割字符
    //这一部分可能会越写越多
    //一个是单注码中多个位置多个注数,除了最后一个位置，其他位置的注数都是用汉字数字来表示
    private static String weirdReplace(String str){
        StringBuilder value = new StringBuilder();
        char[] cs = str.toCharArray();
        for(int i = 0; i<cs.length ;){
            if(cs[i] == NEW_LOCAL_CHAR ){
                StringBuilder value2 = new StringBuilder();
                StringBuilder value3 = new StringBuilder();
                value.append(cs[i]);
                if(i < cs.length -2 && isLocal(cs[i+1]) &&  isLocal(cs[i+2])){
                    value.append(cs[i+1]);
                    value.append(cs[i+2]);
                    i = i+3;
                }else{
                    i++;
                    continue;
                }
                if(i < cs.length && isNumber(cs[i])){
                    while (i < cs.length && isNumber(cs[i])){
                        value2.append(cs[i]);
                        value3.append(NUMBER_REPALACE[cs[i]-'0']);
                        i++;
                    }
                }else {
                    continue;
                }
                if(i >= cs.length || cs[i] != NEW_LOCAL_CHAR){
                    value.append(value2.toString());
                }else{
                    value.append(value3.toString());
                    value.append(cs[i]);
                    i++;
                }
            }else{
                value.append(cs[i]);
                i++;
            }
        }
        return value.toString();

    }


    //特殊字符替代
    //处理了位置信息，XX位 前 后 前二 后二 王千百十个，在位置前面添加了一个特殊字符
    //处理了全 单 双 大 小
    //处理了合 将其数字符号转化为中文数字
    //不要 直接替代为啥
    private static String repalace(String str){
        char[] cs = str.toCharArray();
        String newStr;
        StringBuilder builder = new StringBuilder();
        for(int i =0 ; i<cs.length ;i++){
              if(cs[i] == '平' || cs[i] == '双' ){
                  if(i< cs.length-1 && (cs[i+1] == '重' || cs[i+1] == '从')){
                      i = i+1;
                      builder.append("死00 11 22 33 44 55 66 77 88 99死");
                      continue;
                  }
              }else if((cs[i] == '不' &&   i< cs.length-1  && cs[i+1] == '要')) {
                  i = i + 1;
                  builder.append("杀");
                  continue;
              }else if( cs[i] == '没'){
                  builder.append("杀");
                  continue;
              }else if(cs[i] == '对' &&   i< cs.length-1  && cs[i+1] == '子'){
                  i = i+1;
                  builder.append("死00 11 22 33 44 55 66 77 88 99死");
                  continue;
              }else if(cs[i] == '合' && i< cs.length-1 ){
                  if( cs[i+1] == '单'){
                      builder.append("死13579-02468,02468-13579死");
                  }else if( cs[i+1] == '双'){
                      builder.append("死02468-02468,13579-13579死");
                  }
                  i = i+1;
                  continue;
              }
              newStr = repalaceAAssociation(cs[i]);
             if(newStr != null){
                 builder.append(newStr);
                 continue;
             }else if(cs[i] == '位'  && i >1 && isNumber(cs[i-1]) && isNumber(cs[i-2])){
                int fri = cs[i-2] - '1';
                int sec = cs[i-1] - '1';
                builder.delete(builder.length() -2,builder.length());
                builder.append(NEW_LOCAL_CHAR);
                builder.append(LOCAL_REPALCE[fri]);
                builder.append(LOCAL_REPALCE[sec]);
             }else  if(cs[i] == '前' ){
                 builder.append(NEW_LOCAL_CHAR);
                 builder.append(LOCAL_REPALCE[0]);
                 builder.append(LOCAL_REPALCE[1]);
                 if(i < cs.length-1 && cs[i+1] == '二'){
                    i++;
                 }
             }else if(cs[i] == '后'){
                 builder.append(NEW_LOCAL_CHAR);
                 builder.append(LOCAL_REPALCE[3]);
                 builder.append(LOCAL_REPALCE[4]);
                 if(i < cs.length-1 && cs[i+1] == '二'){
                     i++;
                 }
             }else if(isLocal(cs[i]) && i > 0 && !isLocal(cs[i-1])) {
                 builder.append(NEW_LOCAL_CHAR);
                 builder.append(cs[i]);
             }else if(isLocal(cs[i]) && i ==0 ){
                 builder.append(NEW_LOCAL_CHAR);
                 builder.append(cs[i]);
             }else if(isNo(cs[i])){
                 if(i < cs.length-1){
                     if(isDuplice(cs[i+1])){
                        builder.append(ALL_PAI_CHAR);
                        i++;
                     }else if(cs[i+1] ==ALL_SUM_CHAR){
                         builder.append(ALL_NOSUM_CHAR);
                         i++;
                         while (true){
                             i++;
                             if(i <cs.length && isNumber(cs[i])){
                                 builder.append(NUMBER_REPALACE[cs[i]- '0']);
                             }else{
                                 if(i != cs.length){
                                     i--;
                                 }
                                 break;
                             }
                         }
                     }else{
                         builder.append(ALL_PAI_CHAR);
                     }
                 }else{
                     builder.append(ALL_PAI_CHAR);
                 }
             } else if(cs[i] == ALL_SUM_CHAR){
                 builder.append(cs[i]);
                 while (true){
                     i++;
                     if(i <cs.length && isNumber(cs[i])){
                         builder.append(NUMBER_REPALACE[cs[i]- '0']);
                     }else{
                         if(i != cs.length){
                             i--;
                         }
                         break;
                     }
                 }
             }else if(cs[i] == '共') {
                 builder.append(StringDealFactory.NEW_SPLIE_CHAR);
                 i++;
                 while (i < cs.length && StringDealFactory.isNumber(cs[i])) {
                     i++;
                 }
             }else if(cs[i]== '元') {
                 int yuanindex = 1;
                 while (yuanindex <=i && isNumber(cs[i-yuanindex])) {
                     yuanindex++;
                 }
                 yuanindex--;
                 builder.delete( builder.length() - yuanindex , builder.length());
                 builder.append(StringDealFactory.NEW_SPLIE_CHAR);
             }else if(cs[i] == '下'){

                 if(i< cs.length-1 && cs[i+1]== '奖' ){
                     builder.append(cs[i]);
                     i++;
                     builder.append(cs[i]);
                 }else{
                    // builder.append(StringDealFactory.NEW_SPLIE_CHAR);
                     builder.append(",");
 //                    i++;
//                     while (i < cs.length && StringDealFactory.isNumber(cs[i])) {
//                         i++;
//                     }
//                     i--;
                 }
             }else{
                 builder.append(cs[i]);
             }
        }
        return builder.toString();
    }

    private static char getThirdSplie(char[] cs){
        int numCount = 0;
        for(int i = 0 ;i<cs.length ;){
            if(isNumber(cs[i])) {
                numCount++;
                while (isNumber(cs[i])) {
                    i++;
                    if (i == cs.length) {
                        break;
                    }
                }
            }else if(isSpileChar(cs[i]) && numCount ==3){
                return cs[i];
            }else{
                i++;
            }
        }
        return 'E';
    }

    public static int chinaToNumber(char c){
        for(int i=0 ;i <NUMBER_REPALACE.length ;i++){
            if(c == NUMBER_REPALACE[i]){
                return i;
            }
        }
        return -1;
    }

    public static boolean isLocal(char c){
        for(char cc :LOCAL_REPALCE){
            if(c == cc){
                return  true;
            }
        }
        return  false;
    }
    public static int getLocalData(char c){
        for(int i = 0;i< LOCAL_REPALCE.length ;i++){
            if(c == LOCAL_REPALCE[i]){
               return i+1;
            }
        }
        return -1;
    }
    private static String repalaceAAssociation(char c){
        for(int i = 0; i<GROUP_SIGN_LIST.length ;i++){
            if(c == GROUP_SIGN_LIST[i]){
                return GROUP_VALUE_LIST[i];
            }
        }
        return null;
    }


    //字符串的分割
    private static ArrayList<String> spileString(String str){
        char[] cs = str.toCharArray();
        boolean isHavaSpoleSign ;
        StringBuilder build = new StringBuilder();
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();

        for(char c :cs){
            if (c == '\n') {
                if(build.length() <1){
                    continue;
                }
                String str2 = build.toString();
                build = new StringBuilder();
                list.add(str2);
            }else{
                build.append(c);
            }
        }
        if(build.length() >0){
            list.add(build.toString());
        }
//        for(int i = 0 ;i <list.size();i++){
//            Log.d("zsbin","按行:data["+i+"] ="+list.get(i));
//        }
        //单行分割
        for(String s: list){
            if(haveNumCount(s) >3){
                ArrayList<String> tmp = sigleLineSpile(s);
                if(tmp ==null ){
                    value.add(s);
                    continue;
                }
                String end = tmp.get(tmp.size() - 1);
                int nuumcount = haveNumCount(end);
                if(nuumcount== 1 || nuumcount == 0){
                    ArrayList<String> tmp2 = new ArrayList<>();
                    for(int ii = 0 ; ii< tmp.size() -1;ii++){
                        tmp2.add(tmp.get(ii)+NEW_SPILE_SIGN+end);
                    }
                    value.addAll(tmp2);
                }else{
                    value.addAll(tmp);
                }
            }else{
                value.add(s);
            }
        }

        return  value;
    }
    private static ArrayList<String> sigleLineSpile(String str){
        ArrayList<String> list = new ArrayList<>();
        ArrayList<Character> noSplieChars = new ArrayList<>();
        noSplieChars.add('=');
        StringBuilder builder = new StringBuilder();
        char[] cs = str.toCharArray();
        int numCount = 0;

//        char thirdSplie = getThirdSplie(cs);
//        if(thirdSplie != 'E'){
//            ArrayList<String> list2 = new ArrayList<>();
//            boolean isSuccess =sigleLineSpileLoop(list2,cs,0,thirdSplie);
//            if(isSuccess){
//                return list2;
//            }
//        }

        for(int i = 0 ; i< cs.length ;){
            if(isNumber(cs[i])){
                numCount ++;
                while (isNumber(cs[i])){
                    builder.append(cs[i]);
                    i++;
                    if(i ==  cs.length){
                        break;
                    }
                }
            }else if(isSpileChar(cs[i]) && numCount == 1){
                builder.append(cs[i]);
                noSplieChars.add(cs[i]);
                i++;
            }else if(isSpileChar(cs[i]) && numCount >= 2 && isNoSpileChar(cs[i],noSplieChars)){
                Log.d("zsbin","noSplieChars  "+cs[i]);
                ArrayList<String> list2 = new ArrayList<>();
                boolean isSuccess =sigleLineSpileLoop(list2,cs,i+1,cs[i]);
                Log.d("zsbin","isSuccess  "+isSuccess);
                if(isSuccess){
                    list.add(builder.toString());
                    list.addAll(list2);
                    return list;
                }else{
                    noSplieChars.add(cs[i]);
                    builder.append(cs[i]);
                    i++;
                }
            }else{
                builder.append(cs[i]);
                i ++;
            }
        }
        return null;
    }
    public static int haveNumCount(String s){
        char[] cs = s.toCharArray();
        int count = 0;
        for(int i= 0; i< cs.length ;){

            if(isNumber(cs[i])){
                count ++;
                while (isNumber(cs[i])){
                    i++;
                    if(i == cs.length){
                        break;
                    }
                }
            }else{
                i++;
            }
        }
      //  Log.d("zsbin",s+":numbe count is "+count);
        return count;
    }

    private static boolean isNoSpileChar(char c, ArrayList<Character> list){
        if(list.size() == 0){
            return true;
        }else{
            for(Character cc : list){
                if(c == cc){
                    return false;
                }
            }
            return true;
        }
    }
    private static boolean sigleLineSpileLoop( ArrayList<String> list ,char[] cs ,int index , char spileChar){
        StringBuilder builder = new StringBuilder();
        int numCount = 0;
        for(;index < cs.length ;){
           // Log.d("zsbin","cs[index] "+cs[index]);
            if(isNumber(cs[index])){
                numCount ++;
//                Log.d("zsbin","numCount  "+numCount);
                while (isNumber(cs[index])){
                    builder.append(cs[index]);
                    index++;
                    if(index == cs.length){
                        break;
                    }
                }
            }else if(cs[index] ==  spileChar ){
                if(numCount >= 2){
                    list.add(builder.toString());
                    return sigleLineSpileLoop(list,cs,index+1,spileChar);
                }else{
                    return false;
                }
            }else{
                builder.append(cs[index]);
                index++;
            }
        }
        if(builder.length() >0){
            list.add(builder.toString());
        }
        return  true;
    }

    private static boolean isNo(char c){
        for(char cc :NOREPEAT_SIGN_LIST){
            if(cc == c){
                return true;
            }
        }
        return  false;
    }
    private static boolean isDuplice(char c){
        for(char cc : DUPLICATE_SIGN_LIST){
            if(cc == c){
                return true;
            }
        }
        return false;
    }

    public static boolean isNumber(char c){
        if(c>= '0' && c <= '9'){
            return true;
        }
        return false;
    }
    private static boolean isSpileChar(char c){
        for(char c2 : ALL_SIGN_LIST){
            if(c == c2){
                return true;
            }
        }
        return false;
    }
    public static boolean isUnuse(char c){
        for(char cc : UNUSER_SIGN_LIST){
            if(cc == c){
                return true;
            }
        }
        return false;
    }
}
