package com.iyangcong.reader.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.flyco.dialog.widget.base.TopBaseDialog;
import com.iyangcong.reader.R;
import com.iyangcong.reader.interfaceset.OnItemClickedListener;
import com.iyangcong.reader.interfaceset.OnAddGroupClickedListener;

/**
 * Created by lg on 2017/1/20.
 */

public class CircleSettingDialog extends Dialog {

    private Context mContext;
    private ListView listView;
    private int state;//是否已经加入圈子
    private DialogAdapter dialogAdapter = new DialogAdapter();
    private String[] menuItem_AddCircle = new String[]{"加入圈子"};
    private int[] imagtiem_AddCircle = new int[]{R.drawable.topic_create,R.drawable.circle_exit};
    private String[] menuItem_NotCreater = new String[]{"发表话题","退出圈子"};
    private int[] imagtiem_Creater_Passing = new int[]{R.drawable.topic_create,R.drawable.setting_shelf};
    private String[] menuItem_Creater_Passing = new String[]{"发表话题","圈子设置","转让圈子"};
    private String[] menuItem_CreaterNotPassedOrPassing = new String[]{"圈子设置"};
//    private String[] menuItems = new String[]{"加入圈子", "发表话题", "圈子设置","退出圈子","转让圈子"};
    private OnAddGroupClickedListener<String> onAddGroupClickedListener;
    private OnItemClickedListener notCreateronItemClickedListener;
    private OnItemClickedListener createrOnItemClickedListener;
    private OnItemClickedListener createrNotPassingListener;
    public static final int State_AddCircle = 0;
    public static final int State_AddCircle_NotCreater = 1;
    public static final int State_AddCricle_Creater_PASSED = 2;//通过审核
    public static final int State_AddCircle_Creater_NotPassed_Or_Passing = 3;//没有通过审核或者正在审核；


    public CircleSettingDialog(Context context,int style) {
        super(context,style);
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.lv_circle_setting_dialog, null);
        listView = (ListView) view.findViewById(R.id.lv_circle_setting);
        this.setCancelable(true);
        Window window = this.getWindow();
        // 设置窗体的对齐方式
        window.setGravity(Gravity.RIGHT|Gravity.TOP);
//        window.setWindowAnimations(R.style.popwin_anim_style);
        // 设置窗体的padding
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.x = 4; // 新位置X坐标
        lp.y = 88; // 新位置Y坐标
        lp.dimAmount = 0.2f;
        window.setAttributes(lp);
        this.setContentView(view);
        listView.setAdapter(dialogAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (state == State_AddCircle){
//                    setState(true);
                    if(onAddGroupClickedListener != null)
                        onAddGroupClickedListener.onAddGroupClicked(menuItem_AddCircle[0]);
                    hide();
                }if(state == State_AddCircle_NotCreater){
                    if(notCreateronItemClickedListener != null)
                        notCreateronItemClickedListener.onItemClickedListener(position);
                    hide();
                }else if(state == State_AddCricle_Creater_PASSED){
                    switch (position){
                        case 0:
                        case 1:
                        case 2:
                            if(createrOnItemClickedListener != null)
                                createrOnItemClickedListener.onItemClickedListener(position);
//                            mContext.startActivity(new Intent(mContext, DiscoverTopicListActivity.class));
                            hide();
                            break;
                    }
                }else if(state == State_AddCircle_Creater_NotPassed_Or_Passing){
                    switch (position){
                        case 0:
                            if(createrNotPassingListener != null){
                                createrNotPassingListener.onItemClickedListener(position);
                            }
                            break;
                    }
                    hide();
                }
            }
        });
    }

    public void setState(int state) {
        this.state = state;
        dialogAdapter.notifyDataSetChanged();
    }


    private class DialogAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if(state == State_AddCircle){//加入圈子
                return menuItem_AddCircle.length;
            } else if (state == State_AddCricle_Creater_PASSED) {//自己创建的圈子并且通过审核
                return menuItem_Creater_Passing.length;
            }else if(state == State_AddCircle_NotCreater){//不是自己创建的圈子但是已经加入
                return menuItem_NotCreater.length;
            }else if (state == State_AddCircle_Creater_NotPassed_Or_Passing){//是自己创建的圈子，但是没有通过审核或者正在审核中。
                return menuItem_CreaterNotPassedOrPassing.length;
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
            View view = mLayoutInflater.inflate(R.layout.item_lv_circle_setting_dialog, parent, false);
            TextView textView = (TextView) view.findViewById(R.id.tv_setting_dialog);
            ImageView imageView = view.findViewById(R.id.imag_pull);
            if (state == State_AddCricle_Creater_PASSED){
                textView.setText(menuItem_Creater_Passing[position]);
                imageView.setImageResource(imagtiem_Creater_Passing[position]);
            }else if(state == State_AddCircle){
                textView.setText(menuItem_AddCircle[0]);
                imageView.setImageResource(R.drawable.join_circle);
            }else if(state == State_AddCircle_NotCreater){
                textView.setText(menuItem_NotCreater[position]);
                imageView.setImageResource(imagtiem_AddCircle[position]);
            }else if(state == State_AddCircle_Creater_NotPassed_Or_Passing){
                textView.setText(menuItem_CreaterNotPassedOrPassing[position]);
                imageView.setImageResource(R.drawable.setting_shelf);
            }
            return view;
        }
    }

    public OnAddGroupClickedListener<String> getOnAddGroupClickedListener() {
        return onAddGroupClickedListener;
    }

    public void setOnAddGroupClickedListener(OnAddGroupClickedListener<String> onAddGroupClickedListener) {
        this.onAddGroupClickedListener = onAddGroupClickedListener;
    }

    public OnItemClickedListener getNotCreateronItemClickedListener() {
        return notCreateronItemClickedListener;
    }

    public void setNotCreateronItemClickedListener(OnItemClickedListener notCreateronItemClickedListener) {
        this.notCreateronItemClickedListener = notCreateronItemClickedListener;
    }

    public OnItemClickedListener getCreaterOnItemClickedListener() {
        return createrOnItemClickedListener;
    }

    public void setCreaterOnItemClickedListener(OnItemClickedListener createrOnItemClickedListener) {
        this.createrOnItemClickedListener = createrOnItemClickedListener;
    }

    public OnItemClickedListener getCreaterNotPassingListener() {
        return createrNotPassingListener;
    }

    public void setCreaterNotPassingListener(OnItemClickedListener createrNotPassingListener) {
        this.createrNotPassingListener = createrNotPassingListener;
    }
}
