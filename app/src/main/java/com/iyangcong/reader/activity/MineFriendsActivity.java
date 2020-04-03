package com.iyangcong.reader.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.MineFriendsAdapter;
import com.iyangcong.reader.bean.DiscoverCircleFriends;
import com.iyangcong.reader.bean.DiscoverCircleGroup;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.interfaceset.RetryCounter;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by DarkFlameMaster on 2017/4/15.
 */

public class MineFriendsActivity extends SwipeBackActivity {
    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.rv_friends_list)
    RecyclerView rvFriendsList;

    private List<DiscoverCircleFriends> MyFriendsList;
    private MineFriendsAdapter mineFriendsAdapter;
    private int retryCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_friends);
        ButterKnife.bind(this);
        setMainHeadView();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMyFriensList();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        MyFriendsList = new ArrayList<>();
//        getMyFriensList();
    }

    @Override
    protected void initView() {
        mineFriendsAdapter = new MineFriendsAdapter(this, MyFriendsList);
        rvFriendsList.setAdapter(mineFriendsAdapter);
        rvFriendsList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText("我的好友");
        btnBack.setImageResource(R.drawable.btn_back);
    }

    @OnClick({R.id.btnBack, R.id.btnFunction})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnFunction:
                break;
        }
    }

    public void getMyFriensList() {
        showLoadingDialog();
        OkGo.get(Urls.DiscoverCircleGetPersonAndAllFriends)
                .tag(this)
                .params("userId", CommonUtil.getUserId() + "")
                .execute(new JsonCallback<IycResponse<List<DiscoverCircleGroup>>>(this) {
                    @Override
                    public void onSuccess(IycResponse<List<DiscoverCircleGroup>> listIycResponse, Call call, Response response) {
                        MyFriendsList.clear();
                        for (DiscoverCircleGroup group : listIycResponse.getData()) {
                            for (DiscoverCircleFriends friend : group.getFriendsList()) {
                                if(!MyFriendsList.contains(friend)){
                                    MyFriendsList.add(friend);
                                }
                            }
                        }
                        mineFriendsAdapter.notifyDataSetChanged();
                        dismissLoadingDialig();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissLoadingDialig();
                    }

                    @Override
                    public void parseError(Call call, Exception e) {
                        super.parseError(call, e);
                        if(retryCount++< RetryCounter.MAX_RETRY_TIMES){
                            dismissLoadingDialig();
                            getMyFriensList();
                        }else{
                            dismissLoadingDialig();
                        }
                    }
                });
    }
}
