#指定压缩级别
-optimizationpasses 5
#不跳过非公共的库的类成员
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
#混淆时采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#把混淆类中的方法名也混淆了
-useuniqueclassmembernames

#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification

#将文件来源重命名为“SourceFile”字符串
-renamesourcefileattribute SourceFile

#保留行号
-keepattributes SourceFile,LineNumberTable

#保持泛型
-keepattributes Signature

#保持所有实现 Serializable 接口的类成员
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#Fragment不需要在AndroidManifest.xml中注册，需要额外保护下
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

#忽略警告
-ignorewarnings
-dontwarn android.support.**
-dontwarn com.umeng.**
#shao add begin

-keepclassmembers,includedescriptorclasses class * { native <methods>; }
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {public static java.lang.String TABLENAME;}


-keepattributes Signature
-keepattributes *Annotation*
-keep class **$Properties
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn org.greenrobot.greendao.database.**
-dontwarn rx.**
#shao add end

-keep class android.support.v4.** {*; }
-keep class com.google.zxing.** {*; }

#第三方jar包，so库
-keep class org.jsoup.**{*;}

-libraryjars src/main/jniLibs/armeabi/libbdpush_V2_7.so
-libraryjars src/main/jniLibs/armeabi/libDeflatingDecompressor-v3.so
-libraryjars src/main/jniLibs/armeabi/libLineBreak-v2.so
-libraryjars src/main/jniLibs/armeabi/libNativeFormats-v4.so
-libraryjars src/main/jniLibs/armeabi/libweibosdkcore.so

-libraryjars src/main/jniLibs/armeabi-v7a/libbdpush_V2_7.so
-libraryjars src/main/jniLibs/armeabi-v7a/libDeflatingDecompressor-v3.so
-libraryjars src/main/jniLibs/armeabi-v7a/libLineBreak-v2.so
-libraryjars src/main/jniLibs/armeabi-v7a/libNativeFormats-v4.so
-libraryjars src/main/jniLibs/armeabi-v7a/libweibosdkcore.so

-libraryjars src/main/jniLibs/mips/libbdpush_V2_7.so
-libraryjars src/main/jniLibs/mips/libDeflatingDecompressor-v3.so
-libraryjars src/main/jniLibs/mips/libLineBreak-v2.so
-libraryjars src/main/jniLibs/mips/libNativeFormats-v4.so
-libraryjars src/main/jniLibs/mips/libweibosdkcore.so

-libraryjars src/main/jniLibs/x86/libbdpush_V2_7.so
-libraryjars src/main/jniLibs/x86/libDeflatingDecompressor-v3.so
-libraryjars src/main/jniLibs/x86/libLineBreak-v2.so
-libraryjars src/main/jniLibs/x86/libNativeFormats-v4.so
-libraryjars src/main/jniLibs/x86/libweibosdkcore.so

#不参与混淆的指定类
-keep public class com.iyangcong.reader.utils.HtmlParserUtils { *; }
-keep public class org.geometerplus.fbreader.formats.PluginCollection { *; }
-keep public class org.geometerplus.fbreader.formats.NativeFormatPlugin { *; }
-keep public class com.iyangcong.reader.ui.TagGroup { *; }
-keep class com.iyangcong.reader.activity.BookMarketSearchActivity$* {
      *;
}

# Alipay
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}


# OrmLite uses reflection
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }
-keepattributes *Annotation*

-keep class com.iyangcong.reader.bean.** { *; }

-keepclasseswithmembers,allowshrinking class * {
    native <methods>;
}

-keep public class * extends android.widget.LinearLayout{
    public *;
}

-keepclasseswithmembernames class * {  # 保持 native 方法不被混淆
    native <methods>;
}
-keepclasseswithmembers class * {   # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {# 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {  # 保持自定义控件类不被混淆
    public void *(android.view.View);
}
-keepclassmembers enum * {     # 保持枚举 enum 类不被混淆
 public static **[] values();
 public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable { # 保持 Parcelable 不被混淆
 public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

-keep public class com.iyangcong.reader.R$* {
     public static final int *;
}

-keep class com.google.**{*;}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#banner
-keep class com.youth.banner.** {
    *;
 }

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

# Gson
-keep class sun.misc.Unsafe { *; }
-keep class com.iyangcong.reader.model.** { *; }

#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#网络请求okGo
#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

#友盟
-dontshrink
-dontoptimize
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-keep public class javax.**
-keep public class android.webkit.**
-dontwarn android.support.v4.**
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**

-keep public class com.umeng.socialize.* {*;}



#shao add begin
-keep class com.j256.ormlite.**
-dontwarn com.j256.ormlite.**

-keep class jp.wasabeef.glide.transformations.gpu.**
-dontwarn jp.wasabeef.glide.transformations.gpu.**

-keep class com.sun.crypto.provider.**
-dontwarn com.sun.crypto.provider.**


-keep class com.akexorcist.roundcornerprogressbar.**
-dontwarn com.akexorcist.roundcornerprogressbar.**

-keep class com.alipay.android.phone.mrpc.core.**
-dontwarn com.alipay.android.phone.mrpc.core.**

-keep class yuku.ambilwarna.widget.**
-dontwarn yuku.ambilwarna.widget.**



-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**
-keep class com.umeng.socialize.handler.**
-keep class com.umeng.socialize.handler.*
-keep class com.umeng.weixin.handler.**
-keep class com.umeng.weixin.handler.*
-keep class com.umeng.qq.handler.**
-keep class com.umeng.qq.handler.*
-keep class UMMoreHandler{*;}
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
-keep class im.yixin.sdk.api.YXMessage {*;}
-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}
-keep class com.tencent.mm.sdk.** {
   *;
}
-keep class com.tencent.mm.opensdk.** {
   *;
}
-keep class com.tencent.wxop.** {
   *;
}
-keep class com.tencent.mm.sdk.** {
   *;
}
-dontwarn twitter4j.**
-keep class twitter4j.** { *; }

-keep class com.tencent.** {*;}
-dontwarn com.tencent.**
-keep class com.kakao.** {*;}
-dontwarn com.kakao.**
-keep public class com.linkedin.android.mobilesdk.R$*{
    public static final int *;
}

-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}
-keep class com.umeng.socialize.impl.ImageImpl {*;}
-keep class com.sina.** {*;}
-dontwarn com.sina.**
-keep class  com.alipay.share.sdk.** {
   *;
}

-keep class com.linkedin.** { *; }
-keep class com.android.dingtalk.share.ddsharemodule.** { *; }

#FBReader
-keep class org.** { *; }

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep class * extends java.lang.Exception

-keep class org.geometerplus.zlibrary.text.model.ZLTextPlainModel$EntryIteratorImpl { *; }
-keep class org.geometerplus.zlibrary.text.view.ZLTextParagraphCursor$Processor { *; }

-keep class org.geometerplus.zlibrary.core.library.ZLibrary
-keepclassmembers class org.geometerplus.zlibrary.core.library.ZLibrary {
    public static ** Instance();
    public ** getVersionName();
}
-keep class org.geometerplus.zlibrary.core.filesystem.ZLFile
-keepclassmembers class org.geometerplus.zlibrary.core.filesystem.ZLFile {
    public static ** createFileByPath(**);
    public ** children();
    public boolean exists();
    public boolean isDirectory();
    public ** getInputStream();
    public ** getPath();
    public long size();
}
-keep class org.geometerplus.zlibrary.core.image.ZLImage
-keep class org.geometerplus.zlibrary.core.image.ZLFileImage
-keepclassmembers class org.geometerplus.zlibrary.core.image.ZLFileImage {
		public <init>(...);
}
-keep class org.geometerplus.zlibrary.text.model.ZLTextModel
-keep class org.geometerplus.fbreader.formats.PluginCollection
-keepclassmembers class org.geometerplus.fbreader.formats.PluginCollection {
    public static ** Instance();
}
-keepclassmembers class org.geometerplus.fbreader.formats.FormatPlugin {
    public ** supportedFileType();
}
-keep class org.geometerplus.fbreader.formats.NativeFormatPlugin
-keepclassmembers class org.geometerplus.fbreader.formats.NativeFormatPlugin {
    public static ** create(**);
}
-keep class org.geometerplus.zlibrary.core.encodings.Encoding
-keepclassmembers class org.geometerplus.zlibrary.core.encodings.Encoding {
		public ** createConverter();
}
-keep class org.geometerplus.zlibrary.core.encodings.EncodingConverter
-keepclassmembers class org.geometerplus.zlibrary.core.encodings.EncodingConverter {
    public ** Name;
		public int convert(byte[],int,int,char[]);
		public void reset();
}
-keep class org.geometerplus.zlibrary.core.encodings.JavaEncodingCollection
-keepclassmembers class org.geometerplus.zlibrary.core.encodings.JavaEncodingCollection {
    public static ** Instance();
    public ** getEncoding(java.lang.String);
    public ** getEncoding(int);
		public boolean providesConverterFor(java.lang.String);
}
-keep class org.geometerplus.fbreader.Paths
-keepclassmembers class org.geometerplus.fbreader.Paths {
    public static ** cacheDirectory();
}
-keep class org.geometerplus.fbreader.book.Book
-keepclassmembers class org.geometerplus.fbreader.book.Book {
    public ** File;
    public ** getTitle();
    public ** getLanguage();
    public ** getEncodingNoDetection();
    public void setTitle(**);
    public void setSeriesInfo(**,**);
    public void setLanguage(**);
    public void setEncoding(**);
    public void addAuthor(**,**);
    public void addTag(**);
    public void addUid(**);
}
-keep class org.geometerplus.fbreader.book.Tag
-keepclassmembers class org.geometerplus.fbreader.book.Tag {
    public static ** getTag(**,**);
}
-keepclassmembers class org.geometerplus.fbreader.bookmodel.BookModelImpl {
		public void addImage(**,**);
}
-keep class org.geometerplus.fbreader.bookmodel.NativeBookModel
-keepclassmembers class org.geometerplus.fbreader.bookmodel.NativeBookModel {
		public ** Book;
		public void initInternalHyperlinks(**,**,int);
		public void addTOCItem(**,int);
		public void leaveTOCItem();
		public ** createTextModel(**,**,int,int[],int[],int[],int[],byte[],**,**,int);
		public void setBookTextModel(**);
		public void setFootnoteModel(**);
}
-keepclassmembers class org.geometerplus.fbreader.bookmodel.BookReadingException {
    public static void throwForFile(**,**);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);

}


### greenDAO 3
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties

# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
# If you do not use RxJava:
-dontwarn rx.*

#EvnetBus
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

#播放器
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**

##新浪微博
-keep class com.sina.weibo.sdk.** { *; }
}

##新版视频播放器
-keep public class cn.jzvd.JZMediaSystem {*; }
-keep public class cn.jzvd.demo.CustomMedia.CustomMedia {*; }
-keep public class cn.jzvd.demo.CustomMedia.JZMediaIjk {*; }
-keep public class cn.jzvd.demo.CustomMedia.JZMediaSystemAssertFolder {*; }

-keep class tv.danmaku.ijk.media.player.** {*; }
-dontwarn tv.danmaku.ijk.media.player.*
-keep interface tv.danmaku.ijk.media.player.** { *; }

##新版画中画视频播放器
-keep class com.dueeeke.videoplayer.** { *; }
-dontwarn com.dueeeke.videoplayer.**

# IjkPlayer
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**

# ExoPlayer
-keep class com.google.android.exoplayer2.** { *; }
-dontwarn com.google.android.exoplayer2.**

-dontwarn com.yanzhenjie.permission.**

