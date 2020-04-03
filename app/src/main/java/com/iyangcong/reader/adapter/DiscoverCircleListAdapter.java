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
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.DiscoverCircleDetailActivity;
import com.iyangcong.reader.bean.DiscoverCircleItemContent;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lg on 2016/12/28.
 */

public class DiscoverCircleListAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<DiscoverCircleItemContent> discoverCircleItemContentList;

    public DiscoverCircleListAdapter(Context mContext, List<DiscoverCircleItemContent> discoverCircleItemContentList) {
        this.mContext = mContext;
        this.discoverCircleItemContentList = discoverCircleItemContentList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DiscoverCircleViewHolder(LayoutInflater.from(mContext).inflate(R.layout.discover_circle_item, parent, false), mContext);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DiscoverCircleViewHolder discoverCircleViewHolder = (DiscoverCircleViewHolder) holder;
        discoverCircleViewHolder.setData(discoverCircleItemContentList);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    class DiscoverCircleViewHolder extends RecyclerView.ViewHolder {
        private Context mContext;
        private List<DiscoverCircleItemContent> discoverCircleItemContentList;
        @BindView(R.id.rv_discover_circle)
        RecyclerView rvDiscoverCircle;

        DiscoverCircleViewHolder(View itemView, Context mContext) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        void setData(List<DiscoverCircleItemContent> discoverCircleItemContentList) {
            CircleAdapter circleAdapter = new CircleAdapter(discoverCircleItemContentList);
            rvDiscoverCircle.setAdapter(circleAdapter);
            GridLayoutManager manager = new GridLayoutManager(mContext, 3);
            rvDiscoverCircle.setLayoutManager(manager);
        }
    }

    class CircleAdapter extends RecyclerView.Adapter {

        private List<DiscoverCircleItemContent> discoverCircleItemContentList;

        CircleAdapter(List<DiscoverCircleItemContent> discoverCircleItemContentList) {
            this.discoverCircleItemContentList = discoverCircleItemContentList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.rv_discover_circle_item, null);
            return new ViewHolder(view, mContext);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder)holder;
            viewHolder.setData(discoverCircleItemContentList.get(position));
        }

        @Override
        public int getItemCount() {
            return discoverCircleItemContentList == null ? 0 : discoverCircleItemContentList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private Context context;
            @BindView(R.id.im_item_head)
            ImageView imItemHead;
            @BindView(R.id.im_item_title)
            TextView imItemTitle;
            @BindView(R.id.im_item_content)
            TextView imItemContent;
            @BindView(R.id.im_item_ll)
            LinearLayout imItemLl;

            public ViewHolder(View itemView, Context mContext) {
                super(itemView);
                context = mContext;
                ButterKnife.bind(this, itemView);

            }
            void setData(final DiscoverCircleItemContent dcc){
                new GlideImageLoader().displayRoundCorner(mContext,dcc.getImgUrl(),imItemHead,R.drawable.pic_default_topic);
                imItemTitle.setText(StringUtils.delHTMLTag(dcc.getName()));
                imItemTitle.setMaxEms(4);
                imItemContent.setVisibility(View.GONE);
                imItemLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(context, DiscoverCircleDetailActivity.class);
                        intent.putExtra(Constants.circleId,dcc.getCircleId());
                        intent.putExtra(Constants.circleName,dcc.getName());
                        context.startActivity(intent);
                    }
                });
            }
        }

    }

}
