//@MrHuxu the author
package com.mode.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class ModeSetting extends Activity {

    String getname = "";        //the name of the new item
    String chooseItem = "";         //the name of the item selected
    TextView input;
    Builder newItem;
    Builder ifDelete;
    int delItem;
    List<String> list;
    int mode_count = 0;
    SQLiteDatabase db;
    ContentValues values = new ContentValues();
    ContentValues cldf_values = new ContentValues();
    ContentValues smsdf_values = new ContentValues();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        list = new ArrayList<String>();
        list.add("原始模式");

        db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString() + "/my.db3", null);
        db.execSQL("create table if not exists mode_db(_id integer primary key autoincrement, mode_name varchar(50))");
        db.execSQL("create table if not exists hd_cl(_id integer primary key autoincrement,id integer, number integer, date integer, duration integer, type integer, new integer, name text, contactid integer, normalized_number varchar(50))");
        //因为好像hd_cl表的第一行始终是没有用的，如果实际使用会导致结果错误。。。所以在这里给这张表初始化一行，number为0，姓名为null
        Cursor dfcl_cur = db.query("hd_cl", null, null, null, null, null, null);
        if (dfcl_cur.getCount() < 1) {
            cldf_values.put("number", 0);
            cldf_values.put("name", "null");
            db.insert("hd_cl", null, cldf_values);
        }
        dfcl_cur.close();
        db.execSQL("create table if not exists hd_sw(_id integer primary key autoincrement,title varchar(50), screen integer)");
        //这里同样是给hd_sms第一行添加一个初始记录，防止出现恢复短信错误。。
        db.execSQL("create table if not exists hd_sms(_id integer primary key autoincrement, address text, person integer, date integer, read integer, status integer, type integer, body text)");
        Cursor dfsms_cur = db.query("hd_sms", null, null, null, null, null, null);
        if (dfsms_cur.getCount() < 1) {
            smsdf_values.put("address", 0);
            smsdf_values.put("body", "null");
            db.insert("hd_sms", null, smsdf_values);
        }
        dfsms_cur.close();
        db.execSQL("create table if not exists hd_dt(_id integer primary key autoincrement, hd varchar(50))");
        Cursor cursor = db.rawQuery("select * from mode_db", null);
        while (cursor.moveToNext()) {
            if (cursor.getString(1) != "1") {
                list.add(cursor.getString(1));
            }
        }
        //执行一条命令行语句，让软件获得最高权限，注意这些命令都在java.io.IOException下，所以在抛出后必须捕获
        try {
            Runtime.getRuntime().exec("su");
        } catch (IOException e){
            Log.v("result", e.getMessage().toString());
        }

        Button add = (Button) findViewById(R.id.add);
        add.setBackgroundColor(0);
        newItem = new AlertDialog.Builder(this);
        //Here is a alert dialog to add a new item into the spinner
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View source) {
                newItem.setTitle("添加一个模式:");
                RelativeLayout addForm = (RelativeLayout) getLayoutInflater().inflate(R.layout.additem, null);
                newItem.setView(addForm);
                input = ((TextView) addForm.findViewById(R.id.input));

                newItem.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getname = input.getText().toString();
                        list.add(getname);
                    }
                });
                newItem.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                newItem.create().show();
            }
        });

        final ArrayAdapter options = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        options.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner spinner = (Spinner) findViewById(R.id.root);
        spinner.setAdapter(options);


        final Button set = (Button) findViewById(R.id.set);
        final Button delete = (Button) findViewById(R.id.delete);
        delete.setBackgroundColor(0);

        spinner.setSelection(0, true);
        chooseItem = spinner.getSelectedItem().toString();
        if (chooseItem == "原始模式") {
            set.setVisibility(10);
            delete.setVisibility(10);
            set.setEnabled(false);
            delete.setEnabled(false);
        } else {
            set.setVisibility(0);
            delete.setVisibility(0);
            set.setEnabled(true);
            delete.setEnabled(true);
        }

        //A listener that if there is a item be selected
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chooseItem = spinner.getSelectedItem().toString();
                if (chooseItem == "原始模式") {
                    set.setVisibility(10);
                    delete.setVisibility(10);
                    set.setEnabled(false);
                    delete.setEnabled(false);
                } else {
                    set.setVisibility(0);
                    delete.setVisibility(0);
                    set.setEnabled(true);
                    delete.setEnabled(true);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ifDelete = new AlertDialog.Builder(this);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View source) {
                ifDelete.setTitle("确定删除该模式？");

                ifDelete.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String dp_tbl1 = "drop table if exists " + spinner.getSelectedItem().toString() + "_cl";
                        db.execSQL(dp_tbl1);
                        String dp_tbl2 = "drop table if exists " + spinner.getSelectedItem().toString() + "_sms";
                        db.execSQL(dp_tbl2);
                        String dp_tbl3 = "drop table if exists " + spinner.getSelectedItem().toString() + "_sw";
                        db.execSQL(dp_tbl3);
                        String dp_tbl4 = "drop table if exists " + spinner.getSelectedItem().toString() + "_dt";
                        db.execSQL(dp_tbl4);
                        delItem = spinner.getSelectedItemPosition();
                        list.remove(delItem);
                        spinner.setAdapter(options);
                    }
                });
                ifDelete.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                ifDelete.create().show();
            }
        });

        //a new intent to connect maininterface to the tab
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View source) {
                Intent intent = new Intent(ModeSetting.this, HideSetting.class);
                intent.putExtra("spn", spinner.getSelectedItem().toString());
                startActivity(intent);
            }
        });
    }

    //create a keyevent to judge if the back_key is clicked
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            db.execSQL("drop table mode_db");
            db.execSQL("create table if not exists mode_db(_id integer primary key autoincrement, mode_name varchar(50))");
            mode_count = list.size();
            if (mode_count != 1) {
                for (int i = 1; i < mode_count; i++) {
                    values.put("mode_name", list.get(i));
                    db.insert("mode_db", null, values);
                }
            }
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
