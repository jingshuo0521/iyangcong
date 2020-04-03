package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.DiscoverCircleAdapter;
import com.iyangcong.reader.bean.DiscoverCircleDescribe;
import com.iyangcong.reader.bean.DiscoverCircleDetail;
import com.iyangcong.reader.bean.DiscoverCircleMember;
import com.iyangcong.reader.bean.DiscoverCreateCircle;
import com.iyangcong.reader.bean.DiscoverTopic;
import com.iyangcong.reader.bean.ShelfBook;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.enumset.StateEnum;
import com.iyangcong.reader.interfaceset.OnAddGroupClickedListener;
import com.iyangcong.reader.interfaceset.OnItemClickedListener;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.CircleSettingDialog;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.AppManager;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.LoadCountHolder;
import com.iyangcong.reader.utils.LoginUtils;
import com.iyangcong.reader.utils.NotNullUtils;
import com.iyangcong.reader.utils.StringUtils;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.ui.CircleSettingDialog.State_AddCircle;
import static com.iyangcong.reader.ui.CircleSettingDialog.State_AddCircle_Creater_NotPassed_Or_Passing;
import static com.iyangcong.reader.ui.CircleSettingDialog.State_AddCircle_NotCreater;
import static com.iyangcong.reader.ui.CircleSettingDialog.State_AddCricle_Creater_PASSED;
import static com.iyangcong.reader.utils.Constants.CREATE_CIRLCE;
import static com.iyangcong.reader.utils.Constants.CREATE_CIRLE_OR_MODIFY;
import static com.iyangcong.reader.utils.Constants.GROUP_TYPE;
import static com.iyangcong.reader.utils.Constants.groupId;
import static com.iyangcong.reader.utils.NotNullUtils.isNull;


/**
 * 展示圈子详情的activity,需要传circleId，circleName还有category
 * by wzp
 */
public class DiscoverCircleDetailActivity extends SwipeBackActivity {


	@BindView(R.id.btnBack)
	ImageButton btnBack;
	@BindView(R.id.textHeadTitle)
	TextView textHeadTitle;
	@BindView(R.id.btnFunction)
	ImageButton btnFunction;
	@BindView(R.id.rv_discover_circle)
	RecyclerView rvDiscoverCircle;
	@BindView(R.id.discover_circle_ptrClassicFrameLayout)
	PtrClassicFrameLayout discoverCirclePtrClassicFrameLayout;
	Handler handler = new Handler();
	private int mCircleId, category;
	private long mUserId;
	private String circleName;
	private int mAuthority = -1;
	private CircleSettingDialog mDialog;
	private DiscoverCircleAdapter discoverCircleAdapter;
	private RecyclerAdapterWithHF mAdapter;
	private DiscoverCircleDetail discoverCircleDetail;
	private LoadCountHolder loadCountHolder = new LoadCountHolder(){
		@Override
		public void refresh() {
			setRefresh(true);
			setPage(1);
		}
	};
	private LoadCountHolder.ClickableRecoder mClickableRecoder = loadCountHolder.new ClickableRecoder(false);
	private int mCircleType = -1;
	private int pageSize = 10;
	private String mPath;//图片路径的相对地址

	@OnClick({R.id.btnBack, R.id.btnFunction})
	void onBtnClick(View view) {
		switch (view.getId()) {
			case R.id.btnBack:
				finish();
				break;
			case R.id.btnFunction:
				if(mClickableRecoder.isClickable()){
					mDialog.show();
				}
				break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discover_circle_detail);
		ButterKnife.bind(this);
		setMainHeadView();
		initView();
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		Intent intent = getIntent();
		mUserId = CommonUtil.getUserId();
		mCircleId = intent.getIntExtra(Constants.circleId, 0);
		circleName = intent.getStringExtra(Constants.circleName);
		category = intent.getIntExtra(Constants.CIRCLE_CATEGORY, 0);
		Logger.i("category:" + category);

		discoverCircleDetail = new DiscoverCircleDetail();
		discoverCircleDetail.setDiscoverCircleDescribe(null);
		discoverCircleDetail.setCircleMemberList(null);
		discoverCircleDetail.setDiscoverTopicList(null);
	}

	@Override
	protected void initView() {
		AppManager.getAppManager().finishActivity(DiscoverCircleActivity.class, DiscoverNewCircleChooseBook.class);
		mDialog = new CircleSettingDialog(this,R.style.DialogTheme);
		mDialog.setOnAddGroupClickedListener(new OnAddGroupClickedListener<String>() {
			@Override
			public void onAddGroupClicked(String s) {
				LoginUtils loginUtils = new LoginUtils();
				if (loginUtils.isLogin(context)||hasAuthority()) {
					joinInCircle(mCircleId, s, mUserId);
				}
			}
		});
		mDialog.setNotCreateronItemClickedListener(new OnItemClickedListener() {
			@Override
			public void onItemClickedListener(int position) {
				if (position == 0) {
					goToRichtextActivity();
				} else {
					quitCircle(mCircleId, mUserId);
				}
			}
		});
		mDialog.setCreaterOnItemClickedListener(new OnItemClickedListener() {
			@Override
			public void onItemClickedListener(int position) {
				switch (position){
					case 0://发表话题
						if(hasAuthority()){
							goToRichtextActivity();
						}
						break;
					case 2://转让圈子
						if(hasAuthority()){
							goToTransforCircle();
						}
						break;
					case 1://修改圈子
						goToModifyCircle();
						break;
				}
			}
		});
		mDialog.setCreaterNotPassingListener(new OnItemClickedListener() {
			@Override
			public void onItemClickedListener(int position) {
				switch (position){
					case 0:
						goToModifyCircle();
						break;
				}
			}
		});
		discoverCircleAdapter = new DiscoverCircleAdapter(this, discoverCircleDetail);
		mAdapter = new RecyclerAdapterWithHF(discoverCircleAdapter);
		rvDiscoverCircle.setAdapter(discoverCircleAdapter);
		//recycleView不仅要设置适配器还要设置布局管理者,否则图片不显示
		//第一个参数是上下文，第二个参数是只有一列
		rvDiscoverCircle.setLayoutManager(new LinearLayoutManager(this));
		rvDiscoverCircle.setAdapter(mAdapter);
		discoverCirclePtrClassicFrameLayout.setHorizontalScrollBarEnabled(false);
		discoverCirclePtrClassicFrameLayout.post(new Runnable() {

			@Override
			public void run() {
				discoverCirclePtrClassicFrameLayout.autoRefresh(true);
			}
		});


		discoverCirclePtrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						onRefresh();
					}
				}, 0);
			}
		});

		discoverCirclePtrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

			@Override
			public void loadMore() {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						onLoadMore();
					}
				}, 0);
			}
		});
	}

	/**
	 * 检查圈子是否已经通过审核
	 */
	private boolean hasAuthority(){
		if (!isNull(discoverCircleDetail) && !isNull(discoverCircleDetail.getDiscoverCircleDescribe())) {
			if(discoverCircleDetail.getDiscoverCircleDescribe().getCheckStatus() == 2)
				return true;
			}
		return false;
	}

	@Override
	protected void setMainHeadView() {
		textHeadTitle.setText(StringUtils.delHTMLTag(circleName));
		btnBack.setImageResource(R.drawable.btn_back);
		btnFunction.setImageResource(R.drawable.menu);
		btnFunction.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * 跳转到转让圈子
	 */
	private void goToTransforCircle() {
		Intent intent = new Intent(context, DiscoverFriendsListActivity.class);
		intent.putExtra(Constants.groupId, mCircleId);
		startActivity(intent);
	}

	/**
	 * 跳转到修改圈子
	 */
	private void goToModifyCircle() {
		DiscoverCreateCircle createCircle = new DiscoverCreateCircle();
		DiscoverCircleDescribe describe = discoverCircleDetail.getDiscoverCircleDescribe();
		if (describe != null) {
			createCircle.setGroupname(describe.getCircleName());
			createCircle.setCategory(describe.getCategoryId());
			createCircle.setAuthority(mAuthority);
			createCircle.setCover(describe.getCircleImage());
			createCircle.setGroupdesc(describe.getCircleDescribe());
			createCircle.setTag(describe.getCircleLabel());
			createCircle.setPath(mPath);
		}
		Intent intent = new Intent(DiscoverCircleDetailActivity.this, DiscoverNewCircle.class);
		Bundle bundle = new Bundle();
		bundle.putParcelable(CREATE_CIRLCE, createCircle);
		intent.putExtra(CREATE_CIRLE_OR_MODIFY, StateEnum.MODIFY);
		intent.putExtra(groupId, mCircleId);
		intent.putExtra(GROUP_TYPE, mCircleType);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * 跳转到发表话题
	 */
	private void goToRichtextActivity() {
		Intent intent = new Intent(DiscoverCircleDetailActivity.this, RichTextActivity.class);
		intent.putExtra(Constants.circleId, mCircleId);
		startActivityForResult(intent, 1);
	}

	private void onLoadMore() {
		loadCountHolder.loadMore();
		getTopicTopicList(mCircleId,pageSize);
	}

	private void onRefresh() {
		loadCountHolder.refresh();
		getDatasFromNetwork();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		getDatasFromNetwork();
	}

	private void getDatasFromNetwork() {
		mClickableRecoder.setClickable(false);
		OkGo.get(Urls.DiscoverCircleDetialURL)
				.params("groupid", mCircleId + "")
				.params("userid", mUserId)
				.execute(new JsonCallback<IycResponse<DiscoverCircleDescribe>>(this) {

					@Override
					public void onSuccess(IycResponse<DiscoverCircleDescribe> discoverCircleDescribe, Call call, Response response) {
						if(isNull(discoverCircleDescribe)||isNull(discoverCircleDescribe.getData())){
							Logger.e("wzp discoverCircleDescribe" + discoverCircleDescribe.toString());
							return;
						}
						discoverCircleDescribe.getData().setUserImgUrl("");
						circleName = discoverCircleDescribe.getData().getCircleName();
						textHeadTitle.setText(StringUtils.delHTMLTag(circleName));
						discoverCircleDetail.setDiscoverCircleDescribe(discoverCircleDescribe.getData());
						updateDialog(discoverCircleDescribe.getData());
						mCircleType = discoverCircleDescribe.getData().getGroupType();
						mAuthority = discoverCircleDescribe.getData().getAuthority();
						mPath = discoverCircleDescribe.getData().getPath();
						updateShenHeLogo(discoverCircleDescribe.getData());
						discoverCircleAdapter.notifyDataSetChanged();
						getCircleCategoryPhoto(discoverCircleDescribe.getData().getCategoryId());
					}

					@Override
					public void onAfter(IycResponse<DiscoverCircleDescribe> discoverCircleDescribeIycResponse, Exception e) {
						super.onAfter(discoverCircleDescribeIycResponse, e);
						mClickableRecoder.setClickable(true);
					}
				});

		OkGo.get(Urls.DiscoverCircleMemberListURL)
				.params("circleId", mCircleId + "")
				.execute(new JsonCallback<IycResponse<List<DiscoverCircleMember>>>(this) {
					@Override
					public void onSuccess(IycResponse<List<DiscoverCircleMember>> commonSectionIycResponse, Call call, Response response) {
						Logger.i("DiscoverCircle" + commonSectionIycResponse.getData());
						discoverCircleDetail.setCircleMemberList(commonSectionIycResponse.getData());
						discoverCircleAdapter.notifyDataSetChanged();
					}
				});

		OkGo.get(Urls.DiscoverCircleGroupBooks)
				.params("circleId", mCircleId + "")
				.execute(new JsonCallback<IycResponse<List<ShelfBook>>>(this) {
					@Override
					public void onSuccess(IycResponse<List<ShelfBook>> listIycResponse, Call call, Response response) {
						discoverCircleDetail.setShelfBookList(listIycResponse.getData());
						discoverCircleAdapter.notifyDataSetChanged();
					}

				});
//		hasJiontInTheCircle(mCircleId, mUserId);
		getTopicTopicList(mCircleId,pageSize);
	}
	private void getTopicTopicList(final int groupId,final int pageSize) {
		OkGo.get(Urls.DiscoverCricleTopTopic)
				.params("pageNo","1")
				.params("groupid", groupId + "")
				.params("pageSize", "5")
				.execute(new JsonCallback<IycResponse<List<DiscoverTopic>>>(context) {
					@Override
					public void onSuccess(IycResponse<List<DiscoverTopic>> listIycResponse, Call call, Response response) {
						List<DiscoverTopic> tempList = listIycResponse.getData();
						List<DiscoverTopic> toSetList =
								discoverCircleDetail.getDiscoverTopicList() == null ?
										new ArrayList<DiscoverTopic>() :
										discoverCircleDetail.getDiscoverTopicList();
						if (loadCountHolder.isRefresh()) {
							toSetList.clear();
						}
						if (tempList != null && tempList.size() > 0) {
							for (DiscoverTopic topic : tempList) {
								if (!toSetList.contains(topic)) {
									topic.setGroupId(groupId);
									toSetList.add(topic);
								}
							}
						}
						discoverCircleDetail.setDiscoverTopicList(toSetList);
						discoverCircleAdapter.notifyDataSetChanged();
						getTopicList(mCircleId, loadCountHolder.getPage(), pageSize);
					}
					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
					}
					@Override
					public void onAfter(IycResponse<List<DiscoverTopic>> listIycResponse, Exception e) {
						super.onAfter(listIycResponse, e);
						if (isNull(listIycResponse) || isNull(listIycResponse.getData())) {
							if (loadCountHolder.isRefresh()) {
								refreshFailed(discoverCirclePtrClassicFrameLayout);
							} else {
								loadMoreFailed(discoverCirclePtrClassicFrameLayout);
							}
						} else {
							boolean isEnd = listIycResponse.getData().size() < pageSize;
							if (loadCountHolder.isRefresh()) {
								refreshSuccessAndSetLoadMoreStatus(discoverCirclePtrClassicFrameLayout,!isEnd);
							} else {
								loadMoreSuccess(discoverCirclePtrClassicFrameLayout, isEnd);
							}
						}
					}
				});
	}
	private void getTopicList(final int groupId, int pageNo, final int pageSize) {
		OkGo.get(Urls.DiscoverCircleDetailTopicList)
				.params("groupid", groupId + "")
				.params("pageNo", pageNo + "")
				.params("pageSize", pageSize + "")
				.execute(new JsonCallback<IycResponse<List<DiscoverTopic>>>(context) {
					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);

					}

					@Override
					public void onSuccess(IycResponse<List<DiscoverTopic>> listIycResponse, Call call, Response response) {
						List<DiscoverTopic> tempList = listIycResponse.getData();
						List<DiscoverTopic> toSetList =
								discoverCircleDetail.getDiscoverTopicList() == null ?
										new ArrayList<DiscoverTopic>() :
										discoverCircleDetail.getDiscoverTopicList();
						if (loadCountHolder.isRefresh()) {

						}
						if (tempList != null && tempList.size() > 0) {
							for (DiscoverTopic topic : tempList) {
								if (!toSetList.contains(topic)) {
									topic.setGroupId(groupId);
									toSetList.add(topic);
								}
							}
						}
						discoverCircleDetail.setDiscoverTopicList(toSetList);
						discoverCircleAdapter.notifyDataSetChanged();

					}


					@Override
					public void onAfter(IycResponse<List<DiscoverTopic>> listIycResponse, Exception e) {
						super.onAfter(listIycResponse, e);
						if (isNull(listIycResponse) || isNull(listIycResponse.getData())) {
							if (loadCountHolder.isRefresh()) {
								refreshFailed(discoverCirclePtrClassicFrameLayout);
							} else {
								loadMoreFailed(discoverCirclePtrClassicFrameLayout);
							}
						} else {
							boolean isEnd = listIycResponse.getData().size() < pageSize;
							if (loadCountHolder.isRefresh()) {
								refreshSuccessAndSetLoadMoreStatus(discoverCirclePtrClassicFrameLayout,!isEnd);
							} else {
								loadMoreSuccess(discoverCirclePtrClassicFrameLayout, isEnd);
							}
						}
					}
				});


	}

	private void getCircleCategoryPhoto(int categoryId) {
		OkGo.get(Urls.DiscoverCircleCategoryPhoto)
				.params("categoryId", categoryId)
				.execute(new JsonCallback<IycResponse<DiscoverCircleDescribe>>(context) {
					@Override
					public void onSuccess(IycResponse<DiscoverCircleDescribe> describe, Call call, Response response) {
						String temp = describe.getData().getCircleImage();
						if (!NotNullUtils.isNull(context, describe.getData().getCircleImage())) {
							if (discoverCircleDetail.getDiscoverCircleDescribe() != null) {
								discoverCircleDetail.getDiscoverCircleDescribe().setUserImgUrl(temp);
								discoverCircleAdapter.notifyDataSetChanged();
							}

						}
					}

					@Override
					public void onError(Call call, Response response, Exception e) {

					}
				});
	}

//	/**
//	 * 是否已经加入圈子
//	 *
//	 * @param groupid
//	 * @param userId
//	 */
//	public void hasJiontInTheCircle(int groupid, final long userId) {
//		OkGo.get(Urls.DiscoverCircleWhetherJiont)
//				.params("groupid", groupid)
//				.params("userid", userId)
//				.execute(new JsonCallback<IycResponse<JionCircleState>>(context) {
//					@Override
//					public void onSuccess(IycResponse<JionCircleState> jionCircleStateIycResponse, Call call, Response response) {
//						if (isNull(jionCircleStateIycResponse) || isNull(jionCircleStateIycResponse.getData()))
//							return;
//					}
//				});
//	}

	/**
	 * 更新dialog显示内容
	 * @param state
	 */
	private void updateDialog(DiscoverCircleDescribe state) {
		if (state.isJoin()) {
			if (state.getCreaterId() == mUserId) {
				if(state.getCheckStatus() == 2){
					mDialog.setState(State_AddCricle_Creater_PASSED);
				}else{
					mDialog.setState(State_AddCircle_Creater_NotPassed_Or_Passing);
				}
			} else {
				mDialog.setState(State_AddCircle_NotCreater);
			}
		} else {
			mDialog.setState(State_AddCircle);
		}
	}

	/**
	 * 刷新审核状态
	 */
	private void updateShenHeLogo(DiscoverCircleDescribe state) {
		if (!isNull(discoverCircleDetail) && !isNull(discoverCircleDetail.getDiscoverCircleDescribe())) {
			discoverCircleDetail.getDiscoverCircleDescribe().setCheckStatus(state.getCheckStatus());
			if (state.getCreaterId() == mUserId) {
				discoverCircleAdapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * 方法作用  :  加入圈子
	 * 方法参数  ：   groupId：圈子id
	 * message：验证信息
	 * userId：用户id
	 * modified by WuZepeng  in 2017-03-28 8:40
	 */
	public void joinInCircle(int groupId, String message, final long userId) {
		showLoadingDialog();
		OkGo.get(Urls.DiscoverJionCircleURL)
				.params("groupid", groupId + "")
				.params("message", message)
				.params("userid", userId + "")
				.execute(new JsonCallback<IycResponse<String>>(context) {
					@Override
					public void onSuccess(IycResponse<String> stringIycResponse, Call call, Response response) {
						dismissLoadingDialig();
						ToastCompat.makeText(context, "成功加入圈子", Toast.LENGTH_SHORT).show();
						if(!isNull(discoverCircleDetail)&&!isNull(discoverCircleDetail.getDiscoverCircleDescribe())){
							discoverCircleDetail.getDiscoverCircleDescribe().setJoin(true);
							updateDialog(discoverCircleDetail.getDiscoverCircleDescribe());
						}
//						hasJiontInTheCircle(mCircleId, userId);
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						dismissLoadingDialig();
						mDialog.setState(CircleSettingDialog.State_AddCircle);
					}
				});
	}

	/**
	 * 退出圈子
	 *
	 * @param groupId
	 * @param userId
	 */
	private void quitCircle(int groupId, long userId) {
		showLoadingDialog();
		OkGo.get(Urls.URL + "/groups/quitgroup")
				.params("groupId", groupId)
				.params("userId", userId)
				.execute(new JsonCallback<IycResponse<String>>(context) {
					@Override
					public void onSuccess(IycResponse<String> stringIycResponse, Call call, Response response) {
						dismissLoadingDialig();
						ToastCompat.makeText(context, "退出圈子成功", Toast.LENGTH_SHORT).show();
						DiscoverCircleDescribe state = new DiscoverCircleDescribe();
						state.setJoin(false);
						updateDialog(state);
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						dismissLoadingDialig();
						super.onError(call, response, e);
						ToastCompat.makeText(context, "退出圈子失败", Toast.LENGTH_SHORT).show();
					}
				});
	}
}
