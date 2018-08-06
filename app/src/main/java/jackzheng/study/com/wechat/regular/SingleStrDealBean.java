package jackzheng.study.com.wechat.regular;

import java.util.ArrayList;

public class SingleStrDealBean {
    public ArrayList<String> numberList = new ArrayList<>();
    public ArrayList<Integer> numberCountList = new ArrayList<>();
    public boolean haveGroup = false;
    public boolean haveCount = false;
    public boolean haveLoacl = false;
    public Boolean isHe ;
    public Boolean isPai;
    public ArrayList<String> spilStrList = new ArrayList<>();
    public ArrayList<Integer[]> mLocal = new ArrayList<>();
    public ArrayList<Integer> mLocalCount= new ArrayList<>();
    public int heNumber = 0;
    public boolean isHaveLatSpile = false;
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("numberList:");
        for(String num : numberList){
            builder.append("%"+num+"%");
        }
        builder.append("\nnumberCountList:");
        for(int num : numberCountList){
            builder.append("%"+num+"%");
        }
        builder.append("\nmLocalCount:");
        for(int num : mLocalCount){
            builder.append("%"+num+"%");
        }
        builder.append("\nmLocal:");
        for(Integer[] num : mLocal){
            builder.append("%"+num[0]+" "+num[1]+"%");
        }
        builder.append("\nspilStrList:");
        for(String num : spilStrList){
            builder.append("%"+num+"%");
        }
        builder.append("\nhaveGroup = "+haveGroup+" haveCount="+haveCount+" haveLoacl="+haveLoacl+" heNumber="+heNumber+" isPai="+isPai+" isHe"+isHe);
        return builder.toString();
    }
}
