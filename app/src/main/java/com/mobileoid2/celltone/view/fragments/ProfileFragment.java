package com.mobileoid2.celltone.view.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.network.model.MainNetworkModel;
import com.mobileoid2.celltone.view.activity.ChangeToolBarTitleListiner;
import com.mobileoid2.celltone.network.APIClient;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.ApiInterface;
import com.mobileoid2.celltone.network.NetworkCallBack;
import com.mobileoid2.celltone.network.SendRequest;
import com.mobileoid2.celltone.network.jsonparsing.JsonResponse;
import com.mobileoid2.celltone.network.model.profile.ProfileBody;
import com.mobileoid2.celltone.network.model.profile.ProfileModel;
import com.mobileoid2.celltone.network.model.profile.ProfilePlanDetail;
import com.mobileoid2.celltone.network.model.profile.UserProfile;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;
import com.mobileoid2.celltone.utility.Utils;
import com.mobileoid2.celltone.view.activity.PlanActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static okhttp3.MediaType.parse;

public class ProfileFragment extends Fragment implements NetworkCallBack {
    private static final int REQUEST_CAMERA = 1000;
    private static final int SELECT_FILE = 1001;
    private ApiInterface apiInterface;
    private LinearLayout llMain, llUploadMedia, llMediaUsage;
    private TextView txtUpgrade, txtProfileName, txtProfileContact, txtProfilePlanName, txtProfileValidateDate, txtProfileUserUpload, txtProfileUsage, txtUploadArrow;
    private CircleImageView profileImage;
    private ImageView editIcon;
    private ProgressBar progressBar;
    private String userChoosenTask;
    private ProfileBody profileBody;
    private ChangeToolBarTitleListiner changeToolBarTitleListiner;


    public static ProfileFragment newInstance(ChangeToolBarTitleListiner changeToolBarTitleListiner) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.changeToolBarTitleListiner = changeToolBarTitleListiner;
        // fragment.context = context;
        return fragment;


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragmnet_profie, container, false);
        llMain = view.findViewById(R.id.ll_main);
        txtProfileName = view.findViewById(R.id.txt_profile_name);
        progressBar = view.findViewById(R.id.media_player_progress_bar);
        txtProfileContact = view.findViewById(R.id.txt_profile_contact);
        txtProfilePlanName = view.findViewById(R.id.txt_profile_plan_name);
        txtProfileUserUpload = view.findViewById(R.id.txt_profile_user_upload);
        txtProfileUsage = view.findViewById(R.id.txt_profile_usage);
        llUploadMedia = view.findViewById(R.id.ll_upload_media);
        llMediaUsage = view.findViewById(R.id.ll_media_usage);
        txtProfileValidateDate = view.findViewById(R.id.txt_profile_validate_date);
        profileImage = view.findViewById(R.id.profile_image);
        txtUploadArrow = view.findViewById(R.id.txt_upload_arrow);
        editIcon = view.findViewById(R.id.edit_icon);
        txtUpgrade = view.findViewById(R.id.txt_upgrade);
        apiInterface = (ApiInterface) APIClient.getClient().create(ApiInterface.class);
        getProfile();
        txtUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PlanActivity.class));
            }
        });
        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        llMediaUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment mediaUsage = MediaSetByUserFregment.newInstance(profileBody.getPlanDetail().getOwnMediaCount(), changeToolBarTitleListiner);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, mediaUsage).addToBackStack(mediaUsage.getClass().getName());
                ft.commit();

            }
        });
        llUploadMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserMediaUploadFragment paymentMethodFragment = UserMediaUploadFragment.newInstance(profileBody.getPlanDetail().getOwnMediaCount(), changeToolBarTitleListiner);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, paymentMethodFragment).addToBackStack(paymentMethodFragment.getClass().getName());
                ft.commit();
            }
        });

        return view;
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library","Remove",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utils.checkPermission(
                        getActivity());

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
                else if (items[item].equals("Remove")) {
                    dialog.dismiss();
                    progressBar.setVisibility(View.VISIBLE);
                    SendRequest.sendRequest(ApiConstant.REMOVE_AVATAR,apiInterface.deleteAvatar(SharedPrefrenceHandler.getInstance().getUSER_TOKEN()),ProfileFragment.this);
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    public void getProfile() {
        SendRequest.sendRequest(ApiConstant.PROFILE_API, apiInterface.getMyProfile(SharedPrefrenceHandler.getInstance().getUSER_TOKEN()), this);
    }

    private void profileParse(String response) {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(getProfileData(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(parseProfile()));
    }

    private DisposableObserver<ProfileModel> parseProfile() {
        return new DisposableObserver<ProfileModel>() {

            @Override
            public void onNext(ProfileModel profileModel) {

                progressBar.setVisibility(View.GONE);
                if (profileModel.getStatus() == 1000) {
                    profileBody = profileModel.getBody();
                    UserProfile userProfile = profileModel.getBody().getUser();
                    com.mobileoid2.celltone.network.model.profile.CurrentPlan currentPlan = userProfile.getCurrentPlan();
                    ProfilePlanDetail planDetail = profileModel.getBody().getPlanDetail();
                    txtProfileName.setText(userProfile.getName());
                    txtProfileContact.setText(userProfile.getMobile());
                    if (planDetail != null) {
                        txtProfilePlanName.setText(planDetail.getName());
                    }
                    if (currentPlan != null)
                        txtProfileValidateDate.setText("(" + Utils.parseDate(currentPlan.getStartDate()) + "-" + Utils.parseDate(currentPlan.getEndDate()) + ")");
                    if (getActivity() != null &&
                            userProfile.getAvatar() != null && !userProfile.getAvatar().isEmpty() )
                        Glide.with(getActivity()).load(ApiConstant.MEDIA_URL + userProfile.getAvatar()).
                                into(profileImage);
                    txtProfileUserUpload.setText(profileBody.getOwnMediaCount() + "/" + planDetail.getOwnMediaCount());
                    if (userProfile.getUsedMedia() != null)
                        txtProfileUsage.setText("" + userProfile.getUsedMedia().size() + "/" + planDetail.getMediaCount());
                    llMain.setVisibility(View.VISIBLE);
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


    private Observable<ProfileModel> getProfileData(String response) {
        Gson gsonObj = new Gson();
        final ProfileModel planBody = gsonObj.fromJson(response, ProfileModel.class);

        return Observable.create(new ObservableOnSubscribe<ProfileModel>() {
            @Override
            public void subscribe(ObservableEmitter<ProfileModel> emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    emitter.onNext(planBody);
                    emitter.onComplete();
                }


            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());

                String filePath = "";
                String wholeID = DocumentsContract.getDocumentId(data.getData());

                // Split at colon, use second item in the array
                String id = wholeID.split(":")[1];

                String[] column = {MediaStore.Images.Media.DATA};

                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";

                Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

                int columnIndex = cursor.getColumnIndex(column[0]);

                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
                if (filePath != null && !filePath.isEmpty()) {
                    File file = new File(filePath);
                    setDataForRequest(file);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        profileImage.setImageBitmap(bm);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream file = null;
        try {
            destination.createNewFile();
            file = new FileOutputStream(destination);
            file.write(bytes.toByteArray());
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setDataForRequest(destination);
        profileImage.setImageBitmap(thumbnail);

    }

    public void setDataForRequest(File file) {


        if (file != null && file.exists()) {

            Uri selectedUri = Uri.fromFile(file);
            String type = null;
            String extension = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
            if (extension != null) {

                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
                if (type != null && type.length() > 0
                        )
                {
                    progressBar.setVisibility(View.VISIBLE);
                    RequestBody requestFile = RequestBody.create(parse(type), file);
                    MultipartBody.Part fileData = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);
                    SendRequest.sendRequest(ApiConstant.PROFILE_UPLOADAVATAR, apiInterface.uploadAvatar(SharedPrefrenceHandler.getInstance().getUSER_TOKEN(), fileData), this);

                }
                else
                    Toast.makeText(getActivity(), "Please click or select other pic", Toast.LENGTH_LONG).show();



            } else
                Toast.makeText(getActivity(), "Please click or select other pic", Toast.LENGTH_LONG).show();


        } else
            progressBar.setVisibility(View.GONE);

    }
    private void parseRemoveAvatar(String response)
    {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(removeProfilePic(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(parseRemovePic()));
    }

    private Observable<MainNetworkModel> removeProfilePic(String response) {
        Gson gsonObj = new Gson();
        final MainNetworkModel planBody = gsonObj.fromJson(response, MainNetworkModel.class);

        return Observable.create(new ObservableOnSubscribe<MainNetworkModel>() {
            @Override
            public void subscribe(ObservableEmitter<MainNetworkModel> emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    emitter.onNext(planBody);
                    emitter.onComplete();
                }


            }
        });
    }

    private DisposableObserver<MainNetworkModel> parseRemovePic() {
        return new DisposableObserver<MainNetworkModel>() {

            @Override
            public void onNext(MainNetworkModel profileModel) {

                progressBar.setVisibility(View.GONE);
                if(profileModel.getStatus()==1000)
                {
                    profileImage.setImageResource(R.drawable.profile_avatar);
                }
                Toast.makeText(getActivity(),profileModel.getMessage(),Toast.LENGTH_LONG).show();

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }




    @Override
    public void getResponse(JsonResponse response, int type) {
        if (response.getObject() != null) {
            switch (type) {
                //       {"status":1000,"body":{"user":{"currentPlan":{"planId":"5b0ea2594f3cf80db6dbe205","planName":"free plan","startDate":"2018-06-13T00:00:00.000Z","endDate":"2018-07-13T00:00:00.000Z"},"avatar":null,"otp":null,"usedMedia":["5b10f488bc20ab2dce9b17b7","5aec55a3bfcf7255e1c4c225","5b10f419bc20ab2dce9b17b6","5b10f232bc20ab2dce9b17b4"],"isVerified":true,"imei":"352875080879785","fcmId":"dyPngeaewY4:APA91bEuUtEtFB_3I00TXYKe8HqP8AK6tq4GSTY20vzuWGu_JxLWBVnXfzQbIZBgOK5YcuS6IqmqqVQe4fnSk9t1COH-YO8nkDpzv5jSAAACH1F9ujio2kH-xlNeIg8JS4yuEknCGHt4","_id":"5b20a91d326f61489e495c69","name":"naren","mobile":"+919873985349","countryCode":"+91","createdAt":"2018-06-13T05:18:21.442Z","updatedAt":"2018-06-18T12:41:34.286Z","__v":4},"ownMediaCount":2,"planDetail":{"mediaCount":5,"userCount":5,"validity":null,"ownMediaCount":5,"price":0,"expiry":null,"published":true,"image":null,"isDefaultPlan":true,"_id":"5b0ea2594f3cf80db6dbe205","name":"free plan","createdAt":"2018-05-30T13:08:41.178Z","updatedAt":"2018-05-30T13:09:24.288Z","__v":0}},"message":"success"}
                case ApiConstant.PROFILE_API:
                    profileParse(response.getObject());
                    break;
                case ApiConstant.PROFILE_UPLOADAVATAR:
                    progressBar.setVisibility(View.GONE);
                    break;
                case ApiConstant.REMOVE_AVATAR:
                    progressBar.setVisibility(View.GONE);
                    parseRemoveAvatar(response.getObject());
                    break;
            }
        } else {
            Toast.makeText(getActivity(), response.getErrorString(), Toast.LENGTH_LONG).show();
        }


    }

}
