package com.iyangcong.reader.handler;

import android.content.Context;
import android.os.Handler;

import com.iyangcong.reader.bean.ReadingRecorder;
import com.iyangcong.reader.utils.CommonUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.orhanobut.logger.Logger;

import org.geometerplus.android.fbreader.FBReader;

import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.Urls.POSTREADINGRECORDE;

/**
 * Created by ljw on 2017/6/3.
 */

public class PostReadingRecorderHandler extends Handler {
    private Context context;
    private ReadingRecorder readingRecorder;

    public PostReadingRecorderHandler(Context context, ReadingRecorder readingRecorder) {
        this.context = context.getApplicationContext();
        this.readingRecorder = readingRecorder;
    }

    public void postReadingRecorder() {
        OkGo.get(POSTREADINGRECORDE)
                .params("bookid", readingRecorder.getBookId())
                .params("chapterid", readingRecorder.getChapterId())
                .params("languagetype", readingRecorder.getLanguageType())
                .params("progress", readingRecorder.getPercent())
                .params("segmentid", readingRecorder.getSegmentIndex())
                .params("terminal", 3)
                .params("userid", (int) CommonUtil.getUserId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Logger.i("post reading progress successfully");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Logger.i("post reading progress failure:" + e.getMessage());
                    }

                    @Override
                    public void parseError(Call call, Exception e) {
                        super.parseError(call, e);
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                    }
                });
    }
}
