package jackzheng.study.com.wechat.net;

import java.util.ArrayList;

import jackzheng.study.com.wechat.regular.SscBean;

public class KaijianRequite {
    public String user;
    public String openNumber;
    public int index;
    public int isThird;
    public ArrayList<KaijiangGroupData> date;


    public static class KaijiangGroupData{
        public String group;
        public ArrayList<SscBean> date;
    }
}
