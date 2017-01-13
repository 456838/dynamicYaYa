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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ycloud.live.YCConstant;
import com.ycloud.live.YCMedia;
import com.ycloud.live.YCMediaRequest;
import com.ycloud.live.YCMessage;
import com.ycloud.live.video.IVideoLiveCallback;
import com.ycloud.live.video.YCVideoPreview;
import com.ycloud.live.video.YCVideoViewLayout;
import com.ycloud.live.yyproto.ProtoEvent;
import com.ycsignal.base.YYHandler;
import com.ycsignal.outlet.IProtoMgr;
import com.ycsignal.outlet.SDKParam;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/11/11 15:17
 * Time: 15:17
 * Description:
 */
public class ChannelTestLivingRoomActivity extends FragmentActivity implements VideoFragment.FragmentListener {

    private ViewPager viewPager;
    private List<Fragment> fragmentList;
    private ChannelVideoLayoutController mChannelVideoController = null;
    private boolean mAudioLinkConnected = false;
    private boolean mVideoLinkConnected = false;
    private YCVideoPreview mVideoPreview = null;
    private int mUid = 0;
    private int mSid = 0;
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

    VideoFragment mVideoFragment;

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        fragmentList = new ArrayList<>();
        mVideoFragment = new VideoFragment();
        BusinessFragment businessFragment = new BusinessFragment();
        fragmentList.add(mVideoFragment);
        fragmentList.add(businessFragment);
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager(), fragmentList));
        viewPager.setCurrentItem(0);
        mViewLay = (YCVideoViewLayout) findViewById(R.id.yvLayout);
        mViewLay2 = (YCVideoViewLayout) findViewById(R.id.yvLayout2);
        mDoubleLayout = (DoubleLayout) findViewById(R.id.double_layout_video);

    }

    private void initData() {

        Intent intent = this.getIntent();
        mUid = intent.getIntExtra("uid", 456838);
        mSid = intent.getIntExtra("sid", 10086);
        //注册信令事件处理
        IProtoMgr.instance().addHandlerWatcher(mSignalHandler);
        //摄像头事件回调
        YCMedia.getInstance().setCameraStatusListener(new IVideoLiveCallback() {
            @Override
            public void onLiveLinkConnected(int i) {
                LogUtils.e("onLiveLinkConnected:" + i);
            }

            @Override
            public void onLiveLinkDisconnected(int i) {
                LogUtils.e("onLiveLinkConnected:" + i);
            }

            @Override
            public void onPreviewCreated(YCVideoPreview ycVideoPreview) {
                //摄像头产生预览
                LogUtils.e("onPreviewCreated:" + ycVideoPreview.getVideoEncodeSize());
            }

            @Override
            public void onPreviewStartSuccess() {
                LogUtils.e("onPreviewStartSuccess");
            }

            @Override
            public void onPreviewStartFailed() {
                LogUtils.e("onPreviewStartFailed");
            }

            @Override
            public void onPreviewStopped() {
                LogUtils.e("onPreviewStopped");
            }

            @Override
            public void onOpenCameraFailed(FailReason failReason, String s) {
                LogUtils.e("onOpenCameraFailed:" + failReason.name() + ";hit:" + s);
            }

            @Override
            public void onVideoRecordStarted() {
                LogUtils.e("onVideoRecordStarted");
            }

            @Override
            public void onVideoRecordStopped() {
                LogUtils.e("onVideoRecordStopped");
            }
        });
        //注册媒体事件处理
        YCMedia.getInstance().addMsgHandler(mMediaHandler);
        //媒体参数配置
        loadMediaConfig();
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
            //信令登录
            signalLogin(mUid, mSid);
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
        } else {
            Toast.makeText(getApplicationContext(), "登录Token为空", Toast.LENGTH_SHORT).show();
        }
    }


    public boolean mFirstClickPause = false;       //用于没有开播之前停止视频的播放
    public boolean mMediaPause = false;            //用于视频开播后停止视频的播放

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
                    ((ImageView) v).setImageResource(R.drawable.jc_click_pause_selector);
                } else {
                    mMediaPause = false;
                    mChannelVideoController.stopSubscribeVideo();
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
            return;
        }

        mWanIp = res.uClientIp;
        mWanIsp = res.uClientIsp;
        mAreaType = res.uClientAreaType;

        LogUtils.e("applogin successed, uid:" + res.uid + ",wanIp:" + res.uClientIp + ",mWanIsp:" + res.uClientIsp + ",mAreaType:" + res.uClientAreaType);
        SDKEngine.loginMedia(YConfig.mAppKey, mSid, mUid, mWanIp, mWanIsp, mAreaType, httpToken);
    }

    public void printToLog(String msg) {
        LogUtils.i(msg);
    }

    Handler mMediaHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case YCMessage.MsgType.onVideoLinkInfoNotity:
                    YCMessage.VideoLinkInfo videoLinkInfo = (YCMessage.VideoLinkInfo) msg.obj;
                    String baseInfo = String.format("appid:%d,ip:%s,port:$s,",videoLinkInfo.appId,videoLinkInfo.ip,videoLinkInfo.port);
                    if (videoLinkInfo.state == YCMessage.VideoLinkInfo.Connect) {
                        printToLog("视频连接中:"+baseInfo);
                    }else if(videoLinkInfo.state == YCMessage.VideoLinkInfo.Connected){
                        printToLog("视频已经连接中:"+baseInfo);
                    }else if(videoLinkInfo.state == YCMessage.VideoLinkInfo.Disconnected){
                        printToLog("视频断开连接:"+baseInfo);
                    }else if(videoLinkInfo.state == YCMessage.VideoLinkInfo.ServerReject){
                        printToLog("服务器拒绝视频连接:"+baseInfo);
                    }
                    //放在business里面
                    break;
                case YCMessage.MsgType.onVideoStreamInfoNotify:
                    YCMessage.VideoStreamInfo streamInfo = (YCMessage.VideoStreamInfo) msg.obj;
                    long userGroupId = streamInfo.userGroupId;
                    int publishId = streamInfo.publishId;
                    int audoSubscribe = streamInfo.audoSubscribe;
                    int state = streamInfo.state;
                    long streamId = streamInfo.streamId;
                    if(state== YCMessage.VideoStreamInfo.Arrive){
                        printToLog("视频流到来:"+String.format("userGroupId:%d,publishId:%d,audoSubscribe:%d,streamId:%d",userGroupId,publishId,audoSubscribe,streamId));
                    }else if(state== YCMessage.VideoStreamInfo.Start){
                        printToLog("视频流开始:"+String.format("userGroupId:%d,publishId:%d,audoSubscribe:%d,streamId:%d",userGroupId,publishId,audoSubscribe,streamId));
                    }else if(state== YCMessage.VideoStreamInfo.Stop){
                        printToLog("视频流停止:"+String.format("userGroupId:%d,publishId:%d,audoSubscribe:%d,streamId:%d",userGroupId,publishId,audoSubscribe,streamId));
                    }
                    mVideoFragment.setText(R.id.anchor, "" + streamInfo.publishId);
                    break;
                case YCMessage.MsgType.onVideoRenderInfoNotify:
                    YCMessage.VideoRenderInfo videoRenderInfo = (YCMessage.VideoRenderInfo) msg.obj;

                    if (videoRenderInfo.state == YCMessage.VideoRenderInfo.Start) {
                        printToLog("开始视频渲染");
                    } else {
                        printToLog("停止视频渲染");
                    }
                    break;
                case YCMessage.MsgType.onVideoDownlinkPlrNotify:
                    YCMessage.VideoDownlinkPlrInfo videoDownlinkPlrInfo = (YCMessage.VideoDownlinkPlrInfo) msg.obj;
                    break;
                case YCMessage.MsgType.onVideoliveBroadcastNotify:
                    break;
                case YCMessage.MsgType.onVideoCodeRateNotify:
                    break;
                case YCMessage.MsgType.onVideoCodeRateChange:
                    break;
                case YCMessage.MsgType.onVideoMetaInfoNotify:
                    break;
                case YCMessage.MsgType.onNoVideoNotify:
                    break;
                case YCMessage.MsgType.onVideoDecodeSlowNotify:
                    break;
                case YCMessage.MsgType.onVideoFrameLossNotify:
                    break;
                case YCMessage.MsgType.onVideoCodeRateLevelSuggest:
                    break;
                case YCMessage.MsgType.onVideoPublishStatus:
                    break;
                case YCMessage.MsgType.onVideoUplinkLossRateNotify:
                    break;
                case YCMessage.MsgType.onServerRecodRes:
                    break;
                case YCMessage.MsgType.onVideoViewerStatInfo:
                    break;
                case YCMessage.MsgType.onVideoPublisherStatInfo:
                    break;
                case YCMessage.MsgType.onVideoMetaDataInfo:
                    break;
                case YCMessage.MsgType.onAudioLinkInfoNotity:
                    YCMessage.AudioLinkInfo audioLinkInfo = (YCMessage.AudioLinkInfo) msg.obj;
                    LogUtils.d("onAudioLinkInfoNotity, state: " + audioLinkInfo.state);
                    break;
                case YCMessage.MsgType.onAudioSpeakerInfoNotity:
                    YCMessage.AudioSpeakerInfo speakerInfo = (YCMessage.AudioSpeakerInfo) msg.obj;
                    LogUtils.d("onAudioSpeakerInfoNotity, state: " + speakerInfo.state);
                    break;
                case YCMessage.MsgType.onMicStateInfoNotify:
                    YCMessage.MicStateInfo micStateInfo = (YCMessage.MicStateInfo) msg.obj;
                    LogUtils.d("onMicStateInfoNotify, state: " + micStateInfo.state);
                    break;
                case YCMessage.MsgType.onAudioStreamVolume:
                    break;
                case YCMessage.MsgType.onChannelAudioStateNotify:
                    break;
                case YCMessage.MsgType.onPlayAudioStateNotify:
                    break;
                case YCMessage.MsgType.onAudioLinkStatics:
                    break;
                case YCMessage.MsgType.onAudioCaptureStatus:
                    break;
                case YCMessage.MsgType.onAudioRendStatus:
                    break;
                case YCMessage.MsgType.onAudioCaptureVolume:
                    break;
                case YCMessage.MsgType.onMediaInnerCommandNotify:
                    YCMessage.MediaInnerCommandInfo cmdInfo = (YCMessage.MediaInnerCommandInfo) msg.obj;
                    break;
                case YCMessage.MsgType.onMediaSdkReady:
                    break;
                case YCMessage.MsgType.onChatTextNotify:
                    YCMessage.ChatText sessionText = (YCMessage.ChatText) msg.obj;
                    LogUtils.d("onChatTextNotify msg:" + sessionText.text);
                    break;
                case YCMessage.MsgType.onAppUplinkFlowNotify:
                    break;
                case YCMessage.MsgType.onAudioDiagnoseResInfo:
                    break;
                default:
                    LogUtils.d("can't handle the message:" + msg.what);
                    break;
            }
        }
    };

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

