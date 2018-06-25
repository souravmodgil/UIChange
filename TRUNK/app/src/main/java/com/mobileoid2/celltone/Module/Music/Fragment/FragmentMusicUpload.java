package com.mobileoid2.celltone.Module.Music.Fragment;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobileoid2.celltone.CustomWidget.Dialog.BeanDialogsOption;
import com.mobileoid2.celltone.CustomWidget.Dialog.DialogsCustom;
import com.mobileoid2.celltone.Module.Base.ActivityBase;
import com.mobileoid2.celltone.Module.Music.Bean.Music;
import com.mobileoid2.celltone.Module.Music.Utility.VideoCapture;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.AppUtils;
import com.mobileoid2.celltone.Util.AudioPlayer;
import com.mobileoid2.celltone.Util.Constant;
import com.mobileoid2.celltone.database.DatabaseConstants;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMusicUpload extends Fragment {


    private static final int VIDEO_RECORDING_CODE = 3053;
    private CardView cardViewMic, cardViewVideoCamera;
    private Activity activity;
    private View view;
    private RelativeLayout layoutRecording;

    public static final int REPEAT_INTERVAL = 1000;

    private MediaRecorder myAudioRecorder;
    private boolean isRecording;
    private String currentOutFile;
    private String nameFile;
    private ImageButton imageButtonLeft;
    private ImageButton imageButtonRight;
    private ImageButton imageButtonCenter;
    private TextView textViewAudioRecordingName, textViewAudioRecordingTime;
    private Handler handler = new Handler();
    private boolean isRecordingLayoutVisible = false;
    private String videoPath;
    private static boolean isFileMade = true;
    private boolean isVideoRecorded = false;

    public static FragmentMusicUpload newInstance() {
        FragmentMusicUpload fragment = new FragmentMusicUpload();
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

            layoutRecording = view.findViewById(R.id.layout_record_audio);

            cardViewMic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!isRecordingLayoutVisible)
                        requestRecording();
                }
            });

            cardViewVideoCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!isFileMade) {
                        Toast.makeText(activity, R.string.text_unable_to_create, Toast.LENGTH_SHORT).show();
                        return;
                    }


                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
                    String currentTimeStamp = dateFormat.format(new Date());

                    nameFile = "video_recording_" + currentTimeStamp + ".3gp";
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
            });


        } catch (Exception e) {
            Log.e("ERROR: UPLOAD OWN", AppUtils.instance.getExceptionString(e));
        }
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIDEO_RECORDING_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                if (Integer.parseInt(data.getStringExtra("length")) < 10) {
                    Toast.makeText(activity, getResources().getString(R.string.text_make_a_rec), Toast.LENGTH_SHORT).show();
                } else {
                    proceedFurtherWithVideoFile();
                }
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

        selectIncomingOrOutgoing(music);
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
        layoutRecording.setVisibility(View.GONE);
        isRecordingLayoutVisible = false;
        // action had taken place
        return true;
    }

    private int statusCenter = 0;

    public void requestRecording() {
        try {
            imageButtonLeft = view.findViewById(R.id.image_button_left);
            imageButtonCenter = view.findViewById(R.id.image_button_center);
            imageButtonRight = view.findViewById(R.id.image_button_right);
            textViewAudioRecordingName = view.findViewById(R.id.text_view_name);
            textViewAudioRecordingTime = view.findViewById(R.id.text_view_time);


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
                        setRecordingOnScreen();
                    } else if (statusCenter == 1) {
                        stopRecorder();
                        statusCenter = 2;
                        imageButtonCenter.setImageDrawable(getResources().getDrawable(R.mipmap.add2contact_btn));
                        textViewAudioRecordingName.setText("");
                        imageButtonRight.setVisibility(View.VISIBLE);
                        imageButtonLeft.setVisibility(View.VISIBLE);
                        imageButtonLeft.setImageDrawable(getResources().getDrawable(R.mipmap.replay_btn));
                        imageButtonRight.setImageDrawable(getResources().getDrawable(R.mipmap.delete_btn));
                          isRecording = false;
                    } else if (statusCenter == 2) {

                        try {
                            proceedFurtherWithAudioFile();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });


            imageButtonLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
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
                        if (isRecording)
                            pauseRecorder();
                        else {
                            resumeRecorder();
                        }

                    }

                    if (statusCenter == 2) {
                        File recording = new File(currentOutFile);
                        if (recording.exists() && recording.delete()) {
                            Toast.makeText(activity, "Recording deleted : " + currentOutFile, Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(activity, "Doesnt exist" + currentOutFile, Toast.LENGTH_SHORT).show();


                        setInitialRecordingScreen();


                    }
                }
            });

            setInitialRecordingScreen();
        } catch (Exception e) {
            Log.e("Constant.RECORDING", AppUtils.instance.getExceptionString(e));
        }
    }

    private void setInitialRecordingScreen() {
        imageButtonCenter.setImageDrawable(getResources().getDrawable(R.mipmap.start_recording_btn));
        imageButtonLeft.setVisibility(View.GONE);
        imageButtonRight.setVisibility(View.GONE);
        statusCenter = 0;
        totalSeconds = 0;
        isVideoRecorded = false;
        layoutRecording.setVisibility(View.VISIBLE);
        isRecordingLayoutVisible = true;
        textViewAudioRecordingName.setText("");
        textViewAudioRecordingTime.setText("00:00");
        textViewAudioRecordingTime.setTextSize(25);
        currentOutFile = "";
        nameFile = "";
    }

    private void setRecordingOnScreen() {

        if (!isFileMade) {
            Toast.makeText(activity, R.string.text_unable_to_create, Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String currentTimeStamp = dateFormat.format(new Date());

        nameFile = "audio_recording_" + currentTimeStamp + ".3gp";
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
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
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

                Toast.makeText(activity, "Recording saved : " + currentOutFile, Toast.LENGTH_SHORT).show();
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


    private void proceedFurtherWithAudioFile() throws Exception {

        if (AudioPlayer.getMeidaDuration() < 10000) {
            Toast.makeText(activity, getResources().getString(R.string.text_make_a_rec), Toast.LENGTH_SHORT).show();
            File recording = new File(currentOutFile);
            if (recording.exists() && recording.delete()) {
                Toast.makeText(activity, "Recording deleted : " + nameFile, Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(activity, "Doesnt exist" + nameFile, Toast.LENGTH_SHORT).show();


            AudioPlayer.stop();
            setInitialRecordingScreen();

            return;
        }


        Music music = new Music();
        music.setGerne("recordings");
        music.setIsVideo(DatabaseConstants.VALUE_FALSE);
        music.setSongAlbum("recordings");
        music.setSongsPath(currentOutFile);
        music.setSongTitle(nameFile);
        music.setThumbUrl("");

        selectIncomingOrOutgoing(music);
    }

    public void selectIncomingOrOutgoing(Music selectedMusic) {

        ArrayList<BeanDialogsOption> option = new ArrayList<BeanDialogsOption>();

        option.add(new BeanDialogsOption(getString(R.string.text_set_as_incoming), activity.getResources().getDrawable(R.mipmap.dialog_music), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogsCustom.instance.cancelDialog();
                ((ActivityBase) activity).initiateContactSelection(selectedMusic, true);
            }
        }));


        option.add(new BeanDialogsOption(getString(R.string.text_as_outgoing), activity.getResources().getDrawable(R.mipmap.dialog_video), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogsCustom.instance.cancelDialog();
                ((ActivityBase) activity).initiateContactSelection(selectedMusic, false);
            }
        }));

        DialogsCustom.instance.showOptionsDialog(activity, option, getString(R.string.text_set_an_option));


    }

}
