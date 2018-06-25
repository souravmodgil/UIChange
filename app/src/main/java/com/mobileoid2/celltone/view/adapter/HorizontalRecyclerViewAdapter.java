package com.mobileoid2.celltone.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.model.treadingMedia.Category;
import com.mobileoid2.celltone.network.model.treadingMedia.Song;
import com.mobileoid2.celltone.pojo.audio.Body;
import com.mobileoid2.celltone.view.listener.OnListFragmentInteractionListener;


import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HorizontalRecyclerViewAdapter extends RecyclerView.Adapter<HorizontalRecyclerViewAdapter.HorizontalViewHolder> {

    private Context mContext;
    private List<Song> mArrayList;
    private int isAudio ;
    private Category category;
    private OnListFragmentInteractionListener onListFragmentInteractionListener;
    private int poistion;
    Activity mActivity;

    public HorizontalRecyclerViewAdapter(int isAudio, Context mContext, List<Song> mArrayList, Activity activity,
                                         OnListFragmentInteractionListener  onListFragmentInteractionListener, int poistion, Category category) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.mActivity = activity;
        this.category =category;
        this.onListFragmentInteractionListener =onListFragmentInteractionListener;
        this.poistion =poistion;
        this.isAudio=isAudio;
    }




    @Override
    public HorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal, parent, false);
        return new HorizontalViewHolder(view);
    }

    AlertDialog alertDialog;

    @Override
    public void onBindViewHolder(HorizontalViewHolder holder,final int position) {
        final Song current = mArrayList.get(position);
        holder.txtTitle.setText(current.getTitle());
        holder.txtSubTitle.setText(current.getArtistName());
        //holder.txtTitle.setTypeface(null, Typeface.ITALIC);

        if (current.getContentType().equalsIgnoreCase("video"))
        Glide.with(mContext).load(ApiConstant.MEDIA_URL+current.getClipArtUrl()).into(holder.ivThumb);
        else Glide.with(mContext).load(ApiConstant.MEDIA_URL+current.getClipArtUrl()).into(holder.ivThumb);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    Toast.makeText(mContext, current.getTitle(), Toast.LENGTH_SHORT).show();
                onListFragmentInteractionListener.onListFragmentInteraction(category,position);
                /*--------------------------------*/
                /*if (alertDialog == null) {
                    playVideoInDialog(current);
                } else {
                    alertDialog.dismiss();
                    alertDialog=null;
                    playVideoInDialog(current);
                }
*/
            }
        });
    }

    private void playVideoInDialog(Body current) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext,R.style.Theme_Dialog);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_video, null);
        dialogBuilder.setView(dialogView);
        VideoView customVideoView = (VideoView) dialogView.findViewById(R.id.video);
        Uri video = Uri.fromFile(new File(current.getOriginalFileUrl()));

        customVideoView.setVideoURI(video);
        customVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                customVideoView.start();
            }
        });
        alertDialog = dialogBuilder.create();
        alertDialog.show();
       // alertDialog.getWindow().setBackgroundDrawable(null);
        alertDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, 400);
        alertDialog.getWindow().setGravity(Gravity.TOP);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alertDialog.getWindow();
        lp.copyFrom(window.getAttributes());
//This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = 400;
        window.setAttributes(lp);

    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class HorizontalViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txtTitle)
        TextView txtTitle;
        @BindView(R.id.txtSubTitle)
        TextView txtSubTitle;
        @BindView(R.id.ivThumb)
        ImageView ivThumb;

        public HorizontalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
