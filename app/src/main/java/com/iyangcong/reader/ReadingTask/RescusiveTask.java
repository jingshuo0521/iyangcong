package com.iyangcong.reader.ReadingTask;

import android.content.Context;

import com.iyangcong.reader.bean.ReadingDataBean;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.ReadingDataDao;
import com.iyangcong.reader.utils.BookInfoUtils;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.DateUtils;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.orhanobut.logger.Logger;

import org.geometerplus.android.fbreader.FBReader;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.network.urlInfo.BookUrlInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;
import java.util.UUID;

import io.github.xudaojie.qrcodelib.zxing.decoding.Intents;

public class RescusiveTask extends TimerTask implements DataCollectTask<ReadingDataBean> {
	private Context mContext;
	private volatile ReadingDataDao mDao;
	private volatile ReadingDataBean mBean;
	private final static ThreadLocal<SimpleDateFormat> tl = new ThreadLocal<>();
	private final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private FBReaderApp mApp;
	public RescusiveTask(Context context, FBReaderApp app, ReadingDataBean bean) {
		mContext = context;
		mApp = app;
		mBean = bean;
	}

	public void updateReadingData(ReadingDataBean bean){
		mBean = bean;
	}

	public ReadingDataBean getBean(){
		return mBean;
	}
	@Override
	public boolean checkExecutParams(ReadingDataBean readingDataBean) {
		return mContext != null&&mApp != null&&readingDataBean != null;
	}

	@Override
	public void execute(ReadingDataBean readingDataBean) {

		if(!SingleTimer.canRecord){
			SingleTimer.startTime = System.currentTimeMillis();
			Logger.e("定时任务不执行记录");
		}else{
			readingDataBean.setUuid(UUID.randomUUID().toString());
			readingDataBean.setLanguageType(SharedPreferenceUtil.getInstance().getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, -1));
			SingleTimer.endTime = System.currentTimeMillis();
			readingDataBean.setStartTime(formatDate(SingleTimer.startTime,DATE_PATTERN));
			readingDataBean.setEndTime(formatDate(SingleTimer.endTime,DATE_PATTERN));
			Logger.e("记录时间 startTime:%s 记录时间endTime:%d", SingleTimer.startTime,SingleTimer.endTime);
			SingleTimer.startTime = SingleTimer.endTime;
			SingleTimer.endTime = SingleTimer.INITIAL_TIME;
			readingDataBean.setEndParagraphId(BookInfoUtils.getAbsoluteParagraphId(mApp));
			SingleTimer.canRecord = false;
			Logger.e("wzp执行定时任务");
			if(SingleTimer.startTime == SingleTimer.INITIAL_TIME)
				return;
			mDao = new ReadingDataDao(DatabaseHelper.getHelper(mContext));
			mDao.add(readingDataBean);
			readingDataBean.setStartParagraphId(readingDataBean.getEndParagraphId());
		}

	}

	private String formatDate(long time,String regex){
		SimpleDateFormat format = tl.get();
		if(format == null){
			format = new SimpleDateFormat();
			tl.set(format);
		}
		format.applyPattern(regex);
		return format.format(new Date(time));
	}
	@Override
	public boolean hasExecuted() {
		return false;
	}

	@Override
	public void run() {
		if(checkExecutParams(mBean)){
			execute(mBean);
		}
	}
}
