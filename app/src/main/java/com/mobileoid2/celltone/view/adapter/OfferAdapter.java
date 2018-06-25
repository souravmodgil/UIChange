package com.mobileoid2.celltone.view.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.network.model.offerPlan.OfferBody;
import com.mobileoid2.celltone.view.listener.PlanListiner;

import java.util.List;

public class OfferAdapter  extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {

    private final List<OfferBody> mValues;
    Context mcontext;
    PlanListiner planListiner;




    public OfferAdapter(Context context, List<OfferBody> items,PlanListiner planListiner) {
        mcontext = context;
        mValues = items;
       this.planListiner =planListiner;


    }


    @Override
    public OfferAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_offer_row, parent, false);
        return new OfferAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OfferAdapter.ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        //holder.mIdView.setText(mValues.get(position).id);
        holder.offerName.setText(holder.mItem.getTitle());
        holder.offerDecription.setText(holder.mItem.getDescription());
        holder.applyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                planListiner.selectedPlan(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //public final TextView mIdView;
        public final TextView offerName,offerDecription;
        public final AppCompatButton applyNow;
        public OfferBody mItem;

        public ViewHolder(View view) {
            super(view);
            offerName = view.findViewById(R.id.offer_name);
            offerDecription =view.findViewById(R.id.offer_decription);
            applyNow = view.findViewById(R.id.apply_now);



        }


    }
}

