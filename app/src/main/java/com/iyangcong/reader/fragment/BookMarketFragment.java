package com.iyangcong.reader.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.BFSUBookMarketAdapter;
import com.iyangcong.reader.adapter.BookMarketAdapter;
import com.iyangcong.reader.base.BaseFragment;
import com.iyangcong.reader.bean.CommonBanner;
import com.iyangcong.reader.bean.CommonBroadcast;
import com.iyangcong.reader.bean.MarketBookListItem;
import com.iyangcong.reader.bean.MarketContent;
import com.iyangcong.reader.bean.MarketRecommend;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.event.SlideEvent;
import com.iyangcong.reader.interfaceset.RetryCounter;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.CustomPtrClassicFrameLayout;
import com.iyangcong.reader.utils.LoadCountHolder;
import com.iyangcong.reader.utils.NetUtil;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 书城
 */
public class BookMarketFragment extends BaseFragment {


    @BindView(R.id.rv_book_market)
    RecyclerView rvBookMarket;
    @BindView(R.id.book_market_ptrClassicFrameLayout)
    CustomPtrClassicFrameLayout ptrClassicFrameLayout;

    private static final int REFRESH_FAILURE = 1;
    private static final int LOADMORE_FAILURE = 2;

    private BookMarketAdapter bookMarketAdapter;
    private RecyclerAdapterWithHF mAdapter;
    MyHandler handler = new MyHandler(this);
    private MarketContent bookMarket;
    private LoadCountHolder holderForReview = new LoadCountHolder();
    private List<MarketBookListItem> mBookList = new ArrayList<>();
    private List<MarketBookListItem> thirdPartBookList=new ArrayList<>();
    SharedPreferenceUtil sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
    private int showType=0;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_market, container, false);
        ButterKnife.bind(this, view);
        initVaryViewHelper(mContext, ptrClassicFrameLayout, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getNetworkState(mContext) == NetUtil.NETWORK_MOBILE || getNetworkState(mContext) == NetUtil.NETWORK_WIFI) {
                    showDataView();
                    refreshAuto(ptrClassicFrameLayout);
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
//        refreshAuto(ptrClassicFrameLayout);
    }

    @Override
    protected void initData() {
        bookMarket = new MarketContent();
        bookMarket.setBookList(mBookList);
        showType=  sharedPreferenceUtil.getInt(SharedPreferenceUtil.SHOW_TYPE,0);
        bookMarket.setThirtpartBookList(thirdPartBookList);

        if(showType==0){
            bookMarketAdapter = new BookMarketAdapter(mContext, bookMarket, ptrClassicFrameLayout);
        }else if(showType==5){
            bookMarketAdapter = new BFSUBookMarketAdapter(mContext, bookMarket, ptrClassicFrameLayout);
        }


        mAdapter = new RecyclerAdapterWithHF(bookMarketAdapter);
        rvBookMarket.setAdapter(bookMarketAdapter);
        bookMarketAdapter.notifyDataSetChanged();
        //recycleView不仅要设置适配器还要设置布局管理者,否则图片不显示
        //第一个参数是上下文，第二个参数是只有一列
        rvBookMarket.setLayoutManager(new LinearLayoutManager(mContext));
        rvBookMarket.setAdapter(mAdapter);

        ptrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                holderForReview.refresh();
                getDatasFromNetwork();
            }
        });

        ptrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void loadMore() {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holderForReview.loadMore();
                        updateBookListFromNetwork();
                    }
                }, 0);
            }
        });

        refreshAuto(ptrClassicFrameLayout);

    }
    private int bannerTryCount = 0;
    private void getDatasFromNetwork() {
        OkGo.get(Urls.BannerURL)//
                .params("bannerPosition", "1")//
                .execute(new JsonCallback<IycResponse<List<CommonBanner>>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<List<CommonBanner>> listIycResponse, Call call, Response response) {
                        bookMarket.setBannerList(listIycResponse.getData());
                        bookMarketAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }

                    @Override
                    public void parseError(Call call, Exception e) {
                        super.parseError(call, e);
                        if(bannerTryCount++< RetryCounter.MAX_RETRY_TIMES){
                            getDatasFromNetwork();
                        }
                    }

                    @Override
                    public void onAfter(IycResponse<List<CommonBanner>> listIycResponse, Exception e) {
                        super.onAfter(listIycResponse, e);
                        bannerTryCount = 0;
                        if(showType==0) {
                            getBroadCast();
                        }else if(showType==5){
                            getThirtpartBookList_BFSU();
                        }
                    }
                });
    }
    private int broadCastRetryCount = 0;
    private void getBroadCast() {
        OkGo.get(Urls.BroadcastURL)
                .params("broadcastPosition", "1")//
                .execute(new JsonCallback<IycResponse<List<CommonBroadcast>>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<List<CommonBroadcast>> listIycResponse, Call call, Response response) {
                        bookMarket.setBookBroadcast(listIycResponse.data);
                        bookMarketAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }

                    @Override
                    public void parseError(Call call, Exception e) {
                        super.parseError(call, e);
                        if(broadCastRetryCount++<RetryCounter.MAX_RETRY_TIMES) {
                            getBroadCast();
                        }
                    }

                    @Override
                    public void onAfter(IycResponse<List<CommonBroadcast>> listIycResponse, Exception e) {
                        super.onAfter(listIycResponse, e);
                        broadCastRetryCount = 0;
                        requestTodayPush();
                    }
                });
    }

    private int todayPushCount = 1;

    private void requestTodayPush() {
        OkGo.get(Urls.BookMarketTodayPushURL)
                .execute(new JsonCallback<IycResponse<MarketRecommend>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<MarketRecommend> marketRecommendIycResponse, Call call, Response response) {
                        bookMarket.setBookRecommend(marketRecommendIycResponse.getData());
                        bookMarketAdapter.notifyDataSetChanged();
                        refreshSuccess(ptrClassicFrameLayout);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        refreshFailed(ptrClassicFrameLayout);
                    }

                    @Override
                    public void parseError(Call call, Exception e) {
                        super.parseError(call, e);
                        if (todayPushCount < 3) {
                            todayPushCount++;
                            requestTodayPush();
                        } else {
                            Message tmpMessage = Message.obtain();
                            tmpMessage.what = REFRESH_FAILURE;
                            tmpMessage.obj = null;
                            handler.sendMessage(tmpMessage);
                        }
                    }

                    @Override
                    public void onAfter(IycResponse<MarketRecommend> marketRecommendIycResponse, Exception e) {
                        super.onAfter(marketRecommendIycResponse, e);
                        updateBookListFromNetwork();
                    }
                });
    }

    private void updateBookListFromNetwork() {
      // OkGo.get(Urls.BookMarketBookListURL)//BookMarketBookListMethodTwo
       OkGo.get(Urls.BookMarketBookListMethodTwo)
                .params("page", holderForReview.getPage() + 1)
                .params("pageNum", "10")
                .execute(new JsonCallback<IycResponse<List<MarketBookListItem>>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<List<MarketBookListItem>> listIycResponse, Call call, Response response) {
                        if (holderForReview.isRefresh())
                            mBookList.clear();
                        if (listIycResponse.getData() == null) {
                            loadMoreSuccess(ptrClassicFrameLayout, true);
                            return;
                        }
                        for (MarketBookListItem bookIntroduction : listIycResponse.getData())
                            mBookList.add(bookIntroduction);
                        if(showType==0){
                            bookMarketAdapter.notifyItemChanged(4);
                        }else if(showType==5){
                            bookMarketAdapter.notifyItemChanged(5);
                        }

                        boolean isEnd = listIycResponse.getData().size() < 10;
                        if (!holderForReview.isRefresh()) {
                            loadMoreSuccess(ptrClassicFrameLayout, isEnd);
                        }else {
                            refreshSuccess(ptrClassicFrameLayout,!isEnd);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (holderForReview.getPage() > 0) {
                            holderForReview.loadMoreFailed();
                            loadMoreFailed(ptrClassicFrameLayout);
                        }
                    }

                    @Override
                    public void parseError(Call call, Exception e) {
                        super.parseError(call, e);
                        Message tmpMessage = Message.obtain();
                        if(!holderForReview.isRefresh()){
                            holderForReview.loadMoreFailed();
                        }
                        tmpMessage.what = holderForReview.isRefresh()?REFRESH_FAILURE:LOADMORE_FAILURE;
                        tmpMessage.obj = null;
                        handler.sendMessage(tmpMessage);
                    }
                });
    }

    //shao add begin
    private int thirdpartRetryCount=0;
    private void getThirtpartBookList_BFSU() {



        OkGo.get(Urls.BUFSRecommendBook)
                .params("keyfrom", "beiwaionline_xueli")
                .params("limit", 3)
                .params("deviceType",3)
                .execute(new JsonCallback<IycResponse<List<MarketBookListItem>>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<List<MarketBookListItem>> listIycResponse, Call call, Response response) {
                      bookMarket.setThirtpartBookList(listIycResponse.data);
                        bookMarketAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }

                    @Override
                    public void parseError(Call call, Exception e) {
                        super.parseError(call, e);
                        if(thirdpartRetryCount++<RetryCounter.MAX_RETRY_TIMES) {
                            getThirtpartBookList_BFSU();
                        }
                    }

                    @Override
                    public void onAfter(IycResponse<List<MarketBookListItem>> listIycResponse, Exception e) {
                        super.onAfter(listIycResponse, e);
                        thirdpartRetryCount = 0;
                        getBroadCast();
                        //updateBookListFromNetwork();

                    }
                });


    }
//    private int indexMainRetry=0;
//    private void getIndexMainInfo() {
//        OkGo.get(Urls.INDEX_MAIN)
//                .params("deviceType", 3)
//                .params("broadcastPosition", 1)
//                .params("bannerPosition",1)
//                .execute(new JsonCallback<IycResponse<Map<String,Object>>>(mContext) {
//                    @Override
//                    public void onSuccess(IycResponse<Map<String,Object>> listIycResponse, Call call, Response response) {
//                        int a=0;
//                        int b=a;
//                        Map<String,Object> data=listIycResponse.data;
//                       List<Map<String,Object>> broadcastData= (List<Map<String, Object>>) data.get("broadcast");
//                        List<CommonBroadcast> broadcasts=new ArrayList<>();
//                       for(int i=0;i<broadcastData.size();i++){
//                           Map<String,Object> map= (Map<String, Object>) broadcastData.get(i);
//                           CommonBroadcast broadcastItem = new CommonBroadcast();
//                            Double d= (Double) map.get("id");
//                           int id=d.intValue();
//                            //Integer ivalue=(Integer)d;
//                           broadcastItem.setId((id));
//                           broadcastItem.setBroadcastContent((String)map.get("broadcastContent"));
//                           broadcastItem.setBroadcastTitle((String)map.get("broadcastTitle"));
//                           broadcastItem.setBroadcastType(Integer.parseInt((String)map.get("broadcastType")));
//                           broadcasts.add(broadcastItem);
//
//
//                       }
//                        bookMarket.setBookBroadcast(broadcasts);
//
//                        //IndexMain content=listIycResponse.data;
//                        List<CommonBanner> bannerData= (ArrayList<CommonBanner>) data.get("banner");
//                        List<CommonBanner> banner=new ArrayList<>();
//                        for(int i=0;i<bannerData.size();i++){
//                            Map<String,Object> map= (Map<String, Object>) bannerData.get(i);
//                            CommonBanner bannerItem=new CommonBanner();
//                            bannerItem.setBannerType(Integer.parseInt((String)map.get("bannerType")));
//                            bannerItem.setBannerUrl((String) map.get("bannerUrl"));
//                            bannerItem.setContent((String)map.get("content"));
//                            bannerItem.setHtmlUrl((String)map.get("htmlUrl"));
//                            bannerItem.setTitle((String)map.get("title"));
//                            bannerItem.setId(Integer.parseInt((String)map.get("id")));
//                            banner.add(bannerItem);
//
//                            //Map<String,Object> map2= (Map<String, Object>) bannerData.get(i);
//                        }
//
////                        for(CommonBanner bar : bannerData){
////                          int type=  bar.getBannerType();
////                          String url=bar.getBannerUrl();
////                        }
//
//                        bookMarket.setBannerList(banner);
//                        List<MarketRecommend> marketRecommendData= (ArrayList<MarketRecommend>) data.get("pushbooks");
//                        List<MarketRecommend> recommends=new ArrayList<>();
//
//
//                            Map<String,Object> map= (Map<String, Object>) marketRecommendData.get(0);
//                            MarketRecommend recommendItem=new MarketRecommend();
//                            recommendItem.setCategoryP((String)map.get("categoryP"));
//                            recommendItem.setCutP((String)map.get("categoryP"));
//                            recommendItem.setMonthP((String)map.get("monthP"));
//                            MarketToddayPush todaypush = new MarketToddayPush();
//                            Map<String,Object> free= (Map<String, Object>) map.get("free");
//                            //for(int j=0;j<free.size();j++){
//                                MarketToddayPush todayfree = new MarketToddayPush();
//                                todayfree.setBookid((String)free.get("bookid"));
//                                todayfree.setBookPhoto((String)free.get("bookPhoto"));
//                                todayfree.setContent((String)free.get("content"));
//                                todayfree.setTitle((String)free.get("title"));
//                                recommendItem.setFree(todayfree);
//                           // }
//
//                            Map<String,Object> tongshi= (Map<String, Object>) map.get("tongshi");
//                           // for(int j=0;j<free.size();j++){
//                                MarketToddayPush todaytongshi = new MarketToddayPush();
//                                todaytongshi.setBookid((String)tongshi.get("bookid"));
//                                todaytongshi.setBookPhoto((String)tongshi.get("bookPhoto"));
//                                todaytongshi.setContent((String)tongshi.get("content"));
//                                todaytongshi.setTitle((String)tongshi.get("title"));
//                                recommendItem.setTongshi(todaytongshi);
//                          //  }
//                            Map<String,Object> hot= (Map<String, Object>) map.get("hot");
//                         //   for(int j=0;j<free.size();j++){
//                                MarketToddayPush todayhot = new MarketToddayPush();
//                                todayhot.setBookid((String)hot.get("bookid"));
//                                todayhot.setBookPhoto((String)hot.get("bookPhoto"));
//                                todayhot.setContent((String)hot.get("content"));
//                                todayhot.setTitle((String)hot.get("title"));
//                                recommendItem.setHot(todayhot);
//                         //   }
//
//                            recommendItem.setFreeP((String)map.get("categoryP"));
//                            recommendItem.setNewbookP((String)map.get("newbookP"));
//                            //recommends.add(recommendItem);
//
//
//
//                        bookMarket.setBookRecommend(recommendItem);
//                        bookMarketAdapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onError(Call call, Response response, Exception e) {
//                        super.onError(call, response, e);
//                    }
//
//                    @Override
//                    public void parseError(Call call, Exception e) {
//                        super.parseError(call, e);
//                        if(indexMainRetry++<RetryCounter.MAX_RETRY_TIMES) {
//                            getIndexMainInfo();
//                        }else {
//                            Message tmpMessage = Message.obtain();
//                            tmpMessage.what = REFRESH_FAILURE;
//                            tmpMessage.obj = null;
//                            handler.sendMessage(tmpMessage);
//                        }
//                    }
//
//                    @Override
//                    public void onAfter(IycResponse<Map<String,Object>> listIycResponse, Exception e) {
//                        super.onAfter(listIycResponse, e);
//                        indexMainRetry = 0;
//                        if(showType==0){
//                            updateBookListFromNetwork();
//                        }else if(showType==5){
//                            getThirtpartBookList_BFSU();
//                        }
//
//                    }
//                });
//
//
//    }
    //shao add end

    public void refreshFail(){
        refreshFailed(ptrClassicFrameLayout);
    }

    public void loadMoreFail(){
        loadMoreFailed(ptrClassicFrameLayout);
    }

    private static class MyHandler extends Handler {
        private final WeakReference<BookMarketFragment> mFragment;

        public MyHandler(BookMarketFragment fragment) {
            mFragment = new WeakReference<BookMarketFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            BookMarketFragment tmpFragment = mFragment.get();
            if (tmpFragment != null) {
                switch (msg.what){
                    case REFRESH_FAILURE:
                        tmpFragment.refreshFail();
                        break;
                    case LOADMORE_FAILURE:
                        tmpFragment.loadMoreFail();
                        break;
                }
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReturnToTop(SlideEvent slideEvent){
        if(isVisible()&&rvBookMarket != null){
            rvBookMarket.scrollToPosition(slideEvent.getIndex());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    public void refreshFragment(){
        showType=  sharedPreferenceUtil.getInt(SharedPreferenceUtil.SHOW_TYPE,0);
        bookMarket.setThirtpartBookList(thirdPartBookList);

        if(showType==0){
            bookMarketAdapter = new BookMarketAdapter(mContext, bookMarket, ptrClassicFrameLayout);
        }else if(showType==5){
            bookMarketAdapter = new BFSUBookMarketAdapter(mContext, bookMarket, ptrClassicFrameLayout);
        }


        mAdapter = new RecyclerAdapterWithHF(bookMarketAdapter);
        rvBookMarket.setAdapter(bookMarketAdapter);
        bookMarketAdapter.notifyDataSetChanged();
        //recycleView不仅要设置适配器还要设置布局管理者,否则图片不显示
        //第一个参数是上下文，第二个参数是只有一列
        rvBookMarket.setLayoutManager(new LinearLayoutManager(mContext));
        rvBookMarket.setAdapter(mAdapter);
        if(showType==5){
            getThirtpartBookList_BFSU();
        }
    }
}
