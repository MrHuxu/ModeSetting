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

        Intent contact_intent = new Intent(this, ContactList.class);
        Intent app_intent = new Intent(this, AppList.class);
        TabHost tabHost = getTabHost();

        LayoutInflater.from(this).inflate(R.layout.main, tabHost.getTabContentView(), true);
        tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator("通话记录")
                .setContent(contact_intent));
        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator("短信")
                .setContent(contact_intent));
        tabHost.addTab(tabHost.newTabSpec("tab3")
                .setIndicator("应用程序")
                .setContent(app_intent));
        tabHost.addTab(tabHost.newTabSpec("tab4")
                .setIndicator("数据")
                .setContent(app_intent));

        TabWidget tabWidget = this.getTabWidget();
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            TextView tab_title = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            tab_title.setGravity(BIND_AUTO_CREATE);
            tab_title.setPadding(10, 10, 10, 10);
            tab_title.setTextSize(20);
        }
    }
}