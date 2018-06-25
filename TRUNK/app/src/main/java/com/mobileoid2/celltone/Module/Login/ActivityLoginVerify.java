package com.mobileoid2.celltone.view.activity.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;

import com.mobileoid2.celltone.CustomWidget.EditTextView.EditTextEuro55Regular;
import com.mobileoid2.celltone.Module.Base.ActivityBase;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.AppSharedPref;
import com.mobileoid2.celltone.Util.AppUtils;
import com.mobileoid2.celltone.Util.PermissionsMarshmallow;

/**
 * A login screen that offers login via email/password.
 */
public class ActivityLoginVerify extends AppCompatActivity {


    private EditTextEuro55Regular editTextOtp1, editTextOtp2, editTextOtp3, editTextOtp4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_verify);

        editTextOtp1 = findViewById(R.id.edittext_otp_1);
        editTextOtp2 = findViewById(R.id.edittext_otp_2);
        editTextOtp3 = findViewById(R.id.edittext_otp_3);
        editTextOtp4 = findViewById(R.id.edittext_otp_4);

        findViewById(R.id.edittext_get_otp).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), ActivityBase.class));
                finish();


            }
        });

        editTextOtp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    editTextOtp2.requestFocus();
                }
            }
        });

        editTextOtp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    editTextOtp3.requestFocus();
                }
            }
        });

        editTextOtp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    editTextOtp4.requestFocus();
                }
            }
        });

        editTextOtp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    AppUtils.instance.hideKeyboard(ActivityLoginVerify.this);
                }
            }
        });

    }

}

