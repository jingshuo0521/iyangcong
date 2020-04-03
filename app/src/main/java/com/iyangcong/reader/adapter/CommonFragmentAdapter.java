package com.iyangcong.reader.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.iyangcong.reader.ui.customtablayout.listener.CustomTabEntity;

import java.util.List;

/**
 * Created by ljw on 2016/12/30.
 */

public class CommonFragmentAdapter extends FragmentStatePagerAdapter {
    private List<CustomTabEntity> mTabTitles;
    private List<Fragment> mFragmentList;

    public CommonFragmentAdapter(FragmentManager fm, List<CustomTabEntity> tabTitles, List<Fragment> fragmentList) {
        super(fm);
        mTabTitles = tabTitles;
        mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles.get(position).getTabTitle();
    }
}
