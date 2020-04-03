package com.iyangcong.reader.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.MinePageActivity;
import com.iyangcong.reader.bean.PrivateMessageBean;
import com.iyangcong.reader.interfaceset.OnDeleteListener;
import com.iyangcong.reader.interfaceset.OnItemClickedListener;
import com.iyangcong.reader.ui.button.FlatButton;
import com.iyangcong.reader.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

/**
 * Created by WuZepeng on 2017-06-09.
 */

public class PrivateMessageAdapter extends RecyclerView.Adapter {
	private List<PrivateMessageBean> mList;
	private LayoutInflater mLayoutInflater;
	private Context mContext;
	private OnDeleteListener onDeleteListener;
	private OnItemClickedListener onItemClickedListener;
	private OnItemClickedListener onItemTouchListener;
	public PrivateMessageAdapter(List<PrivateMessageBean> mList, Context mContext) {
		this.mList = mList;
		this.mContext = mContext;
		this.mLayoutInflater = LayoutInflater.from(mContext);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = mLayoutInflater.inflate(R.layout.item_private_message, parent, false);
		return new PrivateMessageViewHolder(view);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		PrivateMessageViewHolder privateMessageViewHolder = (PrivateMessageViewHolder) holder;
		privateMessageViewHolder.setData(mList.get(position));
	}

	@Override
	public int getItemCount() {
		return mList == null ? 0 : mList.size();
	}


	public class PrivateMessageViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.tv_message_sender)
		TextView tvMessageSender;
		@BindView(R.id.tv_message_title)
		TextView tvMessageTitle;
		@BindView(R.id.tv_message_context)
		TextView tvMessageContext;
		@BindView(R.id.btn_delete)
		FlatButton btnDelete;
		@BindView(R.id.iv_has_read)
		ImageView ivHasRead;
		@BindView(R.id.v_divider)
		View vDivider;
		@BindView(R.id.layout_private_message)
		RelativeLayout layoutPrivateMessage;


		PrivateMessageViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}

		private boolean setLayoutVisibility(PrivateMessageBean bean) {
			if (bean == null) {
				layoutPrivateMessage.setVisibility(View.GONE);
				return false;
			}
			layoutPrivateMessage.setVisibility(View.VISIBLE);
			return true;
		}

		@OnClick({R.id.tv_message_sender, R.id.tv_message_title, R.id.tv_message_context, R.id.btn_delete})
		public void onClick(View view) {
			PrivateMessageBean bean = (PrivateMessageBean)layoutPrivateMessage.getTag();
			if(isNull(bean))
				return;
			int position = mList.indexOf(bean);
			if(onItemClickedListener != null){
				onItemClickedListener.onItemClickedListener(position);
			}
			switch (view.getId()) {
				case R.id.tv_message_sender:
					Intent intent = new Intent(mContext, MinePageActivity.class);
					intent.putExtra(Constants.USER_ID, bean.getFromUserId() + "");
					intent.putExtra(Constants.IS_MYSELF, false);
					mContext.startActivity(intent);
					break;
				case R.id.tv_message_title:
				case R.id.tv_message_context:
					break;
				case R.id.btn_delete:
					if(onDeleteListener != null){
						onDeleteListener.onDelete(bean.getMessageId(),position);
					}
					break;
			}
		}
		private void bindData(PrivateMessageBean bean) {
			tvMessageSender.setText(bean.getFromUserName() + mContext.getString(R.string.send_you_private_message));
			tvMessageTitle.setText(mContext.getString(R.string.title) + bean.getTitle());
			tvMessageContext.setText(mContext.getString(R.string.content) + bean.getBody());
			ivHasRead.setImageResource(bean.isHasRead() == 2 ? R.drawable.has_read : R.drawable.has_not_read);
			layoutPrivateMessage.setTag(bean);
			layoutPrivateMessage.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent motionEvent) {
					PrivateMessageBean bean = (PrivateMessageBean)layoutPrivateMessage.getTag();
					onItemTouchListener.onItemClickedListener(mList.indexOf(bean));
					return false;
				}
			});
		}

		private void setData(PrivateMessageBean bean) {
			if (setLayoutVisibility(bean)) {
				bindData(bean);
			}
		}
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

	public OnItemClickedListener getOnItemTouchListener() {
		return onItemTouchListener;
	}

	public void setOnItemTouchListener(OnItemClickedListener onItemTouchListener) {
		this.onItemTouchListener = onItemTouchListener;
	}
}
