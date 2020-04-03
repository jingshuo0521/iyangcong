package com.iyangcong.reader.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.iyangcong.reader.R;

/**
 * Created by WuZepeng on 2017-04-27.
 */

public class RecycleViewUtils {

	public static View getHeaderView(Activity activity,String url,int defaultPicId) {
		//加载布局为一个视图
		View view = LayoutInflater.from(activity).inflate(R.layout.iv_item_topic_image,null);

		ImageView ivTopicImage= (ImageView) view.findViewById(R.id.iv_topic_image);
		ImageView ivTopicBanner= (ImageView) view.findViewById(R.id.iv_topic_banner);
		ivTopicImage.setVisibility(View.GONE);
		ivTopicBanner.setVisibility(View.VISIBLE);
		new GlideImageLoader().displaysetdefault(activity, ivTopicBanner, url, defaultPicId);
		return view;
	}
}
