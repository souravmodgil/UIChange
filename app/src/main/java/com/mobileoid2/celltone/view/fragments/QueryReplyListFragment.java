package com.mobileoid2.celltone.view.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
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
import com.mobileoid2.celltone.network.model.feedback.FeedBackModel;
import com.mobileoid2.celltone.network.model.feedback.comment.CommentModel;
import com.mobileoid2.celltone.pojo.CommentRequest;
import com.mobileoid2.celltone.pojo.QUERYREQUEST;
import com.mobileoid2.celltone.utility.SharedPrefrenceHandler;
import com.mobileoid2.celltone.utility.Utils;
import com.mobileoid2.celltone.view.SeparatorDecoration;
import com.mobileoid2.celltone.view.adapter.QueryListAdapter;
import com.mobileoid2.celltone.view.adapter.QueryReplyAdapter;
import com.mobileoid2.celltone.view.listener.QueryLisiner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class QueryReplyListFragment extends Fragment implements NetworkCallBack {
    private TextView txtTitle,txtType,dateQuery;
    private ProgressBar progressBar;
    private RecyclerView rcQueryList;
    private EditText inputComments;
    private FeedBackList feedBackList;
    private ApiInterface apiInterface;
    private  String id ="";

    public  static QueryReplyListFragment newInstance (FeedBackList feedBackList)
    {
        QueryReplyListFragment queryFragment = new QueryReplyListFragment();
        queryFragment.feedBackList= feedBackList;
        return queryFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_query_reply, container, false);
        apiInterface = (ApiInterface) APIClient.getClient().create(ApiInterface.class);
        txtTitle = view.findViewById(R.id.txt_title);
        txtType =view.findViewById(R.id.txt_type);
        progressBar = view.findViewById(R.id.progress_bar);
        dateQuery =view.findViewById(R.id.date_query);
        rcQueryList =view.findViewById(R.id.rc_query_list);
        inputComments =view.findViewById(R.id.input_comments);
        txtTitle.setText(feedBackList.getTitle());
        id =feedBackList.getId();
        dateQuery.setText(Utils.parseDate(feedBackList.getCreatedAt()));
        if (feedBackList.getQueryType().equals("query"))
            txtType.setBackground(getActivity().getDrawable(R.drawable.query_shape));
        else
            txtType.setBackground(getActivity().getDrawable(R.drawable.issue_shape));
        txtType.setText(feedBackList.getQueryType());
        List<Comment> commentList = new ArrayList<>();

        Comment comment = new Comment();
        comment.setFrom("user");
        comment.setCreatedAt(feedBackList.getCreatedAt());
        comment.setReply(feedBackList.getDescription());
        commentList.add(comment);
        if(feedBackList.getComments()!=null &&feedBackList.getComments().size()>0)
            commentList.addAll(feedBackList.getComments());
        QueryReplyAdapter adapter = new QueryReplyAdapter(getActivity(),commentList,feedBackList.getUserId().getName());
        SeparatorDecoration separatorDecoration = new SeparatorDecoration(getActivity(), Color.parseColor("#e8e8e8"), 1.5F);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rcQueryList.setLayoutManager(mLayoutManager);
        rcQueryList.setItemAnimator(new DefaultItemAnimator());
        rcQueryList.setItemAnimator(new DefaultItemAnimator());
        rcQueryList.addItemDecoration(separatorDecoration);
        rcQueryList.setAdapter(adapter);



        return view;
    }
    public void sendComment()
    {
     if(inputComments.length()>0)
     {
         progressBar.setVisibility(View.VISIBLE);
         CommentRequest commentRequest = new CommentRequest();
         commentRequest.setId(id);
         commentRequest.setReply(inputComments.getText().toString());
         SendRequest.sendRequest(ApiConstant.QUERY_SEND_COMMENT_API, apiInterface.sendComment(SharedPrefrenceHandler.getInstance().getUSER_TOKEN(),

                 commentRequest), this);
     }
     else
         Toast.makeText(getActivity(),"Please Fill Comment",Toast.LENGTH_LONG).show();
    }

    private void parseResponse(String response) {
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(getList(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(UploadMediaObserver()));
    }

    private DisposableObserver<CommentModel> UploadMediaObserver() {
        return new DisposableObserver<CommentModel>() {

            @Override
            public void onNext(CommentModel modle) {

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


    private Observable<CommentModel> getList(String response) {
        Gson gsonObj = new Gson();
        final CommentModel responseUpload = gsonObj.fromJson(response, CommentModel.class);

        return Observable.create(new ObservableOnSubscribe<CommentModel>() {
            @Override
            public void subscribe(ObservableEmitter<CommentModel> emitter) throws Exception {
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


