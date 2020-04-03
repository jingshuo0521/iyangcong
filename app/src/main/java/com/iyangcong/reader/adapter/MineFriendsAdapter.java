package com.iyangcong.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.MinePageActivity;
import com.iyangcong.reader.bean.DiscoverCircleFriends;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/4/15.
 */

public class MineFriendsAdapter extends RecyclerView.Adapter {


    private Context context;


    /**
     * 使用mLayoutInflater来初始化布局
     */
    private LayoutInflater mLayoutInflater;
    private List<DiscoverCircleFriends> myFriendsList;
    private String userId = CommonUtil.getUserId() + "";

    public MineFriendsAdapter(Context context, List<DiscoverCircleFriends> myFriendsList) {
        this.context = context;
        this.myFriendsList = myFriendsList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(context, mLayoutInflater.inflate(R.layout.item_mine_friend, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(myFriendsList.get(position));
    }

    @Override
    public int getItemCount() {
        return myFriendsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final Context mContext;
        private boolean isAlreadyCare = true;

        @BindView(R.id.iv_friend_head)
        ImageView ivFriendHead;
        @BindView(R.id.tv_friend_name)
        TextView tvFriendName;
        @BindView(R.id.tv_friend_desc)
        TextView tvFriendDesc;
        @BindView(R.id.rl_friend)
        RelativeLayout rlFriend;
        @BindView(R.id.iv_icon_care)
        ImageView ivIconCare;
        @BindView(R.id.tv_care)
        TextView tvCare;
        @BindView(R.id.ll_care)
        LinearLayout llCare;

        ViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        void setData(final DiscoverCircleFriends friend) {
            new GlideImageLoader().displayProtrait(mContext, friend.getPhoto(), ivFriendHead);
            tvFriendName.setText(friend.getUserName());
            tvFriendDesc.setText(((friend.getUserDesc()==null)||(friend.getUserDesc()==""))?"这颗小洋葱还没有简介哦~":friend.getUserDesc());
            rlFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MinePageActivity.class);
                    intent.putExtra(Constants.USER_ID, friend.getUserId() + "");
                    mContext.startActivity(intent);
                }
            });
            ivIconCare.setImageResource(R.drawable.icon_already_care);
            tvCare.setText(R.string.already_care);
            llCare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isAlreadyCare == true) {
                        cancleAttentionSomebody(friend);
                    } else if (isAlreadyCare == false) {
                        attentionSomeBody(friend);
                    }
                }
            });
        }

        public void attentionSomeBody(final DiscoverCircleFriends friend) {
            if(context instanceof SwipeBackActivity)
                ((SwipeBackActivity)context).showLoadingDialog();
            OkGo.get(Urls.AttentionSomeBodyURL)
                    .tag(this)
                    .params(Constants.USER_ID, userId)
                    .params("attentionId", friend.getUserId())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            isAlreadyCare = !isAlreadyCare;
                            ivIconCare.setImageResource(R.drawable.icon_already_care);
                            tvCare.setText(R.string.already_care);
                            if(context instanceof SwipeBackActivity)
                                ((SwipeBackActivity)context).dismissLoadingDialig();
                            ToastCompat.makeText(context, context.getResources().getString(R.string.care_somebody) + friend.getUserName(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            if(context instanceof SwipeBackActivity)
                                ((SwipeBackActivity)context).dismissLoadingDialig();
                            ToastCompat.makeText(context, context.getResources().getString(R.string.care_failed), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        public void cancleAttentionSomebody(final DiscoverCircleFriends friend) {
            if(context instanceof SwipeBackActivity)
                ((SwipeBackActivity)context).showLoadingDialog();
            OkGo.get(Urls.CancleAttentionSomebody)
                    .tag(this)
                    .params(Constants.USER_ID, userId)
                    .params("attentionId", friend.getUserId())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            if(context instanceof SwipeBackActivity)
                                ((SwipeBackActivity)context).dismissLoadingDialig();
                            isAlreadyCare = !isAlreadyCare;
                            ivIconCare.setImageResource(R.drawable.icon_plus_care);
                            tvCare.setText(R.string.plus_care);
                            ToastCompat.makeText(context, context.getResources().getString(R.string.uncare_somebody) + friend.getUserName(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            if(context instanceof SwipeBackActivity)
                                ((SwipeBackActivity)context).dismissLoadingDialig();
                            ToastCompat.makeText(context, context.getResources().getString(R.string.uncare_failed), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}
