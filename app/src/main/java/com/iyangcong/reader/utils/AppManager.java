package com.iyangcong.reader.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WuZepeng on 2017-03-29.
 * 一个activity管理类
 */

public class AppManager {

	private static List<Activity> activityList;
	private AppManager(){}

	private static class SingletonHolder {
		private static final AppManager sInstance = new AppManager();
	}

	public static AppManager getAppManager(){
		return SingletonHolder.sInstance;
	}

	/**
	 * 添加某个activity
	 * @param activity
	 */
	public void addActivity(Activity activity){
		if(activityList == null){
			activityList = new ArrayList<>();
		}
		Logger.i("AppManagerTest:"+ " size:" + activityList.size()+ " list:" + activityList.toString());
		activityList.add(activity);
	}

	/**
	 * 销毁最后一个activity
	 */
	public void finishActivity(){
		if(activityList.size()>0){
			Activity lastActivity = activityList.get(activityList.size()-1);
			finishActivity(lastActivity);
		}
	}

	/**
	 * 销毁某个activity
	 * @param activity
	 */
	public void finishActivity(Activity activity){
		if(activity != null){
			activityList.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 销毁某个activity
	 * @param klass
	 */
	public void finishActivity(Class<?> klass){
		for (Activity activity:activityList){
			if(activity.getClass().equals(klass))
				finishActivity(activity);
		}
	}

	/**
	 * 这个方法最好放在要销毁的最后一个activity的后面的activity里面，不要放在to里面；
	 * @param from 要销毁的第一个activity
	 * @param to 要销毁的最后一个activity
	 * by wzp
	 */
	public void finishActivity(Class<?> from,Class<?> to){
		int fromIndex = 0,toIndex = 0;
		for (int i = 0; i < activityList.size(); i++) {
			if(activityList.get(i).getClass().equals(from)){
				fromIndex = i;
			}
			if(activityList.get(i).getClass().equals(to)){
				toIndex = i;
			}
		}
		Logger.i("AppManagerTest:" + " fromIndex=" +fromIndex +" toIndex:"+ toIndex);
		if(fromIndex > 0 && toIndex > 0 && toIndex >= fromIndex){
			for (int i = fromIndex; i <= toIndex; toIndex--) {
				Logger.i("AppManagerTest:" + activityList.get(i).getClass().getSimpleName());
				finishActivity(activityList.get(i));
			}
		}
	}
	/**
	 * 销毁所有activity
	 */
	public void finishAllActivity(){
		for(Activity activity:activityList){
			if(null != activity){
				activity.finish();
			}
		}
		activityList.clear();
	}

	public void AppExit(Context context){
		try {
			finishAllActivity();
			ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
			activityManager.killBackgroundProcesses(context.getPackageName());
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
