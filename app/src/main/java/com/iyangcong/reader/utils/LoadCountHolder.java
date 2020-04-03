package com.iyangcong.reader.utils;

/**
 * Created by WuZepeng on 2017-03-07.
 */

public class LoadCountHolder {
    /**
     * 重新加载的次数
     */
    private int reloadTimes = 0;
    /**
     * 加载的页面
     */
    private int page = 0;
    /**
     * 是否是刷新
     */
    private boolean isRefresh = false;

    private boolean canLoadMore = true;
    private int pageSize = 10;

    public void refresh(){
        page = 0;
        isRefresh = true;
        canLoadMore = true;
    }

    public void loadMore(){
        page++;
        isRefresh = false;
    }

    public void reload(){
        reloadTimes++;
    }

    public void loadMoreFailed(){
        page--;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }

    public boolean isCanLoadMore() {
        return canLoadMore;
    }

    public void setCanLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getReloadTimes() {
        return reloadTimes;
    }

    public void setReloadTimes(int reloadTimes) {
        this.reloadTimes = reloadTimes;
    }

    public class ClickableRecoder{
        private boolean clickable;

        public ClickableRecoder(boolean clickable) {
            this.clickable = clickable;
        }

        public boolean isClickable() {
            return clickable;
        }

        public void setClickable(boolean clickable) {
            this.clickable = clickable;
        }
    }

    @Override
    public String toString() {
        return "LoadCountHolder{" +
                "page=" + page +
                ", isRefresh=" + isRefresh +
                '}';
    }
}
