package com.mobileoid2.celltone.view.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.database.ContactEntity;
import com.mobileoid2.celltone.pojo.PopUpPojo;
import com.mobileoid2.celltone.view.activity.HomeActivity;
import com.mobileoid2.celltone.view.fragments.ContactsFragment.OnListFragmentInteractionListener;
import com.mobileoid2.celltone.pojo.Contact;
import com.mobileoid2.celltone.view.fragments.CustomFilter;
import com.mobileoid2.celltone.view.listener.SelectedContactListner;
import com.mobileoid2.celltone.view.listener.UpdateDailogView;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Contact} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyContactsRecyclerViewAdapter extends RecyclerView.Adapter<MyContactsRecyclerViewAdapter.ViewHolder> implements UpdateDailogView {

    private final List<ContactEntity> mValues;
    public List<ContactEntity> mlistFiltered;
    private Context context;
    private TextView txtSet,txtDelete;
    private SelectedContactListner selectedContactListner;
    private int isEdit;
    private int isIncoming =1;
    CustomFilter filter;
    private ContactEntity selectedContactEntity;


    public MyContactsRecyclerViewAdapter(Context context, List<ContactEntity> items, SelectedContactListner selectedContactListner, int isEdit) {
        mValues = items;
        this.context = context;
        mlistFiltered = items;
        this.isEdit = isEdit;
        this.selectedContactListner = selectedContactListner;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contacts2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mlistFiltered.get(position);
        //holder.mIdView.setText(position+1+"");
        String contactName = "";
        if (holder.mItem.getName() != null && !holder.mItem.getName().isEmpty())
            contactName = holder.mItem.getName();
        else
            contactName = holder.mItem.getNumber();

        holder.mContentView.setText(contactName);

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(contactName.substring(0, 1), context.getResources().getColor(R.color.colorPrimary));
        holder.imageView.setImageDrawable(drawable);

        if (isEdit == 1) {
            holder.editIcon.setVisibility(View.VISIBLE);
            holder.cbContact.setVisibility(View.GONE);
        } else {
            holder.cbContact.setVisibility(View.VISIBLE);
            holder.editIcon.setVisibility(View.GONE);
        }

        if (holder.mItem.getIsSelcted() == 0)
            holder.cbContact.setChecked(false);
        if (holder.mItem.getIsSelcted() == 1)
            holder.cbContact.setChecked(true);

        if (holder.mItem.getIsOutgoing() == 1) {
            holder.llOutgoing.setVisibility(View.VISIBLE);
            if (holder.mItem.getOutgoingIsVideo() == 0)
                holder.txtOutgoingSongTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.audio_icon,0);
            else
                holder.txtOutgoingSongTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.video_icon,0);



            holder.txtOutgoingSongTitle.setText(holder.mItem.getOutgoingSongName());
        } else
            holder.llOutgoing.setVisibility(View.GONE);

        if (holder.mItem.getIsIncoming() == 1) {
            holder.llIncoming.setVisibility(View.VISIBLE);
            if (holder.mItem.getIsincomingVideo() == 0)
                holder.txtIncomingSongTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.audio_icon,0);
            else
                holder.txtIncomingSongTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.video_icon,0);
            holder.txtIncomingSongTitle.setText(holder.mItem.getIncomingSongName());
        } else {
            holder.llIncoming.setVisibility(View.GONE);
        }
      //  showPopUp
        holder.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp(holder.mItem);
            }
        });

        holder.cbContact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedContactListner.setContacts(holder.mItem.getNumber(), 1, position);
                    holder.mItem.setIsSelcted(1);
                    mValues.get(position).setIsSelcted(1);


                } else {
                    selectedContactListner.setContacts(holder.mItem.getNumber(), 0, position);
                    holder.mItem.setIsSelcted(0);
                    mValues.get(position).setIsSelcted(0);
                }


            }
        });


    }

    @Override
    public int getItemCount() {
        if (mlistFiltered != null)
            return mlistFiltered.size();
        else
            return 0;
    }

    private void showPopUp(ContactEntity entity) {

        int isDeleteButtonShow =0;
        List<PopUpPojo> listItems = new ArrayList<>();
        PopUpPojo popUpPojo;
        popUpPojo = new PopUpPojo();
        popUpPojo.setTitle("Incoming Call");
        popUpPojo.setIsSelected(1);
        if (entity.getIsIncoming() == 1) {
            isDeleteButtonShow =1;
            popUpPojo.setTrackName(entity.getIncomingSongName());
            if (entity.getIsincomingVideo() == 0)
                popUpPojo.setIsAudio(1);
            else
                popUpPojo.setIsAudio(0);
        }
        listItems.add(popUpPojo);

        popUpPojo = new PopUpPojo();
        popUpPojo.setTitle("Outgoing Call");
        if (entity.getIsOutgoing() == 1) {
            popUpPojo.setTrackName(entity.getOutgoingSongName());
            if (entity.getOutgoingIsVideo() == 0)
                popUpPojo.setIsAudio(1);
            else
                popUpPojo.setIsAudio(0);
        }
        popUpPojo.setIsSelected(0);
        listItems.add(popUpPojo);



        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dailog_outgoing_incoming_layout, null);
        RecyclerView lv = (RecyclerView) view.findViewById(R.id.rc_dailog_list);
        txtDelete =view.findViewById(R.id.txt_delete);
        txtSet = view.findViewById(R.id.txt_set);
        // Change MyActivity.this and myListOfItems to your own values
        CustomDailogListAdapterDialog adapter = new CustomDailogListAdapterDialog(context, listItems,this::updateView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        lv.setLayoutManager(mLayoutManager);
        lv.setItemAnimator(new DefaultItemAnimator());
        lv.setAdapter(adapter);
        dialog.setView(view);
        if(isDeleteButtonShow==1)
        {
            txtDelete.setVisibility(View.VISIBLE);
            txtSet.setText(context.getString(R.string.edit));
        }
        else
        {
            txtDelete.setVisibility(View.GONE);
            txtSet.setText(context.getString(R.string.label_set));
        }
        txtSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HomeActivity.class);
                intent.putExtra("isEdit",1);
                intent.putExtra("mobile_no",entity.getNumber());
                intent.putExtra("contact_name",entity.getName());
                intent.putExtra("isIncoming",isIncoming);
                intent.putExtra("contact_entity",entity);
                context.startActivity(intent);

            }
        });

        dialog.show();
    }

    @Override
    public void updateView(int isDeleteButtonShow,int position) {
        if(position==0)
            isIncoming=1;

        else
            isIncoming =0;
        if(isDeleteButtonShow==1)
        {
            txtDelete.setVisibility(View.VISIBLE);
            txtSet.setText(context.getString(R.string.edit));
        }
        else
        {
            txtDelete.setVisibility(View.GONE);
            txtSet.setText(context.getString(R.string.label_set));
        }

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //public final TextView mIdView;
        public final TextView mContentView;
        public final ImageView editIcon;
        public final TextView mContactsLetter;
        public final TextView txtIncomingSongTitle, txtOutgoingSongTitle;
        public ContactEntity mItem;
        public ImageView   imageView;
        public CheckBox cbContact;
        public LinearLayout llOutgoing, llIncoming;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            /// mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            mContactsLetter = (TextView) view.findViewById(R.id.contactsLetter);
            txtIncomingSongTitle = view.findViewById(R.id.txt_incoming_song_title);
            editIcon = view.findViewById(R.id.edit_icon);
            llOutgoing = view.findViewById(R.id.ll_outgoing);
            txtOutgoingSongTitle = view.findViewById(R.id.txt_outgoing_song_title);
            cbContact = view.findViewById(R.id.cb_contact);
            imageView = view.findViewById(R.id.image_view);
            llIncoming = view.findViewById(R.id.ll_incoming);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public void updateAdater(List<ContactEntity> mlistFiltered, int isGetAllData) {
        if (isGetAllData == 0) {
            this.mlistFiltered = mlistFiltered;
            notifyDataSetChanged();
        } else {
            this.mlistFiltered = mValues;
            notifyDataSetChanged();
        }
    }


}
