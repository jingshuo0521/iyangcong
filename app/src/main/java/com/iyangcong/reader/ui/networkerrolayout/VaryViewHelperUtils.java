package com.iyangcong.reader.ui.networkerrolayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.iyangcong.reader.R;

/**
 * Created by WuZepeng on 2017-04-10.
 */

public class VaryViewHelperUtils {
	private VaryViewHelper mVaryViewHelper;

	public VaryViewHelper initVaryViewHelper(Context context, View view,View.OnClickListener onErrorLayoutClicked){
		if(mVaryViewHelper == null){
			mVaryViewHelper = new VaryViewHelper.Builder()
					.setDataView(view)
//				.setDataView(context.findViewById(R.id.vary_content))//放数据的父布局，逻辑处理在该Activity中处理
//				.setLoadingView(LayoutInflater.from(context).inflate(R.layout.layout_loadingview, null))//加载页，无实际逻辑处理
//				.setEmptyView(LayoutInflater.from(context).inflate(R.layout.layout_emptyview, null))//空页面，无实际逻辑处理
					.setErrorView(LayoutInflater.from(context).inflate(R.layout.layout_error_network, null))//错误页面
					.setRefreshListener(onErrorLayoutClicked)//错误页点击刷新实现
					.build();
		}
		return mVaryViewHelper;
	}



	public void showDataView() {
		mVaryViewHelper.showDataView();
	}

	public VaryViewHelper getVaryViewHelper() {
		return mVaryViewHelper;
	}

	public void showErrorView() {
		mVaryViewHelper.showErrorView();
	}
}
