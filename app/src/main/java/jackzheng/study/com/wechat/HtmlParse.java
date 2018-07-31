package jackzheng.study.com.wechat;
import android.text.TextUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import de.robv.android.xposed.XposedBridge;

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
            XposedBridge.log(e.toString());
            return null;
        }
    }
    public static class MaxIndexResult{
        public int index = 0;
        public String str;
    }
}
