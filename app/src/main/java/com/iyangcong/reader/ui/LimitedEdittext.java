package com.iyangcong.reader.ui;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.drakeet.support.toast.ToastCompat;

/**
 * Created by WuZepeng on 2017-04-12.
 */

public class LimitedEdittext extends AppCompatEditText {

	private NoEmojiTextWatcher textWatcher;


	public LimitedEdittext(Context context) {
		super(context);
	}

	public LimitedEdittext(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LimitedEdittext(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setTextWatcher(Context context,int maxLen, boolean isClearEmoji,String errorTipsForLength,String errorTipsForEmoji){
		setTextWatcher(context, isClearEmoji,errorTipsForEmoji);
		setTextWatcher(context, maxLen,errorTipsForLength);
	}

	public void setTextWatcher(Context context, int maxLen,String errorTipsForLength) {
		addTextChangedListener(new LimiteLengthTextWatcher(context, maxLen,errorTipsForLength));
	}

	public void setTextWatcher(Context context, boolean isClearEmoji,String errorTipsForEmoji) {
		addTextChangedListener(new NoEmojiTextWatcher(isClearEmoji,context,errorTipsForEmoji));
	}

	public void setTextNumberDownlineTextWatcher(Context context,String errorTips){
		addTextChangedListener(new TextNumberDownlineWatcher(context,errorTips));
	}

	public class LimiteLengthTextWatcher implements TextWatcher{
		private Context context;
		private int maxLine;
		private int currenPos;
		private String errorTips;
		public LimiteLengthTextWatcher(Context context, int maxLine,String errorTips) {
			this.maxLine = maxLine;
			this.context = context;
			this.errorTips = errorTips;
		}

		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
						handlStringLength(maxLine);
		}

		@Override
		public void afterTextChanged(Editable editable) {

		}

		/**设置EditText的最大输入长度
		 * @param limitedLenght
		 */
		private void handlStringLength(int limitedLenght) {
			Editable editable = getText();
			int len = editable.length();
			if(len > limitedLenght) {
				ToastCompat.makeText(context,errorTips, Toast.LENGTH_SHORT).show();
//				ToastCompat.makeText(context,context.getString(R.string.circle_name_toolong),Toast.LENGTH_SHORT).show();
				int selEndIndex = Selection.getSelectionEnd(editable);
				String str = editable.toString();
				//截取新字符串
				String newStr = str.substring(0, limitedLenght);
				setText(newStr);
				editable = getText();
				//新字符串的长度
				int newLen = editable.length();
				//旧光标位置超过字符串长度
				if(selEndIndex > newLen) {
					selEndIndex = editable.length();
				}
				//设置新光标所在的位置
				Selection.setSelection(editable, selEndIndex);
			}
		}
	}

	public class NoEmojiTextWatcher implements TextWatcher{
		boolean resetText;
		int currentPost;
		String inputText;
		private boolean isClearEmoji = true;
		private Context context;
		private String mErrorTips;

		public NoEmojiTextWatcher(boolean isClearEmoji,Context context,String errorTips) {
			super();
			this.isClearEmoji = isClearEmoji;
			this.context = context;
			this.mErrorTips = errorTips;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int before, int count) {
			if(!resetText){
				currentPost = getSelectionEnd();
				inputText = s.toString();
			}
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if(isClearEmoji)
				resetText =hanldeEmoji(resetText,before,count, s);
		}



		/**
		 * 设置是否需要隐藏Emoji表情
		 * @param resetText
		 * @param count
		 * @param s
		 */
		private boolean hanldeEmoji(boolean resetText, int before, int count, CharSequence s) {
			if(!resetText){
				if(before != 0)
					return resetText;
				if(count >= 2){
					CharSequence input = s.subSequence(currentPost, currentPost + count);
					if (containsEmoji(input.toString())) {
						ToastCompat.makeText(context,mErrorTips,Toast.LENGTH_SHORT).show();
//						ToastCompat.makeText(context,context.getString(R.string.circle_name_no_emoji),Toast.LENGTH_SHORT).show();
//						resetText = true;
						//是表情符号就将文本还原为输入表情符号之前的内容
						setText(inputText);
						CharSequence text = getText();
						if (text instanceof Spannable) {
							Spannable spanText = (Spannable) text;
							Selection.setSelection(spanText, text.length());
						}
					}
				}
			}else{
				resetText = false;
			}
			return resetText;
		}

		@Override
		public void afterTextChanged(Editable editable) {

		}
	}

	public class TextNumberDownlineWatcher implements TextWatcher{
		private String mErrotTips;
		private int currentPos;
		private boolean resetText;
		private String text;
		private Context mContext;

		public TextNumberDownlineWatcher(Context context, String errotTips) {
			mErrotTips = errotTips;
			mContext = context;
		}

		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			currentPos = getSelectionEnd();
			text = charSequence.toString();
			if(text == null)
				text = "";

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			Logger.e("Wzp start:%d before:%d count:%d currentPos:%d",start,before,count,currentPos);
			clearUnfitableChar(start,count,s);
		}

		@Override
		public void afterTextChanged(Editable editable) {

		}

		public boolean containsUnfitChars(String input){
			Pattern tmpPattern = Pattern.compile("[^0-9a-zA-Z_]");
			Matcher tmpMatcher = tmpPattern.matcher(input);
			return tmpMatcher.find();
		}

		public void clearUnfitableChar(int start, int count, CharSequence s){
			if(s.length()>0){
				CharSequence input = s.subSequence(start, start + count);
				boolean containsUnfitChars = containsUnfitChars(input.toString());
				Logger.e("wzp input:%s containUnfitChars:%s",input.toString(),containsUnfitChars+"");
				if (containsUnfitChars) {
					ToastCompat.makeText(mContext,mErrotTips,Toast.LENGTH_SHORT).show();
//						resetText = true;
					//是表情符号就将文本还原为输入表情符号之前的内容
					setText(text);
					CharSequence text = getText();
					if (text instanceof Spannable) {
						Spannable spanText = (Spannable) text;
						Selection.setSelection(spanText, text.length());
					}
				}
			}
		}
	}
	public static boolean containsEmoji(String source) {
		int len = source.length();
		for (int i = 0; i < len; i++) {
			char codePoint = source.charAt(i);
			if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
				return true;
			}
		}
		return false;
	}

	private static boolean isEmojiCharacter(char codePoint) {
		return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
				(codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
				((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
				&& (codePoint <= 0x10FFFF));
	}
}
