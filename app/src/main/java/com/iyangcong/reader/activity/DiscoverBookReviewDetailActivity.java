package com.iyangcong.reader.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.flyco.dialog.widget.NormalDialog;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.DiscoverBookReviewAdapter;
import com.iyangcong.reader.bean.DiscoverCircleMember;
import com.iyangcong.reader.bean.DiscoverComment;
import com.iyangcong.reader.bean.DiscoverReviewDetails;
import com.iyangcong.reader.bean.DiscoverThoughtDetail;
import com.iyangcong.reader.bean.LikeStatus;
import com.iyangcong.reader.bean.Person;
import com.iyangcong.reader.bean.ReplyComment;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.enumset.LikeFrom;
import com.iyangcong.reader.interfaceset.OnLikeClickedListener;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.ClearEditText;
import com.iyangcong.reader.ui.CustomPtrClassicFrameLayout;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivityHelper;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.DialogUtils;
import com.iyangcong.reader.utils.LoadCountHolder;
import com.iyangcong.reader.utils.LoginUtils;
import com.iyangcong.reader.utils.NetUtil;
import com.iyangcong.reader.utils.NotNullUtils;
import com.iyangcong.reader.utils.ShareUtils;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.StringUtils;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

/**
 * 类作用  :  展示读后感详情页面，具体内容包括读后感内容，喜欢这条读后感的人的列表，回复读后感的列表
 * 必备参数  ：   (int)reviewId (String)mTitle
 * modified by WuZepeng  in 2017-03-16 10:04
 */

public class DiscoverBookReviewDetailActivity extends SwipeBackActivity implements ClearEditText.ClearListener {

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.ceSearch)
    ClearEditText ceSearch;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.ll_edit_bar)
    RelativeLayout editorBar;
    @BindView(R.id.discover_ptrClassicFrameLayout)
    CustomPtrClassicFrameLayout discoverPtrClassicFrameLayout;
    private RecyclerAdapterWithHF adapterWithHF;
    private DiscoverBookReviewAdapter discoverThoughtAdapter;
    private DiscoverThoughtDetail discoverThoughtDetail;
    private SwipeBackActivityHelper mHelper;
    private int reviewId;
    private long userId = 0;
    private int fatherId = 0;
    private InputMethodManager imm;
    @BindView(R.id.rv_discover_thought)
    RecyclerView rvDiscoverThought;
    private String mTitle = "";
    private LoginUtils loginUtils = new LoginUtils();
    private ShareUtils shareUtils;
    private Counter commenCounter = new Counter();
    private LoadCountHolder.ClickableRecoder clickableRecoder;

    @Override
    public void clear() {
        ceSearch.setText("");
    }

    @OnClick({R.id.btnBack, R.id.tv_search, R.id.btnFunction})
    void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.tv_search:
                if (loginUtils.isLogin(context)) {
                    addComments(ceSearch.getText().toString(), fatherId, reviewId, userId);
                }
                break;
            case R.id.btnFunction:
                shareUtils.addImagUrl(discoverThoughtDetail.getDiscoverReviewDetails().getBookcover());
                shareUtils.open();
                break;
        }
    }

    private void openOrCloseInputMethod(InputMethodManager imm, boolean open) {
        if (open)
            imm.showSoftInput(ceSearch, InputMethodManager.SHOW_FORCED);
        else
            imm.hideSoftInputFromWindow(ceSearch.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
        setContentView(R.layout.activity_discover_thought);
//        setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//        AndroidBug5497Workaround.assistActivity(this);
        ButterKnife.bind(this);
        setMainHeadView();
        initView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        reviewId = getIntent().getIntExtra(Constants.reviewId, 0);
        mTitle = StringUtils.delHTMLTag(getIntent().getStringExtra(Constants.THOUGHT_ACTIVITY_TITLE));
        String mBookName = getIntent().getStringExtra(Constants.TOPIC_ACTIVITY_BOOK_NAME);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        userId = CommonUtil.getUserId();
        clickableRecoder = commenCounter.new ClickableRecoder(true);
        discoverThoughtDetail = new DiscoverThoughtDetail();
        discoverThoughtDetail.setDiscoverReviewDetails(null);
        discoverThoughtDetail.setDiscoverCircleMemberList(null);
        discoverThoughtDetail.setCommment(null);
        discoverThoughtDetail.setCommentList(null);
        HashMap<String, String> map = new HashMap<>();
        map.put(ShareUtils.CONTENT_KEY, mTitle);
        map.put(ShareUtils.TITLE_KEY, mBookName);
        String url = Urls.URL + "/iycong_web/html/found/journal_details.html?reviewsid=" + reviewId;
        map.put(ShareUtils.URLS_KEY, url);
        shareUtils = new ShareUtils(context, map, ShareUtils.REVIEW_SHARE);
    }

    @Override
    protected void initView() {
        initVaryViewHelper(context, discoverPtrClassicFrameLayout, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getNetworkState(context) == NetUtil.NETWORK_MOBILE || getNetworkState(context) == NetUtil.NETWORK_WIFI) {
                    showDataView();
                    autoRefresh(discoverPtrClassicFrameLayout);
                } else {
                    showErrorView();
                }
            }
        });
        //这句话解决了输入法弹出遮挡的问题
        ceSearch.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        ceSearch.setClearListener(this);
        setSearcheBarView();
        ((SimpleItemAnimator)rvDiscoverThought.getItemAnimator()).setSupportsChangeAnimations(false);
        discoverThoughtAdapter = new DiscoverBookReviewAdapter(this, discoverThoughtDetail);
        adapterWithHF = new RecyclerAdapterWithHF(discoverThoughtAdapter);
        rvDiscoverThought.setAdapter(adapterWithHF);
        rvDiscoverThought.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        setAdapter(discoverThoughtAdapter);
        initPtrClassicFrameLayout(discoverPtrClassicFrameLayout);
    }

    private void initPtrClassicFrameLayout(CustomPtrClassicFrameLayout layout) {
        layout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refreshhandler();
            }
        });
        layout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                loadMoreHandler();
            }
        });
    }

    private void loadMoreHandler() {
        commenCounter.loadMore();
        loadComments(userId, reviewId, commenCounter.getPage(), commenCounter.getPageSize());
    }

    private void refreshhandler() {
        commenCounter.refresh();
        getDataFromNetwork();
    }

    private void setSearcheBarView() {
        tvSearch.setText(getResources().getString(R.string.addcomment));
        ceSearch.setHint(R.string.hint_input_content);
        ceSearch.setCompoundDrawables(null, null, null, null);
    }


    public boolean setEditBarShow() {
        if (editorBar.getVisibility() == View.GONE || editorBar.getVisibility() == View.INVISIBLE) {
            ceSearch.setFocusable(true);
            editorBar.setVisibility(View.VISIBLE);
            openOrCloseInputMethod(imm, true);
            return true;
        } else {
            editorBar.setVisibility(View.GONE);
            openOrCloseInputMethod(imm, false);
        }
        return false;
    }

    private void setAdapter(DiscoverBookReviewAdapter adapter) {
        adapter.setOnDetailCommentClickListener(new DiscoverBookReviewAdapter.OnReviewDetaiComementClickListener() {
            @Override
            public void onDetailComentClicked(DiscoverReviewDetails details) {
                fatherId = 0;
                if (loginUtils.isLogin(context))
                    setEditBarShow();
            }
        });
        adapter.setOnDetailLikeClickListener(new OnLikeClickedListener<DiscoverReviewDetails>() {
            @Override
            public void onLikeButtonClicked(DiscoverReviewDetails details) {
//                if(reviewId == details.getReviewId())
                if (CommonUtil.getLoginState()) {
                    likeTheComment(reviewId, 1, userId, LikeFrom.DETAIL);
                }
            }
        });
        adapter.setOnReplyItemClicked(new DiscoverBookReviewAdapter.OnReplyItemClicked() {
            @Override
            public void onClicked(DiscoverComment data, int position) {
                fatherId = data.getResponseid();
                if (loginUtils.isLogin(context))
                    setEditBarShow();
                Logger.i("DiscoverComment:" + data.toString());
            }

            @Override
            public void onClicked(ReplyComment comment) {
                fatherId = comment.getResponseid();
                if (loginUtils.isLogin(context))
                    setEditBarShow();
                Logger.i("ReplyComment:" + comment.toString());
            }
        });
        adapter.setCommentLikedClickListener(new OnLikeClickedListener<DiscoverComment>() {
            @Override
            public void onLikeButtonClicked(DiscoverComment comment) {
                LoginUtils loginUtils = new LoginUtils();
                if (loginUtils.isLogin(DiscoverBookReviewDetailActivity.this)) {
                    int position = discoverThoughtDetail.getCommentList().indexOf(comment);
                    if (position >= 0 && position < discoverThoughtDetail.getCommentList().size()) {
                        likeTheComment(comment.getResponseid(), 2, userId, LikeFrom.COMMENT, position);
                    }
                }
            }
        });
        adapter.setSubCommentLikedClickListener(new OnLikeClickedListener<ReplyComment>() {
            @Override
            public void onLikeButtonClicked(ReplyComment comment) {
//				likeTheComment(comment.getResponseid(), 2, userId, LikeFrom.SUBCOMMENT, );
            }
        });
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText(mTitle);
        btnBack.setImageResource(R.drawable.btn_back);
        btnFunction.setImageResource(R.drawable.share);
        btnFunction.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoRefresh(discoverPtrClassicFrameLayout);
    }

    private void getDataFromNetwork() {
        getReviewDetails(reviewId);
        loadPersonLikeThisReviewList(reviewId);
        loadComments(userId, reviewId, commenCounter.getPage(), commenCounter.getPageSize());
    }

    /**
     * 方法作用  :  获得读后感详情
     * 方法参数  ：   reviewId：读后感id
     * modified by WuZepeng  in 2017-03-27 16:44
     */
    private void getReviewDetails(int reviewId) {
        OkGo.get(Urls.DiscoverCircleReviewsDetails)
                .params("reviewid", reviewId)
                .params("userId", CommonUtil.getUserId())
                .params("deviceType", "3")
                .execute(new JsonCallback<IycResponse<DiscoverReviewDetails>>(this) {
                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (commenCounter.isRefresh()) {
                            refreshFailed(discoverPtrClassicFrameLayout);
                        }
                    }

                    @Override
                    public void onSuccess(IycResponse<DiscoverReviewDetails> discoverReviewDetailsIycResponse, Call call, Response response) {
                        if (NotNullUtils.isNull(discoverReviewDetailsIycResponse.getData()))
                            return;
                        discoverThoughtDetail.setDiscoverReviewDetails(discoverReviewDetailsIycResponse.getData());
                        adapterWithHF.notifyDataSetChanged();
                        if (commenCounter.isRefresh()) {
                            refreshSuccess(discoverPtrClassicFrameLayout);
                        }
                        shareUtils.addBookName(discoverReviewDetailsIycResponse.getData().getTitleZh());
                    }
                });
    }

    /**
     * 方法作用  :  获得点赞的用户列表
     * 方法参数  ：   reviewId:读后感的id
     * modified by WuZepeng  in 2017-03-27 16:42
     */
    private void loadPersonLikeThisReviewList(int reviewId) {
        OkGo.get(Urls.DiscoverCircleReviewLikePersonList)
                .params("reviewId", reviewId)
                .execute(new JsonCallback<IycResponse<List<Person>>>(this) {
                    @Override
                    public void onSuccess(IycResponse<List<Person>> listIycResponse, Call call, Response response) {
                        if (NotNullUtils.isNull(listIycResponse.getData()))
                            return;
                        List<DiscoverCircleMember> tempList = new ArrayList<DiscoverCircleMember>();
                        for (Person p : listIycResponse.getData()) {
                            DiscoverCircleMember member = new DiscoverCircleMember();
                            member.setCircleMemberImgUrl(p.getUserImage());
                            member.setCircleMemberName(p.getUserName());
                            member.setCircleMemberId(p.getUserId());
                            tempList.add(member);
                        }
                        discoverThoughtDetail.setDiscoverCircleMemberList(tempList);
                        adapterWithHF.notifyDataSetChanged();
                    }
                });
    }

    /**
     * 方法作用  加载某个读后感的评论
     * 方法参数  ：   reviewId:读后感id
     * modified by WuZepeng  in 2017-03-27 16:41
     */
    private void loadComments(long userId, int reviewId, int pageNo, int pageSize) {
        OkGo.get(Urls.DiscoverCirlceReviewsDetailsComment)
                .params("pageNo", pageNo)
                .params("pageSize", pageSize)
                .params("reviewId", reviewId)
                .params("userId", userId)
                .execute(new JsonCallback<IycResponse<List<DiscoverComment>>>(this) {
                    @Override
                    public void onSuccess(IycResponse<List<DiscoverComment>> listIycResponse, Call call, Response response) {
                        if (listIycResponse.getMsg().startsWith("没有评论") && listIycResponse.getData() == null) {
                            listIycResponse.setData(new ArrayList<DiscoverComment>());
                        }
                        if (commenCounter.isRefresh()) {
                            discoverThoughtDetail.setCommentList(listIycResponse.getData());
                        } else {
                            List<DiscoverComment> temp = (!NotNullUtils.isNull(discoverThoughtDetail) && !NotNullUtils.isNull(discoverThoughtDetail.getCommentList())) ?
                                    discoverThoughtDetail.getCommentList() : new ArrayList<DiscoverComment>();
                            List<DiscoverComment> received = !NotNullUtils.isNull(listIycResponse.getData()) ? listIycResponse.getData() : new ArrayList<DiscoverComment>();
                            for (DiscoverComment comment : received) {
                                if (!temp.contains(comment)) {
                                    temp.add(comment);
                                }
                            }
                            discoverThoughtDetail.setCommentList(temp);
                        }
//						if(commenCounter.isRefresh()){
//							refreshSuccess(discoverPtrClassicFrameLayout);
//						}else{
//							boolean isEnd = (isNull(listIycResponse.getData())?0:listIycResponse.getData().size()) < commenCounter.getPageSize();
//							loadMoreSuccess(discoverPtrClassicFrameLayout,isEnd);
//						}
                        adapterWithHF.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
//						if(commenCounter.isRefresh()){
//							refreshFailed(discoverPtrClassicFrameLayout);
//						}else{
//							loadMoreFailed(discoverPtrClassicFrameLayout);
//							commenCounter.loadMoreFailed();
//						}
                    }

                    @Override
                    public void onAfter(IycResponse<List<DiscoverComment>> listIycResponse, Exception e) {
                        super.onAfter(listIycResponse, e);
                        boolean isSuccess = isNull(listIycResponse) && isNull(listIycResponse.getData());
                        if(isSuccess){
                            if (commenCounter.isRefresh()) {
                                refreshFailed(discoverPtrClassicFrameLayout);
                            } else {
                                loadMoreFailed(discoverPtrClassicFrameLayout);
                                commenCounter.loadMoreFailed();
                            }
                        }else {
                            boolean isEnd = listIycResponse.getData().size() < commenCounter.getPageSize();
                            if (commenCounter.isRefresh()) {
                                refreshSuccessAndSetLoadMoreStatus(discoverPtrClassicFrameLayout,!isEnd);
//                                refreshSuccess(discoverPtrClassicFrameLayout);
                            } else {
                                loadMoreSuccess(discoverPtrClassicFrameLayout, isEnd);
                            }
                        }
                    }
                });
    }


    /**
     * 方法作用  :  添加评论
     * 方法参数  ：   content:评论内容
     * fatherId：评论的父id，当对读后感进行评论时，为0，对评论进行评论时，取评论的responseId;
     * reviewId:读后感的id
     * userId:用户id;
     * modified by WuZepeng  in 2017-03-27 16:36
     */
    private void addComments(String content, int fatherId, int reviewid, long userId) {
        if (NotNullUtils.isNull(context, content)) {
            return;
        }
        OkGo.get(Urls.DiscoverCirlceReviewsAddComment)
                .params("content", content)
                .params("contentsize", content.length() + "")
                .params("fatherid", fatherId + "")
                .params("reviewid", reviewid + "")
                .params("userid", userId + "")
                .execute(new JsonCallback<IycResponse<String>>(this) {
                    @Override
                    public void onSuccess(IycResponse<String> stringIycResponse, Call call, Response response) {
                        autoRefresh(discoverPtrClassicFrameLayout);
                        ceSearch.setText("");
                        setEditBarShow();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        NormalDialog dialog = new NormalDialog(context);
                        DialogUtils.setAlertDialogOneButtonStyle(dialog, "提示", e.getMessage());
                    }
                });
    }

    private void likeTheComment(int id, int type, long userId, final LikeFrom from) {
        LoginUtils loginUtils = new LoginUtils();
        if (loginUtils.isLogin(DiscoverBookReviewDetailActivity.this)) {
            likeTheComment(id, type, userId, from, -1);
        }
    }

    /**
     * 方法作用  :  点赞
     * 方法参数  ： id:资源id,
     * type:类型1：喜欢读后感2：喜欢读后感评论
     * userId:用户id
     * modified by WuZepeng  in 2017-03-27 10:11
     */
    private void likeTheComment(int id, int type, long userId, final LikeFrom from, final int position) {
        if (!clickableRecoder.isClickable())
            return;
        clickableRecoder.setClickable(false);
        OkGo.get(Urls.DiscoverCirlceReviewLike)
                .params("id", id + "")
                .params("type", type + "")
                .params("userid", userId)
                .execute(new JsonCallback<IycResponse<LikeStatus>>(context) {
                    @Override
                    public void onSuccess(IycResponse<LikeStatus> stringIycResponse, Call call, Response response) {
                        if (!isNull(stringIycResponse) && !isNull(stringIycResponse.getData())) {
                            handlerWhenLikedSuccess(stringIycResponse.getData(), from, position);
                        } else {
                            NormalDialog dialog = new NormalDialog(context);
                            DialogUtils.setAlertDialogNormalStyle(dialog, "提示", stringIycResponse.getMsg());
                        }
                    }

                    private void handlerWhenLikedSuccess(LikeStatus likeStatus, LikeFrom from, int position) {
                        int status = likeStatus.getStatus();
                        switch (from) {
                            case DETAIL:
                                int userId = (int) CommonUtil.getUserId();
                                int num = discoverThoughtDetail.getDiscoverReviewDetails().getReviewsLike();
                                if (status == 0) {
                                    discoverThoughtDetail.getDiscoverReviewDetails().setLiked(false);
                                    discoverThoughtDetail.getDiscoverReviewDetails().setReviewsLike(num - 1);
                                    Iterator<DiscoverCircleMember> iterator = discoverThoughtDetail.getDiscoverCircleMemberList().iterator();
                                    while (iterator.hasNext()) {
                                        DiscoverCircleMember member = iterator.next();
                                        if (member.getCircleMemberId() == userId) {
                                            iterator.remove();
                                        }
                                    }
                                } else if (status == 1) {
                                    discoverThoughtDetail.getDiscoverReviewDetails().setLiked(true);
                                    discoverThoughtDetail.getDiscoverReviewDetails().setReviewsLike(num + 1);
                                    DiscoverCircleMember member = new DiscoverCircleMember();
                                    SharedPreferenceUtil sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
                                    member.setCircleMemberImgUrl(sharedPreferenceUtil.getString(SharedPreferenceUtil.USER_PORTAIT_URL, ""));
                                    member.setCircleMemberName("");//因为本地没有缓存过用户的名称，而且目前的业务并不要求显示名称；
                                    member.setCircleMemberId(userId);
                                    if (discoverThoughtDetail.getDiscoverCircleMemberList() == null) {
                                        discoverThoughtDetail.setDiscoverCircleMemberList(new ArrayList<DiscoverCircleMember>());
                                    }
                                    if (!discoverThoughtDetail.getDiscoverCircleMemberList().contains(member)) {
                                        discoverThoughtDetail.getDiscoverCircleMemberList().add(member);
                                    }
                                }
                                discoverThoughtAdapter.notifyDetailLikeStatus();
//                                adapterWithHF.notifyDataSetChanged();
                                break;
                            case COMMENT:
                                if (position != -1) {
                                    int count = discoverThoughtDetail.getCommentList().get(position).getLikecount();
                                    if (status == 0) {
                                        discoverThoughtDetail.getCommentList().get(position).setLike(false);
                                        discoverThoughtDetail.getCommentList().get(position).setLikecount(count - 1);
                                    } else if (status == 1) {
                                        discoverThoughtDetail.getCommentList().get(position).setLike(true);
                                        discoverThoughtDetail.getCommentList().get(position).setLikecount(count + 1);
                                    }
                                }
                                discoverThoughtAdapter.notifySubLikeStatus(position);
                                break;
                        }
//                        adapterWithHF.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        //这里的onError方法不用去掉super.onError
                        super.onError(call, response, e);
                        NormalDialog dialog = new NormalDialog(context);
                        DialogUtils.setAlertDialogNormalStyle(dialog, "提示", e.getMessage());
                    }

                    @Override
                    public void onAfter(IycResponse<LikeStatus> likeStatusIycResponse, Exception e) {
                        super.onAfter(likeStatusIycResponse, e);
                        clickableRecoder.setClickable(true);
                    }
                });

    }


    private class Counter extends LoadCountHolder {
        @Override
        public void refresh() {
            setRefresh(true);
            setPage(1);
            setCanLoadMore(true);
        }

        @Override
        public int getPageSize() {
            return 10;
        }

    }


}
