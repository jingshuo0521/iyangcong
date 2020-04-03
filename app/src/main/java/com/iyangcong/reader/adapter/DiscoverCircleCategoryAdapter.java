package com.iyangcong.reader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.DiscoverNewCircle;
import com.iyangcong.reader.bean.DiscoverCrircleCategory;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by WuZepeng on 2017-02-25.
 */

public class DiscoverCircleCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = "DiscoverCircleCategory";
    private Context context;
    private List<DiscoverCrircleCategory> categoryList;
    private OnItemSelectedListener listener = null;

    public DiscoverCircleCategoryAdapter(Context context, List<DiscoverCrircleCategory> discoverCrircleCategoryList, OnItemSelectedListener onItemSelectedListener) {
        this.context = context;
        this.categoryList = discoverCrircleCategoryList;
        listener = onItemSelectedListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.discover_category_item, parent, false);
        CategoryViewHolder holder = new CategoryViewHolder(context, view);
        return holder;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        CategoryViewHolder viewHolder = (CategoryViewHolder) holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CategoryViewHolder viewHolder = (CategoryViewHolder) holder;
        viewHolder.chooseButton(categoryList, position);
    }


    @Override
    public int getItemCount() {
        return categoryList == null ? 0 : categoryList.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        @BindView(R.id.btn_category_style1)
        Button rb_category1;
        @BindView(R.id.btn_category_style2)
        Button rb_category2;

        public CategoryViewHolder(Context context, View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.context = context;
        }

        public void setData(Button button, List<DiscoverCrircleCategory> dccList, final int position) {
            DiscoverCrircleCategory dcc = dccList.get(position);
            button.setText(dcc.getCategoryname());
            button.setTag(dcc.getCategoryname());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                        listener.onItemSelected(view, position);
                }
            });


        }


        private void chooseButton(List<DiscoverCrircleCategory> dccList, int position) {
            if (context.getClass().getSimpleName().equals(DiscoverNewCircle.class.getSimpleName())) {
                rb_category1.setVisibility(View.GONE);
                rb_category2.setVisibility(View.VISIBLE);
                setData(rb_category2, dccList, position);
                if (dccList.get(position).isClicked()) {
                    rb_category2.setBackgroundResource(R.drawable.ic_round_button_pressed);
                    rb_category2.setTextColor(context.getResources().getColor(R.color.white));
                } else {
                    rb_category2.setBackgroundResource(R.drawable.ic_round_button_normal);
                    rb_category2.setTextColor(context.getResources().getColor(R.color.main_color));
                }
            } else {
                rb_category1.setVisibility(View.VISIBLE);
                rb_category2.setVisibility(View.GONE);
                setData(rb_category1, dccList, position);
                if (dccList.get(position).isClicked()) {
                    rb_category1.setTextColor(context.getResources().getColor(R.color.text_color_orange));
                    rb_category1.setBackgroundColor(context.getResources().getColor(R.color.page_bg));
                } else {
                    rb_category1.setTextColor(context.getResources().getColor(R.color.text_color_middlegray));
                    rb_category1.setBackgroundColor(context.getResources().getColor(R.color.white));
                }
            }
        }

    }

    public interface OnItemSelectedListener {
        public void onItemSelected(View view, int position);
    }

}
