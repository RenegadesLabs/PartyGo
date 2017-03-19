package com.renegades.labs.partygo;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private String mPhoneNumber;
    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("com.renegades.labs.partygo", MODE_PRIVATE);

        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneNumber = tMgr.getLine1Number();

        String toast = String.format(Locale.ENGLISH, "Phone Number is %s", mPhoneNumber);
        Toast.makeText(this, toast, Toast.LENGTH_LONG).show();

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
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (prefs.getBoolean("firstrun", true)) {
            showRegisterDialog();
            prefs.edit().putBoolean("firstrun", false).apply();
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
        String toast = String.format(Locale.ENGLISH, "Phone Number is %s", phoneNumber);
        Toast.makeText(this, toast, Toast.LENGTH_LONG).show();

        final String userName = phoneNumber + "partygo";

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
