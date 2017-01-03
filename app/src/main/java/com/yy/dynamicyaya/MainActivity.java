package com.yy.dynamicyaya;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.yy.dynamicyaya.adapter.PluginFragmentState;
import com.yy.dynamicyaya.adapter.PluginFragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PagerTabStrip pagerTabStrip;
    private ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTabStrip);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        List<PluginFragmentState> pluginFragmentStateList = new ArrayList<>();
        pluginFragmentStateList.add(new PluginFragmentState("已安装", new InstalledFragment()));
//        pluginFragmentStateList.add(new PluginFragmentState("待安装", new MarketFragmentDul()));
        pluginFragmentStateList.add(new PluginFragmentState("插件市场", new TestFragment()));
        viewPager.setAdapter(new PluginFragmentStatePagerAdapter(getSupportFragmentManager(), pluginFragmentStateList));
    }
}
