package com.iyangcong.reader.bean;

public class SearchBook {
    private String bookName;
    private String bookCover;
    private String bookId;
    private String bookSource;

    public String getBookName() {
        return "《"+bookName+"》";
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookSource() {
        return bookSource;
    }

    public void setBookSource(String bookSource) {
        this.bookSource = bookSource;
    }
}
