package com.mobileoid2.celltone.view.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.InputType;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
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
import com.mobileoid2.celltone.network.model.aboutUs.AboutUsModel;
import com.mobileoid2.celltone.network.model.myPlan.CurrentPlan;
import com.mobileoid2.celltone.network.model.myPlan.CurrentPlanDetail;
import com.mobileoid2.celltone.network.model.myPlan.MyPlanModel;
import com.mobileoid2.celltone.view.activity.AbousUs;
import com.mobileoid2.celltone.view.activity.ChangeToolBarTitleListiner;
import com.mobileoid2.celltone.view.adapter.MyPlanAdapter;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class AbousUsFragment extends Fragment implements NetworkCallBack {
    private String type;
    private ProgressBar progressBar;
    private TextView txtAboutUs;
    private ApiInterface apiInterface;
    public static AbousUsFragment newInstance(String type) {
        AbousUsFragment fragment = new AbousUsFragment();
        // fragment.context = context;
        fragment.type =type;
        //'about', 'contact', 'privacy'
        return fragment;


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_about, container, false);
        progressBar = view.findViewById(R.id.progress_bar);
        txtAboutUs =view.findViewById(R.id.txt_aboutus);
        apiInterface = (ApiInterface) APIClient.getClient().create(ApiInterface.class);
        SendRequest.sendRequest(ApiConstant.ABOUT_US_API,apiInterface.getAboutUs(type),this);

        return view;
    }

    private void parseResponse(String response) {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(getAbousUs(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getPlanObserver()));
    }
    private int getScale(){
        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        Double val = new Double(width)/new Double(1200);
        val = val * 100d;
        return val.intValue();
    }


    private DisposableObserver<AboutUsModel> getPlanObserver() {
        return new DisposableObserver<AboutUsModel>() {

            @Override
            public void onNext(AboutUsModel planModel) {
                AboutUsModel model = planModel;
                if(model.getBody()!=null && model.getBody().getDescription()!=null) {
                    txtAboutUs.setText(Html.fromHtml(model.getBody().getDescription()));
                    progressBar.setVisibility(View.GONE);
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


    private Observable<AboutUsModel> getAbousUs(String response) {
        Gson gsonObj = new Gson();
        final AboutUsModel planBody = gsonObj.fromJson(response, AboutUsModel.class);

        return Observable.create(new ObservableOnSubscribe<AboutUsModel>() {
            @Override
            public void subscribe(ObservableEmitter<AboutUsModel> emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    emitter.onNext(planBody);
                    emitter.onComplete();
                }


            }
        });
    }


    @Override
    public void getResponse(JsonResponse response, int type) {
        progressBar.setVisibility(View.GONE);
        if(response.getObject()!=null)
            parseResponse(response.getObject());


    }
}
