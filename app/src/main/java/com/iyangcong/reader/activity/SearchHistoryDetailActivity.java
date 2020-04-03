package com.iyangcong.reader.activity;


import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.Word;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.NotNullUtils;
import com.iyangcong.reader.utils.StringUtils;
import com.iyangcong.reader.utils.Urls;
import com.iyangcong.reader.utils.query.QueryUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchHistoryDetailActivity extends SwipeBackActivity {
    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.ll_sentence)
    LinearLayout llSentence;
    @BindView(R.id.img_trumpet)
    ImageView imTrumpet;
    @BindView(R.id.img_trumpet1)
    ImageView imTrumpet1;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.tv_word)
    TextView tvWord;
    @BindView(R.id.ll_varient)
    LinearLayout llVarient;
    @BindView(R.id.gradeLevel)
    TextView tvGradeLevel;
    @BindView(R.id.uk_phonetic)
    TextView tvUKPhibetic;
    @BindView(R.id.us_phonetic)
    TextView tvUSPhibetic;
    @BindView(R.id.explains)
    TextView tvExplains;
    @BindView(R.id.tv_sentence)
    TextView tvSentence;
    @BindView(R.id.tv_bookName)
    TextView tvBookName;
    @BindView(R.id.tv_variant)
    TextView tvVarient;
    @BindView(R.id.ll_detail)
    LinearLayout llDetail;

    private String word;
    private MediaPlayer mediaPlayer;

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
        setContentView(R.layout.activity_history_detail);
        ButterKnife.bind(this);
        setMainHeadView();
        initView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        word = getIntent().getStringExtra("word");
        QueryUtils.queryfromYoudaoOnLineNewMethod(StringUtils.deleSymbol(word), youdaoHandler,true);
    }

    @Override
    protected void initView() {

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
                        llDetail.setVisibility(View.VISIBLE);
                        if (obj.getWord() != null && obj.getWord().size()>0){
                            tvWord.setText(obj.getWord().get(0));
                        }
                        if(obj!=null&&obj.getWord()!=null&&!obj.getWord().isEmpty()) {
                            String levelNums = QueryUtils.getLevelNumsFromWord(obj.getWord().get(0));
                            if (QueryUtils.getLevelsStrFromLevelNums(levelNums,2)!=null){
                                tvGradeLevel.setText(QueryUtils.getLevelsStrFromLevelNums(levelNums,2));
                            }
                        }
                        if (!TextUtils.isEmpty(obj.getUs_phonetic())){
                            imTrumpet1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    annouceWord("us/"+obj.getWord().get(0));
                                }
                            });
                            tvUSPhibetic.setText("美 [" + obj.getUs_phonetic() + "]" );
                        }
                        if (!TextUtils.isEmpty(obj.getUk_phonetic())){

                            imTrumpet.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    annouceWord("uk/"+obj.getWord().get(0));
                                }
                            });
                            tvUKPhibetic.setText("英 [" + obj.getUk_phonetic() + "]" );
                        }

                        if (obj.getBooks().get(0).getBookSource()!=null){
                            if (obj.getBooks().get(0).getBookSource() == null||obj.getBooks().get(0).getBookSource().equals("")||obj.getBooks().get(0).getBookSource().equals("null")
                                    ||obj.getBooks().get(0).getBookName() == null||obj.getBooks().get(0).getBookName().equals("")){
                                llSentence.setVisibility(View.GONE);
                            }else {
                                Log.e(TAG, obj.getBooks().get(0).getBookSource() );
                                llSentence.setVisibility(View.VISIBLE);
                                tvSentence.setText(obj.getBooks().get(0).getBookSource());
                                tvBookName.setText(obj.getBooks().get(0).getBookName());

                            }
                            if (obj.getVarients()==null){
                                llVarient.setVisibility(View.GONE);
                            }else {
                                llVarient.setVisibility(View.VISIBLE);
                                tvVarient.setText(obj.getVarients().toString().substring(1,obj.getVarients().toString().length()-1));
                            }
                            tvBookName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(SearchHistoryDetailActivity.this, BookMarketBookDetailsActivity.class);
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
                    }

            }
        }
    };

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText("洋葱词典");
        btnBack.setImageResource(R.drawable.btn_back);

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

}
