package jackzheng.study.com.wechat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import jackzheng.study.com.wechat.regular.RegularUtilsTest;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  RegularUtilsTest.getIntance(this);
        HtmlParse.parse();
    }
}
