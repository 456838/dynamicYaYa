package com.yy.msdkquality.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;

import java.util.List;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/8/4 15:51
 * Time: 15:51
 * Description:
 */
public class MyViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragmentList;
    private FragmentManager fm;

    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    public MyViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fm = fm;
        this.fragmentList = fragments;
        notifyDataSetChanged();
    }


    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setFragments(List<Fragment> fragments) {
        if (this.fragmentList == null) {
            FragmentTransaction ft = fm.beginTransaction();
            for (Fragment f : this.fragmentList) {
                ft.remove(f);
            }
            ft.commit();
            ft = null;
            fm.executePendingTransactions();
            notifyDataSetChanged();
        }
    }
}
