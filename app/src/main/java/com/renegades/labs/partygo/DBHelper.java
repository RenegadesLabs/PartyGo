package com.renegades.labs.partygo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Виталик on 02.04.2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBHelper";

    public DBHelper(Context context) {
        super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "--- onCreate database ---");
        db.execSQL("create table contacts ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "phone text,"
                + "priority integer" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
