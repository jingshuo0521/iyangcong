package com.iyangcong.reader.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.CommonFragmentAdapter;
import com.iyangcong.reader.fragment.EmailFindFragment;
import com.iyangcong.reader.fragment.PhoneFindFragment;
import com.iyangcong.reader.ui.customtablayout.CommonTabLayout;
import com.iyangcong.reader.ui.customtablayout.listener.CustomTabEntity;
import com.iyangcong.reader.ui.customtablayout.listener.OnTabSelectListener;
import com.iyangcong.reader.ui.customtablayout.listener.TabEntity;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sheng on 2017/1/16.
 */

public class FindPassWordActivity extends SwipeBackActivity {
    @BindView(R.id.tabs)
    CommonTabLayout tabsFindPassword;
    @BindView(R.id.vp_find_password)
    ViewPager vpFindPassword;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnBack)
    ImageButton btnBack;

    private ArrayList<CustomTabEntity> tabTitles = new ArrayList<>();
    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        ButterKnife.bind(this);
        initView();
        setMainHeadView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        fragmentList = new ArrayList<Fragment>();
        for (int i = 0; i < 2; i++) {
            fragmentList.add(instantFragment(i));
        }
    }

    @Override
    protected void initView() {
        CommonFragmentAdapter registerFragmentAdapter = new CommonFragmentAdapter(getSupportFragmentManager(),tabTitles,fragmentList);
        vpFindPassword.setAdapter(registerFragmentAdapter);
        textHeadTitle.setText("找回密码");
        btnBack.setImageResource(R.drawable.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setTabLayout();
    }

    /**
     * 初始化tablayout
     */
    private void setTabLayout() {
        tabTitles.add(new TabEntity(this.getResources().getString(R.string.find_by_phone)));
        tabTitles.add(new TabEntity(this.getResources().getString(R.string.find_by_email)));
        tabsFindPassword.setTabData(tabTitles);
        tabsFindPassword.setBackgroundColor(getResources().getColor(R.color.white));
        tabsFindPassword.setIndicatorWidth(60);
        tabsFindPassword.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                vpFindPassword.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
                if (position == 0) {
//                    mTabLayout_2.showMsg(0, mRandom.nextInt(100) + 1);
//                    UnreadMsgUtils.show(mTabLayout_2.getMsgView(0), mRandom.nextInt(100) + 1);
                }
            }
        });

        vpFindPassword.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabsFindPassword.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        vpFindPassword.setCurrentItem(0);
    }

    @Override
    protected void setMainHeadView() {

    }

    private Fragment instantFragment(int currIndex) {
        switch (currIndex) {
            case 0:
                return new PhoneFindFragment();
            case 1:
                return new EmailFindFragment();
            default:
                return null;
        }
    }


}