package com.example.admin.myapplication;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by admin on 2018/1/17.
 */

public class StringTools {

    private String string;
    private String firststr;
    private String laststr;
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getFirststr() {
        return firststr;
    }

    public void setFirststr(String firststr) {
        this.firststr = firststr;
    }

    public String getLaststr() {
        return laststr;
    }

    public void setLaststr(String laststr) {
        this.laststr = laststr;
    }

    public StringTools(String string){

    }

    public StringTools(Context context) {
        this.context = context;
    }

    public StringTools(String string, String firststr, String laststr){
        this.string = string;
        this.firststr = firststr;
        this.laststr = laststr;
    }

    public StringTools() {
    }

    //擷取檔案名稱的方法
    public String pathnameCapture(String pathname){
        String name = pathname.substring(pathname.lastIndexOf("/") + 1, pathname.length());
        if (name==null){
            return pathname;
        }else {
            return name;
        }
    }

    //傳入虛擬檔案路徑並解析，最後分割成檔案名稱
    public String filenameCapture(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){//如果回傳OK，代表有選擇檔案的話
            Uri uri = data.getData();
            return pathnameCapture(getPathByUri4kitkat(context,uri));
        }else{
            return null;
        }
    }

    //給URL擷取字串並解碼
    public String textCapture2(String string,String firststr,String laststr){
        if (firststr==null || laststr==null){
            return decode(pathnameCapture(string));
        }else{
            String str = string.substring(string.indexOf(firststr)+firststr.length(),string.indexOf(laststr));
            return decode(str);
        }
    }
    //URLdecode解碼方法
    public String decode(String url) {
        try {
            String prevURL = "";
            String decodeURL = url;
            while(!prevURL.equals(decodeURL)) {
                prevURL = decodeURL;
                decodeURL = URLDecoder.decode( decodeURL, "UTF-8" );
            }
            return decodeURL;
        } catch (UnsupportedEncodingException e) {
            return "Error: " + e.getMessage();
        }
    }
    //URLdecode編碼方法
    public String encode(String url) {
        try {
            String encodeURL = URLEncoder.encode( url, "UTF-8" );
            return encodeURL;
        } catch (UnsupportedEncodingException e) {
            return "Error: " + e.getMessage();
        }
    }

    //擷取""中的字串的方法
    public List textCapture(String text){
        List<Integer> searchint = new ArrayList< Integer>();
        List<String> textList = new ArrayList<String>();
        if (text!=null){
            String str = "";
            for (int i = 0;i<text.length();i++){//把接收到的數據從頭跑到底
                str = String.valueOf(text.charAt(i));//把每個字元都存入做判斷
                if (str.equals("\"")){//如果中途有"的判斷
                    searchint.add(i);//將"的位置轉成String放入List中
                }
            }
            if (searchint.size()%2==0){
                str= "";//清空檢查的字串繼續使用
                for (int i = 0;i<searchint.size();i+=2){//從0開始跑上一個for迴圈存取到的"的位置，因為需要抓取的資料是用""括起來的所以是+=2，一次跑兩個"
                    int fir = searchint.get(i);//把for迴圈跑的"位置抓出來
                    int sec = searchint.get(i+1);//把for迴圈跑的第二個"位置抓出來
                    str = text.substring(fir+1,sec);//使用剛剛的字串來接收，並用substring把第一個"的位置跟第二個"的位置作為條件抓取中間的值，由於substring的搜索點是從起點(含)到結束點(不含)，所以要+1
                    textList.add(str);//最後把擷取到的字串存進List就成功了。
                }
            }
        }else{
            return null;
        }
        return textList;
    }

    //將選的檔案路徑轉成實體路徑
    @SuppressLint("NewApi")
    public String getPathByUri4kitkat(final Context context, final Uri uri) {//需傳入Activity跟要解析的路徑
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
    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
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
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    /* @param uri
    *            The Uri to check.
    * @return Whether the Uri authority is DownloadsProvider.
    */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    /* @param uri
    *            The Uri to check.
    * @return Whether the Uri authority is MediaProvider.
    */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
