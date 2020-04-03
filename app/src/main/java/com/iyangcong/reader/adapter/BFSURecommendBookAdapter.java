package com.iyangcong.reader.adapter;
/**
 * Created by ljw on 2016/12/21.
 */

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.MarketBookListItem;
import com.iyangcong.reader.bean.MarketRecommend;
import com.iyangcong.reader.utils.GlideImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 通用版块布局适配器
 */
public class BFSURecommendBookAdapter extends BaseAdapter {

    private final Context mContext;

    private List<MarketBookListItem> bookList;
    private GlideImageLoader glideImageLoader;

    public BFSURecommendBookAdapter(Context mContext, List<MarketBookListItem> bookList) {
        this.mContext = mContext;
        this.bookList=bookList;
        glideImageLoader = new GlideImageLoader();
    }

    @Override
    public int getCount() {
        return bookList == null?0:3;
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
            convertView = View.inflate(mContext, R.layout.item_market_bfsu_book, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(bookList.size()>0) {
            MarketBookListItem item = bookList.get(position);
            GlideImageLoader.displaysetdefault(mContext, holder.iv_bookType, item.getBookImageUrl(), R.drawable.pic_default_special_topic);
            holder.tv_bookType.setText(item.getBookName());
        }
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.iv_book_type)
        public ImageView iv_bookType;
        @BindView(R.id.tv_book_type)
        public TextView tv_bookType;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}