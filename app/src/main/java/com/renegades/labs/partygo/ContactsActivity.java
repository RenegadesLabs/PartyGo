package com.renegades.labs.partygo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {
    static final String TAG = "ContactsActivity";
    ArrayList<MyContact> contactsList;
    MyListViewAdapter myListViewAdapter;
    String theme;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Intent intent = getIntent();
        theme = intent.getStringExtra("theme");
        if (theme != null) {
            ImageView backgroundImage = (ImageView) findViewById(R.id.background_image_contacts);
            if (theme.equals("men")) {
                backgroundImage.setImageResource(R.drawable.men);
            } else if (theme.equals("ladies")) {
                backgroundImage.setImageResource(R.drawable.lady);
            } else if (theme.equals("birthday")) {
                backgroundImage.setImageResource(R.drawable.birthday);
            } else if (theme.equals("childBirthday")) {
                backgroundImage.setImageResource(R.drawable.child_birthday);
            } else if (theme.equals("wedding")) {
                backgroundImage.setImageResource(R.drawable.wedding);
            }
        } else {
            theme = "";
        }

        dbHelper = new DBHelper(this);
        contactsList = new ArrayList<>();

        ListView contactsListView = (ListView) findViewById(R.id.contacts_list);
        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                checkBox.toggle();
                contactsList.get(i).setChecked(checkBox.isChecked());
            }
        });
        myListViewAdapter = new MyListViewAdapter(this, contactsList);
        contactsListView.setAdapter(myListViewAdapter);

        AsyncTask<Void, Void, List<MyContact>> asyncTask = new LoadContactsTask(this,
                myListViewAdapter, contactsList);
        asyncTask.execute();

        Button sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = contactsList.size();
                ArrayList<String> phonesList = new ArrayList<String>();
                ArrayList<String> recipientsList = new ArrayList<String>();
                Log.d(TAG, "onClick: count = " + count);

                for (int i = 0; i < count; i++) {
                    if (contactsList.get(i).isChecked()) {
                        putInDatabase(contactsList.get(i));

                        String phoneNo = contactsList.get(i).getPhone();
                        phoneNo = phoneNo.replaceAll(" ", "");
                        if (phoneNo.charAt(0) == '+') {
                            phoneNo = phoneNo.substring(1);
                        } else if (phoneNo.charAt(0) == '0') {
                            phoneNo = "38" + phoneNo;
                        }

                        String recipient = contactsList.get(i).getName();

                        Log.d(TAG, "onClick: phone = " + phoneNo);
                        Log.d(TAG, "onClick: name = " + recipient);
                        phonesList.add(phoneNo);
                        recipientsList.add(recipient);
                    }
                }

                if (phonesList.size() > 0 && recipientsList.size() > 0) {
                    Intent intent = new Intent(ContactsActivity.this, NotificationActivity.class);
                    intent.putExtra("theme", theme);
                    intent.putStringArrayListExtra("phones", phonesList);
                    intent.putStringArrayListExtra("recipients", recipientsList);
                    startActivity(intent);
                }
            }
        });
    }

    private void putInDatabase(MyContact contact) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cv.clear();
        cv.put("name", contact.getName());
        cv.put("phone", contact.getPhone());
        cv.put("priority", contact.getPriority() + 1);
        db.insert("contacts", null, cv);
        db.close();

        Log.d(TAG, "putInDatabase: name = " + contact.getName());
        Log.d(TAG, "putInDatabase: priority = " + (contact.getPriority() + 1));
    }

}
