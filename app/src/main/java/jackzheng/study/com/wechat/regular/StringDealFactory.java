package jackzheng.study.com.wechat.regular;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XposedBridge;
import jackzheng.study.com.wechat.sscManager.DateSaveManager;


public class StringDealFactory {

    private static final char[] SPILE_SIGN_LIST = {',','，','.','。','/','、',' '};
    public final static char NEW_SPLIE_CHAR = '死';
    public final static char NEW_LOCAL_CHAR = '囲';
    public final static char ALL_PAI_CHAR = '排';
    public final static char ALL_NOSUM_CHAR = '无';
    public final static char ALL_SUM_CHAR = '合';
    public final static char COUNT_SIGN_CHAR = '注';
    public final static char KILL_SIGN_CHAR = '杀';
    private static final char[] ALL_SIGN_LIST = {',','，','.','。','/','、',' ','-','_','—','=','+',':',':','：','－'};
    private final static String  NEW_SPILE_SIGN = "死";

    //目前使用在单组多个位置多注数的情况，合数
    private static char[] NUMBER_REPALACE = {'拾','壹','贰','叁','肆','伍','陆','柒','捌','玖'};

    public static char[]  LOCAL_REPALCE = {'万','千','百','十','个'};
    public static char[] ALL_NUMBER_REPALCE = {'0','1','2','3','4','5','6','7','8','9'};
    public static int[] ALL_NUMBER_REPALCE_INT = {0,1,2,3,4,5,6,7,8,9};

    private static final char[] GROUP_SIGN_LIST = {'全','单','双','大','小'};
    private static String[] GROUP_VALUE_LIST = {"-0123456789-","-13579-","-02468-","-56789-","-01234-"};

    private static final char[]  NOREPEAT_SIGN_LIST = {'排','无','去','不'};
    private static final char[] DUPLICATE_SIGN_LIST = {'重','从'};

    private static final char[] UNUSER_SIGN_LIST = {'头','尾'};

    public static ArrayList<StringDealBean.StringSimpleDealBean> stringDeal(String str){
        // Log.d("zsbin","str ="+str+"***************************************************************************************************\n");

        if(DateSaveManager.getIntance().isThird){
            String[] split = str.split("\n");
            if(split != null && split.length > 0){
                DateBean2 thirdOne = RegularUtils2.getThirdOne(split[0]);
                if(thirdOne != null){
                    XposedBridge.log("zsbin\n"+thirdOne.toString());
                    ArrayList<StringDealBean.StringSimpleDealBean> list =  new ArrayList<>();
                    for(String s : split){
                        StringDealBean.StringSimpleDealBean sb = new StringDealBean.StringSimpleDealBean();
                        sb.str = s;
                        list.add(sb);
                    }
                    return list;
                }
            }
        }

        str = replaceXieXian(str);
        Log.d("zsbin","replaceXieXian str ="+str+"***************************************************************************************************\n");
        str = repalaceFrist(str);
        str = rejectNouserChar(str);
        //       Log.d("zsbin","rejectNouserChar:str ="+str);



        ArrayList<StringDealBean.StringSimpleDealBean> result = new ArrayList<StringDealBean.StringSimpleDealBean>();

//        Log.d("zsbin","weirdReplace:str ="+str);
        ArrayList<String> list =  spileStringNew(str);
      //  list = repalaceDianDian(list);
        for(int i= 0;i< list.size() ;i++){
            StringDealBean.StringSimpleDealBean strDec = new StringDealBean.StringSimpleDealBean();
            String s = list.get(i);
            Log.d("zsbin","repalaceX str ="+str+"***************************************************************************************************\n");
            Log.d("zsbin","repalace:s ="+s);
            if(s.contains("下奖") || s.contains("上奖") ){
                strDec.dec = new StringDealBean.StringDecBean();
                strDec.dec.isXiajiang = true;
                if(s.contains("下奖5位")||s.contains("下奖五位") ||s.contains("下奖5囲个位")|| s.contains("下奖五囲个位")){
                    strDec.dec.isWuwei = true;
                    String str111 = null;
                    if(s.contains("下奖5位")){
                        str111 = "下奖5位";
                    }else if(s.contains("下奖五位")){
                        str111 = "下奖五位";
                    }else if(s.contains("下奖5囲个位")){
                        str111 = "下奖5囲个位";
                    }else if(s.contains("下奖五囲个位")){
                        str111 = "下奖五囲个位";
                    }
                    s = s.replace(str111,StringDealFactory.NEW_SPILE_SIGN);
                //    list.remove(i);
                    char[] cs = s.toCharArray();
                    StringBuilder builder = new StringBuilder();
                    int index = 0;
                    while(index < cs.length && !isNumber(cs[index])){
                        //    builder.append(cs[index]);
                        index++;
                    }
                    while(index < cs.length && isNumber(cs[index])){
                        builder.append(cs[index]);
                        index++;
                    }
                    StringBuilder builder2 = new StringBuilder();
                    while(index < cs.length){
                        builder2.append(cs[index]);
                        index++;
                    }
                    StringBuilder builder3 = new StringBuilder();
                    strDec.dec.xiajiangNumber = builder.toString();
                    builder3.append(builder.toString());
                    builder3.append("-0123456789");
                    builder3.append("囲万千囲千百囲百十囲十个囲个十");
                    builder3.append(builder2.toString());
                    strDec.str = builder3.toString();
                    result.add(strDec);
                  //  list.add(i,builder3.toString());
                    continue;
                }


              //  String str1 = "";
                String str2 = "";
             //   list.remove(i);
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
               // str1 += builder.toString();
                builder = new StringBuilder();
              //  str1 += "0123456789-";
                while (index <cs.length && isNumber(cs[index])){
                    builder.append(cs[index]);
                    index++;
                }
                strDec.dec.xiajiangNumber = builder.toString();
                str2 += builder.toString();
               // str1 += builder.toString();
                str2 += "-0123456789";
                builder = new StringBuilder();
                while (index <cs.length){
                    builder.append(cs[index]);
                    index++;
                }
                str2 += builder.toString();
            //    str1 += builder.toString();
              //  Log.d("zsbin","String 1 ="+str1);
                Log.d("zsbin","String 2 ="+str2);
              //  list.add(i,str1);
             //   list.add(i,str2);
                strDec.str = str2;
                result.add(strDec);
            }else if(s.contains("任意")){
                String renyi = null;
                if(s.contains("任意两位")){
                    renyi ="任意两位";
                }else if(s.contains("任意二位")){
                    renyi ="任意二位";
                }else if(s.contains("任意2位")){
                    renyi ="任意2位";
                }else if(s.contains("任意位")){
                    renyi ="任意位";
                }
                String str1 = s.replace(renyi,"囲万千囲万百囲万十囲万个囲千百囲千十囲千个囲百十囲百个囲十个");
               // list.remove(i);
               // list.add(i,str1);
                strDec.dec = new StringDealBean.StringDecBean();
                strDec.str = str1;
                strDec.dec.isAllWei = true;
                result.add(strDec);
            }else{
                strDec.str = s;
                result.add(strDec);
            }
        }

        for(int i = 0 ; i< result.size() ; i++){
            StringDealBean.StringSimpleDealBean bean = result.get(i);
            repalace(bean);
            bean.str = weirdReplace(bean.str);
            bean.str = shaReplace(bean.str);
            bean.str =  repalaceX(bean.str);
            Log.d("zsbin","repalace:str ="+bean.str);

        }
        return  result;
    }

    private static String replaceXieXian(String s){

        if(DateSaveManager.getIntance().mModelIndex != 5){
            return s;
        }
        if(!s.contains("//")){
            return s;
        }
        s = s.replace("////","//");
        s = s.replace("///","//");
        s = s.replace("//",",");
        return s;
    }


    private static ArrayList<String>  repalaceDianDian(ArrayList<String> list){
        if(DateSaveManager.getIntance().mModelIndex != 5){
            return list;
        }
        ArrayList<String> newList = new ArrayList<>();
        for(String s : list){
            if(!s.contains("…")){
                newList.add(s);
            }
            s = s.replace("…………","…");
            s = s.replace("………","…");
            s = s.replace("……","…");
            StringBuilder values = new StringBuilder();
            String[] strs = s.split("…");
            if(strs.length <3){
                newList.add(s);
            }else{
                for(int i = 0;i <strs.length ;){
                    if(i< strs.length-1){
                        newList.add(strs[i]+"="+ strs[i+1]);
                        i= i+2;
                    }else{
                        newList.add(strs[i]);
                        i++;
                    }
                }

            }
        }
        return newList;

    }
    private static String repalaceX(String s){
        Log.d("zsbin","repalaceX DateSaveManager.getIntance().mModelIndex="+DateSaveManager.getIntance().mModelIndex);
        if(DateSaveManager.getIntance().mModelIndex != 5){
            return s;
        }
        if(!s.contains("x") && !s.contains("X")){
            return s;
        }
        ArrayList<String> numbers =  new ArrayList<>();
        ArrayList<Integer> locals =  new ArrayList<>();
        StringBuilder builderBumber = new StringBuilder();
        StringBuilder values = new StringBuilder();
        int localIndex = 0;
        boolean isFinish = false;
        char[] cs = s.toCharArray();
        for(int i = 0 ;i < cs.length; ){
            if(isFinish){
                values.append(cs[i]);
                i++;
            }else if(isNumber(cs[i])){
                localIndex++;
                while (i< cs.length && isNumber(cs[i])){
                    builderBumber.append(cs[i]);
                    i++;
                }
                Log.d("zsbin","  numbers.add builderBumber.toString()="+builderBumber.toString());
                numbers.add(builderBumber.toString());
                builderBumber = new StringBuilder();
                if(numbers.size() == 2 && locals.size()== 3){
                    isFinish = true;
                    values.append(numbers.get(0));
                    values.append("-");
                    values.append(numbers.get(1));
                    for(int ii = 0 ; ii < 5 ;ii++){
                        boolean isHaveLocal = false;
                        for(int iii  : locals){
                            if(iii == ii){
                                isHaveLocal = true;
                                break;
                            }
                        }
                        if(!isHaveLocal){
                            values.append("囲"+LOCAL_REPALCE[ii]);
                        }
                    }
                }else if( numbers.size() == 1 &&numbers.get(0).length() == 2  && locals.size()== 3){
                    isFinish = true;
                    values.append(numbers.get(0).getBytes()[0]-'0');
                    values.append("-");
                    values.append(numbers.get(0).getBytes()[1]-'0');
                    for(int ii = 0 ; ii < 5 ;ii++){
                        boolean isHaveLocal = false;
                        for(int iii  : locals){
                            if(iii == ii){
                                isHaveLocal = true;
                                break;
                            }
                        }
                        if(!isHaveLocal){
                            values.append("囲"+LOCAL_REPALCE[ii]);
                            values.append("囲"+LOCAL_REPALCE[ii+1]);
                            break;
                        }
                    }
                }else if(numbers.size() > 2){
                    return s;
                }
            }else if(cs[i] == 'X' || cs[i]=='x'){
                i++;
                if(localIndex <LOCAL_REPALCE.length){
                    locals.add(localIndex);
                    Log.d("zsbin","  locals.add localIndex="+localIndex);
                    localIndex++;


                    if(numbers.size() == 2 && locals.size()== 3){
                        isFinish = true;
                        values.append(numbers.get(0));
                        values.append("-");
                        values.append(numbers.get(1));
                        for(int ii = 0 ; ii < 5 ;ii++){
                            boolean isHaveLocal = false;
                            for(int iii  : locals){
                                if(iii == ii){
                                    isHaveLocal = true;
                                    break;
                                }
                            }
                            if(!isHaveLocal){
                                values.append("囲"+LOCAL_REPALCE[ii]);
                            }
                        }
                    }else if( numbers.size() == 1 &&numbers.get(0).length() == 2  && locals.size()== 3){
                        isFinish = true;
                        values.append(numbers.get(0).getBytes()[0]-'0');
                        values.append("-");
                        values.append(numbers.get(0).getBytes()[1]-'0');
                        for(int ii = 0 ; ii < 5 ;ii++){
                            boolean isHaveLocal = false;
                            for(int iii  : locals){
                                if(iii == ii){
                                    isHaveLocal = true;
                                    break;
                                }
                            }
                            if(!isHaveLocal){
                                values.append("囲"+LOCAL_REPALCE[ii]);
                                values.append("囲"+LOCAL_REPALCE[ii+1]);
                                break;
                            }
                        }
                    }else if( locals.size()> 3){
                        return s;
                    }
                }else{
                    return s;
                }

            }else{
                values.append(cs[i]);
                i++;
            }
        }
        return  values.toString();

    }

    private static String repalaceFrist(String s){
        String renyi = null;
        if(s.contains("包")){
            s = s.replace("包","五位下奖");
        }
        if(s.contains("球")){
            s = s.replace("球","位");
        }



        if(s.contains("5位") && s.contains("奖")){
            renyi = "5位";
        }else if(s.contains("5个位") ){
            renyi = "5个位";
        }else if(s.contains("5星")){
            renyi = "5星";
        }else if(s.contains("全部")){
            renyi = "全部";
        }else if(s.contains("全位") && s.contains("奖")){
            renyi = "全位";
        }else if(s.contains("五个位")){
            renyi = "五个位";
        }else if(s.contains("五位")){
            renyi = "五位";
        }else if(s.contains("走5位")){
            renyi = "走5位";
        }
        if(renyi != null){
           s = s.replace(renyi,"五个位");
            s = s.replace("奖","");
            s = s.replace("下","下奖");
            s =s.replace("上","下奖");
        }
        if(s.contains("下奖全")){
            s = s.replace(renyi,"下奖五位");
        }
        if(s.contains("全位") && !s.contains("奖")){
            s = s.replace("全位","任意位");
        }

        if(s.contains("五个位") && s.contains("下奖")){
            s = s.replace("五个位","");
            s = s.replace("下奖","下奖五位");
        }
        if(s.contains("除双重")){
            s = s.replace("除双重","排");
        }
        if(s.contains("任二")){
            s = s.replace("任二","任意位");
        }else if(s.contains("任位")){
            s = s.replace("任位","任意位");
        }
        if(s.contains("合") ){
            char[] strs = s.toCharArray();
            int count = 0;
            for(char c : strs){
                if(c == '合'){
                    break;
                }else if(isNumber(c)){
                    count++;
                }
            }
            if(count == 0){
                s = "1234567890"+s;
            }
        }

        if(s.contains("任选二")){
            s = s.replace("任选二","任意位");
        }

        return s;
    }
  /*  private static String repalaceFrist(String s){
        if(s.contains("下奖全")||
                s.contains("字5位")||
                s.contains("字5星")||
                s.contains("字走5全位")||
                s.contains("字下奖全位")||
                s.contains("下5位")||
                s.contains("字五位")||
                s.contains("字五星")||
                s.contains("字走五全位")||
                s.contains("字下奖全位")||
                s.contains("下五位")
                ){
            String renyi = null;
            if(s.contains("下奖全")){
                renyi ="下奖全";
            }else if(s.contains("字5位")){
                renyi ="字5位";
            }else if(s.contains("字5星")){
                renyi ="字5星";
            }else if(s.contains("字走5全位")){
                renyi ="字走5全位";
            }else if(s.contains("字下奖全位")){
                renyi ="字下奖全位";
            }else if(s.contains("下5位")){
                renyi ="下5位";
            }else if(s.contains("字五位")){
                renyi ="字五位";
            }else if(s.contains("字五星")){
                renyi ="字五星";
            }else if(s.contains("字走五全位")){
                renyi ="字走五全位";
            }else if(s.contains("字下奖全位")){
                renyi ="字下奖全位";
            }else if(s.contains("下五位")){
                renyi ="下五位";
            }
            return  s.replace(renyi,"下奖五位");
        }
        return s;
    }*/

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
        boolean isHasNumber = false;
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
                    //   i++;
                }
            }else{
                value.append(cs[i]);
                i++;
            }
        }
        return value.toString();
    }

    private static String shaReplace(String str){
        StringBuilder value = new StringBuilder();
        char[] cs = str.toCharArray();
        boolean isHasNumber = false;
        for(int i = 0; i<cs.length ;){
            if(!isHasNumber){
                if(isNumber(cs[i])){
                    isHasNumber = true;
                }else if(isNo(cs[i])){
                    cs[i] = '杀';
                    isHasNumber = true;
                }
            }
            value.append(cs[i]);
            i++;
        }
        return value.toString();
    }



    //特殊字符替代
    //处理了位置信息，XX位 前 后 前二 后二 王千百十个，在位置前面添加了一个特殊字符
    //处理了全 单 双 大 小
    //处理了合 将其数字符号转化为中文数字
    //不要 直接替代为啥
    private static String repalace(StringDealBean.StringSimpleDealBean bean){
        char[] cs = bean.str.toCharArray();
        String newStr;
        StringBuilder builder = new StringBuilder();
        for(int i =0 ; i<cs.length ;i++){
            if(isNo(cs[i] )&& i< cs.length-1 &&  cs[i+1] == '双' ){
                i = i+1;
                builder.append(ALL_PAI_CHAR);
                continue;
            }else if(cs[i] == '平' || cs[i] == '双' ){

                if(i< cs.length-1 && (cs[i+1] == '重' || cs[i+1] == '从')){
                    if(bean.dec == null){
                        bean.dec = new StringDealBean.StringDecBean();
                    }
                    bean.dec.isDuizi = true;
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
                if(bean.dec == null){
                    bean.dec = new StringDealBean.StringDecBean();
                }
                bean.dec.isDuizi = true;
                i = i+1;
                builder.append("死00 11 22 33 44 55 66 77 88 99死");
                continue;
            }else if(cs[i] == '合' && i< cs.length-1 && (cs[i+1] == '单' ||  cs[i+1] == '双')){
                if( cs[i+1] == '单'){
                    if(bean.dec == null){
                        bean.dec = new StringDealBean.StringDecBean();
                    }
                    bean.dec.isHeDang = true;
                    //{'拾','壹','贰','叁','肆','伍','陆','柒','捌','玖'}
                    builder.append("合壹叁伍柒玖死");
                }else if( cs[i+1] == '双'){
                    if(bean.dec == null){
                        bean.dec = new StringDealBean.StringDecBean();
                    }
                    bean.dec.isHeChuang = true;
                    builder.append("合拾贰肆陆捌死");
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
                while (i < cs.length && cs[i]== ' ' ) {
                    i++;
                }
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
            }else if(cs[i]== '（' ||cs[i]== '(') {
                builder.append(cs[i]);
                int yuanindex = 1;
                while (i+ yuanindex<cs.length && cs[i+yuanindex] != ')' && cs[i+yuanindex]!= '）') {
                    builder.append(cs[i+yuanindex]);
                    yuanindex++;
                }
                i = i+yuanindex;
                if( cs[i] == ')' || cs[i]== '）'){
                    builder.append(cs[i]);
                    builder.delete( builder.length() - yuanindex -1 , builder.length());
                    builder.append(StringDealFactory.NEW_SPLIE_CHAR);
                }

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
        bean.str =  builder.toString();
        XposedBridge.log("bean.str  ==="+bean.str );
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

    private static ArrayList<String> spileStringNew(String str){
        str = str.replace(",","\n");
        str = str.replace("，","\n");
        str = str.replace("。","\n");
        str = str.replace("、","\n");
        ArrayList<String> list = new ArrayList<>();
        String[] strs = str.split("\n");
        for(String s: strs){
            if(TextUtils.isEmpty(s)){
                continue;
            }else{
                list.add(s);
            }
        }
        return list;
    }


    //字符串的分割
 /*   private static StringDealBean.StringListDealBean spileString(String str){
        StringDealBean.StringListDealBean result = new StringDealBean.StringListDealBean();
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
                StringDealBean.StringListDealBean tmpr = sigleLineSpile(s);
                if(tmpr ==null ){
                    value.add(s);
                    continue;
                }
                ArrayList<String> tmp = tmpr.list;
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
        result.list = value;
        return  result;
    }
    private static  StringDealBean.StringListDealBean sigleLineSpile(String str){
        StringDealBean.StringListDealBean result = new StringDealBean.StringListDealBean();
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
                    if(cs[i] != ',' && cs[i] !='.'&&cs[i] != '，' && cs[i] !='。'){
                        result.isTrue = false;
                    }
                    return result;
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
    }*/
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

    public static boolean isNo(char c){
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
