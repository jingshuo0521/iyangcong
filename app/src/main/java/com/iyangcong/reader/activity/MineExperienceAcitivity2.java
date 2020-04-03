package com.iyangcong.reader.activity;

import android.os.Bundle;

import com.iyangcong.reader.interfaceset.DeviceType;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Urls;

/**
 * Created by WuZepeng on 2017-05-18.
 */

public class MineExperienceAcitivity2 extends AbsWebViewAcitivity {


	@Override
	protected void initData(Bundle savedInstanceState) {
		setTitle("我的阅历");
		iniShareUtils();
	}

	@Override
	protected boolean isShareMode() {
		return true;
	}

	@Override
	public String getUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append(Urls.URL_NoHttps).append("/onion/yueli.html?userId=").append(CommonUtil.getUserId()).append("&deviceType=").append(DeviceType.WEB_1);
		return sb.toString();
	}
}
