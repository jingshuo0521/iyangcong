package com.iyangcong.reader.database.dao;

import com.iyangcong.reader.bean.ShelfGroup;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

/**
 * Created by ljw on 2017/3/21.
 */

public class GroupDao extends BaseDao<ShelfGroup> {
    /**
     * @param sqliteOpenHelper using this to get instance of  DatabaseHelper
     */
    public GroupDao(OrmLiteSqliteOpenHelper sqliteOpenHelper) {
        super(sqliteOpenHelper);
    }
}
