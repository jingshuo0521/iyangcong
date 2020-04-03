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
import com.iyangcong.reader.base.BaseActivity;
import com.iyangcong.reader.fragment.CommentFragment;
import com.iyangcong.reader.fragment.NoticeFragment;
import com.iyangcong.reader.fragment.PrivateFragment;
import com.iyangcong.reader.ui.customtablayout.CommonTabLayout;
import com.iyangcong.reader.ui.customtablayout.listener.CustomTabEntity;
import com.iyangcong.reader.ui.customtablayout.listener.OnTabSelectListener;
import com.iyangcong.reader.ui.customtablayout.listener.TabEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewMaeesageActivity extends BaseActivity {


    List<Fragment> fragmentList;
    ArrayList<CustomTabEntity> titleList = new ArrayList<>();
    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.tabs)
    CommonTabLayout tabsNewmessage;
    @BindView(R.id.vp_newmessage)
    ViewPager vpNewmessage;
    @BindView(R.id.activity_new_maeesage)
    LinearLayout activityNewMaeesage;
    @BindView(R.id.ll_discover_tab)
    LinearLayout mLlDiscoverTab;


    @OnClick({R.id.btnBack})
    void onBtnClick(View view){
        switch (view.getId()){
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
        setContentView(R.layout.activity_new_maeesage);
        ButterKnife.bind(this);
        initView();
        setMainHeadView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        fragmentList = new ArrayList<>();
        titleList = new ArrayList<>();
        fragmentList.add(new CommentFragment());
        fragmentList.add(new NoticeFragment());
        fragmentList.add(new PrivateFragment());

    }

    @Override
    protected void initView() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mLlDiscoverTab.getLayoutParams();
        lp.setMargins(0,0,0,0);
        mLlDiscoverTab.setLayoutParams(lp);
        mLlDiscoverTab.setPadding(0,0,0,0);
        CommonFragmentAdapter newMessageAdapter = new CommonFragmentAdapter(getSupportFragmentManager(), titleList, fragmentList);
        vpNewmessage.setAdapter(newMessageAdapter);
        setTabLayout();
    }

    /**
     * 初始化tablayout
     */
    private void setTabLayout() {
        titleList.add(new TabEntity(this.getResources().getString(R.string.comment)));
        titleList.add(new TabEntity(this.getResources().getString(R.string.notification)));
        titleList.add(new TabEntity(this.getResources().getString(R.string.private_letter)));
        tabsNewmessage.setTabData(titleList);
        tabsNewmessage.setTextsize(16);
        tabsNewmessage.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                vpNewmessage.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
                if (position == 0) {
//                    mTabLayout_2.showMsg(0, mRandom.nextInt(100) + 1);
//                    UnreadMsgUtils.show(mTabLayout_2.getMsgView(0), mRandom.nextInt(100) + 1);
                }
            }
        });

        vpNewmessage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabsNewmessage.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        vpNewmessage.setCurrentItem(0);
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText("消息");
        btnBack.setImageResource(R.drawable.btn_back);
    }
}
