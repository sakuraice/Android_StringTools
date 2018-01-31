package com.example.admin.myapplication;

import android.content.Context;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018/1/11.
 */

public class SqlserverConnectionColumn implements Linksql {

    private String tablename;
    private String getcolumn_name1;

    public String getGetcolumn_name1() {
        return getcolumn_name1;
    }

    public void setGetcolumn_name1(String getcolumn_name1) {
        this.getcolumn_name1 = getcolumn_name1;
    }

    public String getTablename() {

        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    @Override
    public Connection linkSql(String account, String password) {
        return new SqlserverConnection().linkSql(account, password);
    }

    @Override
    public Object result(String account, String password) {
        List<String> column = new ArrayList<String>();
        try{
            Connection connection = linkSql(account, password);
            String sql = "select column_name from INFORMATION_SCHEMA.COLUMNS where table_name='"+tablename+"'";//搜尋欄位名稱的指令
            Statement stmt = connection.createStatement();//執行
            ResultSet rs = stmt.executeQuery(sql);//執行並取得
            while (rs.next()){
                String a1 = rs.getString("column_name");//取得column_name的資料
                column.add(a1);//將查詢到的欄位名稱存進column陣列
                getcolumn_name1 = column.get(0);//用getcolumn_name 拿出第一個欄位名稱，後面用來作為查詢的字串。
            }
            rs.close();//釋放
            stmt.close();//釋放
            connection.close();//釋放
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return column;
    }

    //未實用的方法
    public Object dataresult(String account,String password){
        List<String> column = (List)result(account,password);
        String firstcolmn = column.get(0);
        List<String>list = new ArrayList<String>();
        try{
            Connection connection = linkSql(account, password);
            String sqldata = "select top 1 * from "+"\""+tablename+"\""+" order by "+firstcolmn+" desc";//搜尋欄位名稱的指令
            Statement stmt = connection.createStatement();//執行
            ResultSet rs = stmt.executeQuery(sqldata);//執行並取得
            while (rs.next()){
                for (int i= 0;i<column.size();i++){//因為這裡搜尋數據的方法是自己想的，所以分成兩層執行
                    list.add(rs.getString(column.get(i)));
                }
            }
            rs.close();//釋放
            stmt.close();//釋放
            connection.close();//釋放
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
