
package org.geometerplus.fbreader.formats.oeb.function;

import android.util.Xml;

import org.geometerplus.fbreader.formats.oeb.function.bean.NavMap;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 王浩
 * @category 解析xml
 * @created 2013.5.14 PM 14:55
 */

public class EpubXmlParser {

    private static XmlPullParser parser;
    private static String encode;

    static {
        parser = Xml.newPullParser();
        encode = "UTF-8";
    }

    /**
     * 解析container
     *
     * @param is
     * @return
     * @throws Exception
     */
    public static Map<String, String> parseContainer(InputStream is) throws Exception {

        parser.setInput(is, encode);
        Map<String, String> map = null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    map = new HashMap<String, String>();
                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("rootfile")) {
                        String full_path = parser.getAttributeValue(null, "full-path");
                        String media_type = parser.getAttributeValue(null, "media-type");
                        map.put("full_path", full_path);
                        map.put("media_type", media_type);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
            }
            eventType = parser.next();
        }
        is.close();
        return map;
    }

    /**
     * 解析Ncx文件
     *
     * @return
     */
    public static List<NavMap> parseNcx(InputStream is) throws Exception {
        parser.setInput(is, encode);
        List<NavMap> list = new ArrayList<NavMap>();
        NavMap map = null;
        int eventType = parser.getEventType();
        int i = 0;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tag = parser.getName();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (tag.equals("navPoint")) {
                        map = new NavMap();
                    } else if (map != null) {
                        if (tag.equals("text")) {
                            String chapterName = parser.nextText();
                            map.setId(i++);
                            map.setName(chapterName);
                        } else if (tag.equals("content")) {
                            String src = parser.getAttributeValue(null, "src");
                            map.setSrc(src);
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (tag.equals("navPoint") && map != null) {
                        list.add(map);
                    }
                    break;
            }
            eventType = parser.next();
        }
        is.close();
        return list;
    }

    /**
     * 解析Spine
     */

    public static List<String> parseSpine(InputStream is) throws Exception {
        parser.setInput(is, encode);
        List<String> list = null;
        String str = null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tag = parser.getName();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (tag.equals("spine")) {
                        list = new ArrayList<String>();
                    } else if (list != null) {
                        if (tag.equals("itemref")) {
                            str = parser.getAttributeValue(null, "idref");
                            list.add(str);
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
            }
            eventType = parser.next();
        }
        is.close();
        return list;
    }
    
    /**
     * 解析Manifest
     *
     * @param is
     * @return
     * @throws Exception
     */
    public static Map<String, String> parseManifest(InputStream is) throws Exception {
        parser.setInput(is, encode);
        Map<String, String> map = null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tag = parser.getName();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (tag.equals("manifest")) {
                        map = new HashMap<String, String>();
                    } else if (map != null) {
                        if (tag.equals("item")) {
                            String href = parser.getAttributeValue(null, "href");
                            String id = parser.getAttributeValue(null, "id");
                            map.put(id, href);
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
            }
            eventType = parser.next();
        }
        is.close();
        return map;
    }

}
