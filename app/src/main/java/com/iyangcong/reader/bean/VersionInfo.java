package com.iyangcong.reader.bean;

import java.io.Serializable;

/**
 * 升级版本信息
 * User: 刘国伟 
 * Date: 13-5-13
 * Time: 下午5:32
 * Modified By 王浩 2013.7.10 AM 10:02
 */
public class VersionInfo implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String lastVersionName, minVersionName;
    
    private String versionDescription;
    private String updateLog;
    private String appUrl;

    public String getLastVersionName() {
        return lastVersionName;
    }
    public void setLastVersionName(String lastVersionName) {
        this.lastVersionName = lastVersionName;
    }
    public String getMinVersionName() {
        return minVersionName;
    }
    public void setMinVersionName(String minVersionName) {
        this.minVersionName = minVersionName;
    }
    public String getUpdateLog() {
        return updateLog;
    }
    public void setUpdateLog(String updateLog) {
        this.updateLog = updateLog;
    }
    public String getAppUrl() {
        return appUrl;
    }
    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }
    public String getVersionDescription() {
        return versionDescription;
    }
    public void setVersionDescription(String versionDescription) {
        this.versionDescription = versionDescription;
    }

}
