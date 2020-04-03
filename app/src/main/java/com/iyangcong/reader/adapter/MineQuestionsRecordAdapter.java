package com.iyangcong.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.ChapterTestActivity;
import com.iyangcong.reader.activity.TestResultActivity;
import com.iyangcong.reader.app.AppContext;
import com.iyangcong.reader.base.BaseActivity;
import com.iyangcong.reader.bean.ChapterTestBean;
import com.iyangcong.reader.bean.QuestionRecord;
import com.iyangcong.reader.bean.TestRecord;
import com.iyangcong.reader.callback.JsonCallback2;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.DateUtils;
import com.iyangcong.reader.utils.Urls;
import com.iyangcong.reader.utils.ViewHolderModel;
import com.lzy.okgo.OkGo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

public class MineQuestionsRecordAdapter extends RecyclerView.Adapter {
    private Context context;

    /**
     * 使用mLayoutInflater来初始化布局
     */
    private LayoutInflater mLayoutInflater;
    private List<QuestionRecord> mRecordList;
    private String userId = CommonUtil.getUserId() + "";

    public MineQuestionsRecordAdapter(Context context, List<QuestionRecord> questionRecords) {
        this.context = context;
        this.mRecordList = questionRecords;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecordViewHolder(context, mLayoutInflater.inflate(R.layout.item_questions_record, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecordViewHolder viewHolder = (RecordViewHolder) holder;
        viewHolder.setData(mRecordList.get(position));
    }

    @Override
    public int getItemCount() {
        return mRecordList == null ? 0 : mRecordList.size();
    }

    public class RecordViewHolder extends ViewHolderModel<QuestionRecord> {
        @BindView(R.id.tv_book_name)
        TextView mTvBookName;
        @BindView(R.id.tv_test_name)
        TextView mTvTestName;
        @BindView(R.id.tv_record_time)
        TextView mTvRecordTime;
        @BindView(R.id.tv_correct_percent)
        TextView mTvCorrectPercent;
        @BindView(R.id.tv_score)
        TextView mTvScore;
        @BindView(R.id.ll_score)
        LinearLayout mLlScore;
        @BindView(R.id.ll_item_record)
        LinearLayout mLlItemRecord;

        Context context;

        public RecordViewHolder(Context context, View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.context = context;
        }

        @Override
        public boolean setLayoutVisibility(QuestionRecord questionRecord) {
            return questionRecord != null;
        }

        @Override
        public void bindData(final QuestionRecord questionRecord) {
            mTvBookName.setText(questionRecord.getBookName());
            mTvTestName.setText(questionRecord.getQuestion_name());
            mTvRecordTime.setText(DateUtils.timeStamp2Date(questionRecord.getTimestamp(),null));
            mTvCorrectPercent.setText("正确率：" + questionRecord.getCorrect_percent()*100 + "%");
            mTvScore.setText("得分：" + questionRecord.getScore() + " 分");
            mLlItemRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getTestRecord(questionRecord);
                }
            });
        }

        /**
         * 进入下个页面前，获取答题记录详细信息
         * @param questionRecord
         */
        public void getTestRecord(final QuestionRecord questionRecord){
            ((BaseActivity)context).showLoadingDialog();
            OkGo.get(Urls.GetTestRecord)
                    .tag(this)
                    .params("id",questionRecord.getId())
                    .execute(new JsonCallback2<TestRecord>(TestRecord.class) {
                        @Override
                        public void onSuccess(TestRecord testRecord, Call call, Response response) {
                            if (isNull(testRecord)){
                                ToastCompat.makeText(context,"获取测试题目为空", Toast.LENGTH_SHORT).show();
                                ((BaseActivity)context).dismissLoadingDialig();
                                return;
                            }
                            ((BaseActivity)context).dismissLoadingDialig();
                            if(AppContext.getInstance().getChapterTestBean()!=null){
                                AppContext.getInstance().setChapterTestBean(null);
                            }
                            ChapterTestBean chapterTestBean = new ChapterTestBean();

                            chapterTestBean.setId((int)questionRecord.getId());
                            chapterTestBean.setChapterId((int)questionRecord.getChapterId());
                            chapterTestBean.setSubmit(true);
                            chapterTestBean.setPassingRate(questionRecord.getCorrect_percent());
                            chapterTestBean.setTestQuestionList(testRecord.record2Question());

                            AppContext.getInstance().setChapterTestBean(chapterTestBean);
                            Intent intent = new Intent(context, TestResultActivity.class);
                            context.startActivity(intent);
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            ((BaseActivity)context).dismissLoadingDialig();
                            ToastCompat.makeText(context,"获取测试题目失败", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
