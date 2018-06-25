package com.mobileoid2.celltone.Util;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.googlecode.mp4parser.boxes.apple.AppleLongDescriptionBox;
import com.mobileoid2.celltone.R;

public enum AppSharedPref {
    instance;

    SharedPreferences sharedPreferences;

    public void saveScreenHeight(int height) {
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(AppLevelConstraints
                        .getAppContext());
        Editor editor = sharedPreferences.edit();
        editor.putInt("HEIGHT", height);
        editor.commit();
    }

    public int getScreenHeight() {
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(AppLevelConstraints
                        .getAppContext());
        return sharedPreferences.getInt("HEIGHT", 0);
    }

    public void saveScreenWidth(int width) {
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(AppLevelConstraints
                        .getAppContext());
        Editor editor = sharedPreferences.edit();
        editor.putInt("WIDTH", width);
        editor.commit();
    }

    public int getScreenWidth() {
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(AppLevelConstraints
                        .getAppContext());
        return sharedPreferences.getInt("WIDTH", 0);
    }

    // to get logged in state
    public boolean getLoginState() {

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(AppLevelConstraints
                        .getAppContext());
        return sharedPreferences.getBoolean("is_logged_in", false);

    }

    // for saving logged in state
    public void setLoginState(boolean isLoggedIn) {

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(AppLevelConstraints
                        .getAppContext());
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_logged_in", isLoggedIn);
        editor.commit();
    }

    //  save and get IMEI
    public void saveProfilePic(String bitmapPath) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AppLevelConstraints.getAppContext());
        Editor editor = sharedPreferences.edit();
        editor.putString("bitmapPath", bitmapPath);
        editor.commit();
    }

    public String getProfilePic() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AppLevelConstraints.getAppContext());
        return sharedPreferences.getString("bitmapPath", "");
    }

    //  save and get name
    public void saveName(String name) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AppLevelConstraints.getAppContext());
        Editor editor = sharedPreferences.edit();
        editor.putString("user_name", name);
        editor.commit();
    }

    public String getName() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AppLevelConstraints.getAppContext());
        return sharedPreferences.getString("user_name", AppLevelConstraints.getAppContext().getResources().getString(R.string.text_guest_user));
    }

    //  save and get number
    public void saveNumber(String number) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AppLevelConstraints.getAppContext());
        Editor editor = sharedPreferences.edit();
        editor.putString("user_number", number);
        editor.apply();
    }

    public String getNumber() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AppLevelConstraints.getAppContext());
        return sharedPreferences.getString("user_number", "");
    }

}
