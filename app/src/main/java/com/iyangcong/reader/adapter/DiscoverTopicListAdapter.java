package com.iyangcong.reader.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.DiscoverCircleDescribe;
import com.iyangcong.reader.bean.DiscoverCircleDetail;
import com.iyangcong.reader.bean.DiscoverTopic;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lg on 2017/1/13.
 */

public class DiscoverTopicListAdapter extends RecyclerView.Adapter {

    private static final int TopicListHead = 0;
    private static final int TopicListContent = 1;
    private Context mContext;
    private int currentType = TopicListHead;
    private LayoutInflater mLayoutInflater;

    private DiscoverCircleDetail discoverCircleDetail;

    public DiscoverTopicListAdapter(Context mContext, DiscoverCircleDetail discoverCircleDetail) {
        this.mContext = mContext;
        this.discoverCircleDetail = discoverCircleDetail;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TopicListHead) {
            return new HeadViewHolder(mContext, mLayoutInflater.inflate(R.layout.item_discover_topic_list_head, parent, false));
        } else if (viewType == TopicListContent) {
            return new CircleTopicViewHolder(mContext, mLayoutInflater.inflate(R.layout.rv_discover_topic, null));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TopicListHead) {
            HeadViewHolder headViewHolder = (HeadViewHolder) holder;
            headViewHolder.setData(null/*discoverCircleDetail.getDiscoverCircleDescribe()*/);
        } else if (getItemViewType(position) == TopicListContent) {
            CircleTopicViewHolder circleTopicViewHolder = (CircleTopicViewHolder) holder;
            circleTopicViewHolder.setData(/*discoverCircleDetail.getDiscoverTopicList()*/null);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case TopicListHead:
                currentType = TopicListHead;
                break;
            case TopicListContent:
                currentType = TopicListContent;
                break;
        }
        return currentType;
    }


    /**
     * 话题头部 ViewHolder
     */
    public class HeadViewHolder extends RecyclerView.ViewHolder {
        private final Context mContext;
        private DiscoverCircleDescribe discoverCircleDescribe;
        @BindView(R.id.civ_topic_head_img)
        ImageView civTopicHeadImg;
        @BindView(R.id.tv_topic_title)
        TextView tvTopicTitle;
        @BindView(R.id.tv_topic_member)
        TextView tvTopicMember;
        @BindView(R.id.tv_topic_num)
        TextView tvTopicNum;

        public HeadViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        void setData(final DiscoverCircleDescribe discoverCircleDescribe) {

        }
    }


    /**
     * 话题列表 ViewHolder
     */
    public class CircleTopicViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;
        private List<DiscoverTopic> discoverTopicList;

        @BindView(R.id.rv_discover_topic)
        RecyclerView rvDiscoverTopic;
        @BindView(R.id.ll_discover_tab)
        LinearLayout llDiscoverTab;

        CircleTopicViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        void setData(final List<DiscoverTopic> discoverTopicList) {
            llDiscoverTab.setVisibility(View.GONE);
            this.discoverTopicList = new ArrayList<DiscoverTopic>();
            for (int i = 0; i < 5; i++) {
                DiscoverTopic discoverTopic = new DiscoverTopic();
                this.discoverTopicList.add(discoverTopic);
            }
            DiscoverTopicAdapter discoverTopicAdapter = new DiscoverTopicAdapter(mContext, this.discoverTopicList,false);
            rvDiscoverTopic.setAdapter(discoverTopicAdapter);
            GridLayoutManager manager = new GridLayoutManager(mContext, 1);
            rvDiscoverTopic.setLayoutManager(manager);
        }
    }
}
