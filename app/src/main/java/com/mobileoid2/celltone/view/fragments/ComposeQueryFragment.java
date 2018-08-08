package com.mobileoid2.celltone.view.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.network.APIClient;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.ApiInterface;
import com.mobileoid2.celltone.network.NetworkCallBack;
import com.mobileoid2.celltone.network.SendRequest;
import com.mobileoid2.celltone.network.jsonparsing.JsonResponse;
import com.mobileoid2.celltone.network.model.feedback.Comment;
import com.mobileoid2.celltone.network.model.feedback.FeedBackList;
import com.mobileoid2.celltone.network.model.feedback.comment.CommentModel;
import com.mobileoid2.celltone.network.model.feedback.composeQuery.ComposeQueryModel;
import com.mobileoid2.celltone.pojo.ComposeQueryRequest;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;
import com.mobileoid2.celltone.utility.Utils;
import com.mobileoid2.celltone.view.SeparatorDecoration;
import com.mobileoid2.celltone.view.adapter.QueryReplyAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class ComposeQueryFragment extends Fragment implements AdapterView.OnItemSelectedListener, NetworkCallBack {
    private ApiInterface apiInterface;
    private Spinner typeSppiner;
    private EditText inputSubject,inputComments;
    String[] typeList = { "Type", "Query", "Issue"};
    private String selectedType ="Type";
    private ProgressBar progressBar;
    private RelativeLayout tutSetQuery;
    private Button buttonSkip;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_compose, container, false);
        apiInterface = (ApiInterface) APIClient.getClient().create(ApiInterface.class);
        typeSppiner = view.findViewById(R.id.type_sppiner);
        inputComments = view.findViewById(R.id.input_comments);
        inputSubject = view.findViewById(R.id.input_subject);
        tutSetQuery =  view.findViewById(R.id.tut_layout);
        progressBar =view.findViewById(R.id.progress_bar);
        buttonSkip = view.findViewById(R.id.button_skip);
        typeSppiner.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,typeList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        typeSppiner.setAdapter(aa);
        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tutSetQuery.setVisibility(View.GONE);
            }
        });

        if(SharedPrefrenceHandler.getInstance().getIsFirstTimeQuery()==0)
        {
            tutSetQuery.setVisibility(View.VISIBLE);
            SharedPrefrenceHandler.getInstance().setIsFirstTimeQuery(1);
            tutSetQuery.postDelayed(new Runnable() {
                public void run() {
                    tutSetQuery.setVisibility(View.GONE);
                }
            }, 100000);

        }
        else
            tutSetQuery.setVisibility(View.GONE);






        return view;
    }
    public   void composeQuery()
    {
        if(!selectedType.equals("Type") &&inputComments.length()>0 && inputSubject.length()>0 )
        {
            progressBar.setVisibility(View.VISIBLE);
            ComposeQueryRequest composeQueryRequest = new ComposeQueryRequest();
            composeQueryRequest.setType(selectedType.toLowerCase());
            composeQueryRequest.setDescription(inputComments.getText().toString());
            composeQueryRequest.setTitle(inputSubject.getText().toString());
            SendRequest.sendRequest(ApiConstant.QUERY_SEND_COMMENT_API,apiInterface.createQuery(SharedPrefrenceHandler.getInstance().getUSER_TOKEN(),
                    composeQueryRequest),this);

        }
        else
            Toast.makeText(getActivity(),getString(R.string.compoase_query_validation),Toast.LENGTH_LONG).show();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedType = typeList[position];


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void parseResponse(String response) {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(getList(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(UploadMediaObserver()));
    }

    private DisposableObserver<ComposeQueryModel> UploadMediaObserver() {
        return new DisposableObserver<ComposeQueryModel>() {

            @Override
            public void onNext(ComposeQueryModel modle) {

                Toast.makeText(getActivity(),modle.getMessage(),Toast.LENGTH_LONG).show();


            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }


    private Observable<ComposeQueryModel> getList(String response) {
        Gson gsonObj = new Gson();
        final ComposeQueryModel responseUpload = gsonObj.fromJson(response, ComposeQueryModel.class);

        return Observable.create(new ObservableOnSubscribe<ComposeQueryModel>() {
            @Override
            public void subscribe(ObservableEmitter<ComposeQueryModel> emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    emitter.onNext(responseUpload);
                    emitter.onComplete();
                }


            }
        });
    }

    @Override
    public void getResponse(JsonResponse response, int type) {

        if (response.getObject() != null) {
            parseResponse(response.getObject());
        }
        progressBar.setVisibility(View.GONE);

    }
}
