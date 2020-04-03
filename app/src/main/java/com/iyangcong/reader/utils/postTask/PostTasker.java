package com.iyangcong.reader.utils.postTask;

import android.content.Context;

import com.iyangcong.reader.callback.JsonCallback;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;

import java.util.HashMap;


/**
 * author:WuZepeng </br>
 * time:2018-05-03 10:50 </br>
 * desc:一个上传任务的实际承载者，实现<code>PostTask</code>抽象类
 * <code>PostTaskManager</code>进行管理</br>
 */
public class PostTasker extends PostTask {
	protected GetRequest mGetRequest;
	protected PostRequest mPostRequest;
	protected byte method = Method.INITIAL;
	@Override
	public void buildTaskRequest(byte method,String url, String... keyValuePairs) {
		int length = keyValuePairs.length;
		if((length & 0x01) ==1){
			throw new IllegalArgumentException("输入的参数个数不对");
		}
		HashMap<String,String> params = new HashMap<>();
		for(int index = 0;index<length;index=index+2){
			params.put(keyValuePairs[index],keyValuePairs[index+1]);
		}
		this.method = method;
		switch (method){
			case Method.GET:
				mGetRequest = OkGo.get(url).params(params);
				break;
			case Method.POST:
				mPostRequest = OkGo.post(url).params(params);
				break;
		}
	}

	@Override
	protected  <T> void executeSingleTask(JsonCallback<T> callback) {
		if(method == Method.GET&&mGetRequest !=null)
			mGetRequest.execute(callback);
		else if(method == Method.POST&&mPostRequest != null){
			mPostRequest.execute(callback);
		}
		else{
			throw new IllegalArgumentException("请先调用buildTaskRequest，再调用executeSingleTask");
		}
	}

	@Override
	public boolean canRetryOnError(Context context, Exception e) {
		return true;
	}
}
