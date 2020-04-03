package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.adapter.DiscoverCircleCategoryAdapter;
import com.iyangcong.reader.bean.DiscoverCircleDescribe;
import com.iyangcong.reader.bean.DiscoverCirclePermission;
import com.iyangcong.reader.bean.DiscoverCreateCircle;
import com.iyangcong.reader.bean.DiscoverCrircleCategory;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.enumset.StateEnum;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.LoadCountHolder;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.Constants.CHAPTERID;
import static com.iyangcong.reader.utils.Constants.CREATE_CIRLCE;
import static com.iyangcong.reader.utils.NotNullUtils.isNull;

/**
 * 创建圈子的第一页，需要选择圈子类型和权限，需要把选择过的内容传给下一页；
 * 在创建该activity时，需要给activity传一个StateEnum类型，标记是创建圈子还是编辑圈子；
 * modified by wzp
 * 2017-03-09
 */
public class DiscoverNewCircle extends SwipeBackActivity {

	@BindView(R.id.btnBack)
	ImageButton btnBack;
	@BindView(R.id.textHeadTitle)
	TextView textHeadTitle;
	@BindView(R.id.btnFunction)
	ImageButton btnFunction;
	@BindView(R.id.layout_header)
	LinearLayout layoutHeader;
	@BindView(R.id.activity_discover_new_circle)
	LinearLayout activityDiscoverNewCircle;
	@BindView(R.id.category_recycler)
	RecyclerView categoryRecycler;
	@BindView(R.id.rg_permission)
	RadioGroup rgPermission;
	@BindView(R.id.rb_private)
	RadioButton rbPrivate;
	@BindView(R.id.rb_private_partly)
	RadioButton rbPrivatePartly;
	@BindView(R.id.rb_public)
	RadioButton rbPublic;
	@BindView(R.id.layout_discover_new_circle)
	LinearLayout layoutDiscoverNewCircle;
	private DiscoverCircleCategoryAdapter categoryAdapter;
	private List<DiscoverCrircleCategory> categoryList = new ArrayList<>();
	private DiscoverCirclePermission permission = new DiscoverCirclePermission();
	private DiscoverCreateCircle circle = new DiscoverCreateCircle();
	private LoadCountHolder holder = new LoadCountHolder();
	private StateEnum mStateEnum;
	private int mGroupId;

	@OnClick({R.id.btnBack, R.id.btnFunction})
	void onBtnClick(View view) {
		switch (view.getId()) {
			case R.id.btnBack:
				finish();
				break;
			case R.id.btnFunction:
				//TODO
				if (!canNext()) {
					ToastCompat.makeText(context, getResources().getString(R.string.choose_type_and_permission),Toast.LENGTH_SHORT).show();
					return;
				}

				Intent intent = new Intent(this, DiscoverNewCircle2.class);
				Bundle bundle = new Bundle();
				bundle.putInt(Constants.groupId,mGroupId);
				bundle.putSerializable(Constants.CREATE_CIRLE_OR_MODIFY, mStateEnum);
				bundle.putParcelable(Constants.CREATE_CIRLCE, circle);
				intent.putExtras(bundle);
				startActivity(intent);

		}


	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discover_new_circle);
//        addContentView(R.layout.activity_discover_new_circle,this);
		ButterKnife.bind(this);
		setMainHeadView();
		initView();
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		mStateEnum = (StateEnum) getIntent().getSerializableExtra(Constants.CREATE_CIRLE_OR_MODIFY);
		mGroupId = getIntent().getIntExtra(Constants.groupId,-1);
		if(mStateEnum == StateEnum.MODIFY){
			circle = getIntent().getParcelableExtra(CREATE_CIRLCE);
		}
		Logger.i("wzp mStateEnum:" + mStateEnum.name());
	}

	@Override
	protected void initView() {
		holder.setPage(-1);
		initVaryViewHelper(context,layoutDiscoverNewCircle, new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onResume();
			}
		});
		if(mStateEnum == StateEnum.MODIFY){
			if(circle!=null&&circle.getAuthority() == 1){
				rgPermission.check(R.id.rb_private);
				circle.setAuthority(circle.getAuthority());
				disableRadioGroup(rgPermission);
			}else{
				chooseByAuthority(circle.getAuthority());
				enableRadioGroup(rgPermission);
			}
		}else{
			enableRadioGroup(rgPermission);
		}
		rgPermission.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				RadioButton rb = (RadioButton) findViewById(i);
				permission.setAuthority(getAuthority(rb.getText().toString()));
				circle.setAuthority(permission.getAuthority());
				Logger.i("permission:" + permission.toString());
			}
		});
		categoryAdapter = new DiscoverCircleCategoryAdapter(context, categoryList, new DiscoverCircleCategoryAdapter.OnItemSelectedListener() {
			@Override
			public void onItemSelected(View view, int position) {
				for (DiscoverCrircleCategory category : categoryList)
					category.setClicked(false);
				categoryList.get(position).setClicked(true);
				categoryAdapter.notifyDataSetChanged();
				circle.setCategory(categoryList.get(position).getCategoryid());
				holder.setPage(position);
			}
		});
		categoryRecycler.setAdapter(categoryAdapter);
		LinearLayoutManager llm = new LinearLayoutManager(context);
		llm.setOrientation(LinearLayoutManager.HORIZONTAL);
		categoryRecycler.setLayoutManager(llm);
	}

	@Override
	protected void setMainHeadView() {
		textHeadTitle.setText(mStateEnum==StateEnum.CREATE?"创建圈子":"编辑圈子");
		btnBack.setImageResource(R.drawable.btn_back);
		btnFunction.setImageResource(R.drawable.ic_next);
		btnFunction.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getDatasFromNetwork();
	}

	private void getDatasFromNetwork() {
		showLoadingDialog();
		OkGo.get(Urls.DiscoverCircleCategoryURL)
				.execute(new JsonCallback<IycResponse<List<DiscoverCrircleCategory>>>(this) {
					@Override
					public void onSuccess(IycResponse<List<DiscoverCrircleCategory>> listIycResponse, Call call, Response response) {
						if(isNull(listIycResponse)||isNull(listIycResponse.getData()))
							return;
						categoryList.clear();
						int index = 0;
						listIycResponse.getData().remove(0);
						for (DiscoverCrircleCategory category : listIycResponse.getData()) {
							category.setClicked(false);
							categoryList.add(category);
							if(mStateEnum == StateEnum.MODIFY && circle!=null){
								if(circle.getCategory() == category.getCategoryid()){
									holder.setPage(index);
									categoryRecycler.scrollToPosition(index);
								}
							}
							index++;
						}
						if (holder != null && holder.getPage() > 0)
							categoryList.get(holder.getPage()).setClicked(true);
						categoryAdapter.notifyDataSetChanged();
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
						ToastCompat.makeText(context,getString(R.string.get_category_failure), Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onAfter(IycResponse<List<DiscoverCrircleCategory>> listIycResponse, Exception e) {
						super.onAfter(listIycResponse, e);
						dismissLoadingDialig();
					}
				});
	}


	/**
	 * 判断是否可以进入下一页，只有在选择过圈子类型和权限以后才能进入下一页
	 *
	 * @return
	 */
	private boolean canNext() {
		if (circle.getAuthority() != 0)
			if (circle.getCategory() != 0)
				return true;
		return false;
	}

	/**
	 * 将权限由字符串的形式转化为数字的形式
	 * 1对应私密
	 * 2对应半私密
	 * 3对应公开
	 * 0对应异常
	 *
	 * @param authority
	 * @return
	 */
	private int getAuthority(String authority) {
		if (authority.equals(rbPrivate.getText()))
			return 1;
		else if (authority.equals(rbPrivatePartly.getText()))
			return 2;
		else if (authority.equals(rbPublic.getText()))
			return 3;
		return 0;
	}

	public void disableRadioGroup(RadioGroup testRadioGroup) {
		for (int i = 0; i < testRadioGroup.getChildCount(); i++) {
			testRadioGroup.getChildAt(i).setEnabled(false);
		}
	}

	public void enableRadioGroup(RadioGroup testRadioGroup) {
		for (int i = 0; i < testRadioGroup.getChildCount(); i++) {
			testRadioGroup.getChildAt(i).setEnabled(true);
		}
	}

	private void chooseByAuthority(int authority){
		switch (authority){
			case 1:
				rgPermission.check(R.id.rb_private);
				break;
			case 2:
				rgPermission.check(R.id.rb_private_partly);
				break;
			case 3:
				rgPermission.check(R.id.rb_public);
				break;
		}
	}
}
