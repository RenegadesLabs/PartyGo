package com.renegades.labs.partygo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;

/**
 * Created by Виталик on 03.12.2016.
 */

public class MainApplication extends Application {
    private static MainApplication instance = new MainApplication();
    public static final String APPLICATION_ID = "Uh9oKcyjfvjDMgdMmBYWNxXHHUE9LE2D1uM6FEPX";
    public static final String CLIENT_KEY = "Cn6erqjSz2GkhLMJvxEyj3QzSmD61TnLYfdrh3j8";
    public static final String BACK4APP_API = "https://parseapi.back4app.com/";

    public MainApplication() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(getContext())
                .applicationId(APPLICATION_ID)
                .clientKey(CLIENT_KEY)
                .server(BACK4APP_API)
                .build());

        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
        Log.d("Parse", "Initialized");

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "993743433763");
        installation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("push", "ok");
                } else {
                    Log.i("push", "nok");
                    e.printStackTrace();
                }
            }
        });

        ParsePush.subscribeInBackground("test_channel", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                    Log.d("Parse", "Success");
                else
                    Log.d("Parse", "Failed");
            }
        });
    }
}
