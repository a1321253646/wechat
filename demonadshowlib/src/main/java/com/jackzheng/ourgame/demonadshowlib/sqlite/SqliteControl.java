package com.jackzheng.ourgame.demonadshowlib.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

public class SqliteControl extends SQLiteOpenHelper{

    private String tableName="local891";
    private String sqlName = "local891";

    private static final String TAG = "JackSqlite";

    public SqliteControl(Context context, String name, SQLiteDatabase.CursorFactory factory,
                         int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table stu_table(id int,sname varchar(20),sage int,ssex varchar(10))";
        Log.i(TAG, "create Database------------->");
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "update Database------------->");
    }


    public void onUodateInfoByTypeAndId(SQLDate date) {

        SQLiteDatabase db =getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("EXTAN", date.extan);
        cv.put("ISNET", 1);
        String whereClause="Type=? and id=? and isdelete=1";
        String [] whereArgs = {String.valueOf(date.type),String.valueOf(date.id)};
        db.update(sqlName, cv, whereClause, whereArgs);
        db.close();
    }

    public void inSertDate(SQLiteDatabase db ,SQLDate date){
        boolean isClocse = false;
        if(db == null){
            isClocse = true;
            db = getReadableDatabase();
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
        db.insert(sqlName, null, cv);
        if(isClocse){
            db.close();
        }
    }

    public void deleteDateBySqldate(SQLiteDatabase db ,SQLDate date){
        boolean isClocse = false;
        if(db == null){
            isClocse = true;
            db = getReadableDatabase();
        }
        String whereClauses = "TYPE=? and ID=? and EXTAN='?' and GOODID=? and GOODTYPE=? and ISCLENAN=? and ISNET=? and ISDELETE=?";
        String [] whereArgs = {String.valueOf(date.type),String.valueOf(date.id),date.extan,String.valueOf(date.goodId)
                ,String.valueOf(date.goodType),String.valueOf(date.isClean),String.valueOf(date.isNet),String.valueOf(date.isDelete),};
        db.delete(sqlName, whereClauses, whereArgs);
        if(isClocse){
            db.close();
        }
    }

    public void deleteDateEndNet(List<SQLDate> list){
        SQLiteDatabase db =getWritableDatabase();
        for(SQLDate index  : list){
            deleteDateBySqldate(db,index);
        }
    }

    public void getAll(){

    }

    public void delectAll(String tableName){
        SQLiteDatabase db =getReadableDatabase();
        db.delete(tableName, null, null);
        db.close();
    }

    public void saveLocal(List<SQLDate> list){
        delectAll(sqlName);
        SQLiteDatabase db =getWritableDatabase();
        for(SQLDate index  : list){
            if(index.type == 10){
                continue;
            }
            inSertDate(db,index);
        }
        db.close();
    }

    public void deleteNetData(){

    }

}
