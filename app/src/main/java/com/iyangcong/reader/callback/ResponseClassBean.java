package com.iyangcong.reader.callback;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by ccb on 2017/10/11.
 *
 */


public class ResponseClassBean<T> implements Serializable {

    public int Code;
    public String msg;
    public List<Map<String,Object>> data;


}