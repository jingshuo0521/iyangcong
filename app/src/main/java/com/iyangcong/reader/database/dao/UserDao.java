package com.iyangcong.reader.database.dao;

import com.iyangcong.reader.bean.User;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

/**
 * Created by ljw on 2017/3/16.
 */

public class UserDao extends BaseDao<User> {
    /**
     * @param sqliteOpenHelper using this to get instance of  DatabaseHelper
     */
    public UserDao(OrmLiteSqliteOpenHelper sqliteOpenHelper) {
        super(sqliteOpenHelper);
    }
}
