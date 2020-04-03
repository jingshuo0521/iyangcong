package com.iyangcong.reader.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.BookSpecialTopicAdapter;
import com.iyangcong.reader.bean.MarketBookListItem;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.LoadCountHolder;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/30.
 */

public class BookMarketSpecialTopicActivity extends SwipeBackActivity {
    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.rv_book_market_topic)
    RecyclerView rvBookMarketTopic;
    @BindView(R.id.book_market_topic_list_ptrClassicFrameLayout)
    PtrClassicFrameLayout bookMarketTopicListPtrClassicFrameLayout;

    Handler handler = new Handler();
    private List<MarketBookListItem> bookList;
    private RecyclerAdapterWithHF mBookSpecialTopicAdapter;
    private LoadCountHolder holderForReview = new LoadCountHolder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_market_special_topic);
        ButterKnife.bind(this);
        setMainHeadView();
        initView();
    }


    @Override
    protected void initData(Bundle savedInstanceState) {
        bookList = new ArrayList<MarketBookListItem>();
    }

    @Override
    protected void initView() {
        mBookSpecialTopicAdapter = new RecyclerAdapterWithHF(new BookSpecialTopicAdapter(this, bookList));
        rvBookMarketTopic.setLayoutManager(new LinearLayoutManager(this));
        rvBookMarketTopic.setAdapter(mBookSpecialTopicAdapter);
        bookMarketTopicListPtrClassicFrameLayout.setHorizontalScrollBarEnabled(false);
        bookMarketTopicListPtrClassicFrameLayout.post(new Runnable() {

            @Override
            public void run() {
                bookMarketTopicListPtrClassicFrameLayout.autoRefresh(true);
            }
        });

        bookMarketTopicListPtrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {
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
                        bookMarketTopicListPtrClassicFrameLayout.refreshComplete();
                        bookMarketTopicListPtrClassicFrameLayout.setLoadMoreEnable(true);
                    }
                });
            }
        });

        bookMarketTopicListPtrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void loadMore() {
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        holderForReview.loadMore();
                        getDatasFromNetwork();
                        bookMarketTopicListPtrClassicFrameLayout.loadMoreComplete(true);
                    }
                }, 0);
            }
        });
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText("阅读专题");
        btnBack.setImageResource(R.drawable.btn_back);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick({R.id.btnBack, R.id.btnFunction})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnFunction:
                break;
        }
    }

    private void getDatasFromNetwork() {
        OkGo.get(Urls.BookMarketSubjectURL)
                .tag(this)
//                .params("page", holderForReview.getPage() + 1)
//                .params("pageNum", "8")
                .execute(new JsonCallback<IycResponse<List<MarketBookListItem>>>(context) {
                    @Override
                    public void onSuccess(IycResponse<List<MarketBookListItem>> listIycResponse, Call call, Response response) {
                        if (holderForReview.isRefresh())
                            bookList.clear();
                        for (MarketBookListItem bookIntroduction : listIycResponse.getData())
                            bookList.add(bookIntroduction);
                        loadMoreSuccess(bookMarketTopicListPtrClassicFrameLayout, true);
                        mBookSpecialTopicAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (holderForReview.getPage() > 0) {
                            holderForReview.loadMoreFailed();
                            loadMoreFailed(bookMarketTopicListPtrClassicFrameLayout);
                        }
                    }
                });
    }
}
