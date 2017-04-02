package com.renegades.labs.partygo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Виталик on 02.04.2017.
 */

public class LoadContactsTast extends AsyncTask<Void, Void, List<MyContact>> {
    private Context mContext;
    private BaseAdapter adapter;
    private List<MyContact> contactsList;
    private static final String TAG = "LoadContactsTast";

    public LoadContactsTast(Context mContext, BaseAdapter adapter, List<MyContact> contactsList) {
        this.mContext = mContext;
        this.adapter = adapter;
        this.contactsList = contactsList;
    }

    @Override
    protected List<MyContact> doInBackground(Void... voids) {
        List<MyContact> contactsList = new ArrayList<MyContact>();
        ContentResolver cr = mContext.getContentResolver();
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

        DBHelper dbHelper = new DBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sqlQuery = "SELECT name, phone, priority FROM contacts ;";
        Cursor c = db.rawQuery(sqlQuery, null);

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    MyContact dbContact = new MyContact();
                    dbContact.setName(c.getString(0));
                    dbContact.setPhone(c.getString(1));
                    dbContact.setPriority(c.getInt(2));
                    dbContact.setChecked(false);

                    if (contactsList.contains(dbContact)) {
                        contactsList.set(contactsList.indexOf(dbContact), dbContact);
                    } else {
                        contactsList.add(dbContact);
                    }

                } while (c.moveToNext());
            }
            c.close();
        } else {
            Log.d(TAG, "Cursor is null");
        }
        db.close();

        Collections.sort(contactsList, new Comparator<MyContact>() {
            @Override
            public int compare(MyContact myContact, MyContact t1) {
                if (t1.getPriority() - myContact.getPriority() != 0) {
                    Log.d(TAG, "Priority1 = " + myContact.getPriority());
                    Log.d(TAG, "Priority2 = " + t1.getPriority());
                    Log.d(TAG, "result = " + (myContact.getPriority() - t1.getPriority()));

                    return t1.getPriority() - myContact.getPriority();
                } else {
                    return myContact.getName().compareToIgnoreCase(t1.getName());
                }
            }
        });
        return contactsList;
    }

    @Override
    protected void onPostExecute(List<MyContact> myContacts) {
        super.onPostExecute(myContacts);
        contactsList.addAll(myContacts);
        Collections.sort(contactsList, new Comparator<MyContact>() {
            @Override
            public int compare(MyContact myContact, MyContact t1) {
                if (t1.getPriority() - myContact.getPriority() != 0) {
                    return t1.getPriority() - myContact.getPriority();
                } else {
                    return myContact.getName().compareToIgnoreCase(t1.getName());
                }
            }
        });
        adapter.notifyDataSetChanged();
    }


}
