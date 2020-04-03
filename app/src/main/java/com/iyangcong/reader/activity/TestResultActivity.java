package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
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
import com.iyangcong.reader.ui.button.FlatButton;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.iyangcong.reader.activity.ChapterTestActivity.RESULT_SELECT;

public class TestResultActivity extends SwipeBackActivity {
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
    @BindView(R.id.tv_tonguolv)
    TextView mTvTonguolv;
    @BindView(R.id.tv_haoshi)
    TextView mTvHaoshi;
    @BindView(R.id.gv_result)
    GridView mGvResult;
    @BindView(R.id.btn_retest)
    FlatButton mBtnRetest;
    @BindView(R.id.btn_backbook)
    FlatButton mBtnBackbook;
    @BindView(R.id.ll_btns)
    LinearLayout mLlBtns;

    AnswerSheetAdapter mAnswerSheetAdapter;
    ChapterTestBean mChapterTestBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);
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
        if(mChapterTestBean.isSubmit()){
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(3);
            String str = "<font><big><big>" + nf.format(mChapterTestBean.getPassingRate()*100) + "</big></big></font>%";
            mTvTonguolv.setText(Html.fromHtml(str));
            mTvHaoshi.setText(Html.fromHtml(timeInt2String(mChapterTestBean.getTime())));
            mAnswerSheetAdapter = new AnswerSheetAdapter(this,mChapterTestBean.getTestQuestionList(),mChapterTestBean.isSubmit());
            mGvResult.setAdapter(mAnswerSheetAdapter);
            mGvResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(TestResultActivity.this,ChapterTestActivity.class);
                    intent.putExtra("question_index", i+1);
                    intent.putExtra("is_only_read", true);
                    startActivity(intent);
                }
            });
        }else{
            mTvHaoshi.setText("测试结果出错，尚未提交");
        }
    }

    private String timeInt2String(int time){
        int hh = time / 3600;
        int mm = time / 60;
        int ss =time % 60;
        return "测试耗时 " + (hh>0?("<font><big><big>" + hh + "</big></big></font>"+" 小时 "):"")
                + (mm>0?("<font><big><big>" + mm + "</big></big></font>"+" 分钟 "):"")
                + "<font><big><big>" + ss + "</big></big></font>" + " 秒";
    }

    @Override
    protected void setMainHeadView() {
        mBtnBack.setVisibility(View.VISIBLE);
        mBtnBack.setImageResource(R.drawable.ic_back);
        mTextHeadTitle.setText("测试结果");
    }

    @OnClick({R.id.btnBack, R.id.btn_retest, R.id.btn_backbook})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btn_retest:
                startActivity(new Intent(this,ChapterTestActivity.class));
//                AppContext.getInstance().getChapterTestBean().resetTest();
                finish();
                break;
            case R.id.btn_backbook:
                finish();
                break;
        }
    }
}
