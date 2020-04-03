package com.iyangcong.reader.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.VideoView;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.iyangcong.reader.R;
import com.iyangcong.reader.broadcast.VideoPlayerBroadcastReceiver;
import com.iyangcong.reader.interfaceset.NetEvent;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.floatVideoWindow.PIPManager;
import com.yanzhenjie.permission.AndPermission;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;

import static com.iyangcong.reader.utils.NetUtil.NETWORK_MOBILE;
import static com.iyangcong.reader.utils.NetUtil.NETWORK_NONE;
import static com.iyangcong.reader.utils.NetUtil.NETWORK_WIFI;

public class VideoPlayerActivity extends SwipeBackActivity implements NetEvent {



	@BindView(R.id.btnBack)
	ImageButton btnBack;
	@BindView(R.id.textHeadTitle)
	TextView textHeadTitle;
	@BindView(R.id.btnFunction)
	ImageButton btnFunction;
	@BindView(R.id.layout_header)
	LinearLayout layoutHeader;
	@BindView(R.id.activity_video_player)
	LinearLayout activityVideoPlayer;

	private String mTitle = "";
	private String mAddress = "";
	private VideoPlayerBroadcastReceiver receiver;
	private String mCover;
	private PIPManager mPIPManager;
	private SharedPreferenceUtil sharePreferenceUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_player);
		ButterKnife.bind(this);
		VideoPlayerBroadcastReceiver.setNetEvent(this);
		initData(savedInstanceState);
		setMainHeadView();
		initView();
	}
	private boolean isFirstFloatVideo() {
		if (sharePreferenceUtil == null) {
			sharePreferenceUtil = SharedPreferenceUtil.getInstance();
		}
		boolean isFirst = sharePreferenceUtil.getBoolean(SharedPreferenceUtil.IS_FIRST_FLOAT_VIDEO, true);
		if (isFirst) {
			sharePreferenceUtil.putBoolean(SharedPreferenceUtil.IS_FIRST_FLOAT_VIDEO, false);
		}
		return isFirst;
	}
//	@Override
	protected void initData(Bundle savedInstanceState) {
		mTitle = getIntent().getStringExtra(Constants.VIDEO_TITLE);
		mAddress = getIntent().getStringExtra(Constants.VIDEO_ADDRESS);
		mCover = getIntent().getStringExtra(Constants.VIDEO_COVER);
//		mAddress = "http://www.androidbegin.com/tutorial/AndroidCommercial.3gp";
	}

//	@Override
	protected void initView() {
		textHeadTitle.setText("播放视频");
		btnFunction.setVisibility(View.VISIBLE);
		btnFunction.setImageResource(R.drawable.xiaochuang);
		btnFunction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isFirstFloatVideo()){
					ToastCompat.makeText(VideoPlayerActivity.this, "请开启当前应用的悬浮窗权限以使用小窗视频功能！", Toast.LENGTH_LONG).show();
				}
				AndPermission
						.with(VideoPlayerActivity.this)
						.overlay()
						.onGranted(data -> {
							mPIPManager.startFloatWindow();
							finish();
						})
						.onDenied(data -> {
							ToastCompat.makeText(VideoPlayerActivity.this, "请开启当前应用的悬浮窗权限以使用小窗视频功能！", Toast.LENGTH_LONG).show();
						})
						.start();
			}
		});
		FrameLayout playerContainer = (FrameLayout) findViewById(R.id.player_container);
		mPIPManager = PIPManager.getInstance();
		VideoView videoView = mPIPManager.getVideoView();
		StandardVideoController controller = new StandardVideoController(this);
		videoView.setVideoController(controller);
		if (mPIPManager.isStartFloatWindow()) {
			mPIPManager.stopFloatWindow();
			controller.setPlayerState(videoView.getCurrentPlayerState());
			controller.setPlayState(videoView.getCurrentPlayState());
            playerContainer.addView(videoView);
		} else {
			mPIPManager.setActClass(VideoPlayerActivity.class);
//			Glide.with(this)
//					.load("http://sh.people.com.cn/NMediaFile/2016/0112/LOCAL201601121344000138197365721.jpg")
//					.asBitmap()
//					.animate(R.anim.anim_alpha_in)
//					.placeholder(android.R.color.darker_gray)
//					.into(controller.getThumb());
			videoView.setUrl(mAddress);
			controller.setTitle(mTitle);
            playerContainer.addView(videoView);
            if(mTitle.equals("阅读器")) {
				if(isFirstFloatVideo()){
					ToastCompat.makeText(VideoPlayerActivity.this, "请开启当前应用的悬浮窗权限以使用小窗视频功能！", Toast.LENGTH_LONG).show();
				}
				AndPermission
						.with(this)
						.overlay()
						.onGranted(data -> {
							mPIPManager.startFloatWindow();
							finish();
						})
						.onDenied(data -> {
							ToastCompat.makeText(VideoPlayerActivity.this, "请开启当前应用的悬浮窗权限以使用小窗视频功能！", Toast.LENGTH_LONG).show();
						})
						.start();
			}
		}

		mPIPManager.getVideoView().start();


	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPIPManager.reset();
		receiver = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mPIPManager.resume();
	}

	@Override
	public void onBackPressed() {
		if (mPIPManager.onBackPress()) return;
		super.onBackPressed();
	}
	@Override
	protected void onPause() {
		super.onPause();
		mPIPManager.pause();
	}


	private void showTips(){
		if(mPIPManager.getVideoView().isPlaying()){
			mPIPManager.pause();
		}
		final NormalDialog dialog = new NormalDialog(this);
		dialog.setTitle("您正在是使用流量观看视频");
		dialog.content("可能会产生流量费用，是否继续？");
		dialog.setOnBtnClickL(
				new OnBtnClickL() {
					@Override
					public void onBtnClick() {
						if(!mPIPManager.getVideoView().isPlaying()){
							mPIPManager.resume();
						}

					}
				},
				new OnBtnClickL() {
					@Override
					public void onBtnClick() {
						mPIPManager.stopFloatWindow();
						dialog.dismiss();
					}
				});
		dialog.btnText("继续","取消");
		dialog.show();
	}

		@Override
	public void onNetChange(int netMobile) {
		switch (netMobile){
			case NETWORK_MOBILE:
				showTips();
				break;
			case NETWORK_WIFI:
				//player.onStateNormal();
				ToastCompat.makeText(this,"已经切换到Wifi下，可以放心观看视频啦", Toast.LENGTH_LONG).show();
				break;
			case NETWORK_NONE:
				ToastCompat.makeText(this,"网络异常，请检查网络",Toast.LENGTH_LONG).show();
				//player.onStateError();
				break;
		}
	}


//	@Override
	protected void setMainHeadView() {
		textHeadTitle.setText(mTitle);
		btnBack.setVisibility(View.VISIBLE);
		btnBack.setImageResource(R.drawable.btn_back);
	}


	@OnClick({R.id.btnBack})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnBack:
				finish();
				break;
		}
	}
}
