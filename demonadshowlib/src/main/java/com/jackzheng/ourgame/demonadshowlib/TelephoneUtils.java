package com.jackzheng.ourgame.demonadshowlib;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

public class TelephoneUtils
{
    public static final int TYPE_NONE = 0;
    public static final int TYPE_CM = 1;
    public static final int TYPE_CU = 2;
    public static final int TYPE_CT = 3;
    public static String UNIQUE_UUID_FILE = "UNIQUE_UUID_FILE";
    public static String UNIQUE_UUID = "UNIQUE_UUID";

    public static boolean isSimExist(Context context)
    {
        TelephonyManager telephonyManager = (TelephonyManager)context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.hasIccCard();
    }

    public static int getProvidersType(Activity context) {
        try {
            String imsi = getIMSI(context);
            if ((TextUtils.isEmpty(imsi)) || (!imsi.startsWith("460"))) {
                return 1;
            }
            String op = imsi.substring(0, 5);
            if (TextUtils.isEmpty(op)) {
                return 1;
            }

            switch (Integer.parseInt(op)) {
                case 46000:
                case 46002:
                case 46007:
                    return 1;
                case 46001:
                case 46006:
                case 46020:
                    return 2;
                case 46003:
                case 46005:
                case 46011:
                case 46099:
                    return 3;
            }
            return 1;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    private static String randomImsi(Context context)
    {
        try {
            String imei = getIMEI(context);
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < imei.length(); i++) {
                char c = imei.charAt(i);
                if (!Character.isDigit(c)) {
                    buffer.append("0");
                } else {
                    buffer.append(c);
                }
            }
            return buffer.toString();
        }
        catch (Exception localException) {}
        return "2561158629";
    }

    public static String getIMSI(Context context) {
        String result = "";
        try {
            TelephonyManager telphonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

           int checkSelfPermission = 0;

            if (Build.VERSION.SDK_INT >= 23) {
                checkSelfPermission  = context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE);

            }
         if (checkSelfPermission  == PackageManager.PERMISSION_GRANTED){
            result = telphonyManager.getSubscriberId();
         }else{
                result = "";
            }

        } catch (Exception e) {
            result = "";
        }

        try
        {
            if ((TextUtils.isEmpty(result)) || (!result.startsWith("460")))
            {



                result = getNetworkOperator(context);
                if ((TextUtils.isEmpty(result)) || (!result.startsWith("460"))) {
                    result = "46000";
                }
                result = result + randomImsi(context);
                if (result.length() < 15) {
                    result = result + "000000000000000";
                }
                if (result.length() > 15) {
                    result = result.substring(0, 15);
                }
            }
        } catch (Exception e) {
            result = "";
        }
        return result != null ? result : "";
    }

    public static String getUUID32(){
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        return uuid;
    }

    public static String getIMEI(Context context) {
        String result = "";
        try {

                int checkSelfPermission = 0;

                if (Build.VERSION.SDK_INT >= 23) {
                    checkSelfPermission  = context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE);

                }
                if (checkSelfPermission  == PackageManager.PERMISSION_GRANTED){
                    TelephonyManager telphonyManager = (TelephonyManager)context
                            .getSystemService(Context.TELEPHONY_SERVICE);
                    result = telphonyManager.getDeviceId();
                }else {

                    if (result == null){
                        result = getUniqueUuid(context);
                    }


                }

        } catch (Exception e) {
            result = getUUID32();
        }
        return result != null ? result : "12345678";
    }

    public static String getAndroidId(Context context) {
        try {
            return "";//Settings.Secure.getString(context.getContentResolver(), "android_id");
        }
        catch (Exception localException) {}
        return "";
    }

    public static String getNetworkOperator(Context context) {
        String result = "";
        try {
            TelephonyManager telphonyManager = (TelephonyManager)context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            result = telphonyManager.getNetworkOperator();
        } catch (Exception e) {
            result = "";
        }
        return result != null ? result : "";
    }

    public static int getChannelID(Activity context, String name) {
        int value = 0;
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            value = appInfo.metaData.getInt(name);
        }
        catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return value != 0 ? value : 0;
    }

    public static String getMetaData(Activity context, String name) {
        String value = "";
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            value = String.valueOf(appInfo.metaData.getInt(name) & 0xFFFFFFFF);
        }
        catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return value != null ? value : "";
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null) && (networkInfo.isConnected());
    }



    /**
     * 获取手机基站信息
     * @throws JSONException
     */
    public static JSONObject getGSMCellLocationInfo(Context context) throws JSONException {

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String operator = manager.getNetworkOperator();
        /**通过operator获取 MCC 和MNC */
        int mcc =0;
        int mnc = 0;
        if (operator != null && operator.length() > 3){
            /**通过operator获取 MCC 和MNC */
            mcc = Integer.parseInt(operator.substring(0, 3));
            mnc = Integer.parseInt(operator.substring(3));
        }
        int checkSelfPermission = 0;

        if (Build.VERSION.SDK_INT >= 23) {
            checkSelfPermission  = context.checkCallingPermission(Manifest.permission.ACCESS_FINE_LOCATION);

        }
          if (checkSelfPermission  == PackageManager.PERMISSION_GRANTED){
            CellLocation cellLocation = manager.getCellLocation();

            if (cellLocation == null){
                return null;
            }

            /**通过GsmCellLocation获取中国移动和联通 LAC 和cellID */
            int lac = -1;
            int cellid = -1;
            if (cellLocation instanceof GsmCellLocation) {

                GsmCellLocation location = (GsmCellLocation) cellLocation;

                lac = location.getLac();
                cellid = location.getCid();
            }else if(cellLocation instanceof CdmaCellLocation){
                CdmaCellLocation location1 = (CdmaCellLocation) cellLocation;
                lac = location1.getNetworkId();
                cellid = location1.getBaseStationId();
                cellid /= 16;

            }


            /**通过CdmaCellLocation获取中国电信 LAC 和cellID */
                 /*CdmaCellLocation location1 = (CdmaCellLocation) mTelephonyManager.getCellLocation();
                 lac = location1.getNetworkId();
                 cellId = location1.getBaseStationId();
                 cellId /= 16;*/

            int strength = 0;
            /**通过getNeighboringCellInfo获取BSSS */
            List<NeighboringCellInfo> infoLists = null;
            try {
                infoLists = manager.getNeighboringCellInfo();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("infoLists:"+infoLists+"     size:"+infoLists.size());
            for (NeighboringCellInfo info : infoLists) {
                strength+=(-133+2*info.getRssi());// 获取邻区基站信号强度
                //info.getLac();// 取出当前邻区的LAC
                //info.getCid();// 取出当前邻区的CID
                System.out.println("rssi:"+info.getRssi()+"   strength:"+strength);
            }


            //以下内容是把得到的信息组合成json体，然后发送给我的服务器，获取经纬度信息
            //如果你没有服务器支持，可以发送给BaiduMap，GoogleMap等地图服务商，具体看定位相关的API格式要求
            JSONObject item = new JSONObject();
            item.put("cid", cellid);
            item.put("lac", lac);
            item.put("mnc", mnc);
            item.put("mcc", mcc);
            item.put("strength", strength);


            return  item;
        }else {
            return  null;
        }

//
//        JSONArray cells = new JSONArray();
//        cells.put(0, item);
//
//        JSONObject json = new JSONObject();
//        json.put("cells", cells);


    }

    public static String getVersionCode(Context context)
    {
        PackageInfo pinfo = null;
        try {
            pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        }
        catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        String versionCode = String.valueOf(pinfo.versionCode);
        return versionCode;
    }


    public static String getPackageName(Context context)
    {
        String pkName = "";
        try {
            pkName = context.getPackageName();


        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return pkName;

    }

    public static String getVersionName(Activity context)
    {
        String versionName = "";
        try {
            String pkName = context.getPackageName();
            versionName = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;



        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return versionName;

    }

    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;

    }
    /**
     * 获取手机厂商
     *
     * @return  手机厂商
     */
    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    /**
     * 获取手机型号
     *
     * @return  手机型号
     */
    public static String getSystemModel() {
        return Build.MODEL;
    }


    public static String getUniqueUuid(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(UNIQUE_UUID_FILE, 0);
        String uuid = sharedPreferences.getString(UNIQUE_UUID, "");
        if(uuid == null || "".equals(uuid)){
            sharedPreferences = context.getSharedPreferences(UNIQUE_UUID_FILE, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(UNIQUE_UUID, getUUID32());
            editor.commit();

        }

        return uuid;
    }


    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();

        if (appProcesses == null){
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i(context.getPackageName(), "后台"
                            + appProcess.processName);
                    return true;
                } else {
                    Log.i(context.getPackageName(), "前台"
                            + appProcess.processName);
                    return false;
                }
            }
        }
        return false;

    }





}
