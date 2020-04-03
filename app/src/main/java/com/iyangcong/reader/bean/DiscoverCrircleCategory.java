package com.iyangcong.reader.bean;

/**
 * Created by WuZepeng on 2017-02-25.
 * 圈子类型POJO
 */
public class DiscoverCrircleCategory {
    /**
     * 圈子标签类型id
     */
    private int categoryid;
    /**
     * 圈子标签名称
     */
    private String categoryname;
    private boolean isClicked = false;
    public int getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(int categoryid) {
        this.categoryid = categoryid;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    @Override
    public String toString() {
        return "DiscoverCrircleCategory{" +
                "categoryid=" + categoryid +
                ", categoryname='" + categoryname + '\'' +
                '}';
    }
}
