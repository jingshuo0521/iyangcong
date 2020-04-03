package com.iyangcong.reader.bean;


import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.iyangcong.reader.utils.DateUtils;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Date;
import java.io.Serializable;
import java.util.List;


import static com.iyangcong.reader.utils.NotNullUtils.tranforNull;

/**
 * 生词表
 */
@DatabaseTable(tableName = "words")
public class NewWord implements Serializable, Comparable<Object> {

    private static final long serialVersionUID = 4793742764389145378L;
    @DatabaseField(generatedId = true)
    @SerializedName("_id")
    private int id;
    @DatabaseField(columnName = "wordId")
    @SerializedName(value = "id",alternate = "Id")
    private int wordId;
    @DatabaseField(columnName = "serverWordId")
    private int serverWordId;
    @DatabaseField(columnName = "word")
    private String word;
    @DatabaseField(columnName = "phonetic")
    private String phonetic;
    private List<String> contentList;
    @DatabaseField(columnName = "content")
    @SerializedName("content")
    private String tempContent;
    @DatabaseField(columnName = "memo")
    private String memo;
    @DatabaseField(columnName = "bookId")
    private Long bookId;
    @DatabaseField(columnName = "bookName")
    private String bookName;
    @DatabaseField(columnName = "articleContent")
    private String articleContent;
    @DatabaseField(columnName = "accountId")
    @SerializedName("userId")
    private int accountId;
    @DatabaseField(columnName = "CET4")
    private int CET4;
    @DatabaseField(columnName = "CET6")
    private int CET6;
    @DatabaseField(columnName = "TEM4")
    private int TEM4;
    @DatabaseField(columnName = "TEM8")
    private int TEM8;
    @DatabaseField(columnName = "SAT")
    private int SAT;
    @DatabaseField(columnName = "TOEFL")
    private int TOEFL;
    @DatabaseField(columnName = "IELTS")
    private int IELTS;
    @DatabaseField(columnName = "GRE")
    private int GRE;
    @DatabaseField(columnName = "level")
    @SerializedName("level")
    private String level;//like 4 6
    /**
     * 1：已上传成功 , 0: 未上传成功；
     */
    @DatabaseField(columnName = "isUpload")
    private int isUpload;

    /**
     * 段id
     */
    @DatabaseField(columnName = "segmentId")
    private int segmentId;
    /**
     * 本地单词
     */
    @DatabaseField(columnName = "localWord")
    private String localWord;

    public String getLocalWord() {
        return TextUtils.isEmpty(localWord)?word:localWord;
    }

    public void setLocalWord(String localWord) {
        this.localWord = localWord;
    }

    public int getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(int segmentId) {
        this.segmentId = segmentId;
    }

    public int getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(int isUpload) {
        this.isUpload = isUpload;
    }


    /**
     * 最近更新时间
     */
    @DatabaseField(columnName = "lastupdatetime")
    public String lastUpdateTime;


    @DatabaseField(columnName = "status")
    public int status;


    @SerializedName("n_status")
    @DatabaseField(columnName = "Isdelete")
    public int isDelete;

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    /**
     * 1：需背诵 , 0: 不需要背诵；
     */
    @DatabaseField(columnName = "IFreadyRecite")
    @SerializedName("ifReadyRecite")
    private int IFreadyRecite;
    /**
     * 是否已掌握,1:已掌握，0：未掌握
     */
    @DatabaseField(columnName = "IFalreadyKnow")
    @SerializedName(value = "ifAlreadyKnow")
    private int IFalreadyKnow;
    /**n
     * 是否已收藏
     */
    @DatabaseField(columnName = "IFfavorite")
    @SerializedName(value = "ifFavorite")
    private int IFfavorite;
    /**
     * 待复习 1：需要复习，0：不需要复习
     */
    @DatabaseField(columnName = "IFneedAgain")
    @SerializedName("ifNeedAgain")
    private int IFneedAgain;

    public String getTime() {
        time = DateUtils.getSystemDateFormat("yyyy-MM-dd HH:mm:ss");
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    /**
     * 最近更新时间
     */
//    @DatabaseField(columnName = "lastupdatetime")
//    public String lastUpdateTime;

    @DatabaseField(columnName = "time")
    public String time;


    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getTempContent() {
        if(TextUtils.isEmpty(tempContent)||"-1".equals(tempContent))
            return "暂无释义";
        return tempContent;
    }

    public void setTempContent(String tempContent) {
        this.tempContent = tempContent;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public List<String> getContent() {
        if(contentList == null){
            return new ArrayList<String>();
        }
        return contentList;
    }

    public void setContent(List<String> contentList) {
        this.contentList = contentList;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookName() {
        return TextUtils.isEmpty(bookName)?"":bookName;
    }
    public void setArticleContent(String articleContent) {
        this.articleContent = articleContent;
    }

    public String getArticleContent() {
        return this.articleContent;
    }

    public void setCET4(int cet4) {
        this.CET4 = cet4;
    }

    public int getCET4() {
        return this.CET4;
    }

    public void setCET6(int cet6) {
        this.CET6 = cet6;
    }

    public int getCET6() {
        return this.CET6;
    }

    public void setTEM4(int tem4) {
        this.TEM4 = tem4;
    }

    public int getTEM4() {
        return this.TEM4;
    }

    public void setTEM8(int tem8) {
        this.TEM8 = tem8;
    }

    public int getTEM8() {
        return this.TEM8;
    }

    public void setSAT(int sat) {
        this.SAT = sat;
    }

    public int getSAT() {
        return this.SAT;
    }

    public void setTOEFL(int toefl) {
        this.TOEFL = toefl;
    }

    public int getTOEFL() {
        return this.TOEFL;
    }

    public void setIELTS(int ielts) {
        this.IELTS = ielts;
    }

    public int getIELTS() {
        return this.IELTS;
    }

    public void setGRE(int gre) {
        this.GRE = gre;
    }

    public int getGRE() {
        return this.GRE;
    }

    public void setIFreadyRecite(int iFreadyRecite) {
        this.IFreadyRecite = iFreadyRecite;
    }

    public int getIFreadyRecite() {
        return this.IFreadyRecite;
    }

    public void setIFalreadyKnow(int iFalreadyKnow) {
        this.IFalreadyKnow = iFalreadyKnow;
    }

    public int getIFalreadyKnow() {
        return this.IFalreadyKnow;
    }

    public void setIFfavorite(int iFfavorite) {
        this.IFfavorite = iFfavorite;
    }

    public int getIFfavorite() {
        return this.IFfavorite;
    }

    public void setIFneedAgain(int iFneedAgain) {
        this.IFneedAgain = iFneedAgain;
    }

    public int getIFneedAgain() {
        return this.IFneedAgain;
    }

    @Override
    public int compareTo(Object o) {
        if (this == o) {
            return 0;
        } else if (o != null && o instanceof NewWord) {
            NewWord n = (NewWord) o;
            if (word.equalsIgnoreCase(n.getWord())) {
                if (articleContent.equalsIgnoreCase(n.getArticleContent())) {
//                    if (serverWordId == n.getServerWordId()) {
//                        Date local = DateUtils.StringToDate(lastUpdateTime,
//                                "yyyy-MM-dd HH:mm:ss");
//                        Date server = DateUtils.StringToDate(n.getLastUpdateTime(),
//                                "yyyy/MM/dd HH:mm:ss");
//                        long localTime = local.getTime();
//                        long serverTime = server.getTime();
//                        if (localTime >= serverTime) {
//                            //本地的是最新的,本地不需要作操作，可能是需要上传的数据
//                            return 0;
//                        } else if (localTime < serverTime) {
//                            //服务器是最新的，需要将本地数据更新成服务器数据
//                            return 2;
//                        }
//                    } else
//                        return 2;//word和articleContent一直，serverWordId不一致，把本地的更新成服务器的
                }
            } else
                return 1;
        }
        return 0;
    }

//    public String getFormatString(){
//        return wordId + ","
//                + IFreadyRecite + ","
//                + IFalreadyKnow + ","
//                + IFfavorite + ","
//                + IFneedAgain +  ","
//                + lastUpdateTime + ","
//                + isDelete + ";";
//    }

    //2017-06-21商量出来的上传单词的格式
//    public String formateString(){
//        return bookId + ",,"
//                + tranforNull(bookName) +",,"
//                + tranforNull(word) + ",,"
//                + tranforNull(phonetic) + ",,"
//                + tranforNull(tempContent) + ",,"
//                + tranforNull(articleContent) + ",,"
//                + (CET4+"_"+CET6+"_"+TEM4+"_"+TEM8+"_"+SAT+"_"+TOEFL+"_"+IELTS+"_"+GRE)+",,"
//                + IFreadyRecite + ",,"
//                + IFalreadyKnow + ",,"
//                + IFfavorite + ",,"
//                + IFneedAgain + ",,"
//                + isDelete+ ",,"
//                + DateUtils.compareLastUpdateTime(lastUpdateTime) + ";;";
//
//    }

    @Override
    public String toString() {
        return "NewWord{" +
                "word='" + word + '\'' +
                ", phonetic='" + phonetic + '\'' +
                ", contentList=" + contentList +
                ", tempContent='" + tempContent + '\'' +
                ", memo='" + memo + '\'' +
                ", bookId=" + bookId +
                ", bookName='" + bookName + '\'' +
                ", articleContent='" + articleContent + '\'' +
                ", accountId=" + accountId +
                ", CET4=" + CET4 +
                ", CET6=" + CET6 +
                ", TEM4=" + TEM4 +
                ", TEM8=" + TEM8 +
                ", SAT=" + SAT +
                ", TOEFL=" + TOEFL +
                ", IELTS=" + IELTS +
                ", GRE=" + GRE +
                ", level='" + level + '\'' +
                ", IFreadyRecite=" + IFreadyRecite +
                ", IFalreadyKnow=" + IFalreadyKnow +
                ", IFfavorite=" + IFfavorite +
                ", IFneedAgain=" + IFneedAgain +
                ", isUpload=" + isUpload +
                ", time=" + time +
                ", localWord=" + localWord +
                '}';
    }
}
