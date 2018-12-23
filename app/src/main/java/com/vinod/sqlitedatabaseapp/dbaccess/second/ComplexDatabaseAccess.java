package com.vinod.sqlitedatabaseapp.dbaccess.second;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import com.vinod.sqlitedatabaseapp.BaseStudentDO;
import com.vinod.sqlitedatabaseapp.ComplexDO;
import com.vinod.sqlitedatabaseapp.EmployeeDO;
import com.vinod.sqlitedatabaseapp.StudentDO;
import com.vinod.sqlitedatabaseapp.dbaccess.first.DATA_TYPE;
import com.vinod.sqlitedatabaseapp.dbaccess.first.Utils;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import static com.vinod.sqlitedatabaseapp.dbaccess.first.Utils.getInsertQuestionMarksString;
import static com.vinod.sqlitedatabaseapp.dbaccess.first.Utils.removeLastChar;

/**
 * Created by Vinod.Kumar on 18-03-2018.
 */

public class ComplexDatabaseAccess extends SQLiteOpenHelper {
    private static   String TABLE_NAME = "student";
    String TAG = "MyDatabase";
    public static SQLiteDatabase _database;
    private boolean isTranscational =false ;
    private boolean isExecutedSuccessfully=true;


    public ComplexDatabaseAccess(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        log("Database constructor is called ");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL("create table student(id INTEGER PRIMARY KEY AUTOINCREMENT ,name varchar,rollno integer, salary real,isEnable integer )");
        sqLiteDatabase.execSQL("create table employee(id INTEGER PRIMARY KEY AUTOINCREMENT ,name varchar,rollno integer, salary real,isEnable integer," +
                "column1 varchar,column2 varchar, column3 varchar, column4 varchar, column5 Integer , column6 Real, column7 varchar, column8 varachar ," +
                "column9 varchar, column10 integer  )");
        sqLiteDatabase.execSQL("create table combined(id INTEGER ,name varchar,rollno integer, salary real,isEnable integer," +
                "column1 varchar,column2 varchar, column3 varchar, column4 varchar, column5 Integer , column6 Real, column7 varchar, column8 varachar ," +
                "column9 varchar, column10 integer,str varchar  )");
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




    public void insert(Object object , String columnList ,String primaryKeyColumn) {
        String arrColumns[] = columnList.split(",");
        String arrWhereColumns[] = primaryKeyColumn.split(",");
        String values = " ";
        String columnListToUpdate = " ";
        TABLE_NAME = getTableName(object);
        columnListToUpdate = Utils.getUpdatedStringFromArrayForUpdateQueryString(arrColumns,true);
        values = getInsertQuestionMarksString(arrColumns.length+arrWhereColumns.length );
        columnListToUpdate = "UPDATE " + TABLE_NAME + " SET " + columnListToUpdate + " WHERE "+
                Utils.getUpdatedStringFromArrayForUpdateQueryString(arrWhereColumns,true);

        String insertQuery = "INSERT INTO " + TABLE_NAME + " (" + columnList + "," + primaryKeyColumn + ") VALUES (" + values + ")";
        String updateQuery = columnListToUpdate;
        compileUpdateOrInsert(_database,object, insertQuery,updateQuery,arrColumns,arrWhereColumns );
    }
    public boolean insert(Object object  ,String primaryKeyColumns ) {
        if(!isTranscational)
            openDataBase();
        if(object instanceof Vector) {
            if (((Vector) object).size() > 0)
                TABLE_NAME = getTableName(((Vector) object).get(0));
        }else if(object instanceof ArrayList) {
            if (((ArrayList) object).size() > 0)
                TABLE_NAME = getTableName(((ArrayList) object).get(0));
        } else
            TABLE_NAME =getTableName(object);

        String arrColumns[] = getColumnNamesList(TABLE_NAME);
        String arrWhereColumns[] = primaryKeyColumns.split(",");
        String columnList = getColumnNames(TABLE_NAME);
        String values = " ";
        String columnListToUpdate = " ";
        columnListToUpdate = Utils.getUpdatedStringFromArrayForUpdateQueryString(arrColumns,true);
        values = getInsertQuestionMarksString(arrColumns.length+arrWhereColumns.length );
        columnListToUpdate = "UPDATE " + TABLE_NAME + " SET " + columnListToUpdate + " WHERE "+
                Utils.getUpdatedStringFromArrayForUpdateQueryString(arrWhereColumns,true);

        String insertQuery = "INSERT INTO " + TABLE_NAME + " (" + columnList + "," + primaryKeyColumns + ") VALUES (" + values + ")";
        String updateQuery = columnListToUpdate;
        Log.e("WRITE","INSERT - "+insertQuery);
        Log.e("WRITE","UPDATE - "+updateQuery);
        return  compileUpdateOrInsert(_database,object, insertQuery,updateQuery,arrColumns,arrWhereColumns );
    }
    //below code to compile and execute query
    private boolean compileUpdateOrInsert(SQLiteDatabase writableDatabase, Object object, String insertQuery, String updateQuery, String[] arrColumns, String[] arrWhereColumns) {
        try {
            SQLiteStatement insertStatement = writableDatabase.compileStatement(insertQuery) ;
            SQLiteStatement updateStatement = writableDatabase.compileStatement(updateQuery);
            Log.e("WRITE" ,"compiled statement successfully");

            if(object instanceof Vector )
            {
                for(int i = 0 ;i<((Vector)object).size();i++)
                {
                    executeUpdateOrInsert(writableDatabase, ((Vector)object).get(i),insertStatement,updateStatement,arrColumns,arrWhereColumns);
                }
            }else if(object instanceof ArrayList)
            {
                for(int i = 0 ;i<((ArrayList)object).size();i++)
                {
                    executeUpdateOrInsert(writableDatabase, ((ArrayList)object).get(i),insertStatement,updateStatement,arrColumns,arrWhereColumns);
                }
            }else
                executeUpdateOrInsert(writableDatabase, object,insertStatement,updateStatement,arrColumns,arrWhereColumns);
        }catch (Exception e)
        {
            isExecutedSuccessfully =false;
            e.printStackTrace();
            Log.e("WRITE" ,"Error occured");
        }
        finally {
            if(isTranscational)
                Log.e("WRITE","added for transcation");
            else {
                Log.e("WRITE", "finally block is executed ");
            }
        }
        return  isExecutedSuccessfully;
    }

    private void executeUpdateOrInsert(SQLiteDatabase writableDatabase, Object object, SQLiteStatement insertStatement, SQLiteStatement updateStatement, String[] arrColumns, String[] arrWhereColumns) {
        bindValues(updateStatement, arrColumns, object, 0 ,false );
        bindValues(updateStatement, arrWhereColumns, object, arrColumns.length  ,false);
        int count = updateStatement.executeUpdateDelete();
        if (count <= 0)
        {
            bindValues(insertStatement, arrColumns, object, 0  ,false);
            bindValues(insertStatement, arrWhereColumns, object, arrColumns.length ,false );
            insertStatement.executeInsert();
            Log.e("WRITE "," Inserted ");
        }
        else
        {
            Log.e("WRITE "," Updated ");
        }
    }

    private void bindValues(SQLiteStatement statement ,String []arrColumns,  Object object,int startingIndex, boolean isNested )
    {
        for(int i =0;i<arrColumns.length ;i++)
        {
            Field field = null;
            try {
                field = object.getClass().getField(arrColumns[i]);
                switch (getDataType(object, arrColumns[i])) {
                    case STRING:
                        statement.bindString(startingIndex + i + 1, (String) field.get(object) + "");
                        break;
                    case INT:
                        statement.bindLong(startingIndex + i + 1, (int) field.get(object));
                        break;
                    case LONG:
                        statement.bindLong(startingIndex + i + 1, (long) field.get(object));
                        break;
                    case FLOAT:
                        statement.bindDouble(startingIndex + i + 1, (float) field.get(object));
                        break;
                    case DOUBLE:
                        statement.bindDouble(startingIndex + i + 1, (double) field.get(object));
                        break;
                    case BOOLEAN:
                        Boolean boo = (Boolean) field.get(object);
                        int booleanEquavalentInt = boo ? 1 : 0;
                        statement.bindLong(startingIndex + i + 1, booleanEquavalentInt);
                        break;
                    default:
                        break;

                }

            }catch (NoSuchFieldException e) {
                if(!isNested) {
                    Field[] fields = object.getClass().getDeclaredFields();
                    for (int index_fields = 0; index_fields < fields.length; index_fields++) {
                        try {
                            bindValues(statement, arrColumns, object.getClass().getFields()[index_fields].get(object), startingIndex, true);
                            Log.e("Nested", " Item "+index_fields);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

    }

    public  <T> Vector<T>   getRecords(T object,String ColumnNames) {
        Vector<T> vec = new Vector<T> ();
//        if(!isTranscational)
//        openTransaction();
        _database=getReadableDatabase();
        SQLiteDatabase db = _database;
        if(TextUtils.isEmpty(ColumnNames))
            ColumnNames = " * ";
        String TABLE_NAME = getTableName(object);
        final Cursor cursor = _database.rawQuery("SELECT  "+ColumnNames+" FROM "+TABLE_NAME+" ", null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        T objectNew = (T)getObjectBasedOnObject(object) ;
                        for (int i = 0; i < cursor.getColumnCount(); i++) {
                            Object obj = getType(objectNew,cursor.getColumnName(i),cursor);
                            setField(objectNew, cursor.getColumnName(i), obj );
                        }
                        vec.add(objectNew);
                    } while (cursor.moveToNext());

                }
            }catch (Exception e )
            {
                e.printStackTrace();
            }finally {
                if(!isTranscational)
                    closeDatabase();
                cursor.close();
            }
        }
        Log.e("Result " , " size of list "+vec.size());
        return  vec;
    }

    public  <T> Vector<T>   getRecordUsingQuery(T   object,String query) {
        Vector<T> list = new Vector<T>();
//        if(!isTranscational)
//        openTransaction();
        _database=getReadableDatabase();
        if(!TextUtils.isEmpty(query)) {
            final Cursor cursor = _database.rawQuery(query, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        do {
                            T objectNew = (T)getObjectBasedOnObject(object);
                            for (int i = 0; i < cursor.getColumnCount(); i++) {
                                Object obj = getType(objectNew, cursor.getColumnName(i), cursor);
                                setField(objectNew, cursor.getColumnName(i), obj);
                            }
                            list.add(objectNew);
                        } while (cursor.moveToNext());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
//                    if(!isTranscational)
                    closeDatabase();
                    cursor.close();
                }
            }
        }
        else
        {
            Log.e("Result " , "Query is not executed");
        }
        Log.e("Result " , " size of list "+list.size());
        return  list;
    }
    public  void   delete(Object   object,String primaryKey) {
        if(!isTranscational)
            openDataBase();
        if(object instanceof Vector) {
            if (((Vector) object).size() > 0)
                TABLE_NAME = getTableName(((Vector) object).get(0));
        }else if(object instanceof ArrayList) {
            if (((ArrayList) object).size() > 0)
                TABLE_NAME = getTableName(((ArrayList) object).get(0));
        } else
            TABLE_NAME =getTableName(object);
        if(primaryKey==null)
        {
              _database.execSQL("delete from "+TABLE_NAME );
        }
        else
        {
            String arrWhereColumns[] = primaryKey.split(",");
            String columnListToDelete = Utils.getUpdatedStringFromArrayForUpdateQueryString(arrWhereColumns, true);
            String deleteQuery = "Delete from "+TABLE_NAME+" where "+columnListToDelete;
            SQLiteStatement deleteStatement = _database.compileStatement(deleteQuery);
            Log.e("Delete",deleteQuery+"");
            if(object instanceof Vector )
            {
                for(int i = 0 ;i<((Vector)object).size();i++)
                {
                    bindValues(deleteStatement, arrWhereColumns, ((Vector)object).get(i), 0,false);
                    deleteStatement.execute();
                }
            }else if(object instanceof ArrayList)
            {
                for(int i = 0 ;i<((ArrayList)object).size();i++)
                {
                    bindValues(deleteStatement, arrWhereColumns, ((ArrayList)object).get(i), 0,false);
                    deleteStatement.execute();
                }
            }else {
                bindValues(deleteStatement, arrWhereColumns, object, 0, false);
                deleteStatement.execute();
            }
        }
    }
//    public  void   delete(Object   tableTypeobject ) {
//        if(!isTranscational)
//            closeDatabase();
//        try {
//            if(!isTranscational)
//                openDataBase();
//            TABLE_NAME =getTableName(tableTypeobject);
//            _database.execSQL("delete from "+TABLE_NAME );
//            Log.e("Delete", "delete from "+TABLE_NAME );
//
//            if(!isTranscational)
//                closeDatabase();
//        }catch (Exception e) {
//            e.printStackTrace();
//            closeDatabase();
//        }
//    }
    public  void   deleteQuery(String deleteQuery) {
        try {
            performQueryOperation(deleteQuery);
        }catch (Exception e) {
            e.printStackTrace();
            closeDatabase();
        }
    }
    public  void   insetQuery(String insertQuery) {
        try {
           performQueryOperation(insertQuery);
        }catch (Exception e) {
            e.printStackTrace();
            closeDatabase();
        }
    }
    public  void   insetUpdateDeleteQuery(Object object,   OPERATION_TYPE type,String insertUpdateDeleteQueryWithQuestionMarks,String variableNamesToBeBind) {
        if(!isTranscational)
            openDataBase();
//        if(object instanceof Vector) {
//            if (((Vector) object).size() > 0)
//                TABLE_NAME = getTableName(((Vector) object).get(0));
//        }else if(object instanceof ArrayList) {
//            if (((ArrayList) object).size() > 0)
//                TABLE_NAME = getTableName(((ArrayList) object).get(0));
//        } else
//            TABLE_NAME =getTableName(object);
        if(insertUpdateDeleteQueryWithQuestionMarks==null)
        {
            return;
        }
        else
        {
            String arrWhereColumns[] = variableNamesToBeBind.split(",");
            SQLiteStatement query = _database.compileStatement(insertUpdateDeleteQueryWithQuestionMarks);
            Log.e("Insert-Update-Delete",insertUpdateDeleteQueryWithQuestionMarks+"");
            if(object instanceof Vector )
            {
                for(int i = 0 ;i<((Vector)object).size();i++)
                {
                    bindValues(query, arrWhereColumns, ((Vector)object).get(i), 0,false);
                    executeStatementBasedOnType(type,query);
                }
            }else if(object instanceof ArrayList)
            {
                for(int i = 0 ;i<((ArrayList)object).size();i++)
                {
                    bindValues(query, arrWhereColumns, ((ArrayList)object).get(i), 0,false);
                    executeStatementBasedOnType(type,query);
                }
            }else {
                bindValues(query, arrWhereColumns, object, 0, false);
                executeStatementBasedOnType(type,query);
            }
        }
    }
    public void executeStatementBasedOnType(OPERATION_TYPE type,SQLiteStatement statement)
    {
        switch (type)
        {
            case INSERT:
                Log.e("inserted ",statement.executeInsert() +"");
                break;
            case UPDATE:
                Log.e("updateed ",statement.executeUpdateDelete()+"");
                break;
            case DELETE:
                statement.execute();
                Log.e("deleted ",  "record is deleted");
                break;
        }

    }
    public  void performQueryOperation(String query)
    {
        if(!isTranscational)
            openDataBase();
        _database.execSQL(query);

        if(!isTranscational)
            closeDatabase();
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
            else  if(dataTypeName.toLowerCase().equalsIgnoreCase("float"))
                return  cursor.getFloat(cursor.getColumnIndex(columnName));
            else  if(dataTypeName.toLowerCase().equalsIgnoreCase("boolean"))
                return  cursor.getLong(cursor.getColumnIndex(columnName)) == 1 ? true: false  ;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return "";
    }

    private   boolean setField(Object targetObject, String fieldName, Object fieldValue) {


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

    private DATA_TYPE getDataType(Object studentDO,String columnName  ) {
        try {
            String dataTypeName;

            dataTypeName = studentDO.getClass().getField( columnName).getType().getSimpleName();
            if(dataTypeName.equalsIgnoreCase("String"))
                return  DATA_TYPE.STRING;
            else  if(dataTypeName.toLowerCase().contains("int"))
                return DATA_TYPE.INT;
            else  if(dataTypeName.toLowerCase().equalsIgnoreCase("long"))
                return  DATA_TYPE.LONG;
            else  if(dataTypeName.toLowerCase().equalsIgnoreCase("double"))
                return DATA_TYPE.DOUBLE;
            else  if(dataTypeName.toLowerCase().equalsIgnoreCase("float"))
                return DATA_TYPE.FLOAT;
            else  if(dataTypeName.toLowerCase().equalsIgnoreCase("boolean"))
                return DATA_TYPE.BOOLEAN;
            else
            {
                Log.e ("Field", dataTypeName+" - Field is not found ");

            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return DATA_TYPE.STRING;
    }
    //returning table name based on object type
    private    String getTableName(Object object) {
        if(object instanceof StudentDO && object.getClass().getFields().length ==new StudentDO().getClass().getFields().length)
            return "student";
        else  if(object instanceof BaseStudentDO && object.getClass().getFields().length == new BaseStudentDO().getClass().getFields().length)
            return "student";
        else  if(object instanceof EmployeeDO && object.getClass().getFields().length ==new EmployeeDO().getClass().getFields().length)
            return "employee";
        else  if(object instanceof ComplexDO && object.getClass().getFields().length ==new ComplexDO().getClass().getFields().length)
            return "combined";
        else
            return "student";
    }
    //creating new object based on given input object type
    private Object getObjectBasedOnObject(Object object) {
        if(object instanceof StudentDO && object.getClass().getFields().length ==new StudentDO().getClass().getFields().length)
            return new StudentDO();
        else  if(object instanceof BaseStudentDO && object.getClass().getFields().length == new BaseStudentDO().getClass().getFields().length)
            return new BaseStudentDO();
        else  if(object instanceof EmployeeDO && object.getClass().getFields().length ==new EmployeeDO().getClass().getFields().length)
            return new EmployeeDO();
        else
            return new BaseStudentDO();
    }
    //column related functions
    private String getColumnNames(String tableName) {
        String columnNames = " ";
        SQLiteDatabase mDataBase;
        mDataBase = getReadableDatabase();
//        mDataBase = _database;
        Cursor dbCursor = mDataBase.rawQuery("SELECT * FROM "+tableName+" WHERE 0",null );
        try {
            String[] arrColumnNames = dbCursor.getColumnNames();
            if(columnNames!=null)
            {
                for(int i = 0 ;i<arrColumnNames .length;i++)
                    columnNames =columnNames+arrColumnNames[i]+",";
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            dbCursor.close();
        }
        return  removeLastChar(columnNames) ;
    }
    private String[] getColumnNamesList(String tableName) {
        SQLiteDatabase mDataBase;
//        openDataBase();
        mDataBase = getReadableDatabase();
        Cursor dbCursor = mDataBase.rawQuery("SELECT * FROM "+tableName+"  ", null);
        try {
            return  dbCursor.getColumnNames();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            dbCursor.close();
        }
        return   null;
    }


    //To open the database
    private   SQLiteDatabase openDataBase() throws SQLException {
        try {
            //Open the database
//            if (_database == null) {
//                _database = SQLiteDatabase.openDatabase(AppConstants.DATABASE_PATH + AppConstants.DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE
//                        | SQLiteDatabase.CREATE_IF_NECESSARY);
//            } else if (!_database.isOpen()) {
//                _database = SQLiteDatabase.openDatabase(AppConstants.DATABASE_PATH + AppConstants.DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE
//                        | SQLiteDatabase.CREATE_IF_NECESSARY);
//            }
            _database =getWritableDatabase();

            return _database;
        } catch (Exception e) {
            // In the case of C++ Exceptions, which occur, in this case
            // when trying to access a non-existence db file in the App
            // Folder. The Native C++ Exception cannot be converted to
            // Java Exception, hence e comes as null !
            // Leading to Null Pointer Exception.
            if (e == null) {
                throw new SQLException("UnKnown Error");
            }
            e.printStackTrace();
            return _database;
        }
    }
    private void closeDatabase() {
        if (_database != null && _database.isOpen())
            _database.close();
    }
    public  void commitTranscation() {
        if (_database != null && _database.isOpen()) {
            _database.setTransactionSuccessful();
            Log.e("WRITE","Committed succesfully");
        }
    }
    //DB Transactions using Sql statements
    public void openTransaction() throws SQLException {
        if (_database == null || !_database.isOpen())
            openDataBase();
        isTranscational = true;
        isExecutedSuccessfully=true;
        _database.beginTransaction();
    }
    public void closeTransaction() {
        if (_database != null && _database.isOpen()) {
            _database.endTransaction();
            closeDatabase();
            isExecutedSuccessfully=true;
            isTranscational = false;
        }
    }



}
