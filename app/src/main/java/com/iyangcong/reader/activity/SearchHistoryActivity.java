package com.iyangcong.reader.activity;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.ContentDetailAdapter;
import com.iyangcong.reader.adapter.LocalWordAdapter;
import com.iyangcong.reader.bean.BookNoteBean;
import com.iyangcong.reader.bean.MarketBookDetails;
import com.iyangcong.reader.bean.MarketBookDetailsSameBooks;
import com.iyangcong.reader.bean.Person;
import com.iyangcong.reader.bean.SearchHistory;
import com.iyangcong.reader.bean.Word;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.ClearEditText;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.NotNullUtils;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.StringUtils;
import com.iyangcong.reader.utils.Urls;
import com.iyangcong.reader.utils.query.QueryUtils;
import com.lzy.okgo.OkGo;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class SearchHistoryActivity extends SwipeBackActivity {
    @BindView(R.id.btnBack)
    ImageView btnBack;
    List<SearchHistory> searchHistoryList = new ArrayList<SearchHistory>();

    private RecyclerView recyclerView;
    private SearchHistoryAdapter searchHistoryAdapter;
    private SharedPreferenceUtil sharePreferenceUtil;

    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;

    @OnClick({R.id.btnBack})
    void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history);
        ButterKnife.bind(this);

        sharePreferenceUtil = SharedPreferenceUtil.getInstance();
        Gson gs = new Gson();
        List<String> list = sharePreferenceUtil.getArray(SharedPreferenceUtil.SEARCHHISTORY);

        for (String text:list){
            SearchHistory jsonObject = gs.fromJson(text, SearchHistory.class);//把JSON字符串转为对象
            searchHistoryList.add(0,jsonObject);
        }

        searchHistoryAdapter = new SearchHistoryAdapter(this,searchHistoryList);
        recyclerView = (RecyclerView)findViewById(R.id.rv_history_list);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(searchHistoryAdapter);
        setMainHeadView();
        initView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void initView() {

    }


    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText("搜索历史");
        btnBack.setImageResource(R.drawable.btn_back);

    }

    public class SearchHistoryAdapter extends RecyclerView.Adapter {

        private Context mContext;
        private ContentDetailAdapter commentDetailAdapter;
        private LayoutInflater mLayoutInflater;
        private List<SearchHistory> list;

        public SearchHistoryAdapter(Context mContext,List<SearchHistory> list) {
            this.mContext = mContext;
            this.list = list;
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(mContext, mLayoutInflater.from(mContext).inflate(R.layout.search_history_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.setData(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final Context mContext;
            @BindView(R.id.tv_word)
            TextView tvWord;
            @BindView(R.id.word_explain)
            TextView tvExplain;
            @BindView(R.id.search_examine)
            ImageView ivExamine ;

            ViewHolder(Context mContext, View itemView) {
                super(itemView);
                this.mContext = mContext;
                ButterKnife.bind(this, itemView);
            }

            void setData(SearchHistory searchHistory) {
                tvWord.setText(searchHistory.getWord());
                tvExplain.setText(searchHistory.getExplain());
                ivExamine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, SearchHistoryDetailActivity.class);
                        intent.putExtra("word", searchHistory.getWord());
                        startActivity(intent);
                    }
                });

            }
        }
    }

}
