package com.iyangcong.reader.epub;

/**
 * Created by WuZepeng on 2018-01-05.
 */

public class BookInfoEpubDeleteInfo {
	private long mBookId;

	public BookInfoEpubDeleteInfo(long bookId) {
		mBookId = bookId;
	}

	public long getBookId() {
		return mBookId;
	}

	public void setBookId(long bookId) {
		mBookId = bookId;
	}
}
