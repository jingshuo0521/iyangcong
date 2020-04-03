package com.iyangcong.reader.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.DiscoverTopicListAdapter;
import com.iyangcong.reader.base.BaseActivity;
import com.iyangcong.reader.bean.DiscoverCircleDetail;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lg on 2017/1/13.
 */

public class DiscoverTopicListActivity extends BaseActivity {

    private Handler handler = new Handler();
    private DiscoverCircleDetail discoverCircleDetail;

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.rv_discover_topic_list)
    RecyclerView rvDiscoverTopicList;
    @BindView(R.id.discover_topic_list_ptrClassicFrameLayout)
    PtrClassicFrameLayout discoverTopicListPtrClassicFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_topic_list);
        ButterKnife.bind(this);
        setMainHeadView();
        initView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initView() {
        rvDiscoverTopicList.setLayoutManager(new LinearLayoutManager(this));
        DiscoverTopicListAdapter discoverTopicListAdapter = new DiscoverTopicListAdapter(this,discoverCircleDetail);
        rvDiscoverTopicList.setAdapter(new RecyclerAdapterWithHF(discoverTopicListAdapter));
        discoverTopicListPtrClassicFrameLayout.setHorizontalScrollBarEnabled(false);
        discoverTopicListPtrClassicFrameLayout.post(new Runnable() {

            @Override
            public void run() {
                discoverTopicListPtrClassicFrameLayout.autoRefresh(true);
            }
        });

        discoverTopicListPtrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler(){
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        discoverTopicListPtrClassicFrameLayout.refreshComplete();
                        discoverTopicListPtrClassicFrameLayout.setLoadMoreEnable(true);
                    }
                }, 0);
            }
        });

        discoverTopicListPtrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void loadMore() {
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        discoverTopicListPtrClassicFrameLayout.loadMoreComplete(true);
                    }
                }, 0);
            }
        });
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText("话题列表");
        btnBack.setImageResource(R.drawable.btn_back);
        btnFunction.setImageResource(R.drawable.ic_add);
    }
}
