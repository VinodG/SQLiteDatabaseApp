package com.vinod.sqlitedatabaseapp;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.vinod.sqlitedatabaseapp.dbaccess.MyDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    MyDatabase myDatabase =null;
    SQLiteDatabase db=null;
    String str="name";
    ArrayList<String> list = new ArrayList<String>();
    ListView lv;
    ArrayAdapter<String> adapter =null;
    private Boolean  isEnable=false;
    private int recordNumber=1;
    private int index=1;
    private EditText et;
    private long start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv=(ListView)findViewById(R.id.listView);
        et = ((EditText) findViewById(R.id.etInput));
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);
        lv.setAdapter(adapter);
        myDatabase = new MyDatabase(this, "contacts.sqlite",null,1);
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
                    emp.id = recordNumber;

                    StudentDO studentDO = new StudentDO();
                    studentDO.id = index;
                    studentDO.name = str ;
                    studentDO.rollno = index;
                    studentDO.salary = (double)index;
                    isEnable =!isEnable;
                    studentDO.setIsEnable(isEnable);
                    index++;
                    recordNumber++;
//                    myDatabase.insert(studentDO, "id");
//                    myDatabase.insert(emp, "id,name,rollno,salary,isEnable,column1,column2,column3,column4,column5," +
//                            "column6,column7,column8,column9,column10","id");
                    Vector<EmployeeDO> vec = new Vector<EmployeeDO>();
                    for (int i = 0;i<1000;i++) {
                        EmployeeDO e = new EmployeeDO();
                        e.id = recordNumber;
                        e.name = "name"+recordNumber++;
                        vec.add(e);
                    }
                    startCounting();

                    myDatabase.openTransaction();
                    boolean isExecuted = myDatabase.insert(vec, "id");
                    if(isExecuted)
                        myDatabase.commitTranscation();
                    myDatabase.closeTransaction();
                    endCounting();
                    et.setText("");
                }
            }
        }).start();
    }

    private void endCounting() {
        Log.e("WRITE","Time Elapsed - "+(Calendar.getInstance().getTimeInMillis()-start));
    }

    private void startCounting() {
        start = Calendar.getInstance().getTimeInMillis();
    }

    public void readData(View view) {
//        myDatabase.getRecords(null);
//        myDatabase.getRecords("id,name");
//        myDatabase. getRecords(new StudentDO(), null);
        startCounting();
//        Vector <EmployeeDO> vec  = myDatabase. getRecords(new EmployeeDO(), null);
//        Vector <EmployeeDO> vec = myDatabase.getRecordUsingQuery(new EmployeeDO(),"select * from employee  where id < "+et.getText().toString());
        Vector <EmployeeDO> vec = myDatabase.getRecordUsingQuery(new EmployeeDO(),"select * from employee  where id <  21000");
        endCounting();
        Log.e("Got Result ", vec.size()+"" );
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
