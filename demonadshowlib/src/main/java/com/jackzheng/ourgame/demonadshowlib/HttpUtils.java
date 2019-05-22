package com.jackzheng.ourgame.demonadshowlib;

import android.app.Activity;

import com.uniplay.adsdk.utils.Base64;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.uniplay.adsdk.utils.Utils.readStream;

public class HttpUtils {


    public static String sdkversion = "1.0.0";
    private final static String KEY="BUi%gs@*jPoC*#sc";
    private final static String IV="T43k&(Jsd8&9UUI9";
/**
     * aes 加密
     * @param data
     * @return
     */
    public static String encryptData(String data){
        try {
            data = new String(Base64.encode(data.getBytes()));
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            int blockSize = cipher.getBlockSize();
            byte[] dataBytes = data.getBytes();
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }
            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
            SecretKeySpec keyspec = new SecretKeySpec(KEY.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);
            return new String(Base64.encode(encrypted));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * aes 解密
     * @param data 密文
     * @return
     */
    public static String decryptData(String data){
        try {
            byte[] encrypted1 =Base64.decode(data);
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keyspec = new SecretKeySpec(KEY.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            byte[] original = cipher.doFinal(encrypted1);
//            String originalString = new String(original);
            if (original == null || original.length == 0){
                return null;
            }
            String originalString = new String(Base64.decode(new String(original)));
            return originalString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *   packageName, packageVer,adType,
     */
    public static JSONObject httpPostJson(String path, Map<String,String> params, Activity context)
            throws Exception {

        Map<String,String> allParams = new HashMap<String,String>();
        allParams.putAll(params);
        allParams.put("imsi",TelephoneUtils.getIMSI(context));
        allParams.put("imei",TelephoneUtils.getIMEI(context));
        allParams.put("phoneModel",TelephoneUtils.getSystemModel());
        allParams.put("sysVersion",TelephoneUtils.getSystemVersion());
        allParams.put("packageName",TelephoneUtils.getPackageName(context));
        allParams.put("packageVer",TelephoneUtils.getVersionCode(context));

        JSONObject jsonObject = TelephoneUtils.getGSMCellLocationInfo(context);
        if (jsonObject != null){

            allParams.put("lac",jsonObject.getString("lac"));
            allParams.put("celid",jsonObject.getString("cid"));
        }
        allParams.put("sdkVersion",sdkversion);
        if (TelephoneUtils.isBackground(context)){
            allParams.put("foreground","0");
            System.out.println("00000================foreground:");
        }else{
            allParams.put("foreground","1");
            System.out.println("11111================foreground:");
        }
        allParams.put("uuid",TelephoneUtils.getUniqueUuid(context));
        String postStr = mapTojson(allParams);
        final String postString = "dsf=" + URLEncoder.encode(HttpUtils.encryptData(postStr));
//        System.out.println(postString);
        try {

            URL url = new URL(path);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");// 提交模式
            httpURLConnection.setConnectTimeout(5000);//连接超时 单位毫秒
            httpURLConnection.setReadTimeout(5000);//读取超时 单位毫秒
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
//            httpURLConnection.setUseCaches(false);
//            httpURLConnection.addRequestProperty("Connection","Keep-Alive");//设置与服务器保持连接
            httpURLConnection.addRequestProperty("Charset","UTF-8");//设置字符编码类型
            //httpURLConnection.addRequestProperty("Content-Type","application/x-www-form-urlencoded");
//            httpURLConnection.setRequestProperty("Content-type","application/x-javascript->json");
//            httpURLConnection.connect();
            OutputStream outputStream = httpURLConnection.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream);
            // 发送请求参数
            printWriter.write(postString);//post的参数 xx=xx&yy=yy
            // flush输出流的缓冲
            printWriter.flush();
            //开始获取数据
            BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();


//            System.out.println("response code:" + httpURLConnection.getResponseCode());
            if (httpURLConnection.getResponseCode() == 200) {// 判断请求码是否200，否则为失败
                InputStream is = httpURLConnection.getInputStream(); // 获取输入流
                byte[] data = readStream(is); // 把输入流转换成字符串组
                String json = new String(data); // 把字符串组转换成字符串
            
                // 数据形式：{"total":2,"success":true,"arrayData":[{"id":1,"name":"张三"},{"id":2,"name":"李斯"}]}
                String string = HttpUtils.decryptData(json);
            

                JSONObject jo = new JSONObject(string.replace("\0",""));
                return jo;
            }


        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }

        return null;
    }

    public static String mapTojson(Map<String,String> params){

        StringBuffer sb = new StringBuffer();
        sb.append("{");

        int i = 0;
        for (String key : params.keySet()) {
            i++;
            //map.keySet()返回的是所有key的值
            String value = params.get(key);//得到每个key多对用value的值
            sb.append("\"");
            sb.append(key);
            sb.append("\"");
            sb.append(":");
            sb.append("\"");
            sb.append(value);
            sb.append("\"");
            if (i != params.keySet().size()){
                sb.append(",");
            }


        }
        sb.append("}");

        return sb.toString();

    }


}
