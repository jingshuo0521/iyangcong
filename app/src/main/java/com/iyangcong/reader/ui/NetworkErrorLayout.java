package com.iyangcong.reader.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;

/**
 * Created by WuZepeng on 2017-04-06.
 */

public class NetworkErrorLayout implements View.OnClickListener{

//	@BindView(R.id.btnBack)
//	ImageButton btnBack;
//	@BindView(R.id.textHeadTitle)
//	TextView textHeadTitle;
//	@BindView(R.id.btnFunction)
//	ImageButton btnFunction;
//	@BindView(R.id.layout_header)
//	LinearLayout layoutHeader;
//	@BindView(R.id.tv_load_failure)
	private TextView tvLoadFailure;
//	@BindView(R.id.tv_makesure_internet_ok)
	private TextView tvMakesureInternetOk;
//	@BindView(R.id.btn_network_error)
	private Button btnNetworkError;
//	@BindView(R.id.layout_network_error)
    private LinearLayout layoutNetworkError;

	private List<View> viewList;
	private OnRefreshClicked onRefreshClicked;

	public NetworkErrorLayout() {
		if (viewList == null) {
			viewList = new ArrayList<>();
		}
	}

	public LinearLayout init(Context context, LayoutInflater inflater, ViewGroup container){
		LinearLayout view = (LinearLayout)inflater.inflate(R.layout.layout_error_network,container,false);
		iniWidgit(view);
		initViewList();
		if(viewList.contains(layoutNetworkError)){
			viewList.remove(layoutNetworkError);
		}
		return view;
	}

	public void init(Activity activity) {
		activity.setContentView(R.layout.layout_error_network);
		iniWidgit(activity);
		initViewList();
	}

	private void initViewList(){
		add(viewList, btnNetworkError)
				.add(viewList, tvLoadFailure)
				.add(viewList, tvMakesureInternetOk)
				.add(viewList,layoutNetworkError);
//				.add(viewList, btnBack)
//				.add(viewList, textHeadTitle)
//				.add(viewList, btnFunction)
//				.add(viewList, layoutHeader);
		setLayoutVisibility(viewList, View.GONE);
	}

	/**
	 * 使用activity的findViewById方法来初始化控件；
	 * @param activity
	 */
	private void iniWidgit(Activity activity) {
		btnNetworkError = (Button) activity.findViewById(R.id.btn_network_error);
		btnNetworkError.setOnClickListener(this);
		tvLoadFailure = (TextView) activity.findViewById(R.id.tv_load_failure);
		tvLoadFailure.setOnClickListener(this);
		tvMakesureInternetOk = (TextView) activity.findViewById(R.id.tv_makesure_internet_ok);
		tvMakesureInternetOk.setOnClickListener(this);
		layoutNetworkError = (LinearLayout) activity.findViewById(R.id.layout_network_error);
		layoutNetworkError.setOnClickListener(this);
//		btnBack = (ImageButton) activity.findViewById(R.id.btnBack);
//		btnBack.setOnClickListener(this);
//		textHeadTitle = (TextView) activity.findViewById(R.id.textHeadTitle);
//		textHeadTitle.setOnClickListener(this);
//		btnFunction = (ImageButton) activity.findViewById(R.id.btnFunction);
//		btnFunction.setOnClickListener(this);
//		layoutHeader = (LinearLayout) activity.findViewById(R.id.layout_header);
//		layoutHeader.setOnClickListener(this);
	}

	/**
	 * 使用view的findViewById()的方法初始化控件
	 * @param view
	 */
	private void iniWidgit(View view) {
		btnNetworkError = (Button) view.findViewById(R.id.btn_network_error);
		btnNetworkError.setOnClickListener(this);
		tvLoadFailure = (TextView) view.findViewById(R.id.tv_load_failure);
		tvLoadFailure.setOnClickListener(this);
		tvMakesureInternetOk = (TextView) view.findViewById(R.id.tv_makesure_internet_ok);
		tvMakesureInternetOk.setOnClickListener(this);
		layoutNetworkError = (LinearLayout) view.findViewById(R.id.layout_network_error);
		layoutNetworkError.setOnClickListener(this);
//		btnBack = (ImageButton)view.findViewById(R.id.btnBack);
//		btnBack.setOnClickListener(this);
//		textHeadTitle = (TextView)view.findViewById(R.id.textHeadTitle);
//		textHeadTitle.setOnClickListener(this);
//		btnFunction = (ImageButton)view.findViewById(R.id.btnFunction);
//		btnFunction.setOnClickListener(this);
//		layoutHeader = (LinearLayout)view.findViewById(R.id.layout_header);
//		layoutHeader.setOnClickListener(this);
	}

	private NetworkErrorLayout add(List<View> list, View view) {
		list.add(view);
		return this;
	}

	private NetworkErrorLayout setLayoutVisibility(List<View> list, int visibility) {
		for (View view : list)
			if(view != null)
				view.setVisibility(visibility);
		return this;
	}

	public NetworkErrorLayout setLayoutVisibility(int visibility) {
		setLayoutVisibility(viewList, visibility);
		return this;
	}

	public NetworkErrorLayout setViewListClickable(List<View> viewList,boolean clickable){
		for(View view:viewList){
			view.setClickable(clickable);
		}
		return this;
	}

	public NetworkErrorLayout setViewListClickable(boolean clickable){
		setViewListClickable(viewList,clickable);
		return this;
	}

	public void setOnRefreshClicked(OnRefreshClicked onRefreshClicked) {
		this.onRefreshClicked = onRefreshClicked;

	}

	@OnClick({R.id.tv_load_failure, R.id.tv_makesure_internet_ok, R.id.btn_network_error, R.id.layout_network_error})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.tv_load_failure:
			case R.id.tv_makesure_internet_ok:
			case R.id.btn_network_error:
			case R.id.layout_network_error:
				Logger.i("refreshrefreshrefreshrefresh");
				ToastCompat.makeText(view.getContext(),"我要刷新。。。。。。。。。。。", Toast.LENGTH_SHORT).show();
				if (onRefreshClicked != null)
					onRefreshClicked.onClicked();
				break;
		}
	}

	public interface OnRefreshClicked {
		public void onClicked();
	}


}
