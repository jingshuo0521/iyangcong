package com.iyangcong.reader.ui.dialog;

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


/**
 * Created by DarkFlameMaster on 2017/4/21.
 */

public class BookDetailsMoreDialog extends Dialog {

    private Context mContext;
    private ListView listView;
    private Boolean state = false;//是否已经收藏
    private DialogAdapter dialogAdapter ;
    private String[] menuItems = new String[]{ "","分享"};
    private int[] menuImages = new int[]{R.drawable.book_favorite,R.drawable.book_share};
    private OnItemClickedListener onItemClickedListener;
    public BookDetailsMoreDialog(Context context,int style) {
        super(context,style);
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.lv_circle_setting_dialog, null);
        listView = (ListView) view.findViewById(R.id.lv_circle_setting);
        dialogAdapter = new DialogAdapter(mContext,state);
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
                switch (position){
                    case 0:
                    case 1:
                        if(onItemClickedListener != null)
                            onItemClickedListener.onItemClickedListener(position);
                        hide();
                        break;
                }

            }
        });
    }

    public void setState(Boolean state) {
        this.state = state;
        if (state){
            menuItems[0] = "取消收藏";
        }else{
            menuItems[0] = "添加收藏";
        }
        if(dialogAdapter != null){
            dialogAdapter.notifyDataSetChanged();
        }
    }

    private class DialogAdapter extends BaseAdapter {
        private Context context;
        private boolean state;

        public DialogAdapter(Context context,boolean state){
            this.state = state;
            this.context = context;
        }
        @Override
        public int getCount() {
            return 2;
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
            imageView.setImageResource(menuImages[position]);
            textView.setText(menuItems[position]);
            return view;
        }
    }
    public OnItemClickedListener getOnItemClickedListener() {
        return onItemClickedListener;
    }

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }
}
