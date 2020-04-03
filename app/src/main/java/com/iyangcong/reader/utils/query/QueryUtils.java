package com.iyangcong.reader.utils.query;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.iyangcong.reader.app.AppContext;
import com.iyangcong.reader.bean.NewWord;
import com.iyangcong.reader.bean.SentenceTranslation;
import com.iyangcong.reader.bean.Word;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.utils.Constants;
import com.iyangcong.reader.utils.SharedPreferenceUtil;
import com.iyangcong.reader.utils.StringUtils;
import com.iyangcong.reader.utils.Urls;
import com.iyangcong.reader.utils.XMLParseTerrbileVersion;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ljw on 2017/4/10.
 */

public class QueryUtils {


    public static void queryfromYoudaoOnLineNewMethod(final String selectedText,final Handler handler,final boolean  isSearch){

        //http://openapi.youdao.com/api?q=good&from=EN&to=zh_CHS&appKey=ff889495-4b45-46d9-8f48-946554334f2a&salt=2&sign=1995882C5064805BC30A39829B779D7B
        if(StringUtils.checkChinese(selectedText)){
            Word word = new Word();
            word.setTranslation("当前不支持查询中文");
            Message msg = new Message();
            msg.obj = word;
            msg.what=3;
            handler.sendMessageDelayed(msg, 0);
        }else if((selectedText==null)||(selectedText.equals(""))){
            //不能查询空字符串
            Word word = new Word();
            word.setTranslation("无法查询当前内容呢~");
            Message msg = new Message();
            msg.obj = word;
            msg.what=2;
            handler.sendMessageDelayed(msg, 0);
        }else{
            String appKey = "1a838f21d870437d";
            String salt = String.valueOf(System.currentTimeMillis());
            String query = replaceBlank(selectedText);
            String sign = YoudaoUtils.md5(appKey + query + salt + "zzCiHIbY2PcgvykPkQlJPOO2K6fGnHE7");
            OkGo.get(isSearch == true?Urls.QUERY_RROM_YOUDAO:Urls.QUERY_RROM_YOUDAO_RUL_NEW)
                    .params("q", query)
                    .params("from", "EN")
                    .params("to", "zh_CHS")
                    .params("appKey", appKey)
                    .params("salt", salt)
                    .params("sign", sign)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            Word word = YoudaoUtils.getWord(s, SharedPreferenceUtil.getInstance().getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 0) == 1 ? 1 : 0);
                            if (word == null) {
                                word = queryFromLocal(selectedText);
                            } else if (word.getQueryErrorTip() != null) {
                                word = queryFromLocal(selectedText);
                            }
                            Message msg = new Message();
                            msg.obj = word;
                            handler.sendMessageDelayed(msg, 0);
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            Word word = queryFromLocal(selectedText);
                            Message msg = new Message();
                            msg.obj = word;
                            handler.sendMessageDelayed(msg, 0);
                        }
                    });
        }
    }

    public static void queryfromYoudaoOnLine(final String selectedText, final Handler handler) {
        OkGo.get(Urls.QUERY_FROM_YOUDAO_URL)
                .params("keyfrom", Constants.YOUDAO_KEYFROM)
                .params("key", Constants.YOUDAO_API_KEY)
                .params("type", "data")
                .params("doctype", "json")
                .params("version", "1.1")
                .params("q", replaceBlank(selectedText))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Word word = YoudaoUtils.getWord(s, SharedPreferenceUtil.getInstance().getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 0) == 1 ? 1 : 0);
                        word.setSelectedText(selectedText);
                        Message msg = new Message();
                        if(word.getWord()==null) {
                            word = queryFromLocal(selectedText);
                        }
                        msg.obj = word;
                        handler.sendMessageDelayed(msg, 0);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Word word = queryFromLocal(selectedText);
                        Message msg = new Message();
                        msg.obj = word;
                        handler.sendMessageDelayed(msg, 0);
                    }
                });
    }


    public static void queryBySentence(final String selectedText, final int chapterId, final int segmentId, final Handler handler, Context context/*, final boolean isDeleteSymbol*/) {
        if (StringUtils.isOneSentence(selectedText) && StringUtils.isOneParagraph(selectedText)) {
            OkGo.get(Urls.FBReaderSearchSentence)
                    .tag(context)
                    .params("bookid", SharedPreferenceUtil.getInstance().getLong(SharedPreferenceUtil.CURRENT_BOOK_ID, 0))
                    .params("chapterid", chapterId)
                    .params("keyword", selectedText)
                    .params("rows", "5")
                    .params("segmentid", segmentId)
                    .params("start", "0")
                    .execute(new JsonCallback<IycResponse<List<SentenceTranslation>>>(context) {
                        @Override
                        public void onSuccess(IycResponse<List<SentenceTranslation>> sentenceTranslationIycResponse, Call call, Response response) {
                            SentenceTranslation translationResult = sentenceTranslationIycResponse.getData().get(0);
                            if (translationResult.getSegment_en().equals("") && translationResult.getSegment_zh().equals("")) {
//                                if (isDeleteSymbol) {
                                translationResult.setSegment_zh("暂无译文");
                                translationResult.setSegment_en(selectedText);
//                                } else {
//                                    QueryUtils.queryfromYoudaoOnLine(StringUtils.deleSymbol(selectedText), handler);
//                                }
                            }
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = translationResult;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            Message msg = new Message();
                            SentenceTranslation translationResult = new SentenceTranslation();
                            if (SharedPreferenceUtil.getInstance().getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 0) == 1) {
                                translationResult.setSegment_zh(selectedText);
                                translationResult.setSegment_en("抱歉，未查到该句释义！");
                            } else {
                                translationResult.setSegment_en(selectedText);
                                translationResult.setSegment_zh("抱歉，未查到该句释义！");
                            }
                            msg.what = 1;
                            msg.obj = translationResult;
                            handler.sendMessage(msg);
                        }
                    });
        } else {
            Message msg = new Message();
            SentenceTranslation translationResult = new SentenceTranslation();
            if (SharedPreferenceUtil.getInstance().getInt(SharedPreferenceUtil.CURRENT_BOOK_LANGUAGE, 0) == 1) {
                translationResult.setSegment_zh("暂无译文");
                translationResult.setSegment_en("");
            } else {
                translationResult.setSegment_en("暂无译文");
                translationResult.setSegment_zh("");
            }
            msg.what = 1;
            msg.obj = translationResult;
            handler.sendMessage(msg);
        }

    }

    public static List<Word> queryFromLocalList(String selectedText){
        return getListWord(selectedText);
    }

    public static Word queryFromLocal(String selectedText) {
        return getWordTranslates(selectedText);
    }

    public static List<Word> getListWord(String str) {
        List<Word> words = new ArrayList<Word>();
        int strLen = str.length();
        String first = null;
        String second = null;
        String xmlName = null;
        if (strLen == 1) {
            first = str.substring(0, 1);
            if (first.equalsIgnoreCase("A")) {
                xmlName = "AA";
            } else if (first.equalsIgnoreCase("B")) {
                xmlName = "BA";
            } else if (first.equalsIgnoreCase("C")) {
                xmlName = "CA";
            } else if (first.equalsIgnoreCase("D")) {
                xmlName = "DA";
            } else if (first.equalsIgnoreCase("E")) {
                xmlName = "EA";
            } else if (first.equalsIgnoreCase("F")) {
                xmlName = "FA";
            } else if (first.equalsIgnoreCase("G")) {
                xmlName = "GA";
            } else if (first.equalsIgnoreCase("H")) {
                xmlName = "HA";
            } else if (first.equalsIgnoreCase("I")) {
                xmlName = "IA";
            } else if (first.equalsIgnoreCase("J")) {
                xmlName = "JA";
            } else if (first.equalsIgnoreCase("K")) {
                xmlName = "KA";
            } else if (first.equalsIgnoreCase("L")) {
                xmlName = "LA";
            } else if (first.equalsIgnoreCase("M")) {
                xmlName = "MA";
            } else if (first.equalsIgnoreCase("N")) {
                xmlName = "NA";
            } else if (first.equalsIgnoreCase("O")) {
                xmlName = "OA";
            } else if (first.equalsIgnoreCase("P")) {
                xmlName = "PA";
            } else if (first.equalsIgnoreCase("Q")) {
                xmlName = "QA";
            } else if (first.equalsIgnoreCase("R")) {
                xmlName = "RA";
            } else if (first.equalsIgnoreCase("S")) {
                xmlName = "SA";
            } else if (first.equalsIgnoreCase("T")) {
                xmlName = "TA";
            } else if (first.equalsIgnoreCase("U")) {
                xmlName = "UA";
            } else if (first.equalsIgnoreCase("V")) {
                xmlName = "VA";
            } else if (first.equalsIgnoreCase("W")) {
                xmlName = "WA";
            } else if (first.equalsIgnoreCase("X")) {
                xmlName = "XA";
            } else if (first.equalsIgnoreCase("Y")) {
                xmlName = "YA";
            } else if (first.equalsIgnoreCase("Z")) {
                xmlName = "ZA";
            }

        } else if (strLen >= 2) {
            first = str.substring(0, 1);
            second = str.substring(1, 2);
            if (first.equalsIgnoreCase("A")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'J') {
                    xmlName = "AA";
                } else if (secondChar >= 'J' && secondChar < 'P') {
                    xmlName = "AJ";
                } else if (secondChar >= 'P') {
                    xmlName = "AP";
                }
            } else if (first.equalsIgnoreCase("B")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'B') {
                    xmlName = "BA";
                } else if (secondChar >= 'B' && secondChar < 'I') {
                    xmlName = "BB";
                } else if (secondChar >= 'I' && secondChar < 'O') {
                    xmlName = "BI";
                } else if (secondChar >= 'O' && secondChar < 'R') {
                    xmlName = "BO";
                } else if (secondChar >= 'R') {
                    xmlName = "BR";
                }
            } else if (first.equalsIgnoreCase("C")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'B') {
                    xmlName = "CA";
                } else if (secondChar >= 'B' && secondChar < 'I') {
                    xmlName = "CB";
                } else if (secondChar >= 'I' && secondChar < 'O') {
                    xmlName = "CI";
                } else if (secondChar >= 'O' && secondChar < 'P') {
                    xmlName = "CO";
                } else if (secondChar >= 'P') {
                    xmlName = "CP";
                }
            } else if (first.equalsIgnoreCase("D")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'E') {
                    xmlName = "DA";
                } else if (secondChar >= 'E' && secondChar < 'I') {
                    xmlName = "DE";
                } else if (secondChar >= 'I' && secondChar < 'J') {
                    xmlName = "DI";
                } else if (secondChar >= 'J' && secondChar < 'P') {
                    xmlName = "DJ";
                } else if (secondChar >= 'P') {
                    xmlName = "DP";
                }
            } else if (first.equalsIgnoreCase("E")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'N') {
                    xmlName = "EA";
                } else if (secondChar >= 'N') {
                    xmlName = "EN";
                }
            } else if (first.equalsIgnoreCase("F")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'L') {
                    xmlName = "FA";
                } else if (secondChar >= 'L') {
                    xmlName = "FL";
                }
            } else if (first.equalsIgnoreCase("G")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'M') {
                    xmlName = "GA";
                } else if (secondChar >= 'M') {
                    xmlName = "GM";
                }
            } else if (first.equalsIgnoreCase("H")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'F') {
                    xmlName = "HA";
                } else if (secondChar >= 'F') {
                    xmlName = "HF";
                }
            } else if (first.equalsIgnoreCase("I")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'N') {
                    xmlName = "IA";
                } else if (secondChar >= 'N') {
                    xmlName = "IN";
                }
            } else if (first.equalsIgnoreCase("J")) {
                xmlName = "JA";
            } else if (first.equalsIgnoreCase("K")) {
                xmlName = "KA";
            } else if (first.equalsIgnoreCase("L")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'I') {
                    xmlName = "LA";
                } else if (secondChar >= 'I') {
                    xmlName = "LI";
                }
            } else if (first.equalsIgnoreCase("M")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'B') {
                    xmlName = "MA";
                } else if (secondChar >= 'B' && secondChar < 'K') {
                    xmlName = "MB";
                } else if (secondChar >= 'K') {
                    xmlName = "MK";
                }
            } else if (first.equalsIgnoreCase("N")) {
                xmlName = "NA";
            } else if (first.equalsIgnoreCase("O")) {
                xmlName = "OA";
            } else if (first.equalsIgnoreCase("P")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'E') {
                    xmlName = "PA";
                } else if (secondChar >= 'E' && secondChar < 'G') {
                    xmlName = "PE";
                } else if (secondChar >= 'G' && secondChar < 'P') {
                    xmlName = "PG";
                } else if (secondChar >= 'P' && secondChar < 'R') {
                    xmlName = "PP";
                } else if (secondChar >= 'R' && secondChar < 'Y') {
                    xmlName = "PR";
                } else if (secondChar >= 'Y') {
                    xmlName = "PY";
                }
            } else if (first.equalsIgnoreCase("Q")) {
                xmlName = "QA";
            } else if (first.equalsIgnoreCase("R")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'E') {
                    xmlName = "RA";
                } else if (secondChar >= 'E' && secondChar < 'H') {
                    xmlName = "RE";
                } else if (secondChar >= 'H' && secondChar < 'Y') {
                    xmlName = "RH";
                } else if (secondChar >= 'Y') {
                    xmlName = "RY";
                }
            } else if (first.equalsIgnoreCase("S")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'E') {
                    xmlName = "SA";
                } else if (secondChar >= 'E' && secondChar < 'G') {
                    xmlName = "SE";
                } else if (secondChar >= 'G' && secondChar < 'O') {
                    xmlName = "SG";
                } else if (secondChar >= 'O' && secondChar < 'P') {
                    xmlName = "SO";
                } else if (secondChar >= 'P' && secondChar < 'T') {
                    xmlName = "SP";
                } else if (secondChar >= 'T' && secondChar < 'U') {
                    xmlName = "ST";
                } else if (secondChar >= 'U' && secondChar < 'Y') {
                    xmlName = "SU";
                } else if (secondChar >= 'Y') {
                    xmlName = "SY";
                }
            } else if (first.equalsIgnoreCase("T")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'H') {
                    xmlName = "TA";
                } else if (secondChar >= 'H' && secondChar < 'I') {
                    xmlName = "TH";
                } else if (secondChar >= 'I' && secondChar < 'Z') {
                    xmlName = "TI";
                } else if (secondChar >= 'Z') {
                    xmlName = "TZ";
                }
            } else if (first.equalsIgnoreCase("U")) {
                xmlName = "UA";
            } else if (first.equalsIgnoreCase("V")) {
                xmlName = "VA";
            } else if (first.equalsIgnoreCase("W")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'E') {
                    xmlName = "WA";
                } else if (secondChar >= 'E' && secondChar < 'H') {
                    xmlName = "WE";
                } else if (secondChar >= 'H' && secondChar < 'I') {
                    xmlName = "WH";
                } else if (secondChar >= 'I' && secondChar < 'K') {
                    xmlName = "WI";
                } else if (secondChar >= 'K' && secondChar < 'Y') {
                    xmlName = "WK";
                } else if (secondChar >= 'Y') {
                    xmlName = "WY";
                }
            } else if (first.equalsIgnoreCase("X")) {
                xmlName = "XA";
            } else if (first.equalsIgnoreCase("Y")) {
                xmlName = "YA";
            } else if (first.equalsIgnoreCase("Z")) {
                xmlName = "ZA";
            }
        }
        try {

            long begin = System.currentTimeMillis();
            //String filepath="/edu/english/study/library/" + xmlName + ".xml";
            String filepath = "library/" + xmlName + ".xml";
            //String filepath="library/12D.xml";
            //InputStream stream=AppContext.getInstance().getClass().getResourceAsStream(filepath);
            InputStream stream = AppContext.getInstance().getResources().getAssets().open(filepath);
            long load = System.currentTimeMillis();
            words = readXML(stream);
            long end = System.currentTimeMillis();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return words;
    }

    public static List<Word> readXML(InputStream inStream) {
        try {
            // 创建解析器
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser saxParser = spf.newSAXParser();
            // 设置解析器的相关特性，true表示开启命名空间特性
            // saxParser.setProperty("http://xml.org/sax/features/namespaces",true);
//            XMLParse handler = new XMLParse();
            XMLParseTerrbileVersion handler = new XMLParseTerrbileVersion();
            saxParser.parse(inStream, handler);
            inStream.close();
            return handler.getWords();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Word getWordTranslates(String str) {

        List<Word> words = new ArrayList<Word>();
        int strLen = str.length();
        String first = null;
        String second = null;
        String xmlName = null;
        if (strLen == 1) {
            first = str.substring(0, 1);
            if (first.equalsIgnoreCase("A")) {
                xmlName = "AA";
            } else if (first.equalsIgnoreCase("B")) {
                xmlName = "BA";
            } else if (first.equalsIgnoreCase("C")) {
                xmlName = "CA";
            } else if (first.equalsIgnoreCase("D")) {
                xmlName = "DA";
            } else if (first.equalsIgnoreCase("E")) {
                xmlName = "EA";
            } else if (first.equalsIgnoreCase("F")) {
                xmlName = "FA";
            } else if (first.equalsIgnoreCase("G")) {
                xmlName = "GA";
            } else if (first.equalsIgnoreCase("H")) {
                xmlName = "HA";
            } else if (first.equalsIgnoreCase("I")) {
                xmlName = "IA";
            } else if (first.equalsIgnoreCase("J")) {
                xmlName = "JA";
            } else if (first.equalsIgnoreCase("K")) {
                xmlName = "KA";
            } else if (first.equalsIgnoreCase("L")) {
                xmlName = "LA";
            } else if (first.equalsIgnoreCase("M")) {
                xmlName = "MA";
            } else if (first.equalsIgnoreCase("N")) {
                xmlName = "NA";
            } else if (first.equalsIgnoreCase("O")) {
                xmlName = "OA";
            } else if (first.equalsIgnoreCase("P")) {
                xmlName = "PA";
            } else if (first.equalsIgnoreCase("Q")) {
                xmlName = "QA";
            } else if (first.equalsIgnoreCase("R")) {
                xmlName = "RA";
            } else if (first.equalsIgnoreCase("S")) {
                xmlName = "SA";
            } else if (first.equalsIgnoreCase("T")) {
                xmlName = "TA";
            } else if (first.equalsIgnoreCase("U")) {
                xmlName = "UA";
            } else if (first.equalsIgnoreCase("V")) {
                xmlName = "VA";
            } else if (first.equalsIgnoreCase("W")) {
                xmlName = "WA";
            } else if (first.equalsIgnoreCase("X")) {
                xmlName = "XA";
            } else if (first.equalsIgnoreCase("Y")) {
                xmlName = "YA";
            } else if (first.equalsIgnoreCase("Z")) {
                xmlName = "ZA";
            }

        } else if (strLen >= 2) {
            first = str.substring(0, 1);
            second = str.substring(1, 2);
            if (first.equalsIgnoreCase("A")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'J') {
                    xmlName = "AA";
                } else if (secondChar >= 'J' && secondChar < 'P') {
                    xmlName = "AJ";
                } else if (secondChar >= 'P') {
                    xmlName = "AP";
                }
            } else if (first.equalsIgnoreCase("B")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'B') {
                    xmlName = "BA";
                } else if (secondChar >= 'B' && secondChar < 'I') {
                    xmlName = "BB";
                } else if (secondChar >= 'I' && secondChar < 'O') {
                    xmlName = "BI";
                } else if (secondChar >= 'O' && secondChar < 'R') {
                    xmlName = "BO";
                } else if (secondChar >= 'R') {
                    xmlName = "BR";
                }
            } else if (first.equalsIgnoreCase("C")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'B') {
                    xmlName = "CA";
                } else if (secondChar >= 'B' && secondChar < 'I') {
                    xmlName = "CB";
                } else if (secondChar >= 'I' && secondChar < 'O') {
                    xmlName = "CI";
                } else if (secondChar >= 'O' && secondChar < 'P') {
                    xmlName = "CO";
                } else if (secondChar >= 'P') {
                    xmlName = "CP";
                }
            } else if (first.equalsIgnoreCase("D")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'E') {
                    xmlName = "DA";
                } else if (secondChar >= 'E' && secondChar < 'I') {
                    xmlName = "DE";
                } else if (secondChar >= 'I' && secondChar < 'J') {
                    xmlName = "DI";
                } else if (secondChar >= 'J' && secondChar < 'P') {
                    xmlName = "DJ";
                } else if (secondChar >= 'P') {
                    xmlName = "DP";
                }
            } else if (first.equalsIgnoreCase("E")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'N') {
                    xmlName = "EA";
                } else if (secondChar >= 'N') {
                    xmlName = "EN";
                }
            } else if (first.equalsIgnoreCase("F")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'L') {
                    xmlName = "FA";
                } else if (secondChar >= 'L') {
                    xmlName = "FL";
                }
            } else if (first.equalsIgnoreCase("G")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'M') {
                    xmlName = "GA";
                } else if (secondChar >= 'M') {
                    xmlName = "GM";
                }
            } else if (first.equalsIgnoreCase("H")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'F') {
                    xmlName = "HA";
                } else if (secondChar >= 'F') {
                    xmlName = "HF";
                }
            } else if (first.equalsIgnoreCase("I")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'N') {
                    xmlName = "IA";
                } else if (secondChar >= 'N') {
                    xmlName = "IN";
                }
            } else if (first.equalsIgnoreCase("J")) {
                xmlName = "JA";
            } else if (first.equalsIgnoreCase("K")) {
                xmlName = "KA";
            } else if (first.equalsIgnoreCase("L")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'I') {
                    xmlName = "LA";
                } else if (secondChar >= 'I') {
                    xmlName = "LI";
                }
            } else if (first.equalsIgnoreCase("M")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'B') {
                    xmlName = "MA";
                } else if (secondChar >= 'B' && secondChar < 'K') {
                    xmlName = "MB";
                } else if (secondChar >= 'K') {
                    xmlName = "MK";
                }
            } else if (first.equalsIgnoreCase("N")) {
                xmlName = "NA";
            } else if (first.equalsIgnoreCase("O")) {
                xmlName = "OA";
            } else if (first.equalsIgnoreCase("P")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'E') {
                    xmlName = "PA";
                } else if (secondChar >= 'E' && secondChar < 'G') {
                    xmlName = "PE";
                } else if (secondChar >= 'G' && secondChar < 'P') {
                    xmlName = "PG";
                } else if (secondChar >= 'P' && secondChar < 'R') {
                    xmlName = "PP";
                } else if (secondChar >= 'R' && secondChar < 'Y') {
                    xmlName = "PR";
                } else if (secondChar >= 'Y') {
                    xmlName = "PY";
                }
            } else if (first.equalsIgnoreCase("Q")) {
                xmlName = "QA";
            } else if (first.equalsIgnoreCase("R")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'E') {
                    xmlName = "RA";
                } else if (secondChar >= 'E' && secondChar < 'H') {
                    xmlName = "RE";
                } else if (secondChar >= 'H' && secondChar < 'Y') {
                    xmlName = "RH";
                } else if (secondChar >= 'Y') {
                    xmlName = "RY";
                }
            } else if (first.equalsIgnoreCase("S")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'E') {
                    xmlName = "SA";
                } else if (secondChar >= 'E' && secondChar < 'G') {
                    xmlName = "SE";
                } else if (secondChar >= 'G' && secondChar < 'O') {
                    xmlName = "SG";
                } else if (secondChar >= 'O' && secondChar < 'P') {
                    xmlName = "SO";
                } else if (secondChar >= 'P' && secondChar < 'T') {
                    xmlName = "SP";
                } else if (secondChar >= 'T' && secondChar < 'U') {
                    xmlName = "ST";
                } else if (secondChar >= 'U' && secondChar < 'Y') {
                    xmlName = "SU";
                } else if (secondChar >= 'Y') {
                    xmlName = "SY";
                }
            } else if (first.equalsIgnoreCase("T")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'H') {
                    xmlName = "TA";
                } else if (secondChar >= 'H' && secondChar < 'I') {
                    xmlName = "TH";
                } else if (secondChar >= 'I' && secondChar < 'Z') {
                    xmlName = "TI";
                } else if (secondChar >= 'Z') {
                    xmlName = "TZ";
                }
            } else if (first.equalsIgnoreCase("U")) {
                xmlName = "UA";
            } else if (first.equalsIgnoreCase("V")) {
                xmlName = "VA";
            } else if (first.equalsIgnoreCase("W")) {
                second = second.toUpperCase();
                char secondChar = second.charAt(0);
                if (secondChar >= 'A' && secondChar < 'E') {
                    xmlName = "WA";
                } else if (secondChar >= 'E' && secondChar < 'H') {
                    xmlName = "WE";
                } else if (secondChar >= 'H' && secondChar < 'I') {
                    xmlName = "WH";
                } else if (secondChar >= 'I' && secondChar < 'K') {
                    xmlName = "WI";
                } else if (secondChar >= 'K' && secondChar < 'Y') {
                    xmlName = "WK";
                } else if (secondChar >= 'Y') {
                    xmlName = "WY";
                }
            } else if (first.equalsIgnoreCase("X")) {
                xmlName = "XA";
            } else if (first.equalsIgnoreCase("Y")) {
                xmlName = "YA";
            } else if (first.equalsIgnoreCase("Z")) {
                xmlName = "ZA";
            }
        }
        try {

            long begin = System.currentTimeMillis();
            //String filepath="/edu/english/study/library/" + xmlName + ".xml";
            String filepath = "library/" + xmlName + ".xml";
            //String filepath="library/12D.xml";
            //InputStream stream=AppContext.getInstance().getClass().getResourceAsStream(filepath);
            InputStream stream = AppContext.getInstance().getResources().getAssets().open(filepath);
            long load = System.currentTimeMillis();
            words = readXML(stream);
            long end = System.currentTimeMillis();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        /*
         * 得到的单词是可能是多项，所以得合并
		 */
        boolean have = false;
        List<Word> wordlist = new ArrayList<Word>();
        for (Word w : words) {
            String value = w.getWord().get(0);
            if (value.equalsIgnoreCase(str)||(!wordlist.isEmpty()&&wordlist.get(0).getSynonyms().equals(value))) {
                have = true;
                wordlist.add(w);
                //return w;
                //return w;
                //后期修改
            }
        }
        int listsize = wordlist.size();
        if (listsize > 0) {
            Word word = parcelToEntiry(wordlist);
            return word;
        }

        if (!have) {
            InputStream stream;
            Map<String, String> verbMap = new HashMap<String, String>();
            try {
                long begin = System.currentTimeMillis();

                //String filepath="/edu/english/study/library/VERB.txt";
                //InputStream stream1=AppContext.getInstance().getClass().getResourceAsStream(filepath);
                //String filepath="/edu/english/study/library/" + xmlName + ".xml";
                String filepath = "library/VERB.txt";
                //InputStream stream=AppContext.getInstance().getClass().getResourceAsStream(filepath);
                stream = AppContext.getInstance().getResources().getAssets().open(filepath);
                InputStreamReader reader = new InputStreamReader(stream);
                BufferedReader inTxt = new BufferedReader(reader);
                String strTmp;
                String arrTmp[];
                while ((strTmp = inTxt.readLine()) != null) {
                    arrTmp = strTmp.split(" ");
                    verbMap.put(arrTmp[0], arrTmp[1]);
                }
                long end = System.currentTimeMillis();
                long distance = (end - begin);
                if (verbMap.size() > 0) {
                    String convertStr = verbMap.get(str);
                    String mFirst = null;
                    if (convertStr != null) {
                        mFirst = convertStr.substring(0, 1);
                    }
                    if (mFirst != null) {
                        if (mFirst.equals(first)) {
                            for (Word w : words) {
                                String value = w.getWord().get(0);
                                if (value.equalsIgnoreCase(convertStr)) {
                                    have = true;
                                    wordlist.add(w);
                                    //return w;
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!have) {
            String lastChar = str.substring(strLen - 1);
            String lastSecondChar = null;
            if (lastChar.equalsIgnoreCase("S")) {
                String tempStr = str.substring(0, strLen - 1);
                for (Word w : words) {
                    String value = w.getWord().get(0);
                    if (value.equalsIgnoreCase(tempStr)) {
                        have = true;
                        wordlist.add(w);
                        //return w;
                    }
                }
            }
            if (!have) {
                if (strLen >= 2) {
                    lastSecondChar = str.substring(strLen - 2, strLen);
                    if (lastSecondChar.equalsIgnoreCase("ED")) {
                        String tempStr = str.substring(0, strLen - 2);
                        for (Word w : words) {
                            String value = w.getWord().get(0);
                            if (value.equalsIgnoreCase(tempStr)) {
                                have = true;
                                wordlist.add(w);
                                //return w;
                            }
                        }
                    }
                    if (!have) {
                        if (lastSecondChar.equalsIgnoreCase("LY")) {
                            String tempStr = str.substring(0, strLen - 2);
                            for (Word w : words) {
                                String value = w.getWord().get(0);
                                if (value.equalsIgnoreCase(tempStr)) {
                                    have = true;
                                    wordlist.add(w);
                                    //return w;
                                }
                            }
                        }
                    }
                }
            }

            if (!have) {
                if (strLen >= 3) {
                    lastSecondChar = str.substring(strLen - 3, strLen);
                    if (lastSecondChar.equalsIgnoreCase("ING")) {
                        String tempStr = str.substring(0, strLen - 3);
                        for (Word w : words) {
                            String value = w.getWord().get(0);
                            if (value.equalsIgnoreCase(tempStr)) {
                                have = true;
                                wordlist.add(w);
                                //return w;
                            }
                        }
                        if (!have) {
                            tempStr += "e";
                            for (Word w : words) {
                                String value = w.getWord().get(0);
                                if (value.equalsIgnoreCase(tempStr)) {
                                    have = true;
                                    wordlist.add(w);
                                    //return w;
                                }
                            }
                        }
                    }
                }
            }
        }


        int listsize2 = wordlist.size();//此处不能省略，此处是在前面没找到结果的情况下再去查一遍
        if (listsize2 > 0) {
            Word word = parcelToEntiry(wordlist);
            return word;
        }
        return null;
    }

    private static Word parcelToEntiry(List<Word> list) {

        Word rword = null;
        int listsize = list.size();
        if (listsize == 1) {
            Word tempW = list.get(0);
            List<String> explains = new ArrayList<String>();
            if (tempW.getWord() != null) {
                rword = new Word();
                rword.setWord(tempW.getWord());
            }
            if (tempW.getCategory() != null) {
                Map<String, List<String>> tempCategory = tempW.getCategory();
                Iterator entries = tempCategory.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry entry = (Map.Entry) entries.next();
                    String key = (String) entry.getKey();
                    List<String> value = (List<String>) entry.getValue();
                    String va = "";
                    for (int n = 0; n < value.size(); n++) {
                        va += value.get(n);
                    }
                    String cxp = "";
                    if ("_TEMP".equals(key)) {
                        cxp = "  " + va;
                    } else {
                        cxp = key + ".  " + va;
                    }
                    explains.add(cxp);
                }
            }
            if (tempW.getExplains() != null) {
                if (rword != null) {
                    rword.setExplains(tempW.getExplains());
                }
            }
            if (tempW.getPhonetic() != null) {
                if (rword != null) {
                    rword.setPhonetic(tempW.getPhonetic());
                }

            }
            if (tempW.getVariant() != null) {
                if (rword != null) {
                    explains.add(tempW.getVariant());
                }
            }
            if (rword != null&&!(explains.size()==1&&explains.get(0).trim().equals(""))) {
                rword.setExplains(explains);
            }
            if(rword!=null&& !TextUtils.isEmpty(tempW.getTranslation())){
                rword.setTranslation(tempW.getTranslation());
            }
        } else {
            List<String> explains = new ArrayList<String>();
            List<String> namelist = new ArrayList<String>();
            for (int m = 0; m < listsize; m++) {
                Word word = list.get(m);
                List<String> tempnamelist = word.getWord();
                if (tempnamelist.size() > 0) {
                    rword = new Word();
                    namelist.add(word.getWord().get(0));
                }
                if (m == listsize - 1) {
                    if (rword != null) {
                        rword.setWord(namelist);
                    }

                }
                if (word.getVariant() != null) {
                    if (rword != null) {
                        explains.add(word.getVariant());
                    }

                }
                if (word.getPhonetic() != null) {
                    if (rword != null) {
                        rword.setPhonetic(word.getPhonetic());
                    }

                }
                if(rword!=null&& !TextUtils.isEmpty(word.getTranslation())){
                    rword.setTranslation(word.getTranslation());
                }
                Map<String, List<String>> tempCategory = list.get(m).getCategory();
                Iterator entries = tempCategory.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry entry = (Map.Entry) entries.next();
                    String key = (String) entry.getKey();
                    List<String> value = (List<String>) entry.getValue();
                    String va = "";
                    for (int n = 0; n < value.size(); n++) {
                        va += value.get(n);
                    }
                    String cxp = "";
                    if ("_TEMP".equals(key)) {
                        cxp = "" + va;
                    } else {
                        cxp = key + ".  " + va;
                    }
                    explains.add(cxp);
                }

            }
            if (rword != null&&!(explains.size()==1&&explains.get(0).trim().equals(""))) {
                rword.setExplains(explains);
            }
        }
        return rword;
    }

    public static String replaceBlank(String str) {
        String dest = "";
        String res = "";
        if (str != null && !"".equals(str)) {
            // Pattern p = Pattern.compile("\\r|\n");
            Pattern p = Pattern.compile("|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        dest = dest.trim();
        char[] chars = dest.toCharArray();
        for (char c : chars) {
            if (c == ' ') {
                res += " ";
            } else if (c == '\n') {
                continue;
            } else if (c == ':') {
                continue;
            } else {
                res += String.valueOf(c);
            }
        }
        return res;
    }

    public static String setWordAllLevel(NewWord word,String levelString){
        if(levelString == null){
            levelString = "";
        }
        word.setTEM8(levelString.contains("0")? 1:0);
        word.setTEM4(levelString.contains("1")? 1 : 0);
        word.setSAT(levelString.contains("3")? 1 : 0);
        word.setCET4(levelString.contains("4")? 1 : 0);
        word.setCET6(levelString.contains("6")? 1 : 0);
        word.setTOEFL(levelString.contains("7")? 1 : 0);
        word.setIELTS(levelString.contains("8")? 1 : 0);
        word.setGRE(levelString.contains("9")? 1 : 0);
        return word.getCET4()+"_"+word.getCET6()+"_"+word.getTEM4()+"_"+word.getTEM8()+"_"+
        word.getSAT()+"_"+word.getTOEFL()+"_"+word.getIELTS()+"_"+word.getGRE();
    }

    public static String getLevelNumsFromWord(String word) {
        if (word != null && word.length() > 0) {
            char jsonName = word.charAt(0);
            word = word.toLowerCase();
            if (jsonName >= 'a' && jsonName <= 'z' || jsonName >= 'A' && jsonName <= 'Z') {
                try {
                    String filepath = "wordlevel/" + jsonName + ".json";
                    InputStream stream = AppContext.getInstance().getResources().getAssets().open(filepath);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer stringBuffer = new StringBuffer();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        stringBuffer.append(line);
                    }
                    JSONObject jsonObject = new JSONObject(stringBuffer.toString());
                    String result = jsonObject.getString(word);
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    public static String getLevelsStrFromLevelNums(String levelNums,int where) {
        if (levelNums == null) {
            return null;
        }
        String[] strs = levelNums.split(",");
        StringBuffer sb = new StringBuffer();
        for (String str : strs) {
            if (where == 1){
                sb.append(getLevelStrFromLevelNum(str)).append("   ");
            }else if (where == 2){
                if (str.equals("2")){
                    continue;
                }
                sb.append(getLevelStrFromLevelNum2(str)).append("/");
            }

        }
        if (sb.length() == 0){
            return null;
        }else {
            return sb.substring(0, sb.length() - 1);
        }

    }

    public static String getLevelStrFromLevelNum(String num) {
        switch (num) {
            case "0":
                return "专八";
            case "1":
                return "专四";
            case "3":
                return "SAT";
            case "4":
                return "四级";
            case "5":
                return "考研";
            case "6":
                return "六级";
            case "7":
                return "托福";
            case "8":
                return "雅思";
            case "9":
                return "GRE";
            default:
                break;
        }
        return "";
    }
    public static String getLevelStrFromLevelNum2(String num) {
        switch (num) {
            case "0":
                return "TEM8";
            case "1":
                return "TEM4";
            case "3":
                return "SAT";
            case "4":
                return "CET4";
            case "5":
                return "考研";
            case "6":
                return "CET6";
            case "7":
                return "TOFFL";
            case "8":
                return "SAT";
            case "9":
                return "GRE";
            default:
                break;
        }
        return "";
    }
}
