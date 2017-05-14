package com.renegades.labs.partygo;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.parse.ParseException;

import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String mPhoneNumber;
    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_SMS},
                1);

        Button ladiesButton = (Button) findViewById(R.id.ladies_button);
        ladiesButton.setOnClickListener(this);
        Button mensButton = (Button) findViewById(R.id.mens_button);
        mensButton.setOnClickListener(this);
        Button birthdayButton = (Button) findViewById(R.id.birthday_button);
        birthdayButton.setOnClickListener(this);
        Button childBirthdayButton = (Button) findViewById(R.id.child_birthday_button);
        childBirthdayButton.setOnClickListener(this);
        Button weddingButton = (Button) findViewById(R.id.wedding_button);
        weddingButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
        switch (view.getId()) {
            case R.id.ladies_button:
                intent.putExtra("theme", "ladies");
                break;
            case R.id.mens_button:
                intent.putExtra("theme", "men");
                break;
            case R.id.birthday_button:
                intent.putExtra("theme", "birthday");
                break;
            case R.id.child_birthday_button:
                intent.putExtra("theme", "childBirthday");
                break;
            case R.id.wedding_button:
                intent.putExtra("theme", "wedding");
                break;
        }
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (prefs.getBoolean("firstrun", true)) {
            int permissionReadContacts = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_CONTACTS);
            int permissionReadSms = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_SMS);
            int permissionReadPhoneState = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_STATE);
            int granted = PackageManager.PERMISSION_GRANTED;

            if (permissionReadContacts == granted && permissionReadPhoneState == granted
                    && permissionReadSms == granted) {
                TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                mPhoneNumber = tMgr.getLine1Number();
                showRegisterDialog();
                prefs.edit().putBoolean("firstrun", false).apply();
            }
        }
    }

    public void showRegisterDialog() {
        FragmentManager fm = getFragmentManager();
        RegisterDialog registerDialog = new RegisterDialog();
        registerDialog.setmPhoneNumber(mPhoneNumber);
        registerDialog.show(fm, "dialog_register");
    }

    public void attemptSignIn(String phoneNumber) {
        ParseUser user = new ParseUser();

        final String userName = phoneNumber + "partygo";
        prefs.edit().putString("userName", userName).apply();

        ParsePush.subscribeInBackground(userName, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                    Log.d("Subscribe Parse", "Success");
                else
                    Log.d("Subscribe Parse", "Failed");
            }
        });

        user.setUsername(userName);
        user.setPassword(userName + "1");
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("Sign Up Parse", "Success");
                } else {
                    Log.d("Sign Up Parse", "Failed");
                }
            }
        });
    }


}
