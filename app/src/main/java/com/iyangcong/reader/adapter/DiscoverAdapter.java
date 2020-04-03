package com.iyangcong.reader.adapter;
/**
 * Created by ljw on 2016/12/21.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Outline;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.DiscoverCircleActivity;
import com.iyangcong.reader.activity.DiscoverCircleDetailActivity;
import com.iyangcong.reader.activity.DiscoverReadingPartyActivity;
import com.iyangcong.reader.activity.DiscoverReadingPartyDetailsActivity;
import com.iyangcong.reader.bean.CommonBanner;
import com.iyangcong.reader.bean.CommonBroadcast;
import com.iyangcong.reader.bean.CommonSection;
import com.iyangcong.reader.bean.DiscoverContent;
import com.iyangcong.reader.interfaceset.OnTabChangedListener;
import com.iyangcong.reader.ui.CustomBanner;
import com.iyangcong.reader.ui.customtablayout.CommonTabLayout;
import com.iyangcong.reader.ui.customtablayout.listener.CustomTabEntity;
import com.iyangcong.reader.ui.customtablayout.listener.OnTabSelectListener;
import com.iyangcong.reader.ui.customtablayout.listener.TabEntity;
import com.iyangcong.reader.ui.textview.MarqueeView;
import com.iyangcong.reader.utils.BannerImageLoader;
import com.iyangcong.reader.utils.Constants;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 发现页面布局适配器
 */
public class DiscoverAdapter extends RecyclerView.Adapter {

    /**
     * 类型1：发现 轮播图
     */
    private static final int DISCOVER_BANNER = 0;

    /**
     * 类型2：发现页广播
     */
    private static final int BOOK_BROADCAST = 1;

    /**
     * 类型2：热门圈子--使用GridView实现
     */
    private static final int HOT_CIRCLE_GV = 2;

    /**
     * 类型3：话题
     */
    private static final int TOPIC_LV = 3;


    /**
     * 当前类型
     */
    private int currentType = DISCOVER_BANNER;

    private Context context;

    /**
     * 使用mLayoutInflater来初始化布局
     */
    private LayoutInflater mLayoutInflater;


    private DiscoverContent discoverContent;
    private String selectedTab = "";

    public String getSelectedTab() {
        return selectedTab;
    }

//    private OnHotCircleSelected hotCircleSelected;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
//    private PtrClassicFrameLayout discoverPtrClassicFrameLayout;
    private OnTabChangedListener onTabChangedListener;

    public DiscoverAdapter(Context context, DiscoverContent discoverContent, String selectedTab/*, PtrClassicFrameLayout discoverPtrClassicFrameLayout*/) {
        this.context = context;
        this.discoverContent = discoverContent;
        mLayoutInflater = LayoutInflater.from(context);
        this.selectedTab = selectedTab;
//        this.discoverPtrClassicFrameLayout = discoverPtrClassicFrameLayout;
    }

    /**
     * 创建ViewHolder布局
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == DISCOVER_BANNER) {
            return new DiscoverBannerViewHolder(context, mLayoutInflater.inflate(R.layout.banner_common, null));
        } else if (viewType == BOOK_BROADCAST) {
            return new DiscoverBrocastViewHolder(context, mLayoutInflater.inflate(R.layout.ll_discover_brocast, parent, false));
        } else if (viewType == HOT_CIRCLE_GV) {
            return new DiscoverHotCircleViewHolder(context, mLayoutInflater.inflate(R.layout.rv_section_common, null));
        } else if (viewType == TOPIC_LV) {
            return new DiscoverTopicViewHolder(context, mLayoutInflater.inflate(R.layout.rv_discover_topic, null));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == DISCOVER_BANNER) {
            DiscoverBannerViewHolder discoverBannerViewHolder = (DiscoverBannerViewHolder) holder;
            discoverBannerViewHolder.setData(discoverContent.getDiscoverBannerList());
        } else if (getItemViewType(position) == BOOK_BROADCAST) {
            DiscoverBrocastViewHolder discoverBrocastViewHolder = (DiscoverBrocastViewHolder) holder;
            discoverBrocastViewHolder.setData(discoverContent.getDiscoverBroadcastList());
        } else if (getItemViewType(position) == HOT_CIRCLE_GV) {
            DiscoverHotCircleViewHolder discoverHotCircleViewHolder = (DiscoverHotCircleViewHolder) holder;
            discoverHotCircleViewHolder.setData(discoverContent.getDiscoverSectionList(), position);
        } else if (getItemViewType(position) == TOPIC_LV) {
            DiscoverTopicViewHolder discoverTopicViewHolder = (DiscoverTopicViewHolder) holder;
            discoverTopicViewHolder.setData(selectedTab);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case DISCOVER_BANNER:
                currentType = DISCOVER_BANNER;
                break;
            case BOOK_BROADCAST:
                currentType = BOOK_BROADCAST;
                break;
            case HOT_CIRCLE_GV:
                currentType = HOT_CIRCLE_GV;
                break;
            case TOPIC_LV:
                currentType = TOPIC_LV;
                break;
            default:
                break;
        }
        return currentType;
    }


    /**
     * 轮播图 ViewHolder
     */
    public class DiscoverBannerViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;
        private List<CommonBanner> bannerList;
        private List<String> imageUrls = new ArrayList<String>();
        @BindView(R.id.banner)
        CustomBanner banner;
        @BindView(R.id.layout_banner)
        RelativeLayout layoutBanner;

        DiscoverBannerViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        public void setItemVisibility(int visibility) {
            layoutBanner.setVisibility(visibility);
            banner.setVisibility(visibility);
        }

        void setData(final List<CommonBanner> bannerList) {
//            banner.setParentView(discoverPtrClassicFrameLayout);
            if (bannerList == null || bannerList.size() == 0) {
                setItemVisibility(View.GONE);
                return;
            }
            setItemVisibility(View.VISIBLE);
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
            banner.setDelayTime(3000);
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
            //新版的banner的使用----偷下懒的使用方法
            banner.setImages(imageUrls).setImageLoader(new BannerImageLoader()).start();

            //设置item的点击事件
            banner.setOnBannerClickListener(new OnBannerClickListener() {
                @Override
                public void OnBannerClick(int position) {
                    CommonBanner banner = bannerList.get(position - 1);
                    Intent intent;
                    switch (banner.getBannerType()) {
//                        case 0:
//
//                            break;
//                        case 1:
//
//                            break;
//                        case 2:
//
//                            break;
                        case 3:
                            intent = new Intent(context, DiscoverCircleDetailActivity.class);
                            intent.putExtra(Constants.circleId, banner.getId());
                            intent.putExtra(Constants.circleName, banner.getTitle());
                            context.startActivity(intent);
                            break;
                        case 4:
                            intent = new Intent(context, DiscoverReadingPartyDetailsActivity.class);
                            intent.putExtra(Constants.readingPartyId, banner.getId());
                            intent.putExtra(Constants.readingPartyTitle, banner.getTitle());
                            context.startActivity(intent);
                            break;
                    }

                    //注意这里的position是从1开始的
//                    Toast.makeText(mContext, "position==" + position, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public class DiscoverBrocastViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;

        @BindView(R.id.iv_text_ad)
        ImageView ivTextAd;
        @BindView(R.id.tv_text_ad)
        MarqueeView tvTextAd;
        //        @BindView(R.id.tv_text_ad2)
//        TextView tvTextAd2;
        @BindView(R.id.ll_text_ad)
        LinearLayout llTextAd;

        public DiscoverBrocastViewHolder(Context context, View inflate) {
            super(inflate);
            this.mContext = context;
            ButterKnife.bind(this, inflate);
        }

        @OnClick({R.id.ll_text_ad, R.id.iv_text_ad, R.id.tv_text_ad})
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_text_ad:
//                    CommonBroadcast broadcast = (CommonBroadcast)llTextAd.getTag();
//                    if(broadcast != null){
                    Intent intent = new Intent(mContext, DiscoverReadingPartyActivity.class);
//                        intent.putExtra(Constants.readingPartyId,broadcast.getId());
                    intent.putExtra(Constants.readingPartyTitle, mContext.getString(R.string.title_reading_party));
                    mContext.startActivity(intent);
                    break;
//                    }
            }
        }


        void setData(List<CommonBroadcast> list) {
            if (list == null || list.size() == 0) {
                llTextAd.setVisibility(View.GONE);
                return;
            }
            llTextAd.setVisibility(View.VISIBLE);
            tvTextAd.startWithList(list);
            llTextAd.setTag(list);
            tvTextAd.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
                @Override
                public void onItemClick(int position, TextView textView) {
                    List<CommonBroadcast> broadcastList = (List<CommonBroadcast>) llTextAd.getTag();
                    if (position >= broadcastList.size())
                        return;
                    CommonBroadcast broadcast = broadcastList.get(position);
                    if (broadcast != null) {
                        Intent intent = new Intent(mContext, DiscoverReadingPartyDetailsActivity.class);
                        intent.putExtra(Constants.readingPartyId, broadcast.getId());
                        intent.putExtra(Constants.readingPartyTitle, broadcast.getBroadcastTitle());
                        context.startActivity(intent);
                    }
                }
            });
//            Handler handler = new Handler(){
//                @Override
//                public void handleMessage(Message msg) {
//                    super.handleMessage(msg);
//                    RotateAnimation ma2 = new RotateAnimation(0.0f,90.0f);
//                    ma2.setDuration(800);
//                    ma2.setFillAfter(true);
//                    tvTextAd2.startAnimation(ma2);
//
//                    RotateAnimation ma1 = new RotateAnimation(-90.0f,0.0f);
//                    ma1.setDuration(800);
//                    ma1.setFillAfter(true);
//                    tvTextAd.startAnimation(ma1);
//                    this.sendEmptyMessageDelayed(1,2000);
//                }
//            };
//            handler.sendEmptyMessageDelayed(1,2000);
        }

    }

    /**
     * 分类 ViewHolder
     */
    public class DiscoverHotCircleViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;

        @BindView(R.id.rl_discover_bar)
        RelativeLayout rlDiscoverBar;
        @BindView(R.id.tv_more)
        TextView tvMore;
        @BindView(R.id.tv_bar_title)
        TextView tvBarTitle;
        @BindView(R.id.iv_more)
        ImageView ivMore;
        @BindView(R.id.rv_discover_hot_circle)
        RecyclerView rvDiscoverHotCircle;

        @OnClick(R.id.rl_discover_bar)
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.rl_discover_bar:
                    context.startActivity(new Intent(mContext, DiscoverCircleActivity.class));
                    break;
            }
        }

        DiscoverHotCircleViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
            TextPaint paint = tvBarTitle.getPaint();
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(1);
        }

        void setData(List<CommonSection> sectionList, int position) {
            if (sectionList == null || sectionList.size() == 0) {
                rlDiscoverBar.setVisibility(View.GONE);
                return;
            }
            rlDiscoverBar.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rvDiscoverHotCircle.setLayoutManager(layoutManager);
            rvDiscoverHotCircle.setAdapter(new OneLineRecyclerAdapter(mContext, sectionList));
            rlDiscoverBar.setVisibility(View.VISIBLE);
        }
    }

    public class DiscoverTopicViewHolder extends RecyclerView.ViewHolder {
        private final Context mContext;

        @BindView(R.id.rv_discover_topic)
        RecyclerView rvDiscoverTopic;
        @BindView(R.id.tabs)
        CommonTabLayout tabLayout;
        @BindView(R.id.layout_rv_discover_topic)
        LinearLayout layoutRvDiscoverTopic;
        @BindView(R.id.ll_discover_tab)
        LinearLayout llDiscoverTab;


        DiscoverTopicViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
            initTabLayout();
        }

        public void setData(String selectedTab) {
            if (discoverContent.getDiscoverReviewList() == null || discoverContent.getDiscoverReviewList().size() == 0) {
                setViewVisibility(View.GONE);
                return;
            }
            setViewVisibility(View.VISIBLE);
            if (selectedTab.equals(mContext.getResources().getString(R.string.discover_review))) {
                DiscoverReviewAdapter reviewAdapter = new DiscoverReviewAdapter(mContext, discoverContent.getDiscoverReviewList(), false);
                rvDiscoverTopic.setAdapter(reviewAdapter);
            } else {
                DiscoverTopicAdapter discoverTopicAdapter = new DiscoverTopicAdapter(mContext, discoverContent.getDiscoverTopicList(),false);
                rvDiscoverTopic.setAdapter(discoverTopicAdapter);
            }
            GridLayoutManager manager = new GridLayoutManager(mContext, 1);
            rvDiscoverTopic.setLayoutManager(manager);
        }

        private void setViewVisibility(int visibility) {
            rvDiscoverTopic.setVisibility(visibility);
            tabLayout.setVisibility(visibility);
            layoutRvDiscoverTopic.setVisibility(visibility);
            llDiscoverTab.setVisibility(visibility);
        }

        private void initTabLayout() {
            mTabEntities.add(new TabEntity(mContext.getResources().getString(R.string.discover_review), R.drawable.shucheng_on, R.drawable.shucheng_off));
            mTabEntities.add(new TabEntity(mContext.getResources().getString(R.string.discover_topic), R.drawable.shucheng_on, R.drawable.shucheng_off));
            tabLayout.setTabData(mTabEntities);

            tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelect(int position) {
                    setData(mTabEntities.get(position).getTabTitle());
                    selectedTab = mTabEntities.get(position).getTabTitle();
                    if (onTabChangedListener != null) {
                        onTabChangedListener.onTabChanged(selectedTab);
                    }
                }

                @Override
                public void onTabReselect(int position) {
//                    if (hotCircleSelected != null)
//                        hotCircleSelected.onCircleSelected(position);
                }
            });
        }
    }

    public OnTabChangedListener getOnTabChangedListener() {
        return onTabChangedListener;
    }

    public void setOnTabChangedListener(OnTabChangedListener onTabChangedListener) {
        this.onTabChangedListener = onTabChangedListener;
    }

//    public interface OnHotCircleSelected {
//        void onCircleSelected(int selectedId);
//    }
}
