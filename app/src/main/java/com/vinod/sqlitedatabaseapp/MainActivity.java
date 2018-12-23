package com.vinod.sqlitedatabaseapp;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.vinod.sqlitedatabaseapp.dbaccess.first.SimpleDatabaseAccess;
import com.vinod.sqlitedatabaseapp.dbaccess.second.ComplexDatabaseAccess;
import com.vinod.sqlitedatabaseapp.dbaccess.second.OPERATION_TYPE;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    ComplexDatabaseAccess myDatabase =null;
    SQLiteDatabase db=null;
    String str="name";
    private EditText et;
    private long start;
    private RecyclerView rv;
    private RecordsAdapter adapter;
    private Vector<EmployeeDO> vec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv=(RecyclerView)findViewById(R.id.rv);
        et = ((EditText) findViewById(R.id.etInput));
        rv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        adapter = new RecordsAdapter(this, new Vector<EmployeeDO>() );
        rv.setAdapter(adapter);
        myDatabase = new ComplexDatabaseAccess(this, "contacts.sqlite",null,1);
    }

    public void saveData(final  View view)
    {
        str = ((EditText) findViewById(R.id.etInput)).getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!TextUtils.isEmpty(str))
                {


//                    myDatabase.insert(studentDO, "id");
//                    myDatabase.insert(emp, "id,name,rollno,salary,isEnable,column1,column2,column3,column4,column5," +
//                            "column6,column7,column8,column9,column10","id");
//                    myDatabase.insert(emp, "id,name,rollno,salary,isEnable,column1 as column10 ","id");
//                    boolean isExecuted = myDatabase.insert(com, "id");

                    Vector<StudentDO> vecStd=  DataUtils.getStudentDO();// StudentDO -- Vector
                    Vector<EmployeeDO> vecEmp =DataUtils.getEmployeeDO();// EmployeeDO -- Vector
                    Vector<ComplexDO> vecmplx= DataUtils.getComplexDO();//CombinedDo -- vector

                    startCounting();
//                    myDatabase.openTransaction();

                    boolean isExecuted =true;
//                    isExecuted = myDatabase.insert(vecmplx, "id") && isExecuted;
//                    isExecuted = myDatabase.insert(vecStd, "id") && isExecuted;
                    isExecuted = myDatabase.insert(vecEmp, "id") && isExecuted;
//                      myDatabase.insetUpdateDeleteQuery(vecEmp,OPERATION_TYPE.UPDATE, "update employee set id=id+100,name = 'xx' where  name =? ",  "name");
//                      myDatabase.insetUpdateDeleteQuery(new EmployeeDO(),OPERATION_TYPE.UPDATE, "update employee set id=id+100,name = 'vinod' where  id =? ",  "id");
//                      myDatabase.insetUpdateDeleteQuery(vecEmp,OPERATION_TYPE.INSERT, "insert into employee (id,name) values(?,'xx')  ",  "id");
//                    if(isExecuted) {
//                        myDatabase.commitTranscation();
//                    }
//                    myDatabase.closeTransaction();
                    endCounting();
                    et.setText("");
                    readData(view);
                }
            }
        }).start();
    }

    public void readData(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //        myDatabase.getRecords(null);
//        myDatabase.getRecords("id,name");
//        myDatabase. getRecords(new StudentDO(), null);
                startCounting();
                vec  = myDatabase. getRecords(new EmployeeDO(), null);
//        Vector <EmployeeDO> vec = myDatabase.getRecordUsingQuery(new EmployeeDO(),"select * from employee  where id <  21000");
//        Log.e("Got Result ", vec.size()+"" );
                endCounting();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.refresh(vec);
                    }
                });

            }
        }).start();

    }
    private void endCounting() {
        Log.e("WRITE","Time Elapsed - "+(Calendar.getInstance().getTimeInMillis()-start));
    }

    private void startCounting() {
        start = Calendar.getInstance().getTimeInMillis();
    }

    public void deleteData(View view) {
        String id = et.getText().toString();
        if(TextUtils.isEmpty(id))
        {
            myDatabase.delete(new EmployeeDO(),null );
        }
        else
        {
//            deleteMultipleRecords();
            EmployeeDO employeeDO = new EmployeeDO();
            employeeDO.id = Integer.parseInt(id);
            employeeDO.name = "name"+employeeDO.id;
            myDatabase.insetUpdateDeleteQuery(employeeDO,OPERATION_TYPE.DELETE, "Delete from employee where  id =? ",  "id");

//            myDatabase.delete(employeeDO,"id");
        }

        et.setText("");
        readData(view);
    }

    private void deleteMultipleRecords() {
        EmployeeDO e = DataUtils.getDummyEmpObject(9);
        e.id =9;
        EmployeeDO e1 = DataUtils.getDummyEmpObject(10);
        e1.id = 10;
        Vector<EmployeeDO> vec = new Vector<EmployeeDO>( );
        vec.add(e);
        vec.add(e1);
        for(int i = 0 ;i<vec.size();i++)
        {
            e =vec.get(i);
//            myDatabase.deleteQuery("delete from employee where id = "+e.id+" and name ='"+e.name+"'");
            myDatabase.delete(e,"id");
        }
//            myDatabase.delete(vec.get(i));
    }

    public void updateData(View view) {
        String str = et.getText().toString();
        if(!TextUtils.isEmpty(str))
        {
            String param [] =str.split(" ");
//            deleteMultipleRecords();
            EmployeeDO employeeDO = new EmployeeDO();
            employeeDO.id = Integer.parseInt(param[0]);
            employeeDO.name = param[1];
            myDatabase.insetUpdateDeleteQuery(employeeDO,OPERATION_TYPE.UPDATE, "update employee set name=? where  id =?",  "name,id");

//            myDatabase.delete(employeeDO,"id");
        }
        readData(view);
        et.setText("");
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
