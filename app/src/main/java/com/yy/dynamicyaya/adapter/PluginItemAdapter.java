package com.yy.dynamicyaya.adapter;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.morgoo.droidplugin.pm.PluginManager;
import com.yy.dynamicyaya.PluginBean;
import com.yy.dynamicyaya.R;
import com.yy.dynamicyaya.YConfig;

import java.io.File;

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

        if (isFileExist(YConfig.DEFALULT_SAVE_LOCATION + model.getName() + ".apk")) {
            File apk = new File(YConfig.DEFALULT_SAVE_LOCATION + model.getName() + ".apk");
            //如果文件存在，加载文件信息
            PackageManager pm = mContext.getPackageManager();
            if (apk.exists() && apk.getPath().toLowerCase().endsWith(".apk")) {
                final PackageInfo info = pm.getPackageArchiveInfo(apk.getPath(), 0);
                model.setApkPackageInfo(pm, info, apk.getPath());
                try {
                    if (PluginManager.getInstance().getPackageInfo(info.packageName, 0) != null) {
                        updateProgress(position, mContext.getString(R.string.open));
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    private boolean isFileExist(String filePath) {
        return new File(filePath).exists();
    }


    /**
     * @param position 当前item位置
     * @param tip      提示
     */
    public void updateProgress(int position, String tip) {
        BootstrapButton btn_down = (BootstrapButton) mRecyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.btn_download);
        btn_down.setText(tip);
        if (tip.equals(mContext.getString(R.string.install))) {
            btn_down.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
        } else if (tip.equals(mContext.getString(R.string.update))) {
            btn_down.setBootstrapBrand(DefaultBootstrapBrand.INFO);
        } else if (tip.equals(mContext.getString(R.string.open))) {
            btn_down.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
        } else {
            btn_down.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
        }
    }

}
