package com.mobileoid2.celltone.utility;

public class Config_URL {

    private static String base_URL = "http://13.126.37.207/celltone-node/api/v01/";
   // public static String base_URL = "http://192.168.1.41:3000/api/v01/";
    public static String tag_json_obj = "json_obj_req";

    /*Login*/
    public static String URL_REGISTER= base_URL + "user/register";
    public static String URL_OTP= base_URL + "user/generate/otp";
    public static String URL_VALIDATE_OTP= base_URL + "user/validate/otp";
    public static String URL_CONTACTS_SYNC= base_URL + "contacts/sync";
    public static String URL_MEDIA_SET= base_URL + "contacts/set/media";
    public static String URL_GET_MEDIA= base_URL + "contacts/my-media";
    public static String URL_GET_AUDIO= base_URL + "media/all/audio";
    public static String URL_GET_VIDEO= base_URL + "media/all/video";
    public static String URL_DOWNLOAD_FILE= base_URL + "media/id/";
    public static String URL_ALL_CONTACTS= base_URL + "contacts/all";


}
