package com.iyangcong.reader.adapter;

import android.content.Context;
import android.nfc.cardemulation.HostNfcFService;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.BookIntroduction;
import com.iyangcong.reader.ui.RatingBar;
import com.iyangcong.reader.ui.TagGroup;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/1/8 0008.
 */

public class DiscoverCircleChooseBookAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<BookIntroduction> booklist;
    private LayoutInflater mInflater;
    private OnBookIntroductionSelection onBookIntroductionSelection;

    public DiscoverCircleChooseBookAdapter(Context context, List<BookIntroduction> booklist) {
        this.context = context;
        this.booklist = booklist;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_book_introduction_circle, parent,false);
        return new BookItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BookItemViewHolder bookItemViewHolder = (BookItemViewHolder)holder;
        bookItemViewHolder.setData(booklist.get(position));
    }

    @Override
    public int getItemCount() {
        return booklist == null ? 0:booklist.size();
    }

    private String listToString(List<String> list){
        StringBuilder str = new StringBuilder("");
        if(list == null || list.size() == 0)
            return "";
        else if(list.size() == 1)
            return list.get(0);
        else{
            str.append(list.get(0));
            for(String s:list.subList(1,list.size())){
                str.append("/"+s);
            }
        }
        return str.toString();
    }

    /**
     * 限制显示tag的最大个数
     * @param tmpList
     * @return
     */
    private List<String> limitMaxTagAmount(List<String> tmpList){
        if(tmpList.size() > 2)
            return tmpList.subList(0,2);
        else
            return tmpList;
    }

    public class BookItemViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.book_introducton_circle_cb)
        CheckBox bookIntroductonCircleCb;
        @BindView(R.id.iv_book_introduction_image)
        ImageView ivBookIntroductionImage;
        @BindView(R.id.tv_book_introduction_tittle)
        TextView tvBookIntroductionTittle;
        @BindView(R.id.rb_level)
        RatingBar rbLevel;
        @BindView(R.id.tv_book_introduction_author)
        TextView tvBookIntroductionAuthor;
        @BindView(R.id.tv_book_introduce)
        TextView tvBookIntroduce;
        @BindView(R.id.tv_book_introduction_language)
        TextView tvBookIntroductionLanguage;
//        @BindView(R.id.tv_book_introduction_type1)
//        TextView tvBookIntroductionType1;
//        @BindView(R.id.tv_book_introduction_type2)
//        TextView tvBookIntroductionType2;
        @BindView(R.id.ll_book_introduction)
        LinearLayout llBookIntroduction;
        @BindView(R.id.book_introducton_circle_ll)
        LinearLayout bookIntroductonCircleLl;
        @BindView(R.id.rl_introduction_type)
        TagGroup rlIntroductionType;
        @BindView(R.id.layout_item_book_introduction_circle)
        LinearLayout layoutItemBookIntroductionCricle;

        BookItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void setLayoutVisibility(int visibility){
            layoutItemBookIntroductionCricle.setVisibility(visibility);
            bookIntroductonCircleLl.setVisibility(visibility);
            llBookIntroduction.setVisibility(visibility);
            tvBookIntroductionLanguage.setVisibility(visibility);
            tvBookIntroduce.setVisibility(visibility);
            tvBookIntroductionAuthor.setVisibility(visibility);
            rbLevel.setVisibility(visibility);
            tvBookIntroductionTittle.setVisibility(visibility);
            ivBookIntroductionImage.setVisibility(visibility);
            bookIntroductonCircleCb.setVisibility(visibility);
            rlIntroductionType.setVisibility(visibility);
        }

        private boolean setLayoutVisibility(BookIntroduction bean){
            if(bean == null || !bean.isVisible()){
                setLayoutVisibility(View.GONE);
                return false;
            }
            setLayoutVisibility(View.VISIBLE);
            return true;
        }

        public void bindData(final BookIntroduction book){
            //holder.tvBookIntroduce.setText(booklist.get(position).getBookIntroduction());
            Logger.i("wzp book.isChecked:" + book.isChecked() + "  bookName:" + book.getBookName());
            bookIntroductonCircleCb.setChecked(book.isChecked());
            new GlideImageLoader().displayBookCover(context,ivBookIntroductionImage,book.getBookImageUrl());
            tvBookIntroductionTittle.setText(book.getBookName());
            rbLevel.setStar(book.getBookRating()/2);
            if(book.getBookTypeList() != null){
                rlIntroductionType.setTags(limitMaxTagAmount(book.getBookTypeList()));
            }
            rbLevel.setmClickable(false);
            tvBookIntroductionAuthor.setText(book.getBookAuthor());
            tvBookIntroduce.setText(book.getBookIntroduction());
            tvBookIntroductionLanguage.setText(listToString(book.getBookLanguage()));
            bookIntroductonCircleCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    book.setChecked(isChecked);
                    bookIntroductonCircleCb.setChecked(book.isChecked());
//                    notifyCheckBoxStateChanged(booklist.indexOf(book));
                }
            });
            bookIntroductonCircleLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    book.setChecked(!book.isChecked());
                    bookIntroductonCircleCb.setChecked(book.isChecked());
//                    notifyCheckBoxStateChanged(booklist.indexOf(book));
                }
            });
        }

        public void setData(BookIntroduction bean){
            if(setLayoutVisibility(bean)){
                bindData(bean);
            }
        }
    }

    private void notifyCheckBoxStateChanged(final int position){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(position);
//                notifyDataSetChanged();
            }
        });
    }

    public interface OnBookIntroductionSelection {
        public void onBookIntroductionSelected(BookIntroduction introduction);
    }

    public OnBookIntroductionSelection getOnBookIntroductionSelection() {
        return onBookIntroductionSelection;
    }

    public void setOnBookIntroductionSelection(OnBookIntroductionSelection onBookIntroductionSelection) {
        this.onBookIntroductionSelection = onBookIntroductionSelection;
    }

    public List<BookIntroduction> getBooklist() {
        return booklist;
    }

    public void setBooklist(List<BookIntroduction> booklist) {
        this.booklist = booklist;
    }
}
