package com.example.admin.myapplication;

/**
 * Created by admin on 2018/1/11.
 */

public class SQLHelper {

    private SQLHelper(){

    }

    public static Linksql getdata(String tablename){
        SqlserverConnectionData sql = new SqlserverConnectionData();
        sql.setTablename(tablename);
        return sql;
    }
    public Object getResult(String account,String password, Linksql linksql){
        return linksql.result(account,password);
    }
}
