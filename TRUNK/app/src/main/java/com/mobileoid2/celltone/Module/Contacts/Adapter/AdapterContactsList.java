package com.mobileoid2.celltone.Module.Contacts.Adapter;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mobileoid2.celltone.CustomWidget.TextView.TextVeiwEuro55Regular;
import com.mobileoid2.celltone.Module.Contacts.Bean.BeanContacts;
import com.mobileoid2.celltone.Module.Contacts.Interface.InterfaceContacts;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.AppSharedPref;
import com.mobileoid2.celltone.Util.AppUtils;
import com.mobileoid2.celltone.Util.ImageGetter;
import com.mobileoid2.celltone.database.DatabaseConstants;

import java.io.File;
import java.util.HashSet;
import java.util.List;

/**
 * Created by mobileoid2 on 9/11/17.
 */

public class AdapterContactsList extends RecyclerView.Adapter<AdapterContactsList.MyViewHolder> {

    private List<BeanContacts> mainList;
    private Context mcontext;
    private HashSet<BeanContacts> selectedPositions = new HashSet<BeanContacts>();
    private HashSet<ImageGetter> uploaders = new HashSet<ImageGetter>();
    private InterfaceContacts interfaceContacts;

    private static final Object lock = new Object();

    public class MyViewHolder extends RecyclerView.ViewHolder {


        public TextVeiwEuro55Regular textViewName;
        public TextVeiwEuro55Regular textViewNo;
        private TextVeiwEuro55Regular textViewFirstLetter;
        private ImageView imageViewThumbIncoming, imageViewSymbolIncoming, imageViewThumbOutgoing, imageViewSymbolOutgoing, imageViewFillerIncoming, imageViewFillerOutgoing;
        private RelativeLayout layoutPlayIncoming, layoutFrameIncoming, layoutPlayOutgoing, layoutFrameOutgoing;
        private LinearLayout layoutName;
        private CheckBox checkbox;
        private ImageGetter thumbnailGetter;


        public MyViewHolder(View view) {
            super(view);

            textViewName = (TextVeiwEuro55Regular) view.findViewById(R.id.textview_name);
            textViewNo = (TextVeiwEuro55Regular) view.findViewById(R.id.textview_number);
            textViewFirstLetter = view.findViewById(R.id.textview_first_letter);
            layoutName = view.findViewById(R.id.layout_name);
            checkbox = view.findViewById(R.id.checkbox);

            imageViewThumbIncoming = view.findViewById(R.id.image_view_thumb);
            imageViewSymbolIncoming = view.findViewById(R.id.image_view_symbol);
            layoutPlayIncoming = view.findViewById(R.id.layout_action_incoming);
            layoutFrameIncoming = view.findViewById(R.id.layout_frame);

            imageViewThumbOutgoing = view.findViewById(R.id.image_view_thumb_outgoing);
            imageViewSymbolOutgoing = view.findViewById(R.id.image_view_symbol_outgoing);
            layoutPlayOutgoing = view.findViewById(R.id.layout_action_outgoing);
            layoutFrameOutgoing = view.findViewById(R.id.layout_frame_outgoing);

            imageViewFillerIncoming = view.findViewById(R.id.img_thumb_incoming);
            imageViewFillerOutgoing = view.findViewById(R.id.img_thumb_outgoing);


            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(AppSharedPref.instance.getScreenWidth() / 7, AppSharedPref.instance.getScreenWidth() / 7);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(5, 5, 5, 5);
            layoutPlayIncoming.setLayoutParams(params);

            params = new RelativeLayout.LayoutParams(AppSharedPref.instance.getScreenWidth() / 7, AppSharedPref.instance.getScreenWidth() / 7);
            params.addRule(RelativeLayout.START_OF, layoutPlayIncoming.getId());
            params.setMargins(5, 5, 0, 5);
            layoutPlayOutgoing.setLayoutParams(params);

        }


    }

    public AdapterContactsList(Context context, List<BeanContacts> mainList, InterfaceContacts interfaceContacts) {
        this.mainList = mainList;
        this.interfaceContacts = interfaceContacts;
        this.mcontext = context;
    }

    public void updateList(List<BeanContacts> mainlist) {

        if (uploaders.size() > 0) {
            for (ImageGetter upPic : uploaders) {
                if (upPic != null)
                    if (upPic.getStatus() == AsyncTask.Status.RUNNING) {
                        upPic.cancel(true);
                        upPic = null;
                    }
            }
            uploaders.clear();
        }


        mainList = mainlist;
        HashSet<BeanContacts> tempPositions = selectedPositions;

        for (BeanContacts contact : tempPositions) {
            if (!mainList.contains(contact)) {
                tempPositions.remove(contact);
            }
        }
        selectedPositions = tempPositions;
        notifyDataSetChanged();
        setCheckButton();
    }

    public void removeItem(BeanContacts contact) {
        if (uploaders.size() > 0) {
            for (ImageGetter upPic : uploaders) {
                if (upPic != null)
                    if (upPic.getStatus() == AsyncTask.Status.RUNNING) upPic.cancel(true);
            }
            uploaders.clear();
        }

        try {
            synchronized (lock) {
                int position = mainList.indexOf(contact);
                mainList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount() - position);
            }
        } catch (Exception e) {
            Log.e("Adapter-Contacts", AppUtils.instance.getExceptionString(e));
        }
    }

    public void cleanCacheWork() {
        if (uploaders.size() > 0) {
            for (ImageGetter upPic : uploaders) {
                if (upPic != null)
                    if (upPic.getStatus() == AsyncTask.Status.RUNNING) {
                        upPic.cancel(true);
                        upPic = null;
                    }
            }
            uploaders.clear();
        }
    }

    @Override
    public void onBindViewHolder(AdapterContactsList.MyViewHolder holder, int realPosition) {

        holder.textViewName.setText(mainList.get(realPosition).getName());
        holder.textViewNo.setText(mainList.get(realPosition).getNumber());
        holder.textViewFirstLetter.setText(mainList.get(realPosition).getName().toUpperCase());

        holder.layoutName.setTag(realPosition);
        holder.textViewFirstLetter.setTag(realPosition);


        holder.layoutPlayIncoming.setTag(realPosition);
        holder.layoutFrameIncoming.setTag(realPosition);
        holder.imageViewSymbolIncoming.setTag(realPosition);
        holder.imageViewThumbIncoming.setTag(realPosition);


        holder.layoutPlayOutgoing.setTag(realPosition);
        holder.layoutFrameOutgoing.setTag(realPosition);
        holder.imageViewSymbolOutgoing.setTag(realPosition);
        holder.imageViewThumbOutgoing.setTag(realPosition);

        holder.imageViewFillerIncoming.setImageDrawable(mcontext.getResources().getDrawable(R.drawable.thumb_image));
        holder.imageViewFillerOutgoing.setImageDrawable(mcontext.getResources().getDrawable(R.drawable.thumb_image));

        if (selectedPositions.size() == 0)
            holder.checkbox.setVisibility(View.GONE);
        else
            holder.checkbox.setVisibility(View.VISIBLE);

        if (selectedPositions.contains(mainList.get(realPosition))) {
            holder.checkbox.setChecked(true);
        } else {
            holder.checkbox.setChecked(false);
        }

        if (new File(mainList.get(realPosition).getMusicIncomingPath()).exists()) {
            if (mainList.get(realPosition).getIsIncomingVideo().equals(DatabaseConstants.VALUE_TRUE))
                holder.imageViewSymbolIncoming.setImageDrawable(mcontext.getResources().getDrawable(R.mipmap.my_contact_video_icon));
            else if (mainList.get(realPosition).getIsIncomingVideo().equals(DatabaseConstants.VALUE_FALSE))
                holder.imageViewSymbolIncoming.setImageDrawable(mcontext.getResources().getDrawable(R.mipmap.my_contact_music_icon));
            else {
                holder.imageViewSymbolIncoming.setImageBitmap(null);
            }
            try {
                if (mainList.get(realPosition).getIsIncomingVideo().equals(DatabaseConstants.VALUE_FALSE)) {
                    Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                    if (!mainList.get(realPosition).getMusicIncomingThumbNail().equals("")) {
                        Uri uri = ContentUris.withAppendedId(sArtworkUri, Long.parseLong(mainList.get(realPosition).getMusicIncomingThumbNail()));
                        if (new File(uri.getPath()).exists())
                            holder.imageViewThumbIncoming.setImageURI(uri);
                    } else {
                        holder.imageViewThumbIncoming.setImageDrawable(mcontext.getResources().getDrawable(R.drawable.thumb_image));
                    }
                    holder.layoutFrameIncoming.setBackground(mcontext.getResources().getDrawable(R.mipmap.mycontact_music_bg));
                } else if (mainList.get(realPosition).getIsIncomingVideo().equals(DatabaseConstants.VALUE_TRUE)) {

                    holder.thumbnailGetter = new ImageGetter(mainList.get(realPosition).getMusicIncomingPath(), holder.imageViewThumbIncoming, new ImageGetter.interfaceOnImageSet() {
                        @Override
                        public void onImageSettingsFinished(ImageGetter instance) {
                            if (uploaders != null)
                                if (uploaders.contains(instance)) {
                                    uploaders.remove(instance);
                                }
                        }
                    });
                    uploaders.add(holder.thumbnailGetter);
                    holder.thumbnailGetter.execute();
                    holder.layoutFrameIncoming.setBackground(mcontext.getResources().getDrawable(R.mipmap.mycontact_video_bg));

                } else {
                    holder.imageViewThumbIncoming.setImageBitmap(null);
                }
            } catch (Exception ex) {
                Log.e("ADAPTER : SONGS", AppUtils.instance.getExceptionString(ex));
            }
        } else {
            holder.imageViewThumbIncoming.setImageBitmap(null);
            holder.imageViewSymbolIncoming.setImageBitmap(null);
            holder.layoutFrameIncoming.setBackground(null);
            holder.imageViewFillerIncoming.setImageBitmap(null);
        }

        if (new File(mainList.get(realPosition).getMusicOutgoingPath()).exists()) {
            if (mainList.get(realPosition).getIsOutgoingVideo().equals(DatabaseConstants.VALUE_TRUE))
                holder.imageViewSymbolOutgoing.setImageDrawable(mcontext.getResources().getDrawable(R.mipmap.my_contact_video_icon));
            else if (mainList.get(realPosition).getIsOutgoingVideo().equals(DatabaseConstants.VALUE_FALSE))
                holder.imageViewSymbolOutgoing.setImageDrawable(mcontext.getResources().getDrawable(R.mipmap.my_contact_music_icon));
            else {
                holder.imageViewSymbolOutgoing.setImageBitmap(null);
            }
            try {

                if (mainList.get(realPosition).getIsOutgoingVideo().equals(DatabaseConstants.VALUE_FALSE)) {
                    Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                    if (!mainList.get(realPosition).getMusicOutgoingThumbNail().equals("")) {
                        Uri uri = ContentUris.withAppendedId(sArtworkUri, Long.parseLong(mainList.get(realPosition).getMusicOutgoingThumbNail()));
                        if (new File(uri.getPath()).exists())
                            holder.imageViewThumbOutgoing.setImageURI(uri);
                    } else {
                        holder.imageViewThumbOutgoing.setImageDrawable(mcontext.getResources().getDrawable(R.drawable.thumb_image));
                    }
                    holder.layoutFrameOutgoing.setBackground(mcontext.getResources().getDrawable(R.mipmap.mycontact_music_bg));
                } else if (mainList.get(realPosition).getIsOutgoingVideo().equals(DatabaseConstants.VALUE_TRUE)) {

                    holder.thumbnailGetter = new ImageGetter(mainList.get(realPosition).getMusicOutgoingPath(), holder.imageViewThumbOutgoing, new ImageGetter.interfaceOnImageSet() {
                        @Override
                        public void onImageSettingsFinished(ImageGetter thumbnailGetterInstance) {
                            if (uploaders != null)
                                if (uploaders.contains(thumbnailGetterInstance)) {
                                    uploaders.remove(thumbnailGetterInstance);
                                }
                        }
                    });
                    uploaders.add(holder.thumbnailGetter);
                    holder.thumbnailGetter.execute();

                    holder.layoutFrameOutgoing.setBackground(mcontext.getResources().getDrawable(R.mipmap.mycontact_video_bg));

                } else {
                    holder.imageViewThumbOutgoing.setImageBitmap(null);
                }
            } catch (Exception ex) {
                Log.e("ADAPTER : SONGS", AppUtils.instance.getExceptionString(ex));
            }

        } else {
            holder.imageViewSymbolOutgoing.setImageBitmap(null);
            holder.imageViewThumbOutgoing.setImageBitmap(null);
            holder.layoutFrameOutgoing.setBackground(null);
            holder.imageViewFillerOutgoing.setImageBitmap(null);
        }
//        holder.layoutPlayIncoming.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setSelectedPositions((Integer) v.getTag());
//            }
//        });
//
//
        holder.layoutName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedPositions(mainList.get((Integer) view.getTag()));
            }
        });
        holder.textViewFirstLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.layoutFrameIncoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedPositions(mainList.get((Integer) v.getTag()));
            }
        });

        holder.layoutFrameOutgoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedPositions(mainList.get((Integer) v.getTag()));
            }
        });

    }

    private void setSelectedPositions(BeanContacts contact) {
        if (selectedPositions.contains(contact))
            selectedPositions.remove(contact);
        else
            selectedPositions.add(contact);
        notifyDataSetChanged();
        setCheckButton();
    }

    public void setSelectAll(boolean isTrue) {
        selectedPositions = new HashSet<BeanContacts>();
        if (isTrue) {
            selectedPositions = new HashSet<BeanContacts>(mainList);
        }
        notifyDataSetChanged();
    }

    private void setCheckButton() {

        if (selectedPositions.containsAll(mainList) && mainList.containsAll(selectedPositions)) {
            interfaceContacts.onContactSelectonChange(true);
        } else {
            interfaceContacts.onContactSelectonChange(false);
        }

    }

    public HashSet<BeanContacts> getSelectedPositions() {
        return selectedPositions;
    }

    @Override
    public int getItemCount() {
        return mainList.size();
    }

    @Override
    public AdapterContactsList.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_design_contacts, parent, false);
        return new AdapterContactsList.MyViewHolder(v);
    }


}

