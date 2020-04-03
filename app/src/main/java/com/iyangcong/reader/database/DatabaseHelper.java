package com.iyangcong.reader.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.BookInfo;
import com.iyangcong.reader.bean.BookNote;
import com.iyangcong.reader.bean.NewWord;
import com.iyangcong.reader.bean.ReadingDataBean;
import com.iyangcong.reader.bean.ShelfBook;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.orhanobut.logger.Logger;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Database helper class used to manage the creation and upgrading of your database.
 * This class also usually provide the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private Context mContext;


    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "_book.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 12;


    private static DatabaseHelper instance;

    private Map<String, Dao> daos = new HashMap<String, Dao>();

    private static String databaseName;


    /**
     * 获取单例
     *
     * @param context
     * @return
     */
    public static synchronized DatabaseHelper getHelper(Context context) {
        if (context == null) {
            return null;
        }
        context = context.getApplicationContext();
        long userId;
        if (CommonUtil.getLoginState()) {
            userId = SharedPreferenceUtil.getInstance().getLong(SharedPreferenceUtil.USER_ID, 0);
        } else {
            userId = 0;
        }
        boolean hasChangeUserId = !(userId == SharedPreferenceUtil.getInstance().getLong(SharedPreferenceUtil.PRE_USER_ID, 0));
        if (hasChangeUserId) {
            instance = null;
        }
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                //hasChangeUserId 判断是否切换用户；
                if (instance == null) {
//                    SharedPreferenceUtil.getInstance().putLong(SharedPreferenceUtil.PRE_USER_ID, userId);
                    String userStr = String.valueOf(userId);
//                    if (!userStr.equals("0")) {
                    databaseName = userStr + DATABASE_NAME;
                    instance = new DatabaseHelper(context);
//                    } else {
//                        ToastCompat.makeText(context, "创建数据库失败", Toast.LENGTH_SHORT);
//                    }
                }
            }
        }
        return instance;
    }

    public synchronized Dao getDao(Class clazz) throws SQLException {
        Dao dao = null;
        String className = clazz.getSimpleName();

        if (daos.containsKey(className)) {
            dao = daos.get(className);
        }
        if (dao == null) {
            dao = super.getDao(clazz);
            daos.put(className, dao);
        }
        return dao;
    }

    private DatabaseHelper(Context context) {
        super(context, databaseName, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            String[] tb = mContext.getResources().getStringArray(R.array.db_tb);
            for (String aTb : tb) {
                Class clazz = Class.forName(aTb);
                TableUtils.createTableIfNotExists(connectionSource, clazz);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource cs,
                          int oldVersion, int newVersion) {
        Logger.i("执行数据库升级操作");

        if (newVersion == 12){
            DatabaseUtil.upgradeTable(db,cs,BookNote.class,DatabaseUtil.OPERATION_TYPE.ADD);
            onCreate(db,cs);
        }else if (newVersion == 11){
            DatabaseUtil.upgradeTable(db,cs,BookNote.class,DatabaseUtil.OPERATION_TYPE.ADD);
            onCreate(db,cs);
        }else if(newVersion == 10) {
            DatabaseUtil.upgradeTable(db,cs, NewWord.class,DatabaseUtil.OPERATION_TYPE.ADD);
            onCreate(db,cs);
        }else if (newVersion == 9){
            onCreate(db,cs);
        }else if (newVersion == 8){
//            DatabaseUtil.upgradeTable(db,cs, NewWord.class,DatabaseUtil.OPERATION_TYPE.ADD);
            onCreate(db,cs);
        }else if (newVersion == 7){
            onCreate(db,cs);
        }else if (newVersion == 6){
            onCreate(db,cs);
        }else if(newVersion == 5){
            DatabaseUtil.upgradeTable(db,cs, BookInfo.class,DatabaseUtil.OPERATION_TYPE.ADD);
            onCreate(db,cs);
        }else if(newVersion == 4){
            DatabaseUtil.upgradeTable(db,cs, BookInfo.class,DatabaseUtil.OPERATION_TYPE.ADD);
            DatabaseUtil.upgradeTable(db,cs, ShelfBook.class,DatabaseUtil.OPERATION_TYPE.ADD);
            onCreate(db,cs);
        }else if(newVersion == 3){
            DatabaseUtil.upgradeTable(db,cs, NewWord.class,DatabaseUtil.OPERATION_TYPE.ADD);
            onCreate(db,cs);
        }else if (newVersion == 2) {
            DatabaseUtil.upgradeTable(db, cs, ShelfBook.class, DatabaseUtil.OPERATION_TYPE.ADD);
            DatabaseUtil.upgradeTable(db, cs, BookNote.class, DatabaseUtil.OPERATION_TYPE.ADD);
            onCreate(db, cs);
        } else {
            try {
                String[] tb = mContext.getResources().getStringArray(R.array.db_tb);
                for (String aTb : tb) {
                    Class clazz = Class.forName(aTb);
                    TableUtils.dropTable(connectionSource, clazz, true);
                }
                onCreate(db, cs);
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void close() {
        super.close();
        mContext = null;
        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao = null;
        }
    }
}
