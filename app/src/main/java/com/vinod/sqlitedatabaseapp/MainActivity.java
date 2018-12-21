package com.vinod.sqlitedatabaseapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    MyDatabase myDatabase =null;
    SQLiteDatabase db=null;
    String str="name";
    ArrayList<String> list = new ArrayList<String>();
    ListView lv;
    ArrayAdapter<String> adapter =null;
    private Boolean  isEnable=false;
    private int recordNumber=100;
    private int index=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv=(ListView)findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);
        lv.setAdapter(adapter);
        myDatabase = new MyDatabase(this, "contacts",null,1);
    }

    public void saveData(View view)
    {
        str = ((EditText) findViewById(R.id.etInput)).getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!TextUtils.isEmpty(str))
                {
                    EmployeeDO emp = new EmployeeDO();
                    emp.id = index;

                    StudentDO studentDO = new StudentDO();
                    studentDO.id = index;
                    studentDO.name = str ;
                    studentDO.rollno = index;
                    studentDO.salary = (double)index;
                    isEnable =!isEnable;
                    studentDO.setIsEnable(isEnable);
                    index++;
//                    myDatabase.insert(studentDO, "id,name,rollno,salary,isEnable","id");
                    myDatabase.insert(emp, "id,name,rollno,salary,isEnable,column1,column2,column3,column4,column5," +
                            "column6,column7,column8,column9,column10","id");
                    ((EditText) findViewById(R.id.etInput)).setText("");
                }
            }
        }).start();
    }
    public void readData(View view) {
//        myDatabase.getRecords(null);
//        myDatabase.getRecords("id,name");
//        myDatabase. getRecords(new StudentDO(), null);
        myDatabase. getRecords(new EmployeeDO(), null);
    }

//    public void saveData(View view)
//    {
//        str = ((EditText) findViewById(R.id.etInput)).getText().toString();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if(!TextUtils.isEmpty(str))
//                {
//                    ContentValues val= new ContentValues();
//                    val .put("id", Calendar.getInstance().getTimeInMillis());
//                    val .put("name", str);
//                    val .put("rollno", recordNumber);
//                    val .put("salary", Double.parseDouble(recordNumber+""));
//                    isEnable =!isEnable;
//                    val .put("isEnable",isEnable);
//                    recordNumber++;
////                    myDatabase.insert(val);
//                    myDatabase.insert(StudentDO object , String columnList ,String whereColumns)
//                    ((EditText) findViewById(R.id.etInput)).setText("");
//                }
//            }
//        }).start();
//    }

//    public void readData(View view) {
//        final String PROVIDER_NAME = "com.vinod.contentproviderapp.MyProvider";
//        final String UrL = "content://" + PROVIDER_NAME + "/students";
//        list.clear();
//        Uri url = Uri.parse(UrL);
////        Cursor cursor = managedQuery(url, new String[]{"name"}, null, null, null);
//        Cursor cursor = getContentResolver().query(url, new String[]{"name"}, null, null, null);
//        if (cursor != null && cursor.moveToFirst()) {
//            do{
//                list.add(cursor.getString(0));
//            }
//            while ( cursor.moveToNext());
//        }
//        adapter.notifyDataSetChanged();
//    }
}
