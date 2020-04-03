package com.iyangcong.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.BookMarketBookListActivity;
import com.iyangcong.reader.bean.MarketBookListItem;
import com.iyangcong.reader.utils.GlideImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/3/30.
 */

public class BookSpecialTopicAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<MarketBookListItem> bookList;
    private LayoutInflater mLayoutInflater;


    public BookSpecialTopicAdapter(Context context, List<MarketBookListItem> bookList) {
        this.mContext = context;
        this.bookList = bookList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SpecialTopicViewHolder(mContext, mLayoutInflater.inflate(R.layout.rv_item_topic_image, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SpecialTopicViewHolder specialTopicViewHolder = (SpecialTopicViewHolder) holder;
        specialTopicViewHolder.setData(bookList);
    }


    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public class SpecialTopicViewHolder extends RecyclerView.ViewHolder {
        private Context mContext;

        @BindView(R.id.rv_book_market_topic)
        RecyclerView rvBookMarketTopic;

        public SpecialTopicViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        void setData(List<MarketBookListItem> bookList) {
            TopicAdapter topicAdapter = new TopicAdapter(mContext, bookList);
            rvBookMarketTopic.setAdapter(topicAdapter);
            GridLayoutManager manager = new GridLayoutManager(mContext, 2);
            rvBookMarketTopic.setLayoutManager(manager);

        }
    }

    class TopicAdapter extends RecyclerView.Adapter {

        private List<MarketBookListItem> bookList;
        private Context mContext;


        TopicAdapter(Context context, List<MarketBookListItem> bookList) {
            this.bookList = bookList;
            this.mContext = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.iv_item_topic_image, null);
            return new ViewHolder(view, mContext);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.setData(bookList.get(position));
        }

        @Override
        public int getItemCount() {
            return bookList == null ? 0 : bookList.size();
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private Context context;
            @BindView(R.id.iv_topic_image)
            ImageView ivTopicImage;
            @BindView(R.id.tv_special_title)
            TextView tvSpecialTitle;

            public ViewHolder(View itemView, Context mContext) {
                super(itemView);
                context = mContext;
                ButterKnife.bind(this, itemView);

            }

            void setData(final MarketBookListItem book) {
                GlideImageLoader.displaysetdefault(context, ivTopicImage, book.getImgUrl(), R.drawable.pic_default_special_topic);
                if (book.getName() != null && book.getName().length() > 0) {
                    if (book.getName().contains("#免费")) {
                        tvSpecialTitle.setText(book.getName().substring(0, book.getName().length() - 3));
                    } else {
                        tvSpecialTitle.setText(book.getName());
                    }
                }
                tvSpecialTitle.setVisibility(View.VISIBLE);
                ivTopicImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, BookMarketBookListActivity.class);
                        intent.putExtra("request", 3);
                        String str = book.getId();
                        intent.putExtra("topicId", str);
                        intent.putExtra("name", book.getName());
                        intent.putExtra("imgurl", book.getImgUrl());
                        intent.putExtra("bookIds", book.getContent());
                        mContext.startActivity(intent);
                    }
                });
            }
        }
    }
}
