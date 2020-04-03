package com.iyangcong.reader.ReadingTask;

import android.content.Context;
import android.text.TextUtils;

import com.iyangcong.reader.bean.ReadingRecordLog;
import com.iyangcong.reader.bean.ReadingRecorder;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.interfaceset.RetryCounter;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.DateUtils;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import java.io.IOException;
import java.util.TimerTask;
import okhttp3.Call;
import okhttp3.Response;

public class TaskWithoutDelay extends TimerTask implements DataCollectTask<ReadingRecorder>,Cloneable{
	private Context mContext;
	private ReadingRecorder mRecorder;
	private volatile boolean hasExecuted;
	private int retry = 0;
	public TaskWithoutDelay(Context context, ReadingRecorder readingRecorder) {
		mContext = context;
		mRecorder = readingRecorder;
	}

	@Override
	public void execute(final ReadingRecorder readingRecorder) {
		SharedPreferenceUtil.getInstance().putString(SharedPreferenceUtil.READING_RECORD_LAST_TIME, DateUtils.getSystemDateFormat("yyyy-MM-dd HH:mm:ss"));
		OkGo.get(Urls.ReadingOrbitRecordBeginURL)
		.tag(this)
		.params("bookid", readingRecorder.getBookId())
		.params("language", readingRecorder.getLanguageType())
		.params("semesterid", readingRecorder.getSemesterId())
		.params("startsegmentid", readingRecorder.getSegmentId())
		.params("userid", CommonUtil.getUserId())
		.params("segmentStr", readingRecorder.getSegmentStr())
		.params("chapterid", readingRecorder.getChapterId())
		.execute(new JsonCallback<IycResponse<ReadingRecordLog>>(mContext) {
			@Override
			public void onSuccess(IycResponse<ReadingRecordLog> readingRecordLogIycResponse, Call call, Response response) {
				SharedPreferenceUtil.getInstance().putInt(SharedPreferenceUtil.READING_RECORD_LODID, readingRecordLogIycResponse.getData().getReadingrecordlogid());
			}

			@Override
			public void onError(Call call, Response response, Exception e) {

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

	@Override
	public boolean checkExecutParams(ReadingRecorder readingRecorder) {
		return 	readingRecorder!=null
				&& readingRecorder.getBookId()>=0
				&& readingRecorder.getLanguageType()>=0
				&& readingRecorder.getSegmentId()>=0
				&& readingRecorder.getChapterId()>=0
				&& !TextUtils.isEmpty(readingRecorder.getSegmentStr());
	}

	public boolean hasExecuted(){
		return hasExecuted;
	}

	@Override
	public void run() {
		if(mContext != null&&checkExecutParams(mRecorder)){
			if(checkExecutParams(mRecorder)){
				hasExecuted = true;
				execute(mRecorder);
			}else{
				throw new UnsupportedOperationException("mRecorder参数有误，请检查");
			}
		}
	}

	public boolean cancel(){
		OkGo.getInstance().cancelTag(this);
		return true;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
