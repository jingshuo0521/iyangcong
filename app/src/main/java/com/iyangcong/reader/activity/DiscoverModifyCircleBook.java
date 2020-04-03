package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.ChoosableBookAdapter;
import com.iyangcong.reader.adapter.DiscoverCircleChooseBookAdapter;
import com.iyangcong.reader.bean.BookIntroduction;
import com.iyangcong.reader.bean.DiscoverCreateCircle;
import com.iyangcong.reader.bean.SearchBookBean;
import com.iyangcong.reader.bean.ShelfBookChoosable;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.ClearEditText;
import com.iyangcong.reader.ui.CustomPtrClassicFrameLayout;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.AppManager;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.LoadCountHolder;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

/**
 * Created by WuZepeng on 2017-06-14.
 */

public class DiscoverModifyCircleBook extends SwipeBackActivity {

	@BindView(R.id.btnBack)
	ImageButton btnBack;
	@BindView(R.id.textHeadTitle)
	TextView textHeadTitle;
	@BindView(R.id.btnFunction)
	ImageButton btnFunction;
	@BindView(R.id.tv_goods_num)
	TextView tvGoodsNum;
	@BindView(R.id.btnFunction1)
	ImageButton btnFunction1;
	@BindView(R.id.tv_goods_num1)
	TextView tvGoodsNum1;
	@BindView(R.id.layout_header)
	LinearLayout layoutHeader;
	@BindView(R.id.ceSearch)
	ClearEditText ceSearch;
	@BindView(R.id.tv_search)
	TextView tvSearch;
	@BindView(R.id.search_bar)
	LinearLayout searchBar;
	@BindView(R.id.rv_books)
	RecyclerView rvToChooseBooks;
	@BindView(R.id.book_ptrClassicFrameLayout)
	CustomPtrClassicFrameLayout bookPtrClassicFrameLayout;
	@BindView(R.id.rv_circle_book)
	RecyclerView rvCircleBook;
	@BindView(R.id.layout_discover_circle_book)
	LinearLayout layoutDiscoverCircleBook;

	private ChoosableBookAdapter discoverCircleBookAdapter;
	private List<ShelfBookChoosable> originalBookList;
	private LinkedList<BookIntroduction> onlineBookList;
	private RecyclerAdapterWithHF adapterWithHF;
	private LoadCountHolder bookLoadCounter = new LoadCountHolder(){
		@Override
		public void refresh() {
			setRefresh(true);
			setPage(1);
		}
	};
	private LoadCountHolder onlineLoadCounter = new LoadCountHolder(){
		@Override
		public void refresh() {
			setRefresh(true);
			setPage(1);
		}
	};
	private State mState;
	private int mGroupId;
	private DiscoverCreateCircle receivedCircle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_modify_circle_book);
		ButterKnife.bind(this);
		setMainHeadView();
		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		getGroupBooks(mGroupId);
		autoRefresh(bookPtrClassicFrameLayout);
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		mGroupId = getIntent().getIntExtra(Constants.groupId,-1);
		if(mGroupId != -1){
			Logger.i("wzp mGroup = " + mGroupId);
		}
		receivedCircle = (DiscoverCreateCircle)getIntent().getParcelableExtra(Constants.CREATE_CIRLCE);
		if(receivedCircle != null){
			Logger.i("wzp receivedCircle " + receivedCircle.getGroupname());
		}
	}

	@Override
	protected void initView() {
		originalBookList = new ArrayList<>();
		onlineBookList = new LinkedList<>();
		mState = State.LOAD;
		discoverCircleBookAdapter = new ChoosableBookAdapter(context, originalBookList);
		rvCircleBook.setAdapter(discoverCircleBookAdapter);
		rvCircleBook.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
		DiscoverCircleChooseBookAdapter adapter = new DiscoverCircleChooseBookAdapter(context, onlineBookList);
		adapterWithHF = new RecyclerAdapterWithHF(adapter);
		rvToChooseBooks.setAdapter(adapterWithHF);
		rvToChooseBooks.setLayoutManager(new LinearLayoutManager(context));
		bookPtrClassicFrameLayout.setHorizontalScrollBarEnabled(false);
		final Handler handler = new Handler();
		bookPtrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {
			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						refreshHandler();
					}
				},0);
			}
		});
		bookPtrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void loadMore() {
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						loadMoreHanlder();
					}
				},0);
			}
		});
		initSearch(ceSearch);
	}

	private void loadMoreHanlder() {
		switch (mState){
			case LOAD:
				bookLoadCounter.loadMore();
				getCollectedBooks();
				break;
			case SEARCH:
				onlineLoadCounter.loadMore();
				searchBookOnline(ceSearch.getText().toString());
				break;
		}

	}

	private void refreshHandler() {
		switch (mState){
			case LOAD:
				bookLoadCounter.refresh();
				getCollectedBooks();
				break;
			case SEARCH:
				onlineLoadCounter.refresh();
				searchBookOnline(ceSearch.getText().toString());
				break;
		}
	}

	@Override
	protected void setMainHeadView() {
		btnBack.setImageResource(R.drawable.btn_back);
		btnBack.setVisibility(View.VISIBLE);
		textHeadTitle.setText("选择图书");
		btnFunction.setVisibility(View.VISIBLE);
		btnFunction.setImageResource(R.drawable.finish_modify);
	}

	private void closeInputMethod() {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(ceSearch.getWindowToken(), 0);
	}

	private void initSearch(final ClearEditText et) {
		et.setClearListener(new ClearEditText.ClearListener() {
			@Override
			public void clear() {
				et.setText("");
				mState = State.LOAD;
				for (BookIntroduction introduction : onlineBookList) {
					introduction.setVisible(true);
				}
				adapterWithHF.notifyDataSetChanged();
			}
		});
		et.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
//                searchLocal(editable.toString().trim().split(""));
			}
		});
	}

	@OnClick({R.id.btnBack, R.id.btnFunction, R.id.tv_search})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnBack:
				finish();
				break;
			case R.id.btnFunction:
				modifyCircleBooks();
				break;
			case R.id.tv_search:
				closeInputMethod();
				mState = State.SEARCH;
				autoRefresh(bookPtrClassicFrameLayout);
				break;
		}
	}

	private void searchBookOnline(String key){
		if (isNull(context, key)) {
			return;
		}
		showLoadingDialog();
		OkGo.get(Urls.searchBookOnline)
				.params("keyword", key)
				.params("userId", CommonUtil.getUserId())
				.params("currentPageNum",onlineLoadCounter.getPage())
				.params("pageSize",onlineLoadCounter.getPageSize())
				.params("categoryId",0)
				.params("courseId",0)
				.params("languageTypeId",0)
				.params("languageDiffcultyId",0)
				.execute(new JsonCallback<IycResponse<SearchBookBean>>(context) {
					@Override
					public void onSuccess(IycResponse<SearchBookBean> bookList, Call call, Response response) {
						if (isNull(bookList) || isNull(bookList.getData()) || isNull(bookList.getData().getBookInfoList()))
							return;
						for (BookIntroduction book : onlineBookList) {
							book.setVisible(book.isChecked());
						}
						for (BookIntroduction introduction : bookList.getData().getBookInfoList()) {
							introduction.setVisible(true);
							if (onlineBookList.contains(introduction)) {
								onlineBookList.remove(introduction);
							}
							onlineBookList.addFirst(introduction);
						}
						adapterWithHF.notifyDataSetChanged();
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
						ToastCompat.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onAfter(IycResponse<SearchBookBean> listIycResponse, Exception e) {
						super.onAfter(listIycResponse, e);
						dismissLoadingDialig();
						adjustPtr(listIycResponse==null?null:(listIycResponse.getData()==null?null:listIycResponse.getData().getBookInfoList()));
					}
				});
	}
	//获取收藏图书
	private void getCollectedBooks(){
		OkGo.get(Urls.DiscoverCircleGetCollectedBookList)
				.params("userId", CommonUtil.getUserId())
//				.params("type", 0)
				.params("currentPageNum", bookLoadCounter.getPage())
				.params("pageSize", bookLoadCounter.getPageSize())
				.execute(new JsonCallback<IycResponse<List<BookIntroduction>>>(this) {
					@Override
					public void onSuccess(IycResponse<List<BookIntroduction>> bookIntroductionIycResponse, Call call, Response response) {
						if(bookLoadCounter.isRefresh()){
							onlineBookList.clear();
						}
						for (BookIntroduction book : bookIntroductionIycResponse.getData()) {
							book.setVisible(true);
							onlineBookList.add(book);
						}
						adapterWithHF.notifyDataSetChanged();
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
					}

					@Override
					public void onAfter(IycResponse<List<BookIntroduction>> listIycResponse, Exception e) {
						super.onAfter(listIycResponse, e);
						adjustPtr(listIycResponse==null?null:listIycResponse.getData());
					}
				});
	}

	private void adjustPtr(List<BookIntroduction> listIycResponse) {
		if(isNull(listIycResponse)){
			if(bookLoadCounter.isRefresh()){
				refreshFailed(bookPtrClassicFrameLayout);
			}else{
				if(mState == State.LOAD){
					bookLoadCounter.loadMoreFailed();
				}else{
					onlineLoadCounter.loadMoreFailed();
				}
				loadMoreFailed(bookPtrClassicFrameLayout);
			}
		}else{
			boolean isEnd = (mState == State.LOAD?
					(listIycResponse.size() < bookLoadCounter.getPageSize()):
					(listIycResponse.size()<onlineLoadCounter.getPageSize()));
			if(bookLoadCounter.isRefresh()){
				refreshSuccess(bookPtrClassicFrameLayout);
				bookPtrClassicFrameLayout.setLoadMoreEnable(!isEnd);
			}else{
				loadMoreSuccess(bookPtrClassicFrameLayout,isEnd);
			}
		}
	}

	/**
	 * 获得圈子图书
	 * @param groupId
	 */
	private void getGroupBooks(long groupId){
		if(groupId <= 0){
			Logger.e("wzp groupId = " + groupId);
		}
		OkGo.get(Urls.GROUPBOOKS)
				.params("groupid", groupId)
				.execute(new JsonCallback<IycResponse<List<ShelfBookChoosable>>>(context) {
					@Override
					public void onSuccess(IycResponse<List<ShelfBookChoosable>> shelfBooks, Call call, Response response) {
						if(isNull(shelfBooks)||isNull(shelfBooks.getData())){
							Logger.e("wzp /groups/getgroupbooks 请求下来的数据有错误" );
							return;
						}
						originalBookList.clear();
						Iterator<ShelfBookChoosable> iterator = shelfBooks.getData().iterator();
						while (iterator.hasNext()){
							ShelfBookChoosable bean = iterator.next();
							bean.setChoosed(true);
							originalBookList.add(bean);
						}
						discoverCircleBookAdapter.notifyDataSetChanged();
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
						Logger.e("wzp " + e.getMessage());
					}
				});
	}

	private enum State{
		LOAD,
		SEARCH;
	}

	//获取选中的图书
	private String getChoosedBooks(){
		TreeSet<BookOnlyId> onlyIdTreeSet = new TreeSet<>();
		Iterator<BookIntroduction> bookIntroductionIterator = onlineBookList.iterator();
		while (bookIntroductionIterator.hasNext()){
			BookIntroduction introduction = bookIntroductionIterator.next();
			if(introduction.isChecked()){
				BookOnlyId bookOnlyId = new BookOnlyId(introduction.getBookId());
				onlyIdTreeSet.add(bookOnlyId);
			}
		}
		Iterator<ShelfBookChoosable> shelfBookChoosableIterator = originalBookList.iterator();
		while (shelfBookChoosableIterator.hasNext()){
			ShelfBookChoosable shelfBookChoosable = shelfBookChoosableIterator.next();
			if(shelfBookChoosable.isChoosed()){
				BookOnlyId bookOnlyId = new BookOnlyId(shelfBookChoosable.getBookId());
				onlyIdTreeSet.add(bookOnlyId);
			}
		}
		String result = onlyIdTreeSet.toString().replace("[","");
		result = result.replace("]","");
		result = result.replaceAll(" ","");
		Logger.i("wzp  getChoosedBooks:" + result);
		return result;
	}

	//修改圈子图书
	private void modifyCircleBooks(){
		OkGo.get(Urls.UpdateCircleBooks)
				.params("userid",CommonUtil.getUserId())
				.params("groupid",mGroupId)
				.params("sbooks",getChoosedBooks())
				.execute(new JsonCallback<IycResponse<String>>(context){
					@Override
					public void onSuccess(IycResponse<String> IycResponse, Call call, Response response) {
						if(IycResponse.statusCode == -1){
							ToastCompat.makeText(context,IycResponse.getMsg(),Toast.LENGTH_SHORT).show();
							return;
						}
						ToastCompat.makeText(context,"修改成功",Toast.LENGTH_SHORT).show();
						AppManager.getAppManager().finishActivity(DiscoverCircleDetailActivity.class,DiscoverNewCircle3.class);
                        finish();
                        Intent intent=new Intent(context,DiscoverCircleDetailActivity.class);
                        intent.putExtra(Constants.circleId,mGroupId);
                        intent.putExtra(Constants.circleName,receivedCircle.getGroupname());
                        intent.putExtra(Constants.CIRCLE_CATEGORY,receivedCircle.getCategory());
                        startActivity(intent);
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
						ToastCompat.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
					}
				});
		}

	private class BookOnlyId implements Comparable<BookOnlyId>{
		private long bookId;

		public BookOnlyId(long bookId) {
			this.bookId = bookId;
		}

		@Override
		public int compareTo(@NonNull BookOnlyId bookOnlyId) {
			return (int)(bookId-bookOnlyId.bookId);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			BookOnlyId that = (BookOnlyId) o;

			return bookId == that.bookId;

		}

		@Override
		public String toString() {
			return bookId+"";
		}
	}
}
