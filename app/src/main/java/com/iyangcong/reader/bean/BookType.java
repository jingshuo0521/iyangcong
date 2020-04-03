package com.iyangcong.reader.bean;

public interface BookType {
//	     *-1：未购买（初始状态） 0:免费图书   1:试读图书  2:已购买图书  3:包月已购买图书
	int INITIAL = -1;//-1：未购买（初始状态）
	int FREE_BOOK = 0;// 0:免费图书
	int TRY_READ_BOOK = 1;//1:试读图书
	int HAS_BUY_BOOk = 2;//2:已购买图书
	int MONTHLY_BOOK = 3;//3:包月已购买图书

	interface BookDownloadState{
		 //0:未下载   1:已下载
		int HAS_ZIPPED = 2;
		int HAS_DOWNLOADED = 1;//已下载；
		int NOT_DOWNLOADED = 0;//未下载
	}

	interface BookBuyState{
		/**
		 * -1:未购买   0:已购买   -2:已添加到购物车
		 */
		int HAS_BOUGHT = 0;//已购买
		int NOT_BUY = -1;//未购买并且未加入购物车；
		int ADD_TO_CHART = -2;//未购买但已经加入购物车；
	}

	/**
	 * 1:上架  2：未上架 3：政治敏感 4：内容错误 5：版权问题
	 公共部分：状态为1显示；
	 私有部分：状态为2、3不显示；
	 详情页面：1 正常显示；2、3提示已下架，不显示信息；4、5提示已下架，显示图书信息不能阅读，如果已购可阅读。
	 */
}
