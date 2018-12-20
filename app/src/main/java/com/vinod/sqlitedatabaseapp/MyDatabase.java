package com.vinod.sqlitedatabaseapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Vinod.Kumar on 18-03-2018.
 */

public class MyDatabase extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "student";
    String TAG = "MyDatabase";
    enum DATA_TYPE
    {
        INT,CHAR,LONG,FLOAT,DOUBLE,BOOLEAN,STRING;

    }

    public MyDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        log("Database constructor is called ");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL("create table student(id INTEGER PRIMARY KEY AUTOINCREMENT ,name varchar,rollno integer, salary real,isEnable integer )");
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
    public void insert(StudentDO object , String columnList ,String whereColumns) {
        String arrColumns[] = columnList.split(",");
        String arrWhereColumns[] = whereColumns.split(",");
        String values = " ";
        String columnListToUpdate = " ";
        for (int i = 0; i < arrColumns.length; i++) {
            columnListToUpdate = columnListToUpdate + arrColumns[i] + " = ?,";
            values = values + " ?,";
        }
        columnListToUpdate = removeLastChar(columnListToUpdate);
        columnListToUpdate = "UPDATE " + TABLE_NAME + " SET " + columnListToUpdate + " WHERE ";

        for (int i = 0; i < arrWhereColumns.length; i++) {
            columnListToUpdate = columnListToUpdate + arrColumns[i] + " = ?,";
            values = values + " ?,";
        }
        columnListToUpdate = removeLastChar(columnListToUpdate);
        values = removeLastChar(values);
        String insertQuery = "INSERT INTO " + TABLE_NAME + " (" + columnList + "," + whereColumns + ") VALUES (" + values + ")";
        String updateQuery = columnListToUpdate;
        try {
            SQLiteStatement insertStatement = getSqlStatement(insertQuery);
            SQLiteStatement updateStatement = getSqlStatement(updateQuery);

            bindValues(updateStatement, arrColumns, object, 0);
            bindValues(updateStatement, arrWhereColumns, object, arrColumns.length);
            int count = updateStatement.executeUpdateDelete();
            if (count <= 0)
            {
                bindValues(insertStatement, arrColumns, object, 0);
                bindValues(insertStatement, arrWhereColumns, object, arrColumns.length);
                insertStatement.executeInsert();
                Log.e("WRITE "," Inserted ");
            }
            else
            {
                Log.e("WRITE "," Updated ");
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            Log.e("WRITE" ,"Error occured");
        }
        finally {
            Log.e("WRITE","finally block is executed ");
        }



    }
    public void bindValues(SQLiteStatement statement ,String []arrColumns,  Object object,int startingIndex )
    {
        for(int i =0;i<arrColumns.length ;i++)
        {
            Field field = null;
            try {
                field = object.getClass().getField(arrColumns[i]);
                switch (getDataType(object,arrColumns[i]))
                {
                    case STRING:
                        statement.bindString(startingIndex+i+1,(String) field.get(object));
                        break;
                    case INT:
                        statement.bindLong(startingIndex+i+1,(int) field.get(object));
                        break;
                    case LONG:
                        statement.bindLong(startingIndex+i+1,(long) field.get(object));
                        break;
                    case FLOAT:
                        statement.bindDouble(startingIndex+i+1,(float) field.get(object));
                        break;
                    case DOUBLE:
                        statement.bindDouble(startingIndex+i+1,(double) field.get(object));
                        break;
                    default:
                        break;
                }

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

    }
    protected SQLiteStatement getSqlStatement(String sqlQuery) {
        SQLiteStatement statement = getWritableDatabase().compileStatement(sqlQuery);
        return statement;
    }
    private String removeLastChar(String str) {
        str = str.substring(0,str.length()-1);
        return str;
    }

    public  ArrayList<StudentDO>   getRecords(String ColumnNames) {
        ArrayList<StudentDO> list = new ArrayList<StudentDO>();
        SQLiteDatabase db = getReadableDatabase();
        if(TextUtils.isEmpty(ColumnNames))
            ColumnNames = " * ";
        final Cursor cursor = db.rawQuery("SELECT  "+ColumnNames+" FROM student ", null);
        int sum = 0;
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        StudentDO studentDO = new StudentDO();
                        for (int i = 0; i < cursor.getColumnCount(); i++) {
                            Object object = getType(studentDO,cursor.getColumnName(i),cursor);
                            setField(studentDO, cursor.getColumnName(i), object);
                        }
                        list.add(studentDO);
                    } while (cursor.moveToNext());

                }
            }catch (Exception e )
            {
                e.printStackTrace();
            }finally {
                cursor.close();
            }
        }
        Log.e("Result " , " size of list "+list.size());
        return  list;




    }

    private Object getType(Object studentDO,String columnName,Cursor cursor) {
        try {
            String dataTypeName = studentDO.getClass().getField( columnName).getType().getSimpleName();
            if(dataTypeName.equalsIgnoreCase("String"))
                return  cursor.getString(cursor.getColumnIndex(columnName));
            else  if(dataTypeName.toLowerCase().contains("int"))
                return  cursor.getInt(cursor.getColumnIndex(columnName));
            else  if(dataTypeName.toLowerCase().equalsIgnoreCase("long"))
                return  cursor.getLong(cursor.getColumnIndex(columnName));
            else  if(dataTypeName.toLowerCase().equalsIgnoreCase("double"))
                return  cursor.getDouble(cursor.getColumnIndex(columnName));
            else  if(dataTypeName.toLowerCase().equalsIgnoreCase("boolean"))
                return  cursor.getInt(cursor.getColumnIndex(columnName));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return "";
    }

    private DATA_TYPE getDataType(Object studentDO,String columnName ) {
        try {
            String dataTypeName = studentDO.getClass().getField( columnName).getType().getSimpleName();
            if(dataTypeName.equalsIgnoreCase("String"))
                return  DATA_TYPE.STRING;
            else  if(dataTypeName.toLowerCase().contains("int"))
                return DATA_TYPE.INT;
            else  if(dataTypeName.toLowerCase().equalsIgnoreCase("long"))
                return  DATA_TYPE.LONG;
            else  if(dataTypeName.toLowerCase().equalsIgnoreCase("double"))
                return DATA_TYPE.DOUBLE;
            else  if(dataTypeName.toLowerCase().equalsIgnoreCase("boolean"))
                return DATA_TYPE.BOOLEAN;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return DATA_TYPE.STRING;
    }

    private static boolean setField(Object targetObject, String fieldName, Object fieldValue) {


        Field field;
        try {
            field = targetObject.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            field = null;
        }
        Class superClass = targetObject.getClass().getSuperclass();
        while (field == null && superClass != null) {
            try {
                field = superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                superClass = superClass.getSuperclass();
            }
        }
        if (field == null) {
            return false;
        }
        field.setAccessible(true);
        try {
            field.set(targetObject, fieldValue);
            return true;
        } catch (IllegalAccessException e) {
            return false;
        }
    }


}
