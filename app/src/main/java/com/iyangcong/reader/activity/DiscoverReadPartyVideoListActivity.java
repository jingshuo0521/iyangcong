package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.DiscoverReadPartyVedioAdapter;
import com.iyangcong.reader.bean.CommonVideo;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.MyGridView;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class DiscoverReadPartyVideoListActivity extends SwipeBackActivity {

	@BindView(R.id.btnBack)
	ImageButton btnBack;
	@BindView(R.id.textHeadTitle)
	TextView textHeadTitle;
	@BindView(R.id.btnFunction)
	ImageButton btnFunction;
	@BindView(R.id.btnFunction1)
	ImageButton btnFunction1;
	@BindView(R.id.layout_header)
	LinearLayout layoutHeader;
	@BindView(R.id.gv_discover_read_party_vedio)
	MyGridView gvDiscoverReadPartyVedio;

	private List<CommonVideo> commonVedioList;
	private DiscoverReadPartyVedioAdapter readPartyVedioAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discover_read_party_video_list);
		ButterKnife.bind(this);
		setMainHeadView();
		initView();
	}

	@Override
	protected void initData(Bundle savedInstanceState) {

	}

	@Override
	protected void initView() {
		commonVedioList = new ArrayList<CommonVideo>();
		readPartyVedioAdapter = new DiscoverReadPartyVedioAdapter(context,commonVedioList);
		gvDiscoverReadPartyVedio.setAdapter(readPartyVedioAdapter);
		gvDiscoverReadPartyVedio.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				if(commonVedioList != null && commonVedioList.size() != 0){
					Intent intent = new Intent(context, VideoPlayerActivity.class);
					Logger.i("videoTitle:"+commonVedioList.get(i).getVedioTitle()+"\nvideoAddress:"+commonVedioList.get(i).getVideoUrl());
//                        intent.putExtra(Constants.VIDEO_TITLE,tempList.get(i).getVedioTitle());
					intent.putExtra(Constants.VIDEO_TITLE,"视频");
//                        intent.putExtra(Constants.VIDEO_ADDRESS,"https://v.qq.com/x/cover/v48wvgrqe0p14df.html");
					intent.putExtra(Constants.VIDEO_ADDRESS,commonVedioList.get(i).getVideoUrl());
					intent.putExtra(Constants.VIDEO_COVER,commonVedioList.get(i).getVideoImage());
					startActivity(intent);
				}
			}
		});
	}

	@Override
	protected void setMainHeadView() {
		textHeadTitle.setText("读书会视频列表");
		btnBack.setVisibility(View.VISIBLE);
		btnBack.setImageResource(R.drawable.btn_back);
	}

	@OnClick({R.id.btnBack, R.id.btnFunction})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnBack:
				finish();
				break;
			case R.id.btnFunction:
				break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		getVideos();
	}

	public void getVideos() {
		OkGo.get(Urls.DiscoverCircleReadiPartyVideoSource)
				.execute(new JsonCallback<IycResponse<List<CommonVideo>>>(context) {
					@Override
					public void onSuccess(IycResponse<List<CommonVideo>> commonVideoIycResponse, Call call, Response response) {
						List<CommonVideo> tempVideoList = commonVideoIycResponse.getData();
						commonVedioList.clear();
						if (tempVideoList.size() > 0)
							commonVedioList.addAll(tempVideoList);
						readPartyVedioAdapter.notifyDataSetChanged();
					}
				});
	}
}
