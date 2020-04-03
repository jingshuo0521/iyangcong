package com.iyangcong.reader.interfaceset;

/**
 * Created by WuZepeng on 2018-01-02.
 *Epub的状态，
 * 目前有7种
 * -2：删除
 * -1：异常状态，
 * 0：初始状态，
 * 1：下载完成
 * 2：DES解密完成
 * 3：DES解密失败
 * 4：AES解密完成（DES解密完成）
 * 5：AES解密失败（DES解密完成）
 */

public interface EpubState {
	int Deleted = -2;
	int Exception = -1;
	int Inital = 0;
	int Download = 1;
	int DESSuccess = 2;
	int DESFailure = 3;
	int AESSuccess = 4;
	int AESFailure = 5;
}
