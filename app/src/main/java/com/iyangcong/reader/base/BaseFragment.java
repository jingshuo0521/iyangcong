package com.iyangcong.reader.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.iyangcong.reader.R;
import com.iyangcong.reader.ui.CustomProgressDialog;
import com.iyangcong.reader.ui.networkerrolayout.VaryViewHelper;
import com.iyangcong.reader.ui.networkerrolayout.VaryViewHelperUtils;
import com.iyangcong.reader.utils.NetUtil;
import com.lzy.okgo.OkGo;

import static com.iyangcong.reader.utils.NetUtil.NETWORK_MOBILE;
import static com.iyangcong.reader.utils.NetUtil.NETWORK_NONE;
import static com.iyangcong.reader.utils.NetUtil.NETWORK_WIFI;
import static com.iyangcong.reader.utils.NetUtil.REMOTE_DATABASE_ERROR;

/**
 * Created by ljw on 2016/12/6.
 * <p>
 * 若把初始化内容放到initData实现,就是采用Lazy方式加载的Fragment
 * 若不需要Lazy加载则initData方法内留空,初始化内容放到initViews即可
 * -
 * -注1: 如果是与ViewPager一起使用，调用的是setUserVisibleHint。
 * ------可以调用mViewPager.setOffscreenPageLimit(size),若设置了该属性 则viewpager会缓存指定数量的Fragment
 * -注2: 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
 * -注3: 针对初始就show的Fragment 为了触发onHiddenChanged事件 达到lazy效果 需要先hide再show
 */
public abstract class BaseFragment extends android.support.v4.app.Fragment {
    protected String fragmentTitle;             //fragment标题
    private boolean isVisible;                  //是否可见状态
    private boolean isPrepared;                 //标志位，View已经初始化完成。
    private boolean isFirstLoad = true;         //是否第一次加载
    protected Context mContext;
    protected LayoutInflater inflater;

    protected boolean isInitCache = false;//是否缓存加载页面
    protected String TAG = getClass().getSimpleName();
    private VaryViewHelperUtils varyViewHelperUtils;
    protected int networkState;

    /**
     * 加载框
     */
    protected CustomProgressDialog dialogProgress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        this.inflater = inflater;
        isFirstLoad = true;
        varyViewHelperUtils = new VaryViewHelperUtils();
        View view = initView(inflater, container, savedInstanceState);
        isPrepared = true;
        lazyLoad();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        changLayoutAccrodToNetwork(getNetworkState(mContext));
    }

    public void showLoadingDialog() {
        if (dialogProgress == null) {
            dialogProgress = new CustomProgressDialog(mContext, getString(R.string.loading_tip));
            dialogProgress.show();
        } else {
            if (!dialogProgress.isShowing())
                dialogProgress.show();
        }
    }

    public void dismissLoadingDialig() {
        if (dialogProgress != null) {
            dialogProgress.dismiss();
        }
    }


    protected int getNetworkState(Context mctx) {
        networkState = NetUtil.getNetWorkState(mctx);
        return networkState;
    }

    private void changLayoutAccrodToNetwork(int networkState) {
        switch (networkState) {
            case NETWORK_NONE:
                if (varyViewHelperUtils.getVaryViewHelper() != null) {
                    varyViewHelperUtils.getVaryViewHelper().showErrorView();
                }
                isVisible = false;
                break;
            case NETWORK_MOBILE:
            case NETWORK_WIFI:
                isVisible = true;
                if (varyViewHelperUtils.getVaryViewHelper() != null)
                    varyViewHelperUtils.getVaryViewHelper().showDataView();
                break;
            case REMOTE_DATABASE_ERROR:
                break;
        }
    }

    /**
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    protected void onInvisible() {
    }

    protected void onVisible() {
        lazyLoad();
    }

    protected void lazyLoad() {
        if (!isPrepared || !isVisible || !isFirstLoad) {
            return;
        }
        isFirstLoad = false;
        initData();
    }

    protected void refreshSuccess(PtrClassicFrameLayout ptrClassicFrameLayout,boolean canloadMore) {
        if (ptrClassicFrameLayout != null) {
            ptrClassicFrameLayout.refreshComplete();
            ptrClassicFrameLayout.setLoadMoreEnable(canloadMore);
        }
    }

    protected void refreshSuccess(PtrClassicFrameLayout ptrClassicFrameLayout) {
        if (ptrClassicFrameLayout != null) {
            ptrClassicFrameLayout.refreshComplete();
            ptrClassicFrameLayout.setLoadMoreEnable(true);
        }
    }

    protected void autoRefresh(final PtrClassicFrameLayout ptrClassicFrameLayout) {
        if (ptrClassicFrameLayout != null) {
            if (!ptrClassicFrameLayout.isAutoRefresh() && !ptrClassicFrameLayout.isRefreshing()) {
                ptrClassicFrameLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        ptrClassicFrameLayout.autoRefresh(true);
                    }
                });
            }
        }
    }

    protected void refreshFailed(PtrClassicFrameLayout ptrClassicFrameLayout) {
        if (ptrClassicFrameLayout != null) {
            ptrClassicFrameLayout.refreshComplete();
            ptrClassicFrameLayout.setLoadMoreEnable(false);
        }
    }

    protected void loadMoreSuccess(PtrClassicFrameLayout ptrClassicFrameLayout, boolean isEnd) {
        if (ptrClassicFrameLayout != null) {
            ptrClassicFrameLayout.loadMoreComplete(true);
            if (isEnd) {
                ptrClassicFrameLayout.setLoadMoreEnable(false);
            } else {
                ptrClassicFrameLayout.setLoadMoreEnable(true);
            }
        }
    }

    protected void loadMoreFailed(PtrClassicFrameLayout ptrClassicFrameLayout) {
        if (ptrClassicFrameLayout != null) {
            ptrClassicFrameLayout.loadMoreComplete(true);
            ptrClassicFrameLayout.setLoadMoreEnable(false);
        }
    }

    protected void refreshAuto(final PtrClassicFrameLayout ptrClassicFrameLayout) {
        if (ptrClassicFrameLayout != null) {
            ptrClassicFrameLayout.autoRefresh(true);
//            ptrClassicFrameLayout.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    ptrClassicFrameLayout.autoRefresh(true);
//                }
//            },1000);
        }
    }

    /**
     * 有子类实现，实现特有效果
     *
     * @return
     */
    protected abstract View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * 初始化数据
     */
    protected abstract void initData();

    public String getTitle() {
        return TextUtils.isEmpty(fragmentTitle) ? "" : fragmentTitle;
    }

    public void setTitle(String title) {
        fragmentTitle = title;
    }

    /**
     * 初始化布局，该方法用来动态增加网络错误时的布局
     *
     * @param context         上下文
     * @param view            要被网络错误布局取代的布局的view实例
     * @param onClickListener 网络错误布局的点击事件
     * @return
     */
    public VaryViewHelper initVaryViewHelper(Context context, View view, View.OnClickListener onClickListener) {
        return varyViewHelperUtils.initVaryViewHelper(context, view, onClickListener);
    }

    public void showDataView() {
        varyViewHelperUtils.showDataView();
    }

    public void showErrorView() {
        varyViewHelperUtils.showErrorView();
    }

    @Override
    public void onStop() {
        super.onStop();
        OkGo.getInstance().cancelAll();
    }
}
