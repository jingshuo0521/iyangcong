package com.iyangcong.reader.utils.postTask;

import android.content.Context;

import com.iyangcong.reader.callback.JsonCallback;
/**
 * author:WuZepeng </br>
 * time:2018-05-03 10:50 </br>
 * desc:一个上传任务的原型，主要完成两个功能：构建请求以及执行请求，实例在被创建以后需要
 * <code>PostTaskManager</code>进行管理</br>
 */
public abstract class PostTask {
	private int retryCounter;
	private boolean onErrorRetry;
	public interface Method{
		byte  GET = 1;
		byte  POST = 2;
		byte INITIAL = -1;
	}
	public abstract void buildTaskRequest(byte method,String url, String... keyValuePairs);
	protected abstract <T> void executeSingleTask(JsonCallback<T> callback);
	public void setRetryCounter(int retryCounter) {
		this.retryCounter = retryCounter;
	}

	public int getRetryCounter() {
		return retryCounter;
	}

	public abstract boolean canRetryOnError(Context context, Exception e);
}
