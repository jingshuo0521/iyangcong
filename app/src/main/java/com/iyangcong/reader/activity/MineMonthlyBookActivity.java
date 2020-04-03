package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.Bundle;

import com.iyangcong.reader.bean.MonthlyMarketBookListItem;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.enumset.MonthlyBookFrom;
import com.iyangcong.reader.interfaceset.OnItemClickedListener;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

/**
 * Created by WuZepeng on 2017-06-23.
 */

public class MineMonthlyBookActivity extends AbsMonthlyBookActivity {

	@Override
	protected void initData(Bundle savedInstanceState) {
		super.initData(savedInstanceState);
		setTitle("我的包月");
	}

	@Override
	protected void initView() {
		super.initView();
		monthlyAdapter.setOnItemClickedListener(new OnItemClickedListener() {
			@Override
			public void onItemClickedListener(int position) {
				Intent intent = new Intent(context, MonthlyBookDetailActivity.class);
				intent.putExtra(Constants.FROM_BOOK_MARKET_OR_MINE, MonthlyBookFrom.MINE);
				intent.putExtra(Constants.MONTHLY_BOOK_ID,Integer.parseInt(mItemList.get(position).getId()));
				intent.putExtra(Constants.MONTHLY_BOOK_LIST_NAME,mItemList.get(position).getName());
				intent.putExtra(Constants.MONTHLY_BOOK_SPECIAL_PRICE,mItemList.get(position).getBookSpecialPrice());
				startActivity(intent);
			}
		});
	}

	@Override
	protected void refreshHandler() {
		super.refreshHandler();
		getMyMontlyBookList();
	}

	@Override
	protected void loadMoreHandler() {
		super.loadMoreHandler();
		getMyMontlyBookList();
	}

	/**
	 * 获取我的包月包列表
	 */
	private void getMyMontlyBookList(){
		if(!CommonUtil.getLoginState()){
			Logger.e("wzp 还没有登陆");
			return;
		}
		OkGo.get(Urls.MineMonthlyBookUrl)
				.params("pageNo",loadCountHolder.getPage())
				.params("pageSize",loadCountHolder.getPageSize())
				.params("userId", CommonUtil.getUserId())
				.execute(new JsonCallback<IycResponse<List<MonthlyMarketBookListItem>>>(context) {
					@Override
					public void onSuccess(IycResponse<List<MonthlyMarketBookListItem>> listIycResponse, Call call, Response response) {
						if(isNull(listIycResponse)||isNull(listIycResponse.getData())){
							return;
						}
						if(loadCountHolder.isRefresh()){
							mItemList.clear();
						}
						mItemList.addAll(listIycResponse.getData());
						adapterWithHF.notifyDataSetChanged();
					}

					@Override
					public void onAfter(IycResponse<List<MonthlyMarketBookListItem>> listIycResponse, Exception e) {
						super.onAfter(listIycResponse, e);
						finishRefreshOrLoadMore(listIycResponse==null?null:listIycResponse.getData());
					}
				});
	}
}
