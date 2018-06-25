package  com.mobileoid2.celltone.view.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileoid2.celltone.CustomWidget.TextView.OptimaBoldTextview;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.database.ContactEntity;
import com.mobileoid2.celltone.network.model.treadingMedia.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeVideoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeVideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeVideoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int isEdit =0;
    private String mobileNo ="";
    private String name = "";
    private OnFragmentInteractionListener mListener;
    private TabLayout tabLayout;

    public HomeVideoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeVideoFragment.
     */
    Context homeActivity;
    List<Category> musicPojoList;
    int isAudio ;
    private int isIncoming;
    private ContactEntity contactEntity;
    private CardView cvFooter;
    private TextView txtFooter;

    // TODO: Rename and change types and number of parameters
    public static HomeVideoFragment newInstance(Context homeActivity, List<Category> musicPojoList,int isAudio,
                                                int isEdit,String mobileNo,String name, int isIncoming,ContactEntity contactEntity) {
        HomeVideoFragment fragment = new HomeVideoFragment();
        fragment.homeActivity = homeActivity;
        fragment.musicPojoList = musicPojoList;
        fragment.isAudio=isAudio;
        fragment.isEdit =isEdit;
        fragment.mobileNo =mobileNo;
        fragment.name =name;
        fragment.isIncoming =isIncoming;
        fragment.contactEntity =contactEntity;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_home_video, container, false);
        View view = inflater.inflate(R.layout.fragment_home_video, container, false);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        OptimaBoldTextview textView = view.findViewById(R.id.txt_register_now);
        SpannableString content = new SpannableString(getString(R.string.register_now));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));
        tabLayout.setSelectedTabIndicatorHeight((int) (5 * getResources().getDisplayMetrics().density));
        tabLayout.setTabTextColors(Color.parseColor("#adadad"), Color.parseColor("#000000"));
//        if(isEdit==1) {
//            cvFooter.setVisibility(View.VISIBLE);
//            if(isIncoming==1)
//            txtFooter.setText(getString(R.string.update_incoming_media_text)+name);
//            else
//                txtFooter.setText(getString(R.string.update_outgoing_media_text)+name);
//        }
//        else
//            cvFooter.setVisibility(View.GONE);


            setCustomFont();


        return view;
    }

    public void setCustomFont() {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();

        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);

            int tabChildsCount = vgTab.getChildCount();

            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    //Put your font in assests folder
                    //assign name of the font here (Must be case sensitive)
                    ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(getActivity().getAssets(),
                            "fonts/OPTIMA.TTF"));
                }
            }
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    /*
     *
     *
     *
     *
     * */
    /*
     *
     * */

    private void setupViewPager(ViewPager viewPager) {

        if(musicPojoList!=null) {
            ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
            adapter.addFragment(HomeSongsListFragment.newInstance(musicPojoList, isAudio,isEdit,mobileNo,name,isIncoming,contactEntity), "Home");
            for (int i = 0; i < musicPojoList.size(); i++) {
                adapter.addFragment(CategorySongListFragment.newInstance(musicPojoList.get(i), musicPojoList.get(i).getId(), musicPojoList.get(i).getType(),
                        musicPojoList.get(i).getSongs(), isAudio,isEdit,mobileNo,name,isIncoming,contactEntity), musicPojoList.get(i).getTitle());
            }


            viewPager.setAdapter(adapter);
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
