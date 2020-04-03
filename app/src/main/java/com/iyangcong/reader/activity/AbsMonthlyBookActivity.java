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
import com.iyangcong.reader.adapter.BookMarketMonthlyAdapter;
import com.iyangcong.reader.bean.MonthlyMarketBookListItem;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.LoadCountHolder;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

public class AbsMonthlyBookActivity extends SwipeBackActivity {

	@BindView(R.id.btnBack)
	ImageButton btnBack;
	@BindView(R.id.textHeadTitle)
	TextView textHeadTitle;
	@BindView(R.id.btnFunction)
	ImageButton btnFunction;
	@BindView(R.id.btnFunction1)
	ImageButton btnFunction1;
	@BindView(R.id.layout_header)
	LinearLayout layoutHeader;
	@BindView(R.id.rv_monthly_book)
	RecyclerView rvMonthlyBook;
	@BindView(R.id.ptrClassicFrameLayout_monthly_book_list)
	PtrClassicFrameLayout mPtrClassicFrameLayoutMonthlyBookList;
	protected BookMarketMonthlyAdapter monthlyAdapter;
	protected RecyclerAdapterWithHF adapterWithHF;
	protected List<MonthlyMarketBookListItem> mItemList;
	protected LoadCountHolder loadCountHolder = new LoadCountHolder(){
		@Override
		public void refresh() {
			setPage(1);
			setRefresh(true);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_market_monthly);
		ButterKnife.bind(this);
		setMainHeadView();
		initView();
	}


	@Override
	protected void initData(Bundle savedInstanceState) {
		mItemList = new ArrayList<>();
		monthlyAdapter = new BookMarketMonthlyAdapter(mItemList, context);
	}

	protected void loadMoreHandler() {
		loadCountHolder.loadMore();
	}

	protected void refreshHandler() {
		loadCountHolder.refresh();
	}

	@Override
	protected void initView() {
		LinearLayoutManager llm = new LinearLayoutManager(this);
		adapterWithHF = new RecyclerAdapterWithHF(monthlyAdapter);
		rvMonthlyBook.setLayoutManager(llm);
		rvMonthlyBook.setAdapter(adapterWithHF);
		final Handler handler = new Handler();
		mPtrClassicFrameLayoutMonthlyBookList.setHorizontalScrollBarEnabled(false);
		mPtrClassicFrameLayoutMonthlyBookList.setPtrHandler(new PtrDefaultHandler() {
			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						refreshHandler();
					}
				},0);
			}
		});
		mPtrClassicFrameLayoutMonthlyBookList.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void loadMore() {
				loadMoreHandler();
			}
		});
	}

	@Override
	protected void setMainHeadView() {
		btnBack.setImageResource(R.drawable.btn_back);
		btnBack.setVisibility(View.VISIBLE);
		textHeadTitle.setText(getTitle());
		textHeadTitle.setVisibility(View.VISIBLE);
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

	@Override
	protected void onResume() {
		super.onResume();
		autoRefresh(mPtrClassicFrameLayoutMonthlyBookList);
	}


	protected void finishRefreshOrLoadMore(List<MonthlyMarketBookListItem> list){
		if(isNull(list)){
			if(loadCountHolder.isRefresh()){
				refreshFailed(mPtrClassicFrameLayoutMonthlyBookList);
			}else{
				loadMoreFailed(mPtrClassicFrameLayoutMonthlyBookList);
			}
		}else{
			boolean isEnd = list.size() < loadCountHolder.getPageSize();
			if(loadCountHolder.isRefresh()){
				refreshSuccessAndSetLoadMoreStatus(mPtrClassicFrameLayoutMonthlyBookList,!isEnd);
			}else{
				loadMoreSuccess(mPtrClassicFrameLayoutMonthlyBookList,isEnd);
			}
		}
	}
}
