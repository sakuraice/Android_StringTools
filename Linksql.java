package com.example.admin.myapplication;

import java.sql.Connection;

/**
 * Created by admin on 2018/1/11.
 */

public interface Linksql {
    Connection linkSql(String account,String password);
    Object result(String account,String password);
}
