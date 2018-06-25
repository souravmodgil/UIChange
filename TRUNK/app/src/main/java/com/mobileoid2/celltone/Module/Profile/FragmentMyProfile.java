package com.mobileoid2.celltone.Module.Profile;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.iceteck.silicompressorr.SiliCompressor;
import com.mobileoid2.celltone.CustomWidget.EditTextView.EditTextEuro55Regular;
import com.mobileoid2.celltone.Module.Base.ActivityBase;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.AppSharedPref;
import com.mobileoid2.celltone.Util.AppUtils;
import com.mobileoid2.celltone.Util.PermissionsMarshmallow;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import id.zelory.compressor.Compressor;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMyProfile extends Fragment {


    private ImageButton imageButtonProfileButton;
    private View view;
    private Activity activity;
    private EditTextEuro55Regular editTextName, editTextNumber;


    private int ACTION_REQUEST_GALLERY = 9876;
    private int ACTION_REQUEST_CAMERA = 8765;
    private String CAPTURE_IMAGE_FILE_PROVIDER = "com.mobileoid2.celltone.fileprovider";
    private float screenWidth = 0;
    private float screenHeight = 0;

    public static FragmentMyProfile newInstance() {
        FragmentMyProfile fragment = new FragmentMyProfile();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        try {

            imageButtonProfileButton = view.findViewById(R.id.image_button_profile_pic);

            editTextName = view.findViewById(R.id.edittext_name);
            editTextNumber = view.findViewById(R.id.edittext_no);
            imageButtonProfileButton.setImageDrawable(getResources().getDrawable(R.mipmap.profile_pic));

            imageButtonProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDiloag();
                }
            });

            view.findViewById(R.id.layout_contacts).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ActivityBase) activity).initiateContactSelection(null, false);
                }
            });

            view.findViewById(R.id.edittext_contacts).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ActivityBase) activity).initiateContactSelection(null, false);
                }
            });


            view.findViewById(R.id.layout_logout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppSharedPref.instance.setLoginState(false);
                    AppSharedPref.instance.saveProfilePic("");
                    AppSharedPref.instance.saveName(getString(R.string.text_guest_user));
                    AppSharedPref.instance.saveNumber("");
                    activity.finish();

                }
            });

            view.findViewById(R.id.edittext_logout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppSharedPref.instance.setLoginState(false);
                    AppSharedPref.instance.saveProfilePic("");
                    AppSharedPref.instance.saveName(getString(R.string.text_guest_user));
                    AppSharedPref.instance.saveNumber("");
                    activity.finish();
                }
            });

            editTextName.setTag(false);
            editTextNumber.setTag(false);
            editTextName.setText(AppSharedPref.instance.getName());
            editTextNumber.setText(AppSharedPref.instance.getNumber());
            ((EditText) view.findViewById(R.id.edittext_name_edit_button_click)).setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.edit_icon), null);
            ((EditText) view.findViewById(R.id.edittext_number_button_click)).setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.edit_icon), null);


            ((EditText) view.findViewById(R.id.edittext_name_edit_button_click)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if ((boolean) editTextName.getTag()) {
                        disableEditTextEditable(editTextName);
                        editTextName.setTag(false);
                        AppUtils.instance.hideKeyboard(activity);

                        if (editTextName.getText().toString().equals("")) {
                            editTextName.setText(AppSharedPref.instance.getName());
                        }
                        AppSharedPref.instance.saveName(editTextName.getText().toString());
                        ((ActivityBase) activity).setNavigationName();
                        ((EditText) view.findViewById(R.id.edittext_name_edit_button_click)).setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.edit_icon), null);
                    } else {
                        enableEditTextEditable(editTextName);
                        editTextName.setTag(true);
                        editTextName.setText("");
                        ((EditText) view.findViewById(R.id.edittext_name_edit_button_click)).setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.save_icon), null);
                    }
                }
            });

            ((EditText) view.findViewById(R.id.edittext_number_button_click)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if ((boolean) editTextNumber.getTag()) {
                        disableEditTextEditable(editTextNumber);

                        if (editTextNumber.getText().toString().equals("")) {
                            editTextNumber.setText(AppSharedPref.instance.getNumber());
                        }

                        AppSharedPref.instance.saveNumber(editTextNumber.getText().toString());
                        editTextNumber.setTag(false);
                        AppUtils.instance.hideKeyboard(activity);
                        ((EditText) view.findViewById(R.id.edittext_number_button_click)).setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.edit_icon), null);
                    } else {
                        enableEditTextEditable(editTextNumber);
                        editTextNumber.setTag(true);
                        editTextNumber.setText("");
                        ((EditText) view.findViewById(R.id.edittext_number_button_click)).setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.save_icon), null);
                    }
                }
            });


            if (!AppSharedPref.instance.getProfilePic().equals("")) {

                Bitmap imageBitmap = SiliCompressor.with(activity.getApplicationContext()).getCompressBitmap(AppSharedPref.instance.getProfilePic());
                imageButtonProfileButton.setImageBitmap(AppUtils.instance.getCircularBitmap(imageBitmap));
            }


        } catch (Exception e) {
            Log.e("ERROR: PROFILE", AppUtils.instance.getExceptionString(e));
        }
        return view;

    }

    // for the name


    private void enableEditTextEditable(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setCursorVisible(true);
        editText.setTag(true);
        editText.requestFocus();
        editText.setSelected(true);
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void disableEditTextEditable(EditText editText) {
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.setCursorVisible(false);
        editText.setTag(false);
        editText.setSelected(false);
        AppUtils.instance.hideKeyboard(activity);
    }


    // for the image

    public void showDiloag() {
        Dialog dialog = new Dialog(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Gallery", "Camera"},
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                Intent chooser = Intent.createChooser(intent, "Choose a Picture");
                                startActivityForResult(chooser, ACTION_REQUEST_GALLERY);
                                break;
                            case 1:
                                launchCamera();
                                break;

                            default:
                                break;
                        }
                    }
                });

        builder.show();
        dialog.dismiss();
    }

    public void launchCamera() {
// check for dangerous permission
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            File path = new File(activity.getFilesDir(), "Gallery/MyImages/");
            if (!path.exists()) path.mkdirs();
            File image = new File(path, "image_capture.jpg");
            Uri imageUri = FileProvider.getUriForFile(activity.getApplicationContext(), CAPTURE_IMAGE_FILE_PROVIDER, image);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            providePermissionForProvider(cameraIntent, imageUri);
            startActivityForResult(cameraIntent, ACTION_REQUEST_CAMERA);
        } else {
            //if permission is not provided
            new PermissionsMarshmallow(activity);
        }
    }

    private void providePermissionForProvider(Intent intent, Uri uri) {
        List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            activity.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }


    private void aslfas() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        // shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri contentUri = Fileprovider.getUriForFile(context, "com.app.tst", csOrignalFile);
        shareIntent.putExtra("SHARE", contentUri);
        providePermissionForProvider(shareIntent, contentUri);
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share)));
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            System.out.println("OnActivityResult");
            if (resultCode == activity.RESULT_OK) {
                if (requestCode == ACTION_REQUEST_GALLERY) {
                    // System.out.println("select file from gallery ");
                    Uri selectedImageUri = data.getData();
                    Bitmap imageBitmap = SiliCompressor.with(activity.getApplicationContext()).getCompressBitmap(getFilePath(activity.getApplicationContext(), selectedImageUri));


                    AppSharedPref.instance.saveProfilePic(getFilePath(activity.getApplicationContext(), selectedImageUri));
                    imageBitmap = AppUtils.instance.getCircularBitmap(imageBitmap);
                    imageButtonProfileButton.setImageBitmap(imageBitmap);

                    ((ActivityBase) activity).setNavigationIcon();


                } else if (requestCode == ACTION_REQUEST_CAMERA) {


                    getScreenResolution();
                    File path = new File(activity.getFilesDir(), "Gallery/MyImages/");
                    if (!path.exists()) path.mkdirs();
                    File imageFile = new File(path, "image_capture.jpg");

                    Uri stringUri = Uri.fromFile(imageFile);

                    Bitmap bitmapFromMedia = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), stringUri);


                    if (bitmapFromMedia.getWidth() > bitmapFromMedia.getHeight()) {
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        bitmapFromMedia = Bitmap.createBitmap(bitmapFromMedia, 0, 0, bitmapFromMedia.getWidth(), bitmapFromMedia.getHeight(), matrix, true);


                        if (imageFile.exists()) imageFile.delete();

                        FileOutputStream out = new FileOutputStream(imageFile);
                        bitmapFromMedia.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();

                    }

                    new CompressCameraImage(imageFile, bitmapFromMedia).execute();

                    path = null;
                    imageFile = null;
                    bitmapFromMedia = null;
                    stringUri = null;
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFilePath(Context context, Uri uri) {
        String selection = null;
        String[] selectionArgs = null;

        try {
            // Uri is different in versions after KITKAT (Android 4.4), we need to
            if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                } else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    uri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                } else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("image".equals(type)) {
                        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    selection = "_id=?";
                    selectionArgs = new String[]{
                            split[1]
                    };
                }
            }
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                String[] projection = {
                        MediaStore.Images.Media.DATA
                };
                Cursor cursor = null;
                try {
                    cursor = context.getContentResolver()
                            .query(uri, projection, selection, selectionArgs, null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                } catch (Exception e) {
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private void getScreenResolution() {
        WindowManager wm = (WindowManager) activity.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = (float) size.x;
        screenHeight = (float) size.y;
    }

    private class CompressCameraImage extends AsyncTask<Void, Void, Bitmap> {

        File imageFile = null;
        Bitmap orignalImage = null;
        ProgressDialog progressDialog = null;
        File compressedImage = null;
        Bitmap compressedBitmap = null;


        private Dialog dialog;

        public CompressCameraImage(File imageFile, Bitmap orignalImage) {
            this.imageFile = imageFile;
            this.orignalImage = orignalImage;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {

                compressedImage = new Compressor.Builder(activity.getApplicationContext())
                        .setMaxWidth(screenWidth)
                        .setMaxHeight(screenHeight)
                        .setQuality(50)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .build()
                        .compressToFile(imageFile);


                Uri stringUri = Uri.fromFile(compressedImage);
                compressedBitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), stringUri);
                AppSharedPref.instance.saveProfilePic(stringUri.getPath());
                return compressedBitmap;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            try {
                Bitmap temp = AppUtils.instance.getCircularBitmap(result);

                if (temp != null) {
                    imageButtonProfileButton.setImageBitmap(temp);
                    ((ActivityBase) activity).setNavigationIcon();
                }

                imageFile = null;
                orignalImage = null;
                progressDialog = null;
                compressedImage = null;
                compressedBitmap = null;

                System.gc();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
