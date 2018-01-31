package com.example.admin.myapplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by admin on 2018/1/16.
 */

public class HttpDownloader {
    private URL url= null;


    public String download(String urlString){
        StringBuffer sb = new StringBuffer();
        String line = null;
        BufferedReader bufferedReader = null;
        try{
            url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while((line = bufferedReader.readLine())!=null){
                sb.append(line);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    public int downFile(String urlStr,String path,String fileName){
        InputStream inputStream = null;
        try{
            FileUtils fileUtils = new FileUtils();
            if (fileUtils.isFileExist(path+fileName)){
                return 1;
            }else {
                inputStream = getInputStreamFromUrl(urlStr);
                File resultFile = fileUtils.write2SDFromInput(path,fileName,inputStream);
                if (resultFile==null){
                    return -1;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                assert inputStream != null;
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
    public InputStream getInputStreamFromUrl(String urlStr)throws MalformedURLException,IOException{
        url = new URL(urlStr);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        InputStream inputStream = urlConnection.getInputStream();
        return inputStream;

    }
}
