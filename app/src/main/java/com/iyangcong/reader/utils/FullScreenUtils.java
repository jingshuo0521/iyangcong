package com.iyangcong.reader.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.VideoView;

/**
 * Created by WuZepeng on 2017-03-30.
 */

public class FullScreenUtils {

	public static void setFullScreenMode(Context context, VideoView videoView){
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowsManger = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		windowsManger.getDefaultDisplay().getMetrics(metrics);
		android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) videoView.getLayoutParams();
		params.width =  metrics.widthPixels;
		params.height = metrics.heightPixels;
		params.leftMargin = 0;
		videoView.setLayoutParams(params);
	}

	public static void setOrginalSizeScreenMode(Context context,VideoView videoView){
		setOrginalSizeScreenMode(context,videoView,300,200,0,0);
	}

	private static void setOrginalSizeScreenMode(Context context,VideoView videoView,int width,int height,int leftMargin,int rightMargin) {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metrics);
		android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) videoView.getLayoutParams();
		params.width =  (int) (width*metrics.density);
		params.height = (int) (height*metrics.density);
		params.leftMargin = leftMargin;
		params.rightMargin = rightMargin;
		videoView.setLayoutParams(params);
	}

}
