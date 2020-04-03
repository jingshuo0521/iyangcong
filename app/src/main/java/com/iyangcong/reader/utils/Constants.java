package com.iyangcong.reader.utils;

import android.graphics.Color;

/**
 * Created by WuZepeng on 2017-02-22.
 */

public class Constants {

    public static final boolean DEBUG =true;
    public static final int PITCUTE_AND_TEXT = 0;
    public static final int ONLY_PTICTURE = 1;
    public static final int CLASSIFICATION = 2;
    public static final int THIRDPART_TYPE_BFSU=5;

    public static final String circleId = "circleId";
    public static final String circleName = "circleName";
    public static final String reviewId = "reviewId";
    public static final String topicId = "topicId";
    public static final String groupId = "groupId";
    public static final String GROUP_TYPE = "groupType";
    public static final String readingPartyId = "readingPartyId";
    public static final String readingPartyTitle = "readingPartyTitle";
    public static final String QQ_APP_ID = "1101100001";
    public static final String WECHAT_APP_ID = "wxc659f70ff4f8a0ba";//微信appId
    public static final String WECHAT_MCH_ID = "1355789502"; //商户ID
    public static final String WEIBO_APP_KEY = "4049139728";
    public static final String WEIBO_APP_SECRET = "e4c312b5dc33cd921be6a7bde094c31f";
    public static final String WXPAY_NOTIFY_URL = "http://www.iyangcong.com/onion/commons/auth/weixin/index.php";//微信成功后回调地址
    public static final String WX_PAY_KEY = "a6f5ee8438688e44e0bafa84ce46687c";//微信支付key
    public static final String CREATE_CIRLCE = "create_cirlce";

    //    public static final String BAIDU_API_KEY="vMh5fRFQZ11HZSsXHnSaySVR";//百度api_key
    public static final String BAIDU_API_KEY = "NfWEnXFZuuLFeXErCepnF7sb";//百度api_key

    /**
     * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
     * <p>
     * <p>
     * 注：关于授权回调页对移动客户端应用来说对用户是不可见的，所以定义为何种形式都将不影响，
     * 但是没有定义将无法使用 SDK 认证登录。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     * </p>
     */
    public static final String WEIBO_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

    /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
     * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利
     * 选择赋予应用的功能。
     * <p>
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的
     * 使用权限，高级权限需要进行申请。
     * <p>
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     * <p>
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */
    public static final String WEIBO_SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog";

    /**
     * 标签颜色值定义
     */
    public static final int[] tagColors = new int[]{
            Color.parseColor("#90C5F0"),
            Color.parseColor("#91CED5"),
            Color.parseColor("#F88F55"),
            Color.parseColor("#C0AFD0"),
            Color.parseColor("#E78F8F"),
            Color.parseColor("#67CCB7"),
            Color.parseColor("#F6BC7E")
    };
    public static final String VIDEO_TITLE = "videoTitle";
    public static final String VIDEO_ADDRESS = "videoAddress";
    public static final String THOUGHT_ACTIVITY_TITLE = "ThoughtActivityTitle";
    public static final String TOPIC_ACITIVITY_TITLE = "TopicAcitivityTitle";
    public static final String TOPIC_ACTIVITY_BOOK_NAME = "TopicActivityBookName";
    public static final String BOOK_ID = "bookId";
    public static final String BOOK_NAME = "bookName";
    public static final String USER_ID = "userId";
    public static final String USER_NAME  ="userName";
    public static final String USER_TYPE="userType";
    public static final String NICK_NAME ="nickName";
    public static final String USER_ACCOUNT ="account";
    public static final String IS_MYSELF = "isMyself";
    public static final String CIRCLE_CATEGORY = "circleCategory";
    public static final String DATA_ERROR_FROM_REMOTE_DATABASE = "data_error_from_remote_database";
    public static final String NETWORK_ERROR = "network_error";
    public static final String NETWORD_STATE = "netwok_state";
    public static final String DATA_ERROR_FROM_REMOTE_DATABASE_FOR_FRAGMENT = "data_error_from_remote_database_for_fragment";
    public static final String DATA_ERROR_FOR_VIDEO = "data_error_for_video";
    //有道翻译
    public static final String YOUDAO_API_KEY = "1236571953";
    public static final String YOUDAO_KEYFROM = "iyangcong";

    /**
     * 0:目录 1:笔记    2:书签
     */
    public static final String TOC_ID = "toc_id";

    public static final String WORDS_WHETHER_HAS_SYNCHRONIZATION = "wordsWhetherHasSynchronization";
    public static final String MONTHLY_BOOK_LIST_NAME = "monthly_book_list_name";
    public static final String MONTHLY_BOOK_ID = "monthly_book_id";
    public static final String MONTHLY_BOOK_SPECIAL_PRICE = "monthly_book_price";

    public static final String CHAPTERID = "chapterid";

    public static final String PARAGRAPH_TEXT = "paragraph_text";

    public static final String OFFSET = "offset";

    public static final String BOOK_UID = "bookUid";

    public static final String CREATE_CIRLE_OR_MODIFY = "createCirleOrModify";
    public static final String URL = "url";
    public static final String Title = "title";
    public static final String USERAGREEMENT = "useragreement";

    public static final String PARAGRAHP_ID = "paragrahpId";

    public static final int maxPortaitSize = 14;
    public static final String IS_BINDING = "isBinding";
    public static final String TO_USER_ID = "ToUserId";
    //绑定账号的类型，电话或者邮箱
    public static final String BIND_TYPE = "BindType";
    public static final String FROM_BOOK_MARKET_OR_MINE = "FromBookMarketOrMine";
    //书籍版本
	public static final String BookEdition = "book_edition";

	public static final String VIDEO_COVER = "video_cover";
	public static final String EPUB_BOOK_NAME = "EpubBookName";
	public static final String ENDSEGMENTID = "endsegmentid";
    public static final String READING_RECORDER = "reading_recorder";
    public static final String READING_END_TIME = "reading_end_time";
    public static final String TASK_TYPE = "task_type";
    public static final int DEFAULT_SEMESTER_ID=1165;//此值在学期更新后做修改，1165，是2019年春季学期
}
