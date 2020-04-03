package com.iyangcong.reader.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/4/11.
 */

public class CloudShelfContent {
    private List<CloudShelfBook> shelfBookCloudList;
    private List<String> CategoryList;
    private int tabPosition;

    public int getTabPosition() {
        return tabPosition;
    }

    public void setTabPosition(int tabPosition) {
        this.tabPosition = tabPosition;
    }

    public List<String> getCategoryList() {
        return CategoryList;
    }

    public void setCategoryList(List<String> categoryList) {
        CategoryList = categoryList;
    }

    public List<CloudShelfBook> getShelfBookCloudList() {
        return shelfBookCloudList;
    }

    public void setShelfBookCloudList(List<CloudShelfBook> shelfBookCloudList) {
        this.shelfBookCloudList = shelfBookCloudList;
    }
}
