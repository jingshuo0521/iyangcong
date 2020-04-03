package com.iyangcong.reader.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.MineFriendsAdapter;
import com.iyangcong.reader.base.BaseFragment;
import com.iyangcong.reader.bean.DiscoverCircleFriends;
import com.iyangcong.reader.bean.DiscoverCircleGroup;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.interfaceset.RetryCounter;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

public class MineFriendsFragment extends BaseFragment {
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
    @BindView(R.id.rv_friends_list)
    RecyclerView mRvFriendsList;

    private List<DiscoverCircleFriends> MyFriendsList;
    private MineFriendsAdapter mineFriendsAdapter;
    private int retryCount = 0;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_mine_friends, container, false);
        ButterKnife.bind(this, view);
        MyFriendsList = new ArrayList<>();
        mineFriendsAdapter = new MineFriendsAdapter(mContext, MyFriendsList);
        mRvFriendsList.setAdapter(mineFriendsAdapter);
        mRvFriendsList.setLayoutManager(new LinearLayoutManager(mContext));
        mLayoutHeader.setVisibility(View.GONE);
        return view;
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        getMyFriensList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void getMyFriensList() {
        showLoadingDialog();
        OkGo.get(Urls.DiscoverCircleGetPersonAndAllFriends)
                .tag(this)
                .params("userId", CommonUtil.getUserId() + "")
                .execute(new JsonCallback<IycResponse<List<DiscoverCircleGroup>>>(mContext) {
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
//                            Looper.prepare();
//                            getMyFriensList();
//                            Looper.loop();
                        }else{
                            dismissLoadingDialig();
                        }
                    }
                });
    }
}
