package com.iyangcong.reader.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;

import java.util.List;

/**
 * Created by Administrator on 2017/3/16.
 */

public class NewWordExplainView extends LinearLayout {
    private Context context;
    LayoutInflater mInflater = null;

    public NewWordExplainView(Context context) {
        super(context);
        this.context = context;
        setOrientation(LinearLayout.VERTICAL);
        mInflater = LayoutInflater.from(context);
    }

    public NewWordExplainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mInflater = LayoutInflater.from(context);
    }


    public void getContentLayout(List<String> datas) {
        this.removeAllViews();
        if (datas.size() != 0) {
            for (int i = 0; i < datas.size(); i++) {
                LinearLayout itemcontain = (LinearLayout) mInflater.inflate(R.layout.explain_view_item, null);
                TextView text_description = (TextView) itemcontain.findViewById(R.id.text_description);
                text_description.setTextColor(Color.parseColor("#ff000000"));
                text_description.setText(datas.get(i));
                this.addView(itemcontain, i);
            }

        }
    }

    public void addView(List<String> datas) {
        getContentLayout(datas);
        invalidate();
    }
}