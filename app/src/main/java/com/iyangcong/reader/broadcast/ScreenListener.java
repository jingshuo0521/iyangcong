package com.iyangcong.reader.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class ScreenListener {
	private Context mContext;
	private ScreenBroadcastReceiver receiver;
	private ScreenStateListener mScreenStateListener;
	private SystemEvent mSystemEvent;
	private InnerRecevier mInnerRecevier;

	private ScreenListener() {
		receiver = new ScreenBroadcastReceiver();
		mInnerRecevier = new InnerRecevier();
	}

	private static class ScreenListenrHolder{
		private final static ScreenListener sInstace = new ScreenListener();
	}

	public static ScreenListener getInstance(Context context){
		ScreenListenrHolder.sInstace.mContext = context;
		return ScreenListenrHolder.sInstace;
	}

	public void register(ScreenStateListener screenStateListener) {
		if (screenStateListener != null) {
			mScreenStateListener = screenStateListener;
		}
		if (receiver != null) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_SCREEN_OFF);
			filter.addAction(Intent.ACTION_SCREEN_ON);
			filter.addAction(Intent.ACTION_USER_PRESENT);
			mContext.registerReceiver(receiver, filter);
		}
	}

	public void unregisterScreenReceiver() {
		if (receiver != null) {
			mContext.unregisterReceiver(receiver);
		}
	}

	public void unregisterInnerReceiver(){
		if(mInnerRecevier != null){
			mContext.unregisterReceiver(mInnerRecevier);
		}
	}

	public void register(SystemEvent event){
		if(event != null)
			mSystemEvent = event;
		if(mInnerRecevier != null){
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
			mContext.registerReceiver(mInnerRecevier,filter);
		}
	}
	private class ScreenBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				String action = intent.getAction();
				if (Intent.ACTION_SCREEN_ON.equals(action)) {
					if (mScreenStateListener != null) {
						Log.e("zhang", "ScreenBroadcastReceiver --> ACTION_SCREEN_ON");
						mScreenStateListener.onScreenOn();
					}
				} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
					if (mScreenStateListener != null) {
						Log.e("zhang", "ScreenBroadcastReceiver --> ACTION_SCREEN_OFF");
						mScreenStateListener.onScreenOff();
					}
				} else if (Intent.ACTION_USER_PRESENT.equals(action)) {
					if (mScreenStateListener != null) {
						Log.e("zhang", "ScreenBroadcastReceiver --> ACTION_USER_PRESENT");
						mScreenStateListener.onUserPresent();
					}
				}
			}
		}
	}

	class InnerRecevier extends BroadcastReceiver {

		final String SYSTEM_DIALOG_REASON_KEY = "reason";

		final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

		final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
				String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
				if (reason != null) {
					if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
						if(mSystemEvent != null){
							mSystemEvent.onHomeKeyClicked();
						}
						Log.e("Home键被监听","");
					} else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
						if(mSystemEvent != null){
							mSystemEvent.onMultiTaskClicked();
						}
						Log.e("多任务被监听","");
					}
				}
			}
		}
	}

	public interface ScreenStateListener {
		void onScreenOn();

		void onScreenOff();

		void onUserPresent();
	}

	public interface SystemEvent{
		void onHomeKeyClicked();
		void onMultiTaskClicked();
	}
}
