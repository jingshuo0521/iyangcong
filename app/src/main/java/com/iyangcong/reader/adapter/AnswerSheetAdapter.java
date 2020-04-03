package com.iyangcong.reader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.TestQuestion;
import com.iyangcong.reader.utils.ViewHolderModelNew;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnswerSheetAdapter extends BaseAdapter {
    Context mContext;
    List<TestQuestion> mList;
    boolean mIsSubmit;

    public AnswerSheetAdapter(Context context, List<TestQuestion> list,boolean isSubmit) {
        this.mContext = context;
        this.mList = list;
        this.mIsSubmit = isSubmit;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        if (mList == null || mList.size() == 0) return null;
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        AnswerSheetItemViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_answer_sheet, viewGroup, false);
            holder = new AnswerSheetItemViewHolder(mContext, convertView);
            holder.setData(mList.get(position), position);
            convertView.setTag(holder);
        } else {
            holder = (AnswerSheetItemViewHolder) convertView.getTag();
            holder.setData(mList.get(position), position);
        }
        return convertView;
    }

    public class AnswerSheetItemViewHolder extends ViewHolderModelNew<TestQuestion> {
        @BindView(R.id.tv_answer_number)
        TextView mTvAnswerNumber;

        public AnswerSheetItemViewHolder(Context context, View itemView) {
            super(context, itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public boolean setLayoutVisibility(TestQuestion testQuestion) {
            return testQuestion != null;
        }

        @Override
        public void bindData(TestQuestion testQuestion, int position) {
            mTvAnswerNumber.setText(position+1+"");
            if(mIsSubmit){
                if(testQuestion.getStatus() == 1){
                    mTvAnswerNumber.setTextColor(mContext.getResources().getColor(R.color.black));
                    mTvAnswerNumber.setBackgroundResource(R.drawable.bg_answer_correct);
                }else{
                    mTvAnswerNumber.setTextColor(mContext.getResources().getColor(R.color.red));
                    mTvAnswerNumber.setBackgroundResource(R.drawable.bg_answer_error);
                }
            }else{
                if(testQuestion.isHasAnswered()){
                    mTvAnswerNumber.setTextColor(mContext.getResources().getColor(R.color.white));
                    mTvAnswerNumber.setBackgroundResource(R.drawable.ic_answered);
                }else{
                    mTvAnswerNumber.setTextColor(mContext.getResources().getColor(R.color.black));
                    mTvAnswerNumber.setBackgroundResource(R.drawable.ic_not_answered);
                }
            }
        }
    }
}
