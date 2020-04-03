package com.iyangcong.reader.utils.postTask;

import android.content.Context;

import java.net.SocketException;

public class PostTaskerCannotRetry extends PostTasker {
	@Override
	public boolean canRetryOnError(Context context, Exception e) {
		return e instanceof SocketException;
	}
}
