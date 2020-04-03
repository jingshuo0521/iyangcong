package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.AnswerSheetAdapter;
import com.iyangcong.reader.app.AppContext;
import com.iyangcong.reader.bean.ChapterTestBean;
import com.iyangcong.reader.bean.TestQuestion;
import com.iyangcong.reader.ui.button.FlatButton;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iyangcong.reader.activity.ChapterTestActivity.RESULT_SELECT;
import static com.iyangcong.reader.activity.ChapterTestActivity.RESULT_SUBMIT;

public class AnswerSheetActivity extends SwipeBackActivity {
    @BindView(R.id.gv_questions)
    GridView mGvQuestions;
    @BindView(R.id.btn_submit)
    FlatButton mBtnSubmit;
    @BindView(R.id.btnBack)
    ImageButton mBtnBack;
    @BindView(R.id.textHeadTitle)
    TextView mTextHeadTitle;
    @BindView(R.id.ibSign)
    ImageButton mIbSign;
    @BindView(R.id.btnFunction)
    ImageButton mBtnFunction;
    @BindView(R.id.tv_goods_num)
    TextView mTvGoodsNum;
    @BindView(R.id.btnFunction1)
    ImageButton mBtnFunction1;
    @BindView(R.id.tv_goods_num1)
    TextView mTvGoodsNum1;
    @BindView(R.id.layout_header)
    LinearLayout mLayoutHeader;

    AnswerSheetAdapter mAnswerSheetAdapter;
    ChapterTestBean mChapterTestBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_sheet);
        ButterKnife.bind(this);
        initView();
        setMainHeadView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mChapterTestBean = AppContext.getInstance().getChapterTestBean();
    }

    @Override
    protected void initView() {
        mAnswerSheetAdapter = new AnswerSheetAdapter(this,mChapterTestBean.getTestQuestionList(),mChapterTestBean.isSubmit());
        mGvQuestions.setAdapter(mAnswerSheetAdapter);
        mGvQuestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("question_index", i+1);
                setResult(RESULT_SELECT, intent);
                finish();
            }
        });
    }

    @Override
    protected void setMainHeadView() {
        mBtnBack.setVisibility(View.VISIBLE);
        mBtnBack.setImageResource(R.drawable.ic_back);
        mTextHeadTitle.setText("答题卡");
    }

    @OnClick({R.id.btnBack, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btn_submit:
                Intent intent = new Intent();
                setResult(RESULT_SUBMIT, intent);
                finish();
                break;
        }
    }
}
