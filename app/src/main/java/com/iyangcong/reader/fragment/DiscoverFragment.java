package com.iyangcong.reader.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.DiscoverAdapter;
import com.iyangcong.reader.base.BaseFragment;
import com.iyangcong.reader.bean.CircleCommonSection;
import com.iyangcong.reader.bean.CommonBanner;
import com.iyangcong.reader.bean.CommonBroadcast;
import com.iyangcong.reader.bean.CommonSection;
import com.iyangcong.reader.bean.DiscoverContent;
import com.iyangcong.reader.bean.DiscoverReviews;
import com.iyangcong.reader.bean.DiscoverTopic;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.event.SlideEvent;
import com.iyangcong.reader.interfaceset.OnTabChangedListener;
import com.iyangcong.reader.model.HotGroupResponse;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.CustomPtrClassicFrameLayout;
import com.iyangcong.reader.ui.ninegridview.NineGridView;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.LoadCountHolder;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NetUtil.NETWORK_MOBILE;
import static com.iyangcong.reader.utils.NetUtil.NETWORK_WIFI;


/**
 * 发现
 */
public class DiscoverFragment extends BaseFragment {

    @BindView(R.id.rv_discover)
    RecyclerView rvDiscover;
    @BindView(R.id.discover_ptrClassicFrameLayout)
    CustomPtrClassicFrameLayout discoverPtrClassicFrameLayout;

    private DiscoverAdapter discoverAdapter;
    private RecyclerAdapterWithHF mAdapter;
    Handler handler = new Handler();
    private DiscoverContent discoverContent;
    private LoadCountHolder holderForTopic = new LoadCountHolder() {
        @Override
        public void refresh() {
            setRefresh(true);
            setPage(1);
        }
    }, holderForReview = new LoadCountHolder() {
        @Override
        public void refresh() {
            setRefresh(true);
            setPage(1);
        }
    };

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        ButterKnife.bind(this, view);
        initVaryViewHelper(mContext, discoverPtrClassicFrameLayout, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getNetworkState(mContext) == NETWORK_MOBILE || getNetworkState(mContext) == NETWORK_WIFI) {
                    showDataView();
                    refreshAuto(discoverPtrClassicFrameLayout);
                } else {
                    showErrorView();
                }
            }
        });
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        refreshAuto(discoverPtrClassicFrameLayout);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            long userId = -1;
            if(CommonUtil.getLoginState()){
                userId = CommonUtil.getUserId();
            }
            getHotGroup(userId, 7);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReturnToTop(SlideEvent event){
        if(isVisible()&&rvDiscover!=null)
            rvDiscover.scrollToPosition(0);
    }
    @Override
    protected void initData() {
        NineGridView.setImageLoader(new GlideImageLoader());
        discoverContent = new DiscoverContent();
        List<CommonBanner> bannerList = new ArrayList<CommonBanner>();
        discoverContent.setDiscoverBannerList(bannerList);

        List<CommonSection> sectionList = new ArrayList<CommonSection>();
        discoverContent.setDiscoverSectionList(sectionList);

        List<CommonBroadcast> commonBroadcastList = new ArrayList<>();
        discoverContent.setDiscoverBroadcastList(commonBroadcastList);

        List<DiscoverTopic> discoverTopicList = new ArrayList<DiscoverTopic>();
        discoverContent.setDiscoverTopicList(discoverTopicList);

        List<DiscoverReviews> discoverReviewses = new ArrayList<>();
        discoverContent.setDiscoverReviewList(discoverReviewses);

        discoverAdapter = new DiscoverAdapter(mContext, discoverContent, mContext.getResources().getString(R.string.discover_review)/*, discoverPtrClassicFrameLayout*/);
        discoverAdapter.setOnTabChangedListener(new OnTabChangedListener() {
            @Override
            public void onTabChanged(String selectedTab) {
                //**选中的是"读后感"
                if (selectedTab.equals(mContext.getResources().getString(R.string.discover_review))) {
                    holderForReview.setCanLoadMore(true);
                    discoverPtrClassicFrameLayout.setLoadMoreEnable(true);
                }
                //**选中的是"话题"
                if (selectedTab.equals(mContext.getResources().getString(R.string.discover_topic))) {
                    holderForTopic.setCanLoadMore(true);
                    discoverPtrClassicFrameLayout.setLoadMoreEnable(true);
                }
            }
        });
        mAdapter = new RecyclerAdapterWithHF(discoverAdapter);
        //recycleView不仅要设置适配器还要设置布局管理者,否则图片不显示
        //第一个参数是上下文，第二个参数是只有一列
        rvDiscover.setLayoutManager(new LinearLayoutManager(mContext));
        rvDiscover.setAdapter(mAdapter);

//        refreshHandle();
        discoverPtrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refreshHandle();
            }
        });

        discoverPtrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void loadMore() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadMoreHandle();
                    }
                }, 0);
            }
        });
        refreshAuto(discoverPtrClassicFrameLayout);
    }


    private void loadMoreHandle() {
        Log.i("loadMoreTag", discoverAdapter.getSelectedTab());
        if (discoverAdapter.getSelectedTab().equals(mContext.getResources().getString(R.string.discover_topic))) {
            holderForTopic.loadMore();
            loadTopics(10);
        } else if (discoverAdapter.getSelectedTab().equals(mContext.getResources().getString(R.string.discover_review))) {
            holderForReview.loadMore();
            loadReviews(10);
        }
    }

    private void refreshHandle() {
        holderForTopic.refresh();
        holderForReview.refresh();
        long userId = -1;
        if(CommonUtil.getLoginState()){
            userId = CommonUtil.getUserId();
        }
        getDatasFromNetwork(userId, 7);
    }

    /**
     * 获取网络数据
     * 该方法的内网络请求不会遇到加载更多的情况，于是写到一起。
     */

    private void getDatasFromNetwork(long userId, int num) {
        getHotGroup(userId, num);

        OkGo.get(Urls.BroadcastURL)
                .params("broadcastPosition", "2")
                .execute(new JsonCallback<IycResponse<List<CommonBroadcast>>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<List<CommonBroadcast>> listIycResponse, Call call, Response response) {
                        discoverContent.setDiscoverBroadcastList(listIycResponse.getData());
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }
                });

        OkGo.get(Urls.BannerURL)
                .params("bannerPosition", "2")
                .execute(new JsonCallback<IycResponse<List<CommonBanner>>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<List<CommonBanner>> listIycResponse, Call call, Response response) {

                        discoverContent.setDiscoverBannerList(listIycResponse.getData());
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }
                });

        loadTopics(10);
        loadReviews(10);
    }

    private void getHotGroup(long userId, int num) {
        OkGo.get(Urls.HotGroupURL)
                .params("num", num)
                .params("userId", userId)
                .execute(new JsonCallback<IycResponse<List<HotGroupResponse>>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<List<HotGroupResponse>> hotGroupResponseIycResponse, Call call, Response response) {

                        List<CommonSection> commonSectionList = new ArrayList<CommonSection>();
                        CircleCommonSection section = null;
                        for (HotGroupResponse hgr : hotGroupResponseIycResponse.getData()) {
                            section = new CircleCommonSection();
                            section.setSectionId(hgr.getGroupid());
                            section.setSectionName(hgr.getGroupname());
                            section.setSectionImageUrl(hgr.getCover());
                            section.setClassGroup(hgr.isClassGroup());
                            section.setSectionType(Constants.PITCUTE_AND_TEXT);
                            commonSectionList.add(section);
                        }
                        discoverContent.setDiscoverSectionList(commonSectionList);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }

                });
    }

    /**
     * 该方法中的两个网络请求由于会有加载更多的情况所以单独写到一个方法内。
     *
     * @param pageSize
     */
    private void loadTopics(final int pageSize) {
        OkGo.get(Urls.HotTopicURL)
                .tag(holderForTopic.getPage())
                .params("pageNo", holderForTopic.getPage())
                .params("pageSize", pageSize)
                .execute(new JsonCallback<IycResponse<List<DiscoverTopic>>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<List<DiscoverTopic>> listIycResponse, Call call, Response response) {
                        if (holderForTopic.isRefresh()) {
                            discoverContent.getDiscoverTopicList().clear();
                            refreshSuccess(discoverPtrClassicFrameLayout);
                        }
                        List<DiscoverTopic> tempTopic =
                                discoverContent.getDiscoverTopicList() == null ?
                                        new ArrayList<DiscoverTopic>() : discoverContent.getDiscoverTopicList();
                        for (DiscoverTopic topic : listIycResponse.getData()) {
                            if (!tempTopic.contains(topic)) {
                                discoverContent.getDiscoverTopicList().add(topic);
                            }
                        }

                        if (listIycResponse.getData().size() < pageSize) {
                            loadMoreSuccess(discoverPtrClassicFrameLayout, true);
                            holderForTopic.setCanLoadMore(false);
                        } else {
                            loadMoreSuccess(discoverPtrClassicFrameLayout, false);
                            holderForTopic.setCanLoadMore(true);
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (!holderForTopic.isRefresh()) {
                            holderForTopic.loadMoreFailed();
                            loadMoreFailed(discoverPtrClassicFrameLayout);
                        } else {
                            refreshFailed(discoverPtrClassicFrameLayout);
                        }
                    }
                });
    }

    private void loadReviews(final int pageSize) {
        OkGo.get(Urls.DiscoverHorReviewURL)
                .tag(holderForReview.getPage())
                .params("pageNo", holderForReview.getPage())
                .params("pageSize", pageSize)
                .execute(new JsonCallback<IycResponse<List<DiscoverReviews>>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<List<DiscoverReviews>> listIycResponse, Call call, Response response) {

                        if (holderForReview.isRefresh()) {
                            discoverContent.getDiscoverReviewList().clear();
                            refreshSuccess(discoverPtrClassicFrameLayout);
                        }
                        List<DiscoverReviews> tempList =
                                discoverContent.getDiscoverReviewList() != null ? discoverContent.getDiscoverReviewList() : new ArrayList<DiscoverReviews>();
                        for (DiscoverReviews reviews : listIycResponse.getData()) {
                            if (!tempList.contains(reviews)) {
                                discoverContent.getDiscoverReviewList().add(reviews);
                            }
                        }


                        if (listIycResponse.getData().size() < pageSize) {
                            loadMoreSuccess(discoverPtrClassicFrameLayout, true);
                            holderForReview.setCanLoadMore(false);
                        } else {
                            loadMoreSuccess(discoverPtrClassicFrameLayout, false);
                            holderForTopic.setCanLoadMore(true);
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        holderForReview.loadMoreFailed();
                        if (!holderForReview.isRefresh()) {
                            holderForReview.loadMoreFailed();
                            loadMoreFailed(discoverPtrClassicFrameLayout);
                        } else {
                            refreshFailed(discoverPtrClassicFrameLayout);
                        }
                    }
                });
    }

}
