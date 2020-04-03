package com.iyangcong.reader.utils;

import android.content.Context;
import android.content.SharedPreferences.Editor;

import com.iyangcong.reader.app.AppContext;

import java.util.ArrayList;
import java.util.List;


public class SharedPreferenceUtil {

    private static final String SP_NAME = "I_YANGCONG";
    /**
     * 用户userId
     */
    public static final String USER_ID = "user_id";

    public static final String LOGIN_TYPE ="login_type";
    /*
    * 用户类型
    * */

    public static final String USER_TYPE ="user_type";
    /**
     * 登录状态
     */
    public static final String LOGIN_STATE = "login_state";
    /**
     * 搜索历史
     */
    public static final String SEARCH_HISTORY = "search_history_";
    /**
     * 搜索历史
     */
    public static final String SEARCH_CIRCLE_HISTORY = "search_circle_history_";
    /**
     * 生词本最后一次更新时间
     */
    public static final String WORD_LAST_UPDATE_TIME = "words_last_update_time";
    /**
     * 前一个用户的userId
     */
    public static final String PRE_USER_ID = "pre_user_id";
    /**
     * 当前阅读的书籍id
     */
    public static final String CURRENT_BOOK_ID = "current_bookId";
    /**
     * 当前购买并且阅读的图书id
     */
    public static final String CURRENT_BUYED_BOOK_ID = "current_buyed_bookId";
    /**
     * 当前阅读的书籍语言 0:双语 1:中文 2:英文
     */
    public static final String CURRENT_BOOK_LANGUAGE = "current_book_language";
    /**
     * 当前阅读的书籍是否打开了句对功能 0 未打开 1已经打开
     */
    public static final String CURRENT_BOOK_HAVESENTENCE = "current_book_havesentence";
    /**
     * 当前阅读的书籍语言 true:可以切换  false:不可以切换     默认可以
     */
    public static final String CAN_CHANGE_LANGUAGE = "can_change_language";

    /**
     * 当前字号大小   默认32
     */
    public static final String CURRENT_FONT_SIZE = "current_font_size";

    /**
     * 当前屏幕亮度
     */
    public static final String CURRENT_LIGHT_VALUE = "current_light_value";

    /**
     * 是否为夜间模式 false:日间模式    true:夜间模式
     */
    public static final String DAY_OR_NIGHT = "day_or_night";

    /**
     * 当前阅读器背景色   默认0
     */
    public static final String CURRENT_BACKGROUND_COLOR = "current_background_color";

    /**
     * 笔记是否公开   默认false
     */
    public static final String IS_NOTE_OPEN = "is_note_open";

    /**
     * 通知开启状态
     */
    public static final String IS_RECEIVE_NOTICE = "is_receive_notice";

    /*
    搜索单词历史
     */
    public static final String SEARCHHISTORY = "searchhistory";


    //阅读器菜单选中状态
    public static final String WORD_SIZE_CHECKED = "word_size_checked";
    public static final String TURN_PAGE_MODE_CHECKED = "turn_page_mode_checked";
    public static final String BACKGROUND_COLOR_CHECKED = "background_color_checked";

    //未上传订单信息
    public static final String NOT_UPLOAD_ORDER_BOOKS = "bookIds";
    public static final String NOT_UPLOAD_ORDER_PRICE = "totalPrice";
    public static final String NOT_UPLOAD_ORDER_PRICESTR = "priceStr";
    //阅读轨迹记录id
    public static final String READING_RECORD_LODID = "readingRecordLogId";
    //阅读轨迹记录最近阅读时间
    public static final String READING_RECORD_LAST_TIME = "readingRecordLastTime";
    //阅读器当前段落
//    public static final String READING_CURRENT_SEGMENT_ID = "readingCurrentSegmentId";
    //当前账号

    public static final String LOGIN_USER_ACCOUNT = "userAccount";
    //学期ID
    public static final String SEMESTER_ID = "semesterId";
    public static final String SEMESTER_NAME ="semesterName";
    public static final String CLASS_IDS ="classIds";
    public static final String CLASS_NAMES="classNames";
    public static final String SHOW_TYPE="showType";

    /**
     * 被删除的默认书籍 -1:生成默认书籍   0:没有被删除的书籍  1:小王子被删除    2:使用说明被删除   3:默认书籍全部被删除
     */
    public static final String DELETED_DEFAULT_BOOK = "deleted_default_book";

    /**
     * 历史登录账户
     */
    public static final String ACCOUNT_IDS = "account_ids";
    /**
     * 上一次查过的词
     */
    public static final String LAST_CHECKED_WORD = "last_chechked_word";
    /**
     * 是否自动添加生词的状态位
     */
    public static final String AOUT_ADD_NEW_WORD = "aout_add_new_word";


    /**
     * 是否显示进度
     */
    public static final String ISSHOWSHELFBOOK = "isshowshelfbook";
    /**
     * 书架图书隐藏
     */
    public static final String SHELFBOOK_HIDE = "shelfbook_hide";

    /**
     * 本地书架还是云书架
     */
    public static final String CLOUD_OR_LOCAL = "cloud_or_local";

    public static final String USER_PORTAIT_URL = "user_portait_url";
    /**
     * 章节id
     */
    public static final String CHAPTERID = "chapterid";
    /**
     * 书签笔记同步时间戳
     */
    public static final String SYNCHRONIZE_TIMESTAMP = "synchronizeTimestamp";
    /**
     * 是否第一次阅读
     */
    public static final String IS_FIRST_READ = "is_first_read";
    /**
     * 是否第一次试用视频悬浮窗功能
     */
    public static final String IS_FIRST_FLOAT_VIDEO = "is_first_float_video";
    /**
     * 打开的书籍在手机本地的id;
     */
    public static final String LOCAL_BOOK_ID = "local_book_id";

    /**
     * 手机设备型号
     */
    public static final String PHONE_MODEL = "phone_model";

    public static final String THIRTPART_TYPE="thirtpart_type";

    private static SharedPreferenceUtil instance = new SharedPreferenceUtil();

    private SharedPreferenceUtil() {

    }

    private static synchronized void syncInit() {
        if (instance == null) {
            instance = new SharedPreferenceUtil();
        }
    }

    public static SharedPreferenceUtil getInstance() {
        if (instance == null) {
            syncInit();
        }
        return instance;
    }

    private android.content.SharedPreferences getSp() {
        return AppContext.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public int getInt(String key, int def) {
        try {
            android.content.SharedPreferences sp = getSp();
            if (sp != null)
                def = sp.getInt(key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    public void putInt(String key, int val) {
        try {
            android.content.SharedPreferences sp = getSp();
            if (sp != null) {
                Editor e = sp.edit();
                e.putInt(key, val);
                e.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getLong(String key, long def) {
        try {
            android.content.SharedPreferences sp = getSp();
            if (sp != null)
                def = sp.getLong(key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    public void putLong(String key, long val) {
        try {
            android.content.SharedPreferences sp = getSp();
            if (sp != null) {
                Editor e = sp.edit();
                e.putLong(key, val);
                e.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float getFloat(String key, float def) {
        try {
            android.content.SharedPreferences sp = getSp();
            if (sp != null)
                def = sp.getFloat(key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    public void putFloat(String key, float val) {
        try {
            android.content.SharedPreferences sp = getSp();
            if (sp != null) {
                Editor e = sp.edit();
                e.putFloat(key, val);
                e.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getString(String key, String def) {
        try {
            android.content.SharedPreferences sp = getSp();
            if (sp != null)
                def = sp.getString(key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    public void putString(String key, String val) {
        try {
            android.content.SharedPreferences sp = getSp();
            if (sp != null) {
                Editor e = sp.edit();
                e.putString(key, val);
                e.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getBoolean(String key, boolean def) {
        try {
            android.content.SharedPreferences sp = getSp();
            if (sp != null)
                def = sp.getBoolean(key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    public void putBoolean(String key, boolean val) {
        try {
            android.content.SharedPreferences sp = getSp();
            if (sp != null) {
                Editor e = sp.edit();
                e.putBoolean(key, val);
                e.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void remove(String key) {
        try {
            android.content.SharedPreferences sp = getSp();
            if (sp != null) {
                Editor e = sp.edit();
                e.remove(key);
                e.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean addArray(String key, String sKey) {
        android.content.SharedPreferences sp = getSp();
        if (sp != null) {
            int size = sp.getInt(key + "size", 0);
            Editor e = sp.edit();
            e.putInt(key + "size", size + 1);
            e.putString(key + size, sKey);
            return e.commit();
        }
        return false;
    }

    public boolean putArray(String key, List<String> sKey) {
        android.content.SharedPreferences sp = getSp();
        if (sp != null) {
            Editor e = sp.edit();
            e.putInt(key + "size", sKey.size()); /*sKey is an array*/
            for (int i = 0; i < sKey.size(); i++) {
                e.remove(key + i);
                e.putString(key + i, sKey.get(i));
            }
            return e.commit();
        }
        return false;
    }

    public List<String> getArray(String key) {
        List<String> sKey = new ArrayList<>();
        sKey.clear();
        android.content.SharedPreferences sp = getSp();
        if (sp != null) {
            int size = sp.getInt(key + "size", 0); /*sKey is an array*/
            for (int i = 0; i < size; i++) {
                sKey.add(sp.getString(key + i, null));
            }
        }
        return sKey;
    }

    public void removeArray(String key) {
        try {
            android.content.SharedPreferences sp = getSp();
            if (sp != null) {
                int size = sp.getInt(key + "size", 0); /*sKey is an array*/
                Editor e = sp.edit();
                e.remove(key + "size"); /*sKey is an array*/
                for (int i = 0; i < size; i++) {
                    e.remove(key + i);
                }
                e.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
