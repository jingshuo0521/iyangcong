package org.geometerplus.android.fbreader;

import android.content.Intent;

import com.iyangcong.reader.utils.BookInfoUtils;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.SharedPreferenceUtil;

import org.geometerplus.android.fbreader.api.FBReaderIntents;
import org.geometerplus.android.fbreader.bookmark.EditNoteActivity;
import org.geometerplus.android.util.OrientationUtil;
import org.geometerplus.fbreader.book.Bookmark;
import org.geometerplus.fbreader.fbreader.FBReaderApp;

/**
 * Created by WuZepeng on 2017-05-12.
 */

public class NoteBookAction extends SelectionBookmarkAction {

    public NoteBookAction(FBReader baseApplication, FBReaderApp fbreader) {
        super(baseApplication, fbreader);
    }

    @Override
    protected void run(Object... params) {
        Bookmark bookmark;
        if (params.length != 0) {
            bookmark = (Bookmark) params[0];
        } else {
            bookmark = null;
        }
        Intent intent = new Intent(BaseActivity.getApplicationContext(), EditNoteActivity.class);
        intent.putExtra(Constants.CHAPTERID, BookInfoUtils.getAbsChapterId(Reader, NoteBookAction.super.BaseActivity));
        intent.putExtra(Constants.BOOK_UID, Reader.getCurrentBook().getUid());
        intent.putExtra("isCreate", true);
        FBReaderIntents.putBookmarkExtra(intent, bookmark);
        OrientationUtil.startActivity(BaseActivity, intent);
    }
}
