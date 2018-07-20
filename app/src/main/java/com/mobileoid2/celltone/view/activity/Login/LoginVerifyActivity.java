package com.mobileoid2.celltone.view.activity.Login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.CelltoneApplication;
import com.mobileoid2.celltone.Util.OtpView;
import com.mobileoid2.celltone.network.APIClient;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.ApiInterface;
import com.mobileoid2.celltone.network.NetworkCallBack;
import com.mobileoid2.celltone.network.SendRequest;
import com.mobileoid2.celltone.network.jsonparsing.JsonResponse;
import com.mobileoid2.celltone.view.activity.PermissionsActivity;
import com.mobileoid2.celltone.celltoneDB.CellToneRoomDatabase;
import com.mobileoid2.celltone.pojo.PojoOTPRequest;
import com.mobileoid2.celltone.pojo.PojoOTPVerifyRequest;
import com.mobileoid2.celltone.pojo.audio.PojoGETALLMEDIA_Request;
import com.mobileoid2.celltone.pojo.otpverifiy.PojoOTPVerifyResponse;
import com.mobileoid2.celltone.utility.Config_URL;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
public class LoginVerifyActivity extends AppCompatActivity implements NetworkCallBack {


    public static final String TAG = LoginVerifyActivity.class.getSimpleName();
    private ApiInterface apiInterface;
    /*private EditTextEuro55Regular editTextOtp1, editTextOtp2, editTextOtp3, editTextOtp4;*/
    private ProgressDialog progressDialog;
    OtpView otpView;
    String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_verify);
        otpView = (OtpView) findViewById(R.id.otp_view);
        apiInterface = APIClient.getClient().create(ApiInterface.class);
        Intent intent = getIntent();
        mobile =(intent.getStringExtra("mobile"));
        String otp =(intent.getStringExtra("otp"));
        PojoOTPRequest pojoOTPRequest = new PojoOTPRequest();
        pojoOTPRequest.setMobile(intent.getStringExtra("mobile"));
        otpView.setOTP(otp);


    }

    public static final int PERMISSION_READ_STATE = 1;

    public void verifyOTP(View view) {


        if (otpView.hasValidOTP()) {
            otpView.getOTP();

            if (ContextCompat.checkSelfPermission(LoginVerifyActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // We do not have this permission. Let's ask the user
                ActivityCompat.requestPermissions(LoginVerifyActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_READ_STATE);
            } else {
                startOtPVerifiyRequest();


            }


        }



    }



    private void startOtPVerifiyRequest() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return ;
        }
        String imei = telephonyManager.getDeviceId();

        PojoOTPVerifyRequest pojoOTPVerifyRequest = new PojoOTPVerifyRequest();
        pojoOTPVerifyRequest.setMobile(mobile);
        pojoOTPVerifyRequest.setImei(imei);
        pojoOTPVerifyRequest.setOtp(Integer.parseInt(otpView.getOTP()));
        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Verify OTP...");
        progressDialog.show();
        SendRequest.sendRequest(ApiConstant.OTP_VERIFY_API,apiInterface.validateOtp(pojoOTPVerifyRequest),this);


// Adding request to request queue
        return ;
    }

    private void startPermissionActivity() {
        startActivity(new Intent(getApplicationContext(), PermissionsActivity.class));
        finish();
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_READ_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    // you may now do the action that requires this permission
                    startOtPVerifiyRequest();
                } else {
                    // permission denied
                    ActivityCompat.requestPermissions(LoginVerifyActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_READ_STATE);
                }
                return;
            }

        }
    }

    private void parseVerifyOTP(String response)

    {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(verifyResponse(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(parseOTP()));

    }

    private DisposableObserver<PojoOTPVerifyResponse> parseOTP() {
        return new DisposableObserver<PojoOTPVerifyResponse>() {

            @Override
            public void onNext(PojoOTPVerifyResponse pojoOTPVerifyResponse) {


                if (pojoOTPVerifyResponse.getStatus()==1000) {
                    SharedPrefrenceHandler.getInstance().setUSER_TOKEN(pojoOTPVerifyResponse.getBody().getToken());
                    SharedPrefrenceHandler.getInstance().setCOUTRYCODE(pojoOTPVerifyResponse.getBody().getUser().getCountryCode());
                    SharedPrefrenceHandler.getInstance().setName(pojoOTPVerifyResponse.getBody().getUser().getName());
                    SharedPrefrenceHandler.getInstance().setLoginState(true);
                    startPermissionActivity();
                    progressDialog.dismiss();
                } else {
                    Toast.makeText(LoginVerifyActivity.this,pojoOTPVerifyResponse.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
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



    private Observable<PojoOTPVerifyResponse> verifyResponse(String response) {
        Gson gsonObj = new Gson();
        final PojoOTPVerifyResponse planBody = gsonObj.fromJson(response, PojoOTPVerifyResponse.class);

        return Observable.create(new ObservableOnSubscribe<PojoOTPVerifyResponse>() {
            @Override
            public void subscribe(ObservableEmitter<PojoOTPVerifyResponse> emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    emitter.onNext(planBody);
                    emitter.onComplete();
                }


            }
        });
    }


    @Override
    public void getResponse(JsonResponse response, int type) {
        if (response.getObject() != null) {
            switch (type) {
                case ApiConstant.OTP_VERIFY_API:
                    parseVerifyOTP(response.getObject());
                    break;


            }
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, response.getErrorString(), Toast.LENGTH_LONG).show();
        }


    }
}

