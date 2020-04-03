package com.iyangcong.reader.ui;

import android.content.Context;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.SuperToast;
import com.iyangcong.reader.utils.SharedPreferenceUtil;


public class IYangCongToast extends SuperToast {

    static public int LENGTH_SHORT = 500;
    static public int LENGTH_LONG = 1500;

    public IYangCongToast(Context context) {
        super(context);
    }

//	public IYangCongToast(Context context) {
//        super(context);
//    }

    /**
     * 获取控件实例
     *
     * @param context
     * @param text    提示消息
     * @return
     */
    public static void show(Context context, CharSequence text, boolean isLengthLong) {
        if (SharedPreferenceUtil.getInstance().getString(SharedPreferenceUtil.PHONE_MODEL, "").contains("MI ")) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        } else {
            IYangCongToast result = new IYangCongToast(context);
            result.setText(text);
            result.setDuration(isLengthLong ? IYangCongToast.LENGTH_LONG : IYangCongToast.LENGTH_SHORT);
            result.show();
        }
    }

    /**
     * 获取控件实例
     *
     * @param context
     * @param text    提示消息
     * @return
     */
    public static void show(Context context, CharSequence text, int showTime) {
        if (SharedPreferenceUtil.getInstance().getString(SharedPreferenceUtil.PHONE_MODEL, "").contains("MI ")) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        } else {
            IYangCongToast result = new IYangCongToast(context);
            result.setText(text);
            result.setDuration(showTime);
            result.show();
        }
    }
}
