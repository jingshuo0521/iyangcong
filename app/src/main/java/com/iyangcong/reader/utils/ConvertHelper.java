package com.iyangcong.reader.utils;

import com.orhanobut.logger.Logger;

/**
 * Created by WuZepeng on 2017-03-16.
 */

public class ConvertHelper {
    /**
     * 将后台传过来的版本字符串进行转义
     * 如果版本字符串中含有1,则说明含有中文版本的书
     * 如果版本字符串中含有2，则说明含有英文版本的书
     * 如果版本字符串中含有3，则说明含有中英对照的书
     *
     * @param str：后台传过来的版本字符串
     * @return
     */
    public static String getEdition(String str) {
        StringBuilder sb = new StringBuilder("版本：");
        if (str.indexOf("3") > -1)
            sb.append("双语");
        else if (str.indexOf("1") > -1)
            sb.append("中文");
        else if (str.indexOf("2") > -1)
            sb.append("英文");
        Logger.i("edtion" + sb.toString());
        return sb.toString();
    }
}
