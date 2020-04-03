package com.iyangcong.reader.utils;

import com.iyangcong.reader.interfaceset.BookDecryptQuery;

/**
 * Created by WuZepeng on 2017-12-27.
 * 查询某本书是否已经下载并且加密
 */

public class BookDecryptQueryImpl implements BookDecryptQuery {

	private SharedPreferenceUtil info = SharedPreferenceUtil.getInstance();
	private final int noBookId = -1;
	@Override
	public boolean isBookDercypt(long id) {
		long result = info.getLong(new StringBuffer("book_").append(id).toString(),noBookId);
		return result != noBookId;
	}

	@Override
	public boolean saveBookDecryInfo(long bookId, boolean isDecrypt) {
		info.putLong(new StringBuffer("book_").append(bookId).toString(),isDecrypt?bookId:noBookId);
		return isDecrypt;
	}
}
