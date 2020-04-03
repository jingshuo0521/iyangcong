package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.Bundle;

import com.iyangcong.reader.bean.MonthlyMarketBookListItem;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.enumset.MonthlyBookFrom;
import com.iyangcong.reader.interfaceset.OnItemClickedListener;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

/**
 * Created by WuZepeng on 2017-06-22.
 */

public class BookMarketMonlyBookActivity extends AbsMonthlyBookActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		super.initData(savedInstanceState);
		setTitle("包月");
	}

	@Override
	protected void initView() {
		super.initView();
		monthlyAdapter.setOnItemClickedListener(new OnItemClickedListener() {
			@Override
			public void onItemClickedListener(int position) {
				Intent intent = new Intent(context, MonthlyBookDetailActivity.class);
				intent.putExtra(Constants.FROM_BOOK_MARKET_OR_MINE, MonthlyBookFrom.BOOK_MARKET);
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
		getDatasFromNetword(10);
	}

	private void getDatasFromNetword(int sum) {
		OkGo.get(Urls.HotMonthlyBookList)
				.params("num", sum)
				.execute(new JsonCallback<IycResponse<List<MonthlyMarketBookListItem>>>(context) {
					@Override
					public void onSuccess(IycResponse<List<MonthlyMarketBookListItem>> monthlyMarketBookListItemIycResponse, Call call, Response response) {
						if(isNull(monthlyMarketBookListItemIycResponse)||isNull(monthlyMarketBookListItemIycResponse.getData())){
							Logger.e("wzp monthlyMarketBookListItemIycResponse = " + monthlyMarketBookListItemIycResponse);
							return;
						}
						mItemList.clear();
						mItemList.addAll(monthlyMarketBookListItemIycResponse.getData());
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
