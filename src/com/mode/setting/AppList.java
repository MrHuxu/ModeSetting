//@MrHuxu the author
package com.mode.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.content.DialogInterface;
import android.app.ProgressDialog;

public class AppList extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_list);
        ListView app_list = (ListView) findViewById(R.id.list);

        TextView app_name = (TextView) findViewById(R.id.name);
        app_name.setText("App");
        TextView app_option_1 = (TextView) findViewById(R.id.option_1);
        app_option_1.setText("Data");
        TextView app_option_2 = (TextView) findViewById(R.id.option_2);
        app_option_2.setText("Software");

        List<Map<String, Object>> appList = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        int i;
        for (i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            String tmpStr = new String();
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                tmpStr = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                map = new HashMap<String, Object>();
                map.put("app_name", tmpStr);
                appList.add(map);
            }
        }

        SimpleAdapter AppAdapter = new SimpleAdapter(this, appList, R.layout.option_line, new String[]{"app_name"}, new int[]{R.id.option_title});
        app_list.setAdapter(AppAdapter);
        app_list.setCacheColorHint(0);
        app_list.setBackgroundColor(0xff000000);
    }
}
