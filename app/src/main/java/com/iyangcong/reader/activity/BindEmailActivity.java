package com.iyangcong.reader.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.CodeReturn;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.enumset.BindType;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.ui.LimitedEdittext;
import com.iyangcong.reader.ui.button.FlatButton;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CodeUtils;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.PatternUtils;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static android.text.InputType.TYPE_CLASS_PHONE;
import static com.iyangcong.reader.utils.NotNullUtils.isNull;

/**
 * Created by WuZepeng on 2017-05-27.
 */

public class BindEmailActivity extends SwipeBackActivity {

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
	@BindView(R.id.layout_header)
	LinearLayout layoutHeader;
	@BindView(R.id.tv_email_account)
	TextView tvEmailAccount;
	@BindView(R.id.et_account_name)
	LimitedEdittext etAccountName;
	@BindView(R.id.v_divider)
	View vDivider;
	@BindView(R.id.tv_code)
	TextView tvCode;
	@BindView(R.id.tv_send_code)
	TextView tvSendCode;
	@BindView(R.id.et_input_code)
	EditText etInputCode;
	@BindView(R.id.fb_change_email_account)
	FlatButton fbChangeEmailAccount;
	@BindView(R.id.random_image)
	ImageView randomImage;
	@BindView(R.id.et_input_randomcode)
	EditText randomCode_et;


	private Handler handler;
	private int recLen;
	private String mCode;
	private boolean isBinding = true;
	private BindType bindType;
	private CodeUtils codeUtils;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bind_email);
		ButterKnife.bind(this);
		setMainHeadView();
		initView();
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		handler = new Handler();
		isBinding = getIntent().getBooleanExtra(Constants.IS_BINDING,true);
		bindType = (BindType)getIntent().getSerializableExtra(Constants.BIND_TYPE);
		Logger.i("bindType:" + bindType.name());

	}

	@Override
	protected void initView() {
		if(bindType == BindType.EMAIL){
			etAccountName.setHint(R.string.input_email);
		}else{
			etAccountName.setHint(R.string.input_bind_phone);
			etAccountName.setInputType(TYPE_CLASS_PHONE);
			etAccountName.setTextWatcher(context,11,true,getString(R.string.content_toolong),getString(R.string.content_no_emoji));
		}
		//shao add being
		codeUtils = CodeUtils.getInstance();
		changeRandomCode();
		//shao add end
	}

	@Override
	protected void setMainHeadView() {
		String title = null;
		if(!isBinding){
			switch (bindType){
				case EMAIL:
					title = "绑定邮箱号";
					tvEmailAccount.setText("邮箱号");
					break;
				case TELEPHONE:
					title = "绑定手机号";
					tvEmailAccount.setText("手机号");
					break;
			}
		}else{
			switch (bindType){
				case EMAIL:
					title = "绑定新邮箱";
					tvEmailAccount.setText("邮箱号");
					break;
				case TELEPHONE:
					title = "绑定新手机号";
					tvEmailAccount.setText("手机号");
					break;
			}
		}
		if(title != null){
			textHeadTitle.setText(title);
			fbChangeEmailAccount.setText(title);
		}
		textHeadTitle.setVisibility(View.VISIBLE);
		btnBack.setImageResource(R.drawable.btn_back);
		btnBack.setVisibility(View.VISIBLE);
	}

	private boolean isEmailAccount(String emailAccount){
		if(isNull(context,emailAccount,""))
			return false;
		int atIndex = emailAccount.indexOf("@");
		return atIndex>0 && atIndex< emailAccount.length() -1;
	}

	private boolean isInputValid(BindType type){
		String accountContent = etAccountName.getText().toString();
		String code = etInputCode.getText().toString();
		return !isNull(context,accountContent,type==BindType.EMAIL?"请输入邮箱号":"请输入手机号")&&!isNull(context,code,"请输入验证码");
	}

	private boolean isCodeSame(String inputCode,String networkCode){
		if(!isNull(context,inputCode,"")&&!isNull(context,networkCode,"")){
			return inputCode.equals(networkCode);
		}
		return false;
	}

	@OnClick({R.id.btnBack, R.id.fb_change_email_account,R.id.tv_send_code,R.id.random_image})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnBack:
				finish();
				break;
			case R.id.fb_change_email_account:
				if(isInputValid(bindType)&&isCodeSame(etInputCode.getText().toString(),mCode)){
					switch (bindType){
						case EMAIL:
							bindEmail(etAccountName.getText().toString());
							break;
						case TELEPHONE:
							bindPhone(etAccountName.getText().toString());
							break;
					}
				}else{
					ToastCompat.makeText(context,"您输入的邮箱验证码不正确", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.tv_send_code:
				if(bindType == BindType.EMAIL){
					sendCode(etAccountName.getText().toString());
				}else if(bindType == BindType.TELEPHONE){
					sendCodeToPhone(true,etAccountName.getText().toString());
				}
				//changeRandomCode();
				break;
			case R.id.random_image:
				changeRandomCode();
				break;
			default:break;
		}
	}

	private boolean isStop = false;
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (!isStop) {
				if (recLen > 0) {
					recLen--;
					tvSendCode.setText("(" + recLen + ")秒后重新发送");
					tvSendCode.setEnabled(false);
					tvSendCode.setTextColor(getResources().getColor(R.color.text_color_hint));
					handler.postDelayed(this, 1000);
				} else {
					tvSendCode.setEnabled(true);
					tvSendCode.setTextColor(getResources().getColor(R.color.main_color));
					tvSendCode.setText("发送验证码");
					changeRandomCode();
				}
			}
		}
	};
	
	private void sendCode(String userName){
		String codeStr=randomCode_et.getText().toString().trim();
		if(userName.equals("")||!isEmailAccount(userName)){
			ToastCompat.makeText(context,getString(R.string.input_email_account), Toast.LENGTH_SHORT).show();
			return;
		}else if("".equals(codeStr)){
			ToastCompat.makeText(context,"图片验证码不能为空", Toast.LENGTH_SHORT).show();
			return ;
		}else {
			String code = codeUtils.getCode();
			if (!code.equalsIgnoreCase(codeStr)) {
				ToastCompat.makeText(context,"图片验证码不正确", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		showLoadingDialog();
		OkGo.get(Urls.SendcodeURL)
				.params("userName",userName)
				.execute(new JsonCallback<IycResponse<CodeReturn>>(context) {
					@Override
					public void onSuccess(IycResponse<CodeReturn> codeReturnIycResponse, Call call, Response response) {
						ToastCompat.makeText(context,getString(R.string.code_has_send), Toast.LENGTH_SHORT).show();
						recLen = 60;
						handler.postDelayed(runnable, 0);
						mCode = codeReturnIycResponse.getData().getCode();
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
						ToastCompat.makeText(context,e.getMessage(),Toast.LENGTH_SHORT);
					}

					@Override
					public void onAfter(IycResponse<CodeReturn> codeReturnIycResponse, Exception e) {
						super.onAfter(codeReturnIycResponse, e);
						dismissLoadingDialig();
					}

				});
	}

	/**
	 * 给手机发送验证码
	 * @param type true表示给手机发送验证码，false表示给邮箱发送验证码
	 * @param emailOrMobile 手机号或者是邮箱号
	 */
	private void sendCodeToPhone(boolean type,String emailOrMobile){
		String codeStr=randomCode_et.getText().toString().trim();
		if(isNull(context,emailOrMobile,"请输入手机号")||!isValidPhoneNumber(emailOrMobile)){
			return;
		}else if("".equals(codeStr)){
			ToastCompat.makeText(context,"图片验证码不能为空", Toast.LENGTH_SHORT).show();
			return ;
		}else {
			String code = codeUtils.getCode();
			if (!code.equalsIgnoreCase(codeStr)) {
				ToastCompat.makeText(context,"图片验证码不正确", Toast.LENGTH_SHORT).show();
				return;
			}
		}

		OkGo.get(Urls.AccountForSure)
				.params("type",type)
				.params("emailormobile",emailOrMobile)
				.execute(new JsonCallback<IycResponse<CodeReturn>>(context) {
					@Override
					public void onSuccess(IycResponse<CodeReturn> codeReturnIycResponse, Call call, Response response) {
						ToastCompat.makeText(context,getString(R.string.code_has_send), Toast.LENGTH_LONG).show();
						recLen = 60;
						handler.postDelayed(runnable, 0);
						mCode = codeReturnIycResponse.getData().getCode();
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
						ToastCompat.makeText(context,e.getMessage(),Toast.LENGTH_SHORT);
					}

					@Override
					public void onAfter(IycResponse<CodeReturn> codeReturnIycResponse, Exception e) {
						super.onAfter(codeReturnIycResponse, e);
						dismissLoadingDialig();
						try{
							mCode = codeReturnIycResponse.getData().getCode();
						}catch(Exception ee){
							ee.printStackTrace();
						}

					}
				});
	}

	private void bindEmail(String userAccount){
		if(isEmailAccount(userAccount)){
			changeEmailAccount(userAccount,false);
		}
	}
	//检测是否是有效的大陆的号码
	private boolean isValidPhoneNumber(String userAccount){
		if(!PatternUtils.isNumberic(userAccount)||userAccount.length() != 11){
			ToastCompat.makeText(context,"请输入正确格式的手机号",Toast.LENGTH_SHORT).show();
		}
		return PatternUtils.isNumberic(userAccount) && userAccount.length() == 11;
	}

	private void bindPhone(String userAccount){
		if(isValidPhoneNumber(userAccount)){
			changeEmailAccount(userAccount,true);
		}
	}

	/**
	 * 更改绑定账号
	 * @param userAccount 更改或者绑定的邮箱或手机号
	 * @param type userAccount的类型：为手机号时传true,为邮箱号时传false
	 */
	private void changeEmailAccount(String userAccount, boolean type){
		long userId = CommonUtil.getUserId();
		if(!CommonUtil.getLoginState()){
			ToastCompat.makeText(context,getString(R.string.login_please),Toast.LENGTH_SHORT).show();
			return;
		}
		showLoadingDialog();
		OkGo.get(Urls.BINGEMAIL)
					.params("emailormobile",userAccount)
					.params("type",type)
					.params("userId",userId)
				    .execute(new JsonCallback<IycResponse<String>>(this) {
					@Override
					public void onSuccess(IycResponse<String> stringIycResponse, Call call, Response response) {
							ToastCompat.makeText(context,stringIycResponse.msg,Toast.LENGTH_SHORT).show();
							finish();
						}

						@Override
						public void onError(Call call, Response response, Exception e) {
							super.onError(call, response, e);
							ToastCompat.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onAfter(IycResponse<String> s, Exception e) {
							super.onAfter(s, e);
							dismissLoadingDialig();
						}
					});

	}

	@Override
	protected void onStop() {
		super.onStop();
		isStop = true;
	}

	private void changeRandomCode(){
		Bitmap bitmap = codeUtils.createBitmap();
		randomImage.setImageBitmap(bitmap);
	}
}
