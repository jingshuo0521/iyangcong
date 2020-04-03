package com.iyangcong.reader.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.DiscoverTopic;
import com.iyangcong.reader.ui.customtablayout.listener.OnTabSelectListener;
import com.iyangcong.reader.ui.customtablayout.widget.SegmentTabLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * Created by ljw on 2017/1/6.
 */

public class MinePageTopicAdapter extends RecyclerView.Adapter {


    private Context context;

    /**
     * 使用mLayoutInflater来初始化布局
     */
    private LayoutInflater mLayoutInflater;
    private List<DiscoverTopic> CreateTopicList ;
    private List<DiscoverTopic> JoinTopicList ;

    private SegPositionCallback segPositionCallback;

    private int segTabposition;
    public MinePageTopicAdapter(Context context,List<DiscoverTopic> CreateTopicList,List<DiscoverTopic> JoinTopicList,int segTabposition ) {
        this.context = context;
        this.segTabposition=segTabposition;
        this.CreateTopicList=CreateTopicList;
        this.JoinTopicList=JoinTopicList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TopicViewHolder(context, mLayoutInflater.inflate(R.layout.item_mine_page_topic, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TopicViewHolder topicViewHolder = (TopicViewHolder) holder;
        topicViewHolder.setData(CreateTopicList,JoinTopicList);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    /**
     * 分类 ViewHolder
     */
    public class TopicViewHolder extends RecyclerView.ViewHolder {
        DiscoverTopicAdapter discoverTopicAdapter;

        private final Context mContext;
        @BindView(R.id.rv_mine_page_topic)
        RecyclerView rvMinePageTopic;
        @BindView(R.id.seg_tab_topic)
        SegmentTabLayout segTabTopic;

        private String[] TopicTitles = new String[2];

        TopicViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);


            segTabTopic.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelect(int position) {
                    if(position == 0){
                        segTabposition = 0;
                        segPositionCallback.getSegPosition(segTabposition);
                    }else if(position == 1){
                        segTabposition = 1;
                        segPositionCallback.getSegPosition(segTabposition);
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onTabReselect(int position) {

                }
            });
        }

        void setData(List<DiscoverTopic> createTopicList,List<DiscoverTopic> joinTopicList) {
            if(segTabposition==0){
                discoverTopicAdapter = new DiscoverTopicAdapter(mContext, createTopicList,true);
            }else if (segTabposition==1){
                discoverTopicAdapter = new DiscoverTopicAdapter(mContext, joinTopicList,true);
            }
            if (createTopicList!=null&&createTopicList.size()>0){
                TopicTitles[0]="创建的话题("+createTopicList.get(0).getTotalNum()+")";
            }else{
                TopicTitles[0]="创建的话题(0)";
            }

            if( joinTopicList!=null&&joinTopicList.size()>0){
                TopicTitles[1]="加入的话题("+joinTopicList.get(0).getTotalNum()+")";
            }else{
                TopicTitles[1]="加入的话题(0)";
            }
            LinearLayoutManager manager = new LinearLayoutManager(mContext);
            rvMinePageTopic.setLayoutManager(manager);
            rvMinePageTopic.setNestedScrollingEnabled(false);
            rvMinePageTopic.setAdapter(discoverTopicAdapter);
            segTabTopic.setTabData(TopicTitles);
        }
    }
    public interface SegPositionCallback {
        public void getSegPosition(int position);
    }
    public void setSegPositionCallback(SegPositionCallback segPositionCallback){
        this.segPositionCallback = segPositionCallback;
    }
}