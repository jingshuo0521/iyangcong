package com.iyangcong.reader.activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
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
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

public class AllWordActivity extends BaseActivity implements ScrollListviewDelete.ItemClickListener, View.OnClickListener {

    private ImageButton default_back;
    private TextView title_content;
    private Toast mToast;
    private NewWord newWord;
    private ScrollListviewDelete listviewDelete;
    private DeleteAdapter adapter;
    private List<NewWord> listDatas = new ArrayList<NewWord>();
    private RelativeLayout wordDetail;
    private ImageView imgMyfavorite;
    private WordDao wordDAO;
    private boolean isShowDetail = false;
    private boolean alreadyCollect = false;
    public static ListItemDelete itemDelete = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_word);
        setMainHeadView();
        wordDAO = new WordDao(DatabaseHelper.getHelper(this));
        listDatas = wordDAO.all();
        imgMyfavorite = findViewById(R.id.img_alreadyknow_myfavorite);
        wordDetail = findViewById(R.id.recitewording_view);
        listviewDelete = findViewById(R.id.list);
        listviewDelete.setVisibility(View.VISIBLE);
        wordDetail.setVisibility(View.INVISIBLE);
        adapter = new DeleteAdapter();
        listviewDelete.setAdapter(adapter);
        imgMyfavorite.setOnClickListener(this);
        listviewDelete.setOnItemClickListener(this);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setMainHeadView() {
        default_back = (ImageButton) findViewById(R.id.btnBack);
        default_back.setVisibility(View.VISIBLE);
        default_back.setImageResource(R.drawable.btn_back);
        default_back.setOnClickListener(this);
        title_content = (TextView) findViewById(R.id.textHeadTitle);
        title_content.getPaint().setFakeBoldText(true);
        title_content.setText("全部单词");
    }

    @Override
    public void onItemClick(int position) {
        newWord = adapter.getItem(position);
        isShowDetail = true;
        switchView(position);
    }

    private void switchView(int position){
        if (isShowDetail){
            listviewDelete.setVisibility(View.INVISIBLE);
            wordDetail.setVisibility(View.VISIBLE);
            showItemView(position);
        }else{
            listviewDelete.setVisibility(View.VISIBLE);
            wordDetail.setVisibility(View.INVISIBLE);
        }
    }

    private void showItemView(int position){
        NewWordExplainView newwordexplain_item = (NewWordExplainView) findViewById(R.id.newwordexplain_item);
        TextView word_detail_view_word = (TextView) findViewById(R.id.word_detail_view_word);
        TextView word_detail_view_word_phonetic = (TextView) findViewById(R.id.word_detail_view_word_phonetic);
        TextView detail_word_from_content_explain = (TextView) findViewById(R.id.detail_word_from_content_explain);
        TextView second_word_from_content_know = (TextView) findViewById(R.id.second_word_from_content_know);
        TextView detail_word_from_bookname = (TextView) findViewById(R.id.detail_word_from_bookname);
        alreadyCollect = newWord.getIFfavorite() == 1 ? true : false;
        if (alreadyCollect) {
            imgMyfavorite.setImageResource(R.drawable.im_favorite);
        } else {
            imgMyfavorite.setImageResource(R.drawable.im_notfavorite);
        }

        String w_value = adapter.getItem(position).getWord();
        SpannableString msp = new SpannableString(w_value);
        msp.setSpan(new RelativeSizeSpan(1.5f), 0, w_value.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        word_detail_view_word.setText(msp);
        String mPhonetic = NewWordUtils.getSinglePhonetic(adapter.getItem(position).getPhonetic());
        word_detail_view_word_phonetic.setText(mPhonetic);
        detail_word_from_content_explain.setText(setMyText(adapter.getItem(position).getWord(), adapter.getItem(position).getTempContent()));
        second_word_from_content_know.setText(StringUtils.setTextColor(
                adapter.getItem(position).getLocalWord(), adapter.getItem(position).getArticleContent()));
        newwordexplain_item.addView(adapter.getItem(position).getContent());
        detail_word_from_bookname.setText("《"+adapter.getItem(position).getBookName()+"》");
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK  && isShowDetail) {
            isShowDetail = false;
            switchView(-1);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK  && !isShowDetail) {
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                if (isShowDetail){
                    isShowDetail = false;
                    switchView(-1);
                }else {
                    this.finish();
                }
                break;
            case R.id.img_alreadyknow_myfavorite:
                alreadyCollect = !alreadyCollect;
                dealwithFavorite(alreadyCollect);
                if (alreadyCollect) {
                    imgMyfavorite.setImageResource(R.drawable.im_favorite);
                    ToastCompat.makeText(AllWordActivity.this, getString(R.string.add_to_already_know), Toast.LENGTH_LONG).show();
                    newWord.setIFfavorite(1);
                } else {
                    imgMyfavorite.setImageResource(R.drawable.im_notfavorite);
                    ToastCompat.makeText(AllWordActivity.this, getString(R.string.has_already_be_cancelled), Toast.LENGTH_LONG).show();
                    newWord.setIFfavorite(0);
                }
                break;
        }
    }

    private void dealwithFavorite(boolean value) {
        NewWord word = wordDAO.updateFavorite(newWord, value);
        String result = CommonUtil.format201906304(word);
        uploadAllWordsList(result,word);
    }

    public class  DeleteAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listDatas == null ? 0 : listDatas.size();
        }

        @Override
        public NewWord getItem(int position) {
            return listDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(AllWordActivity.this, R.layout.item_delete, null);
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
                    deleteWord(listDatas.get(position));
                    listDatas.remove(position);
                    ItemDeleteReset();
                    notifyDataSetChanged();
                }
            });
            holder.tvCollect.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    NewWord word = wordDAO.updateAlreadyKnow(listDatas.get(position), false);
                    String result = CommonUtil.format201906304(word);
                    uploadAllWordsList(result,word);
                    ToastCompat.makeText(AllWordActivity.this, "单词已添加到待复习中", Toast.LENGTH_LONG).show();
                    ItemDeleteReset();
                    notifyDataSetChanged();
                }
            });
            String mWord = listDatas.get(position).getWord();
            SpannableString msp = new SpannableString(mWord);
            msp.setSpan(new RelativeSizeSpan(1.5f), 0, mWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.word.setText(msp);
            holder.wordExplain.setText(setMyText(adapter.getItem(position).getWord(), adapter.getItem(position).getTempContent()));
            return convertView;
        }
    }
    public static void ItemDeleteReset() {
        if (itemDelete != null) {
            itemDelete.reSet();
        }
    }
    class ViewHolder {
        TextView word;
        TextView wordExplain;
        TextView tvDelete;
        TextView tvCollect;
    }

    private void uploadAllWordsList(String str, final NewWord tmpWord){
        if(isNull(context,str,"")){
            com.orhanobut.logger.Logger.e("gft str = " + str);
            return;
        }
        OkGo.post(Urls.AddNewWord)
                .params("dataJsonObjectString",str)
                .execute(new JsonCallback<IycResponse<String>>(context) {
                    @Override
                    public void onSuccess(IycResponse<String> stringIycResponse, Call call, Response response) {
//                        ToastCompat.makeText(context,"上传单词成功",Toast.LENGTH_SHORT).show();
                        wordDAO.updateUpload(tmpWord);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastCompat.makeText(AllWordActivity.this,"上传单词失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteWord(final NewWord word){
        OkGo.post(Urls.DeleteWordsURL)
                .params("userId", CommonUtil.getUserId()+"")
                .params("word",word.getWord()+"")
                .execute(new JsonCallback<IycResponse<String>>(this) {
                    @Override
                    public void onSuccess(IycResponse<String> stringIycResponse, Call call, Response response) {
                        ToastCompat.makeText(AllWordActivity.this, "删除单词成功", Toast.LENGTH_SHORT).show();
//                      wordDAO.updateDeleteStatus(datas.get(position));
                        wordDAO.updateDelete(word);

                    }
                    @Override
                    public void onError(Call call, Response response, Exception e) {

                        ToastCompat.makeText(AllWordActivity.this, "删除单词失败", Toast.LENGTH_SHORT).show();

                    }

                });
    }
}
