package  com.mobileoid2.celltone.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.view.activity.ChangeToolBarTitleListiner;
import com.mobileoid2.celltone.network.APIClient;
import com.mobileoid2.celltone.network.ApiInterface;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.NetworkCallBack;
import com.mobileoid2.celltone.network.SendRequest;
import com.mobileoid2.celltone.network.jsonparsing.JsonResponse;
import com.mobileoid2.celltone.network.model.myPlan.AllPlan;
import com.mobileoid2.celltone.network.model.myPlan.CurrentPlan;
import com.mobileoid2.celltone.network.model.myPlan.CurrentPlanDetail;
import com.mobileoid2.celltone.network.model.myPlan.MyPlanModel;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;
import com.mobileoid2.celltone.view.adapter.MyPlanAdapter;
import com.mobileoid2.celltone.view.listener.PlanListiner;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class PlanFragment extends Fragment implements NetworkCallBack,PlanListiner {

    private ApiInterface apiInterface;
    private ProgressBar progressBar;
    private LinearLayout llMain;
    private TextView txtCurrentPlanName, txtExpired, txtPlanName, txtPlanPrice, txtDaysLeft, txtTotalMedia,
            txtContactsCount, txtUploadOwnMedia, txtRenew;
    private AppCompatButton upgradeButton;
    private RecyclerView planRecyclerView;
    private MyPlanModel myPlanModel;
    private AllPlan allPlan;
    private  ChangeToolBarTitleListiner changeToolBarTitleListiner;


    public static PlanFragment newInstance(ChangeToolBarTitleListiner changeToolBarTitleListiner) {
        PlanFragment fragment = new PlanFragment();
        // fragment.context = context;
        fragment.changeToolBarTitleListiner =changeToolBarTitleListiner;
        return fragment;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragmnent_my_plans, container, false);
        planRecyclerView = view.findViewById(R.id.plan_recycler_view);
        txtCurrentPlanName = view.findViewById(R.id.txt_current_plan_name);
        txtExpired = view.findViewById(R.id.txt_expired);
        txtPlanName = view.findViewById(R.id.txt_plan_name);
        txtPlanPrice = view.findViewById(R.id.txt_plan_price);
        txtDaysLeft = view.findViewById(R.id.txt_days_left);
        txtTotalMedia = view.findViewById(R.id.txt_total_media);
        txtContactsCount = view.findViewById(R.id.txt_contacts_count);
        txtUploadOwnMedia = view.findViewById(R.id.txt_upload_own_media);
        txtRenew = view.findViewById(R.id.txt_renew);
        llMain =view.findViewById(R.id.ll_main);
        llMain.setVisibility(View.GONE);
        upgradeButton = view.findViewById(R.id.upgrade_button);
        upgradeButton.setVisibility(View.GONE);
        progressBar =view.findViewById(R.id.media_player_progress_bar);
        apiInterface = (ApiInterface) APIClient.getClient().create(ApiInterface.class);
        /*Plan List*/
        getPlanList();
        upgradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToolBarTitleListiner.setTitle("Payment Method");
                PaymentMethodFragment paymentMethodFragment =  PaymentMethodFragment.newInstance(allPlan.getName(),
                        allPlan.getPrice(),""+allPlan.getMediaCount(),
                        ""+allPlan.getUserCount(),""+allPlan.getOwnMediaCount(),"1 Month");
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, paymentMethodFragment).addToBackStack(paymentMethodFragment.getClass().getName());
                ft.commit();
            }
        });
        return view;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void getPlanList() {
        SendRequest.sendRequest(ApiConstant.ALL_PLAN_LIST_API, apiInterface.getAllPlans(SharedPrefrenceHandler.getInstance().getUSER_TOKEN()), this);
    }

    private void parsePlan(String response) {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(getPlans(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getPlanObserver()));
    }

    private DisposableObserver<MyPlanModel> getPlanObserver() {
        return new DisposableObserver<MyPlanModel>() {

            @Override
            public void onNext(MyPlanModel planModel) {
                myPlanModel = planModel;
                MyPlanAdapter planAdapter = new MyPlanAdapter(getActivity(), myPlanModel.getBody().getAllPlans(),PlanFragment.this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                planRecyclerView.setLayoutManager(mLayoutManager);
                planRecyclerView.setItemAnimator(new DefaultItemAnimator());
                planRecyclerView.setAdapter(planAdapter);
                planRecyclerView.setNestedScrollingEnabled(false);
                CurrentPlan currentPlan = myPlanModel.getBody().getCurrentPlan();
                CurrentPlanDetail planDetail = myPlanModel.getBody().getCurrentPlanDetail();
                txtCurrentPlanName.setText(currentPlan.getPlanName());
                txtCurrentPlanName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                txtPlanName.setText(currentPlan.getPlanName());
                txtDaysLeft.setText("30 Days");
                txtTotalMedia.setText(planDetail.getMediaCount() + " media");
                txtUploadOwnMedia.setText(planDetail.getOwnMediaCount() + " Upload own media");
                txtContactsCount.setText(planDetail.getUserCount() + " Contacts");
                llMain.setVisibility(View.VISIBLE);
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


    private Observable<MyPlanModel> getPlans(String response) {
        Gson gsonObj = new Gson();
        final MyPlanModel planBody = gsonObj.fromJson(response, MyPlanModel.class);

        return Observable.create(new ObservableOnSubscribe<MyPlanModel>() {
            @Override
            public void subscribe(ObservableEmitter<MyPlanModel> emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    emitter.onNext(planBody);
                    emitter.onComplete();
                }


            }
        });
    }


    @Override
    public void getResponse(JsonResponse response, int type) {
        if (response.getObject() != null) {
            parsePlan(response.getObject());
        } else {

        }

    }

    @Override
    public void selectedPlan(int poistion) {
        allPlan = myPlanModel.getBody().getAllPlans().get(poistion);
        upgradeButton.setVisibility(View.VISIBLE);


    }
}
