package com.iyangcong.reader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.MineShoppingBookIntroduction;
import com.iyangcong.reader.utils.GlideImageLoader;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/12/24 0024.
 */

public class MineShoppingAdapter extends BaseAdapter {
    Context context;
    List<MineShoppingBookIntroduction> list;
    LayoutInflater mInflater;
    GlideImageLoader glideImageLoader;
    BigDecimal bigDecimal;

    public MineShoppingAdapter(Context context, List<MineShoppingBookIntroduction> list) {
        glideImageLoader = new GlideImageLoader();
        this.context = context;
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        int i;
        String language = "";
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_book_introduction_shopping, null);
            holder = new ViewHolder(convertView);
            holder.tvBookIntroduceShoppingPrice = (TextView) convertView.findViewById(R.id.tv_book_introduce_shopping_price);
            holder.tvBookIntroduceShoppingAuthor = (TextView) convertView.findViewById(R.id.tv_book_introduce_shopping_author);
            holder.tvBookIntroduceShoppingTitle = (TextView) convertView.findViewById(R.id.tv_book_introduce_shopping_title);
            holder.tvBookIntroduceShoppingLanguage = (TextView) convertView.findViewById(R.id.tv_book_introduce_shopping_language);
            holder.ivBookIntroductionImageShoppingImage = (ImageView) convertView.findViewById(R.id.iv_book_introduction_image_shopping_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (list.get(position).isChoose())
            holder.cbBookIntroductonShopping.setChecked(true);
        else
            holder.cbBookIntroductonShopping.setChecked(false);

        for (i = 0; i < list.get(position).getBookLanguage().size(); i++) {
            if (i < (list.get(position).getBookLanguage().size() - 1))
                language = list.get(position).getBookLanguage().get(i) + "/" + language;
            else if (i == (list.get(position).getBookLanguage().size() - 1))
                language = language + list.get(position).getBookLanguage().get(i);
        }

        bigDecimal = new BigDecimal(list.get(position).getPresentPrice());
        if (list.get(position).getPresentPrice() < 0) {
            holder.tvBookIntroduceShoppingPrice.setText("价格:免费");
        } else {
            holder.tvBookIntroduceShoppingPrice.setText("¥" + bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        }
        holder.tvBookIntroduceShoppingTitle.setText(list.get(position).getBookName());
        holder.tvBookIntroduceShoppingAuthor.setText("作者：" + list.get(position).getBookAuthor());
        holder.tvBookIntroduceShoppingLanguage.setText("版本：" + language);
        glideImageLoader.displayBookCover(context, holder.ivBookIntroductionImageShoppingImage, list.get(position).getBookImageUrl());
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.cb_book_introducton_shopping)
        CheckBox cbBookIntroductonShopping;
        @BindView(R.id.iv_book_introduction_image_shopping_image)
        ImageView ivBookIntroductionImageShoppingImage;
        @BindView(R.id.tv_book_introduce_shopping_title)
        TextView tvBookIntroduceShoppingTitle;
        @BindView(R.id.tv_book_introduce_shopping_author)
        TextView tvBookIntroduceShoppingAuthor;
        @BindView(R.id.tv_book_introduce_shopping_language)
        TextView tvBookIntroduceShoppingLanguage;
        @BindView(R.id.tv_book_introduce_shopping_price)
        TextView tvBookIntroduceShoppingPrice;
        @BindView(R.id.ll_book_introducton_shopping)
        LinearLayout llBookIntroductonShopping;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
