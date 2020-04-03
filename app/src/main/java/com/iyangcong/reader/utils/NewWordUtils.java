package com.iyangcong.reader.utils;

import java.util.List;

public class NewWordUtils {
    /*
     * 多组音标只取第一个
     * */
    public static String getSinglePhonetic(String oriPhonetic) {
        if (oriPhonetic == null)
            return null;
        if ("".equals(oriPhonetic))
            return null;
        if (oriPhonetic.contains(";")) {
            String des = oriPhonetic.substring(0, oriPhonetic.indexOf(";"));
            return "/" + des + "/";
        } else
            return "/" + oriPhonetic + "/";
    }

    public static String levelListToStr(List<String> list) {
        StringBuffer stringBuffer = new StringBuffer();
        if (list != null) {
            for (String level : list) {
                stringBuffer.append(level).append(",");
            }
            return stringBuffer.toString().substring(0, stringBuffer.length() - 1);
        }
        return "";
    }
}
