package com.iyangcong.reader.utils;

import android.util.Log;

import com.orhanobut.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
        
        public static int Hour(Date time){
            SimpleDateFormat st=new SimpleDateFormat("yyyyMMddHH");
            return Integer.parseInt(st.format(time));
        }

        public static Date StringToDate(String s, String regx){
//            Date time=new Date();
            SimpleDateFormat sd=new SimpleDateFormat(regx);
            try{
                 return sd.parse(s);
            }
            catch (Exception e) {
                Log.e("DateUtils","输入的日期有误！");
				return new Date();
            }
        }
        
        public  static String getCommentDate(String regx){
            SimpleDateFormat format = new SimpleDateFormat(regx);
            String date = format.format(new Date());
            return date;
        }
        
        public static String getClientDateFormat(String serverDateFormat){
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date;
			String result=null;
			try {
				if(serverDateFormat!=null){
					date = sdf.parse(serverDateFormat);
					sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					result = sdf.format(date);
					int b=0;
					int a=b;
				}
				
				return result;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
        }

        public static String getSystemDateFormat(String DateFormat){
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat format = new SimpleDateFormat(DateFormat);
            return format.format(date);
        }

	/**
	 * 将后台传过来的字符串进行解析，转化为Date
	 * @param strTmp 改字符串的格式必须是这样：  yyyyy-mm-dd hh:mm:ss  后台会在yyyy前面加个空格，一定要注意！！！！
	 * @return
	 */
	public static Date getDate(String strTmp){
		String[] strings = strTmp.split(" ");
		//如果后台改了时间格式，去掉了yyyy之前的空格，那length就限制为2；
		if(strings.length != 3){
			throw new IllegalArgumentException("时间格式错误");
		}
		String[] former = strings[1].split("-");
		String[] latter = strings[2].split(":");
		Calendar calendar = Calendar.getInstance();
		if(former.length != 3){
			throw new IllegalArgumentException("时间格式 年月日解析错误");
		}
		Integer year = Integer.parseInt(former[0]);
		Integer month = Integer.parseInt(former[1]);
		Integer date = Integer.parseInt(former[2]);
		calendar.set(Calendar.YEAR,year);
		calendar.set(Calendar.MONTH,month>0?month-1:0);
		calendar.set(Calendar.DAY_OF_MONTH,date);
		if(latter.length < 2 && latter.length > 3){
			throw new IllegalArgumentException("时间格式 时分秒解析错误");
		}
		Integer hour = Integer.parseInt(latter[0]);
		Integer minite = Integer.parseInt(latter[1]);
		calendar.set(Calendar.HOUR,hour);
		calendar.set(Calendar.MINUTE,minite);
		if(latter.length == 3){
			Integer second = Integer.parseInt(latter[2]);
			calendar.set(Calendar.SECOND,second);
		}
		return calendar.getTime();
	}

	public static boolean compareNow(Date end){
		Date now = new Date();
		return end.compareTo(now) > 0;
	}

	public static Date formatDate(String temp){
		long time = Long.parseLong(temp);
		return new Date(time);
	}

	public static int compare(Date now,Date end){
		return now.compareTo(end);
	}

	public static long compareLastUpdateTime(String lastUpdateTime){
//		String time1 = "2017-06-22 09:57:10";
//		String time2 = "2017-06-22 10:57:10";
		Date now = getEffectiveDate(new Date());
//		Date now = StringToDate(time1,"yyyy-mm-dd hh:mm:ss");
		Date last = StringToDate(lastUpdateTime,"yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Logger.i("wzp now :" + simpleDateFormat.format(now)+ "	last:" + simpleDateFormat.format(last));
		Logger.i("wzp 时间差：" + (now.getTime()- last.getTime()));
		return now.getTime() - last.getTime();
	}

	public static Date getEffectiveDate(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return df.parse(df.format(date));
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 时间戳转字符串
	 * @param time
	 * @param format
	 * @return
	 */
	public static String timeStamp2Date(long time, String format) {
		if (format == null || format.isEmpty()) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(time));
	}
}
