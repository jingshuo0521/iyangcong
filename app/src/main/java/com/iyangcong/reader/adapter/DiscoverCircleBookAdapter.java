package com.iyangcong.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.BookMarketBookDetailsActivity;
import com.iyangcong.reader.bean.ShelfBook;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ljw on 2016/12/28.
 */

public class DiscoverCircleBookAdapter extends RecyclerView.Adapter<DiscoverCircleBookAdapter.ViewHolder> {


    private Context mContext;
    protected List<ShelfBook> shelfBookList;
    protected LayoutInflater mInflater;

    public DiscoverCircleBookAdapter(Context mContext, List<ShelfBook> shelfBookList) {
        this.mContext = mContext;
        this.shelfBookList = shelfBookList;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public DiscoverCircleBookAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_circle_book,parent,false);
        return new ViewHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(DiscoverCircleBookAdapter.ViewHolder holder, int position) {
        holder.setData(shelfBookList.get(position));
    }

    @Override
    public int getItemCount() {
        return shelfBookList == null ? 0:shelfBookList.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.layout_item_circle_book)
        RelativeLayout layoutItemCircleBook;
        @BindView(R.id.iv_book_image)
        ImageView ivBookImage;
        @BindView(R.id.tv_book_name)
        TextView tvBookName;
        private Context context;
        ViewHolder(Context mContext, View view) {
            super(view);
            context = mContext;
            ButterKnife.bind(this, view);
        }



        public boolean setLayoutVisibility(ShelfBook book){
            if(book == null){
                layoutItemCircleBook.setVisibility(View.GONE);
                return false;
            }
            tvBookName.setEms(4);
            tvBookName.setSingleLine();
            tvBookName.setEllipsize(TextUtils.TruncateAt.END);
            Logger.i("bookid:" + book.getBookId());
            layoutItemCircleBook.setTag(book);
            layoutItemCircleBook.setVisibility(View.VISIBLE);
            layoutItemCircleBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShelfBook book = (ShelfBook)layoutItemCircleBook.getTag();
                    Intent intent = new Intent(context, BookMarketBookDetailsActivity.class);
                    intent.putExtra(Constants.BOOK_ID,(int)book.getBookId());
                    intent.putExtra(Constants.BOOK_NAME,book.getBookName());
                    context.startActivity(intent);
                }
            });
            return true;
        }

        public void bindData(ShelfBook book){
            tvBookName.setText(book.getBookName());
            GlideImageLoader.displayBookCover(context,ivBookImage,book.getBookImageUrl());
        }
        void setData(ShelfBook book){
            if(setLayoutVisibility(book)) {
                bindData(book);
            }
        }
    }
}