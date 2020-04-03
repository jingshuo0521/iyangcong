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
import com.iyangcong.reader.MainActivity;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.BookIntroduceAdapter;
import com.iyangcong.reader.bean.MarketBookListItem;
import com.iyangcong.reader.bean.SearchedBook;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.ui.TagColor;
import com.iyangcong.reader.ui.TagGroup;
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
 * Created by ljw on 2016/12/24.
 */

public class BookMarketSearchActivity extends SwipeBackActivity implements TextView.OnEditorActionListener {
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
    RecyclerView rvBookIntroduction;
    @BindView(R.id.ll_book_list)
    LinearLayout llBookList;
    @BindView(R.id.search_ptrClassicFrameLayout)
    PtrClassicFrameLayout searchPtrClassicFrameLayout;


    @OnClick({R.id.bt_search, R.id.btnFunction, R.id.tvDeleteHistory, R.id.btnBack,R.id.im_scan})
    void OnButtonClick(View view) {
        switch (view.getId()) {
            case R.id.bt_search:
                doSearchAction();
                break;
            case R.id.tvDeleteHistory:
                sharedPreferenceUtil.removeArray(SharedPreferenceUtil.SEARCH_HISTORY + CommonUtil.getUserId());
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
                Intent i = new Intent(BookMarketSearchActivity.this, SimpleCaptureActivity.class);
                int REQUEST_QR_CODE = 1;
                BookMarketSearchActivity.this.startActivityForResult(i, REQUEST_QR_CODE);
                break;
        }
    }

    private void doSearchAction() {
        if (!etSearch.getText().toString().equals("")) {
            keyWord = etSearch.getText().toString();
            searchPtrClassicFrameLayout.autoRefresh();
            if (!searchList.contains(keyWord)) {
                sharedPreferenceUtil.addArray(SharedPreferenceUtil.SEARCH_HISTORY + CommonUtil.getUserId(), keyWord);
                setHistorySearchList();
                showHotWord(gvSearchHistory, historySearchList);
            }
        }
    }


    private int hotIndex = 0;
    private List<String> hotSearchList = new ArrayList<String>();
    private List<String> historySearchList = new ArrayList<String>();
    private List<String> searchList = new ArrayList<String>();

    private List<MarketBookListItem> bookList = new ArrayList<>();
    private SearchedBook searchedBook;

    private String keyWord;
    private int pageSize = 8;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private BookIntroduceAdapter bookIntroduceAdapter;

    Handler handler = new Handler();
    private LoadCountHolder holderForReview = new LoadCountHolder();
    private RecyclerAdapterWithHF mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_market_search);
        ButterKnife.bind(this);
        initView();
    }

    protected void initView() {
        setMainHeadView();
        gvSearchHot.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                keyWord = tag;
                etSearch.setText(tag);

                searchPtrClassicFrameLayout.autoRefresh();
                if (!searchList.contains(tag)) {
                    sharedPreferenceUtil.addArray(SharedPreferenceUtil.SEARCH_HISTORY + CommonUtil.getUserId(), tag);
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
                    sharedPreferenceUtil.addArray(SharedPreferenceUtil.SEARCH_HISTORY + CommonUtil.getUserId(), tag);
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

        bookIntroduceAdapter = new BookIntroduceAdapter(this, bookList);
        mAdapter = new RecyclerAdapterWithHF(bookIntroduceAdapter);
        rvBookIntroduction.setAdapter(mAdapter);
        GridLayoutManager manager = new GridLayoutManager(this, 1);
        rvBookIntroduction.setLayoutManager(manager);
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

    private void doSearch() {
        if (holderForReview.getPage() == 0) {
            showLoadingDialog();
        }
        OkGo.get(Urls.BookMarketSearchURL)
                .tag(this)
                .params("keyword", keyWord)
                .params("pageSize", pageSize)
                .params("currentPageNum", holderForReview.getPage() + 1)
                .params("userId", CommonUtil.getUserId())
                .params("categoryId",0)
                .params("courseId",0)
                .params("languageTypeId",0)
                .params("languageDiffcultyId",0)
                .execute(new JsonCallback<IycResponse<SearchedBook>>(this) {
                    @Override
                    public void onSuccess(IycResponse<SearchedBook> listIycResponse, Call call, Response response) {
                        if (holderForReview.isRefresh()) {
                            bookList.clear();
                        }
                        searchedBook = listIycResponse.getData();
                        if (searchedBook != null) {
                            if (searchedBook.getTotalResultNum() > 0) {
                                bookList.addAll(searchedBook.getBookInfoList());
                                changeShowView(false);
                                if (holderForReview.getPage() == 0 && listIycResponse.getData().getBookInfoList().size() < 8) {
                                    searchPtrClassicFrameLayout.setLoadMoreEnable(false);
                                }
                                boolean isEnd = listIycResponse.getData().getBookInfoList().size() < 8;
                                if (holderForReview.getPage() > 0) {
                                    loadMoreSuccess(searchPtrClassicFrameLayout, isEnd);
                                }
                                mAdapter.notifyDataSetChanged();
                            }else{
                                ToastCompat.makeText(BookMarketSearchActivity.this,"搜索结果为空", Toast.LENGTH_SHORT).show();
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
                            ToastCompat.makeText(BookMarketSearchActivity.this,"搜索结果为空",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
        searchList = sharedPreferenceUtil.getArray(SharedPreferenceUtil.SEARCH_HISTORY + CommonUtil.getUserId());
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
