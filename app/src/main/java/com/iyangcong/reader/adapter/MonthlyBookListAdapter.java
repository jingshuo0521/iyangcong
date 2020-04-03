package com.iyangcong.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.BookMarketBookDetailsActivity;
import com.iyangcong.reader.bean.MarketBookListItem;
import com.iyangcong.reader.bean.MonthlyMarketBookListItem;
import com.iyangcong.reader.ui.RatingBar;
import com.iyangcong.reader.ui.TagGroup;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.ViewHolderModel;
import com.orhanobut.logger.Logger;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.iwgang.countdownview.CountdownView;

/**
 * Created by WuZepeng on 2017-04-27.
 */

public class MonthlyBookListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private MonthlyMarketBookListItem item;

    public static final int COVER = 0;
    public static final int BOOK = 1;

    public MonthlyBookListAdapter(Context mContext, MonthlyMarketBookListItem item) {
        this.mContext = mContext;
        this.item = item;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == COVER) {
            View view = mLayoutInflater.inflate(R.layout.iv_item_topic_image_second, parent, false);
            return new MonthlyBooksCover(mContext, view);
        } else if (viewType == BOOK) {
            View view = mLayoutInflater.inflate(R.layout.item_book_introduction, parent, false);
            return new BookViewHolder(mContext, view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (COVER == getItemViewType(position)) {
            MonthlyBooksCover monthlyBooksCover = (MonthlyBooksCover) holder;
            monthlyBooksCover.setData(item);
        } else if (BOOK == getItemViewType(position)) {
            BookViewHolder viewHolder = (BookViewHolder) holder;
            viewHolder.setData(item.getBooks().get(position - 1));
        }

    }

    @Override
    public int getItemCount() {
        return item == null ? 0 : (item.getBooks() == null ? 1 : item.getBooks().size() + 1);
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case COVER:
                return COVER;
            default:
                return BOOK;
        }
    }

    @OnClick({R.id.iv_topic_image, R.id.iv_topic_banner})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_topic_image:
                break;
            case R.id.iv_topic_banner:
                break;
        }
    }

    public class MonthlyBooksCover extends ViewHolderModel<MonthlyMarketBookListItem> {

        @BindView(R.id.iv_monthly_book_cover)
        ImageView ivMonthlyBookCover;
        @BindView(R.id.tv_monthly_book_state)
        TextView tvMonthlyBookState;
        @BindView(R.id.cv_timer)
        CountdownView cvTimer;
        @BindView(R.id.rl_monthly_book_state)
        RelativeLayout rlMonthlyBookState;
        @BindView(R.id.tv_tips)
        TextView tvTips;

        private Context coverContext;

        public MonthlyBooksCover(Context context, View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            coverContext = context;
        }

        @Override
        public boolean setLayoutVisibility(MonthlyMarketBookListItem item) {
            if (item == null) {
                return false;
            }
//            if (item.getStatus() == 0) {
//                ivMonthlyBookCover.setVisibility(View.INVISIBLE);
//            } else {
            ivMonthlyBookCover.setVisibility(View.VISIBLE);
//            }
            if (item.getStatus() == 1 || item.getStatus() == 2) { //未购买或者是购买失效
                tvMonthlyBookState.setVisibility(View.VISIBLE);
                tvTips.setVisibility(View.GONE);
                cvTimer.setVisibility(View.GONE);
            } else if (item.getStatus() == 3) {
                tvMonthlyBookState.setVisibility(View.GONE);
                tvTips.setVisibility(View.VISIBLE);
                cvTimer.setVisibility(View.VISIBLE);
            }
            return true;
        }

        @Override
        public void bindData(MonthlyMarketBookListItem item) {
            GlideImageLoader.displaysetdefault(coverContext, ivMonthlyBookCover, item.getBookImageUrl(), R.drawable.image_circle);
            if (item.getStatus() == 1) {
                tvMonthlyBookState.setText("未购买");
            } else if (item.getStatus() == 2) {
                //tvMonthlyBookState.setText("购买失效");
                //shao add begin
                tvMonthlyBookState.setText("包月过期");
                //shao add end

            } else if (item.getStatus() == 3) {
                Logger.i("item.getEndTime:" + item.getDeadLine());
                Date date = new Date(Long.parseLong(item.getDeadLine()));
                Date today = new Date();
                long restTime = date.getTime() - today.getTime();
                cvTimer.setVisibility(View.VISIBLE);
                cvTimer.start(restTime);


            }
        }
    }

    public class BookViewHolder extends ViewHolderModel<MarketBookListItem> {
        @BindView(R.id.iv_book_introduction_image)
        ImageView ivBookIntroductionImage;
        @BindView(R.id.tv_book_kind)
        TextView tvBookKind;
        @BindView(R.id.rl_buy)
        RelativeLayout rlBuy;
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
        @BindView(R.id.ll_book_introduction)
        LinearLayout llBookIntroduction;

        private Context holderContext;

        BookViewHolder(Context context, View view) {
            super(view);
            ButterKnife.bind(this, view);
            holderContext = context;
        }

        @OnClick(R.id.ll_book_introduction)
        public void onClick(View view) {
            MarketBookListItem book = (MarketBookListItem) llBookIntroduction.getTag();
            switch (view.getId()) {
                case R.id.ll_book_introduction:
                    if (book == null/* || item.getStatus() != 3*/)
                        return;
                    Intent intent = new Intent(mContext, BookMarketBookDetailsActivity.class);
                    intent.putExtra("IsMonthlyBookRead", true);
                    intent.putExtra("bookId", Integer.parseInt(book.bookId.trim()));
                    intent.putExtra("bookName", book.bookName);
                    intent.putExtra(Constants.MONTHLY_BOOK_ID, Integer.parseInt(item.getId()));
                    mContext.startActivity(intent);
                    break;
            }
        }

        @Override
        public boolean setLayoutVisibility(MarketBookListItem marketBookListItems) {
            if (marketBookListItems == null) {
                llBookIntroduction.setVisibility(View.GONE);
                return false;
            }
            llBookIntroduction.setVisibility(View.VISIBLE);
            return true;
        }

        @Override
        public void bindData(MarketBookListItem book) {
            GlideImageLoader.displayBookCover(mContext, ivBookIntroductionImage, book.bookImageUrl);
            tvBookIntroductionTittle.setText(book.bookName);
            tvBookIntroductionAuthor.setText(book.bookAuthor);
            rbLevel.setStar(book.bookRating);
            rbLevel.setmClickable(false);
            tvBookIntroduce.setText(book.bookIntroduction);
            llBookIntroduction.setTag(book);
            if (book.bookLanguage != null)
                tvBookIntroductionLanguage.setText(CommonUtil.getSupportLanguage(book.bookLanguage));
            setLabel(book);

        }


        private void setLabel(MarketBookListItem book) {
            if (book.bookTypeList == null)
                return;
            int tagSize = 2;
            String[] tags = new String[tagSize];
            for (int j = 0; j < tagSize && j < book.bookTypeList.size(); j++) {
                tags[j] = book.bookTypeList.get(j);
            }
            rlIntroductionType.setTags(tags);
            rlIntroductionType.setOnTagClickListener(new TagGroup.OnTagClickListener() {
                @Override
                public void onTagClick(String tag) {

                }
            });
        }
    }

}
