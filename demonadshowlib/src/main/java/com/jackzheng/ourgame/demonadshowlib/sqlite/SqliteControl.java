package com.jackzheng.ourgame.demonadshowlib.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.ArrayMap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SqliteControl extends SQLiteOpenHelper{

    private String tableName="local891";
    private String sqlName = "local891";

    private static final String TAG = "JackSqlite";

    public SqliteControl(Context context, String name, SQLiteDatabase.CursorFactory factory,
                         int version,String tableName) {
        super(context, name, factory, version);
        this.tableName = tableName;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + tableName + "(" +
                "TYPE          INT ," +
                "ID            INT ," +
                "EXTAN         TEXT ," +
                "GOODID        INT ," +
                "GOODTYPE      INT ," +
                "ISCLENAN      INT ," +
                "ISNET      INT ," +
                "ISDELETE      INT )";
        Log.i(TAG, "create Database------------->");
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "update Database------------->");
    }

    public long getLevel(){
        SQLiteDatabase db =getReadableDatabase();
        Cursor cursor = db.query(tableName, new String[]{"EXTAN"}, "ID=?  AND TYPE=? AND ISDELETE=1",  new String[]{"1","1"}, null, null, null);
        long level = -100;
        while(cursor.moveToNext()){
            level= Long.parseLong( cursor.getString(cursor.getColumnIndex("EXTAN")) );
        }
        db.close();
        return level;
    }

    public long getPlayVocation(){
        SQLiteDatabase db =getReadableDatabase();
        Cursor cursor = db.query(tableName, new String[]{"EXTAN"}, "ID=?  AND TYPE=?  AND ISDELETE=1",  new String[]{"22","1"}, null, null, null);
        long level = -1;
        while(cursor.moveToNext()){
            level= Long.parseLong( cursor.getString(cursor.getColumnIndex("EXTAN")) );
        }
        db.close();
        return level;
    }

    public void deleteGuide(long id)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ISNET", 1);
        cv.put("ISDELETE", 2);
        String whereClause="TYPE=8 and id=? and ISDELETE=1";
        String [] whereArgs = {String.valueOf(id)};
        db.update(tableName, cv, whereClause, whereArgs);
        db.close();
    }


    public void onUodateInfoByTypeAndId(SQLDate date) {

        SQLiteDatabase db =getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("EXTAN", date.extan);
        cv.put("ISNET", 1);
        String whereClause="Type=? and id=? and isdelete=1";
        String [] whereArgs = {String.valueOf(date.type),String.valueOf(date.id)};
        db.update(tableName, cv, whereClause, whereArgs);
        db.close();
    }

    public void alterTableForIsNetAndIsDelete(){
        try {
            SQLiteDatabase db = getWritableDatabase();
            String common =  "alter table " + tableName + " add ISDELETE int default 1";
            db.execSQL(common);

            common =  "alter table " + tableName + " add ISDELETE int default 1";
            db.execSQL(common);

            db.close();

        }catch (Exception e ){
            e.printStackTrace();
        }
    }

    public boolean isUpdate(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(tableName, new String[]{"EXTAN"}, "ID=? AND TYPE=?",  new String[]{"18","1"}, null, null, null);
        long count =0;
        while(cursor.moveToNext()){
            count++;
        }
        if (count > 0){
            return true;
        }else{
            return false;
        }
    }

    public void clearAllDelete() {
        SQLiteDatabase db= getWritableDatabase();

        String whereClauses = "ISDELETE=?";
        String [] whereArgs = {String.valueOf(2)};
        db.delete(tableName, whereClauses, whereArgs);
        db.close();
    }


    public void inSertDate(SQLiteDatabase db ,SQLDate date){
        boolean isClocse = false;
        if(db == null){
            isClocse = true;
            db = getWritableDatabase();
        }
        ContentValues cv = new ContentValues();
        cv.put("TYPE", date.type);
        cv.put("ID", date.id);
        cv.put("EXTAN", date.extan);
        cv.put("GOODID", date.goodId);
        cv.put("GOODTYPE", date.goodType);
        cv.put("ISCLENAN", date.isClean);
        cv.put("ISNET", date.isNet);
        cv.put("ISDELETE", date.isDelete);
        db.insert(tableName, null, cv);
        if(isClocse){
            db.close();
        }
    }

    public void changeGoodType(SQLDate date)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ISNET", 1);
        cv.put("GOODTYPE", date.goodType);
        String whereClause="GOODID=? and ISDELETE=1";
        String [] whereArgs = {String.valueOf(date.goodId)};
        db.update(tableName, cv, whereClause, whereArgs);
        db.close();
    }

    public void changeGoodSql(SQLDate date,long old)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ISNET", 1);
        cv.put("GOODID", date.goodId);
        String whereClause="GOODID=? and TYPE=? and id=? and ISDELETE=1";
        String [] whereArgs = {String.valueOf(old),String.valueOf(date.type),String.valueOf(date.id)};
        db.update(tableName, cv, whereClause, whereArgs);
        db.close();
    }

    public void updateIdAndType(SQLDate date)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ISNET", 1);
        cv.put("EXTAN", date.extan);
        String whereClause="TYPE=? and id=? and ISDELETE=1";
        String [] whereArgs = {String.valueOf(date.type),String.valueOf(date.id)};
        db.update(tableName, cv, whereClause, whereArgs);
        db.close();
    }
    public void deleteIdAndType(SQLDate date)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ISNET", 1);
        cv.put("ISDELETE", 2);
        String whereClause="TYPE=? and id=? and ISDELETE=1";
        String [] whereArgs = {String.valueOf(date.type),String.valueOf(date.id)};
        db.update(tableName, cv, whereClause, whereArgs);
        db.close();
    }

    public void deleteGood(SQLDate date)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ISNET", 1);
        cv.put("ISDELETE", 2);
        String whereClause="GOODID=? and ISDELETE=1";
        String [] whereArgs = {String.valueOf(date.goodId)};
        db.update(tableName, cv, whereClause, whereArgs);
        db.close();

    }
    public void deleteLuiHui()
    {
        Log.i(TAG, "deleteLuiHui------------->");
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ISNET", 1);
        cv.put("ISDELETE", 2);
        String whereClause="ISCLENAN=1 and ISDELETE=1";
        db.update(tableName, cv, whereClause, null);
        db.close();
        Log.i(TAG, "deleteLuiHui end------------->");
    }

    public void UpdateZhuangbeiInto(SQLDate date)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("EXTAN",date.extan);
        cv.put("ISNET", 1);
        String whereClause="GOODID=? and ISDELETE=1";
        String [] whereArgs = {String.valueOf(date.goodId)};
        db.update(tableName, cv, whereClause, whereArgs);
        db.close();
    }

    public void updateEndNet(List<SQLDate> list){
        SQLiteDatabase db =getWritableDatabase();
        for(SQLDate index  : list){
            ContentValues cv = new ContentValues();
            cv.put("ISNET", 2);
            String whereClause="TYPE=? and ID=? and EXTAN=? and GOODID=? and GOODTYPE=? and ISCLENAN=? and ISNET=? and ISDELETE=?";
            String [] whereArgs = {String.valueOf(index.type),String.valueOf(index.id),index.extan,String.valueOf(index.goodId)
                    ,String.valueOf(index.goodType),String.valueOf(index.isClean),String.valueOf(index.isNet),String.valueOf(index.isDelete),};
            db.update(tableName, cv, whereClause, whereArgs);
        }
        db.close();
    }

    public void deleteCleanNet()
    {
        SQLiteDatabase db= getWritableDatabase();

        String whereClauses = "TYPE=11";
        db.delete(tableName, whereClauses, null);
        db.close();
    }

    public void removeDeleteDate(){
        SQLiteDatabase db= getWritableDatabase();

        String whereClauses = "ISNET=2 AND ISDELETE=2";
        db.delete(tableName, whereClauses, null);
        db.close();
    }

    public List<SQLDate> getNetDate(){
        ArrayList<com.jackzheng.ourgame.demonadshowlib.sqlite.SQLDate> list = new ArrayList<>();
        SQLiteDatabase db =getReadableDatabase();
        Cursor cursor = db.query(tableName, new String[]{"TYPE","ID","EXTAN","GOODID","GOODTYPE","ISCLENAN","ISNET","ISDELETE",}, "ISNET=1 AND ISDELETE=1", null, null, null, null);

        while(cursor.moveToNext()){
            list.add(cursorToSqlDate(cursor));
        }
        db.close();
        return  list;

    }

    private SQLDate cursorToSqlDate(Cursor cursor){
        SQLDate index = new SQLDate();
        index.type= cursor.getLong(cursor.getColumnIndex("TYPE"));
        index.id= cursor.getLong(cursor.getColumnIndex("ID"));
        index.extan= cursor.getString(cursor.getColumnIndex("EXTAN"));
        index.goodId= cursor.getLong(cursor.getColumnIndex("GOODID"));
        index.goodType= cursor.getLong(cursor.getColumnIndex("GOODTYPE"));
        index.isClean= cursor.getLong(cursor.getColumnIndex("ISCLENAN"));
        index.isNet= cursor.getLong(cursor.getColumnIndex("ISNET"));
        index.isDelete= cursor.getLong(cursor.getColumnIndex("ISDELETE"));
        return index;

    }

    public List<SQLDate> getAll(){
        ArrayList<SQLDate> list = new ArrayList<>();
        SQLiteDatabase db =getReadableDatabase();
        Cursor cursor = db.query(tableName, new String[]{"TYPE","ID","EXTAN","GOODID","GOODTYPE","ISCLENAN","ISNET","ISDELETE",}, null, null, null, null, null);

        while(cursor.moveToNext()){
            list.add(cursorToSqlDate(cursor));
        }
        db.close();
        return  list;
    }

    public void delectAll(String tableName){
        SQLiteDatabase db =getWritableDatabase();
        db.delete(tableName, null, null);
        db.close();
    }

    public void saveLocal(List<SQLDate> list){
        delectAll(tableName);
        SQLiteDatabase db =getWritableDatabase();
        for(SQLDate index  : list){
            if(index.type == 10){
                continue;
            }
            inSertDate(db,index);
        }
        db.close();
    }
}
