package com.mobileoid2.celltone.Module.Music.Adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobileoid2.celltone.CustomWidget.TextView.TextVeiwEuro55Regular;
import com.mobileoid2.celltone.Module.Music.Bean.Music;
import com.mobileoid2.celltone.Module.Music.Interface.InterfaceSongSelected;
import com.mobileoid2.celltone.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mobileoid2 on 9/11/17.
 */

public class AdapterSongsGerne extends RecyclerView.Adapter<AdapterSongsGerne.MyViewHolder> {

    private List<Music> musicList;
    private Context mcontext;
    private ArrayList<String> listGernes = new ArrayList<String>();
    private InterfaceSongSelected interfacePositionSelected;

    public void refreshList(List<Music> audioList) {
        musicList = audioList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextVeiwEuro55Regular textViewGerneName;
        private RecyclerView recyclerViewSongs;
        private AdapterSongsInRow adapterSongsInRow;
        private LinearLayoutManager layoutManager;

        public MyViewHolder(View view) {
            super(view);

            textViewGerneName = view.findViewById(R.id.textview_gerne_name);
            recyclerViewSongs = view.findViewById(R.id.list_songs);
            layoutManager = new LinearLayoutManager(mcontext, LinearLayoutManager.HORIZONTAL, false);
            recyclerViewSongs.setLayoutManager(layoutManager);
            recyclerViewSongs.setItemAnimator(new DefaultItemAnimator());

        }
    }

    public AdapterSongsGerne(Context context, List<Music> musicList, InterfaceSongSelected interfacePositionSelected) {
        this.musicList = musicList;
        this.mcontext = context;
        this.interfacePositionSelected = interfacePositionSelected;


        HashSet<String> tempList = new HashSet<String>();

        for (Music object : musicList) {
            tempList.add(object.getGerne());
        }
        listGernes = new ArrayList<String>(tempList);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        List<Music> currentGerneList = new ArrayList<Music>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentGerneList = musicList.stream().filter(p -> p.getGerne().equals(listGernes.get(position))).collect(Collectors.toList());
        } else {
            for (Music music : musicList) {
                if (music.getGerne().equals(listGernes.get(position))) currentGerneList.add(music);
            }
        }

        if (holder.adapterSongsInRow == null) {
            holder.adapterSongsInRow = new AdapterSongsInRow(mcontext, currentGerneList, interfacePositionSelected);
            holder.recyclerViewSongs.setAdapter(holder.adapterSongsInRow);
        } else
            holder.adapterSongsInRow.refreshList(currentGerneList);
        holder.textViewGerneName.setText(listGernes.get(position) + "(" + currentGerneList.size() + ")");

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return listGernes.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_design_gerne, parent, false);
        return new MyViewHolder(v);
    }

}