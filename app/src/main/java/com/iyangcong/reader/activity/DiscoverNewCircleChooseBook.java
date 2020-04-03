package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.iyangcong.reader.adapter.DiscoverCircleChooseBookAdapter;
import com.iyangcong.reader.bean.BookIntroduction;
import com.iyangcong.reader.bean.DiscoverCreateCircle;
import com.iyangcong.reader.bean.SearchBookBean;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.ClearEditText;
import com.iyangcong.reader.ui.CustomPtrClassicFrameLayout;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.LoadCountHolder;
import com.iyangcong.reader.utils.NotNullUtils;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

//TODO这个页面关于搜索的部分还需要进一步地测试！
public class DiscoverNewCircleChooseBook extends SwipeBackActivity {

	@BindView(R.id.btnBack)
	ImageButton btnBack;
	@BindView(R.id.textHeadTitle)
	TextView textHeadTitle;
	@BindView(R.id.btnFunction)
	ImageButton btnFunction;
	@BindView(R.id.layout_header)
	LinearLayout layoutHeader;
	@BindView(R.id.ceSearch)
	ClearEditText ceSearch;
	@BindView(R.id.tv_search)
	TextView tvSearch;
	@BindView(R.id.activity_discover_new_circle_choose_book)
	LinearLayout activityDiscoverNewCircleChooseBook;
	@BindView(R.id.rv_books)
	RecyclerView rvBooks;
	@BindView(R.id.book_ptrClassicFrameLayout)
	CustomPtrClassicFrameLayout bookPtrClassicFrameLayout;

	private RecyclerAdapterWithHF adapterWithHF;
	private DiscoverCircleChooseBookAdapter discoverCircleChooseBookAdapter;
	private LinkedList<BookIntroduction> bookList;
	private DiscoverCreateCircle receivedCircle;
	private String userId;
	private LoadCountHolder bookLoadCounter = new LoadCountHolder(){
		@Override
		public void refresh() {
			setPage(1);
			setRefresh(true);
		}
	};
	private LoadCountHolder searchLoadCounter = new LoadCountHolder(){
		@Override
		public void refresh() {
			setPage(1);
			setRefresh(true);
		}
	};
	private State mState = State.LOAD;
	@OnClick({R.id.btnBack, R.id.btnFunction, R.id.tv_search})
	void onBtnClick(View view) {
		switch (view.getId()) {
			case R.id.btnBack:
				finish();
				break;
			case R.id.btnFunction:
				Logger.i("wzp recivedCircle" + receivedCircle.toString());
				updateCircle(receivedCircle, bookList);
				createCircle(receivedCircle);
				break;
			case R.id.tv_search:
				closeInputMethod();
				mState = State.SEARCH;
				autoRefresh(bookPtrClassicFrameLayout);
				break;
		}
	}

	private void closeInputMethod() {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(ceSearch.getWindowToken(), 0);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discover_new_circle_choose_book);
		ButterKnife.bind(this);
		initView();
		setMainHeadView();
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		bookList = new LinkedList<>();
		receivedCircle = (DiscoverCreateCircle) getIntent().getParcelableExtra(Constants.CREATE_CIRLCE);
		userId = CommonUtil.getUserId() + "";
	}

	@Override
	protected void initView() {
		initVaryViewHelper(context, bookPtrClassicFrameLayout, new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onResume();
			}
		});
		discoverCircleChooseBookAdapter = new DiscoverCircleChooseBookAdapter(this, bookList);
		adapterWithHF = new RecyclerAdapterWithHF(discoverCircleChooseBookAdapter);
		discoverCircleChooseBookAdapter.setOnBookIntroductionSelection(new DiscoverCircleChooseBookAdapter.OnBookIntroductionSelection() {
			@Override
			public void onBookIntroductionSelected(BookIntroduction introduction) {
				introduction.setChecked(!introduction.isChecked());
			}
		});
		rvBooks.setAdapter(adapterWithHF);
		rvBooks.setLayoutManager(new LinearLayoutManager(context));
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
	}

	private void loadMoreHanlder() {
		switch (mState){
			case LOAD:
				bookLoadCounter.loadMore();
				getCollectedBooks();
				break;
			case SEARCH:
				searchLoadCounter.loadMore();
				searOnline(ceSearch.getText().toString());
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
				searchLoadCounter.refresh();
				searOnline(ceSearch.getText().toString());
				break;

		}
	}

	@Override
	protected void setMainHeadView() {
		textHeadTitle.setText("选择图书");
		btnBack.setImageResource(R.drawable.btn_back);
		btnFunction.setImageResource(R.drawable.ic_next);
		btnFunction.setVisibility(View.VISIBLE);
		ceSearch.setHint(R.string.input_book_name);
		initSearch(ceSearch);
	}

	private void initSearch(final ClearEditText et) {
		et.setClearListener(new ClearEditText.ClearListener() {
			@Override
			public void clear() {
				et.setText("");
				mState = State.LOAD;
				for (BookIntroduction introduction : bookList) {
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

	/**
	 * 本地搜索
	 *
	 * @param temp
	 */
	private void searchLocal(String[] temp) {
		if (temp.length > 0) {
			List<BookIntroduction> tempList = new ArrayList<BookIntroduction>(bookList);
			List<BookIntroduction> searchedList = new ArrayList<BookIntroduction>();
			for (String str : temp) {
				if (str.trim().equals(""))
					continue;
				Iterator<BookIntroduction> iterator = tempList.iterator();
				while (iterator.hasNext()) {
					BookIntroduction introduction = iterator.next();
					//如果遍历的时候取出来对比的introduction的bookName中包含这个字符，
					// 或者书籍已经被选中，
					// 并且在searchedList表中没有加入过，
					// 给searchedList加入该
					if ((introduction.getBookName().contains(str) || introduction.isChecked()) && !searchedList.contains(introduction)) {
						searchedList.add(introduction);
					}
				}
			}
			Logger.i("wzp choosedList:" + searchedList);
			discoverCircleChooseBookAdapter = new DiscoverCircleChooseBookAdapter(context, searchedList.size() > 0 ? searchedList : bookList);
			adapterWithHF =  new RecyclerAdapterWithHF(discoverCircleChooseBookAdapter);
			rvBooks.setAdapter(adapterWithHF);
		}
	}

	private void searOnline(String key) {
		if (isNull(context, key)) {
			return;
		}
		showLoadingDialog();
		OkGo.get(Urls.searchBookOnline)
				.params("keyword", key)
				.params("userId", userId)
				.params("currentPageNum",searchLoadCounter.getPage())
				.params("pageSize",searchLoadCounter.getPageSize())
				.params("categoryId",0)
				.params("courseId",0)
				.params("languageTypeId",0)
				.params("languageDiffcultyId",0)
				.execute(new JsonCallback<IycResponse<SearchBookBean>>(context) {
					@Override
					public void onSuccess(IycResponse<SearchBookBean> bookList, Call call, Response response) {
						if (isNull(bookList) || isNull(bookList.getData()) || isNull(bookList.getData().getBookInfoList()))
							return;
						for (BookIntroduction book : DiscoverNewCircleChooseBook.this.bookList) {
							book.setVisible(book.isChecked());
						}
						for (BookIntroduction introduction : bookList.getData().getBookInfoList()) {
							introduction.setVisible(true);
							if (DiscoverNewCircleChooseBook.this.bookList.contains(introduction)) {
								DiscoverNewCircleChooseBook.this.bookList.remove(introduction);
							}
							DiscoverNewCircleChooseBook.this.bookList.addFirst(introduction);
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

	@Override
	protected void onResume() {
		super.onResume();
		autoRefresh(bookPtrClassicFrameLayout);
	}


	/**
	 * 将选中的BookList经过格式化以后赋给DiscoverCreateCircle
	 *
	 * @param circle
	 * @param blist  by wzp
	 */
	private void updateCircle(DiscoverCreateCircle circle, List<BookIntroduction> blist) {
		circle.setSbooks(checkSelectedBooks(blist));
	}

	/**
	 * 获取收藏图书
	 */
	private void getCollectedBooks() {
		OkGo.get(Urls.DiscoverCircleGetCollectedBookList)
				.params("userId", userId)
//				.params("type", 0)
				.params("currentPageNum", bookLoadCounter.getPage())
				.params("pageSize", bookLoadCounter.getPageSize())
				.execute(new JsonCallback<IycResponse<List<BookIntroduction>>>(this) {
					@Override
					public void onSuccess(IycResponse<List<BookIntroduction>> bookIntroductionIycResponse, Call call, Response response) {
						if(bookLoadCounter.isRefresh()){
							bookList.clear();
						}
						for (BookIntroduction book : bookIntroductionIycResponse.getData()) {
							book.setVisible(true);
							bookList.add(book);
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
					searchLoadCounter.loadMoreFailed();
				}
				loadMoreFailed(bookPtrClassicFrameLayout);
			}
		}else{
			boolean isEnd = (mState == State.LOAD?
					(listIycResponse.size() < bookLoadCounter.getPageSize()):
					(listIycResponse.size()<searchLoadCounter.getPageSize()));
			if(bookLoadCounter.isRefresh()){
				refreshSuccess(bookPtrClassicFrameLayout);
				bookPtrClassicFrameLayout.setLoadMoreEnable(!isEnd);
			}else{
				loadMoreSuccess(bookPtrClassicFrameLayout,isEnd);
			}
		}
	}

	/**
	 * 检查BookList中哪些book被选中；
	 *
	 * @param blist
	 * @return book的id，格式：用逗号连接
	 * by wzp
	 */
	private String checkSelectedBooks(List<BookIntroduction> blist) {
		StringBuilder sb = new StringBuilder("");
		for (BookIntroduction book : blist)
			if (book.isChecked())
				sb.append(book.getBookId() + ",");
		Logger.i("listtttttt" + blist.toString());
		if (sb.length() == 0)
			return "-1";
		return sb.substring(0, sb.length() - 1).toString();

	}

	public void createCircle(DiscoverCreateCircle createCircle) {
		if (!NotNullUtils.isNull(context, createCircle.getCover(), "")) {
			createCircle(createCircle.getCover());
		}
	}

	public void createCircle(String photoPath) {
		OkGo.get(Urls.DiscoverCircleCreateURL)
				.params("userid", userId)
				.params(getParams(receivedCircle))
				.params("photo", photoPath)
				.execute(new JsonCallback<IycResponse<String>>(this) {
					@Override
					public void onSuccess(IycResponse<String> stringIycResponse, Call call, Response response) {
						Logger.i(stringIycResponse.getData());
						Intent intent = new Intent(context, DiscoverCircleDetailActivity.class);
						intent.putExtra(Constants.circleId, Integer.parseInt(stringIycResponse.getData()));
						intent.putExtra(Constants.circleName, receivedCircle.getGroupname());
						intent.putExtra(Constants.CIRCLE_CATEGORY, receivedCircle.getCategory());
						Logger.i("category:" + receivedCircle.getCategory());
						startActivity(intent);
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
					}
				});

	}

	public HashMap<String, String> getParams(DiscoverCreateCircle circle) {
		HashMap<String, String> hashMap = new HashMap<>();
		hashMap.put("authority", circle.getAuthority() + "");
		hashMap.put("category", circle.getCategory() + "");
		hashMap.put("degree", circle.getDegree() + "");
		hashMap.put("groupdesc", circle.getGroupdesc());
		hashMap.put("groupname", circle.getGroupname());
		hashMap.put("sbooks", circle.getSbooks());
		hashMap.put("sfriends", circle.getSfriends());
		hashMap.put("tag", circle.getTag());
		return hashMap;
	}

	public enum State{
		LOAD,
		SEARCH;
	}
}
