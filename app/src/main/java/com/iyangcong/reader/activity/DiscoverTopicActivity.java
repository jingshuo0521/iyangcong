package com.iyangcong.reader.activity;

import android.content.Context;
import android.content.Intent;
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
import com.iyangcong.reader.adapter.DiscoverTopicDetailAdaptper;
import com.iyangcong.reader.bean.DiscoverCircleMember;
import com.iyangcong.reader.bean.DiscoverComment;
import com.iyangcong.reader.bean.DiscoverTopicComments;
import com.iyangcong.reader.bean.DiscoverTopicDetail;
import com.iyangcong.reader.bean.DiscoverTopicDetailBean;
import com.iyangcong.reader.bean.LikeStatus;
import com.iyangcong.reader.bean.Person;
import com.iyangcong.reader.bean.ReplyComment;
import com.iyangcong.reader.bean.SubReplyComent;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.enumset.LikeFrom;
import com.iyangcong.reader.interfaceset.OnLikeClickedListener;
import com.iyangcong.reader.interfaceset.OnLikedButtonClicked;
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

import static com.iyangcong.reader.enumset.LikeFrom.COMMENT;
import static com.iyangcong.reader.enumset.LikeFrom.DETAIL;
import static com.iyangcong.reader.utils.NotNullUtils.isNull;
import static com.iyangcong.reader.utils.ShareUtils.CONTENT_KEY;
import static com.iyangcong.reader.utils.ShareUtils.TOPIC_SHARE;
import static com.iyangcong.reader.utils.ShareUtils.URLS_KEY;

/**
 * Created by WuZepeng on 2017-03-18.
 */

public class DiscoverTopicActivity extends SwipeBackActivity implements ClearEditText.ClearListener {


    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.rv_discover_thought)
    RecyclerView rvDiscoverThought;
    @BindView(R.id.ceSearch)
    ClearEditText ceSearch;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.search_bar)
    LinearLayout searchBar;
    @BindView(R.id.ll_edit_bar)
    RelativeLayout llEditBar;
    @BindView(R.id.discover_ptrClassicFrameLayout)
    CustomPtrClassicFrameLayout discoverPtrClassicFrameLayout;
    private SwipeBackActivityHelper mHelper;
    private RecyclerAdapterWithHF adapterWithHF;
    private DiscoverTopicDetailAdaptper discoverTopicDetailAdaptper;
    private DiscoverTopicDetailBean discoverTopicDetailBean;
    private int mTopicId, mGroupId, FatherId;
    private long mUserId;
    private String mTitle;
    private InputMethodManager imm;
    private LoginUtils loginUtils;
    private ShareUtils shareUtils;
    private Counter commentCounter = new Counter();
    private Counter.ClickableRecoder clickableRecoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
        setContentView(R.layout.activity_discover_thought);
//		AndroidBug5497Workaround.assistActivity(this);
        ButterKnife.bind(this);
        initView();
    }

    private void openOrCloseInputMethod(InputMethodManager imm, boolean open) {
        if (open)
            imm.showSoftInput(ceSearch, InputMethodManager.SHOW_FORCED);
        else
            imm.hideSoftInputFromWindow(ceSearch.getWindowToken(), 0);
    }


    @Override
    public void clear() {
        ceSearch.setText("");
    }

    @OnClick({R.id.tv_search, R.id.btnBack, R.id.btnFunction})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_search:
                if (loginUtils.isLogin(context))
                    addTopicComment(ceSearch.getText().toString(), FatherId, mGroupId, mTopicId, mUserId);
                break;
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnFunction:
                if (discoverTopicDetailBean.getDiscoverTopicDetail() != null) {
                    shareUtils.addImagUrl(discoverTopicDetailBean.getDiscoverTopicDetail().getCreateuserimage());
                }
                shareUtils.open();
                break;
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mTopicId = getIntent().getIntExtra(Constants.topicId, 0);
        mGroupId = getIntent().getIntExtra(Constants.groupId, 0);
        mUserId = CommonUtil.getUserId();
        mTitle = StringUtils.delHTMLTag(getIntent().getStringExtra(Constants.TOPIC_ACITIVITY_TITLE));
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        loginUtils = new LoginUtils();
        clickableRecoder = commentCounter.new ClickableRecoder(true);
        HashMap<String, String> tempMap = new HashMap<>();
        tempMap.put(CONTENT_KEY, mTitle);
        String url = Urls.URL + "/iycong_web/html/found/topic_details.html?groupid" + mGroupId + "&grouptype=" + 1 + "" + "&topicid=" + mTopicId;
        tempMap.put(URLS_KEY, url);
        shareUtils = new ShareUtils(this, tempMap, TOPIC_SHARE);
        discoverTopicDetailBean = new DiscoverTopicDetailBean();
        discoverTopicDetailBean.setDiscoverTopicDetail(null);
        discoverTopicDetailBean.setDiscoverCircleMemberList(null);
        discoverTopicDetailBean.setDiscoverTopicCommentList(null);
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
        setMainHeadView();
        ((SimpleItemAnimator)rvDiscoverThought.getItemAnimator()).setSupportsChangeAnimations(false);
        discoverTopicDetailAdaptper = new DiscoverTopicDetailAdaptper(context, discoverTopicDetailBean);
        adapterWithHF = new RecyclerAdapterWithHF(discoverTopicDetailAdaptper);
        rvDiscoverThought.setAdapter(adapterWithHF);
        rvDiscoverThought.setLayoutManager(new LinearLayoutManager(context));
        setAdapter(discoverTopicDetailAdaptper);
        initPtrClassicFrameLayout(discoverPtrClassicFrameLayout);
    }

    private void initPtrClassicFrameLayout(CustomPtrClassicFrameLayout layout) {
        layout.setHorizontalScrollBarEnabled(false);
        layout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refreshHandler();
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
        commentCounter.loadMore();
        getTopicComment(commentCounter.getPage(), commentCounter.getPageSize(), mTopicId, commentCounter.getType());
    }

    private void refreshHandler() {
        commentCounter.refresh();
        getDataFromNetwork();
    }

    private void setAdapter(DiscoverTopicDetailAdaptper adapter) {
        //shao add begin
        adapter.setOnContentFromClickedListener(new DiscoverTopicDetailAdaptper.OnContentFromClickedListener(){
            @Override
            public void onClicked(DiscoverTopicDetail detail) {

                Logger.i("setOnContentFromClickedListener:" );
                Intent intent = new Intent(context, DiscoverCircleDetailActivity.class);
                intent.putExtra(Constants.circleId, detail.getGroupId());
                intent.putExtra(Constants.circleName, detail.getGroupname());
                context.startActivity(intent);
            }
        });
        //shao add end
        adapter.setOnAddCommentClickedListener(new DiscoverTopicDetailAdaptper.OnAddCommentClickedListener() {
            @Override
            public void onClicked(DiscoverTopicDetail detail) {
                FatherId = 0;
                if (loginUtils.isLogin(context))
                    setAddEditorVisibility();
            }
        });
        adapter.setOnLikedButtonClicked(new OnLikedButtonClicked() {
            @Override
            public void onClicked(int id, int position) {
                if (loginUtils.isLogin(context))
                    likeTheTopicOrComment(mTopicId, 1, mUserId, DETAIL);
            }
        });
        adapter.setOnCommentItemClickedListener(new DiscoverTopicDetailAdaptper.OnCommentItemClickedListener() {
            @Override
            public void onFirstClassCommentClicked(DiscoverComment data, int position) {
                if (!loginUtils.isLogin(context))
                    return;
                Logger.i("DiscoverTopicComments FirstClassComment:" + data.toString());
                FatherId = data.getResponseid();
                setAddEditorVisibility();
//				if (data instanceof DiscoverTopicComments) {
//					FatherId = ((DiscoverTopicComments) data).getResponseid();
//					setAddEditorVisibility();
//				}
            }

            @Override
            public void onSecondClassCommentClicked(ReplyComment data) {
                if (!loginUtils.isLogin(context))
                    return;
                Logger.i("DiscoverTopicComments SecondClassComment:" + data.toString());
                if (data instanceof SubReplyComent) {
                    FatherId = ((SubReplyComent) data).getFatherid();
                    setAddEditorVisibility();
                }

            }
        });
        adapter.setDiscoverCommentOnLikeClickedListener(new OnLikeClickedListener<DiscoverComment>() {
            @Override
            public void onLikeButtonClicked(DiscoverComment comment) {
                int position = discoverTopicDetailBean.getDiscoverTopicCommentList().indexOf(comment);
                Logger.v("wzp  likedcommentTopic position:" + position);
                if (position >= 0 && position < discoverTopicDetailBean.getDiscoverTopicCommentList().size()) {
                    LoginUtils loginUtils = new LoginUtils();
                    if(loginUtils.isLogin(DiscoverTopicActivity.this)){
                        likeTheTopicOrComment(comment.getResponseid(), 2, mUserId, COMMENT, position);
                    }
                }
            }
        });
        adapter.setReplyCommentOnLikeClickedListener(new OnLikeClickedListener<ReplyComment>() {
            @Override
            public void onLikeButtonClicked(ReplyComment comment) {
            }
        });
    }

    private void setAddEditorVisibility() {
        if (llEditBar.getVisibility() == View.VISIBLE) {
            llEditBar.setVisibility(View.GONE);
            openOrCloseInputMethod(imm, true);
        } else {
            llEditBar.setVisibility(View.VISIBLE);
            openOrCloseInputMethod(imm, false);
        }
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText(mTitle);
        tvSearch.setText(getResources().getString(R.string.addcomment));
        ceSearch.setHint("请输入回复");
        btnBack.setImageResource(R.drawable.btn_back);
        btnFunction.setVisibility(View.VISIBLE);
        btnFunction.setImageResource(R.drawable.share);
        ceSearch.setCompoundDrawables(null, null, null, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoRefresh(discoverPtrClassicFrameLayout);
    }


    private void getDataFromNetwork() {
        getTopicLikedList(mTopicId);
        getTopicDetail(mGroupId, commentCounter.getGroupType(), mTopicId);
        getTopicComment(commentCounter.getPage(), commentCounter.getPageSize(), mTopicId, commentCounter.getType());
    }

    /**
     * 获得话题详情
     *
     * @param groupId
     * @param grouptype
     * @param topicid
     */
    public void getTopicDetail(final int groupId, int grouptype, int topicid) {
        OkGo.get(Urls.DiscoverCricleTopicDetails)
                .params("groupid", groupId + "")
                .params("grouptype", grouptype + "")
                .params("topicid", topicid + "")
                .params("userId", CommonUtil.getUserId())
                .execute(new JsonCallback<IycResponse<List<DiscoverTopicDetail>>>(context) {
                    @Override
                    public void onSuccess(IycResponse<List<DiscoverTopicDetail>> discoverTopicDetailIycResponse, Call call, Response response) {
                        if (!isNull(discoverTopicDetailIycResponse.getData())) {
                            discoverTopicDetailBean.setDiscoverTopicDetail(discoverTopicDetailIycResponse.getData().get(0));
                            discoverTopicDetailBean.getDiscoverTopicDetail().setGroupId(groupId);
                            adapterWithHF.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {

                    }
                });
    }

    /**
     * 获得评论列表
     *
     * @param pageNo
     * @param pageSize
     * @param topicId
     * @param type
     */
    public void getTopicComment(int pageNo, int pageSize, int topicId, int type) {
        OkGo.get(Urls.DiscoverCircleTopicDetailsComment)
                .params("pageNo", pageNo + "")
                .params("pageSize", pageSize + "")
                .params("topicid", topicId + "")
                .params("type", type + "")
                .params("userid", CommonUtil.getUserId())
                .execute(new JsonCallback<IycResponse<List<DiscoverTopicComments>>>(context) {
                    @Override
                    public void onSuccess(IycResponse<List<DiscoverTopicComments>> listIycResponse, Call call, Response response) {
                        if (isNull(listIycResponse.getData())){
                            if(listIycResponse.getMsg().equals("数据获取成功!")&&listIycResponse.getData()!=null&&listIycResponse.getData().size()==0){
                                discoverTopicDetailBean.setDiscoverTopicCommentList(new ArrayList<DiscoverTopicComments>());
                            }else{
                                return;
                            }
                        }
                        if (commentCounter.isRefresh()) {
                            discoverTopicDetailBean.setDiscoverTopicCommentList(listIycResponse.getData());
                        } else {
                            List<DiscoverTopicComments> temp = discoverTopicDetailBean.getDiscoverTopicCommentList();
                            for (DiscoverTopicComments comment : listIycResponse.getData()) {
                                if (!temp.contains(comment)) {
                                    temp.add(comment);
                                }
                            }
                            discoverTopicDetailBean.setDiscoverTopicCommentList(temp);
                        }
                        adapterWithHF.notifyDataSetChanged();
                    }

                    @Override
                    public void onAfter(IycResponse<List<DiscoverTopicComments>> listIycResponse, Exception e) {
                        super.onAfter(listIycResponse, e);
                        if (isNull(listIycResponse)) {
                            if (commentCounter.isRefresh()) {
                                refreshFailed(discoverPtrClassicFrameLayout);
                            } else {
                                loadMoreFailed(discoverPtrClassicFrameLayout);
                                commentCounter.loadMoreFailed();
                            }
                        } else {
                            boolean isEnd = listIycResponse.getData().size() < commentCounter.getPageSize();
                            if (commentCounter.isRefresh()) {
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
     * 点赞列表
     *
     * @param topicId
     */
    private void getTopicLikedList(int topicId) {
        OkGo.get(Urls.DiscoverCircleTopicDetailsLikedList)
                .params("topicId", topicId + "")
                .execute(new JsonCallback<IycResponse<List<Person>>>(context) {
                    @Override
                    public void onSuccess(IycResponse<List<Person>> listIycResponse, Call call, Response response) {
                        if (isNull(listIycResponse.getData()))
                            return;
                        List<DiscoverCircleMember> memberList = new ArrayList<DiscoverCircleMember>();
                        for (Person p : listIycResponse.getData()) {
                            DiscoverCircleMember member = new DiscoverCircleMember();
                            member.setCircleMemberImgUrl(p.getUserImage());
                            member.setCircleMemberName(p.getUserName());
                            member.setCircleMemberId(p.getUserId());
                            memberList.add(member);
                        }
                        discoverTopicDetailBean.setDiscoverCircleMemberList(memberList);
                        adapterWithHF.notifyDataSetChanged();
                    }
                });
    }

    /**
     * 添加评论
     *
     * @param content  评论内容
     * @param fatherId
     * @param groupId
     * @param topicId
     * @param userId
     */
    private void addTopicComment(final String content, int fatherId, int groupId, int topicId, long userId) {
        if (isNull(context, content))
            return;
        OkGo.get(Urls.DiscoverCirlcleTopicAddComment)
                .params("content", content)
                .params("contentsize", content.length())
                .params("fatherid", fatherId)
                .params("groupid", groupId)
                .params("topicid", topicId)
                .params("userid", userId + "")
                .execute(new JsonCallback<IycResponse<String>>(context) {
                    @Override
                    public void onSuccess(IycResponse<String> stringIycResponse, Call call, Response response) {
                        autoRefresh(discoverPtrClassicFrameLayout);
                        ceSearch.setText("");
                        setAddEditorVisibility();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        NormalDialog dialog = new NormalDialog(context);
                        DialogUtils.setAlertDialogOneButtonStyle(dialog, "提示", e.getMessage());
                    }
                });

    }

    private void likeTheTopicOrComment(int id, int type, long userId, LikeFrom from) {
        LoginUtils loginUtils = new LoginUtils();
        if (loginUtils.isLogin(this)) {
            likeTheTopicOrComment(id, type, userId, from, -1);
        }
    }

    /**
     * 喜欢话题或者评论
     *
     * @param id     内容id
     * @param type   内容类型
     * @param userId 用户id
     */
    private void likeTheTopicOrComment(int id, int type, long userId, final LikeFrom from, final int position) {
        if (!clickableRecoder.isClickable())
            return;
        clickableRecoder.setClickable(false);
        OkGo.get(Urls.DiscoverCirlceTopicCommetLike)
                .params("id", id)
                .params("type", type + "")
                .params("userid", userId + "")
                .execute(new JsonCallback<IycResponse<LikeStatus>>(context) {
                    @Override
                    public void onSuccess(IycResponse<LikeStatus> stringIycResponse, Call call, Response response) {
                        if (!isNull(stringIycResponse.getData())) {
                            handleWhenLikeSuccess(stringIycResponse);
                        } else {
                            NormalDialog dialog = new NormalDialog(context);
                            DialogUtils.setAlertDialogNormalStyle(dialog, "提示", stringIycResponse.getMsg());
                        }
                        clickableRecoder.setClickable(true);
//						if (stringIycResponse.getMsg().equals("成功操作！")) {
////							autoRefresh(discoverPtrClassicFrameLayout);
//						} else {
//							NormalDialog dialog = new NormalDialog(context);
//							DialogUtils.setAlertDialogNormalStyle(dialog, "提示", stringIycResponse.getMsg());
//						}
                    }

                    private void handleWhenLikeSuccess(IycResponse<LikeStatus> stringIycResponse) {
                        int status = stringIycResponse.getData().getStatus();
                        switch (from) {
                            case DETAIL:
                                //点赞成功
                                int userId = (int) CommonUtil.getUserId();
                                synchronized (new Object()) {
                                    int num = discoverTopicDetailBean.getDiscoverTopicDetail().getLikecount();
                                    num = num > 0 ? num : 0;
                                    if (status == 1) {
                                        discoverTopicDetailBean.getDiscoverTopicDetail().setLiked(true);
                                        discoverTopicDetailBean.getDiscoverTopicDetail().setLikecount(num + 1);
                                        DiscoverCircleMember member = new DiscoverCircleMember();
                                        SharedPreferenceUtil sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
                                        member.setCircleMemberImgUrl(sharedPreferenceUtil.getString(SharedPreferenceUtil.USER_PORTAIT_URL, ""));
                                        member.setCircleMemberName("");//因为本地没有缓存过用户的名称，而且目前的业务并不要求显示名称；
                                        member.setCircleMemberId(userId);
                                        if (isNull(discoverTopicDetailBean.getDiscoverCircleMemberList())) {
                                            discoverTopicDetailBean.setDiscoverCircleMemberList(new ArrayList<DiscoverCircleMember>());
                                        }
                                        if (!discoverTopicDetailBean.getDiscoverCircleMemberList().contains(member)) {
                                            discoverTopicDetailBean.getDiscoverCircleMemberList().add(member);
                                        }

                                    } else if (status == 0) {
                                        if (num == 0) {
                                            Logger.e("wzp " + discoverTopicDetailBean.getDiscoverTopicDetail().getLikecount());
                                            return;
                                        }
                                        discoverTopicDetailBean.getDiscoverTopicDetail().setLiked(false);

                                        discoverTopicDetailBean.getDiscoverTopicDetail().setLikecount(num - 1);
                                        Iterator<DiscoverCircleMember> iterator = discoverTopicDetailBean.getDiscoverCircleMemberList().iterator();
                                        while (iterator.hasNext()) {
                                            DiscoverCircleMember member = iterator.next();
                                            if (member.getCircleMemberId() == userId) {
                                                iterator.remove();
                                            }
                                        }
                                    }
                                }
                                adapterWithHF.notifyItemChanged(0);
                                break;
                            case COMMENT:
                                if (position != -1) {
                                    int count = discoverTopicDetailBean.getDiscoverTopicCommentList().get(position).getLikecount();
                                    if (status == 1) {
                                        discoverTopicDetailBean.getDiscoverTopicCommentList().get(position).setLike(true);
                                        discoverTopicDetailBean.getDiscoverTopicCommentList().get(position).setLikecount(count + 1);
                                    } else if (status == 0) {
                                        if (count == 0) {
                                            Logger.e("wzp " + discoverTopicDetailBean.getDiscoverTopicDetail().getLikecount());
                                            return;
                                        }
                                        discoverTopicDetailBean.getDiscoverTopicCommentList().get(position).setLike(false);
                                        discoverTopicDetailBean.getDiscoverTopicCommentList().get(position).setLikecount(count - 1);
                                    }
                                }
                                discoverTopicDetailAdaptper.notifySubLikeStatus(position);
                                break;
                            case SUBCOMMENT:
                                break;
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                    }
                });
    }

    private class Counter extends LoadCountHolder {
        private int type = 1;
        private int groupType = 1;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getGroupType() {
            return groupType;
        }

        public void setGroupType(int groupType) {
            this.groupType = groupType;
        }

        @Override
        public int getPageSize() {
            return 10;
        }

        @Override
        public void refresh() {
            setPage(1);
            setRefresh(true);
        }


    }

}
