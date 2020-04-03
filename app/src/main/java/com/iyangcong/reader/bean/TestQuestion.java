package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TestQuestion {
    private int type;//题目类型 1:单选 2:多选 1(2)(3)(4)(5) 分别代表选择、填空、判断、匹配、选择填空
    private String content; // 文字题目
    private String audio; // 语音题目预留
    private String image; // 图片题目预留

    @SerializedName(value = "choices",alternate = "choose")
    private List<String> choices; // 题目选项

    private List<String> choose_audio; // 语音题目选项
    private List<String> choose_image; // 图片题目选项
    private boolean hasAnswered;// 是否已回答
    private int status; //0:错误  1:正确
    private String yourAnswer; // 答案

    @SerializedName(value = "correctAnswer",alternate = "answer")
    private String correctAnswer;// 正确答案

    private String analysis; // 答案解析

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getChoose_audio() {
        return choose_audio;
    }

    public void setChoose_audio(List<String> choose_audio) {
        this.choose_audio = choose_audio;
    }

    public List<String> getChoose_image() {
        return choose_image;
    }

    public void setChoose_image(List<String> choose_image) {
        this.choose_image = choose_image;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    public boolean isHasAnswered() {
        return hasAnswered;
    }

    public void setHasAnswered(boolean hasAnswered) {
        this.hasAnswered = hasAnswered;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getYourAnswer() {
        return yourAnswer;
    }

    public void setYourAnswer(String yourAnswer) {
        this.yourAnswer = yourAnswer;
        if(this.yourAnswer!=null){
            setStatus(this.yourAnswer.equals(this.correctAnswer)?1:0);
        }
    }

    public void resetQuestion(){
        setHasAnswered(false);
        setStatus(0);
        setYourAnswer(null);
    }
}
