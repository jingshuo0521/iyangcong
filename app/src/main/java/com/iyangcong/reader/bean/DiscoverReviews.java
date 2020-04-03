package com.iyangcong.reader.bean;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.iyangcong.reader.utils.DateUtils;

import org.geometerplus.android.fbreader.httpd.DataUtil;

import java.util.Date;

/**
 * Created by WuZepeng on 2017-03-04.
 */

public class DiscoverReviews implements Comparable<DiscoverReviews>{
    /**
     * 英文标题
     */
    @SerializedName(value = "TitleEn", alternate = {"title_en"})
    private String TitleEn = "";
    /**
     *中文标题
     */
    private String TitleZh = "";
    /**
     * 图书id
     */
    private int bookid;
    /**
     * 作者
     */
    private String auther = "";
    /**
     *图书封面
     */
    private String bookcover = "";
    /**
     *内容
     */
    @SerializedName(value = "content", alternate = {"thoughtDescribe"})
    private String content = "";

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    /**
     *图书版本

     */
    @SerializedName(value = "edition",alternate = "language")
    private String edition = "";
    /**
     *创建时间
     */
    @SerializedName(value = "createtime", alternate = {"thoughtTime"})
    private String createtime = "";
    /**
     * 等级
     */
    @SerializedName(value = "grade", alternate = {"starNum"})
    private int grade;
    /**
     * 导读
     */
    private String guid = "";
    /**
     * 回复人数
     */
    @SerializedName(value = "messageNum",alternate = {"reponsenum","responsenum"})
    private int messageNum;
    /**
     * 读后感id
     */
    @SerializedName(value = "reviewId", alternate = {"reviewsId","reviewid"})
    private int reviewId;
    /**
     * 读后感喜欢数
     */
    @SerializedName(value = "reviewsLike", alternate = {"thoughtLike","likenum"})
    private int reviewsLike;
    /**
     * 标题
     */
    @SerializedName(value = "title", alternate = {"thoughtTitle"})
    private String title = "";
    /**
     * 用户头像
     */
    @SerializedName(value = "userImageUrl",alternate = {"userphoto"})
    private String userImageUrl = "";
    /**
     * 用户名称
     */
    @SerializedName(value = "userName",alternate = {"username"})
    private String userName ="";
    @SerializedName(value = "userId", alternate = {"userIdForReviews","userid"})
    private int userId;
    /**
     * 是否点过赞
     */
    @SerializedName(value = "like")
    private boolean isLiked;
	/**
	 * 出版社名称
	 */
	@SerializedName(value = "PublishHouse",alternate = "publishHouse")
	private String publishHouse;
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitleEn() {
        return TitleEn;
    }

    public void setTitleEn(String titleEn) {
        TitleEn = titleEn;
    }

    public String getTitleZh() {
        return TitleZh;
    }

    public void setTitleZh(String titleZh) {
        TitleZh = titleZh;
    }

    public String getAuther() {
        return auther;
    }

    public void setAuther(String auther) {
        this.auther = auther;
    }

    public String getBookcover() {
        return bookcover;
    }

    public void setBookcover(String bookcover) {
        this.bookcover = bookcover;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getBookid() {
        return bookid;
    }

    public void setBookid(int bookid) {
        this.bookid = bookid;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public int getMessageNum() {
        return messageNum;
    }

    public void setMessageNum(int messageNum) {
//        super.setMessageNum(messageNum);
        this.messageNum = messageNum;
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
//        super.setTopicId(reviewId);
        this.reviewId = reviewId;
    }

    public int getReviewsLike() {
        return reviewsLike;
    }

    public void setReviewsLike(int reviewsLike) {
//        super.setTopicLike(reviewsLike);
        this.reviewsLike = reviewsLike;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
//        super.setTopicName(title);
        this.title = title;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
//        super.setUserImageUrl(userImageUrl);
        this.userImageUrl = userImageUrl;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
//        super.setUserName(userName);
        this.userName = userName;
    }

	public String getPublishHouse() {
		return publishHouse;
	}

	public void setPublishHouse(String publishHouse) {
		this.publishHouse = publishHouse;
	}

	@Override
    public int compareTo(@NonNull DiscoverReviews discoverReviews) {
        Date date1 = DateUtils.StringToDate(createtime,"yyyy-MM-dd HH:mm:ss");
        Date data2 = DateUtils.StringToDate(discoverReviews.createtime,"yyyy-MM-dd HH:mm:ss");
        return date1.compareTo(data2);
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj != null && obj instanceof DiscoverReviews)
            if(reviewId == ((DiscoverReviews)obj).reviewId)
                return true;
        return false;
    }

    @Override
    public String toString() {
        return "DiscoverReviews{" +
                "TitleEn='" + TitleEn + '\'' +
                ", TitleZh='" + TitleZh + '\'' +
                ", bookid=" + bookid +
                ", auther='" + auther + '\'' +
                ", bookcover='" + bookcover + '\'' +
                ", content='" + content + '\'' +
                ", edition='" + edition + '\'' +
                ", createtime='" + createtime + '\'' +
                ", grade=" + grade +
                ", guid='" + guid + '\'' +
                ", messageNum=" + messageNum +
                ", reviewId=" + reviewId +
                ", reviewsLike=" + reviewsLike +
                ", title='" + title + '\'' +
                ", userImageUrl='" + userImageUrl + '\'' +
                ", userName='" + userName + '\'' +
                ", userId=" + userId +
                '}';
    }
}
