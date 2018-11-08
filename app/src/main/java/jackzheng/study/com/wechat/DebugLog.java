package jackzheng.study.com.wechat;

import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;

public class DebugLog {
    public static void saveLog(String str){
        Log.d("zsbin",str);
      //  getString(str);
    }

    private static FileOutputStream outStream = null;
    public static void getString(String str) {
        if(!str.endsWith("\n")){
            str = str+"\n";
        }
        try {
            if(outStream == null){
                String filePath ;

                boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
                if (hasSDCard) {
                    filePath =Environment.getExternalStorageDirectory().toString() + File.separator +"ssclog.txt";
                } else {
                    filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "ssclog.txt";
                }
                Log.d("zsbin","ssc logpath = "+filePath);
                File file = new File(filePath);
                if (!file.exists()) {
                    File dir = new File(file.getParent());
                    dir.mkdirs();
                    file.createNewFile();
                }
               outStream = new FileOutputStream(file);
            }

            outStream.write(str.getBytes());
            outStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
