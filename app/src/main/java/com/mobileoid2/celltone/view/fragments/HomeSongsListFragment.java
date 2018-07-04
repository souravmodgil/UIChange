package com.mobileoid2.celltone.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.database.ContactEntity;
import com.mobileoid2.celltone.view.activity.ViewAllSongActivity;
import com.mobileoid2.celltone.network.model.treadingMedia.Category;
import com.mobileoid2.celltone.network.model.treadingMedia.Song;
import com.mobileoid2.celltone.view.adapter.MyHomeSongsListRecyclerViewAdapter;
import com.mobileoid2.celltone.view.listener.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class HomeSongsListFragment extends Fragment implements OnListFragmentInteractionListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private int isAudio;
    private OnListFragmentInteractionListener mListener;
    private int isEdit = 0;
    private String mobileNo = "";
    private String name = "";
    private int isIncoming;
    private RecyclerView list;
    private ContactEntity contactEntity;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HomeSongsListFragment() {
    }

    List<Category> homeMusicItemList;


    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HomeSongsListFragment newInstance(List<Category> homeMusicItemList, int isAudio,
                                                    int isEdit, String mobileNo, String name, int isIncoming,ContactEntity contactEntity) {
        HomeSongsListFragment fragment = new HomeSongsListFragment();
        fragment.homeMusicItemList = homeMusicItemList;
        fragment.isAudio = isAudio;
        fragment.isEdit = isEdit;
        fragment.mobileNo = mobileNo;
        fragment.name = name;
        fragment.isIncoming = isIncoming;
        fragment.contactEntity =contactEntity;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homesongslist_list, container, false);
        list = view.findViewById(R.id.list);
        // Set the adapter
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setAdapter(new MyHomeSongsListRecyclerViewAdapter(isAudio, homeMusicItemList, this, getContext(), getActivity()));



        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            //throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListFragmentInteraction(Category item, int postion) {
        Intent intent = new Intent(getActivity(), ViewAllSongActivity.class);
        intent.putExtra("type", item.getType());
        intent.putExtra("isEdit",isEdit);
        intent.putExtra("mobile_no",mobileNo);
        intent.putExtra("contact_name",name);
        intent.putExtra("isIncoming",isIncoming);
        intent.putExtra("isAudio", isAudio);
        intent.putExtra("id", item.getId());
        intent.putExtra("postion", postion);
        intent.putExtra("category", item.getTitle());
        intent.putExtra("contact_entity", contactEntity);

        ArrayList<Song> songArrayList = (ArrayList<Song>) item.getSongs();
        intent.putExtra("songsList", songArrayList);
        startActivity(intent);


    }




    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

}
