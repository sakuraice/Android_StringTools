package com.example.admin.myapplication;

/**
 * Created by admin on 2017/10/31.
 */

public class Message {
    private String nickname;
    private String message;
    private String time;
    private String useremail;
    public Message(){
    }

    public Message(String nickname, String message, String time, String useremail) {
        this.nickname = nickname;
        this.message = message;
        this.time = time;
        this.useremail = useremail;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }
}
