package com.example.admin.myapplication;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.http.HttpConnection;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Main4Activity extends AppCompatActivity {
    ImageView firebaseimg;
    Button btnimgupdate,btnpicture,btnimageget,btnimageget2;

    StorageReference storageReference;//firebase儲存
    String URL = "https://firebasestorage.googleapis.com/v0/b/fishfirebase.appspot.com/o/test%2F01.%E3%81%AC%E3%81%8F%E3%82%82%E3%82%8A%E3%81%AE%E6%99%82%E9%96%93.mp3?alt=media&token=ad29aff8-2c27-455f-8db2-d286b2f4475d";
    TextView tv;
    ContentResolver cr;
    Bitmap bitmap;
    private ProgressDialog progressDialog;//進度條
    Uri imguri;//取得檔案的Uri
    DatabaseReference firebaseDatabase;//firebase資料庫
    FirebaseUser firebaseUser;//firebase使用者
    List<Upload> uploads;// Test


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        firebaseimg = findViewById(R.id.firebaseimg);
        btnimgupdate = findViewById(R.id.btnimgupdate);
        btnpicture = findViewById(R.id.btnpicture);
        btnimageget = findViewById(R.id.btnimageget);
        btnimageget2 = findViewById(R.id.btnimageget2);
        tv= findViewById(R.id.textView2);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){//如果有actionBar
            actionBar.setHomeButtonEnabled(true);//頂端的返回按鈕顯示
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        uploads = new ArrayList<Upload>();//Test

        storageReference = FirebaseStorage.getInstance().getReference();//firebase儲存位置
        progressDialog = new ProgressDialog(this);
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("images");//firebase資料庫位置
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();//firebase使用者


        btnpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//選擇檔案
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");//設定顯示的檔案類型
                startActivityForResult(intent, 1);
            }
        });
        btnimgupdate.setOnClickListener(new View.OnClickListener() {//上傳按鈕的事件
            @Override
            public void onClick(View view) {//上傳檔案，正常來說上傳檔案最好也用多線程來實現，不然估計會有報錯的可能，雖然這裡我沒有這樣做
                if (imguri != null) {//如果有選擇圖片的話，字串就不會為空
                    String path = getPathByUri4kitkat(Main4Activity.this,imguri);
                    if (path==null){//如果是網路上的資源，目前我先只開放給手機上的檔案上傳，但雲端上的檔案其實也是可以上傳的
                        Toast.makeText(Main4Activity.this,"雖然有選擇檔案，但無法解析網路上的檔案",Toast.LENGTH_SHORT).show();
                    }else{//如果是手機上的資源，不包含資料夾
                        final String filename = path.substring(path.lastIndexOf("/") + 1, path.length());//擷取出檔案名稱+副檔名
                        progressDialog.setCanceledOnTouchOutside(false);//設定progressdialog不會因為使用者觸碰空白處而取消顯示
                        progressDialog.show();
                        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-HH:mm");//設定顯示格式
                        Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
                        final String date = formatter.format(curDate);//把時間存入字串
                        StorageReference filepath = storageReference.child("test").child(filename);//要上傳的firebase路徑
                        filepath.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {//上傳檔案的監聽事件
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {//成功的方法
                                progressDialog.dismiss();//因為上傳成功，所以關閉progressdialog
                                Upload image = new Upload(firebaseUser.getEmail(),date, taskSnapshot.getDownloadUrl().toString(),filename);//暫時用Upload的Bean來裝上傳檔案的資訊
                                firebaseDatabase.push().setValue(image);//把上傳檔案的資訊存到資料庫
                                Toast.makeText(Main4Activity.this, "Upload is success!", Toast.LENGTH_SHORT).show();//偵錯用的toast
                                int total = (int) taskSnapshot.getTotalByteCount();//取得上傳檔案的大小，由byte顯示，如果要轉成KB要除1024
                                tv.setText(Integer.toString(total));//而taskSnapshot.getTotalByteCount()本身就是傳回一個long的類型，所以需要轉成整數或其他類型在顯示出來
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {//進度條的監聽事件
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();//取得現在上傳的大小，並乘上100，然後除以總大小
                                progressDialog.setMessage("Upload : "+(int)progress+"%");//上面乘100是因為這裡要用整數%來表示，如果不乘100會變成0.XX
                            }
                        });
                    }

                } else {//如果沒有選擇圖片，字串就會為空
                    Toast.makeText(getApplicationContext(), "未選擇檔案", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnimageget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//暫時被我用來跳到檔案頁面
                Intent intent = new Intent(Main4Activity.this,Main5Activity.class);
                startActivity(intent);
                finish();
                //Intent intent= new Intent(Main4Activity.this,DownLoadService.class);
                //startService(intent);
            }
        });

        btnimageget2.setOnClickListener(new View.OnClickListener() {//Download Manager的下載方法
            @Override
            public void onClick(View view) {

                DownloadManager.Request request =  new DownloadManager.Request(Uri.parse(URL));//建立DownloadManager.Request並傳入網址
                request.setTitle("File downloading");//Notify那邊的Title
                request.setDescription("File is being downloading");//Notify那邊的文字

                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);//設定Notify可視
                String filename = URLUtil.guessFileName(URL,null,MimeTypeMap.getFileExtensionFromUrl(URL));//取得路徑檔案的檔案名稱，就像在檔案上按另存新檔會自動取得下載的名稱那樣。
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,filename);//設定下載的檔案名稱位置
                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);//建立DownloadManager
                downloadManager.enqueue(request);//把request的設定都存進對列
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//目前看來大概是startActivityForResult返回的方法，不只用在選擇檔案，我這firebase的登入也是這樣用
        if (resultCode ==RESULT_OK){//如果回傳OK，代表有選擇檔案的話
            Uri uri = data.getData();//取得選取的檔案的Uri
            imguri = uri;
            cr = this.getContentResolver();
            try{
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                firebaseimg.setImageBitmap(bitmap);
                String path = getPathByUri4kitkat(Main4Activity.this,imguri);//把取得的最原始路徑傳給網路上寫的方法，然後傳回轉換後的實體路徑
                if(path==null){//如果選擇的圖片是網路上的就無法解析，所以有可能會傳回空字串，但經過測試後，firebase的儲存空間就算是網路上的資源也可以上傳
                    Toast.makeText(Main4Activity.this,"傳回空字串",Toast.LENGTH_SHORT).show();//傳回空字串偵錯用的
                    tv.setText(imguri.toString());
                }else{//如果不是空字串就執行下面，用來擷取字串如果為空會出現的狀況
                    String filename = path.substring(path.lastIndexOf("/") + 1, path.length());//取得圖片的實體路徑之後，取得路徑最後一個斜線後的檔案名稱+副檔名
                    tv.setText(getPathByUri4kitkat(Main4Activity.this,imguri)+"\n"+filename+"\n"+filename.substring(filename.lastIndexOf(".")+1,filename.length()));//測試路徑跟名稱用的
                }
            }catch (Exception e){
                e.printStackTrace();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //網路上寫的方法，用來轉換選擇的檔案路徑，轉成實體路徑，含檔名與副檔名，實際解析寫法我看不懂
    @SuppressLint("NewApi")
    public static String getPathByUri4kitkat(final Context context, final Uri uri) {//需傳入Activity跟要解析的路徑
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {// ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {// MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore
            // (and
            // general)
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }
    /*
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
     /* @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
     /* @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
     /* @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    //MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
                default:
        }
        return super.onOptionsItemSelected(item);
    }
}
