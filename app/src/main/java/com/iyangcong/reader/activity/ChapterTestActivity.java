package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.ChapterTestCardAdapter;
import com.iyangcong.reader.app.AppContext;
import com.iyangcong.reader.bean.ChapterTestBean;
import com.iyangcong.reader.bean.TestQuestion;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.ui.swipecardview.SwipeFlingAdapterView2;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.BaseRequest;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class ChapterTestActivity extends SwipeBackActivity {
    @BindView(R.id.btnBack)
    ImageButton mBtnBack;
    @BindView(R.id.iv_clock)
    ImageView mIvClock;
    @BindView(R.id.tv_test_time)
    TextView mTvTestTime;
    @BindView(R.id.tv_timu)
    TextView mTvTimu;
    @BindView(R.id.iv_datika)
    ImageView mIvDatika;
    @BindView(R.id.btn_pre_question)
    Button mBtnPreQuestion;
    @BindView(R.id.btn_next_question)
    Button mBtnNextQuestion;
    @BindView(R.id.ll_btns)
    LinearLayout mLlBtns;
    @BindView(R.id.sv_card_list)
    SwipeFlingAdapterView2 mSvCardList;

    ChapterTestCardAdapter mChapterTestCardAdapter;
    Runnable mTimeRunnable;
    Handler mHandler;
    long baseTimer;
    ChapterTestBean mChapterTestBean;
    int testTime;
    boolean isLastOne;

    public final static int REQ_SHEET = 101;
    public final static int RESULT_SELECT = 102;
    public final static int RESULT_SUBMIT = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chpater_test);
        ButterKnife.bind(this);
        initView();
        setMainHeadView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        if(AppContext.getInstance().getChapterTestBean() == null){
            ChapterTestBean bean = new ChapterTestBean();
            List<TestQuestion> testQuestions = new ArrayList<>();
            final List<String> choices = new ArrayList<>();
            choices.add("onto");
            choices.add("into");
            choices.add("through");
            choices.add("broke into");
            for (int i = 0; i < 5; i++) {
                TestQuestion question = new TestQuestion();
                question.setHasAnswered(false);
                question.setContent("The little prince broke _____ a lovely peal of laughter, which irritated me very much.");
                question.setChoices(choices);
                question.setType(1);
                question.setCorrectAnswer("2");
                testQuestions.add(question);
            }
            bean.setTestQuestionList(testQuestions);
            AppContext.getInstance().setChapterTestBean(bean);
        }
        mChapterTestBean = AppContext.getInstance().getChapterTestBean();
        if(!getIntent().getBooleanExtra("is_only_read",false)){
            mChapterTestBean.resetTest();
        }
    }

    @Override
    protected void initView() {
        if (mSvCardList != null) {
            mChapterTestCardAdapter = new ChapterTestCardAdapter(this, mChapterTestBean.getTestQuestionList(),mChapterTestBean.isSubmit());
            mSvCardList.setAdapter(mChapterTestCardAdapter);
            mSvCardList.setSwipeViewListener(new SwipeFlingAdapterView2.SwipeViewListener() {
                @Override
                public void onIndexChanged(int nowIndex) {
                    mTvTimu.setText("题目" + nowIndex + "/" + mChapterTestCardAdapter.getCount());
                    if(nowIndex == 1){
                        mBtnPreQuestion.setVisibility(View.INVISIBLE);
                    }else {
                        mBtnPreQuestion.setVisibility(View.VISIBLE);
                    }

                    if(nowIndex == mChapterTestCardAdapter.getCount()){
                        if(mChapterTestBean.isSubmit()){
                            isLastOne = false;
                            mBtnNextQuestion.setVisibility(View.INVISIBLE);
                        }else{
                            isLastOne = true;
                            mBtnNextQuestion.setText("提交");
                            mBtnNextQuestion.setVisibility(View.VISIBLE);
                        }
                    }else {
                        isLastOne = false;
                        mBtnNextQuestion.setText("下一题");
                        mBtnNextQuestion.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
        if(mChapterTestBean.isSubmit()){
            mIvDatika.setVisibility(View.GONE);
        }
        if(getIntent().getBooleanExtra("is_only_read",false)){
            int position = getIntent().getExtras().getInt("question_index");//得到新Activity 关闭后返回的数据
            mSvCardList.setActivePosition(position);
        }
    }

    @Override
    protected void setMainHeadView() {
        // 计时器
        mHandler = new Handler();
        if(mChapterTestBean.isSubmit()){
            mTvTestTime.setText(timeInt2String(mChapterTestBean.getTime()));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!mChapterTestBean.isSubmit()) {
            stopClock();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!mChapterTestBean.isSubmit()){
            startClock();
        }
    }

    private void stopClock(){
        if (mTimeRunnable != null)
            mHandler.removeCallbacks(mTimeRunnable);
        mTimeRunnable = null;
        baseTimer = 0;
        mChapterTestBean.setTime(testTime);
    }

    private void startClock(){
        if (mTimeRunnable == null) {
            mTimeRunnable = new Runnable() {
                @Override
                public void run() {
                    mTvTestTime.setText(getTime());
                    mHandler.postDelayed(this, 1000);
                }
            };
            mHandler.postDelayed(mTimeRunnable, 0);
        }
    }

    private String getTime() {
        if (0 == baseTimer) {
            baseTimer = SystemClock.elapsedRealtime();
        }
        testTime = (int) ((SystemClock.elapsedRealtime() - baseTimer) / 1000) + mChapterTestBean.getTime();
        return timeInt2String(testTime);
    }

    private String timeInt2String(int time){
        String hh = new DecimalFormat("00").format(time / 3600);
        String mm = new DecimalFormat("00").format(time / 60);
        String ss = new DecimalFormat("00").format(time % 60);
        return hh + ":" + mm + ":" + ss;
    }

    @OnClick({R.id.btnBack, R.id.iv_datika, R.id.btn_pre_question, R.id.btn_next_question})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.iv_datika:
                Intent intent = new Intent(this,AnswerSheetActivity.class);
                startActivityForResult(intent,REQ_SHEET);
                break;
            case R.id.btn_pre_question:
                mSvCardList.pre();
                break;
            case R.id.btn_next_question:
                if(isLastOne){
                    submit();
                }else{
                    mSvCardList.next();
                }
                break;
        }
    }

    private void submit(){
        showLoadingDialog();
        BaseRequest submitPoster = OkGo.get(Urls.CommitAnswer)
                .tag(this)
                .params("bookId",(int) SharedPreferenceUtil.getInstance().getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0))
//                .params("bookId",SharedPreferenceUtil.getInstance().getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0)+"")
                .params("userId", SharedPreferenceUtil.getInstance().getLong(SharedPreferenceUtil.USER_ID, 0)+"")
                .params("chapterId",mChapterTestBean.getChapterId());
        for(TestQuestion item:mChapterTestBean.getTestQuestionList()){
            submitPoster = submitPoster.params("answers[]",item.getYourAnswer()==null?"":item.getYourAnswer(),false);
        }
        submitPoster.execute(new JsonCallback<IycResponse<SubmitResult>>(this) {
            @Override
            public void onSuccess(IycResponse<SubmitResult> submitResultIycResponse, Call call, Response response) {
                if(submitResultIycResponse == null || submitResultIycResponse.data ==null){
                    Toast.makeText(context,"获取测试题目为空",Toast.LENGTH_LONG).show();
                    return;
                }
                dismissLoadingDialig();
                if(mChapterTestBean.sumbit()){
                    mChapterTestBean.setTime(testTime);
                    startActivity(new Intent(context,TestResultActivity.class));
                    finish();
                }else {
                    Toast.makeText(context,"提交测试结果失败",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                dismissLoadingDialig();
                Toast.makeText(context,"提交测试结果失败",Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 为了得到传回的数据，必须在前面的Activity中（指MainActivity类）重写onActivityResult方法
     *
     * requestCode 请求码，即调用startActivityForResult()传递过去的值
     * resultCode 结果码，结果码用于标识返回数据来自哪个新Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_SHEET) {
            switch (resultCode) {
                case RESULT_SELECT:
                    int position = data.getExtras().getInt("question_index");//得到新Activity 关闭后返回的数据
                    mSvCardList.setActivePosition(position);
                    break;
                case RESULT_SUBMIT:
                    submit();
                    break;
            }
        }
    }

    /**
     * 提交结果接受类
     */
    public class SubmitResult{
        private long timestamp;
        private int score;

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }
}
