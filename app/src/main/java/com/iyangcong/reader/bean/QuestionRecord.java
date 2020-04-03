package com.iyangcong.reader.bean;

import java.util.List;

/**
 * author:DarkFlameMaster </br>
 * time:2019/9/27 14:29 </br>
 * desc:我的答题记录数据类 </br>
 */
public class QuestionRecord {
    private int recordCount; // 记录总数
    private long timestamp; // 做题时间
    private long id; // 题目id
    private List<Integer> questions_ids; // 问题ids
    private float correct_percent; // 正确率
    private long chapterId; // 对应章节id
    private List<String>  answers; // 用户答案
    private int score; // 得分
    private long bookId; // 对应图书id
    private String question_name; // 测试名称
    private String bookName; // 对应书名

    public String getQuestion_name() {
        return question_name;
    }

    public void setQuestion_name(String question_name) {
        this.question_name = question_name;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Integer> getQuestions_ids() {
        return questions_ids;
    }

    public void setQuestions_ids(List<Integer> questions_ids) {
        this.questions_ids = questions_ids;
    }

    public float getCorrect_percent() {
        return correct_percent;
    }

    public void setCorrect_percent(float correct_percent) {
        this.correct_percent = correct_percent;
    }

    public long getChapterId() {
        return chapterId;
    }

    public void setChapterId(long chapterId) {
        this.chapterId = chapterId;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
}
