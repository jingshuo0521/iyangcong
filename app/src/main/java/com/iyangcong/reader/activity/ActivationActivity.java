package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.CodeReturn;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.callback.JsonCallback2;
import com.iyangcong.reader.callback.ResponseBean;
import com.iyangcong.reader.callback.SimpleBean;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.button.FlatButton;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.StringUtils;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;




public class ActivationActivity extends SwipeBackActivity {

	@BindView(R.id.btnBack)
	ImageButton btnBack;
	@BindView(R.id.textHeadTitle)
	TextView textHeadTitle;



	@BindView(R.id.activate_button)
	FlatButton activate_button;
	@BindView(R.id.login_school)
	AutoCompleteTextView login_school;
	@BindView(R.id.login_school_number)
	EditText login_school_number;
	@BindView(R.id.login_name)
	EditText login_name;
	@BindView(R.id.login_activate_code)
	EditText login_activate_code;
	@BindView(R.id.login_activate_password)
	EditText login_activate_password;




	public String data;
	public List<String> suggest;
	public Map<Integer,String> schools=new HashMap<Integer,String>();
	private int selectedSchoolId;
	private int userId;
	private String nickName;
	private String userName;
	private String account;


	public ArrayAdapter<String> aAdapter;
	private SharedPreferenceUtil sharedPreferenceUtil;
	private String userAccount;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activation);
		Intent intent = getIntent();

		Bundle bundle = intent.getExtras();
		String strContentString = bundle.getString("fromMain");
		userName=bundle.getString(Constants.USER_NAME,"");
		//userName=intent.getIntExtra(Constants.USER_NAME,"0");
		nickName=bundle.getString(Constants.NICK_NAME,"");
		//userId = bundle.getInt("");
		account=bundle.getString(Constants.USER_ACCOUNT,"");
		ButterKnife.bind(this);
		setMainHeadView();
		initView();
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		//handler = new Handler();
		/*isBinding = getIntent().getBooleanExtra(Constants.IS_BINDING,true);
		bindType = (BindType)getIntent().getSerializableExtra(Constants.BIND_TYPE);
		Logger.i("bindType:" + bindType.name());*/
//		sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
//
//		userAccount=sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_USER_ACCOUNT, null);

	}

	@Override
	protected void initView() {
		login_school.addTextChangedListener(new TextWatcher(){

			public void afterTextChanged(Editable editable) {

				Log.i("shao","afterTextChanged------");
				if(schools.size()>0){
					for (Map.Entry<Integer, String> entry : schools.entrySet()) {
						if(!login_school.getText().equals("")||login_school.getText()!=null)
						if(entry.getValue().equals(login_school.getText().toString())){
							selectedSchoolId=entry.getKey();
							Log.i("shao","schoolid------"+selectedSchoolId);
						}
						System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
					}
				}

			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				Log.i("shao","beforeTextChanged");
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String newText = s.toString();
				new getJson().execute(newText);
			}

		});
	}

	@Override
	protected void setMainHeadView() {
	    textHeadTitle.setText("学校账号激活");
        btnBack.setImageResource(R.drawable.btn_back);
	}







	@OnClick({R.id.btnBack, R.id.activate_button})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnBack:
				finish();
				break;
			case R.id.activate_button:

				//checkUser();
                doActivate();
				break;

			default:break;
		}
	}


	class getJson extends AsyncTask<String,String,String> {

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			aAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.item_schools,suggest);
			login_school.setAdapter(aAdapter);
			aAdapter.notifyDataSetChanged();
		}

		@Override
		protected String doInBackground(String... key) {
			String newText = key[0];
			newText = newText.trim();
			newText = newText.replace(" ", "+");
			try{
				HttpClient hClient = new DefaultHttpClient();
				//HttpGet hGet = new HttpGet("http://en.wikipedia.org/w/api.php?action=opensearch&search="+newText+"&limit=8&namespace=0&format=json");
				HttpGet hGet = new HttpGet(Urls.GetSchools+"?universityName="+login_school.getText().toString());
				ResponseHandler<String> rHandler = new BasicResponseHandler();
				data = hClient.execute(hGet,rHandler);
				//JSONObject o= new JSONObject()
				suggest = new ArrayList<String>();
				JSONObject o=new JSONObject(data);
				JSONArray jArray = new JSONArray(o.get("data").toString());
				for(int i=0;i<jArray.length();i++){
					JSONObject oo =(JSONObject) jArray.get(i);
					int sid=(Integer)oo.get("universityId");
					String sname=(String)oo.get("universityName");
					schools.put(sid,sname);
					suggest.add(sname);
				}



			}catch(Exception e){
				Log.w("Error", e.getMessage());
			}
			return null;
		}

	}

	private void checkUser(){
		showLoadingDialog();
		OkGo.get(Urls.CheckUser)
				.params("sid",login_school_number.getText().toString())
				.params("universityId",selectedSchoolId)
				.params("studentName",login_name.getText().toString())
				.params("randomCode",login_activate_code.getText().toString())
				//.params("sid","1616")
				//.params("universityId","77")
				//.params("studentName","园园")
				//.params("randomCode","666888")
				.execute(new JsonCallback2<ResponseBean<SimpleBean>>(context) {
//					@Override
//					public void onSuccess(IycResponse<CodeReturn> codeReturnIycResponse, Call call, Response response) {
//						IYangCongToast.show(context,"success", IYangCongToast.LENGTH_LONG);
//						//验证通过，则激活
//						activateStudentAccount(Integer.parseInt(login_school_number.getText().toString()));
//
//					}

					@Override
					public void onSuccess(ResponseBean<SimpleBean> simpleBeanResponseBean, Call call, Response response) {
						//IYangCongToast.show(context,"success", IYangCongToast.LENGTH_LONG);
						//验证通过，则激活
						//activateStudentAccount(Integer.parseInt(login_school_number.getText().toString()));
                        Map<String,Object> data = simpleBeanResponseBean.data;
                        if(data!=null){
							double id= (double)data.get("u_sId");
							int u_sId =(int)id;
							activateStudentAccount(u_sId);

                        }else{
							ToastCompat.makeText(context,simpleBeanResponseBean.msg,Toast.LENGTH_LONG).show();
							dismissLoadingDialig();
						}

					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
						dismissLoadingDialig();
						ToastCompat.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
					}


				});
	}


	private void activateStudentAccount(final int u_sId){
        String passWord = StringUtils.MD5(login_activate_password.getText().toString());
		//String passWord = StringUtils.MD5("pppppp");
		OkGo.get(Urls.AcitivateStudentAccount)
				.params("u_sId",u_sId)
				.params("userName",account)
				.params("nickname",nickName)
				.params("passWord",passWord)
				.params("userId",CommonUtil.getUserId())
				.execute(new JsonCallback2<ResponseBean<SimpleBean>>(context) {
					@Override
					public void onSuccess(ResponseBean<SimpleBean> codeReturnIycResponse, Call call, Response response) {
						//IYangCongToast.show(context,"success", IYangCongToast.LENGTH_LONG);
						//验证通过，则激活
						Map<String,Object> data = codeReturnIycResponse.data;
						if(data!=null){
							CompleteActivate(u_sId);
						}else{
							dismissLoadingDialig();
							ToastCompat.makeText(context,codeReturnIycResponse.msg,Toast.LENGTH_LONG).show();
						}


					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);

						String msg = e.getMessage();
						if(msg.contains("可进行下一步")){
							CompleteActivate(u_sId);
						}else{
							ToastCompat.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
							dismissLoadingDialig();
						}
					}

					@Override
					public void onAfter(ResponseBean<SimpleBean>codeReturnIycResponse, Exception e) {
						super.onAfter(codeReturnIycResponse, e);
						//dismissLoadingDialig();

					}
				});
	}


        private void CompleteActivate(int u_sId){
            OkGo.get(Urls.CompleteActivite)
                    .params("u_sId",u_sId)
                    .params("landu_ids",null)
                    .params("languages_ids",null)
                    .params("subject_ids",null)
                    .params("userId",CommonUtil.getUserId())
                    .execute(new JsonCallback2<ResponseBean<SimpleBean>>(context) {
                        @Override
                        public void onSuccess(ResponseBean<SimpleBean> codeReturnIycResponse, Call call, Response response) {
                        	if(codeReturnIycResponse.Code==0){
								dismissLoadingDialig();
								ToastCompat.makeText(context,codeReturnIycResponse.msg, Toast.LENGTH_LONG).show();
								setResult(0);
								finish();

							}else{
								dismissLoadingDialig();
								ToastCompat.makeText(context,codeReturnIycResponse.msg,Toast.LENGTH_LONG).show();
							}

                            //验证通过，则激活


                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
							dismissLoadingDialig();
                            ToastCompat.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onAfter(ResponseBean<SimpleBean> codeReturnIycResponse, Exception e) {
                            super.onAfter(codeReturnIycResponse, e);
                           // dismissLoadingDialig();

                        }
                    });
        }



    private void doActivate(){
        showLoadingDialog();
		String passWord = StringUtils.MD5(login_activate_password.getText().toString());
		//String passWord = StringUtils.MD5("pppppp");
        OkGo.get(Urls.AppActivate)
                .params("sid",login_school_number.getText().toString())
                .params("universityId",selectedSchoolId)
                .params("studentName",login_name.getText().toString())
                .params("randomCode",login_activate_code.getText().toString())
//                .params("sid","1616")
//                .params("universityId","77")
//                .params("studentName","园园")
//                .params("randomCode","666888")
				.params("landu_ids",null)
				.params("languages_ids",null)
				.params("subject_ids",null)
				.params("userId",CommonUtil.getUserId())
				.params("userName",account)
				.params("nickname",nickName)
				.params("passWord",passWord)
                .execute(new JsonCallback<IycResponse<CodeReturn>>(context) {
//					@Override
//					public void onSuccess(IycResponse<CodeReturn> codeReturnIycResponse, Call call, Response response) {
//						IYangCongToast.show(context,"success", IYangCongToast.LENGTH_LONG);
//						//验证通过，则激活
//						activateStudentAccount(Integer.parseInt(login_school_number.getText().toString()));
//
//					}

                    @Override
                    public void onSuccess(IycResponse<CodeReturn> simpleBeanResponseBean, Call call, Response response) {
                        //IYangCongToast.show(context,"success", IYangCongToast.LENGTH_LONG);
                        //验证通过，则激活
                        //activateStudentAccount(Integer.parseInt(login_school_number.getText().toString()));
						ToastCompat.makeText(context,simpleBeanResponseBean.getMsg(),Toast.LENGTH_LONG).show();
						dismissLoadingDialig();
						setResult(0);
						finish();
//                        Map<String,Object> data = simpleBeanResponseBean.data;
//                        if(data!=null){
//                            double id= (double)data.get("u_sId");
//                            int u_sId =(int)id;
//                            activateStudentAccount(u_sId);
//
//                        }else{
//                            ToastCompat.makeText(context,simpleBeanResponseBean.msg,Toast.LENGTH_LONG).show();
//                            dismissLoadingDialig();
//                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dismissLoadingDialig();
                        ToastCompat.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                    }


                });
    }

	@Override
	protected void onStop() {
		super.onStop();
		//isStop = true;
	}
}
