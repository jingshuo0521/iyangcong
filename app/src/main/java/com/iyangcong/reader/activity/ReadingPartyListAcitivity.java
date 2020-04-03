package com.iyangcong.reader.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.DiscoverReadPartyListAdapter;
import com.iyangcong.reader.bean.DiscoverReadPartyExercise;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class ReadingPartyListAcitivity extends SwipeBackActivity{

    @BindView(R.id.rv_read_party_all_list)
    RecyclerView rvReadPartyAllList;
    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;

    List<DiscoverReadPartyExercise> discoverReadPartyActivityList = new ArrayList<>();
    DiscoverReadPartyListAdapter discoverReadPartyListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_party_list_acitivity);
        ButterKnife.bind(this);
        setMainHeadView();
        initView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }


    @Override
    protected void initView() {
        discoverReadPartyListAdapter = new DiscoverReadPartyListAdapter(context, discoverReadPartyActivityList,-1);
        rvReadPartyAllList.setAdapter(discoverReadPartyListAdapter);
        rvReadPartyAllList.setLayoutManager(new GridLayoutManager(context, 1));
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText(R.string.reading_party_list);
        btnBack.setImageResource(R.drawable.btn_back);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getActivities();
    }

    public void getActivities() {
        OkGo.get(Urls.DiscoverCirlceReadPartyAcitivytList)
                .execute(new JsonCallback<IycResponse<List<DiscoverReadPartyExercise>>>(context) {
                    @Override
                    public void onSuccess(IycResponse<List<DiscoverReadPartyExercise>> discoverReadPartyExerciseIycResponse, Call call, Response response) {
                        List<DiscoverReadPartyExercise> tempList = discoverReadPartyExerciseIycResponse.getData();
                        if (tempList.size() > 0){
                            discoverReadPartyActivityList.clear();
                            discoverReadPartyActivityList.addAll(tempList);
                        }
                        discoverReadPartyListAdapter.notifyDataSetChanged();
                    }
                });
    }

    @OnClick(R.id.btnBack)
    public void onClick() {
        finish();
    }

}
