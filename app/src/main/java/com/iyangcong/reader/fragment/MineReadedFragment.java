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
import com.iyangcong.reader.adapter.MinePageRaededAdapter;
import com.iyangcong.reader.base.BaseFragment;
import com.iyangcong.reader.bean.MineReaded;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.LoadCountHolder;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.Urls.PersonReadedBookURL;

/**
 * Created by ljw on 2017/1/6.
 */

public class MineReadedFragment extends BaseFragment {

    @BindView(R.id.rv_mine_page)
    RecyclerView rvMinePage;
    @BindView(R.id.mine_ptrClassicFrameLayout)
    PtrClassicFrameLayout minePtrClassicFrameLayout;

    private MinePageRaededAdapter minePageRaededAdapter;
    private List<MineReaded> mineReadedList = new ArrayList<>();
    Handler handler = new Handler();
    private LoadCountHolder holderForReview = new LoadCountHolder();
    private RecyclerAdapterWithHF mAdapter;
    private String userId;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine_page, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        if (bundle != null) {
            userId = bundle.getString(Constants.USER_ID);
        } else if (bundle == null) {
            userId = "";
        }
        initData();
        return view;
    }

    @Override
    protected void initData() {
        minePageRaededAdapter = new MinePageRaededAdapter(mContext, mineReadedList);
        mAdapter = new RecyclerAdapterWithHF(minePageRaededAdapter);
        rvMinePage.setAdapter(minePageRaededAdapter);
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
                        minePtrClassicFrameLayout.setLoadMoreEnable(true);
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

    private void getDatasFromNetwork() {
        OkGo.get(PersonReadedBookURL)
                .tag(this)
                .params("currentPageNum", holderForReview.getPage() + 1)
                .params("pageSize", "5")
                .params(Constants.USER_ID, userId)
                .execute(new JsonCallback<IycResponse<List<MineReaded>>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<List<MineReaded>> listIycResponse, Call call, Response response) {
                        if (holderForReview.isRefresh())
                            mineReadedList.clear();
                        if (listIycResponse.getData() != null && listIycResponse.getData().size() > 0) {
                            for (MineReaded books : listIycResponse.getData())
                                mineReadedList.add(books);
                            if (holderForReview.getPage() == 0 && listIycResponse.getData().size() < 5) {
                                minePtrClassicFrameLayout.setLoadMoreEnable(false);
                            }
                            boolean isEnd = listIycResponse.getData().size() < 5;
                            if (holderForReview.getPage() > 0) {
                                loadMoreSuccess(minePtrClassicFrameLayout, isEnd);
                            }
                        }
                        minePageRaededAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (holderForReview.getPage() > 0) {
                            holderForReview.loadMoreFailed();
                            loadMoreFailed(minePtrClassicFrameLayout);
                        }
                    }
                });
    }
}