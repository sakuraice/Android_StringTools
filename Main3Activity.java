package com.example.admin.myapplication;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ArrowKeyMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Main3Activity extends AppCompatActivity {
    ListView lv;
    List<String> list;//目前是用來裝訊息的List
    List<String> useremail;//目前是用來裝使用者Email的List
    List<String> username;//目前是用來裝使用者名稱的List
    List<String> time;//目前是用來裝發送時間的List
    EditText chatedit;
    Button btnchat;
    ArrayAdapter<String> adapter;
    private static final int REQUEST_LOGIN = 0;//如果真的需要使用者重新登入的話，這裡設定一個狀態(旗標)，判斷使用者登入
    Myadapter myadapter;//自定義的Adapter
    int firstopen = 0;//用來解決剛進聊天室就會震動的BUG

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Main3Activity.this.setTheme(R.style.AppTheme_Base3);//對這個Activity設定Theme
        setContentView(R.layout.activity_main3);
        //以下是onCreate的開始，要設定佈局建議寫在setContentView之前
        Intent getintent = getIntent();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//進入時不會彈出鍵盤
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getintent.getStringExtra("chatname"));//設定Toolbar的標題
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);//頂端的返回按鈕顯示
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        lv = findViewById(R.id.testList);
        chatedit = findViewById(R.id.chatedit);
        btnchat = findViewById(R.id.btnchat);
        list = new ArrayList<String>();//訊息的List
        useremail = new ArrayList<String>();//email的List
        username = new ArrayList<String>();//使用者名稱的List
        time = new ArrayList<String>();//時間的List
        //adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        myadapter = new Myadapter(this);//設定自定義的adapter
        Firebase.setAndroidContext(this);
        final Firebase firebase = new Firebase("https://fishfirebase.firebaseio.com/");//設定firebase路徑
        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {//監聽狀態，其實也不需要監聽，因為這個頁面不會被登出
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {//使用者狀態的監聽
                FirebaseUser user = firebaseAuth.getCurrentUser();//取得現在的使用者
                if (user==null){//如果沒有使用者的話
                    startActivityForResult(new Intent(Main3Activity.this,loginActivity.class),REQUEST_LOGIN);//如果沒有使用者就跳到登入介面
                    finish();
                }
            }
        };
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("chat").child(getintent.getStringExtra("chatname"));//設定firebase為剛剛選的聊天室名稱
        databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener() {//設定監聽
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {//如果資料有變動(有更新的情況)
                firstopen++;//如果剛打開頁面最新的訊息不是自己發送的就會震動，為此剃除這種BUG，我用一個int 來判斷是否是剛開啟的狀態，判斷範圍除開第一次開啟，所以第一次開啟不會震動。
                list.clear();//先將List都清除，以便接下來放資料，自從1129寫好JAVA Bean之後，這個就可以改成List<Java bean>，這樣就只需要使用一個List就可以達到目的
                useremail.clear();
                time.clear();
                username.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){//跑此聊天室內所有的訊息分支
                    list.add(ds.child("message").getValue().toString());//取得訊息
                    useremail.add(ds.child("useremail").getValue().toString());//取得email
                    username.add(ds.child("nickname").getValue().toString());//取得暱稱
                    time.add(ds.child("time").getValue().toString());//取得時間
                }
                if (useremail.size()==0){//如果email的長度為0，即沒有任何資料，就關閉頁面
                    finish();
                    Toast.makeText(Main3Activity.this,"聊天室為空",Toast.LENGTH_SHORT).show();
                }else if(!auth.getCurrentUser().getEmail().equals(useremail.get(useremail.size()-1)) && firstopen>=2){//如果最新的訊息不為自己發的，就震動，且除開剛進頁面的一次載入如果有第2次以上的載入就震動。
                    Vibrator vb = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);//震動
                    vb.vibrate(300);
                }

                myadapter.notifyDataSetChanged();//更新整個List
                lv.setSelection(list.size()-1);//設定指標在最底下，即滾輪總在最底下
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        lv.setAdapter(myadapter);//設定adapter為自定義
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {//點取到選項就將鍵盤縮小
                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(Main3Activity.this.getCurrentFocus().getWindowToken(), 0);
            }
        });

        btnchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//設定發送按鈕監聽
                String message = chatedit.getText().toString().trim();//取得使用者輸入
                if (message.isEmpty()){

                }else {
                    String email = auth.getCurrentUser().getEmail();//取得當前的使用者的email
                    SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-HH:mm");//設定顯示格式
                    Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
                    String date = formatter.format(curDate);
                    String nickname = auth.getCurrentUser().getDisplayName();//取得使用者暱稱
                    Message setmessage = new Message(nickname,message,date,email);//打包進Message類中
                    databaseReference.push().setValue(setmessage);//將資料push到資料庫中，push方式會產生一組不重複的專用碼
                    chatedit.setText("");
                }
            }
        });
    }

    class Myadapter extends BaseAdapter{//自定義adapter
        LayoutInflater myinflater;
        public Myadapter(Main3Activity m){
            myinflater = LayoutInflater.from(m);
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {//覆寫getView方法
            view = myinflater.inflate(R.layout.list_layout,null);
            TextView message_user = view.findViewById(R.id.messageuser);//使用者名稱
            TextView message_text = view.findViewById(R.id.messagetext);//訊息文字
            TextView message_time = view.findViewById(R.id.messagetime);//時間
            if (useremail.get(i).equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){//email 做判斷，如果是當前使用者的話就改變使用者名稱顏色
                message_user.setTextColor(getResources().getColor(R.color.lightBLUE));//會用email 做判斷是因為email不重複且比UID還短
            }else{//如果不是目前的使用者，就是紅色
                message_user.setTextColor(Color.RED);
            }
            message_user.setText(username.get(i));//設定使用者名稱
            message_text.setText(list.get(i));//設定訊息
            message_time.setText(time.get(i));//設定時間
            return view;
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener!=null){
            auth.removeAuthStateListener(authStateListener);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish(); // back button
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}