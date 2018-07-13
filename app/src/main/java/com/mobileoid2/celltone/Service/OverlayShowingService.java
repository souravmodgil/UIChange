package com.mobileoid2.celltone.Service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Patterns;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.mobileoid2.celltone.Util.Constant;
import com.mobileoid2.celltone.database.AppDatabase;
import com.mobileoid2.celltone.database.RingtoneEntity;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.pojo.getmedia.Body;
import com.mobileoid2.celltone.pojo.getmedia.PojoGETMediaResponse;
import com.mobileoid2.celltone.pojo.getmedia.UserId;
import com.mobileoid2.celltone.utility.Config_URL;
import com.mobileoid2.celltone.utility.CustomVideoView;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;
import com.mobileoid2.celltone.utility.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class OverlayShowingService extends Service implements OnTouchListener, OnClickListener {

    private View topLeftView;
    private LinearLayout overlayedLayout;
    private float offsetX;
    private float offsetY;
    private int originalXPos;
    private int originalYPos;
    private boolean moving;
    private WindowManager windowManager;
    private CustomVideoView videoView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!Utils.isAccessibilitySettingsOn(this)) {
            Toast.makeText(this, "Switch on Accessibility for this application", Toast.LENGTH_SHORT).show();
        }

        if (Constant.PHONENUMBER == null && Constant.PHONENUMBER.isEmpty() && !
                SharedPrefrenceHandler.getInstance().getLoginState()
                ) {
            stopVideoAndService();
            return;
        }


        Constant.PHONENUMBER = Constant.PHONENUMBER.replaceAll("[^+0-9]", "");
        if (!Constant.PHONENUMBER.substring(0, 1).equals("+")) {

            if (Constant.PHONENUMBER.substring(0, 1).equals("0"))
                Constant.PHONENUMBER = Constant.PHONENUMBER.substring(1);
            Constant.PHONENUMBER = SharedPrefrenceHandler.getInstance().getCOUTRYCODE() + Constant.PHONENUMBER;

        }


        if (!Patterns.PHONE.matcher(Constant.PHONENUMBER).matches()) {
            stopVideoAndService();
            return;
        }

//        if (Constant.PHONENUMBER.length() > 9) {
//            Constant.PHONENUMBER = Constant.PHONENUMBER.substring(Constant.PHONENUMBER.length() - 9);
//        }
        PojoGETMediaResponse pojoContactsUploadResonse =
                Arrays.asList(new Gson().fromJson(SharedPrefrenceHandler.getInstance().getGET_MEDIA_RESPONSE(),
                        PojoGETMediaResponse.class)).get(0);
        Body body = new Body();
        body.setUserId(new UserId());
        body.getUserId().setMobile(Constant.PHONENUMBER);
        if (pojoContactsUploadResonse != null
                && pojoContactsUploadResonse.getBody() != null
                && pojoContactsUploadResonse.getBody().contains(body)) {

            Body body1 = pojoContactsUploadResonse.getBody().get(pojoContactsUploadResonse.getBody().indexOf(body));
            File path = null;
            if (body1.getOutgoing() != null && !Constant.isIncoming) {
                File file = new File(Utils.getFilePath(this) + "/" + body1.getOutgoing().getSampleFileUrl());
                if (file.exists()) {
                    //path=Utils.getFilePath(this) + "/" + body1.getOutgoing().getSampleFileUrl();
                    path = file;
                }
            }


            if (path == null) {
                stopVideoAndService();
                return;
            }
            windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            overlayedLayout = new LinearLayout(this);
            overlayedLayout.setOnClickListener(this);
            overlayedLayout.setOnTouchListener(this);
            overlayedLayout.setAlpha(0.0f);

            overlayedLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 300));
            overlayedLayout.setOrientation(LinearLayout.VERTICAL);
            overlayedLayout.setBackgroundColor(Color.RED);
            overlayedLayout.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300));

            if (path.getPath().endsWith("mp4")) {
                videoView = new CustomVideoView(this);
                videoView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300));
                videoView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                //videoView.getLayoutParams().height = 300;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    videoView.setForegroundGravity(Gravity.CENTER);
                }
                Uri video = Uri.fromFile(path);
                videoView.setVideoURI(video);
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                        mp.start();
                    }
                });
                overlayedLayout.addView(videoView);


            } else {

                ImageView imageView = new ImageView(this);


                Glide.with(this).load(ApiConstant.MEDIA_URL + body1.getOutgoing().getClipArtUrl()).
                        into(imageView);

                imageView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300));
                imageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                overlayedLayout.addView(imageView);
                mediaPlayer = new MediaPlayer();

                try {
                    mediaPlayer.setDataSource(path.getPath());
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    mediaPlayer.prepare();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mediaPlayer.start();


            }


            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_SYSTEM_ALERT, LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.CENTER;
            params.x = 0;
            params.y = 0;
            windowManager.addView(overlayedLayout, params);

            topLeftView = new View(this);
            LayoutParams topLeftParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_SYSTEM_ALERT, LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
            topLeftParams.gravity = Gravity.CENTER;
            topLeftParams.x = 0;
            topLeftParams.y = 0;
            topLeftParams.width = 0;
            topLeftParams.height = 0;
            windowManager.addView(topLeftView, topLeftParams);
        } else {
            if (!Constant.isIncoming) {
                File path = null;
                AppDatabase appDatabase = AppDatabase.getAppDatabase(this);
                RingtoneEntity ringtoneEntity = appDatabase.daoRingtone().getContatcByNumber(Constant.PHONENUMBER);
                if (ringtoneEntity == null)
                    return;
                File file = new File(Utils.getFilePath(this) + "/" + ringtoneEntity.getSampleFileUrl());
                if (file.exists())
                    path = file;

                if (path == null) {
                    stopVideoAndService();
                    return;
                }
                windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                overlayedLayout = new LinearLayout(this);
                overlayedLayout.setOnClickListener(this);
                overlayedLayout.setOnTouchListener(this);
                overlayedLayout.setAlpha(0.0f);

                overlayedLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 300));
                overlayedLayout.setOrientation(LinearLayout.VERTICAL);
                overlayedLayout.setBackgroundColor(Color.RED);
                overlayedLayout.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300));

                if (path.getPath().endsWith("mp4")) {
                    videoView = new CustomVideoView(this);
                    videoView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300));
                    videoView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                    //videoView.getLayoutParams().height = 300;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        videoView.setForegroundGravity(Gravity.CENTER);
                    }
                    Uri video = Uri.fromFile(path);
                    // Uri vidUri = Uri.parse(ApiConstant.MEDIA_URL + ringtoneEntity.getSampleFileUrl());
                    videoView.setVideoURI(video);
                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.setLooping(true);
                            videoView.start();
                        }
                    });
                    overlayedLayout.addView(videoView);

               /* ImageView imageView=new ImageView(this);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.adduser_icon));
                imageView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300));
                imageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    imageView.setForegroundGravity(Gravity.CENTER);
                }
                overlayedLayout.addView(imageView);*/


                } else {

                    ImageView imageView = new ImageView(this);
//                    if (Constant.isIncoming) {
//                        Glide.with(this).load(Config_URL.MEDIA_URL + ringtoneEntity.getClipArtUrl()).
//                                into(imageView);
//                    } else {
//
//                        Glide.with(this).load(Config_URL.MEDIA_URL + ringtoneEntity.get()).
//                                into(imageView);
//                    }
                    imageView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300));
                    imageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                    overlayedLayout.addView(imageView);
                    mediaPlayer = new MediaPlayer();

                    try {
                        mediaPlayer.setDataSource(path.getPath());
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    try {
                        mediaPlayer.prepare();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mediaPlayer.start();


                }


                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_SYSTEM_ALERT, LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
                params.gravity = Gravity.CENTER;
                params.x = 0;
                params.y = 0;
                windowManager.addView(overlayedLayout, params);

                topLeftView = new View(this);
                LayoutParams topLeftParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_SYSTEM_ALERT, LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
                topLeftParams.gravity = Gravity.CENTER;
                topLeftParams.x = 0;
                topLeftParams.y = 0;
                topLeftParams.width = 0;
                topLeftParams.height = 0;
                windowManager.addView(topLeftView, topLeftParams);


            }

        }
    }


    MediaPlayer mediaPlayer;

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*if (overlayedLayout != null) {
            windowManager.removeView(overlayedLayout);
            windowManager.removeView(topLeftView);
            overlayedLayout = null;
            topLeftView = null;
        }*/
        if (videoView != null) {
            videoView.stopPlayback();
            videoView.setVisibility(View.GONE);
            //windowManager.removeView(overlayedLayout);

        }
        ;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getRawX();
            float y = event.getRawY();

            moving = false;

            int[] location = new int[2];
            overlayedLayout.getLocationOnScreen(location);

            originalXPos = location[0];
            originalYPos = location[1];

            offsetX = originalXPos - x;
            offsetY = originalYPos - y;

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int[] topLeftLocationOnScreen = new int[2];
            topLeftView.getLocationOnScreen(topLeftLocationOnScreen);

            System.out.println("topLeftY=" + topLeftLocationOnScreen[1]);
            System.out.println("originalY=" + originalYPos);

            float x = event.getRawX();
            float y = event.getRawY();

            WindowManager.LayoutParams params = (LayoutParams) overlayedLayout.getLayoutParams();

            int newX = (int) (offsetX + x);
            int newY = (int) (offsetY + y);

            if (Math.abs(newX - originalXPos) < 1 && Math.abs(newY - originalYPos) < 1 && !moving) {
                return false;
            }

            params.x = newX - (topLeftLocationOnScreen[0]);
            params.y = newY - (topLeftLocationOnScreen[1]);

            windowManager.updateViewLayout(overlayedLayout, params);
            moving = true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (moving) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        stopVideoAndService();
    }

    private void stopVideoAndService() {
        if (videoView != null) {
            videoView.stopPlayback();
            videoView.setVisibility(View.GONE);
            // windowManager.removeView(overlayedLayout);
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            ;

        }
        stopService(new Intent(this, OverlayShowingService.class));
    }

}