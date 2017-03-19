package com.renegades.labs.partygo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
        } else {
            phonesList = new ArrayList<>();
            recipientsList = new ArrayList<>();
        }

        contactsListView.setAdapter(new BaseAdapter() {
            class ViewHolder {
                TextView name;
            }

            @Override
            public int getCount() {
                return recipientsList.size();
            }

            @Override
            public Object getItem(int i) {
                return recipientsList.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                ViewHolder viewHolder;

                if (view == null) {
                    LayoutInflater inflater = (LayoutInflater) NotificationActivity.this
                            .getSystemService(LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.simple_list_item, viewGroup, false);
                    viewHolder = new ViewHolder();
                    viewHolder.name = (TextView) view.findViewById(R.id.simple_name);
                    view.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) view.getTag();
                }

                viewHolder.name.setText((String) getItem(i));
                return view;
            }
        });


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
