package com.iyangcong.reader.bean;

import java.util.List;

/**
 * Created by WuZepeng on 2017-06-14.
 */

public class SearchBookBean {
	/**
	 * 书籍简介列表
	 */
	private List<BookIntroduction> bookInfoList;
	/**
	 * 分页的总页数
	 */
	private int totalPageNum;
	/**
	 * 当前页
	 */
	private int currentPageNum;
	/**
	 * 书籍列表总数据数
	 */
	private int totalResultNum;

	public List<BookIntroduction> getBookInfoList() {
		return bookInfoList;
	}

	public void setBookInfoList(List<BookIntroduction> bookInfoList) {
		this.bookInfoList = bookInfoList;
	}

	public int getTotalPageNum() {
		return totalPageNum;
	}

	public void setTotalPageNum(int totalPageNum) {
		this.totalPageNum = totalPageNum;
	}

	public int getCurrentPageNum() {
		return currentPageNum;
	}

	public void setCurrentPageNum(int currentPageNum) {
		this.currentPageNum = currentPageNum;
	}

	public int getTotalResultNum() {
		return totalResultNum;
	}

	public void setTotalResultNum(int totalResultNum) {
		this.totalResultNum = totalResultNum;
	}
}
