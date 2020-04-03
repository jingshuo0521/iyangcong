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
import com.iyangcong.reader.adapter.NewMessageNoticeAdapter;
import com.iyangcong.reader.base.BaseFragment;
import com.iyangcong.reader.bean.MessageNoticeInvite;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.interfaceset.OnDeleteListener;
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
import static com.iyangcong.reader.utils.Urls.ReadTheMessageURL;

/**
 * Created by sheng on 2017/3/14.
 */

public class NoticeFragment extends BaseFragment {

	@BindView(R.id.message_ptrClassicFrameLayout)
	PtrClassicFrameLayout messagePtrClassicFrameLayout;
	@BindView(R.id.rv_new_message)
	RecyclerView rvNewMessage;

	private RecyclerAdapterWithHF adapterWithHF;
	private NewMessageNoticeAdapter newMessageNoticeAdapter;
	private List<MessageNoticeInvite> inviteList;
	private Handler handler = new Handler();
	private LoadCountHolder loadCountHolder;
	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_private_message2, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	protected void initData() {
		inviteList = new ArrayList<>();
		loadCountHolder = new LoadCountHolder(){
			@Override
			public void refresh() {
				setPage(1);
				setRefresh(true);
			}
		};
		newMessageNoticeAdapter = new NewMessageNoticeAdapter(mContext, inviteList);
		newMessageNoticeAdapter.setOnDeleteListener(new OnDeleteListener() {
			@Override
			public void onDelete(int id, int position) {
				deleteMessage(id, position);
			}
		});
		newMessageNoticeAdapter.setOnItemClickedListener(new OnItemClickedListener() {
			@Override
			public void onItemClickedListener(int position) {
				readMessage(inviteList.get(position),position);
			}
		});
//        newmessageLv.setDivider(null);
		adapterWithHF = new RecyclerAdapterWithHF(newMessageNoticeAdapter);
		rvNewMessage.setAdapter(adapterWithHF);

		rvNewMessage.setLayoutManager(new LinearLayoutManager(mContext));
		messagePtrClassicFrameLayout.setHorizontalScrollBarEnabled(false);
		messagePtrClassicFrameLayout.post(new Runnable() {

			@Override
			public void run() {
				messagePtrClassicFrameLayout.autoRefresh(true);
			}
		});
		//由于这个页面没有添加分页接口
		messagePtrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						loadCountHolder.refresh();
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
						loadCountHolder.loadMore();
						getDatasFromNetwork();
					}
				});
			}
		});
	}


	private void getDatasFromNetwork() {
		OkGo.get(Urls.MessageNoticeInviteURL)//
				.tag(this)//
				.params("userId", CommonUtil.getUserId())
				.params("currentPageNum",loadCountHolder.getPage())
				.params("pageSize",loadCountHolder.getPageSize())
				.execute(new JsonCallback<IycResponse<List<MessageNoticeInvite>>>(mContext) {
					@Override
					public void onSuccess(IycResponse<List<MessageNoticeInvite>> listIycResponse, Call call, Response response) {
						if(isNull(listIycResponse) || isNull(listIycResponse.getData())){
							return;
						}
						if(loadCountHolder.isRefresh()){
							inviteList.clear();
						}
						inviteList.addAll(listIycResponse.getData());
						adapterWithHF.notifyDataSetChanged();
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						Logger.e("wzp " + e.getMessage());
					}

					@Override
					public void onAfter(IycResponse<List<MessageNoticeInvite>> listIycResponse, Exception e) {
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

	private void readMessage(MessageNoticeInvite invite,int position){
		if(isNull(invite)){
			Logger.e("wzp invite=" + invite);
			return;
		}
		if(invite.getStatus() == 2){
			Logger.i("wzp 已经已读");
			return;
		}
		readMessage(invite.getMessageId(),position);
	}

	private void readMessage(int messageId, final int position){
		if(messageId <= 0 ){
			Logger.e("wzp messageId=" + messageId);
			return;
		}
		OkGo.get(ReadTheMessageURL)
				.params("messageId",messageId)
				.execute(new StringCallback() {
					@Override
					public void onSuccess(String s, Call call, Response response) {
						Logger.i("wzp 已读消息成功");
						if(inviteList.size()>position) {
							if (inviteList.get(position).getStatus() == 1) {
								inviteList.get(position).setStatus(2);
								adapterWithHF.notifyItemChanged(position);
							}
						}
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
						Logger.e("wzp " + e.getMessage());
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
						if(inviteList.size()>position) {
							inviteList.remove(position);
							adapterWithHF.notifyDataSetChanged();
						}
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						if (response != null)
							Logger.i(TAG + "NoticeResponse", response.body().toString());
					}
				});
	}
}
