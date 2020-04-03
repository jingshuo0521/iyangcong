package com.iyangcong.reader.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.iyangcong.reader.ui.ListItemDelete;
import com.iyangcong.reader.ui.NewWordExplainView;
import com.iyangcong.reader.ui.ScrollListviewDelete;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.NewWordUtils;
import com.iyangcong.reader.utils.StringUtils;
import com.iyangcong.reader.utils.UIHelper;
import com.iyangcong.reader.utils.Urls;

import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;
import java.util.ArrayList;
import java.util.List;

import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

/**
 * Created by Administrator on 2017/3/15.
 */

public class ReciteAlreadyKnowActivity extends BaseActivity implements ScrollListviewDelete.ItemClickListener,View.OnClickListener {

    private ImageButton default_back, rightview;
    private TextView title_content;
    private int status = 0;
    private RelativeLayout recitewording_view, reciteword_nodata_view;
    public static ListItemDelete itemDelete = null;
    private MyAdapter myAdapter;
    private ScrollListviewDelete mListView;
    private List<NewWord> datas;
    private int recitePosition = 0;
    private boolean isShowDetail = false;
    private WordDao wordDAO;
    private NewWord newWord;
    private ImageView img_alreadyknow_myfavorite;
    private boolean alreadyCollect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciteword_alreadyknow);
        setMainHeadView();
        initView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        wordDAO = new WordDao(DatabaseHelper.getHelper(this));
    }

    @Override
    protected void initView() {
        initData();
        if (datas.isEmpty()){
            status = 2;
        }
        recitewording_view = (RelativeLayout) findViewById(R.id.recitewording_view);
        reciteword_nodata_view = (RelativeLayout) findViewById(R.id.reciteword_nodata_view);
        img_alreadyknow_myfavorite = (ImageView) findViewById(R.id.img_alreadyknow_myfavorite);
        img_alreadyknow_myfavorite.setOnClickListener(this);
        mListView = (ScrollListviewDelete) findViewById(R.id.reciteworded_view);
        mListView.setVisibility(View.VISIBLE);
        reciteword_nodata_view.setVisibility(View.INVISIBLE);
        recitewording_view.setVisibility(View.INVISIBLE);
        mListView.setAdapter(new MyAdapter());
        mListView.setOnItemClickListener(this);
        switchView(status, -1);
    }

    @Override
    protected void setMainHeadView() {
        default_back = (ImageButton) findViewById(R.id.btnBack);
        default_back.setVisibility(View.VISIBLE);
        default_back.setImageResource(R.drawable.btn_back);
        default_back.setOnClickListener(this);
        title_content = (TextView) findViewById(R.id.textHeadTitle);
        title_content.getPaint().setFakeBoldText(true);
        title_content.setText("已掌握");
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initData() {
        if (datas != null) {
            datas.clear();
        } else {
            datas = new ArrayList<NewWord>();
        }
//        List<NewWord> allwords = wordDAO.getAlreadyKnowWords(true);
        List<NewWord> allwords = wordDAO.getWordsByDeleteStatusAndIFalreadyKnow();
        datas.addAll(allwords);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next_group:
                initData();
                status = 0;
                switchView(status, -1);
                break;
            case R.id.stop_here:
                break;
            case R.id.btnBack:
                if (isShowDetail) {
                    switchView(0, -1);
                    isShowDetail = false;
                } else {
                    this.finish();
                }
                break;
            case R.id.btnFunction:
//
                break;
            case R.id.img_alreadyknow_myfavorite:
                alreadyCollect = !alreadyCollect;
                dealwithFavorite(alreadyCollect);
                NewWord word = datas.get(recitePosition);
                if (alreadyCollect) {
                    img_alreadyknow_myfavorite.setImageResource(R.drawable.im_favorite);
                    ToastCompat.makeText(ReciteAlreadyKnowActivity.this, getString(R.string.add_to_already_know), Toast.LENGTH_LONG).show();
                    word.setIFfavorite(1);
                } else {
                    img_alreadyknow_myfavorite.setImageResource(R.drawable.im_notfavorite);
                    ToastCompat.makeText(ReciteAlreadyKnowActivity.this, getString(R.string.has_already_be_cancelled), Toast.LENGTH_LONG).show();
                    word.setIFfavorite(0);
                }
                break;
            default:
                break;

        }
    }


    private void dealwithFavorite(boolean value) {
        newWord = wordDAO.updateFavorite(datas.get(recitePosition), value);
        String result = CommonUtil.format201906304(newWord);
        uploadAllWordsList(result);
    }

    private void switchView(int mStatus, final int pos) {

        if (mStatus == 0) {
            mListView.setVisibility(View.VISIBLE);
            reciteword_nodata_view.setVisibility(View.INVISIBLE);
            recitewording_view.setVisibility(View.INVISIBLE);
        } else if (mStatus == 1) {
            showItemView(pos);
        } else if (mStatus == 2) {
            mListView.setVisibility(View.INVISIBLE);
            recitewording_view.setVisibility(View.INVISIBLE);
            reciteword_nodata_view.setVisibility(View.VISIBLE);
        }

    }
    private void showItemView(int position) {
        NewWordExplainView newwordexplain_item = (NewWordExplainView) findViewById(R.id.newwordexplain_item);
        TextView word_detail_view_word = (TextView) findViewById(R.id.word_detail_view_word);
        TextView word_detail_view_word_phonetic = (TextView) findViewById(R.id.word_detail_view_word_phonetic);
        TextView detail_word_from_content_explain = (TextView) findViewById(R.id.detail_word_from_content_explain);
        TextView second_word_from_content_know = (TextView) findViewById(R.id.second_word_from_content_know);
        TextView detail_word_from_bookname = (TextView) findViewById(R.id.detail_word_from_bookname);
        isShowDetail = true;
        reciteword_nodata_view.setVisibility(View.INVISIBLE);
        mListView.setVisibility(View.INVISIBLE);
        recitewording_view.setVisibility(View.VISIBLE);
        NewWord word = datas.get(position);
        Logger.i("hahahahahaha word:" + word.toString());
        alreadyCollect = word.getIFfavorite() == 1 ? true : false;
        if (alreadyCollect) {
            img_alreadyknow_myfavorite.setImageResource(R.drawable.im_favorite);
        } else {
            img_alreadyknow_myfavorite.setImageResource(R.drawable.im_notfavorite);
        }

        String w_value = word.getWord();
        SpannableString msp = new SpannableString(w_value);
        msp.setSpan(new RelativeSizeSpan(1.5f), 0, w_value.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        word_detail_view_word.setText(msp);
        String mPhonetic = NewWordUtils.getSinglePhonetic(word.getPhonetic());
        word_detail_view_word_phonetic.setText(mPhonetic);
        detail_word_from_content_explain.setText(setMyText(word.getWord(), word.getTempContent()));
        second_word_from_content_know.setText(StringUtils.setTextColor(
                word.getLocalWord(), word.getArticleContent()));
        newwordexplain_item.addView(word.getContent());
        detail_word_from_bookname.setText("《"+word.getBookName()+"》");
    }


    private SpannableString setMyText(String selectString, String text) {

        if (text != null && selectString != null) {
            int start = 0;
            int end = (start + text.length()>text.length()?text.length():start + text.length());
            SpannableString msp = new SpannableString(text);
//            msp.setSpan(new ForegroundColorSpan(Color.parseColor("#ee4d22")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置前景色
            return msp;
        }
        return null;
    }


    public int LocationString(int startPos, String ori, String des) {
        ori = ori.toLowerCase();
        if (ori.contains(des)) {
            int start = ori.indexOf(des);
            int end = start + des.length();
            int len = ori.length();
            if (len == end) {
                return start;
            } else {
                char a = ori.charAt(end);
                if (a >= 'a' && a <= 'z' || a >= 'A' && a <= 'Z') {
                    String str = ori.substring(end);
                    return LocationString(end, str, des);
                } else {
                    return start + startPos;
                }
            }

        }
        return 0;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && isShowDetail) {
            switchView(0, -1);
            isShowDetail = false;
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK
                && !isShowDetail) {
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onItemClick(int position) {
        recitePosition = position;
        switchView(1, recitePosition);
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (null == convertView) {
                holder = new ViewHolder();
                convertView = View.inflate(ReciteAlreadyKnowActivity.this, R.layout.item_delete, null);
                holder.word = convertView.findViewById(R.id.tv_word);
                holder.wordExplain = convertView.findViewById(R.id.word_explain);
                holder.tvDelete = convertView.findViewById(R.id.delete);
                holder.tvCollect = convertView.findViewById(R.id.collect);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteWord(datas.get(position));
                    datas.remove(position);
                    ItemDeleteReset();
                    notifyDataSetChanged();
                }
            });
            holder.tvCollect.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    newWord = wordDAO.updateAlreadyKnow(datas.get(position), false);
                    String result = CommonUtil.format201906304(newWord);
                    uploadAllWordsList(result);
                    ToastCompat.makeText(ReciteAlreadyKnowActivity.this, "单词已添加到待复习中", Toast.LENGTH_LONG).show();
                    datas.remove(position);
                    ItemDeleteReset();
                    notifyDataSetChanged();
                }
            });
            String mWord = datas.get(position).getWord();
            SpannableString msp = new SpannableString(mWord);
            msp.setSpan(new RelativeSizeSpan(1.5f), 0, mWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.word.setText(msp);
            holder.wordExplain.setText(setMyText(datas.get(position).getWord(), datas.get(position).getTempContent()));
            return convertView;
        }
    }

    public static void ItemDeleteReset() {
        if (itemDelete != null) {
            itemDelete.reSet();
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
                        wordDAO.updateUpload(newWord);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastCompat.makeText(context,"上传单词失败",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteWord(final NewWord word){
        OkGo.post(Urls.DeleteWordsURL)
                .params("userId",CommonUtil.getUserId()+"")
                .params("word",word.getWord()+"")
                .execute(new JsonCallback<IycResponse<String>>(this) {
                    @Override
                    public void onSuccess(IycResponse<String> stringIycResponse, Call call, Response response) {
                        ToastCompat.makeText(ReciteAlreadyKnowActivity.this, "删除单词成功", Toast.LENGTH_SHORT).show();
//                      wordDAO.updateDeleteStatus(datas.get(position));
                       wordDAO.updateDelete(word);

                    }
                    @Override
                    public void onError(Call call, Response response, Exception e) {

                        ToastCompat.makeText(ReciteAlreadyKnowActivity.this, "删除单词失败", Toast.LENGTH_SHORT).show();

                        }

                });
    }


    class ViewHolder {
        TextView word;
        TextView wordExplain;
        TextView tvDelete;
        TextView tvCollect;
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (UIHelper.isNetAvailable(context)) {

        }
    }

}
