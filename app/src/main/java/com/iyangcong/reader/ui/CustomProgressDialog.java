package com.iyangcong.reader.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.ui.progressWheel.ProgressWheel;

/**
 * @author ljw
 * @Description:加载动画
 */
public class CustomProgressDialog extends Dialog {

    private Context mContext;
    private String mLoadingTip;
    private ProgressWheel progressWheel;
    private TextView mLoadingTv;

    public CustomProgressDialog(Context context, String content) {
        super(context);
        this.mContext = context;
        this.mLoadingTip = content;
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }


    private void initView() {
        setContentView(R.layout.progress_dialog);
        mLoadingTv = (TextView) findViewById(R.id.loadingTv);
        mLoadingTv.setText(mLoadingTip);
        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
    }

}
