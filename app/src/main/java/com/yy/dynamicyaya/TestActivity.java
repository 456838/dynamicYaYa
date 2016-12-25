package com.yy.dynamicyaya;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.yy.saltonframework.base.AdapterBase;
import com.yy.saltonframework.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/12/19 15:21
 * Time: 15:21
 * Description:
 */
public class TestActivity extends AppCompatActivity {


    private ListView lv_plugin;

    private PluginAdapter mPluginAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        lv_plugin = (ListView) findViewById(R.id.lv_plugin);
        mPluginAdapter = new PluginAdapter(this);
        mPluginAdapter.AddAll(getPlugins());
        lv_plugin.setAdapter(mPluginAdapter);
        lv_plugin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i) != null) {
                    PluginBean pluginBean = (PluginBean) adapterView.getItemAtPosition(i);
                    downloadPlugin(pluginBean.getPluginUrl());
                }
            }
        });
    }

    private void downloadPlugin(String pluginUrl) {

    }

    class PluginAdapter extends AdapterBase<PluginBean> {

        public PluginAdapter(Context pContext) {
            super(pContext);
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = GetLayoutInflater().inflate(R.layout.item_plugin, null, false);
            }
            TextView tv_plugin_name = ViewHolder.get(view, R.id.tv_plugin_name);
            tv_plugin_name.setText(getItem(position) == null ? "未知插件" : getItem(position).getName());
            return view;
        }
    }

    private List<PluginBean> getPlugins() {
        List<PluginBean> plugins = new ArrayList<>();
        plugins.add(new PluginBean(1, "wifi密码查看", "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png", "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png"));
        return plugins;
    }
}
