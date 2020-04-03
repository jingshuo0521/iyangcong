package com.iyangcong.reader.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.iyangcong.reader.R;
import com.iyangcong.reader.ui.customtablayout.listener.CustomTabEntity;

import java.util.List;

/**
 * Created by WuZepeng on 2017-05-31.
 */

public abstract class CommonFragmentAda extends FragmentStatePagerAdapter {


	private List<String> mFragmentTags;
	private List<CustomTabEntity> mTabTitles;
	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;

	public CommonFragmentAda(FragmentManager fm, List<String> mFragmentTags, List<CustomTabEntity> mTabTitles) {
		super(fm);
		this.mFragmentManager = fm;
		this.mFragmentTags = mFragmentTags;
		this.mTabTitles = mTabTitles;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if(mFragmentTransaction == null){
			mFragmentTransaction = mFragmentManager.beginTransaction();
		}
		Fragment fragment = mFragmentManager.findFragmentByTag(mFragmentTags.get(position));
		if (fragment == null) {
			fragment = getItem(position);
		}
		for (int i = 0; i < mFragmentTags.size(); i++) {
			Fragment f = mFragmentManager.findFragmentByTag(mFragmentTags.get(i));
			if (f != null && f.isAdded()) {
				mFragmentTransaction.hide(f);
			}
		}
		if (fragment.isAdded()) {
			mFragmentTransaction.show(fragment);
		} else {
			mFragmentTransaction.add(container.getId(), fragment, mFragmentTags.get(position));
			mFragmentTransaction.hide(fragment);
			mFragmentTransaction.show(fragment);
		}
		return fragment;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
		Fragment fragment = (Fragment)object;
		if(mFragmentTransaction == null){
			mFragmentTransaction = mFragmentManager.beginTransaction();
		}
		mFragmentTransaction.remove(fragment);
	}

	@Override
	public int getCount() {
		return mFragmentTags.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mTabTitles.get(position).getTabTitle();
	}
}
