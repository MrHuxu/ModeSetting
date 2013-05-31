//@MrHuxu the author
package com.mode.setting;

import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.mode.setting.MyAdapter.ViewHolder;
import android.os.Process;
import android.app.ActivityManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private Button app_settings;
    private int checkNum; // 记录选中的条目数量
    private TextView tv_show;// 用于显示选中的条目数量
    ContentValues values = new ContentValues();
    ContentValues cl_values = new ContentValues();
    ContentValues recl_values = new ContentValues();
    ContentValues sms_values = new ContentValues();
    ContentValues resms_values = new ContentValues();
    ContentValues dt_values = new ContentValues();
    ContentValues sw_values = new ContentValues();
    ContentValues resw_values = new ContentValues();
    SQLiteDatabase db;
    Bundle bundle;
    int type;
    String crt_tbl;           //创建表用的字符串
    String dp_tbl;          //删除表要用的字符串
    String df_dbname;          //当前使用来存储的表的名字
    Cursor df_cursor;           //扫描当前使用的数据库的cursor
    ArrayList df_list = new ArrayList();          //数据库中已经存在的值，df：default
    ArrayList hdcl_list = new ArrayList();           //在calllog中已经选中的项
    ArrayList hvhdcl_list = new ArrayList();         //在hd_cl表中已经存在的项的name属性
    ArrayList hdsms_list = new ArrayList();
    ArrayList hdsmsnum_list = new ArrayList();
    ArrayList hvhdsmsnum_list = new ArrayList();
    ArrayList hdsw_list = new ArrayList();
    ArrayList hvhdscr_list = new ArrayList();
    ArrayList hvhdsw_list = new ArrayList();
    ArrayList hddt_list = new ArrayList();
    ArrayList hvhddt_list = new ArrayList();


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
        app_settings = (Button) findViewById(R.id.app_settings);
        tv_show = (TextView) findViewById(R.id.tv);
        db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString() + "/my.db3", null);
        bundle = this.getIntent().getBundleExtra("bd");
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

        try {
            Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            Log.v("result", e.getMessage().toString());
        }

        //初始化列表中的checkbox
        if (type == 1) {
            df_dbname = bundle.getString("db_name") + "_cl";
        } else if (type == 2) {
            df_dbname = bundle.getString("db_name") + "_sms";
        } else if (type == 3) {
            df_dbname = bundle.getString("db_name") + "_sw";
        } else if (type == 4) {
            df_dbname = bundle.getString("db_name") + "_dt";
        }
        db.execSQL("create table if not exists " + df_dbname + "(_id integer primary key autoincrement, hd varchar(50))");
        df_cursor = db.query(df_dbname, new String[]{"hd"}, null, null, null, null, null, null);
        while (df_cursor.moveToNext()) {
            String df_tmp = df_cursor.getString(0);
            df_list.add(df_tmp);
        }
        for (int i = 0; i < list.size(); i++) {
            if (df_list.contains(list.get(i).toString())) {
                MyAdapter.getIsSelected().put(i, true);
                checkNum++;
            }
        }
        df_cursor.close();
        dataChanged();

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
                List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
                int type = bundle.getInt("type");
                dp_tbl = "drop table if exists " + df_dbname;
                crt_tbl = "create table if not exists " + df_dbname + "(_id integer primary key autoincrement, hd varchar(50))";
                db.execSQL(dp_tbl);
                db.execSQL(crt_tbl);
                for (int i = 0; i < list.size(); i++) {
                    if (MyAdapter.getIsSelected().get(i)) {
                        values.put("hd", list.get(i));
                        db.insert(df_dbname, null, values);
                    }
                }
            }
        });

        //应用按钮的回调接口
        app_settings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //当在通话记录界面按下应用按钮时
                String name;
                if (type == 1) {
                    //遍历当前选项，获得需要隐藏的联系人名
                    Cursor hdcl_cur = db.query(df_dbname, null, null, null, null, null, null);
                    while (hdcl_cur.moveToNext()) {
                        int hdname_cl = hdcl_cur.getColumnIndex("hd");
                        hdcl_list.add(hdcl_cur.getString(hdname_cl));
                    }
                    //遍历已经隐藏的数据库，获得已经隐藏的联系人名
                    Cursor hvhdcl_cur = db.query("hd_cl", null, null, null, null, null, null);
                    while (hvhdcl_cur.moveToNext()) {
                        hvhdcl_list.add(hvhdcl_cur.getString(hvhdcl_cur.getColumnIndex("name")));
                    }
                    //将需要隐藏的通话记录从系统数据库中取出，存入hd_cl表
                    Cursor cl_cur = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
                    while (cl_cur.moveToNext()) {
                        name = cl_cur.getString(cl_cur.getColumnIndex(CallLog.Calls.CACHED_NAME));
                        if (hdcl_list.contains(name)) {
                            cl_values.put("number", cl_cur.getString(cl_cur.getColumnIndex("number")));
                            cl_values.put("date", cl_cur.getString(cl_cur.getColumnIndex("date")));
                            cl_values.put("duration", cl_cur.getInt(cl_cur.getColumnIndex("duration")));
                            cl_values.put("type", cl_cur.getInt(cl_cur.getColumnIndex("type")));
                            cl_values.put("new", cl_cur.getInt(cl_cur.getColumnIndex("new")));
                            cl_values.put("name", cl_cur.getString(cl_cur.getColumnIndex("name")));
                            cl_values.put("contactid", cl_cur.getInt(cl_cur.getColumnIndex("contactid")));
                            cl_values.put("normalized_number", cl_cur.getString(cl_cur.getColumnIndex("normalized_number")));
                            db.insert("hd_cl", null, cl_values);
                            getContentResolver().delete(CallLog.Calls.CONTENT_URI, "_id=?", new String[]{Integer.toString(cl_cur.getInt(cl_cur.getColumnIndex("_id")))});
                        }

                    }
                    //将取消隐藏的通话记录从hd_cl表获取存入系统数据库
                    if (hvhdcl_cur.moveToFirst()) {
                        while (hvhdcl_cur.moveToNext()) {
                            name = hvhdcl_cur.getString(hvhdcl_cur.getColumnIndex("name"));
                            if (!hdcl_list.contains(name)) {
                                recl_values.put(CallLog.Calls.NUMBER, hvhdcl_cur.getString(hvhdcl_cur.getColumnIndex("number")));
                                recl_values.put(CallLog.Calls.DATE, hvhdcl_cur.getString(hvhdcl_cur.getColumnIndex("date")));
                                recl_values.put(CallLog.Calls.DURATION, hvhdcl_cur.getInt(hvhdcl_cur.getColumnIndex("duration")));
                                recl_values.put(CallLog.Calls.CACHED_NAME, hvhdcl_cur.getString(hvhdcl_cur.getColumnIndex("name")));
                                recl_values.put(CallLog.Calls.TYPE, hvhdcl_cur.getInt(hvhdcl_cur.getColumnIndex("type")));
                                recl_values.put(CallLog.Calls.CACHED_NUMBER_TYPE, 2);
                                recl_values.put(CallLog.Calls._ID, hvhdcl_cur.getInt(hvhdcl_cur.getColumnIndex("contactid")));
                                getContentResolver().insert(CallLog.Calls.CONTENT_URI, recl_values);
                            }
                        }
                    }
                    //得到现在存在与hd_cl表中的项，并且和hdcl_list对比，将取消隐藏的通话记录从hd_cl表中删除
                    hvhdcl_list.clear();
                    if (hvhdcl_cur.moveToFirst()) {
                        while (hvhdcl_cur.moveToNext()) {
                            if (!hvhdcl_list.contains(hvhdcl_cur.getString(hvhdcl_cur.getColumnIndex("name"))))
                                hvhdcl_list.add(hvhdcl_cur.getString(hvhdcl_cur.getColumnIndex("name")));
                        }
                    }
                    for (int i = 0; i < hvhdcl_list.size(); i++) {
                        if (!hdcl_list.contains(hvhdcl_list.get(i).toString()))
                            db.delete("hd_cl", "name=?", new String[]{hvhdcl_list.get(i).toString()});
                    }
                    //清除数据，并关闭cursor，这一步是必须的，少了前两个，数据可能出错，少了后三个，程序可能FC
                    hdcl_list.clear();
                    hvhdcl_list.clear();
                    cl_cur.close();
                    hdcl_cur.close();
                    hvhdcl_cur.close();

                } else if (type == 2) {

                    //当在短信界面下按下应用按钮时
                    //遍历当前选项，获得需要隐藏短信的联系人名
                    Cursor hdsms_cur = db.query(df_dbname, null, null, null, null, null, null);
                    while (hdsms_cur.moveToNext()) {
                        int hdname_cl = hdsms_cur.getColumnIndex("hd");
                        hdsms_list.add(hdsms_cur.getString(hdname_cl));
                    }
                    //使用系统API，获得需要隐藏短信的联系人的电话号码
                    Cursor getnum_cur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                    while (getnum_cur.moveToNext()) {
                        if (hdsms_list.contains(getnum_cur.getString(getnum_cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)))) {
                            String con_num = getnum_cur.getString(getnum_cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            hdsmsnum_list.add(con_num);
                        }
                    }
                    //考虑有些号码前面被加了中国国际区号+86的情况
                    int tmp = hdsmsnum_list.size();
                    for (int i = 0; i < tmp; i++) {
                        hdsmsnum_list.add(("+86" + hdsmsnum_list.get(i).toString()));
                    }
                    //将需要隐藏的短信从系统数据库移入hd_sms数据库
                    Uri sms_uri = Uri.parse("content://sms/");
                    Cursor sms_cur = getContentResolver().query(sms_uri, null, null, null, null);
                    while (sms_cur.moveToNext()) {
                        String real_num = sms_cur.getString(sms_cur.getColumnIndex("address"));
                        if (hdsmsnum_list.contains(sms_cur.getString(sms_cur.getColumnIndex("address")))) {
                            sms_values.put("address", sms_cur.getString(sms_cur.getColumnIndex("address")));
                            sms_values.put("person", sms_cur.getInt(sms_cur.getColumnIndex("person")));
                            sms_values.put("date", sms_cur.getString(sms_cur.getColumnIndex("date")));
                            sms_values.put("read", sms_cur.getInt(sms_cur.getColumnIndex("read")));
                            sms_values.put("status", sms_cur.getInt(sms_cur.getColumnIndex("status")));
                            sms_values.put("type", sms_cur.getInt(sms_cur.getColumnIndex("type")));
                            sms_values.put("body", sms_cur.getString(sms_cur.getColumnIndex("body")));
                            db.insert("hd_sms", null, sms_values);
                            getContentResolver().delete(sms_uri, "_id=?", new String[]{Integer.toString(sms_cur.getInt(sms_cur.getColumnIndex("_id")))});
                        }
                    }
                    //获得hd_sms中的项的address属性
                    Cursor hvhdsms_cur = db.query("hd_sms", null, null, null, null, null, null);
                    while (hvhdsms_cur.moveToNext()) {
                        hvhdsmsnum_list.add(hvhdsms_cur.getString(hvhdsms_cur.getColumnIndex("address")));
                    }
                    //将hd_sms表中包含但当前没有选择的项移入系统短信数据库
                    if (hvhdsms_cur.moveToFirst()) {
                        while (hvhdsms_cur.moveToNext()) {
                            if (!hdsmsnum_list.contains(hvhdsms_cur.getString(hvhdsms_cur.getColumnIndex("address")))) {
                                resms_values.put("address", hvhdsms_cur.getString(hvhdsms_cur.getColumnIndex("address")));
                                resms_values.put("person", hvhdsms_cur.getString(hvhdsms_cur.getColumnIndex("person")));
                                resms_values.put("date", hvhdsms_cur.getString(hvhdsms_cur.getColumnIndex("date")));
                                resms_values.put("read", hvhdsms_cur.getInt(hvhdsms_cur.getColumnIndex("read")));
                                resms_values.put("status", hvhdsms_cur.getInt(hvhdsms_cur.getColumnIndex("status")));
                                resms_values.put("type", hvhdsms_cur.getInt(hvhdsms_cur.getColumnIndex("type")));
                                resms_values.put("body", hvhdsms_cur.getString(hvhdsms_cur.getColumnIndex("body")));
                                getContentResolver().insert(sms_uri, resms_values);
                            }
                        }
                    }
                    //消除hd_sms表中已经不需要隐藏的项
                    hvhdsmsnum_list.remove("0");
                    for (int i = 0; i < hvhdsmsnum_list.size(); i++) {
                        if (!hdsmsnum_list.contains(hvhdsmsnum_list.get(i).toString()))
                            db.delete("hd_sms", "address=?", new String[]{hvhdsmsnum_list.get(i).toString()});
                    }
                    //数据清零
                    hdsms_list.clear();
                    hdsmsnum_list.clear();
                    hvhdsmsnum_list.clear();
                    hdsms_cur.close();
                    getnum_cur.close();
                    sms_cur.close();
                    hvhdsms_cur.close();

                } else if (type == 3) {
                    Uri sw_uri = Uri.parse("content://" + "com.android.launcher2.settings" + "/favorites?notify=true");
                    Cursor sw_cur = getContentResolver().query(sw_uri, null, null, null, null);
                    Cursor hdsw_cur = db.query(df_dbname, null, null, null, null, null, null);
                    while (hdsw_cur.moveToNext()) {
                        int hdname_sw = hdsw_cur.getColumnIndex("hd");
                        hdsw_list.add(hdsw_cur.getString(hdname_sw));
                    }
                    Cursor hvhdsw_cur = db.query("hd_sw", null, null, null, null, null, null);
                    while (hvhdsw_cur.moveToNext()) {
                        hvhdsw_list.add(hvhdsw_cur.getString(hvhdsw_cur.getColumnIndex("title")));
                        hvhdscr_list.add(hvhdsw_cur.getInt(hvhdsw_cur.getColumnIndex("screen")));
                    }
                    ContentValues sw_value = new ContentValues();
                    sw_value.put("screen", 30);
                    while (sw_cur.moveToNext()) {
                        String sw_title = sw_cur.getString(sw_cur.getColumnIndex("title"));
                        if (hdsw_list.contains(sw_title) && (!hvhdsw_list.contains(sw_title))) {
                            sw_values.put("title", sw_title);
                            sw_values.put("screen", sw_cur.getInt(sw_cur.getColumnIndex("screen")));
                            db.insert("hd_sw", null, sw_values);
                            getContentResolver().update(sw_uri, sw_value, "title=?", new String[]{sw_title});
                        } else if (hvhdsw_list.contains(sw_title) && (!hdsw_list.contains(sw_title))) {
                            int screen = Integer.parseInt(hvhdscr_list.get(hvhdsw_list.indexOf(sw_title)).toString());
                            resw_values.put("screen", screen);
                            db.delete("hd_sw","title=?", new String[]{sw_title});
                            getContentResolver().update(sw_uri, resw_values, "title=?", new String[]{sw_title});
                        }
                    }
                    ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
                    am.killBackgroundProcesses("com.baidu.input");
                    sw_value.clear();
                    sw_values.clear();
                    resw_values.clear();
                    hdsw_list.clear();
                    hvhdsw_list.clear();
                    sw_cur.close();
                    hdsw_cur.close();
                    hvhdsw_cur.close();
                } else {
                    Toast.makeText(getApplicationContext(), "即将加入该功能，敬请期待！", Toast.LENGTH_SHORT).show();
//                    Cursor hddt_cur = db.query(df_dbname, null, null, null, null, null, null);
//                    while (hddt_cur.moveToNext()) {
//                        hddt_list.add(hddt_cur.getString(hddt_cur.getColumnIndex("hd")));
//                    }
//                    Cursor hvhddt_cur = db.query("hd_dt", null, null, null, null, null, null);
//                    while (hvhddt_cur.moveToNext()) {
//                        hvhddt_list.add(hvhddt_cur.getString(hvhddt_cur.getColumnIndex("hd")));
//                    }
//                    List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
//                    String tmpStr = new String();
//                    String cmd_command = new String();
//                    for (int i = 0; i < packages.size(); i++) {
//                        PackageInfo packageInfo = packages.get(i);
//                        tmpStr = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
//                        if (hddt_list.contains(tmpStr) && (!hvhddt_list.contains(tmpStr))) {
//                            dt_values.put("hd", tmpStr);
//                            db.insert("hd_dt", null, dt_values);
//                            try {
//                                Log.v("pkg name", packageInfo.packageName.toString());
//                                Runtime.getRuntime().exec("su");
//                                cmd_command = "su mv /data/data/" + packageInfo.packageName.toString() + "/databases /data/data/" + packageInfo.packageName.toString() + "/databases_old\n";
//                                Log.v("cmd 1:", cmd_command);
//                                do_exec(cmd_command);
//                                cmd_command = "su mkdir /data/data/" + packageInfo.packageName.toString() + "/databases\n";
//                                Log.v("cmd 2:", cmd_command);
//                                do_exec(cmd_command);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            break;
//                        }
//                    }
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
            while (cursor.moveToNext()) {
                int NameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                String con_tmp = cursor.getString(NameColumn);
                list.add(con_tmp);
            }
            cursor.close();
        } else if (type == 3 || type == 4) {
            List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
            int i;
            for (i = 0; i < packages.size(); i++) {
                PackageInfo packageInfo = packages.get(i);
                String tmpStr = new String();
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    tmpStr = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
//                    //这就是获得包名的方式，记住了
//                    tmpStr = packageInfo.packageName.toString();
                    if (!tmpStr.equals("ModeSetting"))
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


    //这里是执行shell语句的地方，但是带有/data/data的语句并不好使
//    String do_exec(String cmd) {
//        String s = "/n";
//        try {
//            Process p = Runtime.getRuntime().exec(cmd);
//            BufferedReader in = new BufferedReader(
//                    new InputStreamReader(p.getInputStream()));
//            String line = null;
//            while ((line = in.readLine()) != null) {
//                s += line + "/n";
//            }
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return cmd;
//    }
}