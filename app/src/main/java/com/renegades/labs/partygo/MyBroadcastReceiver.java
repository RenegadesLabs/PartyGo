package com.renegades.labs.partygo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Виталик on 03.04.2017.
 */

public class MyBroadcastReceiver extends com.parse.ParsePushBroadcastReceiver {

    private static final String TAG = "MyBroadcastReceiver";
    private HashMap<String, String> dataMap;

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
        JSONObject json = null;
        try {
            json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Iterator itr = json.keys();
        dataMap = new HashMap<>();
        while (itr.hasNext()) {
            String key = (String) itr.next();
            Log.d(TAG, "key: " + key);
            try {
                String value = json.getString(key);
                Log.d(TAG, "value: " + value);
                dataMap.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (dataMap.containsKey("alert")) {
            Intent msgPushIntent = new Intent(context, IncomingMessageActivity.class);
            msgPushIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            msgPushIntent.putExtra("alert", dataMap.get("alert"));
            msgPushIntent.putExtra("theme", dataMap.get("theme"));
            msgPushIntent.putExtra("sender", dataMap.get("sender"));
            context.startActivity(msgPushIntent);
        }
    }
}
