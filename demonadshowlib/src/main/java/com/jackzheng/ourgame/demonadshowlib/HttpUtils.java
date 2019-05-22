package com.jackzheng.ourgame.demonadshowlib;

import android.app.Activity;
import android.util.Base64;


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
            data = new String(Base64.encodeToString(data.getBytes("UTF-8"),Base64.DEFAULT));
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
            return new String(Base64.encodeToString(encrypted,Base64.DEFAULT));
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
            byte[] encrypted1 =Base64.decode(data,Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keyspec = new SecretKeySpec(KEY.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            byte[] original = cipher.doFinal(encrypted1);
//            String originalString = new String(original);
            if (original == null || original.length == 0){
                return null;
            }
            String originalString = new String(Base64.decode(new String(original),Base64.DEFAULT));
            return originalString;
        } catch (Exception e) {
            e.printStackTrace();
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
