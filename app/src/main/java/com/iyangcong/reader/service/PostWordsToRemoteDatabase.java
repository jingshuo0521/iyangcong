package com.iyangcong.reader.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.iyangcong.reader.activity.MineNewWordActivity;
import com.iyangcong.reader.bean.NewWord;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.WordDao;
import com.iyangcong.reader.interfaceset.DeviceType;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.DateUtils;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by WuZepeng on 2017-06-27.
 */

public class PostWordsToRemoteDatabase extends Service{
	private WordDao mWordDao;
	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("目前暂不支持");
	}

	/**
	 * 初始化wordDao
	 */
	private void initDao(){
		if(mWordDao == null){
			mWordDao = new WordDao(DatabaseHelper.getHelper(getApplicationContext()));
		}
	}

	/**
	 * 上传单词接口，这里加了逻辑处理
	 */
	private void postWords() {

		List<NewWord> noUpload = mWordDao.queryByColumn("isUpload", 0);
		if (noUpload != null && noUpload.size() > 0) {
			for (int i = 0; i < noUpload.size(); i++) {
				NewWord newWord = noUpload.get(i);
				String result = CommonUtil.format201906304(newWord);
				postWords(result);
			}
		}
	}
	/**
	 * 上传单词
	 * @param str
	 */
	private void postWords(String str){
		if(!CommonUtil.getLoginState()){
			Logger.e("wzp 没登陆，不上传，嘿嘿");
			return;
		}
		OkGo.post(Urls.AddNewWord)
				.params("dataJsonObjectString",str)
				.execute(new JsonCallback<IycResponse<String>>(this) {
					@Override
					public void onSuccess(IycResponse<String> stringIycResponse, Call call, Response response) {
						Logger.e("上传成功!");
					}
					@Override
					public void onError(Call call, Response response, Exception e) {
						Logger.e("上传失败!");
					}
				});
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Logger.e("wzp 开始传单词");
		initDao();
		postWords();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mWordDao != null){
			mWordDao = null;
		}
	}
}
