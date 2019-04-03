package jackzheng.study.com.wechat.regular;

import java.util.ArrayList;

public class StringDealBean {

    public static class StringListDealBean{
        ArrayList<StringSimpleDealBean> list ;
        public boolean isTrue = true;
    }

    public static class  StringSimpleDealBean{
        String str;
        StringDecBean dec;
    }


    public static class StringDecBean{
        String xiajiangNumber;
        boolean isXiajiang= false;
        boolean isDuizi = false;
        boolean isWuwei = false;
        boolean isAllWei = false;
        boolean isHeDang = false;
        boolean isHeChuang = false;

    }
}
