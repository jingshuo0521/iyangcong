package org.geometerplus.android.fbreader.bookmark;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.widget.NormalDialog;
import com.iyangcong.reader.R;
import com.iyangcong.reader.base.BaseActivity;
import com.iyangcong.reader.bean.BookInfo;
import com.iyangcong.reader.bean.BookNote;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.callback.JsonCallback2;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.BookInfoDao;
import com.iyangcong.reader.database.dao.NoteDao;
import com.iyangcong.reader.interfaceset.DeviceType;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.LimitedEdittext;
import com.iyangcong.reader.utils.BookInfoUtils;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.DialogUtils;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.GetRequest;
import com.orhanobut.logger.Logger;

import org.geometerplus.android.fbreader.api.FBReaderIntents;
import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.fbreader.book.Bookmark;
import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.application.ZLApplication;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

public class EditNoteActivity extends BaseActivity {
    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.btnFunction1)
    ImageButton btnFunction1;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.tv_note)
    TextView tvNote;
    @BindView(R.id.et_note)
    LimitedEdittext etNote;
    @BindView(R.id.open)
    TextView open;
    @BindView(R.id.activity_edit_note)
    RelativeLayout activityEditNote;
    @BindView(R.id.ll_note_content)
    LinearLayout mLlNoteContent;
    @BindView(R.id.iv_is_public)
    ImageView mIvIsPublic;
    @BindView(R.id.changeStatus)
    RelativeLayout mChangeStatus;
    @BindView(R.id.tv_comment_count)
    TextView mTvCommentCount;

    private BookNote newNote;

    private Bookmark myBookmark;
    private NoteDao noteDao;
    private final BookCollectionShadow myCollection = new BookCollectionShadow();
    private SharedPreferenceUtil sharedPreferenceUtil;
    private int mChapterId;
    private String currentBookUid = "";
    private String offsets = "";
    private STATE state;
    private BookInfoDao bookInfoDao;
    private FBReaderApp tmpApp;
    private String paragraphText = "";
    private int isPublic; // 0:私密 1:公开

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        ButterKnife.bind(this);
        noteDao = new NoteDao(DatabaseHelper.getHelper(context));
        initView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        tmpApp = (FBReaderApp) ZLApplication.Instance();
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
        myBookmark = FBReaderIntents.getBookmarkExtra(getIntent());
        int tempInt1 = getIntent().getIntExtra(Constants.CHAPTERID, -1);
        int tempInt2 = sharedPreferenceUtil.getInt(SharedPreferenceUtil.CHAPTERID, -1);
        currentBookUid = getIntent().getStringExtra(Constants.BOOK_UID);
        mChapterId = tempInt1 != -1 ? tempInt1 : ((tempInt2 != -1) ? tempInt2 : -1);
        state = getIntent().getBooleanExtra("isCreate", true) ? STATE.NEW : STATE.UPDATE;
        Logger.e("wzp state:%s%n", state.name());
    }

    @Override
    protected void initView() {
        setMainHeadView();

        if (myBookmark != null) {
            tvNote.setText(myBookmark.getText());
            etNote.setText(myBookmark.getOriginalText());
            newNote = getBookNote(myBookmark);
            mTvCommentCount.setText(myBookmark.getOriginalText().length() + "/1000");
        } else {
            if(tmpApp!=null) {
                tvNote.setText(tmpApp.getTextView().getSelectedSnippet().getText());
                mTvCommentCount.setText(tmpApp.getTextView().getSelectedSnippet().getText().length() + "/1000");
            }
        }
        etNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mTvCommentCount.setText(charSequence.length() + "/1000");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        newNote = newNote == null ? new BookNote() : newNote;
        // TODO: 2019/9/3
        isPublic = newNote.getIscommon(); // 默认为0 私密
        setNoteIsPublic(isPublic);
//        mIvIsPublic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                isPublic = isPublic == 1 ? 0 : 1;
//                setNoteIsPublic(isPublic);
//            }
//        });
        mChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPublic = isPublic == 1 ? 0 : 1;
                setNoteIsPublic(isPublic);
            }
        });
        bookInfoDao = new BookInfoDao(DatabaseHelper.getHelper(this));
    }

    /**
     * 根据公开私密情况刷新ui
     * @param isPublic
     */
    protected void setNoteIsPublic(int isPublic) {
        if (isPublic == 1) {
            mIvIsPublic.setImageResource(R.drawable.ic_public);
            open.setText("公开");
        } else {
            mIvIsPublic.setImageResource(R.drawable.ic_private);
            open.setText("私密");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myCollection.unbind();
        ZLApplication.Instance().runAction(ActionCode.SELECTION_CLEAR);
    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText("编辑笔记");
        btnBack.setImageResource(R.drawable.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnFunction.setImageResource(R.drawable.ic_send);
        btnFunction.setVisibility(View.VISIBLE);
    }

    /**
     *
     * @param orginalText
     * @param noteComment
     * @param commentId  对应m_id
     * @param id  对应真正的commentid
     */
    public void updateBookNote(String orginalText, String noteComment, int commentId, int id) {
        int bookId = (int) sharedPreferenceUtil.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0);
        newNote.setChapterId(mChapterId);
        newNote.setNote(orginalText);
        newNote.setNoteComment(noteComment);
        newNote.setCommentId(commentId);
        if (isPublic == 1) {
            newNote.setStuCommentsId(id);
            newNote.setIscommon(1);
        }
        newNote.setBookId(bookId);
        newNote.setLanguage(sharedPreferenceUtil.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, -1));
        newNote.setSegmentNum(myBookmark.getParagraphIndex());
        updateNoteDao(newNote);
    }

    private BookNote getBookNote(Bookmark myBookmark) {
        BookNote temp = null;
        List<BookNote> willChangeNote = noteDao.queryByColumn("bookId", (int) sharedPreferenceUtil.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0), "language", sharedPreferenceUtil.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 0));
        for (BookNote note : willChangeNote) {
            if (note.getSegmentNum() == myBookmark.getParagraphIndex() && note.getNote().equals(myBookmark.getText())) {
                temp = note;
            }
        }
        state = (temp == null) ? STATE.NEW : STATE.UPDATE;
        Logger.e("wzp state:%s%n", state.name());
        return temp;
    }

    private void updateNote() {
        List<BookNote> bookNoteList = noteDao.queryByColumn("note", myBookmark.getText().replace("'","''"));
        if(bookNoteList!=null) {
            postNote(bookNoteList.get(0), etNote.getText().toString());
        }
    }

    private void postNote(final BookNote newNote, String newContent) {
        if (newNote.getCommentId() == 0) {
            ToastCompat.makeText(this, "更新失败，稍后再试", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoadingDialog();
        GetRequest eidtReq = getEditNoteRequest(newNote, newContent);
        if (eidtReq!=null){
            eidtReq.execute(new StringCallback() {
                @Override
                public void onSuccess(String s, Call call, Response response) {
                    dismissLoadingDialig();
                    ToastCompat.makeText(EditNoteActivity.this, "编辑成功", Toast.LENGTH_SHORT).show();
                    postService(myCollection);
                    newNote.setIscommon(isPublic);
//                    newNote.setStuCommentsId();
                    updateNoteDao(newNote);
                    finish();
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    dismissLoadingDialig();
                }
            });
        }
//        OkGo.get(Urls.FBReaderUpdateBookNoteURL)
//                .tag(this)
//                .params("commentsId", newNote.getCommentId())
//                .params("newContent", newContent)
//                .execute(new StringCallback() {
//                    @Override
//                    public void onSuccess(String s, Call call, Response response) {
//                        dismissLoadingDialig();
//                        ToastCompat.makeText(EditNoteActivity.this, "编辑成功", Toast.LENGTH_SHORT).show();
//                        postService(myCollection);
//                        finish();
//                    }
//
//                    @Override
//                    public void onError(Call call, Response response, Exception e) {
//                        dismissLoadingDialig();
//                    }
//                });
    }

    /**
     * 根据id和mid判断返回对应的笔记编辑请求
     * @param note
    (如果commentId和m_id都传了，则以m_id为准)
    (如果只传commentId，则以commentId为准)
    (如果只传m_id，则以m_id为准)
     * 	commentId
     * 	m_id
     * 	isConverse（是否要转换笔记的状态）
     * 	content（修改后的笔记内容））
     * @return
     *
     */
    private GetRequest getEditNoteRequest(BookNote note, String content){
        int isConverse =  note.getIscommon() == isPublic ? 0 : 1;
        if (note.getIscommon() == 1) {
            return OkGo.get(Urls.FBReaderEditBookNoteURL)
                    .tag(this)
                    .params("commentId", note.getStuCommentsId())
                    .params("m_id", note.getCommentId())
                    .params("isConverse", isConverse)
                    .params("content", content);
        }else if(note.getIscommon() == 0){
            return OkGo.get(Urls.FBReaderEditBookNoteURL)
                    .tag(this)
                    .params("m_id", note.getCommentId())
                    .params("isConverse", isConverse)
                    .params("content", content);
        }
        return null;
    }

    private void postService(BookCollectionShadow shadow) {
        shadow.bindToService(this, new Runnable() {
            public void run() {
                myBookmark.setText(tvNote.getText().toString() + "$%^&*");
                myBookmark.setMyOriginalText(etNote.getText().toString());
                myCollection.saveBookmark(myBookmark);
            }
        });
    }

    @OnClick({R.id.btnBack, R.id.btnFunction})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnFunction:
                //检查字符串里是否只有空白的字符
                BookNoteChecker tmpChecker = new BookNoteCheckerImpl();
                if (TextUtils.isEmpty(etNote.getText().toString())) {
                    NormalDialog dialog = new NormalDialog(context);
                    DialogUtils.setAlertDialogOneButtonStyle(dialog, getString(R.string.tips), getString(R.string.input_note_comment));
                    return;
                } else if (tmpChecker.isOnlySpace(etNote.getText().toString())) {
                    NormalDialog dialog = new NormalDialog(context);
                    DialogUtils.setAlertDialogOneButtonStyle(dialog, getString(R.string.tips), getString(R.string.note_comment_cannot_empty));
                    return;
                } else if(etNote.getText().toString().length()>1000){
                    NormalDialog dialog = new NormalDialog(context);
                    DialogUtils.setAlertDialogOneButtonStyle(dialog, getString(R.string.tips), getString(R.string.note_too_long));
                    return;
                }
                else{
                    btnFunction.setEnabled(false);
                    if (state == STATE.NEW) {
                        myBookmark = tmpApp.addSelectionBookmark();
                        offsets = BookInfoUtils.getStartAndEndOffset(tmpApp, myBookmark, SharedPreferenceUtil.getInstance().getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 2));
                        paragraphText = BookInfoUtils.getParagraphTextByIndex(tmpApp, myBookmark != null ? myBookmark.getParagraphIndex() : 0, true);
                        if (paragraphText != null && paragraphText.length() > 100) {
                            paragraphText = paragraphText.substring(0, 100);
                        }
                        uploadBookNote(myBookmark, newNote);
                    } else {
                        updateNote();
                    }
                }


                //暂停传笔记，因为目前安卓的段落和后台的段落对不上,目前只在本地保存
//				updateBookNote(tvNote.getText().toString(),etNote.getText().toString(),-1);

//				ToastCompat.makeText(this,"已保存",Toast.LENGTH_SHORT).show();
//				finish();
                break;
        }
    }


    public void uploadBookNote(final Bookmark bookmark, final BookNote bookNote) {
        if (!CommonUtil.getLoginState()) {
            return;
        }
        if (mChapterId == -1) {
            ToastCompat.makeText(context, "上传失败", Toast.LENGTH_SHORT).show();
            return;
        }
        final long bookId = sharedPreferenceUtil.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, -1);
        int semesterId = sharedPreferenceUtil.getInt(SharedPreferenceUtil.SEMESTER_ID, -1);
        final int readingRecordLogId = sharedPreferenceUtil.getInt(SharedPreferenceUtil.READING_RECORD_LODID, -1);
        final int languageType = sharedPreferenceUtil.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, -1);
        final long userId = sharedPreferenceUtil.getLong(SharedPreferenceUtil.USER_ID, -1);
        List<BookInfo> bookInfos = bookInfoDao.queryByColumn("bookId", bookId);
        if (bookInfos.size() == 0) {
            return;
        }
//		final int paragraphId = bookInfos.get(0).getSegmentId() + bookmark.getParagraphIndex() + /*BookInfoUtils.getBooksFisrtChapterId(bookInfos.get(0))*/ -mChapterId;

        //理论上，tmpApp应该是满足不为null的。
        final int paragraphId;
        if (tmpApp != null) {
//			relativeParagraphId = BookInfoUtils.getTextRelativeParagraphIndexWithParagraphIndex(tmpApp, myBookmark.getParagraphIndex());
            paragraphId = BookInfoUtils.getAbsoluteParagraphId(tmpApp, myBookmark.getParagraphIndex());
//			paragraphId = relativeParagraphId + BookInfoUtils.getBeginSegmentId(this);
        } else {
            paragraphId = bookInfos.get(0).getSegmentId() + bookmark.getParagraphIndex() + /*BookInfoUtils.getBooksFisrtChapterId(bookInfos.get(0))*/ -mChapterId;
        }
        if (paragraphId < 0) {
            ToastCompat.makeText(this, "添加笔记失败！", Toast.LENGTH_SHORT);
            return;
        }
        Logger.e("wzp生成笔记时 paragraphIndex:%d paragraphId:%d", bookmark.getParagraphIndex(), paragraphId);
        final int startOffset = bookmark.getElementIndex();
        final int endOffset = bookmark.getEnd().getElementIndex();
        String[] offset = offsets.split(",");
        // TODO: 2019/9/5 为何会有两种偏移量
        Logger.e("wzp bookmarker offsets:%s%n", offsets);
        Logger.e("wzp bookmarker startOffset:", startOffset + "" );
        Logger.e("wzp bookmarker endOffset:", endOffset + "" );
        Logger.e("wzp bookmarker offsets2:", (endOffset - startOffset) );
        showLoadingDialog();
        OkGo.post(Urls.FBReaderAddBookNoteURL)
                .tag(this)
                .params("commentedcontent", bookmark.getText())
                .params("content", etNote.getText().toString())
                .params("userid", userId)
                .params("bookid", bookId)
                .params("chapterid", mChapterId)
                .params("segmentid", paragraphId)
                .params("startoffset", offset[0])
                .params("endoffset", offset[1])
                .params("languagetype", languageType)
                .params("terminal", DeviceType.ANDROID_3)
                .params("iscommon", isPublic)
                .execute(new JsonCallback2<NoteResponse>(NoteResponse.class) {
                    @Override
                    public void onSuccess(NoteResponse noteResponse, Call call, Response response) {
                        if (isNull(noteResponse) || noteResponse.getStatus() != 1){
                            ToastCompat.makeText(EditNoteActivity.this, noteResponse ==  null ? "返回参数为空" : noteResponse.getComments(), Toast.LENGTH_SHORT).show();
                            dismissLoadingDialig();
                            return;
                        }
                        postService(myCollection);
//						ToastCompat.makeText(EditNoteActivity.this, commentIdIycResponse.getMsg(), Toast.LENGTH_SHORT).show();
                        updateBookNote(bookmark.getText(), etNote.getText().toString(), noteResponse.getM_id(), noteResponse.getId());
//                        updateNoteDao(bookNote);
                        dismissLoadingDialig();
                        finish();
                    }
                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissLoadingDialig();
                        myCollection.deleteBookmark(bookmark);
                        ToastCompat.makeText(EditNoteActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
//        OkGo.get(Urls.FBReaderOrdinaryAddBookNoteURL)
//                .tag(this)
//                .params("bookid", bookId)
//                .params("chapterid", mChapterId)
//                .params("chapterIndex", mChapterId)
//                .params("commentedcontent", bookmark.getText())
//                .params("endoffset", offset[1])
//                .params("languagetype", languageType)
//                .params("note", etNote.getText().toString())
//                .params("segmentid", paragraphId)
//                .params("startoffset", offset[0])
//                .params("terminal", DeviceType.ANDROID_3)
//                .params("type", "1")
//                .params("segmentStr", paragraphText)
//                .params("userid", userId)
//                .execute(new JsonCallback<IycResponse<CommentId>>(this) {
//                    @Override
//                    public void onSuccess(IycResponse<CommentId> commentIdIycResponse, Call call, Response response) {
//                        if (isNull(commentIdIycResponse) || isNull(commentIdIycResponse.getData())) {
//                            ToastCompat.makeText(EditNoteActivity.this, commentIdIycResponse.getMsg(), Toast.LENGTH_SHORT).show();
//                            dismissLoadingDialig();
//                            return;
//                        }
//                        postService(myCollection);
////						ToastCompat.makeText(EditNoteActivity.this, commentIdIycResponse.getMsg(), Toast.LENGTH_SHORT).show();
//                        updateBookNote(bookmark.getText(), etNote.getText().toString(), commentIdIycResponse.getData().getCommentid());
//                        postStudentNoteInfo(bookmark, bookNote, bookId, languageType, userId, paragraphId, startOffset, endOffset, readingRecordLogId);
//                    }
//
//                    @Override
//                    public void onError(Call call, Response response, Exception e) {
//                        dismissLoadingDialig();
//                        myCollection.deleteBookmark(bookmark);
//                        ToastCompat.makeText(EditNoteActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
////		final int readingRecordLogId = sharedPreferenceUtil.getInt(SharedPreferenceUtil.READING_RECORD_LODID, 0);
////        if (semesterId != 0 && readingRecordLogId != 0) {
////			postStudentNoteInfo(bookmark,bookNote,bookId,languageType,userId,paragraphId,startOffset,endOffset,readingRecordLogId);
////        }
    }

    // 已经弃用
    private void postStudentNoteInfo(final Bookmark bookmark, final BookNote bookNote, long bookId, int languageType, final long userId, int paragraphId, int startOffset, int endOffset, final int readingRecordLogId) {
        OkGo.get(Urls.FBReaderStudentAddBookNote1URL)
                .tag(this)
                .params("bookid", bookId)
                .params("chapterid", mChapterId)
                .params("chapter_index", mChapterId)
                .params("commentedcontent", bookmark.getText())
                .params("endoffset", endOffset)
                .params("languagetype", languageType)
                .params("segmentid", paragraphId)
                .params("startoffset", startOffset)
                .params("segmentStr", paragraphText)
                .params("type", "1")
                .execute(new JsonCallback<IycResponse<NoteResult>>(this) {
                    @Override
                    public void onSuccess(IycResponse<NoteResult> noteResultIycResponse, Call call, Response response) {
                        NoteResult result = noteResultIycResponse.getData();
                        if (noteResultIycResponse.getData().getCommentedcontentid() == 0) {
                            finish();
                            dismissLoadingDialig();
//							ToastCompat.makeText(EditNoteActivity.this, getString(R.string.addnote_failed), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        OkGo.get(Urls.FBReaderStudentAddBookNote2URL)
                                .tag(this)
                                .params("commentedcontentid", result.getCommentedcontentid())
                                .params("content", etNote.getText().toString())
                                .params("commentedcontentString", bookmark.getText())
                                .params("readingrecordlogid", readingRecordLogId)
                                .params("chapterIndex", mChapterId)
                                .params("userid", userId)
                                .execute(new JsonCallback<IycResponse<CommentId>>(EditNoteActivity.this) {
                                    @Override
                                    public void onSuccess(IycResponse<CommentId> commentIdIycResponse, Call call, Response response) {
                                        if (isNull(commentIdIycResponse) || isNull(commentIdIycResponse.getData())) {
                                            ToastCompat.makeText(EditNoteActivity.this, commentIdIycResponse.getMsg(), Toast.LENGTH_SHORT).show();
                                            dismissLoadingDialig();
                                            return;
                                        }
                                        //后台写的时候，学生用户的commentId就是stuCommentId;
                                        bookNote.setStuCommentsId(commentIdIycResponse.getData().getCommentid());
                                        updateNoteDao(bookNote);
                                        Logger.e("wzp 传笔记结束拉！！！！！！");
                                        dismissLoadingDialig();
                                        finish();
                                    }

                                    @Override
                                    public void onError(Call call, Response response, Exception e) {
                                        ToastCompat.makeText(EditNoteActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        dismissLoadingDialig();
                                    }

                                    @Override
                                    public void parseError(Call call, Exception e) {
                                        super.parseError(call, e);
                                        Logger.e("wzp 调用学生用户笔记上传接口异常信息：" + e.getMessage());
                                    }
                                });
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissLoadingDialig();
                        ToastCompat.makeText(EditNoteActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Logger.e("wzp 调用学生用户笔记上传接口异常信息：" + e.getMessage());
                    }

                    @Override
                    public void parseError(Call call, Exception e) {
                        super.parseError(call, e);
                        Logger.e("wzp 调用学生用户笔记上传接口异常信息：" + e.getMessage());
                    }
                });
    }

    private void updateNoteDao(BookNote bookNote) {
        List<BookNote> bookNotes = noteDao.queryByColumn("note", bookNote.getNote());
        if (bookNotes != null && bookNotes.size() > 0) {
            BookNote bookNote1 = bookNotes.get(0);
            bookNote1.setNoteComment(bookNote.getNoteComment());
            bookNote1.setStuCommentsId(bookNote.getStuCommentsId());
            bookNote1.setIscommon(bookNote.getIscommon());
            noteDao.update(bookNote1);
            state = STATE.UPDATE;
        } else {
            noteDao.add(bookNote);
            state = STATE.NEW;
        }

    }

    class NoteResponse {
        private int status; // 请求状态
        private int id; // 笔记id（私密笔记没有此项）
        private int m_id; // 笔记的m_id
        private String comments; // 请求信息
        private int commentid;
        private int chapterIndex;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getM_id() {
            return m_id;
        }

        public void setM_id(int m_id) {
            this.m_id = m_id;
        }

        public int getStatus() {
            return status;
        }

        public String getComments() {
            return comments;
        }
    }

    class CommentId {
        private int commentid;
        private int stuCommentsId;

        public int getCommentid() {
            return commentid;
        }

        public void setCommentid(int commentid) {
            this.commentid = commentid;
        }

        public int getStuCommentsId() {
            return stuCommentsId;
        }

        public void setStuCommentsId(int stuCommentsId) {
            this.stuCommentsId = stuCommentsId;
        }
    }

    class NoteResult {
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

    public enum STATE {
        NEW,
        UPDATE,
    }
}
