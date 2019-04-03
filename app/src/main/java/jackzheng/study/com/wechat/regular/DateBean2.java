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

    public StringDealBean.StringDecBean  dec= null;

    private void getOtherLoc(ArrayList<Integer[]> list,boolean isAll,StringBuilder builder){
        ArrayList<Integer[]> tmp = new ArrayList<Integer[]>();
        StringBuilder tmpBuild = new StringBuilder();
        if(isAll ){
            if(list.size() <= 10){
                return;
            }
            for(Integer[] item : list){
                tmp.add(item);
            }
            for(int i =1 ; i< 6 ;i++){
                for(int ii = i+1;ii< 6;ii++){
                    for(int iii = 0 ;iii< tmp.size() ;iii ++){
                        Integer[] item = tmp.get(iii);
                        if(item[0]== i && item[1]==ii){
                            tmp.remove(iii);
                            break;
                        }
                    }
                }
            }
            for(Integer[] item : tmp){
                builder.append(".");
                builder.append(StringDealFactory.LOCAL_REPALCE[item[0]-1]+StringDealFactory.LOCAL_REPALCE[item[1]-1]);
            }

        }else {
            if(list.size() <= 5 ){
                return;
            }
            for(Integer[] item : list){
                tmp.add(item);
            }
            for(int i =1 ; i< 6 ;i++){
                int ii = i==5?4:i+1;
                for(int iii = 0 ;iii< tmp.size() ;iii ++){
                    Integer[] item = tmp.get(iii);
                    if(item[0]== i && item[1]==ii){
                        tmp.remove(iii);
                        break;
                    }
                }
            }
            for(Integer[] item : tmp){
                builder.append(".");
                builder.append(StringDealFactory.LOCAL_REPALCE[item[0]-1]+StringDealFactory.LOCAL_REPALCE[item[1]-1]);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        boolean isHaceDec = false;
        if(dec != null){
            if(dec.isWuwei){
                builder.append("全位");
                getOtherLoc(local,false,builder);
                isHaceDec = true;
            }else if(dec.isAllWei){
                builder.append("任二");
                getOtherLoc(local,true,builder);
                isHaceDec = true;
            }
        }
        if(!isHaceDec){
            boolean isFirstrLoc = true;
            int leng = local.size();
            if(dec != null && dec.isXiajiang){
                leng = leng/2;
            }
            for(int i = 0; i< leng ;i++){
                Integer[] loc = local.get(i);
                if(!isFirstrLoc){
                    builder.append(".");
                }else{
                    isFirstrLoc = false;
                }
                builder.append(StringDealFactory.LOCAL_REPALCE[loc[0]-1]+""+StringDealFactory.LOCAL_REPALCE[loc[1]-1]);
            }

        }
        builder.append(":");
        isHaceDec = false;
        if(dec != null){
            if(dec.isXiajiang){
                builder.append(dec.xiajiangNumber+"下奖");
                isHaceDec = true;
            }else if(dec.isDuizi){
                builder.append("对子");
                isHaceDec = true;
            }else if(dec.isHeChuang){
                builder.append("合双");
                isHaceDec = true;
            }else if(dec.isHeDang){
                builder.append("合单");
                isHaceDec = true;
            }
        }
        if(!isHaceDec){
            boolean isFirstrLoc = true;
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
                    if(isFirstrLoc){
                        isFirstrLoc = false;
                    }else{
                        builder.append(".");
                    }
                    builder.append(""+data[0]+data[1]);
                }
            }
        }
        if(isHe !=null){
            builder.append((isHe?"":"不")+"合");
            for(Integer number : heNumber){
                builder.append(number);
            }
        }
        if(isNoSame){
            builder.append("排");
        }else{
            builder.append("=");
        }
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
            builder.append(""+c);
        }else{
            boolean isFirstrLoc = true;
            for(Integer count : mCountList ){
                if(isFirstrLoc){
                    isFirstrLoc = false;
                }else{
                    builder.append(".");
                }
                builder.append(count);
            }
        }
        builder.append("共");
        builder.append(allCount);
        /*
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
        builder.append(allCount);*/
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
