package com.iyangcong.reader.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class ViewHolderModelNew<T> extends RecyclerView.ViewHolder {

    public ViewHolderModelNew(Context context, View itemView) {
        super(itemView);
    }

    public abstract boolean setLayoutVisibility(T t);

    public abstract void bindData(T t,int position);

    public void setData(T t,int position){
        if(setLayoutVisibility(t)){
            bindData(t,position);
        }
    }
}
