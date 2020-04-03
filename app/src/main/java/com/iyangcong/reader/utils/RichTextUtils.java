package com.iyangcong.reader.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.iyangcong.reader.R;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.LinkHolder;
import com.zzhoujay.richtext.RichText;
import com.zzhoujay.richtext.RichTextConfig;
import com.zzhoujay.richtext.callback.ImageFixCallback;
import com.zzhoujay.richtext.callback.ImageGetter;
import com.zzhoujay.richtext.callback.ImageLoadNotify;
import com.zzhoujay.richtext.callback.LinkFixCallback;
import com.zzhoujay.richtext.callback.OnUrlClickListener;
import com.zzhoujay.richtext.callback.SimpleImageFixCallback;
import com.zzhoujay.richtext.ig.DefaultImageGetter;

/**
 * Created by WuZepeng on 2017-03-25.
 */

public class RichTextUtils {

	public static void showHtmlText(String html, ImageGetter imageGetter, TextView textView){
		RichText.from(html)
				.autoFix(false)
				.imageGetter(imageGetter)
				.into(textView);
	}

	public static void showHtmlText(final String head, String html,final TextView textView){
		RichText.from(html)
				.autoFix(false)
				.fix(new SimpleImageFixCallback() {
					@Override
					public void onInit(ImageHolder holder) {
						String url = holder.getSource();
						if(url != null && !"".equals(url))
							Log.i("hahahahahaha oldUrl",url);
						if(!url.startsWith("http") && head != null && !head.equals("")){
							Log.i("hahahahahaha head",head);
							String newUrl = head + url;
							holder.setSource(newUrl);
							holder.setAutoFix(true);
							holder.setAutoPlay(true);
							holder.setAutoStop(false);
						}

					}
				})
				.clickable(true).urlClick(new OnUrlClickListener() {
					@Override
					public boolean urlClicked(String url) {
						Toast.makeText(textView.getContext(), url, Toast.LENGTH_SHORT).show();
		//                    Uri uri = Uri.parse(url);
		//                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		//                    intent.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());
		//                    try {
		//                        startActivity(intent);
		//                        Log.i("RichText","zz:"+url);
		//                        return true;
		//                    } catch (ActivityNotFoundException e) {
		//                        Log.w("URLSpan", "Actvity was not found for intent, " + intent.toString());
		//                    }
						return false;
			}
		}).into(textView);
	}

	public static void showHtmlText(String html, TextView textView){
		RichText.from(html)
				.autoFix(true)
				.fix(new SimpleImageFixCallback() {
					@Override
					public void onInit(ImageHolder holder) {
						super.onInit(holder);
						if(holder.getSource().contains("http")&&holder.getSource().contains(".gif")){
//							holder.setAutoFix(true);
							holder.setAutoPlay(true);
							holder.setAutoStop(false);
						}
					}
				})
				.error(R.drawable.pic_default_topic)
				.into(textView);
	}
}
