package com.yy.dynamicyaya.adapter;

import android.support.v4.app.Fragment;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/12/19 15:24
 * Time: 15:24
 * Description:
 */
public class PluginFragmentState {
    private String pageTitle;

    private Fragment targetFragment;


    public PluginFragmentState() {
    }

    public PluginFragmentState(String pageTitle, Fragment targetFragment) {
        this.pageTitle = pageTitle;
        this.targetFragment = targetFragment;
    }

    public Fragment getTargetFragment() {
        return targetFragment;
    }

    public void setTargetFragment(Fragment targetFragment) {
        this.targetFragment = targetFragment;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    @Override
    public String toString() {
        return "PluginFragmentState{" +
                "pageTitle='" + pageTitle + '\'' +
                '}';
    }
}
