package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.BookIntroduceAdapter;
import com.iyangcong.reader.bean.BannerImg;
import com.iyangcong.reader.bean.BookClassify;
import com.iyangcong.reader.bean.BookListCategory;
import com.iyangcong.reader.bean.GeneralBookInfo;
import com.iyangcong.reader.bean.MarketBookListItem;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.CustomBaseDialog;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.LoadCountHolder;
import com.iyangcong.reader.utils.NotNullUtils;
import com.iyangcong.reader.utils.RecycleViewUtils;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by lg on 2016/12/24.
 */

public class BookMarketBookListActivity extends SwipeBackActivity implements CustomBaseDialog.SortCallback {

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.btnFunction1)
    ImageButton btnFunction1;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.layout_no_content)
    LinearLayout layoutNoContent;
    @BindView(R.id.rv_booklist)
    RecyclerView rvBookList;
    RecyclerAdapterWithHF mBookListAdapter;
    //通识阅读
    private final int COMMEN_SUBSCRIPTION_TYPE = 1;
    //新书列表
    private final int NEW_BOOK_LIST_TYPE = 2;
    //免费列表
    private final int FREE_BOOKLIST_TYPE = 4;
    //特价列表
    private final int SPECIAL_BOOKLIST_TYPE = 3;
    //包月列表
    private final int MONTHLY_SUBSCRIPTION_TYPE = 5;
    //积分兑换列表
    private final int POINT_CHANGE_TYPE = 6;
    //热门推荐列表
    private final int HOT_RECOMMEND_TYPE = 7;
    //标签列表页
    private final int TAG_BOOKS_TYPE = 8;
    //分类列表
    private final int ClASSIfFY_BOOKLIST_TYPE = 9;

    private StringBuffer selectedButtons = new StringBuffer();

    private Handler handler = new Handler();
    @BindView(R.id.book_market_booklist_ptrClassicFrameLayout)
    PtrClassicFrameLayout bookMarketBooklistPtrClassicFrameLayout;

    /**
     * currentListType
     * 1:通识阅读
     * 2:新书列表
     * 3:特价列表
     * 4:免费列表
     * 5:包月列表
     * 6:积分兑换列表
     * 7:热门推荐列表
     * 8:标签图书列表页
     */
    private int currentListType;
    private int keyfrom;
    List<MarketBookListItem> bookList;
    private LoadCountHolder holderForReview = new LoadCountHolder();
    private List<BookListCategory> subjectList = new ArrayList<BookListCategory>();
    private List<BookListCategory> versionList = new ArrayList<BookListCategory>();
    private List<BookListCategory> gradetList = new ArrayList<BookListCategory>();
    private String[] strVersion = new String[]{};
    private String[] strGrade = new String[]{};
    private HashMap<String, String> hashMap = new HashMap<>();
    private HashMap<String, String> classify = new HashMap<>();
    private int request;
    private String titleName;
    private LayoutInflater mLayoutInflater;
    private String books;
    private BookIntroduceAdapter bookIntroduceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_market_book_list);
        ButterKnife.bind(this);
        mLayoutInflater = LayoutInflater.from(context);
        this.currentListType = getIntent().getIntExtra("list_type", -1);
        keyfrom=getIntent().getIntExtra("keyfrom",0);
        getClassify();
        setMainHeadView();
        initView();
    }

    protected void initView() {
        //***
        strVersion = getResources().getStringArray(R.array.language_version);
        strGrade = getResources().getStringArray(R.array.language_grade);
        getStrSubjectFromNet();
        for (int i = 0; i < 3; i++) {
            BookListCategory category = new BookListCategory();
            category.setType(strVersion[i]);
            category.setTypeId("" + (i + 1));
            versionList.add(category);
        }
        for (int i = 0; i < 4; i++) {
            BookListCategory category = new BookListCategory();
            category.setType(strGrade[i]);
            category.setTypeId("" + (1 + i));
            gradetList.add(category);
        }
        //***
        if (request == 0) {
            if (currentListType == 1) {
                hashMap.put("category", "all");
                hashMap.put("difficultyStr", "all");
                hashMap.put("languageStr", "all");
            } else {
                hashMap.put("difficultyStr", "");
                hashMap.put("languageStr", "");
            }
        }

        switch (currentListType) {
            case NEW_BOOK_LIST_TYPE:
                textHeadTitle.setText("新书");
                break;
            case FREE_BOOKLIST_TYPE:
                if(keyfrom==0) {
                    textHeadTitle.setText("免费");
                }else if(keyfrom==Constants.THIRDPART_TYPE_BFSU){
                    textHeadTitle.setText("北外网院电子图书馆");
                }
                break;
            case SPECIAL_BOOKLIST_TYPE:
                textHeadTitle.setText("优惠");
                break;
            case MONTHLY_SUBSCRIPTION_TYPE:
                textHeadTitle.setText("包月");
                break;
            case COMMEN_SUBSCRIPTION_TYPE:
                textHeadTitle.setText("通识阅读");
                break;
            case POINT_CHANGE_TYPE:
                textHeadTitle.setText("积分兑换");
                break;
            case HOT_RECOMMEND_TYPE:
                textHeadTitle.setText("热门推荐");
                btnFunction.setVisibility(View.GONE);
                break;
            case TAG_BOOKS_TYPE:
                titleName = getIntent().getStringExtra("tagsName");
                textHeadTitle.setText(titleName);
                btnFunction.setVisibility(View.GONE);
                break;
            case ClASSIfFY_BOOKLIST_TYPE:
                textHeadTitle.setText(titleName);
                btnFunction.setVisibility(View.VISIBLE);
                break;
            case -1:
                if (titleName.contains("#免费")) {
                    titleName = titleName.substring(0, titleName.length() - 3);
                    btnFunction1.setVisibility(View.GONE);
                }
                textHeadTitle.setText(titleName);
                break;
        }

        rvBookList.setLayoutManager(new LinearLayoutManager(this));
        bookIntroduceAdapter = new BookIntroduceAdapter(this, bookList);
        mBookListAdapter = new RecyclerAdapterWithHF(bookIntroduceAdapter);
        //测试添加header
        if (request == 3) {
            mBookListAdapter.addHeader(RecycleViewUtils.getHeaderView(this, getIntent().getStringExtra("imgurl"), R.drawable.pic_default_special_topic));
        }
        books = getIntent().getStringExtra("bookIds");
        rvBookList.setAdapter(mBookListAdapter);

        bookMarketBooklistPtrClassicFrameLayout.setHorizontalScrollBarEnabled(false);
//        getDatasFromNetwork();
        bookMarketBooklistPtrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        holderForReview.refresh();
                        getDatasFromNetwork();
                    }
                });
            }
        });

        bookMarketBooklistPtrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void loadMore() {
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        holderForReview.loadMore();
                        getDatasFromNetwork();
                    }
                }, 0);
            }
        });
        autoRefresh(bookMarketBooklistPtrClassicFrameLayout);
    }

    @Override
    protected void setMainHeadView() {
        btnBack.setImageResource(R.drawable.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnFunction.setImageResource(R.drawable.ic_classify);
        btnFunction1.setImageResource(R.drawable.ic_shopping);
        if (request == 0) {
            btnFunction.setVisibility(View.VISIBLE);
        } else {
            btnFunction.setVisibility(View.GONE);
        }
        if (request == 3) {
            btnFunction1.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) btnFunction1.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, R.id.btnFunction1);
            btnFunction1.setLayoutParams(layoutParams);
        } else {
            btnFunction1.setVisibility(View.GONE);
        }
        btnFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //**
                customBaseDialog(currentListType, subjectList, versionList, gradetList);
                //**
            }
        });
        btnFunction1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAllToShopCart();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        bookList = new ArrayList<MarketBookListItem>();
//        showLoadingDialog();
    }

    private void getDatasFromNetwork() {
        //showLoadingDialog();
        if (currentListType > 1 && currentListType < 6) {
            addBanner(currentListType);
            OkGo.get(Urls.BookMarketNavBookListURL)
                    .tag(this)
                    .params("page", holderForReview.getPage() + 1)
                    .params("pageNum", holderForReview.getPageSize())
                    .params("bookType", currentListType)
                    .params(hashMap)
                    .execute(new JsonCallback<IycResponse<List<MarketBookListItem>>>(this) {
                        @Override
                        public void onSuccess(IycResponse<List<MarketBookListItem>> listIycResponse, Call call, Response response) {
                            layoutNoContent.setVisibility(View.GONE);
                            if(NotNullUtils.isNull(listIycResponse)||NotNullUtils.isNull(listIycResponse.getData())){
                                if(holderForReview.isRefresh())
                                    refreshFailed(bookMarketBooklistPtrClassicFrameLayout);
                                else
                                    loadMoreFailed(bookMarketBooklistPtrClassicFrameLayout);
                                if(holderForReview.getPage()==0) {
                                    bookList.clear();
                                    mBookListAdapter.notifyDataSetChanged();
                                    layoutNoContent.setVisibility(View.VISIBLE);
                                }
                                return;
                            }
                            if (holderForReview.isRefresh()) {
                                bookList.clear();
                            }
                            bookList.addAll(listIycResponse.getData());
                            boolean isEnd = listIycResponse.getData().size() < holderForReview.getPageSize();
                            if(holderForReview.isRefresh()){
                                refreshSuccess(bookMarketBooklistPtrClassicFrameLayout,false);
                                addListHeader();
                            }else{
                                loadMoreSuccess(bookMarketBooklistPtrClassicFrameLayout,false);
                            }
                            mBookListAdapter.notifyDataSetChanged();
                            dismissLoadingDialig();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            if (holderForReview.getPage() > 0) {
                                holderForReview.loadMoreFailed();
                                loadMoreFailed(bookMarketBooklistPtrClassicFrameLayout);
                            } else if (holderForReview.getPage() == 0) {
                                refreshFailed(bookMarketBooklistPtrClassicFrameLayout);
                            }
                            dismissLoadingDialig();
                        }
                    });
        } else if (currentListType == 1 || request == 2) {
            addBanner(currentListType);
            OkGo.get(Urls.BookMarketGeneralBookListURL)
                    .tag(this)
                    .params("page", holderForReview.getPage() + 1)
                    .params("pageNum", "5")
                    .params(hashMap)
                    .execute(new JsonCallback<IycResponse<List<GeneralBookInfo>>>(this) {
                        @Override
                        public void onSuccess(IycResponse<List<GeneralBookInfo>> listIycResponse, Call call, Response response) {
                            layoutNoContent.setVisibility(View.GONE);
                            if(listIycResponse == null || listIycResponse.getData() == null
                                    || listIycResponse.getData().size() == 0){
                                if(holderForReview.isRefresh())
                                    refreshFailed(bookMarketBooklistPtrClassicFrameLayout);
                                else
                                    loadMoreFailed(bookMarketBooklistPtrClassicFrameLayout);
                                if(holderForReview.getPage()==0) {
                                    bookList.clear();
                                    mBookListAdapter.notifyDataSetChanged();
                                    layoutNoContent.setVisibility(View.VISIBLE);
                                }
                            }
                            if (holderForReview.isRefresh())
                                bookList.clear();
                            for (GeneralBookInfo book : listIycResponse.getData()) {
                                bookList.add(generalToItem(book));
                            }
                            boolean isEnd = listIycResponse.getData().size() < 5;
                            if (holderForReview.getPage() > 0) {
                                loadMoreSuccess(bookMarketBooklistPtrClassicFrameLayout, false);
                            } else if (holderForReview.getPage() == 0) {
                                refreshSuccess(bookMarketBooklistPtrClassicFrameLayout, false);
                            }
                            mBookListAdapter.notifyDataSetChanged();
                            dismissLoadingDialig();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            if (holderForReview.getPage() > 0) {
                                holderForReview.loadMoreFailed();
                                loadMoreFailed(bookMarketBooklistPtrClassicFrameLayout);
                            } else if (holderForReview.getPage() == 0) {
                                refreshFailed(bookMarketBooklistPtrClassicFrameLayout);
                            }
                            dismissLoadingDialig();
                        }
                    });
        } else if (request == 1) {
            OkGo.get(Urls.BookMarketAppBoms)
                    .tag(this)
                    .params("page", holderForReview.getPage() + 1)
                    .params("pageNum", "5")
                    .params(classify)
                    .execute(new JsonCallback<IycResponse<List<MarketBookListItem>>>(this) {
                        @Override
                        public void onSuccess(IycResponse<List<MarketBookListItem>> listIycResponse, Call call, Response response) {
                            if (holderForReview.isRefresh())
                                bookList.clear();
                            for (MarketBookListItem bookIntroduction : listIycResponse.getData())
                                bookList.add(bookIntroduction);
                            boolean isEnd = listIycResponse.getData().size() < 5;
                            if (holderForReview.getPage() > 0) {
                                loadMoreSuccess(bookMarketBooklistPtrClassicFrameLayout, isEnd);
                            } else if (holderForReview.getPage() == 0) {
                                refreshSuccess(bookMarketBooklistPtrClassicFrameLayout, isEnd);
                            }
                            mBookListAdapter.notifyDataSetChanged();
                            dismissLoadingDialig();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            if (holderForReview.getPage() > 0) {
                                holderForReview.loadMoreFailed();
                                loadMoreFailed(bookMarketBooklistPtrClassicFrameLayout);
                            } else if (holderForReview.getPage() == 0) {
                                refreshFailed(bookMarketBooklistPtrClassicFrameLayout);
                            }
                            dismissLoadingDialig();
                        }
                    });
        } else if (request == 3) {
            OkGo.get(Urls.BookMarketClass)
                    .tag(this)
                    .params("page", holderForReview.getPage() + 1)
                    .params("pageNum", "5")
                    .params(classify)
                    .execute(new JsonCallback<IycResponse<List<MarketBookListItem>>>(this) {
                        @Override
                        public void onSuccess(IycResponse<List<MarketBookListItem>> listIycResponse, Call call, Response response) {
                            if (holderForReview.isRefresh())
                                bookList.clear();
                            for (MarketBookListItem bookIntroduction : listIycResponse.getData())
                                bookList.add(bookIntroduction);
                            boolean isEnd = listIycResponse.getData().size() < 5;
                            if (holderForReview.getPage() > 0) {
                                loadMoreSuccess(bookMarketBooklistPtrClassicFrameLayout, isEnd);
                            } else if (holderForReview.getPage() == 0) {
                                refreshSuccess(bookMarketBooklistPtrClassicFrameLayout, isEnd);
                            }
                            mBookListAdapter.notifyDataSetChanged();
                            dismissLoadingDialig();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            if (holderForReview.getPage() > 0) {
                                holderForReview.loadMoreFailed();
                                loadMoreFailed(bookMarketBooklistPtrClassicFrameLayout);
                            } else if (holderForReview.getPage() == 0) {
                                refreshFailed(bookMarketBooklistPtrClassicFrameLayout);
                            }
                            dismissLoadingDialig();
                        }
                    });
        } else if (currentListType == 6) {
            OkGo.get(Urls.BookMarketPointBookListURL)
                    .tag(this)
                    .params("page", holderForReview.getPage() + 1)
                    .params("pageNum", "5")
                    .execute(new JsonCallback<IycResponse<List<GeneralBookInfo>>>(this) {
                        @Override
                        public void onSuccess(IycResponse<List<GeneralBookInfo>> listIycResponse, Call call, Response response) {
                            if (holderForReview.isRefresh())
                                bookList.clear();
                            if (listIycResponse.getData() == null) {
                                loadMoreSuccess(bookMarketBooklistPtrClassicFrameLayout, true);
                            } else {
                                for (GeneralBookInfo book : listIycResponse.getData()) {
                                    bookList.add(generalToItem(book));
                                }
                                if (holderForReview.getPage() == 0 && listIycResponse.getData().size() < 5) {
                                    bookMarketBooklistPtrClassicFrameLayout.setLoadMoreEnable(false);
                                }
                                boolean isEnd = listIycResponse.getData().size() < 5;
                                if (holderForReview.getPage() > 0) {
                                    loadMoreSuccess(bookMarketBooklistPtrClassicFrameLayout, isEnd);
                                } else if (holderForReview.getPage() == 0) {
                                    refreshSuccess(bookMarketBooklistPtrClassicFrameLayout, isEnd);
                                }
                                mBookListAdapter.notifyDataSetChanged();
                                dismissLoadingDialig();
                            }

                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            if (holderForReview.getPage() > 0) {
                                holderForReview.loadMoreFailed();
                                loadMoreFailed(bookMarketBooklistPtrClassicFrameLayout);
                            }
                            dismissLoadingDialig();
                        }
                    });
        } else if (currentListType == 7) {
            OkGo.get(Urls.BookMarketHotBookListURL)
                    .tag(this)
                    .params("page", holderForReview.getPage() + 1)
                    .params("pageNum", holderForReview.getPageSize())
                    .execute(new JsonCallback<IycResponse<List<MarketBookListItem>>>(this) {
                        @Override
                        public void onSuccess(IycResponse<List<MarketBookListItem>> listIycResponse, Call call, Response response) {
//                            if (holderForReview.isRefresh()) {
//                                bookList.clear();
//                            }
//                            if (listIycResponse.getData() == null) {
//                                if (bookMarketBooklistPtrClassicFrameLayout != null) {
//                                    refreshSuccess(bookMarketBooklistPtrClassicFrameLayout, false);
//                                    loadMoreSuccess(bookMarketBooklistPtrClassicFrameLayout, true);
//                                }
//                            } else {
//                                for (MarketBookListItem bookIntroduction : listIycResponse.getData())
//                                    bookList.add(bookIntroduction);
//                                if (bookMarketBooklistPtrClassicFrameLayout != null) {
//                                    refreshSuccess(bookMarketBooklistPtrClassicFrameLayout, false);
//                                    loadMoreSuccess(bookMarketBooklistPtrClassicFrameLayout, true);
//                                }
//                                mBookListAdapter.notifyDataSetChanged();
//                            }
//                            dismissLoadingDialig();
                            layoutNoContent.setVisibility(View.GONE);
                            if(NotNullUtils.isNull(listIycResponse)||NotNullUtils.isNull(listIycResponse.getData())){
                                if(holderForReview.isRefresh())
                                    refreshFailed(bookMarketBooklistPtrClassicFrameLayout);
                                else
                                    loadMoreFailed(bookMarketBooklistPtrClassicFrameLayout);
                                if(holderForReview.getPage()==0) {
                                    bookList.clear();
                                    mBookListAdapter.notifyDataSetChanged();
                                    layoutNoContent.setVisibility(View.VISIBLE);
                                }
                                return;
                            }
                            if (holderForReview.isRefresh()) {
                                bookList.clear();
                            }
                            bookList.addAll(listIycResponse.getData());
                            boolean isEnd = listIycResponse.getData().size() < holderForReview.getPageSize();
                            if(holderForReview.isRefresh()){
                                refreshSuccess(bookMarketBooklistPtrClassicFrameLayout,false);
                                addListHeader();
                            }else{
                                loadMoreSuccess(bookMarketBooklistPtrClassicFrameLayout,false);
                            }
                            mBookListAdapter.notifyDataSetChanged();
                            dismissLoadingDialig();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            if (holderForReview.getPage() > 0) {
                                holderForReview.loadMoreFailed();
                                loadMoreFailed(bookMarketBooklistPtrClassicFrameLayout);
                            } else if (holderForReview.getPage() == 0) {
                                refreshFailed(bookMarketBooklistPtrClassicFrameLayout);
                            }
                            dismissLoadingDialig();
                        }
                    });
//                    .execute(new JsonCallback<IycResponse<List<GeneralBookInfo>>>(this) {
//                        @Override
//                        public void onSuccess(IycResponse<List<GeneralBookInfo>> listIycResponse, Call call, Response response) {
//                            if (holderForReview.isRefresh())
//                                bookList.clear();
//                            for (GeneralBookInfo book : listIycResponse.getData()) {
//                                bookList.add(generalToItem(book));
//                            }
//                            if (holderForReview.getPage() == 0 && listIycResponse.getData().size() < 5) {
//                                bookMarketBooklistPtrClassicFrameLayout.setLoadMoreEnable(false);
//                            }
//                            boolean isEnd = listIycResponse.getData().size() < 5;
//                            if (holderForReview.getPage() > 0) {
//                                loadMoreSuccess(bookMarketBooklistPtrClassicFrameLayout, isEnd);
//                            }
//                            mBookListAdapter.notifyDataSetChanged();
//                            dismissLoadingDialig();
//                        }
//
//                        @Override
//                        public void onError(Call call, Response response, Exception e) {
//                            if (holderForReview.getPage() > 0) {
//                                holderForReview.loadMoreFailed();
//                                loadMoreFailed(bookMarketBooklistPtrClassicFrameLayout);
//                            }
//                            dismissLoadingDialig();
//                        }
//                    });
        } else if (currentListType == 8) {
            //按标签请求数据
            OkGo.get(Urls.BookMarketTagsBookListURL)
                    .tag(this)
                    .params("pageNo", holderForReview.getPage() + 1)
                    .params("pageSize", "5")
                    .params("tags", titleName)
                    .execute(new JsonCallback<IycResponse<List<MarketBookListItem>>>(this) {
                        @Override
                        public void onSuccess(IycResponse<List<MarketBookListItem>> listIycResponse, Call call, Response response) {
                            if (holderForReview.isRefresh()) {
                                bookList.clear();
                            }
                            if (listIycResponse.getData() == null) {
                                loadMoreSuccess(bookMarketBooklistPtrClassicFrameLayout, true);
                            } else {
                                for (MarketBookListItem bookIntroduction : listIycResponse.getData())
                                    bookList.add(bookIntroduction);
                                if (holderForReview.getPage() == 0) {
                                    if (listIycResponse.getData().size() < 5) {
                                        bookMarketBooklistPtrClassicFrameLayout.setLoadMoreEnable(false);
                                    }
                                    addListHeader();
                                }
                                boolean isEnd = listIycResponse.getData().size() < 5;
                                if (holderForReview.getPage() > 0) {
                                    loadMoreSuccess(bookMarketBooklistPtrClassicFrameLayout, isEnd);
                                } else if (holderForReview.getPage() == 0) {
                                    refreshSuccess(bookMarketBooklistPtrClassicFrameLayout, isEnd);
                                    addListHeader();
                                }
                                mBookListAdapter.notifyDataSetChanged();
                            }
                            dismissLoadingDialig();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            if (holderForReview.getPage() > 0) {
                                holderForReview.loadMoreFailed();
                                loadMoreFailed(bookMarketBooklistPtrClassicFrameLayout);
                            } else if (holderForReview.getPage() == 0) {
                                refreshFailed(bookMarketBooklistPtrClassicFrameLayout);
                            }
                            dismissLoadingDialig();
                        }
                    });
        }
    }

    public void customBaseDialog(int sign, List<BookListCategory> subjectList, List<BookListCategory> versionList, List<BookListCategory> gradetList) {
        final CustomBaseDialog dialog = new CustomBaseDialog(this, sign, subjectList, versionList, gradetList, selectedButtons);
        dialog.setsortCallback(this);
        dialog.show();
    }

    @Override
    public void getsort(String selectSub, String selectVer, String selectGra, int sign) {
        layoutNoContent.setVisibility(View.GONE);
        if (sign == 1) {
            if (selectSub.equals("")) {
                hashMap.put("category", "all");
            } else {
                hashMap.put("category", selectSub);
            }
            if (selectVer.equals("")) {
                hashMap.put("languageStr", "all");
            } else {
                hashMap.put("languageStr", selectVer);
            }
            if (selectGra.equals("")) {
                hashMap.put("difficultyStr", "all");
            } else {
                hashMap.put("difficultyStr", selectGra);
            }
        } else {
            classify.put("difficultyStr", selectGra);
            classify.put("languageStr", selectVer);
            classify.put("category", getIntent().getStringExtra("classifyImgType"));
            hashMap.put("languageStr", selectVer);
            hashMap.put("difficultyStr", selectGra);
        }
        bookMarketBooklistPtrClassicFrameLayout.autoRefresh();
    }

    @Override
    public void getSortIndex(String selectedIndexs) {
        selectedButtons.setLength(0);
        selectedButtons.append(selectedIndexs);
    }

    public String getStringFromList(List<String> selectList) {
        StringBuilder sfl = new StringBuilder("");
        int i;
        if (selectList == null) {
            return "";
        }
        for (i = 0; i < selectList.size(); i++) {
            if (i == 0) {
                sfl.append(selectList.get(i));
            } else {
                sfl.append("," + selectList.get(i));
            }
        }
        return sfl.toString();
    }

    //dialog回调
    public void getClassify() {
        request = getIntent().getIntExtra("request", 0);
        if (request == 1) {
            titleName = getIntent().getStringExtra("classifyName");
            classify.put("difficultyStr", "all");
            classify.put("languageStr", "all");
            classify.put("category", getIntent().getStringExtra("classifyImgType"));
        } else if (request == 2) {
            titleName = getIntent().getStringExtra("name");
            hashMap.put("difficultyStr", "all");
            hashMap.put("languageStr", "all");
            String str = getIntent().getStringExtra("name");
            hashMap.put("category", str);
        } else if (request == 3) {
            titleName = getIntent().getStringExtra("name");
            classify.put("difficultyStr", "");
            classify.put("languageStr", "");
            String str = getIntent().getStringExtra("topicId");
            classify.put("id", str);
        }
    }

    public void datasClear() {
        bookList.clear();
        mBookListAdapter.notifyDataSetChanged();
    }

    private void addAllToShopCart() {
        showLoadingDialog("图书批量添加中，请稍后...");
//        String books = "";
//        for (int i = 0; i < bookList.size(); i++) {
//            if (i == 0) {
//                books = books + bookList.get(i).getBookId();
//            } else {
//                books = books + "," + bookList.get(i).getBookId();
//            }
//        }
        OkGo.get(Urls.BookMarketAddAllToShoppingCartURL)
                .tag(this)
                .params("userid", CommonUtil.getUserId())
                .params("books", books.replaceAll(" ", ""))
                .execute(new JsonCallback<IycResponse<ShoppingInfo>>(this) {
                    @Override
                    public void onSuccess(IycResponse<ShoppingInfo> shoppingInfoIycResponse, Call call, Response response) {
                        dismissLoadingDialig();
                        ToastCompat.makeText(BookMarketBookListActivity.this, "一键添加到购物车成功", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(BookMarketBookListActivity.this, MineShoppingActivity.class));
                    }

                    public void onError(Call call, Response response, Exception e) {
                        dismissLoadingDialig();
                        ToastCompat.makeText(BookMarketBookListActivity.this, "添加到购物车失败，请重新添加", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    class ShoppingInfo {
        List<String> books;
    }

    public void addListHeader() {
        if (bookList != null && bookList.size() != 0) {
            if (bookList.get(0).getBookKind() == 2) {
                if (mBookListAdapter.getHeadSize() == 0 && bookList.get(0).getBookImageUrl() != null) {
                    mBookListAdapter.addHeader(RecycleViewUtils.getHeaderView(context, bookList.get(0).getBookImageUrl(), R.drawable.pic_default_special_topic));
                }
                bookList.remove(0);
            }
        }
    }

    public MarketBookListItem generalToItem(GeneralBookInfo book) {
        MarketBookListItem item = new MarketBookListItem();
        item.setBookAuthor(book.getBookAuthor());
        item.setBookId(book.getBookId() + "");
        item.setBookImageUrl(book.getBookImageUrl());
        item.setBookIntroduction(book.getBookIntroduction());
        item.setBookKind(book.getBookKind());
        item.setBookLanguage(book.getBookLanguage());
        item.setBookName(book.getBookName());
        item.setBookPrice(book.getPrice());
        item.setBookRating(Integer.parseInt(book.getBookRating()));
        item.setBookSpecialPrice(book.getSpecial_price());
        item.setBookTypeList(book.getTags());
        return item;
    }

    private void getStrSubjectFromNet() {
        OkGo.get(Urls.BookMarketBookClassifyURL)
                .tag(this)
                .execute(new JsonCallback<IycResponse<BookClassify>>(this) {
                    @Override
                    public void onSuccess(IycResponse<BookClassify> iycResponse, Call call, Response response) {
                        if (iycResponse != null && iycResponse.getData().getCommonBook() != null) {
                            for (BookClassify.CommonBook commonBook : iycResponse.getData().getCommonBook()) {
                                BookListCategory category = new BookListCategory();
                                category.setType(commonBook.getName());
                                category.setTypeId("" + commonBook.getId());
                                subjectList.add(category);
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                    }
                });
    }

    /**
     * @param type 获取头图
     *             1通识读物 2新书 3优惠 4免费 5包月
     */
    public void addBanner(int type) {
        OkGo.get(Urls.BookMarketGetBanner)
                .tag(this)
                .params("type", type)
                .execute(new JsonCallback<IycResponse<BannerImg>>(this) {
                    @Override
                    public void onSuccess(IycResponse<BannerImg> bannerImgIycResponse, Call call, Response response) {
                        if (mBookListAdapter.getHeadSize() == 0) {
                            if (bannerImgIycResponse.getData().getImgUrl() != null) {
                                mBookListAdapter.addHeader(RecycleViewUtils.getHeaderView(context, bannerImgIycResponse.getData().getImgUrl(), R.drawable.pic_default_special_topic));
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {

                    }
                });
    }
}
