package com.mobileoid2.celltone.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.network.APIClient;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.ApiInterface;
import com.mobileoid2.celltone.network.NetworkCallBack;
import com.mobileoid2.celltone.network.SendRequest;
import com.mobileoid2.celltone.network.jsonparsing.JsonResponse;
import com.mobileoid2.celltone.network.model.feedback.FeedBackList;
import com.mobileoid2.celltone.network.model.feedback.FeedBackModel;
import com.mobileoid2.celltone.network.model.upload_media_list.UploadMediaList;
import com.mobileoid2.celltone.network.model.upload_media_list.UploadMediaListModle;
import com.mobileoid2.celltone.pojo.QUERYREQUEST;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;
import com.mobileoid2.celltone.utility.Utils;
import com.mobileoid2.celltone.view.activity.ComposeQueryActivity;
import com.mobileoid2.celltone.view.activity.QueryListActivity;
import com.mobileoid2.celltone.view.adapter.MediaUploadAdapter;
import com.mobileoid2.celltone.view.adapter.QueryListAdapter;
import com.mobileoid2.celltone.view.listener.QueryLisiner;
import com.mobileoid2.celltone.view.listener.QueryReplyListiner;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class QueryListFragment extends Fragment implements NetworkCallBack,QueryReplyListiner {
    private RecyclerView recyleViewQuery;
    private FloatingActionButton fabCompose;
    private ProgressBar progressBar;
    private ApiInterface apiInterface;
    private int limit = 0;
    private int skip = 0;
    private boolean loading = true;
    private int noOfPages =0;
    private TextView  txtNoRecords;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private android.support.v7.widget.LinearLayoutManager mLayoutManager;
     QueryLisiner queryLisiner;

    public  static QueryListFragment newInstance (QueryLisiner queryLisiner)
    {
        QueryListFragment queryFragment = new QueryListFragment();
        queryFragment.queryLisiner =queryLisiner;
        return queryFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_query, container, false);
        recyleViewQuery = view.findViewById(R.id.recyle_view_query);
        fabCompose = view.findViewById(R.id.fab_compose);
        progressBar = view.findViewById(R.id.progress_bar);
        txtNoRecords = view.findViewById(R.id.txt_no_records);
        apiInterface = (ApiInterface) APIClient.getClient().create(ApiInterface.class);
        mLayoutManager = new LinearLayoutManager(getActivity());
        getQueryList();
        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), ComposeQueryActivity.class));
            }
        });
        recyleViewQuery.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading && noOfPages>limit) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;

                            skip = skip + limit;
                            getQueryList();

                        }
                    }
                }
            }
        });

        return view;
    }

    public void getQueryList() {
        QUERYREQUEST queryrequest = new QUERYREQUEST();
        queryrequest.setLimit(limit);
        queryrequest.setSkip(skip);
        SendRequest.sendRequest(ApiConstant.QUERY_LIST_API, apiInterface.getQueryList(SharedPrefrenceHandler.getInstance().getUSER_TOKEN(),
                queryrequest), this);

    }

    private void parseResponse(String response) {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(getList(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(UploadMediaObserver()));
    }

    private DisposableObserver<FeedBackModel> UploadMediaObserver() {
        return new DisposableObserver<FeedBackModel>() {

            @Override
            public void onNext(FeedBackModel modle) {

                QueryListAdapter planAdapter = new QueryListAdapter(getActivity(), modle.getBody().getList(),modle,QueryListFragment.this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                noOfPages=modle.getBody().getCount();
                recyleViewQuery.setLayoutManager(mLayoutManager);
                recyleViewQuery.setItemAnimator(new DefaultItemAnimator());
                recyleViewQuery.setAdapter(planAdapter);
                progressBar.setVisibility(View.GONE);
                txtNoRecords.setVisibility(View.GONE);
                recyleViewQuery.setVisibility(View.VISIBLE);
                if(modle.getBody().getList().size()==0)
                {
                    txtNoRecords.setVisibility(View.VISIBLE);
                    recyleViewQuery.setVisibility(View.GONE);

                }


            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }


    private Observable<FeedBackModel> getList(String response) {
        Gson gsonObj = new Gson();
        final FeedBackModel responseUpload = gsonObj.fromJson(response, FeedBackModel.class);

        return Observable.create(new ObservableOnSubscribe<FeedBackModel>() {
            @Override
            public void subscribe(ObservableEmitter<FeedBackModel> emitter) throws Exception {
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
            loading = true;
            parseResponse(response.getObject());
        }
        progressBar.setVisibility(View.GONE);


    }

    @Override
    public void onReply(FeedBackList feedBack) {
        Intent intent = new Intent(getActivity(),QueryListActivity.class);
        intent.putExtra("feedbacllist",feedBack);
        getActivity().startActivity(intent);

    }
}
