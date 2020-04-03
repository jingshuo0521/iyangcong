package com.iyangcong.reader.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.iyangcong.reader.MainActivity;
import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.DiscoverReviewAdapter;
import com.iyangcong.reader.bean.BaseBook;
import com.iyangcong.reader.bean.BookDownloadUrl;
import com.iyangcong.reader.bean.BookType;
import com.iyangcong.reader.bean.MarketBookDetails;
import com.iyangcong.reader.bean.MarketBookDetailsSameBooks;
import com.iyangcong.reader.bean.MineShoppingBookIntroduction;
import com.iyangcong.reader.bean.MonthlyMarketBookListItem;
import com.iyangcong.reader.bean.ReadingRecorder;
import com.iyangcong.reader.bean.ShelfBook;
import com.iyangcong.reader.callback.FileCallback;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.BookDao;
import com.iyangcong.reader.epub.EpubProcessResult;
import com.iyangcong.reader.interfaceset.OnItemClickedListener;
import com.iyangcong.reader.interfaceset.RetryCounter;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.RatingBar;
import com.iyangcong.reader.ui.TagGroup;
import com.iyangcong.reader.ui.button.FlatButton;
import com.iyangcong.reader.ui.dialog.BookDetailsMoreDialog;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.DateUtils;
import com.iyangcong.reader.utils.DialogUtils;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.IntentUtils;
import com.iyangcong.reader.utils.InvokerDESServiceUitls;
import com.iyangcong.reader.utils.ShareUtils;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.iyangcong.reader.utils.antiShake.AntiShake;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.orhanobut.logger.Logger;

import org.geometerplus.android.fbreader.FBReader;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import static com.iyangcong.reader.utils.Urls.HAVESENTENCE;
import static com.iyangcong.reader.utils.Urls.PersonShopcartURL;
import static com.iyangcong.reader.utils.Urls.READINGRECORDS;

//import com.iyangcong.reader.handler.EpubDecryptHandler;

/**
 * Created by lg on 2016/12/25.
 */

public class BookMarketBookDetailsActivity extends SwipeBackActivity {

	boolean BookIntroduceisExpand = false;
	boolean AuthorIntroduceisExpand = false;
	int durationMillis = 350;//动画持续时间
	int maxDescripLine = 5; //TextView默认最大展示行数
	int bookId = 0;
	String bookName;
	@BindView(R.id.rv_comment)
	RecyclerView rvComment;
	@BindView(R.id.btnBack)
	ImageButton btnBack;
	@BindView(R.id.textHeadTitle)
	TextView textHeadTitle;
	@BindView(R.id.btnFunction)
	ImageButton btnFunction;
	@BindView(R.id.iv_book_details_image)
	ImageView ivBookDetailsImage;
	@BindView(R.id.tv_book_details_tittle)
	TextView tvBookDetailsTittle;
	@BindView(R.id.rb_level)
	RatingBar rbLevel;
	@BindView(R.id.tv_book_details_title_english)
	TextView tvBookDetailsTitleEnglish;
	@BindView(R.id.tv_book_details_language)
	TextView tvBookDetailsLanguage;
	@BindView(R.id.tv_book_details_difficulty)
	TextView tvBookDetailsDifficulty;
	@BindView(R.id.tv_book_details_author_name)
	TextView tvBookDetailsAuthorName;
	@BindView(R.id.tv_book_details_translator_name)
	TextView tvBookDetailsTranslatorName;
	@BindView(R.id.tv_book_details_price)
	TextView tvBookDetailsPrice;
	@BindView(R.id.tv_book_details_book_introduce)
	TextView tvBookDetailsBookIntroduce;
	@BindView(R.id.im_book_details_book_introduce_more)
	ImageView imBookDetailsBookIntroduceMore;
	@BindView(R.id.tv_book_details_author_introduce)
	TextView tvBookDetailsAuthorIntroduce;
	@BindView(R.id.im_book_details_author_introduce_more)
	ImageView imBookDetailsAuthorIntroduceMore;
	@BindView(R.id.rl_introduction_type)
	TagGroup rlIntroductiontype;
	@BindView(R.id.tv_see_more_comment)
	TextView tvSeeMoreComment;
	@BindView(R.id.relative_see_more_comment)
	RelativeLayout relativeSeeMoreComment;
	@BindView(R.id.gv_similar_recommended_book)
	GridView gvSimilarRecommendedBook;
	@BindView(R.id.tv_read_percentage)
	TextView tvReadPercentage;
	@BindView(R.id.iv_book_details_help)
	ImageView ivBookDetailHelp;
	@BindView(R.id.bt_try_read)
	FlatButton btTryRead;
	@BindView(R.id.bt_buy)
	FlatButton btBuy;
	@BindView(R.id.layout_header)
	LinearLayout layoutHeader;
	@BindView(R.id.iv_ring)
	ImageView ivRing;
	@BindView(R.id.tv_bar_title)
	TextView tvBarTitle;
	@BindView(R.id.tv_more)
	TextView tvMore;
	@BindView(R.id.iv_more)
	ImageView ivMore;
	@BindView(R.id.iv_bar_divide)
	View ivBarDivide;
	@BindView(R.id.rl_discover_bar)
	RelativeLayout rlDiscoverBar;
	@BindView(R.id.im_comment_edit)
	ImageView imCommentEdit;
	@BindView(R.id.tv_book_kind)
	TextView tvBookKind;
	@BindView(R.id.ll_book_details)
	LinearLayout llBookDetails;
	@BindView(R.id.tv_book_details_book_default)
	TextView tvBookDetailsBookDefault;
	@BindView(R.id.tv_book_details_author_default)
	TextView tvBookDetailsAuthorDefault;
	@BindView(R.id.iv_ring_type)
	ImageView ivRingType;
	@BindView(R.id.ll_layout_sofa)
	LinearLayout llLayoutSofa;
	@BindView(R.id.btnFunction1)
	ImageButton btnFunction1;
	@BindView(R.id.view_exchange_blank)
	View viewExchangeBlank;
	@BindView(R.id.bt_exchange)
	FlatButton btExchange;
	@BindView(R.id.view_left_blank)
	View viewLeftBlank;
	@BindView(R.id.view_right_blank)
	View viewRightBlank;
	@BindView(R.id.sv_bookDetail)
	ScrollView scrollView;
	@BindView(R.id.tv_book_details_origin_price)
	TextView tvBookDetailsOriginPrice;
	@BindView(R.id.tv_goods_num1)
	TextView tvGoodsNum1;
	@BindView(R.id.tv_version)
	TextView tvVersion;
	@BindView(R.id.tv_book_number)
	TextView tvBookNumber;
	@BindView(R.id.tv_book_text_length)
	TextView tvBookTextLength;
	@BindView(R.id.tv_out_time)
	TextView tvOutTime;
	@BindView(R.id.ll_book_info)
	LinearLayout llBookInfo;
	@BindView(R.id.tv_book_has_removed)
	TextView tvBookHasRemoved;
	@BindView(R.id.ll_below_info)
	LinearLayout llBelowInfo;
	@BindView(R.id.ll_copyright)
	LinearLayout llCopyright;

	private boolean isBookDetail = true;
	private int collectState;
	/**
	 * supportLanguage: 1中文  2英文 3中/英文 4句对
	 */
	private int supportLanguage;
	private boolean isNotCollect = false;
	private ShelfBook shelfBook = new ShelfBook();
	/**
	 * 书籍状态:    0:未下载   1:已下载
	 */
	private int bookState = BookType.BookDownloadState.NOT_DOWNLOADED;
	/**
	 * 图书状态 1.上架  2.未上架  3.政治敏感  4.内容错误  5.版权问题
	 * 如果status：
	 * 1正常
	 * 4,5还可以下载阅读
	 * 2,3什么按钮都没有
	 */
	private int bookStatus = 1;
	/**
	 * -1:未购买   0:已购买   -2:已添加到购物车
	 */
	private int hasBuy = BookType.BookBuyState.NOT_BUY;
	private boolean isSupportAndroid;
	/**
	 * 只显示上架书籍
	 */
	private boolean isShowToUser;
	//    private EpubKernel epubKernel = null;
//	private EpubDecryptHandler epubHandler;
	private Handler epubHandler;
	private SharedPreferenceUtil sharedPreferenceUtil;
	private BookDetailsMoreDialog mDialog;
	private double displayPrice;
	private int bookVirtualCoin;
	private BookDao bookDao;
	private ShareUtils shareUtils;
	private AntiShake util = new AntiShake();
	private int paymentId;//包月包id
	private List<String> hotSearchList = new ArrayList<>();
	private int getBookStateCount = 0;

	private void setBtTryReadEnabled(final boolean clickable) {
		if (btTryRead.getVisibility() == View.VISIBLE && btTryRead.isEnabled() != clickable) {
			btTryRead.setEnabled(clickable);
		}
	}

//	@Override
//	public void onServiceDisconnected(ComponentName componentName) {
//		mEpubLibrary = null;
//	}

	@OnClick({R.id.relative_see_more_comment, R.id.tv_see_more_comment, R.id.iv_book_details_help, R.id.tv_book_details_difficulty,/* R.id.tv_book_details_author_name,*/ R.id.tv_book_details_author_introduce, R.id.im_book_details_author_introduce_more, R.id.tv_book_details_book_introduce, R.id.im_book_details_book_introduce_more, R.id.bt_try_read, R.id.bt_buy, R.id.im_comment_edit, R.id.btnFunction, R.id.btnFunction1, R.id.bt_exchange})

	public void onClick(View view) {

		if (util.check()) {
			return;
		}
		switch (view.getId()) {
			case R.id.tv_book_details_author_introduce:
			case R.id.im_book_details_author_introduce_more:
				if (tvBookDetailsAuthorIntroduce.getText().length() >= 100) {
					showMoreDetail(tvBookDetailsAuthorIntroduce, imBookDetailsAuthorIntroduceMore, true);
				}
				break;
			case R.id.tv_book_details_book_introduce:
			case R.id.im_book_details_book_introduce_more:
				if (tvBookDetailsBookIntroduce.getText().length() >= 100) {
					showMoreDetail(tvBookDetailsBookIntroduce, imBookDetailsBookIntroduceMore, false);
				}
				break;
			case R.id.im_comment_edit:
				if (CommonUtil.getLoginState()) {
					Intent intentCommont = new Intent(BookMarketBookDetailsActivity.this, CommentEditActivity.class);
					intentCommont.putExtra("bookId", bookId);
					startActivityForResult(intentCommont, 0);
				} else {
					startActivity(new Intent(BookMarketBookDetailsActivity.this, LoginActivity.class));
				}
				break;
			case R.id.bt_buy:
				if (CommonUtil.getLoginState()) {
					showLoadingDialog();
					if (displayPrice > 0) {
						getDataFromNetWork1();
//						IntentUtils.goToPayActivity(this, bookId + "", displayPrice + "", displayPrice, 1);
//                        BigDecimal bigDecimal = new BigDecimal(displayPrice);
//                        Intent intent = new Intent(BookMarketBookDetailsActivity.this, MinePayActivity.class);
//                        intent.putExtra("bookIds", bookId + "");
//                        intent.putExtra("totalPrice", bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
//                        intent.putExtra("count", 1);
//                        startActivity(intent);
//						finish();
					} else if (displayPrice == 0) {
						userGetFreeBook();
					}
				} else {
					startActivity(new Intent(BookMarketBookDetailsActivity.this, LoginActivity.class));
				}
				break;
			case R.id.bt_exchange:
				exchangeDialog();
				break;
			case R.id.bt_try_read:
//				File file1 = new File(CommonUtil.getBooksDir());
//                File[] listFiles1 = file1.listFiles();
//                File file2 = new File(CommonUtil.getBooksDir() + bookId);
//                File[] listFiles2 = file2.listFiles();
				if (CommonUtil.getLoginState()) {
//				if (CommonUtil.getLoginState() || bookId == 53) {
					if (bookState == BookType.BookDownloadState.NOT_DOWNLOADED||
							(!CommonUtil.fileIsExists(CommonUtil.getBooksDir() + bookId + "/" + bookId + ".zh.epub")&&
							!CommonUtil.fileIsExists(CommonUtil.getBooksDir() + bookId + "/" + bookId + ".en.epub"))){
							//如果书籍还没有下载，试读和完整版的都没有下载
							if (btTryRead.getText().toString().equals(getResources().getString(R.string.ok_read))) {
								startDownLoadTask(2);
							} else if (btTryRead.getText().toString().equals(getResources().getString(R.string.try_read))) {
								shelfBook.setBookType(BookType.TRY_READ_BOOK);
								startDownLoadTask(1);
							}
						}else if(bookState == BookType.BookDownloadState.HAS_DOWNLOADED
								&& shelfBook.getBookType() == BookType.TRY_READ_BOOK && hasBuy == BookType.BookBuyState.HAS_BOUGHT){
							//如果书籍下载了，但是是试读版的,并且已经购买了这本书
							startDownLoadTask(2);
						}else {

							List<ShelfBook> shelfBookList = bookDao.queryByColumn("bookId", bookId);
							if (shelfBookList != null && shelfBookList.size() > 0) {
								ShelfBook shelfBook = shelfBookList.get(0);
								getReadingRecordFromNetwork(shelfBook);
							} else {
								ToastCompat.makeText(this, "打开书籍出错", Toast.LENGTH_SHORT).show();
							}

//                        //这里打开书籍并开始阅读
//                        Intent intent = new Intent(this, FBReader.class);
//                        List<ShelfBook> shelfBookList = bookDao.queryByColumn("bookId", bookId);
//                        if (shelfBookList != null && shelfBookList.size() > 0) {
//                            ShelfBook shelfBook = shelfBookList.get(0);
//                            if (shelfBook.getRecentReadingLanguageType() == 1 || supportLanguage == 1) {
//                                intent.putExtra("bookPath", shelfBook.getBookPath() + bookId + ".zh.epub");
//                                startActivity(intent);
//                                sharedPreferenceUtil.putInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 1);
//                                sharedPreferenceUtil.putLong(SharedPreferenceUtil.CURRENT_BOOK_ID, bookId);
//                            } else if (supportLanguage == 2 || (supportLanguage == 3 && shelfBook.getRecentReadingLanguageType() != 1)) {
//                                intent.putExtra("bookPath", shelfBook.getBookPath() + bookId + ".en.epub");
//                                startActivity(intent);
//                                sharedPreferenceUtil.putInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 2);
//                                sharedPreferenceUtil.putLong(SharedPreferenceUtil.CURRENT_BOOK_ID, bookId);
//                            } else {
//                                ToastCompat.makeText(this, "未找到对应书籍！", 1000);
//                            }
//                        } else {
//                            if (supportLanguage == 1) {
//                                intent.putExtra("bookPath", shelfBook.getBookPath() + bookId + ".zh.epub");
//                                startActivity(intent);
//                                sharedPreferenceUtil.putInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 1);
//                                sharedPreferenceUtil.putLong(SharedPreferenceUtil.CURRENT_BOOK_ID, bookId);
//                            } else if (supportLanguage == 2 || supportLanguage == 3) {
//                                intent.putExtra("bookPath", shelfBook.getBookPath() + bookId + ".en.epub");
//                                startActivity(intent);
//                                sharedPreferenceUtil.putInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 2);
//                                sharedPreferenceUtil.putLong(SharedPreferenceUtil.CURRENT_BOOK_ID, bookId);
//                            } else {
//                                ToastCompat.makeText(this, "未找到对应书籍！", 1000);
//                            }
//                        }
					}
				} else {
					startActivity(new Intent(BookMarketBookDetailsActivity.this, LoginActivity.class));
				}
				break;
			case R.id.tv_see_more_comment:
			case R.id.relative_see_more_comment:
				Intent intent = new Intent(this, ReviewListAcitivity.class);
				intent.putExtra(Constants.BOOK_ID, bookId);
				intent.putExtra(Constants.BOOK_NAME, bookName);
				startActivity(intent);
				break;
			case R.id.btnFunction:
				mDialog.show();
				break;
			case R.id.btnFunction1:
				if (CommonUtil.getLoginState()) {
					if (hasBuy == BookType.BookBuyState.ADD_TO_CHART) {
						startActivity(new Intent(BookMarketBookDetailsActivity.this, MineShoppingActivity.class));
//                        ToastCompat.makeText(context, getString(R.string.added_shoppingcart), Toast.LENGTH_SHORT).show();
					} else {
						showLoadingDialog();
						addBookToShoppingCart(bookId);
					}
				} else {
					startActivity(new Intent(BookMarketBookDetailsActivity.this, LoginActivity.class));
				}
				break;
		}
	}

	private void showMoreDetail(final TextView textView, final ImageView imageView, boolean isAuthorIntroduce) {
		boolean b;
		if (isAuthorIntroduce) {
			AuthorIntroduceisExpand = !AuthorIntroduceisExpand;
			b = AuthorIntroduceisExpand;
		} else {
			BookIntroduceisExpand = !BookIntroduceisExpand;
			b = BookIntroduceisExpand;
		}
		textView.clearAnimation();//清楚动画效果
		final int AuthorIntroducedeltaValue;
		final int AuthorIntroducestartValue = textView.getHeight();//起始高度

		if (b) {
			/**
			 * 折叠动画
			 * 从实际高度缩回起始高度
			 */
			AuthorIntroducedeltaValue = textView.getLineHeight() * textView.getLineCount() - AuthorIntroducestartValue;
			RotateAnimation animationAI = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			animationAI.setDuration(durationMillis);
			animationAI.setFillAfter(true);
			imageView.startAnimation(animationAI);
		} else {
			/**
			 * 展开动画
			 * 从起始高度增长至实际高度
			 */
			AuthorIntroducedeltaValue = textView.getLineHeight() * maxDescripLine - AuthorIntroducestartValue;
			RotateAnimation animationAI = new RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			animationAI.setDuration(durationMillis);
			animationAI.setFillAfter(true);
			imageView.startAnimation(animationAI);
		}
		Animation animationAI = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				textView.setHeight((int) (AuthorIntroducestartValue + AuthorIntroducedeltaValue * interpolatedTime));
			}
		};
		animationAI.setDuration(durationMillis);
		textView.startAnimation(animationAI);
	}

	public void userGetFreeBook() {
		showLoadingDialog();
		OkGo.get(Urls.BookFreeGetURL)
				.tag(this)
				.params("userId", CommonUtil.getUserId())
				.params("bookId", bookId)
				.execute(new JsonCallback<IycResponse<freeGetstatus>>(this) {
					@Override
					public void onSuccess(IycResponse<freeGetstatus> freeGetstatusIycResponse, Call call, Response response) {
						dismissLoadingDialig();
						buyedUIChange();
						tvReadPercentage.setVisibility(View.INVISIBLE);
						if (freeGetstatusIycResponse.getData().getBuystatus() == 0) {
							ToastCompat.makeText(context, getString(R.string.have_getted), Toast.LENGTH_SHORT).show();
							hasBuy = BookType.BookBuyState.HAS_BOUGHT;
						} else {
							ToastCompat.makeText(context, getString(R.string.free_get_success), Toast.LENGTH_SHORT).show();
							hasBuy = BookType.BookBuyState.HAS_BOUGHT;
						}
						isDownload();
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						dismissLoadingDialig();
						ToastCompat.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				});
	}

	private void exchangeDialog() {
		final NormalDialog dialog = new NormalDialog(context);
		dialog.content("是否使用积分兑换图书？")
				.btnNum(2)
				.btnText("确定", "取消")
				.isTitleShow(false)
				.show();

		dialog.setOnBtnClickL(
				new OnBtnClickL() {
					@Override
					public void onBtnClick() {
						userVirtualCoinForBook();
						dialog.dismiss();
					}
				},
				new OnBtnClickL() {
					@Override
					public void onBtnClick() {
						dialog.dismiss();
					}
				});

	}

	public void startDownLoadTask(int type) {
		OkGo.get(Urls.FileDownloadURL)
				.tag(this)
				.params("bookid", bookId)
				.params("type", type + "")
				.params("userid", CommonUtil.getUserId())
				.execute(new JsonCallback<IycResponse<BookDownloadUrl>>(this) {

					@Override
					public void onSuccess(IycResponse<BookDownloadUrl> stringIycResponse, Call call, Response response) {
						BookDownloadUrl bookDownloadUrl = stringIycResponse.getData();
						String resourceUrl = bookDownloadUrl.getResourceUrl();
						String imageUrl = bookDownloadUrl.getImgUrl();
						doDownloadTask(resourceUrl, imageUrl);
						tvBookKind.setVisibility(View.INVISIBLE);
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						ToastCompat.makeText(BookMarketBookDetailsActivity.this, "获取下载链接失败", 1000);
					}
				});
	}

	/**
	 * 同步阅读进度
	 */
	private void getReadingRecordFromNetwork(final ShelfBook bookData) {
		long userId = CommonUtil.getUserId();
		if (!CommonUtil.getLoginState()) {
			intentToFBreader(bookData);
			return;
		}
		showLoadingDialog("图书加载中");
		OkGo.get(READINGRECORDS)
				.params("bookid", bookData.getBookId())
				.params("userid", userId)
				.params("languageType", bookData.getRecentReadingLanguageType())
				.execute(new JsonCallback<IycResponse<List<ReadingRecorder>>>(this) {
					@Override
					public void onSuccess(IycResponse<List<ReadingRecorder>> readingRecorderIycResponse, Call call, Response response) {
						if (!isNull(readingRecorderIycResponse) && !isNull(readingRecorderIycResponse.getData())) {
							final ReadingRecorder readingRecorder = readingRecorderIycResponse.getData().get(0);
							final NormalDialog normalDialog = new NormalDialog(BookMarketBookDetailsActivity.this);
							float newProgress = readingRecorder.getPercent() * 100;
							float oldProgress = bookData.getDownloadProgress();
							long oldReadTime = bookData.getTimeStamp()/1000;
							long newReadTime = (readingRecorder.getLeaveTime()==null?0:readingRecorder.getLeaveTime())/1000;
							float num = newProgress - oldProgress;
							bookData.setRecentReadingLanguageType(readingRecorder.getLanguageType()==0?2:readingRecorder.getLanguageType());
//							if (num < 1 && num > -1) {
							if(newReadTime-3 <= oldReadTime){
								intentToFBreader(bookData);
							} else {
								DialogUtils.setAlertDialogNormalStyle(normalDialog, "提示", "此书在其他终端有新的阅读进度,是否同步？");
								normalDialog.btnText("取消", "同步");
								normalDialog.setOnBtnClickL(new OnBtnClickL() {
									@Override
									public void onBtnClick() {
										normalDialog.dismiss();
										intentToFBreader(bookData);
									}
								}, new OnBtnClickL() {
									@Override
									public void onBtnClick() {
										normalDialog.dismiss();
										intentToFBreader(bookData, readingRecorder.getSegmentId(), readingRecorder.getChapterId(), readingRecorder.getSegmentStr());
									}
								});
							}
						}
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
						intentToFBreader(bookData);
						Logger.i(e.getMessage());
					}

					@Override
					public void onAfter(IycResponse<List<ReadingRecorder>> readingRecorderIycResponse, Exception e) {
						super.onAfter(readingRecorderIycResponse, e);
						if (isNull(readingRecorderIycResponse) || isNull(readingRecorderIycResponse.getData())) {
							intentToFBreader(bookData);
						}
					}
				});
	}

	private void addBookToShoppingCart(int bookId) {
		OkGo.get(Urls.BookMarketAddToShoppingCartURL)
				.params("bookid", bookId)
				.params("userid", CommonUtil.getUserId())
				.execute(new JsonCallback<IycResponse<ShoppingInfo>>(this) {

					@Override
					public void onSuccess(IycResponse<ShoppingInfo> ycResponse, Call call, Response response) {
						dismissLoadingDialig();
						btnFunction1.setImageResource(R.drawable.ic_shopping);
						hasBuy = BookType.BookBuyState.ADD_TO_CHART;
						hasBuyUI();
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						dismissLoadingDialig();
						ToastCompat.makeText(BookMarketBookDetailsActivity.this, "添加到购物车失败，请重新添加", 1000);
					}
				});
	}

	public void buyedUIChange() {
		btTryRead.setVisibility(View.VISIBLE);
		btBuy.setVisibility(View.GONE);
		viewLeftBlank.setVisibility(View.VISIBLE);
		viewRightBlank.setVisibility(View.VISIBLE);
		btTryRead.setText(R.string.ok_read);
		btnFunction1.setVisibility(View.GONE);
		btExchange.setVisibility(View.GONE);
		viewExchangeBlank.setVisibility(View.GONE);
	}

	private void isDownload() {
		List<ShelfBook> shelfBookList = bookDao.queryByColumn("bookId", shelfBook.getBookId());
		ShelfBook book;
		int size = 0;
		if (shelfBookList != null) {
			size = shelfBookList.size();
		}
		if (size > 0) {
			book = shelfBookList.get(0);
			shelfBook.setBookType(book.getBookType());
			if ((book.getBookType() == BookType.TRY_READ_BOOK || book.getBookType() == BookType.FREE_BOOK) && hasBuy != BookType.BookBuyState.HAS_BOUGHT) {
				bookState = BookType.BookDownloadState.HAS_DOWNLOADED;
				tvBookKind.setVisibility(View.VISIBLE);
				tvBookKind.setText("已下载");
			} else if ((book.getBookType() == BookType.TRY_READ_BOOK || book.getBookType() == BookType.FREE_BOOK) && hasBuy == BookType.BookBuyState.HAS_BOUGHT) {
//				BookInfoDao bookInfoDao = new BookInfoDao(DatabaseHelper.getHelper(context));
//				if (bookInfoDao != null) {
//					List<BookInfo> bookInfoList = bookInfoDao.queryByColumn("bookId", shelfBook.getBookId());
//					for (BookInfo bookInfo : bookInfoList) {
//						bookInfoDao.delete(bookInfo);
//					}
//				}
//				bookDao.delete(book);
				bookState = BookType.BookDownloadState.HAS_DOWNLOADED;
				tvBookKind.setVisibility(View.VISIBLE);
				tvBookKind.setText("未下载");
			} else {
				bookState = BookType.BookDownloadState.HAS_DOWNLOADED;
				tvBookKind.setVisibility(View.VISIBLE);
				tvBookKind.setText("已下载");
			}
		} else {
			bookState = BookType.BookDownloadState.NOT_DOWNLOADED;
			tvBookKind.setVisibility(View.VISIBLE);
			tvBookKind.setText("未下载");
		}
		if(bookStatus != 1){
			tvBookKind.setText("已下架");
			tvBookKind.setVisibility(View.VISIBLE);
		}
	}

	public void userVirtualCoinForBook() {
		showLoadingDialog();
		OkGo.get(Urls.BookGetByVirtualCoin)
				.tag(this)
				.params("bookId", bookId)
				.params("userId", CommonUtil.getUserId())
				.params("virtualCoin", bookVirtualCoin)
				.execute(new JsonCallback<IycResponse<String>>(this) {
					@Override
					public void onSuccess(IycResponse<String> stringIycResponse, Call call, Response response) {
						dismissLoadingDialig();
						hasBuy = BookType.BookBuyState.HAS_BOUGHT;
						buyedUIChange();
						isDownload();
						ToastCompat.makeText(context, stringIycResponse.getMsg() + "成功", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						dismissLoadingDialig();
						ToastCompat.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
					}
				});
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void epubProcessFinish(EpubProcessResult result) {
		dismissLoadingDialig();
		if (result.isSuccessful()) {
			isDownload();
			setBtTryReadEnabled(true);
			List<ShelfBook> shelfBookList = bookDao.queryByColumn("bookId", bookId);
			if (shelfBookList != null && shelfBookList.size() > 0) {
				ShelfBook shelfBook = shelfBookList.get(0);
				getReadingRecordFromNetwork(shelfBook);
			} else {
				ToastCompat.makeText(BookMarketBookDetailsActivity.this, "打开书籍出错", 1000);
			}
		}else{
				ToastCompat.makeText(this,getString(R.string.unzip_failled), Toast.LENGTH_SHORT).show();
		}
	}

	private void doDownloadTask(String resourceUrl, String imageUrl) {
		tvReadPercentage.setVisibility(View.VISIBLE);
		tvReadPercentage.setText("0%");
		setBtTryReadEnabled(false);

		OkGo.get(resourceUrl)
				.tag(this)
				.execute(new FileCallback(bookId + ".zip") {
					@Override
					public void onBefore(BaseRequest request) {
						super.onBefore(request);
					}

					@Override
					public void onSuccess(File file, Call call, Response response) {
//						ToastCompat.makeText(BookMarketBookDetailsActivity.this, "", 1000);
						tvReadPercentage.setVisibility(View.INVISIBLE);
						tvBookKind.setVisibility(View.VISIBLE);
						tvBookKind.setText("已下载");
						bookState = BookType.BookDownloadState.HAS_DOWNLOADED;
						setBookType();
						shelfBook.setBookState("未读");
						shelfBook.setTimeStamp(System.currentTimeMillis());
						shelfBook.setBookImageUrl(CommonUtil.getBooksDir() + "/image/" + bookId);
						shelfBook.setSupportLanguage(supportLanguage);
						shelfBook.setBookPath(CommonUtil.getBooksDir() + bookId + "/");
						List<ShelfBook> tmpShelfBooks = bookDao.queryByColumn("bookId",bookId);
						if(tmpShelfBooks.isEmpty()){
							bookDao.add(shelfBook);
						}else{
							shelfBook.setId(tmpShelfBooks.get(0).getId());
							bookDao.update(shelfBook);
						}
//                        if (epubHandler == null) {
//                            epubHandler = new EpubDecryptHandler(BookMarketBookDetailsActivity.this, bookId);
//                        }
//                        epubHandler.decryptEpub();
						showLoadingDialog(getString(R.string.unzipping));
//						setBtTryReadEnabled(false);
						InvokerDESServiceUitls invoker = new InvokerDESServiceUitls(BookMarketBookDetailsActivity.this);
						invoker.invokerDESEncodeService(bookId);
//                        Intent intent = new Intent(BookMarketBookDetailsActivity.this,EpubService.class);
//                        String bookNameTmp = bookId+".zip";
//                        intent.putExtra(Constants.EPUB_BOOK_NAME,bookNameTmp);
//                        bindService(intent,BookMarketBookDetailsActivity.this,Context.BIND_AUTO_CREATE);
					}

					@Override
					public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
						Logger.e("wzp totalSize:%d", totalSize);
						tvReadPercentage.setText((int) (Math.round(progress * 10000) * 1 / 100) + "%");
					}

					@Override
					public void onAfter(File file, Exception e) {
						super.onAfter(file, e);
					}
				});

		OkGo.get(imageUrl)
				.tag(this)
				.execute(new FileCallback(CommonUtil.getBooksDir() + "/image/", bookId + "") {
					@Override
					public void onBefore(BaseRequest request) {
						super.onBefore(request);
					}

					@Override
					public void onSuccess(File file, Call call, Response response) {
					}

					@Override
					public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
					}
				});
	}

	//没有同步到阅读进度时执行这个方法
	private void intentToFBreader(ShelfBook bookData) {
		OkGo.get(HAVESENTENCE)
				.tag(BookMarketBookDetailsActivity.this)
				.params("bookId", bookData.getBookId())
				.execute(new JsonCallback<IycResponse<String>>(BookMarketBookDetailsActivity.this) {
					@Override
					public void onSuccess(IycResponse<String> iycResponse, Call call, Response response) {
						int currentSentence = Integer.parseInt(iycResponse.getData());
						sharedPreferenceUtil.putInt(SharedPreferenceUtil.CURRENT_BOOK_HAVESENTENCE, currentSentence);
						intentToFBreader(bookData, -1, -1, "");
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						intentToFBreader(bookData, -1, -1, "");
						e.getMessage();
					}


					@Override
					public void parseError(Call call, Exception e) {
						intentToFBreader(bookData, -1, -1, "");
						super.parseError(call, e);
					}

					@Override
					public void onAfter(IycResponse<String> stringIycResponse, Exception e) {
						super.onAfter(stringIycResponse, e);
						dismissLoadingDialig();
					}
				});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().unregister(this);
		}
	}

	/**
	 * 跳转到阅读器
	 *
	 * @param bookData
	 * @param paragrahpId 等于-1时，说明没有从网络上请求到阅读进度（极有可能是请求失败了）
	 * @param chapterId
	 */
	private void intentToFBreader(ShelfBook bookData, int paragrahpId, int chapterId, String segmentStr) {
		Intent intent = new Intent(this, FBReader.class);
		intent.putExtra(Constants.PARAGRAHP_ID, paragrahpId);
		intent.putExtra(Constants.CHAPTERID, chapterId);
		intent.putExtra(Constants.PARAGRAPH_TEXT, segmentStr);
		if (bookData.getRecentReadingLanguageType() == 1 || bookData.getSupportLanguage() == 1) {
			intent.putExtra("bookPath", bookData.getBookPath() + bookData.getBookId() + ".zh.epub");
			startActivity(intent);
			sharedPreferenceUtil.putInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 1);
			sharedPreferenceUtil.putLong(SharedPreferenceUtil.CURRENT_BOOK_ID, bookData.getBookId());
		} else if (bookData.getSupportLanguage() == 2 || (bookData.getSupportLanguage() == 3 && bookData.getRecentReadingLanguageType() != 1)) {
			intent.putExtra("bookPath", bookData.getBookPath() + bookData.getBookId() + ".en.epub");
			startActivity(intent);
			sharedPreferenceUtil.putInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 2);
			sharedPreferenceUtil.putLong(SharedPreferenceUtil.CURRENT_BOOK_ID, bookData.getBookId());
		} else {
			ToastCompat.makeText(this, "未找到对应书籍！", 1000);
		}
	}

	private void hasBuyUI() {
		if (!isSupportAndroid ) {
			btTryRead.setVisibility(View.GONE);
			btExchange.setVisibility(View.GONE);
			btBuy.setVisibility(View.GONE);
			btnFunction1.setVisibility(View.GONE);
		} else {
			btTryRead.setVisibility(View.VISIBLE);
			if (hasBuy == BookType.BookBuyState.NOT_BUY || hasBuy == BookType.BookBuyState.ADD_TO_CHART) {
				if (hasBuy == BookType.BookBuyState.ADD_TO_CHART) {
					getGoodsNums();
				}
				btBuy.setVisibility(View.VISIBLE);
				btnFunction1.setVisibility(View.VISIBLE);
				if (displayPrice < 0) {
					btBuy.setVisibility(View.GONE);
					viewLeftBlank.setVisibility(View.VISIBLE);
					viewRightBlank.setVisibility(View.VISIBLE);
					btnFunction1.setVisibility(View.GONE);
					btExchange.setVisibility(View.GONE);
					viewExchangeBlank.setVisibility(View.GONE);
				} else if (displayPrice == 0) {
					btBuy.setText("领取");
					btnFunction1.setVisibility(View.GONE);
				}
			} else if (hasBuy == BookType.BookBuyState.HAS_BOUGHT) {
				buyedUIChange();
			}
		}
		HasRemoved(bookStatus,hasBuy == BookType.BookBuyState.HAS_BOUGHT);
	}

	/**
	 * 首先，根据是否已经购买来判断书的类型，
	 * 如果是已经购买的话就设置bookType为2
	 * 如果没有购买的话判断是不是包月的书籍，
	 * 如果是包月的书籍且在包月时间内的话就设置type为3
	 * 如果不是包月的书籍的话就设置type为1
	 */
	private void setBookType() {
		if (hasBuy == BookType.BookBuyState.HAS_BOUGHT) {
			shelfBook.setBookType(BookType.HAS_BUY_BOOk);
		} else {
			if (shelfBook.getBookType() != BookType.MONTHLY_BOOK && shelfBook.getBookType() != BookType.FREE_BOOK) {
				shelfBook.setBookType(BookType.TRY_READ_BOOK);
			}
		}
//		shelfBook.setBookType(BookType.TRY_READ_BOOK);
	}

	public void getGoodsNums() {
		if (CommonUtil.getLoginState()) {

				OkGo.get(PersonShopcartURL)
						.tag(this)
						.params("userId", CommonUtil.getUserId())
						.execute(new JsonCallback<IycResponse<List<MineShoppingBookIntroduction>>>(this) {
							@Override
							public void onSuccess(IycResponse<List<MineShoppingBookIntroduction>> listIycResponse, Call call, Response response) {
								if (listIycResponse.getData() != null && listIycResponse.getData().size() > 0) {
									if(bookStatus ==1) {
										tvGoodsNum1.setVisibility(View.VISIBLE);
									}
									btnFunction1.setImageResource(R.drawable.ic_shopping);
									tvGoodsNum1.setText(listIycResponse.getData().size() + "");
								} else {
									btnFunction1.setImageResource(R.drawable.ic_shopping_add);
								}
							}

							@Override
							public void onError(Call call, Response response, Exception e) {
								ToastCompat.makeText(BookMarketBookDetailsActivity.this, R.string.net_error_tip, Toast.LENGTH_SHORT).show();
							}
						});
			}


	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_market_book_details);
		ButterKnife.bind(this);
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}
		initView();
	}

	private void bookCollect() {
		OkGo.get(Urls.BookMarketCollectURL)
				.tag(this)
				.params("bookid", bookId)
				.params("state", collectState)
				.params("userid", CommonUtil.getUserId())
				.execute(new StringCallback() {
					@Override
					public void onSuccess(String s, Call call, Response response) {
						if (collectState == 1) {
							collectState = 0;
							mDialog.setState(true);
							ToastCompat.makeText(context, "添加收藏成功", Toast.LENGTH_SHORT).show();
						} else if (collectState == 0) {
							collectState = 1;
							mDialog.setState(false);
							ToastCompat.makeText(context, "取消收藏成功", Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						ToastCompat.makeText(context, "操作失败，请稍候再试...", Toast.LENGTH_SHORT).show();
					}
				});
	}
	private int retryCount = 0;

	private void getDataFromNetWork() {
		showLoadingDialog();
		OkGo.get(Urls.BookMarketBookDetailURL)
				.tag(this)
				.params("bookId", bookId)
				.execute(new JsonCallback<IycResponse<MarketBookDetails>>(this) {
					@Override
					public void onSuccess(IycResponse<MarketBookDetails> iycResponse, Call call, Response response) {
						retryCount = 0;
						setData(iycResponse.getData());
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						dismissLoadingDialig();
						shareUtils.addImagUrl(null);
					}

					@Override
					public void parseError(Call call, Exception e) {
						if(retryCount++< RetryCounter.MAX_RETRY_TIMES){
							getDataFromNetWork();
						}else{
							dismissLoadingDialig();
						}
					}
				});
	}

	private void getDataFromNetWork1() {
		showLoadingDialog();
		OkGo.get(Urls.BookMarketBookDetailURL)
				.tag(this)
				.params("bookId", bookId)
				.execute(new JsonCallback<IycResponse<MarketBookDetails>>(this) {
					@Override
					public void onSuccess(IycResponse<MarketBookDetails> iycResponse, Call call, Response response) {
						retryCount = 0;
						setPrice(iycResponse.getData());
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						dismissLoadingDialig();
						shareUtils.addImagUrl(null);
					}
				});
	}

	/**
	 * 获取是否是包月书籍的信息。如果是包月书籍，会返回包月截至时间。
	 *
	 * @param bookId
	 * @param paymentId
	 * @param userId
	 */
	private void getMonthlyBookInfo(int bookId, int paymentId, long userId) {
		if (paymentId == 0 || !CommonUtil.getLoginState() || bookId == 0) {
			dismissLoadingDialig();
			hasBuyUI();
			return;
		}
		OkGo.get(Urls.URL + Urls.GetAppMonthlyBookInfo)
				.params("bookId", bookId)
				.params("paymentId", paymentId)
				.params("userId", userId)
				.execute(new JsonCallback<IycResponse<MonthlyMarketBookListItem>>(context) {
					@Override
					public void onSuccess(IycResponse<MonthlyMarketBookListItem> monthlyMarketBookListItem, Call call, Response response) {
						dismissLoadingDialig();
						if (monthlyMarketBookListItem.getData() == null)
							return;
						//status 1:信息错误
						//status 2:没有购买
						//status 3:有效的
						//status 4:过期
						boolean b = (shelfBook.getBookType() != BookType.HAS_BUY_BOOk) && monthlyMarketBookListItem.getData().getStatus() == 3;
						if (b) {
							String endTime = DateUtils.getClientDateFormat(monthlyMarketBookListItem.getData().getEndTime());
							shelfBook.setBookType(BookType.MONTHLY_BOOK);
							shelfBook.setEndTime(endTime);
							hasBuy = BookType.BookBuyState.HAS_BOUGHT;
						} else {
							if (shelfBook.getBookType() == BookType.HAS_BUY_BOOk) {
								hasBuy = BookType.BookBuyState.HAS_BOUGHT;
							}
							shelfBook.setEndTime("");
						}
						hasBuyUI();
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
						dismissLoadingDialig();
						Logger.i(e.getMessage());
					}
				});
	}

	private String getSupportLanguage(String bookLanguage) {
		if (bookLanguage.contains("4")) {
			supportLanguage = 3;
			return "双语(句对)";
		} else if (bookLanguage.contains("3")) {
			supportLanguage = 3;
			return "双语";
		} else if (bookLanguage.contains("2")) {
			supportLanguage = 2;
			return "英文";
		} else {
			supportLanguage = 1;
			return "中文";
		}
	}

	public String getGradeLevel(int grade) {
		switch (grade) {
			case 1:
				return "美国小学";
			case 2:
				return "美国初中";
			case 3:
				return "美国高中";
			case 4:
				return "美国大学";
		}
		return "";
//        if (grade == 0) {
//            return null;
//        } else if (grade <= 6) {
//            return "美国小学";
//        } else if (grade <= 9) {
//            return "美国初中";
//        } else if (grade <= 12) {
//            return "美国高中";
//        } else {
//            return "美国大学";
//        }
	}

	/**
	 * 如果用户已登录则获取书籍状态(是否已购买，是否已下载)
	 */
	private void getBookState(final boolean isFree) {
		if (CommonUtil.getLoginState()) {
			OkGo.get(Urls.BookisBuyedURL)
					.tag(this)
					.params("bookId", bookId)
					.params("userId", CommonUtil.getUserId())
					.execute(new JsonCallback<IycResponse<String>>(this) {
						@Override
						public void onSuccess(IycResponse<String> iycResponse, Call call, Response response) {
							hasBuy = Integer.parseInt(iycResponse.getData());
							if (!isFree && hasBuy != BookType.BookBuyState.HAS_BOUGHT) {
								getMonthlyBookInfo(bookId, paymentId, CommonUtil.getUserId());
							} else {
								hasBuyUI();
								dismissLoadingDialig();
							}
							isDownload();
						}

						@Override
						public void onError(Call call, Response response, Exception e) {
							e.getMessage();
							hasBuyUI();
							dismissLoadingDialig();
							isDownload();
						}


						@Override
						public void parseError(Call call, Exception e) {
							super.parseError(call, e);
							if (getBookStateCount <= 3) {
								getBookStateCount++;
								getBookState(isFree);
							} else {
								dismissLoadingDialig();
							}
						}
					});
		} else {
			hasBuyUI();
			if(bookStatus != 1){
				tvBookKind.setText("已下架");
				tvBookKind.setVisibility(View.VISIBLE);
			}else {
				tvBookKind.setVisibility(View.INVISIBLE);
			}
			dismissLoadingDialig();
		}
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
		bookDao = new BookDao(DatabaseHelper.getHelper(BookMarketBookDetailsActivity.this));
		bookId = getIntent().getIntExtra(Constants.BOOK_ID, 0);
		bookName = getIntent().getStringExtra(Constants.BOOK_NAME);
		paymentId = getIntent().getIntExtra(Constants.MONTHLY_BOOK_ID, 0);
		shelfBook.setBookType(BookType.INITIAL);
	}

	protected void initView() {
		mDialog = new BookDetailsMoreDialog(this,R.style.DialogTheme);
		mDialog.setOnItemClickedListener(new OnItemClickedListener() {
			@Override
			public void onItemClickedListener(int position) {
				switch (position) {
					case 0:
						if (CommonUtil.getLoginState()) {
							if (!isNotCollect) {
								bookCollect();
							} else {
								ToastCompat.makeText(context, "暂无法收藏，请稍候再试...", Toast.LENGTH_SHORT).show();
							}
						} else {
							startActivity(new Intent(BookMarketBookDetailsActivity.this, LoginActivity.class));
						}
						break;
					case 1:
						shareUtils.open();
						break;
				}
			}
		});
		llBookDetails.setVisibility(View.GONE);
		btnFunction.setVisibility(View.VISIBLE);
		btnFunction1.setVisibility(View.VISIBLE);
		btnFunction.setImageResource(R.drawable.menu);
		btnFunction1.setImageResource(R.drawable.ic_shopping_add);
		textHeadTitle.setText(bookName);
		btnBack.setImageResource(R.drawable.btn_back);
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		ivMore.setVisibility(View.GONE);
		tvMore.setVisibility(View.GONE);
		imCommentEdit.setVisibility(View.VISIBLE);

		HashMap<String, String> tempMap = new HashMap<>();
		tempMap.put(CONTENT_KEY, bookName);
		String url = Urls.URL + "/iycong_web/html/book_store/book_detail.html?id=" + bookId;
		tempMap.put(URLS_KEY, url);
		shareUtils = new ShareUtils(this, tempMap, BOOK_SHARE);
	}

	@Override
	protected void setMainHeadView() {

	}

	@Override
	protected void onResume() {
		super.onResume();
		bookDao = new BookDao(DatabaseHelper.getHelper(BookMarketBookDetailsActivity.this));
		getDataFromNetWork();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		initView();
	}

	private void getIsCollect() {
		OkGo.get(Urls.BookCollectionIdURL)
				.tag(this)
				.params("userId", CommonUtil.getUserId() + "")
				.execute(new JsonCallback<IycResponse<List<BaseBook>>>(this) {
					@Override
					public void onSuccess(IycResponse<List<BaseBook>> listIycResponse, Call call, Response response) {
						boolean sb = false;
						for (BaseBook book : listIycResponse.getData()) {
							if (book.getBookId() == bookId) {
								sb = true;
								break;
							}
						}
						mDialog.setState(sb);
						if (sb) {
							collectState = 0;
						} else {
							collectState = 1;
						}
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						isNotCollect = true;
					}
				});
	}

	private void setBookDetails(MarketBookDetails marketBookDetails) {
		if (marketBookDetails.getVersion() == null) {
			tvVersion.setVisibility(View.GONE);
		} else {
			tvVersion.setText("版权方: " + marketBookDetails.getVersion());
		}
		if (marketBookDetails.getBookNumber() == null) {
			tvBookNumber.setVisibility(View.GONE);
		} else {
			tvBookNumber.setText("书   号: " + marketBookDetails.getBookNumber());
		}
		if (marketBookDetails.getWordNum() == 0) {
			tvBookTextLength.setVisibility(View.GONE);
		} else {
			tvBookTextLength.setText("字   数: " + marketBookDetails.getWordNum() + " 词");
		}
		if (marketBookDetails.getUpTime() == null) {
			tvOutTime.setVisibility(View.GONE);
		} else {
			tvOutTime.setText("上   架: " + marketBookDetails.getUpTime());
		}
	}

	private void setData(final MarketBookDetails marketBookDetails) {

		shelfBook.setUserId(CommonUtil.getUserId());
		shelfBook.setBookId(bookId);
		shelfBook.setBookName(marketBookDetails.getBookName());
		shelfBook.setBookIntroduction(marketBookDetails.getBookIntroduction());
		shelfBook.setBookAuthor(marketBookDetails.getBookAuthor());
		shelfBook.setBookPrice(marketBookDetails.getPrice());
		textHeadTitle.setText(marketBookDetails.getBookName());
		//获取全局图书状态
		bookStatus = marketBookDetails.getStatus();
		llBookDetails.setVisibility(View.VISIBLE);
		shareUtils.addImagUrl(marketBookDetails.getBookImageUrl());

		isSupportAndroid = marketBookDetails.getTerminal().contains("3");
//		isShowToUser = marketBookDetails.getStatus() == 1;

		GlideImageLoader.displayBookCover(this, ivBookDetailsImage, marketBookDetails.getBookImageUrl());
		if (marketBookDetails.getBookIntroduction() == null || "".equals(marketBookDetails.getBookIntroduction())) {
			tvBookDetailsBookDefault.setVisibility(View.VISIBLE);
			tvBookDetailsBookIntroduce.setVisibility(View.GONE);
			imBookDetailsBookIntroduceMore.setVisibility(View.GONE);
		} else {
			tvBookDetailsBookIntroduce.setText(marketBookDetails.getBookIntroduction());
			if (tvBookDetailsBookIntroduce.getText().length() < 100) {
				imBookDetailsBookIntroduceMore.setVisibility(View.GONE);
			} else {
				tvBookDetailsBookIntroduce.setHeight(tvBookDetailsBookIntroduce.getLineHeight() * maxDescripLine);
			}
		}
		if (marketBookDetails.getAuthorSimpleIntroduction() == null || "".equals(marketBookDetails.getAuthorSimpleIntroduction())) {
			tvBookDetailsAuthorDefault.setVisibility(View.VISIBLE);
			tvBookDetailsAuthorIntroduce.setVisibility(View.GONE);
			imBookDetailsAuthorIntroduceMore.setVisibility(View.GONE);
		} else {
			tvBookDetailsAuthorDefault.setVisibility(View.GONE);
			tvBookDetailsAuthorIntroduce.setText(marketBookDetails.getAuthorSimpleIntroduction());
			if (tvBookDetailsAuthorIntroduce.getText().length() < 100) {
				imBookDetailsAuthorIntroduceMore.setVisibility(View.GONE);
			} else {
				tvBookDetailsAuthorIntroduce.setHeight(tvBookDetailsAuthorIntroduce.getLineHeight() * maxDescripLine);
			}
		}
		gvSimilarRecommendedBook.setAdapter(new SimilarRecommendedGridAdapter(this, marketBookDetails.getSameoffer()));
		gvSimilarRecommendedBook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				scrollView.scrollTo(0, 0);
				bookId = marketBookDetails.getSameoffer().get(i).getBookId();
				viewLeftBlank.setVisibility(View.GONE);
				viewRightBlank.setVisibility(View.GONE);
				initView();
				getDataFromNetWork();
			}
		});
		tvBookDetailsTittle.setText(marketBookDetails.getBookName());
		tvBookDetailsTitleEnglish.setText(marketBookDetails.getTitle_en());
		tvBookDetailsTitleEnglish.setEllipsize(TextUtils.TruncateAt.MARQUEE);
		tvBookDetailsTitleEnglish.setSingleLine(true);
		tvBookDetailsTitleEnglish.setSelected(true);
		tvBookDetailsTitleEnglish.setFocusable(true);
		tvBookDetailsTitleEnglish.setFocusableInTouchMode(true);
		String supportLanguage = getSupportLanguage(marketBookDetails.getBookLanguage());
		tvBookDetailsLanguage.setText("语言: " + supportLanguage);
		String grade = getGradeLevel(marketBookDetails.getGradelevel());
		if (grade == null) {
			tvBookDetailsDifficulty.setVisibility(View.INVISIBLE);
		} else {
			tvBookDetailsDifficulty.setText("难度:" + grade);
		}

		if (marketBookDetails.getBookAuthor() == null || marketBookDetails.getBookAuthor().equals("") || marketBookDetails.getBookAuthor().equals("null")) {
			tvBookDetailsAuthorName.setVisibility(View.INVISIBLE);
		} else {
			tvBookDetailsAuthorName.setText("作者: " + marketBookDetails.getBookAuthor());
		}
		if (marketBookDetails.getTranslator() == null || marketBookDetails.getTranslator().equals("") || marketBookDetails.getTranslator().equals("null")) {
			tvBookDetailsTranslatorName.setVisibility(View.GONE);
		} else {
			tvBookDetailsTranslatorName.setText("译者: " + marketBookDetails.getTranslator());
		}
		if (marketBookDetails.getFree_status() == 0) {
			shelfBook.setBookType(BookType.FREE_BOOK);
			displayPrice = 0.0;
		} else if (marketBookDetails.getFree_status() == -1) {
			if (marketBookDetails.getSpecial_status() == 0) {
				tvBookDetailsOriginPrice.setVisibility(View.VISIBLE);
				tvBookDetailsOriginPrice.setText("￥" + String.valueOf(marketBookDetails.getPrice()));
				tvBookDetailsOriginPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
				displayPrice = marketBookDetails.getSpecial_price();
			} else if (marketBookDetails.getSpecial_status() == -1) {
				displayPrice = marketBookDetails.getPrice();
			}
		}
		if (displayPrice <= 0) {
			String detailPrice;
			if (tvBookDetailsOriginPrice.getVisibility() == View.VISIBLE) {
				detailPrice = "价格:";
				tvBookDetailsPrice.setText(detailPrice);
			} else {
				detailPrice = "价格:免费";
				SpannableStringBuilder style = new SpannableStringBuilder(detailPrice);
				style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_color)), 3, detailPrice.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				tvBookDetailsPrice.setText(style);
			}
		} else if (displayPrice > 0) {
			String priceStr = "价格:￥" + displayPrice;
			SpannableStringBuilder style = new SpannableStringBuilder(priceStr);
			style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main_color)), 3, priceStr.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			tvBookDetailsPrice.setText(style);
		}
		if (hasBuy == BookType.BookBuyState.NOT_BUY) {
			if (marketBookDetails.getVirtual_status() == 0) {
				bookVirtualCoin = (int) marketBookDetails.getVirtual_price();
				btExchange.setText(bookVirtualCoin + "积分兑换");
				btExchange.setVisibility(View.VISIBLE);
				viewExchangeBlank.setVisibility(View.VISIBLE);
			} else if (marketBookDetails.getVirtual_status() == -1) {
				btExchange.setVisibility(View.GONE);
				viewExchangeBlank.setVisibility(View.GONE);
			}
		}
		rbLevel.setStar(marketBookDetails.getBookRating() / 2);
		rbLevel.setmClickable(false);
		if (marketBookDetails.getReviews() != null && marketBookDetails.getReviews().size() != 0) {
			tvBarTitle.setText("书评" /*+ marketBookDetails.getReviews().size() + ")"*/);
			relativeSeeMoreComment.setVisibility(View.VISIBLE);
			llLayoutSofa.setVisibility(View.GONE);
		} else {
			tvBarTitle.setText("书评");
			relativeSeeMoreComment.setVisibility(View.GONE);
			llLayoutSofa.setVisibility(View.VISIBLE);
		}
		setLabel(marketBookDetails.getBookTypeList());

		rvComment.setAdapter(new DiscoverReviewAdapter(this, marketBookDetails.getReviews(), isBookDetail) {
			@Override
			public int getItemCount() {
				return super.getItemCount() > 3 ? 3 : super.getItemCount();
			}
		});

		LinearLayoutManager mgr = new LinearLayoutManager(this) {
			@Override
			public boolean canScrollVertically() {
				return false;
			}
		};
		rvComment.setLayoutManager(mgr);
//		if (bookId != 53) {
//			getBookState(marketBookDetails.getFree_status() == 0);
//		} else {
//			dismissLoadingDialig();
//		}
		getBookState(marketBookDetails.getFree_status() == 0);
		getIsCollect();

//		/**
//		 * 如果图书为小王子，则另外判断
//		 */
//		if (bookId == 53) {
//			tvBookKind.setVisibility(View.INVISIBLE);
//			buyedUIChange();
//
//			bookState = 1;
////			if (epubHandler == null) {
////				epubHandler = new EpubDecryptHandler(BookMarketBookDetailsActivity.this, bookId);
////			}
//			sharedPreferenceUtil.putInt(SharedPreferenceUtil.DELETED_DEFAULT_BOOK, -1);
//			BuiltInBookUtil.creatBuiltInBook(this);
////			BuiltInBookUtil.creatBuiltInBook(this, bookDao, epubHandler);
//		}
		setBookDetails(marketBookDetails);
	}

	private void HasRemoved(int bookStatus,boolean hasBuy){
		if((!hasBuy && bookStatus !=1) || (hasBuy && (bookStatus == 2 || bookStatus == 3))){
				llBookInfo.setVisibility(View.GONE);
				tvBookHasRemoved.setVisibility(View.VISIBLE);
				llBelowInfo.setVisibility(View.GONE);
				llCopyright.setVisibility(View.GONE);
				btnFunction.setVisibility(View.GONE);
				btnFunction1.setVisibility(View.GONE);
				tvGoodsNum1.setVisibility(View.GONE);
		}else{
			llBookInfo.setVisibility(View.VISIBLE);
			tvBookHasRemoved.setVisibility(View.GONE);
			llBelowInfo.setVisibility(View.VISIBLE);
			llCopyright.setVisibility(View.VISIBLE);
			btnFunction.setVisibility(View.VISIBLE);
			btnFunction1.setVisibility(View.VISIBLE);
			tvGoodsNum1.setVisibility(View.VISIBLE);
		}
	}

	private void setPrice(final MarketBookDetails marketBookDetails) {

		if (marketBookDetails.getFree_status() == 0) {
			shelfBook.setBookType(BookType.FREE_BOOK);
			displayPrice = 0.0;
		} else if (marketBookDetails.getFree_status() == -1) {
			if (marketBookDetails.getSpecial_status() == 0) {
				tvBookDetailsOriginPrice.setVisibility(View.VISIBLE);
				tvBookDetailsOriginPrice.setText("￥" + String.valueOf(marketBookDetails.getPrice()));
				tvBookDetailsOriginPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
				displayPrice = marketBookDetails.getSpecial_price();
			} else if (marketBookDetails.getSpecial_status() == -1) {
				displayPrice = marketBookDetails.getPrice();
			}
		}
		IntentUtils.goToPayActivity(this, bookId + "", displayPrice + "", displayPrice, 1);
	}

	private synchronized void setLabel(List<String> hotSearchList) {
		rlIntroductiontype.setTags(hotSearchList);
		rlIntroductiontype.setOnTagClickListener(new TagGroup.OnTagClickListener() {
			@Override
			public void onTagClick(String tag) {
				Intent intent = new Intent(BookMarketBookDetailsActivity.this, BookMarketBookListActivity.class);
				intent.putExtra("list_type", 8);
				intent.putExtra("tagsName", tag);
				BookMarketBookDetailsActivity.this.startActivity(intent);
			}
		});
	}

	class ShoppingInfo {
		int num;
		float price;

	}

	class SimilarRecommendedGridAdapter extends BaseAdapter {
		private Context mContext;

		private List<MarketBookDetailsSameBooks> bookList;

		public SimilarRecommendedGridAdapter(Context mContext, List<MarketBookDetailsSameBooks> bookList) {
			this.mContext = mContext;
			this.bookList = bookList;
		}

		@Override
		public int getCount() {
			int maxSize = 0;
			if (bookList != null) {
				if (bookList.size() > 6) {
					maxSize = 6;
				} else {
					maxSize = bookList.size();
				}
			}
			return bookList == null ? 0 : maxSize;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.item_shelf_book, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tvBookState.setVisibility(View.GONE);
//			holder.pgBookAdded.setVisibility(View.GONE);
			holder.tvBookProgress.setVisibility(View.GONE);
			GlideImageLoader.displayBookCover(BookMarketBookDetailsActivity.this, holder.ivShelfBookImage, bookList.get(position).getBookImgUrl());
			holder.tvBookName.setText(bookList.get(position).getBookName());
			return convertView;
		}

		class ViewHolder {
			@BindView(R.id.iv_shelf_book_image)
			ImageView ivShelfBookImage;
			@BindView(R.id.tv_book_name)
			TextView tvBookName;
			@BindView(R.id.pg_book_added)
			RoundCornerProgressBar pgBookAdded;
			@BindView(R.id.tv_book_state)
			TextView tvBookState;
			@BindView(R.id.tv_book_progress)
			TextView tvBookProgress;

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}
		}
	}

	class freeGetstatus {
		int buystatus;

		public int getBuystatus() {
			return buystatus;
		}

		public void setBuystatus(int buystatus) {
			this.buystatus = buystatus;
		}
	}

}
