package com.iyangcong.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.DiscoverCircleDetailActivity;
import com.iyangcong.reader.bean.MineCircle;
import com.iyangcong.reader.ui.customtablayout.listener.OnTabSelectListener;
import com.iyangcong.reader.ui.customtablayout.widget.SegmentTabLayout;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.GlideImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ljw on 2017/1/6.
 */

public class MinePageCircleAdapter extends RecyclerView.Adapter {

    private Context context;

    /**
     * 使用mLayoutInflater来初始化布局
     */
    private LayoutInflater mLayoutInflater;
    private List<MineCircle> mineCreateList;
    private List<MineCircle> mineAttendList;
    private boolean isCreatedByMyself = true;
    private SegPositionCallback segPositionCallback;

    public MinePageCircleAdapter(Context context, List<MineCircle> mineCreateList, List<MineCircle> mineAttendList, boolean isCreatedByMyself) {
        this.context = context;
        this.mineCreateList = mineCreateList;
        this.mineAttendList = mineAttendList;
        this.isCreatedByMyself = isCreatedByMyself;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CircleViewHolder(context, mLayoutInflater.inflate(R.layout.item_mine_page_circle, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CircleViewHolder circleViewHolder = (CircleViewHolder) holder;
        circleViewHolder.setData(mineCreateList, mineAttendList);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    /**
     * 分类 ViewHolder
     */
    public class CircleViewHolder extends RecyclerView.ViewHolder {
        CircleAdapter circleAdapter;
        private final Context mContext;
        @BindView(R.id.rv_mine_page_circle)
        RecyclerView rvMinePageCircle;
        @BindView(R.id.seg_tab_circle)
        SegmentTabLayout segTabcircle;

        private String[] TopicTitles = new String[2];

        CircleViewHolder(final Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);

            segTabcircle.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelect(int position) {
                    if (position == 0) {
                        isCreatedByMyself = true;
                        segPositionCallback.getSegPosition(position);
                    } else if (position == 1) {
                        isCreatedByMyself = false;
                        segPositionCallback.getSegPosition(position);
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onTabReselect(int position) {

                }
            });
        }

        void setData(List<MineCircle> mineCreateList, List<MineCircle> mineAttendList) {
            if (isCreatedByMyself)
                circleAdapter = new CircleAdapter(mContext, mineCreateList);
            else
                circleAdapter = new CircleAdapter(mContext, mineAttendList);
            if (mineCreateList != null && mineCreateList.size() > 0) {
                TopicTitles[0] = "创建的圈子(" + mineCreateList.get(0).getTotalNum() + ")";
            } else {
                TopicTitles[0] = "创建的圈子(0)";
            }
            if (mineAttendList != null && mineAttendList.size() > 0) {
                TopicTitles[1] = "加入的圈子(" + mineAttendList.get(0).getTotalNum() + ")";
            } else {
                TopicTitles[1] = "加入的圈子(0)";
            }
            GridLayoutManager manager = new GridLayoutManager(mContext, 4);
            rvMinePageCircle.setLayoutManager(manager);
            rvMinePageCircle.setNestedScrollingEnabled(false);
            rvMinePageCircle.setAdapter(circleAdapter);
            segTabcircle.setTabData(TopicTitles);
        }
    }

    class CircleAdapter extends RecyclerView.Adapter {

        private Context context;
        private LayoutInflater mLayoutInflater;
        private List<MineCircle> mineCircleList;

        CircleAdapter(Context context, List<MineCircle> mineCircleList) {
            this.mineCircleList = mineCircleList;
            this.context = context;
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CircleHolder(mLayoutInflater.inflate(R.layout.rv_discover_circle_item, null), context);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            CircleHolder circleHolder = (CircleHolder) holder;
            circleHolder.setData(mineCircleList.get(position));
        }

        @Override
        public int getItemCount() {
            return mineCircleList.size();
        }

        class CircleHolder extends RecyclerView.ViewHolder {
            Context context;
            @BindView(R.id.im_item_head)
            ImageView imItemHead;
            @BindView(R.id.im_item_title)
            TextView imItemTitle;
            @BindView(R.id.im_item_content)
            TextView imItemContent;
            @BindView(R.id.im_item_ll)
            LinearLayout imItemLl;
            @BindView(R.id.iv_is_class)
            ImageView ivIsClass;

            public CircleHolder(View itemView, Context mContext) {
                super(itemView);
                this.context = mContext;
                ButterKnife.bind(this, itemView);
            }

            void setData(final MineCircle mineCircle) {
                if(mineCircle.isclassgroup()){
                    ivIsClass.setVisibility(View.VISIBLE);
                }
                GlideImageLoader.displaysetdefault(context, imItemHead, mineCircle.getCover(), R.drawable.pic_default_topic);
                imItemTitle.setText(mineCircle.getGroupname());
                imItemContent.setVisibility(View.INVISIBLE);
                imItemLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, DiscoverCircleDetailActivity.class);
                        intent.putExtra(Constants.circleId, mineCircle.getGroupId());
                        intent.putExtra(Constants.circleName, mineCircle.getGroupname());
                        context.startActivity(intent);
                    }
                });
            }
        }

    }

    public interface SegPositionCallback {
        public void getSegPosition(int position);
    }

    public void setSegPositionCallback(SegPositionCallback segPositionCallback) {
        this.segPositionCallback = segPositionCallback;
    }

}
