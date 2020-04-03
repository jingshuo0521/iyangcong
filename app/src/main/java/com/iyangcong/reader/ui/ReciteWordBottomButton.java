package com.iyangcong.reader.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;

import java.util.Iterator;
import java.util.List;
import java.util.Map;


/*
 * author shao 20151107
 * 
 * */

public class ReciteWordBottomButton extends LinearLayout {
	private Context context;
	LayoutInflater mInflater =null;
	OnClickListener listener;
	public Button thinkup,dontthinkup;

	public ReciteWordBottomButton(Context context) {
		super(context);
		this.context = context;
		setOrientation(LinearLayout.VERTICAL);
		mInflater = LayoutInflater.from(context);
	}

	public ReciteWordBottomButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		mInflater = LayoutInflater.from(context);
		thinkup=(Button)mInflater.inflate(R.layout.reciteword_bottom_button_know_thinkup, null);
		dontthinkup=(Button)mInflater.inflate(R.layout.reciteword_bottom_button_dontkonwthinkup, null);
	}

	
	public void setOnClickListener(OnClickListener listener){
		this.listener=listener;
	}
	public void showFirstViewButton(){
		 removeAllViews();
		 LinearLayout.LayoutParams lp=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		 lp.leftMargin=20;
		 lp.rightMargin=20;
		 lp.topMargin=20;
		 thinkup.setLayoutParams(lp);
		 dontthinkup.setLayoutParams(lp);
		 this.addView(thinkup, 0);
		 this.addView(dontthinkup,1);
		 invalidate();
		 thinkup.setOnClickListener(listener);
		 dontthinkup.setOnClickListener(listener);
		 thinkup.setText("认识");
		 dontthinkup.setText("不认识");
	}

	public void showSecondKnowView(){
		removeAllViews();
		 LinearLayout.LayoutParams lp=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		 lp.leftMargin=20;
		 lp.rightMargin=20;
		 lp.topMargin=20;
		 dontthinkup.setLayoutParams(lp);
		 this.addView(dontthinkup,0);
		 invalidate();
		 dontthinkup.setOnClickListener(listener);
		 dontthinkup.setText("下一个");
	}

	public void showReviewResultView(){
		removeAllViews();
		 LinearLayout.LayoutParams lp=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		 lp.leftMargin=20;
		 lp.rightMargin=20;
		 lp.topMargin=20;
		 dontthinkup.setLayoutParams(lp);
		 this.addView(dontthinkup,0);
		 invalidate();
		 dontthinkup.setOnClickListener(listener);
		 dontthinkup.setText("查看结果");
	}

	public String getThinkupText(){
		return thinkup.getText().toString();
	}

	public String getDontThinkupText(){
		return dontthinkup.getText().toString();
	}

	public void setThinkupText(String value){
		thinkup.setText(value);
	}


	public void setDontThinkupText(String value){
		dontthinkup.setText(value);
	}

	public void getContentLayout(List<String> datas){

		if(datas.size()!=0){
			for(int i=0;i<datas.size();i++){
				 LinearLayout itemcontain=(LinearLayout)mInflater.inflate(R.layout.explain_view_item, null);
					TextView text_description=(TextView)itemcontain.findViewById(R.id.text_description);
					text_description.setText(datas.get(i));
					this.addView(itemcontain,i);
			}

		}else{

		}
	}
	public void getContentLayouts(Map<String,List<String>> datas){
		if(datas.size()!=0){
			Iterator<?> entries = datas.entrySet().iterator();
			int position=0;
			while (entries.hasNext()) {
			    @SuppressWarnings("rawtypes")
				Map.Entry entry = (Map.Entry) entries.next();
			    String key = (String)entry.getKey();
			    @SuppressWarnings("unchecked")
				List<String> value = (List<String>)entry.getValue();
			    LinearLayout itemcontain=(LinearLayout)mInflater.inflate(R.layout.explain_view_item, null);
				TextView text_description=(TextView)itemcontain.findViewById(R.id.text_description);
				//TextView text_description=(TextView)itemcontain.findViewById(R.id.text_description);
				//text_cat.setText(key);
				text_description.setText(value.toString());
				addView(itemcontain,position);
				position++;
			}
		}else{
			//showHintLayout("just a test");
		}
	}



	public void showErrorLayout(String mes, int marginTop){
		this.removeAllViews();
		//LinearLayout itemcontain=(LinearLayout)mInflater.inflate(R.layout.explain_error_view, null);
		LinearLayout itemcontain=new LinearLayout(context);
		itemcontain.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		itemcontain.setOrientation(LinearLayout.HORIZONTAL);

		TextView text_errorhint=new TextView(context);
		//TextView text_errorhint=(TextView)itemcontain.findViewById(R.id.text_errorhint);
		LinearLayout.LayoutParams lp=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lp.topMargin=marginTop;
		text_errorhint.setGravity(Gravity.CENTER_HORIZONTAL);
		text_errorhint.setLayoutParams(lp);
		text_errorhint.setTextColor(context.getResources().getColor(R.color.white));
		text_errorhint.setText(mes);
		itemcontain.addView(text_errorhint);
		this.addView(itemcontain);
		invalidate();
	}
}