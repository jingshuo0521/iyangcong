package com.iyangcong.reader.callback;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by ccb on 2017/10/11.
 *
 */


public class ResponseBean<T> implements Serializable {

    public int Code;
    public String msg;
    public Map<String,Object> data;


}