package com.iyangcong.reader.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.DiscoverReadPartyAdapter;
import com.iyangcong.reader.bean.CommonBanner;
import com.iyangcong.reader.bean.CommonVideo;
import com.iyangcong.reader.bean.DiscoverReadParty;
import com.iyangcong.reader.bean.DiscoverReadPartyExercise;
import com.iyangcong.reader.bean.Orgernization;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class DiscoverReadingPartyActivity extends SwipeBackActivity {

    private DiscoverReadParty discoverReadParty;
    private DiscoverReadPartyAdapter discoverReadPartyAdapter;

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.rv_discover_reading_party)
    RecyclerView rvDiscoverReadingParty;
    private int mId;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_reading_party);
        ButterKnife.bind(this);
        setMainHeadView();
        initView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mId = getIntent().getIntExtra(Constants.readingPartyId, 0);
        mTitle = getIntent().getStringExtra(Constants.readingPartyTitle);

        discoverReadParty = new DiscoverReadParty();
        List<CommonBanner> bannerList = new ArrayList<CommonBanner>();
        discoverReadParty.setCommonBannerList(bannerList);

        List<DiscoverReadPartyExercise> discoverReadPartyActivityList = new ArrayList<>();
        discoverReadParty.setDiscoverReadPartyActivityList(discoverReadPartyActivityList);

        List<CommonVideo> commonVedioList = new ArrayList<>();
        discoverReadParty.setCommonVedioList(commonVedioList);

        discoverReadParty.setCooperationUnitList(new ArrayList<Orgernization>());
    }

    @Override
    protected void initView() {
        discoverReadPartyAdapter = new DiscoverReadPartyAdapter(this, discoverReadParty);
        rvDiscoverReadingParty.setAdapter(discoverReadPartyAdapter);
        //recycleView不仅要设置适配器还要设置布局管理者,否则图片不显示
        //第一个参数是上下文，第二个参数是只有一列
        rvDiscoverReadingParty.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText(mTitle);
        btnBack.setImageResource(R.drawable.btn_back);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromNetwork();
    }

    public void getDataFromNetwork() {
        getBannerData();
        getActivities();
        getVideos();
        getCooperationUnitList();
    }

    public void getBannerData() {
        OkGo.get(Urls.BannerURL)
                .params("bannerPosition", 3)
                .execute(new JsonCallback<IycResponse<List<CommonBanner>>>(context) {
                    @Override
                    public void onSuccess(IycResponse<List<CommonBanner>> listIycResponse, Call call, Response response) {
                        List<CommonBanner> tempList = listIycResponse.getData();
                        if (tempList.size() > 0)
                            discoverReadParty.setCommonBannerList(tempList);
                        discoverReadPartyAdapter.notifyDataSetChanged();
                    }
                });
    }

    public void getActivities() {
        OkGo.get(Urls.DiscoverCirlceReadPartyAcitivytList)
                .execute(new JsonCallback<IycResponse<List<DiscoverReadPartyExercise>>>(context) {
                    @Override
                    public void onSuccess(IycResponse<List<DiscoverReadPartyExercise>> discoverReadPartyExerciseIycResponse, Call call, Response response) {
                        List<DiscoverReadPartyExercise> tempList = discoverReadPartyExerciseIycResponse.getData();
                        if (tempList!= null &&tempList.size() > 0)
                            discoverReadParty.setDiscoverReadPartyActivityList(tempList);
                        discoverReadPartyAdapter.notifyDataSetChanged();
                    }
                });
    }

    public void getVideos() {
        OkGo.get(Urls.DiscoverCircleReadiPartyVideoSource)
                .execute(new JsonCallback<IycResponse<List<CommonVideo>>>(context) {
                    @Override
                    public void onSuccess(IycResponse<List<CommonVideo>> commonVideoIycResponse, Call call, Response response) {
                        List<CommonVideo> tempVideoList = commonVideoIycResponse.getData();
                        if (tempVideoList.size() > 0)
                            discoverReadParty.setCommonVedioList(tempVideoList);
                        discoverReadPartyAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void getCooperationUnitList() {
        OkGo.get(Urls.DiscoverCircleReadPartyCooperation)
                .execute(new JsonCallback<IycResponse<List<Orgernization>>>(context) {
                    @Override
                    public void onSuccess(IycResponse<List<Orgernization>> orgernizations, Call call, Response response) {
                        List<Orgernization> tempList = orgernizations.getData();
                        if (tempList.size() > 0)
                            discoverReadParty.setCooperationUnitList(tempList);
                        discoverReadPartyAdapter.notifyDataSetChanged();
                    }
                });
    }

    @OnClick(R.id.btnBack)
    public void onClick() {
        finish();
    }
}
