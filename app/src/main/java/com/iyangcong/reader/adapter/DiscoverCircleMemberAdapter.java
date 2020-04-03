package com.iyangcong.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.MinePageActivity;
import com.iyangcong.reader.bean.DiscoverCircleMember;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.GlideImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ljw on 2016/12/26.
 */

public class DiscoverCircleMemberAdapter extends BaseAdapter {

    private Context mContext;
    private List<DiscoverCircleMember> discoverCircleMemberList;

    public DiscoverCircleMemberAdapter(Context mContext, List<DiscoverCircleMember> discoverCircleMemberList) {
        this.mContext = mContext;
        this.discoverCircleMemberList = discoverCircleMemberList;
    }

    @Override
    public int getCount() {
        if(discoverCircleMemberList == null)
            return 0;
        int tempSize = discoverCircleMemberList.size();
        return tempSize > 8 ? 8: tempSize;
    }

    @Override
    public Object getItem(int i) {
        return discoverCircleMemberList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            //item的布局：垂直线性，ImagView+TextView
            convertView = View.inflate(mContext, R.layout.item_circle_member, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(position == 7){
            GlideImageLoader.displayProtrait(mContext,"",holder.ivCircleMember,R.drawable.dhg_0009_more_off);
        }else{
            GlideImageLoader.displayProtrait(mContext,
                    discoverCircleMemberList.get(position).getCircleMemberImgUrl(),holder.ivCircleMember);
            holder.ivCircleMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MinePageActivity.class);
                    intent.putExtra(Constants.USER_ID , discoverCircleMemberList.get(position).getCircleMemberId()+"");
                    intent.putExtra(Constants.IS_MYSELF,false);
                    mContext.startActivity(intent);
                }
            });
//            GlideImageLoader.displaysetdefault(mContext,holder.ivCircleMember,
//                    discoverCircleMemberList.get(position).getCircleMemberImgUrl(),
//                    R.drawable.ic_head_default);
        }

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_circle_member)
        ImageView ivCircleMember;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
