package com.mobileoid2.celltone.network;



import com.mobileoid2.celltone.network.model.contacts.SendContactsModel;
import com.mobileoid2.celltone.pojo.CategeoryRequest;
import com.mobileoid2.celltone.pojo.CommentRequest;
import com.mobileoid2.celltone.pojo.ComposeQueryRequest;
import com.mobileoid2.celltone.pojo.FCMMODELREQUEST;
import com.mobileoid2.celltone.pojo.MediaListReqeuestPojo;
import com.mobileoid2.celltone.pojo.PojoContactsUpload;
import com.mobileoid2.celltone.pojo.PojoLogin;
import com.mobileoid2.celltone.pojo.PojoOTPRequest;
import com.mobileoid2.celltone.pojo.PojoOTPVerifyRequest;
import com.mobileoid2.celltone.pojo.QUERYREQUEST;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface ApiInterface {


    @Headers("Content-Type: application/json")
    @POST("user/generate/otp")
    public Call<String> getOtp(@Body PojoOTPRequest pojoOTPRequest);

    @Headers("Content-Type: application/json")
    @POST("user/register")
    public Call<String> register(@Body PojoLogin pojoOTPRequest);
    @GET("contacts/consumed-media")
    public Call<String> getMediForMe(@Header("token") String token);
    @Headers("Content-Type: application/json")
    @POST("user/validate/otp")
    public Call<String> validateOtp(@Body PojoOTPVerifyRequest pojoOTPVerifyRequest);
    @GET("media/trending/audio")
    public Call<String> getAllAudio(@Header("token") String token);
    @GET("media/trending/video")
    public Call<String> getAllVideo(@Header("token") String token);
    @Headers({
            "Content-Type: application/json",

    })

    @GET("contacts/all")
    public Call<String> getAllContatcs(@Header("token") String token);
    @GET("user/my-profile")
    public Call<String> getMyProfile(@Header("token") String token);




    @GET("subscription/plan/active")
    public Call<String> getAllPlans(@Header("token") String token);

    @GET("offer/active")
    public Call<String> getAllOffer(@Header("token") String token);

    @Headers("Content-Type: application/json")
    @POST("contacts/set/media")
    public Call<String> serMediaForUser(@Header("token") String token,@Body  SendContactsModel sendContactsModel);



    @Headers("Content-Type: application/json")
    @POST("contacts/sync")
    public Call<String> syncContact(@Header("token") String token,@Body PojoContactsUpload sendContactsModel);

    @Multipart
    @POST("media/own-media")
    Call<String> uploadMedia(@Header("token") String token,
            @Part MultipartBody.Part file,
            @PartMap() Map<String, RequestBody> partMap);


    @Headers("Content-Type: application/json")
    @POST("user/fcm/update")
    public Call<String> uploadFcm(@Header("token") String token,@Body FCMMODELREQUEST fcmmodelrequest);

    @Headers("Content-Type: application/json")
    @POST("media/user/own-media")
    public Call<String> getUserUploadList(@Header("token") String token,@Body MediaListReqeuestPojo fcmmodelrequest);

    @GET("contacts/media-usage")
    public Call<String> getAllMediaSetByUser(@Header("token") String token);

    @GET
    public Call<String> getAboutUs(@Url String url);


    @Headers("Content-Type: application/json")
    @POST("query/all")
    public Call<String> getQueryList(@Header("token") String token,@Body QUERYREQUEST queryrequest);

    @Headers("Content-Type: application/json")
    @POST("query/comment/add")
    public Call<String> sendComment(@Header("token") String token,@Body CommentRequest commentRequest);

    @Headers("Content-Type: application/json")
    @POST("query/add")
    public Call<String> createQuery(@Header("token") String token,@Body ComposeQueryRequest composeQueryRequest);



    @Multipart
    @POST("user/update/avatar")
    Call<String> uploadAvatar(@Header("token") String token,
                             @Part MultipartBody.Part file);
    @GET("promotion/all")
    public Call<String> getAllBannes(@Header("token") String token);

    @GET("legal/faq")
    public Call<String> getAllFAQ();

    @Headers("Content-Type: application/json")
    @POST("media/all")
    public Call<String> getSongs( @Header("token") String token,@Body CategeoryRequest categeoryRequest);

    @DELETE("user/remove/avatar")
   public Call<String> deleteAvatar( @Header("token") String token);






}
