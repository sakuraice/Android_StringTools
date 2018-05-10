package com.example.admin.myapplication;

/**
 * Created by admin on 2017/11/27.  Upload用的JAVA Bean，使用成功！
 */

public class Upload {
    private String email;
    private String date;
    private String downloadURL;
    private String file;

    public Upload(){

    }

    public Upload(String email, String date, String downloadURL, String file) {
        this.email = email;
        this.date = date;
        this.downloadURL = downloadURL;
        this.file = file;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}