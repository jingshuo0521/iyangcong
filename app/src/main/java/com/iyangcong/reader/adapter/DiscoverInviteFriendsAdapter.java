package com.iyangcong.reader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.DiscoverCircleFriends;
import com.iyangcong.reader.utils.GlideImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/1/7 0007.
 * 用于DiscoverNewCircleInviteFriends
 */

public class DiscoverInviteFriendsAdapter extends RecyclerView.Adapter {
    Context context;
    List<DiscoverCircleFriends> friendlist;
    LayoutInflater mInflater;
    private ClickListener clickListener;
    private boolean selectOneMode;//true表示单选模式，false表示多选模式；

    public DiscoverInviteFriendsAdapter(Context context, List<DiscoverCircleFriends> friendlist) {
        this(context,friendlist,false);//默认多选模式
    }

    public DiscoverInviteFriendsAdapter(Context context, List<DiscoverCircleFriends> friendlist, boolean selectOneMode) {
        this.context = context;
        this.friendlist = friendlist;
        mInflater = LayoutInflater.from(context);
        this.selectOneMode = selectOneMode;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view =  mInflater.inflate(R.layout.item_discover_invite_friends, null);
        return new FriendItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FriendItemViewHolder viewHolder = (FriendItemViewHolder)holder;
        viewHolder.setData(friendlist.get(position));
    }

    @Override
    public int getItemCount() {
        return friendlist == null?0:friendlist.size();
    }

    public class FriendItemViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.invite_friends_iv)
        ImageView inviteFriendsIv;
        @BindView(R.id.invite_friends_tv)
        TextView inviteFriendsTv;
        @BindView(R.id.tv_friends_desc)
        TextView tvFriendsDesc;
        @BindView(R.id.cb_invite_friend)
        CheckBox cbInviteFriend;
        @BindView(R.id.item_invite_friend)
        LinearLayout itemInviteFriend;
        @BindView(R.id.v_divider)
        View vDivider;
        @BindView(R.id.rl_layout)
        RelativeLayout rlLayout;
        FriendItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void setLayout(int visibility){
            inviteFriendsIv.setVisibility(visibility);
            inviteFriendsTv.setVisibility(visibility);
            tvFriendsDesc.setVisibility(visibility);
            cbInviteFriend.setVisibility(visibility);
            itemInviteFriend.setVisibility(visibility);
            vDivider.setVisibility(visibility);
            rlLayout.setVisibility(visibility);
        }

        private boolean setLayoutVisibility(DiscoverCircleFriends friends){
            if(friends == null || !friends.isVisibile()){
                setLayout(View.GONE);
                return false;
            }
            setLayout(View.VISIBLE);
            return true;
        }

        private void bindData(final DiscoverCircleFriends friends){
            inviteFriendsTv.setText(friends.getUserName());
            tvFriendsDesc.setText(friends.getUserDesc());
            GlideImageLoader.displayProtrait(context,friends.getPhoto(),inviteFriendsIv);
            cbInviteFriend.setChecked(friends.isChecked());
            itemInviteFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener != null){
                        clickListener.clicked(friendlist.indexOf(friends));
                    }
                    if(selectOneMode){
                        clearCheckStates(friends);
                    }else{
                        friends.setChecked(!friends.isChecked());
                        cbInviteFriend.setChecked(friends.isChecked());
                    }
                }
            });
            cbInviteFriend.setClickable(false);
            cbInviteFriend.setEnabled(false);
//            cbInviteFriend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
////                    if(selectOneMode){
////                        clearCheckStates();
////                    }
//                    friends.setChecked(isChecked);
////                    cbInviteFriend.setChecked(friends.isChecked());
//                }
//            });
        }

        private void setData(DiscoverCircleFriends friends){
            if(setLayoutVisibility(friends)){
                bindData(friends);
            }
        }
    }

    private void clearCheckStates(DiscoverCircleFriends fri){
        for(DiscoverCircleFriends friends:friendlist){
            if(friends.getUserId() == fri.getUserId()){
                friends.setChecked(!friends.isChecked());
                notifyItemChanged(friendlist.indexOf(friends));
            }else if(friends.isChecked()){
                friends.setChecked(false);
                notifyItemChanged(friendlist.indexOf(friends));
            }
        }
    }

    public ClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener{
        public void clicked(int position);
    }

    public boolean isSelectOneMode() {
        return selectOneMode;
    }

    public void setSelectOneMode(boolean selectOneMode) {
        this.selectOneMode = selectOneMode;
    }
}
