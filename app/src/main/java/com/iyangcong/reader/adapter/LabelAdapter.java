package com.iyangcong.reader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.CircleLabel;
import com.orhanobut.logger.Logger;

import org.geometerplus.fbreader.book.Label;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by WuZepeng on 2017-03-03.
 * 这个adapter在DiscoverCircleAdapter和CircleBaseDialog中使用了，用于显示标签。如果更改的话请考虑产生的影响
 */

public class LabelAdapter extends RecyclerView.Adapter {


    @BindView(R.id.label_style_two1)
    TextView labelStyleTwo1;
    @BindView(R.id.label_style_two2)
    TextView labelStyleTwo2;
    @BindView(R.id.label_style_two3)
    TextView labelStyleTwo3;
    @BindView(R.id.labelLayout)
    LinearLayout labelLayout;
    private Context mContext;
    private List<CircleLabel> labelList;
    private boolean isSmallLabel;
    private List<CircleLabel> selectedLabelList;
    private OnLabelSeclectedListener labelSeclectedListener;
    private OnItemChoosedListener itemChoosedListener;


    public LabelAdapter(Context mContext, List<CircleLabel> labelList, boolean isSmallLabel) {
        this.mContext = mContext;
        this.labelList = labelList;
        this.isSmallLabel = isSmallLabel;
        selectedLabelList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isSmallLabel)
            return new LabelHolder(LayoutInflater.from(mContext).inflate(R.layout.item_label, parent, false), mContext);
        else
            return new ThreeItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.label_three_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isSmallLabel) {
            LabelHolder labelHolder = (LabelHolder) holder;
            labelHolder.setData(labelList.get(position));
        } else {
            ThreeItemViewHolder tvHolder = (ThreeItemViewHolder) holder;
            tvHolder.setData(labelList, position);
        }
    }

    @Override
    public int getItemCount() {
        return labelList.size();
    }



    /**
     * 该ViewHolder用于显示圈子详情页的标签
     */
    public class LabelHolder extends RecyclerView.ViewHolder {
        private Context context;
        @BindView(R.id.label_style_one)
        TextView labelStyleOne;
        @BindView(R.id.layout_item_label)
        LinearLayout layoutItemLabel;
        public LabelHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            ButterKnife.bind(this, itemView);

        }

        public void setData(CircleLabel label) {
            if(label == null){
                layoutItemLabel.setVisibility(View.GONE);
                return;
            }
            layoutItemLabel.setVisibility(View.VISIBLE);
            Logger.i("tagname:" + label.getTagName());
            labelStyleOne.setText(label.getTagName());
        }
    }

    /**
     * 该ViewHolder用于显示创建圈子时需要用到的标签
     */
    public class ThreeItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.label_style_two1)
        TextView labelStyleTwo1;
        @BindView(R.id.label_style_two2)
        TextView labelStyleTwo2;
        @BindView(R.id.label_style_two3)
        TextView labelStyleTwo3;
        @BindView(R.id.labelLayout)
        LinearLayout labelLayout;


        public ThreeItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.label_style_two1, R.id.label_style_two2, R.id.label_style_two3})
        public void onClick(View view) {
            int position = -1;
            switch (view.getId()) {
                case R.id.label_style_two1:
                    position = labelList.indexOf((CircleLabel)labelStyleTwo1.getTag());
                    selectedLabelList = checkChooseState(selectedLabelList,
                            (CircleLabel)labelStyleTwo1.getTag());
                    break;
                case R.id.label_style_two2:
                    position = labelList.indexOf((CircleLabel)labelStyleTwo2.getTag());
                    selectedLabelList = checkChooseState(selectedLabelList,
                            (CircleLabel)labelStyleTwo2.getTag());
                    break;
                case R.id.label_style_two3:
                    position = labelList.indexOf((CircleLabel)labelStyleTwo3.getTag());
                    selectedLabelList = checkChooseState(selectedLabelList,
                            (CircleLabel)labelStyleTwo3.getTag());
                    break;
            }

            if(labelSeclectedListener != null)
                labelSeclectedListener.onLabelSelceted(selectedLabelList);
            if(itemChoosedListener != null)
                itemChoosedListener.onItemChoosed(position);
        }

        /**
         * 检查列表中被点击的label，将被点击的label添加到一个列表中，用于返回数据。
         * @param list
         * @param label
         * @return
         * by wzp
         */
        private List<CircleLabel> checkChooseState(List<CircleLabel> list, CircleLabel label){
            List<CircleLabel> tempList = new ArrayList<>(list);
            if(!list.contains(label)) {
                tempList.add(label);
            }else{
                tempList.remove(label);
            }
            label.setChecked(!label.isChecked());
            notifyDataChanged(labelList.indexOf(label));
            return tempList;
        }


        public void setData(List<CircleLabel> list, int position) {
            if (position % 3 != 0) {
                labelLayout.setVisibility(View.GONE);
                labelStyleTwo1.setVisibility(View.GONE);
                labelStyleTwo2.setVisibility(View.GONE);
                labelStyleTwo3.setVisibility(View.GONE);
            } else {
                labelLayout.setVisibility(View.VISIBLE);
                tvSetter(labelStyleTwo1,list.get(position));
                if (position + 1 < list.size()) {//防止加载数据越界
                    tvSetter(labelStyleTwo2,list.get(position + 1));
                }
                else
                    labelStyleTwo2.setVisibility(View.INVISIBLE);
                if (position + 2 < list.size()) {//防止加载数据越界
                    tvSetter(labelStyleTwo3,list.get(position + 2));
                }else
                    labelStyleTwo3.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void notifyDataChanged(int position){
        if(position % 3 != 0)
            position = position - position % 3;
        this.notifyItemChanged(position);
    }

    /**
     * textview通用设置方法
     * @param tv
     * @param label
     * @return
     * by wzp
     */
    private TextView tvSetter(TextView tv,CircleLabel label){
        tv.setText(label.getTagName());
        tv.setTag(label);
        tv.setVisibility(View.VISIBLE);
        if(label.isChecked())
            tv.setBackgroundResource(R.drawable.bg_text_label_pressed2);
        else
            tv.setBackgroundResource(R.drawable.bg_read_party_coming);
        return tv;
    }

    public void setLabelSeclectedListener(OnLabelSeclectedListener labelSeclectedListener) {
        this.labelSeclectedListener = labelSeclectedListener;
    }

    public OnItemChoosedListener getItemChoosedListener() {
        return itemChoosedListener;
    }

    public void setItemChoosedListener(OnItemChoosedListener itemChoosedListener) {
        this.itemChoosedListener = itemChoosedListener;
    }

    public OnLabelSeclectedListener getLabelSeclectedListener() {
        return labelSeclectedListener;
    }

    public interface OnLabelSeclectedListener{
        public void onLabelSelceted(List<CircleLabel> labels);
    }

    public interface OnItemChoosedListener{
        public void onItemChoosed(int position);
    }
}
