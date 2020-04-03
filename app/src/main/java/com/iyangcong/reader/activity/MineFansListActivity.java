package com.iyangcong.reader.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.MineFansListAdatper;
import com.iyangcong.reader.bean.FansBean;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.interfaceset.RetryCounter;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.CustomPtrClassicFrameLayout;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.LoadCountHolder;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

public class MineFansListActivity extends SwipeBackActivity implements View.OnClickListener{


	@BindView(R.id.btnBack)
	ImageButton mBtnBack;
	@BindView(R.id.textHeadTitle)
	TextView mTextHeadTitle;
	@BindView(R.id.btnFunction)
	ImageButton mBtnFunction;
	@BindView(R.id.tv_goods_num)
	TextView mTvGoodsNum;
	@BindView(R.id.btnFunction1)
	ImageButton mBtnFunction1;
	@BindView(R.id.tv_goods_num1)
	TextView mTvGoodsNum1;
	@BindView(R.id.layout_header)
	LinearLayout mLayoutHeader;
	@BindView(R.id.rv_fanslist)
	RecyclerView mRvFanslist;
	@BindView(R.id.fans_ptrClassicFrameLayout)
	CustomPtrClassicFrameLayout mFansPtrClassicFrameLayout;

	private LoadCountHolder mLoadCountHolder = new LoadCountHolder();
	private List<FansBean> mFansBeanList = null;
	private RecyclerAdapterWithHF mAdapterWithHF;
	private int retry = 0;
	private Handler mHandler = new Handler();
	private final Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			getFanlist();
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fans_list);
		ButterKnife.bind(this);
		setMainHeadView();
		initView();
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		mFansBeanList = new ArrayList<>();
		mLoadCountHolder.setRefresh(true);
		mLoadCountHolder.setPage(1);
		mLoadCountHolder.setPageSize(10);
		mHandler = new Handler();
	}

	@Override
	protected void initView() {
		MineFansListAdatper tmpAdatper = new MineFansListAdatper(this, mFansBeanList);
		mAdapterWithHF = new RecyclerAdapterWithHF(tmpAdatper);
		mRvFanslist.setAdapter(mAdapterWithHF);
		mRvFanslist.setLayoutManager(new LinearLayoutManager(this));
		mRvFanslist.setHasFixedSize(true);
		mFansPtrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {
			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				mLoadCountHolder.refresh();
				mLoadCountHolder.setPage(1);
				mHandler.postAtTime(mRunnable,1000);
			}
		});
		mFansPtrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void loadMore() {
				mLoadCountHolder.loadMore();
				mHandler.postAtTime(mRunnable,1000);
			}
		});
		autoRefresh(mFansPtrClassicFrameLayout);
	}

	@Override
	protected void setMainHeadView() {
		mTextHeadTitle.setText("我的粉丝");
		mBtnBack.setImageResource(R.drawable.btn_back);
		mBtnBack.setOnClickListener(this);
		mBtnBack.setVisibility(View.VISIBLE);
		mBtnFunction.setVisibility(View.GONE);
		mBtnFunction1.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnBack:
				finish();
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mHandler!=null)
			mHandler.removeCallbacks(mRunnable);
	}

	private void getFanlist() {
		OkGo.get(Urls.URL + "/personCenter/getAttentionUserPersonInfoList")
				.params("userId", CommonUtil.getUserId())
				.params("currentPageNum", mLoadCountHolder.getPage())
				.params("pageSize", mLoadCountHolder.getPageSize())
				.execute(new JsonCallback<IycResponse<List<FansBean>>>(this) {
					@Override
					public void onSuccess(IycResponse<List<FansBean>> iycFansBeans, Call call, Response response) {
						if (iycFansBeans == null || iycFansBeans.getData()==null){
							Logger.e("网络请求到的数据为空");
							return;
						}
						if(mLoadCountHolder.isRefresh()){
							if(iycFansBeans.getData().isEmpty()){
								//没有好友
							}
							mFansBeanList.clear();
						}
						mFansBeanList.addAll(iycFansBeans.getData());
						mAdapterWithHF.notifyDataSetChangedHF();
						boolean canLoadMore = iycFansBeans.getData().size()>=mLoadCountHolder.getPageSize();
						if(mLoadCountHolder.isRefresh()){
							mLoadCountHolder.setCanLoadMore(canLoadMore);
							mFansPtrClassicFrameLayout.setLoadMoreEnable(canLoadMore);
							refreshSuccess(mFansPtrClassicFrameLayout);
						}else{
							loadMoreSuccess(mFansPtrClassicFrameLayout,!canLoadMore);
						}
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
						if(mLoadCountHolder.isRefresh()){
							refreshFailed(mFansPtrClassicFrameLayout);
						}else{
							mLoadCountHolder.loadMoreFailed();
							loadMoreFailed(mFansPtrClassicFrameLayout);
						}
					}

					@Override
					public void parseError(Call call, Exception e) {
						super.parseError(call, e);
						if(retry++< RetryCounter.MAX_RETRY_TIMES){
							getFanlist();
						}else{
							dismissLoadingDialig();
						}
					}

					@Override
					public void onAfter(IycResponse<List<FansBean>> listIycResponse, Exception e) {
						super.onAfter(listIycResponse, e);
						dismissLoadingDialig();
					}
				});
	}
}
