//@MrHuxu the author
package com.mode.setting;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ContactList extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_list);
        ListView con_list = (ListView) findViewById(R.id.list);

        TextView con_name = (TextView)findViewById(R.id.name);
        con_name.setText("Name");
        TextView con_option_1 = (TextView) findViewById(R.id.option_1);
        con_option_1.setText("SMS");          
        TextView con_option_2 = (TextView) findViewById(R.id.option_2);
        con_option_2.setText("Call-log");         

        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        startManagingCursor(cursor);
        //create a adapter to contain the arraylist
        ListAdapter ContactAdapter;
        ContactAdapter = new SimpleCursorAdapter(this, R.layout.option_line,
                cursor,
                new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                new int[]{R.id.option_title}); 
        //make the listview to show the adapter
        con_list.setAdapter(ContactAdapter);  
        //make the listview background black
        con_list.setCacheColorHint(0);
        con_list.setBackgroundColor(0xff000000);        
    }
}
