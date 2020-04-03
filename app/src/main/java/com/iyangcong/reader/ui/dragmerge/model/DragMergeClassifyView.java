package com.iyangcong.reader.ui.dragmerge.model;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.iyangcong.reader.R;
import com.iyangcong.reader.ui.dragmerge.MyClassifyView;


public class DragMergeClassifyView extends MyClassifyView {
    public DragMergeClassifyView(Context context) {
        super(context);
    }

    public DragMergeClassifyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragMergeClassifyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected Dialog createSubDialog() {
        Dialog dialog = new Dialog(getContext(), R.style.SubDialogStyle);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.height = getHeight();
        layoutParams.dimAmount = 0.2f;
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        layoutParams.format = PixelFormat.TRANSPARENT;
        layoutParams.windowAnimations = R.style.BottomDialogAnimation;
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    @Override
    protected View getSubContent() {
        return inflate(getContext(), R.layout.extra_bookshelf_sub_content, null);
    }
}
