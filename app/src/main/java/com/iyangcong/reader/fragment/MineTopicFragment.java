package com.iyangcong.reader.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.MinePageTopicAdapter;
import com.iyangcong.reader.base.BaseFragment;
import com.iyangcong.reader.bean.DiscoverTopic;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.LoadCountHolder;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.Urls.PersonCreateTopicURL;

/**
 * Created by ljw on 2017/1/6.
 */

public class MineTopicFragment extends BaseFragment implements MinePageTopicAdapter.SegPositionCallback{

    @BindView(R.id.rv_mine_page)
    RecyclerView rvMinePage;
    @BindView(R.id.mine_ptrClassicFrameLayout)
    PtrClassicFrameLayout minePtrClassicFrameLayout;

    private MinePageTopicAdapter minePageDynamicsAdapter;
    private RecyclerAdapterWithHF mAdapter;
    Handler handler = new Handler();
    private LoadCountHolder holderForCreate = new LoadCountHolder();
    private LoadCountHolder holderForJoin = new LoadCountHolder();

    private List<DiscoverTopic> CreateTopicList = new ArrayList<>();
    private List<DiscoverTopic> JoinTopicList = new ArrayList<>();

    private int segTabposition = 0;
    private int[] sign = new int[]{0,0};
    private String userId;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine_page, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        if(bundle!=null){
            userId = bundle.getString(Constants.USER_ID);
        }else if(bundle == null) {
            userId = "";
        }
        rvMinePage.setPadding(0,0,0,0);
        initData();
        return view;
    }

    @Override
    protected void initData() {
        minePageDynamicsAdapter = new MinePageTopicAdapter(mContext,CreateTopicList,JoinTopicList,0);
        minePageDynamicsAdapter.setSegPositionCallback(this);
        mAdapter= new RecyclerAdapterWithHF(minePageDynamicsAdapter);
        rvMinePage.setAdapter(minePageDynamicsAdapter);
        rvMinePage.setLayoutManager(new LinearLayoutManager(mContext));
        rvMinePage.setAdapter(mAdapter);
        minePtrClassicFrameLayout.setHorizontalScrollBarEnabled(false);
        minePtrClassicFrameLayout.post(new Runnable() {
            @Override
            public void run() {
                minePtrClassicFrameLayout.autoRefresh(true);
            }
        });
        minePtrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return false;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        holderForCreate.refresh();
                        holderForJoin.refresh();
                        getCreateTopicFromNetwork();
                        getJoinTopicFromNetwork();
                        minePtrClassicFrameLayout.refreshComplete();
                        judgeIsLoadMore(segTabposition,sign);

                    }
                });
            }
        });
        minePtrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(segTabposition==0){
                            holderForCreate.loadMore();
                            getCreateTopicFromNetwork();
                        }else if (segTabposition == 1){
                            holderForJoin.loadMore();
                            getJoinTopicFromNetwork();
                        }
                        judgeIsLoadMore(segTabposition,sign);
                        minePtrClassicFrameLayout.loadMoreComplete(true);
//
                    }
                });
            }
        });
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // TODO: inflate a fragment view
//        View rootView = super.onCreateView(inflater, container, savedInstanceState);
//        ButterKnife.bind(this, rootView);
//        return rootView;
//    }
    private void getCreateTopicFromNetwork(){
            int i=holderForCreate.getPage() + 1;
            OkGo.get(PersonCreateTopicURL)
                    .tag(this)
                    .params("currentPageNum", holderForCreate.getPage() + 1 )
                    .params("pageSize", "5")
                    .params(Constants.USER_ID, userId)
                    .execute(new JsonCallback<IycResponse<List<DiscoverTopic>>>(mContext) {
                        @Override
                        public void onSuccess(IycResponse<List<DiscoverTopic>> listIycResponse, Call call, Response response) {
                            if (holderForCreate.isRefresh())
                                CreateTopicList.clear();
                            for(DiscoverTopic topic:listIycResponse.getData())
                                CreateTopicList.add(topic);
                            List<DiscoverTopic> list =CreateTopicList;
                            if(holderForCreate.getPage()==0&&listIycResponse.getData().size()<5){
                                sign[0]=1;
                            }else{
                                sign[0]=0;
                            }
                            boolean isEnd = listIycResponse.getData().size() < 5;
                            if(holderForCreate.getPage() > 0){
                                loadMoreSuccess(minePtrClassicFrameLayout, isEnd);
                                if (isEnd){
                                    sign[0]=1;
                                }else {
                                    sign[0]=0;
                                }
                            }
                            minePageDynamicsAdapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            if(holderForCreate.getPage() > 0){
                                holderForCreate.loadMoreFailed();
                                sign[0]=1;
                                loadMoreFailed(minePtrClassicFrameLayout);
                            }
                        }
                    });



    }

    private void getJoinTopicFromNetwork(){
        OkGo.get(Urls.PersonJoinTopicURL)
                .tag(this)
                .params("currentPageNum", holderForJoin.getPage() + 1)
                .params("pageSize", "5")
                .params(Constants.USER_ID,userId)
                .execute(new JsonCallback<IycResponse<List<DiscoverTopic>>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<List<DiscoverTopic>> listIycResponse, Call call, Response response) {
                        if (holderForJoin.isRefresh())
                            JoinTopicList.clear();
                        for(DiscoverTopic topic:listIycResponse.getData())
                            JoinTopicList.add(topic);
                        if(holderForJoin.getPage()==0&&listIycResponse.getData().size()<5){
//                            minePtrClassicFrameLayout.setLoadMoreEnable(false);
                            sign[1]=1;
                        }else{
//                            minePtrClassicFrameLayout.setLoadMoreEnable(true);
                            sign[1]=0;
                        }
                        boolean isEnd = listIycResponse.getData().size() < 5;
                        if(holderForJoin.getPage() > 0){
                            loadMoreSuccess(minePtrClassicFrameLayout, isEnd);
                            if (isEnd){
                                sign[1]=1;
                            }else {
                                sign[1]=0;
                            }
                        }
                        minePageDynamicsAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if(holderForJoin.getPage() > 0){
                            holderForJoin.loadMoreFailed();
                            sign[1]=1;
                            loadMoreFailed(minePtrClassicFrameLayout);
                        }
                    }
                });
    }
    @Override
    public void getSegPosition(int position) {
            segTabposition = position;
            judgeIsLoadMore(segTabposition,sign);
    }
    private void judgeIsLoadMore(int segTabposition,int[] sign){
        switch (segTabposition){
            case 0:
                switch (sign[0]){
                    case 0:
                        minePtrClassicFrameLayout.setLoadMoreEnable(true);
                        break;
                    case 1:
                        minePtrClassicFrameLayout.setLoadMoreEnable(false);
                        break;
                }
            case 1:
                switch (sign[1]){
                    case 0:
                        minePtrClassicFrameLayout.setLoadMoreEnable(true);
                        break;
                    case 1:
                        minePtrClassicFrameLayout.setLoadMoreEnable(false);
                        break;
                }
        }
    }
}