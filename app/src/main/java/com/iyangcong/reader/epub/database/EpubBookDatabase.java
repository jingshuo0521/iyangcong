package com.iyangcong.reader.epub.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by WuZepeng on 2018-01-02.
 */

public class  EpubBookDatabase {

	private DaoSession mSession;

	public EpubBookDatabase(Context context) {
		DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "book-builder", null);
		SQLiteDatabase db = helper.getWritableDatabase();
		DaoMaster daoMaster = new DaoMaster(db);
		mSession = daoMaster.newSession();
	}

	public DaoSession getSession() {
		return mSession;
	}

	public void onDestory(){

	}
}
