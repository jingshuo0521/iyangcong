package com.iyangcong.reader.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by shao on 16/3/9.
 */

public class TextImageButton extends LinearLayout {

    /**
     * author shao by date 20150520
     *
     * **/
    private ImageView imageViewbutton;
    private TextView textView;

    public TextImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);


        imageViewbutton = new ImageView(context, attrs);

        imageViewbutton.setPadding(0, 0, 0, 0);

        textView = new TextView(context, attrs);
        // 水平居中

        textView.setGravity(android.view.Gravity.CENTER_HORIZONTAL);

        textView.setPadding(0, 10, 0, 0);

        setClickable(true);

        setFocusable(true);

//		setBackgroundResource(android.R.drawable.btn_default);

        setOrientation(LinearLayout.VERTICAL);

        addView(imageViewbutton);

        addView(textView);

    }

    public void setImage(int resId) {
        imageViewbutton.setImageResource(resId);
    }

    public void setText(String text) {
        textView.setText(text);
    }

}
