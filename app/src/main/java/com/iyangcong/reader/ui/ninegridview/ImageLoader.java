package com.iyangcong.reader.ui.ninegridview;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by ljw on 2017/2/24.
 */

public interface ImageLoader extends Serializable  {

    void displayImage(Activity activity, String path, ImageView imageView, int width, int height);

    void clearMemoryCache();
}
