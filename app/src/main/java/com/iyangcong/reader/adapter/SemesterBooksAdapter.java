package com.iyangcong.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.BookMarketBookDetailsActivity;
import com.iyangcong.reader.activity.BookMarketBookListActivity;
import com.iyangcong.reader.bean.BookIntroduction;
import com.iyangcong.reader.bean.SemesterBook;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.ui.RatingBar;
import com.iyangcong.reader.ui.TagGroup;
import com.iyangcong.reader.ui.button.FlatButton;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.Urls.StudentSemesterBooks;

/**
 * Created by Administrator on 2016/12/24 0024.
 */

public class SemesterBooksAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<SemesterBook> list;
    private Context context;
    GlideImageLoader glideImageLoader;
    OnSemesterBookClickedListener onSemesterBookClickedListener;

    public SemesterBooksAdapter(Context context, List<SemesterBook> list) {
        this.context = context;
        this.list = list;
        mInflater = LayoutInflater.from(context);
        glideImageLoader = new GlideImageLoader();
    }

    public interface OnSemesterBookClickedListener{
        public void onClicked(SemesterBook semesterbook,int position);
    }

    public OnSemesterBookClickedListener getContentFromListener(){
        return onSemesterBookClickedListener;
    }

    public void setOnSemesterBookClickedListener(OnSemesterBookClickedListener onSemesterBookClickedListener){
        this.onSemesterBookClickedListener=onSemesterBookClickedListener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        int i, j;
        String language = "";
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_book_semester, null);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //把用list中的内容显示在listview中
        //holder.tvBookIntroduce.setText(list.get(position).getBookIntroduction());
//        for (i = 0; i < list.get(position).getgetBookLanguage().size(); i++) {
//            language += list.get(position).getBookLanguage().get(i);
//        }
//        language = CommonUtil.getSupportLanguage(language);
        glideImageLoader.displayBookCover(context, holder.ivBookIntroductionImage, list.get(position).getBookCover());
       holder.tvBookIntroductionTittle.setText(list.get(position).getBookName());
       holder.tvBookIntroductionAuthor.setText(list.get(position).getBookAuthor());
       //holder.rbLevel.setStar(list.get(position).getBookRating());
        //  holder.rbLevel.setmClickable(false);holder.tvBookIntroductionAuthor.setText(list.get(position).getBookAuthor());
        holder.tvBookIntroduce.setText(list.get(position).getBookContentIntro());
        //showTags(holder.rlIntroduction, list.get(position).getBookTypeList());
        //holder.tvBookIntroductionLanguage.setText(language);

       if(list.get(position).isReceived()){
           //holder.received.setTags("已领取");
           holder.btSemester.setText("已领取");
//                holder.rbLevel.setVisibility(View.GONE);
//                holder.tvBookKind.setVisibility(View.VISIBLE);
           holder.btSemester.setEnabled(false);
           //holder.btSemester.updateBackground(context.getColor(R.id.zhou_default_image_tag_id)));
       } else{
           holder.btSemester.setEnabled(true);
           holder.btSemester.setText("领取");

//                holder.rbLevel.setVisibility(View.GONE);
//                holder.tvBookKind.setVisibility(View.VISIBLE);
           holder.btSemester.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                   SemesterBook semesterBook = list.get(position);
                   onSemesterBookClickedListener.onClicked(semesterBook,position);


               }
           });


       }


        holder.llBookIntroduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BookMarketBookDetailsActivity.class);
                intent.putExtra("bookId", (int) list.get(position).getBookId());
                intent.putExtra("bookName", list.get(position).getBookName());
                context.startActivity(intent);
            }
        });

        holder.rlIntroduction.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                //Toast.makeText(mContext,tag,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, BookMarketBookListActivity.class);
                intent.putExtra("list_type", 8);
                intent.putExtra("tagsName", tag);
                context.startActivity(intent);
            }
        });



        convertView.setTag(holder);
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_book_introduction_image)
        ImageView ivBookIntroductionImage;
        @BindView(R.id.tv_book_introduction_tittle)
        TextView tvBookIntroductionTittle;
//        @BindView(R.id.rb_level)
//        RatingBar rbLevel;
        @BindView(R.id.tv_book_introduction_author)
        TextView tvBookIntroductionAuthor;
        @BindView(R.id.tv_book_introduce)
        TextView tvBookIntroduce;
        @BindView(R.id.tv_book_introduction_language)
        TextView tvBookIntroductionLanguage;
        @BindView(R.id.ll_book_introduction)
        LinearLayout llBookIntroduction;
        @BindView(R.id.rl_introduction_type)
        TagGroup rlIntroduction;
//        @BindView(R.id.rl_introduction_Received)
//        TagGroup received;
        @BindView(R.id.tv_book_kind)
        TextView tvBookKind;
        @BindView(R.id.bt_semester)
        FlatButton btSemester;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private synchronized void showTags(TagGroup tagGroup, List<String> list) {
//        int tagSize = list.size();
        int tagSize = list.size() > 2 ? 2 : list.size();
        String[] tags = new String[tagSize];
        for (int j = 0; j < list.size() && j < 2; j++) {
            tags[j] = list.get(j);
        }
        tagGroup.setTags(tags);
    }
}
