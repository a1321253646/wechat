package jackzheng.study.com.wechat.regular;

import java.util.ArrayList;

public class Regular3Utils {
    public static ArrayList<Regular3Bean> regular(String[] strs){
        ArrayList<Regular3Bean> result = new  ArrayList<Regular3Bean>();
        for(String str : strs){

        }

        return result;

    }



    public static class Regular3Bean{
        public ArrayList<Integer> mLocal = new ArrayList<>();
        public ArrayList<ArrayList<Integer>> mNumber = new ArrayList<>();
        public int mCount = 0;
        public int mAllCount = 0;
        public boolean isPaiSanXiongDi = false;
        public boolean isPaiTwoChong = false;
        public boolean isPaiThreeChong = false;

        public static void getBeans(String[] strs ,RegularStrBean back ){
            ArrayList<Regular3Bean> result = new ArrayList<>();
            for(String str : strs){
                Regular3Bean bean = getBean(str);
                if(bean == null){
                    return ;
                }else{
                    result.add(bean);
                }
            }
            if(result.size() == 0){
                return ;
            }
            back.list3 = result;
        }

        public static Regular3Bean getBean(String str){
            if(!is3Bean(str)){
                return null;
            }
            Regular3Bean result = new Regular3Bean();
            if(str.contains("三兄弟")){
                result.isPaiSanXiongDi = true;
            }
            if(str.contains("三重")){
                result.isPaiThreeChong = true;
            }
            if(str.contains("二重")||str.contains("双重")){
                result.isPaiTwoChong = true;
            }
            char[] cs = str.toCharArray();
            for(int i = 0;i<cs.length;){
                if(StringDealFactory.isLocal(cs[i])){
                    result.mLocal.add(StringDealFactory.getLocalData(cs[i]));
                    i++;
                }else if(StringDealFactory.isNumber(cs[i])){
                    if(result.mNumber.size() == 4){
                        return null;
                    }

                    if(result.mNumber.size() == 3){
                        StringBuilder builder = new StringBuilder();
                        while (i < cs.length && StringDealFactory.isNumber(cs[i])){
                            builder.append(cs[i]);
                            i++;
                        }
                        try {
                            result.mCount = Integer.parseInt(builder.toString());
                        }catch (Exception e){
                            return null;
                        }
                    //    continue;
                    }
                    ArrayList<Integer> list = new ArrayList<>();
                    while (i < cs.length && StringDealFactory.isNumber(cs[i])){
                        list.add(Integer.parseInt(cs[i]+""));
                        i++;
                    }
                    result.mNumber.add(list);
                }
            }
            if(result.mNumber.size() != 3 || result.mLocal.size() !=3 || result.mCount == 0){
                return null;
            }
            result.mAllCount = result.mNumber.get(0).size() *result.mNumber.get(1).size() *result.mNumber.get(2).size();
            int count = 0;
            if(result.isPaiSanXiongDi){
                for(int tmp : result.mNumber.get(0)){
                    for(int tmp2 : result.mNumber.get(1)){
                        if(tmp2 == tmp +1){
                            for(int tmp3 : result.mNumber.get(2)){
                                if(tmp3 == tmp+2 || tmp3 == tmp-1){
                                    count++;
                                }
                            }
                        }else if(tmp2== tmp -1){
                            for(int tmp3 : result.mNumber.get(2)){
                                if(tmp3 == tmp+1 || tmp3 == tmp-2){
                                    count++;
                                }
                            }
                        }else if(tmp2== tmp -2){
                            for(int tmp3 : result.mNumber.get(2)){
                                if(tmp3 == tmp-1){
                                    count++;
                                }
                            }
                        }if(tmp2== tmp +2){
                            for(int tmp3 : result.mNumber.get(2)){
                                if(tmp3 == tmp+1){
                                    count++;
                                }
                            }
                        }
                    }
                }
            }
            if(result.isPaiThreeChong){
                for(int tmp : result.mNumber.get(0)){
                    for(int tmp2 : result.mNumber.get(1)) {
                        if (tmp2 == tmp) {
                            for (int tmp3 : result.mNumber.get(2)) {
                                if (tmp3 == tmp + 2 || tmp3 == tmp - 1) {
                                    count++;
                                }
                            }
                            break;
                        }
                    }
                }
            }
            if(result.isPaiTwoChong){

            }
            return result;
        }

        public static boolean is3Bean(String stmStr){
            char[] tmpChars = stmStr.toCharArray();
            int localIndex = 0;
            int numberCount2 = 0;
            for(int i = 0 ; i< tmpChars.length ; ){
                if(StringDealFactory.isLocal(tmpChars[i])){
                    localIndex++;
                    i++;
                }else if(StringDealFactory.isNumber(tmpChars[i])){
                    while (i< tmpChars.length && StringDealFactory.isNumber(tmpChars[i])){
                        i++;
                    }
                    numberCount2++;
                }else{
                    i++;
                }
            }
            if(localIndex== 3 && numberCount2 ==4){
                return true;
            }else{
                return false;
            }

        }
    }
}
