package jackzheng.study.com.wechat.sscManager;

import java.util.ArrayList;

import jackzheng.study.com.wechat.regular.DateBean;
import jackzheng.study.com.wechat.regular.DateBean2;

public class Sscbean {
    private static int ID=0;
    private int id;
    String message;
    ArrayList<DateBean2> mList;
    int count = 0;
    public static void resetID(){
        ID=0;
    }
    public Sscbean(String message){
        ID++;
        id = ID;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<DateBean2> getmList() {
        return mList;
    }

    public void setmList(ArrayList<DateBean2> mList) {
        this.mList = mList;
    }

    public int getId(){
        return id;
    }
    public String toString(){
        StringBuilder b = new StringBuilder();
        b.append("Sscbean id="+id+" 下注数据\n");
        if(mList != null && mList.size() >0 ){
            for(DateBean2 data : mList){
                b.append("count [" );
                for(Integer count : data.mCountList){
                    b.append(""+count+" ");
                }
                b.append("]" );
                b.append(" locla=[");
                for(Integer[] local : data.local){
                    b.append("["+local[0]+"]["+local[1]+"]");
                }
                b.append("]");
                int i = 0;
                int ii = 0;
                for(Integer[] data2 : data.mLastData){
                    if(i == 0){
                        ii++;
                        b.append("\n       "+ii+"");
                    }
                    i++;
                    if(i== 10){
                        i = 0;
                    }

                    b.append("["+data2[0]+"]["+data2[1]+"] ");
                }
                b.append("\n ");
            }
            return b.toString();
        }
        return "error";
    }
    public int getCount(){
        if(mList == null || mList.size() == 0){
            return  0;
        }
        if(count == 0){
            for(DateBean2 data :mList){
                count += data.allCount;
            }
        }
        return count;
    }
}
