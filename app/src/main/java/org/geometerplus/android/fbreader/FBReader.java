/*
 * Copyright (C) 2009-2015 FBReader.ORG Limited <contact@fbreader.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.breatheview.BreatheView;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.iyangcong.reader.R;
import com.iyangcong.reader.ReadingTask.DataCollectTask;
import com.iyangcong.reader.ReadingTask.RescusiveTask;
import com.iyangcong.reader.ReadingTask.SingleTimer;
import com.iyangcong.reader.ReadingTask.StartEvent;
import com.iyangcong.reader.ReadingTask.TaskWithDelay;
import com.iyangcong.reader.ReadingTask.TaskWithoutDelay;
import com.iyangcong.reader.activity.BookMarketBookDetailsActivity;
import com.iyangcong.reader.activity.BookMarketSearchActivity;
import com.iyangcong.reader.activity.CommentEditActivity;
import com.iyangcong.reader.activity.LoginActivity;
import com.iyangcong.reader.adapter.ContentListAdapter;
import com.iyangcong.reader.bean.BaseBook;
import com.iyangcong.reader.bean.BookInfo;
import com.iyangcong.reader.bean.BookMarker;
import com.iyangcong.reader.bean.BookNote;
import com.iyangcong.reader.bean.BookNoteBean;
import com.iyangcong.reader.bean.BookType;
import com.iyangcong.reader.bean.CommentCount;
import com.iyangcong.reader.bean.PublicComment;
import com.iyangcong.reader.bean.ReadingDataBean;
import com.iyangcong.reader.bean.ReadingRecorder;
import com.iyangcong.reader.bean.ShelfBook;
import com.iyangcong.reader.broadcast.ScreenListener;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.BookDao;
import com.iyangcong.reader.database.dao.BookInfoDao;
import com.iyangcong.reader.database.dao.MarkerDao;
import com.iyangcong.reader.database.dao.NoteDao;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.service.PostReadingRecorderService;
import com.iyangcong.reader.ui.TagGroup;
import com.iyangcong.reader.utils.BookInfoUtils;
import com.iyangcong.reader.utils.ClickUtils;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.DialogUtils;
import com.iyangcong.reader.utils.IntentUtils;
import com.iyangcong.reader.utils.NotNullUtils;
import com.iyangcong.reader.utils.ShareUtils;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.iyangcong.reader.utils.floatVideoWindow.PIPManager;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import org.geometerplus.android.fbreader.api.ApiListener;
import org.geometerplus.android.fbreader.api.ApiServerImplementation;
import org.geometerplus.android.fbreader.api.FBReaderIntents;
import org.geometerplus.android.fbreader.api.MenuNode;
import org.geometerplus.android.fbreader.api.PluginApi;
import org.geometerplus.android.fbreader.dict.DictionaryUtil;
import org.geometerplus.android.fbreader.formatPlugin.PluginUtil;
import org.geometerplus.android.fbreader.httpd.DataService;
import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.android.fbreader.libraryService.SQLiteBooksDatabase;
import org.geometerplus.android.fbreader.sync.SyncOperations;
import org.geometerplus.android.fbreader.tips.TipsActivity;
import org.geometerplus.android.util.DeviceType;
import org.geometerplus.android.util.SearchDialogUtil;
import org.geometerplus.android.util.UIMessageUtil;
import org.geometerplus.android.util.UIUtil;
import org.geometerplus.fbreader.Paths;
import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.book.BookUtil;
import org.geometerplus.fbreader.book.Bookmark;
import org.geometerplus.fbreader.book.BookmarkQuery;
import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.DictionaryHighlighting;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.FBView;
import org.geometerplus.fbreader.fbreader.options.CancelMenuHelper;
import org.geometerplus.fbreader.fbreader.options.ColorProfile;
import org.geometerplus.fbreader.formats.ExternalFormatPlugin;
import org.geometerplus.fbreader.formats.PluginCollection;
import org.geometerplus.fbreader.tips.TipsManager;
import org.geometerplus.fbreader.util.EmptyTextSnippet;
import org.geometerplus.zlibrary.core.application.ZLApplicationWindow;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.library.ZLibrary;
import org.geometerplus.zlibrary.core.options.Config;
import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.core.view.ZLViewEnums;
import org.geometerplus.zlibrary.core.view.ZLViewWidget;
import org.geometerplus.zlibrary.text.view.ZLTextFixedPosition;
import org.geometerplus.zlibrary.text.view.ZLTextRegion;
import org.geometerplus.zlibrary.text.view.ZLTextView;
import org.geometerplus.zlibrary.text.view.ZLTextWordCursor;
import org.geometerplus.zlibrary.ui.android.error.ErrorKeys;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidApplication;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidLibrary;
import org.geometerplus.zlibrary.ui.android.view.AndroidFontUtil;
import org.geometerplus.zlibrary.ui.android.view.ZLAndroidWidget;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;
import static com.iyangcong.reader.utils.ShareUtils.BOOK_SHARE;
import static com.iyangcong.reader.utils.ShareUtils.CONTENT_KEY;
import static com.iyangcong.reader.utils.ShareUtils.URLS_KEY;

public final class FBReader extends FBReaderMainActivity implements ZLApplicationWindow, View.OnClickListener ,SeekBar.OnSeekBarChangeListener {
	public static final int RESULT_DO_NOTHING = RESULT_FIRST_USER;
	public static final int RESULT_REPAINT = RESULT_FIRST_USER + 1;
	private static final String PLUGIN_ACTION_PREFIX = "___";
	final DataService.Connection DataConnection = new DataService.Connection();
	private final FBReaderApp.Notifier myNotifier = new AppNotifier(this);
	private final List<PluginApi.ActionInfo> myPluginActions = new LinkedList<PluginApi.ActionInfo>();
	private List<PublicComment> publicCommentList = new ArrayList<>();
	private final HashMap<MenuItem, String> myMenuItemMap = new HashMap<MenuItem, String>();
	public int semesterId = -1;
	private GestureDetector gestureDetector;
	//    public List<Bookmark> downloadMarkList = new ArrayList<>();

	//	private boolean isRecordBegin = false;
//	private Date beginTime;
//	private Handler handler = new Handler();
//	private Runnable orbitRunnable = new Runnable() {
//		@Override
//		public void run() {
//			if (isRecordBegin) {
////				orbitRecordEnd();
//				buildDelayTask();
//			}
//			executeOrbitBeginTask();
//			isRecordBegin = true;
//			handler.postDelayed(this, 1000 * 60 * 10);
//		}
//	};
	public boolean isHomeClicked = false;
	@BindView(R.id.brvleft)
	BreatheView brvleft;
	@BindView(R.id.brvcenter)
	BreatheView brvcenter;
	@BindView(R.id.brvright)
	BreatheView brvright;
	@BindView(R.id.brvtop)
	BreatheView brvtop;
	@BindView(R.id.bt_closereadertip)
	TagGroup btnCloseReadertip;
	@BindView(R.id.btn_writereview)
	Button btnWritereview;
	@BindView(R.id.btn_gotobookcity)
	Button btnGotobookcity;
	@BindView(R.id.btn_gotobookshelf)
	Button btnGotobookshelf;
	@BindView(R.id.btn_gotobuy)
	Button btnGotobuy;
	volatile boolean IsPaused = false;
	volatile Runnable OnResumeAction = null;
	private FBReaderApp myFBReaderApp;
	private final BroadcastReceiver myPluginInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final ArrayList<PluginApi.ActionInfo> actions = getResultExtras(true).<PluginApi.ActionInfo>getParcelableArrayList(PluginApi.PluginInfo.KEY);
			if (actions != null) {
				synchronized (myPluginActions) {
					int index = 0;
					while (index < myPluginActions.size()) {
						myFBReaderApp.removeAction(PLUGIN_ACTION_PREFIX + index++);
					}
					myPluginActions.addAll(actions);
					index = 0;
					for (PluginApi.ActionInfo info : myPluginActions) {
						myFBReaderApp.addAction(
								PLUGIN_ACTION_PREFIX + index++,
								new RunPluginAction(FBReader.this, myFBReaderApp, info.getId())
						);
					}
				}
			}
		}
	};
	private final MenuItem.OnMenuItemClickListener myMenuListener =
			new MenuItem.OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					myFBReaderApp.runAction(myMenuItemMap.get(item));
					return true;
				}
			};

	public List<BookNoteBean> getListData() {
		return listData;
	}

	public void setListData(List<BookNoteBean> listData) {
		this.listData = listData;
	}

	private List<BookNoteBean> listData;
	private volatile Book myBook;
	private RelativeLayout myRootView;
	private ZLAndroidWidget myMainView;
//	private ViewPager mViewPager ;
//	private TestAdapter mAdapter;
	private LinearLayout llReadEnd = null;
	private FrameLayout flReadHelp = null;
	private TextView tvTips = null;
	private volatile boolean myShowStatusBarFlag;
	private String myMenuLanguage;
	private volatile long myResumeTimestamp;
	private Intent myCancelIntent = null;
	private Intent myOpenBookIntent = null;
	private SharedPreferenceUtil sharePreferenceUtil;
	private BookDao bookDao;
	private BookInfoDao bookInfoDao;
	private ShelfBook shelfBook;
	private PopupWindow mPopupWindow;
	private CheckBox rbMenuChangeZn;
	private LinearLayout llReaderSettingWindow;
	private RadioGroup rgMenu;
	/**
	 * 字号
	 */
	private RadioGroup rgWordSize;
	private RadioGroup rgTurnPage;
	private RadioGroup rgBackgroundColor;
	private SeekBar light_seekBar;
	private int curSeek = 0; // 当前seekBar位置
	private CheckBox cbDayAndNight;
	private RelativeLayout rlNavigation;
	private LinearLayout llMoreSetting;
	private int count ;
	private RelativeLayout rlMenuTop;
	private TextView commentCount;
	private LinearLayout llMenuBottom;
	private LinearLayout llDayNightMode;
	private LinearLayout wordSizeMode;
	private LinearLayout turnPageMode;
	private LinearLayout backgroundColorMode;
	private PowerManager.WakeLock myWakeLock;
	private boolean myWakeLockToCreate;
	private int num = 0;
	private ContentListAdapter commentAdapter;
	private boolean myStartTimer;
	private int myBatteryLevel;
	private BroadcastReceiver myBatteryInfoReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			final int level = intent.getIntExtra("level", 100);
			final ZLAndroidApplication application = (ZLAndroidApplication) getApplication();
			setBatteryLevel(level);
			switchWakeLock(
					hasWindowFocus() &&
							getZLibrary().BatteryLevelToTurnScreenOffOption.getValue() < level
			);
		}
	};
	private BroadcastReceiver mySyncUpdateReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			myFBReaderApp.useSyncInfo(myResumeTimestamp + 10 * 1000 > System.currentTimeMillis(), myNotifier);
		}
	};
	private int downLoadNoteCount = 0;
	private boolean noteHasDown = false;
	private boolean bookMarkerHasDown = false;

	public static void openBookActivity(Context context, Book book, Bookmark bookmark) {
		final Intent intent = defaultIntent(context);
		FBReaderIntents.putBookExtra(intent, book);
		FBReaderIntents.putBookmarkExtra(intent, bookmark);
		context.startActivity(intent);
	}

	public static Intent defaultIntent(Context context) {
		return new Intent(context, FBReader.class)
				.setAction(FBReaderIntents.Action.VIEW)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
								  boolean fromUser) {
		    count = progress;
			if (count == 0) {
				count++;
			}
			Integer tmpInt = seekBar.getProgress();
			curSeek = tmpInt;
			setScreenLight(tmpInt);
			SharedPreferenceUtil.getInstance().putInt(SharedPreferenceUtil.CURRENT_LIGHT_VALUE, tmpInt);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	@OnClick({R.id.btn_writereview, R.id.btn_gotobookcity, R.id.btn_gotobookshelf, R.id.btn_gotobuy})
	public void onLastPageClick(View view) {
		switch (view.getId()) {
			case R.id.btn_writereview:
				if (CommonUtil.getLoginState()) {
					saveBookInfoBeforeBack();
					Intent intent = new Intent(this, CommentEditActivity.class);
					intent.putExtra("bookId", (int) shelfBook.getBookId());
					startActivity(intent);
				} else {
					Intent intent = new Intent(this, LoginActivity.class);
					startActivity(intent);
				}
				break;
			case R.id.btn_gotobookcity:
				break;
			case R.id.btn_gotobookshelf:
				showDialog("是否退出阅读?", 2);
				break;
			case R.id.btn_gotobuy:
				if (CommonUtil.getLoginState()) {
					if (shelfBook.getBookPrice() > 0) {
						IntentUtils.goToPayActivity(this, shelfBook.getBookId() + "", shelfBook.getBookPrice() + "", shelfBook.getBookPrice(), 1);
//                        BigDecimal bigDecimal = new BigDecimal(shelfBook.getBookPrice());
//                        Intent intent = new Intent(this, MinePayActivity.class);
//                        intent.putExtra("bookIds", shelfBook.getBookId() + "");
//                        intent.putExtra("totalPrice", bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
//                        intent.putExtra("count", 1);
//                        startActivity(intent);
						finish();
					} else {
						Intent intent = new Intent(FBReader.this, BookMarketBookDetailsActivity.class);
						intent.putExtra("bookId", (int) shelfBook.getBookId());
						intent.putExtra("bookName", shelfBook.getBookName());
						startActivity(intent);
					}
				} else {
					showDialog("购买书籍请先登录", 1);
					ToastCompat.makeText(this, "购买书籍请先登录", Toast.LENGTH_SHORT).show();
				}
				break;
		}
		llReadEnd.setVisibility(View.GONE);
	}

	/**
	 * 退出前保存书籍信息
	 */
	private void saveBookInfoBeforeBack() {
		if (bookDao != null) {
			List<ShelfBook> shelfBookList = bookDao.queryByColumn("bookId", sharePreferenceUtil.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0));
			if (shelfBookList.size() > 0) {
				ShelfBook shelfBook = shelfBookList.get(0);
				ZLTextView.PagePosition pagePosition = myFBReaderApp.getTextView().pagePosition();
				float progress = (float) pagePosition.Current / pagePosition.Total * 100;
				BigDecimal b = new BigDecimal(progress);
				progress = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
				if (progress > 0) {
//                    shelfBook.setBookState("已读" + progress + "%");
					shelfBook.setBookState("已读");
				}
				shelfBook.setTimeStamp(new Date().getTime());
				shelfBook.setChapterId(BookInfoUtils.getRelativeChapterId(myFBReaderApp, this) + "");
				shelfBook.setDownloadProgress(progress);
				shelfBook.setRecentReadingLanguageType(sharePreferenceUtil.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 0));
				bookDao.update(shelfBook);
			}
		}

	}

	/**
	 * @param context
	 * @param dialogType 1:购买书籍登录提示弹框    2:退出阅读器提示框
	 */
	public void showDialog(String context, final int dialogType) {

		final NormalDialog normalDialog = new NormalDialog(this);
		DialogUtils.setAlertDialogNormalStyle(normalDialog, getResources().getString(R.string.tips), context);
		normalDialog.setOnBtnClickL(new OnBtnClickL() {
			@Override
			public void onBtnClick() {
				normalDialog.dismiss();
				switch (dialogType) {
					case 1:
						startActivity(new Intent(FBReader.this, LoginActivity.class));
						break;
					case 2:
						if (mPopupWindow != null && mPopupWindow.isShowing()) {
							mPopupWindow.dismiss();
						}
						PIPManager.getInstance().stopFloatWindow();
						PIPManager.getInstance().reset();
						saveBookInfoBeforeBack();
						executePostReadingRecordTask();
						runExitAction();
						break;
				}
			}
		}, new OnBtnClickL() {
			@Override
			public void onBtnClick() {
				normalDialog.dismiss();
			}
		});
	}

	/**
	 * author:WuZepeng </br>
	 * time:2018-05-28 14:10 </br>
	 * desc:执行开始记录了阅读轨迹的任务 </br>
	 */
	private void executeOrbitBeginTask() {
		int chapterId = BookInfoUtils.getAbsChapterId(myFBReaderApp, this);
		Logger.e("wzp orbitRecordBegin chapterId:%d%n", chapterId);
		if (chapterId < 0)
			return;
		String paragraphText = BookInfoUtils.getParagraphTextByBookmark(myFBReaderApp);
		;
		if (paragraphText == null) return;
		if ("".equals(paragraphText))
			return;
//		final int relativeParagraphId = BookInfoUtils.getTextRelativeParagraphIndex(myFBReaderApp);
		final int paragraphId = BookInfoUtils.getAbsoluteParagraphId(myFBReaderApp);
		Logger.e("阅读轨迹开始接口 段落内容：%s paragraphId：%d", paragraphText, paragraphId);
		if (paragraphId < 0) {
			return;
		}
//		sharePreferenceUtil.putString(SharedPreferenceUtil.READING_RECORD_LAST_TIME, DateUtils.getSystemDateFormat("yyyy-MM-dd HH:mm:ss"));
		ReadingRecorder tmpRecorder = new ReadingRecorder();
		tmpRecorder.setBookId((int) sharePreferenceUtil.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0));
		tmpRecorder.setLanguageType(sharePreferenceUtil.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 0));
		tmpRecorder.setSegmentId(paragraphId);
		tmpRecorder.setSegmentStr(paragraphText);
		tmpRecorder.setChapterId(chapterId);
		tmpRecorder.setSemesterId(semesterId);
		SingleTimer.getInstance().executeRightNow(new TaskWithoutDelay(this, tmpRecorder));
	}

	/**
	 * author:WuZepeng </br>
	 * time:2018-05-28 14:10 </br>
	 * desc:执行开始结束记录阅读轨迹的任务
	 * 1、退出阅读器
	 * 2、屏幕熄灭
	 * 3、切换语言
	 * 4、点击Home键</br>
	 */
	private void executePostReadingRecordTask() {
//		DataCollectTask<?> task = SingleTimer.getInstance().getTask();
//		if(task instanceof TaskWithDelay){
//			ReadingRecorder tmpRecorder = ((TaskWithDelay) task).getRecorder();
//			if(tmpRecorder != null){
//				Intent tmpIntent = new Intent(this, PostReadingRecorderService.class);
//				tmpIntent.putExtra(Constants.READING_RECORDER,tmpRecorder);
//				tmpIntent.putExtra(Constants.TASK_TYPE,PostReadingRecorderService.ServiceType.ONLY_READING_RECORD);
//				context.startService(tmpIntent);
//			}
//		}
//		if (!SingleTimer.getInstance().hasExecuteTask()) {
//			SingleTimer.getInstance().delayExecute(SingleTimer.NOW);
//		}
		SingleTimer.getInstance().stopeRescusiveTask();
		SingleTimer.canRecord = true;
		DataCollectTask<?> task = SingleTimer.getInstance().getTask();
		if(task instanceof RescusiveTask && ((RescusiveTask) task).getBean()!= null)
			SingleTimer.getInstance().executeEndTask(context,myFBReaderApp,((RescusiveTask) task).getBean());

	}


	 //构建定时任务(1、打开阅读器时  2、切换语言时   3、在阅读器被切到后台，又切回来的时候)
	private void buildRescusiveTask(){
		if (!CommonUtil.getLoginState()) {
			return;
		}
		SingleTimer.canRecord = false;
		final int paragrahpId = BookInfoUtils.getAbsoluteParagraphId(myFBReaderApp);
		SingleTimer.startTime = System.currentTimeMillis();
		ReadingDataBean bean = new ReadingDataBean();
		bean.setUserId(CommonUtil.getUserId());
		bean.setBookId(sharePreferenceUtil.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID,-1));
		bean.setStartParagraphId(paragrahpId);
		SingleTimer.getInstance().executeRescusively(this,myFBReaderApp,bean,SingleTimer.TEN_MINUTES,SingleTimer.TEN_MINUTES);
	}

	public ReadingRecorder buildReadingRecord(){
		if (sharePreferenceUtil == null) {
			sharePreferenceUtil = SharedPreferenceUtil.getInstance();
		}
		long bookId = sharePreferenceUtil.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0);
		List<ShelfBook> shelfBookList = bookDao.queryByColumn("bookId", bookId);
		if (shelfBookList.size() == 0) {
			return null;
		}
		int chapterId = BookInfoUtils.getRelativeChapterId(myFBReaderApp, this);
		final int paragrahpId = BookInfoUtils.getAbsoluteParagraphId(myFBReaderApp);
		int languageType = shelfBookList.get(0).getRecentReadingLanguageType();
		ReadingRecorder readingRecorder = new ReadingRecorder();
		readingRecorder.setBookId((int)bookId);
		readingRecorder.setChapterId(chapterId);
		readingRecorder.setLanguageType(languageType);
		readingRecorder.setPercent(shelfBookList.get(0).getDownloadProgress() / 100);
		readingRecorder.setSegmentId(paragrahpId);
		String paragraphText = BookInfoUtils.getParagraphTextByBookmark(myFBReaderApp);
		readingRecorder.setSegmentStr(paragraphText);
		readingRecorder.setSemesterId(semesterId);
		Logger.e("阅读轨迹结束接口调用 paragraphText:%s paragrahpId:%d", paragraphText, paragrahpId);
		return readingRecorder;
	}
	/**
	 * author:WuZepeng </br>
	 * time:2018-05-28 14:11 </br>
	 * desc:构建延时任务，假如在延时时间段内没有操作，那么，在没有有效UI触摸时间的前提下退出时，不会有阅读轨迹记录 </br>
	 */
	private void buildDelayTask() {
		if (!CommonUtil.getLoginState()) {
//			runExitAction();
			return;
		}
		ReadingRecorder readingRecorder = buildReadingRecord();
		if(readingRecorder != null)
			SingleTimer.getInstance().execute(new TaskWithDelay(this, readingRecorder), SingleTimer.TEN_MINUTES);
	}

	//调用接口上传阅读记录
	private void startPoserReadingRecorderService(ReadingRecorder readingRecorder,byte type){
		Intent intent = new Intent(this, PostReadingRecorderService.class);
		Bundle bundle = new Bundle();
		if(readingRecorder != null)
			bundle.putSerializable(Constants.READING_RECORDER, readingRecorder);
		bundle.putSerializable(Constants.TASK_TYPE,type);
		intent.putExtras(bundle);
		startService(intent);
	}

	private void initBreathButton(BreatheView btn){
		btn.setInterval(2000) //设置闪烁间隔时间
				.setCoreRadius(30f)//设置中心圆半径
				.setDiffusMaxWidth(40f)//设置闪烁圆的最大半径
				.setDiffusColor(Color.parseColor("#fbc9b6"))//设置闪烁圆的颜色
				.setCoreColor(Color.parseColor("#ff8756"))//设置中心圆的颜色
				.onStart();
	}

	private void runExitAction() {
		myFBReaderApp.runAction(ActionCode.EXIT);
	}
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		if(!EventBus.getDefault().isRegistered(this)){
			EventBus.getDefault().register(this);
		}
		sharePreferenceUtil = SharedPreferenceUtil.getInstance();

		curSeek = sharePreferenceUtil.getInt(SharedPreferenceUtil.CURRENT_LIGHT_VALUE, 80);
		bindService(
				new Intent(this, DataService.class),
				DataConnection,
				DataService.BIND_AUTO_CREATE
		);

		final Config config = Config.Instance();
		config.runOnConnect(new Runnable() {
			public void run() {
				config.requestAllValuesForGroup("Options");
				config.requestAllValuesForGroup("Style");
				config.requestAllValuesForGroup("LookNFeel");
				config.requestAllValuesForGroup("Fonts");
				config.requestAllValuesForGroup("Colors");
				config.requestAllValuesForGroup("Files");
			}
		});

		final ZLAndroidLibrary zlibrary = getZLibrary();
		myShowStatusBarFlag = zlibrary.ShowStatusBarOption.getValue();
		setContentView(R.layout.main);
		ButterKnife.bind(this);
		myRootView = (RelativeLayout) findViewById(R.id.root_view);
		myMainView = (ZLAndroidWidget) findViewById(R.id.main_view);
		tvTips = (TextView) findViewById(R.id.tv_tips);

		//到阅读器最后一页给提示
		flReadHelp = (FrameLayout) findViewById(R.id.ll_read_help);
		llReadEnd = (LinearLayout) findViewById(R.id.ll_read_end);
		llReadEnd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				llReadEnd.setVisibility(View.GONE);
				llReadEnd.invalidate();
			}
		});
		//第一次进阅读器给提示

		//mViewPager = findViewById(R.id.layout_read_help);

		if (isFirstRead()) {
			initBreathButton(brvtop);
			initBreathButton(brvcenter);
			initBreathButton(brvleft);
			initBreathButton(brvright);
			String[] tags = new String[]{"我知道了"};
			btnCloseReadertip.setTags(tags);
			btnCloseReadertip.setOnTagClickListener(new TagGroup.OnTagClickListener() {
				@Override
				public void onTagClick(String tag) {
					flReadHelp.setVisibility(View.GONE);
				}
			});
			flReadHelp.setVisibility(View.VISIBLE);
			//mViewPager.setVisibility(View.VISIBLE);
			//mAdapter = new TestAdapter(this);
			//mViewPager.setAdapter(mAdapter);

//			mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//				boolean isPress ;
//				boolean isOpen ;
//				@Override
//				public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//					if ( 1 == position && isPress && positionOffsetPixels == 0 && !isOpen) {
//						isOpen = true;
//						mViewPager.setVisibility(View.GONE);
//					}
//
//				}
//				@Override
//				public void onPageSelected(int position) {
//				}
//
//				@Override
//				public void onPageScrollStateChanged(int state) {
//					if (state == ViewPager.SCROLL_STATE_DRAGGING) {
//						isPress = true;
//					} else {//必须写else，不然的话，倒数第二页就开始自动跳转了
//						isPress = false;
//					}
//				}
//			});
		}

		setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

		myFBReaderApp = (FBReaderApp) FBReaderApp.Instance();
		if (myFBReaderApp == null) {
			myFBReaderApp = new FBReaderApp(Paths.systemInfo(this), new BookCollectionShadow(),this);
		}
		getCollection().bindToService(this, null);
		myBook = null;

		myFBReaderApp.setWindow(this);
		myFBReaderApp.initWindow();

		myFBReaderApp.setExternalFileOpener(new ExternalFileOpener(this));

		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				myShowStatusBarFlag ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN
		);

		if (myFBReaderApp.getPopupById(TextSearchPopup.ID) == null) {
			new TextSearchPopup(myFBReaderApp);
		}
		if (myFBReaderApp.getPopupById(NavigationPopup.ID) == null) {
			new NavigationPopup(myFBReaderApp);
		}
		if (myFBReaderApp.getPopupById(SelectionPopup.ID) == null) {
			new SelectionPopup(myFBReaderApp);
		}

		myFBReaderApp.addAction(ActionCode.SHOW_LIBRARY, new ShowLibraryAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SHOW_PREFERENCES, new ShowPreferencesAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SHOW_BOOK_INFO, new ShowBookInfoAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SHOW_TOC, new ShowTOCAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SHOW_BOOKMARKS, new ShowBookmarksAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.ADD_BOOKMARKERS, new AddBookMarkersAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SHOW_NETWORK_LIBRARY, new ShowNetworkLibraryAction(this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.SHOW_MENU, new ShowMenuAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SHOW_NAVIGATION, new ShowNavigationAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SEARCH, new SearchAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SHARE_BOOK, new ShareBookAction(this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.SELECTION_SHOW_PANEL, new SelectionShowPanelAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SELECTION_HIDE_PANEL, new SelectionHidePanelAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SELECTION_COPY_TO_CLIPBOARD, new SelectionCopyAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SELECTION_SHARE, new SelectionShareAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SELECTION_TRANSLATE, new SelectionTranslateAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SELECTION_BOOKMARK, new NoteBookAction(this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.DISPLAY_BOOK_POPUP, new DisplayBookPopupAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.PROCESS_HYPERLINK, new ProcessHyperlinkAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.OPEN_VIDEO, new OpenVideoAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.HIDE_TOAST, new HideToastAction(this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.SHOW_CANCEL_MENU, new ShowCancelMenuAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.OPEN_START_SCREEN, new StartScreenAction(this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_SYSTEM, new SetScreenOrientationAction(this, myFBReaderApp, ZLibrary.SCREEN_ORIENTATION_SYSTEM));
		myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_SENSOR, new SetScreenOrientationAction(this, myFBReaderApp, ZLibrary.SCREEN_ORIENTATION_SENSOR));
		myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_PORTRAIT, new SetScreenOrientationAction(this, myFBReaderApp, ZLibrary.SCREEN_ORIENTATION_PORTRAIT));
		myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_LANDSCAPE, new SetScreenOrientationAction(this, myFBReaderApp, ZLibrary.SCREEN_ORIENTATION_LANDSCAPE));
		if (getZLibrary().supportsAllOrientations()) {
			myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_REVERSE_PORTRAIT, new SetScreenOrientationAction(this, myFBReaderApp, ZLibrary.SCREEN_ORIENTATION_REVERSE_PORTRAIT));
			myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_REVERSE_LANDSCAPE, new SetScreenOrientationAction(this, myFBReaderApp, ZLibrary.SCREEN_ORIENTATION_REVERSE_LANDSCAPE));
		}
		myFBReaderApp.addAction(ActionCode.OPEN_WEB_HELP, new OpenWebHelpAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.INSTALL_PLUGINS, new InstallPluginsAction(this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.SWITCH_TO_DAY_PROFILE, new SwitchProfileAction(this, myFBReaderApp, ColorProfile.DAY));
		myFBReaderApp.addAction(ActionCode.SWITCH_TO_NIGHT_PROFILE, new SwitchProfileAction(this, myFBReaderApp, ColorProfile.NIGHT));

		myFBReaderApp.addAction(ActionCode.UPDATE_BACKGROUND_COLOR, new UpdateBackGroundColorAction(this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.CHANGE_BOOK_LANGUAGE, new ChangeLanguageAction(this, myFBReaderApp));


		bookDao = new BookDao(DatabaseHelper.getHelper(this));
		bookInfoDao = new BookInfoDao(DatabaseHelper.getHelper(this));
		final List<ShelfBook> shelfBookList = bookDao.queryByColumn("bookId", sharePreferenceUtil.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0));
		if (shelfBookList != null && shelfBookList.size() > 0) {
			shelfBook = shelfBookList.get(0);
			shelfBook.setTimeStamp(System.currentTimeMillis());
			bookDao.update(shelfBook);
			sharePreferenceUtil.putBoolean(SharedPreferenceUtil.CAN_CHANGE_LANGUAGE, shelfBook.getSupportLanguage() == 3 ? true : false);
		}

		final Intent intent = getIntent();
		final String action = intent.getAction();

		//ljw
		final Book book = FBReaderIntents.getBookExtra(intent, myFBReaderApp.Collection);
//        myFBReaderApp.ExternalBook = null;
		final String bookPath = intent.getStringExtra("bookPath");
		final int paragraphId = intent.getIntExtra(Constants.PARAGRAHP_ID, -1);
		final int chapterId = intent.getIntExtra(Constants.CHAPTERID, -1);
		final String segmentText = intent.getStringExtra(Constants.PARAGRAPH_TEXT);
		getCollection().bindToService(this, new Runnable() {
			public void run() {
				final BookCollectionShadow collection = getCollection();
				Book b = collection.getBookByFile(bookPath);
				final List<BookInfo> bookInfo = bookInfoDao.queryByColumn("bookId", sharePreferenceUtil.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0));
				if (sharePreferenceUtil.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0) == sharePreferenceUtil.getLong(SharedPreferenceUtil.CURRENT_BUYED_BOOK_ID, -1)) {
					sharePreferenceUtil.putLong(SharedPreferenceUtil.CURRENT_BUYED_BOOK_ID, -1);
				} else {
					//读取书籍缓存
					if (collection.sameBook(b, book)) {
						b = collection.getRecentBook(1);
					}
				}
				myFBReaderApp.openBook(b, null, new Runnable() {
					@Override
					public void run() {
						if (CommonUtil.getLoginState()) {
							startPoserReadingRecorderService(null, PostReadingRecorderService.ServiceType.ONLY_READING_ORBIT_RECORD);
							downloadAllNote();
							semesterId = sharePreferenceUtil.getInt(SharedPreferenceUtil.SEMESTER_ID, -1);
							if (semesterId > 0) {

								buildRescusiveTask();//刚开打阅读器的时候，构建一个定时循环任务；
//								executeOrbitBeginTask();//进入阅读器打开书的时候，提交一次开始阅读；
//								buildDelayTask();//构建一个延时的任务；
//								beginTime = new Date(System.currentTimeMillis());
							}
						}
					}
				}, myNotifier);
//				setScreenLight(sharePreferenceUtil.getInt(SharedPreferenceUtil.CURRENT_LIGHT_VALUE, 80));

				final Book finalB = b;
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if (paragraphId != -1 && bookInfo.size() > 0 && chapterId != -1 && !segmentText.equals("")) {
							int nativeParagraphIndex = BookInfoUtils.getLocaParagraphIndex(myFBReaderApp, paragraphId);
							boolean hasFind = nativeParagraphIndex >= 0;
//							actualParagrahpId -= 5;
//							for (int i = 0; i < 250; i++) {
//								if (actualParagrahpId < 0) {
//									actualParagrahpId = 0;
//								}
//								String pagraphText = BookInfoUtils.getParagraphTextByIndex(myFBReaderApp, actualParagrahpId, false);
//								pagraphText = pagraphText.replaceAll("[，。；！？：“”~《》]", "");
//								String tempSegmentText = segmentText.replaceAll("[ ，。；！？：“”~《》]", "");
//								if (pagraphText.contains(tempSegmentText)) {
//									hasFind = true;
//									break;
//								} else {
//									actualParagrahpId++;
//								}
//							}
							if (hasFind) {
								myFBReaderApp.savePosition(finalB, nativeParagraphIndex);
								myFBReaderApp.getTextView().gotoPosition(nativeParagraphIndex, 0, 0);
								myFBReaderApp.getViewWidget().reset();
								myFBReaderApp.getViewWidget().repaint();
							}
						}
					}
				}, 1000);


			}
		});
		initMenuSetting();

		ScreenListener.getInstance(this).register(mScreenStateListener);
		ScreenListener.getInstance(this).register(mSystemEvent);
	}

	private boolean isSreenChanged = false;
	ScreenListener.ScreenStateListener mScreenStateListener = new ScreenListener.ScreenStateListener() {
		private boolean isSreenOn = true;
		@Override
		public void onScreenOn() {
			if(!isSreenOn){
				isSreenChanged = true;
				isSreenOn = true;
			}
		}

		@Override
		public void onScreenOff() {
			if(isSreenOn){
				isSreenChanged = true;
				isSreenOn = false;
				executePostReadingRecordTask();
				ReadingRecorder tmpRecorder = buildReadingRecord();
				if(tmpRecorder!=null){
					startPoserReadingRecorderService(tmpRecorder,PostReadingRecorderService.ServiceType.BOTH);
				}
			}

		}

		@Override
		public void onUserPresent() {
			if(!isSreenOn){
				isSreenOn = true;
				isSreenChanged = false;
			}
		}
	};

	ScreenListener.SystemEvent mSystemEvent = new ScreenListener.SystemEvent() {
		@Override
		public void onHomeKeyClicked() {
			Logger.e("home键被点击了,isHomeClicked=%b",isHomeClicked);
			if(!isHomeClicked){
				isHomeClicked = true;
				executePostReadingRecordTask();
				ReadingRecorder tmpRecorder = buildReadingRecord();
				if(tmpRecorder!=null){
					startPoserReadingRecorderService(tmpRecorder,PostReadingRecorderService.ServiceType.BOTH);
				}
			}
		}

		@Override
		public void onMultiTaskClicked() {
			Logger.e("多任务状态");
		}
	};
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			default:
				super.onActivityResult(requestCode, resultCode, data);
				break;
			case REQUEST_PREFERENCES:
				if (resultCode != RESULT_DO_NOTHING && data != null) {
					final Book book = FBReaderIntents.getBookExtra(data, myFBReaderApp.Collection);
					if (book != null) {
						getCollection().bindToService(this, new Runnable() {
							public void run() {
								onPreferencesUpdate(book);
							}
						});
					}
				}
				break;
			case REQUEST_CANCEL_MENU:
				runCancelAction(data);
				break;
		}
	}

	public void hideDictionarySelection() {
		myFBReaderApp.getTextView().hideOutline();
		myFBReaderApp.getTextView().removeHighlightings(DictionaryHighlighting.class);
		myFBReaderApp.getViewWidget().reset();
		myFBReaderApp.getViewWidget().repaint();
	}

	public BookCollectionShadow getCollection() {
		return (BookCollectionShadow) myFBReaderApp.Collection;
	}

	private void onPreferencesUpdate(Book book) {
		AndroidFontUtil.clearFontCache();
		myFBReaderApp.onBookUpdated(book);
	}

	private void runCancelAction(Intent intent) {
		final CancelMenuHelper.ActionType type;
		try {
			type = CancelMenuHelper.ActionType.valueOf(
					intent.getStringExtra(FBReaderIntents.Key.TYPE)
			);
		} catch (Exception e) {
			// invalid (or null) type value
			return;
		}
		Bookmark bookmark = null;
		if (type == CancelMenuHelper.ActionType.returnTo) {
			bookmark = FBReaderIntents.getBookmarkExtra(intent);
			if (bookmark == null) {
				return;
			}
		}
		myFBReaderApp.runCancelAction(type, bookmark);
	}

	public void showLastPage() {
		if (llReadEnd.getVisibility() == View.GONE) {
			if (shelfBook != null) {
				if (shelfBook.getBookType() != BookType.TRY_READ_BOOK) {
					tvTips.setText("阅读已结束");
					btnGotobuy.setVisibility(View.GONE);
				}
			}
			llReadEnd.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		setScreenLight(curSeek);
		myStartTimer = true;
		Config.Instance().runOnConnect(new Runnable() {
			public void run() {
				SyncOperations.enableSync(FBReader.this, myFBReaderApp.SyncOptions);

				final int brightnessLevel =
						getZLibrary().ScreenBrightnessLevelOption.getValue();
				if (brightnessLevel != 0) {
//					getViewWidget().setScreenBrightness(brightnessLevel);
				} else {
//					setScreenBrightnessAuto();
				}
				if (getZLibrary().DisableButtonLightsOption.getValue()) {
					setButtonLight(false);
				}

				getCollection().bindToService(FBReader.this, new Runnable() {
					public void run() {
						final BookModel model = myFBReaderApp.Model;
						if (model == null || model.Book == null) {
							return;
						}
						onPreferencesUpdate(myFBReaderApp.Collection.getBookById(model.Book.getId()));
					}
				});
			}
		});

		registerReceiver(myBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		IsPaused = false;
		myResumeTimestamp = System.currentTimeMillis();
		if (OnResumeAction != null) {
			final Runnable action = OnResumeAction;
			OnResumeAction = null;
			action.run();
		}
		registerReceiver(mySyncUpdateReceiver, new IntentFilter(FBReaderIntents.Event.SYNC_UPDATED),FBReaderIntents.Event.CUSTOM_BROADCAST_PSERMISSION,null);

		SetScreenOrientationAction.setOrientation(this, getZLibrary().getOrientationOption().getValue());
		if (myCancelIntent != null) {
			final Intent intent = myCancelIntent;
			myCancelIntent = null;
			getCollection().bindToService(this, new Runnable() {
				public void run() {
					runCancelAction(intent);
				}
			});
			return;
		} else if (myOpenBookIntent != null) {
			final Intent intent = myOpenBookIntent;
			myOpenBookIntent = null;
			getCollection().bindToService(this, new Runnable() {
				public void run() {
					openBook(intent, null, true);
				}
			});
		} else if (myFBReaderApp.getCurrentServerBook(null) != null) {
			getCollection().bindToService(this, new Runnable() {
				public void run() {
					myFBReaderApp.useSyncInfo(true, myNotifier);
				}
			});
		} else if (myFBReaderApp.Model == null && myFBReaderApp.ExternalBook != null) {
			getCollection().bindToService(this, new Runnable() {
				public void run() {
					myFBReaderApp.openBook(myFBReaderApp.ExternalBook, null, null, myNotifier);
				}
			});
		} else {
			getCollection().bindToService(this, new Runnable() {
				public void run() {
					myFBReaderApp.useSyncInfo(true, myNotifier);
				}
			});
		}

//        PopupPanel.restoreVisibilities(myFBReaderApp);
		ApiServerImplementation.sendEvent(this, ApiListener.EVENT_READ_MODE_OPENED);
	}

	@Override
	protected void onPause() {
		SyncOperations.quickSync(this, myFBReaderApp.SyncOptions);

		IsPaused = true;
		try {
			unregisterReceiver(mySyncUpdateReceiver);
		} catch (IllegalArgumentException e) {
		}

		try {
			unregisterReceiver(myBatteryInfoReceiver);
		} catch (IllegalArgumentException e) {
			// do nothing, this exception means that myBatteryInfoReceiver was not registered
		}

		myFBReaderApp.stopTimer();
		if (getZLibrary().DisableButtonLightsOption.getValue()) {
			setButtonLight(true);
		}
		myFBReaderApp.onWindowClosing();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		getCollection().unbind();
		unbindService(DataConnection);
//		if (isRecordBegin) {
////			orbitRecordEnd();
//			buildDelayTask();
//		}
		ReadingRecorder tmpRecorder = buildReadingRecord();
		if(tmpRecorder!=null){
			startPoserReadingRecorderService(tmpRecorder,PostReadingRecorderService.ServiceType.BOTH);
		}
		if(EventBus.getDefault().isRegistered(this)){
			EventBus.getDefault().unregister(this);
		}
		ScreenListener.getInstance(this).unregisterScreenReceiver();
		ScreenListener.getInstance(this).unregisterInnerReceiver();
		super.onDestroy();
	}

	/**
	 * 获取主题色
	 */
	public int getColorPrimary() {
		TypedValue typedValue = new TypedValue();
		getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
		return typedValue.data;
	}

	@Override
	protected void onStop() {
		saveMenuSetting();
		ApiServerImplementation.sendEvent(this, ApiListener.EVENT_READ_MODE_CLOSED);
		PopupPanel.removeAllWindows(myFBReaderApp, this);
		super.onStop();
	}

	private void saveMenuSetting() {
		if (sharePreferenceUtil != null && curSeek != 0) {
			sharePreferenceUtil.putInt(SharedPreferenceUtil.CURRENT_LIGHT_VALUE, curSeek);
		}
	}

	private void setButtonLight(boolean enabled) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			setButtonLightInternal(enabled);
		}
	}

	private synchronized void openBook(Intent intent, final Runnable action, boolean force) {
		if (!force && myBook != null) {
			return;
		}

		myBook = FBReaderIntents.getBookExtra(intent, myFBReaderApp.Collection);
		final Bookmark bookmark = FBReaderIntents.getBookmarkExtra(intent);
		if (myBook == null) {
			final Uri data = intent.getData();
			if (data != null) {
				myBook = createBookForFile(ZLFile.createFileByPath(data.getPath()));
			}
		}
		if (myBook != null) {

			ZLFile file = BookUtil.fileByBook(myBook);
			if (!file.exists()) {
				if (file.getPhysicalFile() != null) {
					file = file.getPhysicalFile();
				}
				UIMessageUtil.showErrorMessage(this, "fileNotFound", file.getPath());
				myBook = null;
			} else {
				NotificationUtil.drop(this, myBook);
			}
		}
		Config.Instance().runOnConnect(new Runnable() {
			public void run() {
				myFBReaderApp.openBook(myBook, bookmark, action, myNotifier);
				AndroidFontUtil.clearFontCache();
			}
		});
	}

	@TargetApi(Build.VERSION_CODES.FROYO)
	private void setButtonLightInternal(boolean enabled) {
		final WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.buttonBrightness = enabled ? -1.0f : 0.0f;
		getWindow().setAttributes(attrs);
	}

	private Book createBookForFile(ZLFile file) {
		if (file == null) {
			return null;
		}
		Book book = myFBReaderApp.Collection.getBookByFile(file.getPath());
		if (book != null) {
			return book;
		}
		if (file.isArchive()) {
			for (ZLFile child : file.children()) {
				book = myFBReaderApp.Collection.getBookByFile(child.getPath());
				if (book != null) {
					return book;
				}
			}
		}
		return null;
	}

	private void initMenuSetting() {
		if (sharePreferenceUtil != null) {
//            setScreenLight(sharePreferenceUtil.getInt(SharedPreferenceUtil.CURRENT_LIGHT_VALUE, 80));
			myFBReaderApp.runAction(ActionCode.INCREASE_FONT);
			if (sharePreferenceUtil.getBoolean(SharedPreferenceUtil.DAY_OR_NIGHT, false)) {
				myFBReaderApp.runAction(ActionCode.SWITCH_TO_NIGHT_PROFILE);
			} else {
				myFBReaderApp.runAction(ActionCode.SWITCH_TO_DAY_PROFILE);
				if (sharePreferenceUtil.getInt(SharedPreferenceUtil.CURRENT_BACKGROUND_COLOR, 0) == 0) {
					sharePreferenceUtil.putInt(SharedPreferenceUtil.CURRENT_BACKGROUND_COLOR, Color.rgb(253, 236, 202));
					myFBReaderApp.runAction(ActionCode.UPDATE_BACKGROUND_COLOR);
				}
			}
		}
	}

	public ShelfBook getShelfBook(){
		return shelfBook;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.img_menu_back:
				showDialog("是否退出阅读?", 2);
				break;
			case R.id.img_pinglun:
				showDialog(publicCommentList);
				break;
			case R.id.img_menu_search:
				HashMap<String, String> tempMap = new HashMap<>();
				tempMap.put(CONTENT_KEY, shelfBook.getBookName());
				String url = Urls.URL + "/iycong_web/html/book_store/book_detail.html?id=" + shelfBook.getBookId();
				tempMap.put(URLS_KEY, url);
				ShareUtils shareUtils = new ShareUtils(this, tempMap, BOOK_SHARE);
				shareUtils.addImagUrl(shelfBook.getBookImageUrl());
				shareUtils.open();
				break;
			case R.id.img_menu_buy:
				if (shelfBook != null) {
					if (CommonUtil.getLoginState()) {
						if (shelfBook.getBookPrice() > 0) {
							IntentUtils.goToPayActivity(this, shelfBook.getBookId() + "", shelfBook.getBookPrice() + "", shelfBook.getBookPrice(), 1);
//                            BigDecimal bigDecimal = new BigDecimal(shelfBook.getBookPrice());
//                            Intent intent = new Intent(this, MinePayActivity.class);
//                            intent.putExtra("bookIds", shelfBook.getBookId() + "");
//                            intent.putExtra("totalPrice", bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
//                            intent.putExtra("count", 1);
//                            startActivity(intent);
						}
					} else {
						showDialog("购买书籍请先登录", 1);
						ToastCompat.makeText(this, "购买书籍请先登录", Toast.LENGTH_SHORT).show();
					}
				}
				break;
			case R.id.rb_menu_change_zn:
				changeLanguage(false);
				break;
			case R.id.cb_bg_color_mode_5:
				if (cbDayAndNight.isChecked()) {
					sharePreferenceUtil.putBoolean(SharedPreferenceUtil.DAY_OR_NIGHT, true);
					checkDayNightMode(true);
					myFBReaderApp.runAction(ActionCode.SWITCH_TO_NIGHT_PROFILE);
				} else {
					sharePreferenceUtil.putBoolean(SharedPreferenceUtil.DAY_OR_NIGHT, false);
					checkDayNightMode(false);
					myFBReaderApp.runAction(ActionCode.SWITCH_TO_DAY_PROFILE);
					myFBReaderApp.runAction(ActionCode.UPDATE_BACKGROUND_COLOR);
				}
				break;
			case R.id.rb_menu_textsize:
				if (llReaderSettingWindow.getVisibility() == View.VISIBLE) {
					llReaderSettingWindow.setVisibility(View.INVISIBLE);
					rgMenu.clearCheck();
				} else {
					llMoreSetting.setVisibility(View.INVISIBLE);
					llReaderSettingWindow.setVisibility(View.VISIBLE);
					rlNavigation.setVisibility(View.GONE);
				}
				break;
			case R.id.rb_menu_add_marker:
				if (rlNavigation.getVisibility() == View.GONE && llReaderSettingWindow.getVisibility() == View.INVISIBLE) {
					rgMenu.clearCheck();
				} else {
					llMoreSetting.setVisibility(View.INVISIBLE);
					rlNavigation.setVisibility(View.GONE);
					llReaderSettingWindow.setVisibility(View.INVISIBLE);
					myFBReaderApp.runAction(ActionCode.ADD_BOOKMARKERS);
				}
				break;
			case R.id.rb_menu_progress:
				if (rlNavigation.getVisibility() == View.VISIBLE) {
					rlNavigation.setVisibility(View.GONE);
					rgMenu.clearCheck();
				} else {
					llMoreSetting.setVisibility(View.INVISIBLE);
					llReaderSettingWindow.setVisibility(View.INVISIBLE);
					rlNavigation.setVisibility(View.VISIBLE);
					myFBReaderApp.runAction(ActionCode.SHOW_NAVIGATION);
				}
				break;
			case R.id.tv_content_search:
				llMoreSetting.setVisibility(View.INVISIBLE);
				myFBReaderApp.runAction(ActionCode.SEARCH);
				break;
			case R.id.rb_menu_more:
				if (llMoreSetting.getVisibility() == View.VISIBLE) {
					llMoreSetting.setVisibility(View.INVISIBLE);
					rgMenu.clearCheck();
				} else {
					llMoreSetting.setVisibility(View.VISIBLE);
					rlNavigation.setVisibility(View.GONE);
					llReaderSettingWindow.setVisibility(View.INVISIBLE);
				}
				break;
		}
	}

	private void getPublicComments(int segmentId){
		OkGo.get(Urls.CONMENTEDCONENTLIST)
				.tag(this)
				.params("bookid", sharePreferenceUtil.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, -1))
				.params("languagetype", sharePreferenceUtil.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 1))
				.params("segmentid", segmentId)
				.params("pageSize", 0)
				.params("pageNo",0)
				.execute(new JsonCallback<IycResponse<List<PublicComment>>>(this) {
					@Override
					public void onSuccess(IycResponse<List<PublicComment>> listIycResponse, Call call, Response response) {
						Logger.e("gft获取公开笔记成功！",listIycResponse.getData().toString());
						ToastCompat.makeText(FBReader.this, "获取公开笔记成功！", Toast.LENGTH_SHORT);
						if (listIycResponse.getData().size()>0){
							publicCommentList.addAll(listIycResponse.getData());
						}

					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						Logger.e("gft获取公开笔记失败！");
						ToastCompat.makeText(FBReader.this, "获取公开笔记失败！", Toast.LENGTH_SHORT);
					}
				});

	}
	private void showDialog(List<PublicComment> list){
		// 以特定的风格创建一个dialog
		Dialog dialog = new Dialog(this,R.style.MyDialog);
		// 加载dialog布局view
		View purchase = LayoutInflater.from(this).inflate(R.layout.comment_list, null);

		commentAdapter = new ContentListAdapter(this,list);
		RecyclerView rvComment = purchase.findViewById(R.id.rv_comment_list);
		LinearLayoutManager llm = new LinearLayoutManager(context);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		rvComment.setLayoutManager(llm);
		rvComment.setAdapter(commentAdapter);
		// 设置外部点击 取消dialog
		dialog.setCancelable(true);
		// 获得窗体对象
		Window window = dialog.getWindow();
		// 设置窗体的对齐方式
		window.setGravity(Gravity.CENTER);
		// 设置窗体动画
		window.setWindowAnimations(R.style.AnimBottom);
		// 设置窗体的padding
		window.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);
		dialog.setContentView(purchase);
		dialog.show();
//		tvCancel.setOnClickListener(itemsOnClick);
//		imHide.setOnClickListener(itemsOnClick);
//		ivProgress.setOnClickListener(itemsOnClick);

	}
	@Override
	public void onLowMemory() {
		myFBReaderApp.onWindowClosing();
		super.onLowMemory();
	}

	@Override
	protected void onNewIntent(final Intent intent) {
		final String action = intent.getAction();
		final Uri data = intent.getData();
		Logger.e("wzp onNewIntent");
		if ((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0) {
			super.onNewIntent(intent);
		} else if (Intent.ACTION_VIEW.equals(action)
				&& data != null && "fbreader-action".equals(data.getScheme())) {
			myFBReaderApp.runAction(data.getEncodedSchemeSpecificPart(), data.getFragment());
		} else if (Intent.ACTION_VIEW.equals(action) || FBReaderIntents.Action.VIEW.equals(action)) {
			myOpenBookIntent = intent;
			if (myFBReaderApp.Model == null && myFBReaderApp.ExternalBook != null) {
				final BookCollectionShadow collection = getCollection();
				final Book b = FBReaderIntents.getBookExtra(intent, collection);
				if (!collection.sameBook(b, myFBReaderApp.ExternalBook)) {
					try {
						final ExternalFormatPlugin plugin =
								(ExternalFormatPlugin) BookUtil.getPlugin(
										PluginCollection.Instance(Paths.systemInfo(this)),
										myFBReaderApp.ExternalBook
								);
						startActivity(PluginUtil.createIntent(plugin, FBReaderIntents.Action.PLUGIN_KILL));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else if (FBReaderIntents.Action.PLUGIN.equals(action)) {
			new RunPluginAction(this, myFBReaderApp, data).run();
		} else if (Intent.ACTION_SEARCH.equals(action)) {
			final String pattern = intent.getStringExtra(SearchManager.QUERY);
			final Runnable runnable = new Runnable() {
				public void run() {
					final TextSearchPopup popup = (TextSearchPopup) myFBReaderApp.getPopupById(TextSearchPopup.ID);
					popup.initPosition();
					myFBReaderApp.MiscOptions.TextSearchPattern.setValue(pattern);
					if (myFBReaderApp.getTextView().search(pattern, true, false, false, false) != 0) {
						runOnUiThread(new Runnable() {
							public void run() {
								myFBReaderApp.showPopup(popup.getId());
							}
						});
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								UIMessageUtil.showErrorMessage(FBReader.this, "textNotFound");
								popup.StartPosition = null;
							}
						});
					}
				}
			};
			UIUtil.wait("search", runnable, this);
		} else if (FBReaderIntents.Action.CLOSE.equals(intent.getAction())) {
			myCancelIntent = intent;
			myOpenBookIntent = null;
		} else if (FBReaderIntents.Action.PLUGIN_CRASH.equals(intent.getAction())) {
			final Book book = FBReaderIntents.getBookExtra(intent, myFBReaderApp.Collection);
			myFBReaderApp.ExternalBook = null;
			myOpenBookIntent = null;
			getCollection().bindToService(this, new Runnable() {
				public void run() {
					final BookCollectionShadow collection = getCollection();
					Book b = collection.getRecentBook(0);
					if (collection.sameBook(b, book)) {
						b = collection.getRecentBook(1);
					}
					myFBReaderApp.openBook(b, null, null, myNotifier);
				}
			});
		} else {
			super.onNewIntent(intent);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		getCollection().bindToService(this, new Runnable() {
			public void run() {
				new Thread() {
					public void run() {
						getPostponedInitAction().run();
					}
				}.start();

				myFBReaderApp.getViewWidget().repaint();
			}
		});

		initPluginActions();

		final ZLAndroidLibrary zlibrary = getZLibrary();

		Config.Instance().runOnConnect(new Runnable() {
			public void run() {
				final boolean showStatusBar = zlibrary.ShowStatusBarOption.getValue();
				if (showStatusBar != myShowStatusBarFlag) {
					finish();
					startActivity(new Intent(FBReader.this, FBReader.class));
				}
				zlibrary.ShowStatusBarOption.saveSpecialValue();
				myFBReaderApp.ViewOptions.ColorProfileName.saveSpecialValue();
				SetScreenOrientationAction.setOrientation(FBReader.this, zlibrary.getOrientationOption().getValue());
			}
		});

		((PopupPanel) myFBReaderApp.getPopupById(TextSearchPopup.ID)).setPanelInfo(this, myRootView);
		((PopupPanel) myFBReaderApp.getPopupById(SelectionPopup.ID)).setPanelInfo(this, myRootView);
	}

	private Runnable getPostponedInitAction() {
		return new Runnable() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						new TipRunner().start();
						DictionaryUtil.init(FBReader.this, null);
						final Intent intent = getIntent();
						if (intent != null && FBReaderIntents.Action.PLUGIN.equals(intent.getAction())) {
							new RunPluginAction(FBReader.this, myFBReaderApp, intent.getData()).run();
						}
					}
				});
			}
		};
	}

	/**
	 * 上传阅读数据
	 */

	private void initPluginActions() {
		synchronized (myPluginActions) {
			int index = 0;
			while (index < myPluginActions.size()) {
				myFBReaderApp.removeAction(PLUGIN_ACTION_PREFIX + index++);
			}
			myPluginActions.clear();
		}

		sendOrderedBroadcast(
				new Intent(PluginApi.ACTION_REGISTER),
				null,
				myPluginInfoReceiver,
				null,
				RESULT_OK,
				null,
				null
		);
	}

	public void showSelectionPanel() {
		final ZLTextView view = myFBReaderApp.getTextView();
		((SelectionPopup) myFBReaderApp.getPopupById(SelectionPopup.ID))
				.move(view.getSelectionStartY(), view.getSelectionEndY());
		myFBReaderApp.showPopup(SelectionPopup.ID);
	}

	public void hideSelectionPanel() {
		final FBReaderApp.PopupPanel popup = myFBReaderApp.getActivePopup();
		if (popup != null && popup.getId().equals(SelectionPopup.ID)) {
			myFBReaderApp.hideActivePopup();
		}
	}

	public void navigate() {
		((NavigationPopup) myFBReaderApp.getPopupById(NavigationPopup.ID)).runNavigation();
	}

	/**
	 * 切换语言，判断是否手势切换
	 * @param gesTure
	 */
	public void changeLanguage(boolean gesTure) {
		if (sharePreferenceUtil == null) {
			sharePreferenceUtil = SharedPreferenceUtil.getInstance();
		}
		if (!sharePreferenceUtil.getBoolean(SharedPreferenceUtil.CAN_CHANGE_LANGUAGE, true)) {
			if (!ClickUtils.isFastDoubleClick1()){
				ToastCompat.makeText(this,"抱歉，本书没有对应译文版本!",Toast.LENGTH_SHORT).show();
			}else {
				return;
			}
			return;
		}
		String language;
		if (gesTure || rbMenuChangeZn == null) {
			int languageType = sharePreferenceUtil.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 1);
			switch (languageType) {
				case 1:
					sharePreferenceUtil.putInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 2);
					language = ".en.epub";
					break;
				case 2:
					sharePreferenceUtil.putInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 1);
					language = ".zh.epub";
					break;
				default:
					return;
			}
		} else {
			if (rbMenuChangeZn.isChecked()) {
				sharePreferenceUtil.putInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 2);
				language = ".en.epub";
			} else {
				sharePreferenceUtil.putInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 1);
				language = ".zh.epub";
			}
		}
		executePostReadingRecordTask();
		List<ShelfBook> shelfBookList = bookDao.queryByColumn("bookId", sharePreferenceUtil.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0));
		if (shelfBookList != null && shelfBookList.size() > 0) {
			final String bookPath = shelfBookList.get(0).getBookPath() + shelfBookList.get(0).getBookId() + language;
			getCollection().bindToService(this, new Runnable() {
				public void run() {
					final BookCollectionShadow collection = getCollection();
					Book b = collection.getBookByFile(bookPath);
					Bookmark bookmark = myFBReaderApp.createBookmark(80, false);
					int paragraphIndex = 0;
					if (bookmark != null) {
						paragraphIndex = bookmark.getParagraphIndex();
					}
					myFBReaderApp.openBook(b, bookmark, new Runnable() {
						@Override
						public void run() {
							if (semesterId > 0) {
								addBookmark(getListData(),BookmarkerType.BookNote,1);
								buildRescusiveTask();//切换语言以后重新构建一次
//								executeOrbitBeginTask();//在阅读器切换语言成功以后，进行一次开始的任务；
//								buildDelayTask();//切换语言成功以后，构建一个延时任务；
							}
						}
					}, myNotifier, paragraphIndex);
				}
			});
		}
	}

	public void getCommentCount(String segments){
		OkGo.get(Urls.CONMENTEDCONENTCOUNTLIST)
				.tag(this)
				.params("bookId", sharePreferenceUtil.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, -1))
				.params("languageType", sharePreferenceUtil.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 1))
				.params("segmentIds", segments)
				.execute(new JsonCallback<IycResponse<List<CommentCount>>>(this) {
					@Override
					public void onSuccess(IycResponse<List<CommentCount>> listIycResponse, Call call, Response response) {
						Logger.e("gft获取公开笔记数量成功！",listIycResponse.getData().toString());
						for (CommentCount commentCount:listIycResponse.getData()){
							num = num + commentCount.getCount();
						}
						commentCount.setText(num+"");
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						Logger.e("gft获取公开笔记数量失败！");

					}
				});
	}

	public void showMenu() {
		num = 0;
		publicCommentList.clear();
		List<Integer> segmentIds = BookInfoUtils.getCurrentPageSegmentIds(myFBReaderApp);
		String segments = "";
		for (Integer segment:segmentIds){
			segments = segments+segment.toString()+",";
			Log.e("gft测试", "gft在测试segmentId: " + segment.toString());
			getPublicComments(segment);
		}
		Log.e("gft测试", "gft在测试segmentId: " + segments);

		//显示菜单
		// 利用layoutInflater获得Views
		setStatusBarVisibility(true);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.menu, null);
		ImageView imgMenuBack = (ImageView) view.findViewById(R.id.img_menu_back);
		commentCount = (TextView)view.findViewById(R.id.comment_count) ;
		getCommentCount(segments.substring(0,segments.length()-1));

		rlMenuTop = (RelativeLayout) view.findViewById(R.id.rl_menu_top);
		llMenuBottom = (LinearLayout) view.findViewById(R.id.ll_menu_bottom);
		llDayNightMode = (LinearLayout) view.findViewById(R.id.ll_day_night_mode);
		wordSizeMode = (LinearLayout) view.findViewById(R.id.word_size_mode);
		turnPageMode = (LinearLayout) view.findViewById(R.id.turn_page_mode);
		backgroundColorMode = (LinearLayout) view.findViewById(R.id.background_color_mode);
		llReaderSettingWindow = (LinearLayout) view.findViewById(R.id.reader_setting_window);
		rlNavigation = (RelativeLayout) view.findViewById(R.id.rl_navigation_window);
		llMoreSetting = (LinearLayout) view.findViewById(R.id.ll_read_setting_more);
		TextView tvSearch = (TextView) view.findViewById(R.id.tv_content_search);
		tvSearch.setOnClickListener(this);
		TextView tvShare = (TextView) view.findViewById(R.id.tv_book_share);
		tvShare.setOnClickListener(this);
		checkDayNightMode(sharePreferenceUtil.getBoolean(SharedPreferenceUtil.DAY_OR_NIGHT, false));

		imgMenuBack.setOnClickListener(this);
		rbMenuChangeZn = (CheckBox) view.findViewById(R.id.rb_menu_change_zn);
		ImageView imgZhEn = (ImageView) view.findViewById(R.id.img_zh_en);

		if (shelfBook != null && shelfBook.getSupportLanguage() != 3) {
			rbMenuChangeZn.setVisibility(View.GONE);
			if (shelfBook.getSupportLanguage() == 1) {
				imgZhEn.setImageResource(R.drawable.img_zh);
			} else {
				imgZhEn.setImageResource(R.drawable.img_en);
			}
			imgZhEn.setVisibility(View.VISIBLE);
		} else {
			rbMenuChangeZn.setOnClickListener(this);
		}
		if (sharePreferenceUtil.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 0) == 1) {
			rbMenuChangeZn.setChecked(false);
		} else {
			rbMenuChangeZn.setChecked(true);
		}

		// 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
//        final PopupWindow
		mPopupWindow = new PopupWindow(view,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);

		// 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
		mPopupWindow.setFocusable(true);

		// 设置popWindow的显示和消失动画
//        windowTop.setAnimationStyle(R.style.popwindow_top_anim_style);
		// 在顶部显示
		if (!mPopupWindow.isShowing()) {
			mPopupWindow.showAtLocation(view.findViewById(R.id.rl_menu_top),
					Gravity.BOTTOM, 0, 0);
		}
		mPopupWindow.setOutsideTouchable(true);
		View viewContent = view.findViewById(R.id.menu_content);
		viewContent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mPopupWindow.dismiss();
				setStatusBarVisibility(false);
				((NavigationPopup) myFBReaderApp.getPopupById(NavigationPopup.ID)).removeWindow(FBReader.this);
			}
		});

		rgMenu = (RadioGroup) view.findViewById(R.id.rg_menu_contents);
		RadioButton rbMenuTextsize = (RadioButton) view.findViewById(R.id.rb_menu_textsize);
		rbMenuTextsize.setOnClickListener(this);
		RadioButton rbMenuProgress = (RadioButton) view.findViewById(R.id.rb_menu_progress);
		rbMenuProgress.setOnClickListener(this);
		RadioButton rbMenuAddMarker = (RadioButton) view.findViewById(R.id.rb_menu_add_marker);
		rbMenuAddMarker.setOnClickListener(this);
		RadioButton rbMenuMore = (RadioButton) view.findViewById(R.id.rb_menu_more);
		rbMenuMore.setOnClickListener(this);
		rgMenu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				switch (i) {
					case R.id.rb_menu_contents:
						mPopupWindow.dismiss();
						setStatusBarVisibility(false);
						((NavigationPopup) myFBReaderApp.getPopupById(NavigationPopup.ID)).removeWindow(FBReader.this);
						myFBReaderApp.runAction(ActionCode.SHOW_TOC);
						break;
				}
			}
		});
		ImageView pinlun = (ImageView) view.findViewById(R.id.img_pinglun);
		pinlun.setOnClickListener(this);
		ImageView imgMenuSearch = (ImageView) view.findViewById(R.id.img_menu_search);
		imgMenuSearch.setOnClickListener(this);
		ImageView imgShopping = (ImageView) view.findViewById(R.id.img_menu_buy);
		imgShopping.setOnClickListener(this);
		if (CommonUtil.getLoginState()) {
			if (shelfBook != null) {
				if (shelfBook.getBookType() == BookType.TRY_READ_BOOK) {
					imgShopping.setVisibility(View.VISIBLE);
				}
			}
		}


//		setScreenLight(curSeek);
		light_seekBar = (SeekBar) view.findViewById(R.id.light_progress);
		light_seekBar.setMax(255);
		light_seekBar.setOnSeekBarChangeListener(this);
		light_seekBar.setProgress(curSeek);

		rgWordSize = (RadioGroup) view.findViewById(R.id.rg_word_size);
		rgWordSize.check(sharePreferenceUtil.getInt(SharedPreferenceUtil.WORD_SIZE_CHECKED, R.id.rb_word_size_3));
		rgWordSize.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				int wordSize = 46;
				switch (i) {
					case R.id.rb_word_size_1:
						wordSize = 34;
						break;
					case R.id.rb_word_size_2:
						wordSize = 40;
						break;
					case R.id.rb_word_size_3:
						wordSize = 46;
						break;
					case R.id.rb_word_size_4:
						wordSize = 56;
						break;
					case R.id.rb_word_size_5:
						wordSize = 66;
						break;
				}
				sharePreferenceUtil.putInt(SharedPreferenceUtil.CURRENT_FONT_SIZE, wordSize);
				sharePreferenceUtil.putInt(SharedPreferenceUtil.WORD_SIZE_CHECKED, i);
				myFBReaderApp.runAction(ActionCode.INCREASE_FONT);
			}
		});

		rgTurnPage = (RadioGroup) view.findViewById(R.id.rg_turn_page);
		rgTurnPage.check(sharePreferenceUtil.getInt(SharedPreferenceUtil.TURN_PAGE_MODE_CHECKED, R.id.rb_turn_page_mode_1));
		rgTurnPage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				switch (i) {
					case R.id.rb_turn_page_mode_1:
						myFBReaderApp.PageTurningOptions.Animation.setValue(ZLViewEnums.Animation.none);
						break;
					case R.id.rb_turn_page_mode_2:
						myFBReaderApp.PageTurningOptions.Animation.setValue(ZLViewEnums.Animation.curl);
						break;
					case R.id.rb_turn_page_mode_3:
						myFBReaderApp.PageTurningOptions.Animation.setValue(ZLViewEnums.Animation.slide);
						break;
					case R.id.rb_turn_page_mode_4:
						myFBReaderApp.PageTurningOptions.Animation.setValue(ZLViewEnums.Animation.slideOldStyle);
						break;
					case R.id.rb_turn_page_mode_5:
						myFBReaderApp.PageTurningOptions.Animation.setValue(ZLViewEnums.Animation.shift);
						break;
				}
				sharePreferenceUtil.putInt(SharedPreferenceUtil.TURN_PAGE_MODE_CHECKED, i);
			}
		});

		((NavigationPopup) myFBReaderApp.getPopupById(NavigationPopup.ID)).setPanelInfo(this, rlNavigation);

		cbDayAndNight = (CheckBox) view.findViewById(R.id.cb_bg_color_mode_5);
		cbDayAndNight.setChecked(sharePreferenceUtil.getBoolean(SharedPreferenceUtil.DAY_OR_NIGHT, false));
		cbDayAndNight.setOnClickListener(this);

		rgBackgroundColor = (RadioGroup) view.findViewById(R.id.rg_bg_color_mode);
		rgBackgroundColor.check(sharePreferenceUtil.getInt(SharedPreferenceUtil.BACKGROUND_COLOR_CHECKED, Color.rgb(253, 236, 202)));
		rgBackgroundColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				int color = 0;
				switch (i) {
					case R.id.rb_bg_color_mode_1:
						color = Color.rgb(253, 236, 202);
						break;
					case R.id.rb_bg_color_mode_2:
						color = Color.rgb(254, 234, 229);
						break;
					case R.id.rb_bg_color_mode_3:
						color = Color.rgb(233, 248, 244);
						break;
					case R.id.rb_bg_color_mode_4:
						color = Color.rgb(230, 229, 229);
						break;
				}
				if (color != 0) {
					sharePreferenceUtil.putInt(SharedPreferenceUtil.CURRENT_BACKGROUND_COLOR, color);
					sharePreferenceUtil.putInt(SharedPreferenceUtil.BACKGROUND_COLOR_CHECKED, i);
					if (!cbDayAndNight.isChecked()) {
						myFBReaderApp.runAction(ActionCode.UPDATE_BACKGROUND_COLOR);
					}
				}
			}
		});


	}

	private void setStatusBarVisibility(boolean visible) {
		Window window = getWindow();
		if (DeviceType.Instance() != DeviceType.KINDLE_FIRE_1ST_GENERATION && !myShowStatusBarFlag) {
			if (visible) {
				window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			} else {
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			}
		}
	}

	private void setScreenLight(int l) {

		if (l == 0) {
			l++;
		}
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		Float temFloat = (float) l / 255;
		if (0 < temFloat && temFloat <= 1) {
			lp.screenBrightness = temFloat;
		}

		getWindow().setAttributes(lp);
	}

	private Menu addSubmenu(Menu menu, String id) {
		return menu.addSubMenu(ZLResource.resource("menu").getResource(id).getValue());
	}

	private void addMenuItem(Menu menu, String actionId, Integer iconId, String name) {
		if (name == null) {
			name = ZLResource.resource("menu").getResource(actionId).getValue();
		}
		final MenuItem menuItem = menu.add(name);
		if (iconId != null) {
			menuItem.setIcon(iconId);
		}
		menuItem.setOnMenuItemClickListener(myMenuListener);
		myMenuItemMap.put(menuItem, actionId);
	}

	private void addMenuItem(Menu menu, String actionId, String name) {
		addMenuItem(menu, actionId, null, name);
	}

	private void checkDayNightMode(boolean dayOrNight) {
		if (dayOrNight) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				rlMenuTop.setBackground(getResources().getDrawable(R.drawable.img_night_head_bg));
			}
			llMenuBottom.setBackgroundColor(getResources().getColor(R.color.night_dark));
			llDayNightMode.setBackgroundColor(getResources().getColor(R.color.night_dark));
			wordSizeMode.setBackgroundColor(getResources().getColor(R.color.night_dark));
			turnPageMode.setBackgroundColor(getResources().getColor(R.color.night_dark));
			backgroundColorMode.setBackgroundColor(getResources().getColor(R.color.night_dark));
			rlNavigation.setBackgroundColor(getResources().getColor(R.color.night_dark));
		} else {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				rlMenuTop.setBackground(getResources().getDrawable(R.drawable.img_day_head_bg));
			}
			llMenuBottom.setBackgroundColor(getResources().getColor(R.color.white));
			llDayNightMode.setBackgroundColor(getResources().getColor(R.color.white));
			wordSizeMode.setBackgroundColor(getResources().getColor(R.color.white));
			turnPageMode.setBackgroundColor(getResources().getColor(R.color.white));
			backgroundColorMode.setBackgroundColor(getResources().getColor(R.color.white));
			rlNavigation.setBackgroundColor(getResources().getColor(R.color.white));
		}
	}

	private void addMenuItem(Menu menu, String actionId, int iconId) {
		addMenuItem(menu, actionId, iconId, null);
	}

	private void addMenuItem(Menu menu, String actionId) {
		addMenuItem(menu, actionId, null, null);
	}

	private void fillMenu(Menu menu, List<MenuNode> nodes) {
		for (MenuNode n : nodes) {
			if (n instanceof MenuNode.Item) {
				final Integer iconId = ((MenuNode.Item) n).IconId;
				if (iconId != null) {
					addMenuItem(menu, n.Code, iconId);
				} else {
					addMenuItem(menu, n.Code);
				}
			} else /* if (n instanceof MenuNode.Submenu) */ {
				final Menu submenu = addSubmenu(menu, n.Code);
				fillMenu(submenu, ((MenuNode.Submenu) n).Children);
			}
		}
	}

	private void setupMenu(Menu menu) {
		final String menuLanguage = ZLResource.getLanguageOption().getValue();
		if (menuLanguage.equals(myMenuLanguage)) {
			return;
		}
		myMenuLanguage = menuLanguage;

		menu.clear();
		fillMenu(menu, MenuData.topLevelNodes());
		synchronized (myPluginActions) {
			int index = 0;
			for (PluginApi.ActionInfo info : myPluginActions) {
				if (info instanceof PluginApi.MenuActionInfo) {
					addMenuItem(
							menu,
							PLUGIN_ACTION_PREFIX + index++,
							((PluginApi.MenuActionInfo) info).MenuItemName
					);
				}
			}
		}

		refresh();
	}

	protected void onPluginNotFound(final Book book) {
		final BookCollectionShadow collection = getCollection();
		collection.bindToService(this, new Runnable() {
			public void run() {
				final Book recent = collection.getRecentBook(0);
				if (recent != null && !collection.sameBook(recent, book)) {
					myFBReaderApp.openBook(recent, null, null, null);
				} else {
					myFBReaderApp.openHelpBook();
				}
			}
		});
	}

//	@Override
//	protected void onRestoreInstanceState(Bundle savedInstanceState) {
//		super.onRestoreInstanceState(savedInstanceState);
//		ReadingRecorder tmpRecorder = (ReadingRecorder) savedInstanceState.getSerializable(Constants.READING_RECORDER);
//		if(tmpRecorder != null){
//			TaskWithDelay delayTask = new TaskWithDelay(this,tmpRecorder);
//			delayTask.setEndTime(savedInstanceState.getLong(Constants.READING_END_TIME));
//			SingleTimer.getInstance().executeRightNow(new TaskWithDelay(this, tmpRecorder));
//			executeOrbitBeginTask();
//		}
//	}
	@Subscribe(threadMode = ThreadMode.BACKGROUND)
	public void onRestartRecord(StartEvent event){
		Logger.e("无效操作后的有效操作！");
//		executeOrbitBeginTask();//在一段时间内没有有效操作，造成产生过一次调用结束接口的情况下，// 有了有效的操作，需要重新调用开始接口；
	}
	@Override
	protected void onRestart() {
		super.onRestart();
		if (isHomeClicked||isSreenChanged) {
			Logger.e("点击了home:%1$b 点击了电源：%2$b",isHomeClicked,isSreenChanged);
			if(isHomeClicked)
				isHomeClicked = false;
			if(isSreenChanged)
				isSreenChanged = false;
			buildRescusiveTask();//在阅读器被切到后台，又切回来的时候，重新开始构建定时任务；
//			executeOrbitBeginTask();//在阅读器被切到后台，又切回来的时候，调用一次开始接口，重新记录；
//			buildDelayTask();//从后台被切回来以后，调用一次开始
		}
	}

//	@Override
//	public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//		super.onSaveInstanceState(outState, outPersistentState);
//		int chapterId = BookInfoUtils.getAbsChapterId(myFBReaderApp, this);
//		Logger.e("wzp orbitRecordBegin chapterId:%d%n", chapterId);
//		if (chapterId < 0)
//			return;
//		String paragraphText = BookInfoUtils.getParagraphTextByBookmark(myFBReaderApp);
//		;
//		if (paragraphText == null) return;
//		if ("".equals(paragraphText))
//			return;
////		final int relativeParagraphId = BookInfoUtils.getTextRelativeParagraphIndex(myFBReaderApp);
//		final int paragraphId = BookInfoUtils.getAbsoluteParagraphId(myFBReaderApp);
//		Logger.e("阅读轨迹开始接口 段落内容：%s paragraphId：%d", paragraphText, paragraphId);
//		if (paragraphId < 0) {
//			return;
//		}
//		ReadingRecorder tmpRecorder = new ReadingRecorder();
//		tmpRecorder.setBookId((int) sharePreferenceUtil.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0));
//		tmpRecorder.setLanguageType(sharePreferenceUtil.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 0));
//		tmpRecorder.setSegmentId(paragraphId);
//		tmpRecorder.setSegmentStr(paragraphText);
//		tmpRecorder.setChapterId(chapterId);
//		tmpRecorder.setSemesterId(semesterId);
//		outState.putSerializable(Constants.READING_RECORDER, tmpRecorder);
//		outState.putLong(Constants.READING_END_TIME,System.currentTimeMillis());
//
//	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (mViewPager.getVisibility() == View.VISIBLE) {
//			mViewPager.setVisibility(View.GONE);
//			mViewPager.invalidate();
//		}
		if (flReadHelp.getVisibility() == View.VISIBLE) {
			flReadHelp.setVisibility(View.GONE);
			flReadHelp.invalidate();
		} else if (llReadEnd.getVisibility() == View.VISIBLE) {
			llReadEnd.setVisibility(View.GONE);
			llReadEnd.invalidate();
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			showDialog("是否退出阅读?", 2);
		}
		return (myMainView != null && myMainView.onKeyDown(keyCode, event)) || super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return (myMainView != null && myMainView.onKeyUp(keyCode, event)) || super.onKeyUp(keyCode, event);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		switchWakeLock(hasFocus &&
				getZLibrary().BatteryLevelToTurnScreenOffOption.getValue() <
						myFBReaderApp.getBatteryLevel()
		);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
//		setupMenu(menu);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);
	}

	@Override
	public boolean onSearchRequested() {
		final FBReaderApp.PopupPanel popup = myFBReaderApp.getActivePopup();
		myFBReaderApp.hideActivePopup();
		if (DeviceType.Instance().hasStandardSearchDialog()) {
			final SearchManager manager = (SearchManager) getSystemService(SEARCH_SERVICE);
			manager.setOnCancelListener(new SearchManager.OnCancelListener() {
				public void onCancel() {
					if (popup != null) {
						myFBReaderApp.showPopup(popup.getId());
					}
					manager.setOnCancelListener(null);
				}
			});
			startSearch(myFBReaderApp.MiscOptions.TextSearchPattern.getValue(), true, null, false);
		} else {
			SearchDialogUtil.showDialog(
					this, FBReader.class, myFBReaderApp.MiscOptions.TextSearchPattern.getValue(), new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface di) {
							if (popup != null) {
								myFBReaderApp.showPopup(popup.getId());
							}
						}
					}
			);
		}
		return true;
	}

	private final void switchWakeLock(boolean on) {
		if (on) {
			if (myWakeLock == null) {
				myWakeLockToCreate = true;
			}
		} else {
			if (myWakeLock != null) {
				synchronized (this) {
					if (myWakeLock != null) {
						myWakeLock.release();
						myWakeLock = null;
					}
				}
			}
		}
	}

	public final void createWakeLock() {
		if (myWakeLockToCreate) {
			synchronized (this) {
				if (myWakeLockToCreate) {
					myWakeLockToCreate = false;
					myWakeLock =
							((PowerManager) getSystemService(POWER_SERVICE))
									.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "FBReader");
					myWakeLock.acquire();
				}
			}
		}
		if (myStartTimer) {
			myFBReaderApp.startTimer();
			myStartTimer = false;
		}
	}

	public void outlineRegion(ZLTextRegion.Soul soul) {
		myFBReaderApp.getTextView().outlineRegion(soul);
		myFBReaderApp.getViewWidget().repaint();
	}

	public void hideOutline() {
		myFBReaderApp.getTextView().hideOutline();
		myFBReaderApp.getViewWidget().repaint();
	}

	/**
	 * 向数据库写入云书签与笔记
	 */
	public void addCloudBookmarks(final List<Bookmark> bookmarkList) {
		getCollection().bindToService(this, new Runnable() {
			@Override
			public void run() {
				SQLiteBooksDatabase database = new SQLiteBooksDatabase(FBReader.this);
				Logger.e("wzp 加到本地");
				for (final Bookmark androidBookMark : bookmarkList) {
					try {
						boolean isExit = database.updateBookMarker(androidBookMark);
						Logger.e("wzp isBookmarkExist:%s%n", isExit + "");
						if (!isExit) {
							getCollection().saveBookmark(androidBookMark);
						}
					} catch (SQLiteException e) {
						e.printStackTrace();
					}
				}
				database.finalize();
			}
		});
	}

	/**
	 * 向数据库写入云书签与笔记
	 */
	public void addCloudBookmark(final Bookmark bookmark) {
		getCollection().bindToService(this, new Runnable() {
			@Override
			public void run() {
				Logger.e("wzp 加到本地");
					try {
						getCollection().saveBookmark(bookmark);
					} catch (SQLiteException e) {
						e.printStackTrace();
					}
				}
		});
	}

	public void deleteCloudBookmark(final Bookmark bookmark) {
		getCollection().bindToService(this, new Runnable() {
			@Override
			public void run() {
				Logger.e("wzp 加到本地");
				try {
					getCollection().deleteBookmark(bookmark);
				} catch (SQLiteException e) {
					e.printStackTrace();
				}
			}
		});
	}

//    public BookCollection getBookCollection(){
//        return (BookCollection)myFBReaderApp.Collection;
//    }

//	private void loadLocalBookmarkList(BookCollectionShadow collection) {
//		for (BookmarkQuery query = new BookmarkQuery(myBook, 50); ; query = query.next()) {
//			final List<Bookmark> thisBookBookmarks = collection.bookmarks(query);
//			if (thisBookBookmarks.isEmpty()) {
//				break;
//			}
//			localBookmarkList.addAll(thisBookBookmarks);
//		}
//		for (BookmarkQuery query = new BookmarkQuery(myBook, false, 50); ; query = query.next()) {
//			final List<Bookmark> thisBookBookmarks = collection.bookmarks(query);
//			if (thisBookBookmarks.isEmpty()) {
//				break;
//			}
//			localBookmarkList.addAll(thisBookBookmarks);
//		}
//	}

	/**
	 * 下载所有书签或者书摘
	 *
	 * @param type 1：书签  2：笔记
	 */
	private void downloadAllMarker(final int type) {
		if (bookMarkerHasDown)
			return;
		long userId = CommonUtil.getUserId();
		long bookId = sharePreferenceUtil.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, -1);
		if (!CommonUtil.getLoginState() || bookId == -1)
			return;
		OkGo.get(Urls.DownloadAllMarker)
				.params("bookId", bookId)
				.params("userId", userId)
				.params("type", type)
				.execute(new JsonCallback<IycResponse<List<BookNoteBean>>>(context) {
					@Override
					public void onSuccess(IycResponse<List<BookNoteBean>> listIycResponse, Call call, Response response) {
						if (isNull(listIycResponse) || isNull(listIycResponse.getData()))
							return;
						bookMarkerHasDown = true;
						Logger.e("wzp 下载下来的书签内容：%s%n", listIycResponse.getData().toString());

						MarkerThread markerThread = new MarkerThread(listIycResponse.getData());
						markerThread.start();

					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
					}

					@Override
					public void onAfter(IycResponse<List<BookNoteBean>> listIycResponse, Exception e) {
						super.onAfter(listIycResponse, e);
					}
				});
	}


	private void addBookmark(List<BookNoteBean> data,int noteOrMarker,int StyleId){
		List<Bookmark> tmpList = tranformNoteIntoBookmark(data, true, noteOrMarker);
		List<Bookmark> localBookmarkList = new ArrayList<>();
		if (!NotNullUtils.isNull(tmpList)) {
			Logger.e("wzp 转换成本地的笔记的内容：%s%n,大小：%d%n", tmpList.toString(), tmpList.size());
			SQLiteBooksDatabase database = new SQLiteBooksDatabase(FBReader.this);
			for (BookmarkQuery query = new BookmarkQuery(myBook, 50); ; query = query.next()) {
				final List<Bookmark> thisNoteBookmarks = database.getBookMarks(query);
				if (thisNoteBookmarks.isEmpty()) {
					break;
				}
				for (Bookmark notemark:thisNoteBookmarks){
					if (notemark.getStyleId()==StyleId){
						localBookmarkList.add(notemark);
					}
				}
			}
			database.finalize();
			for (final Bookmark androidBookMark : tmpList) {
				boolean isExit = localBookmarkList.contains(androidBookMark);
				Logger.e("wzp isBookmarkExist:%s%n", isExit + "");
				localBookmarkList.remove(androidBookMark);
				if(androidBookMark.getStyleId() == 1 && tmpList.indexOf(androidBookMark) == 0) {
					androidBookMark.setText(androidBookMark.getText()+"$%^&*");
				}
				if (!isExit) {
					addCloudBookmark(androidBookMark);
				}
			}
			for (Bookmark bookmark:localBookmarkList){
				deleteCloudBookmark(bookmark);
			}
			localBookmarkList.clear();
		}
	}

	class MarkerThread extends Thread{
		private List<BookNoteBean> data;
		public MarkerThread(List<BookNoteBean> data) {
			this.data= data;
		}
		public void run() {
			addBookmark(data,BookmarkerType.BookMarker,0);
			addBookmarkToLocal(data);
		}
	}

	private void addBookmarkToLocal(final List<BookNoteBean> data) {
		if (data == null || data.size() == 0){
			return;
		}
		MarkerDao markerDao = new MarkerDao(DatabaseHelper.getHelper(context));
		markerDao.clearAll();
		if (sharePreferenceUtil == null) {
			sharePreferenceUtil = SharedPreferenceUtil.getInstance();
		}
		int bookId = (int) sharePreferenceUtil.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, -1);
		if (bookId == -1) {
			Logger.e("ljw: 获取当前书籍信息错误");
			return;
		}
		int languageType = sharePreferenceUtil.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, -1);
		if (languageType == -1) {
			Logger.e("ljw: 获取当年书籍语言版本错误");
			return;
		}

		Iterator<BookNoteBean> iterator = data.iterator();
		List<BookInfo> bookInfoList = bookInfoDao.queryByColumn("bookId", bookId);
		List<ShelfBook> shelfBookList = bookDao.queryByColumn("bookId", bookId);
		if (bookInfoList.size() == 0 || shelfBookList.size() == 0) {
			Logger.i("ljw: 同步书签时读取书籍信息有误");
			return;
		}
		int firstChapterId = BookInfoUtils.getBooksFisrtChapterId(bookInfoList.get(0));
		int firstParagraphId = bookInfoList.get(0).getSegmentId();

//                try {
//                    readingChapterId = Integer.parseInt(shelfBookList.get(0).getRelativeChapterId());
//                } catch (NumberFormatException e) {
//                    Logger.e("wzp " + e.getMessage());
//                }
//                //如果解析不到book表中的正在阅读的章节数据，默认这是第一次打开这本书，正在读的章节就是第一章。
//                if(readingChapterId == 0){
//                    readingChapterId = firstChapterId;
//                }
		BookMarker bookMarker = new BookMarker();
		while (iterator.hasNext()) {
			BookNoteBean bean = iterator.next();
			int readingChapterId = bean.getChapterId();
//					int acutalParagraphId = bean.getSegmentId() + readingChapterId - firstChapterId - firstParagraphId;
//					int relativeParagraphId = bean.getSegmentId() - BookInfoUtils.getBeginSegmentId(FBReader.this);
//					int acutalParagraphId = BookInfoUtils.getLocalParagraphText(myFBReaderApp,relativeParagraphId);
			int nativeParargraphId = BookInfoUtils.getLocaParagraphIndex(myFBReaderApp, bean.getSegmentId());
			Logger.e("生成书签时 本地段落nativeParargraphId：%d", nativeParargraphId);
			bookMarker.setBookId(bookId);
			bookMarker.setLanguage(languageType);
			bookMarker.setBookmarkerId(bean.getBookMarkerId());
			bookMarker.setSegmentNum(nativeParargraphId);
			List<BookMarker> tmpBookMarkers = markerDao.queryByColumn("bookmarkerId", bean.getBookMarkerId());
			if (tmpBookMarkers != null && !tmpBookMarkers.isEmpty()) {
				markerDao.update(bookMarker);
			} else {
				markerDao.add(bookMarker);
			}
		}
	}

	/**
	 * 下载所有笔记
	 */
	private void downloadAllNote() {
		if (noteHasDown)
			return;
		long userId = CommonUtil.getUserId();
		final long book_id = sharePreferenceUtil.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, -1);
		if (!CommonUtil.getLoginState() || book_id == -1)
			return;
		OkGo.get(Urls.DOWNBOOKNOTES)
				.params("bookId", book_id)
				.params("userId", userId)
				.execute(new JsonCallback<IycResponse<List<BookNoteBean>>>(context) {
					@Override
					public void onSuccess(IycResponse<List<BookNoteBean>> bookNoteBeanIycResponse, Call call, Response response) {
						if (isNull(bookNoteBeanIycResponse.getData()))
							return;
						noteHasDown = true;
						Logger.e("wzp 下载下来的笔记的内容：%s%n", bookNoteBeanIycResponse.getData().toString());
						setListData(bookNoteBeanIycResponse.getData());
						NoteThread noteThread = new NoteThread(bookNoteBeanIycResponse.getData());
						noteThread.start();

					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
						Logger.e("wzp:" + e.getMessage());
					}

					@Override
					public void parseError(Call call, Exception e) {
						super.parseError(call, e);
						if (downLoadNoteCount < 3) {
							downloadAllNote();
							downLoadNoteCount++;
						}
					}

					@Override
					public void onAfter(IycResponse<List<BookNoteBean>> listIycResponse, Exception e) {
						super.onAfter(listIycResponse, e);
						downloadAllMarker(BookmarkerType.BookMarker);
					}
				});
	}

	class NoteThread extends Thread{
		private List<BookNoteBean> data;
		public NoteThread(List<BookNoteBean> data) {
			this.data= data;
		}
		public void run() {
			addBookmark(data,BookmarkerType.BookNote,1);
			addNoteToLocal(data);
		}
	}

	/**
	 * 将笔记的评论数据存到bookNote表中
	 *
	 * @param list
	 */
	private void addNoteToLocal(final List<BookNoteBean> list) {
		if (list == null || list.size() == 0){
			return;
		}

		if (sharePreferenceUtil == null || bookInfoDao == null || bookDao == null) {
			Logger.e("wzp 无法获取书本信息");
			return;
		}
		int bookId = (int) sharePreferenceUtil.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, -1);
		NoteDao bookNoteDao = new NoteDao(DatabaseHelper.getHelper(context));
		bookNoteDao.deleteByColumn("bookId",bookId);
		List<BookInfo> bookInfoTmpList = bookInfoDao.queryByColumn("bookId", bookId);
		List<ShelfBook> shelfBookTmpList = bookDao.queryByColumn("bookId", bookId);
		if (bookInfoTmpList.size() == 0 || shelfBookTmpList.size() == 0) {
			Logger.e("wzp 无法查询到书本");
			return;
		}
//		int firstChapterId = getBooksFisrtChapterId(bookInfoTmpList.get(0));
//
//		int firstSegementId = bookInfoTmpList.get(0).getSegmentId();
		int language = sharePreferenceUtil.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, -1);
		for (BookNoteBean bean : list) {
			final int nativeParagraphId = BookInfoUtils.getLocaParagraphIndex(myFBReaderApp, bean.getSegmentId());
			Logger.e("添加到笔记中时的nativeParagraphId:%d", nativeParagraphId);
			BookNote localNote = new BookNote();
			localNote.setChapterId(bean.getChapterId());
			localNote.setBookId(bookId);
			localNote.setLanguage(language);
			localNote.setSegmentNum(nativeParagraphId);
			localNote.setNote(bean.getContent());
			localNote.setNoteComment(bean.getNote());
			localNote.setStuCommentsId(bean.getCommentsId());
			localNote.setCommentId(bean.getCommentId());
			localNote.setIscommon(bean.getIscommon());
			bookNoteDao.createOrUpdate(localNote);
		}
	}

	/**
	 * 将List<BookNoteBean>转化为List<Bookmark>
	 *
	 * @param beanList
	 * @param isVisible
	 * @param noteOrMarker BookmarkerType.BookNote表示笔记，BookmarkerType.BookMarker表示书签
	 * @return
	 */
	private List<Bookmark> tranformNoteIntoBookmark(List<BookNoteBean> beanList, boolean isVisible, int noteOrMarker) {
		List<Bookmark> tempList = new ArrayList<>();
		if (beanList == null || beanList.size() == 0)
			return tempList;
		Bookmark bookmark = null;

		long bookId = sharePreferenceUtil.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, -1);
		if (bookId == -1) {
			return null;
		}
		List<ShelfBook> shelfBookList = bookDao.queryByColumn("bookId", bookId);
		String bookName = "";
		if (shelfBookList != null && shelfBookList.size() > 0) {
			bookName = shelfBookList.get(0).getBookName();
		}
		int languageType = sharePreferenceUtil.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 2);
		SQLiteBooksDatabase database = new SQLiteBooksDatabase(this);
//        bookName = bookName.replaceAll("(（(.|\n)+?）)|(\\((.|\n)+?\\))","");
		long zhBookId = database.getBookIdByTitleAndLanguage(1, bookName);
		long enBookId = database.getBookIdByTitleAndLanguage(2, bookName);
		int supportLanguageType = 0;
		if (zhBookId != -1 && enBookId != -1) {
			supportLanguageType = 3;
		} else if (zhBookId != -1) {
			supportLanguageType = 1;
		} else if (enBookId != -1) {
			supportLanguageType = 2;
		}

//        long bookIdTmp = database.getBookIdByTitleAndLanguage(languageType, bookName);
		database.finalize();
		if (supportLanguageType == 0) {
			return null;
		}
		List<BookInfo> bookInfoList = bookInfoDao.queryByColumn("bookId", bookId);
		if (bookInfoList.size() == 0) {
			return null;
		}
		int segmentId = bookInfoList.get(0).getSegmentId();

		for (BookNoteBean note : beanList) {
			try {
				if (note.getLanguagetype() == 1 && (supportLanguageType == 1 || supportLanguageType == 3)) {
					bookmark = tranformNoteIntoBookmark(note, zhBookId, segmentId, isVisible, noteOrMarker);
				} else if (note.getLanguagetype() == 2 && supportLanguageType >= 2) {
					bookmark = tranformNoteIntoBookmark(note, enBookId, segmentId, isVisible, noteOrMarker);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Logger.e("wzp 生成的bookmark是否是null:%s%n", (bookmark == null) + "");
			if (bookmark != null) {
				tempList.add(bookmark);
			}
		}
		return tempList;
	}

	private Bookmark createBookmark(Book book, int paragraphId) {
		return new Bookmark(getCollection(), book, "", new EmptyTextSnippet(new ZLTextFixedPosition(paragraphId, 0, 0)), false);
	}

	public void addBookmark(Book book, int paragraphId) {
		if (myFBReaderApp != null)
			getCollection().saveBookmark(myFBReaderApp.createNewBookmark(paragraphId, false));
	}

	/**
	 * 将从后台获取到的BookNoteBean转换成Bookmark类
	 *
	 * @param bean
	 * @param bookIdTmp
	 * @param segmentId
	 * @param isVisible
	 * @param noteOrMarker BookmarkerType.BookNote表示笔记，BookmarkerType.BookMarker表示书签   @return
	 */
	private Bookmark tranformNoteIntoBookmark(BookNoteBean bean, long bookIdTmp, int segmentId, boolean isVisible, int noteOrMarker) {
		if (bean == null) {
			Logger.e("wzp BookNoteBean为空");
			return null;
		}
		int nativeParagraphId = BookInfoUtils.getLocaParagraphIndex(myFBReaderApp, bean.getSegmentId());
		Logger.e("添加到book表时生成的nativeParagraphId:%d", nativeParagraphId);
		String uid = UUID.randomUUID().toString();
		String verionUid = UUID.randomUUID().toString();
		int start_word_index = bean.getStartOffset();
		int end_word_index = bean.getEndOffset();
		Logger.e("wzp 下载生成偏移量,下载偏移起始：%d%n,下载偏移末尾：%d%n",end_word_index, start_word_index);
		int[] offsets = null;
		int languageType = sharePreferenceUtil.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 2);
		FBView fbView = myFBReaderApp.getTextView();
		String modelId;
		if (fbView == null || fbView.getModel() == null) {
			modelId = null;
		} else {
			modelId = fbView.getModel().getId();
		}
		int myStyleId = getCollection().getDefaultHighlightingStyleId();
		offsets = BookInfoUtils.getOffsets(myFBReaderApp, nativeParagraphId, start_word_index, end_word_index,bean.getContent(), languageType);
		Logger.e("wzp 下载生成转换偏移量,下载偏移起始：%d%n,下载偏移末尾：%d%n", offsets[0], offsets[1]);

		Date date = new Date();
		return new Bookmark(-1, uid, verionUid, bookIdTmp, "", bean.getContent(), TextUtils.isEmpty(bean.getNote()) ? "i_am_a_bookmark" : bean.getNote(), date.getTime(),
				null, null, modelId, nativeParagraphId, offsets[0], 0, nativeParagraphId, offsets[1], 1, isVisible, noteOrMarker == BookmarkerType.BookNote ? 1 : 0);
	}

	public int getCurrentCursor() {
		final ZLTextWordCursor cursor = myFBReaderApp.getTextView().getStartCursor();
		return cursor.getParagraphIndex();
	}

	public int getStatusBarHeight() {
		int barHeight = 0;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			barHeight = getResources().getDimensionPixelSize(resourceId);
		}
		return barHeight;
	}

	private boolean isFirstRead() {
		boolean isFirst = sharePreferenceUtil.getBoolean(SharedPreferenceUtil.IS_FIRST_READ, true);
		if (isFirst) {
			sharePreferenceUtil.putBoolean(SharedPreferenceUtil.IS_FIRST_READ, false);
		}
		return isFirst;
	}

	public boolean isSelectionPopShowing() {
		final FBReaderApp.PopupPanel popup = myFBReaderApp.getActivePopup();
		if (popup != null && popup.getId().equals(SelectionPopup.ID)) return true;
		return false;
	}

	@Override
	public void setWindowTitle(final String title) {
		runOnUiThread(new Runnable() {
			public void run() {
				setTitle(title);
			}
		});
	}

	// methods from ZLApplicationWindow interface
	@Override
	public void showErrorMessage(String key) {
		UIMessageUtil.showErrorMessage(this, key);
	}

	@Override
	public void showErrorMessage(String key, String parameter) {
		UIMessageUtil.showErrorMessage(this, key, parameter);
	}

	@Override
	public FBReaderApp.SynchronousExecutor createExecutor(String key) {
		return UIUtil.createExecutor(this, key);
	}

	@Override
	public void processException(Exception exception) {
		exception.printStackTrace();

		final Intent intent = new Intent(
				FBReaderIntents.Action.ERROR,
				new Uri.Builder().scheme(exception.getClass().getSimpleName()).build()
		);
		intent.setPackage(FBReaderIntents.DEFAULT_PACKAGE);
		intent.putExtra(ErrorKeys.MESSAGE, exception.getMessage());
		final StringWriter stackTrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(stackTrace));
		intent.putExtra(ErrorKeys.STACKTRACE, stackTrace.toString());
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			// ignore
			e.printStackTrace();
		}
	}

	@Override
	public void refresh() {
		runOnUiThread(new Runnable() {
			public void run() {
				for (Map.Entry<MenuItem, String> entry : myMenuItemMap.entrySet()) {
					final String actionId = entry.getValue();
					final MenuItem menuItem = entry.getKey();
					menuItem.setVisible(myFBReaderApp.isActionVisible(actionId) && myFBReaderApp.isActionEnabled(actionId));
					switch (myFBReaderApp.isActionChecked(actionId)) {
						case TRUE:
							menuItem.setCheckable(true);
							menuItem.setChecked(true);
							break;
						case FALSE:
							menuItem.setCheckable(true);
							menuItem.setChecked(false);
							break;
						case UNDEFINED:
							menuItem.setCheckable(false);
							break;
					}
				}
			}
		});
	}


	@Override
	public ZLViewWidget getViewWidget() {
		return myMainView;
	}

	@Override
	public void close() {
		finish();
	}

	@Override
	public int getBatteryLevel() {
		return myBatteryLevel;
	}

	private void setBatteryLevel(int percent) {
		myBatteryLevel = percent;
	}

	private interface BookmarkerType {
		int BookMarker = 1;
		int BookNote = 2;
	}

	private class TipRunner extends Thread {
		TipRunner() {
			setPriority(MIN_PRIORITY);
		}

		public void run() {
			final TipsManager manager = new TipsManager(Paths.systemInfo(FBReader.this));
			switch (manager.requiredAction()) {
				case Initialize:
					startActivity(new Intent(
							TipsActivity.INITIALIZE_ACTION, null, FBReader.this, TipsActivity.class
					));
					break;
				case Show:
					startActivity(new Intent(
							TipsActivity.SHOW_TIP_ACTION, null, FBReader.this, TipsActivity.class
					));
					break;
				case Download:
					manager.startDownloading();
					break;
				case None:
					break;
			}
		}
	}


}
