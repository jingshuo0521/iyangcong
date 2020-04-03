package com.iyangcong.reader.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.CommonFragmentAdapter;
import com.iyangcong.reader.fragment.EmailRegisterFragment;
import com.iyangcong.reader.fragment.PhoneRegisterFragment;
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
 * Created by sheng on 2016/12/26.
 */
public class RegisterActivity extends SwipeBackActivity {

    @BindView(R.id.tabs)
    CommonTabLayout tabsRegister;
    @BindView(R.id.vp_register)
    ViewPager vpRegister;
    @BindView(R.id.back_register)
    ImageView back_register;

    private ArrayList<CustomTabEntity> tabTitles = new ArrayList<>();
    private List<Fragment> fragmentList;
    private String currentState = "";
    private String thirdLoginID = "";
    private String thirdLoginType = "";
    private String thirdName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_register);
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
        currentState = getIntent().getStringExtra("state");
        thirdLoginID = getIntent().getStringExtra("thirdLoginID");
        thirdLoginType = getIntent().getStringExtra("thirdLoginType");
        thirdName = getIntent().getStringExtra("thirdName");
    }

    @Override
    protected void initView() {
        CommonFragmentAdapter registerFragmentAdapter = new CommonFragmentAdapter(getSupportFragmentManager(), tabTitles, fragmentList);
        vpRegister.setAdapter(registerFragmentAdapter);
        back_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setTabLayout();
    }

    public String getThirdName() {
        return thirdName;
    }

    public String getCurrentState() {
        return currentState;
    }

    public String getThirdLoginID() {
        return thirdLoginID;
    }

    public String getThirdLoginType() {
        return thirdLoginType;
    }

    /**
     * 初始化tablayout
     */
    private void setTabLayout() {
        if (getCurrentState() == null || !getCurrentState().equals("BINDING")) {
            tabTitles.add(new TabEntity(this.getResources().getString(R.string.register_by_phone)));
            tabTitles.add(new TabEntity(this.getResources().getString(R.string.register_by_email)));
        } else {
            tabTitles.add(new TabEntity(this.getResources().getString(R.string.bind_by_phone)));
            tabTitles.add(new TabEntity(this.getResources().getString(R.string.bind_by_email)));
        }
        tabsRegister.setBackgroundResource(R.color.white);
        tabsRegister.setIndicatorWidth(60);
        tabsRegister.setTabData(tabTitles);
        tabsRegister.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                vpRegister.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
                if (position == 0) {
                }
            }
        });

        vpRegister.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabsRegister.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        vpRegister.setCurrentItem(0);
    }

    @Override
    protected void setMainHeadView() {

    }

    @Override
    protected void onStop() {
        super.onStop();
//        finish();
    }

    private Fragment instantFragment(int currIndex) {
        switch (currIndex) {
            case 0:
                return new PhoneRegisterFragment();
            case 1:
                return new EmailRegisterFragment();
            default:
                return null;
        }
    }

}
