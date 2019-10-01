package com.example.taller2;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ListContacts extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contacts);

        Cursor mCursor;
        CursorAdapter mContactsAdapter;
        String[] mProjection;
        ListView listView = (ListView) findViewById(R.id.listViewContacts);

        mProjection = new String[]{ ContactsContract.Profile._ID, ContactsContract.Profile.DISPLAY_NAME_PRIMARY};

        mContactsAdapter = new ContactsAdapter(getApplicationContext(), null, 0);

        mCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, mProjection, null, null, null);
        mContactsAdapter.changeCursor(mCursor);
        listView.setAdapter(mContactsAdapter);

        Toast.makeText(getApplicationContext(), "Llegue al final!!!", Toast.LENGTH_LONG).show();
        //-------------------------------//
    }
}
