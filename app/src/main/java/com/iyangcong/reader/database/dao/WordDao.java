package com.iyangcong.reader.database.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iyangcong.reader.app.AppContext;
import com.iyangcong.reader.bean.NewWord;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.utils.DateUtils;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.stmt.QueryBuilder;
import com.orhanobut.logger.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

public class WordDao extends BaseDao<NewWord> {
    /**
     * @param sqliteOpenHelper using this to get instance of  DatabaseHelper
     */
    public WordDao(OrmLiteSqliteOpenHelper sqliteOpenHelper) {
        super(sqliteOpenHelper);
        dbHelper = (DatabaseHelper) sqliteOpenHelper;
    }

    private DatabaseHelper dbHelper;

    private SQLiteDatabase database;

    private AppContext appContext;

    private long accountId;




	/*
     * 取得生词本数量
	 */

    public synchronized int getCount() {
        database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query("words", null, null, null, null,
                null, null);
        int size = cursor.getCount();
        return size;
    }

    /**
     * 更新我的收藏
     *
     * @param word
     * @param value
     */
    public synchronized NewWord updateFavorite(NewWord word, boolean value) {
        List<NewWord> listword = queryByColumn("id", word.getId());
        NewWord temword = listword.get(0);
        temword.setIsUpload(0);
        temword.setIFfavorite(value ? 1 : 0);
        update(temword);
        return temword;
    }

    /**
     * 更新待复习状态
     *
     * @param word
     * @param value
     */
    public synchronized NewWord updateReadyRecite(NewWord word, boolean value) {
        List<NewWord> listword = queryByColumn("id", word.getId());
        NewWord temword = listword.get(0);
        temword.setIsUpload(0);
        if(value){
            temword.setIFreadyRecite(0);
            temword.setIFneedAgain(1);
            temword.setIFalreadyKnow(0);
        }else{
            temword.setIFreadyRecite(1);
            temword.setIFneedAgain(0);
            temword.setIFalreadyKnow(0);
        }
        update(temword);
		return temword;
	}

    /**
     * 更新待复习的状态，更新完成以后有可能会变成已掌握或者待背诵的状态；
     * @param word
     * @param value
     */
    public synchronized NewWord updateAlreadyKnow(NewWord word, boolean value) {
        List<NewWord> listword = queryByColumn("id", word.getId());
        NewWord temword = listword.get(0);
        temword.setIsUpload(0);
        if (value) {
            temword.setIFalreadyKnow(1);
            temword.setIFneedAgain(0);
        } else {
            temword.setIFalreadyKnow(0);
            temword.setIFneedAgain(1);
        }
        temword.setIFreadyRecite(0);
        update(temword);
        return temword;
    }

    /**
     * 上传成功更新上传状态
     * @param word
     */
    public synchronized void updateUpload(NewWord word) {
        List<NewWord> listword = queryByColumn("id", word.getId());
        if (listword!=null){
            NewWord temword = listword.get(0);
            temword.setIsUpload(1);
            update(temword);
        }
    }
    /**
     * 更新词的状态;
     * @param
     * @return
     */

    public synchronized void updateWord(NewWord word){
        List<NewWord> newWordTmpList = queryByColumn("id", word.getId());
        if (isNull(newWordTmpList)) {
            word.setIsUpload(1);
            add(word);
        } else {
            NewWord newWord = newWordTmpList.get(0);
            newWord.setIsUpload(1);
            newWord.setIFfavorite(newWord.getIFfavorite());
            if(word.getIFalreadyKnow() == 1){
                newWord.setIFalreadyKnow(1);
                newWord.setIFneedAgain(0);
                newWord.setIFreadyRecite(0);
            }else if(word.getIFneedAgain() == 1){
                newWord.setIFalreadyKnow(0);
                newWord.setIFneedAgain(1);
                newWord.setIFreadyRecite(0);
            }else if(word.getIFreadyRecite() == 1){
                newWord.setIFalreadyKnow(0);
                newWord.setIFneedAgain(0);
                newWord.setIFreadyRecite(1);
            }
            update(newWord);
        }
    }
    /**
     * 更新本地数据库单词;
     * @param
     * @return
     */
    public synchronized void addWord(NewWord word){
        List<NewWord> newWordTmpList = queryByColumn("id", word.getId());
        if (isNull(newWordTmpList)) {
            add(word);
        } else {
            NewWord newWord = newWordTmpList.get(0);
            newWord.setIsUpload(1);
            newWord.setIFreadyRecite(1);
            newWord.setIFneedAgain(0);
            newWord.setIFalreadyKnow(0);
            update(newWord);
        }
    }

    /**
     * 更新单词的删除状态，主要用于单词删除
     * @param word
     * @param
     */

    public synchronized void updateDelete(NewWord word){
        List<NewWord> newWordList = queryByColumn("id",word.getId());
        if(isNull(newWordList)){
            Logger.e("查询不到 word = " + word+"的单词");
            return;
        }
        NewWord temWord = newWordList.get(0);
        delete(temWord);
    }


    public synchronized List<NewWord> getOtherWordsList1(boolean ifReadyRecite, boolean IFneedAgain, boolean IFalreadyKnow){
        QueryBuilder builder = daoOpe.queryBuilder();
        try {
            builder.where().eq("CET4",0).and().eq("CET6",0).and().eq("TEM4",0).and().eq("TEM8",0)
                    .and().eq("SAT",0).and().eq("TOEFL",0).and().eq("IELTS",0).and().eq("GRE",0)
                    .and().eq("ifReadyRecite", ifReadyRecite ? 1 : 0).and().eq("IFalreadyKnow", IFalreadyKnow ? 1 : 0)
                    .and().eq("IFneedAgain", IFneedAgain ? 1 : 0);
            return builder.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询所有其他类型的生词
     * @return
	 * @param ifReadyRecite
	 * @param IFneedAgain
	 * @param IFalreadyKnow
     */
    public synchronized List<NewWord> getOtherWordsList(boolean ifReadyRecite, boolean IFneedAgain, boolean IFalreadyKnow){
        return getOtherWordsList1(ifReadyRecite,IFneedAgain,IFalreadyKnow);
    }

    /**
     * 根据单词级别、待背诵状态、待复习状态、已掌握状态和是否删除状态获取单词
     * @param level
     * @param ifReadyRecite
     * @param IFneedAgain
     * @param IFalreadyKnow
     * @return
     */
    public synchronized List<NewWord> gewords(String level, boolean ifReadyRecite, boolean IFneedAgain, boolean IFalreadyKnow){
        try {
            QueryBuilder builder = daoOpe.queryBuilder();
            builder.where().eq(level, 1).and().eq("ifReadyRecite", ifReadyRecite ? 1 : 0).and().eq("IFalreadyKnow", IFalreadyKnow ? 1 : 0).and().eq("IFneedAgain", IFneedAgain ? 1 : 0);
            return builder.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据单词级别、待背诵状态、待复习状态、已掌握状态获取未删除的单词
     * @param level
     * @param ifReadyRecite
     * @param IFneedAgain
     * @param IFalreadyKnow
     * @return
     */
    public synchronized List<NewWord> getwords(String level, boolean ifReadyRecite, boolean IFneedAgain, boolean IFalreadyKnow) {

        return gewords(level,ifReadyRecite,IFneedAgain,IFalreadyKnow);
    }

    /*
     *
     * 取到特定类型的单词列表
     */

    public synchronized void deleteNewWordByUser(long userId){
        List<NewWord> list = queryByColumn("accountId",userId);
        if(isNull(list)){
            Logger.e("wzp 没有查询到accountId = " + accountId+ "  的单词");
            return;
        }
        for(NewWord deleteWord:list){
            delete(deleteWord);
        }
    }

    /**
     * 根据待背诵状态和删除状态获取单词，主要用于获需背诵并且未删除的单词
     * @param
     * @return
     */
    public synchronized List<NewWord> getWordsByDeleteStatusAndIFreadyRecite(){
        return queryByColumn("IFreadyRecite",1);
    }

    /**
     * 根据待复习状态和删除状态获取单词，主要用于获取待复习并且还没有删除的单词
     * @param
     * @return
     */
    public synchronized List<NewWord> getWordsByDeleteStatusAndIFneedAgain(){
        return queryByColumn("IFneedAgain",1);
    }

    /**
     * 根据掌握状态和词的删除状态获取单词，主要用于获取已掌握并且还没有删除的词
     * @param
     * @return
     */
    public synchronized List<NewWord> getWordsByDeleteStatusAndIFalreadyKnow(){
        return queryByColumn("IFalreadyKnow",1);
    }

    public synchronized List<NewWord> getWordsByDeleteStatusAndIFfavorite(){
        return queryByColumn("IFfavorite",1);
    }


}
