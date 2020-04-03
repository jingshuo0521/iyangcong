package com.iyangcong.reader.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.MonthlyMarketBookListItem;
import com.iyangcong.reader.interfaceset.OnItemClickedListener;
import com.iyangcong.reader.ui.RatingBar;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.ViewHolderModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by WuZepeng on 2017-04-26.
 */

//public class BookMarketMonthlyAdapter extends RecyclerView.Adapter {
public class BookMarketMonthlyAdapter extends RecyclerView.Adapter {


	private List<MonthlyMarketBookListItem> mItemList;
	private LayoutInflater mInflater;
	private Context mContext;
	private OnItemClickedListener mOnItemClickedListener;
	public BookMarketMonthlyAdapter(List<MonthlyMarketBookListItem> mItemList, Context mContext) {
		this.mItemList = mItemList;
		this.mContext = mContext;
		mInflater = LayoutInflater.from(mContext);

	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = mInflater.inflate(R.layout.item_monthly_booklist, parent, false);
		return new Holder(mContext, view);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		Holder testHolder = (Holder) holder;
		testHolder.setData(mItemList.get(position));
	}

	@Override
	public int getItemCount() {
		return mItemList == null ? 0 : mItemList.size();
	}



	public class Holder extends ViewHolderModel<MonthlyMarketBookListItem> {

		private Context context;
		@BindView(R.id.iv_monthly_book_introduction_image)
		ImageView ivMonthlyBookIntroductionImage;
		@BindView(R.id.tv_monthly_book_name)
		TextView tvMonthlyBookName;
		@BindView(R.id.tv_monthly_book_guid)
		TextView tvMonthlyBookGuid;
		@BindView(R.id.rb_level)
		RatingBar rbLevel;
		@BindView(R.id.tv_book_origintime)
		TextView tvBookOrigintime;
		@BindView(R.id.tv_book_endtime)
		TextView tvBookEndtime;
		@BindView(R.id.tv_monthly_book_original_price)
		TextView tvMonthlyBookOriginalPrice;
		@BindView(R.id.tv_monthly_book_present_price)
		TextView tvMonthlyBookPresentPrice;
		//		@BindView(R.id.rl_introduction_type)
//		TagGroup rlIntroductionType;
		@BindView(R.id.layout_item_monthly_booklist)
		LinearLayout layoutItemMonthlyBooklist;

		Holder(Context context, View view) {
			super(view);
			ButterKnife.bind(this, view);
			this.context = context;
		}

		@Override
		public boolean setLayoutVisibility(MonthlyMarketBookListItem monthlyMarketBookListItems) {
			if (monthlyMarketBookListItems == null ) {
				rbLevel.setVisibility(View.GONE);
				layoutItemMonthlyBooklist.setVisibility(View.GONE);
				return false;
			}
			rbLevel.setVisibility(View.GONE);
			layoutItemMonthlyBooklist.setVisibility(View.VISIBLE);
			return true;
		}

		@Override
		public void bindData(MonthlyMarketBookListItem item) {
			GlideImageLoader.displayRoundCorner(context,item.getBookImageUrl(), ivMonthlyBookIntroductionImage,R.drawable.pic_default_special_topic);
			tvMonthlyBookName.setText(item.getName());
			tvMonthlyBookGuid.setText(item.getBookIntroduction());
			tvBookOrigintime.setText("包月起始时间：" + item.getOriginTime());
			tvBookEndtime.setText("包月结束时间：" + item.getEndTime());
			tvMonthlyBookOriginalPrice.setText("原价:￥" + item.getBookPrice() + "");
			tvMonthlyBookOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
			tvMonthlyBookPresentPrice.setText("包月价:￥" + item.getBookSpecialPrice() + "");
			layoutItemMonthlyBooklist.setTag(item);
			layoutItemMonthlyBooklist.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					MonthlyMarketBookListItem item = (MonthlyMarketBookListItem)layoutItemMonthlyBooklist.getTag();
					if(item == null || item.getId() == null)
						return;
					if(mOnItemClickedListener != null){
						mOnItemClickedListener.onItemClickedListener(mItemList.indexOf(item));
					}
				}
			});

		}
	}

	public OnItemClickedListener getOnItemClickedListener() {
		return mOnItemClickedListener;
	}

	public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
		mOnItemClickedListener = onItemClickedListener;
	}
}
