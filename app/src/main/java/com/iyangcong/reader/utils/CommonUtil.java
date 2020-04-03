package com.iyangcong.reader.utils;


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.NewWord;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class CommonUtil {
    private static Bitmap bitmap = null;

    private static SharedPreferenceUtil sharedPreferenceUtil = null;


    public static Bitmap getBitmap() {
        return bitmap;
    }

    public static void setBitmap(Bitmap map) {
        bitmap = map;
    }

    public void RecycleBitmap() {
        if (null != bitmap) {
            bitmap.recycle();
        }
    }

    /**
     * 获取上下文的名称
     * @param context
     * @return
     */
    public static Activity getActivityByContext(Context context){
        while(context instanceof ContextWrapper){
            if(context instanceof Activity){
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        return true;
    }

    public static String getRootFilePath() {
        if (hasSDCard()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";// filePath:/sdcard/��Ŀ¼·��
        } else {

            return Environment.getDataDirectory().getAbsolutePath() + "/data/"; // filePath: /data/data/
        }
    }

    public static String getBooksDir() {
        String dir = null;
        if (CommonUtil.hasSDCard()) {
            dir = CommonUtil.getRootFilePath() + "iyangcong/books/";
        } else {
            dir = CommonUtil.getRootFilePath() + "com.iyangcong.reader/files" + "/iyangcong/books/";
        }
        return dir;
    }

    /**
     * 获取登录状态
     */
    public static boolean getLoginState() {
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
        return sharedPreferenceUtil.getBoolean(SharedPreferenceUtil.LOGIN_STATE, false);
    }

    public static long getUserId() {
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
        return sharedPreferenceUtil.getLong(SharedPreferenceUtil.USER_ID, 0);
    }

    public static String getLastCheckedWord() {
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
        return sharedPreferenceUtil.getString(SharedPreferenceUtil.LAST_CHECKED_WORD, "");
    }

    public static String getLocalMacAddressFromIp(Context context) {
        String mac_s = "";
        try {
            byte[] mac;
            NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress.getByName(getLocalIpAddress()));
            mac = ne.getHardwareAddress();
            mac_s = byte2hex(mac);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mac_s;
    }

    private static String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer(b.length);
        String stmp = "";
        int len = b.length;
        for (int n = 0; n < len; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs = hs.append("0").append(stmp);
            else {
                hs = hs.append(stmp);
            }
        }
        return String.valueOf(hs);
    }

    //获取本地IP
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Logger.e("WifiPreference IpAddress", ex.toString());
        }

        return null;
    }

    /**
     * 获取ip地址
     *
     * @return
     */
    public static String getHostIP() {

        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Logger.i("yao", "SocketException");
            e.printStackTrace();
        }
        return hostIp;

    }

    /**
     * 获取书籍支持的语种
     *
     * @param bookLanguage
     * @return
     */
    public static String getSupportLanguage(String bookLanguage) {
        if (bookLanguage.contains("3") || (bookLanguage.contains("中") && bookLanguage.contains("英"))) {
            return "双语";
        } else if (bookLanguage.contains("2") || bookLanguage.contains("英")) {
            return "英文";
        } else {
            return "中文";
        }
    }


//    public static String getFormatNewWordString(List<NewWord> list) {
//        StringBuilder sb = new StringBuilder("");
//        for (NewWord word : list) {
//            sb.append(word.getFormatString());
//        }
//        return sb.toString();
//    }

    /**
     *按照20170621商量出来的上传单词的格式拼接字段，
     * 生成符合上传格式要求的字符串
     * @param
     * @return
     */
//    public static String format20170621(List<NewWord> list){
//        StringBuilder sb = new StringBuilder("");
//        for(NewWord word:list){
//            sb.append(word.formateString());
//        }
//        return sb.toString();
//    }
    public static String format201906304(NewWord temNewWord) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("userId", CommonUtil.getUserId());
            jsonObj.put("bookId", temNewWord.getBookId());
            jsonObj.put("bookName", temNewWord.getBookName());
            jsonObj.put("word", temNewWord.getWord());
            jsonObj.put("phonetic", temNewWord.getPhonetic());
            jsonObj.put("content", temNewWord.getTempContent());
            jsonObj.put("articleContent", temNewWord.getArticleContent());
            jsonObj.put("level", temNewWord.getLevel());
            jsonObj.put("ifReadyRecite", temNewWord.getIFreadyRecite());
            jsonObj.put("ifAlreadyKnow", temNewWord.getIFalreadyKnow());
            jsonObj.put("ifFavorite", temNewWord.getIFfavorite());
            jsonObj.put("ifNeedAgain", temNewWord.getIFneedAgain());
            jsonObj.put("terminal", "3");
            jsonObj.put("segmentId", temNewWord.getSegmentId());
            jsonObj.put("time",temNewWord.getTime());
            jsonObj.put("localWord",temNewWord.getLocalWord());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj.toString();
    }


    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return context.getString(R.string.version_name) + " " + version;
        } catch (Exception e) {
            e.printStackTrace();
            return context.getString(R.string.can_not_find_version_name);
        }
    }

    //判断文件是否存在
    public static boolean fileIsExists(String strFile)
    {
        try
        {
            File f=new File(strFile);
            if(!f.exists())
            {
                return false;
            }
        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }
}
