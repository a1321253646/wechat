package jackzheng.study.com.wechat.regular;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
public class DateBean2 {
    public String message;
    public boolean isNoSame = false;
    public ArrayList<Integer[]> mLastData = new ArrayList<>();
    public ArrayList<ArrayList<Integer>> mDataList = new ArrayList<>();
    public ArrayList<Integer[]> local = new ArrayList<>();
    public  ArrayList<Integer> mCountList = new ArrayList<>();
    public Boolean isHe ;
    public Boolean mHaveGroup ;
    public ArrayList<Integer> heNumber = new ArrayList<>();
    public int allCount;
    public boolean isALlUserFri = false;
    public boolean isALlUserLast = false;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        //     builder.append(message+"\n----------\n");
        builder.append("[玫瑰]【");
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
        builder.append("】[");
        for(Integer[] localitem : local ){
            builder.append(""+localitem[0]+localitem[1]+".");
        }
        builder.append("]");
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
        builder.append("(");
        if(isAllSome){
            builder.append(""+c);
        }else{
            for(Integer count : mCountList ){
                builder.append(count+".");
            }
        }
        builder.append(")");
        builder.append(isNoSame?"排":"重");
        if(isHe !=null){
            builder.append((isHe?"":"不")+"合");
            for(Integer number : heNumber){
                builder.append(number);
            }
            builder.append("共");
        }
        builder.append(allCount);
        return builder.toString();
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("pai", isNoSame);
            if(isHe != null) {
                if(isHe) {
                    json.put("he", 2);
                }else {
                    json.put("he", 1);
                }
                json.put("heNumber", heNumber);
            }else {
                json.put("he", 0);
            }
            json.put("all_ount", allCount);
            json.put("last_data", mLastData.toArray());
            json.put("local", local.toArray());
            json.put("count_list", mCountList.toArray());
            json.put("date_list", mDataList.toArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //json.put("count_list", mCountList.toArray());

        return json;

    }

    public static JSONArray DateBean2ArrayListToJson( ArrayList<DateBean2>  list) {
        JSONArray array = new JSONArray();
        if(list.size() > 0) {
            for(DateBean2 d : list) {
                array.put(d.toJson());
            }
        }
        return array;
    }
}
