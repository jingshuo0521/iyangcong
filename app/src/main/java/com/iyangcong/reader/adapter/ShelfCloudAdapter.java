package com.iyangcong.reader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.CloudShelfBook;
import com.iyangcong.reader.bean.CloudShelfContent;
import com.iyangcong.reader.bean.ShelfBook;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.BookDao;
import com.iyangcong.reader.interfaceset.DESEncodeInvoker;
import com.iyangcong.reader.interfaceset.DeviceType;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.CustomProgressDialog;
import com.iyangcong.reader.ui.MyGridView;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/4/5.
 */

public class ShelfCloudAdapter extends RecyclerView.Adapter {

    /**
     * 类型1：默认、排序时间、书名分页显示
     */
    private static final int PAGE_DISPLAY = 0;

    /**
     * 类型2：分类显示
     */
    private static final int CLASSIFY_DISPLAY = 1;

    /**
     * 当前类型
     * //
     */
    private int currentType = PAGE_DISPLAY;
    private int lastTab;
    //    private int currentType = CLASSIFY_DISPLAY;
    private Context mContext;
    private CloudShelfContent cloudShelfContent;
    private LayoutInflater mLayoutInflater;
    private boolean[] foldList;
    private CustomProgressDialog dialogProgress;
    private List<CloudShelfBook> backupList = new ArrayList<>();
    private DESEncodeInvoker mInvoker;
    private boolean isShowProgress;
    private ShelfBookAddedAdapter shelfBookAddedAdapter;
    private ShelfBookAddedAdapter categoryBookAddedAdapter;


    public boolean isShowProgress() {
        return isShowProgress;
    }

    public void setShowProgress(boolean showProgress) {
        isShowProgress = showProgress;

    }

    public ShelfCloudAdapter(Context context, CloudShelfContent cloudShelfContent, Boolean isShowProgress) {
        this.mContext = context;
        this.cloudShelfContent = cloudShelfContent;
        this.isShowProgress = isShowProgress;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setDESEncodeInvoker(DESEncodeInvoker invoker) {
        mInvoker = invoker;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mContext, mLayoutInflater.inflate(R.layout.gv_shelf_cloud, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        if (cloudShelfContent.getCategoryList() != null && cloudShelfContent.getCategoryList().size() != 0) {
            viewHolder.setData(currentType, cloudShelfContent.getCategoryList().get(position), position);
            foldList = new boolean[cloudShelfContent.getCategoryList().size()];
            for (int i = 0; i < cloudShelfContent.getCategoryList().size(); i++) {
                foldList[i] = false;
            }

        } else {
            viewHolder.setData(currentType, null, 0);
        }
    }


    @Override
    public int getItemCount() {
        if (currentType == PAGE_DISPLAY) {
            return 1;
        } else if (currentType == CLASSIFY_DISPLAY) {
            return cloudShelfContent.getCategoryList().size();
        }
        return 0;
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
        private Context mContext;
        private List<CloudShelfBook> categoryBookList = new ArrayList<>();

        @BindView(R.id.iv_icon)
        ImageView ivIcon;
        @BindView(R.id.tv_category_name)
        TextView tvCategoryName;
        @BindView(R.id.ll_category)
        LinearLayout llCategory;
        @BindView(R.id.gv_book_cloud)
        MyGridView gvBookCloud;

        public ViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            ButterKnife.bind(this, itemView);
        }

        public void setData(int currentType, final String category, final int position) {
            if (currentType == PAGE_DISPLAY) {
                llCategory.setVisibility(View.GONE);
                List<CloudShelfBook> shelfBookCloudList = cloudShelfContent.getShelfBookCloudList();
                setBackupList(shelfBookCloudList);
                shelfBookAddedAdapter = new ShelfBookAddedAdapter(mContext, getFilterList(cloudShelfContent.getTabPosition()),isShowProgress);
                shelfBookAddedAdapter.setDESEncodeInvoker(mInvoker);
                gvBookCloud.setAdapter(shelfBookAddedAdapter);
            } else if (currentType == CLASSIFY_DISPLAY) {
                llCategory.setVisibility(View.VISIBLE);
                tvCategoryName.setText(category);
                categoryBookList.clear();
                inputCategoryList();
                if (foldList != null) {
                    foldList[position] = false;
                }
                ivIcon.setImageResource(R.drawable.icon_plus);

                llCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLoadingDialog();
                        if (foldList[position] == false) {
                            getCategoryBooks(category);
                            ivIcon.setImageResource(R.drawable.icon_reduce);
                        } else if (foldList[position] == true) {
                            clearCategoryBooks();
                            ivIcon.setImageResource(R.drawable.icon_plus);
                        }
                        foldList[position] = !foldList[position];
                    }
                });
            }
        }

        private void getCategoryBooks(String category) {
            HashMap hashMap = new HashMap<>();
            hashMap.put("category", category);
            hashMap.put("userId", "" + CommonUtil.getUserId());
            OkGo.get(Urls.BookShelfByCategoryURL)
                    .tag(this)
                    .params(hashMap)
                    .execute(new JsonCallback<IycResponse<List<CloudShelfBook>>>(mContext) {
                        @Override
                        public void onSuccess(IycResponse<List<CloudShelfBook>> listIycResponse, Call call, Response response) {
                            categoryBookList.clear();
                            for (CloudShelfBook book : listIycResponse.getData())
                                categoryBookList.add(book);
                            setBackupList(categoryBookList);
                            categoryBookList = getFilterList(cloudShelfContent.getTabPosition());
                            inputCategoryList();
                            dismissLoadingDialig();
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            categoryBookList.clear();
                            inputCategoryList();
                            dismissLoadingDialig();
                        }
                    });
        }

        private void clearCategoryBooks() {
            categoryBookList.clear();
            inputCategoryList();
            dismissLoadingDialig();
        }

        void inputCategoryList() {
            categoryBookAddedAdapter = new ShelfBookAddedAdapter(mContext, categoryBookList,isShowProgress);
            categoryBookAddedAdapter.setDESEncodeInvoker(mInvoker);
            gvBookCloud.setAdapter(categoryBookAddedAdapter);
        }
    }

    public void setCurrentType(int type) {
        currentType = type;
    }

    public void showLoadingDialog() {
        if (dialogProgress == null) {
            dialogProgress = new CustomProgressDialog(mContext, mContext.getResources().getString(R.string.loading_tip));
            dialogProgress.show();
        } else {
            if (!dialogProgress.isShowing())
                dialogProgress.show();
        }
    }

    public void dismissLoadingDialig() {
        if (dialogProgress != null) {
            dialogProgress.dismiss();
        }
    }

    // TODO: 2017/5/9  
    /*
    tab切换筛选 0：全部 1：已下载 2：未下载
     */
    private int[] getLocalBooks() {
        BookDao bookDao = new BookDao(DatabaseHelper.getHelper(mContext));
        List<ShelfBook> downloadedList = new ArrayList<>();
        if (bookDao != null) {
            downloadedList = bookDao.all();
        }
        int[] bookIds = new int[downloadedList.size()];
        for (int i = 0; i < downloadedList.size(); i++) {
            bookIds[i] = (int) downloadedList.get(i).getBookId();
        }
        return bookIds;
    }

    public void setBackupList(List<CloudShelfBook> tempList) {
        this.backupList = tempList;
    }

    public List<CloudShelfBook> getBackupList() {
        return backupList;
    }

    public List<CloudShelfBook> getAllList() {
        List<CloudShelfBook> AllList = getBackupList();
        return AllList;
    }

    public List<CloudShelfBook> getNotDownloadList(int[] bookIds) {
        List<CloudShelfBook> tempList = new ArrayList<>();
        for (CloudShelfBook book : getAllList()) {
            boolean isExist = false;
            for (int i = 0; i < bookIds.length; i++) {
                if (bookIds[i] == book.getBookId()) {
                    isExist = true;
                    break;
                }
            }
            if (isExist == false) {
                tempList.add(book);
            }
        }
        return tempList;
    }

    public List<CloudShelfBook> getDownloadedList(int[] bookIds) {
        List<CloudShelfBook> tempList = new ArrayList<>();
        for (CloudShelfBook book : getAllList()) {
            boolean isExist = false;
            for (int i = 0; i < bookIds.length; i++) {
                if (book.getBookId() == bookIds[i]) {
                    isExist = true;
                    break;
                }
            }
            if (isExist == true) {
                tempList.add(book);
            }
        }
        return tempList;
    }

    public List<CloudShelfBook> getFilterList(int tabposion) {
        List<CloudShelfBook> filterList = new ArrayList<>();
        switch (tabposion) {
            case 0:
                filterList = getAllList();
                break;
            case 1:
                filterList = getDownloadedList(getLocalBooks());
                break;
            case 2:
                filterList = getNotDownloadList(getLocalBooks());
                break;
        }
        return filterList;
    }
}
