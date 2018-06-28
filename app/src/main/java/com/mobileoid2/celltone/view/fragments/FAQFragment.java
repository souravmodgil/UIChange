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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.Util.Constant;
import com.mobileoid2.celltone.network.APIClient;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.ApiInterface;
import com.mobileoid2.celltone.network.NetworkCallBack;
import com.mobileoid2.celltone.network.SendRequest;
import com.mobileoid2.celltone.network.jsonparsing.JsonResponse;
import com.mobileoid2.celltone.network.model.faq.FAQModel;
import com.mobileoid2.celltone.network.model.offerPlan.OfferModel;
import com.mobileoid2.celltone.view.SeparatorDecoration;
import com.mobileoid2.celltone.view.adapter.FAQAdapter;
import com.mobileoid2.celltone.view.adapter.OfferAdapter;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class FAQFragment extends Fragment implements NetworkCallBack {
    private RecyclerView rvFaq;
    private ProgressBar progressBar;
    private ApiInterface apiInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_faq, container, false);
        rvFaq = view.findViewById(R.id.rv_faq);
        progressBar = view.findViewById(R.id.progress_bar);
        apiInterface = (ApiInterface) APIClient.getClient().create(ApiInterface.class);
        getFaqList();
        return view;
    }
    private void getFaqList()
    {
        SendRequest.sendRequest(ApiConstant.FAQ_API,apiInterface.getAllFAQ(),this);
    }

    private void parseFaq(String response) {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(getFaq(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getPlanObserver()));
    }

    private DisposableObserver<FAQModel> getPlanObserver() {
        return new DisposableObserver<FAQModel>() {

            @Override
            public void onNext(FAQModel faqModel) {
                FAQAdapter faqAdapter = new FAQAdapter(getActivity(), faqModel.getBody());
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                rvFaq.setLayoutManager(mLayoutManager);
                rvFaq.setItemAnimator(new DefaultItemAnimator());
                SeparatorDecoration separatorDecoration = new SeparatorDecoration(getActivity(), Color.parseColor("#e8e8e8"), 1.5F);
                rvFaq.addItemDecoration(separatorDecoration);
                rvFaq.setAdapter(faqAdapter);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private Observable<FAQModel> getFaq(String response) {
        Gson gsonObj = new Gson();
        final FAQModel offerModel = gsonObj.fromJson(response, FAQModel.class);

        return Observable.create(new ObservableOnSubscribe<FAQModel>() {
            @Override
            public void subscribe(ObservableEmitter<FAQModel> emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    emitter.onNext(offerModel);
                    emitter.onComplete();
                }


            }
        });
    }



    @Override
    public void getResponse(JsonResponse response, int type) {
        progressBar.setVisibility(View.GONE);
        if(response.getObject()!=null)
            parseFaq(response.getObject());
            else
        Toast.makeText(getActivity(),response.getErrorString(),Toast.LENGTH_LONG).show();
    }
}
