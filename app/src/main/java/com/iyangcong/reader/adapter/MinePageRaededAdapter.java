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
import com.iyangcong.reader.bean.MineReaded;
import com.iyangcong.reader.ui.RatingBar;
import com.iyangcong.reader.ui.TagGroup;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.GlideImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ljw on 2017/1/6.
 */

public class MinePageRaededAdapter extends RecyclerView.Adapter {

    private Context context;

    /**
     * 使用mLayoutInflater来初始化布局
     */
    private LayoutInflater mLayoutInflater;
    private List<MineReaded> mineReadedList;

    public MinePageRaededAdapter(Context context, List<MineReaded> mineReadedList) {
        this.context = context;
        this.mineReadedList = mineReadedList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReadedViewHolder(context, mLayoutInflater.inflate(R.layout.item_book_introduction, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ReadedViewHolder readedViewHolder = (ReadedViewHolder) holder;
        readedViewHolder.setData(mineReadedList.get(position));
    }

    @Override
    public int getItemCount() {
        return mineReadedList.size();
    }

    /**
     * 分类 ViewHolder
     */
    public class ReadedViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;
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

        ReadedViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        void setData(final MineReaded mineReaded) {
            String version = new String();
            int i;
            for (i = 0; i < mineReaded.getVersion().size(); i++) {
                version = version + mineReaded.getVersion().get(i);
            }
            tvBookIntroductionTittle.setText(mineReaded.getTitle_zh());
            rbLevel.setVisibility(View.GONE);
            tvBookIntroductionAuthor.setText(mineReaded.getTitle_en());
            GlideImageLoader.displayBookCover(context, ivBookIntroductionImage, mineReaded.getBookImageUrl());
            tvBookIntroduce.setText("作者：" + (mineReaded.getBookAuthor() == null ? "无" : mineReaded.getBookAuthor()) + "\n" + "出版：" + mineReaded.getSchool());
            tvBookIntroductionLanguage.setText("语言：" + CommonUtil.getSupportLanguage(version));
            rlIntroductionType.setVisibility(View.GONE);
            llBookIntroduction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, BookMarketBookDetailsActivity.class);
                    intent.putExtra(Constants.BOOK_ID, mineReaded.getBookid());
                    intent.putExtra(Constants.BOOK_NAME, mineReaded.getTitle_zh());
                    mContext.startActivity(intent);
                }
            });
        }
    }

}