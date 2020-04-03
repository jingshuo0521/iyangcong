package com.iyangcong.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.BookMarketBookDetailsActivity;
import com.iyangcong.reader.activity.DiscoverCircleDetailActivity;
import com.iyangcong.reader.activity.MinePageActivity;
import com.iyangcong.reader.bean.MessageNoticeInvite;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.interfaceset.OnDeleteListener;
import com.iyangcong.reader.interfaceset.OnItemClickedListener;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.button.FlatButton;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

/**
 * Created by sheng on 2017/3/14.
 * 通知消息种类type  0已经删除 1邀请我读书 2邀请我加入圈子 3转让圈子
 * 4拒绝圈子 转让 5拒绝加入圈子 6同意加入圈子
 * 7同意圈子转让8关注我  9 接受荣誉 10接受奖章
 */

public class NewMessageNoticeAdapter extends RecyclerView.Adapter {

	private Context context;
	private List<MessageNoticeInvite> inviteList;
	private LayoutInflater mInflater;
	private OnDeleteListener onDeleteListener;
	private OnItemClickedListener onItemClickedListener;
	//已经删除
	final int DELETED_MESSAGE = 0;
	//邀请我读书
	final int INVENT_READING = 1;
	//邀请我加入圈子
	final int INVENT_CIRCLE = 2;
	//转让圈子
	final int TRANSFER_CIRCLE = 3;
	//拒绝转让圈子
	final int REFUSE_TRANSFER_CIRCLE = 4;
	//拒绝加入圈子
	final int REFUSE_JOININ_CIRCLE = 5;
	//同意加入圈子
	final int AGREE_JOININ_CIRCLE = 6;
	//同意圈子转让
	final int AGREE_TRANSFER_CIRCLE = 7;
	//关注我
	final int ATTENTION_TO_ME = 8;
	//接受荣誉
	final int HONOR = 9;
	//接受奖章
	final int PRIZE = 10;
	//自己同意接受转让圈子以后自己收到的消息
	final int ARGGREMENT_TRANSFOR_CIRCLE = 11;

	public NewMessageNoticeAdapter(Context context, List<MessageNoticeInvite> list) {
		inviteList = list;
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view;
		switch (viewType) {
			case DELETED_MESSAGE://已经删除
				view = mInflater.inflate(R.layout.item_notice, parent, false);
				return new DeletedMessageItem(view);
			case INVENT_READING://邀请我读书
				view = mInflater.inflate(R.layout.item_notice, parent, false);
				return new InventReadingItem(view);
			case INVENT_CIRCLE://邀请我加入圈子
				view = mInflater.inflate(R.layout.message_invent, parent, false);
				return new InventMeJoinCircle(view);
			case TRANSFER_CIRCLE://转让圈子
				view = mInflater.inflate(R.layout.message_invent, parent, false);
				return new TransforCircleToMe(view);
			case REFUSE_TRANSFER_CIRCLE://拒绝转让圈子
				view = mInflater.inflate(R.layout.item_notice, parent, false);
				return new RefuseCircleTransform(view);
			case REFUSE_JOININ_CIRCLE://拒绝加入圈子
				view = mInflater.inflate(R.layout.item_notice, parent, false);
				return new RefuseJoinInCircle(view);
			case AGREE_JOININ_CIRCLE://同意加入圈子
				view = mInflater.inflate(R.layout.item_notice, parent, false);
				return new AgreeJoinCircle(view);
			case AGREE_TRANSFER_CIRCLE://同意圈子转让
				view = mInflater.inflate(R.layout.item_notice, parent, false);
				return new AgreeTransforCircle(view);
			case ATTENTION_TO_ME://关注我
				view = mInflater.inflate(R.layout.item_notice, parent, false);
				return new AttentionToMe(view);
			case HONOR://接受荣誉
				view = mInflater.inflate(R.layout.item_notice, parent, false);
				return new HonorItem(view);
			case PRIZE://接受奖章
				view = mInflater.inflate(R.layout.item_notice, parent, false);
				return new PrizeItem(view);
			case ARGGREMENT_TRANSFOR_CIRCLE:
				view = mInflater.inflate(R.layout.item_notice2, parent, false);
				return new AgreementTransforCircle(view);
		}
		return null;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		switch (getItemViewType(position)) {
			case DELETED_MESSAGE://已经删除
				DeletedMessageItem deletedMessageItem = (DeletedMessageItem) holder;
				deletedMessageItem.setData(inviteList.get(position));
				break;
			case INVENT_READING://邀请我读书
				InventReadingItem inventReadingItem = (InventReadingItem) holder;
				inventReadingItem.setData(inviteList.get(position));
				break;
			case INVENT_CIRCLE://邀请我加入圈子
				InventMeJoinCircle inventMeJoinCircle = (InventMeJoinCircle) holder;
				inventMeJoinCircle.setData(inviteList.get(position));
				break;
			case TRANSFER_CIRCLE://转让圈子
				TransforCircleToMe transforCircleToMe = (TransforCircleToMe) holder;
				transforCircleToMe.setData(inviteList.get(position));
				break;
			case REFUSE_TRANSFER_CIRCLE://拒绝转让圈子
				RefuseCircleTransform refuseCircleTransform = (RefuseCircleTransform) holder;
				refuseCircleTransform.setData(inviteList.get(position));
				break;
			case REFUSE_JOININ_CIRCLE://拒绝加入圈子
				RefuseJoinInCircle refuseJoinInCircle = (RefuseJoinInCircle) holder;
				refuseJoinInCircle.setData(inviteList.get(position));
			case AGREE_JOININ_CIRCLE://同意加入圈子
				AgreeJoinCircle agreeJoinCircle = (AgreeJoinCircle) holder;
				agreeJoinCircle.setData(inviteList.get(position));
				break;
			case AGREE_TRANSFER_CIRCLE://同意圈子转让
				AgreeTransforCircle agreeTransforCircle = (AgreeTransforCircle) holder;
				agreeTransforCircle.setData(inviteList.get(position));
				break;
			case ATTENTION_TO_ME://关注我
				AttentionToMe attentionToMe = (AttentionToMe) holder;
				attentionToMe.setData(inviteList.get(position));
				break;
			case HONOR://接受荣誉
				HonorItem honorItem = (HonorItem) holder;
				honorItem.setData(inviteList.get(position));
				break;
			case PRIZE://接受奖章
				PrizeItem prizeItem = (PrizeItem) holder;
				prizeItem.setData(inviteList.get(position));
				break;
			case ARGGREMENT_TRANSFOR_CIRCLE://自己同意圈子转让以后自己收到的消息
				AgreementTransforCircle agreementTransforCircle = (AgreementTransforCircle) holder;
				agreementTransforCircle.setData(inviteList.get(position));
				break;
		}
	}

	@Override
	public int getItemCount() {
		return inviteList == null ? 0 : inviteList.size();
	}

	@Override
	public int getItemViewType(int position) {
		switch (inviteList.get(position).getType()) {
			case 0:
				return DELETED_MESSAGE;
			case 1:
				return INVENT_READING;
			case 2:
				return INVENT_CIRCLE;
			case 3:
				return TRANSFER_CIRCLE;
			case 4:
				return REFUSE_TRANSFER_CIRCLE;
			case 5:
				return REFUSE_JOININ_CIRCLE;
			case 6:
				return AGREE_JOININ_CIRCLE;
			case 7:
				return AGREE_TRANSFER_CIRCLE;
			case 8:
				return ATTENTION_TO_ME;
			case 9:
				return HONOR;
			case 10:
				return PRIZE;
			case 11:
				return ARGGREMENT_TRANSFOR_CIRCLE;
		}
		return -1;
	}


	public abstract class ReadOnlyMessageViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.message_bell_ll)
		LinearLayout messageBellLl;
		@BindView(R.id.message_user)
		TextView messageUser;
		@BindView(R.id.group_name)
		TextView groupName;
		@BindView(R.id.invite_care)
		TextView invite_care;
		@BindView(R.id.delete_notice)
		FlatButton delete_notice;
		@BindView(R.id.layout_item_notice)
		LinearLayout layoutItemNotice;
		@BindView(R.id.iv_read_status)
		ImageView ivHasRead;

		ReadOnlyMessageViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}

		public boolean setLayoutVisibility(MessageNoticeInvite invite) {
			if (invite == null) {
				layoutItemNotice.setVisibility(View.GONE);
				return false;
			}
			layoutItemNotice.setVisibility(View.VISIBLE);
			return true;
		}

		private void bindData(final MessageNoticeInvite invite) {
			ivHasRead.setImageResource(invite.getStatus() == 1 ? R.drawable.has_not_read : R.drawable.has_read);
			messageUser.setText(invite.getUserName() == null ? invite.getUserId() : invite.getUserName());
			invite_care.setText(invite.getAction());
			layoutItemNotice.setTag(invite);
			setLayout(invite);
		}

		public void setData(MessageNoticeInvite invite) {
			if (setLayoutVisibility(invite)) {
				bindData(invite);
			}
		}

		abstract void setLayout(MessageNoticeInvite invite);

		@OnClick({R.id.message_user, R.id.delete_notice, R.id.layout_item_notice})
		public void onClick(View view) {
			MessageNoticeInvite invite = (MessageNoticeInvite)layoutItemNotice.getTag();
			if(invite == null){
				Logger.e("wzp invite = " + invite);
				return;
			}
			int position = inviteList.indexOf(invite);
			if(onItemClickedListener != null){
				onItemClickedListener.onItemClickedListener(position);
			}
			switch (view.getId()) {
				case R.id.message_user:
					Intent intent = new Intent(context, MinePageActivity.class);
					intent.putExtra(Constants.USER_ID, invite.getUserId() + "");
					intent.putExtra(Constants.IS_MYSELF, false);
					context.startActivity(intent);
					break;
				case R.id.delete_notice:
					if (onDeleteListener != null)
						onDeleteListener.onDelete(invite.getMessageId(), inviteList.indexOf(invite));
					break;
				case R.id.layout_item_notice:
					if (onItemClickedListener != null) {
						onItemClickedListener.onItemClickedListener(position);
					}
					break;
			}
		}
	}

	public class DeletedMessageItem extends ReadOnlyMessageViewHolder {
		public DeletedMessageItem(View view) {
			super(view);
		}

		@Override
		void setLayout(MessageNoticeInvite invite) {
			messageUser.setText(invite.getUserName() == null ? invite.getUserId() : invite.getUserName());
			groupName.setText(invite.getResponseContent());
		}
	}

	public class InventReadingItem extends ReadOnlyMessageViewHolder {
		public InventReadingItem(View view) {
			super(view);
		}

		@Override
		void setLayout(final MessageNoticeInvite invite) {
			groupName.setText(invite.getBookName());
			groupName.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					int bookId = 0;
					try {
						bookId = Integer.parseInt(invite.getBookId());
					} catch (NumberFormatException e) {
						e.printStackTrace();
						return;
					}
					Intent intent = new Intent(context, BookMarketBookDetailsActivity.class);
					intent.putExtra(Constants.BOOK_ID, bookId);
					intent.putExtra(Constants.BOOK_NAME, invite.getBookName());
					context.startActivity(intent);
				}
			});
		}
	}

	public class InventMeJoinCircle extends InventMessageViewHolder {
		public InventMeJoinCircle(View view) {
			super(view);
		}

		@Override
		public void bindData(MessageNoticeInvite invite) {
			super.bindData(invite);
			groupName.setText(invite.getGroupName());
		}

		@Override
		void groupNameClicked(MessageNoticeInvite invite) {
			goToCircle(invite);
		}

		@Override
		void acceptClicked(MessageNoticeInvite invite) {
			replyToInvention(invite, true);
		}

		@Override
		void refuseClicked(MessageNoticeInvite invite) {
			replyToInvention(invite, false);
		}
	}

	public class TransforCircleToMe extends InventMessageViewHolder {
		public TransforCircleToMe(View view) {
			super(view);
		}

		@Override
		public void bindData(MessageNoticeInvite invite) {
			super.bindData(invite);
			groupName.setText(invite.getGroupName());
		}

		@Override
		void groupNameClicked(MessageNoticeInvite invite) {
			goToCircle(invite);
		}

		@Override
		void acceptClicked(MessageNoticeInvite invite) {
			replayToTransforCircle(invite, true);
		}

		@Override
		void refuseClicked(MessageNoticeInvite invite) {
			replayToTransforCircle(invite, false);
		}
	}

	public class RefuseCircleTransform extends ReadOnlyMessageViewHolder {
		public RefuseCircleTransform(View view) {
			super(view);
		}

		@Override
		void setLayout(final MessageNoticeInvite invite) {
			groupName.setText(invite.getGroupName());
			groupName.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					goToCircle(invite);
				}
			});
		}
	}

	public class RefuseJoinInCircle extends ReadOnlyMessageViewHolder {
		public RefuseJoinInCircle(View view) {
			super(view);
		}

		@Override
		void setLayout(final MessageNoticeInvite invite) {
			groupName.setText(invite.getGroupName());
			groupName.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					goToCircle(invite);
				}
			});
		}
	}

	public class AgreeJoinCircle extends ReadOnlyMessageViewHolder {
		public AgreeJoinCircle(View view) {
			super(view);
		}

		@Override
		void setLayout(final MessageNoticeInvite invite) {
			groupName.setText(invite.getGroupName());
			groupName.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					goToCircle(invite);
				}
			});
		}
	}

	public class AgreeTransforCircle extends ReadOnlyMessageViewHolder {
		public AgreeTransforCircle(View view) {
			super(view);
		}

		@Override
		void setLayout(final MessageNoticeInvite invite) {
			groupName.setText(invite.getGroupName());
			groupName.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					goToCircle(invite);
				}
			});
		}
	}

	public class AttentionToMe extends ReadOnlyMessageViewHolder {
		public AttentionToMe(View view) {
			super(view);
		}

		@Override
		void setLayout(MessageNoticeInvite invite) {
			invite_care.setText("关注了我");
			invite_care.setVisibility(View.VISIBLE);
			groupName.setVisibility(View.GONE);
		}
	}

	public class HonorItem extends ReadOnlyMessageViewHolder {
		public HonorItem(View view) {
			super(view);
		}

		@Override
		void setLayout(MessageNoticeInvite invite) {
			groupName.setVisibility(View.GONE);
		}
	}

	public class PrizeItem extends ReadOnlyMessageViewHolder {
		public PrizeItem(View view) {
			super(view);
		}

		@Override
		void setLayout(MessageNoticeInvite invite) {
			groupName.setVisibility(View.GONE);
		}
	}

	public abstract class InventMessageViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.message_bell_ll)
		LinearLayout messageBellLl;
		@BindView(R.id.message_user)
		TextView messageUser;
		@BindView(R.id.invite_care)
		TextView inviteCare;
		@BindView(R.id.group_name)
		TextView groupName;
		@BindView(R.id.tv_accept)
		TextView tvAccept;
		@BindView(R.id.tv_refuse)
		TextView tvRefuse;
		@BindView(R.id.delete_notice)
		FlatButton deleteNotice;
		@BindView(R.id.layout_item_message_invent)
		LinearLayout layoutItemMessageInvent;
		@BindView(R.id.iv_read_status)
		ImageView ivReadStatus;

		InventMessageViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}

		public boolean setLayoutVisibility(MessageNoticeInvite invite) {
			if (invite == null) {
				layoutItemMessageInvent.setVisibility(View.GONE);
				return false;
			}
			layoutItemMessageInvent.setVisibility(View.VISIBLE);
			return true;
		}

		public void bindData(MessageNoticeInvite invite) {
			ivReadStatus.setImageResource(invite.getStatus() == 1 ? R.drawable.has_not_read : R.drawable.has_read);
			messageUser.setText(invite.getUserName() == null ? invite.getUserId() : invite.getUserName());
			inviteCare.setText(invite.getAction());
			layoutItemMessageInvent.setTag(invite);
		}

		public void setData(MessageNoticeInvite invite) {
			if (setLayoutVisibility(invite)) {
				bindData(invite);
			}
		}

		@OnClick({R.id.message_user, R.id.group_name, R.id.tv_accept, R.id.tv_refuse, R.id.delete_notice, R.id.layout_item_message_invent})
		public void onClick(View view) {
			MessageNoticeInvite invite = (MessageNoticeInvite) layoutItemMessageInvent.getTag();
			if (invite == null) {
				Logger.e("wzp 存入的invite信息错误");
				return;
			}
			if (onItemClickedListener != null)
				onItemClickedListener.onItemClickedListener(inviteList.indexOf(invite));
			switch (view.getId()) {
				case R.id.message_user:
					Intent intent = new Intent(context, MinePageActivity.class);
					intent.putExtra(Constants.USER_ID, invite.getUserId() + "");
					intent.putExtra(Constants.IS_MYSELF, false);
					context.startActivity(intent);
					break;
				case R.id.group_name:
					groupNameClicked(invite);
					break;
				case R.id.tv_accept:
					acceptClicked(invite);
					break;
				case R.id.tv_refuse:
					refuseClicked(invite);
					break;
				case R.id.delete_notice:
					if (onDeleteListener != null)
						onDeleteListener.onDelete(invite.getMessageId(), inviteList.indexOf(invite));
					break;
//                case R.id.layout_item_message_invent:
//                    if (onItemClickedListener != null)
//                        onItemClickedListener.onItemClickedListener(inviteList.indexOf(invite));
//                    break;
			}
		}

		abstract void groupNameClicked(MessageNoticeInvite invite);

		abstract void acceptClicked(MessageNoticeInvite invite);

		abstract void refuseClicked(MessageNoticeInvite invite);
	}

	public class AgreementTransforCircle extends RecyclerView.ViewHolder {
		@BindView(R.id.iv_read_status)
		ImageView mIvReadStatus;
		@BindView(R.id.tv_action)
		TextView mTvAction;
		@BindView(R.id.tv_group_name_tips)
		TextView mTvGroupNameTips;
		@BindView(R.id.tv_group_name)
		TextView mTvGroupName;
		@BindView(R.id.layout_item_notice)
		LinearLayout mLayoutItemNotice;

		AgreementTransforCircle(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}

		private boolean setLayoutVisibility(MessageNoticeInvite invite) {
			if (invite == null) {
				mLayoutItemNotice.setVisibility(View.GONE);
				return false;
			}
			mLayoutItemNotice.setVisibility(View.VISIBLE);
			return true;
		}

		private void bindData(MessageNoticeInvite invite) {
			mIvReadStatus.setImageResource(invite.getStatus() == 2 ? R.drawable.has_read : R.drawable.has_not_read);
			mTvAction.setText(invite.getAction());
			mTvGroupName.setText(invite.getGroupName());
			mLayoutItemNotice.setTag(invite);
		}

		@OnClick({R.id.tv_group_name_tips, R.id.tv_group_name, R.id.delete_notice, R.id.layout_item_notice})
		public void onClick(View view) {
			MessageNoticeInvite invite = (MessageNoticeInvite) mLayoutItemNotice.getTag();
			if (invite == null) {
				Logger.e("wzp invite = " + invite);
				return;
			}
			if (onItemClickedListener != null)
				onItemClickedListener.onItemClickedListener(inviteList.indexOf(invite));
			switch (view.getId()) {
				case R.id.tv_group_name_tips:
				case R.id.tv_group_name:
					goToCircle(invite);
					break;
				case R.id.delete_notice:
					if (onDeleteListener != null) {
						onDeleteListener.onDelete(invite.getMessageId(), inviteList.indexOf(invite));
					}
					break;
			}
		}

		private void setData(MessageNoticeInvite invite) {
			if (setLayoutVisibility(invite)) {
				bindData(invite);
			}
		}
	}

	//是否同意把自己加入某个圈子
	private void replyToInvention(final MessageNoticeInvite invite, boolean accept) {
		if (!CommonUtil.getLoginState()) {
			Logger.e("wzp 尚未登陆，无法同意或拒绝");
			return;
		}
		long userId = SharedPreferenceUtil.getInstance().getLong(SharedPreferenceUtil.USER_ID, -1);
		OkGo.get(Urls.AgreeOrRejectJoinInCircle)
				.params("groupId", invite.getGroupId())
				.params("toUserId", userId)
				.params("userId", invite.getUserId())
				.params("userType", 3)
				.params("type", accept ? 17 : 15)
				.execute(new JsonCallback<IycResponse<String>>(context) {
					@Override
					public void onSuccess(IycResponse<String> stringIycResponse, Call call, Response response) {
						ToastCompat.makeText(context, stringIycResponse.getMsg(), Toast.LENGTH_SHORT).show();
						if (onDeleteListener != null)
							onDeleteListener.onDelete(invite.getMessageId(), inviteList.indexOf(invite));
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
						ToastCompat.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				});
	}

	//跳转到圈子页面
	private void goToCircle(MessageNoticeInvite invite) {
		int groupId;
		try {
			groupId = Integer.parseInt(invite.getGroupId());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return;
		}
		Intent intent = new Intent(context, DiscoverCircleDetailActivity.class);
		intent.putExtra(Constants.circleId, groupId);
		intent.putExtra(Constants.circleName, invite.getGroupName());
		context.startActivity(intent);
	}

	//是否同意把圈子转让给自己
	private void replayToTransforCircle(final MessageNoticeInvite invite, boolean accept) {
		if (!CommonUtil.getLoginState()) {
			Logger.e("wzp 还没有登陆");
			return;
		}
		if (isNull(context, invite.getGroupId(), "") || isNull(context, invite.getUserId(), "")) {
			Logger.e("wzp groupId:" + invite.getGroupId() + "  userId:" + invite.getUserId());
			return;
		}
		OkGo.get(Urls.AgreeOrRejectTransforCircle)
				.params("groupId", invite.getGroupId())
				.params("toUserId", invite.getUserId())
				.params("type", accept ? 20 : 14)
				.params("userId", CommonUtil.getUserId())
				.execute(new JsonCallback<IycResponse<String>>(context) {
					@Override
					public void onSuccess(IycResponse<String> IycResponse, Call call, Response response) {
						ToastCompat.makeText(context, IycResponse.getMsg(), Toast.LENGTH_SHORT).show();
						Logger.i(IycResponse.statusCode == 0 ? "wzp 同意把圈子转给自己" : "wzp 拒绝把圈子转给自己");
						if (onDeleteListener != null) {
							onDeleteListener.onDelete(invite.getMessageId(), inviteList.indexOf(invite));
						}
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
						Logger.e("wzp " + e.getMessage());
					}
				});
	}

	public OnDeleteListener getOnDeleteListener() {
		return onDeleteListener;
	}

	public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
		this.onDeleteListener = onDeleteListener;
	}

	public OnItemClickedListener getOnItemClickedListener() {
		return onItemClickedListener;
	}

	public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
		this.onItemClickedListener = onItemClickedListener;
	}


}
