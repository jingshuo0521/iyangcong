package com.iyangcong.reader.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.DiscoverCrircleCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WuZepeng on 2017-02-25.
 */

public class RadioGroupView extends RadioGroup {

    private List<RadioButton> radioBtnList = new ArrayList<>();
    private Context context;
    private OnRGCompletedListener listener;

    public List<DiscoverCrircleCategory> getList() {
        return list;
    }

    public void setList(List<DiscoverCrircleCategory> list) {
        this.list = list;
        init(list);
    }

    private List<DiscoverCrircleCategory> list = new ArrayList<>();

    public RadioGroupView(Context context) {
        this(context,null);
    }

    public RadioGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(list);
    }

    private void init(List<DiscoverCrircleCategory> list){
        radioBtnList = getBtnList(list);
        add(radioBtnList);
        this.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int categoryId) {
                //请看getBtnList()方法中生成radiobutton时使用了categoryid;
                RadioButton tempButton = (RadioButton)findViewById(categoryId);
                tempButton.setChecked(true);
                if(listener != null)
                    listener.onRadioGroupComleted(categoryId);
            }
        });
    }

    private List<RadioButton> getBtnList(List<DiscoverCrircleCategory> list){
        List<RadioButton> resultList = new ArrayList<>();
        for(DiscoverCrircleCategory bean:list){
            RadioButton radioButton = new RadioButton(context);
            radioButton.setId(Integer.valueOf(bean.getCategoryid()));
            radioButton.setText(bean.getCategoryname());
            setRadioStyle(radioButton);
            resultList.add(radioButton);
        }
        return resultList;
    }

    private void setRadioStyle(RadioButton radioBtn){
        radioBtn.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150));
        radioBtn.setGravity(Gravity.CENTER);
        radioBtn.setButtonDrawable(getResources().getDrawable(android.R.color.transparent));
        Resources resources = (Resources)context.getResources();
        ColorStateList csl = (ColorStateList)resources.getColorStateList(R.color.radiobtncolor);
        radioBtn.setTextColor(csl);
        radioBtn.setBackgroundResource(R.drawable.circle_left_bg_color);
    }

    public void add(List<RadioButton> radioBtnList){
        this.setOrientation(VERTICAL);
        for(RadioButton radioButton:radioBtnList){
            if(radioButton !=  null){
                this.addView(radioButton);
            }
        }
    }

    public void setOnRGCompletedListner(OnRGCompletedListener listener){this.listener = listener;}

    public interface OnRGCompletedListener{
        public void onRadioGroupComleted(int category);
    }
}
