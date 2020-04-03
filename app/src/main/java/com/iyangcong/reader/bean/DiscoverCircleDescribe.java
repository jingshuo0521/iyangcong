package com.iyangcong.reader.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljw on 2016/12/26.
 */

public class DiscoverCircleDescribe {
	/**
	 * 用户名称
	 */
	private String userName = "";
	/**
	 * 用户头像
	 */
	private String userImgUrl = "";
	/**
	 * 圈子描述
	 */
	@SerializedName(value = "circleDescribe",alternate = "groupdesc")
	private String circleDescribe = "";
	/**
	 * 话题数
	 */
	@SerializedName(value = "topicNum",alternate = "topicnum")
	private int topicNum = 0;
	/**
	 * 圈子成员数
	 */
	@SerializedName(value = "memberNum",alternate = "usernum")
	private int memberNum = 0;
	/**
	 * 圈子名称
	 */
	@SerializedName(value = "circleName",alternate = "groupname")
	private String circleName = "";
	/**
	 * 圈子图片
	 */
	@SerializedName(value = "circleImgUrl", alternate = {"photoUrl", "cover"})
	private String circleImage = "";
	/**
	 * 圈子标签
	 */
	@SerializedName(value = "circleLabel",alternate = {"tags","tag"})
	private List<CircleLabel> circleLabel = new ArrayList<CircleLabel>();
	/**
	 * 创建时间
	 */
	@SerializedName(value = "createtime",alternate = "createdate")
	private String  createtime= "";
	/**
	 * 圈子类型id
	 */
	@SerializedName(value = "categoryId",alternate = "category")
	private int categoryId;
	/**
	 * 圈子权限
	 */
	private int authority;
	/**
	 * 圈子的审核状态
	 * 1:未审核 2 审核通过  3审核未通过 4：删除5.举报
	 */
	@SerializedName("checkstatus")
	private int checkStatus;
	/**
	 * 喜欢数目
	 */
	@SerializedName(value = "likeNum",alternate = "likecount")
	private int likeNum;

	/**
	 *创建圈子的用户名
	 */
	@SerializedName("createusername")
	private String createUsername;
	/**
	 *图片在数据库中的路径
	 */
	private String dbpath;
	/**
	 *圈子活跃度
	 */
	@SerializedName(value = "activeLevel",alternate = "activelevel")
	private float activeLevel;
	/**
	 *
	 * 用户类型
	 */
	@SerializedName("usertype")
	private int userType;

	/**
	 * 圈子类型
	 */
	@SerializedName(value = "groupType",alternate = "grouptype")
	private int groupType;
	/**
	 * 相关圈子的id;
	 */
	@SerializedName(value = "relatedClassId",alternate = "relatedclassid")
	private String relatedClassId;
	/**
	 * 创建用户id
	 */
	@SerializedName(value = "createrId",alternate = "createuser")
	private long createrId;

	/**
	 * 圈子成长等级
	 */
	private int degree;

	/**
	 * 后台数据库存储的路径
	 */
	private String path;
	/**
	 * 是否加入圈子
	 */
	@SerializedName(value = "isJoin",alternate = "isjoined")
	private boolean isJoin;

	public int getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(int checkstatus) {
		this.checkStatus = checkstatus;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserImgUrl() {
		return userImgUrl;
	}

	public void setUserImgUrl(String userImgUrl) {
		this.userImgUrl = userImgUrl;
	}

	public List<CircleLabel> getCircleLabel() {
		return circleLabel;
	}

	public void setCircleLabel(List<CircleLabel> circleLabel) {
		this.circleLabel = circleLabel;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getCircleImage() {
		return circleImage;
	}

	public void setCircleImage(String circleImage) {
		this.circleImage = circleImage;
	}

	public int getMemberNum() {
		return memberNum;
	}

	public void setMemberNum(int memberNum) {
		this.memberNum = memberNum;
	}

	public int getTopicNum() {
		return topicNum;
	}

	public void setTopicNum(int topicNum) {
		this.topicNum = topicNum;
	}

	public String getCircleDescribe() {
		return circleDescribe;
	}

	public void setCircleDescribe(String circleDescribe) {
		this.circleDescribe = circleDescribe;
	}

	public String getCircleName() {
		return circleName;
	}

	public void setCircleName(String circleName) {
		this.circleName = circleName;
	}

	public int getAuthority() {
		return authority;
	}

	public void setAuthority(int authority) {
		this.authority = authority;
	}

	public int getLikeNum() {
		return likeNum;
	}

	public void setLikeNum(int likeNum) {
		this.likeNum = likeNum;
	}

	public String getCreateUsername() {
		return createUsername;
	}

	public void setCreateUsername(String createUsername) {
		this.createUsername = createUsername;
	}

	public String getDbpath() {
		return dbpath;
	}

	public void setDbpath(String dbpath) {
		this.dbpath = dbpath;
	}

	public float getActiveLevel() {
		return activeLevel;
	}

	public void setActiveLevel(float activeLevel) {
		this.activeLevel = activeLevel;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public int getGroupType() {
		return groupType;
	}

	public void setGroupType(int groupType) {
		this.groupType = groupType;
	}

	public String getRelatedClassId() {
		return relatedClassId;
	}

	public void setRelatedClassId(String relatedClassId) {
		this.relatedClassId = relatedClassId;
	}

	public long getCreaterId() {
		return createrId;
	}

	public void setCreaterId(long createrId) {
		this.createrId = createrId;
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isJoin() {
		return isJoin;
	}

	public void setJoin(boolean join) {
		isJoin = join;
	}
}
