package com.iyangcong.reader.utils;

import android.content.Context;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.ui.IYangCongToast;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import me.drakeet.support.toast.ToastCompat;

/**
 * Created by WuZepeng on 2017-03-27.
 */

public class NotNullUtils{

	public static boolean isNull(Context context,String string,String tips){
		if(null == string || "".equals(string)){
			if(null != tips && !"".equals(tips)){
				ToastCompat.makeText(context, tips, Toast.LENGTH_SHORT).show();
			}
			return true;
		}
		return false;
	}

	public static boolean isNull(Context context,String string){
		return isNull(context,string,context.getString(R.string.must_not_null));
	}

	public static boolean isNull(List<?> list){
		if(null == list || list.size() == 0){
			return true;
		}
		return false;
	}

	public static boolean isNull(Object object){
		return object == null;
	}

	public static <K,V> boolean isNull(Map<K,V> map){
		return map == null || map.isEmpty();
	}
	/**
	 * 如果字符串是null,返回空字符串；
	 * @param str
	 * @return
	 */
	public static String tranforNull(String str){
		return str == null?"":str;
	}
}
