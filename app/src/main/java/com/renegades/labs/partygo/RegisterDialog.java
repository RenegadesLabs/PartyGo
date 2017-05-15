package com.renegades.labs.partygo;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

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

        View v = inflater.inflate(R.layout.dialog_register, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        final EditText phoneEditText = (EditText) v.findViewById(R.id.phone_number);
        if (mPhoneNumber != null) {
            if (mPhoneNumber.equals("")) {
                phoneEditText.setText(getCountryZipCode());
            } else {
                phoneEditText.setText(mPhoneNumber);
            }
        } else {
            phoneEditText.setText(getCountryZipCode());
        }
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

    @Override
    public void onResume() {
        super.onResume();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        getDialog().getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public String getCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = "";

        TelephonyManager manager =
                (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = getActivity().getResources().getStringArray(R.array.CountryCodes);
        for (String aRl : rl) {
            String[] g = aRl.split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }
}
