package com.iyangcong.reader.utils;

import android.graphics.Color;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.flyco.dialog.widget.NormalDialog;

/**
 * Created by ljw on 2016/12/17.
 */

public class DialogUtils {
    private static BaseAnimatorSet mBasIn;//
    private static BaseAnimatorSet mBasOut;

    /**
     * 普通AlertDialog，标题居中，选项按钮左右对称分布
     *
     * @param dialog  dialog对象
     * @param title   标题
     * @param message 对话框内容
     */
    public static void setAlertDialogNormalStyle(final NormalDialog dialog, String title, String message) {
        dialog.title(title)
                .content(message)
                .style(NormalDialog.STYLE_TWO)
                .btnText("是", "否")
                .titleTextSize(23)
                .titleTextColor(Color.parseColor("#FFEE4D22"))
                .showAnim(mBasIn)
                .dismissAnim(mBasOut)
                .show();
    }

    /**
     * 普通AlertDialog，标题居中，选项按钮左右对称分布
     *
     * @param dialog  dialog对象
     * @param title   标题
     * @param message 对话框内容
     */
    public static void setAlertDialogNormalStyle(final IyangcongDialog dialog, String title, String message, String leftBth, String rightBtn) {
        dialog.title(title)
                .content(message)
                .style(NormalDialog.STYLE_TWO)
                .btnText(leftBth,rightBtn)
                .titleTextSize(18)
                .titleTextColor(Color.parseColor("#FFEE4D22"))
                .showAnim(mBasIn)
                .dismissAnim(mBasOut)
                .show();

    }

    /**
     * 普通MaterialDialog，标题居左，选项按钮位于右下方
     *
     * @param dialog  dialog对象
     * @param title   标题
     * @param message 对话框内容
     */
    public static void setAlertDialogNormalStyle(final MaterialDialog dialog, String title, String message) {
        dialog.title(title)
                .content(message)
                .showAnim(mBasIn)
                .dismissAnim(mBasOut)
                .show();
    }

    public static void setAlertDialogOneButtonStyle(final NormalDialog dialog, String title, String message){
        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
            }
        });
        dialog.title(title)
                .content(message)
                .titleLineHeight(0)
                .style(NormalDialog.STYLE_ONE)
                .btnNum(1)
                .btnText("确定")
                .titleTextSize(23)
                .titleTextColor(Color.parseColor("#FFEE4D22"))
                .showAnim(mBasIn)
                .dismissAnim(mBasOut)
                .show();

    }

    /**
     * 退出程序提示框
     *
     * @param dialog  dialog对象
     */
    public static void setAlertDialogExitStyle(final NormalDialog dialog) {
        dialog.isTitleShow(false)
                .content("是否确认退出程序?")
                .style(NormalDialog.STYLE_TWO)
                .titleTextSize(23)
                .showAnim(mBasIn)
                .dismissAnim(mBasOut)
                .show();
    }

}
