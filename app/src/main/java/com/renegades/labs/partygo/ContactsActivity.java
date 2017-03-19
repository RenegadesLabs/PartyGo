package com.renegades.labs.partygo;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ContactsActivity extends AppCompatActivity {
    static final String TAG = "ContactsActivity";
    MyListViewAdapter myListViewAdapter;
    ArrayList<MyContact> contactsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        contactsList = new ArrayList<>();

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            int contactId = 0;

            while (cur.moveToNext()) {
                contactId++;
                MyContact myContact = new MyContact();
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);

                    pCur.moveToNext();
                    String phoneNo = pCur.getString(pCur.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));

                    myContact.setId(contactId);
                    myContact.setName(name);
                    myContact.setPhone(phoneNo);
                    myContact.setChecked(false);
                    contactsList.add(myContact);

                    pCur.close();
                }
            }
        }
        cur.close();

        Collections.sort(contactsList, new Comparator<MyContact>() {
            @Override
            public int compare(MyContact myContact, MyContact t1) {
                return myContact.getName().compareToIgnoreCase(t1.getName());
            }
        });

        ListView contactsListView = (ListView) findViewById(R.id.contacts_list);
        myListViewAdapter = new MyListViewAdapter();
        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                checkBox.toggle();
                contactsList.get(i).setChecked(checkBox.isChecked());
            }
        });
        contactsListView.setAdapter(myListViewAdapter);

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
                    intent.putStringArrayListExtra("phones", phonesList);
                    intent.putStringArrayListExtra("recipients", recipientsList);
                    startActivity(intent);
                }
            }
        });

    }

    class MyListViewAdapter extends BaseAdapter {

        class ViewHolder {
            TextView name;
            TextView phone;
            CheckBox checkBox;
        }

        @Override
        public int getCount() {
            return contactsList.size();
        }

        @Override
        public Object getItem(int i) {
            return contactsList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.contacts_list_item, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) view.findViewById(R.id.contact_name);
                viewHolder.phone = (TextView) view.findViewById(R.id.contact_phone);
                viewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.name.setText(((MyContact) getItem(i)).getName());
            viewHolder.phone.setText(((MyContact) getItem(i)).getPhone());
            viewHolder.checkBox.setChecked(((MyContact) getItem(i)).isChecked());

            return view;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }
    }
}
