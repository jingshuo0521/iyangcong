package com.iyangcong.reader.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.utils.SharedPreferenceUtil;


public class MyDialog extends Dialog {


//    private View mMenuView;
    private LinearLayout localShelf;
    private LinearLayout allShelf;
    private LinearLayout noProgerss;
    private ImageView imgLocal,imgCloud;
    private TextView tvLocal,tvCloud;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private Activity context;
    public MyDialog(final Activity context, View.OnClickListener itemsOnClick,int style) {
        super(context,style);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance();

        View mMenuView = inflater.inflate(R.layout.menu_shelf, null);
        localShelf = mMenuView.findViewById(R.id.only_localshelf);
        allShelf = mMenuView.findViewById(R.id.all_shelf);
        noProgerss = mMenuView.findViewById(R.id.shelf_setting);
        imgLocal = mMenuView.findViewById(R.id.localicon);
        imgCloud = mMenuView.findViewById(R.id.cloudicon);
        tvLocal = mMenuView.findViewById(R.id.localtext);
        tvCloud = mMenuView.findViewById(R.id.cloudtext);

        this.setCancelable(true);
        // 获得窗体对象
        Window window = this.getWindow();
        // 设置窗体的对齐方式
        window.setGravity(Gravity.RIGHT|Gravity.TOP);
        // 设置窗体动画
//        window.setWindowAnimations(R.style.AnimBottom);
        // 设置窗体的padding
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.x = 8; // 新位置X坐标
        lp.y = 88; // 新位置Y坐标
        window.setAttributes(lp);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        this.setContentView(mMenuView);
        //隐藏阴影
//        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        //设置SelectPicPopupWindow弹出窗体的宽
//        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
//        //设置SelectPicPopupWindow弹出窗体的高
//        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//        //设置SelectPicPopupWindow弹出窗体可点击
//        ColorDrawable dw = new ColorDrawable(0x80000000);
//        //设置SelectPicPopupWindow弹出窗体的背景
//        this.setBackgroundDrawable(dw);
//        this.setFocusable(true);
//        this.setOnDismissListener(new PopupWindow.OnDismissListener() {
//
//            @Override
//            public void onDismiss() {
//
//                backgroundAlpha(1f);
//            }
//        });
        boolean isLocalShelf = sharedPreferenceUtil.getBoolean(SharedPreferenceUtil.CLOUD_OR_LOCAL,false);
        if(isLocalShelf){
            imgLocal.setImageResource(R.drawable.local_shelf_hover);
            imgCloud.setImageResource(R.drawable.cloud_shelf);
            tvLocal.setTextColor(context.getResources().getColor(R.color.main_color));
            tvCloud.setTextColor(context.getResources().getColor(R.color.black));
        }else{
            imgLocal.setImageResource(R.drawable.local_shelf);
            imgCloud.setImageResource(R.drawable.cloud_shelf_hover);
            tvCloud.setTextColor(context.getResources().getColor(R.color.main_color));
            tvLocal.setTextColor(context.getResources().getColor(R.color.black));
        }
        localShelf.setOnClickListener(itemsOnClick);
        allShelf.setOnClickListener(itemsOnClick);
        noProgerss.setOnClickListener(itemsOnClick);
    }

    @Override
    public void show() {
        super.show();
    }

    //    public void backgroundAlpha(float bgAlpha) {
//        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
//        lp.alpha = bgAlpha; //0.0-1.0
//        context.getWindow().setAttributes(lp);
//    }

}
