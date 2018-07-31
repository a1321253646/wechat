package cn.grandfan.fanda.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class RegularUtilsTest {

    private static RegularUtilsTest mIntance ;
    private Context mContext;
    public static RegularUtilsTest getIntance(Context contex){
        if(mIntance == null){
            mIntance =  new RegularUtilsTest(contex);
        }
        return mIntance;
    }


    String[] strs ={
            "前135679不6",
            "后235689不6",
            "前23456790无60",
            "前01479不5",
            "前23456789不10",
            "456789后去5",
            "前134670不6",
            "2345678前排2",
            "23456789不从10",
            "1234567后去重4",
            "1457不3",
            "前134670不6",
            "23567890无80",
            "234560各2",
            "2467890不20"};
    String[] strs2 = {
            "前1234678-1235678各4",
            "后0135678-1234578各3",
            "34567-45678-5前排",
            "34567-45678-5后排",
            "0124567一1345678前5",
            "34567-45678-5前后排",
            "前1234678～1345678各8",
            "0124567一1345678前5",
            "前1234678～1345678各8",
            "0124567一1345678前5",
            "34567-1234567不2",
            "4-357前10",
            "后27一0258147＝5",
            "后27一0258147＝5",
            "245678-234567不从5",
            "36974-09637前1",
            "后27一0258147＝5",
            "后57-37不20"
    };
    String[] str3 = {
            "后全头24578尾3",
            "后全头13458尾5",
            "后全头458尾3",
            "后全头24578尾10",
            "后全头8尾5",
            "后全头24578尾10",
            "后全头8尾5",
            "后全头14578尾2",
            "56789-全各15"
    };
    String[] str4 = {
            "前9-9/10",
            "24-7前30",
            "34567-45678-5前后排",
            "27-16各2后",
            "后6-3/10",
            "47前22",
            "139/前10",
            "139/前10",
            "后3690/5",
            "前139/5",
            "34位杀278去1",
            "12位杀278去1",
            "杀12全尾4",
            "全头杀12各4",
            "杀12-3456各4",
            "12321-杀12各4",
            "前后去重全头阿斯顿撒123全尾千百排重而我却二234全尾十个无从杀奥术大师多3456千百无从奥术大师"
    };
    String[] str5={
      "5一02468后10",
       "369一369前后各5",
       "全一02468后2",
        "13579一02468后2",
        "1326479排一前",
        "1345678前后排各2",
        "后全头13579尾各1",
        "2345678/2346789排10",
        "0123479/1346789各40",
        "12869后排1",
        "15869---2前2",
        "12348前排1",
        "12345689排5前",
        "89排28前",
        "57排29前后",
        "8-0/158前",
        "01245679排20后",

    };

    String[] str6 = {
//        "3一5\n" +
//                "5一3后25",
//        "3一5\n" +
//                "5一3前15",
//        "3一5\n" +
//                "5一3后25",
//        "4-1/10。 4-4/10全后",
        "0517234+0516974各1，217+167各3后",
        "2345-234各2，123-34569不4",
        "0517234+0516974各1，217+167各3后",
//        "后 12345689排3。 前后 13 31 各5。",
        "小小前后2，小小前后排2",
        "12468一03579一2\n" +
                "03579一12468一2",
            "小小前后2",
            "大大前后排2",
            "小大前后排2",
            "大小前后排2",
            "单单前后排2",
            "双双前后排2",
            "双单前后排2",
            "单双前后排2",
            "单大前后排2",
            "大单前后排2",
            "双1234前后排2",
            "1234双前后排2",
            "单全前后排2",
    };
    private RegularUtilsTest(Context contex){
        IntentFilter filter_dynamic = new IntentFilter();
        //filter_dynamic.addAction("com.jackzheng.regularTest");
      //  contex.registerReceiver(dynamicReceiver, filter_dynamic);
        Log.d("zsbin","match strs");
//        for(String str : strs){
//            RegularUtils.regularString(str);
//        }
//        Log.d("zsbin","match str2");
//        for(String str : strs2){
//            RegularUtils.regularString(str);
//        }
//        Log.d("zsbin","match str3");
//        for(String str : str3){
//            RegularUtils.regularString(str);
//        }
//        Log.d("zsbin","match str4");
//        for(String str : str4){
//            RegularUtils.regularString(str);
//        }
//        for(String str : str5){
//            RegularUtils.regularString(str);
//        }
        for(String str : str6){
            RegularUtils.regularString(str);
        }
        mContext = contex;
     //   IntentFilter filter = new IntentFilter("com.jackzheng.regularTest");
    }

    private BroadcastReceiver dynamicReceiver = new BroadcastReceiver()
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.d("zsbin","receive action is :"+intent.getAction());
            if (intent.getAction().equals("com.jackzheng.regularTest"))
            {
                String msg = intent.getStringExtra("str");
                int action = intent.getIntExtra("act",-1);
                if(action != -1){
                    if(action == 1){
                        RegularUtils.hasNumber(msg);
                    }else if(action == 2){
                        RegularUtils.getNumberFromStr(msg);
                    }else if(action == 3){
                        RegularUtils.deleteBlank(msg);
                    }else if(action == 4){
                        //splieString(msg);
                    }
                }
            }
        }
    };

}
