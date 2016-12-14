package com.renegades.labs.partygo;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Виталик on 03.12.2016.
 */

public class RegisterDialog extends DialogFragment {

    private String mPhoneNumber;

    public RegisterDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dialog_register, null);

        final EditText phoneEditText = (EditText) v.findViewById(R.id.phone_number);
        phoneEditText.setText(mPhoneNumber);

        Button registerButton = (Button) v.findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = phoneEditText.getText().toString();
                if (!phoneNumber.equals("")) {
                    ((MainActivity) getActivity()).attemptSignIn(phoneNumber);
                    dismiss();
                }
            }
        });
        return v;
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }
}
