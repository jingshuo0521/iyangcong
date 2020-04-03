package com.iyangcong.reader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.AgreementActivity;
import com.iyangcong.reader.activity.LoginActivity;
import com.iyangcong.reader.activity.MembershipPointActivity;
import com.iyangcong.reader.activity.MineCollectionActivity;
import com.iyangcong.reader.activity.MineDictionaryActivity;
import com.iyangcong.reader.activity.MineExchangeActivity;
import com.iyangcong.reader.activity.MineExperienceAcitivity2;
import com.iyangcong.reader.activity.MineFansListActivity;
import com.iyangcong.reader.activity.MineFriendsAndFansActivity;
import com.iyangcong.reader.activity.MineHistoryActivity;
import com.iyangcong.reader.activity.MineMonthlyBookActivity;
import com.iyangcong.reader.activity.MineNewWordActivity;
import com.iyangcong.reader.activity.MinePageActivity;
import com.iyangcong.reader.activity.MinePersonSettingActivity;
import com.iyangcong.reader.activity.MinePurchasedActivity;
import com.iyangcong.reader.activity.MineQuestionsRecordActivity;
import com.iyangcong.reader.activity.MineSettingActivity;
import com.iyangcong.reader.activity.MineShoppingActivity;
import com.iyangcong.reader.activity.MineSuggestActivity;
import com.iyangcong.reader.activity.NewMaeesageActivity;
import com.iyangcong.reader.activity.NewNormalQuestionActivity;
import com.iyangcong.reader.activity.SemesterBooksActivity;
import com.iyangcong.reader.activity.SignCalendarActivity;
import com.iyangcong.reader.base.BaseFragment;
import com.iyangcong.reader.bean.MineBasicInfo;
import com.iyangcong.reader.bean.TeacherClassInfo;
import com.iyangcong.reader.bean.UnreadMsgBean;
import com.iyangcong.reader.bean.UserReadingInfo;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.event.SlideEvent;
import com.iyangcong.reader.interfaceset.RetryCounter;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.SettingItem;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.GlideImageLoader;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;


/**
 * 我的
 */
public class MineFragment extends BaseFragment {

	@BindView(R.id.si_book_purchased)
	LinearLayout siBookPurchased;
	@BindView(R.id.si_mine_monthly_book)
	LinearLayout mSiMineMonthlyBook;
	@BindView(R.id.si_book_exchange)
	LinearLayout siBookExchange;
	@BindView(R.id.si_mine_dictionary)
	LinearLayout siMyDictionary;
	@BindView(R.id.si_membershippoint)
	LinearLayout siMembershippoint;
	@BindView(R.id.si_shopping_cart)
	LinearLayout siShoppingCart;
	@BindView(R.id.si_my_fans)
	SettingItem siMyFans;
	@BindView(R.id.si_my_collection)
	LinearLayout siMyCollection;
	@BindView(R.id.si_my_experience)
	LinearLayout siMyExperience;
	@BindView(R.id.si_suggest)
	SettingItem siSuggest;
	@BindView(R.id.si_more_setting)
	SettingItem siMoreSetting;
	@BindView(R.id.si_normalquestion)
	SettingItem siNormalQuestion;
	@BindView(R.id.iv_mine_head)
	ImageView ivMineHead;
	@BindView(R.id.tv_user_name)
	TextView tvUserName;
	@BindView(R.id.text)
	TextView tvText;
	@BindView(R.id.tv_user_level)
	TextView tvUserLevel;
	@BindView(R.id.tv_user_score)
	TextView tvUserScore;
	@BindView(R.id.rl_mine)
	RelativeLayout rlMine;
	@BindView(R.id.iv_mine_head_sex)
	ImageView ivMineHeadSex;
	@BindView(R.id.textView)
	TextView textView;
	@BindView(R.id.ll_mine_title)
	RelativeLayout llMineTitle;
	@BindView(R.id.ll_login)
	LinearLayout llLogin;
	@BindView(R.id.tv_login)
	TextView tvLogin;
	@BindView(R.id.ll_logout)
	LinearLayout llLogout;
	@BindView(R.id.fl_mine_head)
	FrameLayout flMineHead;
	@BindView(R.id.iv_mine_arrow)
	ImageView ivMineArrow;
	@BindView(R.id.iv_user_level)
	ImageView ivUserLevel;
	@BindView(R.id.si_new_word)
	SettingItem siNewWord;
	@BindView(R.id.si_test_record)
	SettingItem siTestRecord;
	@BindView(R.id.si_my_friends)
	SettingItem siMyFriends;
	@BindView(R.id.tv_goods_num)
	TextView tvGoodsNum;
	@BindView(R.id.si_book_term)
	SettingItem mSiMineTermBook;
	@BindView(R.id.scrollView)
	ScrollView mScrollView;
    @BindView(R.id.tv_week_read_time)
    TextView mTvWeekReadTime;
    @BindView(R.id.tv_week_over_percent)
    TextView mTvWeekOverPercent;
    @BindView(R.id.btn_qiandao)
    ImageView mBtnQiandao;

	private MineBasicInfo mineBasicInfo;
	private SharedPreferenceUtil sharedPreferenceUtil;
	private int reloadTimes;//重新加载次数
	private int userType;
	private List<TeacherClassInfo> classinfos;
	private Map<String,Object> classMap = new HashMap<String,Object>();
	private List<String> classNames = new ArrayList<>();
	private List<Integer> classIds = new ArrayList<>();


	public MineBasicInfo getMineBasicInfo() {
		return mineBasicInfo;
	}

	public void setMineBasicInfo(MineBasicInfo mineBasicInfo) {
		this.mineBasicInfo = mineBasicInfo;
	}

	@BindView(R.id.message)
	ImageView message;

	@OnClick({R.id.si_my_friends, R.id.iv_mine_head, R.id.si_book_purchased, R.id.si_history, R.id.si_book_exchange, R.id.si_shopping_cart, R.id.si_my_collection, R.id.si_my_experience, R.id.si_new_word,R.id.si_test_record, R.id.si_suggest, R.id.si_more_setting, R.id.rl_mine, R.id.message, R.id.tv_login, R.id.iv_user_level, R.id.tv_user_score, R.id.tv_user_level, R.id.textView, R.id.tv_goods_num, R.id.si_mine_monthly_book, R.id.si_normalquestion, R.id.si_membershippoint,R.id.si_my_fans,R.id.si_book_term,R.id.si_mine_dictionary,R.id.btn_qiandao})
	void onSettingItemClick(View view) {
		if (!CommonUtil.getLoginState() && (view.getId() != R.id.si_suggest) && (view.getId() != R.id.si_more_setting)) {
			Intent intent_login = new Intent(mContext, LoginActivity.class);
			startActivity(intent_login);
		} else {
			Intent intent = null;
			switch (view.getId()) {
				case R.id.si_mine_dictionary:
					intent = new Intent(mContext, MineDictionaryActivity.class);
					break;
				case R.id.si_book_purchased:
					intent = new Intent(mContext, MinePurchasedActivity.class);
					break;
				case R.id.si_book_exchange:
					intent = new Intent(mContext, MineExchangeActivity.class);
					break;
				case R.id.si_shopping_cart:
					intent = new Intent(mContext, MineShoppingActivity.class);
					break;
				case R.id.si_my_collection:
					intent = new Intent(mContext, MineCollectionActivity.class);
					break;
				case R.id.tv_goods_num:
				case R.id.message:
					intent = new Intent(mContext, NewMaeesageActivity.class);
					break;
				case R.id.si_new_word:
					intent = new Intent(mContext, MineNewWordActivity.class);
					break;
				case R.id.si_test_record:
					intent = new Intent(mContext, MineQuestionsRecordActivity.class);
					break;
				case R.id.si_suggest:
					intent = new Intent(mContext, MineSuggestActivity.class);
					break;
				case R.id.si_more_setting:
					intent = new Intent(mContext, MineSettingActivity.class);
					break;
				case R.id.rl_mine:
					intent = new Intent(mContext, MinePageActivity.class);
					intent.putExtra(Constants.USER_ID,CommonUtil.getUserId() + "");
					break;
				case R.id.iv_mine_head:
					intent = new Intent(mContext, MinePersonSettingActivity.class);
					break;
				case R.id.tv_login:
					intent = new Intent(mContext, LoginActivity.class);
					break;
				case R.id.si_my_experience:
					intent = new Intent(mContext, MineExperienceAcitivity2.class);
					break;
				case R.id.si_my_friends:
					intent = new Intent(mContext, MineFriendsAndFansActivity.class);
					break;
				case R.id.si_history:
					intent = new Intent(mContext, MineHistoryActivity.class);
					break;
				case R.id.iv_user_level:
//                    View inflate = View.inflate(mContext, R.layout.popup_bubble_text, null);
//                    TextView tv = ButterKnife.findById(inflate, R.id.tv_bubble);
//                    BubblePopup bubblePopup = new BubblePopup(mContext, inflate);
//                    tv.setText(R.string.level_rule);
//                    bubblePopup.anchorView(ivUserLevel)
//                            .gravity(Gravity.BOTTOM)
//                            .show();
					break;
				case R.id.tv_user_score:
					intent = new Intent(mContext, AgreementActivity.class);
					intent.putExtra(Constants.USERAGREEMENT, Urls.URL + "/onion/coin.html");
					break;
				case R.id.tv_user_level:
					intent = new Intent(mContext, AgreementActivity.class);
					intent.putExtra(Constants.USERAGREEMENT, Urls.URL + "/onion/coin.html");
					break;
				case R.id.si_mine_monthly_book:
					intent = new Intent(mContext, MineMonthlyBookActivity.class);
					break;
				case R.id.si_normalquestion:
					/*intent = new Intent(mContext, NormalQuestionActivity.class);*/
					intent = new Intent(mContext, NewNormalQuestionActivity.class);
					break;
				case R.id.si_membershippoint:
					intent = new Intent(mContext, MembershipPointActivity.class);
					break;
				case R.id.si_my_fans:
					intent = new Intent(mContext, MineFansListActivity.class);
					break;
				case R.id.si_book_term:
					intent = new Intent(mContext, SemesterBooksActivity.class);
					intent.putExtra(Constants.USER_TYPE,userType);
					break;
                case R.id.btn_qiandao:
					intent = new Intent(mContext, SignCalendarActivity.class);
                    break;
				default:
					break;
			}
			if (intent != null) {
				startActivity(intent);
			}
		}
	}

	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mine, container, false);
		ButterKnife.bind(this, view);
		sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}
		return view;
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onReturnToTop(SlideEvent slideEvent) {
		if (isVisible() &&mScrollView!=null){
			mScrollView.fullScroll(ScrollView.FOCUS_UP);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().unregister(this);
		}
	}

	public void viewDataChange() {
		if (CommonUtil.getLoginState()&&llLogin != null) {
			if (llLogout != null){
				llLogout.setVisibility(View.GONE);
			}
			if (llLogin != null){
				llLogin.setVisibility(View.VISIBLE);
			}
			if (llMineTitle != null){
				llMineTitle.setVisibility(View.VISIBLE);
				tvText.setVisibility(View.GONE);
			}
			if (ivMineArrow != null)
				ivMineArrow.setVisibility(View.VISIBLE);
			if (ivMineHeadSex != null)
				ivMineHeadSex.setVisibility(View.VISIBLE);
		} else if(ivMineHeadSex!=null){
			if (ivMineHeadSex != null)
				ivMineHead.setImageResource(R.drawable.ic_head_default);
			if (llLogout != null){
				llLogout.setVisibility(View.VISIBLE);
			}

			if (llLogin != null)
				llLogin.setVisibility(View.GONE);
			if (tvUserLevel != null)
				tvUserLevel.setVisibility(View.INVISIBLE);
//            ivUserLevel.setVisibility(View.INVISIBLE);
			if (tvUserName != null)
				tvUserName.setText("");
			if (tvUserScore != null)
				tvUserScore.setText("");
			if (llMineTitle != null){
				llMineTitle.setVisibility(View.GONE);
				tvText.setVisibility(View.VISIBLE);
			}
			if (ivMineArrow != null)
				ivMineArrow.setVisibility(View.GONE);
			if (ivMineHeadSex != null)
				ivMineHeadSex.setVisibility(View.INVISIBLE);
		}
	}

	private void setAutoAddNewWordState() {
		boolean state = sharedPreferenceUtil.getBoolean(SharedPreferenceUtil.AOUT_ADD_NEW_WORD, false);
		userType = sharedPreferenceUtil.getInt(SharedPreferenceUtil.USER_TYPE, -1);
		if(userType==1||userType==2||userType==3){
			//1学生，3 老师
			mSiMineTermBook.setVisibility(View.VISIBLE);
		}else{
			mSiMineTermBook.setVisibility(View.GONE);
		}
		siNewWord.getCheckedTextView().setChecked(state);
		siNewWord.getCheckedTextView().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				boolean checkedState = siNewWord.getCheckedTextView().isChecked();
				siNewWord.getCheckedTextView().setChecked(!checkedState);
				sharedPreferenceUtil.putBoolean(SharedPreferenceUtil.AOUT_ADD_NEW_WORD, !checkedState);
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshView();
	}


	private void refreshView(){
		userType = sharedPreferenceUtil.getInt(SharedPreferenceUtil.USER_TYPE, -1);
		if(userType==1||userType==2||userType==3){
			//1学生，3 老师
			mSiMineTermBook.setVisibility(View.VISIBLE);
		}else{
			mSiMineTermBook.setVisibility(View.GONE);
		}
	}
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			viewDataChange();
			getDatasFromNetwork();
		//shao modified begin
            getUnreadMessage();
            getUserBasicReadingInfo(0);
            //shao modified end
		} else {

		}
	}

	@Override
	protected void initData() {
		siNewWord.setCheckText("自动添加生词");
		setAutoAddNewWordState();
	}

	private int requestCount = 0;

	private void getDatasFromNetwork() {
		if(!CommonUtil.getLoginState()){
			Logger.v("用户未登录，不请求信息");
			return;
		}
		long usid = CommonUtil.getUserId();

		OkGo.get(Urls.PersonBasicInfoURL)
				.tag(mContext)
				.params("userId", CommonUtil.getUserId() + "")
				.params("toUserId", CommonUtil.getUserId() + "")
				.execute(new JsonCallback<IycResponse<MineBasicInfo>>(mContext) {
					@Override
					public void onSuccess(IycResponse<MineBasicInfo> mineBasicInfoIycResponse, Call call, Response response) {
						setMineBasicInfo(mineBasicInfoIycResponse.getData());
						userType =mineBasicInfo.getUserType();
						if(userType==1||userType==2||userType==3){
							//1学生，3 老师
							mSiMineTermBook.setVisibility(View.VISIBLE);
						}
						sharedPreferenceUtil.putString(SharedPreferenceUtil.USER_PORTAIT_URL, mineBasicInfoIycResponse.getData().getPhoto());
						if (mineBasicInfo.getName() == null) {
							if (sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_USER_ACCOUNT, "").indexOf("@") != -1) {
								tvUserName.setText("邮箱用户" + CommonUtil.getUserId());
							} else if (sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_USER_ACCOUNT, "").indexOf("@") == -1) {
								tvUserName.setText("手机用户" + CommonUtil.getUserId());
							}
						} else {
							tvUserName.setText(mineBasicInfo.getName());
						}
						tvUserScore.setText("积分：" + mineBasicInfo.getCoin());
						tvUserLevel.setText(mineBasicInfo.getCoinDegree());
						GlideImageLoader.displayProtrait(mContext, mineBasicInfo.getPhoto(), ivMineHead);
						if (mineBasicInfo.getSex() == 0) {
							ivMineHeadSex.setBackgroundResource(R.drawable.ic_sex_woman);
						} else {
							ivMineHeadSex.setBackgroundResource(R.drawable.ic_sex_man);
						}
//                        ivUserLevel.setVisibility(View.VISIBLE);
						tvUserLevel.setVisibility(View.VISIBLE);
						//shao modified begin
						//getUnreadMessage();
						////shao modified end
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						if (response != null)
							Logger.i(TAG + "failed", response.body().toString());
					}

					@Override
					public void parseError(Call call, Exception e) {
						super.parseError(call, e);
						if (++requestCount == RetryCounter.MAX_RETRY_TIMES) {
							requestCount = 0;
						} else {
							getDatasFromNetwork();
						}
					}
				});
	}


	private void getUnreadMessage() {
		if (!CommonUtil.getLoginState()) {
			Logger.e("还没有登陆，所以不能返回未读信息");
			return;
		}
		OkGo.get(Urls.GetPersonNoReadMessage)
				.params("userId", CommonUtil.getUserId())
				.execute(new JsonCallback<IycResponse<UnreadMsgBean>>(mContext) {
					@Override
					public void onSuccess(IycResponse<UnreadMsgBean> unreadMsgBeanIycResponse, Call call, Response response) {
						if (isNull(unreadMsgBeanIycResponse) || isNull(unreadMsgBeanIycResponse.getData())) {
							Logger.e("wzp 网络请求未读消息数目有误");
							return;
						}
						if (unreadMsgBeanIycResponse.getData().getNoReadMessageNum() != 0) {
							tvGoodsNum.setVisibility(View.VISIBLE);
							tvGoodsNum.setText(unreadMsgBeanIycResponse.getData().getNoReadMessageNum() + "");
						} else {
							tvGoodsNum.setVisibility(View.GONE);
							tvGoodsNum.setText("");
						}
					}

					@Override
					public void parseError(Call call, Exception e) {
						super.parseError(call, e);
						if (reloadTimes == 3) {
							reloadTimes = 0;
						} else {
							reloadTimes++;
							getUnreadMessage();
						}
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
						Logger.e("wzp 网络请求未读消息数目有误" + e.getMessage());
						tvGoodsNum.setVisibility(View.GONE);
					}
				});

	}

	public void getUserBasicReadingInfo(final int reload){
		OkGo.get(Urls.GetUserBasicReadingInfoOnWeek)
				.tag(this)
				.params("userId", CommonUtil.getUserId())
				.execute(new JsonCallback<IycResponse<UserReadingInfo>>(mContext) {
					@Override
					public void onSuccess(IycResponse<UserReadingInfo> userReadingInfoIycResponse, Call call, Response response) {
						if (isNull(userReadingInfoIycResponse) || isNull(userReadingInfoIycResponse.getData())) {
							Logger.e("wzp 网络请求本周阅读情况有误");
							return;
						}
						String readTime = "本周阅读" + userReadingInfoIycResponse.getData().getHavedReadLong() + "小时";
						String OverPercent = "超过" + userReadingInfoIycResponse.getData().getOverRate() + "的好友";
						mTvWeekReadTime.setText(Html.fromHtml(readTime));
						mTvWeekOverPercent.setText(Html.fromHtml(OverPercent));
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
						String readTime = "本周阅读0小时";
						String OverPercent = "超过0%的好友";
						mTvWeekReadTime.setText(Html.fromHtml(readTime));
						mTvWeekOverPercent.setText(Html.fromHtml(OverPercent));
					}

					@Override
					public void parseError(Call call, Exception e) {
						super.parseError(call, e);
						if (reload == 3) {
//							String readTime = "您共阅读 <font color=\"#ff692d\">XX</font> 小时";
//							String OverPercent = "超过 <font color=\"#ff692d\">XX%</font> 的好友";
//							mTvWeekReadTime.setText(Html.fromHtml(readTime));
//							mTvWeekOverPercent.setText(Html.fromHtml(OverPercent));
						} else {
							getUserBasicReadingInfo(reload+1);
						}
					}
				});
	}
}
