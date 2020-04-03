package com.iyangcong.reader.model;

import java.io.Serializable;

/**
 * Created by WuZepeng on 2017-02-20.
 */

public class BroadCastResponse implements Serializable{
    private static final long serialVersionUID = -4958863396586838435L;

    private String broadcastContent;
    private String broadcastTitle;
    private int broadcastType;
    private int id;
}
