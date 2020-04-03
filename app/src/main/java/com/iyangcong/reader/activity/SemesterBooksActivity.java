package com.iyangcong.reader.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.MineExchangeAdapter;
import com.iyangcong.reader.adapter.SemesterBooksAdapter;
//import com.iyangcong.reader.bean.BookIntroduction;
import com.iyangcong.reader.bean.Semester;
import com.iyangcong.reader.bean.SemesterBook;
import com.iyangcong.reader.bean.TeacherClassInfo;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.callback.JsonCallback2;
import com.iyangcong.reader.callback.ResponseBean;
import com.iyangcong.reader.callback.ResponseClassBean;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.ui.button.FlatButton;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.Urls.PersonCollectBookListURL;
import static com.iyangcong.reader.utils.Urls.StudentSemesterBooks;
import static com.iyangcong.reader.utils.Urls.TeacherClassInfos;

public class SemesterBooksActivity extends SwipeBackActivity {

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.textHeadTitle)
    TextView textHeadTitle;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.mine_collection_listview)
    ListView mineCollectionListview;
    @BindView(R.id.mine_collection_ptrClassicFrameLayout)
    PtrClassicFrameLayout mineCollectionPtrClassicFrameLayout;
    @BindView(R.id.nice_spinner)
    NiceSpinner niceSpinner;
    @BindView(R.id.current_semester)
    TextView current_semester;
    @BindView(R.id.semesterinfo_layout)
    LinearLayout semesterinfo_layout;


    private int pageNum = 1;
    private int pageSize = 10;
    private List<Semester> semesters;
    private Semester currentSemester;
    private int userType;
    private List<TeacherClassInfo> classinfos;
    private Map<String,Object> classMap = new HashMap<String,Object>();
    private List<String> classData = new ArrayList<>();
    private List<Integer> classIdList = new ArrayList<>();
    private SharedPreferenceUtil sharedPreferenceUtil;
    private int semesterId=0;
    private String semesterName;
    private List<String> classNames=new ArrayList<String>();
    private List<String> classIds=new ArrayList<String>();


    @OnClick({R.id.btnBack, R.id.btnFunction})
    void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnFunction:
                break;
        }
    }

    private List<SemesterBook> booklist;
    private SemesterBooksAdapter semesterAdapter;
    Handler handler = new Handler();

    public List<SemesterBook> getBooklist() {
        return booklist;
    }

    public void setBooklist(List<SemesterBook> booklist) {
        if (pageNum == 1) {
            this.booklist.clear();
        }
        this.booklist.addAll(booklist);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester_books);
        ButterKnife.bind(this);
        userType = getIntent().getIntExtra(Constants.USER_TYPE,0);
        setMainHeadView();
        initView();
    }


    protected void initView() {
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
        semesterId=sharedPreferenceUtil.getInt(SharedPreferenceUtil.SEMESTER_ID,Constants.DEFAULT_SEMESTER_ID);
        semesterName=sharedPreferenceUtil.getString(SharedPreferenceUtil.SEMESTER_NAME,"");
        String classNameStr=sharedPreferenceUtil.getString(SharedPreferenceUtil.CLASS_NAMES,"");
        String classIdsStr = sharedPreferenceUtil.getString(SharedPreferenceUtil.CLASS_IDS,"");
        current_semester.setText(semesterName);
        classNames=stringToList(classNameStr);
        classIds = stringToList(classIdsStr);
        semesterAdapter = new SemesterBooksAdapter(this, booklist);
        semesterAdapter.setOnSemesterBookClickedListener(new SemesterBooksAdapter.OnSemesterBookClickedListener() {
            @Override
            public void onClicked(SemesterBook semesterbook, int position) {
                Log.i("shao","clicked---------");
                RequireBook(semesterbook,position);
            }
        });
        mineCollectionListview.setAdapter(semesterAdapter);
        mineCollectionListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SemesterBooksActivity.this, BookMarketBookDetailsActivity.class);
                intent.putExtra("bookId", (int) booklist.get(position).getBookId());
                intent.putExtra("bookName", booklist.get(position).getBookName());
                startActivity(intent);
            }
        });
        mineCollectionPtrClassicFrameLayout.setHorizontalScrollBarEnabled(false);
        mineCollectionPtrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pageNum = 1;

                        //getSemester();
                        if(userType==1){
                            getStudentRequiredBooks();
                        }else if(userType==2||userType==3){
                            getTeacherRequiredBooks();
                        }
                    }
                });
            }
        });

        mineCollectionPtrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                pageNum++;
                //getSemester();
                if(userType==1){
                    getStudentRequiredBooks();
                }else if(userType==2||userType==3){
                    if(classNames!=null) {
                        getTeacherRequiredBooks();
                    }else{
                        ToastCompat.makeText(context, "当前学期没有书单图书内容", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        mineCollectionPtrClassicFrameLayout.setLoadMoreEnable(true);

        //niceSpinner.attachDataSource(classData);
        niceSpinner.setBackgroundResource(R.drawable.textview_round_border);
        niceSpinner.setTextColor(Color.WHITE);
        niceSpinner.setTextSize(13);
        niceSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("shao","----------->"+i);
                pageNum = 1;
                if(classNames!=null){

                    getTeacherRequiredBooks();
                }else{

                    ToastCompat.makeText(context, "当前学期没有书单图书内容", Toast.LENGTH_LONG).show();
                }
               // getTeacherRequiredBooks();
            }
        });
        showLoadingDialog();
        //getSemester();

        if(userType==1){
            semesterinfo_layout.setVisibility(View.GONE);
            getStudentRequiredBooks();
        }else if(userType==2||userType==3){
            semesterinfo_layout.setVisibility(View.VISIBLE);
            if(classNames!=null){
                niceSpinner.attachDataSource(classNames);
                getTeacherRequiredBooks();
            }else{
                dismissLoadingDialig();
                ToastCompat.makeText(context, "当前学期没有书单图书内容", Toast.LENGTH_LONG).show();
            }

        }

    }

    @Override
    protected void setMainHeadView() {
        textHeadTitle.setText("学期图书");
        btnBack.setImageResource(R.drawable.btn_back);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        booklist = new ArrayList<SemesterBook>();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        mineCollectionPtrClassicFrameLayout.autoRefresh();
    }


    private void getClassInfo(int semesterId){
        OkGo.get(Urls.TeacherClassInfos)
                .tag(this)
                .params("semesterId",semesterId)
                .params("userId",CommonUtil.getUserId())
                .execute(new JsonCallback2<ResponseClassBean<List<Map<String,Object>>>>(this) {
                    @Override
                    public void onSuccess(ResponseClassBean<List<Map<String,Object>>> listIycResponse, Call call, Response response) {
                        // Logger.i("succeed", listIycResponse.getData().toString());
                        List<Map<String,Object>> classes = listIycResponse.data;
                        int size =classes.size();
                        for(int i=0;i<size;i++){
                            Map<String,Object> item = (Map<String,Object>)classes.get(i);
                            String grade = (String)item.get("grade");
                            List<TeacherClassInfo> classin = (List<TeacherClassInfo>)item.get("classInfo");

                            int size_z=classin.size();
                            if(size_z>0){
                                classinfos=classin;
                                for(int j=0;j<size_z;j++){
                                    Map<String,Object> cMap= (Map<String,Object>)classin.get(j);
                                    TeacherClassInfo classitem =new TeacherClassInfo();
                                    double class_id=(double)cMap.get("class_id");
                                    String class_name=(String)cMap.get("class_name");
                                    classData.add(class_name);
                                    niceSpinner.attachDataSource(classData);
                                    //classMap.put(class_name,class_id);
                                    classIdList.add((int)class_id);

                                }
                            }
                         }

                        //dismissLoadingDialig();

                        getTeacherRequiredBooks();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissLoadingDialig();
                        reFreshOrLoadMoreSuccess(false);
                        ToastCompat.makeText(context, context.getResources().getString(R.string.socket_exception_error), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void getTeacherRequiredBooks(){
        int index =niceSpinner.getSelectedIndex();
        // int classId=classIdList.get(index);
        if(classIds==null)
            return;
        int classId=Integer.parseInt(classIds.get(index));
        OkGo.get(Urls.TeacherRequiredBooks)
                .tag(this)
                .params("userId", CommonUtil.getUserId())
                .params("semesterId",semesterId)
                .params("classId",classId)
                .params("currentPageNum", pageNum)
                .params("pageSize", pageSize)
                .execute(new JsonCallback<IycResponse<List<SemesterBook>>>(this) {
                    @Override
                    public void onSuccess(IycResponse<List<SemesterBook>> listIycResponse, Call call, Response response) {
                        Logger.i("succeed", listIycResponse.getData().toString());
                        setBooklist(listIycResponse.getData());
                        int size =listIycResponse.getData().size();
                        if (listIycResponse.getData().size() < pageNum) {
                            reFreshOrLoadMoreSuccess(true);
                        } else {
                            reFreshOrLoadMoreSuccess(false);
                        }
                        semesterAdapter.notifyDataSetChanged();
                        dismissLoadingDialig();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissLoadingDialig();
                        reFreshOrLoadMoreSuccess(false);
                        ToastCompat.makeText(context, context.getResources().getString(R.string.socket_exception_error), Toast.LENGTH_SHORT).show();
                    }
                });
    }
//    private void getSemester(){
//        OkGo.get(Urls.Semesters)
//                .tag(this)
//                .execute(new JsonCallback<IycResponse<List<Semester>>>(this) {
//
//
//                    @Override
//                    public void onSuccess(IycResponse<List<Semester>> listIycResponse, Call call, Response response) {
//                       // Logger.i("succeed", listIycResponse.getData().toString());
//                        semesters=listIycResponse.getData();
//                        Collections.sort(semesters,new Semester());
//                        System.out.print(semesters);
//                        int size = semesters.size();
//
//                        Log.i("shao","----->"+semesters);
//                        if(size>0){
//                            currentSemester=semesters.get(size-1);
//                            current_semester.setText(currentSemester.getSemesterName());
//                            if(userType==1) {
//                                getStudentRequiredBooks(0, currentSemester.getSemesterId());
//                            }else if(userType==2){
//                                getClassInfo(currentSemester.getSemesterId());
//                            }
//
//                        }
//                    }
//
//                    @Override
//                    public void onError(Call call, Response response, Exception e) {
//                        dismissLoadingDialig();
//                        reFreshOrLoadMoreSuccess(false);
//                        ToastCompat.makeText(context, context.getResources().getString(R.string.socket_exception_error), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }



    private void getStudentRequiredBooks(){
        OkGo.get(StudentSemesterBooks)
                .tag(this)
                .params("userId", CommonUtil.getUserId())
                .params("semesterId",semesterId)
                .params("currentPageNum", pageNum)
                .params("pageSize", pageSize)
                .execute(new JsonCallback<IycResponse<List<SemesterBook>>>(this) {
                    @Override
                    public void onSuccess(IycResponse<List<SemesterBook>> listIycResponse, Call call, Response response) {
                        Logger.i("succeed", listIycResponse.getData().toString());
                         setBooklist(listIycResponse.getData());
                        int size =listIycResponse.getData().size();
                        if(listIycResponse.getData().size()==0&&pageNum==1){
                            ToastCompat.makeText(context, "当前学期没有书单图书内容", Toast.LENGTH_LONG).show();
                        }
                        if (listIycResponse.getData().size() < pageNum) {
                            reFreshOrLoadMoreSuccess(true);
                        } else {
                            reFreshOrLoadMoreSuccess(false);
                        }
                        semesterAdapter.notifyDataSetChanged();
                        dismissLoadingDialig();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissLoadingDialig();
                        reFreshOrLoadMoreSuccess(false);
                        ToastCompat.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void RequireBook(SemesterBook semesterBook, final int position){
        if(semesterBook.isReceived()){
            return;
        }
        OkGo.get(Urls.RequireBook)
                .tag(this)
                .params("userId", CommonUtil.getUserId())
                .params("semesterId", semesterId)
                .params("booklistId",semesterBook.getBookListId())
                .params("bookId",semesterBook.getBookId())
//                .params("pageSize", pageSize)
                .execute(new JsonCallback<IycResponse<SemesterBook>>(this) {


                    @Override
                    public void onSuccess(IycResponse<SemesterBook> listIycResponse, Call call, Response response) {
                        // Logger.i("succeed", listIycResponse.getData().toString());
                     if(listIycResponse.statusCode==0){
                         booklist.get(position).setReceived(true);
                         semesterAdapter.notifyDataSetChanged();
                         ToastCompat.makeText(context, listIycResponse.getMsg(), Toast.LENGTH_SHORT).show();
                     }
                     }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissLoadingDialig();
                        reFreshOrLoadMoreSuccess(false);
                        ToastCompat.makeText(context, context.getResources().getString(R.string.socket_exception_error), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void reFreshOrLoadMoreSuccess(boolean isEnd) {
        if (pageNum == 1) {
            refreshSuccess(mineCollectionPtrClassicFrameLayout, isEnd);
        } else {
            loadMoreSuccess(mineCollectionPtrClassicFrameLayout, isEnd);
        }
    }

    private List<String> stringToList(String strs){
        if("".equals(strs))
            return null;
        String str[] = strs.split(",");
        return Arrays.asList(str);

    }
}
