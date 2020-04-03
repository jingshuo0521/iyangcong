package com.iyangcong.reader.app;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.VersionInfo;
import com.iyangcong.reader.callback.JsonCallback;
import com.iyangcong.reader.interfaceset.DeviceType;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.utils.CommonUtil;
import com.iyangcong.reader.utils.FileHelper;
import com.iyangcong.reader.utils.Urls;
import com.lzy.okgo.OkGo;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import me.drakeet.support.toast.ToastCompat;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 应用程序更新工具包
 *
 * @author liux (http://my.oschina.net/liux)
 * @version 1.1
 * @created 2012-6-29
 */
public class UpdateManager {

    private static final int DOWN_NOSDCARD = 0;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private static final int DIALOG_TYPE_LATEST = 0;
    private static final int DIALOG_TYPE_FAIL = 1;
    private boolean isInner = false;

    // 是否强制更新
    private boolean mustUpdate;

    private static UpdateManager updateManager;

    private Context mContext;
    // 通知对话框
//    private AlertDialog noticeDialog;
    // 下载对话框
    private AlertDialog downloadDialog;
    // '已经是最新' 或者 '无法获取最新版本' 的对话框
    private Dialog latestOrFailDialog;
    // 进度条
    private ProgressBar mProgress;
    // 显示下载数值
    private TextView mProgressText;
    // 查询动画
    private ProgressDialog mProDialog;
    // 进度值
    private int progress;
    // 下载线程
    private Thread downLoadThread;
    // 终止标记
    private boolean interceptFlag;
    // 返回的安装包url
    private String apkUrl = "";
    // 下载包保存路径
    private String savePath = "";
    // apk保存完整路径
    private String apkFilePath = "";
    // 临时下载文件路径
    private String tmpFilePath = "";
    // 下载文件大小
    private String apkFileSize;
    // 已下载文件大小
    private String tmpFileSize;

    private String mDeviceToken;

    private String curVersionName = "";
    private int curVersionCode;
    private VersionInfo mUpdate;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    mProgressText.setText(tmpFileSize + "/" + apkFileSize);
                    break;
                case DOWN_OVER:
                    downloadDialog.dismiss();
                    updateAppAddVirtualCoin();
                    installApk();
                    break;
                case DOWN_NOSDCARD:
                    downloadDialog.dismiss();
                    // UIHelper.showFriendlyMsg(mContext, "无法下载安装文件，请检查SD卡是否挂载");
                    ToastCompat.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        ;
    };

    private void updateAppAddVirtualCoin(){
        OkGo.get(Urls.UPDATEAPPADDVIRTUALCCOIN)
                .params("userId", CommonUtil.getUserId())
                .execute(new JsonCallback<IycResponse<String>>(mContext) {
                    @Override
                    public void onSuccess(IycResponse<String> iycResponse, Call call, Response response) {
                        ToastCompat.makeText(mContext, "更新成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastCompat.makeText(mContext, "更新失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static UpdateManager getUpdateManager() {
        if (updateManager == null) {
            updateManager = new UpdateManager();
        }
        updateManager.interceptFlag = false;
        return updateManager;
    }

    /**
     * 检查App更新
     *
     * @param context
     * @param isShowMsg 是否显示提示消息
     */
    public void checkAppUpdate(Context context, final boolean isShowMsg,
                               String deviceToken, final boolean manual) {
        this.mContext = context;
        mDeviceToken = deviceToken;
        getCurrentVersion();
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                // 进度条对话框不显示 - 检测结果也不显示
                if (mProDialog != null && !mProDialog.isShowing()) {
                    return;
                }
                // 关闭并释放释放进度条对话框
                if (isShowMsg && mProDialog != null) {
                    mProDialog.dismiss();
                    mProDialog = null;
                }
                // 显示检测结果
                if (msg.what == 1) {
                    mUpdate = (VersionInfo) msg.obj;
                    if (mUpdate != null) {
                        try {
                            int minV = Integer.parseInt(arrayToString(mUpdate.getMinVersionName().split("\\.")));
                            int lastV = Integer.parseInt(arrayToString(mUpdate
                                    .getLastVersionName().split("\\.")));
                            int curV = Integer
                                    .parseInt(arrayToString(curVersionName
                                            .split("\\.")));
                            if (curV < lastV) {
                                mustUpdate = false;
                                if (curV < minV)
                                    mustUpdate = true;
                                apkUrl = mUpdate.getAppUrl();
                                showNoticeDialog(mUpdate.getVersionDescription(),mUpdate.getLastVersionName());
                            } else {
                                if (manual) {
                                    ToastCompat.makeText(mContext, "您的版本已经是最新的", Toast.LENGTH_SHORT).show();
                                }
                                String path = CommonUtil.getRootFilePath()
                                        + mContext.getPackageName() + "/app_apks";
                                File file = new File(path);
                                if (file.exists()) {
                                    FileHelper.deleteDirectory(file.getPath());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    if (isShowMsg) {
                        showLatestOrFailDialog(DIALOG_TYPE_FAIL);
                    }
                }
            }
        };
        new Thread() {
            public void run() {
                Message msg = handler.obtainMessage();
                try {
                    VersionInfo info = new VersionInfo();

                    HttpClient client = null;
                    HttpPost request = null;
                    HttpResponse response = null;
                    JSONObject json = new JSONObject();
                    client = new DefaultHttpClient();
                    json.put("deviceType", DeviceType.ANDROID_3);
                    json.put("deviceToken", mDeviceToken);
                    request = new HttpPost(Urls.VERSIONCONTROLLER);
                    request.setEntity(new StringEntity(json.toString(),
                            HTTP.UTF_8));
                    response = client.execute(request);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        JSONObject object = new JSONObject(
                                EntityUtils.toString(response.getEntity()));
                        // 判断errorCode
                        int errorCode = object.getInt("errorCode");
                        if (errorCode == 0) {
                            //Log.d("UpdateManager","getVersion : " + object.toString());
                            String lastVersion = object
                                    .getString("theLastVersion");
                            String minVersion = object
                                    .getString("minimumVersion");
                            String apkURL = object.getString("dowloadUrl");
                            String descriptionString = object
                                    .getString("descriptionInfo");
                            getCurrentVersion();
                            info.setLastVersionName(lastVersion);
                            info.setMinVersionName(minVersion);
                            info.setAppUrl(apkURL);
                            info.setVersionDescription(descriptionString);
                        } else {
                            Looper.prepare();
                            ToastCompat.makeText(mContext, "获取最新版本失败", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                    msg.what = 1;
                    msg.obj = info;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 显示'已经是最新'或者'无法获取版本信息'对话框
     */
    private void showLatestOrFailDialog(int dialogType) {
        if (latestOrFailDialog != null) {
            // 关闭并释放之前的对话框
            latestOrFailDialog.dismiss();
            latestOrFailDialog = null;
        }
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle("系统提示");
        if (dialogType == DIALOG_TYPE_FAIL) {
            builder.setMessage("无法获取版本更新信息");
        }
        builder.setPositiveButton("确定", null);
        latestOrFailDialog = builder.create();
        latestOrFailDialog.show();
    }

    /**
     * 获取当前客户端版本信息
     */
    private void getCurrentVersion() {
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0);
            curVersionName = info.versionName;
            curVersionCode = info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * 显示版本更新通知对话框
     */
    private void showNoticeDialog(String versionDescription,String versionNum) {
        final Dialog updateDialog = new Dialog(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mMenuView = inflater.inflate(R.layout.updat_bacground, null);
        TextView updateText = mMenuView.findViewById(R.id.update_text);
        TextView update = mMenuView.findViewById(R.id.update);
        TextView cancle = mMenuView.findViewById(R.id.cancle);
        TextView version = mMenuView.findViewById(R.id.version);
        updateDialog.setCancelable(true);
        // 获得窗体对象
        Window window = updateDialog.getWindow();
//        // 设置窗体的对齐方式
        window.setGravity(Gravity.CENTER);
        // 设置窗体动画
//        window.setWindowAnimations(R.style.AnimBottom);
        // 设置窗体的padding
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.x = 8; // 新位置X坐标
//        lp.y = 88; // 新位置Y坐标
        window.setAttributes(lp);
        updateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        updateDialog.setContentView(mMenuView);
        updateText.setText(versionDescription.replace("\\n","\n"));
        version.setText("V "+versionNum);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDialog.dismiss();
                showDownloadDialog();
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDialog.dismiss();
            }
        });
        updateDialog.show();
        if (mustUpdate) {
            updateDialog.setCancelable(false);
            cancle.setEnabled(false);
        }
    }

    /**
     * 显示下载对话框
     */
    private void showDownloadDialog() {
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle("正在下载新版本");

        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.update_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        mProgressText = (TextView) v.findViewById(R.id.update_progress_text);

        builder.setView(v);
        if (mustUpdate) {
            builder.setNegativeButton("取消", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    int nPid = android.os.Process.myPid();
                    android.os.Process.killProcess(nPid);
                }
            });
        } else {
            builder.setNegativeButton("取消", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    interceptFlag = true;
                }
            });
        }
        builder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                interceptFlag = true;
            }
        });
        builder.setCancelable(false);
        downloadDialog = builder.create();
        downloadDialog.setCanceledOnTouchOutside(false);
        downloadDialog.show();
        downloadApk();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                String apkName = "IYangCongReader_"
                        + mUpdate.getLastVersionName() + ".apk";
                String tmpApk = "IYangCongReader_"
                        + mUpdate.getLastVersionName() + ".tmp";
                // 判断是否挂载了SD卡
                String storageState = Environment.getExternalStorageState();
                if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                    savePath = Environment.getExternalStorageDirectory()
                            .getPath() + "/iyangcong/update/";
                    File file = new File(savePath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    apkFilePath = savePath + apkName;
                    tmpFilePath = savePath + tmpApk;
                } else {
                    String filePath = "/mnt/";
                    File folder = new File(filePath);
                    if (folder.exists()) {
                        String[] allFiles = folder.list();
                        for (int i = 0; i < allFiles.length; i++) {
                            filePath = filePath + allFiles[i] + "/";
                            File subFolder = new File(filePath);
                            if (subFolder.exists()) {
                                String temppath = filePath
                                        + "/iyangcong/update/";
                                File file = new File(temppath);
                                if (!file.exists()) {
                                    if (file.mkdirs()) {
                                        savePath = temppath;
                                        apkFilePath = savePath + apkName;
                                        tmpFilePath = savePath + tmpApk;
                                        break;
                                    } else {
                                        continue;
                                    }
                                } else {
                                    savePath = temppath;
                                    apkFilePath = savePath + apkName;
                                    tmpFilePath = savePath + tmpApk;
                                    break;
                                }
                            }
                        }
                    }
                }

                // 没有挂载SD卡，无法下载文件
                if (apkFilePath == null || apkFilePath == "") {
                    isInner = true;
                    File file = mContext.getDir("apks", Context.MODE_PRIVATE
                            | Context.MODE_WORLD_READABLE
                            | Context.MODE_WORLD_WRITEABLE);
                    savePath = file.getPath();
                    apkFilePath = savePath + "/" + apkName;
                    tmpFilePath = savePath + "/" + tmpApk;
                    // mHandler.sendEmptyMessage(DOWN_NOSDCARD);
                    // return;
                }
                File ApkFile = new File(apkFilePath);

                // 输出临时下载文件
                File tmpFile = new File(tmpFilePath);

                FileOutputStream fos = new FileOutputStream(tmpFile);
                URL url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.connect();
                int length = conn.getContentLength();
                // 是否已下载更新文件
                if (ApkFile.exists() && (ApkFile.length() == length)) {
                    downloadDialog.dismiss();
                    installApk();
                    updateAppAddVirtualCoin();
                    return;
                }
                InputStream is = conn.getInputStream();
                // 显示文件大小格式：2个小数点显示
                DecimalFormat df = new DecimalFormat("0.00");
                // 进度条下面显示的总文件大小
                apkFileSize = df.format((float) length / 1024 / 1024) + "MB";
                int count = 0;
                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    count += numread;
                    // 进度条下面显示的当前下载文件大小
                    tmpFileSize = df.format((float) count / 1024 / 1024) + "MB";
                    // 当前进度值
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成 - 将临时下载文件转成APK文件
                        if (isInner) {
                            String commad[] = {"chmod", "777", tmpFilePath};
                            ProcessBuilder builder = new ProcessBuilder(commad);
                            try {
                                builder.start();
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                mHandler.sendEmptyMessage(DOWN_NOSDCARD);
                                return;
                            }
                        }
                        if (tmpFile.renameTo(ApkFile)) {
                            //tmpFile.delete();
                            if (isInner) {
                                String commad2[] = {"chmod", "777",
                                        apkFilePath};
                                ProcessBuilder builder2 = new ProcessBuilder(
                                        commad2);
                                try {
                                    builder2.start();
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                    mHandler.sendEmptyMessage(DOWN_NOSDCARD);
                                    return;
                                }
                            }
                            // 通知安装
                            mHandler.sendEmptyMessage(DOWN_OVER);
                        }
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消就停止下载

                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    /**
     * 下载apk
     */
    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

//    /**
//     * 安装apk
//     */
//    private void installApk() {
//        File apkfile = new File(apkFilePath);
//        if (!apkfile.exists()) {
//            return;
//        }
//        Intent i = new Intent(Intent.ACTION_VIEW);
//        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
//                "application/vnd.android.package-archive");
//        mContext.startActivity(i);
//    }



    private void installApk() {
        // TODO Auto-generated method stub
        /*********下载完成，点击安装***********/
        File apkfile = new File(apkFilePath);
        if (!apkfile.exists()) {
            return;
        }
        Uri uri = Uri.fromFile(apkfile);
        Intent intent = new Intent(Intent.ACTION_VIEW);

        /**********加这个属性是因为使用Context的startActivity方法的话，就需要开启一个新的task**********/
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

    /**
     * String[]转String
     *
     * @param args
     * @return
     */
    private String arrayToString(String[] args) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            builder.append(args[i]);
        }
        return builder.toString();
    }
}
