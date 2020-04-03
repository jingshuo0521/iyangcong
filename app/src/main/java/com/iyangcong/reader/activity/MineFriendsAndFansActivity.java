package com.iyangcong.reader.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.CommonFragmentAdapter;
import com.iyangcong.reader.fragment.MineFansFragment;
import com.iyangcong.reader.fragment.MineFriendsFragment;
import com.iyangcong.reader.ui.customtablayout.CommonTabLayout;
import com.iyangcong.reader.ui.customtablayout.listener.CustomTabEntity;
import com.iyangcong.reader.ui.customtablayout.listener.OnTabSelectListener;
import com.iyangcong.reader.ui.customtablayout.listener.TabEntity;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MineFriendsAndFansActivity extends SwipeBackActivity {
    @BindView(R.id.btnBack)
    ImageButton mBtnBack;
    @BindView(R.id.textHeadTitle)
    TextView mTextHeadTitle;
    @BindView(R.id.ibSign)
    ImageButton mIbSign;
    @BindView(R.id.btnFunction)
    ImageButton mBtnFunction;
    @BindView(R.id.tv_goods_num)
    TextView mTvGoodsNum;
    @BindView(R.id.btnFunction1)
    ImageButton mBtnFunction1;
    @BindView(R.id.tv_goods_num1)
    TextView mTvGoodsNum1;
    @BindView(R.id.layout_header)
    LinearLayout mLayoutHeader;
    @BindView(R.id.tabs)
    CommonTabLayout mTabs;
    @BindView(R.id.iv_bar_divide)
    View mIvBarDivide;
    @BindView(R.id.ll_discover_tab)
    LinearLayout mLlDiscoverTab;
    @BindView(R.id.vp_catalog)
    ViewPager mVpCatalog;
    @BindView(R.id.activity_catalog)
    LinearLayout mActivityCatalog;

    List<Fragment> fragmentList;
    ArrayList<CustomTabEntity> titleList = new ArrayList<>();
    List<String> fragmentTags;

    @OnClick({R.id.btnBack})
    void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        ButterKnife.bind(this);
        initView();
        setMainHeadView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        fragmentList = new ArrayList<>();
        titleList = new ArrayList<>();
        fragmentList.add(new MineFriendsFragment());
        fragmentList.add(new MineFansFragment());
    }

    @Override
    protected void initView() {
        fragmentTags = Arrays.asList(new String[]{"MineFriendsFragment","MineFansFragment"});
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mLlDiscoverTab.getLayoutParams();
        lp.setMargins(0,0,0,0);
        mLlDiscoverTab.setLayoutParams(lp);
        mLlDiscoverTab.setPadding(0,0,0,0);
        CommonFragmentAdapter newMessageAdapter = new CommonFragmentAdapter(getSupportFragmentManager(), titleList, fragmentList);
        mVpCatalog.setAdapter(newMessageAdapter);
        setTabLayout();
    }

    /**
     * 初始化tablayout
     */
    private void setTabLayout() {
        titleList.add(new TabEntity("好友"));
        titleList.add(new TabEntity("粉丝"));
        mTabs.setTabData(titleList);
        mTabs.setTextsize(16);
        mTabs.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mVpCatalog.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
                if (position == 0) {

                }
            }
        });
        mVpCatalog.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTabs.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTabs.setCurrentTab(0);
        mVpCatalog.setCurrentItem(0);
    }

    @Override
    protected void setMainHeadView() {
        mTextHeadTitle.setText("我的好友&粉丝");
        mBtnBack.setImageResource(R.drawable.btn_back);
    }
}
