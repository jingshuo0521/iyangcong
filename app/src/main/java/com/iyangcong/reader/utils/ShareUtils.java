package com.iyangcong.reader.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.activity.BookMarketBookDetailsActivity;
import com.orhanobut.logger.Logger;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MultiImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WuZepeng on 2017-04-28.
 */

public class ShareUtils {
    private ShareAction mShareAction;
    private Activity mActivity;
    private UMShareListener mShareListener;
    private WeiBoShareListener mWeiBoShareListener;
    private WbShareHandler mWbShareHandler;
    private HashMap<String, String> contentMap;
    //分享类型：话题
    public static final String TOPIC_SHARE = "topic";
    //分享类型：图书
    public static final String BOOK_SHARE = "book";
    //分享类型：书评
    public static final String BOOKCOMMENT_SHAKE = "bookcommnet";
    //分享类型：读后感
    public static final String REVIEW_SHARE = "review";
    //分享类型：阅历
    public static final String MINE_EXPERIENCE_SHARE = "yueli";
    public static final String PICTURE_URL = "picture_url";

    public static final String CONTENT_KEY = "contentkey";
    public static final String TITLE_KEY = "titlekey";
    public static final String URLS_KEY = "urls_key";
    private static final String SINA = "com.sina.weibo";
    private static final String DOUBAN = "com.douban.frodo";

    //	final Intent intent;
    public ShareUtils(final Activity activity, HashMap<String, String> sharedContent, final String sharedType) {
        this.mActivity = activity;
        contentMap = sharedContent;
        contentMap.put(TOPIC_SHARE, "话题分享");
        contentMap.put(BOOK_SHARE, "图书分享");
        contentMap.put(BOOKCOMMENT_SHAKE, "书评分享");
        contentMap.put(REVIEW_SHARE, "读后感分享");
        contentMap.put(MINE_EXPERIENCE_SHARE, "阅历分享");
//		intent = new Intent(Intent.ACTION_SEND);
//		intent.setType("text/plain");
//		intent.putExtra(Intent.EXTRA_TEXT, "#爱洋葱阅读# "+ getDescription(sharedType,sharedContent));
        mShareListener = new CustomShareListener(activity);

        mShareAction = new ShareAction(activity).setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.SINA,SHARE_MEDIA.QZONE)
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        Logger.i("hahahahahah mShowWord: " + snsPlatform.mShowWord);
                        UMWeb web = new UMWeb(contentMap.get(URLS_KEY));
                        if (snsPlatform.mIcon.equals("umeng_socialize_wechat")) {
                            web.setTitle(getTitle(sharedType, contentMap));
                            web.setDescription(getDescription(sharedType, contentMap));
                            if (contentMap.get(PICTURE_URL) != null) {
                                web.setThumb(new UMImage(activity, contentMap.get(PICTURE_URL)));
                            } else {
                                web.setThumb(new UMImage(activity, R.drawable.ic_launcher));
                            }
                        }
                        if (snsPlatform.mIcon.equals("umeng_socialize_sina")) {
//                            mWbShareHandler = new WbShareHandler(activity);
//                            mWeiBoShareListener = new WeiBoShareListener(activity);
//                            mWbShareHandler.registerApp();
//                            WeiboMultiMessage tmpWeiboMultiMessage = new WeiboMultiMessage();
//                            TextObject tmpTextObject = new TextObject();
//                            tmpTextObject.text = "#爱洋葱阅读# "+getTitle(sharedType,contentMap)
//                                    + getDescription(sharedType,contentMap)+" "+contentMap.get(URLS_KEY);
//                            tmpWeiboMultiMessage.textObject  = tmpTextObject;
//                            if(contentMap.get(PICTURE_URL)!=null){
//                                ArrayList<Uri> pathList = new ArrayList<>();
//                                String url = contentMap.get(PICTURE_URL);
//                                Logger.e("wzp share " + url);
//                                pathList.add(Uri.parse(url));
//                                tmpWeiboMultiMessage.imageObject = new ImageObject();
//                                tmpWeiboMultiMessage.imageObject.imagePath = url;
//                            }
//                            mWbShareHandler.shareMessage(tmpWeiboMultiMessage,false);
//                            return;
                            web.setTitle("#爱洋葱阅读# " + getTitle(sharedType, contentMap));
                            web.setDescription("#爱洋葱阅读# " + getDescription(sharedType, contentMap));
                            if (contentMap.get(PICTURE_URL) != null) {
                                String url = contentMap.get(PICTURE_URL);
                                Logger.e("wzp share " + url);
                                File tmpFile = new File(url);
                                if(tmpFile.exists())
                                    web.setThumb(new UMImage(activity,tmpFile));
                                else
                                     web.setThumb(new UMImage(activity, contentMap.get(PICTURE_URL)));
                            } else {
                                web.setThumb(new UMImage(activity, R.drawable.ic_launcher));
                            }
                        }
                        if (snsPlatform.mIcon.equals("umeng_socialize_wxcircle")) {
                            web.setTitle(getTitle(sharedType, contentMap) + "  " + getDescription(sharedType, contentMap));
                            if (contentMap.get(PICTURE_URL) != null) {
                                web.setThumb(new UMImage(activity, contentMap.get(PICTURE_URL)));
                            } else {
                                web.setThumb(new UMImage(activity, R.drawable.ic_launcher));
                            }
                        }
                        if (snsPlatform.mIcon.equals("umeng_socialize_qq")) {
                            web.setTitle(getTitle(sharedType, contentMap));
                            web.setDescription(getDescription(sharedType, contentMap));
                            if (contentMap.get(PICTURE_URL) != null) {
                                web.setThumb(new UMImage(activity, contentMap.get(PICTURE_URL)));
                            } else {
                                web.setThumb(new UMImage(activity, R.drawable.ic_launcher));
                            }
                        }
                        if(snsPlatform.mIcon.equals("umeng_socialize_qzone")){
                            web.setTitle(getTitle(sharedType, contentMap));
                            web.setDescription(getDescription(sharedType, contentMap));
                            if (contentMap.get(PICTURE_URL) != null) {
                                web.setThumb(new UMImage(activity, contentMap.get(PICTURE_URL)));
                            } else {
                                web.setThumb(new UMImage(activity, R.drawable.ic_launcher));
                            }
                        }
                        new ShareAction(activity)
                                .withMedia(web)
                                .setPlatform(share_media)
                                .setCallback(mShareListener)
                                .share();
                    }
                });
    }

    public void addImagUrl(String imageUrl) {
        contentMap.put(PICTURE_URL, imageUrl);
    }

    public void addBookName(String bookName){
        contentMap.put(ShareUtils.TITLE_KEY,bookName);
    }

    private String getDescription(String type, HashMap<String, String> map) {
        StringBuilder sb = new StringBuilder("");
        if (type.equals(TOPIC_SHARE)) {
            sb.append("我发现了“").append(map.get(CONTENT_KEY)).append("”,一起来讨论吧！");
        } else if (type.equals(BOOK_SHARE)) {
            sb.append("我正在读#").append(map.get(CONTENT_KEY)).append("#,一起来读吧。");
        } else if (type.equals(BOOKCOMMENT_SHAKE)) {
            sb.append(map.get(CONTENT_KEY)).append("#").append(map.get(TITLE_KEY)).append("#");
        } else if (type.equals(REVIEW_SHARE)) {
            sb.append(map.get(CONTENT_KEY)).append("#").append(map.get(TITLE_KEY)).append("#");
        } else if (type.equals(MINE_EXPERIENCE_SHARE)) {
            sb.append("我正在使用爱洋葱阅读。阅读，写作，以书为友，以书会友，人生这般，不亦乐乎！");
        }
        return sb.toString();
    }

    public String getTitle(String type, HashMap<String, String> map) {
        if (type.equals(TOPIC_SHARE))
            return map.get(TOPIC_SHARE);
        else if (type.equals(BOOK_SHARE))
            return map.get(BOOK_SHARE);
        else if (type.equals(BOOKCOMMENT_SHAKE))
            return map.get(BOOKCOMMENT_SHAKE);
        else if (type.equals(REVIEW_SHARE))
            return map.get(REVIEW_SHARE);
        else if (type.equals(MINE_EXPERIENCE_SHARE))
            return map.get(MINE_EXPERIENCE_SHARE);
        return "";
    }

    public void open() {
        if (mShareAction != null)
            mShareAction.open();
//		if(intent != null)
//			mActivity.startActivity(Intent.createChooser(intent, null));
    }

    private static class CustomShareListener implements UMShareListener {

        private WeakReference<BookMarketBookDetailsActivity> mActivity;

        private CustomShareListener(Activity activity) {
            mActivity = new WeakReference(activity);
        }

        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {

            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(mActivity.get(), platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                        && platform != SHARE_MEDIA.EMAIL
                        && platform != SHARE_MEDIA.FLICKR
                        && platform != SHARE_MEDIA.FOURSQUARE
                        && platform != SHARE_MEDIA.TUMBLR
                        && platform != SHARE_MEDIA.POCKET
                        && platform != SHARE_MEDIA.PINTEREST
                        && platform != SHARE_MEDIA.INSTAGRAM
                        && platform != SHARE_MEDIA.GOOGLEPLUS
                        && platform != SHARE_MEDIA.YNOTE
                        && platform != SHARE_MEDIA.EVERNOTE) {
                    Toast.makeText(mActivity.get(), platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
                }

            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                    && platform != SHARE_MEDIA.EMAIL
                    && platform != SHARE_MEDIA.FLICKR
                    && platform != SHARE_MEDIA.FOURSQUARE
                    && platform != SHARE_MEDIA.TUMBLR
                    && platform != SHARE_MEDIA.POCKET
                    && platform != SHARE_MEDIA.PINTEREST
                    && platform != SHARE_MEDIA.INSTAGRAM
                    && platform != SHARE_MEDIA.GOOGLEPLUS
                    && platform != SHARE_MEDIA.YNOTE
                    && platform != SHARE_MEDIA.EVERNOTE) {
                Toast.makeText(mActivity.get(), platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
                if (t != null) {
                    Logger.d("throw", "throw:" + t.getMessage());
                }
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(mActivity.get(), platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    }

    private class DoubanListener implements UMAuthListener {

        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            Toast.makeText(mActivity, "Authorize succeed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            Toast.makeText(mActivity, "Authorize fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            Toast.makeText(mActivity, "Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    }

    private class WeiBoShareListener implements WbShareCallback{
        Context mContext;

        public WeiBoShareListener(Context context) {
            mContext = context;
        }

        @Override
        public void onWbShareSuccess() {
            Toast.makeText(mContext,"分享成功",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onWbShareCancel() {
            Toast.makeText(mContext,"取消分享",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onWbShareFail() {
            Toast.makeText(mContext,"分享失败",Toast.LENGTH_SHORT).show();
        }
    }
}
