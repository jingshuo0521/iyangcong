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
import com.iyangcong.reader.adapter.MinePageDynamicsAdapter;
import com.iyangcong.reader.base.BaseFragment;
import com.iyangcong.reader.bean.MineDynamic;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.interfaceset.DeviceType;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.LoadCountHolder;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.Urls.PersonDynamicStateURL;

/**
 * Created by ljw on 2017/1/6.
 */

public class MineDynamicsFragment extends BaseFragment {

    @BindView(R.id.rv_mine_page)
    RecyclerView rvMinePage;
    @BindView(R.id.mine_ptrClassicFrameLayout)
    PtrClassicFrameLayout minePtrClassicFrameLayout;

    private MinePageDynamicsAdapter dynamicsAdapter;
    private RecyclerAdapterWithHF mAdapter;
    private List<MineDynamic> mineDynamicsList = new ArrayList<>();
    Handler handler = new Handler();
    private LoadCountHolder holderForReview = new LoadCountHolder();
    private String userId;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine_page, container, false);
        Bundle bundle = getArguments();
        if(bundle!=null){
            userId = bundle.getString(Constants.USER_ID);
        }
        else if(bundle == null) {
            userId = "";
        }
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    @Override
    protected void initData() {
        dynamicsAdapter = new MinePageDynamicsAdapter(mContext,mineDynamicsList);
        mAdapter= new RecyclerAdapterWithHF(dynamicsAdapter);
        rvMinePage.setAdapter(dynamicsAdapter);
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
                        holderForReview.refresh();
                        getDatasFromNetwork();
                        minePtrClassicFrameLayout.refreshComplete();

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
                        holderForReview.loadMore();
                        getDatasFromNetwork();
                        minePtrClassicFrameLayout.loadMoreComplete(true);
//                        Toast.makeText(R ecyclerViewActivity.this, "load more complete", Toast.LENGTH_SHORT).show();
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

    private void getDatasFromNetwork(){
    OkGo.get(PersonDynamicStateURL)
            .tag(this)
            .params(Constants.USER_ID, userId)
            .params("currentPageNum", holderForReview.getPage() + 1)
            .params("pageSize", "10")
            .execute(new JsonCallback<IycResponse<List<MineDynamic>>>(mContext) {
                @Override
                public void onSuccess(IycResponse<List<MineDynamic>> listIycResponse, Call call, Response response) {
                      if (holderForReview.isRefresh())
                    mineDynamicsList.clear();
                    for(MineDynamic dynamic:listIycResponse.getData())
                        mineDynamicsList.add(dynamic);
                      if(holderForReview.getPage()==0&&listIycResponse.getData().size()<10){
                          minePtrClassicFrameLayout.setLoadMoreEnable(false);
                      }else{
                          minePtrClassicFrameLayout.setLoadMoreEnable(true);
                      }
                      boolean isEnd = listIycResponse.getData().size() < 10;
                      if(holderForReview.getPage() > 0){
                          loadMoreSuccess(minePtrClassicFrameLayout, isEnd);
                      }
                    dynamicsAdapter.notifyDataSetChanged();
                    }


                @Override
                public void onError(Call call, Response response, Exception e) {
                    Logger.i("nononono");
                    if(holderForReview.getPage() > 0){
                        holderForReview.loadMoreFailed();
                        loadMoreFailed(minePtrClassicFrameLayout);
                    }
                }
            });
}
}
