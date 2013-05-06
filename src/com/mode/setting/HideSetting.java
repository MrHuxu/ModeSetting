//@MrHuxu the author
package com.mode.setting;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.view.View;
import android.widget.TabWidget;
import android.widget.ProgressBar;
import android.content.DialogInterface;
import android.app.ProgressDialog;

public class HideSetting extends TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent contact_intent = new Intent(this, ContactList.class);
        Intent app_intent = new Intent(this, AppList.class);
        TabHost tabHost = getTabHost();

        LayoutInflater.from(this).inflate(R.layout.main, tabHost.getTabContentView(), true);
        //show the contact list
        tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator("联系人")
                .setContent(contact_intent));
        //show the app list
        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator("应用程序")
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