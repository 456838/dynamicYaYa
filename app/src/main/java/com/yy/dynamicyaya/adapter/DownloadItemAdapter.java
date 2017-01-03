package com.yy.dynamicyaya.adapter;

import android.content.pm.PackageInfo;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.morgoo.droidplugin.pm.PluginManager;
import com.orhanobut.logger.Logger;
import com.yy.dynamicyaya.DownloadItem;
import com.yy.dynamicyaya.R;
import com.yy.dynamicyaya.YConfig;

import org.xutils.x;

import java.io.File;
import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2017/1/3 11:29
 * Time: 11:29
 * Description:
 */
public class DownloadItemAdapter extends BGARecyclerViewAdapter<DownloadItem> {
    public DownloadItemAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.rv_plugin_item);
    }

    @Override
    protected void setItemChildListener(BGAViewHolderHelper helper, int viewType) {
        super.setItemChildListener(helper, viewType);
        helper.setItemChildClickListener(R.id.btn_download);
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, DownloadItem model) {
        helper.setText(R.id.tv_title, model.getName()).setText(R.id.tv_category, model.getCategory()).setText(R.id.tv_description, model.getPluginDownloadUrl());
        if (model.isFileExist()) {
            BootstrapButton btn_down = helper.getView(R.id.btn_download);
            btn_down.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
            btn_down.setText(mContext.getString(R.string.install));
        }
        try {
            List<PackageInfo> infos = PluginManager.getInstance().getInstalledPackages(0);
            if (infos != null) {
                Logger.i(infos.size() + "");
                for (PackageInfo info : infos) {
                    Logger.i(info.packageName);
                    Logger.i(model.getPackageName());
                    if (info.packageName.equals(model.getPackageName())) {
                        BootstrapButton btn_down = helper.getView(R.id.btn_download);
                        btn_down.setText(mContext.getString(R.string.open));
                        btn_down.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
                    }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        x.image().bind(helper.getImageView(R.id.iv_icon), model.getIconDownloadUrl());

//        if (isFileExist(YConfig.DEFALULT_SAVE_LOCATION + model.getName() + ".apk")) {
//            File apk = new File(YConfig.DEFALULT_SAVE_LOCATION + model.getName() + ".apk");
//            //如果文件存在，加载文件信息
//            PackageManager pm = mContext.getPackageManager();
//            if (apk.exists() && apk.getPath().toLowerCase().endsWith(".apk")) {
//                final PackageInfo info = pm.getPackageArchiveInfo(apk.getPath(), 0);
//                try {
//                    if (PluginManager.getInstance().getPackageInfo(info.packageName, 0) != null) {
//                        updateProgress(position, mContext.getString(R.string.open));
//                    }
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }

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


    /**
     * 设置全新的数据集合，如果传入null，则清空数据列表（第一次从服务器加载数据，或者下拉刷新当前界面数据表）
     *
     * @param data
     */
    public void setDataAndUpdate(List<DownloadItem> data) {
        setData(data);
        for (DownloadItem item : data) {
            String apkPath = YConfig.DEFALULT_SAVE_LOCATION + item.getName() + ".apk";
            if (isFileExist(apkPath)) {
                item.setFileExist(true);
            }
        }
    }

}