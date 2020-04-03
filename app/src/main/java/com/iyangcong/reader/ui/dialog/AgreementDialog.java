package com.iyangcong.reader.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;

import com.flyco.dialog.widget.NormalDialog;
import com.iyangcong.reader.R;

/**
 * Created by DarkFlameMaster on 2017/3/17.
 */

public class AgreementDialog extends NormalDialog {

    public AgreementDialog (Context context) {
        super(context);

        /** default value*/
        mTitleTextColor = Color.parseColor("#ff692d");
        mTitleTextSize = 20f;
        mContentTextColor = Color.parseColor("#383838");
        mContentTextSize = 16f;
        mLeftBtnTextColor = Color.parseColor("#8a000000");
        mRightBtnTextColor = Color.parseColor("#8a000000");
        mMiddleBtnTextColor = Color.parseColor("#8a000000");
        /** default value*/
    }


    @Override
    public void setUiBeforShow() {
        super.setUiBeforShow();
        /** content */
        mTvTitle.setGravity(Gravity.CENTER);
        mTvTitle.setPadding(dp2px(0), dp2px(15), dp2px(0), dp2px(0));


        mTvContent.setGravity(Gravity.LEFT);
        mTvContent.setMaxLines(8);
        mTvContent.setMovementMethod(ScrollingMovementMethod.getInstance());

    }

}
