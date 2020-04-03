package com.iyangcong.reader.database.dao;

import com.iyangcong.reader.bean.BookMarker;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

/**
 * Created by DarkFlameMaster on 2017/5/2.
 */

public class MarkerDao extends BaseDao<BookMarker>  {
    /**
     * @param sqliteOpenHelper using this to get instance of  DatabaseHelper
     */
    public MarkerDao(OrmLiteSqliteOpenHelper sqliteOpenHelper) {
        super(sqliteOpenHelper);
    }
}
