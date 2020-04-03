
package com.iyangcong.reader.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyangcong.reader.R;
import com.iyangcong.reader.ui.IYangCongToast;

import me.drakeet.support.toast.ToastCompat;


public class UIHelper {

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getScreenWidthDip(Context context) {
        int widthPx = UIHelper.getScreenWidth(context);
        int widthDip = UIHelper.px2dip(context, widthPx);
        return widthDip;
    }

    public static int getScreenHeightDip(Context context) {
        int heightPx = UIHelper.getScreenHeight(context);
        int heightDip = UIHelper.px2dip(context, heightPx);
        return heightDip;
    }

    /**
     * 获取设备密度
     *
     * @param context
     * @return
     */
    public static float getDensity(Context context) {
        if (context != null) {
            return context.getResources().getDisplayMetrics().density;
        } else {
            return 1.0f;
        }
    }

    /**
     * 获取设备密度DPI
     *
     * @param context
     * @return
     */
    public static int getDensityDpi(Context context) {
        if (context != null) {
            return context.getResources().getDisplayMetrics().densityDpi;
        } else {
            return 120;
        }
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        if (context != null) {
            return context.getResources().getDisplayMetrics().widthPixels;
        } else {
            return 0;
        }
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        if (context != null) {
            return context.getResources().getDisplayMetrics().heightPixels;
        } else {
            return 0;
        }
    }

    public static void showFriendlyMsg(Context context, String msg, boolean isLengthLong) {
        ToastCompat.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showFriendlyMsg(Context context, String msg, int showTime) {
        ToastCompat.makeText(context, msg, showTime);
    }

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @return
     */
    public static Dialog createLoadingDialog(Context context, String message) {
        View v = View.inflate(context, R.layout.waiting_loading_view, null);// 得到加载view
        TextView tv_msg = (TextView) v.findViewById(R.id.tv_msg_loading_dialog);
        tv_msg.setText(message);
        Dialog loadingDialog = new Dialog(context, R.style.waiting_dialog_style);// 创建自定义样式dialog
        loadingDialog.setCancelable(true);
        float a = (float) 25 / 72;
        float b = (float) 20 / 128;
        int w = (int) (getScreenWidth(context) * a);
        int h = (int) (getScreenHeight(context) * b);
        loadingDialog.setContentView(v, new LinearLayout.LayoutParams(w, h));
        return loadingDialog;
    }

    /*
     * 仅有一个确定按纽的dialog
     * */
    public static AlertDialog createAlertDialog(Context context, String title, String message,
                                                String btn1_text, final AlertDialogIntent intent1) {
        final AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(context,
                R.style.alert_dialog_style)).create();
        dialog.show();
        Window window = dialog.getWindow();
        View view = LayoutInflater.from(context).inflate(R.layout.dlg_alert2, null);
        int width = getScreenWidth(context);
        int height = getScreenHeight(context);
        float a = (float) 62 / 72;
        float b = (float) 35 / 128;
        int w = (int) (width * a);
        int h = (int) (height * b);
        window.setContentView(view, new LinearLayout.LayoutParams(w, h));
        Button button1 = (Button) view.findViewById(R.id.dlg_btn_postive2);
        TextView tv_title = (TextView) view.findViewById(R.id.dlg_tv_title2);
        TextView tv_message = (TextView) view.findViewById(R.id.dlg_tv_message2);
        tv_title.setText(title);
        tv_message.setText(message);
        button1.setText(btn1_text);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent1 != null) {
                    intent1.onIntent(dialog);
                }
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public static AlertDialog createAlertDialog(Context context, String title, String message,
                                                String btn1_text, String btn2_text, final AlertDialogIntent intent1,
                                                final AlertDialogIntent intent2) {
        final AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(context,
                R.style.alert_dialog_style)).create();
        dialog.show();
        Window window = dialog.getWindow();
        View view = LayoutInflater.from(context).inflate(R.layout.dlg_alert, null);
        int width = getScreenWidth(context);
        int height = getScreenHeight(context);
        float a = (float) 62 / 72;
        float b = (float) 35 / 128;
        int w = (int) (width * a);
        int h = (int) (height * b);
        window.setContentView(view, new LinearLayout.LayoutParams(w, h));
        Button button1 = (Button) view.findViewById(R.id.dlg_btn_postive);
        Button button2 = (Button) view.findViewById(R.id.dlg_btn_ne);
        TextView tv_title = (TextView) view.findViewById(R.id.dlg_tv_title);
        TextView tv_message = (TextView) view.findViewById(R.id.dlg_tv_message);
        tv_title.setText(title);
        tv_message.setText(message);
        button1.setText(btn1_text);
        button2.setText(btn2_text);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent1 != null) {
                    intent1.onIntent(dialog);
                }
                dialog.dismiss();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent2 != null) {
                    intent2.onIntent(dialog);
                }
                dialog.dismiss();
            }
        });
        return dialog;
    }

    /**
     * 判断是否开启了自动亮度调节
     *
     * @param aContentResolver
     * @return
     */
    public static boolean isAutoBrightness(ContentResolver aContentResolver) {
        boolean automicBrightness = false;
        try {
            automicBrightness = Settings.System.getInt(aContentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }

    /**
     * 获取屏幕的亮度
     *
     * @param activity
     * @return
     */
    public static int getScreenBrightness(Activity activity) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try {
            nowBrightnessValue = Settings.System.getInt(resolver,
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    /**
     * 设置亮度
     *
     * @param activity
     * @param brightness
     */
    public static void setBrightness(Activity activity, int brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 停止自动亮度调节
     *
     * @param context
     */
    public static void stopAutoBrightness(Context context) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    /**
     * 开启亮度自动调节
     *
     * @param context
     */
    public static void startAutoBrightness(Context context) {
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    /**
     * 保存亮度设置状态
     *
     * @param resolver
     * @param brightness
     */
    public static void saveBrightness(ContentResolver resolver, int brightness) {
        Uri uri = Settings.System.getUriFor("screen_brightness");
        Settings.System.putInt(resolver, "screen_brightness", brightness);
        resolver.notifyChange(uri, null);
    }

    /**
     * 网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] infos = connectivity.getAllNetworkInfo();
            if (infos != null)
                for (int i = 0; i < infos.length; i++)
                    if (infos[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public static String getReadVision(String readVersion) {
        String str = "";
        if (!TextUtils.isEmpty(readVersion)) {
            if (readVersion.contains("1") && readVersion.contains("2") && readVersion.contains("3")) {
                str = "可读版本：中/英/中英";
            } else if (readVersion.contains("1")) {
                str = "可读版本：中";
            } else if (readVersion.contains("2")) {
                str = "可读版本：英";
            }
        }
        return str;
    }



    public static void exitApplication(final Context context) {
//        AppManager appManager = AppManager.getAppManager();
//        appManager.AppExit(context);
        /*UIHelper.createAlertDialog(context, "提醒", "确认退出爱洋葱阅读？", "确定", "取消",
                new UIHelper.AlertDialogIntent() {
                    @Override
                    public void onIntent(AlertDialog dialog) {
                        dialog.dismiss();
                        AppManager appManager = AppManager.getAppManager();
                        appManager.AppExit(context);
                    }
                }, new UIHelper.AlertDialogIntent() {
                    @Override
                    public void onIntent(AlertDialog dialog) {
                        dialog.dismiss();
                    }
                });*/
    }

    public interface AlertDialogIntent {
        void onIntent(AlertDialog dialog);
    }


}
