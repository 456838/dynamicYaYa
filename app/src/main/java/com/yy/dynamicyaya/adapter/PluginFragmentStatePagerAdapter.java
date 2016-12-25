package com.yy.dynamicyaya.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/12/19 15:22
 * Time: 15:22
 * Description:
 */
public class PluginFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    private List<PluginFragmentState> pluginFragmentStateList;

    public PluginFragmentStatePagerAdapter(FragmentManager fm, List<PluginFragmentState> pluginFragmentStateList) {
        super(fm);
        this.pluginFragmentStateList = pluginFragmentStateList;
    }

    @Override
    public Fragment getItem(int position) {
        if (position < pluginFragmentStateList.size()) {
            return pluginFragmentStateList.get(position).getTargetFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return pluginFragmentStateList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pluginFragmentStateList.get(position).getPageTitle();
    }
}
