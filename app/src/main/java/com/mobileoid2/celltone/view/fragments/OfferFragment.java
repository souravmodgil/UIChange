package  com.mobileoid2.celltone.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.network.APIClient;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.ApiInterface;
import com.mobileoid2.celltone.network.NetworkCallBack;
import com.mobileoid2.celltone.network.SendRequest;
import com.mobileoid2.celltone.network.jsonparsing.JsonResponse;
import com.mobileoid2.celltone.network.model.offerPlan.OfferBody;
import com.mobileoid2.celltone.network.model.offerPlan.OfferModel;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;
import com.mobileoid2.celltone.view.adapter.OfferAdapter;
import com.mobileoid2.celltone.view.listener.PlanListiner;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class OfferFragment extends Fragment implements NetworkCallBack, PlanListiner {
    private RecyclerView rvOfferList;
    private ApiInterface apiInterface;
    private OfferModel offerModel;
    private OfferAdapter offerAdapter;
    private ProgressBar progressBar;

    public static OfferFragment newInstance() {
        OfferFragment fragment = new OfferFragment();
        return fragment;


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_offer, container, false);
        rvOfferList = view.findViewById(R.id.rv_offer_list);
        progressBar = view.findViewById(R.id.media_player_progress_bar);
        apiInterface = (ApiInterface) APIClient.getClient().create(ApiInterface.class);
        getOfferList();
        return view;
    }

    private void getOfferList() {
        SendRequest.sendRequest(ApiConstant.ALL_OFFER_LIST_API, apiInterface.getAllOffer(SharedPrefrenceHandler.getInstance().getUSER_TOKEN()), this);
    }

    private void parseOffer(String response) {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(getOffer(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getPlanObserver()));
    }

    private DisposableObserver<OfferModel> getPlanObserver() {
        return new DisposableObserver<OfferModel>() {

            @Override
            public void onNext(OfferModel offer) {
                offerModel = offer;
                offerAdapter = new OfferAdapter(getActivity(), offerModel.getBody(), OfferFragment.this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                rvOfferList.setLayoutManager(mLayoutManager);
                rvOfferList.setItemAnimator(new DefaultItemAnimator());
                rvOfferList.setAdapter(offerAdapter);
                rvOfferList.setNestedScrollingEnabled(false);
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

    private Observable<OfferModel> getOffer(String response) {
        Gson gsonObj = new Gson();
        final OfferModel offerModel = gsonObj.fromJson(response, OfferModel.class);

        return Observable.create(new ObservableOnSubscribe<OfferModel>() {
            @Override
            public void subscribe(ObservableEmitter<OfferModel> emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    emitter.onNext(offerModel);
                    emitter.onComplete();
                }


            }
        });
    }


    @Override
    public void getResponse(JsonResponse response, int type) {
        if (response.getObject() != null) {
            parseOffer(response.getObject());
        }


    }

    @Override
    public void selectedPlan(int poistion) {
        Intent returnIntent = new Intent();
        OfferBody offerBody = offerModel.getBody().get(poistion);
        returnIntent.putExtra("offer_name", offerBody.getTitle());
        returnIntent.putExtra("offer_description", offerBody.getDescription());
        returnIntent.putExtra("discount_price", offerBody.getDiscount());
        getActivity().setResult(getActivity().RESULT_OK, returnIntent);
        getActivity().finish();

    }
}
