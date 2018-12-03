package jackzheng.study.com.wechat;

import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONObject;

import de.robv.android.xposed.XposedBridge;
import okhttp3.Call;
import okhttp3.Response;

public class MyStringCall extends Callback<String> {

    String groupIdCall;
    String workIdCall;
    String wxIdCall;
    public MyStringCall(String group,String work,String wx){
        groupIdCall = group;
        workIdCall = work;
        wxIdCall = wx;
    }

    @Override
    public String parseNetworkResponse(Response response, int id) throws Exception {
        String s = response.body().string();
        XposedBridge.log("saveDate onResponse = "+s);
        onResponseString(s,id);
        return null;
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        XposedBridge.log("saveDate onErrorResponse = "+e);
    }

    @Override
    public void onResponse(String response, int id) {

    }

    public void onResponseString(String response, int id) {
    }


}
