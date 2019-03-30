package jackzheng.study.com.wechat.regular;

import java.util.ArrayList;

public class SscBean {
    public String msg;
    public ArrayList<DateBean2> list;
    public int count = 0;
    public long mId = -1;
    public long msgId = -1;
    @Override
    public String toString() {
        return "["+mId +"] "+msg+" 共"+count;
    }
}
