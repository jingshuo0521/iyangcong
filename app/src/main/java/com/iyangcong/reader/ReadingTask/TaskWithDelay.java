package com.iyangcong.reader.ReadingTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.iyangcong.reader.bean.ReadingRecorder;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.interfaceset.DeviceType;
import com.iyangcong.reader.interfaceset.RetryCounter;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.service.PostReadingRecorderService;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.DateUtils;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.iyangcong.reader.utils.postTask.PostTaskerCannotRetry;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Response;

public class TaskWithDelay extends TimerTask implements DataCollectTask<ReadingRecorder>,Cloneable{
	private Context mContext;
	private ReadingRecorder mRecorder;
	private volatile boolean isExecuted;
	private int retry = 0;
	private long endTime = -1;//考虑到切到后台有可能被回收的问题，设置了这个
	public TaskWithDelay(Context context, ReadingRecorder recorder) {
		mContext = context;
		mRecorder = recorder;
	}

	@Override
	public void execute(final ReadingRecorder readingRecorder) {

		final SharedPreferenceUtil sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
		String startTime = sharedPreferenceUtil.getString(SharedPreferenceUtil.READING_RECORD_LAST_TIME, "");
		long readingRecordlogId = sharedPreferenceUtil.getInt(SharedPreferenceUtil.READING_RECORD_LODID, -1);
		if (readingRecordlogId > -1 && !TextUtils.isEmpty(startTime)) {
			String endTimeStr = DateUtils.getSystemDateFormat("yyyy-MM-dd HH:mm:ss");
			long endDate = endTime==-1?System.currentTimeMillis():endTime;
			endTime = -1;
			long readingLong = (endDate - DateUtils.StringToDate(startTime, "yyyy-MM-dd HH:mm:ss").getTime()) / 1000;
			(OkGo.get(Urls.ReadingOrbitRecordEndURL))
					.params("bookid", readingRecorder.getBookId())
					.params("endsegmentid", readingRecorder.getSegmentId() + "")
					.params("language", readingRecorder.getLanguageType())
					.params("readingrecordlogid", readingRecordlogId)
					.params("userid", CommonUtil.getUserId())
					.params("start_time", startTime)
					.params("end_time", endTimeStr)
					.params("deviceType", DeviceType.ANDROID_3)
					.params("reading_long", readingLong)
					.params("chapterid", readingRecorder.getChapterId())
					.params("segmentStr", readingRecorder.getSegmentStr())
					.params("terminal", "1")
					.execute(new StringCallback() {
						@Override
						public void onSuccess(String s, Call call, Response response) {
							sharedPreferenceUtil.putInt(SharedPreferenceUtil.READING_RECORD_LODID,-1);
						}

						@Override
						public void onError(Call call, Response response, Exception e) {
							super.onError(call, response, e);
						}

						@Override
						public void parseError(Call call, Exception e) {
							super.parseError(call, e);
							if(e instanceof IOException || "java.io.IOException: Canceled".contains(e.getMessage())){
								if(retry++< RetryCounter.MAX_RETRY_TIMES){
									execute(readingRecorder);
								}
							}
						}
					});
		}
	}

	@Override
	public boolean hasExecuted() {
		return isExecuted;
	}

	@Override
	public boolean checkExecutParams(ReadingRecorder readingRecorder) {
		return readingRecorder.getSegmentId()>=0
				&&readingRecorder.getChapterId()>=0
				&&readingRecorder.getBookId()>=0
				&&!TextUtils.isEmpty(readingRecorder.getSegmentStr());
	}

	@Override
	public void run() {
		if(mContext != null && mRecorder != null){
			if(checkExecutParams(mRecorder)){
				isExecuted = true;
				execute(mRecorder);
			}else{
				throw new IllegalArgumentException("mRecorder参数有误，请检查参数");
			}
		}
	}

	@Override
	public TaskWithDelay clone() throws CloneNotSupportedException {
		TaskWithDelay newDelay = new TaskWithDelay(mContext,mRecorder);
		newDelay.isExecuted = false;
		return newDelay;
	}
	public void setEndTime(long time){
		if(time>0)
			endTime = time;
	}
	public ReadingRecorder getRecorder(){
		return mRecorder;
	}
}
