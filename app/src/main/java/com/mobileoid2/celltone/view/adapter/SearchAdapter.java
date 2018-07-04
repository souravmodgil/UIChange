package com.mobileoid2.celltone.view.adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.ApiInterface;
import com.mobileoid2.celltone.network.model.treadingMedia.Song;
import com.mobileoid2.celltone.view.listener.IncomingOutgoingListener;
import com.mobileoid2.celltone.view.listener.OnSongsClickLisner;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>
{

    private final List<Song> mValues;
    private final OnSongsClickLisner mListener;
    private IncomingOutgoingListener incomingOutgoingListener;
    Context mcontext;
    private int isAudio;
    private int isEdit;
    private int isIncoming;
    private String mobile;
    private ApiInterface apiInterface;



    public SearchAdapter(Context context, List<Song> items, int isAudio, OnSongsClickLisner listener,
                                              IncomingOutgoingListener incomingOutgoingListener, int isEdit) {
        mcontext = context;
        mValues = items;
        mListener = listener;
        this.incomingOutgoingListener = incomingOutgoingListener;
        this.isAudio = isAudio;
        this.isEdit = isEdit;

    }


    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category, parent, false);
        return new SearchAdapter.ViewHolder(view);
    }
    public void updateAdapter(List<Song> songList)
    {
        mValues.addAll(mValues.size()-1,songList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final SearchAdapter.ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.artistName.setText(holder.mItem.getArtistName());
        //holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(holder.mItem.getTitle());
        if (holder.mItem.getContentType().equals("audio"))
            holder.mContentView.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.audio_icon,0);
        else
            holder.mContentView.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.video_icon,0);

        Glide.with(mcontext).load(ApiConstant.MEDIA_URL + holder.mItem.getClipArtUrl()).into(holder.video_thumb);
        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.playSong(position, 1, "");
            }
        });
        if (isEdit == 1) {
            holder.menuClick.setVisibility(View.GONE);
            holder.txtSet.setVisibility(View.VISIBLE);
        } else {
            holder.menuClick.setVisibility(View.VISIBLE);
            holder.txtSet.setVisibility(View.GONE);
        }
        holder.txtSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomingOutgoingListener.setMedia(holder.mItem.getId(),holder.mItem.getSampleFileUrl(),position);

            }
        });

        holder.menuClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popup = new PopupMenu(mcontext, v);

                /** Adding menu items to the popumenu */
                popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());

                /** Defining menu item click listener for the popup menu */
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //Toast.makeText(mcontext, "You selected the action : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                        switch (item.getItemId()) {
                            case R.id.nav_incoming:
                                incomingOutgoingListener.setIncomingOutComingListener(0, holder.mItem);
                                break;
                            case R.id.nav_outgoing:
                                incomingOutgoingListener.setIncomingOutComingListener(1, holder.mItem);
                                break;
                        }
                        return true;
                    }
                });
                /** Showing the popup menu */
                popup.show();
                /*if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }*/
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
        public final TextView mContentView, artistName, txtSet;
        public final ImageButton menuClick;
        public final ImageView video_thumb;
        private LinearLayout llMain;
        public Song mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            // mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            menuClick = (ImageButton) view.findViewById(R.id.menuClick);
            video_thumb = (ImageView) view.findViewById(R.id.video_thumb);
            artistName = view.findViewById(R.id.artist_name);
            txtSet = view.findViewById(R.id.txt_set);
            llMain = view.findViewById(R.id.ll_main);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
