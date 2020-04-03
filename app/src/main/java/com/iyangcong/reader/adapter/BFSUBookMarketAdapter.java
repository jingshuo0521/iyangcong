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
import com.iyangcong.reader.activity.BannerUrlAcitivty;
import com.iyangcong.reader.activity.BookMarketBookDetailsActivity;
import com.iyangcong.reader.activity.BookMarketBookListActivity;
import com.iyangcong.reader.activity.BookMarketClassificationActivity;
import com.iyangcong.reader.activity.BookMarketMonlyBookActivity;
import com.iyangcong.reader.activity.BookMarketSpecialTopicActivity;
import com.iyangcong.reader.activity.DiscoverCircleDetailActivity;
import com.iyangcong.reader.activity.DiscoverReadingPartyDetailsActivity;
import com.iyangcong.reader.activity.DiscoverTopicActivity;
import com.iyangcong.reader.bean.CommonBanner;
import com.iyangcong.reader.bean.CommonBroadcast;
import com.iyangcong.reader.bean.MarketBookListItem;
import com.iyangcong.reader.bean.MarketContent;
import com.iyangcong.reader.bean.MarketRecommend;
import com.iyangcong.reader.ui.CustomBanner;
import com.iyangcong.reader.ui.CustomPtrClassicFrameLayout;
import com.iyangcong.reader.ui.textview.MarqueeView;
import com.iyangcong.reader.utils.BannerImageLoader;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 书城适配器
 */
public class BFSUBookMarketAdapter extends BookMarketAdapter {

    /**
     * 类型1：图书推荐--使用Banner实现
     */
    private static final int BOOK_RECOMMEND_BANNER = 0;

    /*
    * 类型6：针对北外网院电子图书馆
    * */
    private static final int BOOK_RECOMMEND_BFSU=1;

    /**
     * 类型2：图书分类--使用GridView实现
     */
    private static final int BOOK_TYPE_GV = 2;

    /**
     * 类型3：图书版块--使用RecyclerView实现
     */
    private static final int TODAY_BOOK_LL = 3;

    /**
     * 类型4：书城广播--使用LinearLayout实现
     */
    private static final int BOOK_BROADCAST = 4;

    /**
     * 类型5：书籍推荐列表--使用RecyclerView实现
     */
    private static final int BOOK_INTRODUCTION = 5;

    /**
     * 当前类型
     */
    private int currentType = BOOK_RECOMMEND_BANNER;

    private Context context;

    /**
     * 使用mLayoutInflater来初始化布局
     */
    private LayoutInflater mLayoutInflater;

    private MarketContent bookMarket;

    private CustomPtrClassicFrameLayout ptrClassicFrameLayout;



    public BFSUBookMarketAdapter(Context context, MarketContent bookMarket) {
        this(context, bookMarket, null);
    }

    public BFSUBookMarketAdapter(Context context, MarketContent bookMarket, CustomPtrClassicFrameLayout ptrClassicFrameLayout) {
        this.context = context;
        this.bookMarket = bookMarket;
        this.ptrClassicFrameLayout = ptrClassicFrameLayout;
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
        if (viewType == BOOK_RECOMMEND_BANNER) {
            return new BookBannerViewHolder(context, mLayoutInflater.inflate(R.layout.banner_common, null));
        } else if (viewType == BOOK_TYPE_GV) {
            return new BookTypeViewHolder(context, mLayoutInflater.inflate(R.layout.gv_section_common, null));
        } else if (viewType == BOOK_RECOMMEND_BFSU) {
            return new BFSURecommendBookViewHolder(context, mLayoutInflater.inflate(R.layout.rv_book_bfsu_recommendbook, null));
        } else if (viewType == BOOK_BROADCAST) {
            return new BookBroadcastViewHolder(context, mLayoutInflater.inflate(R.layout.ll_market_brocast, parent, false));
        } else if (viewType == TODAY_BOOK_LL) {
            return new TodayBookViewHolder(context, mLayoutInflater.inflate(R.layout.ll_market_today_book, parent, false));
        } else if (viewType == BOOK_INTRODUCTION) {
            return new BookIntroductionViewHolder(context, mLayoutInflater.inflate(R.layout.rv_book_introduction, null));
        }
        return null;
    }

    /**
     * 绑定数据模块
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == BOOK_RECOMMEND_BANNER) {
            BookBannerViewHolder bookBannerViewHolder = (BookBannerViewHolder) holder;
            bookBannerViewHolder.setData(bookMarket.getBannerList());
        } else if (getItemViewType(position) == BOOK_RECOMMEND_BFSU) {
            BFSURecommendBookViewHolder bookTypeViewHolder = (BFSURecommendBookViewHolder) holder;


            bookTypeViewHolder.setData(bookMarket.getThirtpartBookList());
        } else if (getItemViewType(position) == BOOK_TYPE_GV) {
            BookTypeViewHolder bookTypeViewHolder = (BookTypeViewHolder) holder;
            bookTypeViewHolder.setData(bookMarket.getBookRecommend());
        } else if (getItemViewType(position) == TODAY_BOOK_LL) {
            TodayBookViewHolder todayBookViewHolder = (TodayBookViewHolder) holder;
            todayBookViewHolder.setData(bookMarket.getBookRecommend());
        } else if (getItemViewType(position) == BOOK_BROADCAST) {
            BookBroadcastViewHolder bookBroadcastViewHolder = (BookBroadcastViewHolder) holder;
            bookBroadcastViewHolder.setData(bookMarket.getBookBroadcast());
        } else if (getItemViewType(position) == BOOK_INTRODUCTION) {
            BookIntroductionViewHolder bookIntroductionViewHolder = (BookIntroductionViewHolder) holder;
            bookIntroductionViewHolder.setData(bookMarket.getBookList());
        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case BOOK_RECOMMEND_BANNER:
                currentType = BOOK_RECOMMEND_BANNER;
                break;
            case BOOK_RECOMMEND_BFSU:
                currentType = BOOK_RECOMMEND_BFSU;
                break;
            case BOOK_TYPE_GV:
                currentType = BOOK_TYPE_GV;
                break;
            case TODAY_BOOK_LL:
                currentType = TODAY_BOOK_LL;
                break;
            case BOOK_BROADCAST:
                currentType = BOOK_BROADCAST;
                break;
            case BOOK_INTRODUCTION:
                currentType = BOOK_INTRODUCTION;
                break;
            default:
                break;
        }
        return currentType;
    }

    /**
     * 类型1：图书推荐，轮播图 ViewHolder
     */
    public class BookBannerViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;
        private List<CommonBanner> bannerList;
        private List<String> imageUrls = new ArrayList<String>();
        @BindView(R.id.banner)
        CustomBanner banner;

        BookBannerViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        void setData(final List<CommonBanner> bannerList) {
//            banner.setParentView(ptrClassicFrameLayout);
            imageUrls.clear();
            //设置Banner的数据
            //得到图片地址的集合
            this.bannerList = bannerList;
            if (bannerList != null) {
                for (int i = 0; i < bannerList.size(); i++) {
                    String image = bannerList.get(i).getBannerUrl();
                    imageUrls.add(image);
                }
                //banner指示器显示在右边
                banner.setIndicatorGravity(BannerConfig.CENTER);
                banner.setDelayTime(3000);

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
                    Intent intent;

                    @Override
                    public void OnBannerClick(int position) {
                        switch (bannerList.get(position - 1).getBannerType()) {
                            case 1:
                                intent = new Intent(mContext, BookMarketBookDetailsActivity.class);
                                intent.putExtra("bookId", bannerList.get(position - 1).getId());
                                intent.putExtra("bookName", bannerList.get(position - 1).getTitle());
                                mContext.startActivity(intent);
                                break;
                            case 2:
                                /**
                                 * 跳转到专题
                                 */
                                intent = new Intent(mContext, BookMarketBookListActivity.class);
                                intent.putExtra("request", 3);
                                intent.putExtra("topicId", Integer.toString(bannerList.get(position - 1).getId()));
                                intent.putExtra("imgurl", bannerList.get(position - 1).getBannerUrl());
                                intent.putExtra("name", bannerList.get(position - 1).getTitle());
                                intent.putExtra("bookIds", bannerList.get(position - 1).getContent());
                                mContext.startActivity(intent);
                                break;
                            case 3:
                                intent = new Intent(context, DiscoverCircleDetailActivity.class);
                                intent.putExtra(Constants.circleId, bannerList.get(position - 1).getId());
                                intent.putExtra(Constants.circleName, bannerList.get(position - 1).getTitle());
                                mContext.startActivity(intent);
                                break;
                            case 4:
                                intent = new Intent(context, DiscoverReadingPartyDetailsActivity.class);
                                intent.putExtra(Constants.readingPartyId, bannerList.get(position - 1).getId());
                                intent.putExtra(Constants.readingPartyTitle, bannerList.get(position - 1).getTitle());
                                mContext.startActivity(intent);
                                break;
                            case 5:
                                intent = new Intent(mContext, BannerUrlAcitivty.class);
                                intent.putExtra(Constants.URL, bannerList.get(position - 1).getHtmlUrl());
                                intent.putExtra(Constants.Title, bannerList.get(position - 1).getTitle());
                                mContext.startActivity(intent);
                                break;
                            case 6:
                                break;
                            case 7:
                                intent = new Intent(context, DiscoverTopicActivity.class);
                                intent.putExtra(Constants.topicId, bannerList.get(position - 1).getId());
                                intent.putExtra(Constants.TOPIC_ACITIVITY_TITLE, bannerList.get(position - 1).getTitle());
                                mContext.startActivity(intent);
                                break;
                            default:break;
                        }

                    }
                });
            }
        }
    }

    /**
     * 类型2：图书分类ViewHolder
     */
    public class BookTypeViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;
        @BindView(R.id.gv_book_type)
        GridView gridView;
        @BindView(R.id.rl_discover_bar)
        RelativeLayout rlDiscoverBar;

        BookTypeViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        void setData(final MarketRecommend bookRecommend) {
            //已得到数据了
            //设置适配器
            rlDiscoverBar.setVisibility(View.GONE);
            if (bookRecommend == null) {
                gridView.setVisibility(View.GONE);
            } else {
                gridView.setVisibility(View.VISIBLE);
                CommonSectionAdapter adapter = new CommonSectionAdapter(mContext, bookRecommend);
                gridView.setAdapter(adapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        switch (i) {
                            case 0:
                                Intent intent0 = new Intent(mContext, BookMarketBookListActivity.class);
                                intent0.putExtra("list_type", 2);
                                mContext.startActivity(intent0);
                                break;
                            case 1:
                                Intent intent1 = new Intent(mContext, BookMarketSpecialTopicActivity.class);
                                mContext.startActivity(intent1);
                                break;
                            case 2:
                                Intent intent2 = new Intent(mContext, BookMarketBookListActivity.class);
                                intent2.putExtra("list_type", 3);
                                mContext.startActivity(intent2);
                                break;
                            case 3:
                                Intent intent3 = new Intent(mContext, BookMarketMonlyBookActivity.class);
                                intent3.putExtra("list_type", 5);
                                mContext.startActivity(intent3);
                                break;
                            case 4:
                                mContext.startActivity(new Intent(mContext, BookMarketClassificationActivity.class));
                                break;
                        }
                    }
                });
            }

        }
    }


    /**
     * 类型3：图书版块 ViewHolder
     */
    public class TodayBookViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;
        private MarketRecommend bookRecommend;

        @BindView(R.id.ll_general_read)
        LinearLayout llGeneralRead;
        @BindView(R.id.ll_hot_book)
        LinearLayout llHotBook;
        @BindView(R.id.ll_today_free)
        LinearLayout llTodayFree;
        @BindView(R.id.tv_general_read_tittle)
        TextView tvGeneralReadTittle;
        @BindView(R.id.tv_general_read_introduction)
        TextView tvGeneralReadIntroduction;
        @BindView(R.id.iv_general_read_image)
        ImageView ivGeneralReadImage;
        @BindView(R.id.tv_hot_book_tittle)
        TextView tvHotBookTittle;
        @BindView(R.id.tv_hot_book_introduction)
        TextView tvHotBookIntroduction;
        @BindView(R.id.iv_hot_book_image)
        ImageView ivHotBookImage;
        @BindView(R.id.tv_free_book_tittle)
        TextView tvFreeBookTittle;
        @BindView(R.id.tv_free_book_introduction)
        TextView tvFreeBookIntroduction;
        @BindView(R.id.iv_free_book_image)
        ImageView ivFreeBookImage;

        @OnClick({R.id.ll_general_read, R.id.ll_hot_book, R.id.ll_today_free})
        void onLlClick(View v) {
            switch (v.getId()) {
                case R.id.ll_general_read:
                    Intent intent = new Intent(mContext, BookMarketBookListActivity.class);
                    intent.putExtra("list_type", 1);
                    mContext.startActivity(intent);
                    break;
                case R.id.ll_hot_book:
                    //跳转到热门推荐列表
                    Intent intent1 = new Intent(mContext, BookMarketBookListActivity.class);
                    if (bookRecommend != null) {
                        intent1.putExtra("list_type", 7);
                        mContext.startActivity(intent1);
                    }
                    break;
                case R.id.ll_today_free:

                    Intent intent2 = new Intent(mContext, BookMarketBookListActivity.class);
                    intent2.putExtra("list_type", 4);
                    mContext.startActivity(intent2);

                    break;
            }
        }

        private TodayBookViewHolder(Context context, View inflate) {
            super(inflate);
            this.mContext = context;
            ButterKnife.bind(this, inflate);
        }

        void setData(MarketRecommend bookRecommend) {
            if (bookRecommend == null) {
                llGeneralRead.setVisibility(View.GONE);
                llTodayFree.setVisibility(View.GONE);
                llHotBook.setVisibility(View.GONE);
            } else {
                this.bookRecommend = bookRecommend;
                llGeneralRead.setVisibility(View.VISIBLE);
                llTodayFree.setVisibility(View.VISIBLE);
                llHotBook.setVisibility(View.VISIBLE);
                GlideImageLoader.displayBookCover(context, ivGeneralReadImage, bookRecommend.getTongshi().getBookPhoto());
                GlideImageLoader.displayBookCover(context, ivHotBookImage, bookRecommend.getHot().getBookPhoto());
                GlideImageLoader.displayBookCover(context, ivFreeBookImage, bookRecommend.getFree().getBookPhoto());
            }
        }

    }
    /**
     * 类型4：书城广播
     */
    class BookBroadcastViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;

        @BindView(R.id.tv_text_ad)
        MarqueeView tvTextAd;
        @BindView(R.id.ll_text_ad)
        LinearLayout llTextAd;
        @BindView(R.id.tv_title_text_ad)
        TextView tvTitleTextAd;


        private BookBroadcastViewHolder(Context context, View inflate) {
            super(inflate);
            this.mContext = context;
            ButterKnife.bind(this, inflate);
        }

        void setData(final List<CommonBroadcast> bookBroadcastList) {
            if (bookBroadcastList == null) {
                llTextAd.setVisibility(View.GONE);
            } else {
                llTextAd.setVisibility(View.VISIBLE);
                tvTextAd.startWithList(bookBroadcastList);

                tvTextAd.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
                    Intent intent;

                    @Override
                    public void onItemClick(int position, TextView textView) {
                        CommonBroadcast broadcast = bookBroadcastList.get(position);
                        switch (broadcast.getBroadcastType()) {
                            case 1:
                                intent = new Intent(mContext, BookMarketBookDetailsActivity.class);
                                intent.putExtra("bookId", broadcast.getId());
                                intent.putExtra("bookName", broadcast.getBroadcastTitle());
                                mContext.startActivity(intent);
                                break;
                            case 2:
                                /**
                                 * 跳转到专题
                                 */
                                intent = new Intent(mContext, BookMarketBookListActivity.class);
                                intent.putExtra("request", 3);
                                intent.putExtra("topicId", "3");
                                intent.putExtra("name", broadcast.getContent());
                                intent.putExtra("imgurl", broadcast.getBroadcastUrl());
                                mContext.startActivity(intent);
                                break;
                            case 3:
                                intent = new Intent(context, DiscoverCircleDetailActivity.class);
                                intent.putExtra(Constants.circleId, broadcast.getId());
                                intent.putExtra(Constants.circleName, broadcast.getBroadcastTitle());
                                mContext.startActivity(intent);
                                break;
                            case 4:
                                intent = new Intent(context, DiscoverReadingPartyDetailsActivity.class);
                                intent.putExtra(Constants.readingPartyId, broadcast.getId());
                                intent.putExtra(Constants.readingPartyTitle, broadcast.getBroadcastTitle());
                                mContext.startActivity(intent);
                                break;
                            case 5:
                            case 6:
                                intent = new Intent(mContext, BookMarketBookListActivity.class);
                                intent.putExtra("list_type", 6);
                                mContext.startActivity(intent);
                                break;
                        }
                    }
                });
            }
        }
    }
    /**
     * 类型5：书籍推荐列表 ViewHolder
     */
    public class BookIntroductionViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;
        @BindView(R.id.rv_book_introduction)
        RecyclerView rvBookIntroduction;

        public BookIntroductionViewHolder(Context context, View inflate) {
            super(inflate);
            this.mContext = context;
            ButterKnife.bind(this, inflate);
        }

        void setData(List<MarketBookListItem> bookList) {
            BookIntroduceAdapter bookIntroduceAdapter = new BookIntroduceAdapter(mContext, bookList);
            rvBookIntroduction.setAdapter(bookIntroduceAdapter);
            //recycleView不仅要设置适配器还要设置布局管理者,否则图片不显示
            //recycleView不仅要设置适配器还要设置布局管理者,否则图片不显示
            //第一个参数是上下文，第二个参数是只有一列
            GridLayoutManager manager = new GridLayoutManager(mContext, 1);
            rvBookIntroduction.setLayoutManager(manager);
        }
    }

    /**
     * 类型6：针对北外网院电子图书馆 ViewHolder
     */
    public class BFSURecommendBookViewHolder extends RecyclerView.ViewHolder {



        private final Context mContext;
        @BindView(R.id.bfsu_more)
        TextView bfsu_more;
        @BindView(R.id.gv_book_type)
        GridView gridView;

        BFSURecommendBookViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        void setData(final List<MarketBookListItem> bookList) {
            //已得到数据了
            //设置适配器

            if (bookList == null) {
                gridView.setVisibility(View.GONE);
            } else {
                gridView.setVisibility(View.VISIBLE);
                BFSURecommendBookAdapter adapter = new BFSURecommendBookAdapter(mContext, bookList);
                gridView.setAdapter(adapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                     Intent   intent = new Intent(mContext, BookMarketBookDetailsActivity.class);
                     String bookId=(String)bookList.get(i).getBookId().trim();
                        intent.putExtra("bookId", Integer.parseInt(bookId));
                        intent.putExtra("bookName", bookList.get(i).getBookName());
                        mContext.startActivity(intent);
                    }
                });
            }

        }
        @OnClick({R.id.bfsu_more})
        void onLlClick(View v) {
            switch (v.getId()) {
                case R.id.bfsu_more:
                    Intent intent = new Intent(mContext, BookMarketBookListActivity.class);
                    intent.putExtra("list_type", 4);
                    intent.putExtra("keyfrom",Constants.THIRDPART_TYPE_BFSU);
                    mContext.startActivity(intent);
                    break;
                default:
                 break;
            }
        }

    }
}
