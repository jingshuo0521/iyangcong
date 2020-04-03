package com.iyangcong.reader.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iyangcong.reader.utils.ShareUtils;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.j256.ormlite.misc.JavaxPersistenceConfigurer;
import com.j256.ormlite.misc.JavaxPersistenceImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import com.orhanobut.logger.Logger;

import java.sql.SQLException;
import java.util.Arrays;

/**
 * Created by WuZepeng on 2017-05-05.
 */

public class DatabaseUtil {
	public static final String TAG = "";

	public enum OPERATION_TYPE{
		ADD,
		DELETE;
	}
	public static <T> void upgradeTable(SQLiteDatabase db, ConnectionSource cs, Class<T> clazz, OPERATION_TYPE type){
//		long userId = SharedPreferenceUtil.getInstance().getLong(SharedPreferenceUtil.USER_ID, 0);
//		String userStr = String.valueOf(userId);
		String tableName = extractTableName(clazz);

		db.beginTransaction();

		try {
			//Rename table
			String tempTableName = tableName + "_temp";
			String sql = "ALTER TABLE " + tableName + " RENAME TO " + tempTableName;
			db.execSQL(sql);
			//remove the old table;
			sql = "DROP TABLE IF EXISTS " + tableName;
			db.execSQL(sql);
			//Create table
			try {
				sql = TableUtils.getCreateTableStatements(cs,clazz).get(0);
				db.execSQL(sql);
//				TableUtils.createTable(cs, clazz);
			} catch (Exception e) {
				e.printStackTrace();
				TableUtils.createTable(cs,clazz);
			}

			String columns;
			if(type == OPERATION_TYPE.ADD){
				columns = Arrays.toString(getColumnNames(db,tempTableName)).replace("[","").replace("]","");
			}else if(type == OPERATION_TYPE.DELETE){
				columns = Arrays.toString(getColumnNames(db,tempTableName)).replace("[","").replace("]","");
			}else {
				throw new IllegalArgumentException("OPERATION_TYPE error");
			}
			sql = "INSERT INTO " + tableName +
					"(" + columns + ")" +
					" SELECT " + columns + " FROM " + tempTableName;
			db.execSQL(sql);

//			Drop temp table
			sql = "DROP TABLE IF EXISTS " + tempTableName;
			db.execSQL(sql);

			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * 获取表名
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	private static <T> String extractTableName(Class<T> clazz) {
		DatabaseTable databaseTable = clazz.getAnnotation(DatabaseTable.class);
		String name = null;
		if(databaseTable != null && databaseTable.tableName() != null && databaseTable.tableName().length()>0){
			name = databaseTable.tableName();
		}else {
			if( name == null){
				name = clazz.getSimpleName().toLowerCase();
			}
		}
		return name;
	}

	/**
	 * 获取表的列名
	 * @param sqLiteDatabase
	 * @param tableName
	 * @return
	 */
	private static String[] getColumnNames(SQLiteDatabase sqLiteDatabase, String tableName){
		String[] columnNames = null;
		Cursor cursor = null;
		try {
			Logger.e("shao-----");
			cursor = sqLiteDatabase.rawQuery("PRAGMA table_info(" + tableName + ")",null);
			if(cursor != null){
				int columnIndex = cursor.getColumnIndex("name");
				if(columnIndex == -1)
					return null;

				int index = 0;
				columnNames = new String[cursor.getCount()];
				for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
					columnNames[index] = cursor.getString(columnIndex);
					index++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(cursor != null){
				cursor.close();
			}
		}
		return columnNames;
	}
}
