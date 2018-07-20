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

import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.CelltoneApplication;
import com.mobileoid2.celltone.Util.Constant;
import com.mobileoid2.celltone.network.APIClient;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.ApiInterface;
import com.mobileoid2.celltone.network.NetworkCallBack;
import com.mobileoid2.celltone.network.SendRequest;
import com.mobileoid2.celltone.network.jsonparsing.JsonResponse;
import com.mobileoid2.celltone.network.model.profile.ProfileModel;
import com.mobileoid2.celltone.network.model.profile.ProfilePlanDetail;
import com.mobileoid2.celltone.network.model.profile.UserProfile;
import com.mobileoid2.celltone.pojo.CoutryCode;
import com.mobileoid2.celltone.pojo.CoutryPojo;
import com.mobileoid2.celltone.pojo.PojoLogin;
import com.mobileoid2.celltone.pojo.PojoOTPRequest;
import com.mobileoid2.celltone.pojo.PojoOTPResponse;
import com.mobileoid2.celltone.pojo.loginresponse.PojoLoginResponse;
import com.mobileoid2.celltone.utility.Config_URL;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;
import com.mobileoid2.celltone.utility.Utils;

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
public class LoginOtpActivity extends AppCompatActivity implements NetworkCallBack {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private int isRegister = 0;
    private ApiInterface apiInterface;
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
        if(isRegister==0) {
            isRegister = 1;
            txtInputName.setVisibility(View.VISIBLE);
            txtRegister.setText(getString(R.string.alread_registered));


        }
        else
        {
            isRegister = 0;
            txtInputName.setVisibility(View.GONE);
            txtRegister.setText(getString(R.string.register));


        }
    }


    private static final String EMAIL = "email";
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private  LoginManager fbLoginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);
        fbLoginManager =LoginManager.getInstance();
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(ApiInterface.class);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        countryCodeValue = tm.getNetworkCountryIso();
        parseCoutry();
        CallbackManager callbackManager = CallbackManager.Factory.create();
        fbLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // here write code When Login successfully
              //  String mobile = loginResult.getAccessToken();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                // here write code when get error
            }
        });


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
        fbLoginManager.logInWithReadPermissions(LoginOtpActivity.this, Arrays.asList("public_profile", "user_friends","user_mobile_phone"));


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

//        if(number.substring(0, 1).equals("0"))
//            number = number.substring(1);
        PojoLogin pojoLoginGet = new PojoLogin();
        pojoLoginGet.setName(name);
        pojoLoginGet.setMobile(coutryCode + removeZero(number));
        pojoLoginGet.setCode(coutryCode);
        SendRequest.sendRequest(ApiConstant.REGISTER_API, apiInterface.register(pojoLoginGet), this);
    }


    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(String number, String otp) {
        _loginButton.setEnabled(true);

        Intent intent = new Intent(getApplicationContext(), LoginVerifyActivity.class);
        intent.putExtra("mobile", number);
        intent.putExtra("otp", otp);
        startActivity(intent);
        LoginOtpActivity.this.finish();
    }

    private void parseLoginOTP(String response)

    {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(getLoginRespnse(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(parseProfile()));

    }

    private void parseResgisterResposne(String response) {
        //
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(registerResponse(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(parseResgister(response)));

    }

    private Observable<PojoLoginResponse> registerResponse(String response) {
        Gson gsonObj = new Gson();
        final PojoLoginResponse planBody = gsonObj.fromJson(response, PojoLoginResponse.class);

        return Observable.create(new ObservableOnSubscribe<PojoLoginResponse>() {
            @Override
            public void subscribe(ObservableEmitter<PojoLoginResponse> emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    emitter.onNext(planBody);
                    emitter.onComplete();
                }


            }
        });
    }

    private DisposableObserver<PojoLoginResponse> parseResgister(String response) {
        return new DisposableObserver<PojoLoginResponse>() {

            @Override
            public void onNext(PojoLoginResponse pojoLoginResponse) {


                if (pojoLoginResponse.getStatus() == 1000) {
                    SharedPrefrenceHandler.getInstance().setLOGIN_RESPONSE(response);
                    requestTogetOTP();
                    //   onLoginSuccess(number);
                } else {
                    onLoginFailed(pojoLoginResponse.getMessage());
                }


            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }


    private DisposableObserver<PojoOTPResponse> parseProfile() {
        return new DisposableObserver<PojoOTPResponse>() {

            @Override
            public void onNext(PojoOTPResponse pojoOTPResponse) {
                if (pojoOTPResponse.getStatus() == 1000) {
                    onLoginSuccess(mobile, pojoOTPResponse.getBody());
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginOtpActivity.this, pojoOTPResponse.getMessage(), Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }


    private Observable<PojoOTPResponse> getLoginRespnse(String response) {
        Gson gsonObj = new Gson();
        final PojoOTPResponse planBody = gsonObj.fromJson(response, PojoOTPResponse.class);

        return Observable.create(new ObservableOnSubscribe<PojoOTPResponse>() {
            @Override
            public void subscribe(ObservableEmitter<PojoOTPResponse> emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    emitter.onNext(planBody);
                    emitter.onComplete();
                }


            }
        });
    }

    private String removeZero(String number)
    {
        if(number.substring(0,1).equals("0"))
            number = number.substring(1);
        return number;

    }


    private void requestTogetOTP() {

        PojoOTPRequest pojoOTPRequest = new PojoOTPRequest();
        mobile = removeZero(_numberText.getText().toString());
        pojoOTPRequest.setMobile(coutryCode + mobile);
        mobile = pojoOTPRequest.getMobile();
        SendRequest.sendRequest(ApiConstant.LOGIN_OTP_API, apiInterface.getOtp(pojoOTPRequest), this);


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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void getResponse(JsonResponse response, int type) {
        if (response.getObject() != null) {
            switch (type) {
                case ApiConstant.LOGIN_OTP_API:
                    parseLoginOTP(response.getObject());
                    break;
                case ApiConstant.REGISTER_API:
                    parseResgisterResposne(response.getObject());
                    break;


            }
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, response.getErrorString(), Toast.LENGTH_LONG).show();
        }


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

