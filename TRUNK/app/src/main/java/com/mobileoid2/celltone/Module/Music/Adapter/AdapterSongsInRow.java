package com.mobileoid2.celltone.Module.Music.Adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mobileoid2.celltone.CustomWidget.TextView.TextVeiwEuro55Regular;
import com.mobileoid2.celltone.Module.Music.Bean.Music;
import com.mobileoid2.celltone.Module.Music.Interface.InterfaceSongSelected;
import com.mobileoid2.celltone.Module.Music.Utility.ImageCapture;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.AppSharedPref;
import com.mobileoid2.celltone.Util.AppUtils;
import com.mobileoid2.celltone.database.DatabaseConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mobileoid2 on 9/11/17.
 */

public class AdapterSongsInRow extends RecyclerView.Adapter<AdapterSongsInRow.MyViewHolder> {

    private List<Music> musicList;
    private Context mcontext;
    private InterfaceSongSelected interfacePositionSelected;

    /**
     * View holder class
     */

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextVeiwEuro55Regular textViewSongName, textViewAlbumName;
        private ImageButton imageButtonSong;
        private LinearLayout layoutRoot;

        public MyViewHolder(View view) {
            super(view);
            textViewSongName = view.findViewById(R.id.textview_song_name);
            textViewAlbumName = view.findViewById(R.id.textview_album_name);
            imageButtonSong = view.findViewById(R.id.image_button_song);
            layoutRoot = (LinearLayout) view.findViewById(R.id.layout_root);

            int width = AppSharedPref.instance.getScreenWidth();
            width = width / 3;
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutRoot.setPadding(5, 5, 5, 5);
            layoutRoot.setLayoutParams(layoutParams);
            layoutRoot.setBackgroundColor(Color.BLACK);

            RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, width);
            imageButtonSong.setLayoutParams(layoutParams1);
            imageButtonSong.setBackgroundColor(Color.TRANSPARENT);

            ((ImageView) view.findViewById(R.id.image_button_background)).setLayoutParams(layoutParams1);
            ((ImageView) view.findViewById(R.id.image_button_background)).setBackground(mcontext.getResources().getDrawable(R.drawable.thumb_image));

        }
    }

    public AdapterSongsInRow(Context context, List<Music> musicList, InterfaceSongSelected interfacePositionSelected) {
        this.musicList = musicList;
        this.mcontext = context;
        this.interfacePositionSelected = interfacePositionSelected;
    }

    public void refreshList(List<Music> newList) {
        musicList = newList;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.textViewSongName.setText(musicList.get(position).getSongTitle());
        holder.textViewSongName.setSelected(true);
        holder.textViewAlbumName.setText(musicList.get(position).getSongAlbum());
        holder.textViewAlbumName.setSelected(true);
        holder.imageButtonSong.setTag(musicList.get(position));
        try {
            if (musicList.get(position).getIsVideo().equals(DatabaseConstants.VALUE_FALSE)) {
                Bitmap bitmap = ImageCapture.instance.getAlbumart(mcontext, Long.parseLong(musicList.get(position).getThumbUrl()));
                if (bitmap != null)
                    holder.imageButtonSong.setImageBitmap(bitmap);
                else
                    holder.imageButtonSong.setImageDrawable(mcontext.getResources().getDrawable(R.drawable.thumb_image));
            } else {
                Bitmap bMap = ThumbnailUtils.createVideoThumbnail(musicList.get(position).getSongsPath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                holder.imageButtonSong.setImageBitmap(bMap);
            }
        } catch (Exception ex) {
            Log.e("ADAPTER : SONGS", AppUtils.instance.getExceptionString(ex));
        }

        holder.imageButtonSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interfacePositionSelected.onSongSelected((Music) v.getTag());
            }
        });


    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    @Override
    public int getItemCount() {
        return musicList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_design_music_horizontal, parent, false);
        return new MyViewHolder(v);
    }
}
