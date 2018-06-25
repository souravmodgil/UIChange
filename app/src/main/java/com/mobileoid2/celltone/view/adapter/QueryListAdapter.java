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
import com.mobileoid2.celltone.network.model.setMediaByUser.Media;
import com.mobileoid2.celltone.network.model.setMediaByUser.SetMediaByUserBody;
import com.mobileoid2.celltone.utility.Utils;
import com.mobileoid2.celltone.view.listener.QueryReplyListiner;

import java.util.List;

public class QueryListAdapter extends RecyclerView.Adapter<QueryListAdapter.ViewHolder> {

    private final List<FeedBackList> mValues;
    Context mcontext;
    private FeedBackModel feedBackModel;
    private QueryReplyListiner queryReplyListiner;


    public QueryListAdapter(Context context, List<FeedBackList> items, FeedBackModel feedBackModel, QueryReplyListiner queryReplyListiner) {
        mcontext = context;
        mValues = items;
        this.feedBackModel=feedBackModel;
        this.queryReplyListiner =queryReplyListiner;


    }


    @Override
    public QueryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_query_list, parent, false);
        return new QueryListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final QueryListAdapter.ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);

        holder.txtTitle.setText(holder.mItem.getTitle());
        holder.dateQuery.setText(Utils.parseDate(holder.mItem.getCreatedAt()));
        if (holder.mItem.getQueryType().equals("query"))
            holder.txtType.setBackground(mcontext.getDrawable(R.drawable.query_shape));
        else
            holder.txtType.setBackground(mcontext.getDrawable(R.drawable.issue_shape));
        holder.txtType.setText(holder.mItem.getQueryType());
        if(holder.mItem.getComments().size()>0) {
            int index = holder.mItem.getComments().size() - 1;
            Comment comment = holder.mItem.getComments().get(index);
            holder.txtDiscription.setText(comment.getReply());
            if(holder.mItem.getComments().get(index).getFrom().equals("user"))
            holder.txtUserName.setText(holder.mItem.getUserId().getName());
            else
                holder.txtUserName.setText(mcontext.getString(R.string.kolbeat_support));
            holder.txtCommentDate.setText(Utils.parseDate(comment.getCreatedAt()));

        }
        else
        {
            holder.txtDiscription.setText(holder.mItem.getDescription());
            holder.txtUserName.setText(holder.mItem.getUserId().getName());
            holder.txtCommentDate.setText(Utils.parseDate(holder.mItem.getCreatedAt()));
        }
        holder.llReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryReplyListiner.onReply(holder.mItem);
            }
        });


        // issue


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //public final TextView mIdView;
        public final TextView txtTitle, txtType, dateQuery, txtUserName, txtCommentDate, txtDiscription;
        public FeedBackList mItem;
        public  final LinearLayout llReply;

        public ViewHolder(View view) {
            super(view);

            txtTitle = view.findViewById(R.id.txt_title);
            txtType = view.findViewById(R.id.txt_type);
            dateQuery = view.findViewById(R.id.date_query);
            txtUserName = view.findViewById(R.id.txt_user_name);
            txtCommentDate = view.findViewById(R.id.txt_comment_date);
            txtDiscription = view.findViewById(R.id.txt_discription);
            llReply = view.findViewById(R.id.ll_reply);


        }


    }
}



