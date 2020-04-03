package com.iyangcong.reader.bean;
/**
 * author:DarkFlameMaster </br>
 * time:2019/9/20 16:56 </br>
 * desc:章节测试列表数据 </br>
 */
public class ChapterTestItem {
    private int chapterId; // 题目对应章节id
    private int finish;// 如用户已经有过提交历史记录，返回1，否则返回0
    private String name;// 题目名称
    private String chapterName; // 章节名称

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public int getFinish() {
        return finish;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
