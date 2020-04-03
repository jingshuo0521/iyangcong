package com.iyangcong.reader.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.NewWord;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.WordDao;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.service.PostWordsToRemoteDatabase;
import com.iyangcong.reader.ui.TextImageButton;
import com.iyangcong.reader.ui.swipebacklayout.SwipeBackActivity;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.DialogUtils;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
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

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

public class MineNewWordActivity extends SwipeBackActivity {


	@BindView(R.id.btnBack)
	ImageButton btnBack;
	@BindView(R.id.textHeadTitle)
	TextView textHeadTitle;
	@BindView(R.id.btnFunction)
	ImageButton btnFunction;
	@BindView(R.id.allnewword_count_tx)
	TextView allnewwordCountTx;
	@BindView(R.id.needrecite_count_tx)
	TextView needreciteCountTx;
	@BindView(R.id.readyrecite_count_tx)
	TextView readyreciteCountTx;
	@BindView(R.id.alreadyknow_count_tx)
	TextView alreadyknowCountTx;
	@BindView(R.id.layout_2)
	LinearLayout layout2;
	@BindView(R.id.all_word)
	LinearLayout allWord;
	@BindView(R.id.piechart)
	PieChart piechart;
//	@BindView(R.id.layout_1)
//	LinearLayout layout1;
	@BindView(R.id.reciteword)
	TextImageButton reciteword;
	@BindView(R.id.readyrecite)
	TextImageButton readyrecite;
	@BindView(R.id.Iknow)
	TextImageButton Iknow;
	@BindView(R.id.myfavorite)
	TextImageButton myfavorite;

	private SharedPreferenceUtil sharedPreferenceUtil;

	private ArrayList<NewWord> localNewWordList;

	private NewWord newWord;
	private WordDao wordDao;
	private long mUserId;
	private int retryCount = 0;
	@OnClick({R.id.btnBack, R.id.btnFunction,R.id.all_word})
	void onBtnClick(View view) {
		switch (view.getId()) {
			case R.id.btnBack:
				finish();
				break;
			case R.id.btnFunction:
				final NormalDialog normalDialog = new NormalDialog(context);
				DialogUtils.setAlertDialogNormalStyle(normalDialog,"温馨提示","同步会覆盖当前单词");
				normalDialog.setOnBtnClickL(new OnBtnClickL() {
					@Override
					public void onBtnClick() {
						upload();
						normalDialog.dismiss();
					}
				}, new OnBtnClickL() {
					@Override
					public void onBtnClick() {
						normalDialog.dismiss();
					}
				});

				break;
			case R.id.all_word:
				Intent intent = new Intent(this, AllWordActivity.class);
				startActivity(intent);
				break;
		}
	}

	public void setLocalNewWordList(ArrayList<NewWord> localNewWordList)
	{
		this.localNewWordList=localNewWordList;
	}

	public ArrayList<NewWord> getLocalNewWordList()
	{
		return this.localNewWordList;
	}

	private double needrecite_count, readyrecite_count,
			alreadyknow_count;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mine_new_word);
		ButterKnife.bind(this);
		setMainHeadView();
		//将本地数据库中的所有单词取出来
		setLocalNewWordList((ArrayList<NewWord>)wordDao.all());
		initView();
		upload();
	}

	@Override
	protected void onResume() {
		super.onResume();
		sharedPreferenceUtil.putBoolean(Constants.WORDS_WHETHER_HAS_SYNCHRONIZATION,false);
		setWordNums();
		initNewWord();
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
		wordDao = new WordDao(DatabaseHelper.getHelper(this));
		mUserId = CommonUtil.getUserId();
//        showLoadingDialog();
//        getDatasFromNetwork();
	}
	List<NewWord> allwords;

	@Override
	protected void initView() {
		piechart = (PieChart) findViewById(R.id.piechart);
	}

	@Override
	protected void setMainHeadView() {
		textHeadTitle.setText(R.string.mine_new_word);
		btnBack.setImageResource(R.drawable.btn_back);
		btnFunction.setVisibility(View.VISIBLE);
		btnFunction.setImageResource(R.drawable.btn_backup);

	}

	private void initNewWord() {
		List<String> nameList = new ArrayList<String>();
		nameList.add("需背诵");
		nameList.add("待复习");
		nameList.add("已掌握");
		double all = needrecite_count + readyrecite_count + alreadyknow_count;
		float needpercent = (float) (needrecite_count / all) * 100;
		float readypercent = (float) (readyrecite_count / all) * 100;
		float alreadypercent = (float) (alreadyknow_count / all) * 100;
		PieData mPieData = getPieData(3, 100, nameList, needpercent,
				readypercent, alreadypercent);
		showChart(piechart, mPieData);
		allnewwordCountTx.setText("" + (int) all);
		needreciteCountTx.setText("" + (int) needrecite_count);
		readyreciteCountTx.setText("" + (int) readyrecite_count);
		alreadyknowCountTx.setText("" + (int) alreadyknow_count);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		sharedPreferenceUtil.putBoolean(Constants.WORDS_WHETHER_HAS_SYNCHRONIZATION,false);
		Intent intent = new Intent(this,PostWordsToRemoteDatabase.class);
		startService(intent);
	}

	private void showChart(PieChart pieChart, PieData pieData) {
		// pieChart.setHoleColorTransparent(true);

		pieChart.setHoleRadius(60f); // 半径
		pieChart.setTransparentCircleRadius(64f); // 半透明圈

		pieChart.setHoleRadius(0); //实心圆

		// pieChart.setDescription("测试饼状图");

		// piechart.setDrawYValues(true);
		pieChart.setDrawCenterText(true); // 饼状图中间可以添加文字

		pieChart.setDrawHoleEnabled(true);

		pieChart.setRotationAngle(90); // 初始旋转角度

		// draws the corresponding description value into the slice
		// piechart.setDrawXValues(true);

		// enable rotation of the chart by touch
		pieChart.setRotationEnabled(true); // 可以手动旋转

		// display percentage values
		pieChart.setUsePercentValues(true); // 显示成百分比

		// pieChart.setCenterText("Quarterly Revenue"); //饼状图中间的文字
		pieChart.setCenterText(""); // 饼状图中间的文字
		// 设置数据
		pieChart.setData(pieData);

		// undo all highlights
		// pieChart.highlightValues(null);
		// pieChart.invalidate();

		Legend mLegend = pieChart.getLegend(); // 设置比例图
		mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART); // 最右边显示
		// mLegend.setForm(LegendForm.LINE); //设置比例图的形状，默认是方形
		mLegend.setXEntrySpace(7f);
		mLegend.setYEntrySpace(5f);

		pieChart.animateXY(1000, 1000); // 设置动画
		// piechart.spin(2000, 0, 360);
	}

	/**
	 * @param count 分成几部分
	 * @param range
	 */
	private PieData getPieData(int count, float range, List<String> nameList,
							   float needpercent, float readypercent, float alreadypercent) {

		ArrayList<String> xValues = new ArrayList<String>(); // xVals用来表示每个饼块上的内容

		ArrayList<Entry> yValues = new ArrayList<Entry>(); // yVals用来表示封装每个饼块的实际数据

		// 饼图数据
		/**
		 * 将一个饼形图分成四部分， 四部分的数值比例为14:14:34:38 所以 14代表的百分比就是14%
		 */
		float quarterly1 = needpercent;
		float quarterly2 = readypercent;
		float quarterly3 = alreadypercent;
		// float quarterly4 = 90;
		int p = yValues.size();
		if (needpercent > 0) {
			xValues.add(nameList.get(0));
			yValues.add(new Entry(quarterly1, p));
		}
		p = yValues.size();
		if (readypercent > 0) {
			xValues.add(nameList.get(1));
			yValues.add(new Entry(quarterly2, p));
		}
		p = yValues.size();
		if (alreadypercent > 0) {
			xValues.add(nameList.get(2));
			yValues.add(new Entry(quarterly3, p));
		}

		// y轴的集合
		// PieDataSet pieDataSet = new PieDataSet(yValues,
		// "Quarterly Revenue 2014"/*显示在比例图上*/);
		PieDataSet pieDataSet = new PieDataSet(yValues, ""/* 显示在比例图上 */);
		pieDataSet.setSliceSpace(0f); // 设置个饼状图之间的距离

		ArrayList<Integer> colors = new ArrayList<Integer>();

		if (needpercent > 0) {
			//colors.add(Color.rgb(252, 206, 47));
			colors.add(Color.rgb(247, 212, 170));
		}
		if (readypercent > 0) {
			colors.add(Color.rgb(183, 214, 155));
		}
		if (alreadypercent > 0) {
			//
			colors.add(Color.rgb(242, 153, 159));//green
		}

		pieDataSet.setColors(colors);

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float px = 5 * (metrics.densityDpi / 160f);
		pieDataSet.setSelectionShift(px); // 选中态多出的长度

		PieData pieData = new PieData(xValues, pieDataSet);

		return pieData;
	}

	@OnClick({R.id.reciteword, R.id.readyrecite, R.id.Iknow, R.id.myfavorite})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.reciteword:
				Intent intent = new Intent(this, ReciteWordIndexActivity.class);
				startActivity(intent);
				break;
			case R.id.readyrecite:
				Intent intent2 = new Intent(this, ReadyReciteIndexActivity.class);
				startActivity(intent2);
				break;
			case R.id.Iknow:
				Intent intent3 = new Intent(this, ReciteAlreadyKnowActivity.class);
				startActivity(intent3);
				break;
			case R.id.myfavorite:
				Intent intent4 = new Intent(this, ReciteMyFavoriteActivity.class);
				startActivity(intent4);
				break;
		}
	}



	private void upload() {
		showLoadingDialog();
		List<NewWord> noUpload = wordDao.queryByColumn("isUpload", 0);
		if (noUpload != null && noUpload.size() > 0) {
			for (int i = 0;i < noUpload.size();i++) {
				newWord = noUpload.get(i);
				String result = CommonUtil.format201906304(newWord);
				uploadAllWordsList(result);
			}
		}
		getNetWordsListFromNetwork();
	}

	private void getNetWordsListFromNetwork(){
		OkGo.get(Urls.DownWordsURL)
				.tag(this)
				.params("userId", CommonUtil.getUserId())
				.execute(new JsonCallback<IycResponse<List<NewWord>>>(context) {
					@Override
					public void onSuccess(IycResponse<List<NewWord>> listIycResponse, Call call, Response response) {
						if(isNull(listIycResponse)||isNull(listIycResponse.getData())){
							Logger.e("wzp listIycResponse = " + listIycResponse);
							dismissLoadingDialig();
							return;
						}
						MusicThread music = new MusicThread(listIycResponse.getData());
						music.start();
					}

					@Override
					public void onError(Call call, Response respopse, Exception e) {
						ToastCompat.makeText(MineNewWordActivity.this, "同步生词本失败！", Toast.LENGTH_SHORT).show();
						dismissLoadingDialig();
					}

					@Override
					public void parseError(Call call, Exception e) {
						super.parseError(call, e);
						if(++retryCount<=3){
							getNetWordsListFromNetwork();
						}
					}

					@Override
					public void onAfter(IycResponse<List<NewWord>> listIycResponse, Exception e) {
						super.onAfter(listIycResponse, e);
					}
				});
	}

	class MusicThread extends Thread{
		private List<NewWord> data;
		public MusicThread(List<NewWord> data) {
			this.data= data;
		}
		public void run() {
			updateWords(data);
			setWordNums();
			mHandler.sendEmptyMessageAtTime(0x111,0);
		}
	}

	/**
	 * 上传所有isUpload=0的单词
	 * @param str
	 */
	private void uploadAllWordsList(String str){
		if(isNull(context,str,"")){
			Logger.e("wzp str = " + str);
			return;
		}
		OkGo.post(Urls.AddNewWord)
				.params("dataJsonObjectString",str)
				.execute(new JsonCallback<IycResponse<String>>(context) {
					@Override
					public void onSuccess(IycResponse<String> stringIycResponse, Call call, Response response) {
//						ToastCompat.makeText(context,"上传单词成功",Toast.LENGTH_SHORT).show();
						wordDao.updateUpload(newWord);
					}

					@Override
					public void onError(Call call, Response response, Exception e) {
						super.onError(call, response, e);
//						ToastCompat.makeText(context,"上传单词失败",Toast.LENGTH_SHORT).show();
					}
				});
	}

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == 0x111){
				initNewWord();
				dismissLoadingDialig();
				ToastCompat.makeText(MineNewWordActivity.this, "同步生词本成功！", Toast.LENGTH_SHORT).show();
			}
		}
	};

	private void setWordNums() {
		needrecite_count=0;
		readyrecite_count=0;
		alreadyknow_count=0;
		setLocalNewWordList((ArrayList<NewWord>) wordDao.all());
		ArrayList<NewWord> localWordList=getLocalNewWordList();
		for(int n=0;n<localWordList.size();n++)
		{
			NewWord tempNewWord=localWordList.get(n);
			if(tempNewWord.getIsUpload()!=1)
				continue;
			if(tempNewWord.getIFreadyRecite()==1)
			{
				needrecite_count++;
				continue;
			}
			if(tempNewWord.getIFneedAgain()==1)
			{
				readyrecite_count++;
				continue;
			}
			if(tempNewWord.getIFalreadyKnow()==1)
				alreadyknow_count++;
		}
	}

	private void updateWords(List<NewWord> newWordList) {
		if(newWordList==null||newWordList.isEmpty()){
			return;
		}
		ArrayList<NewWord> localWordList = getLocalNewWordList();
		for(int n=0;n<localWordList.size();n++) {
			NewWord tempNewWord=localWordList.get(n);
			if(tempNewWord.getIsUpload()==1)
				wordDao.delete(tempNewWord);
		}
		for(int n=0;n<newWordList.size();n++) {
			NewWord temNewWord=newWordList.get(n);
			if(temNewWord.getWord()==null||temNewWord.getWord().isEmpty()){
				continue;
			}
			if (temNewWord.getLevel()!=null){
				String[] level = temNewWord.getLevel().split("_");
				if(level.length != 8){
					Logger.e("wzp level格式不对");
					continue;
				}
				temNewWord.setCET4(level[0].equals("1")?1:0);
				temNewWord.setCET6(level[1].equals("1")?1:0);
				temNewWord.setTEM4(level[2].equals("1")?1:0);
				temNewWord.setTEM8(level[3].equals("1")?1:0);
				temNewWord.setSAT(level[4].equals("1")?1:0);
				temNewWord.setTOEFL(level[5].equals("1")?1:0);
				temNewWord.setIELTS(level[6].equals("1")?1:0);
				temNewWord.setGRE(level[7].equals("1")?1:0);
			}
			temNewWord.setIsUpload(1);
			wordDao.add(temNewWord);
		}
		setLocalNewWordList((ArrayList<NewWord>) wordDao.all());
	}

}
