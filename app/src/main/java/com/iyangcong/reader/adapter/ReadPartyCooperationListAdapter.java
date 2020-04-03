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
import com.iyangcong.reader.activity.DiscoverCooperationUnitActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/1/17.
 */

public class ReadPartyCooperationListAdapter extends RecyclerView.Adapter {

    private List<String> cooperationUnitList;
    private Context mContext;

    public ReadPartyCooperationListAdapter(List<String> cooperationUnitList, Context mContext) {
        this.cooperationUnitList = cooperationUnitList;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_gv_classification, null);
        CooperationViewHolder cooperationViewHolder = new CooperationViewHolder(view, mContext);
        return cooperationViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CooperationViewHolder cooperationViewHolder = (CooperationViewHolder) holder;
        cooperationViewHolder.setData(cooperationUnitList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return cooperationUnitList.size();
    }

    class CooperationViewHolder extends RecyclerView.ViewHolder {

        private Context context;

        @BindView(R.id.tv_classification_name)
        TextView tvClassificationName;
        @BindView(R.id.layout_gv_classification)
        LinearLayout layoutGVClassification;
        public CooperationViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            ButterKnife.bind(this, itemView);

        }
        void setData(String cooperationUnit, int position){
            if(cooperationUnit == null || cooperationUnit.equals("")){
                layoutGVClassification.setVisibility(View.GONE);
                return;
            }
            layoutGVClassification.setVisibility(View.VISIBLE);
            tvClassificationName.setText(cooperationUnit);
            if (position % 2 == 1){
                tvClassificationName.setBackgroundResource(R.drawable.selector_classification_right_tv);
            }else{
                tvClassificationName.setBackgroundResource(R.drawable.selector_classification_leftmiddle_tv);
            }
            tvClassificationName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    context.startActivity(new Intent(context, DiscoverCooperationUnitActivity.class));
                }
            });
        }
    }
}
