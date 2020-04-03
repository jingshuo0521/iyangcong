package com.iyangcong.reader.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.ChapterTestActivity;
import com.iyangcong.reader.app.AppContext;
import com.iyangcong.reader.base.BaseFragment;
import com.iyangcong.reader.bean.ChapterTestBean;
import com.iyangcong.reader.bean.ChapterTestItem;
import com.iyangcong.reader.bean.TestQuestion;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.iyangcong.reader.utils.ViewHolderModelNew;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

public class TestFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    @BindView(R.id.lv_note)
    ListView mLvNote;
    @BindView(R.id.ll_no_content)
    LinearLayout mLlNoContent;
    TestItemAdapter mTestItemAdapter;
    List<ChapterTestItem> mChapterTestItems;
    SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance();

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        ButterKnife.bind(this, view);
        mChapterTestItems = new ArrayList<>();
        mTestItemAdapter = new TestItemAdapter(mContext,mChapterTestItems);
        mLvNote.setAdapter(mTestItemAdapter);
        mLvNote.setOnItemClickListener(this);
        getChapterTestList();
        return view;
    }

    @Override
    protected void initData() {

    }

    private void getChapterTestList(){
        OkGo.get(Urls.GetHasTestChapterId)
                .tag(this)
                .params("bookId",(int) sp.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0))
//                .params("bookId",SharedPreferenceUtil.getInstance().getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0)+"")
                .params("userId",SharedPreferenceUtil.getInstance().getLong(SharedPreferenceUtil.USER_ID, 0)+"")
                .execute(new JsonCallback<IycResponse<List<ChapterTestItem>>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<List<ChapterTestItem>> listIycResponse, Call call, Response response) {
                        if(listIycResponse == null || listIycResponse.data ==null || listIycResponse.data.size() == 0){
                            //  Toast.makeText(mContext,"获取测试列表为空",Toast.LENGTH_LONG).show();
                            mLlNoContent.setVisibility(View.VISIBLE);
                            mLvNote.setVisibility(View.GONE);
                            return;
                        }
                        if(mChapterTestItems!=null && mChapterTestItems.size()!=0){
                            mChapterTestItems.clear();
                        }
                        mLlNoContent.setVisibility(View.GONE);
                        mLvNote.setVisibility(View.VISIBLE);
                        mChapterTestItems.addAll(listIycResponse.getData());
                        mTestItemAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(mContext,"获取测试列表失败",Toast.LENGTH_LONG).show();
                        mLlNoContent.setVisibility(View.VISIBLE);
                        mLvNote.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        getQuestionsByChapterId(mTestItemAdapter.getItem(i));
    }

    /**
     * 跳转到测试页面前先请求测试题，请求成功再跳转
     * @param testItem
     */
    public void getQuestionsByChapterId(final ChapterTestItem testItem){
        showLoadingDialog();
        OkGo.get(Urls.GetQuestions)
                .tag(this)
                .params("bookId",(int) sp.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0))
//                .params("bookId",SharedPreferenceUtil.getInstance().getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0)+"")
                .params("userId",SharedPreferenceUtil.getInstance().getLong(SharedPreferenceUtil.USER_ID, 0)+"")
                .params("chapterId",testItem.getChapterId())
                .execute(new JsonCallback<IycResponse<List<TestQuestion>>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<List<TestQuestion>> listIycResponse, Call call, Response response) {
                        if(listIycResponse == null || listIycResponse.data ==null || listIycResponse.data.size() == 0){
                            Toast.makeText(mContext,"获取测试题目为空",Toast.LENGTH_LONG).show();
                            return;
                        }
                        dismissLoadingDialig();
                        if(AppContext.getInstance().getChapterTestBean()!=null){
                            AppContext.getInstance().setChapterTestBean(null);
                        }
                        ChapterTestBean chapterTestBean = new ChapterTestBean();
                        chapterTestBean.setChapterId(testItem.getChapterId());
                        chapterTestBean.setSubmit(testItem.getFinish() == 1);
                        chapterTestBean.setTestQuestionList(listIycResponse.getData());
                        AppContext.getInstance().setChapterTestBean(chapterTestBean);
                        Intent intent = new Intent(mContext, ChapterTestActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dismissLoadingDialig();
                        Toast.makeText(mContext,"获取测试题目失败",Toast.LENGTH_LONG).show();
                    }
                });
    }

    public final class TestItemAdapter extends BaseAdapter {
        Context mContext;
        List<ChapterTestItem> mList;

        public TestItemAdapter(Context context, List<ChapterTestItem> list) {
            this.mContext = context;
            this.mList = list;
        }

        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public ChapterTestItem getItem(int position) {
            if (mList == null || mList.size() == 0) return null;
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            TestItemViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.chapter_test_item, viewGroup, false);
                holder = new TestItemViewHolder(mContext, convertView);
                holder.setData(mList.get(position), position);
                convertView.setTag(holder);
            } else {
                holder = (TestItemViewHolder) convertView.getTag();
                holder.setData(mList.get(position), position);
            }
            return convertView;
        }

        public class TestItemViewHolder extends ViewHolderModelNew<ChapterTestItem> {
            @BindView(R.id.iv_test_finished)
            ImageView mIvTestFinished;
            @BindView(R.id.tv_test_name)
            TextView mTvTestName;

            public TestItemViewHolder(Context context, View itemView) {
                super(context, itemView);
                ButterKnife.bind(this, itemView);
            }

            @Override
            public boolean setLayoutVisibility(ChapterTestItem chapterTestItem) {
                return chapterTestItem!=null;
            }

            @Override
            public void bindData(ChapterTestItem chapterTestItem, int position) {
                mTvTestName.setText(chapterTestItem.getName());
                if(chapterTestItem.getFinish()==1){
                    mIvTestFinished.setVisibility(View.VISIBLE);
                }else {
                    mIvTestFinished.setVisibility(View.INVISIBLE);
                }

//                mTvIsFinished.setText(chapterTestItem.getFinish()==1?"已完成":"未完成");
            }
        }
    }
}
