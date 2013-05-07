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
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

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
        Cursor cursor = db.rawQuery("select * from mode_db", null);
        while (cursor.moveToNext()) {
            if (cursor.getString(1) != "1") {
                list.add(cursor.getString(1));
            }
        }
        db.execSQL("drop table mode_db");
        db.execSQL("create table if not exists mode_db(_id integer primary key autoincrement, mode_name varchar(50))");


        Button add = (Button) findViewById(R.id.add);
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
                startActivity(intent);
            }
        });
    }

    //create a keyevent to judeg if the back_key is clicked
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
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
