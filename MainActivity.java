package com.example.admin.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    private Button btn1,btn2,btnselect,btnnewselect,btntableselect;//insert and delete
    private String result;
    EditText edtcno,edtcname,edtcnum;//enter cno,cname,cnum
    String getedtcno,getedtcname,getedtcnum;//save string
    String returnresult;
    TextView tv;
    Spinner spinner;
    List<String> searchint;//存取搜尋到的"位置
    List<String> savetable;//存取擷取的資料
    ArrayAdapter<String> adapter;//spinner的接口
    int search = 0;//一開始用來檢測找到的"數量是否正確，現在沒再用了
    String spinnerselect = "";//存取spinner讀到的字串，結果我好像直接用 get(i)了，所以這個也沒再用

    final String API = "http://192.168.0.132:3631/Servicetest/WebServicetest.asmx";
    //需要指定WebService的namespace，可以在WebService頁面上查看
    public static String service_ns = "http://tempuri.org/";
    //WebService位置
    public static String service_url = "http://192.168.0.132:3631/Servicetest/WebServicetest.asmx";
    public static String methodName = "insertCargoInfo";
    //delete method
    public static String methodname_delete = "deleteCargoInfo";
    //select method  ---已經無用的方法
    public static String methodname_select = "selectAllCargoInfor";
    //select table method
    public static String methodname_selecttable = "selecttable";
    //select column method ---沒用到的方法
    public static String methodname_selectcolumn = "selectcolumn";
    //selectAll data method
    public static String methodname_selectAll = "selecttest";
    //select 最新的資料
    public static String methodname_selecttopdata = "selecttopdata";

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(MainActivity.this,"連接失敗",Toast.LENGTH_SHORT).show();
                case 1:
                    Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();
                    default:
                        break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        MainActivity.this.setTheme(R.style.AppTheme);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btnselect = findViewById(R.id.btnselect);
        btnnewselect = findViewById(R.id.btnnewselect);
        btntableselect = findViewById(R.id.btntableselect);
        edtcno = findViewById(R.id.edtcno);
        edtcname = findViewById(R.id.edtcname);
        edtcnum = findViewById(R.id.edtcnum);
        tv = findViewById(R.id.textView);
        spinner = findViewById(R.id.spinner);
        savetable = new ArrayList<String>();
        searchint= new ArrayList<String>();

        adapter = new ArrayAdapter<String>(this,R.layout.simple_list_item_1,savetable);

        new MyAsyncTasktable().execute();
        final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");//設定顯示格式
        Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
        String date = formatter.format(curDate) ;

        Firebase.setAndroidContext(this);
        // Creating a Firebase database Reference
        Firebase myFirebaseRef = new Firebase("https://fishfirebase.firebaseio.com/");
        //myFirebaseRef.child("message").setValue("Do you have data? You'll love Firebase.");

        btn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //BtnClick();
                getedtcno = edtcno.getText().toString().trim();
                getedtcname = edtcname.getText().toString().trim();
                getedtcnum = edtcnum.getText().toString().trim();
                if (getedtcno.isEmpty() || getedtcname.isEmpty()){
                    Toast.makeText(MainActivity.this,"欄位中有空",Toast.LENGTH_SHORT).show();
                }else {
                    new MyAsyncTaskinsert().execute(getedtcno,getedtcname,getedtcnum);//使用MyAsyncTask的方法新增
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {//進入firebase應用
                Intent it = new Intent(MainActivity.this,Main2Activity.class);
                startActivity(it);
                finish();
                /*getedtcno = edtcno.getText().toString();
                if (getedtcno.isEmpty()){
                    Toast.makeText(MainActivity.this,"欄位中有空",Toast.LENGTH_SHORT).show();
                }else{
                    new MyAsyncTaskdelete().execute(getedtcno);
                }*/
            }
        });
        btnselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//搜尋所有數據的按鈕，但是沒再用了，因為有其他按鈕做出來了
                returnresult ="";
                new MyAsyncTaskselect().execute();
            }
        });
        //selectAll data 的按鈕
        btnnewselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getedtcno = edtcno.getText().toString().trim();
                if (spinnerselect.isEmpty()){//如果選取的字串不為空
                    Toast.makeText(MainActivity.this,"欄位有空值",Toast.LENGTH_SHORT).show();
                }else{
                    returnresult = "";//重置returnresult字串
                    search = 0;
                    searchint.clear();//重置放置"位置的List
                    new  MyAsyncTaskselectAll().execute(spinnerselect);//把spinner選取的項目傳入MyAsyncTask中搜索
                }
            }
        });
        //select table的按鈕
        btntableselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnresult="";//重置returnresult字串
                savetable.clear();//重置存取table的List
                search = 0;
                searchint.clear();//重置存放"位置的List
                new MyAsyncTasktable().execute();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                searchint.clear();//重置存放"位置的List
                spinnerselect = savetable.get(i);//取得選取的項目存成字串
                new MyAsyncTaskselecttop().execute(savetable.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    //insert主要的class
    private class MyAsyncTaskinsert extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            SoapObject request = new SoapObject(service_ns,methodName);
            PropertyInfo infocno = new PropertyInfo();//因為在WebService上需要傳入三個參數，cno,cname,cnum所以這部分需要設定三個
            infocno.setName("cno");//注意這個名稱要跟WebService尚需要傳入的參數名稱一樣
            infocno.setType(String.class);//傳入的類型，因為我的資料庫都是string所以這裡都是用string來設定
            infocno.setValue(strings[0]);//設定第幾個傳入的參數，這個也要正確對應WebService上的順序
            request.addProperty(infocno);//加入SoapObject中

            PropertyInfo infocname = new PropertyInfo();
            infocname.setName("cname");//同上
            infocname.setType(String.class);//同上
            infocname.setValue(strings[1]);//同上
            request.addProperty(infocname);//同上

            PropertyInfo infocnum = new PropertyInfo();
            infocnum.setName("cnum");//同上
            infocnum.setType(String.class);//同上
            infocnum.setValue(strings[2]);//同上
            request.addProperty(infocnum);//同上

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransportSE = new HttpTransportSE(service_url);
            try{
                httpTransportSE.call(service_ns+methodName,envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result!=null){
                    returnresult = result.getProperty(0).toString();
                }
            }catch (Exception e){
                e.printStackTrace();
                return e.toString();
            }
            return returnresult;
        }

        @Override
        protected void onPostExecute(String res) {//執行過doInBackground後會自動跳轉到這個方法執行，並且doInBackground回傳的結果會傳送到這裡
            if (res.equals("true") ){//如果回傳是true
                Toast.makeText(MainActivity.this,"操作成功",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this,"操作失敗",Toast.LENGTH_SHORT).show();
            }
        }
    }
    //delete 主要的class
    private class MyAsyncTaskdelete extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            SoapObject request = new SoapObject(service_ns,methodname_delete);
            PropertyInfo infocno = new PropertyInfo();
            infocno.setName("cno");
            infocno.setType(String.class);
            infocno.setValue(strings[0]);
            request.addProperty(infocno);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE httpTransportSE = new HttpTransportSE(service_url);
            try{
                httpTransportSE.call(service_ns+methodname_delete,envelope);
                SoapObject result = (SoapObject) envelope.bodyIn;
                if (result!=null){
                    returnresult = result.getProperty(0).toString();
                }

            }catch (Exception e){
                e.printStackTrace();
                return e.toString();
            }

            return returnresult;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("true")){
                Toast.makeText(MainActivity.this,"操作成功",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();
            }
        }
    }
    // selectAll data的主要class ，舊的思考方向。現在已經沒再用了
    private class MyAsyncTaskselect extends AsyncTask<String,Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            SoapObject request = new SoapObject(service_ns,methodname_select);
            SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
            envelope.dotNet =true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE httpTransportSE = getHttpTransportSE();
            //httpTransportSE.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");//  xml 格式----網頁的效果

            try{
                httpTransportSE.call(service_ns+methodname_select,envelope);
                //SoapPrimitive response = (SoapPrimitive)envelope.getResponse();
                SoapObject result = (SoapObject) envelope.bodyIn;//自己邏輯寫出來的效果
                //SoapObject result = (SoapObject) envelope.getResponse();//砍掉anytype的效果
                if (result!= null)
                //returnresult=httpTransportSE.responseDump;  //xml格式-----網頁的效果
                returnresult = result.getProperty(0)+ "\n\n";   //----自己的邏輯寫出來的效果
                //returnresult = ((SoapObject)result.getPropertySafely("test")).toString();//----砍掉anytype的效果，但只能拿到一個數據
            }catch (Exception e){
                e.printStackTrace();
                return "error";
            }
            return returnresult;
        }
        @Override
        protected void onPostExecute(String s) {
            if (!"error".equals(s) && !"".equals(s)){

                tv.setText(s);
            }
        }
    }
    //select table的主要class
    private class MyAsyncTasktable extends AsyncTask<String,Void,String>{//網路上看到多半都是用此方法來連接的，創一個類繼承AsyncTask

        @Override
        protected String doInBackground(String... strings) {//在背景執行的方法，主要的執行動作都是寫在這裡，onPreExecute是此類在doinBackground之前執行的方法，有些人會不去寫此方法，例如我這裡就沒寫
            SoapObject request = new SoapObject(service_ns,methodname_selecttable);//使用SOAP物件需傳入NameSpace與methodName
            SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);//新建一個傳送SOAP的信封(?)
            envelope.dotNet = true;//對象WebService是否為dotNet開發的，雖然我不知道加入這個參數有什麼意義
            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransportSE = getHttpTransportSE();//需要傳入serviceURL，請參照下面我另寫的方法
            try{
                httpTransportSE.call(service_ns+methodname_selecttable,envelope);//使用httpTransportSE的CALL方法傳入兩個參數，http://tempuri.org/selecttable與包著SOAP的envelope
                SoapObject result = (SoapObject)envelope.bodyIn;//接收數據
                if (result!=null){//如果接收到的數據不為空
                    returnresult = result.getProperty(0).toString();//把接收到的數據存入字串
                }
            }catch (Exception e){
                e.printStackTrace();
                return "error";//傳回error
            }
            return returnresult;
        }
        @Override
        protected void onPostExecute(String s) {//自己想出最土法煉鋼的方法，這是doInBackground後會自動執行的方法，傳入的值是doInBackground回傳的值
            if (!"".equals(s) && !"error".equals(s)){//如果有接收到數據
                String sa;//宣告一個字串來檢查"
                for (int i = 0;i<s.length();i++){//把接收到的數據從頭跑到底
                    sa = String.valueOf(s.charAt(i));//把每個字元都存入做判斷
                    if (sa.equals("\"")){//如果中途有"的判斷
                        search++;//測試用的數量檢查
                        String search_int = Integer.toString(i);//將"的位置轉成String放入List中
                        searchint.add(search_int);
                    }
                }
                sa= "";//清空檢查的字串繼續使用
                for (int i = 0;i<searchint.size();i+=2){//從0開始跑上一個for迴圈存取到的"的位置，因為需要抓取的資料是用""括起來的所以是+=2，一次跑兩個"
                    int fir = Integer.parseInt(searchint.get(i));//把for迴圈跑的"位置抓出來
                    int sec = Integer.parseInt(searchint.get(i+1));//把for迴圈跑的第二個"位置抓出來
                    sa = s.substring(fir+1,sec);//使用剛剛的字串來接收，並用substring把第一個"的位置跟第二個"的位置作為條件抓取中間的值，由於substring的搜索點是從起點(含)到結束點(不含)，所以要+1
                    savetable.add(sa);//最後把擷取到的字串存進List就成功了。
                }
                spinner.setAdapter(adapter);//如果出現資料無法顯示，但表單卻能抓到資料，就需要將setAdapter放在抓取資料的地方(放入資料的地方)
                tv.setText(sa);
            }
        }
    }
    //selectAll data 主要的class
    private class MyAsyncTaskselectAll extends AsyncTask<String, Void , String>{

        @Override
        protected String doInBackground(String... strings) {//在selectAll data 類別中，不需要寫搜尋欄位是因為，搜尋欄位的工作已經在C#中做完了，這裡只需要傳入要搜尋的table
            SoapObject request = new SoapObject(service_ns,methodname_selectAll);
            PropertyInfo infotablename = new PropertyInfo();//因為有些WebService需要傳入參數，所以需要新建PropertyInfo來設定要傳入的參數訊息
            infotablename.setName("table_name");//設定要傳入的參數名稱，就是WebService上需要輸入的那個欄位名稱
            infotablename.setType(String.class);//需要傳入的型態
            infotablename.setValue(strings[0]);//需要傳入的第幾項，如果有多項就需要設定多個
            request.addProperty(infotablename);//將傳入參數加入SOAP

            SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE httpTransportSE = new HttpTransportSE(service_url);//傳入要連接的WebSerivceURL
            try{
                httpTransportSE.call(service_ns+methodname_selectAll,envelope);
                SoapObject result = (SoapObject)envelope.bodyIn;
                if(result!=null){
                    returnresult = result.getProperty(0).toString();
                }
            }catch (Exception e){
                e.printStackTrace();
                return "error";
            }
            return returnresult;
        }

        @Override
        protected void onPostExecute(String s) {
            if (!"".equals(s) && !"error".equals(s)){
                String sa;
                for (int i =0; i<s.length();i++){
                    sa = String.valueOf(s.charAt(i));
                    if (sa.equals("\"")){
                        search++;
                        String search_int = Integer.toString(i);
                        searchint.add(search_int);
                    }
                }
                sa = "";
                for (int i = 0;i<searchint.size();i+=2){
                    int fir = Integer.parseInt(searchint.get(i));
                    int sec = Integer.parseInt(searchint.get(i+1));
                    sa+= s.substring(fir+1,sec);
                    sa +="\n";
                }
                tv.setText(sa);
            }
        }
    }
    //select Top data 主要的 class
    private class MyAsyncTaskselecttop extends AsyncTask<String , Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            SoapObject request = new SoapObject(service_ns,methodname_selecttopdata);
            PropertyInfo infotopdata = new PropertyInfo();
            infotopdata.setName("table_name");
            infotopdata.setType(String.class);
            infotopdata.setValue(strings[0]);

            request.addProperty(infotopdata);
            SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE httpTransportSE = new HttpTransportSE(service_url);
            try{
                httpTransportSE.call(service_ns+methodname_selecttopdata,envelope);
                SoapObject result = (SoapObject)envelope.bodyIn;
                if (result!=null)
                    returnresult = result.getProperty(0).toString();
            }catch (Exception e){
                e.printStackTrace();
                return "error";
            }
            return returnresult;
        }

        @Override
        protected void onPostExecute(String s) {
            if (!"".equals(s) && !"error".equals(s)){
                String sa;
                for (int i =0;i<s.length();i++){
                    sa = String.valueOf(s.charAt(i));
                    if (sa.equals("\"")){
                        String search_int = Integer.toString(i);
                        searchint.add(search_int);
                    }
                }
                sa = "";
                for (int i =0;i<searchint.size();i+=2){
                    int fir = Integer.parseInt(searchint.get(i));
                    int sec = Integer.parseInt(searchint.get(i+1));
                    sa+= s.substring(fir+1,sec);
                    sa+="\n";
                }
                tv.setText(sa);
            }
        }
    }

    //連接HttpTransportSE 的方法
    public static HttpTransportSE getHttpTransportSE(){
        HttpTransportSE ht = new HttpTransportSE(service_url);
        ht.debug = true;
        ht.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->");
        return ht;
    }
    //設定SoapSerializationEnvelope的方法
    public static SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request){
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);
        envelope.setOutputSoapObject(request);
        return envelope;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            return true;
        }
        return false;
    }

    /*空白處縮小鍵盤
        public boolean onTouchEvent(MotionEvent event){
            if (null!= this.getCurrentFocus()){
                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            }
            return super.onTouchEvent(event);
        }*/
    //menu選單
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //menu選單方法
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.new_table://因為太多Button佔著畫面，改用menu
                getedtcno = edtcno.getText().toString().trim();
                if (!"".equals(getedtcno)){
                    searchint.clear();
                    new MyAsyncTaskselecttop().execute(getedtcno);
                }
                else{
                    Toast.makeText(MainActivity.this,"欄位是空值",Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.logout://登出
                //Service的方法

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //新的API學習，順便學用 http方法來連。
    private class httpConnection extends AsyncTask<String,Void, String>{

        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(API);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                InputStream is = httpURLConnection.getInputStream();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setReadTimeout(5000);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                String line;
                while((line=bufferedReader.readLine())!=null){
                }
                int byteCharacter ;
                String sa="";
                while((byteCharacter = is.read())!=-1){
                    sa += (char)byteCharacter;
                }
                Toast.makeText(MainActivity.this, sa, Toast.LENGTH_SHORT).show();
            }catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }


/*
    private void BtnClick() {
        final String SERVICE_NS = "http://tempuri.org/";
        final String SOAP_ACTION = "http://tempuri.org/HelloWorld";
        final String SERVICE_URL = "http://192.168.0.105:3631/Servicetest/WebServicetest.asmx";
        String methodName = "HelloWorld";
        final HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
        ht.debug = true;

        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        SoapObject soapobject = new SoapObject(SERVICE_NS,methodName);
        envelope.dotNet = true;
        envelope.bodyOut = soapobject;

        new Thread(){
            @Override
            public void run() {
                try {
                    ht.call(SOAP_ACTION,envelope);
                    if (envelope.getResponse()!=null){
                        SoapObject so = (SoapObject)envelope.bodyIn;
                        result = so.getPropertyAsString(0);
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }else {
                        Message msg = new Message();
                        msg.what = 0;
                        handler.sendMessage(msg);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }*/
}
