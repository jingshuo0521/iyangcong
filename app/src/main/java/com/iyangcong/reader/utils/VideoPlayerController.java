package com.iyangcong.reader.utils;

import android.net.Uri;

import com.orhanobut.logger.Logger;
import com.ravenfeld.easyvideoplayer.EasyVideoCallback;
import com.ravenfeld.easyvideoplayer.internal.PlayerView;

/**
 * Created by WuZepeng on 2017-03-30.
 */

public class VideoPlayerController extends EasyVideoCallback {
	@Override
	public void onVideoProgressUpdate(PlayerView player, int position, int duration) {
		super.onVideoProgressUpdate(player, position, duration);
	}

	public VideoPlayerController() {
		super();
	}

	@Override
	public void onStarted(PlayerView player) {
		super.onStarted(player);
		Logger.i("VideoPlayerController：onStarted"+player);
	}

	@Override
	public void onPaused(PlayerView player) {
		super.onPaused(player);
		Logger.i("VideoPlayerController：onPaused"+player);
	}

	@Override
	public boolean onPreparing(PlayerView player) {
		Logger.i("VideoPlayerController：onPreparing"+player);
		return super.onPreparing(player);
	}

	@Override
	public void onPrepared(PlayerView player) {
		super.onPrepared(player);
		Logger.i("VideoPlayerController：onPrepared"+player);
	}

	@Override
	public void onBuffering(PlayerView player, int percent) {
		super.onBuffering(player, percent);
		Logger.i("VideoPlayerController：onBuffering"+player);
	}

	@Override
	public void onError(PlayerView player, Exception e) {
		super.onError(player, e);
		Logger.i("VideoPlayerController：onError"+player);
	}

	@Override
	public void onCompletion(PlayerView player) {
		super.onCompletion(player);
		Logger.i("VideoPlayerController：onCompletion"+player);
	}

	@Override
	public void onRetry(PlayerView player, Uri source) {
		super.onRetry(player, source);
		Logger.i("VideoPlayerController：onRetry"+player);
	}

	@Override
	public void onSubmit(PlayerView player, Uri source) {
		super.onSubmit(player, source);
		Logger.i("VideoPlayerController：onSubmit"+player);
	}

	@Override
	public void onFullScreen(PlayerView player) {
		super.onFullScreen(player);
		Logger.i("VideoPlayerController：onFullScreen"+player);
	}

	@Override
	public void onFullScreenExit(PlayerView player) {
		super.onFullScreenExit(player);
		Logger.i("VideoPlayerController：onFullScreenExit"+player);
	}
}
