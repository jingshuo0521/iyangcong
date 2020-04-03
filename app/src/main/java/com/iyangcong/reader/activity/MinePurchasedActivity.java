package com.iyangcong.reader.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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
import com.iyangcong.reader.interfaceset.RetryCounter;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.Urls.PersonBuyBookListURL;

/**
 * Created by Administrator on 2016/12/23 0023.
 */

public class MinePurchasedActivity extends SwipeBackActivity {
    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.mine_purchased_ptrClassicFrameLayout)
    PtrClassicFrameLayout minePurchasedPtrClassicFrameLayout;
    @BindView(R.id.mine_purchased_listview)
    ListView minePurchasedListview;

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
    private MineExchangeAdapter minePurchasedAdapter;
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
        setContentView(R.layout.activity_mine_purchased);
        ButterKnife.bind(this);
        setMainHeadView();
        initView();
    }


    protected void initView() {
        minePurchasedAdapter = new MineExchangeAdapter(this, booklist);
        minePurchasedListview.setAdapter(minePurchasedAdapter);
        minePurchasedAdapter.notifyDataSetChanged();
        minePurchasedPtrClassicFrameLayout.setHorizontalScrollBarEnabled(false);

        minePurchasedPtrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {

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

        minePurchasedPtrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                pageNum++;
                getDatasFromNetwork();
            }
        });
        minePurchasedPtrClassicFrameLayout.setLoadMoreEnable(true);
        showLoadingDialog();
        getDatasFromNetwork();
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText("我的图书");
        btnBack.setImageResource(R.drawable.btn_back);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        booklist = new ArrayList<BookIntroduction>();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    int retry = 0;
    private void getDatasFromNetwork() {
        OkGo.get(PersonBuyBookListURL)
                .tag(this)
                .params("userId", CommonUtil.getUserId())
                .params("currentPageNum", pageNum)
                .params("pageSize", pageSize)
                .execute(new JsonCallback<IycResponse<List<BookIntroduction>>>(getApplicationContext()) {
                    @Override
                    public void onSuccess(IycResponse<List<BookIntroduction>> listIycResponse, Call call, Response response) {
                        setBooklist(listIycResponse.getData());
                        if (listIycResponse.getData().size() < pageNum) {
                            reFreshOrLoadMoreSuccess(true);
                        } else {
                            reFreshOrLoadMoreSuccess(false);
                        }
                        minePurchasedAdapter.notifyDataSetChanged();
                        dismissLoadingDialig();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        reFreshOrLoadMoreSuccess(false);
                        dismissLoadingDialig();
                    }

                    @Override
                    public void parseError(Call call, Exception e) {
                        super.parseError(call, e);
                        if(retry++< RetryCounter.MAX_RETRY_TIMES){
                            getDatasFromNetwork();
                        }else{
                            retry = 0;
                            dismissLoadingDialig();
                        }
                    }
                });
    }

    private void reFreshOrLoadMoreSuccess(boolean isEnd) {
        if (pageNum == 1) {
            refreshSuccess(minePurchasedPtrClassicFrameLayout, isEnd);
        } else {
            loadMoreSuccess(minePurchasedPtrClassicFrameLayout, isEnd);
        }
    }

}
