package com.vinod.sqlitedatabaseapp.dbpreview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.vinod.sqlitedatabaseapp.R;

import java.util.Vector;

public class TablePreviewDialog extends Dialog implements android.view.View.OnClickListener {
    private static final int BUTTON_CANCEL_ID =101 ;
    private static final int BUTTON_READ_RECORDS_ID = 102;
    public   final String DATABASE_NAME = "contacts.sqlite";
    public   String DATABASE_PATH = "/data/data/com.vinod.sqlitedatabaseapp/databases/";
    SQLiteDatabase _database;

    public Activity c;
    public Dialog d;
    public Button yes ;
    private RecyclerView rvTableNames,rvRecords;
    Vector< DictionaryEntry[]> vecTablesNames,vecRecords;
    private Context context;
    private MyAdapter adapterTableNames;
    private MyAdapter adapterRecords;
    private String selectedItem="";
    private Button btnRead;
    Handler handler = new Handler();

    public TablePreviewDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

//        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.MATCH_PARENT);
        LinearLayout llMain = new LinearLayout(c);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(0,0 ,0,0);
        llMain.setLayoutParams(params);
        LinearLayout.LayoutParams btnparams = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        RecyclerView.LayoutParams rvTableparams = new RecyclerView.LayoutParams( RecyclerView.LayoutParams.MATCH_PARENT,RecyclerView.LayoutParams.WRAP_CONTENT);
        RecyclerView.LayoutParams rvRecordParams = new RecyclerView.LayoutParams( RecyclerView.LayoutParams.MATCH_PARENT,RecyclerView.LayoutParams.WRAP_CONTENT);
        llMain.setOrientation(LinearLayout.VERTICAL);

        yes = new Button(c);
        btnRead = new Button(c);
        yes.setText("Cancel");
        btnRead.setText("Read Records");
        yes.setId(BUTTON_CANCEL_ID);
        btnRead.setId(BUTTON_READ_RECORDS_ID);
        btnRead.setLayoutParams(btnparams);
        yes.setLayoutParams(btnparams);
        yes.setOnClickListener(this);
        btnRead.setOnClickListener(this);

        context = c;
        rvTableNames  = new RecyclerView(c);
        rvRecords  = new RecyclerView(c);
        rvTableNames.setLayoutParams(rvTableparams);
        rvRecords.setLayoutParams(rvTableparams);
        rvTableNames.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        rvRecords.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        adapterTableNames = new MyAdapter(context,vecTablesNames,true);
        adapterRecords = new MyAdapter(context,vecRecords,false);
        rvTableNames.setAdapter(adapterTableNames);
        rvRecords.setAdapter(adapterRecords);
        EditText et = new EditText(c );
        et.setLayoutParams(btnparams);
        et.setHint("Query");
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.e("EditText", "focus changeds"+b);
                setWindowParams();

            }
        });
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                InputMethodManager inputMethodManager =
//                        (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputMethodManager.toggleSoftInputFromWindow(
//                        view.getApplicationWindowToken(),
//                        InputMethodManager.SHOW_FORCED, 0);
            }
        });


        llMain.addView(yes);
        llMain.addView(et);
        llMain.addView(rvTableNames);
        llMain.addView(btnRead);
        llMain.addView(rvRecords);
        llMain.setBackgroundColor(Color.RED);
        setContentView(llMain);
        setWindowParams();


        setData(null,true);
        setData("employee",false);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.e("WINDOW_FOCUS",hasFocus+"");
        if(hasFocus)
            hideNavBar();
    }

    private void setWindowParams() {
        Window window = getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        window.setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        GradientDrawable gd = new GradientDrawable( GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] { 0xffffffFf, 0xFFFFFF00});
        window.setBackgroundDrawable(gd);
        setCancelable(false);


        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                hideNavBar();
            }

        });
    }

    private void hideNavBar() {

        if (Build.VERSION.SDK_INT >= 19) {
            View v = getWindow().getDecorView();
            v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    |View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case BUTTON_CANCEL_ID:
                dismiss();
                break;
            case BUTTON_READ_RECORDS_ID:
                setRecords(v);
                break;
            default:
                break;
        }
    }
    public void setRecords(View view) {
        setData(selectedItem,false);
    }
    private void setData(final String  tablename, final boolean isToExecuteTables) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(isToExecuteTables) {
                    openDataBase();
                    vecTablesNames =  get("SELECT name FROM sqlite_master WHERE type='table'");

                    handler.post( new Runnable() {
                        @Override
                        public void run() {
                            vecTablesNames.addAll(vecTablesNames);
                            adapterTableNames.refresh(vecTablesNames);
                        }
                    });
                }
                else
                {
                    vecRecords = get("SELECT * FROM "+tablename);
                    handler.post( new Runnable() {
                        @Override
                        public void run() {
                            if(vecRecords!=null && vecRecords.size()> 0  ) {
                                Vector<DictionaryEntry> temp = new Vector<DictionaryEntry>();
                                DictionaryEntry dict [] =new DictionaryEntry[vecRecords.get(0).length];

                                for(int i = 0 ;i<vecRecords.get(0).length;i++) {
                                    dict[i] = new DictionaryEntry();
                                    dict[i].value = vecRecords.get(0)[i].key;
                                    dict[i].key = vecRecords.get(0)[i].key;
                                }

                                vecRecords.add(0,dict);
                            }
                            adapterRecords.refresh(vecRecords);
                        }
                    });
                }
            }
        }).start();
    }
    private SQLiteDatabase openDataBase() throws SQLException {

        try {
            //Open the database
            if (_database == null) {
                _database = SQLiteDatabase.openDatabase( DATABASE_PATH +  DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE
                        | SQLiteDatabase.CREATE_IF_NECESSARY);
            } else if (!_database.isOpen()) {
                _database = SQLiteDatabase.openDatabase( DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE
                        | SQLiteDatabase.CREATE_IF_NECESSARY);
            }

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
    public   Vector< DictionaryEntry[]> get(String query_str)
    {
        DictionaryEntry dir = null;
        String[] columns;
        int index;
        int rowIndex = 0;
        DictionaryEntry[] row_obj = null; //An array of columns and their values
        Vector< DictionaryEntry[]> data_arr = null;
        Cursor c = null;
        openDataBase();

        if (_database != null) {
            try {
                c = _database.rawQuery(query_str, null);
                if (c.moveToFirst()) {
                    rowIndex = 0;
                    data_arr = new Vector<DictionaryEntry[]>();
//                    data_arr = new DictionaryEntry[c.getCount()][];
                    do {
                        columns = c.getColumnNames();
                        row_obj = new DictionaryEntry[columns.length]; //(columns.length);
                        for (int i = 0; i < columns.length; i++) {
                            dir = new DictionaryEntry();
                            dir.key = columns[i];
                            index = c.getColumnIndex(dir.key);
                            if (dir.key.equals("barcode") || dir.key.equals("ImageLarge")) {
                                dir.value = c.getBlob(index);
                            } else
                                dir.value = c.getString(index);
                            row_obj[i] = dir;
                        }
                        data_arr.add(row_obj);
                        rowIndex++;
                    }
                    while (c.moveToNext());
                }
                if (c != null && !c.isClosed())
                    c.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (c != null && !c.isClosed())
                    c.close();
                if(_database!=null && _database.isOpen())
                    _database.close();
            }
        }
        return data_arr;

    }
    public class DictionaryEntry {
        public String key;
        public Object value;
    }


    class MyAdapter extends RecyclerView.Adapter< MyAdapter.MyHolder>
    {
        private   boolean isSelectable;
        private Vector< DictionaryEntry[]> vec;
        private Context context;
        public MyAdapter(Context context , Vector<DictionaryEntry[]> vec , boolean isSelectable ) {
            this.vec =vec;
            this.context= context;
            this.isSelectable= isSelectable;
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            int numColumns = 0;
            if(vec!=null && vec.size()>0)
                numColumns = vec.get(0).length;

            for (int i = 0;i<numColumns;i++) {
                TextView tv = new TextView(context);
                tv.setId(i+1000);
                tv.setLayoutParams(new LinearLayout.LayoutParams(200,ViewGroup.LayoutParams.WRAP_CONTENT));
                linearLayout.addView(tv);
                TextView tvLine = new TextView(context);
                tvLine.setWidth(5);
//                tvLine.setHeight(tv.getHeight());
                tvLine.setHeight(tv.getHeight());
//                tvLine.setHeight(30);
                tvLine.setBackgroundColor(Color.RED);
                linearLayout.addView(tvLine);
            }
            linearLayout.setId((int)10000);
            return new MyHolder(linearLayout);
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            DictionaryEntry[] record = vec.get(position);
            final LinearLayout layout = holder.getMainLayout();
            for (int i = 0 ;i<record.length;i++)
            {
                TextView   tv = (TextView )layout.findViewById(i+1000);
                tv.setText(((String)record[i].value)+"");
            }
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isSelectable)
                        selectedItem = ((TextView)layout.getChildAt(0)).getText().toString();
                }
            });

        }

        @Override
        public int getItemCount() {
            return vec!=null ? vec.size():0;
        }

        public void refresh(Vector< DictionaryEntry[]> vecTablesNames) {
            this.vec =vecTablesNames;
            notifyDataSetChanged();
        }

        public class MyHolder extends RecyclerView.ViewHolder  {
            public LinearLayout ll ;
            public MyHolder(View itemView) {
                super(itemView);
                ll =(LinearLayout) (itemView);

            }
            public LinearLayout  getMainLayout()
            {
                return ll;
            }


        }

    }

}
