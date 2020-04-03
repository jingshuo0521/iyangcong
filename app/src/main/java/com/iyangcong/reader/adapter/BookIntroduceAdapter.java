package com.iyangcong.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.BookMarketBookDetailsActivity;
import com.iyangcong.reader.activity.BookMarketBookListActivity;
import com.iyangcong.reader.bean.MarketBookListItem;
import com.iyangcong.reader.ui.RatingBar;
import com.iyangcong.reader.ui.TagGroup;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.GlideImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookIntroduceAdapter extends RecyclerView.Adapter {

    /**
     * 类型1：图书分类--使用ListView实现
     */
    private static final int BOOK_INTRODUCTION_BOOK = 0;

    /**
     * 类型2：图书推荐--使用LinearLayout实现
     */
    private static final int BOOK_INTRODUCTION_IMAGE = 1;


    private Context mContext;
    /**
     * 使用mLayoutInflater来初始化布局
     */

    /**
     * 当前类型
     */
    private int currentType = BOOK_INTRODUCTION_BOOK;

    private LayoutInflater mLayoutInflater;
    private List<MarketBookListItem> bookList;

    public BookIntroduceAdapter(Context mContext, List<MarketBookListItem> bookList) {
        this.mContext = mContext;
        this.bookList = bookList;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == BOOK_INTRODUCTION_BOOK) {
            return new BookViewHolder(mContext, mLayoutInflater.inflate(R.layout.item_book_introduction, null));
        } else if (viewType == BOOK_INTRODUCTION_IMAGE) {
            return new ImageViewHolder(mContext, mLayoutInflater.inflate(R.layout.item_book_image, null));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == BOOK_INTRODUCTION_BOOK) {
            BookViewHolder bookViewHolder = (BookViewHolder) holder;
            bookViewHolder.setData(bookList.get(position));
        } else if (getItemViewType(position) == BOOK_INTRODUCTION_IMAGE) {
            ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
            imageViewHolder.setData(bookList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return bookList == null ? 0 : bookList.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (bookList.get(position).bookKind) {
            case 1:
                currentType = BOOK_INTRODUCTION_BOOK;
                break;
            case 0:
                currentType = BOOK_INTRODUCTION_IMAGE;
                break;
            default:
                break;
        }
        return currentType;
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;
        @BindView(R.id.ll_book_introduction)
        LinearLayout llBookIntroduction;
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
        @BindView(R.id.rl_introduction_type)
        TagGroup rlIntroductionType;

        public BookViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        private synchronized void setLabel(final MarketBookListItem book) {
            int size;
            String[] tags;
            if (book.bookTypeList == null) {
                size = 0;
            } else {
                size = book.bookTypeList.size();
            }
            if (size >= 2) {
                tags = new String[2];
            } else {
                tags = new String[size];
            }
            for (int j = 0; j < 2 && j < size; j++) {
                String tag = book.bookTypeList.get(j);
                if (tag.length() > 6) {
                    tag = tag.substring(0, 5) + "…";
                }
                tags[j] = tag;
            }
            rlIntroductionType.setTags(tags);
            rlIntroductionType.setOnTagClickListener(new TagGroup.OnTagClickListener() {
                @Override
                public void onTagClick(String tag) {
                    Intent intent = new Intent(mContext, BookMarketBookListActivity.class);
                    intent.putExtra("list_type", 8);
                    intent.putExtra("tagsName", tag);
                    mContext.startActivity(intent);
                }
            });
        }

        void setData(final MarketBookListItem book) {
            GlideImageLoader.displayBookCover(mContext, ivBookIntroductionImage, book.bookImageUrl);
            tvBookIntroductionTittle.setText(book.bookName);
            tvBookIntroductionAuthor.setText(book.bookAuthor);
            rbLevel.setStar(book.bookRating / 2);
            rbLevel.setmClickable(false);
            tvBookIntroduce.setText(book.bookIntroduction);
            if (book.bookLanguage != null)
                tvBookIntroductionLanguage.setText(CommonUtil.getSupportLanguage(book.bookLanguage));
            if (book.getBookTypeList() != null && book.getBookTypeList().size() != 0) {
                setLabel(book);
            }

            llBookIntroduction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (book.bookId != null) {
                        Intent intent = new Intent(mContext, BookMarketBookDetailsActivity.class);
                        intent.putExtra("bookId", Integer.parseInt(book.bookId.trim()));
                        intent.putExtra("bookName", book.bookName);
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;
        @BindView(R.id.iv_book_image)
        ImageView ivBookImage;
        @BindView(R.id.tv_topic_title)
        TextView tvtopictitle;
        @BindView(R.id.tv_topic_content)
        TextView tvtopiccontent;
        @BindView(R.id.ll_item_topic)
        LinearLayout llitemtopic;

        public ImageViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        void setData(final MarketBookListItem book) {
            GlideImageLoader.displayRoundCorner(mContext, book.getImgUrl(),ivBookImage,  R.drawable.pic_default_special_topic);
            tvtopictitle.setText(book.getName());
            tvtopiccontent.setText(book.getContent());
            llitemtopic.setOnClickListener(new View.OnClickListener() {
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