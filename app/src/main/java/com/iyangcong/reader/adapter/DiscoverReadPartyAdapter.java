package com.iyangcong.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Outline;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.DiscoverReadPartyVideoListActivity;
import com.iyangcong.reader.activity.DiscoverReadingPartyDetailsActivity;
import com.iyangcong.reader.activity.ReadingPartyListAcitivity;
import com.iyangcong.reader.activity.VideoPlayerActivity;
import com.iyangcong.reader.bean.CommonBanner;
import com.iyangcong.reader.bean.CommonVideo;
import com.iyangcong.reader.bean.DiscoverReadParty;
import com.iyangcong.reader.bean.DiscoverReadPartyExercise;
import com.iyangcong.reader.bean.Orgernization;
import com.iyangcong.reader.utils.BannerImageLoader;
import com.iyangcong.reader.utils.Constants;
import com.orhanobut.logger.Logger;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerClickListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ljw on 2017/1/4.
 */

public class DiscoverReadPartyAdapter extends RecyclerView.Adapter {

    /**
     * 类型1：读书会轮播图
     */
    private static final int READ_PARTY_BANNER = 0;

    /**
     * 类型2：读书会列表--使用ListView实现
     */
    private static final int READ_PARTY_LV = 1;

    /**
     * 类型2：读书会视频列表--使用GridView实现
     */
    private static final int READ_PARTY_GV = 2;

    /**
     * 类型2：合作机构--RecyclerView
     */
    private static final int READ_PARTY_COOPERATION_GV = 3;


    /**
     * 当前类型
     */
    private int currentType = READ_PARTY_BANNER;

    private Context context;

    /**
     * 使用mLayoutInflater来初始化布局
     */
    private LayoutInflater mLayoutInflater;

    private DiscoverReadParty discoverReadParty;

    public DiscoverReadPartyAdapter(Context context, DiscoverReadParty discoverReadParty) {
        this.context = context;
        this.discoverReadParty = discoverReadParty;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == READ_PARTY_BANNER) {
            return new ReadPartyBannerViewHolder(context, mLayoutInflater.inflate(R.layout.banner_common, null));
        } else if (viewType == READ_PARTY_LV) {
            return new ReadPartyListViewHolder(context, mLayoutInflater.inflate(R.layout.rv_read_party_list, parent, false));
        } else if (viewType == READ_PARTY_GV) {
            return new ReadPartyVedioListViewHolder(context, mLayoutInflater.inflate(R.layout.gv_read_party_vedio, null));
        } else if (viewType == READ_PARTY_COOPERATION_GV) {
            return new ReadPartyCooperationUnitListViewHolder(context, mLayoutInflater.inflate(R.layout.rv_read_party_cooperation_list, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == READ_PARTY_BANNER) {
            ReadPartyBannerViewHolder readPartyBannerViewHolder = (ReadPartyBannerViewHolder) holder;
            readPartyBannerViewHolder.setData(discoverReadParty.getCommonBannerList());
        } else if (getItemViewType(position) == READ_PARTY_LV) {
            ReadPartyListViewHolder readPartyListViewHolder = (ReadPartyListViewHolder) holder;
            readPartyListViewHolder.setData(discoverReadParty.getDiscoverReadPartyActivityList());
        } else if (getItemViewType(position) == READ_PARTY_GV) {
            ReadPartyVedioListViewHolder readPartyVedioListViewHolder = (ReadPartyVedioListViewHolder) holder;
            readPartyVedioListViewHolder.setData(discoverReadParty.getCommonVedioList());
        } else if (getItemViewType(position) == READ_PARTY_COOPERATION_GV) {
            ReadPartyCooperationUnitListViewHolder readPartyCooperationUnitListViewHolder = (ReadPartyCooperationUnitListViewHolder) holder;
            readPartyCooperationUnitListViewHolder.bindData(discoverReadParty.getCooperationUnitList());
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case READ_PARTY_BANNER:
                currentType = READ_PARTY_BANNER;
                break;
            case READ_PARTY_LV:
                currentType = READ_PARTY_LV;
                break;
            case READ_PARTY_GV:
                currentType = READ_PARTY_GV;
                break;
            case READ_PARTY_COOPERATION_GV:
                currentType = READ_PARTY_COOPERATION_GV;
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
     * 轮播图 ViewHolder
     */
    class ReadPartyBannerViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;
        private List<CommonBanner> bannerList;
        private List<String> imageUrls = new ArrayList<String>();
        @BindView(R.id.banner)
        Banner banner;

        ReadPartyBannerViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        void setData(final List<CommonBanner> bannerList) {
            if(bannerList ==  null || bannerList.size() == 0){
                banner.setVisibility(View.GONE);
                return;
            }
            banner.setVisibility(View.VISIBLE);
            imageUrls.clear();
            //设置Banner的数据
            //得到图片地址的集合
            this.bannerList = bannerList;
            for (int i = 0; i < bannerList.size(); i++) {
                String image = bannerList.get(i).getBannerUrl();
                imageUrls.add(image);
            }
            //banner指示器显示在右边
            banner.setIndicatorGravity(BannerConfig.CENTER);
            //新版的banner的使用----偷下懒的使用方法
            banner.setImages(imageUrls).setImageLoader(new BannerImageLoader()).start();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //getBannerView添加圆角轮廓，否则在滑动过程中会变成直角
                banner.setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 30);
                    }
                });
                banner.setClipToOutline(true);
            }
            //设置item的点击事件
            banner.setOnBannerClickListener(new OnBannerClickListener() {
                @Override
                public void OnBannerClick(int position) {
                    //注意这里的position是从1开始的
//                    Toast.makeText(mContext, "position==" + position, Toast.LENGTH_SHORT).show();
                    CommonBanner commonBanner = (CommonBanner) bannerList.get(position - 1);
                    Intent intent = new Intent(mContext, DiscoverReadingPartyDetailsActivity.class);
                    intent.putExtra(Constants.readingPartyId, commonBanner.getId());
                    intent.putExtra(Constants.readingPartyTitle, commonBanner.getTitle());
                    mContext.startActivity(intent);
                }
            });

        }
    }

    /**
     * 读书会活动 ViewHolder
     */
    class ReadPartyListViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;
        @BindView(R.id.rv_read_party_list)
        RecyclerView rvReadPartyList;

        ReadPartyListViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        void setData(List<DiscoverReadPartyExercise> discoverReadPartyActivityList) {
            if(discoverReadPartyActivityList == null || discoverReadPartyActivityList.size() == 0){
                rvReadPartyList.setVisibility(View.GONE);
                return;
            }
            rvReadPartyList.setVisibility(View.VISIBLE);
            DiscoverReadPartyListAdapter discoverReadPartyListAdapter = new DiscoverReadPartyListAdapter(mContext, discoverReadPartyActivityList,4);
            rvReadPartyList.setAdapter(discoverReadPartyListAdapter);
            GridLayoutManager manager = new GridLayoutManager(mContext, 1);
            rvReadPartyList.setLayoutManager(manager);
        }
    }

    //把活动列表种icComing为false的项删除
    private List<DiscoverReadPartyExercise> getCommingList(List<DiscoverReadPartyExercise> discoverReadPartyActivityList){
        List<DiscoverReadPartyExercise> tempList = new ArrayList<>(discoverReadPartyActivityList);
        Iterator<DiscoverReadPartyExercise> iterator = tempList.iterator();
        while (iterator.hasNext()){
            DiscoverReadPartyExercise next = iterator.next();
            if(!next.isComing())
                iterator.remove();
        }
        return tempList;
    }

    /**
     * 读书会视频 ViewHolder
     */
    class ReadPartyVedioListViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;
        @BindView(R.id.layout_more_activity)
        LinearLayout layoutMoreAcitivity;
        @BindView(R.id.layout_read_party_video)
        LinearLayout layoutReadPartyVideo;
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

        ReadPartyVedioListViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.ll_see_more,R.id.tv_more})
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ll_see_more:
                    Intent intent = new Intent(mContext, ReadingPartyListAcitivity.class);
                    mContext.startActivity(intent);
                    break;
                case R.id.tv_more:
                    Intent intent1 = new Intent(mContext, DiscoverReadPartyVideoListActivity.class);
                    mContext.startActivity(intent1);
                    break;
            }
        }
        //设置更多按钮的layout是显示或者不显示，如果活动列表中有isComing的活动，则显示，否则不显示。
        private void setMoreAcitivityLayoutVisibility(){
            int comingListSize = discoverReadParty.getDiscoverReadPartyActivityList().size();
//            int comingListSize = getCommingList(discoverReadParty.getDiscoverReadPartyActivityList()).size();
            if( comingListSize == 0){
                layoutMoreAcitivity.setVisibility(View.GONE);
            }else{
                layoutMoreAcitivity.setVisibility(View.VISIBLE);
            }
        }

        void setData(List<CommonVideo> commonVedioList) {
            setMoreAcitivityLayoutVisibility();

            if(commonVedioList == null && commonVedioList.size() == 0){
                layoutReadPartyVideo.setVisibility(View.GONE);
                return;
            }
            layoutReadPartyVideo.setVisibility(View.VISIBLE);
            layoutReadPartyVideo.setTag(commonVedioList);
            DiscoverReadPartyVedioAdapter discoverReadPartyVedioAdapter = new DiscoverReadPartyVedioAdapter(mContext, commonVedioList){
                @Override
                public int getCount() {
                    return super.getCount()>4?4:super.getCount();
                }
            };
            gvDiscoverReadPartyVedio.setAdapter(discoverReadPartyVedioAdapter);
            gvDiscoverReadPartyVedio.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //TODO点击某个视频以后跳转，播放视频
                    List<CommonVideo> tempList = (List<CommonVideo>)layoutReadPartyVideo.getTag();
                    if(tempList != null && tempList.size() != 0){
                        Intent intent = new Intent(mContext, VideoPlayerActivity.class);
                        Logger.i("videoTitle:"+tempList.get(i).getVedioTitle()+"\nvideoAddress:"+tempList.get(i).getVideoUrl());
                        intent.putExtra(Constants.VIDEO_TITLE,"视频");
                        intent.putExtra(Constants.VIDEO_ADDRESS,tempList.get(i).getVideoUrl());
                        intent.putExtra(Constants.VIDEO_COVER,tempList.get(i).getVideoImage());
                        mContext.startActivity(intent);
                    }
                }
            });
            tvBarTitle.setText("精彩回顾");
        }
    }

    /**
     * 合作机构 ViewHolder
     */
    class ReadPartyCooperationUnitListViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;
        @BindView(R.id.tv_bar_title)
        TextView tvBarTitle;
        @BindView(R.id.rv_cooperation_list)
        RecyclerView rvCooperationList;
        @BindView(R.id.rl_discover_bar)
        RelativeLayout rlDiscoverBar;
        ReadPartyCooperationUnitListViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        public void bindData(List<Orgernization> cooperationUnitList) {
            if(cooperationUnitList == null || cooperationUnitList.size() == 0){
                rlDiscoverBar.setVisibility(View.GONE);
                return;
            }
            rlDiscoverBar.setVisibility(View.VISIBLE);
            tvBarTitle.setText("合作机构");
            rvCooperationList.setLayoutManager(new GridLayoutManager(mContext, 2));
            rvCooperationList.setAdapter(new ReadPartyCooperationListAdapter(getStringList(cooperationUnitList), mContext));
        }

        private List<String> getStringList(List<Orgernization> cooperationUnitList) {
            List<String> tempList = new ArrayList<>();
            for (Orgernization or : cooperationUnitList) {
                tempList.add(or.getOrgernizationName());
            }
            return tempList;
        }
    }

}
