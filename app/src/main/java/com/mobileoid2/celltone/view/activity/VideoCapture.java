package com.mobileoid2.celltone.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mobileoid2.celltone.CustomWidget.Surface.CameraPreview;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.AppUtils;

import java.io.File;
import java.io.IOException;

public class VideoCapture extends Activity implements Camera.PreviewCallback {
    private static final long REPEAT_INTERVAL = 1000;
    private int isVideoStart =0;

    private CameraPreview mCameraPreview;
    private FrameLayout topLayout;
    public MediaRecorder mrec = new MediaRecorder();
    private ImageButton startRecording, buttonCamera, buttonDelete, buttonAssignContact;

    private TextView textViewTime;
    private Camera mCamera;
    private boolean isRecording = false;

    public static final String PATH_WITH_NAME = "PATH_WITH_NAME";

    private String pathWithName = "";
    private int whichFront = Camera.CameraInfo.CAMERA_FACING_BACK;
    private String TAG = this.getClass().getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_video_recorder);

        try {
            pathWithName = getIntent().getStringExtra(PATH_WITH_NAME);
        } catch (Exception e) {
            pathWithName = "";
            e.printStackTrace();
        }

        if (pathWithName.equalsIgnoreCase("")) {
            Toast.makeText(this, "Please provide a valid storage path.", Toast.LENGTH_SHORT).show();
            return;
        }


        Log.i(null, "Video starting");
        startRecording = (ImageButton) findViewById(R.id.buttonstart);
        buttonCamera = findViewById(R.id.button_camera);
        buttonDelete = findViewById(R.id.button_delete);
        buttonAssignContact = findViewById(R.id.button_add_to_contact);
        topLayout = findViewById(R.id.surface_camera);
        textViewTime = findViewById(R.id.text_view_time);


        startRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isRecording) {
                    stopRecording();
                } else {
                    try {
                        isVideoStart=1;
                        releaseMediaRecorder();
                        startRecording();
                        handler.post(updateTimer);
                        startRecording.setImageDrawable(getResources().getDrawable(R.mipmap.pause_btn));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopRecording();

                if (whichFront == Camera.CameraInfo.CAMERA_FACING_FRONT)
                    whichFront = Camera.CameraInfo.CAMERA_FACING_BACK;
                else
                    whichFront = Camera.CameraInfo.CAMERA_FACING_FRONT;


                inititate();

            }
        });


        buttonAssignContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tempPath = pathWithName;
                File recording = new File(pathWithName);
                if (recording.exists() && recording.delete()) {
                    Toast.makeText(getApplicationContext(), "Recording deleted : " + pathWithName, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "File doesnt exist " + pathWithName, Toast.LENGTH_SHORT).show();

                pathWithName = tempPath;
                inititate();
            }
        });

        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                inititate();
            }
        }.execute();

    }

    public void inititate() {
        buttonDelete.setVisibility(View.GONE);
        buttonAssignContact.setVisibility(View.GONE);
        buttonCamera.setVisibility(View.VISIBLE);

        if (Camera.getNumberOfCameras() == 0) {
            return;
        }
        if (Camera.getNumberOfCameras() < 2) {
            whichFront = Camera.CameraInfo.CAMERA_FACING_BACK;
            buttonCamera.setVisibility(View.GONE);
        }
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
        mCamera = Camera.open(whichFront);
        try {
            totalSeconds = 0;
            textViewTime.setText("");
            topLayout.removeAllViews();
            mCameraPreview = new CameraPreview(getApplicationContext(), mCamera);

            topLayout.addView(mCameraPreview);

        } catch (Exception exception) {
            Log.e(TAG, AppUtils.instance.getExceptionString(exception));
            mCamera.release();
            mCamera = null;
        }

    }

    protected void startRecording() throws IOException {
        buttonDelete.setVisibility(View.GONE);
        buttonAssignContact.setVisibility(View.GONE);

        buttonCamera.setVisibility(View.GONE);
        mrec = new MediaRecorder();  // Works well
        if (mCamera == null) inititate();
        mCamera.unlock();
        mrec.setCamera(mCamera);
        mrec.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mrec.setAudioSource(MediaRecorder.AudioSource.MIC);
        mrec.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        mrec.setOutputFile(pathWithName);

        mrec.prepare();
        mrec.start();
        isRecording = true;
    }

    protected void stopRecording() {
        buttonCamera.setVisibility(View.GONE);
        buttonDelete.setVisibility(View.GONE);
        buttonAssignContact.setVisibility(View.VISIBLE);
        startRecording.setImageDrawable(getResources().getDrawable(R.mipmap.record_video));
        startRecording.setVisibility(View.GONE);

        if (mrec == null) return;
        try {
            mrec.stop();
            mrec.release();
        } catch (Exception e) {
            Log.e("Video capture", AppUtils.instance.getExceptionString(e));
        }
        if (mCamera != null)
            mCamera.release();

        isRecording = false;
        mrec = null;
        mCamera = null;
        handler.removeCallbacks(updateTimer);
    }

    private void releaseMediaRecorder() {
        if (mrec != null) {
            mrec.reset();   // clear recorder configuration
            mrec.release(); // release the recorder object
            mrec = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private long totalSeconds = 0;
    private Handler handler = new Handler();
    // updates the visualizer every 1000 milliseconds
    private Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            if (isRecording) // if we are already recording
            {
                totalSeconds = totalSeconds + 1;
                VideoCapture.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewTime.setText("" + getTimeFromSeconds(totalSeconds));
                    }
                });
                handler.postDelayed(this, REPEAT_INTERVAL);
            }
        }
    };

    private String getTimeFromSeconds(long totalSeconds) {

        if (totalSeconds > 30) {
            stopRecording();
            return "";
        }

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

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

    }

    @Override
    public void onBackPressed() {

        stopRecording();

        Intent data = new Intent();
        data.putExtra("length", "" + totalSeconds);
        data.putExtra("filepath",  pathWithName);
        data.putExtra("isAudioRecord",isVideoStart);

        setResult(RESULT_OK, data);
        finish();

      //  super.onBackPressed();

    }
}
