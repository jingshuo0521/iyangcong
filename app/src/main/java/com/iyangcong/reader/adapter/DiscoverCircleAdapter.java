package com.iyangcong.reader.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.CircleLabel;
import com.iyangcong.reader.bean.DiscoverCircleDescribe;
import com.iyangcong.reader.bean.DiscoverCircleDetail;
import com.iyangcong.reader.bean.DiscoverCircleMember;
import com.iyangcong.reader.bean.DiscoverTopic;
import com.iyangcong.reader.bean.ShelfBook;

import com.iyangcong.reader.ui.TagGroup;

import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.HtmlParserUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.iyangcong.reader.utils.Constants.maxPortaitSize;

/**
 * Created by ljw on 2016/12/26.
 */

public class DiscoverCircleAdapter extends RecyclerView.Adapter {

    /**
     * 类型1：圈子描述--使用LinearLayout实现
     */
    private static final int DISCOVER_CIRCLE_LL = 0;

    /**
     * 类型2：圈子成员--使用GridView实现
     */
    private static final int DISCOVER_CIRCLE_MEMBER = 1;

    /**
     * 类型3：圈子图书--使用GridView实现
     */
    private static final int DISCOVER_CIRCLE_BOOK = 2;

    /**
     * 类型4：圈子话题--使用ListView实现
     */
    private static final int DISCOVER_CIRCLE_TOPIC = 3;


    /**
     * 当前类型
     */
    private int currentType = DISCOVER_CIRCLE_LL;

    private Context context;

    /**
     * 使用mLayoutInflater来初始化布局
     */
    private LayoutInflater mLayoutInflater;

    private DiscoverCircleDetail discoverCircleDetail;

    public DiscoverCircleAdapter(Context context, DiscoverCircleDetail discoverCircleDetail) {
        this.context = context;
        this.discoverCircleDetail = discoverCircleDetail;
        mLayoutInflater = LayoutInflater.from(context);
    }

    /**
     * 创建ViewHolder布局
     *
     * @param parent
     * @param viewType 当前类型
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == DISCOVER_CIRCLE_LL) {
            return new CircleDescribeViewHolder(context, mLayoutInflater.inflate(R.layout.ll_discover_circle_describe,parent,false));
        } else if (viewType == DISCOVER_CIRCLE_MEMBER) {
            return new CircleMemberViewHolder(context, mLayoutInflater.inflate(R.layout.rv_section_common, parent,false));
        } else if (viewType == DISCOVER_CIRCLE_BOOK) {
            return new CircleBookViewHolder(context, mLayoutInflater.inflate(R.layout.gv_discover_circle_book, parent,false));
        } else if (viewType == DISCOVER_CIRCLE_TOPIC) {
            return new CircleTopicViewHolder(context, mLayoutInflater.inflate(R.layout.rv_discover_topic, parent,false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == DISCOVER_CIRCLE_LL) {
            CircleDescribeViewHolder circleDescribeViewHolder = (CircleDescribeViewHolder) holder;
            circleDescribeViewHolder.setData(discoverCircleDetail.getDiscoverCircleDescribe());
        } else if (getItemViewType(position) == DISCOVER_CIRCLE_MEMBER) {
            CircleMemberViewHolder circleMemberViewHolder = (CircleMemberViewHolder) holder;
            circleMemberViewHolder.setData(discoverCircleDetail.getCircleMemberList());
        } else if (getItemViewType(position) == DISCOVER_CIRCLE_BOOK) {
            CircleBookViewHolder circleBookViewHolder = (CircleBookViewHolder) holder;
            circleBookViewHolder.setData(discoverCircleDetail.getShelfBookList());
        } else if (getItemViewType(position) == DISCOVER_CIRCLE_TOPIC) {
            CircleTopicViewHolder circleTopicViewHolder = (CircleTopicViewHolder) holder;
            circleTopicViewHolder.setData(discoverCircleDetail.getDiscoverTopicList());
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case DISCOVER_CIRCLE_LL:
                currentType = DISCOVER_CIRCLE_LL;
                break;
            case DISCOVER_CIRCLE_MEMBER:
                currentType = DISCOVER_CIRCLE_MEMBER;
                break;
            case DISCOVER_CIRCLE_BOOK:
                currentType = DISCOVER_CIRCLE_BOOK;
                break;
            case DISCOVER_CIRCLE_TOPIC:
                currentType = DISCOVER_CIRCLE_TOPIC;
                break;
            default:
                break;
        }
        return currentType;
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    /**
     * 圈子介绍 ViewHolder
     */
    public class CircleDescribeViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;
        @BindView(R.id.tv_circle_member)
        TextView tvCircleMember;
        @BindView(R.id.tv_circle_topic)
        TextView tvCircleTopic;
        @BindView(R.id.civ_circle_portait)
        ImageView civCircleProtait;
        @BindView(R.id.iv_image_circle)
        ImageView ivImageCircle;
        @BindView(R.id.tv_circle_describe)
        TextView tvCircleDescribe;
        @BindView(R.id.rl_introduction_type)
        TagGroup rlIntrodutionType;
//        @BindView(R.id.tv_book_introduction_type2)
//        TextView tvBookIntroductionType2;
//        @BindView(R.id.label_recyclerview)
//        RecyclerView labelRecycler;
        @BindView(R.id.layout_circle_decribe)
        LinearLayout layoutCricleDescirbe;
        GlideImageLoader glideImageLoader;
		@BindView(R.id.iv_shenhe_status)
		ImageView ivShenheStatus;

        CircleDescribeViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
            glideImageLoader = new GlideImageLoader();
        }

        private boolean setLayoutVisibility(DiscoverCircleDescribe describe) {
            if (describe == null) {
                layoutCricleDescirbe.setVisibility(View.GONE);
                return false;
            }
            layoutCricleDescirbe.setVisibility(View.VISIBLE);
            return true;
        }
        private void bindData(DiscoverCircleDescribe discoverCircleDescribe){
            tvCircleMember.setText(discoverCircleDescribe.getMemberNum() + "");
            tvCircleTopic.setText(discoverCircleDescribe.getTopicNum() + "");
            Logger.i("wzp circelImage:" + discoverCircleDescribe.getCircleImage());
            glideImageLoader.displayProtrait(mContext,discoverCircleDescribe.getCircleImage(),civCircleProtait);
//            glideImageLoader.onDisplayImage(mContext,ivImageCircle,discoverCircleDescribe.getCircleImage());
            glideImageLoader.onDisplayImage(mContext,ivImageCircle,discoverCircleDescribe.getUserImgUrl());
            String html = discoverCircleDescribe.getCircleDescribe();
            tvCircleDescribe.setText(Html.fromHtml(HtmlParserUtils.getContent(html)));
            rlIntrodutionType.setTags(getList(discoverCircleDescribe.getCircleLabel()));
			if(discoverCircleDescribe.getCheckStatus() == 1){//圈子正在审核
				ivShenheStatus.setImageResource(R.drawable.shenheing);
				ivShenheStatus.setVisibility(View.VISIBLE);
			}else if(discoverCircleDescribe.getCheckStatus() == 3){//圈子未通过审核
				ivShenheStatus.setImageResource(R.drawable.shenhenotpass);
				ivShenheStatus.setVisibility(View.VISIBLE);
			}else{
				ivShenheStatus.setVisibility(View.GONE);
			}

//            LabelAdapter labelAdapter = new LabelAdapter(mContext,discoverCircleDescribe.getCircleLabel(),true);
//            LinearLayoutManager llm = new LinearLayoutManager(mContext);
//            llm.setOrientation(LinearLayoutManager.HORIZONTAL);
//            labelRecycler.setLayoutManager(llm);
//            labelRecycler.hasFixedSize();
//            labelRecycler.setAdapter(labelAdapter);
        }

        void setData(DiscoverCircleDescribe discoverCircleDescribe) {
            if(setLayoutVisibility(discoverCircleDescribe))
                bindData(discoverCircleDescribe);
        }

        private List<String> getList(List<CircleLabel> list){
            List<String> tempList = new ArrayList<>();
            for(CircleLabel label:list)
                tempList.add(label.getTagName());
            return tempList;
        }
    }

    /**
     * 圈子成员列表 ViewHolder
     */
    public class CircleMemberViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;
//        private List<DiscoverCircleMember> discoverCircleMemberList;
        @BindView(R.id.layout_section_common)
        LinearLayout layoutSectionCommon;
        @BindView(R.id.rl_discover_bar)
        RelativeLayout rlDiscoverBar;
        @BindView(R.id.rv_discover_hot_circle)
        RecyclerView rvDiscoverHotCircle;


        CircleMemberViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        private boolean setLayoutVisibility(List<DiscoverCircleMember> discoverCircleMemberList){
            rlDiscoverBar.setVisibility(View.GONE);
            if(discoverCircleMemberList == null || discoverCircleMemberList.size() == 0){
                layoutSectionCommon.setVisibility(View.GONE);
                return false;
            }
            layoutSectionCommon.setVisibility(View.VISIBLE);
            return true;
        }

        private void bindData(List<DiscoverCircleMember> list){
//            if(list == null || list.size() == 0)
//                return;
            for(DiscoverCircleMember member : list){
//                member = new DiscoverCircleMember();
                member.setSectionType(Constants.ONLY_PTICTURE);
            }
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rvDiscoverHotCircle.setLayoutManager(layoutManager);
            rvDiscoverHotCircle.setAdapter(new OneLineRecyclerAdapter(mContext, list){
                @Override
                public int getItemCount() {
                    return super.getItemCount() > maxPortaitSize ? maxPortaitSize:super.getItemCount();
                }
            });
        }

        public void setData(List<DiscoverCircleMember> discoverCircleMemberList) {
            if(setLayoutVisibility(discoverCircleMemberList))
                bindData(discoverCircleMemberList);
        }
    }

    /**
     * 圈子图书列表 ViewHolder
     */
    public class CircleBookViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;
        @BindView(R.id.layout_discover_circle_book)
        LinearLayout layoutDiscoverCircleBook;
        @BindView(R.id.rv_circle_book)
        RecyclerView gvCircleBook;

        CircleBookViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        private boolean setLayoutVisibility(List<ShelfBook> list){
            if(list == null || list.size() == 0){
                layoutDiscoverCircleBook.setVisibility(View.GONE);
                return false;
            }
            layoutDiscoverCircleBook.setVisibility(View.VISIBLE);
            return true;
        }

        private void bindData(List<ShelfBook> list){
            DiscoverCircleBookAdapter discoverCircleBookAdapter = new DiscoverCircleBookAdapter(mContext, list);
            LinearLayoutManager llm = new LinearLayoutManager(mContext);
            llm.setOrientation(LinearLayoutManager.HORIZONTAL);
            gvCircleBook.setLayoutManager(llm);
            gvCircleBook.setAdapter(discoverCircleBookAdapter);
        }

        void setData(List<ShelfBook> shelfBookList) {
            if(setLayoutVisibility(shelfBookList))
                bindData(shelfBookList);
        }
    }

    /**
     * 圈子详情话题列表 ViewHolder
     */
    public class CircleTopicViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;

        @BindView(R.id.rv_discover_topic)
        RecyclerView rvDiscoverTopic;
        @BindView(R.id.ll_discover_tab)
        LinearLayout llDiscoverTab;
		@BindView(R.id.layout_rv_discover_topic)
		LinearLayout layoutRVDiscoverTopic;
        CircleTopicViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);

        }

        private boolean setLayoutVisibility(List<DiscoverTopic> list){
            llDiscoverTab.setVisibility(View.GONE);
            if(list == null || list.size() == 0) {
                layoutRVDiscoverTopic.setVisibility(View.GONE);
                return false;
            }
            layoutRVDiscoverTopic.setVisibility(View.VISIBLE);
            return true;
        }

        private void bindData(List<DiscoverTopic> list){
            DiscoverTopicAdapter discoverTopicAdapter = new DiscoverTopicAdapter(mContext, list,false,true,discoverCircleDetail);
            rvDiscoverTopic.setAdapter(discoverTopicAdapter);
            GridLayoutManager manager = new GridLayoutManager(mContext, 1);
            rvDiscoverTopic.setLayoutManager(manager);
        }

        void setData(List<DiscoverTopic> discoverTopicList) {
            if(setLayoutVisibility(discoverTopicList)){
                bindData(discoverTopicList);
            }
        }
    }

}
