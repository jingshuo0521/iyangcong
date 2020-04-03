package com.iyangcong.reader.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.NetUtil;
import com.orhanobut.logger.Logger;

import java.util.Timer;
import java.util.TimerTask;

public class NetBroadcastReceiver extends BroadcastReceiver {
    private static NetEvent event;
	private static boolean hasShown = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)&&!hasShown) {
			hasShown = true;
			int netWorkState = NetUtil.getNetWorkState(context);
			Logger.e("wzp send!!!"+netWorkState);
			BroadCastSender.sendBroadCastToVideoPlay(context,netWorkState);
            // 接口回调传过去状态的类型
            if (event != null) {
                event.onNetChange(netWorkState);
            } else if (intent.getAction().equals(Constants.DATA_ERROR_FROM_REMOTE_DATABASE)) {
                int state = intent.getIntExtra(Constants.NETWORK_ERROR, 0);
                if (state != 0) {
                    event.onNetChange(state);
                }
            }
			clearHasShown();
        }
    }

	private void clearHasShown() {
		Timer tmpTimer = new Timer();
		tmpTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				hasShown = false;
			}
		},1500);
	}


	// 自定义接口
    public interface NetEvent {
        public void onNetChange(int netMobile);
    }

    public static void setNetEvent(NetEvent netEvent) {
        event = netEvent;
    }
}
