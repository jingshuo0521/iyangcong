package com.iyangcong.reader.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.ViewHolderModel;
import com.iyangcong.reader.utils.ViewHolderModelNew;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnswersAdapter extends BaseAdapter {
    public static int SINGLE_CHOICE = 80001;
    public static int MULTIPLE_CHOICE = 80002;
    private Context mContext;
    private List<String> mAnswerList;
    private int questionType; // 默认单选

    public AnswersAdapter(Context context, List<String> answerList) {
        this(context,answerList,SINGLE_CHOICE);
    }

    public AnswersAdapter(Context context, List<String> answerList,int questionType) {
        this.mContext = context;
        this.mAnswerList = answerList;
        this.questionType = questionType;
    }

    @Override
    public int getCount() {
        return mAnswerList == null ? 0 : mAnswerList.size();
    }

    @Override
    public Object getItem(int i) {
        return mAnswerList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ChoiceListItemView view;
        if (convertView == null) {
            view = new ChoiceListItemView(mContext, null);
        } else {
            view = (ChoiceListItemView) convertView;
        }
        view.setData(mAnswerList.get(position),position);
        return view;
    }
    public class ChoiceListItemView extends LinearLayout implements Checkable {
        @BindView(R.id.tv_answer_id)
        TextView mTvAnswerId;
        @BindView(R.id.tv_answer_content)
        TextView mTvAnswerContent;
        @BindView(R.id.iv_answer_content)
        ImageView mIvAnswerContent;
        @BindView(R.id.ll_answer)
        LinearLayout mLlanswer;

        private boolean isChecked;
        public ChoiceListItemView(Context context, AttributeSet attrs) {
            super(context, attrs);
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.item_test_answer, this, true);
            ButterKnife.bind(this, v);
        }

        public void setData(String s,int position) {
            char answerId = (char)(65 + position);
            mTvAnswerId.setText(answerId+"");
            if(s.contains("%image%")){
                mTvAnswerContent.setVisibility(View.GONE);
                mIvAnswerContent.setVisibility(View.VISIBLE);
                GlideImageLoader.displayNoDefault(mContext,mIvAnswerContent,s.replaceAll("%image%",""));
            }else {
                mIvAnswerContent.setVisibility(View.GONE);
                mTvAnswerContent.setVisibility(View.VISIBLE);
                mTvAnswerContent.setText(s);
            }
        }

        @Override
        public boolean isChecked() {
            return isChecked;
        }

        @Override
        public void setChecked(boolean checked) {
            isChecked = checked;
            //根据是否选中来选择不同的背景图片
            if (checked) {
                mTvAnswerId.setBackgroundResource(R.drawable.bg_item_answer_selected);
                mTvAnswerId.setTextColor(mContext.getResources().getColor(R.color.white));
                mTvAnswerContent.setTextColor(mContext.getResources().getColor(R.color.black));
            } else {
                mTvAnswerId.setBackgroundResource(R.drawable.bg_item_answer);
                mTvAnswerId.setTextColor(mContext.getResources().getColor(R.color.black));
                mTvAnswerContent.setTextColor(mContext.getResources().getColor(R.color.black1));
            }
        }

        @Override
        public void toggle() {
        }
    }
}
