package com.iyangcong.reader.utils;

import android.net.Uri;
import android.util.Log;

import com.orhanobut.logger.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by WuZepeng on 2017-02-24.
 */

public class HtmlParserUtils {
    /**获取图片链接的属性*/
    public static final String IMAGE_ATTRIBUTE = "img";
    /**获取图片绝对链接的属性*/
    public static final String PATH_ABS = "abs:src";
    /**获取图片的路径*/
    public static final String PATH ="src";
    private static final String[] pirFormatory = new String[]{"img[src$=.jpg]","img[src$=.png]"};
    /**显示后台传回来的相对路径需要的路径*/
    public static final String ONIONBACK = "/onionback";

    /**获取html字符串中的所有图片链接*/
    public static List<String> getUrls(String html,String ip){
        List<String> list = new ArrayList<>();
        if (null == html || "".equals(html)) {
            Logger.e("HtmlParserError：html string is null or \" \"","");
            return list;
        }
        Document doc = Jsoup.parse(html,ip);
        for(Element element:doc.select(IMAGE_ATTRIBUTE)){
            if(element != null){
                Log.i("AAAAAAAAAAA",element.toString());
                String linkSrc = element.attr(PATH_ABS);
                Logger.i("TagName:"+element.tagName(),"");
                Logger.i("Id:" + element.id(),"");
                Logger.i("ClassName:" + element.className(),"");
                list.add(linkSrc);
                Logger.i("linkSrc:" + linkSrc,"");
            }
        }
        return list;
    }

    public static List<String> getPicUrl(String headString,String html){
        List<String> tempList = new ArrayList<>();
        if (null == html || "".equals(html)||null == headString || "".equals(headString)) {
            Logger.e("HtmlParserError：html string is null or \" \"","");
            return tempList;
        }
        Document doc = Jsoup.parse(html);

        for(String formatory:pirFormatory){
            for(Element element:doc.select(formatory)){
                if(element != null){
                    String linkSrc = element.attr(PATH);
                    if(!linkSrc.startsWith("http")){
                        String tempString = headString + linkSrc;
                        Log.i("hahahahahaha linkSrc",linkSrc + "   tempString:" + tempString);
                        tempList.add(tempString);
                    }else{
                        tempList.add(linkSrc);
                    }

                }
            }
        }
        return tempList;

    }

    public static List<String> getPictureUrl(String html,String ip){
        List<String> tempList = new ArrayList<>();
        if (null == html || "".equals(html)) {
            Logger.e("HtmlParserError：html string is null or \" \"","");
            return tempList;
        }
        Document doc = Jsoup.parse(html,ip);

        for(String formatory:pirFormatory){
            for(Element element:doc.select(formatory)){
                if(element != null){
                    String linkSrc = element.attr(PATH_ABS);
                    Uri uri = Uri.parse(linkSrc);
                    String content = uri.getPath();
                    if(!content.startsWith(ONIONBACK))
                        content = ONIONBACK + content;
//                    String url = "http://" + uri.getAuthority() + content;
                    element.attr(PATH,content);
                    linkSrc = element.attr(PATH_ABS);
                    tempList.add(linkSrc);
                }
            }
        }
        return tempList;
    }

    public static String getContent(String html){

        if (null == html || "".equals(html)) {
            Logger.e("HtmlParserError：html string is null or \" \"","");
            return "";
        }
        Document doc = Jsoup.parse(html,Urls.URL);
   
        return doc.text();
    }

    public static String handleAbsPathHtml(String htmlString){
        Document doc = Jsoup.parse(htmlString);
        for(Element element:doc.select(IMAGE_ATTRIBUTE)){
            String abc = element.attr(PATH);
            Uri uri = Uri.parse(abc);
            String content = uri.getPath();
            if(content.startsWith(ONIONBACK))
                content = content.substring(ONIONBACK.length());
            element.attr(PATH,content);
        }
        Log.i("doc.text",doc.body().toString());
        return doc.body().toString();
    }
}
