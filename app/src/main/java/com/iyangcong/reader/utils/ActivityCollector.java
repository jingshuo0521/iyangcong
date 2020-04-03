package com.iyangcong.reader.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {
    public static List<Activity> activities=new ArrayList<Activity>();
    public static void addActivity(Activity activity){
        activities.add(activity);//添加activity
    }
    public static void removeActivity(Activity activity){
        activities.remove(activity);//删除添加activity
    }

    //结束所有的activity
    public static void finishAll(){
        for(Activity activity:activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}