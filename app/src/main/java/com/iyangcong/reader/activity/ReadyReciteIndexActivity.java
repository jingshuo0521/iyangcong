package com.iyangcong.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.app.AppContext;
import com.iyangcong.reader.base.BaseActivity;
import com.iyangcong.reader.bean.MineReciteDegree;
import com.iyangcong.reader.bean.NewWord;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.WordDao;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.ui.IYangCongToast;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/15.
 */

public class ReadyReciteIndexActivity extends BaseActivity implements View.OnClickListener {
	@BindView(R.id.btnBack)
	ImageButton btnBack;
	@BindView(R.id.textHeadTitle)
	TextView textHeadTitle;
	@BindView(R.id.btnFunction)
	ImageButton btnFunction;
	@BindView(R.id.layout_header)
	LinearLayout layoutHeader;
	@BindView(R.id.tx_count_cet4)
	TextView txCountCet4;
	@BindView(R.id.grade_4)
	LinearLayout grade4;
	@BindView(R.id.tx_count_cet6)
	TextView txCountCet6;
	@BindView(R.id.grade_6)
	LinearLayout grade6;
	@BindView(R.id.tx_count_tem4)
	TextView txCountTem4;
	@BindView(R.id.grade_special_4)
	LinearLayout gradeSpecial4;
	@BindView(R.id.tx_count_tem8)
	TextView txCountTem8;
	@BindView(R.id.grade_special_8)
	LinearLayout gradeSpecial8;
	@BindView(R.id.tx_count_sat)
	TextView txCountSat;
	@BindView(R.id.grade_sat)
	LinearLayout gradeSat;
	@BindView(R.id.tx_count_toefl)
	TextView txCountToefl;
	@BindView(R.id.grade_toefl)
	LinearLayout gradeToefl;
	@BindView(R.id.tx_count_ielts)
	TextView txCountIelts;
	@BindView(R.id.grade_ielts)
	LinearLayout gradeIelts;
	@BindView(R.id.tx_count_gre)
	TextView txCountGre;
	@BindView(R.id.grade_gre)
	LinearLayout gradeGre;
	@BindView(R.id.tx_count_others)
	TextView txCountOthers;
	@BindView(R.id.grade_other)
	LinearLayout gradeOther;
	@BindView(R.id.tx_count_invisibile)
	TextView txCountInvisibile;
	@BindView(R.id.grade_invisible)
	LinearLayout gradeInvisible;
	private AppContext appContext;
	private int count_cet4, count_cet6, count_tem4, count_tem8, count_sat, count_toefl, count_ielts, count_gre,count_other;
	private MineReciteDegree mineReciteDegree = new MineReciteDegree();
	private WordDao wordDao;


	public MineReciteDegree getMineReciteDegree() {
		return mineReciteDegree;
	}

	public void setMineReciteDegree(MineReciteDegree mineReciteDegree) {
		this.mineReciteDegree = mineReciteDegree;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reciteword_index);
		ButterKnife.bind(this);
		setMainHeadView();
//        getDatasFromNetwork();
		initView();
	}

	@OnClick({R.id.btnBack, R.id.grade_4, R.id.grade_6, R.id.grade_special_4, R.id.grade_special_8, R.id.grade_sat, R.id.grade_toefl, R.id.grade_ielts, R.id.grade_gre,R.id.grade_other})
	public void onClick(View view) {
		String reciteType = "";
		Intent intent = null;
		switch (view.getId()) {
			case R.id.btnBack:
				finish();
				break;
			case R.id.grade_4:
				if (count_cet4 == 0) {
					ToastCompat.makeText(this, "暂无此类词汇", Toast.LENGTH_SHORT).show();
				} else {
					reciteType = "CET4";
					intent = new Intent(this, ReadyReciteActivity.class);
					intent.putExtra("reciteType", reciteType);
					startActivity(intent);
				}
				break;
			case R.id.grade_6:
				if (count_cet6 == 0) {
					ToastCompat.makeText(this, "暂无此类词汇", Toast.LENGTH_SHORT).show();
				} else {
					reciteType = "CET6";
					intent = new Intent(this, ReadyReciteActivity.class);
					intent.putExtra("reciteType", reciteType);
					startActivity(intent);
				}
				break;
			case R.id.grade_special_4:
				if (count_tem4 == 0) {
					ToastCompat.makeText(this, "暂无此类词汇", Toast.LENGTH_SHORT).show();
				} else {
					reciteType = "TEM4";
					intent = new Intent(this, ReadyReciteActivity.class);
					intent.putExtra("reciteType", reciteType);
					startActivity(intent);
				}
				break;
			case R.id.grade_special_8:
				if (count_tem8 == 0) {
					ToastCompat.makeText(this, "暂无此类词汇", Toast.LENGTH_SHORT).show();
				} else {
					reciteType = "TEM8";
					intent = new Intent(this, ReadyReciteActivity.class);
					intent.putExtra("reciteType", reciteType);
					startActivity(intent);
				}
				break;
			case R.id.grade_sat:
				if (count_sat == 0) {
					ToastCompat.makeText(this, "暂无此类词汇", Toast.LENGTH_SHORT).show();
				} else {
					reciteType = "SAT";
					intent = new Intent(this, ReadyReciteActivity.class);
					intent.putExtra("reciteType", reciteType);
					startActivity(intent);
				}
				break;
			case R.id.grade_toefl:
				if (count_toefl == 0) {
					ToastCompat.makeText(this, "暂无此类词汇", Toast.LENGTH_SHORT).show();
				} else {
					reciteType = "TOEFL";
					intent = new Intent(this, ReadyReciteActivity.class);
					intent.putExtra("reciteType", reciteType);
					startActivity(intent);
				}
				break;
			case R.id.grade_ielts:
				if (count_ielts == 0) {
					ToastCompat.makeText(this, "暂无此类词汇", Toast.LENGTH_SHORT).show();
				} else {
					reciteType = "IELTS";
					intent = new Intent(this, ReadyReciteActivity.class);
					intent.putExtra("reciteType", reciteType);
					startActivity(intent);
				}
				break;
			case R.id.grade_gre:
				if (count_gre == 0) {
					ToastCompat.makeText(this, "暂无此类词汇", Toast.LENGTH_SHORT).show();
				} else {
					reciteType = "GRE";
					intent = new Intent(this, ReadyReciteActivity.class);
					intent.putExtra("reciteType", reciteType);
					startActivity(intent);
				}
				break;
			case R.id.grade_other:
				if (count_other == 0) {
					ToastCompat.makeText(this, "暂无此类词汇", Toast.LENGTH_SHORT).show();
				} else {
					reciteType = "others";
					intent = new Intent(this, ReadyReciteActivity.class);
					intent.putExtra("reciteType", reciteType);
					startActivity(intent);
				}
				break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		updataFromLocalDataBase();
		updataCount();
	}

	@Override
	protected void setMainHeadView() {
		textHeadTitle.setText("待复习");
		btnBack.setImageResource(R.drawable.btn_back);
		btnBack.setVisibility(View.VISIBLE);
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		wordDao = new WordDao(DatabaseHelper.getHelper(this));
	}

	private void updataFromLocalDataBase() {
		initNums();
//		List<NewWord> recitingWordList = wordDao.getWordListByIFneedAgain(true);
		List<NewWord> recitingWordList = wordDao.getWordsByDeleteStatusAndIFneedAgain();
		Log.i("hahahahahah reciting:",recitingWordList.toString());
		for (NewWord word : recitingWordList) {
			if (word.getCET4() == 1) {
				count_cet4++;
			}
			if (word.getCET6() == 1) {
				count_cet6++;
			}
			if (word.getGRE() == 1) {
				count_gre++;
			}
			if (word.getIELTS() == 1) {
				count_ielts++;
			}
			if (word.getSAT() == 1) {
				count_sat++;
			}
			if (word.getTEM4() == 1) {
				count_tem4++;
			}
			if (word.getTEM8() == 1) {
				count_tem8++;
			}
			if (word.getTOEFL() == 1) {
				count_toefl++;
			}
			if (word.getCET4() == 0 && word.getCET6() == 0 && word.getGRE() == 0 && word.getIELTS() == 0 &&
					word.getSAT() == 0 && word.getTEM4() == 0 && word.getTEM8() == 0 && word.getTOEFL() == 0) {
				count_other++;
			}
		}
	}

	private void initNums(){
		count_cet4 = 0;
		count_cet6 = 0;
		count_tem4 = 0;
		count_tem8 = 0;
		count_sat = 0;
		count_toefl = 0;
		count_ielts = 0;
		count_gre = 0;
		count_other = 0;
	}

	private void updataCount() {
		if (count_cet4 == 0) {
			txCountCet4.setText("暂无此类词汇");
		} else {
			txCountCet4.setText("(" + count_cet4 + ")");
		}

		if (count_cet6 == 0) {
			txCountCet6.setText("暂无此类词汇");
		} else {
			txCountCet6.setText("(" + count_cet6 + ")");
		}
		if (count_tem4 == 0) {
			txCountTem4.setText("暂无此类词汇");
		} else {
			txCountTem4.setText("(" + count_tem4 + ")");
		}
		if (count_tem8 == 0) {
			txCountTem8.setText("暂无此类词汇");
		} else {
			txCountTem8.setText("(" + count_tem8 + ")");
		}
		if (count_sat == 0) {
			txCountSat.setText("暂无此类词汇");
		} else {
			txCountSat.setText("(" + count_sat + ")");
		}
		if (count_toefl == 0) {
			txCountToefl.setText("暂无此类词汇");
		} else {
			txCountToefl.setText("(" + count_toefl + ")");
		}
		if (count_ielts == 0) {
			txCountIelts.setText("暂无此类词汇");
		} else {
			txCountIelts.setText("(" + count_ielts + ")");
		}
		if (count_gre == 0) {
			txCountGre.setText("暂无此类词汇");
		} else {
			txCountGre.setText("(" + count_gre + ")");
		}

		if(count_other == 0){
			txCountOthers.setText("暂无此类词汇");
		} else {
			txCountOthers.setText("(" + count_other + ")");
		}
	}

	@Override
	protected void initView() {
//        List<NewWord> newWordList = wordDao.all();
//        if (mineReciteDegree != null) {
//            count_cet4 = mineReciteDegree.getCet4Num();
//            count_cet6 = mineReciteDegree.getCet6Num();
//            count_gre = mineReciteDegree.getGreNum();
//            count_ielts = mineReciteDegree.getIeltsNum();
//            count_sat = mineReciteDegree.getSatNum();
//            count_tem4 = mineReciteDegree.getTem4Num();
//            count_tem8 = mineReciteDegree.getTem8Num();
//            count_toefl = mineReciteDegree.getToeflNum();
//        }

	}

	private void getDatasFromNetwork() {
		OkGo.get(Urls.PersonReviewNewWordsDegreeInfoURL)
				.tag(this)
				.params("userId", CommonUtil.getUserId())
				.execute(new JsonCallback<IycResponse<MineReciteDegree>>(this) {
					@Override
					public void onSuccess(IycResponse<MineReciteDegree> mineReciteDegreeIycResponse, Call call, Response response) {
						setMineReciteDegree(mineReciteDegreeIycResponse.getData());
						initView();
					}

					@Override
					public void onError(Call call, Response response, Exception e) {

					}
				});

	}
}
