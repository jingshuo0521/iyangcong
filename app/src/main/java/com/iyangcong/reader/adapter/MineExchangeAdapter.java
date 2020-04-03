package com.iyangcong.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.BookMarketBookDetailsActivity;
import com.iyangcong.reader.activity.BookMarketBookListActivity;
import com.iyangcong.reader.bean.BookIntroduction;
import com.iyangcong.reader.ui.RatingBar;
import com.iyangcong.reader.ui.TagGroup;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.GlideImageLoader;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/12/24 0024.
 */

public class MineExchangeAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<BookIntroduction> list;
    private Context context;
    GlideImageLoader glideImageLoader;

    public MineExchangeAdapter(Context context, List<BookIntroduction> list) {
        this.context = context;
        this.list = list;
        mInflater = LayoutInflater.from(context);
        glideImageLoader = new GlideImageLoader();
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
        int i, j;
        String language = "";
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_book_introduction, null);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //把用list中的内容显示在listview中
        //holder.tvBookIntroduce.setText(list.get(position).getBookIntroduction());
        for (i = 0; i < list.get(position).getBookLanguage().size(); i++) {
            language += list.get(position).getBookLanguage().get(i);
        }
        language = CommonUtil.getSupportLanguage(language);
        glideImageLoader.displayBookCover(context, holder.ivBookIntroductionImage, list.get(position).getBookImageUrl());
        holder.tvBookIntroductionTittle.setText(list.get(position).getBookName());
        holder.rbLevel.setStar(list.get(position).getBookRating());
        holder.rbLevel.setmClickable(false);
        holder.tvBookIntroductionAuthor.setText(list.get(position).getBookAuthor());
        holder.tvBookIntroduce.setText(list.get(position).getBookIntroduction());
        showTags(holder.rlIntroduction, list.get(position).getBookTypeList());
        holder.tvBookIntroductionLanguage.setText(language);
        switch (list.get(position).getBookType()) {
            case 0:
                holder.tvBookKind.setText("免费");
                holder.rbLevel.setVisibility(View.GONE);
                holder.tvBookKind.setVisibility(View.VISIBLE);
                break;
            case 1:
                holder.tvBookKind.setText("试读");
                holder.rbLevel.setVisibility(View.GONE);
                holder.tvBookKind.setVisibility(View.VISIBLE);
                break;
            case 2:
                holder.tvBookKind.setText("已购");
                holder.rbLevel.setVisibility(View.GONE);
                holder.tvBookKind.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        holder.llBookIntroduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BookMarketBookDetailsActivity.class);
                intent.putExtra("bookId", (int) list.get(position).getBookId());
                intent.putExtra("bookName", list.get(position).getBookName());
                context.startActivity(intent);
            }
        });

        holder.rlIntroduction.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                //Toast.makeText(mContext,tag,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, BookMarketBookListActivity.class);
                intent.putExtra("list_type", 8);
                intent.putExtra("tagsName", tag);
                context.startActivity(intent);
            }
        });

        convertView.setTag(holder);
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_book_introduction_image)
        ImageView ivBookIntroductionImage;
        @BindView(R.id.tv_book_introduction_tittle)
        TextView tvBookIntroductionTittle;
        @BindView(R.id.rb_level)
        RatingBar rbLevel;
        @BindView(R.id.tv_book_introduction_author)
        TextView tvBookIntroductionAuthor;
        @BindView(R.id.tv_book_introduce)
        TextView tvBookIntroduce;
        @BindView(R.id.tv_book_introduction_language)
        TextView tvBookIntroductionLanguage;
        @BindView(R.id.ll_book_introduction)
        LinearLayout llBookIntroduction;
        @BindView(R.id.rl_introduction_type)
        TagGroup rlIntroduction;
        @BindView(R.id.tv_book_kind)
        TextView tvBookKind;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private synchronized void showTags(TagGroup tagGroup, List<String> list) {
//        int tagSize = list.size();
        int tagSize = list.size() > 2 ? 2 : list.size();
        String[] tags = new String[tagSize];
        for (int j = 0; j < list.size() && j < 2; j++) {
            tags[j] = list.get(j);
        }
        tagGroup.setTags(tags);
    }
}
