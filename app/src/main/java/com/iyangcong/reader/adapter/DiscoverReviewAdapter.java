package com.iyangcong.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.BookMarketBookDetailsActivity;
import com.iyangcong.reader.activity.DiscoverBookReviewDetailActivity;
import com.iyangcong.reader.activity.MinePageActivity;
import com.iyangcong.reader.bean.DiscoverReviews;
import com.iyangcong.reader.ui.RatingBar;
import com.iyangcong.reader.ui.ninegridview.NineGridView;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.ConvertHelper;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.HtmlParserUtils;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by WuZepeng on 2017-03-06.
 */

public class DiscoverReviewAdapter extends RecyclerView.Adapter {

	private final Context context;
	private LayoutInflater mLayoutInflater;
	private List<DiscoverReviews> discoverReviewList;
	private boolean isBookDetail;

	public DiscoverReviewAdapter(Context mContext, List<DiscoverReviews> discoverReviewList, boolean isBookDetail) {
		this.context = mContext;
		this.discoverReviewList = discoverReviewList;
		this.isBookDetail = isBookDetail;
		mLayoutInflater = LayoutInflater.from(mContext);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new DiscoverReviewViewHolder(context, mLayoutInflater.inflate(R.layout.item_discover_topic, null));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		DiscoverReviewViewHolder discoverTopicViewHolder = (DiscoverReviewViewHolder) holder;
		discoverTopicViewHolder.setData(discoverReviewList.get(position));
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public int getItemCount() {
		return discoverReviewList == null ? 0 : discoverReviewList.size();
	}


	public class DiscoverReviewViewHolder extends RecyclerView.ViewHolder {

		private Context mContext;
		@BindView(R.id.iv_user_image)
		ImageView ivUserImage;
		@BindView(R.id.tv_user_name)
		TextView tvUserName;
		@BindView(R.id.tv_topic_title)
		TextView tvTopicTitle;
		@BindView(R.id.tv_topic_describe)
		TextView tvTopicDescribe;
		@BindView(R.id.ngv_image)
		NineGridView ngvImage;
		@BindView(R.id.tv_deliver_time)
		TextView tvDeliverTime;
		@BindView(R.id.tv_topic_like_num)
		TextView tvTopicLikeNum;
		@BindView(R.id.iv_topic_message)
		ImageView ivTopicMessage;
		@BindView(R.id.tv_message_num)
		TextView tvMessageNum;
		@BindView(R.id.book_item)
		LinearLayout llBookItem;
		@BindView(R.id.tv_book_title)
		TextView tvBookTitle;
		@BindView(R.id.tv_book_author)
		TextView tvBookAuthor;
		@BindView(R.id.tv_book_version)
		TextView tvBookVersion;
		@BindView(R.id.iv_book_cover)
		ImageView ivBookCover;
		@BindView(R.id.cardView)
		CardView cardView;
		@BindView(R.id.rb_level)
		RatingBar rbLevel;
		@BindView(R.id.tv_book_publish_house)
		TextView mTvBookPublishHouse;

		@OnClick({R.id.cardView})
		void onItemClick(View view) {
			switch (view.getId()) {
				case R.id.cardView:
					Intent intent = new Intent(context, DiscoverBookReviewDetailActivity.class);
					DiscoverReviews tempReviews = (DiscoverReviews) cardView.getTag();
					Logger.i("hahaha:" + tempReviews);
					if (tempReviews != null && tempReviews.getReviewId() != 0) {
						intent.putExtra(Constants.reviewId, tempReviews.getReviewId());
						intent.putExtra(Constants.THOUGHT_ACTIVITY_TITLE, tempReviews.getTitle());
						intent.putExtra(Constants.TOPIC_ACTIVITY_BOOK_NAME, tempReviews.getTitleZh());
						intent.putExtra(Constants.BookEdition, tempReviews.getEdition());
					}
					context.startActivity(intent);
					break;
			}
		}

		public DiscoverReviewViewHolder(Context mContext, View itemView) {
			super(itemView);
			this.mContext = mContext;
			ButterKnife.bind(this, itemView);
		}

		void setData(final DiscoverReviews reviews) {
			Logger.i(reviews.toString());
			cardView.setTag(reviews);
			if (isBookDetail) {
				llBookItem.setVisibility(View.GONE);
			} else {
				llBookItem.setVisibility(View.VISIBLE);
			}
			rbLevel.setVisibility(View.VISIBLE);
			tvBookTitle.setText(reviews.getTitleZh());
			tvBookAuthor.setText(reviews.getAuther());
			tvBookVersion.setText(ConvertHelper.getEdition(reviews.getEdition()));
			new GlideImageLoader().displayBookCover(mContext, ivBookCover, reviews.getBookcover());
			tvUserName.setText(reviews.getUserName());
			tvTopicTitle.setText(reviews.getTitle());
			tvDeliverTime.setText(reviews.getCreatetime());
			tvTopicLikeNum.setText(reviews.getReviewsLike() + "");
			tvMessageNum.setText(reviews.getMessageNum() + "");
			mTvBookPublishHouse.setText(mContext.getString(R.string.public_house)+reviews.getPublishHouse());
			GlideImageLoader.displayProtrait(mContext, reviews.getUserImageUrl(), ivUserImage);
			final String html = reviews.getContent();
			tvTopicDescribe.setText(Html.fromHtml(HtmlParserUtils.getContent(html)));
			if (reviews.getGrade() == 0) {
				rbLevel.setVisibility(View.INVISIBLE);
			} else {
				rbLevel.setStar(reviews.getGrade() / 2);
			}
			llBookItem.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, BookMarketBookDetailsActivity.class);
					intent.putExtra(Constants.BOOK_ID, reviews.getBookid());
					intent.putExtra(Constants.BOOK_NAME, reviews.getTitleZh());
					mContext.startActivity(intent);
				}
			});

			ivUserImage.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, MinePageActivity.class);
					intent.putExtra(Constants.USER_ID, reviews.getUserId() + "");
					intent.putExtra(Constants.IS_MYSELF, false);
					mContext.startActivity(intent);
				}
			});

		}

	}

	public List<DiscoverReviews> getDiscoverReviewList() {
		return discoverReviewList;
	}

	public void setDiscoverReviewList(List<DiscoverReviews> discoverReviewList) {
		this.discoverReviewList = discoverReviewList;
	}
}
