package com.iyangcong.reader.bean;

/**
 * Created by Administrator on 2017/3/27.
 */

public class CloudShelfBook {
    private String bookResourceUrl;
    private String createtime;
    private String bookImageUrl;
    private String readingProgress;
    private String bookAuthor;
    private String bookName;
    private int bookId;
    private String tags;
    private String bookIntroduction;
    private int totalNum;
    private String bookImageResource;
    private double price;
    private String category;
    private int book_id;

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookImageResource() {
        return bookImageResource;
    }

    public void setBookImageResource(String bookImageResource) {
        this.bookImageResource = bookImageResource;
    }

    public String getBookImageUrl() {
        return bookImageUrl;
    }

    public void setBookImageUrl(String bookImageUrl) {
        this.bookImageUrl = bookImageUrl;
    }

    public String getBookIntroduction() {
        return bookIntroduction;
    }

    public void setBookIntroduction(String bookIntroduction) {
        this.bookIntroduction = bookIntroduction;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookResourceUrl() {
        return bookResourceUrl;
    }

    public void setBookResourceUrl(String bookResourceUrl) {
        this.bookResourceUrl = bookResourceUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getReadingProgress() {
        return readingProgress;
    }

    public void setReadingProgress(String readingProgress) {
        this.readingProgress = readingProgress;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    @Override
    public String toString() {
        return "CloudShelfBook{" +
                "bookAuthor='" + bookAuthor + '\'' +
                ", bookResourceUrl='" + bookResourceUrl + '\'' +
                ", createtime='" + createtime + '\'' +
                ", bookImageUrl='" + bookImageUrl + '\'' +
                ", readingProgress='" + readingProgress + '\'' +
                ", bookName='" + bookName + '\'' +
                ", bookId='" + bookId + '\'' +
                ", tags='" + tags + '\'' +
                ", bookIntroduction='" + bookIntroduction + '\'' +
                ", totalNum=" + totalNum +
                ", bookImageResource='" + bookImageResource + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object object) {

        if (this == object) {
            return true;
        }
        if (object instanceof CloudShelfBook) {
            CloudShelfBook book = (CloudShelfBook) object;
            return (this.bookId == book.bookId)
                    && this.bookName.equals(book.bookName);
        }
        return super.equals(object);
    }
}

