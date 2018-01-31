package com.example.admin.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by admin on 2018/1/18.
 */

public class AsyncTaskDownloadFile extends AsyncTask<String,Integer,String>{

    private URL url;
    private StringTools stringTools;
    private String str;
    private Context context;
    private ProgressDialog progressDialog;


    public void showNotification(Context context){
        NotificationCompat.Builder notify = new NotificationCompat.Builder(context);
        notify.setProgress(100,0,false);
        notify.setContentTitle("FileDownload");
        notify.setContentText("Download");

    }

    public AsyncTaskDownloadFile(Context context) {
        this.context = context;
    }

    protected String doInBackground(String... strings) {
        int lenght;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            url = new URL(strings[0]);
            stringTools = new StringTools(strings[0],"test%2F","?alt");
            str = stringTools.textCapture2(stringTools.getString(),stringTools.getFirststr(),stringTools.getLaststr());
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            lenght = urlConnection.getContentLength();

            File new_folder = new File("sdcard/sdCarddownload");
            //File new_folder = Environment.getExternalStorageDirectory();
            if (!new_folder.exists()){
                new_folder.mkdir();
            }
            File input_file = new File(new_folder,str);
            inputStream = new BufferedInputStream(url.openStream(),8192);
            byte[] data = new byte[2048];
            int total = 0;
            int count;
            outputStream = new FileOutputStream(input_file);
            while((count= inputStream.read(data))!=-1){
                total+= count;
                outputStream.write(data,0,count);
                int progress = (total*100)/lenght;
                publishProgress(progress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (inputStream != null){
                    inputStream.close();
                }
                if (outputStream != null){
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
        }
        return "Download Complete";
    }

    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMax(100);
        progressDialog.setProgress(0);
        progressDialog.show();
    }

    protected void onPostExecute(String o) {
        progressDialog.dismiss();
    }

    protected void onProgressUpdate(Integer[] values) {
        progressDialog.setProgress(values[0]);
    }
}
