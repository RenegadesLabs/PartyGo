package com.renegades.labs.partygo;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
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
            switch (theme) {
                case "partyGo":
                    backgroundImage.setImageResource(R.drawable.party_go);
                    getSupportActionBar().setTitle(R.string.party_go);
                    break;
                case "men":
                    backgroundImage.setImageResource(R.drawable.men);
                    getSupportActionBar().setTitle(R.string.mens_day);
                    break;
                case "ladies":
                    backgroundImage.setImageResource(R.drawable.lady);
                    getSupportActionBar().setTitle(R.string.ladies_day);
                    break;
                case "birthday":
                    backgroundImage.setImageResource(R.drawable.birthday);
                    getSupportActionBar().setTitle(R.string.birthday);
                    break;
                case "childBirthday":
                    backgroundImage.setImageResource(R.drawable.child_birthday);
                    getSupportActionBar().setTitle(R.string.child_birthday);
                    break;
                case "wedding":
                    backgroundImage.setImageResource(R.drawable.wedding);
                    getSupportActionBar().setTitle(R.string.wedding);
                    break;
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
                        } else if (phoneNo.length() < 12) {
                            int differ = 12 - phoneNo.length();
                            String code = getCountryZipCode();
                            if (code.length() == differ) {
                                phoneNo = code + phoneNo;
                            } else if (code.length() > differ) {
                                phoneNo = code.substring(0, differ) + phoneNo;
                            }
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

    public String getCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (String aRl : rl) {
            String[] g = aRl.split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }

}
