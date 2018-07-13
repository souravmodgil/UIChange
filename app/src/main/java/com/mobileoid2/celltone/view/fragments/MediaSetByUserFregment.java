package  com.mobileoid2.celltone.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoid2.celltone.R;
import com.mobileoid2.celltone.network.model.setMediaByUser.SetMediaByUserBody;
import com.mobileoid2.celltone.network.model.setMediaByUser.SetMediaByUserResponse;
import com.mobileoid2.celltone.view.activity.ChangeToolBarTitleListiner;
import com.mobileoid2.celltone.network.APIClient;
import com.mobileoid2.celltone.network.ApiConstant;
import com.mobileoid2.celltone.network.ApiInterface;
import com.mobileoid2.celltone.network.NetworkCallBack;
import com.mobileoid2.celltone.network.SendRequest;
import com.mobileoid2.celltone.network.jsonparsing.JsonResponse;
import com.mobileoid2.celltone.network.model.upload_media_list.UploadMediaListModle;
import com.mobileoid2.celltone.pojo.MediaListReqeuestPojo;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;
import com.mobileoid2.celltone.view.activity.UploadActivity;
import com.mobileoid2.celltone.view.adapter.SetMediaByUserAdapter;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MediaSetByUserFregment extends Fragment implements NetworkCallBack {
    private int totalMediaCoun;
    private TextView txtUserUploadCount,btUpload;
    private RecyclerView rcUserUploadList;
    private ApiInterface apiInterface;
    private ProgressBar progressBar;
    private LinearLayout llMain;
    private UploadMediaListModle uploadMediaListModle;
    private ChangeToolBarTitleListiner changeToolBarTitleListiner;

    public static MediaSetByUserFregment newInstance(int totalMediaCount, ChangeToolBarTitleListiner changeToolBarTitleListiner) {
        MediaSetByUserFregment fragment = new MediaSetByUserFregment();
        fragment.totalMediaCoun = totalMediaCount;
        fragment.changeToolBarTitleListiner =changeToolBarTitleListiner;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_user_media, container, false);
        rcUserUploadList = view.findViewById(R.id.rc_user_upload_list);
        txtUserUploadCount = view.findViewById(R.id.txt_user_upload_count);
        progressBar =view.findViewById(R.id.media_player_progress_bar);
        llMain =view.findViewById(R.id.ll_main);
        btUpload =view.findViewById(R.id.bt_upload);
       // changeToolBarTitleListiner.setTitle("Usage");
        changeToolBarTitleListiner.setTitle(getString(R.string.profile_usage));
        apiInterface = (ApiInterface) APIClient.getClient().create(ApiInterface.class);
        getMediaList();
        btUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UploadActivity.class));
            }
        });

        return view;
    }

    private void getMediaList() {
        MediaListReqeuestPojo mediaListReqeuestPojo = new MediaListReqeuestPojo();
        mediaListReqeuestPojo.setSkip(null);
        mediaListReqeuestPojo.setLimit(null);
        SendRequest.sendRequest(ApiConstant.SET_MEDIA_BY_USER_API, apiInterface.getAllMediaSetByUser(SharedPrefrenceHandler.getInstance().getUSER_TOKEN()), this);

    }

    private void parseResponse(String response) {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(getUploadList(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(UploadMediaObserver()));
    }

    private DisposableObserver<List<SetMediaByUserBody>> UploadMediaObserver() {
        return new DisposableObserver<List<SetMediaByUserBody>>() {

            @Override
            public void onNext(List<SetMediaByUserBody> modle) {

                SetMediaByUserAdapter planAdapter = new SetMediaByUserAdapter(getActivity(),modle );
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                rcUserUploadList.setLayoutManager(mLayoutManager);
                rcUserUploadList.setItemAnimator(new DefaultItemAnimator());
                rcUserUploadList.setAdapter(planAdapter);
                rcUserUploadList.setNestedScrollingEnabled(false);
                progressBar.setVisibility(View.GONE);
                llMain.setVisibility(View.VISIBLE);
                if(totalMediaCoun==0)
                    txtUserUploadCount.setText(""+modle.size()+"/"+"Unlimited");
                if(totalMediaCoun>0)
                    txtUserUploadCount.setText(""+modle.size()+"/"+totalMediaCoun);


            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }


    private Observable<List<SetMediaByUserBody>> getUploadList(String response) {
        Gson gsonObj = new Gson();
        final SetMediaByUserResponse responseUpload = gsonObj.fromJson(response, SetMediaByUserResponse.class);

        return Observable.create(new ObservableOnSubscribe<List<SetMediaByUserBody>>() {
            @Override
            public void subscribe(ObservableEmitter<List<SetMediaByUserBody>> emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    List<SetMediaByUserBody> lists = responseUpload.getBody();
                    emitter.onNext(lists);
                    emitter.onComplete();
                }


            }
        });
    }

    @Override
    public void getResponse(JsonResponse response, int type) {
        if(response.getObject()!=null)
            parseResponse(response.getObject());

    }
}

