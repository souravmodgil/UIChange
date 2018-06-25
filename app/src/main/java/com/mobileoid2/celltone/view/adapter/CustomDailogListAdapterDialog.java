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
import com.mobileoid2.celltone.pojo.PopUpPojo;
import com.mobileoid2.celltone.view.listener.UpdateDailogView;

import java.util.List;

public class CustomDailogListAdapterDialog extends RecyclerView.Adapter<CustomDailogListAdapterDialog.ViewHolder>  {

    private final List<PopUpPojo> mValues;
    Context mcontext;
    private int previousSelcted =0;
    private UpdateDailogView updateDailogView;


    public CustomDailogListAdapterDialog(Context context, List<PopUpPojo> items,UpdateDailogView updateDailogView) {
        mcontext = context;
        mValues = items;
        this.updateDailogView =updateDailogView;


    }


    @Override
    public CustomDailogListAdapterDialog.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dailog_outgoing, parent, false);
        return new CustomDailogListAdapterDialog.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomDailogListAdapterDialog.ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        //holder.mIdView.setText(mValues.get(position).id);
       if(!holder.mItem.getTrackName().isEmpty()) {

           holder.txtTitle.setText(holder.mItem.getTitle() + " (" + holder.mItem.getTrackName() + ")");
           if (holder.mItem.getIsAudio()==1)
               holder.txtTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.audio_icon,0);
           else
               holder.txtTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.video_icon,0);
       }
       else
           holder.txtTitle.setText(holder.mItem.getTitle());
       if(holder.mItem.getIsSelected()==1) {
           holder.redioButton.setBackgroundResource(R.drawable.radio_btn_checked);
       }
       else if(holder.mItem.getIsSelected()==0) {
            holder.redioButton.setBackgroundResource(R.drawable.radio_btn_uncheck);
        }
        holder.redioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(previousSelcted !=position && previousSelcted>=0)
                {

                    PopUpPojo popUpPojo =mValues.get(previousSelcted);
                    popUpPojo.setIsSelected(0);
                    mValues.set(previousSelcted,popUpPojo);
                    previousSelcted =position;
                    holder.mItem.setIsSelected(1);
                    mValues.set(previousSelcted,holder.mItem);
                    notifyDataSetChanged();
                    if(!holder.mItem.getTrackName().isEmpty())
                        updateDailogView.updateView(1,position);
                    else
                        updateDailogView.updateView(0,position);
                }


            }
        });





    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //public final TextView mIdView;
        public final TextView txtTitle;
        public final ImageView redioButton;
        public PopUpPojo mItem;

        public ViewHolder(View view) {
            super(view);
            txtTitle = view.findViewById(R.id.txt_title);
            redioButton = view.findViewById(R.id.redio_button);


        }


    }
}


