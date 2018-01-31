package com.example.admin.myapplication;

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

public class SqlserverConnectionData implements Linksql {
    private String tablename;
    private String firstcolmn;

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getFirstcolmn() {
        return firstcolmn;
    }

    public void setFirstcolmn(String firstcolmn) {
        this.firstcolmn = firstcolmn;
    }

    @Override
    public Connection linkSql(String account, String password) {
        return new SqlserverConnection().linkSql(account, password);
    }

    @Override
    public Object result(String account, String password) {

        SqlserverConnectionColumn sqlcolumn = new SqlserverConnectionColumn();
        sqlcolumn.setTablename(tablename);
        List<String> column = (List) sqlcolumn.result(account, password);
        firstcolmn = sqlcolumn.getGetcolumn_name1();

        List<String> list = new ArrayList<String>();
        try{
            Connection connection = linkSql(account, password);
            String sql = "select top 1 * from "+"\""+tablename+"\""+" order by "+firstcolmn+" desc";//如果tablename存在空白，查詢指令會不正確，所以用""括起來
            Statement stmt = connection.createStatement();//執行
            ResultSet rs = stmt.executeQuery(sql);//執行
            while (rs.next()){
                for (int i= 0;i<column.size();i++){//因為這裡搜尋數據的方法是自己想的，所以分成兩層執行
                    list.add(rs.getString(column.get(i)));
                }
            }
            rs.close();//釋放
            stmt.close();//釋放
            connection.close();//釋放
        } catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }
}
