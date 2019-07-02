package com.jackzheng.ourgame.demonadshowlib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * activity base
 */
public abstract class BaseActivity extends Activity {
    protected Context ctx;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx=this;
        doInit();
    }
    /**
     * 初始化操作
     */
    protected abstract void doInit();
    /**
     * @param classes
     *
     */
    protected  void startActivity(Class<? extends Activity> classes){
        startActivity(new Intent(ctx,classes));

    }

    /**
     * @param msg
     */
    protected void showTip(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
