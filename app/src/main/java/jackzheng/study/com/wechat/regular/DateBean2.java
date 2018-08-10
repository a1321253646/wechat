package jackzheng.study.com.wechat.regular;

import java.util.ArrayList;

public class DateBean2 {

    public boolean isNoSame = false;
    public ArrayList<Integer[]> mLastData = new ArrayList<>();
    public ArrayList<ArrayList<Integer>> mDataList = new ArrayList<>();
    public ArrayList<Integer[]> local = new ArrayList<>();
    public  ArrayList<Integer> mCountList = new ArrayList<>();
    public Boolean isHe ;
    public int heNumber = 0;
    public int allCount;
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
         if(mDataList.size() >0){
            builder.append("头尾 = ");
            for(ArrayList<Integer> ss : mDataList){
                for(Integer inte : ss){
                    builder.append(""+inte);
                }
                builder.append("-");
            }
        }else if(mLastData.size() >0){
             builder.append("单吊 = ");
             for(Integer[] data : mLastData){
                 builder.append("["+data[0]+data[1]+"] ");
             }
         }
        builder.append("    位置 = ");
        for(Integer[] localitem : local ){
            builder.append("["+localitem[0]+"，"+localitem[1]+"] ");
        }
        builder.append("注数 = ");
        for(Integer count : mCountList ){
            builder.append("["+count+"] ");
        }

        builder.append((isNoSame?"排":"")+"重 ");
        if(isHe !=null){
            builder.append((isHe?"":"不")+"合 "+heNumber);
        }
        builder.append("共 "+allCount);
        return builder.toString();
    }
}
