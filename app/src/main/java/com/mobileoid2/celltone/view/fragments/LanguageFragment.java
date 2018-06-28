package com.mobileoid2.celltone.view.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.network.APIClient;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.ApiInterface;
import com.mobileoid2.celltone.network.SendRequest;
import com.mobileoid2.celltone.pojo.LanguagePojo;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;
import com.mobileoid2.celltone.view.SeparatorDecoration;
import com.mobileoid2.celltone.view.adapter.LanguageAdapter;
import com.mobileoid2.celltone.view.adapter.MyHomeSongsListRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class LanguageFragment extends Fragment {
   private RecyclerView rcLanguage;


    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_lanquage, container, false);
        rcLanguage = view.findViewById(R.id.rc_language);
        LanguageAdapter adapter = new LanguageAdapter(getActivity(),getLanguageList());
        SeparatorDecoration separatorDecoration = new SeparatorDecoration(getActivity(), Color.parseColor("#e8e8e8"), 1.5F);
        rcLanguage.addItemDecoration(separatorDecoration);
        rcLanguage.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcLanguage.setAdapter(adapter);

        return view;

    }

    private List<LanguagePojo> getLanguageList() {
        String code = SharedPrefrenceHandler.getInstance().getLanguageCode();

        List<LanguagePojo> languagePojoList = new ArrayList<>();
        LanguagePojo pojo = new LanguagePojo();
        pojo.setLanguageName(getActivity().getString(R.string.english));
        pojo.setLanguageCode("en");
        if (code.equals("en"))
            pojo.setIsTcked(1);
        languagePojoList.add(pojo);

        pojo = new LanguagePojo();
        pojo.setLanguageName(getActivity().getString(R.string.hebrew));
        pojo.setLanguageCode("he");
        if (code.equals("he"))
            pojo.setIsTcked(1);
        languagePojoList.add(pojo);

        pojo = new LanguagePojo();
        pojo.setLanguageName(getActivity().getString(R.string.german));
        pojo.setLanguageCode("de");
        if (code.equals("de"))
            pojo.setIsTcked(1);
        languagePojoList.add(pojo);

        pojo = new LanguagePojo();
        pojo.setLanguageName(getActivity().getString(R.string.italian));
        pojo.setLanguageCode("it");
        if (code.equals("it"))
            pojo.setIsTcked(1);
        languagePojoList.add(pojo);

        pojo = new LanguagePojo();
        pojo.setLanguageName(getActivity().getString(R.string.spanish));
        pojo.setLanguageCode("es");
        if (code.equals("es"))
            pojo.setIsTcked(1);
        languagePojoList.add(pojo);

        pojo = new LanguagePojo();
        pojo.setLanguageName(getActivity().getString(R.string.arabic));
        pojo.setLanguageCode("ar");
        if (code.equals("ar"))
            pojo.setIsTcked(1);
        languagePojoList.add(pojo);

        pojo = new LanguagePojo();
        pojo.setLanguageName(getActivity().getString(R.string.indonesian));
        pojo.setLanguageCode("id");
        if(code.equals("id"))
            pojo.setIsTcked(1);
        languagePojoList.add(pojo);


        return languagePojoList;

    }
}
