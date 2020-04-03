package com.iyangcong.reader.activity;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.DiscoverReviewAdapter;
import com.iyangcong.reader.bean.DiscoverReviews;
import com.iyangcong.reader.bean.SubDiscoverReviews;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

public class ReviewListAcitivity extends SwipeBackActivity {

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.rv_review_list)
    RecyclerView rvReviewList;
    @BindView(R.id.activity_review_list)
    LinearLayout activityReviewList;
    DiscoverReviewAdapter discoverReviewAdapter;
    private List<DiscoverReviews> reviewsList;
    private int mBookId;
    private String mBookName;
    private int mPageNo = 0;
    private int mPageSize = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);
        ButterKnife.bind(this);
        setMainHeadView();
        initView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mBookId = getIntent().getIntExtra(Constants.BOOK_ID, 0);
        mBookName = getIntent().getStringExtra(Constants.BOOK_NAME);
        reviewsList = new ArrayList<>();


    }

    @Override
    protected void initView() {
        discoverReviewAdapter = new DiscoverReviewAdapter(context, reviewsList, true);
        rvReviewList.setAdapter(discoverReviewAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvReviewList.setLayoutManager(llm);
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText(mBookName);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setImageResource(R.drawable.btn_back);
    }

    @OnClick(R.id.btnBack)
    public void onClick() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getReviewList(mBookId, mPageNo, mPageSize);
    }

    private void getReviewList(int bookid, int pageNo, int pageSize) {
        OkGo.get(Urls.BookMarketGetReviewByBookId)
                .params("bookid", bookid)
                .params("type", 1)
                .params("pageNo", pageNo)
                .params("pageSize", pageSize)
                .execute(new JsonCallback<IycResponse<List<SubDiscoverReviews>>>(context) {
                    @Override
                    public void onSuccess(IycResponse<List<SubDiscoverReviews>> listIycResponse, Call call, Response response) {
                        reviewsList.clear();
                        if (listIycResponse.getData() != null) {
                            for (SubDiscoverReviews reviews : listIycResponse.getData()) {
                                reviewsList.add(reviews);
                            }
                            discoverReviewAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        ToastCompat.makeText(context, getString(R.string.net_error_tip), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
