package com.mobileoid2.celltone.view.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.AppUtils;
import com.mobileoid2.celltone.Util.AudioPlayer;
import com.mobileoid2.celltone.Util.Constant;
import com.mobileoid2.celltone.Util.VideoPlayer;
import com.mobileoid2.celltone.database.DatabaseConstants;
import com.mobileoid2.celltone.network.APIClient;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.ApiInterface;
import com.mobileoid2.celltone.network.NetworkCallBack;
import com.mobileoid2.celltone.network.SendRequest;
import com.mobileoid2.celltone.network.jsonparsing.JsonResponse;
import com.mobileoid2.celltone.network.model.setOwnMedia.SetOwnMediaModel;
import com.mobileoid2.celltone.pojo.Music;
import com.mobileoid2.celltone.pojo.getmedia.Body;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;
import com.mobileoid2.celltone.utility.Utils;
import com.mobileoid2.celltone.view.activity.Tutorial;
import com.mobileoid2.celltone.view.activity.UploadActivity;
import com.mobileoid2.celltone.view.activity.VideoCapture;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;
import static okhttp3.MediaType.parse;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMusicUpload extends Fragment implements NetworkCallBack {


    private static final int VIDEO_RECORDING_CODE = 3053;
    private CardView cardViewMic, cardViewVideoCamera;
    public static final String PATH_WITH_NAME = "PATH_WITH_NAME";
    private Activity activity;
    private int isAudio;
    private View view;
    private File mediaFile;
    private Map<String, RequestBody> partMap = new HashMap<>();
    private MultipartBody.Part fileData = null;
    private ApiInterface apiInterface;
    private RelativeLayout layoutRecording;
    private EditText etxtFileName;
    public static final int REPEAT_INTERVAL = 1000;
    private ProgressBar progressBar;
    private MediaRecorder myAudioRecorder;
    private boolean isRecording;
    private String currentOutFile;
    private VideoView videoView;
    private String nameFile;
    private ImageButton imageButtonLeft;
    private ImageButton imageButtonRight;
    private ImageButton imageButtonCenter;
    private LinearLayout llFileName, llButtons, llVideo, llAudio;
    private TextView txtCancel, txtSave;
    private TextView textViewAudioRecordingName, textViewAudioRecordingTime;
    private Handler handler = new Handler();
    private boolean isRecordingLayoutVisible = false;
    private String videoPath;
    private static boolean isFileMade = true;
    private File mediaFolder;
    private int isRecord = 0;
    private int IsAudio;
    private boolean isVideoRecorded = false;
    //  File mediaFolder = Environment.getExternalStoragePublicDirectory("VideoCompression");


    public static FragmentMusicUpload newInstance(int isAudio, int isRecord, String currentOutFile) {
        FragmentMusicUpload fragment = new FragmentMusicUpload();
        fragment.isAudio = isAudio;
        fragment.currentOutFile = currentOutFile;
        fragment.isRecord = isRecord;
        return fragment;
    }


    static {

        if (!new File(Constant.RECORDING_AUDIO_PATH).exists()) {
            isFileMade = new File(Constant.RECORDING_AUDIO_PATH).mkdirs();
        }
        if (!new File(Constant.RECORDING_VIDEO_PATH).exists()) {
            isFileMade = new File(Constant.RECORDING_VIDEO_PATH).mkdirs();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_upload, container, false);
        try {

            cardViewMic = view.findViewById(R.id.card_view_mic);
            cardViewVideoCamera = view.findViewById(R.id.card_view_camera);
            apiInterface = (ApiInterface) APIClient.getClient().create(ApiInterface.class);
            layoutRecording = view.findViewById(R.id.layout_record_audio);
            progressBar = view.findViewById(R.id.media_player_progress_bar);
            llButtons = view.findViewById(R.id.ll_buttons);
            cardViewMic.setVisibility(View.GONE);
            cardViewVideoCamera.setVisibility(View.GONE);
            if (isAudio == 0) {
                if (isRecord == 1)
                    recordVideo();
                else {
                    statusCenter = 2;
//                    playVideoAudio();

                    if (!currentOutFile.isEmpty()) {
                        UploadActivity.isPopupVisible = 1;
                        layoutRecording.setVisibility(View.VISIBLE);
                    }
                    isAudio = 0;
                    statusCenter = 2;
                    requestRecording(isAudio, isRecord);
                }
            } else if (isAudio == 1) {
                if (isRecord == 1) {
                    if (!isRecordingLayoutVisible) {
                        isAudio = 1;
                        requestRecording(isAudio, isRecord);
                    }
                } else {
                    //     stopRecorder();

                    requestRecording(isAudio, isRecord);
                    statusCenter = 2;

                }

            } else {
                cardViewMic.setVisibility(View.VISIBLE);
                cardViewVideoCamera.setVisibility(View.VISIBLE);
            }

            cardViewMic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!isRecordingLayoutVisible) {
                        isAudio = 1;
                        requestRecording(isAudio, isRecord);
                    }
                }
            });

            cardViewVideoCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    recordVideo();
                }
            });


        } catch (Exception e) {
            Log.e("ERROR: UPLOAD OWN", AppUtils.instance.getExceptionString(e));
        }
        return view;
    }

    private void recordVideo() {
        if (!isFileMade) {

            Toast.makeText(activity, R.string.text_unable_to_create, Toast.LENGTH_SHORT).show();
            return;
        }


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String currentTimeStamp = dateFormat.format(new Date());

        nameFile = "video_recording_" + currentTimeStamp + ".mp4";
        File currentFile = new File(Constant.RECORDING_VIDEO_PATH, nameFile);
        try {
            if (!currentFile.createNewFile()) {
                Toast.makeText(activity, R.string.text_unable_to_create, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (IOException e) {
            Toast.makeText(activity, R.string.text_unable_to_create, Toast.LENGTH_SHORT).show();
            return;
        }
        videoPath = currentFile.getAbsolutePath();
        isVideoRecorded = true;
        Intent intent = new Intent(activity.getApplicationContext(), VideoCapture.class);
        intent.putExtra(VideoCapture.PATH_WITH_NAME, videoPath);
        startActivityForResult(intent, VIDEO_RECORDING_CODE);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isRecordingLayoutVisible = false;
        if (requestCode == VIDEO_RECORDING_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                currentOutFile = data.getStringExtra("filepath");
                if (!currentOutFile.isEmpty()) {
                    UploadActivity.isPopupVisible = 1;
                    layoutRecording.setVisibility(View.VISIBLE);
                }
                isAudio = 0;
                statusCenter = 2;
                requestRecording(isAudio, isRecord);
                //    PATH_WITH_NAME


                //}
            }
        }
    }

    private void proceedFurtherWithVideoFile() {

        Music music = new Music();
        music.setGerne("recordings");
        music.setIsVideo(DatabaseConstants.VALUE_TRUE);
        music.setSongAlbum("recordings");
        music.setSongsPath(videoPath);
        music.setSongTitle(nameFile);
        music.setThumbUrl("");

        // selectIncomingOrOutgoing(music);
    }


    // for recording audio
    public boolean hideRecorder() {
        // return false is layout is already not showing
        if (!isRecordingLayoutVisible) return false;
        try {
            if (null != myAudioRecorder) {
                myAudioRecorder.stop();
                myAudioRecorder.release();
                myAudioRecorder = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (handler != null) handler.removeCallbacks(updateTimer);
        setInitialRecordingScreen();
        if (currentOutFile != null) {
            File recording = new File(currentOutFile);
            if (recording.exists()) recording.delete();
        }
        UploadActivity.isPopupVisible = 0;
        layoutRecording.setVisibility(View.GONE);
        isRecordingLayoutVisible = false;
        // action had taken place
        return true;
    }

    private int statusCenter = 0;


    private void requestRecording(int isAudio, int isRecord) {
        try {
            imageButtonLeft = view.findViewById(R.id.image_button_left);
            imageButtonLeft.setVisibility(View.GONE);
            imageButtonCenter = view.findViewById(R.id.image_button_center);
            imageButtonRight = view.findViewById(R.id.image_button_right);
            textViewAudioRecordingName = view.findViewById(R.id.text_view_name);
            textViewAudioRecordingTime = view.findViewById(R.id.text_view_time);
            llFileName = view.findViewById(R.id.ll_file_name);
            txtCancel = view.findViewById(R.id.txt_cancel);
            etxtFileName = view.findViewById(R.id.etxt_file_name);
            txtSave = view.findViewById(R.id.txt_save);
            videoView = view.findViewById(R.id.video_view);
            if (isRecord == 1) {
                llFileName.setVisibility(View.GONE);
                llButtons.setVisibility(View.VISIBLE);
            } else {
                llFileName.setVisibility(View.VISIBLE);
                llButtons.setVisibility(View.GONE);

            }


            llAudio = view.findViewById(R.id.ll_audio);
            llVideo = view.findViewById(R.id.ll_video);
            if (isAudio == 0) {
                llAudio.setVisibility(View.GONE);
                llVideo.setVisibility(View.VISIBLE);
                VideoPlayer.getInstance(activity, videoView);
                videoView.setVideoPath(currentOutFile);
                videoView.start();
                playVideoAudio();
            } else {
                llAudio.setVisibility(View.VISIBLE);
                llVideo.setVisibility(View.GONE);

            }
            txtCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UploadActivity.isPopupVisible = 0;
                    layoutRecording.setVisibility(View.GONE);
                    if(videoView!=null && videoView.isPlaying())
                    videoView.stopPlayback();
                    try
                    {
                        if(AudioPlayer.isMusicPlaying())
                            AudioPlayer.stop();
                    }
                    catch (Exception ex)
                    {

                    }

                }
            });
            txtSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (etxtFileName.getText().toString().trim().length() > 0) {
                        progressBar.setVisibility(View.VISIBLE);
                        setDataForRequest(etxtFileName.getText().toString(), currentOutFile);
                    } else
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.file_name_message), Toast.LENGTH_LONG).show();
                }
            });


            view.findViewById(R.id.layout_upper).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (statusCenter == 2) {
                        if (AudioPlayer.getmMediaController() != null)
                            AudioPlayer.getmMediaController().show();
                    }
                }
            });

            imageButtonCenter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (statusCenter == 0) {
                        if (isAudio == 1)
                            setRecordingOnScreen();
                    } else if (statusCenter == 1) {
                        stopRecorder();
                        statusCenter = 2;
                        playVideoAudio();

                    } else if (statusCenter == 2) {
                        llFileName.setVisibility(View.VISIBLE);
                    }
                }
            });


            imageButtonLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (isAudio == 1)
                            AudioPlayer.play();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            imageButtonRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (statusCenter == 1) {
                        if (isAudio == 1) {
                            if (isRecording)
                                pauseRecorder();
                            else {
                                resumeRecorder();
                            }
                        }

                    }

                    if (statusCenter == 2) {
                        File recording = new File(currentOutFile);
                        if (recording.exists() && recording.delete()) {
                            Toast.makeText(activity, "Recording deleted : " + currentOutFile, Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(activity, "Doesnt exist" + currentOutFile, Toast.LENGTH_SHORT).show();

                        if (isAudio == 1)
                            setInitialRecordingScreen();


                    }
                }
            });

            setInitialRecordingScreen();

            if (isAudio == 0) {
                llButtons.setVisibility(View.GONE);
                llFileName.setVisibility(View.VISIBLE);
                imageButtonCenter.setImageDrawable(getResources().getDrawable(R.mipmap.add2contact_btn));
                textViewAudioRecordingName.setText("");
                imageButtonRight.setVisibility(View.VISIBLE);
                imageButtonLeft.setVisibility(View.VISIBLE);
                imageButtonLeft.setImageDrawable(getResources().getDrawable(R.mipmap.replay_btn));
                imageButtonRight.setImageDrawable(getResources().getDrawable(R.mipmap.delete_btn));
                isRecording = false;
            }
        } catch (Exception e) {
            Log.e("Constant.RECORDING", AppUtils.instance.getExceptionString(e));
        }
    }

    private void playVideoAudio() {
        llButtons.setVisibility(View.GONE);
        llFileName.setVisibility(View.VISIBLE);
        imageButtonCenter.setImageDrawable(getResources().getDrawable(R.mipmap.add2contact_btn));
        textViewAudioRecordingName.setText("");
        imageButtonRight.setVisibility(View.VISIBLE);
        imageButtonLeft.setVisibility(View.GONE);
        imageButtonLeft.setImageDrawable(getResources().getDrawable(R.mipmap.replay_btn));
        imageButtonRight.setImageDrawable(getResources().getDrawable(R.mipmap.delete_btn));
        if (isAudio == 1)
            isRecording = false;
    }


    public void setDataForRequest(String fileName, String filePath) {


        partMap.put("title", RequestBody.create(parse("text/plain"), fileName));


        if (!filePath.isEmpty()) {
            File file = new File(filePath);
            Uri selectedUri = Uri.fromFile(file);
            String type = null;
            String extension = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
            if (extension != null) {

                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }

            RequestBody requestFile = RequestBody.create(parse(type), file);

            fileData = MultipartBody.Part.createFormData("media", file.getName(), requestFile);
            SendRequest.sendRequest(ApiConstant.VIDEOAPI, apiInterface.uploadMedia(SharedPrefrenceHandler.getInstance().getUSER_TOKEN(), fileData, partMap), this);
        } else
            progressBar.setVisibility(View.GONE);

    }

    private void setInitialRecordingScreen() {
        imageButtonCenter.setImageDrawable(getResources().getDrawable(R.mipmap.start_recording_btn));
        imageButtonLeft.setVisibility(View.GONE);
        imageButtonRight.setVisibility(View.GONE);
        statusCenter = 0;
        totalSeconds = 0;
        isVideoRecorded = false;
        UploadActivity.isPopupVisible = 1;
        layoutRecording.setVisibility(View.VISIBLE);
        isRecordingLayoutVisible = true;
        if (isAudio == 1) {
            textViewAudioRecordingName.setText("");
            textViewAudioRecordingTime.setText("00:00");
            textViewAudioRecordingTime.setTextSize(25);
            currentOutFile = "";
            nameFile = "";

        }

    }

    public void hidePopup() {
        layoutRecording.setVisibility(View.GONE);

    }

    private void setRecordingOnScreen() {

        if (!isFileMade) {
            Toast.makeText(activity, R.string.text_unable_to_create, Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String currentTimeStamp = dateFormat.format(new Date());

        nameFile = "audio_recording_" + currentTimeStamp + ".mp3";
        File currentFile = new File(Constant.RECORDING_AUDIO_PATH, nameFile);
        try {
            if (!currentFile.createNewFile()) {
                Toast.makeText(activity, R.string.text_unable_to_create, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (IOException e) {
            Toast.makeText(activity, R.string.text_unable_to_create, Toast.LENGTH_SHORT).show();
            return;
        }
        currentOutFile = currentFile.getAbsolutePath();


        imageButtonLeft.setVisibility(View.GONE);
        imageButtonRight.setVisibility(View.GONE);

        statusCenter = 1;
        imageButtonCenter.setImageDrawable(getResources().getDrawable(R.mipmap.stop_btn));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageButtonRight.setVisibility(View.VISIBLE);
            imageButtonRight.setImageDrawable(getResources().getDrawable(R.mipmap.pause_btn));
        }


        textViewAudioRecordingName.setText(nameFile);
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myAudioRecorder.setMaxDuration(30 * 1000);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);


        myAudioRecorder.setOutputFile(currentOutFile);

        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();
            Toast.makeText(activity, "Recording started.", Toast.LENGTH_LONG).show();
            handler.post(updateTimer);
            isRecording = true;
        } catch (Exception e) {
            isRecording = false;
        }

    }

    private long totalSeconds = 0;
    // updates the visualizer every 1000 milliseconds
    private Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            if (isRecording) // if we are already recording
            {
                totalSeconds = totalSeconds + 1;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        textViewAudioRecordingTime.setText("" + getTimeFromSeconds(totalSeconds));
                    }
                });
                handler.postDelayed(this, REPEAT_INTERVAL);
            }
        }
    };

    private String getTimeFromSeconds(long totalSeconds) {

        if (totalSeconds < 10) return "00:0" + totalSeconds;
        if (totalSeconds >= 10 && totalSeconds < 60) return "00:" + totalSeconds;
        if (totalSeconds >= 60) {
            long minutes = totalSeconds / 60;
            long remainingSeconds = totalSeconds - minutes * 60;
            if (minutes < 10) return "0" + minutes + ":" + remainingSeconds;
            if (minutes >= 10 && minutes < 60) return minutes + ":" + remainingSeconds;
        }
        return "";
    }

    private void stopRecorder() {
        try {
            if (null != myAudioRecorder) {
                myAudioRecorder.stop();
                myAudioRecorder.release();
                myAudioRecorder = null;
                handler.removeCallbacks(updateTimer);
                totalSeconds = 0;

                    AudioPlayer.getInstance(activity, view.findViewById(R.id.layout_upper), currentOutFile, new Handler());

                //  Toast.makeText(activity, "Recording saved : " + currentOutFile, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pauseRecorder() {
        try {
            if (null != myAudioRecorder) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    myAudioRecorder.pause();
                    isRecording = false;
                    handler.removeCallbacks(updateTimer);
                    imageButtonRight.setImageDrawable(activity.getResources().getDrawable(R.mipmap.small_recording_btn));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
       try
       {
           if(AudioPlayer.isMusicPlaying())
           AudioPlayer.stop();
       }
       catch (Exception ex)
       {

       }
        super.onDestroyView();
    }

    private void resumeRecorder() {
        try {
            if (null != myAudioRecorder) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    myAudioRecorder.resume();
                    isRecording = true;
                    handler.post(updateTimer);
                    imageButtonRight.setImageDrawable(activity.getResources().getDrawable(R.mipmap.pause_btn));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parsePlan(String response) {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(getMediaResponse(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getPlanObserver()));
    }

    private DisposableObserver<SetOwnMediaModel> getPlanObserver() {
        return new DisposableObserver<SetOwnMediaModel>() {

            @Override
            public void onNext(SetOwnMediaModel model) {
                Toast.makeText(getActivity(), model.getMessage(), Toast.LENGTH_LONG).show();
                UploadActivity.isPopupVisible = 0;
                layoutRecording.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                getActivity().finish();


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
    public void onDetach() {
        super.onDetach();
        try {
            //     if (AudioPlayer.getmMediaController() != null)
            //   AudioPlayer.stop();
        } catch (Exception ex) {

        }

    }


    private Observable<SetOwnMediaModel> getMediaResponse(String response) {
        Gson gsonObj = new Gson();
        final SetOwnMediaModel planBody = gsonObj.fromJson(response, SetOwnMediaModel.class);
        if (mediaFile != null && mediaFile.exists())
            mediaFile.delete();

        return Observable.create(new ObservableOnSubscribe<SetOwnMediaModel>() {
            @Override
            public void subscribe(ObservableEmitter<SetOwnMediaModel> emitter) throws Exception {
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
            parsePlan(response.getObject());

        } else {
            Toast.makeText(getActivity(), response.getErrorString(), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }

    }
}
