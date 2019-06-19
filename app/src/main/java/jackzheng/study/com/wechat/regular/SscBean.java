package jackzheng.study.com.wechat.regular;

import java.util.ArrayList;

import jackzheng.study.com.wechat.sscManager.DateSaveManager;

public class SscBean {
    public String msg;
    public ArrayList<DateBean2> list;
    public float count = 0;
    public long mId = -1;
    public long msgId = -1;
    public boolean isTure = false;
    public boolean isThirdModel = false;
    @Override
    public String toString() {
        if(DateSaveManager.getIntance().isThird){
            return "["+mId +"] "+msg+" 共"+count;

        }else{

            return "["+mId +"] "+msg+" 共"+((long)count);
        }

    }
}
