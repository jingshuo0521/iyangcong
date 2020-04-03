package org.geometerplus.android.fbreader.bookmark;

import java.util.regex.Pattern;

/**
 * Created by WuZepeng on 2018-01-08.
 */

public class BookNoteCheckerImpl implements BookNoteChecker {
	//检查笔记的内容是否仅含有空白的字符串，包括[\f\n\r\t\v]
	@Override
	public boolean isOnlySpace(String noteComemnt) {
		return noteComemnt.replaceAll("\\s","").equals("");
	}
}
