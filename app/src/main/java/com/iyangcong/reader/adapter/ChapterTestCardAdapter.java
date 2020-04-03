package com.iyangcong.reader.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.TestQuestion;
import com.iyangcong.reader.ui.MyListView;
import com.iyangcong.reader.utils.ViewHolderModelNew;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChapterTestCardAdapter extends BaseAdapter {
    List<TestQuestion> mList;
    Context mContext;
    private boolean mIsSubmit;

    public ChapterTestCardAdapter(Context context, List<TestQuestion> list,boolean isSubmit) {
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
        ChapterTestCardViewHolder holder;
        if (convertView == null) {
//            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_local_video, null);
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_chapter_test_card, viewGroup, false);
            holder = new ChapterTestCardViewHolder(mContext, convertView);
            holder.setData(mList.get(position), position);
            convertView.setTag(holder);
        } else {
            holder = (ChapterTestCardViewHolder) convertView.getTag();
            holder.setData(mList.get(position), position);
        }
        return convertView;
    }

    public class ChapterTestCardViewHolder extends ViewHolderModelNew<TestQuestion>{
        @BindView(R.id.tv_question)
        TextView mTvQuestion;
        @BindView(R.id.lv_options)
        MyListView mLvOptions;
        @BindView(R.id.btn_view_answer)
        TextView mBtnViewAnswer;
        @BindView(R.id.tv_hint)
        TextView mTvHint;
        @BindView(R.id.tv_ref_answer)
        TextView mTvRefAnswer;
        @BindView(R.id.ll_ref_answer)
        LinearLayout mLlRefAnswer;
        @BindView(R.id.tv_your_answer)
        TextView mTvYourAnswer;
        @BindView(R.id.ll_your_answer)
        LinearLayout mLlYourAnswer;
        @BindView(R.id.ll_answer)
        LinearLayout mLlAnswer;
        @BindView(R.id.tv_answer_analysis)
        TextView mTvAnswerAnalysis;
        @BindView(R.id.ll_answer_analysis)
        LinearLayout mLlAnswerAnalysis;
        @BindView(R.id.v_divider)
        View mVDivider;
        AnswersAdapter mAnswersAdapter;

        public ChapterTestCardViewHolder(Context context, View itemView) {
            super(context, itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public boolean setLayoutVisibility(TestQuestion testQuestion) {
            return testQuestion!=null;
        }

        @Override
        public void bindData(final TestQuestion testQuestion, final int position) {
            mTvQuestion.setText(testQuestion.getContent());
            mTvAnswerAnalysis.setText(testQuestion.getAnalysis());
            mAnswersAdapter = new AnswersAdapter(mContext, testQuestion.getChoices());
            mLvOptions.setAdapter(mAnswersAdapter);
            mLvOptions.setDividerHeight(0);
            if(mIsSubmit){
                mLvOptions.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                mLlAnswer.setVisibility(View.VISIBLE);
                if(testQuestion.getYourAnswer() != null && testQuestion.getYourAnswer() != ""){
                    mLvOptions.setItemChecked(answer2Index(testQuestion.getYourAnswer()),true);
                    mTvYourAnswer.setText(testQuestion.getYourAnswer());
                }
                mLvOptions.setEnabled(false);
                // TODO: 2019/9/18
                mTvRefAnswer.setText(testQuestion.getCorrectAnswer());
                mLlAnswerAnalysis.setVisibility(View.VISIBLE);
                mBtnViewAnswer.setVisibility(View.GONE);
                mTvHint.setVisibility(View.GONE);
            }else {
                mLvOptions.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                mLvOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if(((AnswersAdapter.ChoiceListItemView)view).isChecked()){
                            mList.get(position).setHasAnswered(true);
//                            mList.get(position).setYourAnswer(i+"");
                            char answerId = (char)(65 + i);
                            mList.get(position).setYourAnswer(answerId+"");
                        }
                    }
                });
                mBtnViewAnswer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mVDivider.setVisibility(View.VISIBLE);
                        mLlAnswerAnalysis.setVisibility(View.VISIBLE);
                        mBtnViewAnswer.setVisibility(View.GONE);
                        mTvHint.setVisibility(View.GONE);
                        mLvOptions.setEnabled(false);
                    }
                }); 
            }
        }

        // TODO: 2019/9/23
        public int answer2Index(String answer){
            char l = answer.toCharArray()[0];
            int index;
            try {
                index = (int)l-65;
            }catch (Exception e){
                return 0;
            }
            return index;
        }
        
        public String answerTranslate(String answer){
            String result = null;
            try {
                result = (char)(Integer.parseInt(answer) + 65) + "";
            }catch (Exception e){
                return "";
            }
            return result;
        }
    }
}
