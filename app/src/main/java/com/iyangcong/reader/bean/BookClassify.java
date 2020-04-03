package com.iyangcong.reader.bean;

import java.util.List;

/**
 * Created by ljw on 2017/3/24.
 */

public class BookClassify {

    private List<CommonBook> commonBook;
    private List<Books> book;
    private List<Books> magazine;

    public List<CommonBook> getCommonBook() {
        return commonBook;
    }

    public void setCommonBook(List<CommonBook> commonBook) {
        this.commonBook = commonBook;
    }

    public List<Books> getBook() {
        return book;
    }

    public void setBook(List<Books> book) {
        this.book = book;
    }

    public List<Books> getMagazine() {
        return magazine;
    }

    public void setMagazine(List<Books> magazine) {
        this.magazine = magazine;
    }

    public static class Books{
        private int classifyImgType;
        private String classifyName;

        public int getClassifyImgType() {
            return classifyImgType;
        }

        public void setClassifyImgType(int classifyImgType) {
            this.classifyImgType = classifyImgType;
        }

        public String getClassifyName() {
            return classifyName;
        }

        public void setClassifyName(String classifyName) {
            this.classifyName = classifyName;
        }
    }

    public static class CommonBook{
        private int id;
        private String imgUrl;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
