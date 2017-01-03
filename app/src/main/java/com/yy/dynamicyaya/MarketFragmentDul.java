package com.yy.dynamicyaya;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.morgoo.droidplugin.pm.PluginManager;
import com.orhanobut.logger.Logger;
import com.yy.dynamicyaya.adapter.PluginItemAdapter;
import com.yy.saltonframework.base.BaseFragment;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import okhttp3.Call;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/12/19 15:33
 * Time: 15:33
 * Description:
 */
public class MarketFragmentDul extends BaseFragment implements BGAOnItemChildClickListener, ServiceConnection {

    private static final int INASTALL_SUCCESS = 1;
    //    @BindView(R.id.mv_RecyclerView)
    RecyclerView mvRecyclerView;
    //    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    PluginItemAdapter recycleViewAdapter;
    private ArrayList<PluginBean> videosList = new ArrayList<>();
    private String areaCode;

    protected boolean refresh;
    protected int lastVisibleItem;
    protected boolean hasMore = true;
    protected static final int SIZE = 20;
    protected int mOffset = 0;

    public static MarketFragmentDul getInstance(String areaCode) {
        MarketFragmentDul mvViewPagerItemFragment = new MarketFragmentDul();
        Bundle bundle = new Bundle();
        bundle.putString("areaCode", areaCode);
        mvViewPagerItemFragment.setArguments(bundle);
        return mvViewPagerItemFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Bundle bundle = getArguments();
//        areaCode = bundle.getString("areaCode");

    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fm_market);
        mvRecyclerView = getViewById(R.id.recyclerView);
        swipeRefreshLayout = getViewById(R.id.bGARefreshLayout);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        initView();
    }

    @Override
    protected void onUserVisible() {

    }


    private void initView() {
        recycleViewAdapter = new PluginItemAdapter(mvRecyclerView);
        recycleViewAdapter.setOnItemChildClickListener(this);
        mvRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mvRecyclerView.setAdapter(recycleViewAdapter);
        mvRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                Logger.e("onScrollStateChanged");
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE && (lastVisibleItem + 1 == recycleViewAdapter.getItemCount()) && hasMore) {
//                    getData(mOffset + 1, SIZE, areaCode);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                Logger.e("onScrolled");
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources()
                        .getDisplayMetrics()));
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh = true;
                videosList.clear();
                mOffset = 0;
                getData(mOffset, SIZE, areaCode);
//                Logger.e("onRefresh");
//                dismissLoading();
                //加载数据
            }
        });
        getData(mOffset, SIZE, areaCode);
    }

    public void getData(int offset, int size, String areaCode) {
        showLoading();
        ArrayList<PluginBean> plugins = new ArrayList<>();
        plugins.add(new PluginBean(1, "Root Explorer", "http://down11.zol.com.cn/suyan/RootExplorer4.0.4.apk", "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png"));
        plugins.add(new PluginBean(2, "cpuz", "http://shouji.360tpcdn.com/161111/cac110bfec605a9967d79314ab2a0c4e/com.cpuid.cpu_z_21.apk", "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png"));
        plugins.add(new PluginBean(3, "wifi密码查看3", "http://down11.zol.com.cn/suyan/RootExplorer4.0.4.apk", "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png"));
        plugins.add(new PluginBean(4, "wifi密码查看4", "http://down11.zol.com.cn/suyan/RootExplorer4.0.4.apk", "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png"));
        setData(plugins);
//       OkHttpManager.getOkHttpManager().asyncGet(URLProviderUtil.getMVListUrl(areaCode, offset, size), new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                showSnake(e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response != null) {
//                    try {
//                        MVListBean mvListBean = new Gson().fromJson(response.body().string(), MVListBean.class);
//                        Message msg = Message.obtain();
//                        msg.obj = mvListBean.getVideos();
//                        msg.what = 101;
//                        mHandler.sendMessage(msg);
//                        //setData(mvListBean.getVideos());
//                    } catch (JsonSyntaxException e) {
//                        e.printStackTrace();
//                        showSnake(e.getLocalizedMessage());
//                    }
//                } else {
//                    showSnake("");
//                }
//            }
//        });
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 101:
                    ArrayList<PluginBean> PluginBeanArrayList = (ArrayList<PluginBean>) msg.obj;
                    setData(PluginBeanArrayList);
                    break;
            }
        }
    };

    public void setData(List<PluginBean> videoList) {
        dismissLoading();
        if (refresh) {
            refresh = false;
            mOffset = 0;
            videosList.clear();
        }
        if (videoList == null || videoList.size() == 0) {
            hasMore = false;
        } else {
            hasMore = true;
            int pos = videosList.size() - 1;
            videosList.addAll(videoList);
            recycleViewAdapter.notifyItemRangeChanged(pos, videoList.size());
            mOffset += videoList.size();
        }
        recycleViewAdapter.addNewData(videoList);
    }

    public void showLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    public void dismissLoading() {
        swipeRefreshLayout.setRefreshing(false);
    }

    public void showSnake(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onItemChildClick(ViewGroup parent, final View childView, final int position) {
        String text = ((BootstrapButton) childView.findViewById(R.id.btn_download)).getText().toString().trim();
        Logger.i(text);
        if (text.equals(getString(R.string.download)) || text.equals(getString(R.string.retry))) {     //
            download(position);
        } else if (text.equals(getString(R.string.install))) {
            install(position);
        } else if (text.equals(getString(R.string.open))) {
            open(position);
        } else if (text.equals(getString(R.string.update))) {
            uninstall(position);
        }
    }

    private void open(int position) {
        final PluginBean pluginBean = recycleViewAdapter.getItem(position);
        PackageManager pm = getActivity().getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(pluginBean.getApkPackageInfo().packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean uninstall(int position) {
        return false;
    }

    private void install(final int position) {
        final PluginBean pluginBean = recycleViewAdapter.getItem(position);
        Toast.makeText(getContext(), "接入DroidPlugin", Toast.LENGTH_SHORT).show();
        if (PluginManager.getInstance().isConnected()) {
            try {
                if (PluginManager.getInstance().getPackageInfo(pluginBean.getApkPackageInfo().packageName, 0) != null) {
                    recycleViewAdapter.updateProgress(position, getString(R.string.open));
                    Toast.makeText(getActivity(), "已经安装了，不能再安装", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                int ret = PluginManager.getInstance().installPackage(pluginBean.getApkFile(), 0);
                                switch (ret) {
                                    case INASTALL_SUCCESS:
                                        recycleViewAdapter.updateProgress(position, getString(R.string.open));
                                        break;
                                }
                                Logger.i("安装结果：" + ret);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        } else {
            recycleViewAdapter.updateProgress(position, "初始化");
        }
    }


    private void download(final int position) {
        final PluginBean pluginBean = recycleViewAdapter.getItem(position);
        OkHttpUtils.getInstance().initWithDefaultClient().get().url(pluginBean.getPluginUrl()).build().execute(new FileCallBack(YConfig.DEFALULT_SAVE_LOCATION, pluginBean.getName() + ".apk") {
            /**
             * UI Thread
             * @param progress
             */
            public void inProgress(float progress, long total, int id) {
                Logger.i("下载进度：" + (int) (progress * 100));
                if (progress == 1) {
                    recycleViewAdapter.updateProgress(position, getString(R.string.install));
                    return;
                }
                recycleViewAdapter.updateProgress(position, (int) (progress * 100) + "");
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                Logger.i("下载错误：" + e.getMessage());
                Toast.makeText(getContext(), "下载错误，请重试", Toast.LENGTH_SHORT).show();
                recycleViewAdapter.updateProgress(position, getString(R.string.retry));
            }

            @Override
            public void onResponse(File apk, int id) {
                Logger.i("下载完成：" + id);
//                String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() +File.separator+ pluginBean.getName() + ".apk";
//                //下载完成判断是否已经安装过该应用，并且版本高于之前的版本，若不是则解析安装相关信息
                PackageManager pm = getActivity().getPackageManager();
                if (apk.exists() && apk.getPath().toLowerCase().endsWith(".apk")) {
                    final PackageInfo info = pm.getPackageArchiveInfo(apk.getPath(), 0);
                    pluginBean.setApkPackageInfo(pm, info, apk.getPath());
                    try {
                        if (PluginManager.getInstance().getPackageInfo(info.packageName, 0) != null) {
                            recycleViewAdapter.updateProgress(position, getString(R.string.open));
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    Logger.i(pluginBean.toString());
                }

            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
            }
        });
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//        startLoad();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    @Override
    public void onDestroy() {
        PluginManager.getInstance().removeServiceConnection(this);
        super.onDestroy();
    }
}


