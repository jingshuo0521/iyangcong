package com.iyangcong.reader.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iyangcong.reader.interfaceset.NetEvent;
import com.iyangcong.reader.utils.Constants;
import com.orhanobut.logger.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by WuZepeng on 2017-05-07.
 */

public class VideoPlayerBroadcastReceiver extends BroadcastReceiver{

	private static NetEvent NetEvent;
	private boolean hasShown = false;
	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.i("hahahahahaha onReceive");
		if(intent.getAction().equals(Constants.DATA_ERROR_FOR_VIDEO)&&!hasShown){
			hasShown = true;
			int stateCode = intent.getIntExtra(Constants.NETWORD_STATE,-2);
			if(stateCode > -2 && stateCode < 3 && NetEvent != null){
				NetEvent.onNetChange(stateCode);
			}
			clearHasShown();
		}
	}
	/**
	 * author:WuZepeng </br>
	 * time:2017-09-11 10:47 </br>
	 * desc:清除标志位 </br>
	 */
	private void clearHasShown(){
		Timer tmpTimer = new Timer();
		tmpTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				hasShown = false;
			}
		},1500);
	}

	public static void setNetEvent(NetEvent netEvent) {
		NetEvent = netEvent;
		Logger.i("hahahahahaha setNetEvent");
	}
}
