package com.iyangcong.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.VideoPlayerActivity;
import com.iyangcong.reader.bean.CommonVideo;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ljw on 2017/1/4.
 */

public class DiscoverReadPartyVedioAdapter extends BaseAdapter {

	private final Context mContext;
	private GlideImageLoader glideImageLoader;
	private List<CommonVideo> commonVedioList;


	public DiscoverReadPartyVedioAdapter(Context mContext, List<CommonVideo> commonVedioList) {
		this.mContext = mContext;
		this.commonVedioList = commonVedioList;

	}



	@Override
	public int getCount() {
		return commonVedioList == null ? 0 : commonVedioList.size();
	}

	@Override
	public Object getItem(int i) {
		return commonVedioList.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.item_read_party_vedio, null);
			holder = new ViewHolder(mContext, convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.setData(commonVedioList.get(position));
		return convertView;
	}

	class ViewHolder {

		private Context mContext;
		@BindView(R.id.iv_read_party_vedio)
		ImageView ivReadPartyVedio;
		@BindView(R.id.iv_play)
		ImageView ivPlay;

		ViewHolder(Context context, View view) {
			mContext = context;
			ButterKnife.bind(this, view);
			if (glideImageLoader == null) {
				glideImageLoader = new GlideImageLoader();
			}
		}

		private void setData(CommonVideo video) {
			glideImageLoader.onDisplayImage(mContext, ivReadPartyVedio, video.getVideoImage());
			ivPlay.setTag(video);
		}

		@OnClick({R.id.iv_read_party_vedio, R.id.iv_play})
		public void onClick(View view) {
			CommonVideo tmpVideo = (CommonVideo)ivPlay.getTag();
			if(tmpVideo == null){
				Logger.e("wzp" + tmpVideo);
				return;
			}
			switch (view.getId()) {
				case R.id.iv_read_party_vedio:
				case R.id.iv_play:
					Intent intent = new Intent(mContext, VideoPlayerActivity.class);
					intent.putExtra(Constants.VIDEO_TITLE,"视频");
					intent.putExtra(Constants.VIDEO_ADDRESS,tmpVideo.getVideoUrl());
					intent.putExtra(Constants.VIDEO_COVER,tmpVideo.getVideoImage());
					mContext.startActivity(intent);
					break;
			}
		}
	}
}
