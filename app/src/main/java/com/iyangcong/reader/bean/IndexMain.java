package com.iyangcong.reader.bean;

import java.io.Serializable;
import java.util.Map;

public class IndexMain implements Serializable {
    private Map<String,Object> banner;
    private Map<String,Object> broadcast;
    private Map<String,Object> pushbooks;

    public Map<String, Object> getBanner() {
        return banner;
    }

    public void setBanner(Map<String, Object> banner) {
        this.banner = banner;
    }

    public Map<String, Object> getBroadcast() {
        return broadcast;
    }

    public void setBroadcast(Map<String, Object> broadcast) {
        this.broadcast = broadcast;
    }

    public Map<String, Object> getPushbooks() {
        return pushbooks;
    }

    public void setPushbooks(Map<String, Object> pushbooks) {
        this.pushbooks = pushbooks;
    }
}
