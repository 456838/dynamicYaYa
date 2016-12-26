package com.yy.dynamicyaya;

import android.support.v7.widget.RecyclerView;

import com.beardedhen.androidbootstrap.BootstrapButton;

import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/12/19 16:16
 * Time: 16:16
 * Description:
 */
public class PluginItemAdapter extends BGARecyclerViewAdapter<PluginBean> {
    public PluginItemAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.rv_plugin_item);
    }


//    @Override
//    protected void setItemChildListener(BGAViewHolderHelper viewHolderHelper) {
//        viewHolderHelper.setItemChildClickListener(R.id.btn_download);
//    }


    @Override
    protected void setItemChildListener(BGAViewHolderHelper helper, int viewType) {
        super.setItemChildListener(helper, viewType);
        helper.setItemChildClickListener(R.id.btn_download);
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, PluginBean model) {
        helper.setText(R.id.tv_title, model.getName()).setText(R.id.tv_category, model.getPluginUrl()).setText(R.id.tv_description, model.getPluginUrl());
        helper.setPosition(model.getOrder());
//        x.image().bind(helper.getImageView(R.id.iv_icon), model.getIconUrl());
    }

    /**
     * @param position 当前item位置
     * @param tip      提示
     */
    public void updateProgress(int position, String tip) {
        BootstrapButton btn_down = (BootstrapButton) mRecyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.btn_download);
        btn_down.setText(tip);
    }

}
