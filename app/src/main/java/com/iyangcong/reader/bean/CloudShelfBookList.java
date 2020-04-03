package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/8.
 */

public class CloudShelfBookList {
    @SerializedName(value = "history",alternate = {"历史哲学"})
    private List<CloudShelfBook> history;
    @SerializedName(value = "science",alternate = {"科幻冒险"})
    private List<CloudShelfBook> science;
    @SerializedName(value = "novel",alternate = {"小说散文"})
    private List<CloudShelfBook> novel;
    @SerializedName(value = "humrous",alternate = {"讽刺幽默"})
    private List<CloudShelfBook> humrous;
    @SerializedName(value = "person",alternate = {"人物传记"})
    private List<CloudShelfBook> person;
    @SerializedName(value = "sex",alternate = {"两性情感"})
    private List<CloudShelfBook> sex;

    public List<CloudShelfBook> getBook() {
        return history;
    }

    public void setBook(List<CloudShelfBook> history) {
        this.history = history;
    }

    public List<CloudShelfBook> getHumrous() {
        return humrous;
    }

    public void setHumrous(List<CloudShelfBook> humrous) {
        this.humrous = humrous;
    }

    public List<CloudShelfBook> getNovel() {
        return novel;
    }

    public void setNovel(List<CloudShelfBook> novel) {
        this.novel = novel;
    }

    public List<CloudShelfBook> getPerson() {
        return person;
    }

    public void setPerson(List<CloudShelfBook> person) {
        this.person = person;
    }

    public List<CloudShelfBook> getScience() {
        return science;
    }

    public void setScience(List<CloudShelfBook> science) {
        this.science = science;
    }

    public List<CloudShelfBook> getSex() {
        return sex;
    }

    public void setSex(List<CloudShelfBook> sex) {
        this.sex = sex;
    }

    public List<CloudShelfBook> add(){
        List<CloudShelfBook> temp = new ArrayList<>();
        temp.addAll(history);
        temp.addAll(science);
        temp.addAll(novel);
        temp.addAll(humrous);
        temp.addAll(person);
        temp.addAll(sex);
        return temp;
    }
}
