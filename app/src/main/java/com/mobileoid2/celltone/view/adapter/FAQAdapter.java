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
import com.mobileoid2.celltone.network.model.faq.FAQBody;
import com.mobileoid2.celltone.pojo.LanguagePojo;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;

import java.util.List;

public class FAQAdapter  extends RecyclerView.Adapter<FAQAdapter.ViewHolder> {

    private final List<FAQBody> mValues;
    Context mcontext;
    private int previousItemTick = 0;


    public FAQAdapter(Context context, List<FAQBody> items) {
        mcontext = context;
        mValues = items;


    }


    @Override
    public FAQAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_faq, parent, false);
        return new FAQAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FAQAdapter.ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        //holder.mIdView.setText(mValues.get(position).id);
        holder.txtQuestion.setText(holder.mItem.getQuestion());
        holder.txtAnswer.setText(holder.mItem.getAnswer());



    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //public final TextView mIdView;
        public final TextView txtQuestion,txtAnswer;

        public FAQBody mItem;

        public ViewHolder(View view) {
            super(view);
            txtQuestion = view.findViewById(R.id.txt_question);
            txtAnswer = view.findViewById(R.id.txt_answer);


        }


    }
}