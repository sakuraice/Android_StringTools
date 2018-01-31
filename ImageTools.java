package com.example.admin.myapplication;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;

/**
 * Created by admin on 2018/1/23.
 */

public class ImageTools {
    private Uri imguri;
    private ContentResolver cr;
    private Bitmap bitmap;
    private Context context;
    private String filename;

    public ImageTools(Context context){
        this.context = context;

    }

    public Bitmap imageResult(Intent data){

            Uri uri = data.getData();//取得選取的檔案的Uri
            imguri = uri;
            cr = context.getContentResolver();
            try{
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                String path = new StringTools().getPathByUri4kitkat(context,imguri);//把取得的最原始路徑傳給網路上寫的方法，然後傳回轉換後的實體路徑
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        return bitmap;
    }

    public Bitmap imageBitmap(Uri imageuri){
        cr = context.getContentResolver();
        try{
            bitmap = BitmapFactory.decodeStream(cr.openInputStream(imageuri));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }
}
