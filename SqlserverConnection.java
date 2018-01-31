package com.example.admin.myapplication;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by admin on 2018/1/11.
 */

public class SqlserverConnection implements Linksql {

    final String connectaddress = "jdbc:jtds:sqlserver:/192.168.0.132:1433/fish";

    @Override
    public Connection linkSql(String account, String password) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connection = DriverManager.getConnection(connectaddress,account,password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    @Override
    public Object result(String account, String password) {
        String result = "";
        try {
            Connection connection = linkSql(account, password);
            String sql = "select top 1 * from fishtest order by cno desc";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                result = rs.getString("cname");
            }
            rs.close();
            stmt.close();
            connection.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
