package com.mobileoid2.celltone.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.zxing.common.StringUtils;
import com.mobileoid2.celltone.CustomWidget.TextView.OptimaBoldTextview;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.view.listener.OnListFragmentInteractionListener;
import com.mobileoid2.celltone.network.model.treadingMedia.Category;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link } and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyHomeSongsListRecyclerViewAdapter extends RecyclerView.Adapter<MyHomeSongsListRecyclerViewAdapter.ViewHolder> {

    private final List<Category> mValues;
    private final OnListFragmentInteractionListener mListener;
    Context mContext;
    Activity mActivity;
    private int isAudio ;
    public MyHomeSongsListRecyclerViewAdapter(int isAudio , List<Category> items, OnListFragmentInteractionListener listener, Context context, Activity activity) {
        mValues = items;
        mListener = listener;
        mContext=context;
        this.isAudio =isAudio;
        mActivity=activity;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_homesongslist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getTitle());

        holder.mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onListFragmentInteraction(holder.mItem,0);

            }
        });


        HorizontalRecyclerViewAdapter itemListDataAdapter =
                new HorizontalRecyclerViewAdapter(isAudio,mContext, holder.mItem.getSongs(),mActivity,mListener,position,holder.mItem);

        holder.mHorizontalRV.setHasFixedSize(true);
        holder.mHorizontalRV.setLayoutManager(new LinearLayoutManager(mContext,
                LinearLayoutManager.HORIZONTAL, false));
        holder.mHorizontalRV.setAdapter(itemListDataAdapter);

        holder.mHorizontalRV.setNestedScrollingEnabled(false);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final RecyclerView mHorizontalRV;
        public Category mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView =  view.findViewById(R.id.tvTitle);
            mContentView = view.findViewById(R.id.viewAll);
            mHorizontalRV = view.findViewById(R.id.rvHorizontal);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context,  int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }
}
