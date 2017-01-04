package com.yy.dynamicyaya;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.morgoo.droidplugin.pm.PluginManager;
import com.orhanobut.logger.Logger;
import com.yy.dynamicyaya.adapter.DownloadItemAdapter;
import com.yy.saltonframework.base.BaseFragment;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemLongClickListener;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;
import okhttp3.Call;

import static com.morgoo.helper.compat.PackageManagerCompat.INSTALL_FAILED_NOT_SUPPORT_ABI;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2017/1/3 10:18
 * Time: 10:18
 * Description:
 */
public class TestFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate, BGAOnItemChildClickListener, BGAOnRVItemLongClickListener {

    private static final String TAG = "aa";
    private RecyclerView mRecyclerView;
    private BGARefreshLayout mBGARefreshLayout;
    private DownloadItemAdapter mDownloadItemAdapter;
    private PackageManager mPackageManager;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fm_market);
        mRecyclerView = getViewById(R.id.recyclerView);
        mBGARefreshLayout = getViewById(R.id.bGARefreshLayout);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mDownloadItemAdapter = new DownloadItemAdapter(mRecyclerView);
        mRecyclerView.setAdapter(mDownloadItemAdapter);
        BGAStickinessRefreshViewHolder stickinessRefreshViewHolder = new BGAStickinessRefreshViewHolder(getActivity(), true);
        stickinessRefreshViewHolder.setStickinessColor(R.color.colorPrimary);
        stickinessRefreshViewHolder.setRotateImage(R.mipmap.bga_refresh_loading01);
        mBGARefreshLayout.setRefreshViewHolder(stickinessRefreshViewHolder);
        mPackageManager = getActivity().getPackageManager();
    }

    @Override
    protected void setListener() {
        mBGARefreshLayout.setDelegate(this);
        mDownloadItemAdapter.setOnItemChildClickListener(this);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                Log.i(TAG, "测试自定义onScrollStateChanged被调用");
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                Log.i(TAG, "测试自定义onScrolled被调用");
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {


        mDownloadItemAdapter.setDataAndUpdate(getData());
//        mDownloadItemAdapter.setData(itemList);
    }

    private List<DownloadItem> getData() {
        List<DownloadItem> itemList = new ArrayList<>();
        itemList.add(new DownloadItem(1, 1, "com.cpuid.cpu_z", "http://shouji.360tpcdn.com/161111/cac110bfec605a9967d79314ab2a0c4e/com.cpuid.cpu_z_21.apk"
                , "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png",
                "1.0.0", "cpuz", "系统", 1024000
        ));
        itemList.add(new DownloadItem(1, 2, "com.duowan.mobile", "http://repo.yypm.com/dwbuild/mobile/android/entmobile/entmobile-android_social_feature/20170103-43073-r529702/yymobile_client-6.0.0-SNAPSHOT-43073-official.apk"
                , "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png",
                "1.0.0", "手机YY", "娱乐", 1024000
        ));
        itemList.add(new DownloadItem(1, 3, "com.yy.ourtimes", "http://repo.yypm.com/dwbuild/mobile/android/ourtimes/2.7.0/20161220-3775-r1450335/ourtimes-2.7.0-3775-official.apk"
                , "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png",
                "1.0.0", "小时代", "系统", 1024000
        ));
        itemList.add(new DownloadItem(1, 4, "com.cpuid.cpu_z", "http://shouji.360tpcdn.com/161111/cac110bfec605a9967d79314ab2a0c4e/com.cpuid.cpu_z_21.apk"
                , "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png",
                "1.0.0", "cpuz3", "系统", 1024000
        ));
        itemList.add(new DownloadItem(1, 5, "com.cpuid.cpu_z", "http://shouji.360tpcdn.com/161111/cac110bfec605a9967d79314ab2a0c4e/com.cpuid.cpu_z_21.apk"
                , "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png",
                "1.0.0", "cpuz4", "系统", 1024000
        ));
        return itemList;
    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    public void onItemChildClick(ViewGroup parent, View childView, int position) {
        String text = ((BootstrapButton) childView.findViewById(R.id.btn_download)).getText().toString().trim();
        Logger.i(text);
        if (text.equals(getString(R.string.download)) || text.equals(getString(R.string.retry))) {     //
            download(position);
        } else if (text.equals(getString(R.string.install))) {
            install(position);
        } else if (text.equals(getString(R.string.open))) {
            open(position);
        } else if (text.equals(getString(R.string.update))) {
//            uninstall(position);
        }
    }

    private void open(int position) {
        try {
            final DownloadItem pluginBean = mDownloadItemAdapter.getItem(position);
            Intent intent = mPackageManager.getLaunchIntentForPackage(pluginBean.getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void download(final int position) {
        final DownloadItem pluginBean = mDownloadItemAdapter.getItem(position);
        OkHttpUtils.getInstance().initWithDefaultClient().get().url(pluginBean.getPluginDownloadUrl()).build().execute(new FileCallBack(YConfig.DEFALULT_SAVE_LOCATION, pluginBean.getName() + ".apk") {
            /**
             * UI Thread
             * @param progress
             */
            public void inProgress(float progress, long total, int id) {
//                Logger.i("下载进度：" + (int) (progress * 100));
                if (progress == 1) {
                    mDownloadItemAdapter.updateProgress(position, getString(R.string.install));
                    return;
                }
                mDownloadItemAdapter.updateProgress(position, (int) (progress * 100) + "");
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                Logger.i("下载错误：" + e.getMessage());
                Toast.makeText(getContext(), "下载错误，请重试", Toast.LENGTH_SHORT).show();
                mDownloadItemAdapter.updateProgress(position, getString(R.string.retry));
            }

            @Override
            public void onResponse(File apk, int id) {

            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
            }
        });
    }

    private static final int INASTALL_SUCCESS = 1;

    private void install(final int position) {
        final DownloadItem downloadItem = mDownloadItemAdapter.getItem(position);
        if (PluginManager.getInstance().isConnected()) {
            final String apkPath = YConfig.DEFALULT_SAVE_LOCATION + downloadItem.getName() + ".apk";
            final PackageInfo info = mPackageManager.getPackageArchiveInfo(apkPath, 0);
//            Logger.i(info.permissions.toString());

            try {
                if (PluginManager.getInstance().getPackageInfo(info.packageName, 0) != null) {

                    Logger.i("安装Package:" + info.packageName);
                    Toast.makeText(getActivity(), "已经安装了，不能再安装", Toast.LENGTH_SHORT).show();
                } else {
                    int ret = PluginManager.getInstance().installPackage(apkPath, 0);
                    Logger.i("安装结果:" + ret);
                    switch (ret) {
                        case INASTALL_SUCCESS:
                            mDownloadItemAdapter.updateProgress(position, getString(R.string.open));
                            break;
                        case PluginManager.INSTALL_FAILED_NO_REQUESTEDPERMISSION:
                            Toast.makeText(getActivity(), "安装失败，文件请求的权限太多", Toast.LENGTH_SHORT).show();
                            break;
                        case INSTALL_FAILED_NOT_SUPPORT_ABI:
                            Toast.makeText(getActivity(), "宿主不支持插件的abi环境，可能宿主运行时为64位，但插件只支持32位", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(getActivity(), "安装失败:" + ret, Toast.LENGTH_SHORT).show();

                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//
        } else {
            mDownloadItemAdapter.updateProgress(position, "初始化");
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        mDownloadItemAdapter.clear();
        mDownloadItemAdapter.setDataAndUpdate(getData());

    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }

    @Override
    public boolean onRVItemLongClick(ViewGroup parent, View itemView, int position) {
        doUninstall(mDownloadItemAdapter.getItem(position));
        return true;
    }

    private void doUninstall(final DownloadItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("警告，你确定要删除么？");
        builder.setMessage("警告，你确定要删除" + item.getName() + "么？");
        builder.setNegativeButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!PluginManager.getInstance().isConnected()) {
                    Toast.makeText(getActivity(), "服务未连接", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        PluginManager.getInstance().deletePackage(item.getPackageName(), 0);
                        Toast.makeText(getActivity(), "删除完成", Toast.LENGTH_SHORT).show();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        builder.setNeutralButton("取消", null);
        builder.show();
    }
}
