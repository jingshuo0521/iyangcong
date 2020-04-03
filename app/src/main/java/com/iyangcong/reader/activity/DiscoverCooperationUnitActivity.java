package com.iyangcong.reader.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.DiscoverCooperationUnitAdapter;
import com.iyangcong.reader.base.BaseActivity;
import com.iyangcong.reader.bean.CommonVideo;
import com.iyangcong.reader.bean.DiscoverCooperationUnit;
import com.iyangcong.reader.bean.DiscoverReadPartyExercise;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/1/17.
 */

public class DiscoverCooperationUnitActivity extends BaseActivity {

    private DiscoverCooperationUnit discoverCooperationUnit;
    private DiscoverCooperationUnitAdapter discoverCooperationUnitAdapter;

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.rv_discover_cooperation)
    RecyclerView rvDiscoverCooperation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_cooperation_unit);
        ButterKnife.bind(this);
        setMainHeadView();
        initView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        discoverCooperationUnit = new DiscoverCooperationUnit();
        List<DiscoverReadPartyExercise> partyActivityList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            DiscoverReadPartyExercise discoverReadPartyActivity = new DiscoverReadPartyExercise();
            partyActivityList.add(discoverReadPartyActivity);
        }
        discoverCooperationUnit.setDiscoverCooperationUnitActivityList(partyActivityList);

        List<CommonVideo> commonVideoList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            CommonVideo commonVideo = new CommonVideo();
            commonVideoList.add(commonVideo);
        }
        discoverCooperationUnit.setCommonVideoList(commonVideoList);
    }

    @Override
    protected void initView() {
        discoverCooperationUnitAdapter = new DiscoverCooperationUnitAdapter(this, discoverCooperationUnit);
        rvDiscoverCooperation.setAdapter(discoverCooperationUnitAdapter);
        rvDiscoverCooperation.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText("合作机构");
        btnBack.setImageResource(R.drawable.btn_back);
    }
}
