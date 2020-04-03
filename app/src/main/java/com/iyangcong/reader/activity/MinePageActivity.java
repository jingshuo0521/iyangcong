package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.CommonFragmentAdapter;
import com.iyangcong.reader.bean.MineBasicInfo;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.fragment.MineCircleFragment;
import com.iyangcong.reader.fragment.MineDynamicsFragment;
import com.iyangcong.reader.fragment.MineReadedFragment;
import com.iyangcong.reader.fragment.MineThoughtToBookFragment;
import com.iyangcong.reader.fragment.MineTopicFragment;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.ui.customtablayout.CommonTabLayout;
import com.iyangcong.reader.ui.customtablayout.listener.CustomTabEntity;
import com.iyangcong.reader.ui.customtablayout.listener.OnTabSelectListener;
import com.iyangcong.reader.ui.customtablayout.listener.TabEntity;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.Urls.PersonBasicInfoURL;

public class MinePageActivity extends SwipeBackActivity {


    @BindView(R.id.main_backdrop)
    ImageView mainBackdrop;
    @BindView(R.id.iv_mine_head)
    ImageView ivMineHead;
    @BindView(R.id.iv_mine_head_sex)
    ImageView ivMineHeadSex;
    @BindView(R.id.fl_mine_head)
    FrameLayout flMineHead;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.ibtnBack)
    ImageButton ibtnBack;
    @BindView(R.id.text_head_title)
    TextView textHeadTitle;
    @BindView(R.id.btn_function)
    ImageButton btnFunction;
    @BindView(R.id.btn_function1)
    ImageButton btnFunction1;
    @BindView(R.id.rl_layout_header)
    RelativeLayout layoutHeader;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    CommonTabLayout tabs;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @BindView(R.id.vp_mine_page)
    ViewPager vpMinePage;
    @BindView(R.id.tv_mecare)
    TextView tvMecare;
    @BindView(R.id.tv_userdesc)
    TextView tvUserdesc;
    @BindView(R.id.ll_mine_fans)
    LinearLayout llMineFans;
    @BindView(R.id.ll_mine_frends)
    LinearLayout llMineFrends;


    private CollapsingToolbarLayoutState state;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private List<Fragment> fragmentList;
    private MineBasicInfo minebasicinfo = new MineBasicInfo();
    private String userId = CommonUtil.getUserId() + "";
    private boolean isFollow;
    private String toUserId;
    private int careMeNum;
    private SharedPreferenceUtil sharedPreferenceUtil;

    public MineBasicInfo getMinebasicinfo() {
        return minebasicinfo;
    }

    public void setMinebasicinfo(MineBasicInfo minebasicinfo) {
        this.minebasicinfo = minebasicinfo;
    }

    @OnClick({R.id.btn_back, R.id.btn_function, R.id.btn_function1,R.id.ll_mine_fans,R.id.ll_mine_frends})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.ll_mine_fans:
                intent = new Intent(this, FansActivity.class);
                intent.putExtra("toUserId", toUserId);
                break;
            case R.id.ll_mine_frends:
                intent = new Intent(this, AttentionActivity.class);
                intent.putExtra("toUserId", toUserId);
                break;
            case R.id.btn_function:
                if (!isFollow) {
                    attentionSomeBody();
                } else {
                    cancleAttentionSomebody();
                }
                break;
            case R.id.btn_function1:
                //发私信
                Intent intentCommont = new Intent(MinePageActivity.this, LetterEditActivity.class);
                intentCommont.putExtra("toUserId", toUserId);
                intentCommont.putExtra("toUserName", minebasicinfo.getName());
                startActivity(intentCommont);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }


    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_page);
        ButterKnife.bind(this);
        setMainHeadView();
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getDatasFromNetwork();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
        toUserId = getIntent().getStringExtra(Constants.USER_ID);
        fragmentList = new ArrayList<Fragment>();
        for (int i = 0; i < 5; i++) {
            fragmentList.add(instantFragment(i));
        }
    }

    @Override
    protected void initView() {

        appbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset == 0) {
                    if (state != CollapsingToolbarLayoutState.EXPANDED) {
                        state = CollapsingToolbarLayoutState.EXPANDED;//修改状态标记为展开
                        toolbarLayout.setTitle("");//设置title为EXPANDED
                    }
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                        toolbarLayout.setTitle("");//设置title不显示
                        layoutHeader.setVisibility(View.VISIBLE);//隐藏播放按钮
                        state = CollapsingToolbarLayoutState.COLLAPSED;//修改状态标记为折叠
                    }
                } else {
                    if (state != CollapsingToolbarLayoutState.INTERNEDIATE) {
                        if (state == CollapsingToolbarLayoutState.COLLAPSED) {
//                            layoutHeader.setVisibility(View.GONE);//由折叠变为中间状态时隐藏播放按钮
                        }
                        toolbarLayout.setTitle("");//设置title为INTERNEDIATE
                        state = CollapsingToolbarLayoutState.INTERNEDIATE;//修改状态标记为中间
                    }
                }
            }
        });

        CommonFragmentAdapter registerFragmentAdapter = new CommonFragmentAdapter(getSupportFragmentManager(), mTabEntities, fragmentList);
        vpMinePage.setAdapter(registerFragmentAdapter);
        vpMinePage.setOffscreenPageLimit(4);
//        tabs.setupWithViewPager(vpMinePage);
//        tabs.setTabsFromPagerAdapter(registerFragmentAdapter);
        setTabLayout();
    }

    /**
     * 初始化tablayout
     */
    private void setTabLayout() {
        mTabEntities.add(new TabEntity(this.getResources().getString(R.string.mine_dynamic)));
        mTabEntities.add(new TabEntity(this.getResources().getString(R.string.discover_review)));
        mTabEntities.add(new TabEntity(this.getResources().getString(R.string.discover_circle)));
        mTabEntities.add(new TabEntity(this.getResources().getString(R.string.discover_topic)));
        mTabEntities.add(new TabEntity(this.getResources().getString(R.string.mine_read)));

        tabs.setTabData(mTabEntities);
        tabs.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                vpMinePage.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
                if (position == 0) {
//                    mTabLayout_2.showMsg(0, mRandom.nextInt(100) + 1);
//                    UnreadMsgUtils.show(mTabLayout_2.getMsgView(0), mRandom.nextInt(100) + 1);
                }
            }
        });

        vpMinePage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabs.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        vpMinePage.setCurrentItem(0);
    }

    private Fragment instantFragment(int currIndex) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.USER_ID, toUserId);
        switch (currIndex) {
            case 0:
                MineDynamicsFragment mineDynamicsFragment = new MineDynamicsFragment();
                mineDynamicsFragment.setArguments(bundle);
                return mineDynamicsFragment;
            case 1:
                MineThoughtToBookFragment mineThoughtToBookFragment = new MineThoughtToBookFragment();
                mineThoughtToBookFragment.setArguments(bundle);
                return mineThoughtToBookFragment;
            case 2:
                MineCircleFragment mineCircleFragment = new MineCircleFragment();
                mineCircleFragment.setArguments(bundle);
                return mineCircleFragment;
            case 3:
                MineTopicFragment mineTopicFragment = new MineTopicFragment();
                mineTopicFragment.setArguments(bundle);
                return mineTopicFragment;
            case 4:
                MineReadedFragment mineReadedFragment = new MineReadedFragment();
                mineReadedFragment.setArguments(bundle);
                return mineReadedFragment;
            default:
                return null;
        }
    }


    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText(R.string.mine_page);
        btnBack.setImageResource(R.drawable.btn_back);
        if (toUserId.equals(userId)) {
            btnFunction.setVisibility(View.GONE);
            btnFunction1.setVisibility(View.GONE);
        } else {
            btnFunction.setVisibility(View.VISIBLE);
            btnFunction1.setVisibility(View.VISIBLE);
            btnFunction1.setImageResource(R.drawable.sixin);
            btnFunction.setImageResource(R.drawable.icon_follow);
        }
    }

    @Override
    protected boolean translucentStatusBar() {
        return true;
    }

    private void getDatasFromNetwork() {
        OkGo.get(PersonBasicInfoURL)
                .tag(this)
                .params("toUserId", toUserId)
                .params(Constants.USER_ID, userId)
                .execute(new JsonCallback<IycResponse<MineBasicInfo>>(this) {
                    @Override
                    public void onSuccess(IycResponse<MineBasicInfo> mineBasicInfoIycResponse, Call call, Response response) {

                        setMinebasicinfo(mineBasicInfoIycResponse.getData());
                        tvMecare.setText("" + minebasicinfo.getMecareNum());
                        textView2.setText("" + minebasicinfo.getCaremeNum());
                        careMeNum = minebasicinfo.getCaremeNum();
                        if (minebasicinfo.getName() == null) {
                            if (sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_USER_ACCOUNT, null).indexOf("@") != -1) {
                                textView.setText("邮箱用户" + CommonUtil.getUserId());
                            } else if (sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_USER_ACCOUNT, null).indexOf("@") == -1) {
                                textView.setText("手机用户" + CommonUtil.getUserId());
                            }
                        } else {
                            textView.setText(minebasicinfo.getName());
                        }
                        tvUserdesc.setText(minebasicinfo.getUserdesc());
                        GlideImageLoader.displayProtrait(context, minebasicinfo.getPhoto(), ivMineHead);
                        if (minebasicinfo.getSex() == 0) {
                            ivMineHeadSex.setBackgroundResource(R.drawable.ic_sex_woman);
                        } else {
                            ivMineHeadSex.setBackgroundResource(R.drawable.ic_sex_man);
                        }
                        if (minebasicinfo.getIsCared() == 1) {
                            isFollow = false;
                            btnFunction.setImageResource(R.drawable.icon_follow);
                        } else if (minebasicinfo.getIsCared() == 2) {
                            isFollow = true;
                            btnFunction.setImageResource(R.drawable.icon_unfollow);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        ToastCompat.makeText(context, "刷新失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void parseError(Call call, Exception e) {
                        getDatasFromNetwork();
                        dismissLoadingDialig();
                    }
                });
    }

    public void attentionSomeBody() {
        OkGo.get(Urls.AttentionSomeBodyURL)
                .tag(this)
                .params(Constants.USER_ID, userId)
                .params("attentionId", toUserId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        isFollow = !isFollow;
                        careMeNum = careMeNum + 1;
                        btnFunction.setImageResource(R.drawable.icon_unfollow);
                        textView2.setText(careMeNum + "");
                        ToastCompat.makeText(context, context.getResources().getString(R.string.care_somebody) + minebasicinfo.getName(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        ToastCompat.makeText(context, context.getResources().getString(R.string.care_failed), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void cancleAttentionSomebody() {
        OkGo.get(Urls.CancleAttentionSomebody)
                .tag(this)
                .params(Constants.USER_ID, userId)
                .params("attentionId", toUserId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        isFollow = !isFollow;
                        careMeNum = careMeNum - 1;
                        btnFunction.setImageResource(R.drawable.icon_follow);
                        textView2.setText(careMeNum + "");
                        ToastCompat.makeText(context, context.getResources().getString(R.string.uncare_somebody) + minebasicinfo.getName(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        ToastCompat.makeText(context, context.getResources().getString(R.string.uncare_failed), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
