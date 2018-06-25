package com.mobileoid2.celltone.Module.Music.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import com.mobileoid2.celltone.Util.ImageGetter;
import com.mobileoid2.celltone.database.DatabaseConstants;

import java.util.HashSet;
import java.util.List;

/**
 * Created by mobileoid2 on 9/11/17.
 */

public class AdapterMusicList extends RecyclerView.Adapter<AdapterMusicList.MyViewHolder> {

    private List<Music> musicList;
    private Context mcontext;
    private boolean isSearch;
    private HashSet<ImageGetter> uploaders = new HashSet<ImageGetter>();
    private InterfaceSongSelected interfacePositionSelected;

    public void refreshList(List<Music> refreshedList) {

        if (uploaders.size() > 0) {
            for (ImageGetter upPic : uploaders) {
                if (upPic != null)
                    if (upPic.getStatus() == AsyncTask.Status.RUNNING) upPic.cancel(true);
            }
            uploaders.clear();
        }


        musicList = refreshedList;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        musicList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount() - position);
    }

    public void cleanCacheWork() {
        if (uploaders.size() > 0) {
            for (ImageGetter upPic : uploaders) {
                if (upPic != null)
                    if (upPic.getStatus() == AsyncTask.Status.RUNNING) {
                        upPic.cancel(true);
                        upPic = null;
                    }
            }
            uploaders.clear();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextVeiwEuro55Regular textViewSongTitle;
        public TextVeiwEuro55Regular textViewSongAlbum;
        public ImageView imageViewThumbnail;
        private LinearLayout layoutSongDetails;
        private ImageButton imageButtonSelect;


        public MyViewHolder(View view) {
            super(view);

            textViewSongTitle = view.findViewById(R.id.textview_name);
            textViewSongAlbum = view.findViewById(R.id.textview_number);
            imageViewThumbnail = view.findViewById(R.id.img_thumb);
            layoutSongDetails = view.findViewById(R.id.layout_song_details);
            imageButtonSelect = view.findViewById(R.id.image_button_select);


            int width = AppSharedPref.instance.getScreenWidth();
            width = width / 8;
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, width);
            layoutParams.setMargins(0, 0, 20, 0);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            imageViewThumbnail.setLayoutParams(layoutParams);

            layoutParams = new RelativeLayout.LayoutParams(width, width);
            layoutParams.setMargins(0, 0, 0, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            imageButtonSelect.setLayoutParams(layoutParams);


        }
    }

    public AdapterMusicList(Context context, List<Music> musicList, InterfaceSongSelected interfacePositionSelected, boolean isSearch) {
        this.musicList = musicList;
        this.mcontext = context;
        this.isSearch = isSearch;
        this.interfacePositionSelected = interfacePositionSelected;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        Music musicDetails = musicList.get(position);
        holder.textViewSongTitle.setText(musicDetails.getSongTitle());
        holder.textViewSongAlbum.setText(musicDetails.getSongAlbum());
        holder.layoutSongDetails.setTag(position);
        holder.imageButtonSelect.setTag(position);
        holder.textViewSongTitle.setSelected(true);
        holder.textViewSongAlbum.setSelected(true);

        try {

            if (musicDetails.getIsVideo().equals(DatabaseConstants.VALUE_FALSE)) {
                Bitmap bitmap = ImageCapture.instance.getAlbumart(mcontext, Long.parseLong(musicList.get(position).getThumbUrl()));
                if (bitmap != null)
                    holder.imageViewThumbnail.setImageBitmap(bitmap);
                else
                    holder.imageViewThumbnail.setImageDrawable(mcontext.getResources().getDrawable(R.drawable.thumb_image));
            } else {
                ImageGetter instance = new ImageGetter(musicDetails.getSongsPath(), holder.imageViewThumbnail, new ImageGetter.interfaceOnImageSet() {
                    @Override
                    public void onImageSettingsFinished(ImageGetter imageGetterInstance) {
                        if (uploaders != null)
                            if (uploaders.contains(imageGetterInstance)) {
                                uploaders.remove(imageGetterInstance);
                            }
                    }
                });
                uploaders.add(instance);
                instance.execute();
            }
        } catch (Exception ex) {
            Log.e("ADAPTER : SONGS", AppUtils.instance.getExceptionString(ex));
        }


        holder.layoutSongDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSearch)
                    interfacePositionSelected.onSongSelected(musicList.get((Integer) v.getTag()));
                else
                    interfacePositionSelected.onSongSelected((Integer) v.getTag(), false);
            }
        });

        holder.imageButtonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSearch)
                    interfacePositionSelected.onSongSelected(musicList.get((Integer) v.getTag()));
                else
                    interfacePositionSelected.onSongSelected((Integer) v.getTag(), true);
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_design_music, parent, false);
        return new MyViewHolder(v);
    }
}
