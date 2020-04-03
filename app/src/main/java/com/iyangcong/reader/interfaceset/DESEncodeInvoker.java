package com.iyangcong.reader.interfaceset;

/**
 * Created by WuZepeng on 2017-12-21.
 * 调用ESD解密
 */

public interface DESEncodeInvoker {
	/**
	 * 调用DES解密服务，对某个id的书籍进行解密
	 * @param id
	 */
	void invokerDESEncodeService(long id);
	/**
	 * 退出解密服务，解绑服务
	 */
	void unbindService();
}
