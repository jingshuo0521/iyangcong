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
import com.iyangcong.reader.adapter.MinePageCircleAdapter;
import com.iyangcong.reader.base.BaseFragment;
import com.iyangcong.reader.bean.MineCircle;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.LoadCountHolder;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.Urls.PersonAttendGroupsInfoListURL;
import static com.iyangcong.reader.utils.Urls.PersonCreateGroupsInfoListURL;
/**
 * Created by ljw on 2017/1/6.
 */

public class MineCircleFragment extends BaseFragment implements MinePageCircleAdapter.SegPositionCallback{

    @BindView(R.id.rv_mine_page)
    RecyclerView rvMinePage;
    @BindView(R.id.mine_ptrClassicFrameLayout)
    PtrClassicFrameLayout minePtrClassicFrameLayout;

    private MinePageCircleAdapter minePageDynamicsAdapter;
    private RecyclerAdapterWithHF mAdapter;
    Handler handler = new Handler();
    private LoadCountHolder holderForCreate = new LoadCountHolder();
    private LoadCountHolder holderForJoin = new LoadCountHolder();

    private List<MineCircle> mineCreateList = new ArrayList<>();
    private List<MineCircle> mineAttendList = new ArrayList<>();

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
        initData();
        rvMinePage.setPadding(0,0,0,0);
        return view;
    }

    @Override
    protected void initData() {
        minePageDynamicsAdapter = new MinePageCircleAdapter(mContext,mineCreateList,mineAttendList,true);
        mAdapter= new RecyclerAdapterWithHF(minePageDynamicsAdapter);
        minePageDynamicsAdapter.setSegPositionCallback(this);
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
                        getCreateCircleFromNetwork();
                        getJoinCircleFromNetwork();
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
                            getCreateCircleFromNetwork();
                        }else if (segTabposition == 1){
                            holderForJoin.loadMore();
                            getJoinCircleFromNetwork();
                        }
                        judgeIsLoadMore(segTabposition,sign);
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
    private void getCreateCircleFromNetwork(){
        OkGo.get(PersonCreateGroupsInfoListURL)
                .tag(this)
                .params("userid", userId)
                .params("pageNo", holderForCreate.getPage()  + 1)
                .params("pageSize", "20")
                .params("type",userId.equals(CommonUtil.getUserId()+"")?1:2)//type=1表示自己查看自己创建的圈子，2表示别人查看我创建的圈子
                .execute(new JsonCallback<IycResponse<List<MineCircle>>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<List<MineCircle>> listIycResponse, Call call, Response response) {
                        if (holderForCreate.isRefresh())
                            mineCreateList.clear();
                        for(MineCircle circle:listIycResponse.getData())
                            mineCreateList.add(circle);
                        if(holderForCreate.getPage()==0&&listIycResponse.getData().size()<20){
                            sign[0]=1;
                        }else{
                            sign[0]=0;
                        }
                        boolean isEnd = listIycResponse.getData().size() < 20;
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
    private void getJoinCircleFromNetwork(){
        OkGo.get(PersonAttendGroupsInfoListURL)
                .tag(this)
                .params("userid",userId)
                .params("pageNo", holderForJoin.getPage() + 1)
                .params("pageSize", "20")
                .execute(new JsonCallback<IycResponse<List<MineCircle>>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<List<MineCircle>> listIycResponse, Call call, Response response) {
                        if (holderForJoin.isRefresh())
                            mineAttendList.clear();
                        for(MineCircle circle:listIycResponse.getData())
                            mineAttendList.add(circle);
                        if(holderForJoin.getPage()==0&&listIycResponse.getData().size()<20){
                            sign[1]=1;
                        }else{
                            sign[1]=0;
                        }
                        boolean isEnd = listIycResponse.getData().size() < 20;
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
