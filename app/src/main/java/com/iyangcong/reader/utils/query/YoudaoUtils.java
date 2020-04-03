package com.iyangcong.reader.utils.query;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.iyangcong.reader.bean.SearchBook;
import com.iyangcong.reader.bean.Word;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class YoudaoUtils {

    public static Word getWord(String string, int languageType) {
        Word word = null;
        try {
            JSONObject retObject = new JSONObject(string);
            int errorCode = retObject.getInt("errorCode");
            if (errorCode == 0) {
                word = new Word();
                JSONObject basicObject = null;
                if (languageType == 0) {
                    if (retObject.has("query")) {
                        List<String> wordvalue = new ArrayList<String>();
                        wordvalue.add(retObject.getString("query"));
                        word.setWord(wordvalue);
                    }
                    if (retObject.has("translation")) {
                        JSONArray transArray = retObject.getJSONArray("translation");
                        for (int i = 0; i < transArray.length(); i++) {
                            word.setTranslation(transArray.getString(0));
                        }
                    }
                    if (retObject.has("basic")) {
                        basicObject = retObject.getJSONObject("basic");
                        if (basicObject.has("us-phonetic")) {
                            String us_phonetic = basicObject.getString("us-phonetic");
                            word.setUs_phonetic(us_phonetic);
                        }
                        if (basicObject.has("books")) {
                            List<SearchBook> searchBookList = new ArrayList<>();
                            JSONArray transArray = basicObject.getJSONArray("books");
                            for (int i = 0; i < transArray.length(); i++) {
                                JSONObject jsonObject = new JSONObject(transArray.getString(0));
                                SearchBook searchBook = new SearchBook();
                                String bookName = jsonObject.getString("bookName");
                                String bookCover = jsonObject.getString("bookCover");
                                String bookId = jsonObject.getString("bookId");
                                String bookSource = jsonObject.getString("bookSource");
                                searchBook.setBookCover(bookCover);
                                searchBook.setBookId(bookId);
                                searchBook.setBookName(bookName);
                                searchBook.setBookSource(bookSource);
                                searchBookList.add(searchBook);
                            }
                            word.setBooks(searchBookList);
                        }
                        if (basicObject.has("variant")) {
                            JSONArray variantArray = basicObject.getJSONArray("variant");
                            List<String> variants = new ArrayList<String>();
                            for (int i = 0; i < variantArray.length(); i++) {
                                String str = variantArray.getString(i);
                                variants.add(str);
                            }
                            word.setVarients(variants);
                        }
                        if (basicObject.has("phonetic")) {
                            String phonetic = basicObject.getString("phonetic");
                            word.setPhonetic(phonetic);
                        }
                        if (basicObject.has("uk-phonetic")) {
                            String uk_phonetic = basicObject.getString("uk-phonetic");
                            word.setUk_phonetic(uk_phonetic);
                        }
                        if (basicObject.has("explains")) {
                            JSONArray booksArray = basicObject.getJSONArray("explains");
                            List<String> explains = new ArrayList<String>();
                            for (int i = 0; i < booksArray.length(); i++) {
                                String str = booksArray.getString(i);
                                explains.add(str);
                            }
                            word.setExplains(explains);
                        }
                    }
                    if (retObject.has("web")) {
                        JSONArray transArray = retObject.getJSONArray("web");
                        HashMap<String, String> webExplains = new HashMap<>();
                        for (int i = 0; i < transArray.length(); i++) {
                            JSONObject jsonObject = new JSONObject(transArray.getString(0));
                            JSONArray values = jsonObject.getJSONArray("value");
                            String key = jsonObject.getString("key");
                            StringBuilder value = new StringBuilder("");
                            if(values.length() == 0) {

                            }else if(values.length() == 1)
                                value.append(values.getString(0));
                            else {
                                value.append(values.getString(0));
                                for (int j = 1; j < values.length(); j++) {
                                    value.append("，"+values.getString(j));
                                }
                            }
                            webExplains.put(key, value.toString());
                        }
                        word.setWebExplains(webExplains);
                    }
                } else if (languageType == 1) {
                    if (retObject.has("query")) {
                        List<String> wordvalue = new ArrayList<String>();
                        wordvalue.add(retObject.getString("query"));
                        word.setWord(wordvalue);
                    }
                    if (retObject.has("translation")) {
                        JSONArray transArray = retObject.getJSONArray("translation");
                        for (int i = 0; i < transArray.length(); i++) {
                            word.setTranslation(transArray.getString(0));
                        }
                    }
                    if (retObject.has("basic")) {
                        basicObject = retObject.getJSONObject("basic");
                        if (basicObject.has("us-phonetic")) {
                            String us_phonetic = basicObject.getString("us-phonetic");
                            word.setUs_phonetic(us_phonetic);
                        }
                        if (basicObject.has("phonetic")) {
                            String phonetic = basicObject.getString("phonetic");
                            word.setPhonetic(phonetic);
                        }
                        if (basicObject.has("uk-phonetic")) {
                            String uk_phonetic = basicObject.getString("uk-phonetic");
                            word.setUk_phonetic(uk_phonetic);
                        }
                        if (basicObject.has("explains")) {
                            JSONArray booksArray = basicObject.getJSONArray("explains");
                            List<String> explains = new ArrayList<String>();
                            for (int i = 0; i < booksArray.length(); i++) {
                                String str = booksArray.getString(i);
                                explains.add(str);
                            }
                            word.setExplains(explains);
                        }
                    }
                    if (retObject.has("web")) {
                        JSONArray transArray = retObject.getJSONArray("web");
                        HashMap<String, String> webExplains = new HashMap<>();
                        for (int i = 0; i < transArray.length(); i++) {
                            JSONObject jsonObject = new JSONObject(transArray.getString(0));
                            JSONArray values = jsonObject.getJSONArray("value");
                            String key = jsonObject.getString("key");
                            String value = "";
                            for (int j = 0; j < values.length(); j++) {
                                value += values.getString(j) + " ";
                            }
                            webExplains.put(key, value);
                        }
                        word.setWebExplains(webExplains);
                    }
                }
            } else if (errorCode == 20) {
                //要翻译的文本过长
                //buffer.append("要翻译的文本过长");
                word = new Word();
                word.setQueryErrorTip("要翻译的文本过长");
            } else if (errorCode == 30) {
                //无法进行有效的翻译
                //buffer.append("无法进行有效的翻译");
                word = new Word();
                word.setQueryErrorTip("无法进行有效的翻译");
            } else if (errorCode == 40) {
                //不支持的语言类型
                //buffer.append("暂不支持此类语言的翻译");
                word = new Word();
                word.setQueryErrorTip("暂不支持此类语言的翻译");
            } else if (errorCode == 50) {
                //无效的key
                //buffer.append("无效的key");
                word = new Word();
                word.setQueryErrorTip("无法进行有效的翻译");
            } else if (errorCode == 60) {
                //无词典结果，仅在获取词典结果生效
                //buffer.append("无词典结果");
                word = new Word();
                word.setQueryErrorTip("无词典结果");
            }
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            word = new Word();
            word.setQueryErrorTip("暂无释义");
            return word;
        }
        return word;
    }

    public static Word getWordMethod2(String string, int languageType){

        //应用ID 1a838f21d870437d



        //应用密钥 zzCiHIbY2PcgvykPkQlJPOO2K6fGnHE7


        String appKey ="您的appKey";
        String query = "你好";
        String salt = String.valueOf(System.currentTimeMillis());
        String from = "zh-CHS";
        String to = "EN";
        String sign = md5(appKey + query + salt+ "您的密钥");

        Map params = new HashMap();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);
        params.put("sign", sign);
        params.put("salt", salt);
        params.put("appKey", appKey);
        //System.out.println(requestForHttp("http://openapi.youdao.com/api", params));
        return null;
    }




    /**
     * 生成32位MD5摘要
     * @param string
     * @return
     */
    public static String md5(String string) {
        if(string == null){
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};

        try{
            byte[] btInput = string.getBytes("utf-8");
            /** 获得MD5摘要算法的 MessageDigest 对象 */
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            /** 使用指定的字节更新摘要 */
            mdInst.update(btInput);
            /** 获得密文 */
            byte[] md = mdInst.digest();
            /** 把密文转换成十六进制的字符串形式 */
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        }catch(NoSuchAlgorithmException | UnsupportedEncodingException e){
            return null;
        }
    }

    /**
     * 根据api地址和参数生成请求URL
     * @param url
     * @param params
     * @return
     */
    public static String getUrlWithQueryString(String url, Map params) {
        if (params == null) {
            return url;
        }

        StringBuilder builder = new StringBuilder(url);
        if (url.contains("?")) {
            builder.append("&");
        } else {
            builder.append("?");
        }

        int i = 0;


        for (Object key :params.keySet()) {
            String value = (String)params.get(key);
            if (value == null) { // 过滤空的key
                continue;
            }

            if (i != 0) {
                builder.append('&');
            }

            builder.append(key);
            builder.append('=');
            builder.append(encode(value));

            i++;
        }

        return builder.toString();
    }
    /**
     * 进行URL编码
     * @param input
     * @return
     */
    public static String encode(String input) {
        if (input == null) {
            return "";
        }

        try {
            return URLEncoder.encode(input, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return input;
    }

}
