package com.iyangcong.reader.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.ShelfCloudAdapter;
import com.iyangcong.reader.bean.CloudShelfBook;
import com.iyangcong.reader.bean.CloudShelfContent;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.epub.EpubProcessResult;
import com.iyangcong.reader.interfaceset.DESEncodeInvoker;
import com.iyangcong.reader.interfaceset.DeviceType;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.ClearEditText;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.ui.customtablayout.listener.CustomTabEntity;
import com.iyangcong.reader.ui.customtablayout.listener.OnTabSelectListener;
import com.iyangcong.reader.ui.customtablayout.widget.SegmentTabLayout;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.InvokerDESServiceUitls;
import com.iyangcong.reader.utils.LoadCountHolder;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

public class ShelfCloudActivity extends SwipeBackActivity implements ClearEditText.ClearListener{

    @BindView(R.id.ceSearch)
    ClearEditText ceSearch;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView tvHeadTitle;
    @BindView(R.id.rv_shelf_cloud)
    RecyclerView rvShelfCloud;
    @BindView(R.id.shelf_cloud_ptrClassicFrameLayout)
    PtrClassicFrameLayout shelfCloudPtrClassicFrameLayout;
    @BindView(R.id.seg_tab_sort)
    SegmentTabLayout segTabSort;

    @OnClick({R.id.btnBack, R.id.btnFunction, R.id.tv_search})
    void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.tv_search:
                if (!ceSearch.getText().toString().equals("")) {
                    searchWords = ceSearch.getText().toString();
                    SORT_SIGN = 4;
                    shelfCloudAdapter.setCurrentType(0);
                    shelfCloudPtrClassicFrameLayout.autoRefresh();
                }
                break;
        }
    }

    Handler handler = new Handler();
    private HashMap<String, String> hashMap;
    private RecyclerAdapterWithHF mShelfCloudAdapter;
    private LoadCountHolder holderForReview = new LoadCountHolder();
    private CloudShelfContent cloudShelfContent;
    private ShelfCloudAdapter shelfCloudAdapter;
    private String[] sortTitles = new String[]{"时间", "书名", "类别"};
    private String searchWords;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    /*排序标志位
    0:默认 1:时间 2:分类 3:书名 4:搜索
    */
    private int SORT_SIGN = 1;
    private DESEncodeInvoker mInvoker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf_cloud);
        ButterKnife.bind(this);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        setMainHeadView();
        initView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        cloudShelfContent = new CloudShelfContent();

        List<CloudShelfBook> shelfBookCloudList = new ArrayList<>();
        cloudShelfContent.setShelfBookCloudList(shelfBookCloudList);

        List<String> CategoryList = new ArrayList<>();
        cloudShelfContent.setCategoryList(CategoryList);

    }

    @Override
    protected void initView() {
        mInvoker = new InvokerDESServiceUitls(this);
        shelfCloudAdapter = new ShelfCloudAdapter(this, cloudShelfContent,false);
        shelfCloudAdapter.setDESEncodeInvoker(mInvoker);
        mShelfCloudAdapter = new RecyclerAdapterWithHF(shelfCloudAdapter);
        rvShelfCloud.setLayoutManager(new LinearLayoutManager(this));
        rvShelfCloud.setAdapter(mShelfCloudAdapter);
        shelfCloudPtrClassicFrameLayout.setHorizontalScrollBarEnabled(false);
        shelfCloudPtrClassicFrameLayout.post(new Runnable() {

            @Override
            public void run() {
                shelfCloudPtrClassicFrameLayout.autoRefresh(true);
            }
        });

        shelfCloudPtrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return false;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showLoadingDialog();
                        holderForReview.refresh();
                        getDatasFromNetwork();
                        shelfCloudPtrClassicFrameLayout.refreshComplete();
                    }
                });
            }
        });

        shelfCloudPtrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void loadMore() {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        holderForReview.loadMore();
                        getDatasFromNetwork();
                        shelfCloudPtrClassicFrameLayout.loadMoreComplete(true);
                    }
                });
            }
        });
        ceSearch.setClearListener(this);

        segTabSort.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                showLoadingDialog();
                switch (position) {
                    case 0:
                        SORT_SIGN = 1;
                        shelfCloudAdapter.setCurrentType(0);
                        datasChange();
                        break;
                    case 1:
                        SORT_SIGN = 3;
                        shelfCloudAdapter.setCurrentType(0);
                        datasChange();
                        break;
                    case 2:
                        SORT_SIGN = 2;
                        shelfCloudAdapter.setCurrentType(1);
                        datasChange();
                        break;
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

    }

    @Override
    public void clear() {
        ceSearch.setText("");
    }

    @Override
    protected void setMainHeadView() {
        tvHeadTitle.setText(R.string.activity_shelf_cloud_title);
        btnBack.setImageResource(R.drawable.btn_back);
        segTabSort.setTabData(sortTitles);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void getDatasFromNetwork() {
        setHashmap();
        switch (SORT_SIGN) {
            case 0:
                getByOkGo(Urls.CloudBookShelf, hashMap);
                break;
            case 1:
                getByOkGo(Urls.CloudBookShelfSort, hashMap);
                break;
            case 2:
                getByOkGo(Urls.CloudBookShelfSort, hashMap);
                break;
            case 3:
                getByOkGo(Urls.CloudBookShelfSort, hashMap);
                break;
            case 4:
                getByOkGo(Urls.BookShelfSearchURL, hashMap);
                break;
        }
    }

    public void setHashmap() {
        hashMap = new HashMap<>();
        hashMap.put("deviceType", DeviceType.ANDROID_3);
        hashMap.put("userId", "" + CommonUtil.getUserId());
        switch (SORT_SIGN) {
            case 0:
                hashMap.put("currentPageNum", holderForReview.getPage() + 1 + "");
                hashMap.put("pageSize", "9");
                break;
            case 1:
                hashMap.put("currentpageNum", holderForReview.getPage() + 1 + "");
                hashMap.put("pageSize", "9");
                hashMap.put("type", SORT_SIGN + "");
                break;
            case 2:
                hashMap.put("currentpageNum", "1");
                hashMap.put("pageSize", "1");
                hashMap.put("type", SORT_SIGN + "");
                break;
            case 3:
                hashMap.put("currentpageNum", holderForReview.getPage() + 1 + "");
                hashMap.put("pageSize", "9");
                hashMap.put("type", SORT_SIGN + "");
                break;
            case 4:
                hashMap.put("string", searchWords);
                break;
        }
    }

    private void getByOkGo(String url, HashMap<String, String> hashMap) {
        switch (SORT_SIGN) {
            case 0:
            case 1:
            case 3:
                OkGo.get(url)
                        .tag(this)
                        .params(hashMap)
                        .execute(new JsonCallback<IycResponse<List<CloudShelfBook>>>(this) {
                            @Override
                            public void onSuccess(IycResponse<List<CloudShelfBook>> listIycResponse, Call call, Response response) {
                                if (holderForReview.isRefresh())
                                    cloudShelfContent.getShelfBookCloudList().clear();
                                if(listIycResponse.getData() != null){
                                    for (CloudShelfBook book : listIycResponse.getData())
                                        cloudShelfContent.getShelfBookCloudList().add(book);
                                    if (listIycResponse.getData().size() < 9 && holderForReview.getPage() == 0) {
                                        shelfCloudPtrClassicFrameLayout.setLoadMoreEnable(false);
                                    } else {
                                        shelfCloudPtrClassicFrameLayout.setLoadMoreEnable(true);
                                    }
                                    boolean isEnd = listIycResponse.getData().size() < 9;
                                    if (holderForReview.getPage() > 0) {
                                        loadMoreSuccess(shelfCloudPtrClassicFrameLayout, isEnd);
                                    }
                                    mShelfCloudAdapter.notifyDataSetChanged();
                                    dismissLoadingDialig();
                                }else {
                                    shelfCloudPtrClassicFrameLayout.setLoadMoreEnable(false);
                                }
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                if (holderForReview.getPage() > 0) {
                                    holderForReview.loadMoreFailed();
                                    loadMoreFailed(shelfCloudPtrClassicFrameLayout);
                                }
                                dismissLoadingDialig();
                            }
                        });
                break;
            case 2:
                OkGo.get(url)
                        .tag(this)
                        .params(hashMap)
                        .execute(new JsonCallback<IycResponse<List<String>>>(this) {
                            @Override
                            public void onSuccess(IycResponse<List<String>> listIycResponse, Call call, Response response) {
                                if (holderForReview.isRefresh())
                                    cloudShelfContent.getCategoryList().clear();
                                for (String category : listIycResponse.getData())
                                    cloudShelfContent.getCategoryList().add(category);
                                shelfCloudPtrClassicFrameLayout.setLoadMoreEnable(false);
                                mShelfCloudAdapter.notifyDataSetChanged();
                                dismissLoadingDialig();
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                ToastCompat.makeText(context, context.getString(R.string.net_error_tip), Toast.LENGTH_SHORT).show();
                                dismissLoadingDialig();
                        }
                        });
                break;
            case 4:
                OkGo.get(url)
                        .tag(this)
                        .params(hashMap)
                        .execute(new JsonCallback<IycResponse<List<CloudShelfBook>>>(this) {
                            @Override
                            public void onSuccess(IycResponse<List<CloudShelfBook>> listIycResponse, Call call, Response response) {
                                if (holderForReview.isRefresh())
                                    cloudShelfContent.getShelfBookCloudList().clear();
                                for (CloudShelfBook book : listIycResponse.getData())
                                    cloudShelfContent.getShelfBookCloudList().add(book);
                                shelfCloudPtrClassicFrameLayout.setLoadMoreEnable(false);
                                mShelfCloudAdapter.notifyDataSetChanged();
                                dismissLoadingDialig();
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                ToastCompat.makeText(context, context.getString(R.string.net_error_tip), Toast.LENGTH_SHORT).show();
                                dismissLoadingDialig();
                            }
                        });
        }
    }

    public void datasChange() {
        cloudShelfContent.getCategoryList().clear();
        cloudShelfContent.getShelfBookCloudList().clear();
        holderForReview.refresh();
        getDatasFromNetwork();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void epubProcessFinish(EpubProcessResult result) {
        dismissLoadingDialig();
        if(!result.isSuccessful()){
            ToastCompat.makeText(this,getString(R.string.unzip_failled), Toast.LENGTH_LONG).show();
        }
    }

}
