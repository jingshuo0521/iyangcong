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
import com.iyangcong.reader.fragment.BookMarkersFragment;
import com.iyangcong.reader.fragment.NoteFragment;
import com.iyangcong.reader.fragment.TestFragment;
import com.iyangcong.reader.fragment.TocFragment;
import com.iyangcong.reader.ui.customtablayout.CommonTabLayout;
import com.iyangcong.reader.ui.customtablayout.listener.CustomTabEntity;
import com.iyangcong.reader.ui.customtablayout.listener.OnTabSelectListener;
import com.iyangcong.reader.ui.customtablayout.listener.TabEntity;

import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.application.ZLApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CatalogActivity extends BaseActivity {

    List<Fragment> fragmentList;
    ArrayList<CustomTabEntity> titleList = new ArrayList<>();
	List<String> fragmentTags;
    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.tabs)
    CommonTabLayout tabs;
    @BindView(R.id.iv_bar_divide)
    View ivBarDivide;
    @BindView(R.id.ll_discover_tab)
    LinearLayout llDiscoverTab;
    @BindView(R.id.vp_catalog)
    ViewPager vpCatalog;
    @BindView(R.id.activity_catalog)
    LinearLayout activityCatalog;

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
        fragmentList.add(new TocFragment());
        fragmentList.add(new NoteFragment());
        fragmentList.add(new BookMarkersFragment());
        fragmentList.add(new TestFragment());
    }

    @Override
    protected void initView() {
		fragmentTags = Arrays.asList(new String[]{"TocFragment","NoteFragment","BookMarkersFragment"});//,TestFragment"});
        CommonFragmentAdapter newMessageAdapter = new CommonFragmentAdapter(getSupportFragmentManager(), titleList, fragmentList);
//		CommonFragmentAda newMessageAdapter = new CommonFragmentAda(getSupportFragmentManager(),fragmentTags,titleList) {
//			@Override
//			public Fragment getItem(int position) {
//				switch (position) {
//					case 0:
//						return new TocFragment();
//					case 1:
//						return new NoteFragment();
//					case 2:
//						return new BookMarkerFragment();
//				}
//				return null;
//			}
//		};
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) llDiscoverTab.getLayoutParams();
        lp.setMargins(0,0,0,0);
        llDiscoverTab.setLayoutParams(lp);
        llDiscoverTab.setPadding(0,0,0,0);
		vpCatalog.setAdapter(newMessageAdapter);
        setTabLayout();
    }

    /**
     * 初始化tablayout
     */
    private void setTabLayout() {
        titleList.add(new TabEntity(this.getResources().getString(R.string.toc)));
        titleList.add(new TabEntity(this.getResources().getString(R.string.notes)));
        titleList.add(new TabEntity(this.getResources().getString(R.string.book_marker)));
        titleList.add(new TabEntity(this.getResources().getString(R.string.chapter_test)));
        tabs.setTabData(titleList);
        tabs.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                vpCatalog.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
                if (position == 0) {

                }
            }
        });
        vpCatalog.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

        tabs.setCurrentTab(0);
        vpCatalog.setCurrentItem(0);
    }

    @Override
    protected void setMainHeadView() {
        final FBReaderApp fbreader = (FBReaderApp) ZLApplication.Instance();
        textHeadTitle.setText(fbreader.getCurrentBook().getTitle());
        btnBack.setImageResource(R.drawable.btn_back);
    }
}
