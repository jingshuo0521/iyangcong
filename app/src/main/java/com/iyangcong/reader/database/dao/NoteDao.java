package com.iyangcong.reader.database.dao;

import com.iyangcong.reader.bean.BookNote;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

/**
 * Created by Administrator on 2017/5/5.
 */

public class NoteDao extends BaseDao<BookNote> {
    /**
     * @param sqliteOpenHelper using this to get instance of  DatabaseHelper
     */
    public NoteDao(OrmLiteSqliteOpenHelper sqliteOpenHelper) {
        super(sqliteOpenHelper);
    }
}
