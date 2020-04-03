package com.iyangcong.reader.activity;


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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.iyangcong.reader.adapter.LocalWordAdapter;
import com.iyangcong.reader.bean.GuessYourLove;
import com.iyangcong.reader.bean.SearchHistory;
import com.iyangcong.reader.bean.Word;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.ClearEditText;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.NotNullUtils;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.StringUtils;
import com.iyangcong.reader.utils.Urls;
import com.iyangcong.reader.utils.query.QueryUtils;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class MineDictionaryActivity extends SwipeBackActivity implements ClearEditText.ClearListener {
    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.tv_word)
    TextView tvWord;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.gradeLevel)
    TextView tvGradeLevel;
    @BindView(R.id.us_phonetic)
    TextView tvUSPhonetic;
    @BindView(R.id.uk_phonetic)
    TextView tvUKPhonetic;
    @BindView(R.id.tv_sentence)
    TextView tvSentence;
    @BindView(R.id.tv_bookName)
    TextView tvBookName;
    @BindView(R.id.tv_variant)
    TextView tvVariant;
    @BindView(R.id.explains)
    TextView tvExplains;
    @BindView(R.id.rl_us)
    RelativeLayout rlUs;
    @BindView(R.id.rl_uk)
    RelativeLayout rlUk;
    @BindView(R.id.ll_varient)
    LinearLayout llVarient;
    @BindView(R.id.ll_list)
    LinearLayout llList;
    @BindView(R.id.ll_sentence)
    LinearLayout llSentence;
    @BindView(R.id.ll_search_word)
    LinearLayout llSearchWord;
    @BindView(R.id.llword_detail)
    LinearLayout tlWordDetail;
    @BindView(R.id.local_wordlist)
    ListView localWordList;
    @BindView(R.id.bt_query_word)
    ImageView searchWordList;
    @BindView(R.id.et_inputWord)
    ClearEditText etInputWord;
    @BindView(R.id.gv_recommended_book)
    GridView gvRecommendedBook;
    private List<GuessYourLove> recommendedBooklist;
    private RecommendedBookAdapter recommendedbookAdapter;
    private List<Word> searchList;
    private MediaPlayer mediaPlayer;
    private SharedPreferenceUtil sharedPreferenceUtil;


    @OnClick({R.id.btnBack,R.id.bt_query_word,R.id.btnFunction})
    void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.bt_query_word:
                showDetail(etInputWord.getText().toString());
                break;
            case R.id.btnFunction:
                Intent intent = new Intent(this, SearchHistoryActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void annouceWord(String word){
        Uri uri = Uri.parse(Urls.SHANBEI_ANNOUNCE_URL+word+".mp3");
        if(uri!=null) {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(this, uri);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    Handler youdaoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    final Word obj = (Word) msg.obj;
                    if (obj != null ){
                        tlWordDetail.setVisibility(View.VISIBLE);

                        if (obj.getWord() != null && obj.getWord().size()>0){
                            tvWord.setText(obj.getWord().get(0));
                        }
                        if(obj!=null&&obj.getWord()!=null&&!obj.getWord().isEmpty()) {
                            String levelNums = QueryUtils.getLevelNumsFromWord(obj.getWord().get(0));
                            if (QueryUtils.getLevelsStrFromLevelNums(levelNums,2)!=null){
                                tvGradeLevel.setVisibility(View.VISIBLE);
                                tvGradeLevel.setText(QueryUtils.getLevelsStrFromLevelNums(levelNums,2));
                            }else {
                                tvGradeLevel.setVisibility(View.GONE);
                            }
                        }
                        if (!TextUtils.isEmpty(obj.getUs_phonetic())){
                            rlUs.setVisibility(View.VISIBLE);
                            rlUs.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    annouceWord("us/"+obj.getWord().get(0));
                                }
                            });
                            tvUSPhonetic.setText("美 [" + obj.getUs_phonetic() + "]" );
                        }else{
                            rlUs.setVisibility(View.GONE);
                        }
                        if (!TextUtils.isEmpty(obj.getUk_phonetic())){
                            rlUk.setVisibility(View.VISIBLE);
                            rlUk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    annouceWord("uk/"+obj.getWord().get(0));
                                }
                            });
                            tvUKPhonetic.setText("英 [" + obj.getUk_phonetic() + "]" );
                        }else{
                            rlUk.setVisibility(View.GONE);
                        }

                        if (obj.getBooks().get(0).getBookSource()!=null){
                            if (obj.getBooks().get(0).getBookSource() == null||obj.getBooks().get(0).getBookSource().equals("")||obj.getBooks().get(0).getBookSource().equals("null")
                            ||obj.getBooks().get(0).getBookName() == null||obj.getBooks().get(0).getBookName().equals("")){
                                llSentence.setVisibility(View.GONE);
                            }else {
                                Log.e(TAG, obj.getBooks().get(0).getBookSource());
                                llSentence.setVisibility(View.VISIBLE);
                                tvSentence.setText(obj.getBooks().get(0).getBookSource());
                                tvBookName.setText(obj.getBooks().get(0).getBookName());

                            }
                            if (obj.getVarients()== null){
                                llVarient.setVisibility(View.GONE);
                            }else {
                                llVarient.setVisibility(View.VISIBLE);
                                tvVariant.setText(obj.getVarients().toString().substring(1,obj.getVarients().toString().length()-1));
                            }
                            Log.e(TAG, obj.getBooks().get(0).getBookId().toString());
                            tvBookName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MineDictionaryActivity.this, BookMarketBookDetailsActivity.class);
                                    intent.putExtra(Constants.BOOK_ID,Integer.parseInt(obj.getBooks().get(0).getBookId()));
                                    intent.putExtra(Constants.BOOK_NAME,obj.getBooks().get(0).getBookName());
                                    startActivity(intent);
                                }
                            });
                        }

                        String explain = NotNullUtils.isNull(obj.getExplains())?
                                (obj.getWebExplains()==null||obj.getWebExplains().equals("")?
                                        (obj.getTranslation()==null||"".equals(obj.getTranslation().trim()))?
                                                " 暂无释义":obj.getTranslation():
                                        "网络释义："+ TextUtils.join(",",obj.getWebExplains().values().toArray())+"\n"):
                                (StringUtils.listToString(obj.getExplains()) == null || StringUtils.listToString(obj.getExplains()).equals("")?"":
                                        /*"释：" + */StringUtils.listToString(obj.getExplains()) + "\n");
                        tvExplains.setText(explain);
                        Gson gs = new Gson();
                        List<String> historyList = new ArrayList<>();
                        historyList = sharedPreferenceUtil.getArray(SharedPreferenceUtil.SEARCHHISTORY);
                        SearchHistory searchHistory = new SearchHistory();
                        searchHistory.setWord(obj.getWord().get(0));
                        searchHistory.setExplain(explain);
                        searchHistory.setPhonetic(obj.getUs_phonetic());
                        historyList.add(gs.toJson(searchHistory));
                        sharedPreferenceUtil.putArray(SharedPreferenceUtil.SEARCHHISTORY,historyList);
                    }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_dictionary);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setMainHeadView();
        initView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
        recommendedBooklist = new ArrayList<GuessYourLove>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void initView() {
        llSearchWord.setFocusable(true);
        llSearchWord.setFocusableInTouchMode(true);
        etInputWord.setFocusable(true);
        etInputWord.setFocusableInTouchMode(true);
        etInputWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                llList.setVisibility(View.VISIBLE);
                final List<Word> myList = new ArrayList<>();
                if (etInputWord.getText().toString() != null && s.length() > 0) {
                    llList.setVisibility(View.VISIBLE);
                    myList.clear();
                    searchList = QueryUtils.queryFromLocalList(etInputWord.getText().toString());
                    if (searchList!=null){
                        for (Word word:searchList){
                            if (word.getWord().get(0).contains(etInputWord.getText().toString())){
                                myList.add(word);
                            }
                        }
                    }
                    LocalWordAdapter localWordAdapter = new LocalWordAdapter(MineDictionaryActivity.this,myList);
                    localWordList.setAdapter(localWordAdapter);
                    localWordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Word word = myList.get(position);
                            etInputWord.setText(word.getWord().get(0));
                            showDetail(word.getWord().get(0));
                        }
                    });

                }else{
                    llList.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        localWordList.setDivider(new ColorDrawable(Color.parseColor("#E5E5E5")));
        localWordList.setDividerHeight(1);
        etInputWord.setClearListener(this);
        getDatasFromNetwork();
        recommendedbookAdapter = new RecommendedBookAdapter();
        gvRecommendedBook.setAdapter(recommendedbookAdapter);
        gvRecommendedBook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(context, BookMarketBookDetailsActivity.class);
                intent.putExtra("bookId", (int) recommendedBooklist.get(position).getBookId());
                intent.putExtra("bookName", recommendedBooklist.get(position).getBookName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 拦截ACTION_DOWN事件，判断是否需要隐藏输入法
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isShouldHideInput(view, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }

        // 交由DecorView去做Touch事件的分发
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }

        // Activity内没有View对这个Touch事件做处理，那么有Activity来处理
        return onTouchEvent(ev);
    }

    private boolean isShouldHideInput(View view, MotionEvent ev) {
        // 1、判断是否是EditText，如果不是，直接返回false
        if (view != null && (view instanceof EditText)) {
            int[] location = {0, 0};
            view.getLocationOnScreen(location);
            int left = location[0];
            int top = location[1];

            // 2、判断Touch的点是否在EditText外
            if (ev.getX() < left || (ev.getX() > left + view.getWidth())
                    || ev.getY() < top || (ev.getY() > top + view.getHeight())) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private void showDetail(String word){
        if (!TextUtils.isEmpty(word)){
            ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
               (MineDictionaryActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            llList.setVisibility(View.GONE);
            QueryUtils.queryfromYoudaoOnLineNewMethod(StringUtils.deleSymbol(word), youdaoHandler,true);

        }
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText("洋葱词典");
        btnBack.setImageResource(R.drawable.btn_back);
        btnFunction.setImageResource(R.drawable.search_history);
    }
/*    private void getDatasFromNetwork() {
        OkGo.get(Urls.BookMarketBookDetailURL)
                .tag(this)
                .params("bookId", 15)
                .execute(new JsonCallback<IycResponse<MarketBookDetails>>(this) {
                    @Override
                    public void onSuccess(IycResponse<MarketBookDetails> iycResponse, Call call, Response response) {

                        for (MarketBookDetailsSameBooks marketBookDetailsSameBooks:iycResponse.getData().getSameoffer()){
                            recommendedBooklist.add(marketBookDetailsSameBooks);
                        }
                        recommendedbookAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onError(Call call, Response response, Exception e) {

                    }
                });
    }*/

    private void getDatasFromNetwork() {
        OkGo.get(Urls.GetOutLessBooks)
                .tag(this)
                .params("userid", CommonUtil.getUserId())
                .execute(new JsonCallback<IycResponse <List<GuessYourLove>>>(this) {
                    @Override
                    public void onSuccess(IycResponse <List<GuessYourLove>> iycResponse, Call call, Response response) {

                        for (GuessYourLove marketBookDetailsSameBooks:iycResponse.getData()){
                            recommendedBooklist.add(marketBookDetailsSameBooks);
                        }
                        recommendedbookAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onError(Call call, Response response, Exception e) {

                    }
                });
    }

    @Override
    public void clear() {
        etInputWord.setText("");
    }

    class RecommendedBookAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            int maxSize = 0;
            if (recommendedBooklist != null) {
                if (recommendedBooklist.size() > 6) {
                    maxSize = 6;
                } else {
                    maxSize = recommendedBooklist.size();
                }
            }
            return recommendedBooklist == null ? 0 : maxSize;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(MineDictionaryActivity.this, R.layout.item_recommended_book, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            GlideImageLoader.displayBookCover(MineDictionaryActivity.this, holder.ivShelfBookImage, recommendedBooklist.get(position).getBookImgUrl());
            holder.tvBookName.setText(recommendedBooklist.get(position).getBookName());
            holder.tvBookName.setGravity(Gravity.CENTER_HORIZONTAL);
            return convertView;
        }

        public class ViewHolder {

            @BindView(R.id.iv_shelf_book_image)
            ImageView ivShelfBookImage;
            @BindView(R.id.tv_book_name)
            TextView tvBookName;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
