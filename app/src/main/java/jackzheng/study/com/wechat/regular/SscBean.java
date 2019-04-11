package jackzheng.study.com.wechat.regular;

import java.util.ArrayList;

public class SscBean {
    public String msg;
    public String group;
    public ArrayList<DateBean2> list;
    public int count = 0;
    public long mId = -1;
    public long msgId = -1;
    public String orderNo;
    public boolean isTrue;
    public long issue;
    @Override
    public String toString() {
        return "["+mId +"] "+msg+" 共"+count;
    }

    public String getStr(){
        StringBuilder builder = new StringBuilder();
        if(list != null){
            for(DateBean2 tmp : list){
                count += tmp.allCount;
                builder.append( tmp.toString()+"\n");
            }
            return builder.toString();
        }
        return "";
    }

    public String getNetDate(){
        boolean isFrist = true;
        StringBuilder builder = new StringBuilder();
        for(DateBean2 date  : list){
            for(int i = 0 ; i< date.local.size() ; i++){
                Integer[] local = date.local.get(i);
                String demn = getLoaclDemo(local,date.mCountList.get(i));
                for(Integer[] xia : date.mLastData){
                   if(!isFrist){
                       builder.append(",");
                   }
                    isFrist = false;
                    builder.append(demn.replace("1",xia[0]+"").replace("2",xia[1]+""));
                }

            }
        }

        return  builder.toString();
    }

    private String getLoaclDemo(Integer[] local,int number){
        StringBuilder builder = new StringBuilder();
        char[] cs = new char[]{'X','X','X','X','X'};
        cs[local[0]-1] = '1';
        cs[local[1]-1] = '2';
        builder.append(cs);
        builder.append("=");
        builder.append(number);
        return builder.toString();
    }
}
