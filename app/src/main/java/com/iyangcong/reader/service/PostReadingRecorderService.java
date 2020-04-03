package com.iyangcong.reader.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyangcong.reader.bean.ReadingDataBean;
import com.iyangcong.reader.bean.ReadingRecorder;
import com.iyangcong.reader.bean.ShelfBook;
import com.iyangcong.reader.bean.UuidBean;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.BookDao;
import com.iyangcong.reader.database.dao.ReadingDataDao;
import com.iyangcong.reader.interfaceset.DeviceType;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.Urls;
import com.iyangcong.reader.utils.postTask.PostTask;
import com.iyangcong.reader.utils.postTask.PostTaskManager;
import com.iyangcong.reader.utils.postTask.PostTasker;
import com.iyangcong.reader.utils.postTask.PostTaskerCannotRetry;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.Urls.POSTREADINGRECORDE;

public class PostReadingRecorderService extends Service {

    public PostReadingRecorderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null){
            Logger.e("wzp 上传阅读进度时intent异常：" + intent);
        }else{
            final ReadingRecorder readingRecorder = (ReadingRecorder) intent.getSerializableExtra(Constants.READING_RECORDER);
            final byte taskType = intent.getByteExtra(Constants.TASK_TYPE,ServiceType.ERROR);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PostTaskManager tmpManager = buildTaskQueue(readingRecorder,taskType);
                    tmpManager.execute(getApplicationContext(), new PostTaskManager.OnPostTaskFinishedListener() {
                        @Override
                        public void onTaskFinished() {
                            stopSelf();
                        }
                    });
                }
            }).start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private PostTaskManager buildTaskQueue(ReadingRecorder readingRecorder,byte taskType) {
        PostTaskManager manager = new PostTaskManager();
        if(readingRecorder != null &&(taskType == ServiceType.BOTH || taskType == ServiceType.ONLY_READING_RECORD)){
            if(readingRecorder.getSegmentId() < 0){
                Logger.e("计算出来的段落值：%d 小于0，所以不构建任务。",readingRecorder.getSegmentId());
                return manager;
            }
            final int bookid = readingRecorder.getBookId();
            PostTasker readingProgressTask = new PostTasker();
            readingProgressTask.buildTaskRequest(PostTask.Method.GET,POSTREADINGRECORDE,
                    "bookId", readingRecorder.getBookId() + "",
                    "chapterId", readingRecorder.getChapterId() + "",
                    "languageType", readingRecorder.getLanguageType() + "",
                    "progress", readingRecorder.getPercent() + "",
                    "segmentId", readingRecorder.getSegmentId()+"",
                    "terminal", DeviceType.ANDROID_3,
                    "deviceType","3",
                    "userId", (int) CommonUtil.getUserId()+"");
            manager.addTask(readingProgressTask, new JsonCallback<IycResponse>(this) {
                @Override
                public void onSuccess(IycResponse iycResponse, Call call, Response response) {
                    Logger.e("gft:" + iycResponse.getMsg());
                    if(iycResponse.getData()!=null) {
                        final BookDao bookDao = new BookDao(DatabaseHelper.getHelper(PostReadingRecorderService.this));
                        if (bookDao != null) {
                            List<ShelfBook> shelfBookList = bookDao.queryByColumn("bookId", bookid);
                            if (shelfBookList.size() > 0) {
                                ShelfBook shelfBook = shelfBookList.get(0);
                                Long date = Math.round((Double)iycResponse.getData());
                                shelfBook.setTimeStamp(date);
                                bookDao.update(shelfBook);
                            }
                        }
                    }
                }
            });
        }
        if(taskType == ServiceType.BOTH || taskType == ServiceType.ONLY_READING_ORBIT_RECORD){
            final ReadingDataDao tmpDao = new ReadingDataDao(DatabaseHelper.getHelper(this));
            List<ReadingDataBean> tmpList = tmpDao.all();
            Set<ReadingDataBean> getRidOfRepeatSet = new TreeSet<ReadingDataBean>();
            if(tmpList!=null&&!tmpList.isEmpty()){
                List<ReadingDataBean> gsonList = new ArrayList<>();
                Iterator<ReadingDataBean> tmpIterator1 = tmpList.iterator();
                while (tmpIterator1.hasNext()){
                    getRidOfRepeatSet.add(tmpIterator1.next());
                    tmpIterator1.remove();
                }
                tmpList = null;
                Iterator<ReadingDataBean> tmpIterator = getRidOfRepeatSet.iterator();
                while (tmpIterator.hasNext()){
                    gsonList.add(tmpIterator.next());
                    tmpIterator.remove();
                }
                getRidOfRepeatSet = null;
                Gson gson = new Gson();
                String dataStr = gson.toJson(gsonList);
                PostTaskerCannotRetry orbitReadingEndTask = new PostTaskerCannotRetry();
                Logger.e("wzp 阅读数据：%s",dataStr);
                orbitReadingEndTask.buildTaskRequest(PostTask.Method.POST,Urls.POST_READING_DATA,
                        "data", dataStr,
                        "macAddress", CommonUtil.getLocalMacAddressFromIp(this),
                        "deviceType",DeviceType.ANDROID_3);
                JsonCallback<IycResponse> callback = new JsonCallback<IycResponse>(this) {
                    @Override
                    public void onSuccess(IycResponse iycResponse, Call call, Response response) {
                        if(iycResponse != null && iycResponse.getData() != null){
                            String jsonStr = iycResponse.data.toString();
                            Logger.e("wzp jsonStr:%s",jsonStr);
                            if(!TextUtils.isEmpty(jsonStr)){
                                Gson tmpGson = new Gson();
                                List<UuidBean> beanList = tmpGson.fromJson(jsonStr,new TypeToken<List<UuidBean>>(){}.getType());
                                for(UuidBean uuid:beanList){
                                    if(uuid.getState() == UuidBean.UuidState.FAILURE)
                                        continue;
                                    tmpDao.deleteByColumn("uuid",uuid.getUuid());
                                }
                            }
                        }
                    }
                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Logger.e("gft:" + e.getMessage());
                    }

                };
                manager.addTask(orbitReadingEndTask,callback);
            }

        }
        return manager;
    }

    public static interface ServiceType{
        byte ERROR = -1;
        byte ONLY_READING_ORBIT_RECORD = 1;
        byte ONLY_READING_RECORD = 2;
        byte BOTH = 3;
    }
//    private int count1 = 0;
//
//    private void postReadingRecorder(final ReadingRecorder readingRecorder, final int endSegmentid, final String paragraphText) {
//        OkGo.get(POSTREADINGRECORDE)
//                .params("bookid", readingRecorder.getBookId() + "")
//                .params("chapterid", readingRecorder.getRelativeChapterId() + "")
//                .params("segmentStr", paragraphText)
//                .params("languagetype", readingRecorder.getLanguageType() + "")
//                .params("progress", readingRecorder.getPercent() + "")
//                .params("segmentid", readingRecorder.getSegmentId())
//                .params("terminal", 3)
//                .params("deviceType", 3)
//                .params("userid", (int) CommonUtil.getUserId())
//                .execute(new StringCallback() {
//                    @Override
//                    public void onSuccess(String s, Call call, Response response) {
//                        orbitRecordEnd(endSegmentid);
//                        Logger.i("post reading progress successfully");
//                    }
//
//                    @Override
//                    public void onError(Call call, Response response, Exception e) {
//                        super.onError(call, response, e);
//                        Logger.i("post reading progress failure:" + e.getMessage());
//                    }
//
//                    @Override
//                    public void parseError(Call call, Exception e) {
//                        super.parseError(call, e);
//                        count1++;
//                        if (count1 < 3) {
//                            postReadingRecorder(readingRecorder, endSegmentid, paragraphText);
//                        } else {
//                            stopSelf();
//                        }
//                    }
//
//                    @Override
//                    public void onAfter(String s, Exception e) {
//                        super.onAfter(s, e);
//                    }
//                });
//    }
//
//    private int count2 = 0;
//
//    private void orbitRecordEnd(final int endSegmentid) {
//        SharedPreferenceUtil sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
//        Date endDate = new Date(System.currentTimeMillis());
//        String endTime = DateUtils.getSystemDateFormat("yyyy-MM-dd HH:mm:ss");
//        String startTime = sharedPreferenceUtil.getString(SharedPreferenceUtil.READING_RECORD_LAST_TIME, "");
//        long readingLong = (endDate.getTime() - DateUtils.StringToDate(startTime, "yyyy-MM-dd HH:mm:ss").getTime()) / 1000;
//        OkGo.get(Urls.ReadingOrbitRecordEndURL)
//                .tag(this)
//                .params("bookid", (int) sharedPreferenceUtil.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0))
//                .params("endsegmentid", BookInfoUtils.getBeginSegmentId(this) + endSegmentid)
//                .params("language", sharedPreferenceUtil.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 0))
//                .params("readingrecordlogid", sharedPreferenceUtil.getInt(SharedPreferenceUtil.READING_RECORD_LODID, 0))
//                .params("userid", CommonUtil.getUserId())
//                .params("start_time", sharedPreferenceUtil.getString(SharedPreferenceUtil.READING_RECORD_LAST_TIME, ""))
//                .params("end_time", endTime)
//                .params("deviceType", 3)
//                .params("reading_long", readingLong + "")
//                .execute(new StringCallback() {
//                    @Override
//                    public void onSuccess(String s, Call call, Response response) {
//                        Logger.i("上传阅读记录成功");
//                    }
//
//                    @Override
//                    public void onError(Call call, Response response, Exception e) {
//                    }
//
//                    @Override
//                    public void parseError(Call call, Exception e) {
//                        super.parseError(call, e);
//                        if (count2 < 3) {
//                            orbitRecordEnd(endSegmentid);
//                        } else {
//                            stopSelf();
//                        }
//                    }
//
//                    @Override
//                    public void onAfter(String s, Exception e) {
//                        super.onAfter(s, e);
//                        stopSelf();
//                    }
//                });
//    }
}
