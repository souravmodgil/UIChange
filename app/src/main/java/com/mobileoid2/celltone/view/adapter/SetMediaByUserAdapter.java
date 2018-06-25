package com.mobileoid2.celltone.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.network.model.setMediaByUser.Media;
import com.mobileoid2.celltone.network.model.setMediaByUser.SetMediaByUserBody;
import com.mobileoid2.celltone.network.model.upload_media_list.UploadMediaList;

import java.util.List;

public class SetMediaByUserAdapter extends RecyclerView.Adapter<SetMediaByUserAdapter.ViewHolder> {

    private final List<SetMediaByUserBody> mValues;
    Context mcontext;


    public SetMediaByUserAdapter(Context context, List<SetMediaByUserBody> items) {
        mcontext = context;
        mValues = items;


    }


    @Override
    public SetMediaByUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_media, parent, false);
        return new SetMediaByUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SetMediaByUserAdapter.ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        //holder.mIdView.setText(mValues.get(position).id);
        final Media media = holder.mItem.getMedia();
        holder.txtUploadMediaName.setText(media.getTitle());
        holder.txtUploadMediaStatus.setText(""+holder.mItem.getCount());
        if (media.getContentType().equals("audio"))
            holder.txtUploadMediaName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.audio_icon,0);
        else
            holder.txtUploadMediaName.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.video_icon,0);


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //public final TextView mIdView;
        public final TextView txtUploadMediaName, txtUploadMediaStatus;
        public SetMediaByUserBody mItem;

        public ViewHolder(View view) {
            super(view);

            txtUploadMediaName = view.findViewById(R.id.txt_upload_media_name);
            txtUploadMediaStatus = view.findViewById(R.id.txt_upload_media_status);


        }


    }
}


