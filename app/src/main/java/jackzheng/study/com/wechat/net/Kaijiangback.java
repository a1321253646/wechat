package jackzheng.study.com.wechat.net;

import java.util.ArrayList;

import jackzheng.study.com.wechat.regular.SscBean;
import jackzheng.study.com.wechat.sscManager.ServerManager2;

public class Kaijiangback {

    public String openNumber;
    public int index;
    public ArrayList<KaijiangGroupBackData> date;


    public static class KaijiangGroupBackData{
        public String group;
        public ArrayList<KaijiangGroupBackDataItem> date;
    }
    public static class KaijiangGroupBackDataItem{
        public long id;
        public ServerManager2.ZhongDate date;
    }
}
