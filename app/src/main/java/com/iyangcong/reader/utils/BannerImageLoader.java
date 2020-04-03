package com.iyangcong.reader.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.iyangcong.reader.R;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by ljw on 2017/2/25.
 */

public class BannerImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        //Glide 加载图片简单用法
        Glide.with(context).load(path)//
                .placeholder(R.drawable.pic_default_special_topic)// 这行貌似是glide的bug,在部分机型上会导致第一次图片不在中间
                .error(R.drawable.pic_default_special_topic)//
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)//
                .into(imageView);
    }
}
