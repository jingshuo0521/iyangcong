package com.iyangcong.reader.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.dialog.listener.OnBtnClickL;
import com.iyangcong.reader.MainActivity;
import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.MineAgreement;
import com.iyangcong.reader.bean.TeacherClassInfo;
import com.iyangcong.reader.bean.ThirdLoginReturn;
import com.iyangcong.reader.bean.User;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.callback.JsonCallback2;
import com.iyangcong.reader.callback.ResponseClassBean;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.UserDao;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.button.FlatButton;
import com.iyangcong.reader.ui.dialog.AgreementDialog;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.StringUtils;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.SocializeUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.IntentUtils.goToWebViewActivity;

//import com.umeng.socialize.utils.Log;

/**
 * Created by sheng on 2016/12/26.
 */

public class LoginActivity extends SwipeBackActivity implements AdapterView.OnItemClickListener, PopupWindow.OnDismissListener ,WbAuthListener{

    //    @BindView(R.id.textView2)
//    TextView textView2;
    @BindView(R.id.login_account)
    EditText loginAccount;
    @BindView(R.id.login_password)
    EditText loginPassword;
    @BindView(R.id.login_button)
    FlatButton login_button;
    @BindView(R.id.register_now)
    TextView register_now;
    @BindView(R.id.forget_password)
    TextView forget_password;
    @BindView(R.id.qq)
    ImageView qq;
    @BindView(R.id.sina)
    ImageView sina;
    @BindView(R.id.wechat)
    ImageView wechat;
    @BindView(R.id.douban)
    ImageView douban;
    @BindView(R.id.tv_Agreement)
    TextView tvAgreement;
    @BindView(R.id.userId_LinearLayout)
    LinearLayout mUserIdLinearLayout;
    @BindView(R.id.login_more_user)
    ImageView ivLoginMoreUser;

    private MineAgreement Agreement = new MineAgreement();
    private BaseAnimatorSet mBasIn;
    private BaseAnimatorSet mBasOut;
    private Context mContext = this;
    private int keyfrom;//1 from thirdpart

    private SHARE_MEDIA[] thirdLoginList = {SHARE_MEDIA.WEIXIN, SHARE_MEDIA.QQ, SHARE_MEDIA.SINA, SHARE_MEDIA.DOUBAN};
    public static  ArrayList<SnsPlatform> platforms = new ArrayList<SnsPlatform>();
    //public static loginType =0;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private int thirdLoginType = 0;
    //private SsoHandler mSsoHandler;
    private List<TeacherClassInfo> classinfos;

    private List<String> classNames = new ArrayList<>();
    private List<String> classIds = new ArrayList<>();
    private String thirdPartUserId;
    private long userId;

    @Override
    public void onSuccess(Oauth2AccessToken oauth2AccessToken) {
        thirdLogin(oauth2AccessToken.getUid(),"");
    }

    @Override
    public void cancel() {
        dismissLoadingDialig();
    }

    @Override
    public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
        dismissLoadingDialig();
        ToastCompat.makeText(LoginActivity.this, "失败：" + wbConnectErrorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
    }

    public MineAgreement getAgreement() {
        return Agreement;
    }

    public void setAgreement(MineAgreement agreement) {
        Agreement = agreement;
    }

    private PopupWindow mPop; // 下拉弹出窗
    private ListView lvAccounts; // 下拉弹出窗显示的ListView对象
    private AccountAapter accountAapter; // ListView的监听器
    List<String> accounts = new ArrayList<>();
    private String mAccountString;
    private String mPwdString;


    @OnClick({R.id.tv_Agreement,R.id.tv_policy,R.id.forget_password, R.id.qq, R.id.wechat, R.id.sina, R.id.douban, R.id.login_button, R.id.register_now, R.id.login_more_user})
    public void onClick(View view) {
//        if(mSsoHandler!=null)
//            mSsoHandler = null;
        switch (view.getId()) {
            case R.id.tv_Agreement:
                goToWebViewActivity(LoginActivity.this, Urls.URL + "/onion/user_protocol.html");
                break;
            case R.id.tv_policy:
                goToWebViewActivity(LoginActivity.this, Urls.URL + "/onion/privacy.html");
                break;
            case R.id.login_button:
                if (loginAccount.getText().toString().equals("")) {
                    ToastCompat.makeText(this, "请正确填写手机号或邮箱", Toast.LENGTH_LONG).show();
                } else if (loginPassword.getText().toString().equals("")) {
                    ToastCompat.makeText(this, "请输入密码", Toast.LENGTH_LONG).show();
                } else {
                    login();
                }
                break;
            case R.id.register_now:
                Intent intent_register = new Intent(this, RegisterActivity.class);
                startActivity(intent_register);
                finish();
                break;
            case R.id.forget_password:
                Intent intent_forget = new Intent(this, FindPassWordActivity.class);
                startActivity(intent_forget);
                break;
            case R.id.qq:
                goToAuth(1);
                showLoadingDialog();
                break;
            case R.id.sina:
//                mSsoHandler = new SsoHandler(LoginActivity.this);
//                mSsoHandler.authorize(this);
                goToAuth(2);
                showLoadingDialog();
                break;
            case R.id.wechat:
                goToAuth(0);
                showLoadingDialog();
                break;
            case R.id.douban:
                goToAuth(3);
                showLoadingDialog();
                break;
            case R.id.login_more_user:
                if (mPop == null) {
                    initPop();
                }
                if (!mPop.isShowing() && accounts.size() > 0) {
                    // Log.i(TAG, "切换为角向上图标");
                    ivLoginMoreUser.setImageResource(R.drawable.login_more_down); // 切换图标
                    mPop.showAsDropDown(mUserIdLinearLayout, 2, 1); // 显示弹出窗口
                }
                break;
        }
    }


    //登录接口
    private void login() {
        showLoadingDialog();
        String pwdMD5 = StringUtils.MD5(loginPassword.getText().toString());
        OkGo.get(Urls.LoginURL)
                .params("passWord", pwdMD5)
                .params("userName", loginAccount.getText().toString())
                .params("MACAddress", CommonUtil.getLocalMacAddressFromIp(this))

                .execute(new JsonCallback<IycResponse<User>>(this) {
                    @Override
                    public void onSuccess(IycResponse<User> listIycResponse, Call call, Response
                            response) {
                        MainActivity.exitLogin=false;
                        saveAccountId();
                        ToastCompat.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                        User user = listIycResponse.getData();
                        userId=user.getUserId();
                        sharedPreferenceUtil.putLong(SharedPreferenceUtil.USER_ID, user.getUserId());
                        sharedPreferenceUtil.putBoolean(SharedPreferenceUtil.LOGIN_STATE, true);
                        sharedPreferenceUtil.putString(SharedPreferenceUtil.LOGIN_USER_ACCOUNT, loginAccount.getText().toString());
                        sharedPreferenceUtil.putInt(SharedPreferenceUtil.SEMESTER_ID, user.getSemesterId());
                        sharedPreferenceUtil.putString(SharedPreferenceUtil.USER_PORTAIT_URL, user.getPhotoUrl());
                        sharedPreferenceUtil.putInt(SharedPreferenceUtil.USER_TYPE, user.getUserType());
                        //shao add begin
                        sharedPreferenceUtil.putInt(SharedPreferenceUtil.LOGIN_TYPE,0);
                        sharedPreferenceUtil.putInt(SharedPreferenceUtil.SEMESTER_ID,user.getSemesterId());
                        sharedPreferenceUtil.putString(SharedPreferenceUtil.SEMESTER_NAME,user.getSemesterName());
                        //shao add end
//                        sharedPreferenceUtil.putInt(SharedPreferenceUtil.DELETED_DEFAULT_BOOK, -1);
                        UserDao userDao = new UserDao(DatabaseHelper.getHelper(LoginActivity.this));
                        userDao.add(user);
                        if(user.getUserType()==2||user.getUserType()==3){
                            getClassInfo();
                        }else {
                            if(keyfrom==0) {
                                if(user.getUserBoundSources()!=null){
                                    Map<String,Object> userBoundSources=user.getUserBoundSources();
                                    boolean HasBFSUAccount=false;
                                    if(userBoundSources.size()>0){
                                        for(Map.Entry<String,Object> entry:userBoundSources.entrySet()){
                                            if(entry.getKey().equals("bfsu")){
                                                try {
                                                    HasBFSUAccount=true;
                                                    thirdPartUserId=(String)entry.getValue();
                                                    new Get().start();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }
//                                            else{
//                                                sharedPreferenceUtil.putInt(SharedPreferenceUtil.SHOW_TYPE,0);
//                                                dismissLoadingDialig();
//                                                finish();
//                                            }
                                        }
                                        if(!HasBFSUAccount){
                                            sharedPreferenceUtil.putInt(SharedPreferenceUtil.SHOW_TYPE,0);
                                            dismissLoadingDialig();
                                            finish();
                                        }
                                    }else{
                                        dismissLoadingDialig();
                                        finish();
                                    }

                                }

                            }else if(keyfrom==Constants.THIRDPART_TYPE_BFSU){
                                sharedPreferenceUtil.putInt(SharedPreferenceUtil.SHOW_TYPE,5);
								Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                sharedPreferenceUtil.putInt(SharedPreferenceUtil.THIRTPART_TYPE,Constants.THIRDPART_TYPE_BFSU);//5
                                Bundle bundle = new Bundle();
                                bundle.putInt("redirectionType", 0);
                                intent.putExtras(bundle);
                                startActivity(intent);
                               
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissLoadingDialig();
                        String message;
                        if (e.getMessage().equals("Failed to connect to /42.159.121.164:80")) {
                            message = "网络故障，请稍后重试";
                        } else {
                            message = e.getMessage();
                        }
                        ToastCompat.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    class Get extends Thread {


        @Override
        public void run() {

            String url = "http://appd10.beiwaionline.com/app/getLearnerStatus/"+thirdPartUserId;
            try {

                String result = doGet(url);
                JSONObject o= new JSONObject(result);

                String k=(String)o.getString("code");
                if("1".equals(k)){
                    JSONObject oo=o.getJSONObject("result");
                    String status=oo.getString("status");
                    if(status.equals("100000302")){
                        //不合法，则删除后台的绑定，避免重复请求
                        cutThirdPartBind(userId);
                    }else if(status.equals("100000301")){
                        //cutThirdPartBind(userId);
                        sharedPreferenceUtil.putInt(SharedPreferenceUtil.SHOW_TYPE,5);
//                        Intent intent = new  Intent();
//                        //设置intent的动作为com.example.broadcast，可以任意定义
//                        intent.setAction("com.iyangcong.reader.loginactivity.statechange");
//                        //发送无序广播
//                        sendBroadcast(intent);
                        finish();
                    }
                }else{
                    sharedPreferenceUtil.putInt(SharedPreferenceUtil.SHOW_TYPE,0);
                }

               // String data=result;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void cutThirdPartBind(long userId){


        OkGo.get(Urls.CutconnectURL)
                .params("userId",userId)
                .params("type", 5)
                .execute(new JsonCallback<IycResponse<Integer>>(LoginActivity.this) {
                    @Override
                    public void onSuccess(IycResponse<Integer> listIycResponse, Call call, Response
                            response) {
                        dismissLoadingDialig();
                        Integer userReturn = listIycResponse.getData();
                        sharedPreferenceUtil.putInt(SharedPreferenceUtil.SHOW_TYPE,0);
                        finish();

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissLoadingDialig();

                        super.onError(call, response, e);
                    }

                    @Override
                    public void parseError(Call call, Exception e) {

                        dismissLoadingDialig();
                        super.parseError(call, e);
                    }
                });
    }

    public  String doGet(String httpurl) {
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        String result = null;// 返回结果字符串
        try {
            // 创建远程url连接对象
            URL url = new URL(httpurl);
            // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接方式：get
            connection.setRequestMethod("GET");
            // 设置连接主机服务器的超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            connection.setReadTimeout(60000);
            // 发送请求
            connection.connect();
            // 通过connection连接，获取输入流
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                // 封装输入流is，并指定字符集
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                // 存放数据
                StringBuffer sbf = new StringBuffer();
                String temp = null;
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append("\r\n");
                }
                result = sbf.toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            connection.disconnect();// 关闭远程连接
        }

        return result;
    }


    /**
     * 保存账号到历史记录
     */
    private void saveAccountId() {
        boolean hasSave = false;
        for (String ids : accounts) {
            if (ids.equals(mAccountString)) {
                hasSave = true;
            }
        }
        if (!hasSave) {
            sharedPreferenceUtil.addArray(SharedPreferenceUtil.ACCOUNT_IDS, mAccountString);
            accounts = sharedPreferenceUtil.getArray(SharedPreferenceUtil.ACCOUNT_IDS);
        }
    }

    /**
     * 根据type进入不同的第三方登录界面
     *
     * @param type
     */
    private void goToAuth(int type) {
        if (type == 3) {
            UMShareAPI.get(this).doOauthVerify(this, platforms.get(type).mPlatform, authListener);
        } else {
            UMShareAPI.get(this).getPlatformInfo(this, platforms.get(type).mPlatform, authListener);
        }
        thirdLoginType = type + 1;
    }


    @Override
    protected void initData(Bundle savedInstanceState) {
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
    }

    @Override
    protected void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        accounts = sharedPreferenceUtil.getArray(SharedPreferenceUtil.ACCOUNT_IDS);
        accountAapter = new AccountAapter(this, accounts);
        LinearLayout parent = (LinearLayout) getLayoutInflater().inflate(
                R.layout.userifo_listview, null);
        lvAccounts = (ListView) parent.findViewById(android.R.id.list);
        parent.removeView(lvAccounts); // 必须脱离父子关系,不然会报错
        lvAccounts.setOnItemClickListener(LoginActivity.this); // 设置点击事
        lvAccounts.setAdapter(accountAapter);
        setListener();
        if (accounts.size() > 0) {
            loginAccount.setText(accounts.get(0));
        }
    }

    public void initPop() {
        int width = mUserIdLinearLayout.getWidth() - 4;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mPop = new PopupWindow(lvAccounts, width, height, true);
        mPop.setOnDismissListener(this);// 设置弹出窗口消失时监听器

        // 注意要加这句代码，点击弹出窗口其它区域才会让窗口消失
        mPop.setBackgroundDrawable(new ColorDrawable(0xffffffff));
    }

    private void setListener() {
        loginAccount.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                mAccountString = s.toString();
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void setMainHeadView() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init3rdPlatforms();
        ButterKnife.bind(this);
        initView();
        init3rdLoginListener();
        Bundle mBundle = this.getIntent().getExtras();
              if(mBundle!=null){
                  keyfrom = mBundle.getInt("keyfrom");
              }
//        getDatasFromNetwork();
    }

    /**
     * 实现第三方登录初始化
     */

    private void init3rdPlatforms() {
        platforms.clear();
        for (SHARE_MEDIA e : thirdLoginList) {
            if (!e.toString().equals(SHARE_MEDIA.GENERIC.toString())) {
                platforms.add(e.toSnsPlatform());
            }
        }
    }

    /**
     * 第三方监听事件注册
     */
    private void init3rdLoginListener() {
/*        UMShareAPI.get(this).fetchAuthResultWithBundle(this, savedInstanceState, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
                SocializeUtils.safeShowDialog(dialog);
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                Toast.makeText(getApplicationContext(), "onRestoreInstanceState Authorize succeed", Toast.LENGTH_SHORT).show();
                shareAdapter.notifyDataSetChanged();
                SocializeUtils.safeCloseDialog(dialog);
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                Toast.makeText(getApplicationContext(), "onRestoreInstanceState Authorize onError", Toast.LENGTH_SHORT).show();
                shareAdapter.notifyDataSetChanged();
                SocializeUtils.safeCloseDialog(dialog);
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                Toast.makeText(getApplicationContext(), "onRestoreInstanceState Authorize onCancel", Toast.LENGTH_SHORT).show();
                shareAdapter.notifyDataSetChanged();
                SocializeUtils.safeCloseDialog(dialog);
            }
        });*/
    }
    private void thirdLogin(final String uId,final String thirdName) {

        OkGo.get(Urls.BookMarketTestBound)
                .params("macAddress", CommonUtil.getLocalMacAddressFromIp(LoginActivity.this))
                .params("thirdLoginID", uId)
                .params("thirdLoginType", thirdLoginType)
                .execute(new JsonCallback<IycResponse<ThirdLoginReturn>>(LoginActivity.this) {
                    @Override
                    public void onSuccess(IycResponse<ThirdLoginReturn> listIycResponse, Call call, Response
                            response) {
                        dismissLoadingDialig();
                        ThirdLoginReturn userReturn = listIycResponse.getData();
                        if (userReturn.getIsbinding() == 0) {
                            ToastCompat.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                            Logger.e("test", "isBinding:" + userReturn.getIsbinding());
                            Logger.e("test", "userId:" + userReturn.getUserId());
                            sharedPreferenceUtil.putLong(SharedPreferenceUtil.USER_ID, userReturn.getUserId());
                            sharedPreferenceUtil.putBoolean(SharedPreferenceUtil.LOGIN_STATE, true);
                            sharedPreferenceUtil.putInt(SharedPreferenceUtil.SEMESTER_ID, userReturn.getSemesterId());
                            sharedPreferenceUtil.putString(SharedPreferenceUtil.LOGIN_USER_ACCOUNT, loginAccount.getText().toString());
                            //shao add begin
                            sharedPreferenceUtil.putInt(SharedPreferenceUtil.LOGIN_TYPE,thirdLoginType);
                            sharedPreferenceUtil.putInt(SharedPreferenceUtil.USER_TYPE, userReturn.getUserType());
                            sharedPreferenceUtil.putString(SharedPreferenceUtil.SEMESTER_NAME,userReturn.getSemesterName());
                            if(userReturn.getUserType()==2||userReturn.getUserType()==3){
                                getClassInfo();
                            }else {
                                dismissLoadingDialig();
                                if(keyfrom==1){
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                                    startActivity(intent);
                                }
                                finish();
                            }
                            //shao add end
                            //finish();
//                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                startActivity(intent);
                        } else {
                            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                            intent.putExtra("state", "BINDING");
                            intent.putExtra("thirdLoginID", uId);
                            intent.putExtra("thirdLoginType", thirdLoginType + "");
                            intent.putExtra("thirdName",thirdName);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissLoadingDialig();
                        ToastCompat.makeText(LoginActivity.this, "第三方登录失败，请重新登陆", Toast.LENGTH_SHORT).show();
                        super.onError(call, response, e);
                    }

                    @Override
                    public void parseError(Call call, Exception e) {
                        ToastCompat.makeText(LoginActivity.this, "第三方登录失败，请重新登陆", Toast.LENGTH_SHORT).show();
                        dismissLoadingDialig();
                        super.parseError(call, e);
                    }
                });
    }
    /**
     * 第三方回调监听
     */
    UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            SocializeUtils.safeShowDialog(dialog);
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            SocializeUtils.safeCloseDialog(dialog);
            for (String in : data.keySet()) {
                //map.keySet()返回的是所有key的值
                String str = data.get(in);//得到每个key多对用value的值
                Logger.e("test", in + "    9 " + str);
            }
            final String uId;
            switch (thirdLoginType){
//                case 1:
//                    uId = data.get("openid");
//                    break;
                case 3:
                case 4:
                    uId = data.get("uid");
                    break;
                default:
                    uId = data.get("unionid");
                    break;
            }
            final String name = data.get("name");
            thirdLogin(uId,name);
        }



        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            SocializeUtils.safeCloseDialog(dialog);
            dismissLoadingDialig();
            ToastCompat.makeText(LoginActivity.this, "失败：" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            SocializeUtils.safeCloseDialog(dialog);
            dismissLoadingDialig();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(mSsoHandler == null)
            UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
//        else
//            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        UMShareAPI.get(this).onSaveInstanceState(outState);
    }

    //shao add begin

    private void getClassInfo(){
        int semesterId=sharedPreferenceUtil.getInt(SharedPreferenceUtil.SEMESTER_ID,-1);
        OkGo.get(Urls.TeacherClassInfos)
                .tag(this)
                .params("semesterId",semesterId)
                .params("userId",CommonUtil.getUserId())
                .execute(new JsonCallback2<ResponseClassBean<List<Map<String,Object>>>>(mContext) {
                    @Override
                    public void onSuccess(ResponseClassBean<List<Map<String,Object>>> listIycResponse, Call call, Response response) {
                        // Logger.i("succeed", listIycResponse.getData().toString());
                        List<Map<String,Object>> classes = listIycResponse.data;
                        int size =classes.size();
                        for(int i=0;i<size;i++){
                            Map<String,Object> item = (Map<String,Object>)classes.get(i);
                            String grade = (String)item.get("grade");
                            List<TeacherClassInfo> classin = (List<TeacherClassInfo>)item.get("classInfo");

                            int size_z=classin.size();
                            if(size_z>0){
                                classinfos=classin;
                                for(int j=0;j<size_z;j++){
                                    Map<String,Object> cMap= (Map<String,Object>)classin.get(j);
                                    TeacherClassInfo classitem =new TeacherClassInfo();
                                    double class_id=(double)cMap.get("class_id");
                                    String class_name=(String)cMap.get("class_name");
                                    classNames.add(class_name);
                                    classIds.add(""+(int)class_id);
                                }
                            }
                        }
                        String class_names= listToString(classNames);
                        String clcass_ids=listToString(classIds);
                        sharedPreferenceUtil.putString(SharedPreferenceUtil.CLASS_NAMES, class_names);
                        sharedPreferenceUtil.putString(SharedPreferenceUtil.CLASS_IDS, clcass_ids);
                        dismissLoadingDialig();
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        dismissLoadingDialig();
                        //reFreshOrLoadMoreSuccess(false);
                        //ToastCompat.makeText(context, context.getResources().getString(R.string.socket_exception_error), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //shao add end
    private void AgreementDialog() {
        final AgreementDialog dialog = new AgreementDialog(mContext);
        dialog.content(Agreement.getUserAgreement())
                .titleTextSize(20)//
                .btnNum(1)
                .titleLineColor(Color.parseColor("#e6e5e5"))
                .btnText("确定")
                .title("用户协议")
                .showAnim(mBasIn)//
                .dismissAnim(mBasOut)//
                .show();

        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
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

    private void getDatasFromNetwork() {
        OkGo.get(Urls.PersonUserAgreementURL)
                .tag(this)
                .execute(new JsonCallback<IycResponse<MineAgreement>>(this) {
                    @Override
                    public void onSuccess(IycResponse<MineAgreement> mineAgreementIycResponse, Call call, Response response) {
                        setAgreement(mineAgreementIycResponse.getData());
                        Logger.i("darkflame" + Agreement.getUserAgreement());
                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        loginAccount.setText(accounts.get(i));
        mPop.dismiss();
    }

    @Override
    public void onDismiss() {
        ivLoginMoreUser.setImageResource(R.drawable.login_more_up);
    }

    /* ListView的适配器 */
    class AccountAapter extends BaseAdapter {

        private Context mContext;
        private List<String> accounts;

        public AccountAapter(Context context, List<String> accounts) {
            this.mContext = context;
            this.accounts = accounts;
        }

        @Override
        public int getCount() {
            return accounts.size();
        }

        @Override
        public Object getItem(int i) {
            return accounts.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(
                        R.layout.lv_item_account, null);
            }

            TextView userIdText = (TextView) convertView
                    .findViewById(R.id.listview_userid);
            userIdText.setText(accounts.get(position));

            ImageView deleteUser = (ImageView) convertView
                    .findViewById(R.id.login_delete_user);
            deleteUser.setOnClickListener(new View.OnClickListener() {
                // 点击删除deleteUser时,在mUsers中删除选中的元素
                @Override
                public void onClick(View v) {

                    if (getItem(position).equals(mAccountString)) {
                        // 如果要删除的用户Id和Id编辑框当前值相等，则清空
                        mAccountString = "";
                        mPwdString = "";
                        loginAccount.setText(mAccountString);
                        loginPassword.setText(mPwdString);
                    }
                    accounts.remove(getItem(position));
                    sharedPreferenceUtil.removeArray(SharedPreferenceUtil.ACCOUNT_IDS);
                    sharedPreferenceUtil.putArray(SharedPreferenceUtil.ACCOUNT_IDS, accounts);
                    accountAapter.notifyDataSetChanged(); // 更新ListView
                }
            });
            return convertView;
        }

    }

    public static String listToString(List<String> list){

        if(list==null){
            return null;
        }

        StringBuilder result = new StringBuilder();
        boolean first = true;

        //第一个前面不拼接","
        for(String string :list) {
            if(first) {
                first=false;
            }else{
                result.append(",");
            }
            result.append(string);
        }
        return result.toString();
    }

    private List<String> stringToList(String strs){
        String str[] = strs.split(",");
        return Arrays.asList(str);

    }


}
