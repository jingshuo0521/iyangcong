package com.iyangcong.reader.broadcast;

import android.content.Context;
import android.content.Intent;

import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.NetUtil;

/**
 * Created by WuZepeng on 2017-04-07.
 */

public class BroadCastSender {

	public static void sendBroadCast(Context context){
		Intent intent = new Intent();
		intent.setAction(Constants.DATA_ERROR_FROM_REMOTE_DATABASE);
		intent.putExtra(Constants.NETWORK_ERROR, NetUtil.REMOTE_DATABASE_ERROR);
		context.sendBroadcast(intent);
	}

	public static void sendBroadCastToVideoPlay(Context context,int errorCode){
		Intent intent = new Intent();
		intent.setAction(Constants.DATA_ERROR_FOR_VIDEO);
		intent.putExtra(Constants.NETWORD_STATE,errorCode);
		context.sendBroadcast(intent);
	}
}
