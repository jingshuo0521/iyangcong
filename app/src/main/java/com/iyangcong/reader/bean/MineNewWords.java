package com.iyangcong.reader.bean;

/**
 * Created by Administrator on 2017/3/16.
 */
/*
生词本
 */
public class MineNewWords {
    /*
    已掌握
     */
    private int knownNum;
    /*
    需背诵
     */
    private int reciteNum;
    /*
    待复习
     */
    private int reviewNum;
    /*
    总词量
     */
    private int totalWord;

    public int getKnownNum() {
        return knownNum;
    }

    public void setKnownNum(int knownNum) {
        this.knownNum = knownNum;
    }

    public int getReciteNum() {
        return reciteNum;
    }

    public void setReciteNum(int reciteNum) {
        this.reciteNum = reciteNum;
    }

    public int getReviewNum() {
        return reviewNum;
    }

    public void setReviewNum(int reviewNum) {
        this.reviewNum = reviewNum;
    }

    public int getTotalWord() {
        return totalWord;
    }

    public void setTotalWord(int totalWord) {
        this.totalWord = totalWord;
    }
}
