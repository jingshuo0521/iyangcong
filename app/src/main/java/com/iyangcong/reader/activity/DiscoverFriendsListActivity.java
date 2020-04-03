package com.iyangcong.reader.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.DiscoverInviteFriendsAdapter;
import com.iyangcong.reader.bean.DiscoverCircleFriends;
import com.iyangcong.reader.bean.DiscoverCircleGroup;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.ClearEditText;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.SearchLocalFriendUtils;
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

/**
 * Created by WuZepeng on 2017-06-15.
 */

public class DiscoverFriendsListActivity extends SwipeBackActivity {


	@BindView(R.id.btnBack)
	ImageButton btnBack;
	@BindView(R.id.textHeadTitle)
	TextView textHeadTitle;
	@BindView(R.id.btnFunction)
	ImageButton btnFunction;
	@BindView(R.id.tv_goods_num)
	TextView tvGoodsNum;
	@BindView(R.id.btnFunction1)
	ImageButton btnFunction1;
	@BindView(R.id.tv_goods_num1)
	TextView tvGoodsNum1;
	@BindView(R.id.layout_header)
	LinearLayout layoutHeader;
	@BindView(R.id.ceSearch)
	ClearEditText ceSearch;
	@BindView(R.id.tv_search)
	TextView tvSearch;
	@BindView(R.id.search_bar)
	LinearLayout searchBar;
	@BindView(R.id.invite_friends_lv)
	RecyclerView inviteFriendsLv;
	@BindView(R.id.activity_invite_friends)
	LinearLayout activityInviteFriends;

	private List<DiscoverCircleFriends> friendsList;
	private DiscoverInviteFriendsAdapter friendsAdapter;
	private int mGroupId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_friends);
		ButterKnife.bind(this);
		setMainHeadView();
		initView();
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		mGroupId = getIntent().getIntExtra(Constants.groupId,-1);
	}

	@Override
	protected void initView() {
		friendsList = new ArrayList<>();
		friendsAdapter = new DiscoverInviteFriendsAdapter(context,friendsList,true);
//		friendsAdapter.setClickListener(new DiscoverInviteFriendsAdapter.ClickListener() {
//			@Override
//			public void clicked(int position) {
//				for(DiscoverCircleFriends friend:friendsList){
//					if(friend.isChecked()){
//						friend.setChecked(false);
//					}
//				}
//				friendsList.get(position).setChecked(true);
//				friendsAdapter.notifyDataSetChanged();
//			}
//		});
		inviteFriendsLv.setAdapter(friendsAdapter);
		inviteFriendsLv.setLayoutManager(new LinearLayoutManager(context));
		initSearch();
	}

	private void initSearch(){
		ceSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				if(editable.toString().equals("")){
					clearSearchHandler();
				}else{
					SearchLocalFriendUtils.getSearchedList(editable.toString(),friendsList);
					friendsAdapter.notifyDataSetChanged();
				}
			}
		});
		ceSearch.setClearListener(new ClearEditText.ClearListener() {
			@Override
			public void clear() {
				for(DiscoverCircleFriends friend:friendsList){
					friend.setVisibile(true);
				}
				friendsAdapter.notifyDataSetChanged();
			}
		});
		ceSearch.setClearListener(new ClearEditText.ClearListener() {
			@Override
			public void clear() {
				ceSearch.setText("");
				clearSearchHandler();
			}
		});
	}

	private void clearSearchHandler(){
		for(DiscoverCircleFriends friend:friendsList){
			if(!friend.isVisibile()){
				friend.setVisibile(true);
			}
		}
		friendsAdapter.notifyDataSetChanged();
	}

	@Override
	protected void setMainHeadView() {
		btnBack.setImageResource(R.drawable.btn_back);
		btnBack.setVisibility(View.VISIBLE);
		textHeadTitle.setText("转让圈子给好友");
		btnFunction.setImageResource(R.drawable.finish_modify);
		btnFunction.setVisibility(View.VISIBLE);
		ceSearch.setHint("请输入好友昵称");
	}

	@OnClick({R.id.btnBack, R.id.btnFunction, R.id.tv_search})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnBack:
				finish();
				break;
			case R.id.btnFunction:
				tranforCircle(getCheckedUserId(),mGroupId);
				break;
			case R.id.tv_search:
				break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		getFriendsList();
	}

	/**
	 * 获取好友列表
	 */
	private void getFriendsList(){
		OkGo.get(Urls.DiscoverCircleGetPersonAndAllFriends)
				.params("userId", CommonUtil.getUserId())
				.execute(new JsonCallback<IycResponse<List<DiscoverCircleGroup>>>(this) {
					@Override
					public void onSuccess(IycResponse<List<DiscoverCircleGroup>> listIycResponse, Call call, Response response) {
						friendsList.clear();
						for(DiscoverCircleGroup group:listIycResponse.getData())
							for(DiscoverCircleFriends friend:group.getFriendsList()) {
								if(!friendsList.contains(friend)){
									friend.setVisibile(true);
									friendsList.add(friend);
								}
							}
						friendsAdapter.notifyDataSetChanged();
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
					}

				});
	}

	private int getCheckedUserId(){
		for(DiscoverCircleFriends friends:friendsList){
			if(friends.isChecked()){
				return friends.getUserId();
			}
		}
		return -1;
	}

	/**
	 * 转让圈子
	 * @param toUserId
	 * @param groupId
	 */
	private void tranforCircle(int toUserId, int groupId){
		if(toUserId == -1){
			Logger.e("wzp toUserId=" + toUserId);
			return;
		}
		if(groupId <=0 ){
			Logger.e("wzp groupId=" + groupId);
			return;
		}
		OkGo.get(Urls.TransforCircle)
				.params("userId",CommonUtil.getUserId())
				.params("toUserId",toUserId)
				.params("groupId",groupId)
				.execute(new JsonCallback<IycResponse<String>>(context) {
					@Override
					public void onSuccess(IycResponse<String> IycString, Call call, Response response) {
						Logger.i("wzp msg:" + IycString.getMsg());
						if(IycString.statusCode == 0){
							finish();
						}
						ToastCompat.makeText(context,IycString.getMsg(), Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
						Logger.e("wzp " + e.getMessage());
						ToastCompat.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
					}
				});
	}
}
