package com.iyangcong.reader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.ReplyPrivateMessageActivity;
import com.iyangcong.reader.base.BaseFragment;
import com.iyangcong.reader.bean.PrivateMessageBean;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.interfaceset.OnDeleteListener;
import com.iyangcong.reader.interfaceset.OnItemClickedListener;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.LoadCountHolder;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

/**
 * Created by sheng on 2017/3/14.
 */

public class PrivateFragment extends BaseFragment {

	@BindView(R.id.rv_new_message)
	RecyclerView rvNewMessage;
	@BindView(R.id.message_ptrClassicFrameLayout)
	PtrClassicFrameLayout messagePtrClassicFrameLayout;

	private PrivateMessageAdapter privateMessageAdapter;
	private RecyclerAdapterWithHF adapterWithHF;
	private List<PrivateMessageBean> messageList;
	private LoadCountHolder loadCountHolder;
	private Handler handler;

	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_private_message2, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	protected void initData() {
		Logger.e("wzp PrivateFragment initData");
		handler = new Handler();
		loadCountHolder = new LoadCountHolder(){
			@Override
			public void refresh() {
				setRefresh(true);
				setPage(1);
			}
		};
		messageList = new ArrayList<>();
		privateMessageAdapter = new PrivateMessageAdapter(messageList, mContext);
		privateMessageAdapter.setOnItemClickedListener(new OnItemClickedListener() {
			@Override
			public void onItemClickedListener(int position) {
				if(messageList.get(position).getFromUserId() != -1){
					goToReplay(messageList.get(position));
				}
				readMessage(messageList.get(position).getMessageId(),position);
			}
		});
		privateMessageAdapter.setOnItemTouchListener(new OnItemClickedListener() {
			@Override
			public void onItemClickedListener(int position) {
				readMessage(messageList.get(position).getMessageId(),position);
			}
		});
		privateMessageAdapter.setOnDeleteListener(new OnDeleteListener() {
			@Override
			public void onDelete(int id, int position) {
				deletePrivateMessage(id,position);
			}
		});
		adapterWithHF = new RecyclerAdapterWithHF(privateMessageAdapter);
		rvNewMessage.setAdapter(adapterWithHF);
		rvNewMessage.setLayoutManager(new LinearLayoutManager(mContext));
		messagePtrClassicFrameLayout.setHorizontalScrollBarEnabled(false);
		messagePtrClassicFrameLayout.post(new Runnable() {

			@Override
			public void run() {
				messagePtrClassicFrameLayout.autoRefresh(true);
			}
		});
		messagePtrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						refreshHandler();
					}
				});
			}
		});

		messagePtrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

			@Override
			public void loadMore() {
				handler.post(new Runnable() {

					@Override
					public void run() {
						loadMoreHandler();
					}
				});
			}
		});
	}

	private void refreshHandler(){
		loadCountHolder.refresh();
		getPrivateMessageList();
	}

	private void loadMoreHandler() {
		loadCountHolder.loadMore();
		getPrivateMessageList();
	}

	@Override
	public void onResume() {
		super.onResume();
//		refreshAuto(messagePtrClassicFrameLayout);
	}
	private void goToReplay(PrivateMessageBean bean){
		if(bean.getMessageId() > 0){
			goToReply(bean.getFromUserId());
		}else{
			Logger.e("wzp messageId =" + bean.getMessageId());
		}
	}

	private void goToReply(long toUserId){
		Intent intent = new Intent(mContext,ReplyPrivateMessageActivity.class);
		intent.putExtra(Constants.TO_USER_ID,toUserId);
		mContext.startActivity(intent);
	}

	private void getPrivateMessageList(){
		if(!CommonUtil.getLoginState()){
			Logger.e("wzp 还没登录");
			return;
		}
		OkGo.get(Urls.PersonReadMessageList)
				.params("userId", CommonUtil.getUserId())
				.params("currentPageNum",loadCountHolder.getPage())
				.params("pageSize",loadCountHolder.getPageSize())
				.execute(new JsonCallback<IycResponse<List<PrivateMessageBean>>>(mContext) {
					@Override
					public void onSuccess(IycResponse<List<PrivateMessageBean>> listIycResponse, Call call, Response response) {
						if(isNull(listIycResponse)|| isNull(listIycResponse.getData()))
							return;
						if(loadCountHolder.isRefresh()){
							messageList.clear();
						}
						messageList.addAll(listIycResponse.getData());
						adapterWithHF.notifyDataSetChanged();
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
					}

					@Override
					public void onAfter(IycResponse<List<PrivateMessageBean>> listIycResponse, Exception e) {
						super.onAfter(listIycResponse, e);
						if(isNull(listIycResponse)||isNull(listIycResponse.getData())){
							if(loadCountHolder.isRefresh()){
								refreshFailed(messagePtrClassicFrameLayout);
							}else{
								loadCountHolder.loadMoreFailed();
								loadMoreFailed(messagePtrClassicFrameLayout);
							}
						}else{
							boolean isEnd = listIycResponse.getData().size() < loadCountHolder.getPageSize();
							if(loadCountHolder.isRefresh()){
								refreshSuccess(messagePtrClassicFrameLayout,!isEnd);
							}else{
								loadMoreSuccess(messagePtrClassicFrameLayout,isEnd);
							}
						}
					}
				});
	}

	private void deletePrivateMessage(int messageId,final int position){
		if(!CommonUtil.getLoginState()){
			Logger.e("wzp 还没有登录，不能删除");
			return ;
		}
		OkGo.get(Urls.MessageDeleteURL)
				.params("messageId",messageId)
				.execute(new StringCallback() {
					@Override
					public void onSuccess(String s, Call call, Response response) {
						Logger.i("删除成功");
						if(messageList.size()>position) {
							messageList.remove(position);
							adapterWithHF.notifyDataSetChanged();
						}
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
						Logger.e("wzp 删除私信失败:" + e.getMessage());
					}
				});
	}

	private void readMessage(final int messageId, final int position){
		if(!CommonUtil.getLoginState()){
			Logger.e("wzp 还没有登录，不能设置为已读");
			return;
		}
		if(messageList.get(position).isHasRead() == 2){
			Logger.e("wzp 已经已读咯");
			return;
		}
		OkGo.get(Urls.ReadTheMessageURL)
				.params("messageId",messageId)
				.execute(new StringCallback() {
					@Override
					public void onSuccess(String s, Call call, Response response) {
						Logger.i( messageId + " 已读成功");
						if(messageList.size()>position) {
							if (messageList.get(position).isHasRead() == 1) {
								messageList.get(position).setHasRead(2);
								adapterWithHF.notifyItemChanged(position);
							}
						}
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
						Logger.e("wzp " + messageId + " 已读失败");
					}
				});
	}

}
