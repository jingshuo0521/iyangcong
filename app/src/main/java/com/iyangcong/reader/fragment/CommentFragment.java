package com.iyangcong.reader.fragment;

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
import com.iyangcong.reader.adapter.NewMessageCommentAdapter;
import com.iyangcong.reader.base.BaseFragment;
import com.iyangcong.reader.bean.MessageComment;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.interfaceset.OnItemClickedListener;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.utils.CommonUtil;
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
 * Created by sheng on 2017/3/13.
 */

public class CommentFragment extends BaseFragment {


	@BindView(R.id.rv_new_message)
	RecyclerView rvNewMessage;
	@BindView(R.id.message_ptrClassicFrameLayout)
	PtrClassicFrameLayout messagePtrClassicFrameLayout;

	private NewMessageCommentAdapter newMessageCommentAdapter;
	private List<MessageComment> commentList;
	private Handler handler = new Handler();
	private LoadCountHolder holderForReview = new LoadCountHolder(){
		@Override
		public void refresh() {
			setPage(1);
			setRefresh(true);
		}
	};
	private RecyclerAdapterWithHF adapterWithHF;
	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_private_message2, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	protected void initData() {
		commentList = new ArrayList<>();
		newMessageCommentAdapter = new NewMessageCommentAdapter(mContext, commentList);
		newMessageCommentAdapter.setOnItemClickedListener(new OnItemClickedListener() {
			@Override
			public void onItemClickedListener(int position) {
				readMessage(commentList.get(position).getMessageId(),position);
			}
		});
		newMessageCommentAdapter.setOnDeleteListener(new NewMessageCommentAdapter.OnDeleteListener() {
			@Override
			public void onDelete(int id, int position) {
				deleteMessage(id, position);
			}
		});
		adapterWithHF = new RecyclerAdapterWithHF(newMessageCommentAdapter);
		rvNewMessage.setLayoutManager(new LinearLayoutManager(mContext));
		rvNewMessage.setAdapter(adapterWithHF);
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
						holderForReview.refresh();
						getDatasFromNetwork();
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
						holderForReview.loadMore();
						getDatasFromNetwork();
					}
				});
			}
		});

	}

	//删除信息方法
	private void deleteMessage(int id, final int position) {

		OkGo.get(Urls.MessageDeleteURL)
				.tag(this)//
				.params("messageId", id)
				.params("userId", CommonUtil.getUserId())
				.execute(new JsonCallback<IycResponse<String>>(mContext) {

					@Override
					public void onSuccess(IycResponse<String> stringIycResponse, Call call, Response response) {
						if(commentList.size()>position) {
							commentList.remove(position);
							newMessageCommentAdapter.notifyDataSetChanged();
						}
					}

				});
	}

	private void getDatasFromNetwork() {
		OkGo.get(Urls.MessageCommentURL)//
				.tag(this)//
				.params("userId", CommonUtil.getUserId())//
				.params("currentPageNum", holderForReview.getPage())
				.params("pageSize", holderForReview.getPageSize())
				.execute(new JsonCallback<IycResponse<List<MessageComment>>>(mContext) {
					@Override
					public void onSuccess(IycResponse<List<MessageComment>> listIycResponse, Call call, Response response) {
						if(isNull(listIycResponse)|| isNull(listIycResponse.getData()))
							return;
						if (holderForReview.isRefresh()) {
							commentList.clear();
						}
						commentList.addAll(listIycResponse.getData());
						newMessageCommentAdapter.notifyDataSetChanged();
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
					}

					@Override
					public void parseError(Call call, Exception e) {
						super.parseError(call, e);
//						refreshFailed(messagePtrClassicFrameLayout);
						if(holderForReview.getReloadTimes()<=2){
							holderForReview.reload();
							refreshAuto(messagePtrClassicFrameLayout);
							Logger.e("wzp hahahaha reloadTime:" + holderForReview.getReloadTimes());
						}
					}

					@Override
					public void onAfter(IycResponse<List<MessageComment>> listIycResponse, Exception e) {
						super.onAfter(listIycResponse, e);
						if(isNull(listIycResponse) || isNull(listIycResponse.getData())){
							if(holderForReview.isRefresh()){
								refreshFailed(messagePtrClassicFrameLayout);
							}else{
								holderForReview.loadMoreFailed();
								loadMoreFailed(messagePtrClassicFrameLayout);
							}
						}else{
							boolean isEnd = listIycResponse.getData().size() < holderForReview.getPageSize();
							if(holderForReview.isRefresh()){
								refreshSuccess(messagePtrClassicFrameLayout,!isEnd);
							}else{
								loadMoreSuccess(messagePtrClassicFrameLayout,isEnd);
							}
						}
					}
				});
	}

	/**
	 * 把消息设置为已读
	 * @param messageId
	 * @param position
	 */
	private void readMessage(final int messageId, final int position){
		if(!CommonUtil.getLoginState()){
			Logger.e("wzp 还没有登录，不能设置为已读");
			return;
		}
		if(commentList.get(position).getStatus() == 2){
			Logger.e("wzp 已经已读咯");
			return;
		}
		OkGo.get(Urls.ReadTheMessageURL)
				.params("messageId",messageId)
				.execute(new StringCallback() {
					@Override
					public void onSuccess(String s, Call call, Response response) {
						Logger.i( messageId + " 已读成功");
						if(commentList.size()>position) {
							if (commentList.get(position).getStatus() == 1) {
								commentList.get(position).setStatus(2);
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
