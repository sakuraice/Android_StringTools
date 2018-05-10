package com.example.admin.myapplication;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Main5Activity extends AppCompatActivity {
    ListView lv;
    ArrayAdapter<String> adapter;//ListView 用的Adapter
    List<String> downloadList;//下載網址的List
    List<String> fileList;//檔案名稱的List
    List<String> emailList;//上傳者的List
    List<String> dateList;//上傳時間的List
    List<Upload> firebase;//自己測試寫JAVA Bean的List


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        lv= findViewById(R.id.FileListView);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);//頂端的返回按鈕顯示
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        downloadList = new ArrayList<String>();
        fileList = new ArrayList<String>();
        emailList = new ArrayList<String>();
        dateList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,android.R.id.text1);//設定adapter，adapter本身就可以當作一個List來使用
        lv.setAdapter(adapter);//設定ListView 的adapter

        firebase = new ArrayList<Upload>();//JAVA bean的List

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("images");//這裡設定要儲存的資料庫路徑
        reference.addValueEventListener(new ValueEventListener() {//設定資料庫監聽事件
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//資料變更的事件
                adapter.clear();//都先將資料清空以免資料疊加
                firebase.clear();//都先將資料清空以免資料疊加
                for (DataSnapshot ds :dataSnapshot.getChildren()){//跑遍整個支線
                    Upload upload = new Upload(ds.child("email").getValue().toString(),//自己嘗試寫JAVA Bean，要依照Upload類寫的順序放入資料，取得支線下的資料
                            ds.child("date").getValue().toString(),
                            ds.child("downloadURL").getValue().toString(),
                            ds.child("file").getValue().toString());
                    firebase.add(upload);//把JAVA Bean加入List中
                    adapter.add(ds.child("file").getValue().toString());//將檔案名稱添加到adapter中，作為ListView的項目顯示
                    //downloadList.add(ds.child("downloadURL").getValue().toString());//以下四個都可以改成firebase這個List了
                    //fileList.add(ds.child("file").getValue().toString());
                    //emailList.add(ds.child("email").getValue().toString());
                    //dateList.add(ds.child("date").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {//設定ListView 的項目被點擊的監聽事件
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {//點擊事件，會傳入四個參數，最常用的大概是int i 這個(項目的位址)
                final AlertDialog.Builder dialog = new AlertDialog.Builder(Main5Activity.this);//建立一個AlertDialog用於顯示檔案資訊與下載檔案的
                dialog.setTitle("檔案資訊");//設定標題
                dialog.setMessage("檔案名稱： "+firebase.get(i).getFile()+"\n上傳時間： "+firebase.get(i).getDate()+"\n上傳者："+firebase.get(i).getEmail());//設定AlertDialog內的文字
                final String Url = firebase.get(i).getDownloadURL();//取得檔案的下載位址
                dialog.setPositiveButton("下載檔案", new DialogInterface.OnClickListener() {//下載檔案的按鈕監聽事件
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {//下載按鈕事件，正常來說下載應該要搭配多線程來使用，這裡我就懶得寫了，所以還是有報錯的可能
                        DownloadManager.Request request =  new DownloadManager.Request(Uri.parse(Url));//建立DownloadManager.Request並傳入網址
                        request.setTitle("File downloading");//Notify那邊的Title
                        request.setDescription("File is being downloading");//Notify那邊的文字

                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);//設定Notify可視
                        String filename = URLUtil.guessFileName(Url,null, MimeTypeMap.getFileExtensionFromUrl(Url));//取得路徑檔案的檔案名稱，就像在檔案上按另存新檔會自動取得下載的名稱那樣。
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,filename);//設定下載的檔案名稱位置
                        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);//建立DownloadManager
                        downloadManager.enqueue(request);
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(Main5Activity.this,Main4Activity.class);//跳回上傳頁面
                startActivity(intent);
                finish();//關閉目前頁面
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}