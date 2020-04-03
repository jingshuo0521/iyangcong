package org.geometerplus.android.util;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 作者：chenkeliang on 2019-10-25 22:56
 * 描述：
 */
public class GetProtocol {
    private final  static String HTTP= "http://";
    private final  static  String HTTPS =  "https://";
    private String newurl;

    //判断协议 能连接上则协议正确
    public String getProtocol(String url) {
        newurl = clearUrl(url);
        url = HTTP + newurl;
        if (exists(url)) {
            return url;
        } else {
            url = HTTPS + newurl;
            if (exists(url)) {
                return url;
            } else {
                return null;
            }
        }

    }
    //清除URL里多余的符号
    private  String clearUrl(String url) {
        if (url.contains(HTTP)) {
            url = url.substring(url.lastIndexOf(HTTP) + HTTP.length());
            for (int i = 0; i < url.length(); i++) {
                if (url.charAt(i) == '/' || url.charAt(i) == '.'
                        || url.charAt(i) == '\\') {
                } else {
                    url = url.substring(i);
                    return url;
                }
            }
        } else if (url.contains(HTTPS)) {
            url = url.substring(url.lastIndexOf(HTTPS) + HTTPS.length());
            for (int i = 0; i < url.length(); i++) {
                if (url.charAt(i) == '/' || url.charAt(i) == '.'
                        || url.charAt(i) == '\\') {
                } else {
                    url = url.substring(i);
                    return url;
                }
            }
        }else{
            for (int i = 0; i < url.length(); i++) {
                if (url.charAt(i) == '/' || url.charAt(i) == '.'
                        || url.charAt(i) == '\\') {
                } else {
                    url = url.substring(i);
                    return url;
                }
            }
        }
        return url;
    }
    //是否能连接上
    private  boolean exists(String url) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setConnectTimeout(3000);
            return (con.getResponseCode() == 200);
        } catch (Exception e) {
            return false;
        }
    }

}
