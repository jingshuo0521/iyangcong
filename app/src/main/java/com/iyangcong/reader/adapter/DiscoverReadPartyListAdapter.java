package com.iyangcong.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.DiscoverReadingPartyActivity;
import com.iyangcong.reader.activity.DiscoverReadingPartyDetailsActivity;
import com.iyangcong.reader.bean.DiscoverReadPartyExercise;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.IntentUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ljw on 2017/1/4.
 */

public class DiscoverReadPartyListAdapter extends RecyclerView.Adapter {

    private Context context;

    /**
     * 使用mLayoutInflater来初始化布局
     */
    private LayoutInflater mLayoutInflater;

    private int limitCount;

    private List<DiscoverReadPartyExercise> discoverReadPartyActivityList;

    public DiscoverReadPartyListAdapter(Context context, List<DiscoverReadPartyExercise> discoverReadPartyActivityList,int limitCount) {
        this.context = context;
        this.discoverReadPartyActivityList = discoverReadPartyActivityList;
        mLayoutInflater = LayoutInflater.from(context);
        this.limitCount = limitCount;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReadPartyListViewHolder(context, mLayoutInflater.inflate(R.layout.item_discover_read_party_activity, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ReadPartyListViewHolder readPartyListViewHolder = (ReadPartyListViewHolder) holder;
        readPartyListViewHolder.setData(discoverReadPartyActivityList.get(position));
    }

    @Override
    public int getItemCount() {
        if(discoverReadPartyActivityList == null)
            return 0;
        int size = discoverReadPartyActivityList.size();
        if(limitCount < 0)
            return size;
        return size>limitCount?limitCount:size;
    }



    /**
     * 读书会活动 ViewHolder
     */
    public class ReadPartyListViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;
        @BindView(R.id.tv_read_party_activity_title)
        TextView tvReadPartyActivityTitle;
        @BindView(R.id.tv_read_party_activity_btn)
        TextView tvReadPartyActivityBtn;
        @BindView(R.id.layout_item_read_party_acitivity)
        LinearLayout layoutItemReadPartyAcitivity;
        ReadPartyListViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }


        @OnClick(R.id.layout_item_read_party_acitivity)
        public void onClick() {
            DiscoverReadPartyExercise exercise = (DiscoverReadPartyExercise)tvReadPartyActivityBtn.getTag();
            if(exercise != null){
                Intent intent = new Intent(mContext, DiscoverReadingPartyDetailsActivity.class);
                intent.putExtra(Constants.readingPartyId,exercise.getClubId());
                intent.putExtra(Constants.readingPartyTitle,exercise.getClubName());
                context.startActivity(intent);
            }

        }

        void setData(DiscoverReadPartyExercise exercise) {
            if(exercise != null){
                layoutItemReadPartyAcitivity.setVisibility(View.VISIBLE);
                tvReadPartyActivityTitle.setText(exercise.getClubName());
                if(exercise.isComing()){
                    tvReadPartyActivityBtn.setText("COMING");
                }else {
                    tvReadPartyActivityBtn.setText("WAIT");
                }
                tvReadPartyActivityBtn.setTag(exercise);
                return;
            }else{
                layoutItemReadPartyAcitivity.setVisibility(View.GONE);
            }
        }

        private String getContent(DiscoverReadPartyExercise exercise) {
            if (!"".equals(exercise.getLocation()) && !"".equals(exercise.getClubName()))
                return "【" + exercise.getLocation() + "】" + "  " + exercise.getClubName();
            return "null!!!!!!!!";
        }
    }
}
