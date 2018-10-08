package jackzheng.study.com.wechat.regular;

import java.util.ArrayList;

public class DateBean2 {

    public boolean isNoSame = false;
    public ArrayList<Integer[]> mLastData = new ArrayList<>();
    public ArrayList<ArrayList<Integer>> mDataList = new ArrayList<>();
    public ArrayList<Integer[]> local = new ArrayList<>();
    public  ArrayList<Integer> mCountList = new ArrayList<>();
    public Boolean isHe ;
    public Boolean mHaveGroup ;
    public int heNumber = 0;
    public int allCount;
    public boolean isALlUserFri = false;
    public boolean isALlUserLast = false;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("【");
         if(mDataList.size() >0){
             for(int i = 0;i <mDataList.size() ;i++){
                 ArrayList<Integer> ss = mDataList.get(i);
                 for(Integer inte : ss){
                     builder.append(""+inte);
                 }
                 if(i <mDataList.size() -1){
                     builder.append("-");
                 }
             }
        }else if(mLastData.size() >0){
             for(Integer[] data : mLastData){
                 builder.append(""+data[0]+data[1]+".");
             }
         }
        builder.append("】");
        builder.append("位");
        for(Integer[] localitem : local ){
            builder.append("["+localitem[0]+localitem[1]+"]");
        }
        builder.append(" 注");
        boolean isAllSome = true;
        int c = -1;
        for(Integer count : mCountList ){
            if(c != -1 ){
                if(c == count){
                    continue;
                }else{
                    isAllSome = false;
                    break;
                }
            }else{
                c = count;
            }
        }
        if(isAllSome){
            builder.append("("+c+")");
        }else{
            for(Integer count : mCountList ){
                builder.append("("+count+")");
            }
        }
        builder.append(isNoSame?"排":"重");
        if(isHe !=null){
            builder.append((isHe?"":"不")+"合"+heNumber);
        }
        builder.append(" 共"+allCount);
        return builder.toString();
    }
}
