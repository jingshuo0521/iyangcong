package com.iyangcong.reader.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyangcong.reader.R;

/**
 * Created by ljw on 2016/12/26.
 */

public class TextItem extends CardView {

    private ImageView ivTextImage;
    private TextView tvTextContent;

    public TextItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_text, this, true);
        ivTextImage = (ImageView) findViewById(R.id.iv_text_image);
        tvTextContent = (TextView) findViewById(R.id.tv_text_content);
        TypedArray localTypedArray = context.obtainStyledAttributes(attrs, R.styleable.text_item);
        setTextSize(localTypedArray.getDimension(R.styleable.text_item_textItemContentSize, 12));
        setDrawable(localTypedArray.getDrawable(R.styleable.text_item_textItemDrawable));
        setText(localTypedArray.getString(R.styleable.text_item_textItemContent));
        setTextColor(localTypedArray.getColor(R.styleable.text_item_textItemContentColor, 0));
        localTypedArray.recycle();
    }


    public void setTextSize(float textSize) {
        if (textSize != 0) {
            tvTextContent.setTextSize(textSize);
        }
    }

    public void setDrawable(Drawable settingItemImage) {
        ivTextImage.setImageDrawable(settingItemImage);
    }

    public void setText(String text) {
        tvTextContent.setText(text);
    }

    public void setTextColor(Integer textColor) {
        if (textColor != 0) {
            tvTextContent.setTextColor(textColor);
        }
    }

}
