package cn.grandfan.fanda.utils;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Matcher;

public class DateBean {
    Matcher mData;
    private boolean isKill = false;
    private boolean isAllHeart = false;
    private boolean isAllEnd = false;
    private boolean isNoSame = false;
    private int[] heart;
    private int[] end;
    private int[][] local;
    private int count = 0;
    private String other1;
    private String other2;
    private String other3;

    private final int[] isKillIndex = {16,35,54};
    private final int[] isAllHeartIndex = {14};
    private final int[] isAllEndIndex = {20,39};
    private final int[] isNoSameIndex = {11,33,52,67};
    private final int[] isLocalIndex = {1,4,7,23,26,29,42,45,48,57,60,63};

    private int FRIST_DATA_INDEX = 18;
    private int SECOND_DATA_INDEX = 37;
    private int THIRD_DATA_INDEX = 56;

    public DateBean(Matcher data){
        mData = data;
        init();
    }
    private void init(){
        for(int i=0;i <isLocalIndex.length ;i++){
            if(!TextUtils.isEmpty(mData.group(isLocalIndex[i]))){
                if(i==0){//前后
                    if(!TextUtils.isEmpty(mData.group(isLocalIndex[i]+1))){
                        local = new int[2][2];
                        local[0] = new int[]{1,2};
                    }
                    if(!TextUtils.isEmpty(mData.group(isLocalIndex[i]+2))){
                        if(local == null){
                            local = new int[2][2];
                            local[0] = new int[]{3,4};
                        }else{
                            local[1] = new int[]{3,4};
                        }
                    }
                }else if(i==1){//数字指位
                    local = new int[2][2];
                    local[0] = getIntArrary(mData.group(isLocalIndex[i]+1));
                }else if(i==2){
                    local = new int[2][2];
                    String fir =mData.group(isLocalIndex[i]+1);
                    String sec =mData.group(isLocalIndex[i]+1);
                    local[0] =  new int[2];
                    local[0][0] = getIndexForChina(fir);
                    local[0][1] = getIndexForChina(sec);
                }
            }
        }
        if(local == null){
            local = new int[2][2];
            local[0] = new int[]{3,4};
        }

        for(int i : isKillIndex){
            if(!TextUtils.isEmpty(mData.group(i))){
                isKill = true;
            }
        }
        for(int i : isNoSameIndex){
            if(!TextUtils.isEmpty(mData.group(i))){
                isNoSame = true;
            }
        }

        for(int i : isAllHeartIndex){
            if(!TextUtils.isEmpty(mData.group(i))){
                isAllHeart = true;
                heart = new int[]{0,1,2,3,4,5,6,7,8,9};
            }
        }

        for(int i : isAllEndIndex){
            if(!TextUtils.isEmpty(mData.group(i))){
                isAllEnd = true;
                end = new int[]{0,1,2,3,4,5,6,7,8,9};
            }
        }

        if(!isKill){
            if(!TextUtils.isEmpty(mData.group(THIRD_DATA_INDEX)) && !isAllEnd && !isAllHeart){
                heart = getIntArrary(mData.group(FRIST_DATA_INDEX));
                end = getIntArrary(mData.group(SECOND_DATA_INDEX));
                count =getCountIntArrary(mData.group(THIRD_DATA_INDEX));
            }else{
                if(isAllEnd){
                    heart = getIntArrary(mData.group(FRIST_DATA_INDEX));
                }else if(isAllHeart){
                    end = getIntArrary(mData.group(FRIST_DATA_INDEX));
                }else{
                    heart = end = getIntArrary(mData.group(FRIST_DATA_INDEX));
                }
                count = getCountIntArrary(mData.group(SECOND_DATA_INDEX));
            }
        }else{
            if(!TextUtils.isEmpty(mData.group(THIRD_DATA_INDEX)) ){
                if(!TextUtils.isEmpty(mData.group(isKillIndex[0]))){
                    heart =  killDeal(mData.group(FRIST_DATA_INDEX));
                    end = getIntArrary(mData.group(SECOND_DATA_INDEX));
                }else if(!TextUtils.isEmpty(mData.group(isKillIndex[0]))){
                    heart = getIntArrary(mData.group(FRIST_DATA_INDEX));
                    end = killDeal(mData.group(SECOND_DATA_INDEX));
                }
                count =getCountIntArrary(mData.group(THIRD_DATA_INDEX));
            }else{
                int[] tmp = killDeal(mData.group(18));
                if(heart == null){
                    heart = tmp;
                }
                if(end == null){
                    end = tmp;
                }
                count =getCountIntArrary(mData.group(SECOND_DATA_INDEX));
            }
        }
    }

    private int[] killDeal(String str){
        int[] filter = getIntArrary(str);
        ArrayList<Integer> a = new ArrayList<>();
        boolean isFilter;
        for(int i= 0 ;i<10 ;i++){
            isFilter = false;
            for(int ii : filter){
                if(i == ii){
                    isFilter = true;
                    break;
                }
            }
            if(!isFilter){
                a.add(i);
            }
        }
        int[] result = new int[a.size()];
        for(int index = 0 ; index <a.size();index++){
            result[index] = a.get(index);
        }
        return result;
    }

    private int getIndexForChina(String str){
        int index = 0;
        if(str.equals("千")){
            index = 4;
        }else  if(str.equals("百")){
            index = 3;
        }else  if(str.equals("十")){
            index = 2;
        }else  if(str.equals("个")){
            index = 1;
        }
        return index;
    }
    private int[] getIntArrary(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return stringToIntArray(str);
    }
    private static int[] stringToIntArray(String str) {
        int[] arr = new int[str.length()];
        for (int i = 0; i < str.length(); i++) {
            arr[i] = Character.getNumericValue(str.charAt(i));
        }
        return arr;
    }
    private int getCountIntArrary(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        return Integer.parseInt(str);
    }
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(mData.group(0));
        builder.append("--> ");
        if(heart == null || heart.length <1){
            builder.append("无头错误");
            //return builder.toString();
        }else{
            for(int hear : heart){
                builder.append(hear);
            }
        }
        builder.append(" - ");
        if(end == null || end.length <1){
            builder.append("无尾错误");
          //  return builder.toString();
        }else{
            for(int hear : end){
                builder.append(hear);
            }
        }

        builder.append(" ");
        if(local == null || local.length <1){
            builder.append("位置信息错误");
            //  return builder.toString();
        }else{
            for(int[] tmp : local){
                if(tmp != null && tmp.length == 2){
                    if(tmp[0] == 0 || tmp[1]== 0){
                        continue;
                    }
                    builder.append(tmp[0]);
                    builder.append(tmp[1]);
                    builder.append("位");
                }
            }
        }

        if(isNoSame){
            builder.append("排重");
        }
        builder.append("各");
        builder.append(count);
        builder.append("组");
        if(isKill){
            builder.append("杀");
        }
        return  builder.toString();
    }
}
