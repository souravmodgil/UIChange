package com.mobileoid2.celltone.view.activity.Login;

import android.Manifest;
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

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.CelltoneApplication;
import com.mobileoid2.celltone.Util.OtpView;
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

/**
 * A login screen that offers login via email/password.
 */
public class LoginVerifyActivity extends AppCompatActivity {


    public static final String TAG = LoginVerifyActivity.class.getSimpleName();
    /*private EditTextEuro55Regular editTextOtp1, editTextOtp2, editTextOtp3, editTextOtp4;*/

    OtpView otpView;
    String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_verify);
        otpView = (OtpView) findViewById(R.id.otp_view);
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

        JSONObject params = null;
        try {
            Gson gson = new Gson();
            String jsoon = gson.toJson(pojoOTPVerifyRequest);
            params = new JSONObject(jsoon);
        } catch (Exception e) {
        }
        if (params == null) return;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, Config_URL.URL_VALIDATE_OTP, params, response -> {
            try {
                PojoOTPVerifyResponse pojoOTPVerifyResponse = Arrays.asList(new Gson().fromJson(response.toString(), PojoOTPVerifyResponse.class)).get(0);
                if (pojoOTPVerifyResponse.getStatus()==1000) {
                    SharedPrefrenceHandler.getInstance().setUSER_TOKEN(pojoOTPVerifyResponse.getBody().getToken());
                    SharedPrefrenceHandler.getInstance().setCOUTRYCODE(pojoOTPVerifyResponse.getBody().getUser().getCountryCode());
                    SharedPrefrenceHandler.getInstance().setLoginState(true);
                    startPermissionActivity();
                } else {
                    Toast.makeText(this,pojoOTPVerifyResponse.getMessage(),Toast.LENGTH_SHORT).show();
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
        return ;
    }

    private void startPermissionActivity() {
        getALLAUDIO();
        getALLVIDEO();
        startActivity(new Intent(getApplicationContext(), PermissionsActivity.class));
        finish();
    }


    private void getALLAUDIO() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Config_URL.URL_GET_AUDIO, null, response -> {
            try {
                System.out.println("HomeActivity.refreshMediaSetByOther" + response.toString());


                PojoGETALLMEDIA_Request pojoContactsUploadResonse = Arrays.asList(new Gson().fromJson(response.toString(), PojoGETALLMEDIA_Request.class)).get(0);
                if (pojoContactsUploadResonse.getStatus() == 1000) {

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            /*for (int i = 0; i < 10; i++) {
                                pojoContactsUploadResonse.getBody().addAll(pojoContactsUploadResonse.getBody());
                            }*/
                            CellToneRoomDatabase.getDatabase(getApplicationContext()).get_pojoALLMediaDAO().insertList(pojoContactsUploadResonse.getBody());
                            return null;
                        }
                    }.execute();
                    SharedPrefrenceHandler.getInstance().setGET_ALL_AUDIO(true);
                } else {
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }, error -> {
            VolleyLog.d(TAG, "Error: " + error.getMessage());
            // hide the progress dialog
        }) {

        /*@Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("token", SharedPrefrenceHandler.getInstance().getUSER_TOKEN());
            return headers;
        }*/

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                //headers.put("token", SharedPrefrenceHandler.getInstance().getUSER_TOKEN());
                return headers;
            }
        };
        CelltoneApplication.getInstance().addToRequestQueue(jsonObjReq, Config_URL.tag_json_obj);
    }

    private void getALLVIDEO() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Config_URL.URL_GET_VIDEO, null, response -> {
            try {
                System.out.println("HomeActivity.refreshMediaSetByOther" + response.toString());


                PojoGETALLMEDIA_Request pojoContactsUploadResonse = Arrays.asList(new Gson().fromJson(response.toString(), PojoGETALLMEDIA_Request.class)).get(0);
                if (pojoContactsUploadResonse.getStatus() == 1000) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            CellToneRoomDatabase.getDatabase(getApplicationContext()).get_pojoALLMediaDAO().insertList(pojoContactsUploadResonse.getBody());
                            return null;
                        }
                    }.execute();
                    SharedPrefrenceHandler.getInstance().setGET_ALL_VIDEO(true);
                } else {
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }, error -> {
            VolleyLog.d(TAG, "Error: " + error.getMessage());
            // hide the progress dialog
        }) {

        /*@Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("token", SharedPrefrenceHandler.getInstance().getUSER_TOKEN());
            return headers;
        }*/

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                //headers.put("token", SharedPrefrenceHandler.getInstance().getUSER_TOKEN());
                return headers;
            }
        };
        CelltoneApplication.getInstance().addToRequestQueue(jsonObjReq, Config_URL.tag_json_obj);
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


}

