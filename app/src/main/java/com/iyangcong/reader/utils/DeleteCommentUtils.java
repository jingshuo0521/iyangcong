package com.iyangcong.reader.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.ui.IYangCongToast;
import com.lzy.okgo.OkGo;

import me.drakeet.support.toast.ToastCompat;

/**
 * Created by WuZepeng on 2017-11-16.
 */

public class DeleteCommentUtils {

	public static interface DeleteType{
		int topic = 1;
		int comment = 2;
	}
	private int mType;
	private long mCommentUserId;
	private long mUserId;
	private Context mContext;
	private int mWhere;
	private int mResponseId;
	private String mComment;
	public DeleteCommentUtils(Context context, int type, long userId, long commentUserId, int responseId, String comment, int where) {
		mType = type;
		mUserId = userId;
		mContext = context;
		mCommentUserId = commentUserId;
		mResponseId = responseId;
		mWhere = where;
		mComment = comment;
	}

	public <T> void delete(final JsonCallback<T> jsonCallback, final DialogInterface.OnDismissListener dismissListener){
		if(mCommentUserId == mUserId){
			//先判断是否是用户自己的评论，如果不是，不允许删除，如果是，允许删除
			AlertDialog.Builder tmpBuilder = new AlertDialog.Builder(mContext)
					.setTitle("提示")
					.setMessage("您确定要删除此评论吗？")
//					.setMessage("您确定要删除内容为 "+mComment+" 的评论吗？")
					.setPositiveButton("删除", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							if (mWhere==0){
								deleteTopicComment(mResponseId, mType,jsonCallback);
							}else{
								deleteReviewComment(mResponseId, mType,jsonCallback);
							}

							dialogInterface.dismiss();
							if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1){
								dismissListener.onDismiss(dialogInterface);
							}
						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							dialogInterface.dismiss();
							if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1){
								dismissListener.onDismiss(dialogInterface);
							}
						}
					}
					);
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
				tmpBuilder.setOnDismissListener(dismissListener);
			}
			tmpBuilder.create().show();
		}else{
			ToastCompat.makeText(mContext,mContext.getResources().getText(R.string.you_cannot_delete_otherones_comment), Toast.LENGTH_SHORT).show();
			dismissListener.onDismiss(null);
		}
	}

	private <T> void deleteReviewComment(int itemId,int type,JsonCallback<T> jsonCallback){
		OkGo.get(Urls.DiscoverDeleteComment)
				.params("userId", CommonUtil.getUserId())
				.params("itemId",itemId)
				.params("type",type)//mType=1 话题  2 回复
				.execute(jsonCallback);
	}
	private <T> void deleteTopicComment(int itemId,int type,JsonCallback<T> jsonCallback){
		OkGo.get(Urls.TopicDeleteComment)
				.params("userId", CommonUtil.getUserId())
				.params("itemId",itemId)
				.params("type",type)//mType=1 话题  2 回复
				.execute(jsonCallback);
	}
}
