package com.iyangcong.reader.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * author:DarkFlameMaster </br>
 * time:2019/9/30 14:56 </br>
 * desc:测试记录类 </br>
 */
public class TestRecord {
    List<String> correct_answers; // 正确答案列表
    List<Integer> questions_ids; // 问题ids
    int score; // 得分
    List<String> answers; // 用户的答案
    int bookId; // 图书id
    long commit_time; // 提交时间
    List<Integer> type; // 题目类型
    List<String> analysis; // 问题分析列表
    List<List<String>> option_txt;
    List<String> title_txt;
    int id;
    float correct_percent;
    long chapterId;
    int userId;
    String question_name;
    String bookName;

    public List<String> getCorrect_answers() {
        return correct_answers;
    }

    public void setCorrect_answers(List<String> correct_answers) {
        this.correct_answers = correct_answers;
    }

    public List<Integer> getQuestions_ids() {
        return questions_ids;
    }

    public void setQuestions_ids(List<Integer> questions_ids) {
        this.questions_ids = questions_ids;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public long getCommit_time() {
        return commit_time;
    }

    public void setCommit_time(long commit_time) {
        this.commit_time = commit_time;
    }

    public List<Integer> getType() {
        return type;
    }

    public void setType(List<Integer> type) {
        this.type = type;
    }

    public List<String> getAnalysis() {
        return analysis;
    }

    public void setAnalysis(List<String> analysis) {
        this.analysis = analysis;
    }

    public List<List<String>> getOption_txt() {
        return option_txt;
    }

    public void setOption_txt(List<List<String>> option_txt) {
        this.option_txt = option_txt;
    }

    public List<String> getTitle_txt() {
        return title_txt;
    }

    public void setTitle_txt(List<String> title_txt) {
        this.title_txt = title_txt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getQuestion_name() {
        return question_name;
    }

    public void setQuestion_name(String question_name) {
        this.question_name = question_name;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public List<TestQuestion> record2Question(){
        List<TestQuestion> list = new ArrayList<>();
        for (int i = 0; i < questions_ids.size(); i++) {
            TestQuestion test = new TestQuestion();
            test.setContent(title_txt.get(i));
            test.setType(type.get(i));
            test.setChoices(option_txt.get(i));
            test.setAnalysis(analysis.get(i));
            test.setCorrectAnswer(correct_answers.get(i));
            test.setYourAnswer(answers.get(i));// 必须在correctanswer之后
            test.setHasAnswered(true);
            list.add(test);
        }
        return list;
    }
}
