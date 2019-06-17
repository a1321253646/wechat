package com.jackzheng.ourgame.demonadshowlib;

import android.os.Bundle;
import android.util.Log;

import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.jackzheng.ourgame.demonadshowlib.googlePay.BillingControl;
import com.jackzheng.ourgame.demonadshowlib.sqlite.SQLDate;
import com.jackzheng.ourgame.demonadshowlib.sqlite.SqliteControl;
import com.unity3d.player.UnityPlayerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends UnityPlayerActivity {


    SqliteControl mSqliteControl ;
    BillingControl mBillingControl;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mBillingControl = new BillingControl(this, new BillingControl.BillingUpdatesListener() {
            @Override
            public void onBillingClientSetupFinished() {

            }

            @Override
            public void onConsumeFinished(String token, int result) {

            }

            @Override
            public void onPurchasesUpdated(List<Purchase> purchases) {

            }

            @Override
            public void onQueryPurchases(List<SkuDetails> skus) {
                
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    public void createTable(String sqlName,String tableName){
        if(mSqliteControl == null){
            Log.d("mysql","createTable sqlName="+sqlName+" tableName="+tableName);
            mSqliteControl  = new SqliteControl(MainActivity.this,sqlName,null,1,tableName);
            Log.d("mysql","createTable end");
        }
    }


    public long getLevel(){
        Log.d("mysql","getLevel");
        long level =  mSqliteControl.getLevel();
        Log.d("mysql","getLevel end="+level+" end");
        return level;

    }
    public long getPlayVocation(){
        Log.d("mysql","getPlayVocation");
        long vocation =   mSqliteControl.getPlayVocation();
        Log.d("mysql","getPlayVocation end="+vocation+" end");
        return vocation;
    }

    public void deleteGuide(String str){
        Log.d("mysql","deleteGuide str = "+str);
        long id = Long.parseLong(str);
         mSqliteControl.deleteGuide(id);
        Log.d("mysql","deleteGuide end");
    }

    public void onUodateInfoByTypeAndId(String str) {
        Log.d("mysql","onUodateInfoByTypeAndId str = "+str);
        SQLDate date = stringToSqlDate(str);
        mSqliteControl.onUodateInfoByTypeAndId(date);
        Log.d("mysql","onUodateInfoByTypeAndId end");
    }

    public void alterTableForIsNetAndIsDelete(){
        Log.d("mysql","alterTableForIsNetAndIsDelete ");
        mSqliteControl.alterTableForIsNetAndIsDelete();
        Log.d("mysql","alterTableForIsNetAndIsDelete end");
    }

    public boolean isUpdate(){
        Log.d("mysql","isUpdate ");
        boolean value= mSqliteControl.isUpdate();
        Log.d("mysql","isUpdate end ="+value);
        return value;
    }

    public void clearAllDelete() {
        Log.d("mysql","clearAllDelete ");
        mSqliteControl.clearAllDelete();
        Log.d("mysql","clearAllDelete end");
    }

    public void inSertDate( String str){
        Log.d("mysql","inSertDate str="+str);
        SQLDate date = stringToSqlDate(str);
        mSqliteControl.inSertDate(null,date);
        Log.d("mysql","inSertDate end");
    }

    public void changeGoodType(String str)
    {
        Log.d("mysql","changeGoodType str="+str);
        SQLDate date = stringToSqlDate(str);
        mSqliteControl.changeGoodType(date);
        Log.d("mysql","changeGoodType end");
    }

    public void changeGoodSql(String str,String oldstr)
    {
        Log.d("mysql","changeGoodSql str="+str);
        long old = Long.parseLong(oldstr);
        SQLDate date = stringToSqlDate(str);
        mSqliteControl.changeGoodSql(date,old);
        Log.d("mysql","changeGoodSql end");
    }

    public void updateIdAndType(String str)
    {
        Log.d("mysql","updateIdAndType str="+str);
        SQLDate date = stringToSqlDate(str);
        mSqliteControl.updateIdAndType(date);
        Log.d("mysql","updateIdAndType end");
    }

    public void deleteIdAndType(String str)
    {
        Log.d("mysql","deleteIdAndType str="+str);
        SQLDate date = stringToSqlDate(str);
        mSqliteControl.deleteIdAndType(date);
        Log.d("mysql","deleteIdAndType end");
    }

    public void deleteGood(String str)
    {
        Log.d("mysql","deleteGood str="+str);
        SQLDate date = stringToSqlDate(str);
        mSqliteControl.deleteGood(date);
        Log.d("mysql","deleteGood end");

    }

    public void deleteLuiHui()
    {
        Log.d("mysql","deleteLuiHui ");
        mSqliteControl.deleteLuiHui();
        Log.d("mysql","deleteLuiHui end");
    }

    public void UpdateZhuangbeiInto(String str)
    {
        Log.d("mysql","UpdateZhuangbeiInto str="+str);
        SQLDate date = stringToSqlDate(str);
        mSqliteControl.UpdateZhuangbeiInto(date);
        Log.d("mysql","UpdateZhuangbeiInto end");
    }

    public void updateEndNet(String str){
        Log.d("mysql","updateEndNet str="+str);
        List<SQLDate> list = stringToSqlDateList(str);
        mSqliteControl.updateEndNet(list);
        Log.d("mysql","updateEndNet end");
    }

    public void deleteCleanNet()
    {
        Log.d("mysql","deleteCleanNet ");
        mSqliteControl.deleteCleanNet();
        Log.d("mysql","deleteCleanNet end");
    }

    public void removeDeleteDate(){
        Log.d("mysql","removeDeleteDate ");
        mSqliteControl.removeDeleteDate();
        Log.d("mysql","removeDeleteDate end");
    }

    public String getNetDate(){
        Log.d("mysql","getNetDate ");
        List<SQLDate> list =  mSqliteControl.getNetDate();
        String value =  sqlDateListToString(list);
        Log.d("mysql","getNetDate end ="+value);
        return value;
    }

    public String getAll(){
        Log.d("mysql","getAll ");
        List<SQLDate> list =  mSqliteControl.getAll();
        String value = sqlDateListToString(list);
        Log.d("mysql","getAll end ="+value);
        return value;
    }

    public void delectAll(String tableName){
        Log.d("mysql","delectAll  tableName="+tableName);
        mSqliteControl.delectAll(tableName);
        Log.d("mysql","delectAll end");
    }

    public void saveLocal(String str){
        Log.d("mysql","saveLocal  str="+str);
        List<SQLDate> list = stringToSqlDateList(str);
        mSqliteControl.saveLocal(list);
        Log.d("mysql","saveLocal end");
    }

    private SQLDate stringToSqlDate(String str){
        try {
            JSONObject jb =new JSONObject(str);
            SQLDate date = new SQLDate();
            date.type = jb.getLong("type");
            date.id = jb.getLong("id");
            date.goodId = jb.getLong("goodId");
            date.goodType = jb.getLong("goodType");
            date.isClean = jb.getLong("isClean");
            date.extan = jb.getString("extan");
            date.isDelete = jb.getLong("isDelete");
            date.isNet = jb.getLong("isNet");
            return  date;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
    private List<SQLDate> stringToSqlDateList(String str){
        try {
            ArrayList<SQLDate> list = new ArrayList<>();
            JSONArray array = new JSONArray(str);
            for(int i = 0 ; i< array.length() ; i++){
                JSONObject jb = array.getJSONObject(i);
                SQLDate date = new SQLDate();
                date.type = jb.getLong("type");
                date.id = jb.getLong("id");
                date.goodId = jb.getLong("goodId");
                date.goodType = jb.getLong("goodType");
                date.isClean = jb.getLong("isClean");
                date.extan = jb.getString("extan");
                date.isDelete = jb.getLong("isDelete");
                date.isNet = jb.getLong("isNet");
                list.add(date);
            }
            return list;

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }
    private String sqlDateListToString(List<SQLDate> list){
        JSONArray array = new JSONArray();
        try {
            for(SQLDate date : list){
                JSONObject jb =new JSONObject();
                jb.put("type", date.type);
                jb.put("id", date.id);
                jb.put("goodId", date.goodId);
                jb.put("goodType", date.goodType);
                jb.put("isClean", date.isClean);
                jb.put("extan", date.extan);
                jb.put("isDelete", date.isDelete);
                jb.put("isNet", date.isNet);
                array.put(jb);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array.toString();
    }

    public String showTaptap(String str){
        return "";
    }

}
