package com.renegades.labs.partygo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.HashMap;

public class NotificationActivity extends AppCompatActivity {

    private EditText receiverEditText;
    private EditText notificationEditText;
    private View mProgressView;
    private View mSendFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mSendFormView = findViewById(R.id.send_form);
        mProgressView = findViewById(R.id.login_progress_2);

        receiverEditText = (EditText) findViewById(R.id.receiver);
        notificationEditText = (EditText) findViewById(R.id.notification);

        Button sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true);
                String receiverName = receiverEditText.getText().toString();
                String notificationMessage = notificationEditText.getText().toString();

                if (!receiverName.equals("") && !notificationMessage.equals("")) {
                    receiverName = receiverName + "partygo";
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("channel", receiverName);
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
