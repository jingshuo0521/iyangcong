package com.iyangcong.reader.interfaceset;

/**
 * Created by WuZepeng on 2017-12-27.
 * 查询一本书是否已经下载并且加密的接口
 */

public interface BookDecryptQuery {

	boolean isBookDercypt(long id);

	boolean saveBookDecryInfo(long bookId,boolean isDecrypt);
}
