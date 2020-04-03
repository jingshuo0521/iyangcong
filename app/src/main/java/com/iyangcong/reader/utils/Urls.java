package com.iyangcong.reader.utils;

/**
 * Created by ljw on 2016/12/8.
 */

/**
 * url路径
 */
public class Urls {

    //public static final String URL = "https://edu.iyangcong.com";
   //public static final String URL = "http://10.112.200.42:8080/iyangcong";
    public static final String URL = "https://edu.iyangcong.com";
    //public static final String URL = "http://testedu.iyangcong.com";
    public static final String URL_NoHttps = "http://edu.iyangcong.com";

	// public static final String SEARCH_URL = "http://s.iyangcong.com:8088/iyc-solr/solrSearch/";
    public static final String SEARCH_URL = "http://edu.iyangcong.com/solrSearch/";

//    public static final String SEARCH_URL = "http://s.iyangcong.com:8088/iyc-solr/solrSearch/";
//    public static final String SEARCH_URL = "http://42.159.119.151:8088/iyc-solr/solrSearch/";
//    public static final String URL = "http://192.165.2.151:8080/iyangcong";
//    public static final String URL = "http://123.56.252.18:8088";

    public static final String TMP_URL_LHJ = "http://10.112.120.4:8080/iyangcong";//李红锦的ip
    public static final String TMP_URL_ZZS = "http://10.112.200.42:8080/iyangcong";//张子实的ip
    public static final String TMP_URL_CWK = "http://10.103.41.231:8080/iyangcong";//陈文楷的ip
    public static final String TMP_URL_WZR = "http://192.168.2.130:8080/iyangcong";//王梓任的ip
    public static final String TMP_URL_WZP2 = "http://10.103.41.246:8080/iyangcong";
    public static final String TMP_URL_LY = "http://10.112.187.66:8080/iyangcong";//李阳的ip
    public static final String TMP_URL_QYP = "http://10.103.41.244:8080/iyangcong";//钱远鹏的ip
    public static final String TML_URL_CKL = "http://192.165.7.133:8080/iyangcong";
    public static final String TMP_URL_CQ = "http://10.112.190.185:8081/iyangcong";//陈乾的ip


    //广告页
    public static final String AdvPage = URL + "/appbook/getStartAD";
    //登录模块
    public static final String LoginURL = URL + "/applogincontroller/login";//
    //发送验证码
    public static final String SendcodeURL = URL + "/activatecontroller/sendcode";
    //判断用户是否已注册
    public static final String IsUserRegistURL = URL + "/registercontroller/registerHasAcount";//
    public static final String FindPasswordCodeURL = URL + "/applogincontroller/forgivepassword";
    public static final String RegisterURL = URL + "/registercontroller/register";//
    public static final String FindPasswordURL = URL + "/logincontroller/resetpassword";//
    public static final String ModifyPasswordURL = URL + "/logincontroller/resetpasswordPerson";//
    public static final String BookMarketTestBound = URL + "/login/threelogin/testbound";
    public static final String BookMarketBoundLoginPhone = URL + "/login/threelogin/loginphone";
    //第三方绑定到后台已有手机账号或邮箱，并登录
    public static final String ThirdPartBindLogin=URL+"/login/threelogin/thirdPartBindLogin";
    //第三方绑定注册登录一次性完成
    public static final String ThirdPartBoundNotRegister=URL+"/login/threelogin/thirdPartsetnotregisterlogin";


    public static final String BoundNotRegisterURL = URL + "/login/threelogin/setnotregisterlogin";
    public static final String BindingSituationURL = URL + "/login/getbyuserid";
    public static final String CutconnectURL = URL + "/login/cutconnect";
    //绑定邮箱
    public static final String BINGEMAIL = Urls.URL + "/logincontroller/bindaccount";


    public static final String SERVER = "http://server.jeasonlzy.com/OkHttpUtils";
    public static final String URL_METHOD = SERVER + "/method";
    public static final String APIKEY = "593e074aa96b18276fbe1aec8992f398";
    //书城模块
    public static final String BookMarketBookListURL = URL + "/appbook/recommendbook";
    public static final String BookMarketBookListMethodTwo = URL + "/appbook/recommendbookMethodTwo";
    public static final String BookMarketNavBookListURL = URL + "/appbook/characterbook";
    //通识阅读
    public static final String BookMarketGeneralBookListURL = URL + "/appbook/apptongshi";
    //积分兑换列表
    public static final String BookMarketPointBookListURL = URL + "/appbook/point";
    //热门推荐列表
    public static final String BookMarketHotBookListURL = URL + "/appbook/gethotbooks";
    public static final String BookMarketTodayPushURL = URL + "/appbook/bookbanners";
    public static final String BookMarketBookSectionURL = URL + "/appbook/getcategory";
    public static final String BookMarketBookDetailURL = URL + "/appbook/getbookdetail";
    public static final String BookMarketSearchURL = SEARCH_URL + "searchReadingMaterialByCondition";
    public static final String BookMarketHotSearchURL = URL + "/solrSearch/getHotSearchWord";
    public static final String BookMarketBookClassifyURL = URL + "/appbook/getcategory";
    public static final String BookMarketAppBoms = URL + "/appbook/appboms";
    public static final String BookMarketClass = URL + "/appbook/getbookclass";
    public static final String BookMarketSubjectURL = URL + "/appbook/appallsubject";
    public static final String BookMarketCollectURL = URL + "/uacontroller/shoucang";
    public static final String BookCollectionIdURL = URL + "/reflagbookcontroller/getuserbookid";
    public static final String BookGetByVirtualCoin = URL + "/personCenter/userVirtualCoinForBook";
    public static final String BookFreeGetURL = URL + "/payController/freeorder";
    public static final String BookMarketGetBanner = URL + "/appbook/firstphoto";
    //北外在线接口
    public static final String BUFSRecommendBook =URL+"/appbook/thirdPartBooks";
    public static final String INDEX_MAIN=URL+"/banner/getIndexInfo";
    //按照标签请求数据
    public static final String BookMarketTagsBookListURL = URL + "/appbook/getapptagbooks";
    /*
    阅读轨迹开始记录
     */
    public static final String ReadingOrbitRecordBeginURL = URL + "/app/sentencecontroller/reading_orbit_record_begin";
    /*
    阅读轨迹结束记录
     */
    public static final String ReadingOrbitRecordEndURL = URL + "/app/sentencecontroller/record";
    //添加书摘书签
    public static final String FBReaderAddBookmarkURL = URL + "/app/commentscontroller/addExcerpt";
    //删除书签
    public static final String FBReaderDelBookmarkURL = URL + "/app/commentscontroller/delbookmarkbyid";

    //获取公开笔记评论
    public static final String COMMENTSNEW = URL + "/commentscontroller/commentsNew";

    //获取公开笔记
    public static final String CONMENTEDCONENTLIST = URL + "/commentscontroller/commentedcontentList";

    //获取公开笔记数量
    public static final String CONMENTEDCONENTCOUNTLIST = URL + "/app/commentscontroller/getCommentsCountBySegments";
    //普通用户添加笔记  弃用
    public static final String FBReaderOrdinaryAddBookNoteURL = URL + "/app/commentscontroller/booknote";
    //学生用户添加笔记  弃用
    public static final String FBReaderStudentAddBookNote1URL = URL + "/app/commentscontroller/findCommentedcontentOrInsert";
    public static final String FBReaderStudentAddBookNote2URL = URL + "/app/commentscontroller/add2";
    // 9.5更新后 三端统一的笔记上传接口
    public static final String FBReaderAddBookNoteURL = URL + "/commentscontroller/addComments";
    //更新笔记 弃用
    public static final String FBReaderUpdateBookNoteURL = URL + "/app/commentscontroller/updateComment";
    // 9.5更新后 修改笔记接口（三端统一）
    public static final String FBReaderEditBookNoteURL = URL + "/commentscontroller/CommentEditnew";
    //批量上传阅读进度
    public static final String POST_READING_DATA = URL + "/sentencecontroller/insert";
    //删除笔记  弃用
    public static final String FBReaderDelBookNoteURL = URL + "/app/commentscontroller/deleteComment";
    // 9.5更新后 三端统一的笔记删除接口
    public static final String FBReaderDelBookNoteNewURL = URL + "/commentscontroller/deleteComments";

    //上传阅读进度 需修改参数
    public static final String POSTREADINGRECORDE = URL + "/sentencecontroller/app/insertBrowserRecord2";
    //同步阅读进度
    public static final String READINGRECORDS = URL + "/sentencecontroller/app/updateFromUserBrowRecord";
    //获取图书是否打开句对功能
    public static final String HAVESENTENCE = URL + "/sentencecontroller/sentenceswitch";
    //句对翻译
    public static final String FBReaderSearchSentence = URL + "/solr/search/app/sentence";
    /**
     * 添加读后感
     */
    public static final String BookMarketAddReview = URL + "/reviews/addreviews";
    /**
     * 发送私信
     */
    public static final String SendPrivateLetter = URL + "/personCenter/writeMessageToSomebody";
    /**
     * 添加到购物车
     */
    public static final String BookMarketAddToShoppingCartURL = URL + "/payController/setshopCar";
    public static final String BookMarketAddAllToShoppingCartURL = URL + "/payController/setshopcarmore";
    /*
    *支付信息
     */
    public static final String SaveAppPayResultURL = URL + "/payController/saveAppPayResult";
    /*
    支付宝获取订单号
     */
    public static final String GetAlipayOrderInfoURL = URL + "/payController/getAndroidAlipayOrderInfo";


    /*
        微信支付获取订单信息
         */
    public static final String GetWechatpayOrderInfoURL = URL + "/payController/getAndroidWechatpayOrderInfo";
    /*
    查询微信支付结果
     */
    public static final String CheckStatusOfWechatPay = URL + "/payController/checkStatusOfWechatPay";
    /**
    /**
     * 从购物车移除
     */
    public static final String BookMarketRemoveFromShoppingCartURL = URL + "/payController/bookdelete";
    //批量移除
    public static final String BookMarketBatchRemoveFromShoppingCartURL = URL + "/personCenter/removeBooksFromShopCart";

    /**
     * 图书是否已购买
     */
    public static final String BookisBuyedURL = URL + "/appbook/getorbook";

    /**
     * 扫一扫中猜你喜欢
     */
    public static final String GetOutLessBooks = URL + "/relpersonbookcontroller/getoutlessbooks";
    /**
     * 图书下载
     */
    public static final String FileDownloadURL = URL + "/book/downloadepub";
    //书架模块
    public static final String GetUserContinuousSignDays = URL + "/signController/getUserContinuousSignDays";
    public static final String SignInfoURL = URL + "/app/personCenter/getUserSignInfo";
    public static final String SignInURL1 = URL + "/signController/sign";
    public static final String GetMonthSign = URL + "/signController/getUserSignInfoByMonth";
    public static final String SignInURL = URL + "/app/personCenter/userSignIn";
    public static final String CloudBookShelf = URL + "/appbook/bookshelf";
    public static final String CloudBookShelfSort = URL + "/appbook/yunbooksort";
    public static final String BookShelfByCategoryURL = URL + "/appbook/bookshelfbycategory";
    public static final String BookShelfSearchURL = URL + "/appbook/sequencebookshelf";
    // 发现模块
    public static final String BannerURL = URL + "/banner/getbanner";
    public static final String BroadcastURL = URL + "/banner/getbroadcast";
    //    public static final String HotGroupURL = URL + "/found/getapphotgroups";
    public static final String HotGroupURL = URL + "/found/getapphotgroups";
    public static final String HotTopicURL = URL + "/found/getapphottopics";
    //发现首页读后感
    public static final String DiscoverHorReviewURL = URL + "/found/getapphotreviews";
    public static final String CircleCategoryURL = URL + "/found/getappcategory";
    public static final String DiscoverCircleItemContentURL = URL + "/found/getappgroupsbycategory";
    //话题详情
    public static final String DiscoverCricleTopicDetails = URL + "/groups/getdetailtopic";
    public static final String DiscoverCricleTopTopic = URL + "/found/getapptoptopics";
    public static final String DiscoverCircleTopicDetailsComment = URL + "/groups/getresponses";
    public static final String DiscoverCircleTopicDetailsLikedList = URL + "/found/getapptopiclike";
    public static final String DiscoverCirlcleTopicAddComment = URL + "/groups/addresponse";
    public static final String DiscoverCirlceTopicCommetLike = URL + "/groups/like";
    //读后感详情
    public static final String DiscoverCircleReviewsDetails = URL + "/reviews/getdetailreviews";
    public static final String DiscoverCirlceReviewsDetailsComment = URL + "/found/getappreviewsresponse";
    public static final String DiscoverCirlceReviewsAddComment = URL + "/reviews/addresponse";
    public static final String DiscoverCircleReviewLikePersonList = URL + "/found/getlikeperson";
    public static final String DiscoverCirlceReviewLike = URL + "/reviews/like";
    /*读书会主页*/
    public static final String DiscoverCirlceReadPartyAcitivytList = URL + "/found/getreadinglist";
    public static final String DiscoverCircleReadiPartyVideoSource = URL + "/found/getclubresource";
    public static final String DiscoverCirlceReadPartyVideoList = URL + "/readingclub/getreadingclubresource";
    public static final String DiscoverCircleReadPartyCooperation = URL + "/found/getapporgernization";
    /*读书会详情 */
    public static final String DiscoverCircleReadPartyDetail = URL + "/readingclub/getdetailreadingclub";
    public static final String DiscoverCirlceReadPartyDetailVideoList = URL + "/readingclub/getresource";
    /*获取圈子类别（按类别查找圈子时，新建圈子选择类别时）*/
    public static final String DiscoverCircleCategoryURL = URL + "/groups/getcategory";
    /*圈子详情页*/
//    public static final String DiscoverCircleDetialURL = URL + "/found/getappgroupinfo";
    //搜索圈子接口
    public static final String DiscoverCircleSearchURL = SEARCH_URL + "searchGroupByConditions";
    //搜索话题接口
    public static final String DiscoverTopicSearchURL = SEARCH_URL + "searchTopicByConditions";
    //搜索书评接口
    public static final String DiscoverReviewSearchURL = SEARCH_URL + "searchReviewsByConditions";
    public static final String DiscoverCircleDetialURL = URL + "/groups/getgroupinfo";
    public static final String DiscoverCircleDetailTopicList = URL + "/found/getapptopics";
    public static final String DiscoverCircleCategoryPhoto = URL + "/found/getappcategoryphoto";
    public static final String DiscoverJionCircleURL = URL + "/groups/joingroup";
    public static final String DiscoverCircleWhetherJiont = URL + "/groups/getgroupinfo";
    /*新建话题*/
    public static final String DiscoverCircleDetailAddTopic = URL + "/groups/addtopic";
    public static final String DiscoverCircleDetailUploadPicture = URL + "/found/uploadphoto";
    /**
     * 获取圈子标签（在创建圈子中使用）
     */
    public static final String DiscoverCircleLabelURL = URL + "/found/gettags";
    public static final String DiscoverCircleGetPersonAndAllFriends = URL + "/personCenter/getPersonAllFriends";
    public static final String DiscoverCircleMemberDegree = URL + "/groups/degree";
    public static final String DiscoverCircleMemberListURL = URL + "/found/getappmembers";
    public static final String DiscoverCircleGroupBooks = URL + "/found/getappgroupbooks";
    public static final String DiscoverCircleGetCollectedBookList = URL + "/app/personCenter/getPersonCollectBookList";
    public static final String DiscoverCircleCreateURL = URL + "/groups/addgroup";
    public static final String DiscoverCircleCheckNameRepeated = URL + "/groups/checkgroupname";
    public static final String DiscoverCircleUploadPhoto = URL + "/found/uploadappphoto";
    public static final String searchBookOnline = SEARCH_URL + "searchReadingMaterialByCondition";
//    public static final String searchBookOnline = SEARCH_URL + "searchReadingMaterialByTitleLike";
    //我的模块
    public static final String MessageCommentURL = URL + "/personCenter/getPersonResponseInfoList";//
    public static final String MessageNoticeInviteURL = URL + "/personCenter/getUserNoticeMessageList";//
    public static final String MessageDeleteURL = URL + "/personCenter/deleteMessage";
    //购物车
    public static final String PersonShopcartURL = URL + "/app/personCenter/getPersonShopcart";
    //已购图书
    public static final String PersonBuyBookListURL = URL + "/app/personCenter/getIOSPersonBuyBookList";
    //我的收藏
    public static final String PersonCollectBookListURL = URL + "/app/personCenter/getPersonCollectBookList";
    //我的浏览历史
    public static final String PersonHistoryBookListURL = URL + "/relpersonbookcontroller/getPersonHistory";
    //个人主页动态
    public static final String PersonDynamicStateURL = URL + "/app/personCenter/getPersonDynamicState";
    //个人主页读后感
    public static final String PersonReviewListURL = URL + "/app/personCenter/getPersonReviewList";
    //个人主页创建的圈子
    public static final String PersonCreateGroupsInfoListURL = URL + "/groups/getmygroups";
    //个人主页加入的圈子
    public static final String PersonAttendGroupsInfoListURL = URL + "/groups/getjoingroups";
    //个人主页个人信息
    public static final String PersonBasicInfoURL = URL + "/app/personCenter/getPersonBasicInfo";
    //个人主页读过
    public static final String PersonReadedBookURL = URL + "/app/personCenter/readedbook";
    //个人主页创建的话题
    public static final String PersonCreateTopicURL = URL + "/app/personCenter/apponetopic";
    //个人主页加入的话题
    public static final String PersonJoinTopicURL = URL + "/app/personCenter/appjoinonetopic";
    //更新生词本
    public static final String AddNewWord = URL + "/newwords/addNewWord";
    public static final String DownWordsURL = URL + "/newwords/downloadWord";
    public static final String DeleteWordsURL = URL + "/newwords/deleteWord";
    public static final String updateWordStatusFromApp = URL + "/newwords/app/updateWordStatusFromApp";
    //用户协议
    public static final String PersonUserAgreementURL = URL + "/app/personCenter/getUserAgreement";
    //兑换图书
    public static final String PersonExcodeForBookURL = URL + "/app/personCenter/userExcodeForBook";
    //积分充值
    public static final String MembershipPointURL  = URL +"/app/personCenter/userRecharge";
    //退出登陆
    public static final String LogOutURL = URL + "/applogincontroller/loGout";
    //上传个人设置
    public static final String PersonUserSettingUpload = URL + "/personCenter/savePersonSetInfo";
    //读取个人设置
    public static final String PersonUserSettingLoad = URL + "/app/personCenter/getUserBasicInfo";
    //上传头像
    public static final String PersonUserHeadupLoad = URL + "/app/personCenter/uploadUserPhoto";
    //生词本-添加到生词本
    public static final String AddToNewWordURL = URL + "/newwords/app/createNewWord";
    //生词本-背单词-级别数量
    public static final String PersonReciteNewWordsDegreeInfoURL = URL + "/newwords/app/getUserReciteNewWordsDegreeInfo";
    //生词本-待复习-级别数量
    public static final String PersonReviewNewWordsDegreeInfoURL = URL + "/newwords/app/getUserReviewNewWordsDegreeInfo";

    public static final String BookMarketGetReviewByBookId = URL + "/reviews/getreviewbybookid";

    public static final String QUERY_FROM_YOUDAO_URL = "http://fanyi.youdao.com/openapi.do";
    public static final String QUERY_RROM_YOUDAO_RUL_NEW ="http://openapi.youdao.com/api";
    public static final String QUERY_RROM_YOUDAO="http://114.251.154.158:86/tongshiciku/api";
    public static final String YOUDAO_ANNOUNCE_URL ="http://dict.youdao.com/dictvoice?audio=";
    public static final String SHANBEI_ANNOUNCE_URL ="http://media.shanbay.com/audio/";

    //意见反馈备选项
    public static final String PersonSuggestionChoicesURL = URL + "/app/personCenter/getSuggestionChoices";
    //意见反馈提交(带图片)
    public static final String PersonSubmitSuggestionURL = URL + "/app/personCenter/submitAndroidSuggestionResult";
    //意见反馈提交(无图片)
    public static final String PersonSubmitSuggestionNoImageURL = URL + "/app/personCenter/submitAndroidSuggestionResultNoImage";
    //关注某人
    public static final String AttentionSomeBodyURL = URL + "/personCenter/attentionSomeBody";
    //取消关注
    public static final String CancleAttentionSomebody = URL + "/personCenter/cancleAttentionSomebody";
    public static final String HotMonthlyBookList = URL + "/monthlypayment/gethotmonthlypayment";
    public static final String MonthlyBookPaymentDetail = URL + "/monthlypayment/getapppaymentdetail";
    public static final String MonthlyBookPaymentStatus = URL + "/monthlypayment/paymentstatus";

    //版本控制
    public static final String VERSIONCONTROLLER = URL + "/AndroidVersionController/updateOrNot";
    //版本更新加积分
    public static final String UPDATEAPPADDVIRTUALCCOIN = URL + "/app/personCenter/updateAppAddVirtualCoin";


    //Android专用书签笔记
    public static final String AndroidUploadBookmark = URL + "/androidtip/settip";
    public static final String AndroidDeleteBookmark = URL + "/androidtip/deltip";
    public static final String AndroidUpdateBookmark = URL + "/androidtip/updtip";
    public static final String AndroidDownloadBookmark = URL + "/androidtip/gettip";

    //下载所有的笔记
    public static final String DOWNBOOKNOTES = URL + "/app/commentscontroller/downloadbooknotes";
    //下载所有的书签
    public static final String DownloadAllMarker = URL + "/app/commentscontroller/downloadbookmark";
    //获取私信列表
    public static final String PersonReadMessageList = URL + "/personCenter/getPersonReadMessageList";
    //已读私信
    public static final String ReadTheMessageURL = URL + "/personCenter/changeMessageStatusToKnown";
    //获取圈子关联书籍
    public static final String GROUPBOOKS = URL + "/groups/getgroupbooks";
    //修改圈子图书
    public static final String UpdateCircleBooks = URL + "/groups/updatebooks";
    //转让圈子
    public static final String TransforCircle = URL + "/found/apptransfergroup";
    //同意或者拒绝转让圈子
    public static final String AgreeOrRejectTransforCircle = URL + "/personCenter/dealWithTransferGroup";
    public static final String AgreeOrRejectJoinInCircle = URL + "/personCenter/dealWithInviteJoinGroup";
    public static final String AccountForSure = URL + "/logincontroller/accountForSure";
    //我的包月包列表
    public static final String MineMonthlyBookUrl = URL + "/monthlypayment/getappmymonthlypayment";
	public static final String GetAppMonthlyBookInfo = "/appbook/getappbookmpinfo";
    //删除书评详情页中的评论
    public static final String DiscoverDeleteComment = URL + "/reviews/delitem";
    //点击置顶话题
    public static final String Settopicstatus = URL + "/groups/settopicstatus";
    //删除话题详情页中的评论
    public static final String TopicDeleteComment = URL + "/groups/delitem";
    //举报圈子中的评论
    public static final String DiscoverReportComment = URL + "/groups/report";
//    public static final String Test;
    //取得学校列表
    public static final String GetSchools =URL+"/universitycontroller/getequaluniversitielist";

    //验证用户
    public static final String CheckUser = URL+"/activatecontroller/checkuser";
    //激活学生账号
   // public static final String AcitivateStudentAccount=URL+"/activatecontroller/accountActvHasAcount";
    public static final String AcitivateStudentAccount=URL+"/activatecontroller/accountactv";
    //设置个人爱好，完成激活
    public static final String CompleteActivite=URL+"/activatecontroller/setreadhobby";
    //学期列表
    public static final String  Semesters=URL+"/universityStatisticControoler/getsemester";
    //学生学期书单
    public static final String  StudentSemesterBooks=URL+"/requiredbook/getStudentRequiredBooks";
    //领取图书
    public static final String RequireBook=URL+"/requiredbook/userReceiveRequiredBook";
    //教师班级
    public static final String TeacherClassInfos=URL+"/universityclass/getTeacherClassInfos";
    //必读书目
    public static final String  TeacherRequiredBooks=URL+"/requiredbook/getTeacherRequiredBooks";
    //新的激活方法
    public static final String AppActivate=URL+"/appactivatecontroller/appActivate";
    // 返回有章节测试的段Id
    public static final String GetHasTestChapterId = URL + "/questionsController/getHasTestChapterId";
    // 请求某一章节的测试题
    public static final String GetQuestions = URL + "/questionsController/getQuestions";
    // 提交答案
    public static final String CommitAnswer = URL + "/questionsController/commitAnswer";
    // 获取用户做题记录
    public static final String GetUserQuestionsRecord = URL + "/questionsController/getUserQuestionsRecord";
    // 获取单个做题记录详情
    public static final String GetTestRecord = URL + "/questionsController/getTestRecord";
    // 获取用户阅读信息
    public static final String GetUserBasicReadingInfo = URL + "/app/myReadingExperience/getUserBasicReadingInfo";
    //获取用户一周的阅读数据
    public static final String GetUserBasicReadingInfoOnWeek = URL + "/app/myReadingExperience/getUserBasicReadingInfoOneWeek";
    //获取用户未读消息
    public static final String GetPersonNoReadMessage = URL + "/app/personCenter/getPersonNoReadMessageApp";
}
