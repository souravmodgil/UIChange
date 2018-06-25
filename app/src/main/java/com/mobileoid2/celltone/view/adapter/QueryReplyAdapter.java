package com.mobileoid2.celltone.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.network.model.feedback.Comment;
import com.mobileoid2.celltone.network.model.feedback.FeedBackList;
import com.mobileoid2.celltone.network.model.feedback.FeedBackModel;
import com.mobileoid2.celltone.utility.Utils;
import com.mobileoid2.celltone.view.listener.QueryReplyListiner;

import java.util.List;

public class QueryReplyAdapter extends RecyclerView.Adapter<QueryReplyAdapter.ViewHolder> {

    private final List<Comment> mValues;
    Context mcontext;
    private String name;


    public QueryReplyAdapter(Context context, List<Comment> items, String userName) {
        mcontext = context;
        mValues = items;
        name = userName;


    }


    @Override
    public QueryReplyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_query_reply, parent, false);
        return new QueryReplyAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final QueryReplyAdapter.ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);


        holder.txtDiscription.setText(holder.mItem.getReply());
        if (holder.mItem.getFrom().equals("user"))
            holder.txtUserName.setText(name);
        else
            holder.txtUserName.setText(mcontext.getString(R.string.kolbeat_support));
        holder.txtCommentDate.setText(Utils.parseDate(holder.mItem.getCreatedAt()));


        // issue


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //public final TextView mIdView;
        public final TextView txtUserName, txtCommentDate, txtDiscription;
        public Comment mItem;


        public ViewHolder(View view) {
            super(view);


            txtUserName = view.findViewById(R.id.txt_user_name);
            txtCommentDate = view.findViewById(R.id.txt_comment_date);
            txtDiscription = view.findViewById(R.id.txt_discription);


        }


    }
}



