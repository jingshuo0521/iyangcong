package com.iyangcong.reader.adapter;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anarchy.classify.adapter.BaseMainAdapter;
import com.anarchy.classify.adapter.BaseSubAdapter;
import com.anarchy.classify.simple.PrimitiveSimpleAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.iyangcong.reader.MainActivity;
import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.BookDownloadUrl;
import com.iyangcong.reader.bean.BookInfo;
import com.iyangcong.reader.bean.ReadingRecorder;
import com.iyangcong.reader.bean.ShelfBook;
import com.iyangcong.reader.bean.ShelfGroup;
import com.iyangcong.reader.callback.FileCallback;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.BookDao;
import com.iyangcong.reader.database.dao.BookInfoDao;
import com.iyangcong.reader.database.dao.GroupDao;
import com.iyangcong.reader.epub.BookInfoEpubDeleteInfo;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.dragmerge.model.BookDataGroup;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.DialogUtils;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.InvokerDESServiceUitls;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.BaseRequest;
import com.orhanobut.logger.Logger;

import org.geometerplus.android.fbreader.FBReader;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;
import static com.iyangcong.reader.utils.Urls.HAVESENTENCE;
import static com.iyangcong.reader.utils.Urls.READINGRECORDS;

/**
 * Created by lg on 2017/2/21.
 */

public class BookShelfAdapter extends PrimitiveSimpleAdapter<BookDataGroup, BookShelfAdapter.BookShelfViewHolder> {

    private Context mContext;
    private List<BookDataGroup> mLists;
    private List<ShelfBook> mCheckedData = new ArrayList<>();
    private List<ShelfBook> localShelfBookList;
    boolean mEditMode;
    private boolean mSubEditMode;
    private BookShelfObservable mObservable = new BookShelfObservable();
    private SubObserver mSubObserver = new SubObserver(mObservable);
    private SharedPreferenceUtil sharedPreferenceUtil;

    private boolean isShowProgress;
    private GroupDao groupDao;
    private BookDao bookDao;

    private DialogInterface.OnDismissListener mDismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            if (mObservable.isRegister(mSubObserver)) mObservable.unregisterObserver(mSubObserver);
            mSubEditMode = false;
        }
    };

    public BookShelfAdapter(List<BookDataGroup> mLists, Context context, Boolean isShowProgress) {
        this.mLists = mLists;
        this.mContext = context;
        this.isShowProgress = isShowProgress;
        groupDao = new GroupDao(DatabaseHelper.getHelper(mContext));
        bookDao = new BookDao(DatabaseHelper.getHelper(mContext));
        localShelfBookList = bookDao.all();
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
    }

    @Override
    protected boolean canDragOnLongPress(int mainPosition, int subPosition) {
        if (!mEditMode) {
            setEditMode(true);
            return false;
        }
        return true;
    }

    public void registerObserver(BookShelfObserver observer) {
        mObservable.registerObserver(observer);
    }

    public boolean isShowProgress() {
        return isShowProgress;
    }

    public void setShowProgress(boolean showProgress) {
        isShowProgress = showProgress;
    }

    @Override
    protected void onSubDialogShow(Dialog dialog, final int parentPosition) {
        dialog.setOnDismissListener(mDismissListener);
        //当次级窗口显示时需要修改标题
        final ViewGroup contentView = (ViewGroup) dialog.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        final TextView selectAll = (TextView) contentView.findViewById(R.id.text_select_all);
        final TextView title = (TextView) contentView.findViewById(R.id.text_title);
        final TextView tvComplete = (TextView) contentView.findViewById(R.id.tv_complete);
        final EditText editText = (EditText) contentView.findViewById(R.id.edit_title);
        final BookDataGroup mockDataGroup = mLists.get(parentPosition);
        mSubObserver.setBindResource(mockDataGroup, selectAll, getMainAdapter(), getSubAdapter(), parentPosition);
        if (!mObservable.isRegister(mSubObserver)) mObservable.registerObserver(mSubObserver);
        //selectAll.setErrorLayoutVisibility(mEditMode ? mSubEditMode ? View.GONE : View.VISIBLE : View.GONE);
        title.setText(String.valueOf(mockDataGroup.getCategory()));
        editText.setVisibility(View.GONE);


        title.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                mSubEditMode = true;
                //selectAll.setErrorLayoutVisibility(View.GONE);
                title.setVisibility(View.INVISIBLE);
                editText.setVisibility(View.VISIBLE);
                tvComplete.setVisibility(View.VISIBLE);
                editText.setSelection(0, editText.getText().toString().length());
                int originWidth = editText.getWidth();
                editText.setWidth(0);
                TransitionManager.beginDelayedTransition(contentView);
                editText.setWidth(originWidth);
            }
        });
        tvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSubEditMode = false;
                if (editText.getText().toString().length() != 0) {
                    String groupName = editText.getText().toString();
                    mockDataGroup.setCategory(groupName);
                    int size = mockDataGroup.getChildCount();
                    for (int i = 0; i < size; i++) {
                        ShelfBook shelfBook = bookDao.queryByColumn("bookId", mockDataGroup.getChild(i).getBookId()).get(0);
                        shelfBook.setGroupName(groupName);
                        bookDao.update(shelfBook);
                    }
                }
                title.setText(String.valueOf(mockDataGroup.getCategory()));
                title.setVisibility(View.VISIBLE);
                editText.setVisibility(View.GONE);
                tvComplete.setVisibility(View.GONE);
                notifyDataSetChanged();
            }
        });


    }

    class SubObserver extends BookShelfObserver {
        final BookShelfObservable mObservable;
        BookDataGroup mGroup;
        TextView selectAll;
        BaseSubAdapter mSubAdapter;
        BaseMainAdapter mMainAdapter;
        int parentPosition;
        boolean mLastIsAllSelect;

        SubObserver(@NonNull BookShelfObservable observable) {
            mObservable = observable;
        }

        View.OnClickListener allSelectListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int childCount = mGroup.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    ShelfBook child = mGroup.getChild(i);
                    if (!child.getChecked()) {
                        child.setChecked(true);
                        mObservable.notifyItemCheckChanged(true);
                    }
                }
                mSubAdapter.notifyDataSetChanged();
                mMainAdapter.notifyItemChanged(parentPosition,"test");
            }
        };
        View.OnClickListener cancelSelectListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int childCount = mGroup.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    ShelfBook child = mGroup.getChild(i);
                    if (child.getChecked()) {
                        child.setChecked(false);
                        mObservable.notifyItemCheckChanged(false);
                    }
                }
                mSubAdapter.notifyDataSetChanged();
                mMainAdapter.notifyItemChanged(parentPosition,"test");
            }
        };

        void setBindResource(BookDataGroup source, TextView bindView, BaseMainAdapter mainAdapter, BaseSubAdapter subAdapter, int parentPosition) {
            mGroup = source;
            selectAll = bindView;
            mSubAdapter = subAdapter;
            mMainAdapter = mainAdapter;
            this.parentPosition = parentPosition;
            updateBind(true);
        }


        @Override
        public void onChecked(boolean isChecked) {
            updateBind(false);
        }

        private void updateBind(boolean force) {
            boolean isAllSelect = mGroup.getChildCount() == mGroup.getCheckedCount();
            if (force) {
                updateBindInternal(isAllSelect);
                return;
            }
            if (mLastIsAllSelect != isAllSelect) {
                updateBindInternal(isAllSelect);
            }
        }

        private void updateBindInternal(boolean isAllSelect) {
            mLastIsAllSelect = isAllSelect;
            selectAll.setText(isAllSelect ? "取消" : "全选");
            selectAll.setOnClickListener(isAllSelect ? cancelSelectListener : allSelectListener);
        }

    }

    @Override
    protected BookShelfViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container, parent, false);
        return new BookShelfViewHolder(view, mLists);
    }

    @Override
    public View getView(ViewGroup parent, View convertView, int mainPosition, int subPosition) {
        ItemViewHolder itemViewHolder;
        if (convertView == null) {
            itemViewHolder = new ItemViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_book_inner, null);
            itemViewHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
            itemViewHolder.textView = (TextView) convertView.findViewById(R.id.tv_book_kind);
            convertView.setTag(itemViewHolder);
        } else {
            itemViewHolder = (ItemViewHolder) convertView.getTag();
        }
        GlideImageLoader.displayBookCover(mContext, itemViewHolder.imageView, mLists.get(mainPosition).getChild(subPosition).getBookImageUrl());
        mLists.get(mainPosition).getChild(subPosition).getBookImageUrl();
        String bookKind = "";
        switch (mLists.get(mainPosition).getChild(subPosition).getBookType()) {
            case 0:
                itemViewHolder.textView.setVisibility(View.INVISIBLE);
                break;
            case 1:
                bookKind = "试读";
                itemViewHolder.textView.setVisibility(View.VISIBLE);
                break;
            case 2:
                itemViewHolder.textView.setVisibility(View.INVISIBLE);
                break;
            case 3:
                bookKind = "包月";
                itemViewHolder.textView.setVisibility(View.VISIBLE);
                itemViewHolder.textView.setBackgroundColor(mContext.getResources().getColor(R.color.main_color));
                break;
        }
        itemViewHolder.textView.setText(bookKind);
        return convertView;
    }

    @Override
    protected void onItemClick(BookShelfViewHolder viewHolder, int parentIndex, int index) {

        ShelfBook bookData;

        if (index == -1 && mLists.get(parentIndex).getChildCount() == 1) {
            bookData = mLists.get(parentIndex).getChild(0);
        } else {
            bookData = mLists.get(parentIndex).getChild(index);
        }
        if (mEditMode) {
            if (bookData != null) {
                bookData.setChecked(!bookData.getChecked());
                if (bookData.getChecked()) {
                    mCheckedData.add(bookData);
                } else {
                    mCheckedData.remove(bookData);
                }
                mObservable.notifyItemCheckChanged(bookData.getChecked());
                if (index != -1) {
                    getSubAdapter().notifyItemChanged(index,"test");
//                    getSubAdapter().notifyItemChanged(index);
                } else {
                    getMainAdapter().notifyItemChanged(parentIndex,"test");
//                    notifyItemChanged(parentIndex);
                }
            }
        } else {
            if (bookData != null) {
                if(CommonUtil.fileIsExists(bookData.getBookPath() + bookData.getBookId() + ".zh.epub")||
                        CommonUtil.fileIsExists(bookData.getBookPath() + bookData.getBookId() + ".en.epub")){
                    getReadingRecordFromNetwork(bookData);
                }else {
                    ToastCompat.makeText(mContext, "数据已损坏，重新下载中...", Toast.LENGTH_SHORT).show();
                        //如果书籍还没有下载，试读和完整版的都没有下载
                    if (bookData.getBookType()==0||bookData.getBookType()==2) {
                        startDownLoadTask(bookData.getBookId(),2);
                    } else if (bookData.getBookType()==1) {
                        startDownLoadTask(bookData.getBookId(),1);
                    }
                }
            }
        }
    }






    private void startDownLoadTask(final long bookId, int type) {
        OkGo.get(Urls.FileDownloadURL)
                .tag(this)
                .params("bookid", bookId)
                .params("type", type + "")
                .params("userid", CommonUtil.getUserId())
                .execute(new JsonCallback<IycResponse<BookDownloadUrl>>(mContext) {

                    @Override
                    public void onSuccess(IycResponse<BookDownloadUrl> stringIycResponse, Call call, Response response) {
                        BookDownloadUrl bookDownloadUrl = stringIycResponse.getData();
                        String resourceUrl = bookDownloadUrl.getResourceUrl();
                        doDownloadTask(bookId,resourceUrl);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        ToastCompat.makeText(mContext, "获取下载链接失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void doDownloadTask(final long bookId, String resourceUrl) {

        OkGo.get(resourceUrl)
                .tag(this)
                .execute(new FileCallback(bookId + ".zip") {
                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        InvokerDESServiceUitls invoker = new InvokerDESServiceUitls(mContext);
                        invoker.invokerDESEncodeService(bookId);
                        ToastCompat.makeText(mContext, "下载成功", Toast.LENGTH_SHORT).show();
                    }

//                    @Override
//                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
//                        Logger.e("wzp totalSize:%d", totalSize);
//                        ToastCompat.makeText(mContext, "重新下载中...", 1000);
//                    }

                    @Override
                    public void onAfter(File file, Exception e) {
                        super.onAfter(file, e);
                    }
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                    }
                });
    }


    //没有同步到阅读进度时执行这个方法
    private void intentToFBreader(ShelfBook bookData) {
        //跳转之前先确认一下句对功能是否被打开
        OkGo.get(HAVESENTENCE)
                .tag(mContext)
                .params("bookId", bookData.getBookId())
                .execute(new JsonCallback<IycResponse<String>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<String> iycResponse, Call call, Response response) {
                        int currentSentence = Integer.parseInt(iycResponse.getData());
                        sharedPreferenceUtil.putInt(SharedPreferenceUtil.CURRENT_BOOK_HAVESENTENCE, currentSentence);
                        intentToFBreader(bookData, -1, -1, "");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        intentToFBreader(bookData, -1, -1, "");
                        e.getMessage();
                    }


                    @Override
                    public void parseError(Call call, Exception e) {
                        intentToFBreader(bookData, -1, -1, "");
                        super.parseError(call, e);
                    }

                    @Override
                    public void onAfter(IycResponse<String> stringIycResponse, Exception e) {
                        super.onAfter(stringIycResponse, e);
                        ((MainActivity) mContext).dismissLoadingDialig();
                    }
                });

    }

    ;

    /**
     * 跳转到阅读器
     *
     * @param bookData
     * @param paragrahpId 等于-1时，说明没有从网络上请求到阅读进度（极有可能是请求失败了）
     * @param chapterId
     */
    private void intentToFBreader(ShelfBook bookData, int paragrahpId, int chapterId, String segmentStr) {
        Intent intent = new Intent(mContext, FBReader.class);
        intent.putExtra(Constants.PARAGRAHP_ID, paragrahpId);
        intent.putExtra(Constants.CHAPTERID, chapterId);
        intent.putExtra(Constants.PARAGRAPH_TEXT, segmentStr);
        if (bookData.getRecentReadingLanguageType() == 1 || bookData.getSupportLanguage() == 1) {
            intent.putExtra("bookPath", bookData.getBookPath() + bookData.getBookId() + ".zh.epub");
            mContext.startActivity(intent);
            sharedPreferenceUtil.putInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 1);
            sharedPreferenceUtil.putLong(SharedPreferenceUtil.CURRENT_BOOK_ID, bookData.getBookId());
        } else if (bookData.getSupportLanguage() == 2 || (bookData.getSupportLanguage() == 3 && bookData.getRecentReadingLanguageType() != 1)) {
            intent.putExtra("bookPath", bookData.getBookPath() + bookData.getBookId() + ".en.epub");
            mContext.startActivity(intent);
            sharedPreferenceUtil.putInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 2);
            sharedPreferenceUtil.putLong(SharedPreferenceUtil.CURRENT_BOOK_ID, bookData.getBookId());
        } else {
            ToastCompat.makeText(mContext, "图书处理中，请稍后！", 1000);
        }
    }


    private void getReadingRecordFromNetwork(final ShelfBook bookData) {
//        intentToFBreader(bookData);
        long userId = CommonUtil.getUserId();
        if (!CommonUtil.getLoginState()) {
            intentToFBreader(bookData);
            return;
        }
        ((MainActivity) mContext).showLoadingDialog("图书加载中");
        OkGo.get(READINGRECORDS)
                .params("bookid", bookData.getBookId())
                .params("userid", userId)
                .params("languageType", bookData.getRecentReadingLanguageType())
                .execute(new JsonCallback<IycResponse<List<ReadingRecorder>>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<List<ReadingRecorder>> readingRecorderIycResponse, Call call, Response response) {
                        if (!isNull(readingRecorderIycResponse) && !isNull(readingRecorderIycResponse.getData())) {
                            final ReadingRecorder readingRecorder = readingRecorderIycResponse.getData().get(0);
                            final NormalDialog normalDialog = new NormalDialog(mContext);
                            float newProgress = readingRecorder.getPercent() * 100;
                            float oldProgress = bookData.getDownloadProgress();
                            long oldReadTime = bookData.getTimeStamp()/1000;
                            long newReadTime = (readingRecorder.getLeaveTime()==null?0:readingRecorder.getLeaveTime())/1000;
                            float num = newProgress - oldProgress;
                            bookData.setRecentReadingLanguageType(readingRecorder.getLanguageType());
//                            if (num < 1 && num > -1) {
                            if(newReadTime-3 <= oldReadTime){
                                intentToFBreader(bookData);
                            } else {
                                DialogUtils.setAlertDialogNormalStyle(normalDialog, "提示", "此书在其他终端有新的阅读进度,是否同步？");
                                normalDialog.btnText("取消", "同步");
                                normalDialog.setOnBtnClickL(new OnBtnClickL() {
                                    @Override
                                    public void onBtnClick() {
                                        normalDialog.dismiss();
                                        intentToFBreader(bookData);
                                    }
                                }, new OnBtnClickL() {
                                    @Override
                                    public void onBtnClick() {
                                        normalDialog.dismiss();
                                        intentToFBreader(bookData, readingRecorder.getSegmentId(), readingRecorder.getChapterId(), readingRecorder.getSegmentStr());
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        intentToFBreader(bookData);
                        Logger.i(e.getMessage());
                    }

                    @Override
                    public void onAfter(IycResponse<List<ReadingRecorder>> readingRecorderIycResponse, Exception e) {
                        super.onAfter(readingRecorderIycResponse, e);
                        if (isNull(readingRecorderIycResponse) || isNull(readingRecorderIycResponse.getData())) {
                            intentToFBreader(bookData);
                        }
                    }
                });
    }

    /**
     * 从书架移除被选中的书籍
     */
    public void removeAllCheckedBook(boolean isAllSelected) {
        if (isAllSelected) {
            mCheckedData.clear();
            for (BookDataGroup bookDataGroup : mLists) {
                for (int i = 0; i < bookDataGroup.getChildCount(); i++) {
                    mCheckedData.add(bookDataGroup.getChild(i));
                }
            }
        }
        if (mCheckedData.size() == 0) return;
        for (ShelfBook data : mCheckedData) {
            BookDao tmpBookDao = new BookDao(DatabaseHelper.getHelper(mContext));
            tmpBookDao.delete(data);
            BookInfoDao tmpBookInfoDao = new BookInfoDao(DatabaseHelper.getHelper(mContext));
            List<BookInfo> tmpBookInfos = tmpBookInfoDao.queryByColumn("bookId", data.getBookId());
            if (tmpBookInfos != null && tmpBookInfos.size() > 0) {
                for(BookInfo tmpBookInfo:tmpBookInfos){
                    tmpBookInfoDao.delete(tmpBookInfo);
                }
            }
            EventBus.getDefault().post(new BookInfoEpubDeleteInfo(data.getBookId()));
            deleteBookFile(data.getBookPath());
            deleteBookCover(data.getBookImageUrl());
            if (data.getParent() != null) {
                BookDataGroup parent = data.getParent();
                parent.removeChild(data);
                if (parent.getChildCount() == 0) {
                    mLists.remove(parent);
                }
            }
        }
        notifyDataSetChanged();
        getSubAdapter().notifyDataSetChanged();
        mObservable.notifyItemRestore();
    }

    private boolean deleteBookCover(String imagePath){
        File tmpFile = new File(imagePath);
        if(tmpFile.exists()){
            tmpFile.delete();
        }
        return tmpFile.exists();
    }

    private boolean deleteBookFile(String bookPath){
        File tmpFile = new File(bookPath);;
        if(tmpFile.exists()){
            for (String subPath:tmpFile.list()){
                File tmpSubFile = new File(bookPath+subPath);
                tmpSubFile.delete();
            }
            tmpFile.delete();
        }
        return !tmpFile.exists();
    }
    public void setNoHide(){
        mCheckedData = bookDao.all();
        notifyDataSetChanged();
        getSubAdapter().notifyDataSetChanged();
        mObservable.notifyItemRestore();
    }


    public void setEditMode(boolean editMode) {
        mEditMode = editMode;
        if (!editMode) {
            if (mCheckedData.size() > 0) {
                for (ShelfBook data : mCheckedData) {
                    data.setChecked(false);
                }
                mCheckedData.clear();
            }
            mObservable.notifyItemRestore();
        }
        notifyDataSetChanged();
        getSubAdapter().notifyDataSetChanged();
        mObservable.notifyItemEditModeChanged(editMode);
    }

    @Override
    protected int getItemCount() {
        if (mLists == null) return 0;
        return mLists.size();
    }

    @Override
    protected int getSubItemCount(int parentPosition) {
        if (mLists == null) {
            return 0;
        }
        if (parentPosition >= mLists.size()) {
            return 0;
        }
        return mLists.get(parentPosition).getChildCount();
    }

    @Override
    protected BookDataGroup getSubSource(int parentPosition) {
        return mLists.get(parentPosition);
    }

    @Override
    protected boolean canExplodeItem(int position, View pressedView) {
        if (position < mLists.size() && mLists.get(position).getChildCount() > 1) {
            return true;
        }
        return false;
    }

    @Override
    protected void onMove(int selectedPosition, int targetPosition) {
        BookDataGroup list = mLists.remove(selectedPosition);
        mLists.add(targetPosition, list);
    }

    @Override
    protected void onSubMove(BookDataGroup bookDataGroup, int selectedPosition, int targetPosition) {
        bookDataGroup.addChild(targetPosition, bookDataGroup.removeChild(selectedPosition));
    }

    @Override
    protected boolean canMergeItem(int selectPosition, int targetPosition) {
        BookDataGroup currentSelected = mLists.get(selectPosition);
        return currentSelected.getChildCount() < 2;
    }

    /**
     * 添加书籍到分组
     * <p>
     * 创建新的分组记录
     * 定义新的分组名称
     * 给本地数据库中的书籍添加所在的分组名称
     *
     * @param selectedPosition
     * @param targetPosition
     */
    @Override
    protected void onMerged(int selectedPosition, int targetPosition) {
        mLists.get(targetPosition).addChild(mLists.get(selectedPosition).getChild(0));
        ShelfGroup shelfGroup = new ShelfGroup();
        if (mLists.get(targetPosition).getChildCount() == 2) {
            String groupName = "分组" + String.valueOf(groupDao.count() + 1);
            shelfGroup.setGroupName(groupName);
            groupDao.add(shelfGroup);
            for (int i = 0; i < mLists.get(targetPosition).getChildCount(); i++) {
                //ShelfBook shelfBook = mLists.get(targetPosition).getChild(i);
                ShelfBook shelfBook = bookDao.queryByColumn("bookId", mLists.get(targetPosition).getChild(i).getBookId()).get(0);
                shelfBook.setGroupName(groupName);
                bookDao.update(shelfBook);
            }
            mLists.get(targetPosition).setCategory(groupName);
        } else {
            String groupName = mLists.get(targetPosition).getChild(0).getGroupName();
            ShelfBook shelfBook = bookDao.queryByColumn("bookId", mLists.get(selectedPosition).getChild(0).getBookId()).get(0);
            shelfBook.setGroupName(groupName);
            bookDao.update(shelfBook);
        }
        mLists.remove(selectedPosition);
    }

    /**
     * 从分组中移除图书
     *
     * @param parentPosition
     * @param bookDataGroup
     * @param selectedPosition
     * @return
     */
    @Override
    protected int onLeaveSubRegion(int parentPosition, BookDataGroup bookDataGroup, int selectedPosition) {
        BookDataGroup group = new BookDataGroup();
        if (bookDataGroup.getChildCount() > 2) {
            ShelfBook shelfBook = bookDao.queryByColumn("bookId", bookDataGroup.getChild(selectedPosition).getBookId()).get(0);
            shelfBook.setGroupName(null);
            bookDao.update(shelfBook);
        } else {
            List<ShelfBook> shelfBookList = bookDao.queryByColumn("bookId", bookDataGroup.getChild(selectedPosition).getBookId());
            String groupName = shelfBookList.get(0).getGroupName();
            for (ShelfBook shelfBook : shelfBookList) {
                shelfBook.setGroupName(null);
                bookDao.update(shelfBook);
            }
            if (groupDao == null) {
                groupDao = new GroupDao(DatabaseHelper.getHelper(mContext));
            }
            List<ShelfGroup> shelfGroupList = groupDao.queryByColumn("groupName", groupName);
            if (shelfGroupList != null && shelfGroupList.size() > 0) {
                ShelfGroup shelfGroup = groupDao.queryByColumn("groupName", groupName).get(0);
                groupDao.delete(shelfGroup);
            }
        }
        group.addChild(bookDataGroup.removeChild(selectedPosition));
        mLists.add(group);
        return mLists.size() - 1;
    }

    @Override
    protected void onBindMainViewHolder(BookShelfViewHolder holder, int position) {
        holder.bind(position, mEditMode);
    }

    @Override
    protected void onBindSubViewHolder(BookShelfViewHolder holder, int mainPosition, int subPosition) {
        holder.bindSub(mainPosition, subPosition, mEditMode);
    }

     class BookShelfViewHolder extends PrimitiveSimpleAdapter.ViewHolder {


        List<BookDataGroup> lists;
        View itemView;
        TextView textView;
        TextView checkbox;
        TextView tvBookState;
        TextView tvBookProgress;

        BookShelfViewHolder(View itemView, List<BookDataGroup> lists) {
            super(itemView);
            this.itemView = itemView;
            this.lists = lists;

            textView = (TextView) itemView.findViewById(R.id.i_tag);
            checkbox = (TextView) itemView.findViewById(R.id.i_check_box);
            tvBookState = (TextView) itemView.findViewById(R.id.tv_book_state);
            tvBookProgress = (TextView) itemView.findViewById(R.id.tv_book_progress);
        }
        void bind(int position, boolean editMode) {

            if (lists.get(position).getChildCount() > 1) {
                textView.setText(lists.get(position).getCategory());
                tvBookState.setVisibility(View.GONE);
                tvBookProgress.setVisibility(View.GONE);
            } else {
                textView.setText(lists.get(position).getChild(0).getBookName());
                if (lists.get(position).getChild(0).getBookType()==1||!isShowProgress){
                    tvBookState.setVisibility(View.GONE);
                    tvBookProgress.setVisibility(View.GONE);
                }else{
                    tvBookState.setVisibility(View.VISIBLE);
                    tvBookProgress.setVisibility(View.VISIBLE);
                    tvBookProgress.setText(lists.get(position).getChild(0).getDownloadProgress() + "%");
                }

            }
            if (editMode && lists.get(position).getChildCount() == 1) {
                checkbox.setVisibility(View.VISIBLE);
                Drawable drawable = ContextCompat.getDrawable(itemView.getContext(), lists.get(position).getChild(0).getChecked() ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                checkbox.setBackgroundDrawable(drawable);
            } else {
                checkbox.setVisibility(View.GONE);
            }
        }

        void bindSub(int mainPosition, int subPosition, boolean editMode) {
            textView.setText(lists.get(mainPosition).getChild(subPosition).getBookName());
            if (lists.get(mainPosition).getChild(subPosition).getBookType()==1||!isShowProgress){
                tvBookState.setVisibility(View.GONE);
                tvBookProgress.setVisibility(View.GONE);
            }else {
                tvBookState.setVisibility(View.VISIBLE);
                tvBookProgress.setVisibility(View.VISIBLE);
                tvBookProgress.setText(lists.get(mainPosition).getChild(subPosition).getDownloadProgress() + "%");
            }
            if (editMode) {
                checkbox.setVisibility(View.VISIBLE);
                Drawable drawable = ContextCompat.getDrawable(itemView.getContext(), lists.get(mainPosition).getChild(subPosition).getChecked() ? R.drawable.ic_checked : R.drawable.ic_unchecked);
                checkbox.setBackgroundDrawable(drawable);
            } else {
                checkbox.setVisibility(View.GONE);
            }
        }
    }

    static class BookShelfObservable extends android.database.Observable<BookShelfObserver> {

        public boolean isRegister(BookShelfObserver observer) {
            return mObservers.contains(observer);
        }


        public void notifyItemCheckChanged(boolean isChecked) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChecked(isChecked);
            }
        }

        public void notifyItemEditModeChanged(boolean editMode) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onEditChanged(editMode);
            }
        }

        public void notifyItemRestore() {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onRestore();
            }
        }

        public void notifyItemHideSubDialog() {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onHideSubDialog();
            }
        }
    }

    public static abstract class BookShelfObserver {
        public void onChecked(boolean isChecked) {

        }


        public void onEditChanged(boolean inEdit) {

        }

        public void onRestore() {

        }

        public void onHideSubDialog() {

        }
    }

    static class ItemViewHolder {
        ImageView imageView;
        TextView textView;
    }

}
