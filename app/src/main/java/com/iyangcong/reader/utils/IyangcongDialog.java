package com.iyangcong.reader.utils;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.flyco.dialog.widget.NormalDialog;

public class IyangcongDialog extends NormalDialog {


    public IyangcongDialog(Context context) {
        super(context);
    }

    public TextView getContentView(){
        return mTvContent;
    }

    @Override
    public void setUiBeforShow() {
        super.setUiBeforShow();
        /** title */
        mTvContent.setMovementMethod(LinkMovementMethod.getInstance());
        mTvContent.setText(Html.fromHtml(mContent));
        mTvContent.setPadding(dp2px(15), dp2px(10), dp2px(15), dp2px(10));
        mTvContent.setMinHeight(dp2px(68));
        mTvContent.setGravity(mContentGravity);
    }
}