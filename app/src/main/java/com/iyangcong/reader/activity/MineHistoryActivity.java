package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.MineExchangeAdapter;
import com.iyangcong.reader.bean.BookIntroduction;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.store.PersistentCookieStore;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.Urls.PersonHistoryBookListURL;

public class MineHistoryActivity extends SwipeBackActivity {

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.mine_collection_listview)
    ListView mineCollectionListview;
    @BindView(R.id.mine_collection_ptrClassicFrameLayout)
    PtrClassicFrameLayout mineCollectionPtrClassicFrameLayout;

    private int pageNum = 1;
    private int pageSize = 8;

    @OnClick({R.id.btnBack, R.id.btnFunction})
    void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnFunction:
                break;
        }
    }

    private List<BookIntroduction> booklist;
    private MineExchangeAdapter mineCollectionAdapter;
    Handler handler = new Handler();

    public List<BookIntroduction> getBooklist() {
        return booklist;
    }

    public void setBooklist(List<BookIntroduction> booklist) {
        if (pageNum == 1) {
            this.booklist.clear();
        }
        this.booklist.addAll(booklist);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_collection);
        ButterKnife.bind(this);
        setMainHeadView();
        initView();
    }


    protected void initView() {
        mineCollectionAdapter = new MineExchangeAdapter(this, booklist);
        mineCollectionListview.setAdapter(mineCollectionAdapter);
        mineCollectionListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MineHistoryActivity.this, BookMarketBookDetailsActivity.class);
                intent.putExtra("bookId", (int) booklist.get(position).getBookId());
                intent.putExtra("bookName", booklist.get(position).getBookName());
                startActivity(intent);
            }
        });
        mineCollectionPtrClassicFrameLayout.setHorizontalScrollBarEnabled(false);
        mineCollectionPtrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pageNum = 1;
                        getDatasFromNetwork();
                    }
                });
            }
        });

        mineCollectionPtrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                pageNum++;
                getDatasFromNetwork();
            }
        });

        mineCollectionPtrClassicFrameLayout.setLoadMoreEnable(true);
        showLoadingDialog();
        getDatasFromNetwork();

//        mineCollectionPtrClassicFrameLayout.post(new Runnable() {
//
//            @Override
//            public void run() {
//                mineCollectionPtrClassicFrameLayout.autoRefresh(true);
//            }
//        });

//        mineCollectionPtrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {
//            @Override
//            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
//                return false;
//            }
//
//            @Override
//            public void onRefreshBegin(PtrFrameLayout frame) {
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        getDatasFromNetwork();
//                        mineCollectionPtrClassicFrameLayout.refreshComplete();
//                        mineCollectionPtrClassicFrameLayout.setLoadMoreEnable(false);
//                    }
//                });
//            }
//        });

    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText(getResources().getString(R.string.mine_history));
        btnBack.setImageResource(R.drawable.btn_back);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        booklist = new ArrayList<BookIntroduction>();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        mineCollectionPtrClassicFrameLayout.autoRefresh();
    }

    private void getDatasFromNetwork() {
        //showLoadingDialog();
        PersistentCookieStore hhdj = new PersistentCookieStore();
        boolean hj= hhdj.removeAllCookie();
        if (hj){
            OkGo.get(PersonHistoryBookListURL)
                    .tag(this)
                    .params("userId", CommonUtil.getUserId())
                    .params("currentPageNum", pageNum)
                    .params("pageSize", pageSize)
                    .execute(new JsonCallback<IycResponse<List<BookIntroduction>>>(this) {
                        @Override
                        public void onSuccess(IycResponse<List<BookIntroduction>> listIycResponse, Call call, Response response) {
                            Logger.i("succeed", listIycResponse.getData().toString());
                            dismissLoadingDialig();
                            setBooklist(listIycResponse.getData());
                            if (listIycResponse.getData().size() < pageNum) {
                                reFreshOrLoadMoreSuccess(true);
                            } else {
                                reFreshOrLoadMoreSuccess(false);
                            }
                            mineCollectionAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            dismissLoadingDialig();
                            reFreshOrLoadMoreSuccess(false);
                        }
                    });
        }

    }

    private void reFreshOrLoadMoreSuccess(boolean isEnd) {
        if (pageNum == 1) {
            refreshSuccess(mineCollectionPtrClassicFrameLayout, isEnd);
        } else {
            loadMoreSuccess(mineCollectionPtrClassicFrameLayout, isEnd);
        }
    }
}
