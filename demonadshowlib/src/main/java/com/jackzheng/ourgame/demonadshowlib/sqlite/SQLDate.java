package com.jackzheng.ourgame.demonadshowlib.sqlite;

public class SQLDate {

    public long type = -1;
    public long id = -1;
    public String extan = "-1";
    public long goodId = -1;
    public long goodType = 7;
    public long isClean = 1;// 1为清除，2为不清除
    public long isNet = 1;// 1为清除，2为不清除
    public long isDelete = 1;// 1为清除，2为不清除

    @Override
    public String toString() {
            return "type = " + type + " id =" + id + " extan=" + extan+" sqlid="+ goodId+ " sqltype= "+ goodType;
    }
}
