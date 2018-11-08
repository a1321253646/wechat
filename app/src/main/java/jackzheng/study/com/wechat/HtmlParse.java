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

    public  static MaxIndexResult parse(){
        MaxIndexResult result = new MaxIndexResult();
        int a;
        try {
            Document doc = Jsoup.connect("http://caipiao.163.com/award/cqssc/").get();
            Elements elements4 =  doc.select("td.start");
            for(Element element5 :elements4){
                String win = element5.attr("data-win-number");
                String index =element5.attr("data-period");
                if(!TextUtils.isEmpty(win) && !TextUtils.isEmpty(index)){
                    index = index.substring(index.length() -3 ,index.length());
                    a = Integer.parseInt(index);
                    win = win.replace(" ","");
                    if(a > result.index){
                        result.index = a;
                        result.str = win;
                    }
                }
            }
            return  result;
        }catch(Exception e) {
//            XposedBridge.log(e.toString());
            return null;
        }
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
    public  static MaxIndexResult parse2(){
        MaxIndexResult result = new MaxIndexResult();
        try {
            Document doc = Jsoup.connect("https://www.1396j.com/shishicai").get();
            Elements elements4 =  doc.select("div.lotteryPublic_tableBlock");
            //  .select("div.main_d lotteryPublic_main");
            Elements select = elements4.get(0).select("table#tbHistory.zebra_crossing");
            Element tr = select.select("tr").get(1);
            String time = tr.select("i.font_gray666").text();
            Elements span = tr.select("div.number_redAndBlue").select("span");
            StringBuilder builder = new StringBuilder();
            for(Element element5 :span){
                builder.append(element5.text());
            }
            Log.d("zsbin"," code = "+builder.toString());
            Log.d("zsbin"," time = "+time);
            result.index = Integer.parseInt(time.split("-")[1]);
            result.str = builder.toString();
            return  result;
        }catch(Exception e) {
             XposedBridge.log(e.toString());
            return null;
        }
    }


    public static class MaxIndexResult{
        public int index = 0;
        public String str;
    }
}
