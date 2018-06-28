package com.mobileoid2.celltone.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.LocaleHelper;
import com.mobileoid2.celltone.pojo.LanguagePojo;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;

import java.util.List;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {

    private final List<LanguagePojo> mValues;
    Context mcontext;
    private int previousItemTick = 0;


    public LanguageAdapter(Context context, List<LanguagePojo> items) {
        mcontext = context;
        mValues = items;


    }


    @Override
    public LanguageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_lamguage, parent, false);
        return new LanguageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LanguageAdapter.ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        //holder.mIdView.setText(mValues.get(position).id);
        holder.txtlanguage.setText(holder.mItem.getLanguageName());
        if (holder.mItem.getIsTcked() == 1) {
            holder.imgTick.setVisibility(View.VISIBLE);
            previousItemTick = position;
        } else
            holder.imgTick.setVisibility(View.GONE);
        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocaleHelper.setLocale(mcontext, holder.mItem.getLanguageCode());
                SharedPrefrenceHandler.getInstance().setLanguageCode(holder.mItem.getLanguageCode());
                LanguagePojo pojo = mValues.get(previousItemTick);
                pojo.setIsTcked(0);
                mValues.set(previousItemTick, pojo);
                holder.mItem.setIsTcked(1);
                mValues.set(position, holder.mItem);
                notifyDataSetChanged();




            }
        });


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //public final TextView mIdView;
        public final TextView txtlanguage;
        public final ImageView imgTick;
        public final LinearLayout llMain;
        public LanguagePojo mItem;

        public ViewHolder(View view) {
            super(view);
            txtlanguage = view.findViewById(R.id.txt_language);
            imgTick = view.findViewById(R.id.img_tick);
            llMain = view.findViewById(R.id.ll_main);


        }


    }
}



