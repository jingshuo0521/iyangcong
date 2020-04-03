package com.iyangcong.reader.database.dao;

import com.iyangcong.reader.bean.ShelfBook;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

/**
 * Created by ljw on 2017/3/16.
 */

public class BookDao extends BaseDao<ShelfBook> {
    /**
     * @param sqliteOpenHelper using this to get instance of  DatabaseHelper
     */
    public BookDao(OrmLiteSqliteOpenHelper sqliteOpenHelper) {
        super(sqliteOpenHelper);
    }
}
