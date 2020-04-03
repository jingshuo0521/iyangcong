package com.iyangcong.reader;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.activity.BannerUrlAcitivty;
import com.iyangcong.reader.activity.BookMarketBookDetailsActivity;
import com.iyangcong.reader.activity.BookMarketBookListActivity;
import com.iyangcong.reader.activity.BookMarketSearchActivity;
import com.iyangcong.reader.activity.DiscoverCircleDetailActivity;
import com.iyangcong.reader.activity.DiscoverReadingPartyDetailsActivity;
import com.iyangcong.reader.activity.DiscoverTopicActivity;
import com.iyangcong.reader.activity.DiscoverySearchActivity;
import com.iyangcong.reader.activity.MineShoppingActivity;
import com.iyangcong.reader.activity.NewMaeesageActivity;
import com.iyangcong.reader.activity.SimpleCaptureActivity;
import com.iyangcong.reader.app.UpdateManager;
import com.iyangcong.reader.base.BaseActivity;
import com.iyangcong.reader.bean.AdvertisingPage;
import com.iyangcong.reader.bean.BuildBookCommand;
import com.iyangcong.reader.bean.MineShoppingBookIntroduction;
import com.iyangcong.reader.bean.ShelfBook;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.BookDao;
import com.iyangcong.reader.database.dao.BookInfoDao;
import com.iyangcong.reader.epub.BookInfoEpubDeleteInfo;
import com.iyangcong.reader.epub.EpubProcessResult;
import com.iyangcong.reader.epub.database.EpubBookInfo;
import com.iyangcong.reader.epub.database.EpubBookInfoDao;
import com.iyangcong.reader.event.SlideEvent;
import com.iyangcong.reader.fragment.BookMarketFragment;
import com.iyangcong.reader.fragment.BookShelfFragment;
import com.iyangcong.reader.fragment.DiscoverFragment;
import com.iyangcong.reader.fragment.MineFragment;
import com.iyangcong.reader.interfaceset.DeviceType;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.MyDialog;
import com.iyangcong.reader.utils.BuiltInBookUtil;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.DisplayUtil;
import com.iyangcong.reader.utils.FileHelper;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.HttpUtils;
import com.iyangcong.reader.utils.InvokerDESServiceUitls;
import com.iyangcong.reader.utils.LoginUtils;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.UIHelper;
import com.iyangcong.reader.utils.Urls;
import com.iyangcong.reader.utils.floatVideoWindow.PIPManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.InvokerDESServiceUitls.StartService;
import static com.iyangcong.reader.utils.Urls.PersonShopcartURL;

public class MainActivity extends BaseActivity implements View.OnClickListener{

	public static boolean exitLogin;

	private int showType;
	private static final String CURR_INDEX = "currIndex";
	private static int currIndex = 0;
	private CountDownTimer mCountDownTimer;
	private MyDialog menuDialog;
	private Fragment fragment;
	private ArrayList<String> fragmentTags;
	private String url;
	private static Boolean isExit = false;
	private SharedPreferenceUtil sharedPreferenceUtil;
	private String bookIds;
	private float price;
	private InvokerDESServiceUitls mInvoker;
	@BindView(R.id.group)
	RadioGroup group;
	@BindView(R.id.btnBack)
	ImageButton btnBack;
	@BindView(R.id.textHeadTitle)
	public TextView tvHeadTitle;
	@BindView(R.id.start_skip_count_down)
	TextView mCountDownTextView;
	@BindView(R.id.adv)
	ImageView imAdv;
	@BindView(R.id.btnFunction)
	ImageButton btnFunction;
	@BindView(R.id.ibSign)
	ImageButton ibSign;
	@BindView(R.id.layout_header)
	LinearLayout llHeader;
	@BindView(R.id.foot_book_market)
	RadioButton footBookMarket;
	@BindView(R.id.foot_book_shelf)
	RadioButton footBookShelf;
	@BindView(R.id.fragment_container)
	FrameLayout fragmentContainer;
	@BindView(R.id.foot_discover)
	RadioButton footDiscover;
	@BindView(R.id.main_footbar_mine)
	RadioButton mainFootbarMine;
	@BindView(R.id.viewSplitLine)
	View viewSplitLine;
	@BindView(R.id.layoutFooter)
	RelativeLayout layoutFooter;
	@BindView(R.id.tv_goods_num)
	TextView tvGoodsNum;
//	@BindView(R.id.all_view)
//	LinearLayout allView;
	@BindView(R.id.search_bar_home)
	RelativeLayout searchBarHome;
	@BindView(R.id.search_tips)
	TextView tvSearchTips;

	public void setCurrIndex(int currIndex){
		this.currIndex = currIndex;
	}

	public void setGroupIcon(int drawable){
		group.check(drawable);
	}

	@OnClick({R.id.btnBack, R.id.btnFunction,R.id.ibSign,R.id.search_bar_home})
	void OnButtonClick(View view) {
		Intent intent = new Intent();
		switch (view.getId()) {
			case R.id.btnBack:
//				switch (currIndex) {
//					case 0:
//						intent.setClass(this, BookMarketSearchActivity.class);
//						startActivity(intent);
//						break;
//					case 1:
////						startActivity(new Intent(this, ChapterTestActivity.class));
////						if (CommonUtil.getLoginState()) {
////							intent.setClass(this, ShelfCloudActivity.class);
////							startActivity(intent);
////						} else {
////							intent.setClass(this, LoginActivity.class);
////							startActivity(intent);
////						}
//						break;
//					case 2:
//						intent.setClass(this, DiscoverySearchActivity.class);
//						startActivity(intent);
//						break;
//				}
				break;
			case R.id.btnFunction:
				switch (currIndex) {
					case 0:
						LoginUtils loginUtils = new LoginUtils();
						if (loginUtils.isLogin(this)) {
							intent.setClass(this, MineShoppingActivity.class);
							startActivity(intent);
						}
						break;
					case 1:
                        menuDialog = new MyDialog(MainActivity.this,itemsOnClick,R.style.DialogTheme);
//                        menuPopup.showAsDropDown(llHeader,-10,-15,Gravity.RIGHT);
                        menuDialog.show();
						break;
					case 3:
						intent.setClass(this, NewMaeesageActivity.class);
						startActivity(intent);
						break;
				}
				break;

			case R.id.ibSign:
//				intent.setClass(this,SignCalendarActivity.class);
//				startActivity(intent);
				Intent i = new Intent(MainActivity.this, SimpleCaptureActivity.class);
				int REQUEST_QR_CODE = 1;
				MainActivity.this.startActivityForResult(i, REQUEST_QR_CODE);
				break;
			case R.id.search_bar_home:
				switch (currIndex) {
					case 0:
						intent.setClass(this, BookMarketSearchActivity.class);
						startActivity(intent);
						break;
					case 1:
//						startActivity(new Intent(this, ChapterTestActivity.class));
//						if (CommonUtil.getLoginState()) {
//							intent.setClass(this, ShelfCloudActivity.class);
//							startActivity(intent);
//						} else {
//							intent.setClass(this, LoginActivity.class);
//							startActivity(intent);
//						}
						break;
					case 2:
						intent.setClass(this, DiscoverySearchActivity.class);
						startActivity(intent);
						break;
				}
				break;
		}
	}

	private View.OnClickListener itemsOnClick = new View.OnClickListener(){
		public void onClick(View v) {
            menuDialog.dismiss();
			BookShelfFragment bookShelfFragment=(BookShelfFragment)fragmentManager.findFragmentByTag(fragmentTags.get(currIndex));
			switch (v.getId()) {
				case R.id.only_localshelf:
					tvHeadTitle.setText("本地书架");
					bookShelfFragment.setLocalShelf(true);
					bookShelfFragment.showShelf();
					bookShelfFragment.showProgress();
					if (bookShelfFragment.getmBookGroupLists().size()==0||
                            bookShelfFragment.getmBookGroupLists()==null){
					    bookShelfFragment.showNoBook(true);
                    }else{
                        bookShelfFragment.showNoBook(false);
                    }
					break;
				case R.id.shelf_setting:
					bookShelfFragment.showShelfSettingView();
					break;
				case R.id.all_shelf:
					tvHeadTitle.setText("云书架");
					bookShelfFragment.setLocalShelf(false);
					bookShelfFragment.showNoBook(false);
					bookShelfFragment.showShelf();
					bookShelfFragment.showProgress();
					break;
			}
			if(!CommonUtil.getLoginState()){
				bookShelfFragment.showNoBook(true);
			}
		}

	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
		showType=  sharedPreferenceUtil.getInt(SharedPreferenceUtil.SHOW_TYPE,0);
		MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
		Log.i("shao ","onCreate--------=====================-");
		/**
		 * 百度云推送
		 */
		if (sharedPreferenceUtil.getBoolean(SharedPreferenceUtil.IS_RECEIVE_NOTICE, false)) {
//			PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, Constants.BAIDU_API_KEY);
//            // 2.自定义通知
//            // 设置自定义的通知样式，具体API介绍见用户手册
//            // 请在通知推送界面中，高级设置->通知栏样式->自定义样式，选中并且填写值：1，
//            // 与下方代码中 PushManager.setNotificationBuilder(this, 1, cBuilder)中的第二个参数对应
//            CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
//                    R.layout.notification_custom_builder,
//                    R.id.notification_icon,
//                    R.id.notification_title,
//                    R.id.notification_text);
//
//            cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
//            cBuilder.setNotificationDefaults(Notification.DEFAULT_VIBRATE);
//            cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
//            cBuilder.setLayoutDrawable(this.getApplicationInfo().icon);
//            cBuilder.setNotificationSound(Uri.withAppendedPath(
//                    MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "6").toString());
//            // 若您的应用需要适配Android O（8.x）系统，且将目标版本targetSdkVersion设置为26及以上时：
//            // 可自定义channelId/channelName, 若不设置则使用默认值"Push"；
//            // 注：非targetSdkVersion 26的应用无需以下2行调用且不会生效
//            cBuilder.setChannelId("Push");
//            cBuilder.setChannelName("Push");
//            // 推送高级设置，通知栏样式设置为下面的ID，ID应与server下发字段notification_builder_id值保持一致
//            PushManager.setNotificationBuilder(this, 1, cBuilder);
		}

		sharedPreferenceUtil.putString(SharedPreferenceUtil.PHONE_MODEL, Build.MODEL);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}
		final boolean firstTimeUse = SharedPreferenceUtil.getInstance().getBoolean("first-time-use", true);
		exitLogin = getIntent().getBooleanExtra("ExitLogin",false);
		if (exitLogin) {
			SharedPreferenceUtil.getInstance().putBoolean("first-time-use", false);
			mCountDownTextView.setVisibility(View.GONE);
			imAdv.setVisibility(View.GONE);
			//allView.setVisibility(View.VISIBLE);
		}else if (firstTimeUse) {
			SharedPreferenceUtil.getInstance().putBoolean("first-time-use", false);
			mCountDownTextView.setVisibility(View.GONE);
			imAdv.setVisibility(View.GONE);
			//allView.setVisibility(View.VISIBLE);
			autoUpgrade();
		} else {
			getAdvPage();
		}
        init();
	}


	private void getAdvPage() {
		OkGo.get(Urls.AdvPage)//
				.execute(new JsonCallback<IycResponse<List<AdvertisingPage>>>(this) {
					@Override
					public void onSuccess(IycResponse<List<AdvertisingPage>> IycResponse, Call call, Response response) {
						showAdvaPage(IycResponse.getData().get(0));
					}
					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
						mCountDownTextView.setVisibility(View.GONE);
						imAdv.setVisibility(View.GONE);
						//allView.setVisibility(View.VISIBLE);
						autoUpgrade();
					}
				});
	}
	private void showAdvaPage(final AdvertisingPage advertisingPage){
		if(!HttpUtils.isImgUrl(advertisingPage.getBannerUrl())){
			//没有广告页
			mCountDownTextView.setVisibility(View.GONE);
			imAdv.setVisibility(View.GONE);
			//allView.setVisibility(View.VISIBLE);
			autoUpgrade();
			return;
		}
		mCountDownTextView.setVisibility(View.VISIBLE);
		imAdv.setVisibility(View.VISIBLE);
		mCountDownTimer = new CountDownTimer(5100,1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				if (millisUntilFinished>=1000){
					mCountDownTextView.setText( millisUntilFinished / 1000-1 + "s 跳过");
				}
			}
			@Override
			public void onFinish() {
				mCountDownTextView.setVisibility(View.GONE);
				imAdv.setVisibility(View.GONE);
				//allView.setVisibility(View.VISIBLE);
				autoUpgrade();
			}
		};
		mCountDownTimer.start();
		GlideImageLoader.displayNoDefault(this, imAdv,advertisingPage.getBannerUrl() );
		mCountDownTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCountDownTextView.setVisibility(View.GONE);
				imAdv.setVisibility(View.GONE);
				//allView.setVisibility(View.VISIBLE);
				autoUpgrade();
				if (mCountDownTimer != null) {
					mCountDownTimer.cancel();
				}
			}
		});
		imAdv.setOnClickListener(this);
		//设置item的点击事件
		imAdv.setOnClickListener(new View.OnClickListener() {
			Intent intent;

			@Override
			public void onClick(View v) {
				switch (advertisingPage.getBannerType()) {
					case 1:
						intent = new Intent(MainActivity.this, BookMarketBookDetailsActivity.class);
						intent.putExtra("bookId", advertisingPage.getId());
						intent.putExtra("bookName", advertisingPage.getTitle());
						startActivity(intent);
						break;
					case 2:
						/**
						 * 跳转到专题
						 */
						intent = new Intent(MainActivity.this, BookMarketBookListActivity.class);
						intent.putExtra("request", 3);
						intent.putExtra("topicId", Integer.toString(advertisingPage.getId()));
						intent.putExtra("imgurl", advertisingPage.getBannerUrl());
						intent.putExtra("name", advertisingPage.getTitle());
						intent.putExtra("bookIds", advertisingPage.getContent());
						startActivity(intent);
						break;
					case 3:
						intent = new Intent(MainActivity.this, DiscoverCircleDetailActivity.class);
						intent.putExtra(Constants.circleId, advertisingPage.getId());
						intent.putExtra(Constants.circleName, advertisingPage.getTitle());
						startActivity(intent);
						break;
					case 4:
						intent = new Intent(MainActivity.this, DiscoverReadingPartyDetailsActivity.class);
						intent.putExtra(Constants.readingPartyId, advertisingPage.getId());
						intent.putExtra(Constants.readingPartyTitle, advertisingPage.getTitle());
						startActivity(intent);
						break;
					case 5:
						intent = new Intent(MainActivity.this, BannerUrlAcitivty.class);
						intent.putExtra(Constants.URL, advertisingPage.getHtmlUrl());
						intent.putExtra(Constants.Title, advertisingPage.getTitle());
						startActivity(intent);
						break;
					case 6:
						break;
					case 7:
						intent = new Intent(context, DiscoverTopicActivity.class);
						intent.putExtra(Constants.topicId, advertisingPage.getId());
						intent.putExtra(Constants.TOPIC_ACITIVITY_TITLE, advertisingPage.getTitle());
						startActivity(intent);
						break;
					default:break;
				}
			}
		});

	}

	private void init(){
		bookIds = sharedPreferenceUtil.getString(SharedPreferenceUtil.NOT_UPLOAD_ORDER_BOOKS, null);
		price = sharedPreferenceUtil.getFloat(SharedPreferenceUtil.NOT_UPLOAD_ORDER_PRICE, 0);
		String priceStr = sharedPreferenceUtil.getString(SharedPreferenceUtil.NOT_UPLOAD_ORDER_PRICESTR, null);
		if (bookIds != null && price != 0) {
			saveAppPayResult(bookIds, price, priceStr);
		}
		initView();
		mInvoker = new InvokerDESServiceUitls(this);//启动构建加解密图书的服务
		mInvoker.invokerDESEncodeService(StartService);
		doPatch();
	}

	public void doPatch() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					BookDao tmpBookDao = new BookDao(DatabaseHelper.getHelper(context));
					List<ShelfBook> tmpBookList = tmpBookDao.queryByColumn("encryVersion", -1);
					if (tmpBookList != null && !tmpBookList.isEmpty()) {
						tmpBookDao.deleteByColumn("encryVersion", -1);
						BookInfoDao tmpBookInfoDao = new BookInfoDao(DatabaseHelper.getHelper(context));
						tmpBookInfoDao.deleteByColumn("encryVersion", -1);
						EpubBookInfoDao tmpEpubBookInfo = appContext.getEpubSession().getEpubBookInfoDao();
						tmpEpubBookInfo.deleteAll();
						FileHelper.deleteDirectory(CommonUtil.getBooksDir());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void buildDefaultBook(BuildBookCommand command) {
		showLoadingDialog();
		BuiltInBookUtil.createBuiltInBookInThread(appContext);//构建默认的内置书籍
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void epubProcessFinish(EpubProcessResult result) {
		dismissLoadingDialig();
		if(!result.isSuccessful()){
			ToastCompat.makeText(this,getString(R.string.unzip_failled), Toast.LENGTH_LONG).show();
		}
	}

	@Subscribe(threadMode = ThreadMode.BACKGROUND)
	public void epubDeleteBookEpubInfoRecorder(BookInfoEpubDeleteInfo info){
		EpubBookInfoDao tmpEpubBookInfoDao = appContext.getEpubSession().getEpubBookInfoDao();
		List<EpubBookInfo> tmpEpubBookInfos = tmpEpubBookInfoDao.queryBuilder().where(EpubBookInfoDao.Properties.BookId.eq(info.getBookId())).list();
		if(tmpEpubBookInfos != null && !tmpEpubBookInfos.isEmpty()){
			for(EpubBookInfo tmpEpubBookInfo:tmpEpubBookInfos){
				tmpEpubBookInfoDao.delete(tmpEpubBookInfo);
				//默认图书在删除的时候只会把本地数据库中记录epub解密情况的表中的EpubState字段置为删除状态，
				// 不会删除这条记录，
				// 表明用户是想删除小王子的，这样就app不会自动构建默认图书；
//				if(tmpEpubBookInfo.getBookId() == BuiltInBookUtil.DefualtBookId){
//					tmpEpubBookInfo.setEpubState(EpubState.Deleted);
//				}else{
//					tmpEpubBookInfoDao.delete(tmpEpubBookInfo);
//				}
			}
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
		if (mInvoker != null)
			mInvoker.unbindService();
		if (EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().unregister(this);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(CURR_INDEX, currIndex);
	}

	protected void initData(Bundle savedInstanceState) {
		fragmentTags = new ArrayList<>(Arrays.asList("BookMarketFragment", "BookShelfFragment", "DiscoverFragment", "MineFragment"));
		currIndex = 0;
		if (savedInstanceState != null) {
			currIndex = savedInstanceState.getInt(CURR_INDEX);
			hideSavedFragment();
		}
	}

	private void hideSavedFragment() {
		Fragment fragment1 = fragmentManager.findFragmentByTag(fragmentTags.get(currIndex));
		if (fragment1 != null) {
			fragmentManager.beginTransaction().hide(fragment1).commit();
		}
	}

	protected void initView() {
		footBookMarket.setOnClickListener(this);
		footBookShelf.setOnClickListener(this);
		footDiscover.setOnClickListener(this);
		mainFootbarMine.setOnClickListener(this);
		group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.foot_book_market:
						currIndex = 0;
						break;
					case R.id.foot_book_shelf:
						currIndex = 1;
						break;
					case R.id.foot_discover:
						currIndex = 2;
						break;
					case R.id.main_footbar_mine:
						currIndex = 3;
						break;
					default:
						break;
				}
				showFragment();
			}
		});
		showFragment();
	}

	public void showFragment() {
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragment = fragmentManager.findFragmentByTag(fragmentTags.get(currIndex));
		if (fragment == null) {
			fragment = instantFragment(currIndex);
		}
		for (int i = 0; i < fragmentTags.size(); i++) {
			Fragment f = fragmentManager.findFragmentByTag(fragmentTags.get(i));
			if (f != null && f.isAdded()) {
				fragmentTransaction.hide(f);
			}
		}
		if (fragment.isAdded()) {
			fragmentTransaction.show(fragment);
		} else {
			fragmentTransaction.add(R.id.fragment_container, fragment, fragmentTags.get(currIndex));
			fragmentTransaction.hide(fragment);
			fragmentTransaction.show(fragment);
		}
		setMainHeadView();
		fragmentTransaction.commitAllowingStateLoss();
		fragmentManager.executePendingTransactions();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//shao test
		Log.i("shao ","onResume--------=====================-"+currIndex);

        redirectionFragment();

        //shao testend
		if (exitLogin) {
			exitLogin=false;
			currIndex = 3;
			group.check(R.id.main_footbar_mine);
		}
		showFragment();



	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);// must store the new intent unless getIntent() will
		// return the old one
	}

	private  void redirectionFragment(){
		int mShowType=  sharedPreferenceUtil.getInt(SharedPreferenceUtil.SHOW_TYPE,0);
		int redirectionType=0;
		String bookId="";
		Bundle mBundle = this.getIntent().getExtras();
		String action=getIntent().getAction();
		if(mBundle!=null){
			if(mBundle.containsKey("redirectionType")) {
				redirectionType = mBundle.getInt("redirectionType");
				bookId = mBundle.getString("bookId");
				if (redirectionType == 0) {
					currIndex = 0;
					group.check(R.id.foot_book_market);
					BookMarketFragment fragment1 = (BookMarketFragment) fragmentManager.findFragmentByTag(fragmentTags.get(currIndex));
					fragment1.refreshFragment();

					if (!("".equals(bookId))) {
						if ("ToBookDetail".equals(action)) {
							Intent intent = new Intent(this, BookMarketBookDetailsActivity.class);
							intent.putExtra("bookId", Integer.parseInt(bookId));
							//intent.putExtra("bookName", book.bookName);
							getIntent().setAction("");
							startActivity(intent);
							//fragmentManager.beginTransaction().remove()
						}

					}
				} else if (redirectionType == 1) {
					currIndex = 1;
					group.check(R.id.foot_book_shelf);
				} else if (redirectionType == 2) {
					currIndex = 2;
					group.check(R.id.foot_discover);
				} else if (redirectionType == 3) {
					currIndex = 3;
					group.check(R.id.main_footbar_mine);
				}
			}
		}
		if(showType!=mShowType){
			currIndex=0;
			group.check(R.id.foot_book_market);
			BookMarketFragment fragment1=(BookMarketFragment)fragmentManager.findFragmentByTag(fragmentTags.get(currIndex));
			fragment1.refreshFragment();
			showType=mShowType;
		}
	}

	private Fragment instantFragment(int currIndex) {
		switch (currIndex) {
			case 0:
				return new BookMarketFragment();
			case 1:
				return new BookShelfFragment();
			case 2:
				return new DiscoverFragment();
			case 3:
				return new MineFragment();
			default:
				return null;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (fragment instanceof BookShelfFragment&&((BookShelfFragment) fragment).getShow()){
					((BookShelfFragment) fragment).onKeyDown();
					return false;
			} else if (exitBy2Click()){
				finish();
				return true;
			}
		}
		return false;
	}

	private boolean exitBy2Click() {
		Timer tExit;
		if (!isExit) {
			isExit = true; // 准备退出
			ToastCompat.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
			return false;
		} else {
			PIPManager.getInstance().stopFloatWindow();
			PIPManager.getInstance().reset();
			return true;
		}
	}

	protected void setMainHeadView() {
		switch (currIndex) {
			case 0:
				btnBack.setVisibility(View.INVISIBLE);
				searchBarHome.setVisibility(View.VISIBLE);
				llHeader.setVisibility(View.VISIBLE);
				btnFunction.setVisibility(View.VISIBLE);
				RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) btnFunction.getLayoutParams();
				lp.rightMargin = DisplayUtil.dip2px(context,15);
				btnFunction.setLayoutParams(lp);
				ibSign.setVisibility(View.VISIBLE);
				ibSign.setImageResource(R.drawable.icon_scan_white);

				//btnBack.setImageResource(R.drawable.ic_search);
				tvHeadTitle.setVisibility(View.INVISIBLE);//.setText("书城");
				btnFunction.setImageResource(R.drawable.ic_shopping);
				tvSearchTips.setText(getResources().getString(R.string.search_book));
				getGoodsNums();
				break;
			case 1:
				searchBarHome.setVisibility(View.GONE);
				llHeader.setVisibility(View.VISIBLE);
				btnFunction.setVisibility(View.VISIBLE);
				btnFunction.setImageResource(R.drawable.menu);
				lp = (RelativeLayout.LayoutParams) btnFunction.getLayoutParams();
				lp.rightMargin = DisplayUtil.dip2px(context,0);
				btnFunction.setLayoutParams(lp);
				ibSign.setVisibility(View.INVISIBLE);
				tvHeadTitle.setVisibility(View.VISIBLE);
				boolean isLocalShelf = sharedPreferenceUtil.getBoolean(SharedPreferenceUtil.CLOUD_OR_LOCAL,false);
				if(isLocalShelf){
					tvHeadTitle.setText("本地书架");
				}else{
					tvHeadTitle.setText("云书架");
				}
				tvGoodsNum.setVisibility(View.GONE);
//				btnBack.setVisibility(View.VISIBLE);
				btnBack.setVisibility(View.GONE);
				break;
			case 2:
				llHeader.setVisibility(View.VISIBLE);
				ibSign.setVisibility(View.VISIBLE);
				//btnBack.setImageDrawable(null);
				btnBack.setVisibility(View.INVISIBLE);
				//btnBack.setImageResource(R.drawable.ic_search);
				lp = (RelativeLayout.LayoutParams) btnFunction.getLayoutParams();
				lp.rightMargin = DisplayUtil.dip2px(context,0);
				btnFunction.setLayoutParams(lp);
				searchBarHome.setVisibility(View.VISIBLE);
				btnFunction.setImageDrawable(null);
				btnFunction.setVisibility(View.GONE);
				tvGoodsNum.setVisibility(View.GONE);
				tvHeadTitle.setVisibility(View.INVISIBLE);//setText("发现");
				tvSearchTips.setText(getResources().getString(R.string.search_circle));
				break;
			case 3:
				llHeader.setVisibility(View.GONE);
				tvGoodsNum.setVisibility(View.GONE);
				searchBarHome.setVisibility(View.GONE);
				break;
		}
	}

	public void saveAppPayResult(String bookIds, float price, String priceStr) {
		final HashMap<String, String> hashmap = new HashMap();
		hashmap.put("bookIds", bookIds);
		hashmap.put("totalPrice", price + "");
		hashmap.put("deviceType", DeviceType.ANDROID_3);
		hashmap.put("orderType", "0");
		hashmap.put("payResult", "1");
		hashmap.put("payType", "5");
		hashmap.put("userId", CommonUtil.getUserId() + "");
		hashmap.put("orderInfo", "重新提交");
		hashmap.put("priceString", priceStr);
		OkGo.get(Urls.SaveAppPayResultURL)
				.tag(this)
				.params(hashmap)
				.execute(new StringCallback() {
					@Override
					public void onSuccess(String s, Call call, Response response) {
						ToastCompat.makeText(context, "上传上次支付成功信息成功", Toast.LENGTH_SHORT).show();
						sharedPreferenceUtil.remove(SharedPreferenceUtil.NOT_UPLOAD_ORDER_BOOKS);
						sharedPreferenceUtil.remove(SharedPreferenceUtil.NOT_UPLOAD_ORDER_PRICE);
						sharedPreferenceUtil.remove(SharedPreferenceUtil.NOT_UPLOAD_ORDER_PRICESTR);
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						Logger.e("shibai");
					}
				});
	}

	private void autoUpgrade() {
		if (UIHelper.isNetAvailable(this)) {
			UpdateManager updateManager = UpdateManager.getUpdateManager();
			updateManager.checkAppUpdate(this, false,
					appContext.getDeviceToken(), false);
		}
	}

	public void getGoodsNums() {
		if (CommonUtil.getLoginState()) {

				OkGo.get(PersonShopcartURL)
						.tag(this)
						.params("userId", CommonUtil.getUserId())
						.execute(new JsonCallback<IycResponse<List<MineShoppingBookIntroduction>>>(this) {
							@Override
							public void onSuccess(IycResponse<List<MineShoppingBookIntroduction>> listIycResponse, Call call, Response response) {
								if (listIycResponse.getData() != null && listIycResponse.getData().size() > 0 && currIndex == 0) {
									tvGoodsNum.setVisibility(View.VISIBLE);
									tvGoodsNum.setText(listIycResponse.getData().size() + "");
								}
							}

							@Override
							public void onError(Call call, Response response, Exception e) {
								ToastCompat.makeText(MainActivity.this, R.string.net_error_tip, Toast.LENGTH_SHORT).show();
							}
						});
			}


	}

	@Override
	public void onClick(View view) {
		EventBus.getDefault().post(new SlideEvent(0));
	}

//    //假设输入的是一个byte[] inputeByteArr = new byte[2048];
//    private String test(byte[] inputByteArr){
//        return new String(inputByteArr);
//    }
//    //假设输入的是一个byte[] inputeByteArr = new byte[2048];
//    private String test2(byte[] inputByteArr){
//        ByteArrayInputStream tmpInputStream = new ByteArrayInputStream(inputByteArr);
//        StringBuffer stringBuffer = new StringBuffer();
//        byte[] buffer = new byte[128];
//        while (tmpInputStream.read(buffer,0,128) >= 128){
//            stringBuffer.append(new String(buffer));
//        }
//        tmpInputStream.read(buffer,0,128);
//        stringBuffer.append(buffer);
//        return stringBuffer.toString();
//    }


/*public static	 class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
//			intent.getAction().equals("com.iyangcong.reader.loginactivity.statechange"){
//				redirectionFragment();
//			}
			Toast.makeText(context,"收到广播", Toast.LENGTH_SHORT).show();
		}
	}*/


}
