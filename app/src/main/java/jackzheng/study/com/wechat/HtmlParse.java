package jackzheng.study.com.wechat;
import android.text.TextUtils;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.robv.android.xposed.XposedBridge;
import jackzheng.study.com.wechat.regular.StringDealFactory;


public class HtmlParse {

    public  static MaxIndexResult parse(final int nowIndex){
        MaxIndexResult result = new MaxIndexResult();
        int a;
        try {
            Document doc = Jsoup.connect("http://vietlotto.org/analy.php").get();
            Elements elements4 =  doc.select("div.list_right_box");
            XposedBridge.log("MaxIndexResult elements4= "+elements4);
            elements4 = elements4.select("div.item");
            for(Element element7 :elements4){
                String win = "";
                String indexStr = ""+element7.select("div.date");
                String[] strs = indexStr.split("-");
                XposedBridge.log("MaxIndexResult  strs.length = "+strs.length);
                if(strs.length > 1){
                    indexStr = strs[1].replaceAll("\n","");
                    indexStr = indexStr.replaceAll(" ","");
                    indexStr = indexStr.replaceAll("</div>","");
                    if(!TextUtils.isEmpty(indexStr)){
                        int indexTmp = -1;
                            indexTmp = Integer.parseInt(indexStr);
                        if(indexTmp != nowIndex){
                            continue;
                        }else{
                            result.index = indexTmp;
                        }
                    }
                }else{
                    return null;
                }
                String str = ""+element7.select("div.ball");
                strs = str.split("<em>");
                if(strs.length > 5){
                    for(int i = 1;i< strs.length;i++){
                        win += strs[i].split("</em>")[0];
                    }
                }
                if(!TextUtils.isEmpty(win)){
                    result.str = win;
                }
                XposedBridge.log("MaxIndexResult index = "+result.index+" str = "+result.str);
            }
            if(result.index == nowIndex && result.str.length() == 5 && isAllNumber(result.str)){

                return  result;
            }
            return null;
        }catch(Exception e) {
            XposedBridge.log(e.toString());
            return null;
        }
    }
    private static boolean isAllNumber(String str){
        str = str.replaceAll(" ","");
        char[] chars = str.toCharArray();
        for(char c: chars){
            if(c < '0' || c > '9'){
                return false;
            }
        }
        return true;
    }
    public  static MaxIndexResult parseQuite(){
        Log.d("zsbin","HtmlParse MaxIndexResult = ");
        MaxIndexResult result = new MaxIndexResult();
        try {
            Document doc = Jsoup.connect("https://csj.1396j.com/shishicai/?utm=new_csj").get();
            Elements select = doc.select("table#history.lot-table");
            //  Log.d("zsbin","select ="+select.toString());
            for(Element element5 :select){
                String text = element5.text();
                int index = text.indexOf("2018", 0);
                char[] chars = text.toCharArray();
                StringBuilder build = new StringBuilder();
                String qishu=null,haoma=null;
                int count = 0;
                for(int i =index ;i<chars.length; ){
                    if(qishu == null &&chars[i]== '-' && i<chars.length -1 && StringDealFactory.isNumber(chars[i+1])){
                        i++;
                        while (StringDealFactory.isNumber(chars[i])){
                            build.append(chars[i]);
                            i++;
                        }
                        qishu = build.toString();
                        build = new StringBuilder();
                    }else if(chars[i] == ' '){
                        count++;
                        i++;
                    }else if(count == 2 && StringDealFactory.isNumber(chars[i])){
                        for(int ii =0; ii<5 ;ii++ ){
                            build.append(chars[i]);
                            if(i < chars.length -2 && chars[i+1] == ' ' && StringDealFactory.isNumber(chars[i+2])){
                                i= i+2;
                            }else{
                                return null;
                            }
                        }
                        haoma = build.toString();
                        break;
                    }else{
                        i++;
                    }

                }
                result.index = Integer.parseInt(qishu);
                result.str = haoma;
                return  result;
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public static class MaxIndexResult{
        public int index = 0;
        public String str;
        int[] number;
        public int[] getNumber(){
            if(number == null){
                char[] chars = str.toCharArray();
                number= new int[5];
                for(int i = 0 ; i< 5 ;i++){
                    try {
                        number[i] = Integer.parseInt(chars[i]+"");
                    }catch (Exception e){
                        return null;
                    }

                }
            }
            return number;
        }
    }
}
