//@MrHuxu the author
package com.mode.setting;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class HideSetting extends TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String db_name = this.getIntent().getStringExtra("spn");

        Intent cl_intent = new Intent(this, OptionList.class);
        Bundle cl_bundle = new Bundle();
        cl_bundle.putString("db_name", db_name);
        cl_bundle.putInt("type", 1);
        cl_intent.putExtra("bd", cl_bundle);

        Intent mms_intent = new Intent(this, OptionList.class);
        Bundle mms_bundle = new Bundle();
        mms_bundle.putString("db_name", db_name);
        mms_bundle.putInt("type", 2);
        mms_intent.putExtra("bd", mms_bundle);

        Intent sw_intent = new Intent(this, OptionList.class);
        Bundle sw_bundle = new Bundle();
        sw_bundle.putString("db_name", db_name);
        sw_bundle.putInt("type", 3);
        sw_intent.putExtra("bd", sw_bundle);

        Intent dt_intent = new Intent(this, OptionList.class);
        Bundle dt_bundle = new Bundle();
        dt_bundle.putString("db_name", db_name);
        dt_bundle.putInt("type", 4);
        dt_intent.putExtra("bd", dt_bundle);

        TabHost tabHost = getTabHost();

        LayoutInflater.from(this).inflate(R.layout.main, tabHost.getTabContentView(), true);
        tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator("通话记录")
                .setContent(cl_intent));
        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator("短信")
                .setContent(mms_intent));
        tabHost.addTab(tabHost.newTabSpec("tab3")
                .setIndicator("应用程序")
                .setContent(sw_intent));
        tabHost.addTab(tabHost.newTabSpec("tab4")
                .setIndicator("数据")
                .setContent(dt_intent));

        TabWidget tabWidget = this.getTabWidget();
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            TextView tab_title = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            tab_title.setGravity(BIND_AUTO_CREATE);
            tab_title.setPadding(10, 10, 10, 10);
            tab_title.setTextSize(20);
        }
    }
}