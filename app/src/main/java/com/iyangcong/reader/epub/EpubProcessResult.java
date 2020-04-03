package com.iyangcong.reader.epub;

/**
 * Created by WuZepeng on 2018-01-03.
 */

public class EpubProcessResult {
	private long mBookId;
	private boolean isSuccessful;
	private String message;

	public EpubProcessResult() {
		this(0,true,"");
	}

	public EpubProcessResult(long bookId, boolean isSuccessful, String message) {
		mBookId = bookId;
		this.isSuccessful = isSuccessful;
		this.message = message;
	}

	public long getBookId() {
		return mBookId;
	}

	public boolean isSuccessful() {
		return isSuccessful;
	}

	public String getMessage() {
		return message;
	}
}
