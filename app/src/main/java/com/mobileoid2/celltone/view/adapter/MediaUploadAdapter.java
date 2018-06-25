package com.mobileoid2.celltone.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.network.model.upload_media_list.UploadMediaList;

import java.util.List;

public class MediaUploadAdapter extends RecyclerView.Adapter<MediaUploadAdapter.ViewHolder> {

    private final List<UploadMediaList> mValues;
    Context mcontext;


    public MediaUploadAdapter(Context context, List<UploadMediaList> items) {
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
        holder.txtUploadMediaName.setText(holder.mItem.getTitle());
        holder.txtUploadMediaStatus.setText(holder.mItem.getStatus());
        if (holder.mItem.getContentType().equals("audio"))
            holder.txtUploadMediaName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.audio_icon,0);
        else
            holder.txtUploadMediaName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.video_icon,0);
        if (holder.mItem.getIsVerified())
            holder.mediaStatusIcon.setBackgroundResource(R.drawable.circle_green);
        else
            holder.mediaStatusIcon.setBackgroundResource(R.drawable.circle_red);


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //public final TextView mIdView;
        public final TextView txtUploadMediaDate, txtUploadMediaName, txtUploadMediaStatus;
        public final ImageView  mediaStatusIcon;
        public UploadMediaList mItem;

        public ViewHolder(View view) {
            super(view);
            txtUploadMediaDate = view.findViewById(R.id.txt_upload_media_date);
            txtUploadMediaName = view.findViewById(R.id.txt_upload_media_name);
            mediaStatusIcon = view.findViewById(R.id.media_status_icon);
            txtUploadMediaStatus = view.findViewById(R.id.txt_upload_media_status);


        }


    }
}


