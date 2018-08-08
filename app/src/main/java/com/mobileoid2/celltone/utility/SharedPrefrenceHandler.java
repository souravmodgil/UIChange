package com.mobileoid2.celltone.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.mobileoid2.celltone.Util.CelltoneApplication;

public class SharedPrefrenceHandler {


    private static SharedPrefrenceHandler sInstance;

    private static String Shared_Preference_name = "etisalat";
    private static SharedPreferences pSharedPref;

    private SharedPrefrenceHandler() {
    }

    public static synchronized SharedPrefrenceHandler getInstance() {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (sInstance == null) {
            sInstance = new SharedPrefrenceHandler();
        }

        pSharedPref = CelltoneApplication.getAppContext().getSharedPreferences(Shared_Preference_name, Context.MODE_PRIVATE);

        return sInstance;
    }


    public void clearData() {
        pSharedPref.edit().clear().apply();
    }

    private String USER_LOGIN_STATE = "USER_LOGIN_STATE";
    private String USER_LOGIN_RESPONSE = "USER_LOGIN_RESPONSE";
    private String USER_TOKEN = "USER_TOKEN";
    private String FCP_ID = "fcp_id";
    private String ContactsUploaded = "ContactsUploaded";
    private String ContactsUploadedResponse = "ContactsUploadedResponse";
    private String GET_MEDIA_RESPONSE = "GET_MEDIA_RESPONSE";
    private String AUDIORESPOSNE = "audio_response";
    private String BANNERRESPONSE = "banner_response";
    private String VIDEORESPONSE = "video_resposne";
    private String COUTRYCODE = "coutry_code";
    private String IS_FIRST_TIME_ON_HOME ="isFirstTimeHome";
    private String IS_FIRST_TIME_ON_CONTACT ="fisrtTimeContact";
    private String NAME = "name";
    private String  FCMIDSET = "fcm_id_set";
    private String GET_ALL_AUDIO = "GET_ALL_AUDIOS";
    private String GET_ALL_VIDEO = "GET_ALL_VIDEOS";
    private String LANGUAGE_CODE = "language_code";
    private String IS_FIRST_TIME_ON_SET_SONG ="firstTimeSongSet";
    private String IS_FIRST_TIME_ON_QUERY ="firstTimeQuery";



    /////////////Save data here

    /* ContactsUploaded*/
    public boolean getContactsUploaded() {
        return pSharedPref.getBoolean(ContactsUploaded, false);

    }
    public void setContactsUploaded(boolean androidID) {
        pSharedPref.edit().putBoolean(ContactsUploaded, androidID).apply();
    }

    /*FirstTime APP Install*/
    public int getIsFirstTime() {
        return pSharedPref.getInt(IS_FIRST_TIME_ON_HOME, 0);

    }
    public void setISFirstTimeHome(int isFirstTime) {
        pSharedPref.edit().putInt(IS_FIRST_TIME_ON_HOME, isFirstTime).apply();
    }

    /*FirstTime APP Install*/
    public int getIsFirstTimeSetSong() {
        return pSharedPref.getInt(IS_FIRST_TIME_ON_SET_SONG, 0);

    }

    public void setIsFirstTimeSetSong(int isFirstTime) {
        pSharedPref.edit().putInt(IS_FIRST_TIME_ON_SET_SONG, isFirstTime).apply();
    }


    public void setIsFirstTimeContact(int isFirstTime) {
        pSharedPref.edit().putInt(IS_FIRST_TIME_ON_CONTACT, isFirstTime).apply();
    }

    /*FirstTime APP Install*/
    public int getIsFirstTimeContact() {
        return pSharedPref.getInt(IS_FIRST_TIME_ON_CONTACT, 0);

    }


    public void setIsFirstTimeQuery(int isFirstTime) {
        pSharedPref.edit().putInt(IS_FIRST_TIME_ON_QUERY, isFirstTime).apply();
    }

    /*FirstTime APP Install*/
    public int getIsFirstTimeQuery() {
        return pSharedPref.getInt(IS_FIRST_TIME_ON_QUERY, 0);

    }






    /*USER_LOGIN_STATE*/
    public boolean getLoginState() {
        return pSharedPref.getBoolean(USER_LOGIN_STATE, false);

    }
    public void setLoginState(boolean androidID) {
        pSharedPref.edit().putBoolean(USER_LOGIN_STATE, androidID).apply();
    }

    /* UserNameJID*/
    public String getLOGIN_RESPONSE() {
        return pSharedPref.getString(USER_LOGIN_RESPONSE, null);

    }

    public void setLOGIN_RESPONSE(String response) {
        pSharedPref.edit().putString(USER_LOGIN_RESPONSE, response).apply();
    }

    public String getContactsUploadedResponse() {
        return pSharedPref.getString(ContactsUploadedResponse, null);

    }

    public void setContactsUploadedResponse(String response) {
        pSharedPref.edit().putString(ContactsUploadedResponse, response).apply();
    }

    /*
    *GET_MEDIA_RESPONSE
    * */
    public String getGET_MEDIA_RESPONSE() {
        return pSharedPref.getString(GET_MEDIA_RESPONSE, null);

    }

    public void setBannerResponse(String response) {
        pSharedPref.edit().putString(BANNERRESPONSE, response).apply();
    }
    public String getBannerResponse() {

        return pSharedPref.getString(BANNERRESPONSE, "");
    }

    public void setGET_MEDIA_RESPONSE(String response) {
        pSharedPref.edit().putString(GET_MEDIA_RESPONSE, response).apply();
    }
    public void setAudioResponse(String response) {
        pSharedPref.edit().putString(AUDIORESPOSNE, response).apply();
    }
    public String getAudioRespose() {

        return pSharedPref.getString(AUDIORESPOSNE, "");
    }
    public void setVedioResponse(String response) {
        pSharedPref.edit().putString(VIDEORESPONSE, response).apply();
    }
    public String getVideoRespose() {

        return pSharedPref.getString(VIDEORESPONSE, "");
    }

    ///
    public boolean getGET_ALL_AUDIO() {
        return pSharedPref.getBoolean(GET_ALL_AUDIO, false);

    }

    public void setGET_ALL_AUDIO(boolean response) {
        pSharedPref.edit().putBoolean(GET_ALL_AUDIO, response).apply();
    }

    ///
    public boolean getGET_ALL_VIDEO() {
        return pSharedPref.getBoolean(GET_ALL_VIDEO, false);

    }

    public void setGET_ALL_VIDEO(boolean response) {
        pSharedPref.edit().putBoolean(GET_ALL_VIDEO, response).apply();
    }

    public String getUSER_TOKEN() {
        return pSharedPref.getString(USER_TOKEN, null);
    }

    public void setUSER_TOKEN(String user_token) {
        pSharedPref.edit().putString(USER_TOKEN, user_token).apply();
    }


    public String getFCPTCOKEN() {
        return pSharedPref.getString(FCP_ID, null);
    }

    public void setFCPTOKEN(String user_token) {
        pSharedPref.edit().putString(FCP_ID, user_token).apply();
    }
    public void setCOUTRYCODE(String code) {
        pSharedPref.edit().putString(COUTRYCODE, code).apply();

    }
    public String getName() {
        return pSharedPref.getString(NAME, "");
    }

    public void setName(String code) {
        pSharedPref.edit().putString(NAME, code).apply();

    }
    public String getCOUTRYCODE() {
        return pSharedPref.getString(COUTRYCODE, "");
    }


    public boolean isFcpSend() {
        return pSharedPref.getBoolean(FCMIDSET, false);

    }

    public void setFCMID_SEND(boolean response) {
        pSharedPref.edit().putBoolean(FCMIDSET, response).apply();
    }
    public String getLanguageCode()
    {
        return pSharedPref.getString(LANGUAGE_CODE, "en");
    }
    public void setLanguageCode(String value)
    {
        pSharedPref.edit().putString(LANGUAGE_CODE, value).apply();

    }

}