package com.iyangcong.reader.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.OneLineRecyclerAdapter;
import com.iyangcong.reader.bean.BookClassify;
import com.iyangcong.reader.bean.CommonSection;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.galleryflow.GalleryFlow;
import com.iyangcong.reader.ui.galleryflow.ImageAdapter;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by lg on 2016/12/23.
 */

public class BookMarketClassificationActivity extends SwipeBackActivity {

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.book_classification_type)
    GridView bookGridView;
    @BindView(R.id.magzine_classification_type)
    GridView magzineGridView;
    @BindView(R.id.ll_classification)
    LinearLayout llClassification;
//    @BindView(R.id.gallery_flow)
//    GalleryFlow galleryFlow;
    @BindView(R.id.rv_classification)
    RecyclerView rvClassification;

    private List<BookClassify.CommonBook> commonBookList = new ArrayList<>();
    private List<BookClassify.Books> booksList = new ArrayList<>();
    private List<BookClassify.Books> magazineList = new ArrayList<>();
    private List<CommonSection> sectionList = new ArrayList<>();

//    private Bitmap bitmaps[];
//    private ImageView imageViews[];

//    private ImageAdapter imageAdapter;
//    private GestureDetector detector;

//    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book_market_classification);
        ButterKnife.bind(this);
//        handler = new ImageLoadHandler();
        initView();
    }

    protected void initView() {
        llClassification.setVisibility(View.GONE);
        btnFunction.setVisibility(View.GONE);
        textHeadTitle.setText("图书分类");
        btnBack.setImageResource(R.drawable.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bookGridView.setAdapter(new ClassificationGridViewAdaper(booksList, this));
        magzineGridView.setAdapter(new ClassificationGridViewAdaper(magazineList, this));
        bookGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, BookMarketBookListActivity.class);
                intent.putExtra("classifyImgType", booksList.get(position).getClassifyImgType() + "");
                intent.putExtra("classifyName", booksList.get(position).getClassifyName());
                intent.putExtra("list_type", 9);
                intent.putExtra("request", 1);
                startActivity(intent);
            }
        });
        magzineGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, BookMarketBookListActivity.class);
                intent.putExtra("classifyImgType", magazineList.get(position).getClassifyImgType() + "");
                intent.putExtra("classifyName", magazineList.get(position).getClassifyName());
                intent.putExtra("list_type", 9);
                intent.putExtra("request", 1);
                startActivity(intent);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvClassification.setLayoutManager(layoutManager);
        rvClassification.setAdapter(new OneLineRecyclerAdapter(this, sectionList));

    }

    @Override
    protected void setMainHeadView() {

    }

    class ClassificationGridViewAdaper extends BaseAdapter {
        private List<BookClassify.Books> classificationList;
        private Context mContext;

        public ClassificationGridViewAdaper(List<BookClassify.Books> classificationList, Context mContext) {
            this.classificationList = classificationList;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return classificationList == null ? 0 : classificationList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.item_gv_classification, null, false);
            } else {
                view = convertView;
            }
            TextView textView = (TextView) view.findViewById(R.id.tv_classification_name);
            textView.setText(classificationList.get(position).getClassifyName());
            if (position % 3 == 2) {
                textView.setBackgroundResource(R.drawable.selector_classification_right_tv);
            } else {
                textView.setBackgroundResource(R.drawable.selector_classification_leftmiddle_tv);
            }
            return view;
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        getDataFromNet();
        showLoadingDialog();
    }

    private void getDataFromNet() {
        OkGo.get(Urls.BookMarketBookClassifyURL)
                .tag(this)
                .execute(new JsonCallback<IycResponse<BookClassify>>(this) {
                    @Override
                    public void onSuccess(IycResponse<BookClassify> iycResponse, Call call, Response response) {
                        dismissLoadingDialig();
                        setData(iycResponse.getData());
                        llClassification.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissLoadingDialig();
                    }
                });
    }

    private void setData(BookClassify data) {
        commonBookList.clear();
        commonBookList.addAll(data.getCommonBook());
        sectionList.clear();
        for (int i = 0; i < commonBookList.size(); i++) {
            CommonSection commonSection = new CommonSection();
            commonSection.setSectionImageUrl(commonBookList.get(i).getImgUrl());
            commonSection.setSectionId(commonBookList.get(i).getId());
            commonSection.setSectionName(commonBookList.get(i).getName());
            commonSection.setSectionType(2);
            sectionList.add(commonSection);
        }
        booksList.clear();
        booksList.addAll(data.getBook());
        magazineList.clear();
        magazineList.addAll(data.getMagazine());
//        detector = new GestureDetector(new MyGestureListener());
//
//        imageViews = new ImageView[commonBookList.size()];
//        bitmaps = new Bitmap[commonBookList.size()];

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < commonBookList.size(); i++) {
//                    try {
//                        Bitmap myBitmap = Glide.with(BookMarketClassificationActivity.this)
//                                .load(commonBookList.get(i).getImgUrl())
//                                .asBitmap() //必须
//                                .centerCrop()
//                                .into(400, 400)
//                                .get();
//                        bitmaps[i] = myBitmap;
//                        ImageView imageView = new ImageView(BookMarketClassificationActivity.this);
//                        imageView.setImageBitmap(myBitmap);
//                        imageViews[i] = imageView;
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();
//                    }
//                }
//                handler.sendEmptyMessage(0);
//            }
//        }).start();

//        galleryFlow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(BookMarketClassificationActivity.this, BookMarketBookListActivity.class);
//                intent.putExtra("id", commonBookList.get(i).getId() + "");
//                intent.putExtra("name", commonBookList.get(i).getName());
//                intent.putExtra("request", 2);
//                startActivity(intent);
//            }
//        });

    }

//    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
//
//        private MotionEvent mLastOnDownEvent = null;
//
//        @Override
//        public boolean onDown(MotionEvent arg0) {
//            mLastOnDownEvent = arg0;
//            return false;
//        }
//
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//                               float velocityY) {
//            if (null == e1) {
//                e1 = mLastOnDownEvent;
//            }
//            if (e1 == null || e2 == null) {
//                return false;
//            }
//            int currentPosition = galleryFlow.getSelectedItemPosition();
//            if (e1.getX() - e2.getX() > 50 && Math.abs(velocityX) > 100) {
//                if (currentPosition + 1 == imageAdapter.getCount()) {
//                    galleryFlow.setSelection(0);
//                } else {
//                    galleryFlow.setSelection(currentPosition + 1);
//                }
//            } else if (e2.getX() - e1.getX() > 50 && Math.abs(velocityX) > 100) {
//                if (currentPosition - 1 < 0) {
//                    galleryFlow.setSelection(imageAdapter.getCount() - 1);
//                } else {
//                    galleryFlow.setSelection(currentPosition - 1);
//                }
//            }
//            return super.onFling(e1, e2, velocityX, velocityY);
//        }
//
//    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return detector.onTouchEvent(event);
//    }

//    class ImageLoadHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    imageAdapter = new ImageAdapter(BookMarketClassificationActivity.this, imageViews, commonBookList);
//                    imageAdapter.createReflectedImages();
//                    galleryFlow.setAdapter(imageAdapter);
//                    galleryFlow.setSelection(1);
//                    break;
//            }
//        }
//    }

}
