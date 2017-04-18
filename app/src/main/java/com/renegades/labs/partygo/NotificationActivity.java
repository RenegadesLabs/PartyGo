package com.renegades.labs.partygo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationActivity extends AppCompatActivity {

    static final String TAG = "NotificationActivity";
    private EditText notificationEditText;
    private View mProgressView;
    private View mSendFormView;
    private ListView contactsListView;
    ArrayList<String> phonesList;
    ArrayList<String> recipientsList;
    String theme;
    SharedPreferences prefs = null;
    FrameLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mSendFormView = findViewById(R.id.send_form);
        mProgressView = findViewById(R.id.login_progress_2);
        contactsListView = (ListView) findViewById(R.id.recipients_list);
        notificationEditText = (EditText) findViewById(R.id.notification);
        rootLayout = (FrameLayout) findViewById(R.id.root_notification);

        prefs = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            phonesList = extras.getStringArrayList("phones");
            recipientsList = extras.getStringArrayList("recipients");
            theme = extras.getString("theme");
            if (theme != null) {
                ImageView backgroundImage = (ImageView) findViewById(
                        R.id.background_image_notification);
                switch (theme) {
                    case "men":
                        backgroundImage.setImageResource(R.drawable.men);
                        break;
                    case "ladies":
                        backgroundImage.setImageResource(R.drawable.lady);
                        break;
                    case "birthday":
                        backgroundImage.setImageResource(R.drawable.birthday);
                        break;
                    case "childBirthday":
                        backgroundImage.setImageResource(R.drawable.child_birthday);
                        break;
                    case "wedding":
                        backgroundImage.setImageResource(R.drawable.wedding);
                        break;
                }
            }
        } else {
            phonesList = new ArrayList<>();
            recipientsList = new ArrayList<>();
        }

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_list_item, recipientsList);

        contactsListView.setAdapter(itemsAdapter);

        ImageButton sendButton = (ImageButton) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                showProgress(true);
                String notificationMessage = notificationEditText.getText().toString();

                for (int i = 0; i < phonesList.size(); i++) {
                    String phoneNo = phonesList.get(i);

                    if (!phoneNo.equals("") && !notificationMessage.equals("")) {
                        phoneNo = phoneNo + "partygo";
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("channel", phoneNo);
                        params.put("sender", prefs.getString("userName", ""));
                        params.put("message", notificationMessage);
                        params.put("theme", theme);
                        ParseCloud.callFunctionInBackground("push", params, new FunctionCallback<String>() {
                            public void done(String success, ParseException e) {
                                if (e == null) {
                                    showProgress(false);
                                    Toast.makeText(NotificationActivity.this,
                                            getString(R.string.message_sent),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    e.printStackTrace();
                                    showProgress(false);
                                    Toast.makeText(NotificationActivity.this,
                                            getString(R.string.message_failed),
                                            Toast.LENGTH_SHORT).show();
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

    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mSendFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
