package  com.mobileoid2.celltone.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.database.ContactEntity;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.SendRequest;
import com.mobileoid2.celltone.network.model.contacts.SendContactsModel;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;
import com.mobileoid2.celltone.view.activity.ViewAllSongActivity;
import com.mobileoid2.celltone.network.model.treadingMedia.Category;
import com.mobileoid2.celltone.network.model.treadingMedia.Song;
import com.mobileoid2.celltone.view.SeparatorDecoration;
import com.mobileoid2.celltone.view.adapter.MyMusicVideoRecyclerViewAdapter;
import com.mobileoid2.celltone.view.listener.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CategorySongListFragment extends Fragment implements OnListFragmentInteractionListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CategorySongListFragment() {
    }

    private List<Song> musicPojoList;
    private String id;
    private String type;
    private  int isAudio;
    private  Category category;
    private int isEdit =0;
    private String mobileNo ="";
    private String name = "";
    private int isIncoming;
    private ContactEntity contactEntity;




    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CategorySongListFragment newInstance(Category category,String id, String type ,
                                                       List<Song> musicPojoList,int isAudio,
                                                       int isEdit,String mobileNo,String name,int isIncoming,ContactEntity contactEntity) {
        CategorySongListFragment fragment = new CategorySongListFragment();
      /*  Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);*/
        fragment.musicPojoList = musicPojoList;
        fragment.contactEntity =contactEntity;
        fragment.id = id;
        fragment.type = type;
        fragment.category =category;
        fragment.isAudio =isAudio;
        fragment.isEdit =isEdit;
        fragment.mobileNo =mobileNo;
        fragment.isIncoming =isIncoming;
        fragment.name =name;
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
        View view = inflater.inflate(R.layout.fragment_categorysonglist_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            //if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            SeparatorDecoration separatorDecoration = new SeparatorDecoration(getActivity(), Color.parseColor("#e8e8e8"), 1.5F);
            recyclerView.addItemDecoration(separatorDecoration);
            /*} else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }*/
            //recyclerView.setAdapter(new MyCategorySongListRecyclerViewAdapter(musicPojoList, mListener));
            recyclerView.setAdapter(new MyMusicVideoRecyclerViewAdapter(getContext(), musicPojoList,isAudio,this));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
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
        intent.putExtra("type",category.getType());
        intent.putExtra("isAudio",isAudio);
        intent.putExtra("id",category.getId());
        intent.putExtra("postion",postion);
        intent.putExtra("category",category.getTitle());
        intent.putExtra("isEdit",isEdit);
        intent.putExtra("mobile_no",mobileNo);
        intent.putExtra("contact_name",name);
        intent.putExtra("isIncoming",isIncoming);
        ArrayList<Song> songArrayList =(ArrayList<Song>) musicPojoList;
        intent.putExtra("songsList",songArrayList);
        intent.putExtra("contact_entity", contactEntity);
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
