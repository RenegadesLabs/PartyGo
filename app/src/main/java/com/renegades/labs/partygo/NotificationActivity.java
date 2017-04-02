package com.renegades.labs.partygo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationActivity extends AppCompatActivity {

    private EditText notificationEditText;
    private View mProgressView;
    private View mSendFormView;
    private ListView contactsListView;
    ArrayList<String> phonesList;
    ArrayList<String> recipientsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mSendFormView = findViewById(R.id.send_form);
        mProgressView = findViewById(R.id.login_progress_2);
        contactsListView = (ListView) findViewById(R.id.recipients_list);
        notificationEditText = (EditText) findViewById(R.id.notification);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            phonesList = extras.getStringArrayList("phones");
            recipientsList = extras.getStringArrayList("recipients");
            String theme = extras.getString("theme");
            if (!theme.equals("")) {
                ImageView backgroundImage = (ImageView) findViewById(
                        R.id.background_image_notification);
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
            }
        } else {
            phonesList = new ArrayList<>();
            recipientsList = new ArrayList<>();
        }

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_list_item, recipientsList);

        contactsListView.setAdapter(itemsAdapter);

        Button sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true);
                String notificationMessage = notificationEditText.getText().toString();

                for (int i = 0; i < phonesList.size(); i++) {
                    String phoneNo = phonesList.get(i);

                    if (!phoneNo.equals("") && !notificationMessage.equals("")) {
                        phoneNo = phoneNo + "partygo";
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("channel", phoneNo);
                        params.put("message", notificationMessage);
                        ParseCloud.callFunctionInBackground("push", params, new FunctionCallback<String>() {
                            public void done(String success, ParseException e) {
                                if (e == null) {
                                    showProgress(false);
                                    Toast.makeText(NotificationActivity.this,
                                            "Notification sent", Toast.LENGTH_LONG).show();
                                } else {
                                    showProgress(false);
                                    e.printStackTrace();
                                    Toast.makeText(NotificationActivity.this,
                                            "Notification FAILED", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        showProgress(false);
                    }
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSendFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSendFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSendFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSendFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
