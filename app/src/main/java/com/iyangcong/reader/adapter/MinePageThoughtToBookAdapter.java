package com.iyangcong.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.BookMarketBookDetailsActivity;
import com.iyangcong.reader.activity.DiscoverBookReviewDetailActivity;
import com.iyangcong.reader.bean.MineToughtToBook;
import com.iyangcong.reader.ui.ninegridview.NineGridView;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.HtmlParserUtils;
import com.iyangcong.reader.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ljw on 2017/1/6.
 */

public class MinePageThoughtToBookAdapter extends RecyclerView.Adapter {

    private final Context context;

    /**
     * 使用mLayoutInflater来初始化布局
     */
    private LayoutInflater mLayoutInflater;
    private List<MineToughtToBook> mineToughtToBookList;

    public MinePageThoughtToBookAdapter(Context context, List<MineToughtToBook> mineToughtToBookList) {
        this.context = context;
        this.mineToughtToBookList = mineToughtToBookList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ThoughtToBookViewHolder(context, mLayoutInflater.inflate(R.layout.item_discover_topic, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ThoughtToBookViewHolder dynamicsViewHolder = (ThoughtToBookViewHolder) holder;
        dynamicsViewHolder.setData(mineToughtToBookList.get(position));
    }

    @Override
    public int getItemCount() {
        return mineToughtToBookList.size();
    }

    /**
     * 分类 ViewHolder
     */
    public class ThoughtToBookViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;
        @BindView(R.id.iv_user_image)
        ImageView ivUserImage;
        @BindView(R.id.iv_logoimg)
        ImageView ivLogoImg;
        @BindView(R.id.tv_user_name)
        TextView tvUserName;
        @BindView(R.id.tv_topic_title)
        TextView tvTopicTitle;
        @BindView(R.id.tv_topic_describe)
        TextView tvTopicDescribe;
        @BindView(R.id.ngv_image)
        NineGridView ngvImage;
        @BindView(R.id.tv_deliver_time)
        TextView tvDeliverTime;
        @BindView(R.id.tv_topic_like_num)
        TextView tvTopicLikeNum;
        @BindView(R.id.iv_topic_message)
        ImageView ivTopicMessage;
        @BindView(R.id.tv_message_num)
        TextView tvMessageNum;
        @BindView(R.id.book_item)
        LinearLayout llBookItem;
        @BindView(R.id.tv_book_title)
        TextView tvBookTitle;
        @BindView(R.id.tv_book_author)
        TextView tvBookAuthor;
        @BindView(R.id.tv_book_version)
        TextView tvBookVersion;
        @BindView(R.id.interactive_info)
        RelativeLayout interactiveInfo;
        @BindView(R.id.iv_book_cover)
        ImageView ivBookCover;
        @BindView(R.id.cardView)
        CardView cardView;
        @BindView(R.id.tv_book_publish_house)
        TextView mTvBookPublishHouse;

        public ThoughtToBookViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        void setData(final MineToughtToBook mineToughtToBook) {
            if(CommonUtil.getActivityByContext(context).getLocalClassName().equals("activity.DiscoverySearchActivity")){
                //从搜索页面过来的就不显示下面的评论标识
                llBookItem.setVisibility(View.GONE);
                interactiveInfo.setVisibility(View.GONE);
                tvUserName.setVisibility(View.VISIBLE);
                tvUserName.setText(mineToughtToBook.getUserName());
                ivLogoImg.setVisibility(View.VISIBLE);
                GlideImageLoader.displayBookCover(mContext,ivLogoImg, mineToughtToBook.getBookcover());
            }else{
                llBookItem.setVisibility(View.VISIBLE);
                tvUserName.setVisibility(View.GONE);
            }
            ivUserImage.setVisibility(View.GONE);
            tvTopicTitle.setText(StringUtils.delHTMLTag(mineToughtToBook.getTitle()));
            tvTopicTitle.setGravity(Gravity.CENTER_VERTICAL);
            String html = mineToughtToBook.getContent();
            tvTopicDescribe.setText(Html.fromHtml(HtmlParserUtils.getContent(html)));
            tvDeliverTime.setText(mineToughtToBook.getCreatetime());
            tvTopicLikeNum.setText("" + mineToughtToBook.getReviewsLike());
            tvMessageNum.setText("" + mineToughtToBook.getMessageNum());
            tvBookTitle.setText(mineToughtToBook.getTitleZh());
            tvBookAuthor.setText(mineToughtToBook.getAuther());
            tvBookVersion.setText("版本："+getSupportLanguage(mineToughtToBook.getEdition()));
            mTvBookPublishHouse.setText(mContext.getText(R.string.public_house)+mineToughtToBook.getPublishHouse());
//            GlideImageLoader.displaysetdefault(mContext,ivBookCover,mineToughtToBook.getBookCover(),R.drawable.ic_head_default);
            GlideImageLoader.displayBookCover(mContext, ivBookCover, mineToughtToBook.getBookcover());

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DiscoverBookReviewDetailActivity.class);
                    intent.putExtra(Constants.reviewId, mineToughtToBook.getReviewId());
                    intent.putExtra(Constants.THOUGHT_ACTIVITY_TITLE, mineToughtToBook.getTitle());
                    mContext.startActivity(intent);
                }
            });
            llBookItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, BookMarketBookDetailsActivity.class);
                    intent.putExtra(Constants.BOOK_ID,mineToughtToBook.getBookid());
                    intent.putExtra(Constants.BOOK_NAME,mineToughtToBook.getTitleZh());
                    mContext.startActivity(intent);
                }
            });
            ivLogoImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, BookMarketBookDetailsActivity.class);
                    intent.putExtra(Constants.BOOK_ID,mineToughtToBook.getBookid());
                    intent.putExtra(Constants.BOOK_NAME,mineToughtToBook.getTitleZh());
                    mContext.startActivity(intent);
                }
            });
        }
    }
    private String getSupportLanguage(String bookLanguage) {
        if(bookLanguage==null){
            return "";
        }
        if (bookLanguage.contains("3")) {
            return "中/英/中英";
        } else if (bookLanguage.contains("2")&&bookLanguage.contains("1")) {
            return "中/英";
        } else if(bookLanguage.contains("1")){
            return "中";
        } else {
            return "英";
        }
        }
}
