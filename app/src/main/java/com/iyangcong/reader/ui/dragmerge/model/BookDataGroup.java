package com.iyangcong.reader.ui.dragmerge.model;

import android.support.annotation.NonNull;

import com.iyangcong.reader.bean.ShelfBook;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lg on 2017/2/19.
 */

public class BookDataGroup {
    private List<ShelfBook> mChild = new ArrayList<>();
    private String mCategory = "分类名称";

    public void addChild(@NonNull ShelfBook bookData){
        bookData.setParent(this);
        mChild.add(bookData);
    }

    public void addChild(int location,@NonNull ShelfBook bookData){
        bookData.setParent(this);
        mChild.add(location,bookData);
    }

    public ShelfBook removeChild(int location){
        ShelfBook bookData = mChild.remove(location);
        bookData.setParent(null);
        return bookData;
    }

    public boolean removeChild(@NonNull ShelfBook bookData){
        bookData.setParent(null);
        return mChild.remove(bookData);
    }


    public int getChildCount(){
        return mChild.size();
    }


    public ShelfBook getChild(int position){
        return mChild.get(position);
    }


    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public int getCheckedCount(){
        if(mChild != null){
            int i = 0;
            for(ShelfBook data:mChild){
                if(data.getChecked()){
                    i++;
                }
            }
            return i;
        }
        return 0;
    }
}
