package com.example.admin.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main2Activity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    TextView tv,tvtime;
    Button btnfbinsert,btnfbupdate,btnlogout;
    ListView lv;
    private static final int REQUEST_LOGIN = 0; //依照網路文章宣告的，實際作用不太明白，文主搜尋android studio 綠豆湯 登入
    private FirebaseAuth auth;//宣告firebase使用者
    private FirebaseAuth.AuthStateListener authStateListener;//使用者監聽
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        tv = findViewById(R.id.textView5);
        tvtime = findViewById(R.id.textView6);
        lv= findViewById(R.id.lv);
        btnfbinsert = findViewById(R.id.btnfbinsert);
        btnfbupdate = findViewById(R.id.btnfbupdate);
        btnlogout = findViewById(R.id.btnlogout);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,android.R.id.text1);
        lv.setAdapter(adapter);
        Firebase.setAndroidContext(this);//設定主要監聽的Activity
        final Firebase firebase = new Firebase("https://fishfirebase.firebaseio.com/");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);//頂端的返回按鈕顯示
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {//設置使用者狀態監聽事件，如果沒有登入就直接跳到login介面
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();//取得當前的使用者
                if (user==null){//如果取得當前使用者是空的 就等於沒有使用者登入，所以會跳轉到login介面
                    startActivityForResult(new Intent(Main2Activity.this,loginActivity.class),REQUEST_LOGIN);//ForResult要多傳一個類似狀態標籤的
                }else{

                }
            }
        };

        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//因為firebase有自動監聽此頁面使用者狀態的功能，所以這裡一旦使用者按登出就會自動跳轉到login介面
                auth.signOut();
            }
        });
        btnfbinsert.setOnClickListener(new View.OnClickListener() {//更換使用者暱稱的按鈕
            @Override
            public void onClick(View view) {//更換使用者暱稱的按鈕監聽事件
                final AlertDialog.Builder editDialog = new AlertDialog.Builder(Main2Activity.this);
                editDialog.setTitle("更換暱稱");
                final EditText editText = new EditText(Main2Activity.this);
                editText.setText("");
                editText.setSingleLine(true);//設定單行
                editDialog.setView(editText);
                editDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String nickname = editText.getText().toString().trim();
                        if (nickname.isEmpty()){//如果輸入為空

                        }else{//如果輸入不為空
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//取得當前使用者
                            UserProfileChangeRequest profileupdate = new UserProfileChangeRequest.Builder()//使用者資料變更
                                    .setDisplayName(nickname)//設定要變更的資料
                                    .build();
                            user.updateProfile(profileupdate).addOnCompleteListener(new OnCompleteListener<Void>() {//監聽使用者資料是否變更成功
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {//這裡會回傳一個變更成功與否的task狀態
                                    if (task.isSuccessful()){//如果成功，但是這裡更改暱稱是不會馬上更新的，需要重新登入才能顯示更換後的，估計是firebase那邊的bug
                                        Toast.makeText(Main2Activity.this,"更名成功",Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(Main2Activity.this,"更名失敗",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
                editDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                editDialog.show();
            }
        });

        btnfbupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//創建新聊天室
                /*Firebase userref = firebase.child("usertest").child("first");
                Map<String,Object> namemp = new HashMap<String , Object>();
                namemp.put("cno","5");
                namemp.put("cname","nanami");
                namemp.put("cnum","imoto");
                userref.updateChildren(namemp);
                Intent intent = new Intent(Main2Activity.this,Main3Activity.class);
                startActivity(intent);
                finish();*/
                AlertDialog.Builder editdialog = new AlertDialog.Builder(Main2Activity.this);//
                editdialog.setTitle("設定聊天室名稱");
                final EditText editText = new EditText(Main2Activity.this);//設定元件EditText
                editText.setText("");
                editText.setSingleLine(true);//設定單行
                editdialog.setView(editText);//將設定的EditText元件傳入Alertdialog
                editdialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String chatname = editText.getText().toString().trim();//先去除空白
                        for (int j = 0;j<adapter.getCount();j++){//跑所有的聊天室判斷是否有重複的名稱
                            String check = adapter.getItem(j);//將adapter中的項目取得出來
                            if (check.equals(chatname)){//判斷取得出來的項目跟自己出入的是否一樣
                                chatname = "";//將字串變成空的以便下面做進一步的判斷
                                Toast.makeText(Main2Activity.this,"此聊天室已創建",Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (chatname.isEmpty()){//如果使用者未輸入，或是已經有重複的名稱，所以被清空

                        }else{//如果使用者有輸入，且不重複名稱
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference("chat").child(chatname);//設定database路徑
                            String email = "admin@testemail";//設定第一個訊息的email
                            String nickname = "admin";//設定第一個訊息的發訊人
                            String message = "Hello, you can type any text to here.";//設定第一條訊息內容
                            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");//設定顯示格式
                            Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
                            String date = formatter.format(curDate);
                            Message setmessage = new Message(nickname,message,date,email);//放入暱稱+訊息+時間+email ，因為firebase不能創建空的資料庫，所以這裡必須先輸入第一筆訊息
                            database.push().setValue(setmessage);//將剛剛的設定push到資料庫

                        }
                    }
                });
                editdialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                editdialog.show();
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chat");//設定一開始進來的聊天室清單
        reference.addValueEventListener(new com.google.firebase.database.ValueEventListener() {//監聽狀態
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {//如果數據有變動
                adapter.clear();//清空adapter
                for (DataSnapshot ds : dataSnapshot.getChildren()){//跑設定路徑下的所有第一層資料
                    //adapter.add(ds.child("name").getValue().toString());
                    adapter.add(ds.getKey());//將資料增加到adapter中
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tv.setOnClickListener(new View.OnClickListener() {//使用者狀態的查看，為了方便才寫在這裡的
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//取得當前使用者
                AlertDialog.Builder dialog = new AlertDialog.Builder(Main2Activity.this);//用AlertDialog來顯示
                dialog.setTitle("user Information");
                dialog.setMessage(user.getDisplayName()+ "\n" + user.getEmail());//顯示使用者的暱稱 + email
                dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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
        lv.setOnItemClickListener(this);//設定聊天室List的項目選擇事件監聽
        tvtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//設定跳到上傳檔案的介面，因為太懶了而且不想在擠頁面，所以乾脆直接寫在現有的原件上
                Intent intent = new Intent(Main2Activity.this,Main4Activity.class);//跳轉到檔案上傳下載頁面
                startActivity(intent);//注意這裡一樣只是做跳轉，並沒有關閉這個頁面
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//IntentForResult返回的事件，正確來說是有帶值的傳回事件
        switch (requestCode){
            case REQUEST_LOGIN://檢查傳入的LOGIN狀態
                if (resultCode!=RESULT_OK){//如果不等於OK，即無登入就返回頁面，就關閉
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener!=null){//離開頁面的時候
            auth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);//第一次進入頁面的時候
    }
    @Override
    protected void onDestroy() {
        //auth.signOut();
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {//聊天室List項目選擇的監聽事件
        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileupdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(adapter.getItem(i))
                .build();
        user.updateProfile(profileupdate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Main2Activity.this,"成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Main2Activity.this,"失敗",Toast.LENGTH_SHORT).show();
                }
            }
        });*/
        Intent intent = new Intent(Main2Activity.this,Main3Activity.class);//跳到聊天室頁面
        intent.putExtra("chatname",adapter.getItem(i));//取得選取的聊天室名稱
        startActivity(intent);//只有開啟聊天室，但注意這裡並沒有關閉Main2Activity
    }


    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode== KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){ //確定按下退出鍵and防止重複按下退出鍵
            dialog();
        }
        return false;
    }
    private void dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this); //創建訊息方塊
        builder.setMessage("確定要暫時離開？");
        builder.setTitle("暫時離開APP");
        builder.setPositiveButton("確認", new DialogInterface.OnClickListener()  {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //dismiss為關閉dialog,Activity還會保留dialog的狀態
                //Main2Activity.this.finish();//關閉activity
                //handler.removeCallbacks(runnable);
                moveTaskToBack(false);//不關閉activity進行退出，重新按圖示會回到退出前的頁面
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()  {//設定取消鍵
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();//關閉dialog
            }
        });
        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//我雖然沒有寫Menu但是依然使用onCreateOptionsMenu
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home://左上角的HOME鍵按鈕的事件
                Intent intent= new Intent(Main2Activity.this,MainActivity.class);//返回第一個頁面
                startActivity(intent);
                this.finish(); // back button
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
