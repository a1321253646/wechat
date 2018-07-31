package cn.grandfan.fanda.utils;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularUtils {


    public static boolean hasNumber(String str) {
        boolean match = isMatch(str, ".*\\d+.*");
        Log.d("zsbin", str + " is have number is "+match);
        return match;
    }
    private static boolean isMatch(String str, String pattern) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return Pattern.matches(pattern, str);
    }

    private static void add(String str,StringBuilder builder,int leng,int index,char[] chars){
        if(index >0 &&
                (('0' <=chars[index-1] && chars[index-1]<= '9')||
                        (chars[index-1] == '单')||
                        (chars[index-1] == '双')||
                        (chars[index-1] == '小')||
                        (chars[index-1] == '大'))){
            builder.append("填");
            builder.append(str);
        }else if(index < leng-1 &&'0' <=chars[index+1] && chars[index+1]<= '9'){
            builder.append(str);
            builder.append("填");
        }else{
            builder.append(str);
        }
    }

    private static String preRegular(String str){
        StringBuilder builder = new StringBuilder();
        char[] chars = str.toCharArray();
        int leng = chars.length;
        for(int i = 0;i < leng;i++){
            if('单' == chars[i]){
                add("13579",builder,leng,i,chars);
            }else if('双' == chars[i]){
                add("02468",builder,leng,i,chars);
            }else if('小' == chars[i]){
                add("01234",builder,leng,i,chars);
            }else if('大' == chars[i]){
                add("56789",builder,leng,i,chars);
            }else{
                builder.append(chars[i]);
            }
        }
        return builder.toString();
    }


    public static void regularStr(String str){

    }


    static String pattern = "((\\D+)?(前|后)(\\D+)?)?(\\d+)(前|后)?(不|无|排)?(从|重)?(各)?(\\d+)(\\D+)?";
    static String pattern2 = "((\\D+)?(前|后)(\\D+)?)?(\\d+)(\\D+)?(-+|—+|～+|一+)(\\D+)?(\\d+)(前|后)?(不|无|排)?(从|重)?(各)?(\\D+)?(\\d+)(\\D+)?";
    static String pattern3 = "(\\D+)?(前|后)?(\\D+)?(全)?(头)?(\\d+)(-+|—+|～+|一+)?(全)?(尾)?(各)?(\\D+)?(\\d+)(\\D+)?";
    static String patternAll = "((前)?(后)?)?((\\d+)(位))?((千|百|十|个)(千|百|十|个))?((去|不|无|排)(从|重)?)?((全)?(头)?)?(杀)?(\\D+)?(\\d+)?" +
            "(((\\D+)?全)?(尾)?)?((前)?(后)?)?((\\d+)(位))?((千|百|十|个)(千|百|十|个))?((去|不|无|排)(从|重)?)?(杀)?(\\D+)?(\\d+)?" +
            "(((\\D+)?全)?(尾)?)?((前)?(后)?)?((\\d+)(位))?((千|百|十|个)(千|百|十|个))?((去|不|无|排)(从|重)?)?(杀)?(\\D+)?(\\d+)?" +
            "((前)?(后)?)?((\\d+)(位))?((千|百|十|个)(千|百|十|个))?((去|不|无|排)(从|重)?)?(\\D+)?";

    public static void regularString(String str){
        String[] strings = splieString(str);
        for(String str1 : strings){
            getNumberFromStr(str1);
        }
    }

    private static String[] splieString(String str){
        Log.d("zsbin","splieString");
        //       String [] dataStr = str.split(",|.|，|。|;|；|:|：| |\n");
        str = replace(str,",");
        str = replace(str,"\\.");
        str = replace(str,";");
        str = replace(str,":");
        str = replace(str,"，");
        str = replace(str,"；");
        str = replace(str,"：");
        str = replace(str,"\n");
        Log.d("zsbin","正则表达式分割++ str ="+str);
        String [] dataStr = str.split("(。)+");
        return  dataStr;
    }

    private static String replace(String str ,String target){
        return str.replaceAll(target,"。");
    }
    public static DataBean getNumberFromStr(String str) {

        //前后去重全头阿斯顿撒123全尾12位排重杀而我却二234全尾十个无从杀奥术大师多3456千百无从奥术大师
        Pattern r = Pattern.compile(patternAll);
        str = preRegular(str);
        // 现在创建 matcher 对象
        Matcher m = r.matcher(str);
        if (m.find()) {
            int couunt = m.groupCount();

            if (couunt == 4) {
                DataBean bean = new DataBean();
                bean.pre = m.group(1);
                bean.back = m.group(2);
                bean.count = m.group(3);
                bean.toString();
                return bean;

            }else if(couunt == 3){
                DataBean bean = new DataBean();
                bean.all = m.group(1);
                bean.count = m.group(2);
                bean.toString();
                return bean;
            }else{
         //       Log.d("zsbin", "error: " + str + " matcher group count =" + couunt);
                StringBuilder builder = new StringBuilder();
                for(int i = 0;i<couunt+1;i++){
                    builder.append(" ["+i+"]");
                    builder.append(m.group(i));

                }
        //        Log.d("zsbin","str = "+builder.toString());
                Log.d("zsbin","result::"+new DateBean(m).toString());
                return null;
            }

        } else {
            Log.d("zsbin", "NO MATCHER "+str);
            return null;
        }
    }


    public static String deleteBlank(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String str2 = str.replaceAll(" ", "");
        Log.d("zsbin","str = "+str+">>>str2= "+str2);
        return str;
    }

    public static class DataBean {
        String pre;
        String back;
        String count;
        String all;


        public int[] getPreIntArrary() {
            if (TextUtils.isEmpty(pre)) {
                return null;
            }
            return stringToIntArray(pre);
        }

        public int[] getBackIntArrary() {
            if (TextUtils.isEmpty(back)) {
                return null;
            }
            return stringToIntArray(back);
        }

        public int[] getAllIntArrary() {
            if (TextUtils.isEmpty(all)) {
                return null;
            }
            return stringToIntArray(all);
        }

        public String toString(){
            String str = "new DataBean pre = " + pre + " back = " + back + " count = " + count +" all = "+all;
            Log.d("zsbin", str);
            return str;
        }

        private static int[] stringToIntArray(String str) {
            int[] arr = new int[str.length()];
            for (int i = 0; i < str.length(); i++) {
                arr[i] = Character.getNumericValue(str.charAt(i));
            }
            return arr;
        }

        public String getPre() {
            return pre;
        }

        public void setPre(String pre) {
            this.pre = pre;
        }

        public String getBack() {
            return back;
        }

        public void setBack(String back) {
            this.back = back;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getAll() {
            return all;
        }

        public void setAll(String all) {
            this.all = all;
        }
    }
}
