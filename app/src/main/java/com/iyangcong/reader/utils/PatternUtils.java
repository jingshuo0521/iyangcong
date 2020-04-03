package com.iyangcong.reader.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by WuZepeng on 2017-04-10.
 */

public class PatternUtils {

	private static String numberic = "[0-9]*+";
	private static String isLetter = "[a-zA-Z]+";
	private static String isChineseCharacter = "[\\u4e00-\\u9fa5]+";
	private static Pattern pattern;

	public static boolean isNumberic(String inputString){
		return matcher(inputString,numberic);
	}

	public static boolean isLetter(String inputString){
		return matcher(inputString,isLetter);
	}

	public static boolean isChinese(String inputString){
		return matcher(inputString,isChineseCharacter);
	}

	public static boolean containChinese(String inputString){
		return contains(inputString,isChineseCharacter);
	}

	public static boolean containLetter(String inputString){
		return contains(inputString,isLetter);
	}

	public static boolean contains(String inputString,String containType){
		pattern = Pattern.compile(containType);
		Matcher m = pattern.matcher(inputString);
		return m.find();
	}

	public static boolean matcher(String inputString, String matchType){
		pattern = Pattern.compile(matchType);
		Matcher m = pattern.matcher(inputString);
		return m.matches();
	}
}
