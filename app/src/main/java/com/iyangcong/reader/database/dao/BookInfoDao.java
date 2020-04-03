package com.iyangcong.reader.database.dao;

import com.iyangcong.reader.bean.BookInfo;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

/**
 * Created by DarkFlameMaster on 2017/5/3.
 */

public class BookInfoDao extends BaseDao<BookInfo>{
    /**
     * @param sqliteOpenHelper using this to get instance of  DatabaseHelper
     */
    public BookInfoDao(OrmLiteSqliteOpenHelper sqliteOpenHelper) {
        super(sqliteOpenHelper);
    }
}
