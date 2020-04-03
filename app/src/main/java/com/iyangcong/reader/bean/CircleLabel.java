package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WuZepeng on 2017-03-07.
 */

public class CircleLabel{
    @SerializedName(value = "tagname",alternate = "tagName")
    private String tagName = "";
    private int tagId;
    private boolean isChecked;

    public CircleLabel(String tagName, int tagId) {
        this.tagName = tagName;
        this.tagId = tagId;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
