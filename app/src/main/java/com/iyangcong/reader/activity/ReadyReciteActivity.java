package com.iyangcong.reader.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.app.AppContext;
import com.iyangcong.reader.base.BaseActivity;
import com.iyangcong.reader.bean.NewWord;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.WordDao;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.ui.NewWordExplainView;
import com.iyangcong.reader.ui.QQListView2;
import com.iyangcong.reader.ui.ReciteWordBottomButton;
import com.iyangcong.reader.ui.ReciteWordResultToastView;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.NewWordUtils;
import com.iyangcong.reader.utils.StringUtils;
import com.iyangcong.reader.utils.UIHelper;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

public class ReadyReciteActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    private AppContext appContext;
    private ImageView default_back, rightview;
    private TextView title_content;
    //private Button know_thinkup,dontknow_dontthinkup;
    private ReciteWordBottomButton bottomButton;
    private Button next_group, stop_here;
    private int status = 0;
    private LinearLayout shape_view, first_review_view, second_dontknow_word_view, second_know_word_view, result_toast_view;
    private ImageView img_first_myfavorite, img_second_know_myfavorite, img_second_dontknow_myfavorite;
    private RelativeLayout recitewording_view, reciteworded_view, reciteword_detail_view;
    private TextView second_know_word, second_know_phonetic, second_word_from_bookname, second_word_from_content_know;

    private TextView word_from_content_dontknow, second_word_from_bookname_dontknow, second_dontkonw_word,word_meaning_dontknow,second_word_meaning_know;
    private TextView first_review_view_word;
    private NewWordExplainView newwordexplain_item;
    private QQListView2 mListView;
    private List<NewWord> datas;
    private boolean ReciteFinish;
    private int recitePosition = 0;
    private final int MAX_GROUP_COUNT = 10;
    private int group_count;
    private boolean isAllReviewed = false;
    private List<Boolean> groupResult = new ArrayList<Boolean>();
    private boolean alreadyCollect = false;
    private String reciteType = "";
    private ReciteWordResultToastView resultview;
    private float rightResult = 0;
    private float wrongResult = 0;
    private boolean isShowDetail = false;
    private int allWordCount = 0;
    List<NewWord> allwords;
    private WordDao wordDao;
    private NewWord newWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readyrecite);
        ButterKnife.bind(this);
        setMainHeadView();
        Intent intent = getIntent();
        if (intent != null) {
            reciteType = intent.getStringExtra("reciteType");
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initView() {
        btnBack.setOnClickListener(this);
        bottomButton = (ReciteWordBottomButton) findViewById(R.id.bottomButton);
        bottomButton.setOnClickListener(this);

        recitewording_view = (RelativeLayout) findViewById(R.id.recitewording_view);
        reciteworded_view = (RelativeLayout) findViewById(R.id.reciteworded_view);
        shape_view = (LinearLayout) findViewById(R.id.shape_view);
        first_review_view = (LinearLayout) findViewById(R.id.first_review_view);
        first_review_view.setVisibility(View.VISIBLE);
        img_first_myfavorite = (ImageView) findViewById(R.id.img_first_myfavorite);
        img_first_myfavorite.setOnClickListener(this);
        second_dontknow_word_view = (LinearLayout) findViewById(R.id.second_dontknow_word_view);
        second_dontknow_word_view.setVisibility(View.INVISIBLE);
        second_word_from_content_know = (TextView) findViewById(R.id.second_word_from_content_know);
        second_word_meaning_know = (TextView)findViewById(R.id.second_word_meaning_know);
        second_know_word_view = (LinearLayout) findViewById(R.id.second_know_word_view);
        second_know_word = (TextView) findViewById(R.id.second_know_word);
        second_word_from_bookname = (TextView) findViewById(R.id.second_word_from_bookname);
        second_know_phonetic = (TextView) findViewById(R.id.second_know_phonetic);
        second_know_word_view.setVisibility(View.INVISIBLE);
        newwordexplain_item = (NewWordExplainView) findViewById(R.id.newwordexplain_item);
        img_second_know_myfavorite = (ImageView) findViewById(R.id.img_second_know_myfavorite);
        img_second_know_myfavorite.setOnClickListener(this);
        //---------------dontknow
        img_second_dontknow_myfavorite = (ImageView) findViewById(R.id.img_second_dontknow_myfavorite);
        img_second_dontknow_myfavorite.setOnClickListener(this);
        second_dontkonw_word = (TextView) findViewById(R.id.second_dontkonw_word);
        word_from_content_dontknow = (TextView) findViewById(R.id.word_from_content_dontknow);
        word_meaning_dontknow = (TextView)findViewById(R.id.word_meaning_dontknow);
        second_word_from_bookname_dontknow = (TextView) findViewById(R.id.second_word_from_bookname_dontknow);


        //result view
        result_toast_view = (LinearLayout) findViewById(R.id.result_toast_view);
        //detail_view
        reciteword_detail_view = (RelativeLayout) findViewById(R.id.reciteword_detail_view);
        //状态一时的view初始化
        first_review_view_word = (TextView) findViewById(R.id.first_review_view_word);
        next_group = (Button) findViewById(R.id.next_group);
        next_group.setOnClickListener(this);
        stop_here = (Button) findViewById(R.id.stop_here);
        stop_here.setOnClickListener(this);
        wordDao = new WordDao(DatabaseHelper.getHelper(this));
        initData();
        switchView(status, -1);
        mListView = (QQListView2) findViewById(R.id.LV_reciteresult);
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText("待复习");
        btnBack.setImageResource(R.drawable.btn_back);
        btnBack.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    @Override
    protected void onDestroy() {
        showviewHandler = null;
        super.onDestroy();
    }

    private void initData() {
        if (datas != null) {
            datas.clear();
        } else {
            datas = new ArrayList<NewWord>();
            if(!reciteType.equals("others")){
                allwords = wordDao.getwords(reciteType, false, true, false);
            }else {
                allwords = wordDao.getOtherWordsList(false,true ,false );
            }
            Log.i("hahahahahah  allwords",allwords.toString());
            allWordCount = allwords.size();
        }
        if (MAX_GROUP_COUNT > allWordCount) {
            datas.clear();
            for (int i = 0; i < allWordCount; i++) {
                datas.add(allwords.get(i));
                Logger.e("gft allwords.get(i) = " +allwords.get(i));
                groupResult.add(false);
            }
            isAllReviewed = true;
        } else {
            datas.clear();
            groupResult.clear();
            for (int i = 0; i < MAX_GROUP_COUNT; i++) {
                if ((group_count * MAX_GROUP_COUNT + i) < allwords.size()) {
                    datas.add(allwords.get(group_count * MAX_GROUP_COUNT + i));
                    groupResult.add(false);
                } else {
                    isAllReviewed = true;
                    break;
                }
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.know_thinkup:

                String thinkupText = bottomButton.getThinkupText();
                if ("认识".equals(thinkupText)) {
                    newWord = wordDao.updateAlreadyKnow(datas.get(recitePosition), true);
                    String result = CommonUtil.format201906304(newWord);
                    uploadAllWordsList(result);
                    rightResult++;
                    status = 1;//认识时状态为1
                    groupResult.set(recitePosition, true);
                    switchView(status, -1);
                } else if ("需要复习".equals(thinkupText)) {
                    status = 1;
                    newWord = wordDao.updateAlreadyKnow(datas.get(recitePosition), false);
                    String result = CommonUtil.format201906304(newWord);
                    uploadAllWordsList(result);
                    switchView(status, -1);
                }
                break;
            case R.id.dontknow_dontthinkup:
                String dontthinkupText = bottomButton.getDontThinkupText();
                if ("下一个".equals(dontthinkupText)) {
                    //wordDAO.updateReadyRecite(datas.get(recitePosition),true);//设置为待复习
                    recitePosition++;
                    status = 0;
                    alreadyCollect = false;
                    if (recitePosition <= MAX_GROUP_COUNT) {
                        switchView(status, -1);
                    } else {
                        //recitePosition=0;
                        status = 3;//一组练习完了显示列表
                        switchView(status, -1);
                    }
                    switchView(status, -1);
                } else if ("查看结果".equals(dontthinkupText)) {
                    //recitePosition=0;
                    status = 3;//一组练习完了显示列表
                    switchView(status, -1);
                } else if ("不认识".equals(dontthinkupText)) {
                    wrongResult++;
                    groupResult.set(recitePosition, false);
                    status = 2;//不认识状态为2
                    switchView(status, -1);
                } else if ("我已掌握".equals(dontthinkupText)) {
                    newWord = wordDao.updateAlreadyKnow(datas.get(recitePosition), true);
                    String result = CommonUtil.format201906304(newWord);
                    uploadAllWordsList(result);
                    status = 1;
                    switchView(status, -1);
                }
                break;
            case R.id.next_group:
                alreadyCollect = false;
                if (isAllReviewed) {
                    ToastCompat.makeText(ReadyReciteActivity.this, "都已掌握啦！", Toast.LENGTH_SHORT).show();
                } else {
                    group_count++;
                    recitePosition = 0;
                    initData();
                    status = 0;
                    if (datas.size() != 0) {
                        switchView(status, -1);
                    }
                }
                break;
            case R.id.stop_here:
                showviewHandler.sendEmptyMessage(0);
                break;
            case R.id.img_first_myfavorite:
                alreadyCollect = !alreadyCollect;
                dealwithFavorite(alreadyCollect);
                NewWord word = datas.get(recitePosition);
                if (alreadyCollect) {
                    img_first_myfavorite.setImageResource(R.drawable.im_favorite);
                    ToastCompat.makeText(ReadyReciteActivity.this, "已添加到我的收藏", Toast.LENGTH_SHORT).show();
                    word.setIFfavorite(1);
                } else {
                    img_first_myfavorite.setImageResource(R.drawable.im_notfavorite);
                    ToastCompat.makeText(ReadyReciteActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                    word.setIFfavorite(0);
                }

                break;
            case R.id.img_second_dontknow_myfavorite:
                alreadyCollect = !alreadyCollect;
                dealwithFavorite(alreadyCollect);
                word = datas.get(recitePosition);
                if (alreadyCollect) {
                    img_second_dontknow_myfavorite.setImageResource(R.drawable.im_favorite);
                    ToastCompat.makeText(ReadyReciteActivity.this, "已添加到我的收藏", Toast.LENGTH_SHORT).show();
                    word.setIFfavorite(1);
                } else {
                    img_second_dontknow_myfavorite.setImageResource(R.drawable.im_notfavorite);
                    ToastCompat.makeText(ReadyReciteActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                    word.setIFfavorite(0);
                }
                break;
            case R.id.img_second_know_myfavorite:
                alreadyCollect = !alreadyCollect;
                dealwithFavorite(alreadyCollect);
                word = datas.get(recitePosition);
                if (alreadyCollect) {
                    img_second_know_myfavorite.setImageResource(R.drawable.im_favorite);
                    ToastCompat.makeText(ReadyReciteActivity.this, "已添加到我的收藏", Toast.LENGTH_SHORT).show();
                    word.setIFfavorite(1);
                } else {
                    img_second_know_myfavorite.setImageResource(R.drawable.im_notfavorite);
                    ToastCompat.makeText(ReadyReciteActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                    word.setIFfavorite(0);
                }
                break;
            case R.id.btnBack:
                if (isShowDetail) {
                    switchView(3, -1);
                    isShowDetail = false;
                } else {
                    this.finish();
                }
                break;
            default:
                break;

        }
    }

    private void uploadAllWordsList(String str){
        if(isNull(context,str,"")){
            com.orhanobut.logger.Logger.e("wzp str = " + str);
            return;
        }
        OkGo.post(Urls.AddNewWord)
                .params("dataJsonObjectString",str)
                .execute(new JsonCallback<IycResponse<String>>(context) {
                    @Override
                    public void onSuccess(IycResponse<String> stringIycResponse, Call call, Response response) {
//                        ToastCompat.makeText(context,"上传单词成功",Toast.LENGTH_SHORT).show();
                        wordDao.updateUpload(newWord);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastCompat.makeText(context,"上传单词失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void ToastResult() {

        resultview = new ReciteWordResultToastView(ReadyReciteActivity.this);
        float allResult = rightResult + wrongResult;
        float level1 = 8.0f / 10.0f;
        float level2 = 6.0f / 10.0f;


        FrameLayout fm = (FrameLayout) LayoutInflater.from(ReadyReciteActivity.this).inflate(R.layout.reciteword_result_toastview, null);
        ImageView result_imageview = (ImageView) fm.findViewById(R.id.result_imageview);
        TextView toast_textview1 = (TextView) fm.findViewById(R.id.toast_textview1);
        TextView toast_textview2 = (TextView) fm.findViewById(R.id.toast_textview2);
        float myResult = rightResult / allResult;
        toast_textview1.setText((int) allResult + "个认识" + (int) rightResult + "个，");
        if (myResult >= level1) {
            result_imageview.setBackgroundResource(R.drawable.reciteword_best);
            toast_textview2.setText("真了不起！");
        } else if (myResult < level1 && myResult >= level2) {
            result_imageview.setBackgroundResource(R.drawable.reciteword_better);
            toast_textview2.setText("还可以哦！");
        } else if (myResult < level2) {
            result_imageview.setBackgroundResource(R.drawable.reciteword_normal);
            toast_textview2.setText("加油哦！");
        }

        resultview.ToastView(getWindow().getDecorView(), fm);

        resultview.setmOnFolderClosedListener(new ReciteWordResultToastView.OnFolderClosedListener() {

            @Override
            public void onOpened() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onClosed() {
                // TODO Auto-generated method stub
                ReadyReciteActivity.this.finish();
            }
        });
        Message msg = new Message();
        msg.what = 1;
        showviewHandler.sendMessageDelayed(msg, 2000);

    }


    Handler showviewHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    ToastResult();
                    break;

                case 1:
                    resultview.dismiss();
                    ReadyReciteActivity.this.finish();
                    break;

                default:
                    break;
            }
        }

    };

    private void dealwithFavorite(boolean value) {
        newWord = wordDao.updateFavorite(datas.get(recitePosition), value);
        String result = CommonUtil.format201906304(newWord);
        uploadAllWordsList(result);
    }

    private void switchView(int mStatus, int position) {

        if (mStatus == 0) {
            recitewording_view.setVisibility(View.VISIBLE);
            reciteworded_view.setVisibility(View.INVISIBLE);
            first_review_view.setVisibility(View.VISIBLE);
            second_dontknow_word_view.setVisibility(View.INVISIBLE);
            second_know_word_view.setVisibility(View.INVISIBLE);
            first_review_view.setBackgroundResource(R.drawable.reciteword_shape_corner);
            bottomButton.showFirstViewButton();
            NewWord word = datas.get(recitePosition);
            alreadyCollect = word.getIFfavorite() == 1 ? true : false;
            if (alreadyCollect) {
                img_first_myfavorite.setImageResource(R.drawable.im_favorite);
            } else {
                img_first_myfavorite.setImageResource(R.drawable.im_notfavorite);
            }
            first_review_view_word.setText(datas.get(recitePosition).getWord());
        } else if (mStatus == 1) {
            first_review_view.setVisibility(View.INVISIBLE);
            second_dontknow_word_view.setVisibility(View.INVISIBLE);
            second_know_word_view.setVisibility(View.VISIBLE);
            second_know_word_view.setBackgroundResource(R.drawable.reciteword_index_shape_corner_white);
            //know_thinkup.setErrorLayoutVisibility(View.INVISIBLE);
            bottomButton.showSecondKnowView();
            if (recitePosition == datas.size() - 1) {
                //dontknow_dontthinkup.setText("查看结果");
                bottomButton.showReviewResultView();
                NewWord word = datas.get(recitePosition);
                alreadyCollect = word.getIFfavorite() == 1 ? true : false;
                if (alreadyCollect) {
                    img_second_know_myfavorite.setImageResource(R.drawable.im_favorite);
                } else {
                    img_second_know_myfavorite.setImageResource(R.drawable.im_notfavorite);
                }

                String mWord = word.getWord();
                SpannableString msp = new SpannableString(mWord);
                msp.setSpan(new RelativeSizeSpan(1.5f), 0, mWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                second_know_word.setText(msp);
                //second_know_word.setText(word.getWord());
                if (word.getPhonetic() != null) {
                    String mPhonetic = NewWordUtils.getSinglePhonetic(word.getPhonetic());
                    second_know_phonetic.setText(mPhonetic);
                }
                second_word_from_content_know.setText(StringUtils.setTextColor(
                        word.getLocalWord(), word.getArticleContent()));
                second_word_meaning_know.setText(getString(R.string.translation) + word.getTempContent());
                second_word_from_bookname.setText("《"+word.getBookName()+"》");
                newwordexplain_item.addView(word.getContent());
            } else {
                //dontknow_dontthinkup.setText("下一个");
                NewWord word = datas.get(recitePosition);
                Logger.e("gft word = " + word);
                alreadyCollect = word.getIFfavorite() == 1 ? true : false;
                if (alreadyCollect) {
                    img_second_know_myfavorite.setImageResource(R.drawable.im_favorite);
                } else {
                    img_second_know_myfavorite.setImageResource(R.drawable.im_notfavorite);
                }

                String mWord = word.getWord();
                SpannableString msp = new SpannableString(mWord);
                msp.setSpan(new RelativeSizeSpan(1.5f), 0, mWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                second_know_word.setText(msp);

                //second_know_word.setText(word.getWord());
                if (word.getPhonetic() != null) {
                    String mPhonetic = NewWordUtils.getSinglePhonetic(word.getPhonetic());
                    second_know_phonetic.setText(mPhonetic);
                }
                second_word_from_content_know.setText(StringUtils.setTextColor( word.getLocalWord(), word.getArticleContent()));
                second_word_meaning_know.setText(getString(R.string.translation) + word.getTempContent());
                second_word_from_bookname.setText("《"+word.getBookName()+"》");
                newwordexplain_item.addView(word.getContent());
            }
        } else if (mStatus == 2) {
            first_review_view.setVisibility(View.INVISIBLE);
            second_dontknow_word_view.setVisibility(View.VISIBLE);
            second_know_word_view.setVisibility(View.INVISIBLE);
            second_dontknow_word_view.setBackgroundResource(R.drawable.reciteword_index_shape_corner_white);
//			know_thinkup.setErrorLayoutVisibility(View.VISIBLE);
//			dontknow_dontthinkup.setErrorLayoutVisibility(View.VISIBLE);
            NewWord word = datas.get(recitePosition);
            String mWord = word.getWord();
            SpannableString msp = new SpannableString(mWord);
            msp.setSpan(new RelativeSizeSpan(1.5f), 0, mWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            second_dontkonw_word.setText(msp);

            //second_dontkonw_word.setText(word.getWord());
            word_from_content_dontknow.setText(StringUtils.setTextColor( word.getLocalWord(), word.getArticleContent()));
            word_meaning_dontknow.setText(getString(R.string.translation) + word.getTempContent());
            second_word_from_bookname_dontknow.setText("《"+word.getBookName()+"》");
            alreadyCollect = word.getIFfavorite() == 1 ? true : false;
            if (alreadyCollect) {
                img_second_dontknow_myfavorite.setImageResource(R.drawable.im_favorite);
            } else {
                img_second_dontknow_myfavorite.setImageResource(R.drawable.im_notfavorite);
            }
            bottomButton.setThinkupText("需要复习");
            bottomButton.setDontThinkupText("我已掌握");
//			know_thinkup.setText("需要复习");
//			dontknow_dontthinkup.setText("我已掌握");
        } else if (mStatus == 3) {
            recitewording_view.setVisibility(View.INVISIBLE);
            reciteworded_view.setVisibility(View.VISIBLE);
            mListView.setAdapter(new MyAdapter());
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if (mListView.canClick()) {
                        switchView(5, position);
                    }
                }
            });
            int allp = group_count * MAX_GROUP_COUNT + recitePosition + 1;
            if (allp == allWordCount) {
                next_group.setBackgroundResource(R.drawable.reciteword_btn_short_gray);
            }
        } else if (mStatus == 4) {
            recitewording_view.setVisibility(View.INVISIBLE);
            reciteworded_view.setVisibility(View.INVISIBLE);
            result_toast_view.setVisibility(View.VISIBLE);
        } else if (mStatus == 5) {
            //显示细节
            isShowDetail = true;
            reciteword_detail_view.setVisibility(View.VISIBLE);
            recitewording_view.setVisibility(View.INVISIBLE);
            reciteworded_view.setVisibility(View.INVISIBLE);
            TextView _detail_view_word = (TextView) findViewById(R.id._detail_view_word);
            TextView _detail_view_word_phonetic = (TextView) findViewById(R.id._detail_view_word_phonetic);
            TextView detail_word_from_content_explain = (TextView) findViewById(R.id.detail_word_from_content_explain);
            TextView detail_word_from_bookname = (TextView) findViewById(R.id.detail_word_from_bookname);
            NewWordExplainView detail_newwordexplain_item = (NewWordExplainView) findViewById(R.id.detail_newwordexplain_item);
            NewWord word = datas.get(position);

            String mWord = word.getWord();
            SpannableString msp = new SpannableString(mWord);
            msp.setSpan(new RelativeSizeSpan(1.5f), 0, mWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            _detail_view_word.setText(msp);
            //_detail_view_word.setText(word.getWord());
            String mPhonetic = NewWordUtils.getSinglePhonetic(word.getPhonetic());
            _detail_view_word_phonetic.setText(mPhonetic);
            detail_newwordexplain_item.addView(word.getContent());
            detail_word_from_content_explain.setText(StringUtils.setTextColor(word.getLocalWord(), word.getTempContent()));
            detail_word_from_bookname.setText("《"+word.getBookName()+"》");
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && isShowDetail) {
            switchView(3, -1);
            isShowDetail = false;
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK
                && !isShowDetail) {
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (null == convertView) {
                convertView = View.inflate(ReadyReciteActivity.this, R.layout.reciteword_explainview_listview_item2, null);
                viewHolder = new ViewHolder();
                viewHolder.word = (TextView) convertView.findViewById(R.id.tv);
                viewHolder.wordExplain = (TextView) convertView.findViewById(R.id._wordexplain);
                viewHolder.resultimage = (ImageView) convertView.findViewById(R.id._recite_result_img);
                if (!groupResult.get(position)) {
                    viewHolder.resultimage.setBackgroundResource(R.drawable.reciteword_failed);
                } else {
                    viewHolder.resultimage.setBackgroundResource(R.drawable.reciteword_pass);
                }
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String mWord = datas.get(position).getWord();
            SpannableString msp = new SpannableString(mWord);
            msp.setSpan(new RelativeSizeSpan(1.5f), 0, mWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.word.setText(msp);
            String phonetic = datas.get(position).getPhonetic();
            if (phonetic != null) {
                String mPhonetic = NewWordUtils.getSinglePhonetic(phonetic);
                viewHolder.wordExplain.setText(mPhonetic);
            }
            TextView delete = (TextView) convertView.findViewById(R.id.delete);
            TextView collect = (TextView) convertView.findViewById(R.id.collect);

            final int pos = position;
            collect.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                   newWord = wordDao.updateFavorite(datas.get(pos), true);
                    String result = CommonUtil.format201906304(newWord);
                    uploadAllWordsList(result);
                    datas.remove(pos);
                    notifyDataSetChanged();
                    mListView.turnToNormal();
                    ToastCompat.makeText(ReadyReciteActivity.this, "单词已添加到收藏夹", Toast.LENGTH_SHORT).show();

                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteWord(datas.get(pos));
                    datas.remove(pos);
                    notifyDataSetChanged();
                    mListView.turnToNormal();
                }
            });

            return convertView;
        }
    }

    private void deleteWord(final NewWord word){
        OkGo.post(Urls.DeleteWordsURL)
                .params("userId",CommonUtil.getUserId()+"")
                .params("word",word.getWord()+"")
                .execute(new JsonCallback<IycResponse<String>>(this) {
                    @Override
                    public void onSuccess(IycResponse<String> stringIycResponse, Call call, Response response) {
                        ToastCompat.makeText(ReadyReciteActivity.this, "删除单词成功", Toast.LENGTH_SHORT).show();
//                      wordDAO.updateDeleteStatus(datas.get(position));
                        wordDao.updateDelete(word);

                    }
                    @Override
                    public void onError(Call call, Response response, Exception e) {

                        ToastCompat.makeText(ReadyReciteActivity.this, "删除单词失败", Toast.LENGTH_SHORT).show();

                    }

                });
    }

    class ViewHolder {
        TextView word;
        TextView wordExplain;
        ImageView resultimage;
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (UIHelper.isNetAvailable(context)) {
//            UploadNewWord();
        }
    }

//    private void UploadNewWord(){
//
//        new Thread() {
//            public void run() {
//                String accountId=String.valueOf(getAccountId());
//                List<NewWord> unUploadNewWords = wordDAO.getUnUploadNewWords(accountId);
//                JSONObject jsonObject = null;
//                JSONObject arrayObject = null;
//                JSONArray jsonArray = null;
//                try {
//                    int size=unUploadNewWords.size();
//                    if (size > 0) {
//                        jsonObject = new JSONObject();
//                        jsonArray = new JSONArray();
//                        jsonObject.put("accountId", accountId);
//                        for (int i = 0; i < size; i++) {
//                            arrayObject = new JSONObject();
//                            NewWord word = unUploadNewWords.get(i);
//                            long mBookId = word.getBookId();
//                            arrayObject.put("bookId", mBookId == 0 ? 53
//                                    : mBookId);
//                            arrayObject.put("accountId", accountId);
//                            arrayObject.put("bookName", word.getBookName());
//                            arrayObject.put("word", word.getWord());
//                            arrayObject.put("phonetic",word.getPhonetic());
//                            arrayObject.put("content", word.getTempContent());
//                            arrayObject.put("articleContent",word.getArticleContent());
//                            arrayObject.put("level", word.getLevel());
//                            arrayObject.put("lastUpdateTime", word.getLastUpdateTime());
//                            int status=word.getStatus();
//                            if(status==1){
//                                //new
//                                arrayObject.put("status", 1);// 1插入，3删除
//                                arrayObject.put("id", 0);
//                            }else if(status==2){
//                                //edit
//                                arrayObject.put("status", 2);// 2编辑，3删除
//                                arrayObject.put("id", word.getServerWordId());
//                            }else if(status==3){
//                                //delete
//                                arrayObject.put("status", 3);// 1不删除，3删除
//                                arrayObject.put("id", word.getServerWordId());
//                            }
//                            arrayObject.put("IFreadyRecite",word.getIFreadyRecite());
//                            arrayObject.put("IFalreadyknow",word.getIFalreadyKnow());
//                            arrayObject.put("IFfavorite",word.getIFfavorite());
//                            arrayObject.put("IFneedagain",word.getIFneedAgain());
//                            arrayObject.put("IFreadyRecite",word.getIFreadyRecite());
//                            jsonArray.put(arrayObject);
//                        }
//                        jsonObject.put("newWords", jsonArray);
//                        jsonObject.put("deviceType",
//                                Constant.DEVICETYPE_ANDROID);
//                        jsonObject.put("deviceToken",
//                                appContext.getDeviceToken());
//                        String jsonStr = InternetApi.readJson(
//                                Constant.UPLOAD_NEWWORDS_RECORD, jsonObject,
//                                InternetApi.globalSession);
//                        int errorCode = InternetApi.getErrorCode(jsonStr);
//                        if (errorCode == 0) {
//                            JSONObject obj = new JSONObject(jsonStr);
//                            JSONArray arr = obj.getJSONArray("newWords");
//                            int arrsize = arr.length();
//                            List<NewWord> newWords = new ArrayList<NewWord>();
//                            for (int i = 0; i < arrsize; i++) {
//                                JSONObject o = arr.getJSONObject(i);
//                                NewWord bean = new NewWord();
//                                long mBookId = o.getLong("bookId");
//                                bean.setBookId(mBookId == 53 ? 0 : mBookId);
//                                bean.setWord(o.getString("word"));
//                                bean.setAccountId(o.getInt("accountId"));
//                                bean.setServerWordId(o.getInt("id"));
//                                bean.setLastUpdateTime(o.getString("lastUpdateTime"));
//                                int status=o.getInt("status");
//
//                                if(status==1){
//                                    //add and edit
//                                    wordDAO.updateNewWord(bean);
//                                }else if(status==2){
//                                    wordDAO.updateNewWord(bean);
//                                }else if(status==3){
//                                    //delete
//                                    wordDAO.deleteNewWordById(String.valueOf(o.getInt("id")));
//                                }
//                                //newWords.add(bean);
//                            }
////							dao.deleteServerAlreadyNotExistData();
//                            //wordDao.updateNewWordRecords(newWords);
//                        }
////							else if (errorCode == 3) {
////							for (int i = 0; i < size; i++) {
////								NoteBean note = unUploadRecords.get(i);
////								if (note.getStatus() == 3
////										|| note.getStatus() == 2) {
////									dao.deleteNoteById(note.getNoteId());
////								}
////							}
////						}
//                        else{
//                            // handler.sendEmptyMessage(NOTE_RECORD_UPLOAD_FAILED);
//                            L.d(TAG, "上传用户阅读信息失败");
//                        }
//                    }
//
//                } catch (Exception e) {
//                    //handler.sendEmptyMessage(-1);
//                }
//            }
//        }.start();
//    }
//
//    private long getAccountId(){
//        long accId;
//        appContext=AppContext.getInstance();
//        if (appContext.user!=null||(!appContext.isLogin)) {
//            SharedPreferences loginPreference = context.getSharedPreferences(
//                    "userlogin", 0);
//            accId = loginPreference.getLong("accountId", 0);
//        } else {
//            accId = appContext.user.accountId;
//        }
//        return accId;
//    }
}
