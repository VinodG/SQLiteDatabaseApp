package com.vinod.sqlitedatabaseapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Vinod.Kumar on 18-03-2018.
 */

public class MyDatabase extends SQLiteOpenHelper {
    String TAG = "MyDatabase";

    public MyDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        log("Database constructor is called ");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL("create table student(id INTEGER PRIMARY KEY AUTOINCREMENT ,name varchar )");
        log("table is created ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        log("table is updated ");

    }
    void log(String str)
    {
        Log.d(TAG,str+"");
    }
    public void insert(ContentValues val)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.insert("student",null,val);
        log("Record is inserted ");

    }

}
