package com.iyangcong.reader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.bean.IYangCongBind;
import com.iyangcong.reader.ui.button.FlatButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/12/29 0029.
 */

public class MineSettingBindAdapter extends BaseAdapter {
    private Context context;
    LayoutInflater mInflater;
    List<IYangCongBind> mBindList;
    private ClickTypeCallback clickTypeCallback ;

    public MineSettingBindAdapter(Context context , List<IYangCongBind> mBindList) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.mBindList = mBindList;
//        1微信 2QQ 3微博 4豆瓣
    }

    @Override
    public int getCount() {
        return mBindList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBindList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_mine_setting, null);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        IYangCongBind iycb= (IYangCongBind) getItem(position);

        holder.mineSettingBindIm.setImageResource(iycb.getImage());
        holder.mineSettingBindTv1.setText(iycb.getName());
        holder.mineSettingBindTv2.setText(iycb.getUser());
        if(iycb.getisbind() == false){
            holder.mineSettingBindBtn.setText("绑定");
            holder.mineSettingBindBtn.setButtonColor(context.getResources().getColor(R.color.graywhite));
            holder.mineSettingBindBtn.setTextColor(context.getResources().getColor(R.color.text_color_orange));
        }else if(iycb.getisbind() == true){
            if(position != 0 && position != 1){
                holder.mineSettingBindBtn.setText("解除");
            }else{
                holder.mineSettingBindBtn.setText("更换");
            }
            holder.mineSettingBindBtn.setButtonColor(context.getResources().getColor(R.color.button_color));
            holder.mineSettingBindBtn.setTextColor(context.getResources().getColor(R.color.white));
        }
        holder.mineSettingBindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTypeCallback.getClickType(position,mBindList.get(position).getisbind());
            }
        });
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.mine_setting_bind_im)
        ImageView mineSettingBindIm;
        @BindView(R.id.mine_setting_bind_tv1)
        TextView mineSettingBindTv1;
        @BindView(R.id.mine_setting_bind_tv2)
        TextView mineSettingBindTv2;
        @BindView(R.id.mine_setting_bind_btn)
        FlatButton mineSettingBindBtn;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
    public interface ClickTypeCallback{
        public void getClickType(int type,boolean isbind);
    }
    public void setDialogCallback(ClickTypeCallback clickTypeCallback) {
        this.clickTypeCallback = clickTypeCallback;
    }
}
