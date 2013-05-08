//@MrHuxu the author
package com.mode.setting;

import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.mode.setting.MyAdapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class OptionList extends Activity {
    private ListView lv;
    private MyAdapter mAdapter;
    private ArrayList<String> list;
    private Button bt_selectall;
    private Button bt_cancel;
    private Button bt_deselectall;
    private Button bt_savetodb;
    private int checkNum; // 记录选中的条目数量
    private TextView tv_show;// 用于显示选中的条目数量
    ContentValues values = new ContentValues();
    SQLiteDatabase db;
    Bundle bundle;
    String db_name;
    int type;

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_list);
        /* 实例化各个控件 */
        lv = (ListView) findViewById(R.id.lv);
        bt_selectall = (Button) findViewById(R.id.bt_selectall);
        bt_cancel = (Button) findViewById(R.id.bt_cancleselectall);
        bt_deselectall = (Button) findViewById(R.id.bt_deselectall);
        bt_savetodb = (Button) findViewById(R.id.save_to_db);
        tv_show = (TextView) findViewById(R.id.tv);
        db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString() + "/my.db3", null);
        bundle = this.getIntent().getBundleExtra("bd");
        db_name = bundle.getString("db_name");
        type = bundle.getInt("type");
        list = new ArrayList<String>();
        // 为Adapter准备数据
        initDate();
        // 实例化自定义的MyAdapter
        mAdapter = new MyAdapter(list, this);
        // 绑定Adapter
        LinearLayout line = (LinearLayout) findViewById(R.id.line);
        line.setBackgroundColor(0xff000000);
        lv.setCacheColorHint(0);
        lv.setBackgroundColor(0xff000000);
        lv.setAdapter(mAdapter);

        // 全选按钮的回调接口
        bt_selectall.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 遍历list的长度，将MyAdapter中的map值全部设为true
                for (int i = 0; i < list.size(); i++) {
                    MyAdapter.getIsSelected().put(i, true);
                }
                // 数量设为list的长度
                checkNum = list.size();
                // 刷新listview和TextView的显示
                dataChanged();
            }
        });

        // 反选按钮的回调接口
        bt_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 遍历list的长度，将已选的设为未选，未选的设为已选
                for (int i = 0; i < list.size(); i++) {
                    if (MyAdapter.getIsSelected().get(i)) {
                        MyAdapter.getIsSelected().put(i, false);
                        checkNum--;
                    } else {
                        MyAdapter.getIsSelected().put(i, true);
                        checkNum++;
                    }
                }
                // 刷新listview和TextView的显示
                dataChanged();
            }
        });

        // 取消按钮的回调接口
        bt_deselectall.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 遍历list的长度，将已选的按钮设为未选
                for (int i = 0; i < list.size(); i++) {
                    if (MyAdapter.getIsSelected().get(i)) {
                        MyAdapter.getIsSelected().put(i, false);
                        checkNum--;// 数量减1
                    }
                }
                // 刷新listview和TextView的显示
                dataChanged();
            }
        });

        //保存按钮的回调接口
        bt_savetodb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 遍历list的长度，将已选的项存入数据库
                String db_name = bundle.getString("db_name");
                int type = bundle.getInt("type");
                for (int i = 0; i < list.size(); i++) {
                    if (MyAdapter.getIsSelected().get(i)) {
                        if (type == 1) {
                            values.put("cl", list.get(i));
                            db.insert(db_name, null, values);
                        } else if (type == 2) {
                            values.put("mms", list.get(i));
                            db.insert(db_name, null, values);
                        } else if (type == 3) {
                            values.put("sw", list.get(i));
                            db.insert(db_name, null, values);
                        }else if (type == 4) {
                            values.put("dt", list.get(i));
                            db.insert(db_name, null, values);
                        }
                    }
                }
            }
        });
        // 绑定listView的监听器
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
                ViewHolder holder = (ViewHolder) arg1.getTag();
                // 改变CheckBox的状态
                holder.cb.toggle();
                // 将CheckBox的选中状况记录下来
                MyAdapter.getIsSelected().put(arg2, holder.cb.isChecked());
                // 调整选定条目
                if (holder.cb.isChecked() == true) {
                    checkNum++;
                } else {
                    checkNum--;
                }
                // 用TextView显示
                tv_show.setText("已选中" + checkNum + "项");
            }
        });
    }

    // 初始化数据
    private void initDate() {
        if (type == 1 || type == 2) {
            Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            startManagingCursor(cursor);
            while (cursor.moveToNext()) {
                int NameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                String con_tmp = cursor.getString(NameColumn);
                list.add(con_tmp);
            }
        } else if (type == 3 || type == 4) {
            List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
            int i;
            for (i = 0; i < packages.size(); i++) {
                PackageInfo packageInfo = packages.get(i);
                String tmpStr = new String();
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    tmpStr = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                    list.add(tmpStr);
                }
            }
        }
    }

    // 刷新listview和TextView的显示
    private void dataChanged() {
        // 通知listView刷新
        mAdapter.notifyDataSetChanged();
        // TextView显示最新的选中数目
        tv_show.setText("已选中" + checkNum + "项");
    }
}