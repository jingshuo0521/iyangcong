package com.iyangcong.reader.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.dialog.utils.CornerUtils;
import com.flyco.dialog.widget.base.BaseDialog;
import com.iyangcong.reader.R;
import com.iyangcong.reader.ui.CircleBaseDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/3/21.
 */

public class SettingDialog extends BaseDialog<CircleBaseDialog> {
    @BindView(R.id.et_setting)
    EditText etSetting;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_setting_title)
    TextView tvSettingTitle;

    private Context mContext;

    private String setStr;
    private String title;
    private int sign;
    private DialogCallback dialogCallback;
    /** default value*/
    int mTitleTextColor ;
    float mTitleTextSize = 22f;
    int mContentTextColor = Color.parseColor("#383838");
    float mContentTextSize;

    {
        mContentTextSize = 17f;
    }

    int mLeftBtnTextColor = Color.parseColor("#8a000000");
    int mRightBtnTextColor = Color.parseColor("#8a000000");
    /** default value*/
    /** show gravity of content(正文内容显示位置) */
    protected int mContentGravity = Gravity.LEFT;
    /** background color(背景颜色) */
    protected int mBgColor = Color.parseColor("#ffffff");
    /** btn press color(按钮点击颜色) */
    protected int mBtnPressColor = Color.parseColor("#E3E3E3");// #85D3EF,#ffcccccc,#E3E3E3
    /** corner radius,dp(圆角程度,单位dp) */
    protected float mCornerRadius = 3;
    /** btn textsize(按钮字体大小) */
    protected float mLeftBtnTextSize = 15f;
    protected float mRightBtnTextSize = 15f;


    public SettingDialog(Context context, String setStr , String title,int sign) {
        super(context);
        this.setStr = setStr;
        this.title=title;
        this.sign=sign;
        this.mContext=context;

    }

    @Override
    public View onCreateView() {
        widthScale(0.85f);
        View inflate = View.inflate(mContext, R.layout.dlg_setting, null);
        ButterKnife.bind(this, inflate);
        inflate.setBackgroundDrawable(
                CornerUtils.cornerDrawable(Color.parseColor("#ffffff"), dp2px(5)));
        if(sign==1){
            etSetting.setInputType(InputType.TYPE_CLASS_PHONE);
        }
        setEditText(setStr);
        setSettingTitle(title);
        return inflate;
    }

    @Override
    public void setUiBeforShow() {
        /*title*/
        mTitleTextColor = mContext.getResources().getColor(R.color.text_color_orange);
        tvSettingTitle.setTextColor(mTitleTextColor);
        tvSettingTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTitleTextSize);
        tvSettingTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        tvSettingTitle.setGravity(Gravity.CENTER);
        tvSettingTitle.setPadding(dp2px(0), dp2px(10), dp2px(0), dp2px(0));
        /*content*/
        etSetting.setGravity(mContentGravity);
        etSetting.setTextColor(mContentTextColor);
        etSetting.setTextSize(TypedValue.COMPLEX_UNIT_SP, mContentTextSize);
        etSetting.setLineSpacing(0, 1.3f);
        etSetting.setPadding(dp2px(15), dp2px(10), dp2px(15), dp2px(10));
        /*left btn */
        float radius = dp2px(mCornerRadius);
        tvConfirm.setGravity(Gravity.CENTER);
        tvConfirm.setTextColor(mLeftBtnTextColor);
        tvConfirm.setTextSize(TypedValue.COMPLEX_UNIT_SP, mLeftBtnTextSize);
        tvConfirm.setLayoutParams(new LinearLayout.LayoutParams(0, dp2px(45), 1));
        tvConfirm.setBackgroundDrawable(com.flyco.dialog.utils.CornerUtils.btnSelector(radius, mBgColor, mBtnPressColor, 0));
        /*right btn*/
        tvCancel.setGravity(Gravity.CENTER);
        tvCancel.setTextColor(mRightBtnTextColor);
        tvCancel.setTextSize(TypedValue.COMPLEX_UNIT_SP, mRightBtnTextSize);
        tvCancel.setLayoutParams(new LinearLayout.LayoutParams(0, dp2px(45), 1));
        tvCancel.setBackgroundDrawable(com.flyco.dialog.utils.CornerUtils.btnSelector(radius, mBgColor, mBtnPressColor, 1));
        /*btn click*/
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCallback.getEditText(etSetting.getText().toString(),sign);
                dismiss();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setEditText(String setStr) {
        if(setStr==null){

        }else{
            etSetting.setText(setStr);
            etSetting.selectAll();
        }
    }

    public void setSettingTitle(String title) {
        if(title==null){

        }else{
            tvSettingTitle.setText(title);
        }
    }

    public interface DialogCallback {
        public String getEditText(String editText,int sign);
    }

    public void setDialogCallback(DialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }
    public void setContentLines(int lines){
        etSetting.setMinLines(lines);
    }
}
