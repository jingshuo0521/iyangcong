package com.iyangcong.reader.utils.postTask;

import android.content.Context;

import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.interfaceset.RetryCounter;
import com.iyangcong.reader.model.IycResponse;
import com.orhanobut.logger.Logger;

import java.util.ArrayDeque;

import okhttp3.Call;
import okhttp3.Response;
/**
 * author:WuZepeng </br>
 * time:2018-05-03 10:52 </br>
 * desc:PostTask的管理者。 完成两个任务：将<code>PostTask</>添加到队列中以及消费队列中的<code>PostTask</>
 * 在消费PostTask失败的时候会进行多次重试,最多重试次数由PostTask.MAX_RETRY_TIMES决定</br>
 */
public class PostTaskManager {
	private ArrayDeque<PostTask> mTasks;
	private ArrayDeque<JsonCallback<IycResponse>> mCallback;
	public PostTaskManager() {
		mTasks = new ArrayDeque<>();
		mCallback = new ArrayDeque<>();
	}

	public void addTask(PostTask task,JsonCallback<IycResponse> callback){
		if(task == null || callback == null)
			throw new IllegalArgumentException("task和callback均不可以为null");
		mTasks.add(task);
		mCallback.add(callback);
	}
	public void execute(final Context context, final OnPostTaskFinishedListener listener){
		if(mTasks == null ){
			return;
		}else if(mTasks.isEmpty()){
			listener.onTaskFinished();
			return;
		}
		final PostTask current = mTasks.pollFirst();
		final JsonCallback<IycResponse> callback = mCallback.pollFirst();
		if(current == null||callback == null){
			if(!mTasks.isEmpty()&&!mCallback.isEmpty())
				execute(context,listener);
			return;
		}
		current.executeSingleTask(new JsonCallback<IycResponse<Object>>(context) {
			@Override
			public void onSuccess(IycResponse<Object> iycResponse, Call call, Response response) {
				if(callback != null){
					callback.onSuccess(iycResponse,call,response);
				}
				if(!mTasks.isEmpty()){
					execute(context,listener);
				}

			}

			@Override
			public void onError(Call call, Response response, Exception e) {
				super.onError(call, response, e);
				Logger.e("wzp postTaskError:%s",e.getMessage());
				callback.onError(call,response,e);
				if(current.canRetryOnError(context, e))
					retry(current,context,listener);
			}

			@Override
			public void parseError(Call call, Exception e) {
				super.parseError(call, e);
				retry(current,context,listener);
			}
		});
	}

	public void retry(PostTask current,Context context,OnPostTaskFinishedListener listener){
		int retryCount = current.getRetryCounter();
		if(retryCount< RetryCounter.MAX_RETRY_TIMES){
			current.setRetryCounter(retryCount+1);
			execute(context,listener);
		}else{
			if(!mTasks.isEmpty())
				mTasks.removeFirst();
		}
	}
	public static abstract class OnPostTaskFinishedListener {
		public abstract void onTaskFinished();
	}
}
