package com.iyangcong.reader.activity;

import android.content.Intent;
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
import com.iyangcong.reader.adapter.DiscoverCircleCategoryAdapter;
import com.iyangcong.reader.adapter.DiscoverCircleListAdapter;
import com.iyangcong.reader.bean.DiscoverCircleItemContent;
import com.iyangcong.reader.bean.DiscoverCrircleCategory;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.enumset.StateEnum;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.LoginUtils;
import com.iyangcong.reader.utils.NetUtil;
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

/**
 * Created by lg on 2016/12/27.
 * Modified by wzp
 * 圈子列表界面
 */
public class DiscoverCircleActivity extends SwipeBackActivity{

	private static DiscoverCrircleCategory category = null;


	//    @BindView(R.id.book_discover_circle_ptrClassicFrameLayout)
//    PtrClassicFrameLayout bookDiscoverCirclePtrClassicFrameLayout;
	private DiscoverCircleCategoryAdapter categoryAdapter;
	private List<DiscoverCircleItemContent> discoverCircleContentList = new ArrayList<>();
	private RecyclerAdapterWithHF mCircleAdapter;
	private Holder holder = new Holder();
	private DiscoverCircleListAdapter circleListAdapter;
	//临时
	private Handler handler = new Handler();
	List<DiscoverCrircleCategory> discoverCrircleCategoryList = new ArrayList<>();

	@BindView(R.id.layout_header)
	LinearLayout layoutHeader;

	@BindView(R.id.circle_category_recyclerview)
	RecyclerView categoryRecylerView;
	@BindView(R.id.btnBack)
	ImageButton btnBack;
	@BindView(R.id.textHeadTitle)
	TextView textHeadTitle;
	@BindView(R.id.btnFunction)
	ImageButton btnFunction;
	@BindView(R.id.book_discover_circle_ptrClassicFrameLayout)
	PtrClassicFrameLayout ptrClassicFrameLayout;
	@BindView(R.id.rv_book_discover_circle)
	RecyclerView rvBookDiscoverCircle;
	@BindView(R.id.layout_activity_discover_circle)
	LinearLayout layoutActivityDiscoverCircle;
//	@BindView(R.id.layout_network_error)
//	LinearLayout layoutNetworkError;
//	@BindView(R.id.btn_network_error)
//	Button btnNetworkError;
	private boolean mIsLogin;


	@OnClick({R.id.btnBack, R.id.btnFunction})
	void onBtnClick(View view) {
		switch (view.getId()) {
			case R.id.btnBack:
				finish();
				break;
			case R.id.btnFunction:
				LoginUtils loginUtils = new LoginUtils(){
					@Override
					public void setIntentData(Intent intent) {
						intent.putExtra(Constants.CREATE_CIRLE_OR_MODIFY, StateEnum.CREATE);
					}
				};
				loginUtils.startIntent(this, DiscoverNewCircle.class);
				break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		addContentView(R.layout.activity_discover_circle,this);
		setContentView(R.layout.activity_discover_circle);
		ButterKnife.bind(this);
		setMainHeadView();
		initView();
	}


	@Override
	protected void setMainHeadView() {
		btnFunction.setImageResource(R.drawable.ic_add);
		btnFunction.setVisibility(View.VISIBLE);
		textHeadTitle.setText(R.string.discover_circle);
		btnBack.setImageResource(R.drawable.btn_back);
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		mIsLogin = CommonUtil.getLoginState();
	}


	@Override
	protected void onStop() {
		super.onStop();
		Logger.i("onStop");
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	public void autoRefresh() {
		if(ptrClassicFrameLayout.isRefreshing()){
			ptrClassicFrameLayout.refreshComplete();
			OkGo.getInstance().cancelTag(ptrClassicFrameLayout);
		}
		ptrClassicFrameLayout.post(new Runnable() {

			@Override
			public void run() {
				ptrClassicFrameLayout.autoRefresh(true);
			}
		});
	}

	@Override
	protected void initView() {
		initVaryViewHelper(context, layoutActivityDiscoverCircle, new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(getNetworkState(context) == NetUtil.NETWORK_WIFI || getNetworkState(context) == NetUtil.NETWORK_MOBILE){
					showDataView();
					getDataFromNetwork();
				}else{
					showErrorView();
				}
			}
		});
		categoryAdapter = new DiscoverCircleCategoryAdapter(context, discoverCrircleCategoryList, new DiscoverCircleCategoryAdapter.OnItemSelectedListener() {
			@Override
			public void onItemSelected(View view, int position) {
				holder.setLastPosition(holder.getCategrogyPosition());
				holder.setCategrogyPosition(position);
				for (DiscoverCrircleCategory category : discoverCrircleCategoryList) {
					category.setClicked(false);
				}
				category = discoverCrircleCategoryList.get(position);
				category.setClicked(true);
				categoryAdapter.notifyDataSetChanged();
				autoRefresh();
			}
		});
		categoryRecylerView.hasFixedSize();
		LinearLayoutManager manager = new LinearLayoutManager(this);
		manager.setOrientation(LinearLayoutManager.VERTICAL);
		categoryRecylerView.setLayoutManager(new LinearLayoutManager(this));
		categoryRecylerView.setAdapter(categoryAdapter);

		circleListAdapter = new DiscoverCircleListAdapter(this, discoverCircleContentList);
		mCircleAdapter = new RecyclerAdapterWithHF(circleListAdapter);
		LinearLayoutManager mgr = new LinearLayoutManager(this);
		rvBookDiscoverCircle.setLayoutManager(mgr);
		rvBookDiscoverCircle.setAdapter(mCircleAdapter);

		ptrClassicFrameLayout.setHorizontalScrollBarEnabled(false);
		ptrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {
			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						refreshCurrentGrid();
					}
				});
			}
		});

		ptrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

			@Override
			public void loadMore() {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						loadMoreCurrentGrid();
					}
				}, 0);
			}
		});
	}


	private void refreshCurrentGrid() {
		if (category != null) {
			holder.refresh();
			updateCircleItemContent(category.getCategoryid(), 1, 30);
		}
	}



	private void loadMoreCurrentGrid() {
		if (category != null) {
			if (!category.getCategoryname().equals("推荐")) {
				holder.load();
				updateCircleItemContent(category.getCategoryid(), holder.getLoadMoreTimes() + 1, 30);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Logger.i("onResume");
		getDataFromNetwork();
	}


	private void getDataFromNetwork() {
		updateCircleCategory();
	}


	private void updateCircleCategory() {
		OkGo.get(Urls.CircleCategoryURL)
				.execute(new JsonCallback<IycResponse<List<DiscoverCrircleCategory>>>(this) {
					@Override
					public void onSuccess(IycResponse<List<DiscoverCrircleCategory>> listIycResponse, Call call, Response response) {

						discoverCrircleCategoryList.clear();
						for (DiscoverCrircleCategory category : listIycResponse.getData()) {
							discoverCrircleCategoryList.add(category);
						}
						category = discoverCrircleCategoryList.get(holder.getCategrogyPosition());
						category.setClicked(true);
						categoryAdapter.notifyDataSetChanged();
						autoRefresh();
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call,response,e);
						showErrorView();
					}
				});
	}

	private void updateCircleItemContent(final int categoryId, int pageNo, final int pageSize) {
		showLoadingDialog();
		OkGo.get(Urls.DiscoverCircleItemContentURL)
				.tag(ptrClassicFrameLayout)
				.params("category", "" + categoryId)
				.params("pageNo", "" + pageNo)
				.params("pageSize", "" + pageSize)
				.execute(new JsonCallback<IycResponse<List<DiscoverCircleItemContent>>>(this) {
					@Override
					public void onSuccess(IycResponse<List<DiscoverCircleItemContent>> listIycResponse, Call call, Response response) {
						getClearOrNotList(holder.isRefresh());
						for (DiscoverCircleItemContent item : listIycResponse.getData()) {
							discoverCircleContentList.add(item);
						}
						if(holder.isRefresh()){
							refreshSuccess(ptrClassicFrameLayout);
							setLoadMoreGone();
						}else{
							boolean isEnd = listIycResponse.getData().size() < pageSize;
							loadMoreSuccess(ptrClassicFrameLayout,isEnd);
						}

						circleListAdapter.notifyDataSetChanged();
						dismissLoadingDialig();
					}

					private void setLoadMoreGone() {
						if(category != null && category.getCategoryname()!=null && category.getCategoryname().equals("推荐")){
							ptrClassicFrameLayout.setLoadMoreEnable(false);
						}
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
//						BroadCastSender.sendBroadCast(context);
						super.onError(call,response,e);
						if(holder.isRefresh()){
							refreshFailed(ptrClassicFrameLayout);
						}else{
							loadMoreFailed(ptrClassicFrameLayout);
							holder.loadFailed();
						}
						dismissLoadingDialig();
					}

				});
	}

	/**
	 * 是否需要清空contentList,如果是刷新的话需要清空，如果是加载更多的话不需要清空
	 */
	private List<DiscoverCircleItemContent> getClearOrNotList(boolean needClear) {
		if (needClear)
			discoverCircleContentList.clear();
		return discoverCircleContentList;
	}


	//刷新次数和加载次数的记录类
	private class Holder {
		/**
		 * 加载次数
		 */
		private int LoadMoreTimes = 0;
		/**
		 * category列表选中的位置
		 */
		private int categrogyPosition = 0;
		/**
		 *
		 */
		private int lastPosition = -1;
		/***/
		private boolean needClear = true;

		/**
		 * 加载更多的计数器，同一次刷新下的加载次数
		 */
		public void load() {
			LoadMoreTimes++;
			needClear = false;
		}

		/**
		 * 确保本类中的LoadMoreTimes是一次刷新下的加载次数
		 */
		public void refresh() {
			LoadMoreTimes = 0;
			needClear = true;
		}

		/**
		 * 获取是否需要清空content列表的标志位
		 */
		public boolean isRefresh() {
			return needClear;
		}

		/**
		 * 设置categoryList选中位置
		 */
		public void setCategrogyPosition(int position) {
			categrogyPosition = position;
		}

		public int getLoadMoreTimes() {
			return LoadMoreTimes;
		}

		public int getCategrogyPosition() {
			return categrogyPosition;
		}

		public boolean isPosChanged(){
			return lastPosition != categrogyPosition;
		}

		public int getLastPosition() {
			return lastPosition;
		}

		public void setLastPosition(int lastPosition) {
			this.lastPosition = lastPosition;
		}

		public void loadFailed(){
			LoadMoreTimes--;
		}
	}
}
