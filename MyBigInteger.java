package com.example.admin.myapplication;

import android.content.Context;
import android.preference.Preference;
import android.widget.Toast;

import com.fasterxml.jackson.databind.AnnotationIntrospector;

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

public class MyBigInteger implements MyMath{

    //private String bigmath1;
    //private String bigmath2;
    private String res="";
    private int llevo= 0;
    private int flag = 0;
    private String zero = "";
    private int negative = 0;


    public MyBigInteger(String bigmath1,String bigmath2){
        //this.bigmath1 = bigmath1;
        //this.bigmath2 = bigmath2;
    }

    public MyBigInteger(){

    }

    //加法 負號判斷完成
    public String plus(String bigmath1,String bigmath2){
        int plusFlag = 0;
        if (bigmath1.charAt(0) == '-' && bigmath2.charAt(0) =='-'){
            bigmath1 = bigmath1.substring(1,bigmath1.length());
            bigmath2 = bigmath2.substring(1,bigmath2.length());
            plusFlag++;
        }else if (bigmath2.charAt(0) == '-' && bigmath1.charAt(0) != '-'){
            bigmath2 = bigmath2.substring(1,bigmath2.length());
            if (bigmath2.equals(bigmath1)) return "0";
            return minus(bigmath1,bigmath2);
        }else if (bigmath1.charAt(0) == '-' && bigmath2.charAt(0) != '-'){
            if (bigmath1.substring(1,bigmath1.length()).equals(bigmath2)) return "0";
            bigmath2 = "-" + bigmath2;
            return minus(bigmath1,bigmath2);
        }
        int a= Math.abs(bigmath1.length() - bigmath2.length());
        if (bigmath1.length()>bigmath2.length()){
            for (int i = 0;i < a;i++){
                bigmath2 = "0" + bigmath2;
            }
        }else{
            for (int i = 0; i<a ;i++){
                bigmath1 = "0" + bigmath1;
            }
        }
        for (int i = bigmath1.length() -1 ;i>=0 ;i--){
            String b = "" + bigmath1.charAt(i);
            String c = "" + bigmath2.charAt(i);
            int aux = Integer.parseInt(b) + Integer.parseInt(c) + llevo;
            if ( aux >9 ){//如果上數相加大於9，就取餘數並+上之前的字串，然後將數字除10得到進位的值
                res = aux%10 + res;
                llevo = aux/10;
            }else{//如果沒有大於9，就直接+上之前的字串，且不用進位，所以進位設0
                res = aux + res;
                llevo = 0;
            }
        }
        if (llevo>0) res = llevo + res;
        if (plusFlag> 0 ) res = "-" + res;
        return res;
    }


    //大數運算 相減，負號判斷完成
    public String minus(String bigmath1,String bigmath2){
        int minusflag = 0;
        if (bigmath1.equals(bigmath2)) return "0";
        if (bigmath1.charAt(0) == '-' && bigmath2.charAt(0) != '-'){
            bigmath1 = bigmath1.substring(1,bigmath1.length());
            return "-" + plus(bigmath1,bigmath2);
        }else if (bigmath1.charAt(0) != '-' && bigmath2.charAt(0) == '-'){
            bigmath2 = bigmath2.substring(1,bigmath2.length());
            return plus(bigmath1,bigmath2);
        }else if (bigmath1.charAt(0) == '-' && bigmath2.charAt(0) == '-'){
            bigmath1 = bigmath1.substring(1,bigmath1.length());
            bigmath2 = bigmath2.substring(1,bigmath2.length());
            if (bigmath1.length() > bigmath2.length()){
                minusflag++;
            }else if (bigmath2.length() > bigmath1.length()){
                String Copbigmath = bigmath1;
                bigmath1 = bigmath2;
                bigmath2 = Copbigmath;
            }
            for (int i = 0; i < bigmath1.length();i++){
                String str1 = "" + bigmath1.charAt(i);
                String str2 = "" + bigmath2.charAt(i);
                if (Integer.parseInt(str1)>Integer.parseInt(str2)){
                    break;
                }else if (Integer.parseInt(str1) == Integer.parseInt(str2)){
                    continue;
                }else {
                    str1 = bigmath1;
                    bigmath1 = bigmath2;
                    bigmath2 = str1;
                    break;
                }
            }
        }
        int flag = 0;
        int a = Math.abs(bigmath1.length() - bigmath2.length());
        String Copybigmath1 = bigmath1;
        if (bigmath1.length() > bigmath2.length()){ //如果第一個數的長度大於第二個數
            for (int i = 0 ; i < a; i++){
                bigmath2 = "0" + bigmath2;
            }
        }else if (bigmath2.length() > bigmath1.length()){//如果第二個數的長度大於第一個數，就先把第一個數補上0
            for (int i = 0 ; i < a; i++){
                bigmath1 = "0" + bigmath1;
            }
            Copybigmath1 = bigmath1;
            bigmath1 = bigmath2;
            bigmath2 = Copybigmath1;
            flag++;
        }else {//如果兩個數的長度一樣，就判斷哪一個數比較大
            for (int i =0; i<bigmath1.length();i++){//用迴圈從最前面的數字開始跑
                String str1 = "" + bigmath1.charAt(i);
                String str2 = "" + bigmath2.charAt(i);
                if (Integer.parseInt(str1) > Integer.parseInt(str2)){//如果第一個數比較大，就直接break，因為順序正確不用修改
                    break;
                }else if (Integer.parseInt(str1) == Integer.parseInt(str2)){//如果兩個數一樣大，那就繼續判斷，判斷到最後一個數
                    continue;
                }else {//如果第二個數大於第一個數，那就代表第二個字串較大，所以跟第一個字串做顛倒來運算，因為顛倒做運算，所以設一個flag做最後提醒要加上負號
                    bigmath1 = bigmath2;
                    bigmath2 = Copybigmath1;
                    flag++;
                    break;
                }
            }
        }
        for (int i = bigmath1.length() -1 ; i >= 0; i--){
            String b = "" + bigmath1.charAt(i);
            String c = "" + bigmath2.charAt(i);
            int aux = (Integer.parseInt(b) - llevo) - Integer.parseInt(c);
            if (aux < 0){
                res = ((Integer.parseInt(b)+10) - llevo) -Integer.parseInt(c) + res;//55
                llevo = 1;
            }else{
                llevo = 0;
                res = aux + res;
            }
        }
        if (res.charAt(0)=='0') res = res.substring(1,res.length());
        if (flag > 0) res = "-" + res;
        if (minusflag > 0) res = "-" + res;
        return res;
    }


    //大數運算 乘法 OK，完全沒問題了
    public String mul(String bigmath1,String bigmath2){
        if (bigmath1.charAt(0) =='-' && bigmath2.charAt(0) == '-'){
            bigmath1 = bigmath1.substring(1,bigmath1.length());
            bigmath2 = bigmath2.substring(1,bigmath2.length());
        }else if (bigmath1.charAt(0) == '-'){
            bigmath1 = bigmath1.substring(1,bigmath1.length());
            negative++;//如果是負值，這個flag就會++
        }else if (bigmath2.charAt(0) == '-'){
            bigmath2 = bigmath2.substring(1,bigmath2.length());
            negative++;
        }

        List<String> value = new ArrayList<String>();
        int a= Math.abs(bigmath1.length()-bigmath2.length());
        for (int i = bigmath1.length() -1 ;i>=0 ;i--){//18 * 19
            for (int j = bigmath2.length() -1 ; j >= 0; j--){
                String b = "" + bigmath1.charAt(i);
                String c = "" + bigmath2.charAt(j);
                int aux = Integer.parseInt(b) * Integer.parseInt(c) + llevo;
                if ( aux >9 ){//如果上數相加大於9，就取餘數並+上之前的字串，然後將數字除10得到進位的值
                    res = aux%10 + res;
                    llevo = aux/10;
                }else{//如果沒有大於9，就直接+上之前的字串，且不用進位，所以進位設0
                    res = aux + res;
                    llevo = 0;
                }
            }
            if (llevo>0){
                res = llevo + res;
                if (flag>0){
                    res = res + zero;
                }
                value.add(res);
            }else{
                if (flag>0){
                    res = res + zero;
                }
                value.add(res);
            }
            llevo = 0;
            flag++;
            zero = zero + 0;
            res = "";
        }
        if (negative>0){
            return "-" + multotal(value,0,bigmath1,bigmath2);
        }else{
            return multotal(value,0,bigmath1,bigmath2);
        }
    }

    //大數運算 除法，待補上
    @Override
    public String divide() {
        return null;
    }

    //大數運算  乘法後的數相加
    private String multotal(List<String> big,int count,String bigmath1,String bigmath2) {
        if (count >= big.size()) {
            return res;
        } else {
            bigmath1 = res;
            res = "";
            bigmath2 = big.get(count);
            int a = Math.abs(bigmath1.length() - bigmath2.length());
            if (bigmath1.length() > bigmath2.length()) {
                for (int i = 0; i < a; i++) {
                    bigmath2 = "0" + bigmath2;
                }
            } else {
                for (int i = 0; i < a; i++) {
                    bigmath1 = "0" + bigmath1;
                }
            }
            for (int i = bigmath1.length() - 1; i >= 0; i--) {
                String b = "" + bigmath1.charAt(i);
                String c = "" + bigmath2.charAt(i);
                int aux = Integer.parseInt(b) + Integer.parseInt(c) + llevo;
                if (aux > 9) {//如果上數相加大於9，就取餘數並+上之前的字串，然後將數字除10得到進位的值
                    res = aux % 10 + res;
                    llevo = aux / 10;
                } else {//如果沒有大於9，就直接+上之前的字串，且不用進位，所以進位設0
                    res = aux + res;
                    llevo = 0;
                }
            }
            if (llevo > 0) {
                res = llevo + res;
            }
            llevo = 0;
            return multotal(big, count + 1,bigmath1,bigmath2);
        }
    }

    //md5加密
    public String Md5(String password) {
        StringBuffer buffer = new StringBuffer();
        if (password.trim().equals(null))//判斷是否為空
            return "";
        else if (!password.matches("[a-zA-Z0-9]*")) {//如果有其他特殊符號或中文
            return "Have another byte";
        } else {//判斷是否為英文數字
            try {
                MessageDigest digest = MessageDigest.getInstance("md5");
                byte[] result = digest.digest(password.getBytes());
                for (byte b : result) {
                    int number = b & 0xff;
                    String str = Integer.toHexString(number);
                    if (str.length() == 1) {
                        buffer.append("0");
                    }
                    buffer.append(str);
                }
            } catch (Exception e) {
                System.out.print(e.toString());
            } finally {
                return buffer.toString();
            }
        }
    }
    //正則表達判斷輸入的是否為數字
    private String checkBig(String bigmath){
        if (bigmath.charAt(0) == '-')
            if (bigmath.substring(1,bigmath.length()).matches("\\d+"))
                return bigmath;
            else
                return "";
        else
            if (bigmath.matches("\\d+"))
                return bigmath;
            else
                return "";
    }
}
