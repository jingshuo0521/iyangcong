package com.iyangcong.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.BookMarketBookListActivity;
import com.iyangcong.reader.activity.DiscoverCircleDetailActivity;
import com.iyangcong.reader.activity.MinePageActivity;
import com.iyangcong.reader.bean.CircleCommonSection;
import com.iyangcong.reader.bean.CommonSection;
import com.iyangcong.reader.bean.DiscoverCircleMember;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.DisplayUtil;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.NotNullUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iyangcong.reader.utils.Constants.maxPortaitSize;


/**
 * Created by Administrator on 2017/1/20 0020.
 */
public class OneLineRecyclerAdapter extends RecyclerView.Adapter {
    Context mcontext;
    private List<? extends CommonSection> sectionList = new ArrayList<>();
    private GlideImageLoader glideImageLoader;


    public OneLineRecyclerAdapter(Context context, List<? extends CommonSection> sectionList) {
        mcontext = context;
        this.sectionList = sectionList;
        glideImageLoader = new GlideImageLoader();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DiscoverCircleViewHolder(LayoutInflater.from(mcontext).inflate(R.layout.item_market_book_type, parent, false), mcontext);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DiscoverCircleViewHolder discoverCircleViewHolder = (DiscoverCircleViewHolder) holder;
        discoverCircleViewHolder.setData(sectionList, position);

    }

    @Override
    public int getItemCount() {
        return sectionList == null ? 0 : sectionList.size();
    }


    class DiscoverCircleViewHolder extends RecyclerView.ViewHolder {
        Context context;
        @BindView(R.id.iv_book_type)
        ImageView ivBookType;
        @BindView(R.id.tv_book_type)
        TextView tvBookType;
        @BindView(R.id.rl_book_type)
        RelativeLayout llBookType;
        @BindView(R.id.iv_book_type2)
        ImageView ivBookType2;
        @BindView(R.id.iv_is_class)
        ImageView ivIsClass;

        DiscoverCircleViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            ButterKnife.bind(this, itemView);
            ivBookType.setLayoutParams(new RelativeLayout.LayoutParams(ivBookType.getLayoutParams().width + 50, ivBookType.getLayoutParams().height + 50));
            ivIsClass.setLayoutParams(new RelativeLayout.LayoutParams(ivIsClass.getLayoutParams().width + 50, ivIsClass.getLayoutParams().height + 50));
        }

        @OnClick({R.id.iv_book_type, R.id.iv_is_class})
        public void onClick(View view) {
            CommonSection commonSection = (CommonSection) llBookType.getTag();
            switch (view.getId()) {
                case R.id.iv_book_type:
                case R.id.iv_is_class:
                    if (NotNullUtils.isNull(commonSection)) {
                        return;
                    }
                    if (commonSection.getSectionType() == Constants.PITCUTE_AND_TEXT) {
                        Intent intent = new Intent(context, DiscoverCircleDetailActivity.class);
                        intent.putExtra(Constants.circleId, commonSection.getSectionId());
                        intent.putExtra(Constants.circleName, commonSection.getSectionName());
                        context.startActivity(intent);
                    } else {
                        //书城首页图书分类中的图片通识阅读跳转
                        Intent intent = new Intent(context, BookMarketBookListActivity.class);
                        intent.putExtra("id", commonSection.getSectionId() + "");
                        intent.putExtra("list_type", 9);
                        intent.putExtra("name", commonSection.getSectionName());
                        intent.putExtra("request", 2);
                        context.startActivity(intent);
                    }
                    break;
            }
        }

        void setData(final List<? extends CommonSection> sectionList, final int position) {
            llBookType.setTag(sectionList.get(position));

            showWitchPic(sectionList.get(position));
            switch (sectionList.get(position).getSectionType()) {
                case Constants.PITCUTE_AND_TEXT:
                    tvBookType.setGravity(Gravity.LEFT);
                    tvBookType.setText(sectionList.get(position).getSectionName());
                    tvBookType.setTextSize(12.0f);
                    RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) tvBookType.getLayoutParams();
                    lp1.topMargin = 0;
                    tvBookType.setLayoutParams(lp1);
                    tvBookType.setTextColor(context.getResources().getColor(R.color.text_color));
                    tvBookType.setPadding(DisplayUtil.dip2px(context,4),DisplayUtil.dip2px(context,4),DisplayUtil.dip2px(context,4),DisplayUtil.dip2px(context,4));
                    llBookType.setBackground(context.getResources().getDrawable(R.drawable.bg_lightorange_radius));

                    android.support.v7.widget.RecyclerView.LayoutParams lp = ( android.support.v7.widget.RecyclerView.LayoutParams) llBookType.getLayoutParams();
                    lp.leftMargin = DisplayUtil.dip2px(context,3);
                    lp.rightMargin = DisplayUtil.dip2px(context,3);
                    llBookType.setPadding(0,0,0,0);
                    llBookType.setLayoutParams(lp);
                    //llBookType.setBackgroundColor(context.getResources().getColor(R.color.lightorange));
                    glideImageLoader.displayRoundCorner(context, sectionList.get(position).getSectionImageUrl(), ivBookType,R.drawable.pic_default_topic);
                    break;
                case Constants.ONLY_PTICTURE:
                    final DiscoverCircleMember member = (DiscoverCircleMember) sectionList.get(position);
                    String url;
                    if (isDCMList(sectionList, position))
                        url = member.getCircleMemberImgUrl();
                    else
                        url = sectionList.get(position).getSectionImageUrl();
                    if (getItemCount() >= maxPortaitSize) {
                        if (position < getItemCount() - 1)
                            GlideImageLoader.displayProtrait(context, url, ivBookType2);
                        else
                            ivBookType2.setImageResource(R.drawable.portait_selector);
                    } else {
                        GlideImageLoader.displayProtrait(context, url, ivBookType2);
                    }
                    ivBookType2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (getItemCount() >= maxPortaitSize && position == getItemCount() - 1)
                                return;
                            Intent intent = new Intent(context, MinePageActivity.class);
                            intent.putExtra(Constants.USER_ID, member.getCircleMemberId() + "");
                            intent.putExtra(Constants.IS_MYSELF, false);
                            context.startActivity(intent);
                        }
                    });
                    break;
                case Constants.CLASSIFICATION:
                    tvBookType.setVisibility(View.INVISIBLE);
                    glideImageLoader.onDisplayImage(context, ivBookType, sectionList.get(position).getSectionImageUrl());
                    break;
            }


        }

        /**
         * 判断setData传进来的list是否是装载了DiscoverCircleMember的list,
         * 如果是，取url的时候从DiscoverCircleMember中取circleMemberImgUrl
         * 如果不是，从CommonSection取sectionImageUrl
         * by wzp
         */
        private boolean isDCMList(List<? extends CommonSection> sectionList, int position) {
            DiscoverCircleMember member = (DiscoverCircleMember) sectionList.get(position);
            Logger.i("member", member.toString());
            return !"".equals(member.getCircleMemberImgUrl()) &&
                    null != member.getCircleMemberImgUrl();
        }

        /**
         * 选择需要显示的控件
         * 如果是PITCUTE_AND_TEXT模式就显示ivBookType和tvBookType，隐藏ivBookType2
         * 如果是ONLY_PTICTURE模式就显示ivBookType2，隐藏ivBookType和tvBookType
         *
         * @param commonSection by wzp
         */
        private void showWitchPic(CommonSection commonSection) {
            switch (commonSection.getSectionType()) {
                case Constants.PITCUTE_AND_TEXT:
                    ivBookType2.setVisibility(View.GONE);
                    if (commonSection instanceof CircleCommonSection && ((CircleCommonSection) commonSection).isClassGroup()) {
                        ivIsClass.setVisibility(View.VISIBLE);
                    } else {
                        ivIsClass.setVisibility(View.GONE);
                    }
                    ivBookType.setVisibility(View.VISIBLE);
                    tvBookType.setVisibility(View.VISIBLE);
                    break;
                case Constants.ONLY_PTICTURE:
                    ivBookType2.setVisibility(View.VISIBLE);
                    ivIsClass.setVisibility(View.GONE);
                    ivBookType.setVisibility(View.GONE);
                    tvBookType.setVisibility(View.GONE);
                    break;
            }
        }
    }
}
