package com.iyangcong.reader.utils;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ljw on 2017/3/18.
 */

public class StringUtils {
    private static final String wordsCountPattern = "[\\u4E00-\\u9FFF]|(\\b\\w+\\b| )";
    private static final String enligshWordChar = "(\\b\\w+\\b)";
    private static final String TELEPHONE_NEW = "(^1\\d{10}$)";
    public static final String TELEPHONE_OLD = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(14[0-8]))\\d{8}$";
    /*
     * 判断是否为整数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
    /**
     * 32 MD5加密密码
     *
     * @param str
     * @return
     */
    public static String MD5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuilder hexValue = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    public static String listToString(List<String> list) {
        if (list == null) {
            return "";
        } else {
            String result = "";
            for (String s : list) {
                result += s + "\n";
            }
            return result;
        }
    }

    public static SpannableString setTextColor(String selectString, String text) {

        if (text != null && text.length() >= selectString.length()&&text.toLowerCase().contains(selectString.toLowerCase())) {
            int start = LocationString(0, text, selectString);
            int end = start + selectString.length();
            SpannableString msp = new SpannableString(text);
            msp.setSpan(new ForegroundColorSpan(Color.parseColor("#ee4d22")),
                    start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置前景色
            return msp;
        }
        return null;
    }

    public static int LocationString(int startPos, String ori, String des) {
        ori = ori.toLowerCase();
        des = des.toLowerCase();
        if (ori.contains(des)) {
            int start = ori.indexOf(des);//查找一个字符串中，第一次出现指定字符串的位置
            int end = start + des.length();
            int len = ori.length();
            if (len == end) {
                return start;
            } else {
                char a = ori.charAt(end);//检索ori中的end位置的字符
                if (a >= 'a' && a <= 'z' || a >= 'A' && a <= 'Z') {
                    String str = ori.substring(end);//返回一个新的字符串，它是此字符串的一个子字符串。该子字符串始于指定索引处的字符，一直到此字符串末尾
                    return LocationString(end, str, des);
                } else {
                    return start + startPos;
                }
            }

        }
        return 0;
    }

    /**
     * @return
     */
    public static String mapToString(HashMap<String, String> map) {
        if (map == null) {
            return null;
        }
        Map.Entry entry;
        String result = "";
        for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
            entry = (Map.Entry) stringStringEntry;
            result += entry.getKey().toString() + "：" + entry.getValue() + "\n";
        }
        return result;
    }
    public static int getFirstEnWordChar(String input){
        if(null == input || "".equals(input)){
            return 0;
        }
        Pattern pattern = Pattern.compile(enligshWordChar);
        Matcher matcher = pattern.matcher(input);
        int count;
        for(count=0;matcher.find();count++){
            System.out.println(matcher.group());
        }
        return count;
    }
    public static int getStringLength(String input){
        if(null == input || "".equals(input)){
            return 0;
        }
        Pattern pattern = Pattern.compile(wordsCountPattern);
        Matcher matcher = pattern.matcher(input);
        int count;
        for(count=0;matcher.find();count++){
            System.out.println(matcher.group());
        }
        return count;
    }
    /**
     * 判断手机格式是否正确
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        //        Pattern p = Pattern
//                .compile("^((17[0-9])|(13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Pattern p = Pattern.compile(TELEPHONE_NEW);//add  147 phone number
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 正则表达式校验邮箱
     *
     * @param emaile 待匹配的邮箱
     * @return 匹配成功返回true 否则返回false;
     */
    public static boolean checkEmaile(String emaile) {
        String RULE_EMAIL = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
        //正则表达式的模式
        Pattern p = Pattern.compile(RULE_EMAIL);
        //正则表达式的匹配器
        Matcher m = p.matcher(emaile);
        //进行正则匹配
        return m.matches();
    }

    public static boolean isOneWord(String words) {
        String[] arrStr = words.split(" ");
        return arrStr.length <= 1;
    }

    public static boolean isOneSentence(String words) {
        if (SharedPreferenceUtil.getInstance().getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 0) == 1) {
            String[] arrStr = words.split("。");
            return arrStr.length <= 1;
        } else {
            String[] arrStr = words.split("\\.");
            return arrStr.length <= 1;
        }
    }

    public static boolean isOneParagraph(String words) {
        String[] arrStr = words.split("\\n");
        return arrStr.length <= 1;
    }

    public static boolean hasZh(String str) {
        int count = 0;
        String regEx = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                count = count + 1;
            }
        }
        return count > 0;
    }

    /**
     * 删除特殊字符
     *
     * @param str
     * @return
     */
    public static String deleSymbol(String str) {
        SharedPreferenceUtil sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
        if (sharedPreferenceUtil.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 0) == 2) {
            String regEx = "([`~!@#$%^&*()+=|{}:;,\\\\[\\\\].<>/?！￥…（）—【】‘；：”“’。，、？\\\"]+'*)";
//            String regEx = "[`~!@#$%^&*()+=|{}:;,\\[\\].<>/?！￥…（）—【】‘；：”“’。，、？\"]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(str);
            String string = m.replaceAll("").trim();
            for(int index = 0;index<string.length();index++){
                if(string.charAt(index) != "'".charAt(0)){
                    if(index == 0)
                        return string;
                    else
                        return string.substring(index);
                }
            }
            return string;
        }
        return str;
    }

    /**
     * 该函数判断一个字符串是否包含标点符号（中文英文标点符号）。
     * 原理是原字符串做一次清洗，清洗掉所有标点符号。
     * 此时，如果原字符串包含标点符号，那么清洗后的长度和原字符串长度不同。返回true。
     * 如果原字符串未包含标点符号，则清洗后长度不变。返回false。
     * @param c
     * @return
     */
    public static boolean checkPunctuation(char c) {
        boolean b = false;
        String s = c+"";
        String tmp = s;
        tmp = tmp.replaceAll("\\p{P}", "");
        if (s.length() != tmp.length()) {
            b = true;
        }
        return b;
    }

    /**
     * 判断字符串是否包含中文
     * @param text
     * @return
     */
    public static boolean checkChinese(String text) {

//        if(text.matches("[\u4E00-\u9FA5]+")){
//            return true;
//        }else {
//            return false;
//        }

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(text);
        if (m.find()) {
            return true;
        }
        return false;

    }


    /**
     * 定义script的正则表达式
     */
    private static final String REGEX_SCRIPT = "<script[^>]*?>[\\s\\S]*?<\\/script>";
    /**
     * 定义style的正则表达式
     */
    private static final String REGEX_STYLE = "<style[^>]*?>[\\s\\S]*?<\\/style>";
    /**
     * 定义HTML标签的正则表达式
     */
    private static final String REGEX_HTML = "<[^>]+>";
    /**
     * 定义空格回车换行符
     */
    private static final String REGEX_SPACE = "\\s*|\t|\r|\n";

    /**
     * 去除文本中的html标识
     * @param htmlStr
     * @return
     */
    public static String delHTMLTag(String htmlStr) {
        if(htmlStr==null){
            return "";
        }
        // 过滤script标签
        Pattern p_script = Pattern.compile(REGEX_SCRIPT, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll("");
        // 过滤style标签
        Pattern p_style = Pattern.compile(REGEX_STYLE, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll("");
        // 过滤html标签
        Pattern p_html = Pattern.compile(REGEX_HTML, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll("");
        // 过滤空格回车标签
        Pattern p_space = Pattern.compile(REGEX_SPACE, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(htmlStr);
        htmlStr = m_space.replaceAll("");
        return htmlStr.trim(); // 返回文本字符串
    }

}
