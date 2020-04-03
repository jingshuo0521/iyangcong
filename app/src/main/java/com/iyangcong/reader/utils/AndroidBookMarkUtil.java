package com.iyangcong.reader.utils;

import android.content.Context;
import android.util.Log;

import com.iyangcong.reader.bean.AndroidBookMark;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.geometerplus.android.util.PackageUtil;
import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.book.Bookmark;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by DarkFlameMaster on 2017/5/17.
 */

public class AndroidBookMarkUtil {

    public static void uploadBookmark(Bookmark bookmark,String bookUid){
        OkGo.post(Urls.AndroidUploadBookmark)
                .params("userId",(int)CommonUtil.getUserId())
                .params("uid",bookmark.Uid)
                .params("bookUid",bookUid)
                .params("bookTitle",bookmark.BookTitle)
                .params("myText",bookmark.getText())
                .params("creationTimestamp",bookmark.CreationTimestamp + "")
                .params("isVisible",bookmark.IsVisible?(byte)1:0)
                .params("styleId",bookmark.getStyleId())
                .params("start_paragraphIndex",bookmark.getParagraphIndex())
                .params("end_paragraphIndex",bookmark.getEnd().getParagraphIndex())
                .params("start_wordIndex",bookmark.getElementIndex())
                .params("start_charIndex",bookmark.getCharIndex())
                .params("end_wordIndex",bookmark.getEnd().getElementIndex())
                .params("end_charIndex",bookmark.getEnd().getCharIndex())
                .params("myOriginalText",bookmark.getOriginalText()==null?"-1":bookmark.getOriginalText())
                .params("myVersionUid",bookmark.getVersionUid()==null?"-1":bookmark.getVersionUid())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.i("DFM IS NO1 : " ,"");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Log.i("DFM IS NO2 : " ,"");
                    }
                });
    }

    public static void deleteBookmark(String uid){
        OkGo.get(Urls.AndroidDeleteBookmark)
                .params("userId",CommonUtil.getUserId())
                .params("uid",uid)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.i("DFM IS NO1 : " ,"");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Log.i("DFM IS NO2 : " ,"");
                    }
                });
    }

    public static void updateBookmark(String uid,String myOriginalText){
        OkGo.get(Urls.AndroidUpdateBookmark)
                .params("userId",CommonUtil.getUserId())
                .params("uid",uid)
                .params("myOriginalText",myOriginalText)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.i("DFM IS NO1 : " ,"");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Log.i("DFM IS NO2 : " ,"");
                    }
                });
    }

    public static void downloadBookmark(Context context,long timestamp){
        OkGo.get(Urls.AndroidDownloadBookmark)
                .tag(context)
                .params("userId",CommonUtil.getUserId())
                .params("creationTimestamp",timestamp)
                .execute(new JsonCallback<IycResponse<List<AndroidBookMark>>>(context) {
                    @Override
                    public void onSuccess(IycResponse<List<AndroidBookMark>> listIycResponse, Call call, Response response) {
                        List<AndroidBookMark> downloadlist = listIycResponse.getData();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {

                    }
                });
    }

    /**
     *
     * @param isVisible
     * @return 0:false 1:true
     */
    public static boolean getVisible(int isVisible){
        if(isVisible == 0){
            return false;
        }else {
            return true;
        }
    }
}
