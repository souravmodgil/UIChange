package com.mobileoid2.celltone.view.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.VideoPlayer;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.model.upload_media_list.UploadMediaList;

import java.util.List;

public class MediaUploadAdapter extends RecyclerView.Adapter<MediaUploadAdapter.ViewHolder> {

    private final List<UploadMediaList> mValues;
    Activity mcontext;


    public MediaUploadAdapter(Activity context, List<UploadMediaList> items) {
        mcontext = context;
        mValues = items;


    }


    @Override
    public MediaUploadAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_upload, parent, false);
        return new MediaUploadAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MediaUploadAdapter.ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        //holder.mIdView.setText(mValues.get(position).id);
        holder.txtUploadMediaDate.setText(holder.mItem.getCreatedAt());
        holder.txtUploadMediaName.setText(holder.mItem.getTitle().trim());
        holder.txtUploadMediaStatus.setText(holder.mItem.getStatus());
        if (holder.mItem.getContentType().equals("audio"))
            holder.txtUploadMediaName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.audio_icon,0);
        else
            holder.txtUploadMediaName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.video_icon,0);
        if (holder.mItem.getIsVerified())
            holder.mediaStatusIcon.setBackgroundResource(R.drawable.circle_green);
        else
            holder.mediaStatusIcon.setBackgroundResource(R.drawable.circle_red);
        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(holder.mItem.getSampleFileUrl());
            }
        });


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private void showPopup(String url)
    {
        final Dialog dialog = new Dialog(mcontext);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_video_dialog);
        VideoView videoView = dialog.findViewById(R.id.video_view);
        TextView txtClose = dialog.findViewById(R.id.txt_close);
        final ProgressBar progressBar = dialog.findViewById(R.id.media_player_progress_bar);

        VideoPlayer.getInstance(mcontext, videoView);
        Uri vidUri = Uri.parse(ApiConstant.MEDIA_URL + url);
        VideoPlayer.getInstance(mcontext, videoView);
        videoView.setVideoURI(vidUri);
        videoView.start();
        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                videoView.stopPlayback();
            }
        });
        dialog.show();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // TODO Auto-generated method stub


                progressBar.setVisibility(View.GONE);

                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int arg1,
                                                   int arg2) {
                        // TODO Auto-generated method stub

                    }
                });
            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //public final TextView mIdView;
        public final TextView txtUploadMediaDate, txtUploadMediaName, txtUploadMediaStatus;
        public final ImageView  mediaStatusIcon;
        public UploadMediaList mItem;
        public LinearLayout llMain;

        public ViewHolder(View view) {
            super(view);
            txtUploadMediaDate = view.findViewById(R.id.txt_upload_media_date);
            txtUploadMediaName = view.findViewById(R.id.txt_upload_media_name);
            mediaStatusIcon = view.findViewById(R.id.media_status_icon);
            txtUploadMediaStatus = view.findViewById(R.id.txt_upload_media_status);
            llMain =view.findViewById(R.id.ll_main);


        }


    }
}


