package com.iyangcong.reader.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.iyangcong.reader.bean.BookInfo;
import com.iyangcong.reader.database.DatabaseHelper;
import com.iyangcong.reader.database.dao.BookInfoDao;
import com.orhanobut.logger.Logger;

import org.geometerplus.fbreader.book.Bookmark;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.FBView;
import org.geometerplus.zlibrary.text.model.ZLTextParagraph;
import org.geometerplus.zlibrary.text.view.ZLTextElement;
import org.geometerplus.zlibrary.text.view.ZLTextView;
import org.geometerplus.zlibrary.text.view.ZLTextWord;
import org.geometerplus.zlibrary.text.view.ZLTextWordCursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DarkFlameMaster on 2017/5/7.
 */

public class BookInfoUtils {

    /**
     * 希腊数字转罗马数字
     */
    public static int exchangeNum(String s) {
        // TODO 自动生成的方法存根
        int[] a = new int[256];
        a['I'] = 1;
        a['V'] = 5;
        a['X'] = 10;
        a['C'] = 100;
        a['M'] = 1000;
        a['L'] = 50;
        a['D'] = 500;
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            //如果是最后一个字符，或者前一个大于后面的，就加，否则减
            if (i == s.length() - 1 || a[s.charAt(i+1)] <= a[s.charAt(i)]) {
                val += a[s.charAt(i)];
            }else {
                val -= a[s.charAt(i)];
            }
        }
        return val;
    }

    /**
     * 判断字符串中是否包含数字
     * @param source 待判断字符串
     * @return 字符串中是否包含数字，true：包含数字，false：不包含数字
     */
    public static boolean containDigit(String source) {
        char ch;
        for(int i=0; i<source.length(); i++) {
            ch = source.charAt(i);
            if(ch >= '0' && ch <= '9') {
                return true;
            }
        }

        return false;
    }
    public static boolean isHave(String[] strs,String s){
        /*此方法有两个参数，第一个是要查找的字符串数组，第二个是要查找的字符或字符串
         * */
        for(int i=0;i<strs.length;i++){
            if(strs[i].indexOf(s)!=-1){//循环查找字符串数组中的每个字符串中是否包含所有查找的内容
                return true;//查找到了就返回真，不在继续查询
            }
        }
        return false;//没找到返回false
    }
    /**
     * 获取相对章节id
     * @param Reader
     * @param context
     * @return
     */
    public static int getRelativeChapterId(FBReaderApp Reader, Context context) {
        return getChapterIdByRelative(Reader, context, true);
    }

    /**
     * <p>获得当前页章节Id。这个方法设置为私有，然后暴露了两个共有的相关方法，这样代码可读性会更高。维护代码逻辑也简单一些</p>
     * @param Reader
     * @param context
     * @param isRelative 是否是相对的章节id
     * @return
     */
    private static int getChapterIdByRelative(FBReaderApp Reader, Context context, boolean isRelative) {
        Gson gson = new Gson();
        BookInfoDao bookInfoDao = new BookInfoDao(DatabaseHelper.getHelper(context));
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance();
        String str = (Reader.getCurrentTOCElement() == null ? "" : (Reader.getCurrentTOCElement().getText() == null ? "" : Reader.getCurrentTOCElement().getText()));
        int chapterId = -1;
        String[] xila = {"I","II","III","IV","V","VI","VII","VIII","IX","X","XI","XII",
                "XIII","XIV","XV","XVI","XVII","XVIII","XIX","XX","XXI","XXII","XXV",
                "XXVI","XXVII","XXVIII","XXIX","XXX","XXXI","XXXII","XXXII","XXXIV",
                "XXXV","XXXVI","XXXVII","XXXVIII","XXXIX","XL","XLI","XLII","XLIII","XLIV","XLV"};
        List<BookInfo> bookInfoList = bookInfoDao.queryByColumn("bookId", (int) sp.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0));
//        List<BookInfo> bookInfoList = bookInfoDao.queryByColumn("bookId", (int) sp.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0), "language", sp.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 0));
        if (bookInfoList != null && bookInfoList.size() != 0) {
            int[] chapterIdList = gson.fromJson(bookInfoList.get(0).getChapterId(), int[].class);
            String[] chapterNameList = gson.fromJson(bookInfoList.get(0).getEnglishChapterName(), String[].class);
            if(chapterNameList !=null){
                for (int i = 0; i < chapterNameList.length; i++) {
                    chapterNameList[i] = chapterNameList[i].replaceAll("</.*>", "");

                    chapterNameList[i] = chapterNameList[i].replaceAll("   "," ");
                    if (str.contains(chapterNameList[i]) || chapterNameList[i].contains(str)) {
                        if (isRelative) {
                            chapterNameList[i] = chapterNameList[i].replaceAll("—", " ");
                            String[] AA= chapterNameList[i].split("\\s+");
                            for (String aa:AA){
                                if (isHave(xila,aa)){
                                    chapterId =exchangeNum(aa);
                                    break;
                                }
//                                else if (containDigit(aa)){
//                                    chapterId = Integer.parseInt(aa);
//                                    break;
//                                }
                                else {
                                    chapterId = i+1;
                                    break;
                                }
                            }
//                            chapterId = chapterIdList[i] - chapterIdList[0];
                        } else {
                            chapterId = chapterIdList[i];
                        }
                        break;
                    }
                }
            }

            //如果在英文的章节中没有找到，则到中文章节中找对应的章节的id;
            if(chapterId==-1){
                chapterNameList = gson.fromJson(bookInfoList.get(0).getChineseChapterName(),String[].class);
                if(chapterNameList != null)
                    for(int i = 0;i<chapterNameList.length;i++){
                        if (str.contains(chapterNameList[i]) || chapterNameList[i].contains(str)) {
                            if (isRelative) {
                                String[] AA= chapterNameList[i].split("\\s+");
                                for (String aa:AA){
                                    if (isHave(xila,aa)){
                                        chapterId =exchangeNum(aa);
                                        break;
                                    }
//                                    else if (containDigit(aa)){
//                                        chapterId = Integer.parseInt(aa);
//                                        break;
//                                    }
                                    else {
                                        chapterId = i+1;
                                        break;
                                    }
                                }
//                            chapterId = chapterIdList[i] - chapterIdList[0];
                            } else {
                                chapterId = chapterIdList[i];
                            }
                            break;
                        }
                    }
            }
        }
        return chapterId==-1?0:chapterId;
    }

    /**
     * 根据绝对章节id获取章节名称
     * @param chapterId
     * @param context
     * @return
     */
    public static String getChapterNameByAbsChapterId(int chapterId,int language,Context context){
        Gson gson = new Gson();
        BookInfoDao bookInfoDao = new BookInfoDao(DatabaseHelper.getHelper(context));
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance();
        List<BookInfo> bookInfoList = bookInfoDao.queryByColumn("bookId", (int) sp.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0));
        String chapterName = "";
        if (bookInfoList != null && bookInfoList.size() != 0) {
            String[] chapterNameList;
            if (language == 1)
                chapterNameList = gson.fromJson(bookInfoList.get(0).getChineseChapterName(), String[].class);
            else
                chapterNameList = gson.fromJson(bookInfoList.get(0).getEnglishChapterName(), String[].class);
            int[] chapterIdList = gson.fromJson(bookInfoList.get(0).getChapterId(), int[].class);
            int index = -1;
            for(int i=0;i<chapterIdList.length;i++){
                if(chapterIdList[i] == chapterId){
                    index = i;
                }
            }
            if(index == -1 || index > chapterNameList.length){
                return "未找到对应章节";
            }
            chapterName = chapterNameList[index];
        }
        String finalChapterName = StringUtils.delHTMLTag(chapterName);
        return finalChapterName;
    }

    /**
     * 获取绝对章节id
     * @param Reader
     * @param context
     * @return
     */
    public static int getAbsChapterId(FBReaderApp Reader,Context context){
        return getChapterIdByRelative(Reader,context,false);
    }

    /**
     * 获取当前阅读的书籍的起始段落id.这个段落id存在本地数据库中。
     * @param context
     * @return segmentId
     */
    public static int getBeginSegmentId(Context context) {
        BookInfoDao bookInfoDao = new BookInfoDao(DatabaseHelper.getHelper(context));
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance();
        int segmentId = 0;
        List<BookInfo> bookInfoList = bookInfoDao.queryByColumn("bookId", (int) sp.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0));
//        List<BookInfo> bookInfoList = bookInfoDao.queryByColumn("bookId", (int) sp.getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0), "language", sp.getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 0));
        if (bookInfoList != null && bookInfoList.size() != 0) {
            segmentId = bookInfoList.get(0).getSegmentId();
        }
        return segmentId;
    }

    /**
     * <p><获取ios端的对应的偏移量。/p>
     * <p>安卓和ios对偏移量计算方式不同。ios阅读器中的偏移量指的是字符在文本中的相对位置。安卓不以字符计算，而是以一个Element计算。所以要根据element对应的类型，算出ios阅读器中的偏移量</p>
     * @param Reader
     * @param bookmark
     * @param languageType 1:中文    2:英文 这个参数其实已经没有用了。
     * @return
     */
    public static String getStartAndEndOffset(FBReaderApp Reader, Bookmark bookmark, int languageType) {
        final StringBuilder sb = new StringBuilder();
        final ZLTextWordCursor cursor = new ZLTextWordCursor(Reader.getTextView().getStartCursor());
        cursor.moveToParagraph(bookmark.getParagraphIndex());
        cursor.moveToParagraphStart();
        String result;
        int index = 0,count = 0;
        while (!cursor.isEndOfParagraph()&&index<bookmark.getElementIndex()) {
            ZLTextElement element = cursor.getElement();
            index++;
            if (element instanceof ZLTextWord) {
                count += ((ZLTextWord) element).Length;
            }else{
                count++;
            }
            cursor.nextWord();
        }

        result = count+","+(count + bookmark.getText().replaceAll(" ","a").length());
        return result;
    }

    /**
     * 获取选中的文字
     * @param Reader
     * @return
     */
    public static String getParagraphText(FBReaderApp Reader) {
        final StringBuilder sb = new StringBuilder();
        final ZLTextWordCursor cursor = new ZLTextWordCursor(Reader.getTextView().getStartCursor());
        FBView fbview = Reader.getTextView();
        if (fbview == null) {
            return "";
        }
        cursor.moveToParagraph(fbview.getSelectedSnippet().getStart().getParagraphIndex());
        cursor.moveToParagraphStart();
        while (!cursor.isEndOfParagraph()) {
            ZLTextElement element = cursor.getElement();
            if (element instanceof ZLTextWord) {
                sb.append(element.toString()).append(" ");
            }
            cursor.nextWord();
        }
        return sb.toString();
    }

    /**
     * <p>根据ios的偏移量算出安卓对应的偏移量。</p>
     * @param Reader
     * @param paragraphIndex
     * @param startIndex
     * @param text
     * @param languageType 这个参数其实用不到了
     * @return
     */
    public static int[] getOffsets(FBReaderApp Reader, int paragraphIndex, int startIndex,int endIndex, String text, int languageType) {
        final StringBuilder sb = new StringBuilder();
        final ZLTextWordCursor cursor = new ZLTextWordCursor(Reader.getTextView().getStartCursor());
        int[] offsets = new int[2];
        cursor.moveToParagraph(paragraphIndex);
        cursor.moveToParagraphStart();
        int index = 0,startCounter = 0,lengthCounter=0;
        if(endIndex<startIndex){
            endIndex = startIndex + text.length();
        }
        while (!cursor.isEndOfParagraph()) {
            if (cursor.isNull()) {
                return null;
            }
            ZLTextElement element = cursor.getElement();
            if(element != null) {
                if (startCounter >= startIndex) {
                    offsets[0] = index;
                    break;
                }
                if (element instanceof ZLTextWord) {
                    startCounter += ((ZLTextWord) element).Length;
                }else{
                    startCounter++;
                }
                index++;
            }
            cursor.nextWord();
        }
        while (!cursor.isEndOfParagraph()) {
            if (cursor.isNull()) {
                return null;
            }
            ZLTextElement element = cursor.getElement();
            if(element != null) {
                if (element instanceof ZLTextWord) {
                    startCounter += ((ZLTextWord) element).Length;
                }else {
                    startCounter++;
                }
                if (startCounter >= endIndex) {
                    offsets[1] = index;
                    break;
                }
                index++;
            }
            cursor.nextWord();
        }
//        boolean isStart = true;
//        while (!cursor.isEndOfParagraph()) {
//            if (cursor.isNull()) {
//                return null;
//            }
//            ZLTextElement element = cursor.getElement();
//            if(element != null){
//                index++;
//                if (element instanceof ZLTextWord) {
//                    if(isStart){
//                        startCounter += ((ZLTextWord) element).Length;
//                    }else{
//                        lengthCounter += ((ZLTextWord) element).Length;
//                    }
//
//                    sb.append(element.toString());
//                } else if(element == ZLTextElement.HSpace){
//                    if(!isStart)
//                        lengthCounter++;
//                }
//                if(startCounter <= startIndex){
//                    offsets[0] = index;
//                }else  if(isStart){
//                    cursor.previousWord();
//                    index--;
//                    isStart = false;
//                }
//                if(!isStart&&lengthCounter <= text.length()){
//                    offsets[1] = index;
//                }
//            }
//            cursor.nextWord();
//        }
        //TODO
//        if (languageType == LanguageType.EN || (sb.toString().getBytes().length == sb.toString().length())) {
//            if (startIndex < 0 ||sb.length() < startIndex || sb.toString().equals("")) {
//                return offsets;
//            }
//            String startOffsetStr = sb.substring(0, startIndex);
//            String[] startOffsetStrs = startOffsetStr.split(" ");
//            int startOffset = 2;
//            for (String s : startOffsetStrs) {
//                if (!s.equals("")) {
//                    startOffset++;
//                }
//            }
//            startOffset += startOffsetStrs.length - 1;
//            int endOffset = startOffset;
//
//            String[] endOffsetStrs = text.split(" ");
//            for (String s : endOffsetStrs) {
//                if (!s.equals("")) {
//                    endOffset++;
//                }
//            }
//            endOffset += endOffsetStrs.length - 1;
//            offsets[0] = startOffset;
//            offsets[1] = endOffset;
//        } else {
//            String startOffsetStr;
//            if (startIndex == 0 || startIndex > sb.toString().replaceAll(" ", "").length() - 1) {
//                startOffsetStr = "";
//            } else {
//                startOffsetStr = sb.toString().replaceAll(" ", "").substring(0, startIndex);
//            }
//            String tmpStr1 = startOffsetStr.replaceAll("[，。；！？：“”~《》]", "");
//            int startOffset = tmpStr1.length() + 2;
//
//            String tmpStr2 = text.replaceAll("[，。；！？：“”~《》]", "");
//            int endOffset = startOffset + tmpStr2.length() - 1;
//            offsets[0] = startOffset;
//            offsets[1] = endOffset;
//        }
        return offsets;
    }

    /**
     * <p>获取某本书的第一章的id，如果有前言，则获取前言的章节id;</p>
     * @param bookInfo
     * @return
     */
    public static int getBooksFisrtChapterId(BookInfo bookInfo) {
        Gson gson = new Gson();
        int[] chapterId = gson.fromJson(bookInfo.getChapterId(), int[].class);
        if (chapterId == null || chapterId.length == 0)
            return -1;
        return chapterId[0];
    }

    /**
     * 获取绝对的段落id.这个段落id指的是epub文件中的段落id
     * @param app
     * @return
     */
    public static int getAbsoluteParagraphId(FBReaderApp app){
        return getAbsoluteParagraphId(app,-1);
    }
    public static int getAbsoluteParagraphId2(FBReaderApp app){
        return getAbsoluteParagraphId2(app,-1);
    }
    /**
     * 根据书签数据中对应的段落id获取epub文件中的绝对段落id
     * @param app
     * @param bookMarkerParagraphIndex
     * @return
     */
    public static int getAbsoluteParagraphId(FBReaderApp app,int bookMarkerParagraphIndex){
        ZLTextView textView = app.getTextView();
        if(app == null || app.Model == null ||textView == null || textView.getStartCursor().isNull())
            return -1;
        String originId;
        int paragraphIndex = bookMarkerParagraphIndex;
        // TODO: 2019/9/3 找书签错
        if(bookMarkerParagraphIndex >0){
            originId = app.Model.getParagraphId(paragraphIndex);
            Log.e("AbsoluteParagraphId1", "getAbsoluteParagraphId: " + originId);
        }else{
            int result = 0;
            ZLTextWordCursor tmpCursor = textView.getStartCursor();
            while (tmpCursor != null && !tmpCursor.isNull()&&tmpCursor.getParagraphCursor().isEmpty()){
                if(!tmpCursor.nextParagraph()){//如果后面一段没有的话就取空的段落的前一段；
                    tmpCursor.previousParagraph();
                }
            }
            paragraphIndex = tmpCursor.getParagraphIndex();
            originId = app.Model.getParagraphId(paragraphIndex);
            if(originId == null){
                final int maxNumber = tmpCursor.getParagraphIndex();
                for(int index = 0;index<=maxNumber;index++){
                    ZLTextParagraph tmpParagraph = textView.getModel().getParagraph(index);
                    if(tmpParagraph.getTag() == ZLTextParagraph.ParagraphTag.TAG_P){
                        result++;
                    }
                }
                if(result>0)
                    result--;
                originId = app.Model.getParagraphId(result);
            }
            Log.e("AbsoluteParagraphId2", "getAbsoluteParagraphId: " + originId);
        }
        if(originId == null)
            return -2;
        //发现epub中有脚注，对应的id为achor,所以先前移动，取最近的一个实际的段落id;
        // 试读版的论语（英文版）最后一章<p class="footnote"><a href="#ac1" id="anchor1">
        while (!android.text.TextUtils.isDigitsOnly(originId)) {
            originId = app.Model.getParagraphId(--paragraphIndex);
            Log.e("AbsoluteParagraphId3", "getAbsoluteParagraphId: " + originId);
        }
        return Integer.parseInt(originId);
    }

    /**
     * author:GuoFangtao </br>
     * time:2020-01-13 10:30 </br>
     * 获取当前页的末尾段落
     */
    public static int getAbsoluteParagraphId2(FBReaderApp app,int bookMarkerParagraphIndex){
        ZLTextView textView = app.getTextView();
        if(app == null || app.Model == null ||textView == null || textView.getEndCursor().isNull())
            return -1;
        String originId;
        int paragraphIndex = bookMarkerParagraphIndex;
        // TODO: 2019/9/3 找书签错
        if(bookMarkerParagraphIndex >0){
            originId = app.Model.getParagraphId(paragraphIndex);
            Log.e("AbsoluteParagraphId1", "getAbsoluteParagraphId: " + originId);
        }else{
            int result = 0;
            ZLTextWordCursor tmpCursor = textView.getEndCursor();
            while (tmpCursor != null && !tmpCursor.isNull()&&tmpCursor.getParagraphCursor().isEmpty()){
                if(!tmpCursor.nextParagraph()){//如果后面一段没有的话就取空的段落的前一段；
                    tmpCursor.previousParagraph();
                }
            }
            paragraphIndex = tmpCursor.getParagraphIndex();
            originId = app.Model.getParagraphId(paragraphIndex);
            if(originId == null){
                final int maxNumber = tmpCursor.getParagraphIndex();
                for(int index = 0;index<=maxNumber;index++){
                    ZLTextParagraph tmpParagraph = textView.getModel().getParagraph(index);
                    if(tmpParagraph.getTag() == ZLTextParagraph.ParagraphTag.TAG_P){
                        result++;
                    }
                }
                if(result>0)
                    result--;
                originId = app.Model.getParagraphId(result);
            }
            Log.e("AbsoluteParagraphId2", "getAbsoluteParagraphId: " + originId);
        }
        if(originId == null)
            return -2;
        //发现epub中有脚注，对应的id为achor,所以先前移动，取最近的一个实际的段落id;
        // 试读版的论语（英文版）最后一章<p class="footnote"><a href="#ac1" id="anchor1">
        while (!android.text.TextUtils.isDigitsOnly(originId)) {
            originId = app.Model.getParagraphId(--paragraphIndex);
            Log.e("AbsoluteParagraphId3", "getAbsoluteParagraphId: " + originId);
        }
        return Integer.parseInt(originId);
    }

    /**
     * author:GuoFangtao </br>
     * time:2020-01-13 10:30 </br>
     * 获取当前页的segmentId
     */
    public static List<Integer> getCurrentPageSegmentIds(FBReaderApp app){
        List<Integer> segmentIds = new ArrayList<>();
        int startSegmentId = BookInfoUtils.getAbsoluteParagraphId(app);
        int endSegmentId = BookInfoUtils.getAbsoluteParagraphId2(app);
        Logger.e("gft测试startSegmentId：%d, gft测试endSegmentId：%d ",startSegmentId,endSegmentId);
        for (int i = 0; i <= endSegmentId - startSegmentId; i++){
            int id = startSegmentId+i;
            segmentIds.add(id);
            Log.e("gft测试", "gft测试segmentId: " + id);
        }
        return segmentIds;
    }
    /**
     * author:WuZepeng </br>
     * time:2018-05-14 9:30 </br>
     * desc:method_params: [app FBreaderApp的实例]* </br>
     * 本方法通过生成一个临时书签，获得本段在书籍中的段落的Id.将Id转换成p标签的数量。
     * p标签的数量才是实际上后台对应的相对的段落；
     * method_return: int 返回的是绝对的段落Id </br>
     */
    public static int getTextRelativeParagraphIndex(FBReaderApp app){
       return getTextRelativeParagraphIndexWithParagraphIndex(app,-1);
    }

    /**
     * author:WuZepeng </br>
     * time:2018-05-14 9:30 </br>
     * desc:method_params: [app FBreaderApp的实例 bookMarkerParagraphIndex:本地书签对应的段落Id]* </br>
     * 本方法通过生成一个临时书签，获得本段在书籍中的段落的Id.将Id转换成p标签的数量。
     * p标签的数量才是实际上后台对应的相对的段落；
     * method_return: int 返回的是绝对的段落Id </br>
     */
    public static int getTextRelativeParagraphIndexWithParagraphIndex(FBReaderApp app, int bookMarkerParagraphIndex){
        final ZLTextView textView =  app.getTextView();
        if(textView == null || textView.getStartCursor().isNull())
            return -1;
        final String originId ;
        int result = 0;
        if(bookMarkerParagraphIndex > 0){
            originId = app.Model.getParagraphId(bookMarkerParagraphIndex);
            for(int index = 0;index<=bookMarkerParagraphIndex;index++){
                ZLTextParagraph tmpParagraph = textView.getModel().getParagraph(index);
                if(tmpParagraph.getTag() == ZLTextParagraph.ParagraphTag.TAG_P){
                    result++;
                }
            }

            Logger.e("计算的结果：%d,书签产生的段落Id：%d 总数：%d",result,bookMarkerParagraphIndex,textView.getModel().getParagraphsNumber());
        }else{

//            final Bookmark bookMarker = app.addBookmark(80, false);
            //通过书签生成的段落id,这个段落id等于本段之前的p标签的和加上其他非p标签被识别为段的和
//            final int maxNumber = bookMarker.getParagraphIndex();
            ZLTextWordCursor tmpCursor = textView.getStartCursor();
            while (tmpCursor != null && !tmpCursor.isNull()&&tmpCursor.getParagraphCursor().isEmpty()){
                if(!tmpCursor.nextParagraph()){//如果后面一段没有的话就取空的段落的前一段；
                    tmpCursor.previousParagraph();
                }
            }
            originId = app.Model.getParagraphId(textView.getStartCursor().getParagraphIndex());
            final int maxNumber = tmpCursor.getParagraphIndex();
            for(int index = 0;index<=maxNumber;index++){
                ZLTextParagraph tmpParagraph = textView.getModel().getParagraph(index);
                if(tmpParagraph.getTag() == ZLTextParagraph.ParagraphTag.TAG_P){
                    result++;
                }
            }
            Logger.e("书签的段落Id:%d .nlinks计算的结果：%s 计算的结果：%d 总数：%d",maxNumber,originId,result,textView.getModel().getParagraphsNumber());
//            app.Collection.deleteBookmark(bookMarker);
        }
        return result>0?result-1:0;
//        if(originId == null)
//            return -1;
//        return Integer.parseInt(originId);
    }

    /**
     * 根据绝对段落id获得相对的段落id。这个id是fbreader中的段落
     * @param app
     * @param absParagrapId
     * @return
     */
    public static int getLocaParagraphIndex(FBReaderApp app,int absParagrapId){
        if(app == null || app.Model == null){
            return -1;
        }
        int result = app.Model.getparagraphIndex(String.valueOf(absParagrapId));
        Logger.e("wzp absParagraphId:%d 获取到的相对的段落id：%d",absParagrapId,result);
        return result;
    }

    /**
     * author:WuZepeng </br>
     * time:2018-05-14 13:26 </br>
     * desc:将从后台请求到的书签笔记的段落id转换成fbreader对应的段落Id </br>
     */
    public static int getLocalParagraphText(FBReaderApp app,int relativeParagraphIndex){
        if(relativeParagraphIndex < 0){
            Log.e("相对段落di不可小于0","");
            return -1;
        }
        final ZLTextView textView =  app.getTextView();
        if(textView == null){
            Log.e("textview === null","");
            return -2;
        }
        if(textView.getModel() == null){
            Log.e("textview.Model === null","");
            return -3;
        }
        int relativeCounter = 0;
        int result = 0;
        for(int index = 0;index<=textView.getModel().getParagraphsNumber()&&relativeCounter<=relativeParagraphIndex;index++){
            result++;
            ZLTextParagraph tmpParagraph = textView.getModel().getParagraph(index);
            if(tmpParagraph.getTag() == ZLTextParagraph.ParagraphTag.TAG_P){
                relativeCounter++;
            }
        }
        Log.e("本地段落id:"+(result-1)+" 相对段落id","");
        return result>0?result-1:0;
    }

    /**
     * <p>利用生成书签的方式获取当前显示文字最前面一段的段落内容.</p>
     * @param app
     * @return
     */
    @Deprecated
    @Nullable
	public static String getParagraphTextByBookmark(FBReaderApp app) {
		Bookmark bookMarker = app.addBookmark(80, false);
		String paragraphText = "";//获取当前的显示的内容
		if (bookMarker == null)
			return null;
		int paragraphIndex = bookMarker.getParagraphIndex();
		for (int i = paragraphIndex; i < paragraphIndex + 5; i++) {
			paragraphText = getParagraphTextByIndex(app, i, true);
			if (!paragraphText.equals("")) {
				break;
			}
		}
		app.Collection.deleteBookmark(bookMarker);
		Log.e("wzp 段落内容：",paragraphText);
		return paragraphText;
	}

    /**
     * 获取指定段落对应的内容
     * @param Reader
     * @param index
     * @param needCut
     * @return
     */
    public static String getParagraphTextByIndex(FBReaderApp Reader, int index, boolean needCut) {
        final StringBuilder sb = new StringBuilder();
        final ZLTextWordCursor cursor = new ZLTextWordCursor(Reader.getTextView().getStartCursor());
        FBView fbview = Reader.getTextView();
        if (fbview == null) {
            return "";
        }
        cursor.moveToParagraph(index);
        cursor.moveToParagraphStart();
        if (cursor.getParagraphCursor() == null) {
            return "";
        }
        while (!cursor.isEndOfParagraph()) {
            ZLTextElement element = cursor.getElement();
            if (element instanceof ZLTextWord) {
                sb.append(element.toString());
            }
            cursor.nextWord();
        }
        String result = sb.toString();
        if (needCut) {
            if (result.length() > 100) {
                result = result.substring(0, 100);
            }
        }
        return result.replaceAll("[ •]", "");
    }
}
