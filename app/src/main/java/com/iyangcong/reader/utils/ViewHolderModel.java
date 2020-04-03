package com.iyangcong.reader.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by WuZepeng on 2017-04-26.
 */

public abstract class ViewHolderModel<T> extends RecyclerView.ViewHolder {

	public ViewHolderModel(View itemView) {
		super(itemView);
	}

	public abstract boolean setLayoutVisibility(T t);

	public abstract void bindData(T t);

	public void setData(T t){
		if(setLayoutVisibility(t)){
			bindData(t);
		}
	}
}
