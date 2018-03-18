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
    String str=null;
    ArrayList<String> list = new ArrayList<String>();
    ListView lv;
    ArrayAdapter<String> adapter =null;

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
                    ContentValues val= new ContentValues();
                    val .put("id", Calendar.getInstance().getTimeInMillis());
                    val .put("name", str);
                    myDatabase.insert(val);

                }
            }
        }).start();



    }

    public void readData(View view) {
        final String PROVIDER_NAME = "com.vinod.contentproviderapp.MyProvider";
        final String UrL = "content://" + PROVIDER_NAME + "/students";
        list.clear();
        Uri url = Uri.parse(UrL);
//        Cursor cursor = managedQuery(url, new String[]{"name"}, null, null, null);
        Cursor cursor = getContentResolver().query(url, new String[]{"name"}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do{
                list.add(cursor.getString(0));
            }
            while ( cursor.moveToNext());
        }
        adapter.notifyDataSetChanged();



    }
}
