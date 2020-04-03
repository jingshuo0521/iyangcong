package com.iyangcong.reader.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.MinePageActivity;
import com.iyangcong.reader.bean.DiscoverComment;
import com.iyangcong.reader.bean.DiscoverTopicComments;
import com.iyangcong.reader.bean.ReplyComment;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.interfaceset.OnLikeClickedListener;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.DeleteCommentUtils;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.NotNullUtils;
import com.iyangcong.reader.utils.RichTextUtils;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

/**
 * 评论适配器
 */
public class CommentAdapter extends RecyclerView.Adapter {


	private LayoutInflater mLayoutInflater;

	private Context mContext;
	private int mwhere;
	private List<? extends DiscoverComment> commmentList;
	private OnReplyCommentItemClicked onReplyCommentItemClicked;
	private OnLikeClickedListener<DiscoverComment> discoverCommentOnLikeClickedListener;
	private OnLikeClickedListener<ReplyComment> replyCommentOnLikeClickedListener;
	private HashMap<Integer,Integer> mHashMap = new HashMap<>();//key:id value:id//用来暂存长按点击事件点击的项，使该项背景暂时变化

	public CommentAdapter(Context mContext, List<? extends DiscoverComment> commmentList, int where) {
		this.mContext = mContext;
		this.commmentList = commmentList;
		this.mwhere = where;
		mLayoutInflater = LayoutInflater.from(mContext);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new CommentItemViewHolder(mContext, mLayoutInflater.inflate(R.layout.discover_review_detail_reply, null));
//		return new CommentViewHolder(mContext, mLayoutInflater.inflate(R.layout.item_comment_review_detail, null, false));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		CommentItemViewHolder commentViewHolder = (CommentItemViewHolder) holder;
		commentViewHolder.setData(commmentList.get(position));
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public int getItemCount() {
		return commmentList == null ? 0 : commmentList.size();
	}

//	public class CommentViewHolder extends RecyclerView.ViewHolder {
//
//
//		private Context mContext;
//
//
//		@BindView(R.id.iv_user_image)
//		ImageView ivUserImage;
//		@BindView(R.id.tv_topic_title)
//		TextView tvTopicTitle;
//		@BindView(R.id.tv_user_name)
//		TextView tvUserName;
//		@BindView(R.id.tv_topic_describe)
//		TextView tvTopicDescribe;
//		@BindView(R.id.ngv_image)
//		NineGridView ngvImage;
//		@BindView(R.id.iv_book_cover)
//		ImageView ivBookCover;
//		@BindView(R.id.tv_book_title)
//		TextView tvBookTitle;
//		@BindView(R.id.tv_book_author)
//		TextView tvBookAuthor;
//		@BindView(R.id.tv_book_version)
//		TextView tvBookVersion;
//		@BindView(R.id.book_item)
//		LinearLayout bookItem;
//		@BindView(R.id.tv_deliver_time)
//		TextView tvDeliverTime;
//		@BindView(R.id.iv_topic_like)
//		ImageView ivTopicLike;
//		@BindView(R.id.tv_topic_like_num)
//		TextView tvTopicLikeNum;
//		@BindView(R.id.iv_topic_message)
//		ImageView ivTopicMessage;
//		@BindView(R.id.tv_message_num)
//		TextView tvMessageNum;
//		@BindView(R.id.v_topic_diliver)
//		View vTopicDiliver;
//		@BindView(R.id.container)
//		LinearLayout container;
//		@BindView(R.id.cardView)
//		CardView cardView;
//		@BindView(R.id.reply_RecyclerView)
//		RecyclerView replyRecyclerView;
//
//		public CommentViewHolder(Context mContext, View itemView) {
//			super(itemView);
//			this.mContext = mContext;
//			ButterKnife.bind(this, itemView);
//		}
//
//		@OnClick({R.id.cardView, R.id.iv_topic_like, R.id.tv_topic_like_num, R.id.tv_topic_describe, R.id.tv_deliver_time, R.id.iv_topic_message, R.id.tv_message_num})
//		public void onClick(View view) {
//			DiscoverComment comment = (DiscoverComment) tvTopicDescribe.getTag();
//			switch (view.getId()) {
//				case R.id.iv_topic_like:
//				case R.id.tv_topic_like_num:
//					if (discoverCommentOnLikeClickedListener != null && comment != null) {
//						discoverCommentOnLikeClickedListener.onLikeButtonClicked(comment);
//					}
//					break;
//				case R.id.tv_topic_describe:
//				case R.id.tv_deliver_time:
//				case R.id.iv_topic_message:
//				case R.id.tv_message_num:
//					if (onReplyCommentItemClicked != null && comment != null
//							&& commmentList.indexOf(comment) >= 0) {
//						onReplyCommentItemClicked.onDetailComentClicked(comment, commmentList.indexOf(comment));
//					}
//					break;
//			}
//
//
//		}
//
//		void setData(final DiscoverComment data) {
//			if (data == null)
//				return;
//			GlideImageLoader.displayProtrait(mContext, data.getUserImageUrl(), ivUserImage);
////			GlideImageLoader.displaysetdefault(mContext,ivUserImage,data.getUserImageUrl(),R.drawable.ic_head_default);
//			tvUserName.setText(data.getUserName());
//			RichText.from(data.getCommentContent()).into(tvTopicDescribe);
//			tvDeliverTime.setText(data.getCommentTime());
//			tvTopicLikeNum.setText(data.getLikecount() + "");
//			tvTopicDescribe.setTag(data);
////			if(data.getReplys() == null || data.getReplys().size() == 0)
////				return;
////			tvMessageNum.setText(data.getReplys().size()+"");
////			ReplyAdapter replyAdapter = new ReplyAdapter(mContext,data.getReplys(),data);
//			ReplyAdapter replyAdapter;
//			if (!isDiscoverTopicComments(data)) {
//				if (data.getReplys() == null || data.getReplys().size() == 0)
//					return;
//				tvMessageNum.setText(data.getReplys().size() + "");
//				replyAdapter = new ReplyAdapter(mContext, data.getReplys(), data);
//			} else {
//				if (((DiscoverTopicComments) data).getReplays() == null || ((DiscoverTopicComments) data).getReplays().size() == 0)
//					return;
//				tvMessageNum.setText(((DiscoverTopicComments) data).getReplays().size() + "");
//				replyAdapter = new ReplyAdapter(mContext, ((DiscoverTopicComments) data).getReplays(), data);
//			}
//			setAdapterClickListener(replyAdapter);
//			replyRecyclerView.setAdapter(replyAdapter);
//			GridLayoutManager manager = new GridLayoutManager(mContext, 1);
//			replyRecyclerView.setLayoutManager(manager);
//		}
//
//		private void setAdapterClickListener(ReplyAdapter adapter) {
//			adapter.setOnSecondClassReplyClicked(new ReplyAdapter.OnSecondClassReplyClicked() {
//				@Override
//				public void onDetailComentClicked(ReplyComment reply) {
//					if (onReplyCommentItemClicked != null) {
//						onReplyCommentItemClicked.onDetailComentClicked(reply);
//					}
//				}
//			});
//			adapter.setOnDetailLikeClickListener(new OnLikeClickedListener<ReplyComment>() {
//				@Override
//				public void onLikeButtonClicked(ReplyComment comment) {
//					if (replyCommentOnLikeClickedListener != null) {
//						replyCommentOnLikeClickedListener.onLikeButtonClicked(comment);
//					}
//				}
//			});
//		}
//
//
//	}

	public class CommentItemViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
		@BindView(R.id.iv_user_image)
		ImageView ivUserImage;
		@BindView(R.id.tv_user_name)
		TextView tvUserName;
		@BindView(R.id.tv_deliver_time)
		TextView tvDeliverTime;
		@BindView(R.id.iv_topic_like)
		ImageView ivTopicLike;
		@BindView(R.id.tv_topic_like_num)
		TextView tvTopicLikeNum;
		@BindView(R.id.tv_topic_describe)
		TextView tvTopicDescribe;
		@BindView(R.id.v_topic_diliver)
		View vTopicDiliver;
		@BindView(R.id.container)
		LinearLayout container;
		@BindView(R.id.cardView)
		CardView cardView;
		@BindView(R.id.reply_RecyclerView)
		RecyclerView replyRecyclerView;
		@BindView(R.id.ll_discover_review_detail)
		LinearLayout outContainer;

		private Context context;

		public CommentItemViewHolder(Context context, View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
			this.context = context;
		}

		@OnClick({R.id.cardView, R.id.iv_topic_like, R.id.tv_topic_like_num, R.id.tv_topic_describe, R.id.tv_deliver_time,R.id.tv_user_name})
		public void onClick(View view) {
			DiscoverComment comment = (DiscoverComment) tvTopicDescribe.getTag();
			switch (view.getId()) {
				case R.id.iv_topic_like:
				case R.id.tv_topic_like_num:
					if (discoverCommentOnLikeClickedListener != null && comment != null) {
						discoverCommentOnLikeClickedListener.onLikeButtonClicked(comment);
					}
					break;
				case R.id.tv_user_name:
				case R.id.tv_topic_describe:
				case R.id.tv_deliver_time:
//				case R.id.iv_topic_message:
//				case R.id.tv_message_num:
					if (onReplyCommentItemClicked != null && comment != null
							&& commmentList.indexOf(comment) >= 0) {
						onReplyCommentItemClicked.onClicked(comment, commmentList.indexOf(comment));
					}
					break;
			}
		}

		public void setData(DiscoverComment discoverComment){
			if(setLayoutVisibility(commmentList))
				bindData(discoverComment);
		}

		public boolean setLayoutVisibility(List<? extends DiscoverComment> list){
			if(list == null || list.size() == 0){
				cardView.setVisibility(View.GONE);
				return false;
			}
			cardView.setVisibility(View.VISIBLE);
			return true;
		}

		@Override
		public boolean onLongClick(View view) {
			final DiscoverComment tmpComment = (DiscoverComment)tvTopicDescribe.getTag();
			if(NotNullUtils.isNull(tmpComment)){
				return false;
			}
			//当长按事件响应时，清除之前的选中缓存，然后加入当前选中的项，使当前选中项变色
			mHashMap.clear();
			mHashMap.put(tmpComment.getResponseid(),tmpComment.getResponseid());
			final int index = commmentList.indexOf(tmpComment);
			if(commmentList.contains(tmpComment)){
				notifyItemChanged(index);
			}
			DeleteCommentUtils tmpUtils = new DeleteCommentUtils(mContext,
					DeleteCommentUtils.DeleteType.comment,CommonUtil.getUserId(),
					tmpComment.getUserid() ,tmpComment.getResponseid(),tmpComment.getCommentContent(),mwhere);
			tmpUtils.delete(new JsonCallback<IycResponse<String>>(mContext) {
				@Override
				public void onSuccess(IycResponse<String> stringIycResponse, Call call, Response response) {
					if (NotNullUtils.isNull(stringIycResponse) || NotNullUtils.isNull(stringIycResponse.getData())) {
						Logger.e("wzp " + stringIycResponse);
						return;
					}
//					int index = commmentList.indexOf(tmpComment);
					if (commmentList.remove(tmpComment)) {
						notifyItemRemoved(index);
					}
					ToastCompat.makeText(mContext, stringIycResponse.getMsg(), Toast.LENGTH_LONG).show();
				}

				@Override
				public void onError(Call call, Response response, Exception e) {
					super.onError(call, response, e);
					ToastCompat.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}, new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialogInterface) {
					if (commmentList.contains(tmpComment)) {
						if(mHashMap.remove(tmpComment.getResponseid()) == tmpComment.getResponseid()){
							notifyItemChanged(index);
						}
					}
				}
			});
			return false;
		}

//		private void deleteComment(int itemId,int type){
//			OkGo.get(Urls.URL+"/groups/delitem")
//					.params("userId", CommonUtil.getUserId())
//					.params("itemId",itemId)
//					.params("type",type)//type=1 话题  2 回复
//					.execute(new JsonCallback<IycResponse<DeleteResult>>(context) {
//						@Override
//						public void onSuccess(IycResponse<DeleteResult> deleteResultIycResponse, Call call, Response response) {
//							if(NotNullUtils.isNull(deleteResultIycResponse)||NotNullUtils.isNull(deleteResultIycResponse.getData())){
//								Logger.e("wzp " + deleteResultIycResponse);
//								return;
//							}
//
//							ToastCompat.makeText(mContext,deleteResultIycResponse.getMsg(),Toast.LENGTH_LONG).show();
//						}
//
//						@Override
//						public void onError(Call call, Response response, Exception e) {
//							super.onError(call, response, e);
//							ToastCompat.makeText(mContext,e.getMessage(),Toast.LENGTH_LONG).show();
//						}
//					});
//		}

		public void bindData(final DiscoverComment data){
			GlideImageLoader.displayProtrait(context, data.getUserImageUrl(), ivUserImage);
			tvUserName.setText(data.getUserName());
//			RichText.from(data.getCommentContent()).into(tvTopicDescribe);
			if(mHashMap.get(data.getResponseid())!=null && mHashMap.get(data.getResponseid())== data.getResponseid()){
				container.setBackgroundColor(mContext.getResources().getColor(R.color.book_introduction_color));
				outContainer.setBackgroundColor(mContext.getResources().getColor(R.color.book_introduction_color));
			}else{
				container.setBackgroundColor(mContext.getResources().getColor(R.color.white));
				outContainer.setBackgroundColor(mContext.getResources().getColor(R.color.white));
			}
			RichTextUtils.showHtmlText(data.getPath(),data.getCommentContent(),tvTopicDescribe);
			tvDeliverTime.setText(data.getCommentTime());
			tvTopicLikeNum.setText(data.getLikecount() + "");
			tvTopicDescribe.setSingleLine(false);
			tvTopicDescribe.setEllipsize(null);
			tvTopicDescribe.setTag(data);
			ivTopicLike.setImageResource(data.isLike()?R.drawable.ic_discover_heart_liked :R.drawable.ic_discover_heart);
			ivUserImage.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, MinePageActivity.class);
					intent.putExtra(Constants.USER_ID , data.getUserid()+"");
					intent.putExtra(Constants.IS_MYSELF,false);
					mContext.startActivity(intent);
				}
			});
			cardView.setOnLongClickListener(this);
			tvUserName.setOnLongClickListener(this);
			tvDeliverTime.setOnLongClickListener(this);
			tvTopicDescribe.setOnLongClickListener(this);
//			if(data.getReplys() == null || data.getReplys().size() == 0)
//				return;
//			tvMessageNum.setText(data.getReplys().size()+"");
//			ReplyAdapter replyAdapter = new ReplyAdapter(mContext,data.getReplys(),data);

			ReplyAdapter replyAdapter = null;
			if (!isDiscoverTopicComments(data)) {
				if (!isNull(data.getReplys()))
					replyAdapter = new ReplyAdapter(context, data.getReplys(), data,1);
			} else {
				if (!isNull(((DiscoverTopicComments) data).getReplays()))
					replyAdapter = new ReplyAdapter(context, ((DiscoverTopicComments) data).getReplays(), data,0);
			}
			if(!isNull(replyAdapter)){
				setAdapterClickListener(replyAdapter);
				((SimpleItemAnimator)replyRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
				replyRecyclerView.setAdapter(replyAdapter);
				GridLayoutManager manager = new GridLayoutManager(context, 1);
				replyRecyclerView.setLayoutManager(manager);
				replyRecyclerView.setVisibility(View.VISIBLE);
			}else{
				replyRecyclerView.setVisibility(View.GONE);
			}

		}

		private void setAdapterClickListener(ReplyAdapter adapter) {
			adapter.setOnSecondClassReplyClicked(new ReplyAdapter.OnSecondClassReplyClicked() {
				@Override
				public void onClicked(ReplyComment reply) {
					if (onReplyCommentItemClicked != null) {
						onReplyCommentItemClicked.onClicked(reply);
					}
				}
			});
			adapter.setOnLikeClickedListener(new OnLikeClickedListener<ReplyComment>() {
				@Override
				public void onLikeButtonClicked(ReplyComment comment) {
					if (replyCommentOnLikeClickedListener != null) {
						replyCommentOnLikeClickedListener.onLikeButtonClicked(comment);
					}
				}
			});
		}
	}


	public static boolean isDiscoverTopicComments(DiscoverComment comment) {
		boolean isDiscoverTopicComments = comment instanceof DiscoverTopicComments;
		return isDiscoverTopicComments;
	}

	public interface OnReplyCommentItemClicked {

		public void onClicked(DiscoverComment data, int position);

		public void onClicked(ReplyComment data);
	}

	//    private OnReplySecondClassItemClicked onReplySecondClassItemClicked;
	public OnReplyCommentItemClicked getOnReplyCommentItemClicked() {
		return onReplyCommentItemClicked;
	}

	public void setOnReplyCommentItemClicked(OnReplyCommentItemClicked onReplyCommentItemClicked) {
		this.onReplyCommentItemClicked = onReplyCommentItemClicked;
	}

	public OnLikeClickedListener<DiscoverComment> getDiscoverCommentOnLikeClickedListener() {
		return discoverCommentOnLikeClickedListener;
	}

	public void setDiscoverCommentOnLikeClickedListener(OnLikeClickedListener<DiscoverComment> discoverCommentOnLikeClickedListener) {
		this.discoverCommentOnLikeClickedListener = discoverCommentOnLikeClickedListener;
	}

	public OnLikeClickedListener<ReplyComment> getReplyCommentOnLikeClickedListener() {
		return replyCommentOnLikeClickedListener;
	}

	public void setReplyCommentOnLikeClickedListener(OnLikeClickedListener<ReplyComment> replyCommentOnLikeClickedListener) {
		this.replyCommentOnLikeClickedListener = replyCommentOnLikeClickedListener;
	}


}
