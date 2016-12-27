package com.yy.dynamicyaya;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import com.orhanobut.logger.Logger;
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
public class MarketFragment extends BaseFragment implements BGAOnItemChildClickListener {

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

    public static MarketFragment getInstance(String areaCode) {
        MarketFragment mvViewPagerItemFragment = new MarketFragment();
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
        swipeRefreshLayout = getViewById(R.id.swipeRefreshLayout);
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
        plugins.add(new PluginBean(1, "wifi密码查看1", "http://down11.zol.com.cn/suyan/RootExplorer4.0.4.apk", "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png"));
        plugins.add(new PluginBean(2, "wifi密码查看2", "http://down11.zol.com.cn/suyan/RootExplorer4.0.4.apk", "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png"));
        plugins.add(new PluginBean(3, "wifi密码查看3", "http://down11.zol.com.cn/suyan/RootExplorer4.0.4.apk", "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png"));
        plugins.add(new PluginBean(4, "wifi密码查看4", "http://down11.zol.com.cn/suyan/RootExplorer4.0.4.apk", "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png"));
        setData(plugins);
//        OkHttpManager.getOkHttpManager().asyncGet(URLProviderUtil.getMVListUrl(areaCode, offset, size), new Callback() {
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
//
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

    }

    private boolean uninstall(int position) {
        return false;
    }

    private void install(final int position) {
        Toast.makeText(getContext(), "接入DroidPlugin", Toast.LENGTH_SHORT).show();
    }

    private void download(final int position) {
        PluginBean pluginBean = recycleViewAdapter.getItem(position);
        OkHttpUtils.getInstance().initWithDefaultClient().get().url(pluginBean.getPluginUrl()).build().execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), pluginBean.getName() + ".apk") {
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
            public void onResponse(File response, int id) {

            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
                Logger.i("下载完成：" + id);
            }
        });
    }

}


