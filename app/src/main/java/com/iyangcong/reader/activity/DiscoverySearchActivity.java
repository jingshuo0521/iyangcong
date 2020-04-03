package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.DiscoverCircleListAdapter;
import com.iyangcong.reader.adapter.DiscoverTopicAdapter;
import com.iyangcong.reader.adapter.MinePageThoughtToBookAdapter;
import com.iyangcong.reader.bean.DiscoverCircleItemContent;
import com.iyangcong.reader.bean.DiscoverTopic;
import com.iyangcong.reader.bean.MineToughtToBook;
import com.iyangcong.reader.bean.SearchedCircle;
import com.iyangcong.reader.bean.SearchedReview;
import com.iyangcong.reader.bean.SearchedTopic;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.TagColor;
import com.iyangcong.reader.ui.TagGroup;
import com.iyangcong.reader.ui.customtablayout.listener.OnTabSelectListener;
import com.iyangcong.reader.ui.customtablayout.widget.SegmentTabLayout;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.LoadCountHolder;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by gft at 2019-10-11
 * 圈子、话题的搜索界面
 */

public class DiscoverySearchActivity extends SwipeBackActivity implements TextView.OnEditorActionListener {
    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.im_scan)
    ImageButton ImScan;
    @BindView(R.id.bt_search)
    TextView btSearch;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.gv_search_hot)
    TagGroup gvSearchHot;
    @BindView(R.id.gv_search_history)
    TagGroup gvSearchHistory;

    @BindView(R.id.tvChangeWords)
    TextView mTvChangeWords;
    @BindView(R.id.tvDeleteHistory)
    TextView tvDeleteHistory;
    @BindView(R.id.ll_search_tip)
    LinearLayout llSearchTip;
    @BindView(R.id.rv_book_introduction)
    RecyclerView rvcommonIntroduction;
    @BindView(R.id.ll_book_list)
    LinearLayout llBookList;
    @BindView(R.id.seg_tab_sort)
    SegmentTabLayout segTabSort;
    @BindView(R.id.search_ptrClassicFrameLayout)
    PtrClassicFrameLayout searchPtrClassicFrameLayout;
    private String[] sortTitles = new String[]{"圈子", "话题", "书评"};
    private int SORT_SIGN = 1;//搜索类型：1为圈子，2为话题，3为书评
//    private String searchUrl = Urls.DiscoverCircleSearchURL;


    @OnClick({R.id.bt_search, R.id.btnFunction, R.id.tvDeleteHistory, R.id.btnBack,R.id.im_scan})
    void OnButtonClick(View view) {
        switch (view.getId()) {
            case R.id.bt_search:
                doSearchAction();
                break;
            case R.id.tvDeleteHistory:
                sharedPreferenceUtil.removeArray(SharedPreferenceUtil.SEARCH_CIRCLE_HISTORY + CommonUtil.getUserId());
                historySearchList.clear();
                showHotWord(gvSearchHistory, historySearchList);
                break;
            case R.id.tvChangeWords:
                showHotSearch();
                break;
            case R.id.btnBack:
                if (llBookList.getVisibility() == View.VISIBLE) {
                    changeShowView(true);
                    etSearch.setText("");
                } else {
                    finish();
                }
                break;
            case R.id.im_scan:
                Intent i = new Intent(DiscoverySearchActivity.this, SimpleCaptureActivity.class);
                int REQUEST_QR_CODE = 1;
                DiscoverySearchActivity.this.startActivityForResult(i, REQUEST_QR_CODE);
                break;
        }
    }

    private void doSearchAction() {
        if (!etSearch.getText().toString().equals("")) {
            keyWord = etSearch.getText().toString();
            searchPtrClassicFrameLayout.autoRefresh();
            if (!searchList.contains(keyWord)) {
                sharedPreferenceUtil.addArray(SharedPreferenceUtil.SEARCH_CIRCLE_HISTORY + CommonUtil.getUserId(), keyWord);
                setHistorySearchList();
                showHotWord(gvSearchHistory, historySearchList);
            }
        }
    }


    private int hotIndex = 0;
    private List<String> hotSearchList = new ArrayList<String>();
    private List<String> historySearchList = new ArrayList<String>();
    private List<String> searchList = new ArrayList<String>();

    private List<DiscoverCircleItemContent> circleList = new ArrayList<>();
    private SearchedCircle searchedCircle;
    private SearchedTopic searchedTopic;
    private SearchedReview searchedReview;
    private List<DiscoverTopic> topicList = new ArrayList<>();
    private List<MineToughtToBook> reviewsList = new ArrayList<>();

    private String keyWord;
    private int pageSize = 8;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private DiscoverCircleListAdapter circleListAdapter;
    private DiscoverTopicAdapter discoverTopicListAdapter;
    private MinePageThoughtToBookAdapter discoverReviewListAdapter;

    Handler handler = new Handler();
    private LoadCountHolder holderForReview = new LoadCountHolder();
    private RecyclerAdapterWithHF mCircleAdapter;
    private RecyclerAdapterWithHF mTopicAdapter;
    private RecyclerAdapterWithHF mReviewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery_search);
        ButterKnife.bind(this);
        initView();
    }

    protected void initView() {
        setMainHeadView();
        segTabSort.setTabData(sortTitles);

        gvSearchHot.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                keyWord = tag;
                etSearch.setText(tag);

                searchPtrClassicFrameLayout.autoRefresh();
                if (!searchList.contains(tag)) {
                    sharedPreferenceUtil.addArray(SharedPreferenceUtil.SEARCH_CIRCLE_HISTORY + CommonUtil.getUserId(), tag);
                    setHistorySearchList();
                    showHotWord(gvSearchHistory, historySearchList);
                }
            }
        });
        gvSearchHistory.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                keyWord = tag;
                etSearch.setText(tag);

                searchPtrClassicFrameLayout.autoRefresh();
                if (!searchList.contains(tag)) {
                    sharedPreferenceUtil.addArray(SharedPreferenceUtil.SEARCH_CIRCLE_HISTORY + CommonUtil.getUserId(), tag);
                    setHistorySearchList();
                    showHotWord(gvSearchHistory, historySearchList);
                }
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("")) {
                    changeShowView(true);
                }
            }
        });
        etSearch.setOnEditorActionListener(this);
        circleListAdapter = new DiscoverCircleListAdapter(this, circleList);
        discoverTopicListAdapter = new DiscoverTopicAdapter(this, topicList,true); //new DiscoverTopicListAdapter(this, bookList);
        discoverReviewListAdapter = new MinePageThoughtToBookAdapter(this,reviewsList);
        mCircleAdapter = new RecyclerAdapterWithHF(circleListAdapter);
        mTopicAdapter = new RecyclerAdapterWithHF(discoverTopicListAdapter);
        mReviewsAdapter = new RecyclerAdapterWithHF(discoverReviewListAdapter);
        rvcommonIntroduction.setNestedScrollingEnabled(false);
        rvcommonIntroduction.setAdapter(mCircleAdapter);
        segTabSort.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                showLoadingDialog();
                switch (position) {
                    case 0:
                        SORT_SIGN = 1;
                        rvcommonIntroduction.setAdapter(mCircleAdapter);
                        searchCircle();
                        break;
                    case 1:
                        SORT_SIGN = 2;
                        rvcommonIntroduction.setAdapter(mTopicAdapter);
                        searchTopic();
                        break;
                    case 2:
                        SORT_SIGN = 3;
                        rvcommonIntroduction.setAdapter(mReviewsAdapter);
                        searchReviews();
                        break;
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        GridLayoutManager manager = new GridLayoutManager(this, 1);
        rvcommonIntroduction.setLayoutManager(manager);
        showHotSearch();
        showHotWord(gvSearchHistory, historySearchList);

        searchPtrClassicFrameLayout.setHorizontalScrollBarEnabled(false);

        searchPtrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return false;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        holderForReview.refresh();
                        doSearch();
                        searchPtrClassicFrameLayout.refreshComplete();
                        searchPtrClassicFrameLayout.setLoadMoreEnable(true);
                    }
                });
            }
        });
        searchPtrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        holderForReview.loadMore();
                        doSearch();
                        searchPtrClassicFrameLayout.loadMoreComplete(true);
//                        Toast.makeText(R ecyclerViewActivity.this, "load more complete", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showHotSearch() {
        OkGo.get(Urls.BookMarketHotSearchURL)
                .tag(this)
                .params("topNum", "8")
                .execute(new JsonCallback<IycResponse<HotSearchWord>>(this) {
                    @Override
                    public void onSuccess(IycResponse<HotSearchWord> listIycResponse, Call call, Response response) {
                        hotSearchList.clear();
                        hotSearchList.addAll(listIycResponse.getData().getHotSearchWords());
                        showHotWord(gvSearchHot, hotSearchList);
                    }

                });
    }

    private void searchCircle(){
        OkGo.get(Urls.DiscoverCircleSearchURL)
                .tag(this)
                .params("keyword", keyWord)
                .params("pageSize", pageSize)
                .params("currentPageNum", holderForReview.getPage() + 1)
                .params("userId", CommonUtil.getUserId())
                .params("categoryId",0)
                .params("courseId",0)
                .params("languageTypeId",0)
                .params("languageDiffcultyId",0)
                .execute(new JsonCallback<IycResponse<SearchedCircle>>(this) {
                    @Override
                    public void onSuccess(IycResponse<SearchedCircle> listIycResponse, Call call, Response response) {
                        if (holderForReview.isRefresh()) {
                            circleList.clear();
                        }
                        searchedCircle = listIycResponse.getData();
                        if (searchedCircle != null) {
                            if (searchedCircle.getTotalResultNum() > 0) {
                                circleList.addAll(searchedCircle.getCircleInfoList());
                                changeShowView(false);
                                if (holderForReview.getPage() == 0 && listIycResponse.getData().getCircleInfoList().size() < 8) {
                                    searchPtrClassicFrameLayout.setLoadMoreEnable(false);
                                }
                                boolean isEnd = listIycResponse.getData().getCircleInfoList().size() < 8;
                                if (holderForReview.getPage() > 0) {
                                    loadMoreSuccess(searchPtrClassicFrameLayout, isEnd);
                                }
                                mCircleAdapter.notifyDataSetChanged();
                            }else{
                                ToastCompat.makeText(DiscoverySearchActivity.this,"搜索结果为空", Toast.LENGTH_SHORT).show();
                            }
                        }
                        dismissLoadingDialig();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (holderForReview.getPage() > 0) {
                            holderForReview.loadMoreFailed();
                            loadMoreFailed(searchPtrClassicFrameLayout);
                        }
                        dismissLoadingDialig();
                        if(e.getMessage().contains("服务器数据异常!")){
                            ToastCompat.makeText(DiscoverySearchActivity.this,"搜索结果为空",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void searchTopic(){
        OkGo.get(Urls.DiscoverTopicSearchURL)
                .tag(this)
                .params("keyword", keyWord)
                .params("pageSize", pageSize)
                .params("currentPageNum", holderForReview.getPage() + 1)
                .params("userId", CommonUtil.getUserId())
                .params("categoryId",0)
                .params("courseId",0)
                .params("languageTypeId",0)
                .params("languageDiffcultyId",0)
                .execute(new JsonCallback<IycResponse<SearchedTopic>>(this) {
                    @Override
                    public void onSuccess(IycResponse<SearchedTopic> listIycResponse, Call call, Response response) {
                        if (holderForReview.isRefresh()) {
                            topicList.clear();
                        }
                        searchedTopic = listIycResponse.getData();
                        if (searchedTopic != null) {
                            if (searchedTopic.getTotalResultNum() > 0) {
                                topicList.addAll(searchedTopic.getCircleInfoList());
                                changeShowView(false);
                                if (holderForReview.getPage() == 0 && listIycResponse.getData().getCircleInfoList().size() < 8) {
                                    searchPtrClassicFrameLayout.setLoadMoreEnable(false);
                                }
                                boolean isEnd = listIycResponse.getData().getCircleInfoList().size() < 8;
                                if (holderForReview.getPage() > 0) {
                                    loadMoreSuccess(searchPtrClassicFrameLayout, isEnd);
                                }
                                mTopicAdapter.notifyDataSetChanged();
                            }else{
                                ToastCompat.makeText(DiscoverySearchActivity.this,"搜索结果为空", Toast.LENGTH_SHORT).show();
                            }
                        }
                        dismissLoadingDialig();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (holderForReview.getPage() > 0) {
                            holderForReview.loadMoreFailed();
                            loadMoreFailed(searchPtrClassicFrameLayout);
                        }
                        dismissLoadingDialig();
                        if(e.getMessage().contains("服务器数据异常!")){
                            ToastCompat.makeText(DiscoverySearchActivity.this,"搜索结果为空",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void searchReviews(){
        OkGo.get(Urls.DiscoverReviewSearchURL)
                .tag(this)
                .params("keyword", keyWord)
                .params("pageSize", pageSize)
                .params("currentPageNum", holderForReview.getPage() + 1)
                .params("userId", CommonUtil.getUserId())
                .params("categoryId",0)
                .params("courseId",0)
                .params("languageTypeId",0)
                .params("languageDiffcultyId",0)
                .execute(new JsonCallback<IycResponse<SearchedReview>>(this) {
                    @Override
                    public void onSuccess(IycResponse<SearchedReview> listIycResponse, Call call, Response response) {
                        if (holderForReview.isRefresh()) {
                            circleList.clear();
                        }
                        searchedReview = listIycResponse.getData();
                        if (searchedReview != null) {
                            if (searchedReview.getTotalResultNum() > 0) {
                                reviewsList.addAll(searchedReview.getCircleInfoList());
                                changeShowView(false);
                                if (holderForReview.getPage() == 0 && listIycResponse.getData().getCircleInfoList().size() < 8) {
                                    searchPtrClassicFrameLayout.setLoadMoreEnable(false);
                                }
                                boolean isEnd = listIycResponse.getData().getCircleInfoList().size() < 8;
                                if (holderForReview.getPage() > 0) {
                                    loadMoreSuccess(searchPtrClassicFrameLayout, isEnd);
                                }
                                mReviewsAdapter.notifyDataSetChanged();
                            }else{
                                ToastCompat.makeText(DiscoverySearchActivity.this,"搜索结果为空", Toast.LENGTH_SHORT).show();
                            }
                        }
                        dismissLoadingDialig();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (holderForReview.getPage() > 0) {
                            holderForReview.loadMoreFailed();
                            loadMoreFailed(searchPtrClassicFrameLayout);
                        }
                        dismissLoadingDialig();
                        if(e.getMessage().contains("服务器数据异常!")){
                            ToastCompat.makeText(DiscoverySearchActivity.this,"搜索结果为空",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void doSearch() {
        if (holderForReview.getPage() == 0) {
            showLoadingDialog();
        }
        switch (SORT_SIGN) {
            case 1:
            //圈子
                searchCircle();
                break;
            case 2:
            //话题
                searchTopic();
                break;
            case 3:
                //书评
                searchReviews();
                break;
        }

    }

    private void changeShowView(boolean researchTips) {
        if (researchTips) {
            llBookList.setVisibility(View.GONE);
            llSearchTip.setVisibility(View.VISIBLE);
        } else {
            llBookList.setVisibility(View.VISIBLE);
            llSearchTip.setVisibility(View.GONE);
        }
    }

    /**
     * 每次显示8个热搜词
     */
    private synchronized void showHotWord(TagGroup tagGroup, List<String> list) {
        int tagSize = list.size();
        String[] tags = new String[tagSize];
        for (int j = 0; j < list.size(); hotIndex++, j++) {
            tags[j] = list.get(hotIndex % list.size());
        }
        List<TagColor> colors = TagColor.getRandomColors(tagSize);
        tagGroup.setTags(colors, tags);
    }

    @Override
    protected void setMainHeadView() {
        btnFunction.setVisibility(View.GONE);
        textHeadTitle.setText(R.string.search);
        btnBack.setImageResource(R.drawable.btn_back);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
        setHistorySearchList();
    }

    private void setHistorySearchList() {
        searchList = sharedPreferenceUtil.getArray(SharedPreferenceUtil.SEARCH_CIRCLE_HISTORY + CommonUtil.getUserId());
        historySearchList.clear();
        if (searchList.size() > 0) {
            for (int i = searchList.size() - 1; i > -1 && historySearchList.size() < 8; i--) {
                historySearchList.add(searchList.get(i));
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        doSearchAction();
        return true;
    }

    class HotSearchWord {
        private List<String> hotSearchWords;

        public List<String> getHotSearchWords() {
            return hotSearchWords;
        }

        public void setHotSearchWords(List<String> hotSearchWords) {
            this.hotSearchWords = hotSearchWords;
        }
    }
}
