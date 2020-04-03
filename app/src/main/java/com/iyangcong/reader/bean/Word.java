package com.iyangcong.reader.bean;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import com.iyangcong.reader.utils.NotNullUtils;
import com.iyangcong.reader.utils.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Word implements Serializable {

    /**
     * this file used by local dictionary
     * author shao
     */
    private static final long serialVersionUID = 1L;
    /**
     * 翻译
     */
    private String translation;
    /**
     * query
     */
    private List<String> word;
    /**
     * 发音
     */
    private String phonetic;
    /**
     * 美式发音
     */
    private String us_phonetic;

    public String getUs_phonetic() {
        return us_phonetic;
    }

    public void setUs_phonetic(String us_phonetic) {
        this.us_phonetic = us_phonetic;
    }

    public String getUk_phonetic() {
        return uk_phonetic;
    }

    public void setUk_phonetic(String uk_phonetic) {
        this.uk_phonetic = uk_phonetic;
    }

    private List<SearchBook> books;

    public List<SearchBook> getBooks() {
        return books;
    }

    public void setBooks(List<SearchBook> books) {
        this.books = books;
    }

    /**
     * 美式发音
     */
    private String uk_phonetic;
    /**
     * 其他形式；
     */
    private String variant;

    public String getSelectedText() {
        return selectedText;
    }

    public void setSelectedText(String selectedText) {
        this.selectedText = selectedText;
    }

    /**
     * 选中本地词；
     */
    private String selectedText;
    /**
     * 其他形式的发音；
     */
    private String varaintPhonetic;
    private Map<String, List<String>> category;
    /**
     * 词组部分的标题
     */
//	private String title;
    /**
     * 标签
     */
    private String tag;

    /**
     * 所属段落segmentId
     */
    private int segmentId;

    public int getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(int segmentId) {
        this.segmentId = segmentId;
    }

    /**
     * 解释
     */
    private List<String> varients;

    public List<String> getVarients() {
        return varients;
    }

    public void setVarients(List<String> varients) {
        this.varients = varients;
    }

    private List<String> explains;
    private String queryErrorTip;//做查询时的错误提示用
    private String gradeLevel;//显示GRE等级
    private String from;//摘自那本书
    private String articleContent;//取处的内容
    private Long bookId;
    /**
     * 网络释义
     */
    private HashMap<String, String> webExplains;
    /**同义词*/
    private String mSynonyms;

    public String getSynonyms() {
        return mSynonyms==null?"":mSynonyms;
    }

    public void setSynonyms(String synonyms) {
        mSynonyms = synonyms;
    }

    public void setQueryErrorTip(String queryErrorTip) {
        this.queryErrorTip = queryErrorTip;
    }

    public String getQueryErrorTip() {
        return queryErrorTip;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public List<String> getWord() {
        return word;
    }

    public void setWord(List<String> word) {
        this.word = word;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public Map<String, List<String>> getCategory() {
        return category;
    }

    public void setCategory(Map<String, List<String>> category) {
        this.category = category;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public List<String> getExplains() {
        return explains;
    }

    public void setExplains(List<String> explains) {
        this.explains = explains;
    }

    public String getGradeLevel() {
        if(gradeLevel == null){
            return "";
        }
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return this.from;
    }

    public void setArticleContent(String content) {
        this.articleContent = content;
    }

    public String getArticleContent() {
        return this.articleContent;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getBookId() {
        return this.bookId;
    }

    public HashMap<String, String> getWebExplains() {
        return webExplains;
    }

    public void setWebExplains(HashMap<String, String> webExplains) {
        this.webExplains = webExplains;
    }

    public String getVaraintPhonetic() {
        return varaintPhonetic;
    }

    public void setVaraintPhonetic(String varaintPhonetic) {
        this.varaintPhonetic = varaintPhonetic;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

//	public String getTitle() {
//		return title;
//	}

//	public void setTitle(String title) {
//		this.title = title;
//	}

    /**
     * 返回带颜色和样式的字符创
     * @return
     */
    public Spanned getColorResult(){
        if(word == null){
            return null;
        }
        if(NotNullUtils.isNull(explains))
            // return word.get(0) + "\n" + "网络释义："+ TextUtils.join(",",webExplains.values().toArray());
            //shao modified begin
            return Html.fromHtml(word.get(0) + " "
                    + (phonetic == null || phonetic.equals("")?"": "<font color='#9e9e9e'>美[" + phonetic + "]</font>" + "<br>")
                    + (webExplains==null||webExplains.equals("")?
                    (translation==null||"".equals(translation.trim()))?
                            " 暂无释义":translation:
                    "网络释义："+TextUtils.join(",",webExplains.values().toArray())+"\n"));
        //shao modified end
        return Html.fromHtml(word.get(0) + " "
                /*+ (translation == null||translation.equals("")?"":"译：" + translation+ "\n")*/
                + (phonetic == null || phonetic.equals("")?"": "<font color='#9e9e9e'>美[" + phonetic + "]</font>" + "<br>")
                + (StringUtils.listToString(explains) == null || StringUtils.listToString(explains).equals("")?"":/*"释：" + */StringUtils.listToString(explains) + "\n"))
                /*+ (StringUtils.mapToString(webExplains) == null||StringUtils.mapToString(webExplains).equals("") ?"":"网络释义\n" + StringUtils.mapToString(webExplains)+ "\n")*/;
    }

    @Override
    public String toString() {
        if(word == null){
            return "";
        }
        if(NotNullUtils.isNull(explains))
           // return word.get(0) + "\n" + "网络释义："+ TextUtils.join(",",webExplains.values().toArray());
        //shao modified begin
            return word.get(0) + " "
                    + (phonetic == null || phonetic.equals("")?"": "美[" + phonetic + "]" + " \n")
                    + (webExplains==null||webExplains.equals("")?
                            (translation==null||"".equals(translation.trim()))?
                                    " 暂无释义":translation:
                            "网络释义："+TextUtils.join(",",webExplains.values().toArray())+"\n");
        //shao modified end
        return word.get(0) + " "
                /*+ (translation == null||translation.equals("")?"":"译：" + translation+ "\n")*/
                + (phonetic == null || phonetic.equals("")?"": "美[" + phonetic + "]" + " \n")
                + (StringUtils.listToString(explains) == null || StringUtils.listToString(explains).equals("")?"":/*"释：" + */StringUtils.listToString(explains) + "\n")
                /*+ (StringUtils.mapToString(webExplains) == null||StringUtils.mapToString(webExplains).equals("") ?"":"网络释义\n" + StringUtils.mapToString(webExplains)+ "\n")*/;
    }
}
