package com.iyangcong.reader.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.iyangcong.reader.MainActivity;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.BookShelfAdapter;
import com.iyangcong.reader.adapter.ShelfCloudAdapter;
import com.iyangcong.reader.base.BaseFragment;
import com.iyangcong.reader.bean.BookType;
import com.iyangcong.reader.bean.BuildBookCommand;
import com.iyangcong.reader.bean.CloudShelfBook;
import com.iyangcong.reader.bean.CloudShelfContent;
import com.iyangcong.reader.bean.ShelfBook;
import com.iyangcong.reader.bean.ShelfGroup;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.BookDao;
import com.iyangcong.reader.database.dao.GroupDao;
import com.iyangcong.reader.event.SlideEvent;
import com.iyangcong.reader.interfaceset.DESEncodeInvoker;
import com.iyangcong.reader.interfaceset.DeviceType;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.ClearEditText;
import com.iyangcong.reader.ui.button.FlatButton;
import com.iyangcong.reader.ui.customtablayout.CommonTabLayout;
import com.iyangcong.reader.ui.customtablayout.listener.CustomTabEntity;
import com.iyangcong.reader.ui.customtablayout.listener.OnTabSelectListener;
import com.iyangcong.reader.ui.customtablayout.listener.TabEntity;
import com.iyangcong.reader.ui.dragmerge.model.BookDataGroup;
import com.iyangcong.reader.ui.dragmerge.model.DragMergeClassifyView;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.DateUtils;
import com.iyangcong.reader.utils.InvokerDESServiceUitls;
import com.iyangcong.reader.utils.LoadCountHolder;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

//import com.iyangcong.reader.handler.EpubDecryptHandler;


/**
 * 书架
 */
public class BookShelfFragment extends BaseFragment implements ClearEditText.ClearListener,ClearEditText.SearchListener,TextView.OnEditorActionListener {


    @BindView(R.id.ceSearch)
    ClearEditText ceSearch;
    @BindView(R.id.classify_view)
    DragMergeClassifyView classifyView;
    @BindView(R.id.text_select_all)
    TextView textSelectAll;
    @BindView(R.id.text_complete)
    TextView textComplete;
    @BindView(R.id.tool_bar)
    Toolbar toolBar;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.ic_delete_badge)
    TextView icDeleteBadge;
    @BindView(R.id.container_delete)
    RelativeLayout containerDelete;
    @BindView(R.id.bottom_bar)
    LinearLayout bottomBar;
    @BindView(R.id.ll_sequence)
    LinearLayout llDivider;
    @BindView(R.id.fl_footBar)
    FrameLayout footBar;
    @BindView(R.id.book_shelf_container)
    RelativeLayout bookShelfContainer;
    @BindView(R.id.im_ll_sequence)
    ImageView ivDivider;
    @BindView(R.id.add_num)
    TextView addNum;
    @BindView(R.id.rv_shelf_cloud)
    RecyclerView rvShelfCloud;
    @BindView(R.id.seg_tab_sort)
    CommonTabLayout segTabSort;
    @BindView(R.id.shelf_cloud_ptrClassicFrameLayout)
    PtrClassicFrameLayout shelfCloudPtrClassicFrameLayout;
    @BindView(R.id.layout_no_book)
    LinearLayout llNoBook;
    @BindView(R.id.fb_goto_bookmarket)
    FlatButton btnGotoBookMarket;
    @BindView(R.id.ll_head)
    LinearLayout llhead;


    private List<BookDataGroup> mBookGroupLists = new ArrayList<>();
    private List<ShelfBook> shelfBookList;

    public List<BookDataGroup> getmBookGroupLists() {
        return mBookGroupLists;
    }

    private List<ShelfGroup> shelfGroupList;
    private BookShelfAdapter bookShelfAdapter;
    private RecyclerAdapterWithHF mShelfCloudAdapter;
    private Dialog dialog;
    private ImageView imHide;
    private ImageView ivProgress;
    private TextView tvCancel;
    /*排序标志位
    0:默认 1:时间 2:分类 3:书名 4:搜索
    */
    private int SORT_SIGN = 1;
    private HashMap<String, String> hashMap;
    private String searchWords;
    private CloudShelfContent cloudShelfContent;
    private ShelfBook cShelfBook;
    private LoadCountHolder holderForReview = new LoadCountHolder();
    private ShelfCloudAdapter shelfCloudAdapter;
    private ArrayList<CustomTabEntity> sortTitles = new ArrayList<>();
    private boolean isHide;
    private boolean isShow = false;
    Handler handler = new Handler();
    private DESEncodeInvoker mInvoker;
    private int coinValues;
//    private EpubDecryptHandler epubHandler;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private boolean isAllSelected = false;
    private boolean isLocalShelf;
    public boolean isShowProgress;
    private int num = 0;


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_shelf, container, false);
        ButterKnife.bind(this, view);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        return view;
    }

    @SuppressLint("ResourceType")
    @OnClick({ R.id.text_complete, R.id.container_delete, R.id.text_select_all,R.id.tv_search,R.id.fb_goto_bookmarket})
    void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.text_complete:
                hideEditMode();
                bookShelfAdapter.setEditMode(false);
                break;
            case R.id.text_select_all:
                isAllSelected = !isAllSelected;
                if (isAllSelected) {
                    int count = 0;
                    textSelectAll.setTextColor(getResources().getColor(R.color.main_color));
                    for (BookDataGroup bookDataGroup : mBookGroupLists) {
                        for (int i = 0; i < bookDataGroup.getChildCount(); i++) {
                            count++;
                            bookDataGroup.getChild(i).setChecked(true);
                        }
                    }
                    icDeleteBadge.setVisibility(View.VISIBLE);
                    icDeleteBadge.setText(String.valueOf(count));
                } else {
                    icDeleteBadge.setVisibility(View.INVISIBLE);
                    textSelectAll.setTextColor(getResources().getColor(R.color.middle_dark));
                    for (BookDataGroup bookDataGroup : mBookGroupLists) {
                        for (int i = 0; i < bookDataGroup.getChildCount(); i++) {
                            bookDataGroup.getChild(i).setChecked(false);
                        }
                    }
                }
                bookShelfAdapter.notifyDataSetChanged();
                break;
            case R.id.container_delete:
                bookShelfAdapter.removeAllCheckedBook(isAllSelected);
                break;
            case R.id.tv_search:
                doSearch();
                break;
            case R.id.fb_goto_bookmarket:
                ((MainActivity)getActivity()).setCurrIndex(0);
                ((MainActivity)getActivity()).showFragment();
                ((MainActivity)getActivity()).setGroupIcon(R.id.foot_book_market);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
//        Logger.e("wzp onCreateView");
//        viewDataChange();
        return rootView;
    }

    private void showEditMode() {
        toolBar.animate().translationY(0).start();
        bottomBar.animate().translationY(0).start();
//        classifyView.post(new Runnable() {
//            @Override
//            public void run() {
//                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) classifyView.getLayoutParams();
//                params.topMargin = toolBar.getHeight();
//                classifyView.setLayoutParams(params);
//            }
//        });
        isShow = true;
    }

    private void hideEditMode() {
        toolBar.animate().translationY(-toolBar.getHeight()).start();
        bottomBar.animate().translationY(bottomBar.getHeight()).start();
        classifyView.post(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) classifyView.getLayoutParams();
                params.topMargin = 0;
                classifyView.setLayoutParams(params);
            }
        });
        isShow = false;
    }

    public boolean getShow(){
        return isShow;
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        Logger.e("wzp onHiddenChanged");
        if (!hidden) {
            long userId = -1;
            if(CommonUtil.getLoginState()){
                userId = CommonUtil.getUserId();
                if(isLocalShelf) {
                    viewDataChange();
                    setLocalShelf(true);
                    showShelf();
                    showProgress();
                    if (getmBookGroupLists().size()==0||
                            getmBookGroupLists()==null){
                        showNoBook(true);
                    }else{
                        showNoBook(false);
                    }
                }else{
                    showCloudShelf();
                }
            }else{
                //没登录状态需要一直显示到书城提示
                showNoBook(true);
            }

        }
    }

    public void showShelfSettingView() {
        // 以特定的风格创建一个dialog
        dialog = new Dialog(getActivity(),R.style.MyDialog);
        // 加载dialog布局view
        View purchase = LayoutInflater.from(getActivity()).inflate(R.layout.shelf_setting, null);
        imHide = purchase.findViewById(R.id.im_hide);
        ivProgress = purchase.findViewById(R.id.iv_progress);
        tvCancel = purchase.findViewById(R.id.cancel);
        // 设置外部点击 取消dialog
        dialog.setCancelable(true);
        // 获得窗体对象
        Window window = dialog.getWindow();
        // 设置窗体的对齐方式
        window.setGravity(Gravity.BOTTOM);
        // 设置窗体动画
        window.setWindowAnimations(R.style.AnimBottom);
        // 设置窗体的padding
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        imHide.setImageResource(isHide ? R.drawable.ic_switch_on : R.drawable.ic_switch_off);
        ivProgress.setImageResource(isShowProgress ? R.drawable.ic_switch_on : R.drawable.ic_switch_off);
        dialog.setContentView(purchase);
        dialog.show();
        tvCancel.setOnClickListener(itemsOnClick);
        imHide.setOnClickListener(itemsOnClick);
        ivProgress.setOnClickListener(itemsOnClick);
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener(){
        public void onClick(View v) {
//            dialog.dismiss();
            switch (v.getId()) {
                case R.id.im_hide:
                    isHide = !isHide;
                    sharedPreferenceUtil.putBoolean(SharedPreferenceUtil.SHELFBOOK_HIDE,isHide);
                    imHide.setImageResource(isHide ? R.drawable.ic_switch_on : R.drawable.ic_switch_off);
                    onHiddenChanged(false);
                break;
                case R.id.iv_progress:
                    isShowProgress = !isShowProgress;
                    ivProgress.setImageResource(isShowProgress ? R.drawable.ic_switch_on : R.drawable.ic_switch_off);
                    showProgress();
                    break;
                case R.id.cancel:
                    dialog.dismiss();
                    break;

            }
        }

    };

    private void viewDataChange() {
        showShelfBooks();
        if (isHide){
            setHide();
        }
        if (isLocalShelf&&(mBookGroupLists.size()==0||mBookGroupLists == null)){
            llNoBook.setVisibility(View.VISIBLE);
        }else{
            llNoBook.setVisibility(View.GONE);
        }
        if (bookShelfAdapter != null){
            bookShelfAdapter.notifyDataSetChanged();
            bookShelfAdapter.getSubAdapter().notifyDataSetChanged();
        }
    }
    public void setHide(){
        Iterator<BookDataGroup> iterator = mBookGroupLists.iterator();
        while (iterator.hasNext()){
            BookDataGroup bookDataGroup = iterator.next();
            for (int i = 0; i < bookDataGroup.getChildCount(); i++) {
                if (bookDataGroup.getChildCount()==1&&
                        bookDataGroup.getChild(0).getDownloadProgress() == 100&&
                        bookDataGroup.getChild(0).getBookType()!=1){
                    iterator.remove();
                }else if (bookDataGroup.getChildCount()>1&&
                        bookDataGroup.getChild(i).getDownloadProgress() == 100&&
                        bookDataGroup.getChild(i).getBookType()!=1){
                    bookDataGroup.removeChild(i);
                }
            }
        }
    }


    public void searchBook(){
        Iterator<BookDataGroup> iterator = mBookGroupLists.iterator();
        while (iterator.hasNext()){
            BookDataGroup bookDataGroup = iterator.next();
            for (int i = 0; i < bookDataGroup.getChildCount(); i++) {
                if (bookDataGroup.getChildCount()==1&&
                        !bookDataGroup.getChild(0).getBookName().contains(searchWords)){
                    iterator.remove();
                }else if (bookDataGroup.getChildCount()>1&&
                        !bookDataGroup.getChild(i).getBookName().contains(searchWords)){
                    bookDataGroup.removeChild(i);
                }
            }
        }
    }

    /**
     * 准备书架要显示的内容
     */
    @Override
    protected void initData() {
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
        isShowProgress = sharedPreferenceUtil.getBoolean(SharedPreferenceUtil.ISSHOWSHELFBOOK,true);
        isLocalShelf = sharedPreferenceUtil.getBoolean(SharedPreferenceUtil.CLOUD_OR_LOCAL,false);
        if(isLocalShelf){
            ((MainActivity)getActivity()).tvHeadTitle.setText("本地书架");
        }else{
            ((MainActivity)getActivity()).tvHeadTitle.setText("云书架");
        }
        isHide = sharedPreferenceUtil.getBoolean(SharedPreferenceUtil.SHELFBOOK_HIDE,false);
        ceSearch.setSearchListener(this);
        ceSearch.setClearListener(this);
        ceSearch.setOnEditorActionListener(this);
//        Drawable drawable=getResources().getDrawable(R.drawable.ic_search); //获取图片
//        drawable.setBounds(0,0,45,51); //设置图片参数
//        ceSearch.setCompoundDrawables(drawable,null,null,null);

        mInvoker = new InvokerDESServiceUitls(getActivity());
        cloudShelfContent = new CloudShelfContent();
        List<CloudShelfBook> shelfBookCloudList = new ArrayList<>();
        cloudShelfContent.setShelfBookCloudList(shelfBookCloudList);
        List<String> CategoryList = new ArrayList<>();
        cloudShelfContent.setCategoryList(CategoryList);
        sortTitles.add(new TabEntity("时间"));
        sortTitles.add(new TabEntity("书名"));
        sortTitles.add(new TabEntity("类别"));
        segTabSort.setTabData(sortTitles);
        shelfCloudAdapter = new ShelfCloudAdapter(getActivity(), cloudShelfContent,isShowProgress);
        shelfCloudAdapter.setDESEncodeInvoker(mInvoker);
        mShelfCloudAdapter = new RecyclerAdapterWithHF(shelfCloudAdapter);
        ((SimpleItemAnimator)rvShelfCloud.getItemAnimator()).setSupportsChangeAnimations(false);
        rvShelfCloud.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvShelfCloud.setAdapter(mShelfCloudAdapter);
        showShelf();
    }

    public void showShelf(){
        if (isLocalShelf){
            showLocalShelf();
        }else {
            showCloudShelf();
        }
    }
    public void setLocalShelf(boolean localShelf) {
        isLocalShelf = localShelf;
        sharedPreferenceUtil.putBoolean(SharedPreferenceUtil.CLOUD_OR_LOCAL,isLocalShelf);
    }

    public void showProgress(){

        if (bookShelfAdapter != null){
            bookShelfAdapter.setShowProgress(isShowProgress);
            bookShelfAdapter.notifyDataSetChanged();
        }
        if (shelfCloudAdapter != null){
            shelfCloudAdapter.setShowProgress(isShowProgress);
            shelfCloudAdapter.notifyDataSetChanged();
//            mShelfCloudAdapter.notifyItemRangeChanged(0,cloudShelfContent.getShelfBookCloudList().size());
        }

    }

    /**
     * 显示本地书架
     */

    public void showLocalShelf(){
        isLocalShelf = true;
        ivDivider.setVisibility(View.GONE);
        llDivider.setVisibility(View.GONE);
        footBar.setVisibility(View.VISIBLE);
        toolBar.setVisibility(View.VISIBLE);
        shelfCloudPtrClassicFrameLayout.setVisibility(View.GONE);
        shelfCloudPtrClassicFrameLayout.setLoadMoreEnable(false);
        classifyView.setVisibility(View.VISIBLE);
        bookShelfAdapter = new BookShelfAdapter(mBookGroupLists, mContext,isShowProgress);
        classifyView.setAdapter(bookShelfAdapter);
        bookShelfAdapter.registerObserver(new BookShelfAdapter.BookShelfObserver() {
            int count = 0;
            @Override
            public void onChecked(boolean isChecked) {
                count += isChecked ? 1 : -1;
                if (count <= 0) {
                    count = 0;
                    icDeleteBadge.setVisibility(View.INVISIBLE);
                } else {
                    if (icDeleteBadge.getVisibility() == View.INVISIBLE) {
                        icDeleteBadge.setVisibility(View.VISIBLE);
                    }
                    icDeleteBadge.setText(String.valueOf(count));
                }
            }

            @Override
            public void onEditChanged(boolean inEdit) {
                if (inEdit) {
                    showEditMode();
                } else {
                    hideEditMode();
                }
            }

            @Override
            public void onRestore() {
                count = 0;
                icDeleteBadge.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onHideSubDialog() {
                super.onHideSubDialog();
            }
        });
        bookShelfAdapter.notifyDataSetChanged();
        final float density = getResources().getDisplayMetrics().density;
        bookShelfContainer.post(new Runnable() {
            @Override
            public void run() {
                bottomBar.setTranslationY(55 * density);
            }
        });
        toolBar.setTranslationY(-60 * density);
        showShelfBooks();
        if (isHide){
            setHide();
        }
    }

    /**
     * 显示云书架上的全部内容
     */
    private void showCloudShelf(){
        isLocalShelf = false;
        ivDivider.setVisibility(View.VISIBLE);
        llDivider.setVisibility(View.VISIBLE);
        footBar.setVisibility(View.GONE);
        toolBar.setVisibility(View.GONE);
        classifyView.setVisibility(View.GONE);
        shelfCloudPtrClassicFrameLayout.setVisibility(View.VISIBLE);
        shelfCloudPtrClassicFrameLayout.setHorizontalScrollBarEnabled(false);
        shelfCloudPtrClassicFrameLayout.setLoadMoreEnable(true);
        shelfCloudPtrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                //在这里写自己下拉刷新数据的请求
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        holderForReview.refresh();
                        getDatasFromNetwork();
                        //需要结束刷新头
//                        shelfCloudPtrClassicFrameLayout.refreshComplete();
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
                    }
                });
            }
        });


//        shelfCloudPtrClassicFrameLayout.post(new Runnable() {
//
//            @Override
//            public void run() {
//                shelfCloudPtrClassicFrameLayout.autoRefresh(true);
//            }
//        });
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
        //showLoadingDialog();
        getDatasFromNetwork();
    }

    public void datasChange() {
        cloudShelfContent.getCategoryList().clear();
        cloudShelfContent.getShelfBookCloudList().clear();
        holderForReview.refresh();
        getDatasFromNetwork();
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
                        .execute(new JsonCallback<IycResponse<List<CloudShelfBook>>>(getActivity()) {
                            @Override
                            public void onSuccess(IycResponse<List<CloudShelfBook>> listIycResponse, Call call, Response response) {
                                if (holderForReview.isRefresh()){
                                    cloudShelfContent.getShelfBookCloudList().clear();
                                    refreshSuccess(shelfCloudPtrClassicFrameLayout);
                                }
                                if(listIycResponse.getData() != null){
                                    for (CloudShelfBook book : listIycResponse.getData()){
                                        if(!(cloudShelfContent.getShelfBookCloudList().contains(book)))
                                        cloudShelfContent.getShelfBookCloudList().add(book);
                                    }

                                    if(cloudShelfContent.getShelfBookCloudList().size()==0){
                                        //没有数据
                                        showNoBook(true);
                                    }else{
                                        showNoBook(false);
                                    }
                                    if (listIycResponse.getData().size() < 9 && holderForReview.getPage() == 0) {
                                        shelfCloudPtrClassicFrameLayout.setLoadMoreEnable(false);
                                    } else {
                                        shelfCloudPtrClassicFrameLayout.setLoadMoreEnable(true);
                                    }
                                    boolean isEnd = listIycResponse.getData().size() < 9;
                                    if (holderForReview.getPage() > 0) {
                                        loadMoreSuccess(shelfCloudPtrClassicFrameLayout, isEnd);
                                    }
                                    shelfCloudAdapter.notifyDataSetChanged();
                                    dismissLoadingDialig();
                                }else {
                                    shelfCloudPtrClassicFrameLayout.setLoadMoreEnable(false);
                                    showNoBook(true);
                                }
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                if (holderForReview.getPage() > 0) {
                                    holderForReview.loadMoreFailed();
                                    loadMoreFailed(shelfCloudPtrClassicFrameLayout);
                                }
                                showNoBook(true);
                                refreshFailed(shelfCloudPtrClassicFrameLayout);
                                dismissLoadingDialig();
                            }
                        });
                break;
            case 2:
                OkGo.get(url)
                        .tag(this)
                        .params(hashMap)
                        .execute(new JsonCallback<IycResponse<List<String>>>(getActivity()) {
                            @Override
                            public void onSuccess(IycResponse<List<String>> listIycResponse, Call call, Response response) {
                                if (holderForReview.isRefresh()){
                                    cloudShelfContent.getCategoryList().clear();
                                    refreshSuccess(shelfCloudPtrClassicFrameLayout);
                                }

                                for (String category : listIycResponse.getData())
                                    cloudShelfContent.getCategoryList().add(category);
                                shelfCloudPtrClassicFrameLayout.setLoadMoreEnable(false);
                                shelfCloudAdapter.notifyDataSetChanged();
                                dismissLoadingDialig();
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                ToastCompat.makeText(getContext(), getActivity().getString(R.string.net_error_tip), Toast.LENGTH_SHORT).show();
                                refreshFailed(shelfCloudPtrClassicFrameLayout);
                                dismissLoadingDialig();
                            }
                        });
                break;
            case 4:
                OkGo.get(url)
                        .tag(this)
                        .params(hashMap)
                        .execute(new JsonCallback<IycResponse<List<CloudShelfBook>>>(getActivity()) {
                            @Override
                            public void onSuccess(IycResponse<List<CloudShelfBook>> listIycResponse, Call call, Response response) {
                                if (holderForReview.isRefresh()){
                                    cloudShelfContent.getShelfBookCloudList().clear();
                                    refreshSuccess(shelfCloudPtrClassicFrameLayout);
                                }
                                for (CloudShelfBook book : listIycResponse.getData()){
                                    if(!(cloudShelfContent.getShelfBookCloudList().contains(book)))
                                        cloudShelfContent.getShelfBookCloudList().add(book);

                                }
                                shelfCloudPtrClassicFrameLayout.setLoadMoreEnable(false);
                                shelfCloudAdapter.notifyDataSetChanged();
                                dismissLoadingDialig();
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                ToastCompat.makeText(getActivity(), getActivity().getString(R.string.net_error_tip), Toast.LENGTH_SHORT).show();
                                refreshFailed(shelfCloudPtrClassicFrameLayout);
                                dismissLoadingDialig();
                            }
                        });
        }
    }

    /**
     * 从数据库获取本地图书信息并显示到书架上
     */
    private void showShelfBooks() {
        try {
            mBookGroupLists.clear();
            HashMap<String, List<ShelfBook>> maps = new HashMap<>();
            GroupDao groupDao = new GroupDao(DatabaseHelper.getHelper(mContext));
            BookDao bookDao = new BookDao(DatabaseHelper.getHelper(mContext));
            initBookDao(bookDao);
            shelfBookList = bookDao.all();
            shelfGroupList = groupDao.all();
            if (shelfBookList != null) {
                Collections.sort(shelfBookList);
                for (ShelfBook shelfBook : shelfBookList) {
                    if (shelfBook.getBookType() != BookType.HAS_BUY_BOOk && shelfBook.getBookType() == BookType.MONTHLY_BOOK) {
                        Date now = new Date();
                        Date endTime = DateUtils.StringToDate(shelfBook.getEndTime(), "yyyy-MM-dd HH:mm:ss");
                        if (now.after(endTime))
                            continue;
                    }
                    String groupName = shelfBook.getGroupName();
                    if (groupName != null) {
                        if (maps.get(groupName) == null) {
                            List<ShelfBook> shelfBookList = new ArrayList<>();
                            shelfBookList.add(shelfBook);
                            maps.put(groupName, shelfBookList);
                        } else {
                            List<ShelfBook> shelfBookList = maps.get(groupName);
                            shelfBookList.add(shelfBook);
                        }
                    } else {
                        BookDataGroup bookDataGroup = new BookDataGroup();
                        bookDataGroup.addChild(shelfBook);
                        mBookGroupLists.add(bookDataGroup);
                    }
                }
                Iterator iterator = maps.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, List<ShelfBook>> entry = (Map.Entry<String, List<ShelfBook>>) iterator.next();
                    String groupName = entry.getKey();
                    List<ShelfBook> shelfBookList = entry.getValue();
                    BookDataGroup bookDataGroup = new BookDataGroup();
                    for (ShelfBook shelfBook : shelfBookList) {
                        bookDataGroup.addChild(shelfBook);
                    }
                    bookDataGroup.setCategory(groupName);
                    mBookGroupLists.add(bookDataGroup);
                }
            }
        } catch (RuntimeException e) {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReturnToTop(SlideEvent slideEvent){

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedPreferenceUtil.putBoolean(SharedPreferenceUtil.CLOUD_OR_LOCAL,isLocalShelf);
        sharedPreferenceUtil.putBoolean(SharedPreferenceUtil.SHELFBOOK_HIDE,isHide);
        sharedPreferenceUtil.putBoolean(SharedPreferenceUtil.ISSHOWSHELFBOOK,isShowProgress);
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }


    @Override
    protected void onInvisible() {
        super.onInvisible();
        if (isShow){
            hideEditMode();
            bookShelfAdapter.setEditMode(false);
        }
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if (isLocalShelf&&(mBookGroupLists.size()==0||mBookGroupLists == null)){
            llNoBook.setVisibility(View.VISIBLE);
        }else{
            llNoBook.setVisibility(View.GONE);
        }
    }

    public void showNoBook(boolean show){
        if(show){
            llNoBook.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams paramses= (RelativeLayout.LayoutParams) llhead.getLayoutParams();
            paramses.height= WindowManager.LayoutParams.MATCH_PARENT;
            llhead.setLayoutParams(paramses);
        }else{
            llNoBook.setVisibility(View.GONE);
            RelativeLayout.LayoutParams paramses= (RelativeLayout.LayoutParams) llhead.getLayoutParams();
            paramses.height= WindowManager.LayoutParams.WRAP_CONTENT;
            llhead.setLayoutParams(paramses);
        }
    }

    /**
     * 如果书架为空，则添加小王子到书架
     *
     * @param bookDao
     */
    private void initBookDao(BookDao bookDao) {
//        Logger.e("wzp initBookDao");
        EventBus.getDefault().post(new BuildBookCommand());
//        BuiltInBookUtil.createBuiltInBookInThread(mContext);//构建默认的内置书籍
//        if (epubHandler == null) {
//            epubHandler = new EpubDecryptHandler(mContext, 53);
//        }
//        BuiltInBookUtil.creatBuiltInBook(mContext, bookDao, epubHandler);
//        SharedPreferenceUtil sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
//        if (bookDao == null || sharedPreferenceUtil.getInt(SharedPreferenceUtil.DELETED_DEFAULT_BOOK, -1) != -1) {
//            return;
//        }
//        List<ShelfBook> shelfBookList = bookDao.queryByColumn("bookId", 53);
//        if (shelfBookList == null || shelfBookList.size() == 0) {
//            try {
//                if (!FileHelper.isFileExit(CommonUtil.getBooksDir() + "53.zip")) {
//                    FileHelper.copyDataBase(mContext, CommonUtil.getBooksDir(), "53.zip", "53.zip");
//                    FileHelper.copyDataBase(mContext, CommonUtil.getBooksDir() + "image/", "53", "530");
//                }
//                if (epubHandler == null) {
//                    epubHandler = new EpubDecryptHandler(mContext, 53);
//                }
//                epubHandler.decryptEpub();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        sharedPreferenceUtil.putInt(SharedPreferenceUtil.DELETED_DEFAULT_BOOK, 0);
    }
    public void onKeyDown() {
        hideEditMode();
        bookShelfAdapter.setEditMode(false);
    }

    private void doSearch(){
        if (!ceSearch.getText().toString().equals("")) {
            searchWords = ceSearch.getText().toString();
            if (isLocalShelf){
                showShelfBooks();
                searchBook();
                if (bookShelfAdapter != null){
                    bookShelfAdapter.notifyDataSetChanged();
                    bookShelfAdapter.getSubAdapter().notifyDataSetChanged();
                }
            }else{
                searchWords = ceSearch.getText().toString();
                SORT_SIGN = 4;
                shelfCloudAdapter.setCurrentType(0);
                shelfCloudPtrClassicFrameLayout.autoRefresh();
            }
        }
    }

    @Override
    public void clear() {
        ceSearch.setText("");
    }

    @Override
    public void search() {
        doSearch();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        doSearch();
        return true;
    }
}