package com.iyangcong.reader.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.CommonVideo;
import com.iyangcong.reader.bean.DiscoverCooperationUnit;
import com.iyangcong.reader.bean.DiscoverReadPartyExercise;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/1/19.
 */

public class DiscoverCooperationUnitAdapter extends RecyclerView.Adapter {

    /**
     * 类型1：合作机构活动列表--使用RecyclerView实现
     */
    private static final int COOPERATION_PARTY_LV = 0;

    /**
     * 类型2：合作机构视频列表--使用GridView实现
     */
    private static final int COOPERATION_PARTY_VIDEO_GV = 1;


    /**
     * 当前类型
     */
    private int currentType = COOPERATION_PARTY_LV;

    private Context context;

    private LayoutInflater mLayoutInflater;

    private DiscoverCooperationUnit discoverCooperationUnit;

    public DiscoverCooperationUnitAdapter(Context context, DiscoverCooperationUnit discoverCooperationUnit) {
        this.context = context;
        this.discoverCooperationUnit = discoverCooperationUnit;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == COOPERATION_PARTY_LV) {
            return new CooperationPartyListViewHolder(context, mLayoutInflater.inflate(R.layout.rv_discover_cooperation_party_list, parent, false));
        } else if (viewType == COOPERATION_PARTY_VIDEO_GV) {
            return new CooperationPartyVideoListViewHolder(context, mLayoutInflater.inflate(R.layout.gv_read_party_vedio, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == COOPERATION_PARTY_LV) {
            CooperationPartyListViewHolder cooperationPartyListViewHolder = (CooperationPartyListViewHolder) holder;
            cooperationPartyListViewHolder.setData(discoverCooperationUnit.getDiscoverCooperationUnitActivityList(), discoverCooperationUnit.getUnitImg());
        } else if (getItemViewType(position) == COOPERATION_PARTY_VIDEO_GV) {
            CooperationPartyVideoListViewHolder cooperationPartyVideoListViewHolder = (CooperationPartyVideoListViewHolder) holder;
            cooperationPartyVideoListViewHolder.setData(discoverCooperationUnit.getCommonVideoList());
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case COOPERATION_PARTY_LV:
                currentType = COOPERATION_PARTY_LV;
                break;
            case COOPERATION_PARTY_VIDEO_GV:
                currentType = COOPERATION_PARTY_VIDEO_GV;
                break;
            default:
                break;
        }
        return currentType;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    /**
     * 读书会活动 ViewHolder
     */
    public class CooperationPartyListViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;
        @BindView(R.id.iv_cooperation_banner)
        ImageView ivCooperationBanner;
        @BindView(R.id.rv_cooperation_party_list)
        RecyclerView rvCooperationPartyList;

        CooperationPartyListViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        void setData(final List<DiscoverReadPartyExercise> discoverReadPartyActivityList, String ivCooperationBanner) {
            DiscoverReadPartyListAdapter discoverReadPartyListAdapter = new DiscoverReadPartyListAdapter(mContext, discoverReadPartyActivityList,-1);
            rvCooperationPartyList.setAdapter(discoverReadPartyListAdapter);
            GridLayoutManager manager = new GridLayoutManager(mContext, 1);
            rvCooperationPartyList.setLayoutManager(manager);
        }
    }

    /**
     * 读书会视频 ViewHolder
     */
    public class CooperationPartyVideoListViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;

        @BindView(R.id.iv_more_divide)
        ImageView ivMoreDivide;
        @BindView(R.id.ll_see_more)
        LinearLayout llSeeMore;
        @BindView(R.id.iv_ring)
        ImageView ivRing;
        @BindView(R.id.tv_bar_title)
        TextView tvBarTitle;
        @BindView(R.id.tv_more)
        TextView tvMore;
        @BindView(R.id.iv_more)
        ImageView ivMore;
        @BindView(R.id.iv_bar_divide)
        ImageView ivBarDivide;
        @BindView(R.id.rl_discover_bar)
        RelativeLayout rlDiscoverBar;
        @BindView(R.id.gv_discover_read_party_vedio)
        GridView gvDiscoverReadPartyVedio;

        CooperationPartyVideoListViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        void setData(final List<CommonVideo> commonVedioList) {
            DiscoverReadPartyVedioAdapter discoverReadPartyVedioAdapter = new DiscoverReadPartyVedioAdapter(mContext, commonVedioList);
            gvDiscoverReadPartyVedio.setAdapter(discoverReadPartyVedioAdapter);
        }
    }
}
