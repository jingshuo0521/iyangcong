package com.iyangcong.reader.adapter;
/**
 * Created by ljw on 2016/12/21.
 */

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.MarketRecommend;
import com.iyangcong.reader.utils.DisplayUtil;
import com.iyangcong.reader.utils.GlideImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 通用版块布局适配器
 */
public class CommonSectionAdapter extends BaseAdapter {

    private final Context mContext;

    private MarketRecommend bookRecommend;
    private GlideImageLoader glideImageLoader;

    public CommonSectionAdapter(Context mContext, MarketRecommend bookRecommend) {
        this.mContext = mContext;
        this.bookRecommend=bookRecommend;
        glideImageLoader = new GlideImageLoader();
    }

    @Override
    public int getCount() {
        return bookRecommend == null?0:5;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            //item的布局：垂直线性，ImagView+TextView
            convertView = View.inflate(mContext, R.layout.item_market_book_type, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_bookType.setGravity(Gravity.CENTER|Gravity.TOP);
        holder.rl_bookType.setPadding(DisplayUtil.dip2px(mContext,7),DisplayUtil.dip2px(mContext,7),DisplayUtil.dip2px(mContext,7),DisplayUtil.dip2px(mContext,7));
        ViewGroup.LayoutParams  lp = holder.iv_bookType.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.height = DisplayUtil.dip2px(mContext,40);
        holder.iv_bookType.setLayoutParams(lp);
        holder.iv_bookType.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        switch (position){
            case 0:
                glideImageLoader.displaysetdefault(mContext,holder.iv_bookType, bookRecommend.getNewbookP(),R.drawable.ic_new_book);
                holder.tv_bookType.setText("新书");
                break;
            case 1:
                glideImageLoader.displaysetdefault(mContext, holder.iv_bookType,bookRecommend.getFreeP(), R.drawable.ic_free);
                holder.tv_bookType.setText("专题");
                break;
            case 2:
                glideImageLoader.displaysetdefault(mContext, holder.iv_bookType, bookRecommend.getCutP(), R.drawable.ic_benefit);
                holder.tv_bookType.setText("优惠");
                break;
            case 3:
                glideImageLoader.displaysetdefault(mContext, holder.iv_bookType, bookRecommend.getMonthP(),R.drawable.ic_monthly);
                holder.tv_bookType.setText("包月");
                break;
            case 4:
                glideImageLoader.displaysetdefault(mContext, holder.iv_bookType,bookRecommend.getCategoryP(), R.drawable.ic_sort);
                holder.tv_bookType.setText("分类");
                break;
        }
        //System.out.println("url=" + bookTypeList.get(position).getSectionImageUrl());

//        switch (bookTypeList.get(position).getSectionType()) {
//            case 0:
//                holder.iv_bookType.setImageResource(R.drawable.btn_new_book);
//                break;
//            case 1:
//                holder.iv_bookType.setImageResource(R.drawable.btn_free);
//                break;
//            case 2:
//                holder.iv_bookType.setImageResource(R.drawable.btn_benefit);
//                break;
//            case 3:
//                holder.iv_bookType.setImageResource(R.drawable.btn_monthly);
//                break;
//            case 4:
//                holder.iv_bookType.setImageResource(R.drawable.btn_sort);
//                break;
//            case 5:
//                holder.iv_bookType.setImageResource(R.drawable.ic_default_economic);
//                break;
//            case 6:
//                holder.iv_bookType.setImageResource(R.drawable.ic_default_culture);
//                break;
//            case 7:
//                holder.iv_bookType.setImageResource(R.drawable.ic_default_sports);
//                break;
//            case 8:
//                holder.iv_bookType.setImageResource(R.drawable.ic_default_psychology);
//                break;
//            case 9:
//                holder.iv_bookType.setImageResource(R.drawable.ic_default_history);
//                break;
//        }
        //使用Glide加载图片
//        Glide.with(mContext).load(bookTypeList.get(position).getBookSectionImageUrl()).into(holder.iv_bookType);
        //设置文本
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.iv_book_type)
        public ImageView iv_bookType;
        @BindView(R.id.tv_book_type)
        public TextView tv_bookType;
        @BindView(R.id.rl_book_type)
        public RelativeLayout rl_bookType;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}