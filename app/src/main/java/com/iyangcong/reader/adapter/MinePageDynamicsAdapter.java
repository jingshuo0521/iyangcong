package com.iyangcong.reader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.MineDynamic;
import com.iyangcong.reader.utils.HtmlParserUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ljw on 2017/1/6.
 */

public class MinePageDynamicsAdapter extends RecyclerView.Adapter {

    private Context context;

    /**
     * 使用mLayoutInflater来初始化布局
     */
    private LayoutInflater mLayoutInflater;
    private List<MineDynamic> mineDynamicsList;

    public MinePageDynamicsAdapter(Context context,List<MineDynamic> mineDynamicsList) {
        this.context = context;
        this.mineDynamicsList = mineDynamicsList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DynamicsViewHolder(context, mLayoutInflater.inflate(R.layout.item_mine_page_dynamics, parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DynamicsViewHolder dynamicsViewHolder = (DynamicsViewHolder) holder;
        dynamicsViewHolder.setData(mineDynamicsList.get(position));
    }

    @Override
    public int getItemCount() {
        return mineDynamicsList.size();
    }


    /**
     * 分类 ViewHolder
     */
    public class DynamicsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_action)
        TextView tvAction;
        @BindView(R.id.tv_state)
        TextView tvState;

        private Context context;
        private final Context mContext;

        DynamicsViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        void setData(MineDynamic mineDynamic) {
//            Date date=new Date(mineDynamic.getTime());
//            String time_strs="";
//            SimpleDateFormat sdf=new SimpleDateFormat("MM-dd HH:mm");
//            time_strs=sdf.format(date);
            tvTime.setText(mineDynamic.getTime());
            tvAction.setText(mineDynamic.getAction());
            String html = mineDynamic.getStateName();
            tvState.setText(Html.fromHtml(HtmlParserUtils.getContent(html)));
        }
    }

}
