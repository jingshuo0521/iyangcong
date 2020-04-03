package com.iyangcong.reader.activity;

import android.os.Bundle;

import com.iyangcong.reader.utils.Constants;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

/**
 * Created by WuZepeng on 2017-05-18.
 */

public class BannerUrlAcitivty extends AbsWebViewAcitivity {
	private String url;

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	protected boolean isShareMode() {
		return false;
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		url = getIntent().getStringExtra(Constants.URL);
		String title = getIntent().getStringExtra(Constants.Title);
		setTitle(isNull(title)?"":title);
	}
}
