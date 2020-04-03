package com.iyangcong.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.LoginActivity;
import com.iyangcong.reader.activity.ShelfCloudActivity;
import com.iyangcong.reader.bean.BookType;
import com.iyangcong.reader.bean.CloudShelfBook;
import com.iyangcong.reader.bean.ShelfBook;
import com.iyangcong.reader.callback.FileCallback;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.BookDao;
import com.iyangcong.reader.interfaceset.DESEncodeInvoker;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.BaseRequest;

import org.geometerplus.android.fbreader.FBReader;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

//import org.geometerplus.fbreader.formats.oeb.function.EpubKernel;

/**
 * 书架中已添加书的适配器
 */
public class ShelfBookAddedAdapter extends BaseAdapter {

    private final Context mContext;

    private boolean isDownloading = false;

    private List<CloudShelfBook> shelfBookAddedList;
    /**
     * 书籍状态:    0:未下载   1:已下载   2:下载中
     */
    private int bookState[] = new int[1000];
//    private EpubKernel epubKernel = null;

//    private EpubHandler epubHandler;

    private ShelfBook shelfBook;
    private ShelfBook cloudShelfBook;
    private DESEncodeInvoker mInvoker;
    private boolean isShowProgress;

    public ShelfBookAddedAdapter(Context mContext, List<CloudShelfBook> shelfBookAddedList,Boolean isShowProgress) {
        this.mContext = mContext;
        this.shelfBookAddedList = shelfBookAddedList;
        this.isShowProgress = isShowProgress;
        int j = 0;
        for (int i = 0; i < shelfBookAddedList.size(); i++) {
            bookState[j] = 0;
            j++;
        }
    }

    public boolean isShowProgress() {
        return isShowProgress;
    }

    public void setShowProgress(boolean showProgress) {
        isShowProgress = showProgress;
    }

    public void setDESEncodeInvoker(DESEncodeInvoker invoker) {
        mInvoker = invoker;
    }

    @Override
    public int getCount() {
        return shelfBookAddedList.size();
    }

    @Override
    public Object getItem(int position) {
        return shelfBookAddedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        BookDao bookDao = new BookDao(DatabaseHelper.getHelper(mContext));
        final ViewHolder holder;

        List<ShelfBook> shelfBooks = bookDao.queryByColumn("bookId", shelfBookAddedList.get(position).getBookId());
        if (shelfBooks != null && shelfBooks.size() != 0) {
            cloudShelfBook = shelfBooks.get(0);
        } else {
            cloudShelfBook = null;
        }
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_shelf_book, null);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
//            String newUrl = (String) holder.ivShelfBookImage.getTag();
//            if (newUrl.equals(shelfBookAddedList.get(position).getBookImageUrl())) {
//                return convertView;
//            }

        }
        if (shelfBookAddedList.get(position).getBookId() == -1) {
            holder.ivShelfBookImage.setImageResource(R.drawable.ic_shelf_book_add);
            holder.tvBookName.setText("");
            holder.pgBookAdded.setVisibility(View.INVISIBLE);
            holder.tvBookState.setVisibility(View.INVISIBLE);
        } else if (cloudShelfBook == null) {

            if (shelfBookAddedList.get(position).toString()
                    .equals(holder.ivShelfBookImage.getTag())) {
            }else{
                GlideImageLoader.displayBookCover(mContext, holder.ivShelfBookImage, shelfBookAddedList.get(position).getBookImageUrl());
            }

            holder.tvBookName.setText(shelfBookAddedList.get(position).getBookName());
            if (!isShowProgress||shelfBookAddedList.get(position).getReadingProgress() == null || shelfBookAddedList.get(position).getReadingProgress().equals("0")) {
                holder.tvBookProgress.setVisibility(View.GONE);
            } else {
                holder.tvBookProgress.setVisibility(View.VISIBLE);
                holder.tvBookProgress.setText(shelfBookAddedList.get(position).getReadingProgress());
            }
            if (!isShowProgress){
                holder.tvBookState.setVisibility(View.GONE);
            }else{
                holder.tvBookState.setText(R.string.book_notdownload);
            }

//            String str;
//            if (shelfBookAddedList.get(position).getReadingProgress() != null) {
//                str = shelfBookAddedList.get(position).getReadingProgress().substring(0, 3);
//                float progress = Float.parseFloat(str);
//                holder.pgBookAdded.setProgress(progress);
//            }
        } else {

            if (shelfBookAddedList.get(position).toString()
                    .equals(holder.ivShelfBookImage.getTag())) {
            }else{
                GlideImageLoader.displayBookCover(mContext, holder.ivShelfBookImage, shelfBookAddedList.get(position).getBookImageUrl());
            }
            holder.tvBookName.setText(shelfBookAddedList.get(position).getBookName());
            if (cloudShelfBook.getDownloadProgress() == 0.0 ||!isShowProgress) {
                holder.tvBookProgress.setVisibility(View.GONE);
            } else {
                holder.tvBookProgress.setVisibility(View.VISIBLE);
                holder.tvBookProgress.setText(cloudShelfBook.getDownloadProgress() + "0%");
            }
            if (!isShowProgress){
                holder.tvBookState.setVisibility(View.GONE);
            }else{
                holder.tvBookState.setText(cloudShelfBook.getBookState());
            }

//            holder.pgBookAdded.setProgress((float) cloudShelfBook.getDownloadProgress());
            bookState[position] = 1;
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtil.getLoginState()) {
                    if (bookState[position] != 1) {
                        if (bookState[position] == 0) {
                            if (isDownloading) {
                                ToastCompat.makeText(mContext, "有书籍正在下载中", Toast.LENGTH_SHORT).show();
                            } else {
                                bookState[position] = 2;
                                isDownloading = true;
                                doDownloadTask(holder, position);
                            }
                        }
                    } else {
                        //这里打开书籍并开始阅读
                        openBook(true,position);
                    }
                } else {
                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                }
            }
        });
        convertView.setTag(holder);
        return convertView;
    }

    /**
     * 打开图书的函数
     * @param isShowTips
     * @return
     */
    private boolean openBook(boolean isShowTips,final int position){
        Intent intent = new Intent(mContext, FBReader.class);
        BookDao bookDao = new BookDao(DatabaseHelper.getHelper(mContext));
        ShelfBook bookData = bookDao.queryByColumn("bookId", shelfBookAddedList.get(position).getBookId()).get(0);

        SharedPreferenceUtil sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
        if (bookData.getSupportLanguage() == 1 || bookData.getRecentReadingLanguageType() == 1) {
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
            if(isShowTips) {
                ToastCompat.makeText(mContext, "图书解析中，请稍后！", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }

    static class ViewHolder {
        @BindView(R.id.iv_shelf_book_image)
        ImageView ivShelfBookImage;
        @BindView(R.id.tv_book_name)
        TextView tvBookName;
        @BindView(R.id.pg_book_added)
        RoundCornerProgressBar pgBookAdded;
        @BindView(R.id.tv_book_state)
        TextView tvBookState;
        @BindView(R.id.tv_book_progress)
        TextView tvBookProgress;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private void doDownloadTask(final ViewHolder holder, final int position) {
        shelfBook = new ShelfBook();
        final int bookId = shelfBookAddedList.get(position).getBookId();

        shelfBook.setUserId(CommonUtil.getUserId());
        shelfBook.setBookId(bookId);
        shelfBook.setBookName(shelfBookAddedList.get(position).getBookName());
        shelfBook.setBookIntroduction(shelfBookAddedList.get(position).getBookIntroduction());
        shelfBook.setBookAuthor(shelfBookAddedList.get(position).getBookAuthor());
        shelfBook.setBookPrice(shelfBookAddedList.get(position).getPrice());

        if (!isShowProgress){
            holder.tvBookState.setVisibility(View.GONE);
        }else {
            holder.tvBookState.setVisibility(View.VISIBLE);
            holder.tvBookState.setText(R.string.book_downloading);
        }

        OkGo.get(shelfBookAddedList.get(position).getBookResourceUrl())
                .tag(this)
                .execute(new FileCallback(shelfBookAddedList.get(position).getBookId() + ".zip") {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                    }

                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        ToastCompat.makeText(mContext, shelfBook.getBookName() + "下载完成，图书将自动打开，请稍后~", Toast.LENGTH_SHORT).show();
                        bookState[position] = 1;
                        BookDao bookDao = new BookDao(DatabaseHelper.getHelper(mContext));
                        shelfBook.setBookType(BookType.HAS_BUY_BOOk);
                        shelfBook.setBookState("未读");
                        shelfBook.setTimeStamp(System.currentTimeMillis());
                        shelfBook.setBookImageUrl(CommonUtil.getBooksDir() + "/image/" + bookId);
                        shelfBook.setBookPath(CommonUtil.getBooksDir() + bookId + "/");
                        bookDao.add(shelfBook);
                        holder.tvBookState.setText(R.string.book_downloaded);
                        if(mInvoker != null){
                           if (mContext instanceof ShelfCloudActivity) {
                                ((ShelfCloudActivity) mContext).showLoadingDialog();
                            }
                            mInvoker.invokerDESEncodeService(shelfBook.getBookId());
                        }
                        isDownloading = false;
//                        decryptEpub(shelfBook.getBookId());
//                        if (mContext instanceof ShelfCloudActivity) {
//                            ((ShelfCloudActivity) mContext).dismissLoadingDialig();
//                        }
                        //定时检测图书资源有没有被处理完成
                        Handler mHandler = new Handler();
                        Runnable r = new Runnable() {

                            @Override
                            public void run() {
                                //do something
                                //每隔1s循环执行run方法
                                if(openBook(false,position)){
                                    mHandler.removeCallbacksAndMessages(null);
                                }else {
                                    mHandler.postDelayed(this, 200);
                                }
                            }
                        };
                        mHandler.postDelayed(r, 100);//延时100毫秒
                    }

                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        holder.pgBookAdded.setProgress(progress * 100);
                        if (!isShowProgress){
                            holder.tvBookProgress.setVisibility(View.GONE);
                        }else{
                            holder.tvBookProgress.setVisibility(View.VISIBLE);
                            holder.tvBookProgress.setText((int) (Math.round(progress * 10000) * 1 / 100) + "%");
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        isDownloading = false;
                    }
                });

        OkGo.get(shelfBookAddedList.get(position).getBookImageUrl())
                .tag(this)
                .execute(new FileCallback(CommonUtil.getBooksDir() + "/image/", bookId + "") {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                    }

                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                    }

                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                    }
                });
    }

//    private void decryptEpub(long bookId) {
//        if (epubHandler == null) {
//            epubHandler = new EpubHandler();
//        }
//        epubKernel = new EpubKernel(mContext, bookId + ".zip", epubHandler);
//        epubKernel.addTask();
//    }

//    private class EpubHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            isDownloading = false;
//            BookDao bookDao = new BookDao(DatabaseHelper.getHelper(mContext));
//            BookInfoDao bookInfoDao = new BookInfoDao(DatabaseHelper.getHelper(mContext));
//            MsgBookInfo msgBookInfo = (MsgBookInfo) msg.obj;
//            switch (msg.what) {
//                case 1:
//                case 2:
//                case 3:
//                    List<ShelfBook> shelfBookList = bookDao.queryByColumn("bookId", msgBookInfo.getBookId() + "");
//                    if (shelfBookList != null && shelfBookList.size() > 0) {
//                        ShelfBook shelfBook = shelfBookList.get(0);
//                        shelfBook.setSupportLanguage(msg.what);
//                        bookDao.update(shelfBook);
//                    }
//                    break;
//            }
//            switch (msg.what) {
//                case 1:
//                    bookInfoDao.add(msgBookInfo.getBookInfoZh());
//                    break;
//                case 2:
//                    bookInfoDao.add(msgBookInfo.getBookInfoEn());
//                    break;
//                case 3:
//                    bookInfoDao.add(msgBookInfo.getBookInfoEn());
//                    bookInfoDao.add(msgBookInfo.getBookInfoZh());
//                    break;
//            }
//            if (mContext instanceof ShelfCloudActivity) {
//                ((ShelfCloudActivity) mContext).dismissLoadingDialig();
//            }
//        }
//    }

}
