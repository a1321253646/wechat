package jackzheng.study.com.wechat;
import android.text.TextUtils;
import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.robv.android.xposed.XposedBridge;
import okhttp3.Call;


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
    private static  MaxIndexResult mGetIndex= null;
    private static boolean isGetResouce = false;
    public  static MaxIndexResult parseQuite(final int nowIndex){
        mGetIndex = null;
        isGetResouce = false;
        Log.d("zsbin","HtmlParse MaxIndexResult = ");
        MaxIndexResult result = new MaxIndexResult();
        try {
            OkHttpUtils
                    .get()
                    .url("http://156.236.71.178:10086/query?tdsourcetag=s_pctim_aiomsg")
                    .build()
                    .execute(new StringCallback()
                    {

                        @Override
                        public void onError(Call call, Exception e, int id) {
                            isGetResouce = true;
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            isGetResouce = true;
                            try {
                                JSONObject json = new JSONObject(response);
                                long index = json.getLong("index");
                                if(index%1000 == nowIndex){
                                    MaxIndexResult result = new MaxIndexResult();
                                    result.index = nowIndex;
                                    result.str = json.getString("number").replace(",","");
                                    if(result.index == nowIndex && result.str.length() == 5 && isAllNumber(result.str)){
                                        mGetIndex = result;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }catch(Exception e) {
            e.printStackTrace();
            isGetResouce = true;
        }
        while (!isGetResouce){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return mGetIndex;
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
