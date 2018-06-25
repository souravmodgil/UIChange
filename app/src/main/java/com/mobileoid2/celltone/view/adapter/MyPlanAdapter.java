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
import com.mobileoid2.celltone.network.model.myPlan.AllPlan;
import com.mobileoid2.celltone.view.listener.PlanListiner;

import java.util.List;

public class MyPlanAdapter extends RecyclerView.Adapter<MyPlanAdapter.ViewHolder> {

    private final List<AllPlan> mValues;
    Context mcontext;
    private int previousePlanSelected = -1;
    private PlanListiner planListiner;


    public MyPlanAdapter(Context context, List<AllPlan> items,PlanListiner planListiner) {
        mcontext = context;
        mValues = items;
        this.planListiner =planListiner;

    }


    @Override
    public MyPlanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_layout, parent, false);
        return new MyPlanAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyPlanAdapter.ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        //holder.mIdView.setText(mValues.get(position).id);
        holder.txtPlanName.setText(holder.mItem.getName() + ":");
        holder.txtPlanPrice.setText("$ " + holder.mItem.getPrice());
        holder.txtContactsCount.setText(holder.mItem.getUserCount() + " Contacts");
        holder.txtValidity.setText("1 Month");
        holder.txtOwnMediaUpload.setText(holder.mItem.getOwnMediaCount() + " Upload own media");
        holder.txtMediaCount.setText(holder.mItem.getMediaCount() + " media");
        if (holder.mItem.getIsSelected() == 0)
            holder.redioButton.setImageResource(R.drawable.radio_btn_uncheck);
        else
            holder.redioButton.setImageResource(R.drawable.radio_btn_checked);
        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                planListiner.selectedPlan(position);
                if (previousePlanSelected == -1) {
                    holder.mItem.setIsSelected(1);
                    previousePlanSelected = position;
                    mValues.set(position, holder.mItem);
                } else {
                    AllPlan allPlan = mValues.get(previousePlanSelected);
                    allPlan.setIsSelected(0);
                    mValues.set(previousePlanSelected, allPlan);
                    holder.mItem.setIsSelected(1);
                    mValues.set(position, holder.mItem);
                    previousePlanSelected =position;
                }
                notifyDataSetChanged();
            }
        });


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout llRedioButton, llMain;
        ImageView redioButton;
        //public final TextView mIdView;
        public final TextView txtPlanName, txtPlanPrice, txtValidity, txtMediaCount, txtContactsCount, txtOwnMediaUpload;

        public AllPlan mItem;

        public ViewHolder(View view) {
            super(view);
            txtPlanName = view.findViewById(R.id.txt_plan_name);
            txtPlanPrice = view.findViewById(R.id.txt_plan_price);
            txtValidity = view.findViewById(R.id.txt_validity);
            txtMediaCount = view.findViewById(R.id.txt_media_count);
            txtContactsCount = view.findViewById(R.id.txt_contacts_count);
            txtOwnMediaUpload = view.findViewById(R.id.txt_own_media_upload);
            llRedioButton = view.findViewById(R.id.ll_redio_button);
            llMain = view.findViewById(R.id.ll_main);
            redioButton =view.findViewById(R.id.redio_button);

        }


    }
}

