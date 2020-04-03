package com.iyangcong.reader.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.DiscoverComment;
import com.iyangcong.reader.bean.ReplyComment;
import com.iyangcong.reader.bean.SubReplyComent;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.interfaceset.OnLikeClickedListener;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.DeleteCommentUtils;
import com.iyangcong.reader.utils.NotNullUtils;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by WuZepeng on 2017-03-16.
 */

public class ReplyAdapter extends RecyclerView.Adapter {
	@BindView(R.id.tv_replyer_name)
	TextView tvReplyerName;
	@BindView(R.id.tv_reply_content)
	TextView tvReplyContent;
	@BindView(R.id.tv_reply_time)
	TextView tvReplyTime;
	@BindView(R.id.tv_reply_like_num)
	TextView tvReplyLikeNum;
	@BindView(R.id.item_reply_layout)
	LinearLayout itemReplyLayout;
	private List<? extends ReplyComment> replyCommentList;
	private Context mContext;
	private int mWhere;
	private OnSecondClassReplyClicked onSecondClassReplyClicked;
	private DiscoverComment comment;
	private OnLikeClickedListener<ReplyComment> onLikeClickedListener;
	private HashMap<Integer,Integer> mHashMap = new HashMap<>();//key:id value:id
	public ReplyAdapter(Context mContext, List<? extends ReplyComment> replyCommentList, DiscoverComment comment, int where) {
		this.replyCommentList = replyCommentList;
		this.mContext = mContext;
		this.comment = comment;
		this.mWhere = where;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.item_reply, parent, false);
		View view = LayoutInflater.from(mContext).inflate(R.layout.item_reply_second, parent, false);
		return new ReplyViewHolder(mContext, view);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		ReplyViewHolder replyViewHolder = (ReplyViewHolder) holder;
		replyViewHolder.setData(replyCommentList.get(position));
	}

	@Override
	public int getItemCount() {
		return replyCommentList.size();
	}


//	public class ReplyViewHolder extends RecyclerView.ViewHolder {
//		private Context context;
//		@BindView(R.id.tv_replyer_name)
//		TextView tvReplyerName;
//		@BindView(R.id.tv_reply_content)
//		TextView tvReplyContent;
//		@BindView(R.id.tv_reply_like_num)
//		TextView tvReplyLikeNum;
//		@BindView(R.id.tv_reply_time)
//		TextView tvReplyTime;
//		@BindView(R.id.item_reply_layout)
//		LinearLayout llItemReply;
////        @BindView(R.id.tv_reply_num)
////        TextView tvReplyNum;
//
//		public ReplyViewHolder(Context context, View view) {
//			super(view);
//			this.context = context;
//			ButterKnife.bind(this, view);
//		}
//
//		@OnClick({R.id.tv_replyer_name, R.id.tv_reply_content, R.id.tv_reply_time, R.id.iv_like, R.id.tv_reply_like_num})
//		public void onClick(View view) {
//			ReplyComment comment = (ReplyComment) llItemReply.getTag();
//			switch (view.getId()) {
//				case R.id.tv_replyer_name:
//				case R.id.tv_reply_content:
//				case R.id.tv_reply_time:
//					if (onSecondClassReplyClicked != null && comment != null)
//						onSecondClassReplyClicked.onDetailComentClicked(comment);
//					break;
//				case R.id.iv_like:
//				case R.id.tv_reply_like_num:
//					if (onLikeClickedListener != null)
//						onLikeClickedListener.onLikeButtonClicked(comment);
//					break;
//			}
//		}
////        @OnClick(R.id.item_reply_layout)
////        public void onClick(View view) {
////            switch (view.getId())
////                R.id.
////
////            if (onSecondClassReplyClicked != null && comment != null)
////                onSecondClassReplyClicked.onDetailComentClicked(comment);
////        }
//
//		public void bindData(ReplyComment reply, DiscoverComment comment) {
//			tvReplyContent.setText(reply.getContent());
//			RichTextUtils.showHtmlText(reply.getContent(), tvReplyContent);
//			tvReplyLikeNum.setText(reply.getLikecount() + "");
//			tvReplyTime.setText(reply.getResponsedate());
//			if (!(reply instanceof SubReplyComent)) {
//				tvReplyerName.setText(handlerStr(reply, comment));
////                tvReplyContent.setText(reply.getContent());
////                RichTextUtils.showHtmlText(reply.getContent(),tvReplyContent);
////                tvReplyLikeNum.setText(reply.getLikecount() + "");
////                tvReplyTime.setText(reply.getResponsedate());
//			} else {
//				tvReplyerName.setText(handlderStr((SubReplyComent) reply));
////                tvReplyContent.setText(reply.getContent());
////                RichTextUtils.showHtmlText(reply.getContent(),tvReplyContent);
////                tvReplyLikeNum.setText(reply.getLikecount() + "");
////                tvReplyTime.setText(reply.getResponsedate());
//			}
//			llItemReply.setTag(reply);
//		}
//
//		private String handlderStr(SubReplyComent subReplyComent) {
//			return subReplyComent.getUsername() == null ? "" : subReplyComent.getUpusername()
//					+ " 回复： " +
//					subReplyComent.getUpusername() == null ? "" : subReplyComent.getUpusername();
//		}
//
//		private String handlerStr(ReplyComment reply, DiscoverComment comment) {
//			if (reply.getResponseid() != comment.getResponseid())
//				return reply.getUsername() == null ? "" : reply.getUsername()
//						+ " 回复：" +
//						comment.getUserName() == null ? "" : comment.getUserName();
//			return reply.getUsername() == null ? "" : reply.getUsername();
//		}
//	}

	public class ReplyViewHolder extends  RecyclerView.ViewHolder implements View.OnLongClickListener{


		@BindView(R.id.tv_replyer_name)
		TextView tvReplyerName;
		@BindView(R.id.tv_reply_content)
		TextView tvReplyContent;
		@BindView(R.id.layout_item_reply_second)
		RelativeLayout layoutItemReplySecond;
		private Context context;
		private boolean isLayoutVisibility;
		ReplyViewHolder(Context context,View view) {
			super(view);
			ButterKnife.bind(this, view);
			this.context = context;
			isLayoutVisibility = setLayoutVisibility(replyCommentList);
		}

		@OnClick({R.id.tv_replyer_name, R.id.tv_reply_content})
		public void onClick(View view) {
			ReplyComment comment = (ReplyComment) layoutItemReplySecond.getTag();
			switch (view.getId()) {
				case R.id.tv_replyer_name:
				case R.id.tv_reply_content:
					if (onSecondClassReplyClicked != null && comment != null)
						onSecondClassReplyClicked.onClicked(comment);
					break;
//				case R.id.iv_like:
//				case R.id.tv_reply_like_num:
//					if (onLikeClickedListener != null)
//						onLikeClickedListener.onLikeButtonClicked(comment);
//					break;
			}
		}


		public boolean setLayoutVisibility(List<? extends ReplyComment> list){
			if(list == null || list.size() ==0){
				layoutItemReplySecond.setVisibility(View.GONE);
				return false;
			}
			layoutItemReplySecond.setVisibility(View.VISIBLE);
			return true;
		}


		public void bindDadta(ReplyComment reply){
			tvReplyContent.setText(reply.getContent());
			layoutItemReplySecond.setTag(reply);
			if (!(reply instanceof SubReplyComent)) {
				tvReplyerName.setText(handlerStr(reply, comment));
			} else {
				tvReplyerName.setText(handlderStr((SubReplyComent) reply));
			}
			layoutItemReplySecond.setBackgroundColor(
					mHashMap.get(reply.getResponseid()) != null &&
							mHashMap.get(reply.getResponseid()) == reply.getResponseid()?//是否被选中
					mContext.getResources().getColor(R.color.book_introduction_color)://选中的背景色
					mContext.getResources().getColor(R.color.click_bg));//没被选中的背景色
			layoutItemReplySecond.setOnLongClickListener(this);
			tvReplyerName.setOnLongClickListener(this);
			tvReplyContent.setOnLongClickListener(this);
		}

		public void setData(ReplyComment replyComment){
			if(isLayoutVisibility){
				bindDadta(replyComment);
			}
		}

		private String handlderStr(SubReplyComent subReplyComent) {
			return (subReplyComent.getUsername() == null ? "" : subReplyComent.getUsername())
					+ " 回复 " +
					(subReplyComent.getUpusername() == null ? "" : subReplyComent.getUpusername()) + " : ";
		}

		private String handlerStr(ReplyComment reply, DiscoverComment comment) {
			if (reply.getResponseid() != comment.getResponseid())
				return (reply.getUsername() == null ? "" : reply.getUsername())
						+ "  回复  " +
						(comment.getUserName() == null ? "" : comment.getUserName()) + " : ";
			return (reply.getUsername() == null ? "" : reply.getUsername()) + " : " ;
		}

		@Override
		public boolean onLongClick(View view) {
			final ReplyComment tmpComment = (ReplyComment)layoutItemReplySecond.getTag();
			mHashMap.clear();//清除之前的长按选中事件
			mHashMap.put(tmpComment.getResponseid(),tmpComment.getResponseid());
			final int index = replyCommentList.indexOf(tmpComment);
			if(replyCommentList.contains(tmpComment)){
				notifyItemChanged(index);
			}
			if(tmpComment != null){
				DeleteCommentUtils tmpUtils = new DeleteCommentUtils(mContext,DeleteCommentUtils.DeleteType.comment,
						CommonUtil.getUserId(),tmpComment.getUserid(),tmpComment.getResponseid(),tmpComment.getContent(),mWhere);
				tmpUtils.delete(new JsonCallback<IycResponse<String>>(mContext) {
					@Override
					public void onSuccess(IycResponse<String> stringIycResponse, Call call, Response response) {
						if (NotNullUtils.isNull(stringIycResponse) || NotNullUtils.isNull(stringIycResponse.getData())) {
							Logger.e("wzp:" + stringIycResponse);
							return;
						}
//						int index = replyCommentList.indexOf(tmpComment);
						if (replyCommentList.remove(tmpComment)) {
							notifyItemRemoved(index);
						}
						ToastCompat.makeText(mContext, stringIycResponse.getMsg(), Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
						ToastCompat.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				}, new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialogInterface) {
						if(replyCommentList.contains(tmpComment)){
							if(mHashMap.remove(tmpComment.getResponseid()) == tmpComment.getResponseid())
								notifyItemChanged(index);
						}
					}
				});
			}
			return false;
		}
	}


	public OnSecondClassReplyClicked getOnSecondClassReplyClicked() {
		return onSecondClassReplyClicked;
	}

	public void setOnSecondClassReplyClicked(OnSecondClassReplyClicked onSecondClassReplyClicked) {
		this.onSecondClassReplyClicked = onSecondClassReplyClicked;
	}

	public interface OnSecondClassReplyClicked {
		public void onClicked(ReplyComment reply);
	}

	public OnLikeClickedListener<ReplyComment> getOnLikeClickedListener() {
		return onLikeClickedListener;
	}

	public void setOnLikeClickedListener(OnLikeClickedListener<ReplyComment> onLikeClickedListener) {
		this.onLikeClickedListener = onLikeClickedListener;
	}
}
