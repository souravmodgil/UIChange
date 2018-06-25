package com.mobileoid2.celltone.view.activity.Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.CelltoneApplication;
import com.mobileoid2.celltone.Util.Constant;
import com.mobileoid2.celltone.pojo.CoutryCode;
import com.mobileoid2.celltone.pojo.CoutryPojo;
import com.mobileoid2.celltone.pojo.PojoLogin;
import com.mobileoid2.celltone.pojo.PojoOTPRequest;
import com.mobileoid2.celltone.pojo.PojoOTPResponse;
import com.mobileoid2.celltone.pojo.loginresponse.PojoLoginResponse;
import com.mobileoid2.celltone.utility.Config_URL;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * A login screen that offers login via email/password.
 */
public class LoginOtpActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private int isRegister = 0;
    private String mobile = "";
    private String coutryCode = "";
    private String countryCodeValue;
    private ProgressDialog progressDialog;
    @BindView(R.id.input_name)
    EditText _nameText;
    @BindView(R.id.txt_input_name)
    TextInputLayout txtInputName;
    @BindView(R.id.input_number)
    EditText _numberText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.ms_country_code)
    Spinner msCountryCode;
    @BindView(R.id.txt_register)
    TextView txtRegister;

    @OnClick(R.id.txt_register)
    public void register() {
        isRegister = 1;
        txtInputName.setVisibility(View.VISIBLE);
    }


    private static final String EMAIL = "email";
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);
        ButterKnife.bind(this);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        countryCodeValue = tm.getNetworkCountryIso();
        parseCoutry();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                requestUserProfile(loginResult);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });

    }

    @OnClick(R.id.googleplus)
    public void googleplus(View view) {

    }

    @OnClick(R.id.facebook)
    public void facebook(View view) {

    }

    @OnClick(R.id.btn_login)
    public void login(View view) {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed("Login failed");
            return;
        }


    //    _loginButton.setEnabled(false);

        progressDialog = new ProgressDialog(LoginOtpActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String number = _numberText.getText().toString();

      /*  // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(new Runnable() {
            public void run() {
                // On complete call either onLoginSuccess or onLoginFailed
                onLoginSuccess();
                // onLoginFailed();
                progressDialog.dismiss();
            }
        }, 1000);*/
        if (isRegister == 1)
            startLoginRequest(progressDialog, name, number);
        else
            requestTogetOTP();


    }

    private void parseCoutry() {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(getCoutryList(Constant.CoutryCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getCoutryObserver()));
    }

    private DisposableObserver<CoutryPojo> getCoutryObserver() {
        return new DisposableObserver<CoutryPojo>() {

            @Override
            public void onNext(CoutryPojo coutryPojo) {
                List<CoutryCode> coutryCodeList = coutryPojo.getCoutryCode();
                Collections.sort(coutryCodeList, new NameComparator());
                ArrayAdapter<CoutryCode> spinnerArrayAdapter = new ArrayAdapter<CoutryCode>
                        (LoginOtpActivity.this, android.R.layout.simple_spinner_item,
                                coutryCodeList); //selected item will look like a spinner set from XML
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                        .simple_spinner_dropdown_item);
                msCountryCode.setAdapter(spinnerArrayAdapter);
                View v = msCountryCode.getSelectedView();

                msCountryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        CoutryCode coutryCodemodel = (CoutryCode) parentView.getItemAtPosition(position);
                        coutryCode = coutryCodemodel.getDialCode();
                        ((TextView) selectedItemView).setTextColor(getResources().getColor(R.color.white));


                        // your code here
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });
                CoutryCode coutryCode = new CoutryCode(countryCodeValue);
                setCoutryCode(spinnerArrayAdapter, coutryCode);


            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }


    private Observable<CoutryPojo> getCoutryList(String response) {
        Gson gsonObj = new Gson();
        final CoutryPojo planBody = gsonObj.fromJson(response, CoutryPojo.class);

        return Observable.create(new ObservableOnSubscribe<CoutryPojo>() {
            @Override
            public void subscribe(ObservableEmitter<CoutryPojo> emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    emitter.onNext(planBody);
                    emitter.onComplete();
                }


            }
        });
    }

    public void setCoutryCode(ArrayAdapter<CoutryCode> dataAdapter, CoutryCode coutryCode) {

        if (dataAdapter.getPosition(coutryCode) != -1) {
            int index = dataAdapter.getPosition(coutryCode);
            if (index > -1) msCountryCode.setSelection(index);


        }

    }


    private void startLoginRequest(ProgressDialog progressDialog, String name, String number) {
        PojoLogin pojoLoginGet = new PojoLogin();
        pojoLoginGet.setName(name);
        pojoLoginGet.setMobile(coutryCode+number);
        pojoLoginGet.setCode(coutryCode);


        JSONObject params = null;
        try {
            Gson gson = new Gson();
            String jsoon = gson.toJson(pojoLoginGet);
            params = new JSONObject(jsoon);
        } catch (Exception e) {
        }
        if (params == null) return;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Config_URL.URL_REGISTER, params, response -> {
            try {
                System.out.println("LoginOtpActivity.onResponse" + response);
                PojoLoginResponse pojoLoginResponse = Arrays.asList(new Gson().fromJson(response.toString(), PojoLoginResponse.class)).get(0);


                if (pojoLoginResponse.getStatus() == 1000) {
                    SharedPrefrenceHandler.getInstance().setLOGIN_RESPONSE(response.toString());
                    requestTogetOTP();
                    //   onLoginSuccess(number);
                }
                 else {
                    onLoginFailed(pojoLoginResponse.getMessage());
                }

            } catch (Exception e) {
                e.printStackTrace();
                _loginButton.setEnabled(true);
            }


            progressDialog.dismiss();
        }, error -> {
            VolleyLog.d(TAG, "Error: " + error.getMessage());
            // hide the progress dialog
            progressDialog.dismiss();
            _loginButton.setEnabled(true);
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

// Adding request to request queue
        CelltoneApplication.getInstance().addToRequestQueue(jsonObjReq, Config_URL.tag_json_obj);
    }


    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(String number,String otp) {
        _loginButton.setEnabled(true);

        Intent intent = new Intent(getApplicationContext(), LoginVerifyActivity.class);
        intent.putExtra("mobile", number);
        intent.putExtra("otp", otp);
        startActivity(intent);
        LoginOtpActivity.this.finish();
    }

    private void requestTogetOTP() {
        PojoOTPRequest pojoOTPRequest = new PojoOTPRequest();
        pojoOTPRequest.setMobile(coutryCode + _numberText.getText().toString());

        mobile = pojoOTPRequest.getMobile();
        JSONObject params = null;
        try {
            Gson gson = new Gson();
            String jsoon = gson.toJson(pojoOTPRequest);
            params = new JSONObject(jsoon);
        } catch (Exception e) {
        }
        if (params == null) return;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Config_URL.URL_OTP, params, response -> {
            try {
                System.out.println("LoginOtpActivity.onResponse" + response);
                PojoOTPResponse pojoLoginResponse = Arrays.asList(new Gson().fromJson(response.toString(), PojoOTPResponse.class)).get(0);


                if (pojoLoginResponse.getStatus() == 1000) {
                    onLoginSuccess(mobile,pojoLoginResponse.getBody());
                    // otpView.setOTP(pojoLoginResponse.getBody());
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginOtpActivity.this, pojoLoginResponse.getMessage(), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }, error -> {
            VolleyLog.d(TAG, "Error: " + error.getMessage());
            // hide the progress dialog
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

// Adding request to request queue
        CelltoneApplication.getInstance().addToRequestQueue(jsonObjReq, Config_URL.tag_json_obj);
    }


    public void onLoginFailed(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String mobile = _numberText.getText().toString();

        if (name.isEmpty() && isRegister == 1) {
            _nameText.setError("enter a valid name");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (mobile.isEmpty() || (mobile.length() < 9 || mobile.length() > 13)) {
            _numberText.setError(getString(R.string.mobile_validation_message));
            valid = false;
        } else {
            _numberText.setError(null);
        }
        if (coutryCode.isEmpty()) {
            _numberText.setError(getString(R.string.coutry_code_validation_message));
            valid = false;

        }

        return valid;
    }


    private void goMainScreen() {
        /*Intent intent = new Intent(this, LoginVerifyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    class NameComparator implements Comparator<CoutryCode> {

        @Override
        public int compare(CoutryCode c1, CoutryCode c2) {
            return c1.getName().compareTo(c2.getName()
            );
        }
    }


    public void requestUserProfile(LoginResult loginResult) {
        GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject me, GraphResponse response) {
                if (response.getError() != null) {
                    // handle error
                } else {
                    try {
                        System.out.println("LoginOtpActivity.onCompleted" + me.toString());
                        String email = response.getJSONObject().get("email").toString();
                        Log.e("Result", email);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String id = me.optString("id");
                    // send email and id to your web server
                    Log.e("Result1", response.getRawResponse());
                    Log.e("Result", me.toString());
                }
            }
        }).executeAsync();
    }

}

