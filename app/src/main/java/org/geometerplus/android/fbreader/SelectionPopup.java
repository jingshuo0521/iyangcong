/*
 * Copyright (C) 2007-2015 FBReader.ORG Limited <contact@fbreader.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.MineNewWordActivity;
import com.iyangcong.reader.bean.NewWord;
import com.iyangcong.reader.bean.SentenceTranslation;
import com.iyangcong.reader.bean.ShelfBook;
import com.iyangcong.reader.bean.Word;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.BookDao;
import com.iyangcong.reader.database.dao.WordDao;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.utils.BookInfoUtils;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.DisplayUtil;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.StringUtils;
import com.iyangcong.reader.utils.Urls;
import com.iyangcong.reader.utils.query.QueryUtils;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.Logger;

import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.FBView;
import org.geometerplus.fbreader.util.TextSnippet;
import org.geometerplus.zlibrary.core.resources.ZLResource;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;
import static com.iyangcong.reader.utils.query.QueryUtils.setWordAllLevel;

class SelectionPopup extends PopupPanel implements View.OnClickListener {
    final static String ID = "SelectionPopup";

    private NewWord temNewWord;

    private FBReaderApp fbReaderApp;

    private FBView fbview;

    private TextView tvTranslateResult;

    private ImageView btnAddToNewWord;

    private ImageView btnAnnounce;

    private TextView tvWordLevel;

    private CheckedTextView ctvAddToNewWord;

    private TextView tvTransLate;

    private TextView tvSencenteTranslate;

    private Word word;

    private boolean isAWord = false;

    private SharedPreferenceUtil sharedPreferenceUtil;

    private MediaPlayer mediaPlayer;

    private Uri uri;

    SelectionPopup(FBReaderApp fbReader) {
        super(fbReader);
        this.fbReaderApp = fbReader;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void createControlPanel(FBReader activity, RelativeLayout root) {
        if (myWindow != null && activity == myWindow.getContext()) {
            tvTransLate.setTextColor(getMyActivity().getResources().getColor(R.color.main_color));
            tvSencenteTranslate.setTextColor(getMyActivity().getResources().getColor(R.color.white));
            return;
        }

        activity.getLayoutInflater().inflate(R.layout.selection_panel, root);
        myWindow = (SimplePopupWindow) root.findViewById(R.id.selection_panel);

        final ZLResource resource = ZLResource.resource("selectionPopup");
        setupButton(R.id.selection_panel_copy, resource.getResource("copyToClipboard").getValue());
        setupButton(R.id.selection_panel_share, resource.getResource("share").getValue());
//        setupButton(R.id.selection_panel_translate, resource.getResource("translate").getValue());
        setupButton(R.id.selection_panel_bookmark, resource.getResource("bookmark").getValue());
//        setupButton(R.id.selection_panel_sec_translate, resource.getResource("close").getValue());
        setupButton(R.id.selection_panel_new_word, "生词本");
        tvTransLate = (TextView) myWindow.findViewById(R.id.selection_panel_translate);
        tvTransLate.setOnClickListener(this);
        tvTransLate.setTextColor(getMyActivity().getResources().getColor(R.color.main_color));
        tvSencenteTranslate = (TextView) myWindow.findViewById(R.id.selection_panel_sec_translate);
        tvSencenteTranslate.setOnClickListener(this);
        tvTranslateResult = (TextView) myWindow.findViewById(R.id.tv_translate_result);
        tvTranslateResult.setMaxHeight(DisplayUtil.dip2px(myWindow.getContext(), 120));
        tvTranslateResult.setMovementMethod(ScrollingMovementMethod.getInstance());
        btnAddToNewWord = (ImageView) myWindow.findViewById(R.id.btn_add_to_new_word);
        btnAnnounce = (ImageView) myWindow.findViewById(R.id.btn_announce);
        tvWordLevel = (TextView) myWindow.findViewById(R.id.tv_word_level);
        ctvAddToNewWord = (CheckedTextView) myWindow.findViewById(R.id.ctv_auto_add_new_word);
        ctvAddToNewWord.setVisibility(View.GONE);
        if (sharedPreferenceUtil == null) {
            sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
        }
        boolean autoAddState = sharedPreferenceUtil.getBoolean(SharedPreferenceUtil.AOUT_ADD_NEW_WORD, false);
        if (autoAddState || !CommonUtil.getLoginState()) {
            btnAddToNewWord.setVisibility(View.GONE);
        } else {
            btnAddToNewWord.setVisibility(View.VISIBLE);
        }
        ctvAddToNewWord.setCheckMarkDrawable(myWindow.getContext().getResources().getDrawable(R.drawable.icon_switch));
        ctvAddToNewWord.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        ctvAddToNewWord.setChecked(autoAddState);
        ctvAddToNewWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean state = ctvAddToNewWord.isChecked();
                ctvAddToNewWord.setChecked(!state);
                sharedPreferenceUtil.putBoolean(SharedPreferenceUtil.AOUT_ADD_NEW_WORD, ctvAddToNewWord.isChecked());
            }
        });
        btnAddToNewWord.setOnClickListener(this);
        btnAnnounce.setOnClickListener(this);
        tvTranslateResult.setMovementMethod(ScrollingMovementMethod.getInstance());
        fbview = fbReaderApp.getTextView();
    }

    private void setupButton(int buttonId, String description) {
        final View button = myWindow.findViewById(buttonId);
        button.setOnClickListener(this);
        button.setContentDescription(description);
    }

    public void move(int selectionStartY, int selectionEndY) {
        if (myWindow == null) {
            return;
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        final int verticalPosition;
        final int screenHeight = ((View) myWindow.getParent()).getHeight();
        final int diffTop = screenHeight - selectionEndY;
        final int diffBottom = selectionStartY;
        if (diffTop > diffBottom) {
            verticalPosition = diffTop > myWindow.getHeight() + 20
                    ? RelativeLayout.ALIGN_PARENT_BOTTOM : RelativeLayout.CENTER_VERTICAL;
        } else {
            verticalPosition = diffBottom > myWindow.getHeight() + 20
                    ? RelativeLayout.ALIGN_PARENT_TOP : RelativeLayout.CENTER_VERTICAL;
        }

        layoutParams.addRule(verticalPosition);
        myWindow.setLayoutParams(layoutParams);

    }

    @Override
    protected void update() {
    }

    //是否句对翻译
    private boolean sectence_tranlate = false;

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.selection_panel_copy:
                Application.runAction(ActionCode.SELECTION_COPY_TO_CLIPBOARD);
                break;
            case R.id.selection_panel_share:
                Application.runAction(ActionCode.SELECTION_SHARE);
                break;
            case R.id.selection_panel_translate:
                tvTransLate.setTextColor(getMyActivity().getResources().getColor(R.color.main_color));
                tvSencenteTranslate.setTextColor(getMyActivity().getResources().getColor(R.color.white));
                show_();
                break;
            case R.id.selection_panel_bookmark:
                Application.runAction(ActionCode.SELECTION_BOOKMARK);
                break;
            //句对翻译
            case R.id.selection_panel_sec_translate:
                if (fbview.getSelectedSnippet().getText().split(" ").length >= 3) {
                    tvSencenteTranslate.setTextColor(getMyActivity().getResources().getColor(R.color.main_color));
                    tvTransLate.setTextColor(getMyActivity().getResources().getColor(R.color.white));
                    sectence_tranlate = true;
                    show_();
                } else {
                    ToastCompat.makeText(getMyActivity(), "句对功能要求至少选中3个单词", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_add_to_new_word:
                if (word.getWord()!=null&&word.getWord().size()>0){
                    addNewWordLocal(word,false);
                }else {
                    ToastCompat.makeText(getMyActivity(), "该生词为空，不能添加！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_announce:
                if(uri!=null) {
                    try {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(myWindow.getContext(), uri);
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.prepareAsync();
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.start();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.selection_panel_new_word:
                if (!CommonUtil.getLoginState()) {
                    getMyActivity().showDialog("查看生词本请先登录", 1);
                } else {
                    Application.runAction(ActionCode.SELECTION_CLEAR);
                    Intent intent = new Intent(getMyActivity(), MineNewWordActivity.class);
                    getMyActivity().startActivity(intent);
                }
                break;
        }
        if (view.getId() != R.id.btn_announce &&view.getId() != R.id.btn_add_to_new_word && view.getId() != R.id.selection_panel_sec_translate && view.getId() != R.id.selection_panel_translate) {
            Application.hideActivePopup();
        }
    }

    private void addNewSuccess() {
        btnAddToNewWord.setVisibility(View.GONE);
        super.show_();
    }

    private void addNewWordFail() {
        btnAddToNewWord.setVisibility(View.VISIBLE);
        super.show_();
    }

    private NewWord tranWordIntoNewWord(Word wordTmp) {
        boolean NoAdd = true;
        long bookId = sharedPreferenceUtil.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0);
        WordDao  wordDao = new WordDao(DatabaseHelper.getHelper(getMyActivity()));
        List<NewWord> newWordTmpList = wordDao.all();
        for (NewWord newWord:newWordTmpList){
            if (newWord.getWord().equals(wordTmp.getWord().get(0))){
                NoAdd = false;
            }
        }
        if (NoAdd) {
            NewWord newWordTmp = new NewWord();
            newWordTmp.setLocalWord(wordTmp.getSelectedText());
            newWordTmp.setSegmentId(fbview.getSelectedSnippet().getStart().getParagraphIndex() + BookInfoUtils.getBeginSegmentId(getMyActivity()));
            newWordTmp.setIFreadyRecite(1);//待背诵的状态
            newWordTmp.setIFneedAgain(0);
            newWordTmp.setIFalreadyKnow(0);
            newWordTmp.setIFfavorite(0);
            newWordTmp.setAccountId((int) sharedPreferenceUtil.getLong(SharedPreferenceUtil.USER_ID, 0));
            newWordTmp.setWord( wordTmp.getWord().get(0));
            newWordTmp.setIsUpload(0);
            newWordTmp.setBookId(bookId);
            newWordTmp.setContent(!isNull(wordTmp.getExplains())? wordTmp.getExplains() : new ArrayList<String>());
            newWordTmp.setTempContent(getExplains(wordTmp));
            newWordTmp.setPhonetic(wordTmp.getPhonetic());
            newWordTmp.setArticleContent(getSentenceText(BookInfoUtils.getParagraphText(fbReaderApp)));
            newWordTmp.setLevel(setWordAllLevel(newWordTmp, wordTmp.getGradeLevel()));
            BookDao bookDao = new BookDao(DatabaseHelper.getHelper(myWindow.getContext()));
            if (bookId == 0) {
                Logger.e("wzp bookId = " + bookId);
                return null;
            }
            List<ShelfBook> bookList = bookDao.queryByColumn("bookId", bookId);
            if (isNull(bookList)) {
                Logger.e("wzp 查询不到bookId = " + bookId + " 的书");
                return null;
            }
            newWordTmp.setBookName(bookList.get(0).getBookName());
            wordDao.add(newWordTmp);
            return newWordTmp;
        } else {
            NewWord newWordTmp = newWordTmpList.get(0);
            newWordTmp.setLocalWord(wordTmp.getSelectedText());
            newWordTmp.setIsUpload(0);
            newWordTmp.setIFreadyRecite(1);//待背诵的状态
            newWordTmp.setIFneedAgain(0);
            newWordTmp.setIFalreadyKnow(0);
            wordDao.update(newWordTmp);
            return newWordTmp;
        }
    }

    /**
     * 将Word中的解释转化为字符串类型
     *
     * @param wordTmp
     * @return
     */
    private String getExplains(Word wordTmp) {
        StringBuilder sb = new StringBuilder("");
        if (isNull(wordTmp) || (isNull(wordTmp.getExplains())&&isNull(wordTmp.getWebExplains()))) {
            Logger.e("wzp error wordTmp = " + wordTmp + " or explains is null or 没有内容");
            return "-1";
        }
        if(!isNull(wordTmp.getExplains())){
            for (String str : wordTmp.getExplains()) {
                sb.append(str).append(";");
            }
        }else if(!isNull(wordTmp.getWebExplains())){
            for (String str : wordTmp.getWebExplains().values()) {
                sb.append(str).append(";");
            }
        }
        if(sb.equals("")&&!TextUtils.isEmpty(wordTmp.getTranslation())){
        	sb.append(wordTmp.getTranslation());
		}
        return sb.toString();
    }

    private void addNewWordLocal(Word wordTmp,Boolean isAuto) {
        if (!CommonUtil.getLoginState()) {
            return;
        }
        temNewWord = tranWordIntoNewWord(wordTmp);
        String result = CommonUtil.format201906304(temNewWord);
        Logger.i("wzp word " + result);
        uploadWord(result,isAuto);
        sharedPreferenceUtil.putString(SharedPreferenceUtil.LAST_CHECKED_WORD, temNewWord.getWord());
        addNewSuccess();

    }
    private void uploadWord(String str, final Boolean isAuto){

        OkGo.post(Urls.AddNewWord)
                .params("dataJsonObjectString",str)
                .execute(new JsonCallback<IycResponse<String>>(getMyActivity()) {
                    @Override
                    public void onSuccess(IycResponse<String> stringIycResponse, Call call, Response response) {
                        WordDao  wordDao = new WordDao(DatabaseHelper.getHelper(getMyActivity()));
                        wordDao.updateUpload(temNewWord);
                        if (!isAuto){
                            ToastCompat.makeText(getMyActivity(), "添加生词成功！", Toast.LENGTH_SHORT).show();
                        }

                    }
                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        if (e.getMessage().contains("该生词该用户已经添加过，不能重复添加")) {
                            ToastCompat.makeText(getMyActivity(), "该生词已添加，不能重复添加", Toast.LENGTH_SHORT).show();
                            addNewSuccess();
                        } else {
                            ToastCompat.makeText(getMyActivity(), "添加失败，请重新添加", Toast.LENGTH_SHORT).show();
                            addNewWordFail();
                        }
                    }
                });
    }
    @Override
    protected void show_() {
        super.show_();
        int havesentence = sharedPreferenceUtil.getInt(SharedPreferenceUtil.CURRENT_BOOK_HAVESENTENCE, 0);
        TextSnippet snippet = fbview.getSelectedSnippet();
        if(havesentence == 0){
            //没有打开句对，不显示这个按钮
            tvSencenteTranslate.setVisibility(View.GONE);
        }else{
            //已经打开句对，可以显示这个按钮了
            tvSencenteTranslate.setVisibility(View.VISIBLE);
        }
        isAWord = StringUtils.isOneWord(snippet.getText());
        if (isAWord && !sectence_tranlate) {
            tvSencenteTranslate.setEnabled(false);
            tvSencenteTranslate.setTextColor(myWindow.getContext().getResources().getColor(R.color.board_middle_color));
//            String haha = "thrasymachus";
            QueryUtils.queryfromYoudaoOnLineNewMethod(StringUtils.deleSymbol(snippet.getText()), youdaoHandler,false);
        } else {
            tvSencenteTranslate.setTextColor(getMyActivity().getResources().getColor(R.color.main_color));
            tvTransLate.setTextColor(getMyActivity().getResources().getColor(R.color.white));
            sectence_tranlate = false;
            int i = snippet.getStart().getParagraphIndex();
            int segmentId = i + BookInfoUtils.getBeginSegmentId(getMyActivity());
//            word.setSegmentId(segmentId);
            QueryUtils.queryBySentence(snippet.getText(), BookInfoUtils.getAbsChapterId(fbReaderApp, getMyActivity()), segmentId, youdaoHandler, getMyActivity());
        }
    }

    @Override
    protected void hide_() {
        super.hide_();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private String getSentenceText(String paragraphText) {
        String[] sentences = paragraphText.split("[.;!?]");
        if (sentences.length > 0) {
            for (int i = 0; i < sentences.length; i++) {
                if (sentences[i].contains(fbview.getSelectedSnippet().getText())) {
                    return sentences[i];
                }
            }
        }
        return "";
    }

    Handler youdaoHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //mContextMenu.setWord2(null);

                    Word obj = (Word) msg.obj;
                    word = obj;
                    if (obj != null) {
                        btnAnnounce.setVisibility(View.VISIBLE);
                        //原来处理文本颜色的方法
//                        SpannableStringBuilder style = new SpannableStringBuilder(obj.toString().replace("\\n","\n"));
//                        style.setSpan(new TextAppearanceSpan(getMyActivity(), R.style.style_word), 0, obj.getWord() == null ? 0 : obj.getWord().get(0).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                        if(obj.getWord() == null){
//                            style.setSpan(new TextAppearanceSpan(getMyActivity(), R.style.yinbiao_word),  0, obj.getUs_phonetic() == null ? 0 : obj.getUs_phonetic().length()+4, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                        }else{
//                            style.setSpan(new TextAppearanceSpan(getMyActivity(), R.style.yinbiao_word), obj.getWord().get(0).length()+1, obj.getUs_phonetic() == null ? obj.getWord().get(0).length()+1 : obj.getWord().get(0).length()+obj.getUs_phonetic().length()+4, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                        }
//                        tvTranslateResult.setText(style);
                        uri = Uri.parse(Urls.YOUDAO_ANNOUNCE_URL+obj.getWord().get(0));
                        //新的处理方案
                        tvTranslateResult.setText(obj.getColorResult());

                        if (word != null && word.getWord()!=null &&!word.getWord().isEmpty()&&StringUtils.hasZh(word.getWord().get(0))) {
                            btnAddToNewWord.setVisibility(View.INVISIBLE);
                        } else {
                            if (isAWord) {
                                btnAddToNewWord.setVisibility(View.VISIBLE);
                            } else {
                                btnAddToNewWord.setVisibility(View.INVISIBLE);
                            }
                        }
                        if(obj!=null&&obj.getWord()!=null&&!obj.getWord().isEmpty()) {
                            String levelNums = QueryUtils.getLevelNumsFromWord(obj.getWord().get(0));
                            if(!TextUtils.isEmpty(levelNums))
                                word.setGradeLevel(levelNums);
                            tvWordLevel.setText(QueryUtils.getLevelsStrFromLevelNums(levelNums,1));
                        }
                        boolean state = sharedPreferenceUtil.getBoolean(SharedPreferenceUtil.AOUT_ADD_NEW_WORD, false);
                        if (state) {
                            if (word.getWord()!=null&&word.getWord().size()>0){
                                addNewWordLocal(word,true);
                             }else {
                                ToastCompat.makeText(getMyActivity(), "该生词为空，不能添加！", Toast.LENGTH_SHORT).show();
                             }
                        }
                    } else {
                        btnAnnounce.setVisibility(View.GONE);
                        tvTranslateResult.setText("所选内容暂时查不到呢~");
                        btnAddToNewWord.setVisibility(View.INVISIBLE);
                        tvWordLevel.setText("");
                    }
                    break;
                case 1:
                    SentenceTranslation result = (SentenceTranslation) msg.obj;
                    if (sharedPreferenceUtil.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 0) == 1) {
                        String translation = result.getSegment_zh() + "\n" + result.getSegment_en();
                        SpannableStringBuilder style = new SpannableStringBuilder(translation);
                        if(!result.getSegment_en().equals("")) {
                            style.setSpan(new TextAppearanceSpan(getMyActivity(), R.style.style_word), 0, result.getSegment_zh().length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        }
                        tvTranslateResult.setText(style);
                    } else {
                        String translation = result.getSegment_en() + "\n" + result.getSegment_zh();
                        SpannableStringBuilder style = new SpannableStringBuilder(translation);
                        if(!result.getSegment_zh().equals("")) {
                            style.setSpan(new TextAppearanceSpan(getMyActivity(), R.style.style_word), 0, result.getSegment_en().length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        }
                        tvTranslateResult.setText(style);
                    }
                    btnAddToNewWord.setVisibility(View.INVISIBLE);
                    tvWordLevel.setText("");
                    btnAnnounce.setVisibility(View.GONE);
                    break;
                case 2:
                    obj = (Word) msg.obj;
                    tvTranslateResult.setText(obj.getTranslation());
                    btnAddToNewWord.setVisibility(View.INVISIBLE);
                    tvWordLevel.setText("");
                    btnAnnounce.setVisibility(View.GONE);
                    break;
                case 3:
                    obj = (Word) msg.obj;
                    tvTranslateResult.setText(obj.getTranslation());
                    btnAddToNewWord.setVisibility(View.INVISIBLE);
                    tvWordLevel.setText("");
                    btnAnnounce.setVisibility(View.GONE);
                    break;
                default:
                    btnAddToNewWord.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    };
}
