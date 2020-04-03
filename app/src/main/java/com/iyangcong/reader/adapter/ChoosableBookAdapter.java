package com.iyangcong.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.nfc.cardemulation.HostNfcFService;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.BookMarketBookDetailsActivity;
import com.iyangcong.reader.bean.ShelfBookChoosable;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by WuZepeng on 2017-06-14.
 */
//将书籍列表做成可以选择那种
public class ChoosableBookAdapter extends RecyclerView.Adapter {

	private List<ShelfBookChoosable> mShelfBook;
	private Context mContext;
	private LayoutInflater mLayoutInflater;

	public ChoosableBookAdapter(Context mContext, List<ShelfBookChoosable> shelfBookList) {
		this.mContext = mContext;
		this.mShelfBook = shelfBookList;
		mLayoutInflater = LayoutInflater.from(mContext);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = mLayoutInflater.inflate(R.layout.item_choosable_book, parent, false);
		return new ChoosableBookItem(view);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		ChoosableBookItem choosableBookItem = (ChoosableBookItem)holder;
		choosableBookItem.setData(mShelfBook.get(position));
	}

	@Override
	public int getItemCount() {
		return mShelfBook == null ? 0 : mShelfBook.size();
	}



	public class ChoosableBookItem extends RecyclerView.ViewHolder {
		@BindView(R.id.iv_book_image)
		ImageView ivBookImage;
		@BindView(R.id.cv_book_image)
		CardView cvBookImage;
		@BindView(R.id.tv_book_name)
		TextView tvBookName;
		@BindView(R.id.cb_book_check)
		CheckBox cbBookCheck;
		@BindView(R.id.layout_item_circle_book)
		RelativeLayout layoutItemCircleBook;

		public ChoosableBookItem(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		private boolean setLayoutVisibility(ShelfBookChoosable bean) {
			if (bean == null) {
				layoutItemCircleBook.setVisibility(View.GONE);
				return false;
			}
			layoutItemCircleBook.setVisibility(View.VISIBLE);
			return true;
		}

		private void bindData(final ShelfBookChoosable bean) {
			GlideImageLoader.displayBookCover(mContext, ivBookImage, bean.getBookImageUrl());
			tvBookName.setText(bean.getBookName());
			Logger.i("wzp bean.getChecked:" + bean.isChoosed());
			cbBookCheck.setChecked(bean.isChoosed());
			cbBookCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
					bean.setChoosed(b);
					cbBookCheck.setChecked(bean.isChoosed());
				}
			});
			cvBookImage.setTag(bean);
		}

		private void notiyDataChanged(){
			Handler handler = new Handler();
			handler.post(new Runnable() {
				@Override
				public void run() {
					notifyDataSetChanged();
				}
			});
		}

		public void setData(ShelfBookChoosable bean) {
			if (setLayoutVisibility(bean)) {
				bindData(bean);
			}
		}

		@OnClick({R.id.iv_book_image, R.id.cv_book_image, R.id.tv_book_name, R.id.layout_item_circle_book})
		public void onClick(View view) {
			ShelfBookChoosable shelfBookChoosable = (ShelfBookChoosable)cvBookImage.getTag();
			if(shelfBookChoosable == null){
				Logger.e("wzp shelfBookChoosable="+shelfBookChoosable);
				return;
			}
			switch (view.getId()) {
				case R.id.iv_book_image:
				case R.id.cv_book_image:
				case R.id.tv_book_name:
				case R.id.layout_item_circle_book:
					Intent intent = new Intent(mContext, BookMarketBookDetailsActivity.class);
					intent.putExtra(Constants.BOOK_ID,(int)shelfBookChoosable.getBookId());
					intent.putExtra(Constants.BOOK_NAME,shelfBookChoosable.getBookName());
					mContext.startActivity(intent);
					break;
			}
		}
	}

}
