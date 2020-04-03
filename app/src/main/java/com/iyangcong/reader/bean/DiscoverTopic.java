package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;

/**
 * 发现页面话题
 */
public class DiscoverTopic implements Comparable{
    /**
     * 用户头像
     */
    @SerializedName(value = "userImageUrl",alternate ={"groupCover"})
    private String userImageUrl = "";
    /**
     * 话题id
     */
//    @SerializedName("reviewId")
    @SerializedName("topicId")
    private int topicId ;
    /**
     * 用户名
     */
    @SerializedName(value = "userName",alternate ={"authorName"})
    private String userName = "";


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    /**

     * 话题名
     */
    @SerializedName(value = "topicName",alternate ={"title"})
    private String topicName = "";
    /**
     * 话题内容
     */
    @SerializedName(value = "topicDescribe",alternate ={"content"})
    private String topicDescribe = "";
    /**
     * 话题发布时间
     */
//    @SerializedName("createtime")
    private String topicTime = "";
    /**
     * 点赞人数
     */
//    @SerializedName("reviewsLike")
    private int topicLike;
    /**
     * 评论消息数目
     */
    private int messageNum;
    /**
     * 置顶时间
     **/
    public long time;

    /*
    话题图片
     */
    private String topicImgUrl;
    /**
     * 小组id
     */
    @SerializedName("groupId")
    private int groupId;
    /*
    话题总个数
     */
    private int totalNum;
    /*
     * 话题状态（置顶）
     */
    private int status;
    /*
    用户ID
     */
    private int userId;
    /*
     话题详情中的图片相对路径的前缀；
     */
    private String path;
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public String getTopicImgUrl() {
        return topicImgUrl;
    }

    public void setTopicImgUrl(String topicImgUrl) {
        this.topicImgUrl = topicImgUrl;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicDescribe() {
        return topicDescribe;
    }

    public void setTopicDescribe(String topicDescribe) {
        this.topicDescribe = topicDescribe;
    }

    public String getTopicTime() {
        return topicTime;
    }

    public void setTopicTime(String topicTime) {
        this.topicTime = topicTime;
    }

    public int getTopicLike() {
        return topicLike;
    }

    public void setTopicLike(int topicLike) {
        this.topicLike = topicLike;
    }

    public int getMessageNum() {
        return messageNum;
    }

    public void setMessageNum(int messageNum) {
        this.messageNum = messageNum;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    @Override
    public int compareTo(Object another) {
        if (another == null || !(another instanceof DiscoverTopic)) {
            return -1;
        }

        DiscoverTopic otherSession = (DiscoverTopic) another;
        /**置顶判断 ArrayAdapter是按照升序从上到下排序的，就是默认的自然排序
         * 如果是相等的情况下返回0，包括都置顶或者都不置顶，返回0的情况下要
         * 再做判断，拿它们置顶时间进行判断
         * 如果是不相等的情况下，otherSession是置顶的，则当前session是非置顶的，
         * 应该在otherSession下面，所以返回1
         * 同样，session是置顶的，则当前otherSession是非置顶的，
         * 应该在otherSession上面，所以返回-1
         * */
        int result = 0 - (status - otherSession.getStatus());
        if (result == 0) {
            result = 0 - compareToTime(time, otherSession.getTime());
        }
        return result;
    }

    /**
     * 根据时间对比
     * */
    public static int compareToTime(long lhs, long rhs) {
        Calendar cLhs = Calendar.getInstance();
        Calendar cRhs = Calendar.getInstance();
        cLhs.setTimeInMillis(lhs);
        cRhs.setTimeInMillis(rhs);
        return cLhs.compareTo(cRhs);
    }


    @Override
    public boolean equals(Object obj) {
        if(obj != null && obj instanceof DiscoverTopic)
            if(topicId == ((DiscoverTopic)obj).topicId)
                return true;
        return false;
    }

    @Override
    public String toString() {
        return "DiscoverTopic{" +
                "userImageUrl='" + userImageUrl + '\'' +
                ", topicId=" + topicId +
                ", userName='" + userName + '\'' +
                ", topicName='" + topicName + '\'' +
                ", topicDescribe='" + topicDescribe + '\'' +
                ", topicTime='" + topicTime + '\'' +
                ", topicLike=" + topicLike +
                ", messageNum=" + messageNum +
                ", topicImgUrl='" + topicImgUrl + '\'' +
                ", groupId=" + groupId +
                ", totalNum=" + totalNum +
                ", userId=" + userId +
                ", path='" + path + '\'' +
                '}';
    }
}
