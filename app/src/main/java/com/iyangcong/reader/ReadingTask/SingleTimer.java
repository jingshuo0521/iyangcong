package com.iyangcong.reader.ReadingTask;

import android.content.Context;
import android.util.Log;

import com.iyangcong.reader.bean.ReadingDataBean;

import org.geometerplus.fbreader.fbreader.FBReaderApp;

import java.util.Timer;
import java.util.TimerTask;

public class SingleTimer {
	private Timer mTimer;
	private DataCollectTask<?> mTask;
	public static final long TEN_MINUTES = 5*60*1000;
	public static final long NOW = 0;
	public static final long INITIAL_TIME = -1;
	public static long startTime = INITIAL_TIME;
	public static long endTime = INITIAL_TIME;
	public volatile static boolean canRecord = false;

	private SingleTimer(){
		mTimer = new Timer();
	}

	private static class Holder{
		private final static SingleTimer sIntance = new SingleTimer();
	}

	public static SingleTimer getInstance(){
		return Holder.sIntance;
	}
	public void stopeRescusiveTask(){
		if(mTask instanceof TimerTask){
			((TimerTask) mTask).cancel();
		}
	}
	public void executeEndTask(Context context, FBReaderApp app, ReadingDataBean bean){
		mTask = new RescusiveTask(context,app,bean);
		new Thread((TimerTask)mTask).start();
	}
	public boolean executeRescusively(Context context, FBReaderApp app, ReadingDataBean bean, long delay, long period){
		if(mTask == null){
			mTask = new RescusiveTask(context,app,bean);
			Log.e("PapaTimeTask",":is new Task");
		}else if(mTask instanceof RescusiveTask){
//			((RescusiveTask) mTask).updateReadingData(bean);
			((RescusiveTask) mTask).cancel();
			mTask = null;
			mTask = new RescusiveTask(context,app,bean);
			Log.e("PapaTimeTask",":is old Task");
		}
		if(mTask instanceof TimerTask){
			//BUG #2505
			//2019/5/29 修复成功
			//Timer构造同一个定时任务只能构造一次，所以需要把旧的定时任务取消掉，重新schedule
			//取消定时任务不是取消整个Timer任务类对象，不然每次都会申请新的timer对象，造成浪费
			//只需每次取消之前的task任务，由于task为一次性，因此对于同一个timer对象而言不可复用，取消掉之后创建新的task对象
			//并且每次开始前，需调用purge方法，将task队列中标记取消的任务剔除掉
//			mTimer.cancel();
			int deleteNum = mTimer.purge();
			Log.e("PapaTimeTask","delete sum:"+deleteNum);
//			mTimer = null;
//			mTimer = new Timer();
			if(period>0)
				mTimer.schedule((TimerTask)mTask,delay,period);
			else{
				mTimer.schedule((TimerTask)mTask,delay);
			}
			return true;
		}
		return false;
	}

	public boolean executeRightNow(DataCollectTask<?> task){
		mTask = task;
		if(task instanceof TimerTask)
			mTimer.schedule((TimerTask) task,0);
		return true;
	}

	public boolean execute(TaskWithDelay task, long time){
		if(task == null)
			return false;
		mTask = task;
		mTimer.schedule(task,time>=0?time:0);
		return true;
	}

	public boolean delayExecute(long time){
		if(mTask instanceof TaskWithDelay){
			try {
				TaskWithDelay tmpDelay = ((TaskWithDelay) mTask).clone();
				if (cancelCurrentTask()){
					execute(tmpDelay,time);
					return true;
				}
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				return false;
			}

		}
		return false;
	}

	public boolean hasExecuteTask(){
		return mTask!=null&&mTask.hasExecuted();
	}

	public boolean hasExecuteDelayTask(){
		return mTask != null && mTask instanceof TaskWithDelay && mTask.hasExecuted();
	}

	public DataCollectTask<?> getTask(){
		return mTask;
	}

	public boolean cancelCurrentTask(){
		if(mTask != null && mTask instanceof TimerTask){
			((TimerTask) mTask).cancel();
			return true;
		}
		return false;
	}

	public boolean stop(){
		mTimer.cancel();
		mTimer.purge();
		return true;
	}
}
