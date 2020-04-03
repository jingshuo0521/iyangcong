package org.geometerplus.fbreader.formats.oeb.function;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.iyangcong.reader.EpubLibrary;
//import com.iyangcong.reader.utils.BookDecryptQueryImpl;
import com.iyangcong.reader.epub.EpubProcessResult;
import com.iyangcong.reader.utils.CommonUtil;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.PriorityQueue;
import java.util.WeakHashMap;

/**
 * Created by WuZepeng on 2017-12-20.
 * 用于Epub加解密的Service
 */

public class EpubService extends Service{


	private String basePath = null;
	private String bookZipedDir = null;
	private EpubBinder mBinder;
	//	private static boolean isFinished;
	private PriorityQueue<UnZipTask> mQueue = new PriorityQueue<>();

	@Override
	public void onCreate() {
		super.onCreate();
		mBinder = new EpubBinder();
		initPath();
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private void initPath(){
		if(basePath == null){
			StringBuilder sb = new StringBuilder();
			basePath = sb.append(CommonUtil.getBooksDir()).toString();
		}
		if(bookZipedDir == null){
			bookZipedDir = basePath;
		}
	}


	public void addTask(String originalPath, String zippedpath, long bookId) {
		// 解压成epub文件,然后读取内容
		UnZipTask unZipTask = new EpubUnZipTask(getApplicationContext(),originalPath,zippedpath,bookId);
		if(!mQueue.contains(unZipTask)){
			mQueue.add(unZipTask);
			executeTask();
		}
	}

	public void executeTask(){
		if(!mQueue.isEmpty()){
			Logger.e("wzp 队列：%d%n",mQueue.size());
			UnZipTask earlistTask = mQueue.poll();
			if(earlistTask != null){
				earlistTask.execute();
			}
		}else{
			Logger.e("wzp 队列为空");
		}
	}

	public class EpubBinder extends EpubLibrary.Stub{

		@Override
		public boolean isFinished() throws RemoteException {
			return false;
		}

		@Override
		public void startEpubDecodeAndEncode(long bookId) throws RemoteException {
			if(!android.text.TextUtils.isEmpty(basePath)&&bookId != 0){
				String tmpPath = new StringBuffer(basePath).append("/").append(bookId).append(".zip").toString();
				addTask(tmpPath, bookZipedDir, bookId);
			}
		}
	}

	private class EpubUnZipTask extends UnZipTask{
		WeakReference<EpubService> reference;
		private long startTime;

		public EpubUnZipTask(Context context, String origalPath, String destinationPath, long bookId){
			super(context,origalPath,destinationPath,bookId);
			reference = new WeakReference<EpubService>(EpubService.this);
		}

		@Override
		protected Void doInBackground(Void... params) {
			startTime = System.currentTimeMillis();
			return super.doInBackground(params);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(reference.get() != null){
				executeTask();
				if(mQueue.isEmpty()){
					long endTime = System.currentTimeMillis();
					long length = endTime - startTime;
					Logger.e("解密服务开始时间:%d%n解密服务结束时间：%d%n持续时间：%d%n",startTime,endTime,length);
					Logger.e("wzp isSuccess:%s%nbookId:%d%nmessage:%s%n",isSuccessful+"",mBookId,mMessage);
					EventBus.getDefault().post(new EpubProcessResult(mBookId,isSuccessful,mMessage));
				}
			}
		}

		@Override
		public boolean equals(Object obj) {
			return obj != null && obj instanceof EpubUnZipTask && mBookId == ((EpubUnZipTask)obj).mBookId;
		}
	};
}
