package jackzheng.study.com.wechat.regular;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class RegularUtilsTest {

    private static RegularUtilsTest mIntance ;
    private Context mContext;
    public static RegularUtilsTest getIntance(Context contex){
        if(mIntance == null){
            mIntance =  new RegularUtilsTest(contex);
        }
        return mIntance;
    }

    public void test1() throws Exception {
        File file = new File("/sdcard/test.txt");
        FileReader m=new FileReader(file);
        BufferedReader reader=new BufferedReader(m);

        while(true) {
            String nextline=reader.readLine();
            if(nextline==null) break;
            nextline = nextline.replace("\\n","\n");
            Log.d("zsbin"," 下注原话 \n"+nextline);
            ArrayList<DateBean2> dateBean2s = RegularUtils2.regularStr(nextline);
            if(dateBean2s == null){
                Log.d("zsbin"," 解析出来的数据 不识别，转交管理员处理");
            }else{
                for(DateBean2 date :dateBean2s){
                    Log.d("zsbin"," 解析出来的数据 "+date.toString());
                }
            }

            Log.d("zsbin","---------------------------------------------------------\n\n");
        }
        reader.close();

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
 //           "前后去重全头阿斯顿撒123全尾千百排重而我却二234全尾十个无从杀奥术大师多3456千百无从奥术大师"
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
        "3一5\n" +
                "3一5\n" +
                "3一5\n" +
                "3一5\n" +
                "3一5\n" +
                "5一3后25\n"+
            "3一5\n" +
                "3一5\n" +
                "3一5\n" +
                "3一5\n" +
                "3一5\n" +
                "3一5\n"+
            "3一5\n" +
                    "5一3后25\n",
            "3一5\n" +
                    "3一5\n" +
                    "3一5\n" +
                    "3一5\n" +
                    "3一5\n" +
                    "5一3后25\n"+
                    "3一5\n" +
                    "3一5\n" +
                    "3一5\n" +
                    "3一5\n" +
                    "3一5\n" +
                    "3一5\n"+
            "3一5\n" +
                    "5一3后25\n"+
                    "3一5\n"+
                    "3一5\n"+
                    "3一5\n",
        "3一5\n" +
                "5一3后25",
        "4-1/10。 4-4/10全后",
        "0517234+0516974各1，217+167各3后",
        "2345-234各2，123-34569不4",
        "0517234+0516974各1，217+167各3后",
        "后 12345689排3。 前后 13 31 各5。",
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
            "01 02 12 11 47 17 10 10 15 17 00 23 24 25 65各30",
            "前后33 33 13 31 77 各5，",
            "前01 04 31 34 41 44 14 11各25",
            "47 74 27 72 23各30",
            "前01 04 31 34 41 44 14 11各25",
            "后 12345689排3。前后 14 41 各10。 前后 13 31 各5。",
            "47 74 27 72 23各30",
            "4-1/10。 4-4/10全后\n" +
                    "0517234+0516974各1，217+167各3后\n" +
                    "2345-234各2，123-34569不4\n" +
                    "0517234+0516974各1，217+167各3后",
            "23各90",
            "23/90",
            "10 23各10",
            "10 23千百各10",
            "89排28千百",
    };
    private RegularUtilsTest(Context contex){
        IntentFilter filter_dynamic = new IntentFilter();
        //filter_dynamic.addAction("com.jackzheng.regularTest");
      //  contex.registerReceiver(dynamicReceiver, filter_dynamic);
        Log.d("zsbin","match strs");
//        for(String str : strs){
//            ServerManager.getIntance().receiveMessage("abc",str);
//        }
//        Log.d("zsbin","match str2");
//        for(String str : strs2){
//            ServerManager.getIntance().receiveMessage("abc",str);
//        }
//        Log.d("zsbin","match str3");
//        for(String str : str3){
//            ServerManager.getIntance().receiveMessage("abc",str);
//        }
//        Log.d("zsbin","match str4");
//        for(String str : str4){
//            ServerManager.getIntance().receiveMessage("abc",str);
//        }
//        Log.d("zsbin","match str5");
//        for(String str : str5){
//            ServerManager.getIntance().receiveMessage("abc",str);
//        }
//        Log.d("zsbin","match str6");
//        for(String str : str6){
//            ServerManager.getIntance().receiveMessage("abc",str);;
//        }
        mContext = contex;
     //   IntentFilter filter = new IntentFilter("com.jackzheng.regularTest");
    }

}
