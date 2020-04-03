package com.iyangcong.reader.bean;

import java.io.Serializable;

public class SemesterBook  implements Serializable{

    private int bookId;
    private String bookCover;
    private String bookName;
    private String bookAuthor;
    private int bookDifficulty;
    private String bookGrade;
    private int bookWordNums;
    private String bookContentIntro;
    private boolean isReceived;
    private boolean isSetRequired;
    private int bookListId;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookContentIntro() {
        return bookContentIntro;
    }

    public void setBookContentIntro(String bookContentIntro) {
        this.bookContentIntro = bookContentIntro;
    }

    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    public int getBookDifficulty() {
        return bookDifficulty;
    }

    public void setBookDifficulty(int bookDifficulty) {
        this.bookDifficulty = bookDifficulty;
    }

    public String getBookGrade() {
        return bookGrade;
    }

    public void setBookGrade(String bookGrade) {
        this.bookGrade = bookGrade;
    }

    public int getBookListId() {
        return bookListId;
    }

    public void setBookListId(int bookListId) {
        this.bookListId = bookListId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getBookWordNums() {
        return bookWordNums;
    }

    public void setBookWordNums(int bookWordNums) {
        this.bookWordNums = bookWordNums;
    }

    public boolean isReceived() {
        return isReceived;
    }

    public void setReceived(boolean received) {
        isReceived = received;
    }

    public boolean isSetRequired() {
        return isSetRequired;
    }

    public void setSetRequired(boolean setRequired) {
        isSetRequired = setRequired;
    }
}
