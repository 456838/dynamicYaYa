package com.yy.msdkquality.aty;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.ycloud.live.YCConstant;
import com.ycloud.live.YCMedia;
import com.ycloud.live.YCMediaRequest;
import com.ycloud.live.YCMessage;
import com.ycloud.live.video.YCCameraStatusListener;
import com.ycloud.live.video.YCVideoPreview;
import com.ycloud.live.video.YCVideoViewLayout;
import com.ycloud.live.yyproto.ProtoEvent;
import com.ycsignal.base.YYHandler;
import com.ycsignal.outlet.IProtoMgr;
import com.ycsignal.outlet.SDKParam;
import com.yy.IFragmentListener;
import com.yy.msdkquality.ChannelVideoLayoutController;
import com.yy.msdkquality.R;
import com.yy.msdkquality.YConfig;
import com.yy.msdkquality.adapter.MyViewPagerAdapter;
import com.yy.msdkquality.engine.SDKEngine;
import com.yy.msdkquality.engine.TokenEngine;
import com.yy.msdkquality.fm.BusinessFragment;
import com.yy.msdkquality.fm.VideoFragment;
import com.yy.msdkquality.widget.DoubleLayout;
import com.yy.saltonframework.util.LogUtils;
import com.yy.saltonframework.widget.logcat.ColorTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/11/11 15:17
 * Time: 15:17
 * Description:
 */
public class ChannelTestLivingRoomActivity extends FragmentActivity implements IFragmentListener {

    private static final int UPDATE_LOG = 0x100;
    private ViewPager viewPager;
    private List<Fragment> fragmentList;
    private ChannelVideoLayoutController mChannelVideoController = null;
//    private boolean mAudioLinkConnected = false;
    private boolean mVideoLinkConnected = false;
//    private YCVideoPreview mVideoPreview = null;
    public int mUid = 0;
    public int mSid = 0;
    private int mWanIp = 0;
    private int mWanIsp = 0;
    private int mAreaType = 0;
    private byte[] httpToken = null;
    private int mCameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private YCVideoViewLayout mViewLay;
    private YCVideoViewLayout mViewLay2;
    private DoubleLayout mDoubleLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //屏幕常亮
        setContentView(R.layout.aty_slide_video_test);
        initView();
        initData();
    }

    public static interface Operation {
        public static final int MSG_CAMERA_PREVIEW_CREATED = 1;
        public static final int MSG_CAMERA_PREVIEW_STOP = 2;
        public static final int MSG_GET_TOKEN_FAILED = 3;
        public static final int MSG_SEND_SINGAL_FAILED = 4;
        public static final int MSG_CAMERA_PREVIEW_SUCCESS = 5;
    }

    VideoFragment mVideoFragment;
    BusinessFragment mBusinessFragment;
    private FrameLayout mCameraPreview;

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        fragmentList = new ArrayList<>();
        mVideoFragment = new VideoFragment();
        mBusinessFragment = new BusinessFragment();
        fragmentList.add(mVideoFragment);
        fragmentList.add(mBusinessFragment);
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager(), fragmentList));
        viewPager.setCurrentItem(0);
        mViewLay = (YCVideoViewLayout) findViewById(R.id.yvLayout);
        mViewLay2 = (YCVideoViewLayout) findViewById(R.id.yvLayout2);
        mDoubleLayout = (DoubleLayout) findViewById(R.id.double_layout_video);
        mCameraPreview = (FrameLayout) findViewById(R.id.camera_preview);
    }

    private void initData() {

        Intent intent = this.getIntent();
        Random random = new Random();
        int x = random.nextInt(899999);
        int number = x + 100000;
        mUid = intent.getIntExtra("uid", number);
        mSid = intent.getIntExtra("sid", 5587);
        //注册信令事件处理
        IProtoMgr.instance().addHandlerWatcher(mSignalHandler);
        //摄像头事件回调
        YCMedia.getInstance().setCameraStatusListener(new YCCameraStatusListener() {
            @Override
            public void onPreviewCreated(YCVideoPreview ycVideoPreview) {
                printToLog(ColorTextView.SDK, "预览视图已经创建！");
                Message msg = mMediaHandler.obtainMessage(Operation.MSG_CAMERA_PREVIEW_CREATED, ycVideoPreview);
                mMediaHandler.sendMessage(msg);
            }

            @Override
            public void onPreviewStartSuccess() {
                printToLog(ColorTextView.SDK, "预览视图打开成功！");
                mMediaHandler.sendMessage(mMediaHandler.obtainMessage(Operation.MSG_CAMERA_PREVIEW_SUCCESS));
            }

            @Override
            public void onPreviewStartFailed() {
                printToLog(ColorTextView.SDK, "预览视图打开失败！");
            }

            @Override
            public void onPreviewStopped() {
                printToLog(ColorTextView.SDK, "预览视图停止！");
//                mVideoPreview = null;
                Message msg = mMediaHandler.obtainMessage();
                msg.what = Operation.MSG_CAMERA_PREVIEW_STOP;
                mMediaHandler.sendMessage(msg);
            }

            @Override
            public void onOpenCameraFailed(FailReason failReason, String reasonText) {
                printToLog(ColorTextView.SDK, "打开摄像头失败：" + reasonText);
            }

            @Override
            public void onVideoRecordStarted() {
                printToLog(ColorTextView.SDK, "开始视频录制");
            }

            @Override
            public void onVideoRecordStopped() {
                printToLog(ColorTextView.SDK, "视频录制失败");
            }
        });
        //注册媒体事件处理
        YCMedia.getInstance().addMsgHandler(mMediaHandler);
        //媒体参数配置
        loadMediaConfig();
//        LogUtils.e("hwsupport:"+HwCodecConfig.getH264DecoderSupport().name());
    }

    /**
     * 加载媒体参数
     */
    private void loadMediaConfig() {
        LogUtils.e("默认加载低流模式");
        // 参数配置
        Map<Integer, Integer> configs = new HashMap<Integer, Integer>();
        configs.put(YCConstant.ConfigKey.LOGIN_MODEL, YCConstant.LOGIN_MODEL_LOWLATE);
        // upload bitrate control
        configs.put(YCConstant.ConfigKey.UPLOAD_MIN_CODERATE, 350);
        configs.put(YCConstant.ConfigKey.UPLOAD_MAX_CODERATE, 800);
        configs.put(YCConstant.ConfigKey.UPLOAD_CUR_CODERATE, 700);
        configs.put(YCConstant.ConfigKey.UPLOAD_TOTAL_CODERATE, 1200);
        configs.put(YCConstant.ConfigKey.UPLOAD_USE_CRCONTROL, 1);
        configs.put(YCConstant.ConfigKey.VIDEO_HARDWARE_DECODE,0);
        configs.put(YCConstant.ConfigKey.VIDEO_HARDWARE_ENCODE,0);
        configs.put(YCConstant.ConfigKey.VIDEO_RESOLUTION_HEIGHT, 1280);
        configs.put(YCConstant.ConfigKey.VIDEO_RESOLUTION_WIDTH, 720);

        // video setting
        configs.put(YCConstant.ConfigKey.VIDEO_RECORD_QUALITY,
                YCConstant.MEDIA_QUALITY_MEDIUM);
        configs.put(YCConstant.ConfigKey.VIDEO_MIN_BUFFER, 100);
        configs.put(YCConstant.ConfigKey.VIDEO_AUTO_SUBSCRIBE_STREAM, 1);    //auto subscribe stream

        // audio setting
        configs.put(YCConstant.ConfigKey.AUDIO_RECORD_QUALITY,
                YCConstant.MEDIA_QUALITY_HIGH);
        // configs.put(YCConstant.ConfigKey.AUDIO_RECORD_QUALITY,
        // YCConstant.MEDIA_QUALITY_MUSIC);
        configs.put(YCConstant.ConfigKey.AUDIO_MIN_BUFFER, 100);

        YCMedia.getInstance().requestMethod(
                new YCMediaRequest.YCSetConfigs(YConfig.mAppKey, configs));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mChannelVideoController == null) {
            mChannelVideoController = new ChannelVideoLayoutController(mViewLay, mViewLay2, mDoubleLayout);
        } else {
            mChannelVideoController.resumeSubscribeVideo();
        }
        //信令登录
        signalLogin(mUid, mSid);
        if (mSwitchEnable == true && mIsCameraStarted) {
            YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStartCamera(mCameraType));
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        printToLog(ColorTextView.OTHER, "调用了onPause");
        if (mSwitchEnable == true) {
            YCMedia.getInstance().onPauseCamera();
        }
        if (mChannelVideoController != null) {
            mChannelVideoController.onPauseSubscribeVideo();
        }
    }

    private void signalLogin(final int uid, int sid) {
        httpToken = TokenEngine.genToken(uid, sid, 3);
        if (httpToken != null) {
            String tokenHex = "";
            for (int i = 0; i < httpToken.length; i++) {
                tokenHex = String.format("%s%02x ", tokenHex, httpToken[i]);
            }
            SDKEngine.loginToProtoServerByUid(uid, tokenHex);
            SDKEngine.joinQueue(mSid,tokenHex);
        } else {
            Toast.makeText(getApplicationContext(), "登录Token为空", Toast.LENGTH_SHORT).show();
            printToLog(ColorTextView.OTHER, "登录Token为空");
        }
    }


    public boolean mFirstClickPause = false;       //用于没有开播之前停止视频的播放
    public boolean mMediaPause = false;            //用于视频开播后停止视频的播放
    private boolean mIsCameraStarted = false;
    private boolean mIsVideoPublished = false;

    @Override
    public void setOnViewClick(int tag, View v) {
        switch (tag) {
            case R.id.start:
                LogUtils.e("fm item clicked: start btn");
                //判断视频是否在播放中
                mFirstClickPause = true;
                boolean bSelect = v.isSelected();
                if (bSelect) {//一开始是unselected
                    mMediaPause = true;
                    mChannelVideoController.resumeSubscribeVideo();
                    printToLog(ColorTextView.SDK, "调用了恢复订阅视频方法");
                    ((ImageView) v).setImageResource(R.drawable.jc_click_pause_selector);
                } else {
                    mMediaPause = false;
                    mChannelVideoController.onPauseSubscribeVideo();
                    printToLog(ColorTextView.SDK, "调用了暂停订阅视频方法");
                    ((ImageView) v).setImageResource(R.drawable.jc_click_play_selector);
                }
                v.setSelected(!bSelect);
                break;
            case R.id.tv_property:
                LinearLayout ll_property = (LinearLayout) v.findViewById(R.id.ll_property);
                LinearLayout ll_video = (LinearLayout) v.findViewById(R.id.ll_video);
                if (ll_property.getVisibility() == View.VISIBLE) {   //可见
                    ll_property.setVisibility(View.INVISIBLE);
                    ll_video.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 0));
                    //重新进入频道
                    Toast.makeText(getApplicationContext(), "视频效果正在激活,效果未设置", Toast.LENGTH_SHORT).show();
//                    reJoinChannel(mSid, mSubsid, mApp.getMediaConfigBean().build());
                } else {
                    ll_property.setVisibility(View.VISIBLE);

                    ll_video.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
                }
                break;
            case R.id.iv_fullscreen:
                boolean selected = v.isSelected();
                if (selected) {
                    WindowManager.LayoutParams attr = getWindow().getAttributes();
                    attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    getWindow().setAttributes(attr);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
                } else {
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    getWindow().setAttributes(lp);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
                }
                //mChannelVideoController.updateLayout(500);
                v.setSelected(!selected);
                break;
            case R.id.iv_audio_mute:
                boolean selected2 = v.isSelected();
//                mApp.getMediaVideo().switchVoice(selected2);
                v.setSelected(!selected2);
                break;
            case R.id.rl_publish_video:
                //广播视频
                if (!mVideoLinkConnected) {
                    Toast.makeText(getApplicationContext(), R.string.err_no_server,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mIsCameraStarted) {
                    printToLog(ColorTextView.SDK, "停止服务器录制");
                    //停止服务器录制
                    YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStopServerRecord());
                    printToLog(ColorTextView.SDK, "停止视频流发布");
                    //停止视频流发布
                    YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStopPublishVideo());
                    printToLog(ColorTextView.SDK, "停止摄像头");
                    //停止摄像头
                    YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStopCamera());
                    ((TextView) v.findViewById(R.id.tv_sex)).setText("已开播");
//                    mBtnVideoSwitch.setImageDrawable(getResources().getDrawable(R.drawable.btn_video_off));
                    //mCameraPreview.removeAllViews();
                    //mCameraPreview.setVisibility(View.INVISIBLE);
                    mIsCameraStarted = false;
                    mIsVideoPublished = false;
                } else {
                    //mCameraPreview.setVisibility(View.VISIBLE);
                    YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStartCamera(Camera.CameraInfo.CAMERA_FACING_BACK));
//                    mBtnVideoSwitch.setImageDrawable(getResources().getDrawable(R.drawable.btn_video_on));
                    ((TextView) v.findViewById(R.id.tv_sex)).setText("未开播");
                    mIsCameraStarted = true;
                }

                break;
            case R.id.tv_openMic:
                if (mIsOpenMic) {
                    YCMedia.getInstance().requestMethod(new YCMediaRequest.YCCloseMic());
                    AwesomeTextView tvOpenMic = (AwesomeTextView) v.findViewById(R.id.tv_openMic);
                    tvOpenMic.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                    mIsOpenMic = false;
                    printToLog(ColorTextView.SDK, "调用关闭麦克风接口");
                } else {
                    YCMedia.getInstance().requestMethod(new YCMediaRequest.YCOpenMic());
                    AwesomeTextView tvOpenMic = (AwesomeTextView) v.findViewById(R.id.tv_openMic);
                    tvOpenMic.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                    mIsOpenMic = true;
                    printToLog(ColorTextView.SDK, "调用打开麦克风接口");

                }
                break;
            case R.id.tv_muteaudio:
                if (mIsAudioMute) {
                    YCMedia.getInstance().requestMethod(new YCMediaRequest.YCMuteAudio(false));
                    AwesomeTextView tvMuteAudio = (AwesomeTextView) v.findViewById(R.id.tv_muteaudio);
                    tvMuteAudio.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                    printToLog(ColorTextView.SDK, "静音已关闭");
                    mIsAudioMute = false;
                } else {
                    YCMedia.getInstance().requestMethod(new YCMediaRequest.YCMuteAudio(true));
                    AwesomeTextView tvMuteAudio = (AwesomeTextView) v.findViewById(R.id.tv_muteaudio);
                    tvMuteAudio.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                    printToLog(ColorTextView.SDK, "静音已打开");
                    mIsAudioMute = true;
                }
                break;
            case R.id.tv_lvzhi:
                if (!mVideoLinkConnected) {
                    Toast.makeText(getApplicationContext(), R.string.err_no_server,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mIsCameraStarted) {
                    printToLog(ColorTextView.SDK, "停止录制");
                    YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStopCamera());
                    YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStopPublishVideo());
                    AwesomeTextView tv_lvzhi = (AwesomeTextView) v.findViewById(R.id.tv_lvzhi);
                    tv_lvzhi.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
                    mCameraPreview.setVisibility(View.INVISIBLE);
                    mIsCameraStarted = false;
                    mIsVideoPublished = false;
                } else {
                    printToLog(ColorTextView.SDK, "开始录制");
                    mCameraPreview.setVisibility(View.VISIBLE);
                    mCameraType = Camera.CameraInfo.CAMERA_FACING_BACK;
//                    YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStartPublishVideo());
                    YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStartCamera(mCameraType));
                    AwesomeTextView tv_lvzhi = (AwesomeTextView) v.findViewById(R.id.tv_lvzhi);
                    tv_lvzhi.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                    mIsCameraStarted = true;
                }
                break;
            case R.id.tv_camera:
                if (Camera.getNumberOfCameras() == 1)
                    return;

                if (mSwitchEnable == false)
                    return;
                if (!mVideoLinkConnected) {
                    Toast.makeText(getApplicationContext(), R.string.err_no_server,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                mSwitchEnable = false;
                mCameraType = mCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT;
                YCMedia.getInstance().requestMethod(new YCMediaRequest.YCSwitchCamera(mCameraType));
                break;
        }
    }
    private boolean mIsOpenMic = false;
    private boolean mIsAudioMute = false;
    private boolean mSwitchEnable;

    private void handleCameraPreviewReady(YCVideoPreview v) {

        printToLog(ColorTextView.OTHER, "预览刷新");

        ((View) v).setVisibility(View.VISIBLE);
        //v.bringToFront();
        mCameraPreview.addView((View) v);


        if (!mIsVideoPublished) {
            //开始发布本地视频流
            YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStartPublishVideo());
            //programId 唯一识别字符串, 生成字符串的规则是: channelid_appid_spkuid_timestamp
            int mode = YCMessage.ServerRecordMode.SRM_CHANNEL;
            String businessId = String.format("%d_%d_%d_%d", mSid, YConfig.mAppKey, mUid, System.currentTimeMillis());
            Set<Integer> recordUidSet = new LinkedHashSet<Integer>();
            recordUidSet.add(12345);
            recordUidSet.add(2345);
            printToLog(ColorTextView.AUDIO, "打开服务器录制: " + mode + " businessId: " + businessId + " uidSetCount: " + recordUidSet.size());
            YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStartServerRecord(mode, businessId, recordUidSet));
            mIsVideoPublished = true;
        }
    }

    private YYHandler mSignalHandler = new YYHandler() {
        @MessageHandler(message = SDKParam.Message.messageId)
        public void onEvent(byte[] data) {
            ProtoEvent.ProtoEventBase base = new ProtoEvent.ProtoEventBase();
            base.unmarshal(data);
            switch (base.eventType) {
                case ProtoEvent.EventType.PROTO_EVENT_LOGIN_RES: {
                    onLoginRes(data);
                    break;
                }
                default: {
                    Log.i("YCSdk", "LiveActivity::Signal_Handler: Not care eventType:" + base.eventType);
                    printToLog(ColorTextView.SDK, "未能处理信令消息类型：" + base.eventType);
                }
            }
        }

    };

    private void onLoginRes(byte[] data) {
        ProtoEvent.ProtoEvtLoginRes res = new ProtoEvent.ProtoEvtLoginRes();
        res.unmarshal(data);

        if (res.res != ProtoEvent.ProtoEvtLoginRes.LOGIN_SUCESS) {
            LogUtils.e("applogin failed Res:" + res.res);
            SDKEngine.logoutSignal();
            Toast.makeText(getApplicationContext(), String.format("Signal Login failed %d", res.res),
                    Toast.LENGTH_SHORT).show();
            printToLog(ColorTextView.SIGNAL, "信令登录失败：" + res.toString());
            return;
        }

        mWanIp = res.uClientIp;
        mWanIsp = res.uClientIsp;
        mAreaType = res.uClientAreaType;
        printToLog(ColorTextView.SIGNAL,"信令登录成功, uid:"+ res.uid + ",wanIp:" + res.uClientIp + ",mWanIsp:" + res.uClientIsp + ",mAreaType:" + res.uClientAreaType);
//        LogUtils.e("applogin successed, uid:" + res.uid + ",wanIp:" + res.uClientIp + ",mWanIsp:" + res.uClientIsp + ",mAreaType:" + res.uClientAreaType);
        SDKEngine.loginMedia(YConfig.mAppKey, mSid, mUid, mWanIp, mWanIsp, mAreaType, httpToken);
    }

    public void printToLog(int tag, String msg) {
        LogUtils.i(msg);
        Message message = Message.obtain();
        message.what = UPDATE_LOG;
        message.obj = msg;
        message.arg1 = tag;
        mMediaHandler.sendMessage(message);
//        mBusinessFragment.updateLog(msg + "\n");
    }

    private void handlePreviewStoped() {
        printToLog(ColorTextView.OTHER, "handlePreviewStoped");
        mCameraPreview.removeAllViews();
    }

    Handler mMediaHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_LOG:
                    String msgStr = (String) msg.obj;
                    int msgTag = msg.arg1;
                    mBusinessFragment.updateLog(msgTag, msgStr);

                    break;
                case Operation.MSG_CAMERA_PREVIEW_CREATED:
                    handleCameraPreviewReady((YCVideoPreview) msg.obj);
                    break;

                case Operation.MSG_CAMERA_PREVIEW_STOP:
                    handlePreviewStoped();
                    break;

                case Operation.MSG_GET_TOKEN_FAILED:
                    Toast.makeText(getApplicationContext(), "http get token failed", Toast.LENGTH_SHORT).show();
                    break;
                case Operation.MSG_CAMERA_PREVIEW_SUCCESS:

                    mSwitchEnable = true;
                    break;


                case Operation.MSG_SEND_SINGAL_FAILED:
                    Toast.makeText(getApplicationContext(), "send login ap failed:", Toast.LENGTH_SHORT).show();

                    break;
                case YCMessage.MsgType.onVideoLinkInfoNotity:
                    YCMessage.VideoLinkInfo videoLinkInfo = (YCMessage.VideoLinkInfo) msg.obj;
                    String baseInfo = String.format("appid:%d,ip:%s,port:%d", videoLinkInfo.appId, videoLinkInfo.ip, videoLinkInfo.port);
                    if (videoLinkInfo.state == YCMessage.VideoLinkInfo.Connect) {
                        printToLog(ColorTextView.VIDEO, "视频连接中:" + baseInfo);
                    } else if (videoLinkInfo.state == YCMessage.VideoLinkInfo.Connected) {
                        printToLog(ColorTextView.VIDEO, "视频已经连接中:" + baseInfo);
                    } else if (videoLinkInfo.state == YCMessage.VideoLinkInfo.Disconnected) {
                        printToLog(ColorTextView.VIDEO, "视频断开连接:" + baseInfo);
                    } else if (videoLinkInfo.state == YCMessage.VideoLinkInfo.ServerReject) {
                        printToLog(ColorTextView.VIDEO, "服务器拒绝视频连接:" + baseInfo);
                    }
                    mVideoLinkConnected = videoLinkInfo.state == YCMessage.VideoLinkInfo.Connected ? true : false;
                    //放在business里面
                    break;
                case YCMessage.MsgType.onVideoStreamInfoNotify:
                    YCMessage.VideoStreamInfo streamInfo = (YCMessage.VideoStreamInfo) msg.obj;
                    long userGroupId = streamInfo.userGroupId;
                    int publishId = streamInfo.publishId;
                    int audoSubscribe = streamInfo.audoSubscribe;
                    int state = streamInfo.state;
                    long streamId = streamInfo.streamId;
                    if (state == YCMessage.VideoStreamInfo.Arrive) {

                        printToLog(ColorTextView.VIDEO, "视频流到来:" + String.format("userGroupId:%d,publishId:%d,audoSubscribe:%d,streamId:%d", userGroupId, publishId, audoSubscribe, streamId));
                    } else if (state == YCMessage.VideoStreamInfo.Start) {
                        printToLog(ColorTextView.VIDEO, "视频流开始:" + String.format("userGroupId:%d,publishId:%d,audoSubscribe:%d,streamId:%d", userGroupId, publishId, audoSubscribe, streamId));
                    } else if (state == YCMessage.VideoStreamInfo.Stop) {
                        printToLog(ColorTextView.VIDEO, "视频流停止:" + String.format("userGroupId:%d,publishId:%d,audoSubscribe:%d,streamId:%d", userGroupId, publishId, audoSubscribe, streamId));
                    }
                    mVideoFragment.setText(R.id.anchor, "" + streamInfo.publishId);
                    break;
                case YCMessage.MsgType.onVideoRenderInfoNotify:
                    YCMessage.VideoRenderInfo videoRenderInfo = (YCMessage.VideoRenderInfo) msg.obj;

                    if (videoRenderInfo.state == YCMessage.VideoRenderInfo.Start) {
                        printToLog(ColorTextView.VIDEO, "开始视频渲染");
                    } else {
                        printToLog(ColorTextView.VIDEO, "停止视频渲染");
                    }
                    break;
                case YCMessage.MsgType.onVideoDownlinkPlrNotify:
                    YCMessage.VideoDownlinkPlrInfo videoDownlinkPlrInfo = (YCMessage.VideoDownlinkPlrInfo) msg.obj;
                    int uid = videoDownlinkPlrInfo.uid;
                    float plr = videoDownlinkPlrInfo.plr;
                    printToLog(ColorTextView.VIDEO, "视频下载链路，uid:" + uid + ",丢包率:" + plr);
                    break;
                case YCMessage.MsgType.onVideoliveBroadcastNotify:
                    YCMessage.VideoliveBroadcastInfo videoliveBroadcastInfo = (YCMessage.VideoliveBroadcastInfo) msg.obj;
                    boolean hasVideo = videoliveBroadcastInfo.hasVideo;
                    printToLog(ColorTextView.VIDEO, "hasVideo:" + hasVideo);
                    break;
                case YCMessage.MsgType.onVideoCodeRateNotify:
                    YCMessage.VideoCodeRateInfo videoCodeRateInfo = (YCMessage.VideoCodeRateInfo) msg.obj;
                    int appid = videoCodeRateInfo.appid;
                    Map<Integer, Integer> codeRateList = videoCodeRateInfo.codeRateList;
                    printToLog(ColorTextView.VIDEO, "appid:" + appid + ",codeRateList.size:" + codeRateList.size());
                    break;
                case YCMessage.MsgType.onVideoCodeRateChange:
                    YCMessage.VideoCodeRateChange videoCodeRateChange = (YCMessage.VideoCodeRateChange) msg.obj;
                    int codeRate = videoCodeRateChange.codeRate;
                    int result = videoCodeRateChange.result;
                    String retStr = result == 1 ? "change" : "unchanged";
                    printToLog(ColorTextView.VIDEO, "onVideoCodeRateChange，result:" + retStr + ",codeRate:" + codeRate);
                    break;
                case YCMessage.MsgType.onVideoMetaInfoNotify:
                    YCMessage.VideoMetaInfo videoMetaInfo = (YCMessage.VideoMetaInfo) msg.obj;
                    long streamId4 = videoMetaInfo.streamId;
                    int bitRate = videoMetaInfo.bitRate;
                    int frameRate2 = videoMetaInfo.frameRate;
                    printToLog(ColorTextView.VIDEO, "Meta信息，streamId:" + streamId4 + ",bitRate:" + bitRate + ",frameRate" + frameRate2);
                    break;
                case YCMessage.MsgType.onNoVideoNotify:
                    YCMessage.NoVideoInfo noVideoInfo = (YCMessage.NoVideoInfo) msg.obj;
                    long streamId3 = noVideoInfo.streamId;
                    int reason = noVideoInfo.reason;
                    printToLog(ColorTextView.VIDEO, "onNoVideoNotify,streamID:" + streamId3 + ",reason:" + SDKEngine.getNoVideoReason(reason));
                    break;
                case YCMessage.MsgType.onVideoDecodeSlowNotify:

                    printToLog(ColorTextView.VIDEO, "onVideoDecodeSlowNotify");
                    break;
                case YCMessage.MsgType.onVideoFrameLossNotify:
                    YCMessage.VideoFrameLossInfo videoFrameLossInfo = (YCMessage.VideoFrameLossInfo) msg.obj;
                    long streamId2 = videoFrameLossInfo.streamId;
                    int duration = videoFrameLossInfo.duration;
                    int frameRate = videoFrameLossInfo.frameRate;
                    int playCnt = videoFrameLossInfo.playCnt;
                    int netLossCnt = videoFrameLossInfo.netLossCnt;
                    int discardCnt = videoFrameLossInfo.discardCnt;
                    printToLog(ColorTextView.VIDEO, "streamId:" + streamId2 + ",统计间隔:" + duration + ",丢失帧率:" + frameRate + ",播放帧数:" + playCnt + ",网络丢失帧率：" + netLossCnt + ",主动丢失帧率:" + discardCnt);
                    break;
                case YCMessage.MsgType.onVideoCodeRateLevelSuggest:
                    printToLog(ColorTextView.VIDEO, "onVideoCodeRateLevelSuggest");
                    break;
                case YCMessage.MsgType.onVideoPublishStatus:
                    YCMessage.VideoPublishStatus videoPublishStatus = (YCMessage.VideoPublishStatus) msg.obj;
                    String ret = videoPublishStatus.status == YCMessage.VideoPublishStatus.kVideoPublishSucceed ? "成功" : "失败";
                    printToLog(ColorTextView.VIDEO, "视频发布状态:" + ret);
                    break;
                case YCMessage.MsgType.onVideoUplinkLossRateNotify:
                    YCMessage.VideoUplinkLossRateInfo videoUplinkLossRateInfo = (YCMessage.VideoUplinkLossRateInfo) msg.obj;
                    printToLog(ColorTextView.VIDEO, "视频上行链路丢帧：" + videoUplinkLossRateInfo.lossRate + "，延迟：" + videoUplinkLossRateInfo.rtt);
                    break;
                case YCMessage.MsgType.onServerRecodRes:
                    YCMessage.ServerRecodRes serverRecodRes = (YCMessage.ServerRecodRes) msg.obj;
                    printToLog(ColorTextView.VIDEO, "服务器录制结果：appid:" + serverRecodRes.appId + ",businessId:" + serverRecodRes.businessId + ",programId:" + serverRecodRes.programId);
                    break;
                case YCMessage.MsgType.onVideoViewerStatInfo:
                    printToLog(ColorTextView.VIDEO, "视频下行统计回调,onVideoViewerStatInfo");
                    YCMessage.VideoViewerStatInfo videoViewerStatInfo = (YCMessage.VideoViewerStatInfo) msg.obj;
                    int uid2 = 0;
                    Map<Integer, Integer> statMap = new HashMap();
                    for (int i = 0; i < statMap.size(); i++) {
                        printToLog(ColorTextView.VIDEO, "statMap:" + statMap.get(i).intValue());
                    }
                    Map<Long, YCMessage.StreamStatInfo> streamMap = new HashMap();
                    for (int j = 0; j < streamMap.size(); j++) {
                        int len = streamMap.get(j).dataMap.size();
                        for (int k = 0; k < len; k++) {
                            printToLog(ColorTextView.VIDEO, "streamMap:" + streamMap.get(j).dataMap.get(k).intValue());
                        }
                    }
                    break;
                case YCMessage.MsgType.onVideoPublisherStatInfo:
                    YCMessage.VideoPublisherStatInfo videoPublisherStatInfo = (YCMessage.VideoPublisherStatInfo) msg.obj;
                    printToLog(ColorTextView.VIDEO, "视频发布者：" + videoPublisherStatInfo.uid);
                    break;
                case YCMessage.MsgType.onVideoMetaDataInfo:
                    YCMessage.VideoMetaDataInfo videoMetaDataInfo = (YCMessage.VideoMetaDataInfo) msg.obj;
                    int publishId2 = videoMetaDataInfo.publishId;
                    long streamId5 = videoMetaDataInfo.streamId;
                    long userGroupId2 = videoMetaDataInfo.userGroupId;
//                    Map<Byte, Integer> metaDatas = videoMetaDataInfo.metaDatas;
//                    for (int i = 0; i < metaDatas.size(); i++) {
//                        printToLog("metaDatas:" + metaDatas.get(i).byteValue());
//                    }
                    printToLog(ColorTextView.VIDEO, "Video元消息，publishId:" + publishId2 + ",streamId5:" + streamId5 + ",userGroupId2:" + userGroupId2);
                    break;
                case YCMessage.MsgType.onAudioLinkInfoNotity:
                    YCMessage.AudioLinkInfo audioLinkInfo = (YCMessage.AudioLinkInfo) msg.obj;
                    int state3 = audioLinkInfo.state;
                    int ip = audioLinkInfo.ip;
                    short port = audioLinkInfo.port;
                    printToLog(ColorTextView.AUDIO, "onAudioLinkInfoNotity, state: " + audioLinkInfo.state + ",ip:" + ip + ",port:" + port);
                    break;
                case YCMessage.MsgType.onAudioSpeakerInfoNotity:
                    YCMessage.AudioSpeakerInfo speakerInfo = (YCMessage.AudioSpeakerInfo) msg.obj;
//                    LogUtils.d("onAudioSpeakerInfoNotity, state: " + speakerInfo.state);
                    String stateStr2 = speakerInfo.state == YCMessage.AudioSpeakerInfo.Start ? "开始" : "接收";
                    handleAudioSpeaker(speakerInfo.state);
                    printToLog(ColorTextView.AUDIO, "说话者信息，uid:" + speakerInfo.uid + ",状态:" + stateStr2);
                    break;
                case YCMessage.MsgType.onMicStateInfoNotify:
                    YCMessage.MicStateInfo micStateInfo = (YCMessage.MicStateInfo) msg.obj;
                    LogUtils.d("onMicStateInfoNotify, state: " + micStateInfo.state);
                    printToLog(ColorTextView.AUDIO, "onMicStateInfoNotify");
                    break;
                case YCMessage.MsgType.onAudioStreamVolume:
                    YCMessage.AudioVolumeInfo audioVolumeInfo = (YCMessage.AudioVolumeInfo) msg.obj;
                    printToLog(ColorTextView.AUDIO, "音频流音量，uid:" + audioVolumeInfo.uid + ",volume:" + audioVolumeInfo.volume);
                    break;
                case YCMessage.MsgType.onChannelAudioStateNotify:
                    YCMessage.ChannelAudioStateInfo channelAudioStateInfo = (YCMessage.ChannelAudioStateInfo) msg.obj;
                    String channelAudioStateInfoState = "";
                    if (channelAudioStateInfo.state == YCMessage.ChannelAudioStateInfo.NoAudio) {
                        channelAudioStateInfoState = "没有音频";
                    } else if (channelAudioStateInfo.state == YCMessage.ChannelAudioStateInfo.RecvAudio) {
                        channelAudioStateInfoState = "接收到音频";
                    } else if (channelAudioStateInfo.state == YCMessage.ChannelAudioStateInfo.RecvNoAudio) {
                        channelAudioStateInfoState = "接收不到音频";
                    }
                    printToLog(ColorTextView.AUDIO, "频道音频状态，sid:" + channelAudioStateInfo.sid + ",状态：" + channelAudioStateInfoState);
                    break;
                case YCMessage.MsgType.onPlayAudioStateNotify:
                    YCMessage.PlayAudioStateInfo playAudioStateInfo = (YCMessage.PlayAudioStateInfo) msg.obj;
                    printToLog(ColorTextView.AUDIO, "播放音频质量，speakerUid:" + playAudioStateInfo.speakerUid + ",播放帧数量：" + playAudioStateInfo.playFrameCount + ",服务器丢失帧数量：" + playAudioStateInfo.lossFrameCount + "服务器丢失帧数量：" + playAudioStateInfo.discardFrameCount + ",时间间隔：" + playAudioStateInfo.duration);

                    break;
                case YCMessage.MsgType.onAudioLinkStatics:
                    YCMessage.AudioLinkStatics audioLinkStatics = (YCMessage.AudioLinkStatics) msg.obj;
                    int rtt = audioLinkStatics.rtt;
                    int upSendNum = audioLinkStatics.upSendNum;
                    int upRecvNum = audioLinkStatics.upRecvNum;
                    int downSendNum = audioLinkStatics.downSendNum;
                    int downRecvNum = audioLinkStatics.downRecvNum;
                    int state2 = audioLinkStatics.state;
                    printToLog(ColorTextView.AUDIO, "rtt:" + rtt + ",本地发送语音:" + upSendNum + ",服务器接收语音包:" + upRecvNum + ",服务器发送语音包:" + downSendNum + ",本地接收语音包:" + downRecvNum + ",state:" + state2);
//                    printToLog("onAudioLinkStatics");
                    break;
                case YCMessage.MsgType.onAudioCaptureStatus:
                    printToLog(ColorTextView.AUDIO, "onAudioCaptureStatus");
                    break;
                case YCMessage.MsgType.onAudioRendStatus:
                    printToLog(ColorTextView.AUDIO, "onAudioRendStatus");
                    break;
                case YCMessage.MsgType.onAudioCaptureVolume:
                    printToLog(ColorTextView.AUDIO, "onAudioCaptureVolume");
                    break;
                case YCMessage.MsgType.onMediaInnerCommandNotify:
                    printToLog(ColorTextView.AUDIO, "onMediaInnerCommandNotify");
                    YCMessage.MediaInnerCommandInfo cmdInfo = (YCMessage.MediaInnerCommandInfo) msg.obj;
                    break;
                case YCMessage.MsgType.onMediaSdkReady:
                    YCMessage.MediaSdkReadyInfo mediaSdkReadyInfo = (YCMessage.MediaSdkReadyInfo) msg.obj;
                    String stateStr = mediaSdkReadyInfo.state == YCMessage.MediaSdkReadyInfo.Ready ? "MeidaSdk初始化成功" : "MeidaSdk初始化失败";
                    printToLog(ColorTextView.AUDIO, stateStr);
                    break;
                case YCMessage.MsgType.onChatTextNotify:
                    printToLog(ColorTextView.AUDIO, "onChatTextNotify");
                    YCMessage.ChatText sessionText = (YCMessage.ChatText) msg.obj;
                    LogUtils.d("onChatTextNotify msg:" + sessionText.text);
                    break;
                case YCMessage.MsgType.onAppUplinkFlowNotify:
                    YCMessage.AppUplinkFlowInfo appUplinkFlowInfo = (YCMessage.AppUplinkFlowInfo) msg.obj;
//                    appUplinkFlowInfo.flow
                    printToLog(ColorTextView.SDK, "音视频上行流量统计， appid:" + appUplinkFlowInfo.appid + ",uid:" + appUplinkFlowInfo.uid + ",flow:" + appUplinkFlowInfo.flow + ",nPublishRate:" + appUplinkFlowInfo.nPublishRate);
                    break;
                case YCMessage.MsgType.onAudioDiagnoseResInfo:
//                    YCMessage.AudioDiagnoseResInfo audioDiagnoseResInfo = (YCMessage.AudioDiagnoseResInfo) msg.obj;
                    printToLog(ColorTextView.AUDIO, "onAudioDiagnoseResInfo");
                    break;
                default:
                    LogUtils.d("can't handle the message:" + msg.what);
                    break;
            }
        }
    };

    private void handleAudioSpeaker(int state) {
        if (state == YCMessage.AudioSpeakerInfo.Start && !mIsAudioMute) {
            YCMedia.getInstance().requestMethod(new YCMediaRequest.YCMuteAudio(false));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        leave();
    }

    private void leave() {
        IProtoMgr.instance().removeHandlerWatcher(mSignalHandler);
        SDKEngine.logoutSignal();//信令登出
        YCMedia.getInstance().removeMsgHandler(mMediaHandler);
        YCMedia.getInstance().setCameraStatusListener(null);
        YCMedia.getInstance().requestMethod(new YCMediaRequest.YCCloseMic());
        YCMedia.getInstance().requestMethod(new YCMediaRequest.YCMuteAudio(false));
        YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStopCamera());
        YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStopPublishVideo());
        if (mChannelVideoController != null) {
            mChannelVideoController.destroy();
            mChannelVideoController = null;
        }
        YCMedia.getInstance().requestMethod(new YCMediaRequest.YCLogout());
        //mCameraPreview.removeAllViews();
    }
}

