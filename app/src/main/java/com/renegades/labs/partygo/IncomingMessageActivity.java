package com.renegades.labs.partygo;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.HashMap;

public class IncomingMessageActivity extends AppCompatActivity {
    static final String TAG = "IncomingMessageActivity";
    private String phoneNo;
    private EditText replyEditText;
    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_message);

        prefs = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        Bundle extras = getIntent().getExtras();
        String message;
        if (extras != null) {
            message = extras.getString("alert");
            phoneNo = extras.getString("sender");
            final String theme = extras.getString("theme");
            Log.d(TAG, "theme = " + theme);
            if (theme != null) {
                TextView themeTextView = (TextView) findViewById(R.id.theme_text_view);
                ImageView backgroundImage = (ImageView)
                        findViewById(R.id.background_image_incoming);
                switch (theme) {
                    case "men":
                        backgroundImage.setImageResource(R.drawable.men);
                        themeTextView.setText(getString(R.string.mens_day));
                        break;
                    case "ladies":
                        backgroundImage.setImageResource(R.drawable.lady);
                        themeTextView.setText(getString(R.string.ladies_day));
                        break;
                    case "birthday":
                        backgroundImage.setImageResource(R.drawable.birthday);
                        themeTextView.setText(getString(R.string.birthday));
                        break;
                    case "childBirthday":
                        backgroundImage.setImageResource(R.drawable.child_birthday);
                        themeTextView.setText(getString(R.string.child_birthday));
                        break;
                    case "wedding":
                        backgroundImage.setImageResource(R.drawable.wedding);
                        themeTextView.setText(getString(R.string.wedding));
                        break;
                }
            }

            String senderName = getContactDisplayNameByNumber(phoneNo.replaceAll("partygo", ""));
            getSupportActionBar().setTitle(senderName);

            final TextView textView = (TextView) findViewById(R.id.incomingMessageTextView);
            textView.setText(message);

            replyEditText = (EditText) findViewById(R.id.reply_edit_text);
            ImageButton sendButton = (ImageButton) findViewById(R.id.button_send);
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    String replyMessage = replyEditText.getText().toString();
                    if (!replyMessage.equals("")) {
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("channel", phoneNo);
                        Log.d(TAG, "channel = " + phoneNo);
                        params.put("sender", prefs.getString("userName", ""));
                        Log.d(TAG, "sender = " + prefs.getString("userName", ""));
                        params.put("message", replyMessage);
                        params.put("theme", theme);
                        ParseCloud.callFunctionInBackground("push", params, new FunctionCallback<String>() {
                            public void done(String success, ParseException e) {
                                if (e == null) {
                                    Log.d(TAG, "message sent");
                                    Toast.makeText(IncomingMessageActivity.this,
                                            getString(R.string.message_sent),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    e.printStackTrace();
                                    Log.d(TAG, "message failed");
                                    Toast.makeText(IncomingMessageActivity.this,
                                            getString(R.string.message_failed),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        TextView reply = new TextView(IncomingMessageActivity.this);
                        LinearLayout parent = (LinearLayout) findViewById(R.id.parent_linear_layout);
                        reply.setText(replyMessage);

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(32, 16, 0, 0);
                        reply.setLayoutParams(layoutParams);
                        reply.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        reply.setTextColor(ContextCompat.getColor(IncomingMessageActivity.this,
                                R.color.colorAccent));

                        parent.addView(reply);
                        replyEditText.setText("");
                    }
                }
            });
        }
    }

    public String getContactDisplayNameByNumber(String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String name = "?";

        ContentResolver contentResolver = getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[]{
                ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(0);
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        if (name.equals("?")){
            name = number.replaceAll("partygo", "");
        }
        return name;
    }
}