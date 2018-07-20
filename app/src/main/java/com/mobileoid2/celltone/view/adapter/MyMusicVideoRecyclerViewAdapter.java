package com.mobileoid2.celltone.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.Constant;
import com.mobileoid2.celltone.view.listener.OnListFragmentInteractionListener;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.model.treadingMedia.Song;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link } and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyMusicVideoRecyclerViewAdapter extends RecyclerView.Adapter<MyMusicVideoRecyclerViewAdapter.ViewHolder> {

    private final List<Song> mValues;
    private final  OnListFragmentInteractionListener mListener;
    Context mcontext;
    private  int isAudio;

    public MyMusicVideoRecyclerViewAdapter(Context context, List<Song> items, int isAudio,OnListFragmentInteractionListener listener) {
        mcontext = context;
        mValues = items;
        mListener = listener;
        this.isAudio =isAudio;
        try {
            Constant.TEMP_VIDEO_PATH = items.get(0).getClipArtUrl();
        } catch (Exception e) {

        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_musicvideo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        //holder.mIdView.setText(mValues.get(position).id);

        holder.mContentView.setText(holder.mItem.getTitle());
        if(holder.mItem.getClipArtUrl()!=null)
        Glide.with(mcontext).load(ApiConstant.MEDIA_URL + holder.mItem.getClipArtUrl()).into(holder.video_thumb);
        else
            holder.video_thumb.setImageResource(R.drawable.thumb_image);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onListFragmentInteraction(null,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //public final TextView mIdView;
        public final TextView mContentView;
        public final ImageView video_thumb;
        public Song mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView =  view.findViewById(R.id.content);
            video_thumb =  view.findViewById(R.id.video_thumb);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
