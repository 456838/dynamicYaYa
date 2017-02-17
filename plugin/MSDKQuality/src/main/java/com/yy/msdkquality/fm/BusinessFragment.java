package com.yy.msdkquality.fm;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.ycloud.live.utils.CpuInfoUtils;
import com.ycloud.live.utils.HwCodecConfig;
import com.ycsignal.base.YYHandler;
import com.ycsignal.outlet.IProtoMgr;
import com.ycsignal.outlet.SDKParam;
import com.yy.msdkquality.R;
import com.yy.msdkquality.YConfig;
import com.yy.msdkquality.adapter.ChatMsgListAdapter;
import com.yy.msdkquality.bean.ChatEntity;
import com.yy.saltonframework.util.LogUtils;
import com.yy.saltonframework.widget.logcat.ColorTextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/8/5 13:06
 * Time: 13:06
 * Description:
 */
public class BusinessFragment extends Fragment {


    @BindView(R.id.tv_host_name)
    TextView tvHostName;
    @BindView(R.id.iv_host_icon)
    ImageView ivHostIcon;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;
    @BindView(R.id.quit)
    ImageView quit;
    @BindView(R.id.lst_message)
    ListView lstMessage;
    @BindView(R.id.tv_lines)
    ColorTextView tvLines;


    private ArrayList<ChatEntity> mArrayListChatEntity;
    private ChatMsgListAdapter mChatMsgListAdapter;
    private ArrayList<ChatEntity> mTmpChatList = new ArrayList<ChatEntity>();//缓冲队列
    public static final int MSG_CAMERA_PREVIEW_CREATED = 1;
    public static final int MSG_CAMERA_PREVIEW_STOP = 2;
    public static final int MSG_GET_TOKEN_FAILED = 3;
    public static final int MSG_SEND_SINGAL_FAILED = 4;
    private static final int REFRESH_LISTVIEW = 5;
    private static final int MINFRESHINTERVAL = 500;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IProtoMgr.instance().addHandlerWatcher(new MyYYHandler());
    }

    /**
     * yy消息处理
     */
    class MyYYHandler extends YYHandler {
        @MessageHandler(message = SDKParam.Message.messageId)
        public void onEvent(byte[] data) {
            Log.e("aa", new String(data));
            handleProtoMsg(new String(data));
        }
    }

    private void handleProtoMsg(String jsonStr) {
        try {
            JSONObject top = new JSONObject(jsonStr);
            int eventType = top.getInt("eventType");
            updateLog(ColorTextView.SIGNAL, "eventType:" + eventType);
            switch (eventType) {
                case 512:       //公屏信息,带context
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        IProtoMgr.instance().removeHandlerWatcher(new MyYYHandler());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_business, null, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mArrayListChatEntity = new ArrayList<ChatEntity>();
        if (HwCodecConfig.getH264DecoderSupport().name().equals("SUPPORTED")) {
            LogUtils.e("系统支持硬解");
            updateLog(ColorTextView.VIDEO, "系统支持硬解");
        } else if (HwCodecConfig.getH264DecoderSupport().name().equals("UNSUPPORTED")) {
            updateLog(ColorTextView.VIDEO, "系统不支持硬解");
            LogUtils.e("系统不支持硬解");
        } else if (HwCodecConfig.getH264DecoderSupport().name().equals("UNSUPPORTED")) {
            updateLog(ColorTextView.VIDEO, "无法确定系统是否支持硬解");
            LogUtils.e("无法确定系统是否支持硬解");
        }

        if (HwCodecConfig.getH264EncoderSupport().name().equals("SUPPORTED")) {
            updateLog(ColorTextView.VIDEO, "系统支持硬编");
        } else if (HwCodecConfig.getH264EncoderSupport().name().equals("UNSUPPORTED")) {
            updateLog(ColorTextView.VIDEO, "系统不支持硬编");
        } else if (HwCodecConfig.getH264EncoderSupport().name().equals("UNSUPPORTED")) {
            updateLog(ColorTextView.VIDEO, "无法确定系统是否支持硬编");
        }
        updateLog(ColorTextView.OTHER, "cpuName:" + CpuInfoUtils.getCpuName() + ",info:" + CpuInfoUtils.readCpuInfo());
//        mChatMsgListAdapter = new ChatMsgListAdapter(getActivity(), lstMessage, mArrayListChatEntity);
//        lstMessage.setAdapter(mChatMsgListAdapter);
    }

    public void updateLog(int TAG, String message) {
        if (tvLines != null) {
            tvLines.refreshLogcat(TAG, message);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void showExitDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(getActivity()).create();
        dlg.show();
        Window window = dlg.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.alert_dialog);
        // 为确认按钮添加事件,执行退出应用操作
        TextView txt_dialog_title = (TextView) window.findViewById(R.id.txt_dialog_title);
        txt_dialog_title.setText("您确定要退出直播间吗？");
        TextView txt_dialog_cancel = (TextView) window
                .findViewById(R.id.txt_cancel);
        TextView txt_dialog_ok = (TextView) window
                .findViewById(R.id.txt_ok);

        txt_dialog_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.cancel();
            }
        });
        txt_dialog_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlg.cancel();
                getActivity().finish();
            }
        });

        dlg.setCancelable(true);
    }

    private boolean mBoolRefreshLock = false;
    private boolean mBoolNeedRefresh = false;

//    /**
//     * 发消息弹出框
//     */
//    private void inputMsgDialog() {
//        InputTextMsgDialog inputMsgDialog = new InputTextMsgDialog(this, R.style.inputdialog, mSid);
//        WindowManager windowManager = getWindowManager();
//        Display display = windowManager.getDefaultDisplay();
//        WindowManager.LayoutParams lp = inputMsgDialog.getWindow().getAttributes();
//        lp.width = (int) (display.getWidth()); //设置宽度
//        inputMsgDialog.getWindow().setAttributes(lp);
//        inputMsgDialog.setCancelable(true);
//        inputMsgDialog.show();
//    }


    public void refreshText(String text, String name) {
        if (text != null) {
            refreshTextListView(name, text, YConfig.TEXT_TYPE);
        }
    }

    /**
     * 消息刷新显示
     *
     * @param name    发送者
     * @param context 内容
     * @param type    类型 （上线线消息和 聊天消息）
     */
    public void refreshTextListView(String name, String context, int type) {
        ChatEntity entity = new ChatEntity();
        entity.setSenderName(name);
        entity.setContext(context);
        entity.setType(type);
        //mArrayListChatEntity.add(entity);
        notifyRefreshListView(entity);
        //mChatMsgListAdapter.notifyDataSetChanged();

        lstMessage.setVisibility(View.VISIBLE);
        Logger.d("refreshTextListView height " + lstMessage.getHeight());

        if (lstMessage.getCount() > 1) {
            if (true)
                lstMessage.setSelection(0);
            else
                lstMessage.setSelection(lstMessage.getCount() - 1);
        }
    }

    /**
     * 通知刷新消息ListView
     */
    private void notifyRefreshListView(ChatEntity entity) {
        mBoolNeedRefresh = true;
        mTmpChatList.add(entity);
        if (mBoolRefreshLock) {
            return;
        } else {
            doRefreshListView();
        }
    }

    private TimerTask mTimerTask = null;
    private final Timer mTimer = new Timer();

    /**
     * 刷新ListView并重置状态
     */
    private void doRefreshListView() {
        if (mBoolNeedRefresh) {
            mBoolRefreshLock = true;
            mBoolNeedRefresh = false;
            mArrayListChatEntity.addAll(mTmpChatList);
            mTmpChatList.clear();
            mChatMsgListAdapter.notifyDataSetChanged();

            if (null != mTimerTask) {
                mTimerTask.cancel();
            }
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    Logger.v("doRefreshListView->task enter with need:" + mBoolNeedRefresh);
                    mHandler.sendEmptyMessage(REFRESH_LISTVIEW);
                }
            };
            //mTimer.cancel();
            mTimer.schedule(mTimerTask, MINFRESHINTERVAL);
        } else {
            mBoolRefreshLock = false;
        }
    }


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_LISTVIEW:
                    doRefreshListView();
                    break;
                case 6:
                    break;
            }
            return false;
        }
    });


    @OnClick({R.id.tv_host_name, R.id.iv_host_icon, R.id.relativeLayout, R.id.quit, R.id.tv_lines})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_host_name:
                break;
            case R.id.iv_host_icon:
                break;
            case R.id.relativeLayout:
                break;
            case R.id.quit:
                showExitDialog();
                break;
            case R.id.tv_lines:
                break;
        }
    }
}
