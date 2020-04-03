package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.CodeReturn;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.callback.JsonCallback2;
import com.iyangcong.reader.callback.ResponseBean;
import com.iyangcong.reader.callback.SimpleBean;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.ui.button.FlatButton;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.StringUtils;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;


public class NewNormalQuestionActivity extends SwipeBackActivity {

	@BindView(R.id.btnBack)
	ImageButton btnBack;
	@BindView(R.id.textHeadTitle)
	TextView textHeadTitle;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mine_normalquestion1);

		ButterKnife.bind(this);
		setMainHeadView();
		initView();
	}

	@OnClick({R.id.btnBack})
	void onBtnClick(View view) {
		switch (view.getId()) {
			case R.id.btnBack:
				finish();
				break;

		}
	}

	@Override
	protected void initData(Bundle savedInstanceState) {


	}

	@Override
	protected void initView() {}

	@Override
	protected void setMainHeadView() {
	    textHeadTitle.setText("常见问题");
        btnBack.setImageResource(R.drawable.btn_back);
	}

}
