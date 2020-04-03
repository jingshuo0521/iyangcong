/*
 * Copyright (C) 2007-2015 FBReader.ORG Limited <contact@fbreader.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.fbreader.fbreader;

import com.iyangcong.reader.bean.BookMarker;
import com.iyangcong.reader.bean.ShelfBook;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.BookDao;
import com.iyangcong.reader.utils.BookInfoUtils;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.orhanobut.logger.Logger;

import org.geometerplus.android.fbreader.FBAndroidAction;
import org.geometerplus.android.fbreader.FBReader;
import org.geometerplus.android.fbreader.NoteBookAction;
import org.geometerplus.fbreader.book.Bookmark;
import org.geometerplus.zlibrary.text.view.ZLTextView;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

class ExitAction extends FBAction {
	private BookDao bookDao;

	ExitAction(FBReaderApp fbReaderApp){
		super(fbReaderApp);
	}




	@Override
	protected void run(Object ... params) {
		if (Reader.getCurrentView() != Reader.BookTextView) {
			Reader.showBookTextView();
		} else {
			Reader.closeWindow();
		}
	}


}
