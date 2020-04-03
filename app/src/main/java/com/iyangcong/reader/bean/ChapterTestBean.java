package com.iyangcong.reader.bean;

import java.util.List;

/**
 * author:DarkFlameMaster </br>
 * time:2019/9/18 15:50 </br>
 * desc:章节测试共享类 </br>
 */
public class ChapterTestBean {
    private int id;
    private int chapterId;// 对应段落id
    private int time;// 花费时间
    private float PassingRate;// 通过率
    private List<TestQuestion> mTestQuestionList; // 题目列表
    private boolean isSubmit; // 是否已提交 isfinished

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public float getPassingRate() {
        return PassingRate;
    }

    public void setPassingRate(float passingRate) {
        PassingRate = passingRate;
    }

    public List<TestQuestion> getTestQuestionList() {
        return mTestQuestionList;
    }

    public void setTestQuestionList(List<TestQuestion> testQuestionList) {
        mTestQuestionList = testQuestionList;
    }

    public boolean isSubmit() {
        return isSubmit;
    }

    public void setSubmit(boolean submit) {
        isSubmit = submit;
    }

    public boolean sumbit(){
        if(mTestQuestionList == null && mTestQuestionList.size() == 0){
            return false;
        }
        int passNum = 0;
        for(TestQuestion test:mTestQuestionList){
            if(test.getStatus() == 1){
                passNum ++;
            }
        }
        setPassingRate((float) passNum/mTestQuestionList.size());
        setSubmit(true);
        return true;
    }

    /**
     * 重置测试  为了重新测试
     * @return
     */
    public boolean resetTest(){
        setTime(0);
        setPassingRate(0f);
        setSubmit(false);
        for(TestQuestion test:mTestQuestionList){
            test.resetQuestion();
        }
        return true;
    }
}
