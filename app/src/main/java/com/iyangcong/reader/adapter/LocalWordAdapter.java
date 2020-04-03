package com.iyangcong.reader.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.anarchy.classify.util.L;
import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.MineDictionaryActivity;
import com.iyangcong.reader.bean.Word;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.NotNullUtils;
import com.iyangcong.reader.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocalWordAdapter extends BaseAdapter {

    private Context context;
    private List<Word> wordList;

    public LocalWordAdapter(Context context, List<Word> wordList) {
        this.context = context;
        this.wordList = wordList;
    }

    @Override
    public int getCount() {
        return wordList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.local_search_word, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvTextWord.setText(wordList.get(position).getWord().get(0));
        holder.tvPhonetic.setText(wordList.get(position).getPhonetic() == null || wordList.get(position).getPhonetic().equals("")?"":
                "/" + wordList.get(position).getPhonetic() + "/" + " ");
        holder.tvWebExplains.setText(NotNullUtils.isNull(wordList.get(position).getExplains())?
                (wordList.get(position).getWebExplains()==null||wordList.get(position).getWebExplains().equals("")?
                        (wordList.get(position).getTranslation()==null||"".equals(wordList.get(position).getTranslation().trim()))?
                                " 暂无释义":wordList.get(position).getTranslation():
                        "网络释义："+ TextUtils.join(",",wordList.get(position).getWebExplains().values().toArray())+"\n"):
                (StringUtils.listToString(wordList.get(position).getExplains()) == null || StringUtils.listToString(wordList.get(position).
                        getExplains()).equals("")?"":/*"释：" + */StringUtils.listToString(wordList.get(position).getExplains()) + "\n"));
        return convertView;
    }

    public class ViewHolder {
        @BindView(R.id.webExplains)
        TextView tvWebExplains;
        @BindView(R.id.text_word)
        TextView tvTextWord;
        @BindView(R.id.phonetic)
        TextView tvPhonetic;
        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}
