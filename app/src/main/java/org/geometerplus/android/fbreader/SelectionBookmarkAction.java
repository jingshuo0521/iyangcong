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

package org.geometerplus.android.fbreader;

import android.content.Intent;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.OnClickWrapper;
import com.iyangcong.reader.bean.BookNote;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.NoteDao;
import com.iyangcong.reader.interfaceset.DeviceType;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.utils.BookInfoUtils;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import org.geometerplus.android.fbreader.api.FBReaderIntents;
import org.geometerplus.android.fbreader.bookmark.EditNoteActivity;
import org.geometerplus.android.util.OrientationUtil;
import org.geometerplus.fbreader.book.Bookmark;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.resources.ZLResource;

import java.util.List;

import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.Constants.CHAPTERID;

class SelectionBookmarkAction extends FBAndroidAction {
    private NoteDao noteDao;

    SelectionBookmarkAction(FBReader baseApplication, FBReaderApp fbreader) {
        super(baseApplication, fbreader);
        noteDao = new NoteDao(DatabaseHelper.getHelper(baseApplication));
    }

    @Override
    protected void run(Object... params) {
        final Bookmark bookmark;
        if (params.length != 0) {
            bookmark = (Bookmark) params[0];
        } else {
            bookmark = Reader.addSelectionBookmark();
            uploadBookNote(bookmark);
        }
        if (bookmark == null) {
            return;
        }

        final SuperActivityToast toast =
                new SuperActivityToast(BaseActivity, SuperToast.Type.BUTTON);
        toast.setText(bookmark.getText());
        toast.setDuration(SuperToast.Duration.EXTRA_LONG);
        toast.setButtonIcon(
                android.R.drawable.ic_menu_edit,
                ZLResource.resource("dialog").getResource("button").getResource("edit").getValue()
        );
        toast.setOnClickWrapper(new OnClickWrapper("bkmk", new SuperToast.OnClickListener() {
            @Override
            public void onClick(View view, Parcelable token) {
                final Intent intent =
                        new Intent(BaseActivity.getApplicationContext(), EditNoteActivity.class);
                intent.putExtra(CHAPTERID, BookInfoUtils.getRelativeChapterId(Reader, BaseActivity.getApplicationContext()));
                FBReaderIntents.putBookmarkExtra(intent, bookmark);
                OrientationUtil.startActivity(BaseActivity, intent);
            }
        }));
        BaseActivity.showToast(toast);
    }

    private void uploadBookNote(final Bookmark bookmark) {
        final SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance();
        int semesterId = sp.getInt(SharedPreferenceUtil.SEMESTER_ID, 0);
        final BookNote bookNote = new BookNote();
        bookNote.setBookId((int) sp.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0));
        bookNote.setLanguage(sp.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 0));
        if (semesterId == 0) {
            OkGo.get(Urls.FBReaderOrdinaryAddBookNoteURL)
                    .tag(this)
                    .params("bookid", sp.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0))
                    .params("chapterid", BookInfoUtils.getRelativeChapterId(Reader, SelectionBookmarkAction.super.BaseActivity))
                    .params("chapterIndex", BookInfoUtils.getRelativeChapterId(Reader, SelectionBookmarkAction.super.BaseActivity))
                    .params("commentedcontent", bookmark.getText())
                    .params("endoffset", bookmark.getEnd().getElementIndex())
                    .params("languagetype", sp.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 0))
                    .params("note", bookmark.getText())
                    .params("segmentid", bookmark.getParagraphIndex() + BookInfoUtils.getBeginSegmentId(SelectionBookmarkAction.super.BaseActivity))
                    .params("startoffset", bookmark.getEnd().getElementIndex() - bookmark.getLength())
                    .params("terminal", DeviceType.ANDROID_3)
                    .params("type", "1")
                    .params("userid", sp.getLong(SharedPreferenceUtil.USER_ID, 0))
                    .execute(new JsonCallback<IycResponse<commentId>>(SelectionBookmarkAction.super.BaseActivity) {
                        @Override
                        public void onSuccess(IycResponse<commentId> commentIdIycResponse, Call call, Response response) {
                            bookNote.setCommentId(commentIdIycResponse.getData().getCommentid());
                            bookNote.setSegmentNum(bookmark.getParagraphIndex());
                            bookNote.setNote(bookmark.getText());
                            bookNote.setChapterId(BookInfoUtils.getRelativeChapterId(Reader, SelectionBookmarkAction.super.BaseActivity));
                            noteDao.add(bookNote);
                            ToastCompat.makeText(SelectionBookmarkAction.super.BaseActivity, commentIdIycResponse.getMsg(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            ToastCompat.makeText(SelectionBookmarkAction.super.BaseActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            OkGo.get(Urls.FBReaderStudentAddBookNote1URL)
                    .tag(this)
                    .params("bookid", sp.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0))
                    .params("chapterid", BookInfoUtils.getRelativeChapterId(Reader, SelectionBookmarkAction.super.BaseActivity))
                    .params("commentedcontent", bookmark.getText())
                    .params("chapter_index", BookInfoUtils.getRelativeChapterId(Reader, SelectionBookmarkAction.super.BaseActivity))
                    .params("endoffset", bookmark.getEnd().getElementIndex())
                    .params("languagetype", sp.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 0))
                    .params("segmentid", bookmark.getParagraphIndex() + BookInfoUtils.getBeginSegmentId(SelectionBookmarkAction.super.BaseActivity))
                    .params("startoffset", bookmark.getEnd().getElementIndex() - bookmark.getLength())
                    .params("type", "1")
                    .execute(new JsonCallback<IycResponse<noteResult>>(SelectionBookmarkAction.super.BaseActivity) {
                        @Override
                        public void onSuccess(IycResponse<noteResult> noteResultIycResponse, Call call, Response response) {
                            noteResult result = noteResultIycResponse.getData();

                            OkGo.get(Urls.FBReaderStudentAddBookNote2URL)
                                    .tag(this)
                                    .params("commentedcontentid", result.getCommentedcontentid())
                                    .params("content", bookmark.getText())
                                    .params("commentedcontentString", bookmark.getText())
                                    .params("chapterIndex", BookInfoUtils.getRelativeChapterId(Reader, SelectionBookmarkAction.super.BaseActivity))//这个字段后台没有接收
                                    .params("readingrecordlogid", sp.getInt(SharedPreferenceUtil.READING_RECORD_LODID, 0))
                                    .params("userid", sp.getLong(SharedPreferenceUtil.USER_ID, 0))
                                    .execute(new JsonCallback<IycResponse<commentId>>(SelectionBookmarkAction.super.BaseActivity) {
                                        @Override
                                        public void onSuccess(IycResponse<commentId> commentIdIycResponse, Call call, Response response) {
                                            bookNote.setCommentId(commentIdIycResponse.getData().getCommentid());
                                            bookNote.setSegmentNum(bookmark.getParagraphIndex());
                                            bookNote.setNote(bookmark.getText());
                                            bookNote.setChapterId(BookInfoUtils.getRelativeChapterId(Reader, SelectionBookmarkAction.super.BaseActivity));
                                            noteDao.add(bookNote);
                                            ToastCompat.makeText(SelectionBookmarkAction.super.BaseActivity, commentIdIycResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onError(Call call, Response response, Exception e) {
                                            ToastCompat.makeText(SelectionBookmarkAction.super.BaseActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            ToastCompat.makeText(SelectionBookmarkAction.super.BaseActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    }

    class commentId {
        private int commentid;

        public int getCommentid() {
            return commentid;
        }

        public void setCommentid(int commentid) {
            this.commentid = commentid;
        }
    }

    class noteResult {
        private int commentedcontentid;
        private int count;
        private List<String> commentsList;

        public int getCommentedcontentid() {
            return commentedcontentid;
        }

        public void setCommentedcontentid(int commentedcontentid) {
            this.commentedcontentid = commentedcontentid;
        }

        public List<String> getCommentsList() {
            return commentsList;
        }

        public void setCommentsList(List<String> commentsList) {
            this.commentsList = commentsList;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}
