package com.iyangcong.reader.database.dao;

import com.iyangcong.reader.bean.ReadingDataBean;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

public class ReadingDataDao extends BaseDao<ReadingDataBean> {
	/**
	 * @param sqliteOpenHelper using this to get instance of  DatabaseHelper
	 */
	public ReadingDataDao(OrmLiteSqliteOpenHelper sqliteOpenHelper) {
		super(sqliteOpenHelper);
	}
}
