package com.iyangcong.reader.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.iyangcong.reader.R;
import com.iyangcong.reader.ui.glideimagetransform.GlideCircleTransform;
import com.iyangcong.reader.ui.ninegridview.ImageLoader;
import com.iyangcong.reader.ui.ninegridview.NineGridView;

import java.io.File;

/**
 * Created by ljw on 2016/12/21.
 */

public class GlideImageLoader implements ImageLoader, NineGridView.ImageLoader {
    /**
     * 判断Activity是否Destroy
     * @param activity
     * @return
     */
    public static boolean isDestroy(Activity mActivity) {
        if (mActivity== null || mActivity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed())) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void onDisplayImage(Context context, ImageView imageView, String url) {
        //添加判断
        if(!isDestroy((Activity)context)) {
            Glide.with(context).load(url)//
                    .placeholder(R.drawable.pic_default_topic)// 这行貌似是glide的bug,在部分机型上会导致第一次图片不在中间
                    .error(R.drawable.pic_default_topic)//
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//
                    .into(imageView);
        }
    }

    @Override
    public Bitmap getCacheImage(String url) {
        return null;
    }

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        //添加判断
        if(!isDestroy((Activity)activity)) {
            Glide.with(activity).load(new File(path))//
                    .placeholder(R.drawable.ic_default_color)//
                    .error(R.drawable.ic_default_color)//
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//
                    .into(imageView);
        }
    }

    @Override
    public void clearMemoryCache() {
    }

    public static void displayProtrait(Context context, String url, ImageView imageView) {
        displayProtrait(context, url, imageView, R.drawable.ic_head_default);
    }

    public static void displayProtrait(Context context, String url, ImageView imageView, int drawable) {
        //添加判断
        if(!isDestroy((Activity)context)) {
            Glide.with(context)
                    .load(url)
                    .centerCrop()
                    .error(drawable)
                    .placeholder(drawable)
                    .transform(new GlideCircleTransform(context, 2, context.getResources().getColor(R.color.white)))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(imageView);
        }
    }

    /**
     * 显示圆角图片
     * @param context
     * @param url
     * @param imageView
     * @param drawable
     */
    public static void displayRoundCorner(Context context, String url, ImageView imageView, int drawable) {
        //添加判断
        if(!isDestroy((Activity)context)) {
            Glide.with(context)
                    .load(url)
                    .fitCenter()
                    .error(drawable)
                    .placeholder(drawable)
                    .transform(new FitCenter(context), new GlideRoundTransform(context, 3))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(imageView);
        }
    }

    public static void displayBookCover(Context context, ImageView imageView, String url) {
        //添加判断
        if(!isDestroy((Activity)context)) {
            Glide.with(context)
                    .load(url)
                    .fitCenter()
                    .error(R.drawable.tushupic3x)
                    .placeholder(R.drawable.tushupic3x)
                    .transform(new FitCenter(context), new GlideRoundShadowTransform(context))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(imageView);
        }
    }


    public static void displaysetdefault(Context context, ImageView imageView, String url, int resourceId) {
        //添加判断
        if(!isDestroy((Activity)context)) {
            Glide.with(context).load(url)//
                    .placeholder(resourceId)// 这行貌似是glide的bug,在部分机型上会导致第一次图片不在中间
                    .error(resourceId)//
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//
                    .into(imageView);
        }
    }

    public static void displayNoDefault(Context context, ImageView imageView, String url) {
        //添加判断
        if(!isDestroy((Activity)context)) {
            Glide.with(context).load(url)//
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//
                    .into(imageView);
        }
    }
}
