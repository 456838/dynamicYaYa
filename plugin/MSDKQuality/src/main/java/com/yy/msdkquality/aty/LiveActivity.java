package com.yy.msdkquality.aty;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.ycloud.live.YCConstant;
import com.ycloud.live.YCMedia;
import com.ycloud.live.YCMediaRequest;
import com.ycloud.live.YCMessage;
import com.ycloud.live.YCMessage.AudioLinkInfo;
import com.ycloud.live.YCMessage.AudioSpeakerInfo;
import com.ycloud.live.YCMessage.MediaInnerCommandInfo;
import com.ycloud.live.YCMessage.VideoLinkInfo;
import com.ycloud.live.video.YCCameraStatusListener;
import com.ycloud.live.video.YCVideoPreview;
import com.ycloud.live.video.YCVideoViewLayout;
import com.ycloud.live.yyproto.ProtoEvent;
import com.ycloud.live.yyproto.ProtoEvent.ProtoEventBase;
import com.ycloud.live.yyproto.ProtoEvent.ProtoEvtLoginRes;
import com.ycloud.live.yyproto.ProtoReq;
import com.ycsignal.base.YYHandler;
import com.ycsignal.outlet.IProtoMgr;
import com.ycsignal.outlet.SDKParam;
import com.yy.msdkquality.ChannelVideoLayoutController;
import com.yy.msdkquality.DemoApplication;
import com.yy.msdkquality.R;
import com.yy.msdkquality.YConfig;
import com.yy.msdkquality.adapter.ChatAdpter;
import com.yy.msdkquality.bean.ChatEntity;
import com.yy.msdkquality.engine.TokenEngine;
import com.yy.msdkquality.widget.DoubleLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LiveActivity extends Activity implements YCCameraStatusListener {
    private String TAG = "LiveActivity";

    private boolean mAudioLinkConnected = false;
    private boolean mVideoLinkConnected = false;
    private YCVideoPreview mVideoPreview = null;
    private int mUid = 0;
    private int mSid = 0;
    private int mWanIp = 0;
    private int mWanIsp = 0;
    private int mAreaType = 0;
    private byte[] httpToken = null;

    private DemoApplication mApp = null;

    private ImageButton mBtnMicSwitch;
    private ImageButton mBtnAudioMute;
    private ImageButton mBtnVideoSwitch;
    private ImageButton mBtnCameraSwitch;
    private ImageButton mBtnLoudspeakerSwitch;
    private ListView mListViewChat;
    private EditText mEditText;
    private Button mBtnSend;
    private FrameLayout mCameraPreview;

    private int mCameraType = CameraInfo.CAMERA_FACING_FRONT;

    private boolean mIsOpenMic = false;
    private boolean mIsVideoPublished = false;
    private boolean mIsCameraStarted = false;
    private boolean mIsAudioMute = false;
    private boolean mIsLoudspeaker = true;
    private boolean mIsExternMic = true;

    private List<ChatEntity> mChatList = new ArrayList<ChatEntity>();
    private ChatAdpter mChatAdpter = new ChatAdpter(LiveActivity.this, mChatList);

    public static interface Operation {
        public static final int MSG_CAMERA_PREVIEW_CREATED = 1;
        public static final int MSG_CAMERA_PREVIEW_STOP = 2;
        public static final int MSG_GET_TOKEN_FAILED = 3;
        public static final int MSG_SEND_SINGAL_FAILED = 4;

    }

//	private YCVideoView mRemoteVideoView;
//	private YCVideoView mRemoteVideoView2;
//	private ChannelVideoLayoutController mChannelVideoController = null;

    private YCVideoViewLayout mViewLay;
    private YCVideoViewLayout mViewLay2;
    DoubleLayout dlo;
    private ChannelVideoLayoutController mChannelVideoController = null;

    Handler mTimerHandler = new Handler();
    Runnable mTimerRunnable;


    private YYHandler mSignalHandler = new YYHandler() {
        @MessageHandler(message = SDKParam.Message.messageId)
        public void onEvent(byte[] data) {
            ProtoEventBase base = new ProtoEventBase();
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

    Handler mMediaHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Operation.MSG_CAMERA_PREVIEW_CREATED:
                    handleCameraPreviewReady((YCVideoPreview) msg.obj);
                    break;

                case Operation.MSG_CAMERA_PREVIEW_STOP:
                    handlePreviewStoped();
                    break;

                case Operation.MSG_GET_TOKEN_FAILED: {
                    Toast.makeText(getApplicationContext(), "http get token failed", Toast.LENGTH_SHORT).show();
                }
                break;

                case Operation.MSG_SEND_SINGAL_FAILED: {
                    Toast.makeText(getApplicationContext(), "send login ap failed:", Toast.LENGTH_SHORT).show();
                }
                break;

                case YCMessage.MsgType.onVideoLinkInfoNotity:
                    VideoLinkInfo videoLinkInfo = (VideoLinkInfo) msg.obj;
                    Log.d(TAG, "onVideoLinkInfoNotity, state " + videoLinkInfo.state);
                    mVideoLinkConnected = videoLinkInfo.state == VideoLinkInfo.Connected ? true : false;
                    break;

                case YCMessage.MsgType.onVideoStreamInfoNotify:
//				YCMessage.VideoStreamInfo streamInfo = (YCMessage.VideoStreamInfo) msg.obj;
//				mChannelVideoController.onVideoStreamInfoNotify(streamInfo);
                    break;

                case YCMessage.MsgType.onAudioLinkInfoNotity:
                    AudioLinkInfo audioLinkInfo = (AudioLinkInfo) msg.obj;
                    Log.d(TAG, "onAudioLinkInfoNotity, state: " + audioLinkInfo.state);
                    mAudioLinkConnected = audioLinkInfo.state == AudioLinkInfo.Connected ? true : false;
                    break;

                case YCMessage.MsgType.onMicStateInfoNotify:
                    YCMessage.MicStateInfo micStateInfo = (YCMessage.MicStateInfo) msg.obj;
                    Log.d(TAG, "onMicStateInfoNotify, state: " + micStateInfo.state);
                    break;

                case YCMessage.MsgType.onAudioSpeakerInfoNotity:
                    AudioSpeakerInfo speakerInfo = (AudioSpeakerInfo) msg.obj;
                    Log.d(TAG, "onAudioSpeakerInfoNotity, state: " + speakerInfo.state);
                    handleAudioSpeaker(speakerInfo.state);
                    break;

                case YCMessage.MsgType.onChatTextNotify:
                    YCMessage.ChatText sessionText = (YCMessage.ChatText) msg.obj;
                    Log.d(TAG, "onChatTextNotify msg:" + sessionText.text);

                    ChatEntity chatEntity = new ChatEntity();
                    chatEntity.setContext(sessionText.text);
                    chatEntity.setSenderName(Integer.toString(sessionText.uid));
                    chatEntity.setType(1);
//                    chatEntity.mColor = sessionText.color;
//                    chatEntity.mNickName =;
//                    chatEntity.mText = sessionText.text;

                    mChatList.add(chatEntity);
                    mChatAdpter.notifyDataSetChanged();
                    break;
                case YCMessage.MsgType.onMediaInnerCommandNotify:
                    MediaInnerCommandInfo cmdInfo = (MediaInnerCommandInfo) msg.obj;
                    handleMediaInnerCmd(cmdInfo.command);
                    break;
                default:
                    //Log.e(TAG, "can't handle the message:" + msg.what);
                    break;
            }
        }
    };

    /**
     * 直播模式媒体参数配置, 必须要在login前调用
     */
    private void liveModelMediaConfig() {
        Log.d(TAG, "liveModelMediaConfig");
        Map<Integer, Integer> configs = new HashMap<Integer, Integer>();
        configs.put(YCConstant.ConfigKey.LOGIN_MODEL, YCConstant.LOGIN_MODEL_CHANNEL);
        //video setting
        configs.put(YCConstant.ConfigKey.VIDEO_RECORD_QUALITY, YCConstant.MEDIA_QUALITY_MEDIUM);
        configs.put(YCConstant.ConfigKey.VIDEO_MIN_BUFFER, 100);
        configs.put(YCConstant.ConfigKey.VIDEO_AUTO_SUBSCRIBE_STREAM, 1);    //auto subscribe stream
        //audio setting
        configs.put(YCConstant.ConfigKey.AUDIO_RECORD_QUALITY, YCConstant.MEDIA_QUALITY_HIGH);

        //upload bitrate control
        configs.put(YCConstant.ConfigKey.UPLOAD_MIN_CODERATE, 350);
        configs.put(YCConstant.ConfigKey.UPLOAD_MAX_CODERATE, 800);
        configs.put(YCConstant.ConfigKey.UPLOAD_CUR_CODERATE, 700);
        configs.put(YCConstant.ConfigKey.VIDEO_HARDWARE_DECODE, 1);
        configs.put(YCConstant.ConfigKey.UPLOAD_TOTAL_CODERATE, 1200);
        configs.put(YCConstant.ConfigKey.UPLOAD_USE_CRCONTROL, 1);

        YCMedia.getInstance().requestMethod(new YCMediaRequest.YCSetConfigs(YConfig.mAppKey, configs));
    }

    /**
     * 低延时模式媒体参数配置，此模式适用于1v1或者多人实时通话场景的应用, 必须要在login前调用
     */
    private void lowLatencyModelMediaConfig() {
        Log.d(TAG, "lowLatencyModelMediaConfig");
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        Log.d(TAG, "onCreate");

        CheckBox cb = (CheckBox) this.findViewById(R.id.checkBoxSpeaker);
        cb.setChecked(mIsExternMic);
        YCMedia.getInstance().setLoudspeakerStatus(mIsExternMic);
        cb.setTextColor(Color.LTGRAY);
        cb.setOnClickListener(new OnClickListener() {
            public void onClick(View checkBox) {
                CheckBox cb = (CheckBox) checkBox;
                mIsExternMic = !mIsExternMic;
                YCMedia.getInstance().setLoudspeakerStatus(mIsExternMic);
                cb.setChecked(mIsExternMic);
            }
        });

        initView();

        mApp = (DemoApplication) getApplication();

        //获取UI设置
        Intent intent = this.getIntent();
        mUid = intent.getIntExtra("uid", 0);
        mSid = intent.getIntExtra("sid", 0);
        boolean isLowLatencyModel = intent.getBooleanExtra("loginModel", false);

        //注册信令事件处理
        IProtoMgr.instance().addHandlerWatcher(mSignalHandler);

        //摄像头事件回调
        YCMedia.getInstance().setCameraStatusListener(this);
        //注册媒体事件处理
        YCMedia.getInstance().addMsgHandler(mMediaHandler);
        //媒体参数配置

        if (isLowLatencyModel) {
            lowLatencyModelMediaConfig();
        } else {
            liveModelMediaConfig();
        }

        //定时更新token
        mTimerRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    new Thread() {
                        @Override
                        public void run() {
                            updateToken();
                        }
                    }.start();

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.d(TAG, "update token exception " + e.getMessage());
                }
            }
        };
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (mChannelVideoController == null) {
//        	mChannelVideoController = new ChannelVideoController(mRemoteVideoView, mRemoteVideoView2);
            mChannelVideoController = new ChannelVideoLayoutController(mViewLay, mViewLay2, dlo);
            //信令登录
            signalLogin(mUid);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

    }

    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        leave();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initView() {
        mCameraPreview = (FrameLayout) findViewById(R.id.camera_preview);
        mBtnMicSwitch = (ImageButton) findViewById(R.id.btn_mic_mute2);
        mBtnAudioMute = (ImageButton) findViewById(R.id.btn_audio_mute2);
        mBtnVideoSwitch = (ImageButton) findViewById(R.id.btn_switch_video2);
        mBtnCameraSwitch = (ImageButton) findViewById(R.id.btn_camera_switch);
        mBtnLoudspeakerSwitch = (ImageButton) findViewById(R.id.btn_loudspeaker);

        mBtnMicSwitch.setImageDrawable(getResources().getDrawable(R.drawable.mic_disabled));
        mBtnAudioMute.setImageDrawable(getResources().getDrawable(R.drawable.audio_on));
        mBtnVideoSwitch.setImageDrawable(getResources().getDrawable(R.drawable.btn_video_off));
        mBtnLoudspeakerSwitch.setImageDrawable(getResources().getDrawable(R.drawable.ic_call_speaker_on));
//		mRemoteVideoView = (YCVideoView) findViewById(R.id.remote_view);
//		mRemoteVideoView2 =  (YCVideoView) findViewById(R.id.video_view_2);

        mListViewChat = (ListView) findViewById(R.id.listview_chat);
        mEditText = (EditText) findViewById(R.id.edittext_msg);
        mBtnSend = (Button) findViewById(R.id.btn_send);

        mViewLay = (YCVideoViewLayout) findViewById(R.id.yvLayout);
        mViewLay2 = (YCVideoViewLayout) findViewById(R.id.yvLayout2);
        dlo = (DoubleLayout) findViewById(R.id.double_layout_video);

        initButtonEvent();

        mListViewChat.setAdapter(mChatAdpter);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    private void initButtonEvent() {
        //发言
        mBtnMicSwitch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!mAudioLinkConnected) {
                    Toast.makeText(getApplicationContext(), R.string.err_no_server,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mIsOpenMic) {
                    YCMedia.getInstance().requestMethod(new YCMediaRequest.YCCloseMic());
                    mBtnMicSwitch.setImageDrawable(getResources().getDrawable(
                            R.drawable.mic_disabled));
                    mIsOpenMic = false;
                    Log.d(TAG, "close mic");

                } else {
                    YCMedia.getInstance().requestMethod(new YCMediaRequest.YCOpenMic());
                    mBtnMicSwitch.setImageDrawable(getResources().getDrawable(R.drawable.mic_enabled));
                    mIsOpenMic = true;
                    Log.d(TAG, "open mic");

                }

            }
        });

        //静音切换
        mBtnAudioMute.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!mAudioLinkConnected) {
                    Toast.makeText(getApplicationContext(), R.string.err_no_server,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mIsAudioMute) {
                    Log.d(TAG, "audio mute,  disable");
                    YCMedia.getInstance().requestMethod(new YCMediaRequest.YCMuteAudio(false));
                    mIsAudioMute = false;
                    mBtnAudioMute.setImageDrawable(getResources().getDrawable(R.drawable.audio_on));
                } else {
                    Log.d(TAG, "audio mute,  enable");
                    YCMedia.getInstance().requestMethod(new YCMediaRequest.YCMuteAudio(true));
                    mIsAudioMute = true;
                    mBtnAudioMute.setImageDrawable(getResources().getDrawable(R.drawable.audio_off));
                }
            }
        });

        //广播视频
        mBtnVideoSwitch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!mVideoLinkConnected) {
                    Toast.makeText(getApplicationContext(), R.string.err_no_server,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mIsCameraStarted) {
                    Log.d(TAG, "stop camera");

                    //停止服务器录制
                    YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStopServerRecord());
                    //停止视频流发布
                    YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStopPublishVideo());
                    //停止摄像头
                    YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStopCamera());
                    mBtnVideoSwitch.setImageDrawable(getResources().getDrawable(R.drawable.btn_video_off));
                    //mCameraPreview.removeAllViews();
                    //mCameraPreview.setVisibility(View.INVISIBLE);
                    mIsCameraStarted = false;
                    mIsVideoPublished = false;
                } else {
                    Log.d(TAG, "start camera");
                    //mCameraPreview.setVisibility(View.VISIBLE);

                    YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStartCamera(CameraInfo.CAMERA_FACING_BACK));
                    mBtnVideoSwitch.setImageDrawable(getResources().getDrawable(R.drawable.btn_video_on));
                    mIsCameraStarted = true;
                }

            }
        });

        //前后摄像头切换
        mBtnCameraSwitch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!mVideoLinkConnected) {
                    Toast.makeText(getApplicationContext(), R.string.err_no_server,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                mCameraType = mCameraType == CameraInfo.CAMERA_FACING_FRONT ? CameraInfo.CAMERA_FACING_BACK : CameraInfo.CAMERA_FACING_FRONT;
                YCMedia.getInstance().requestMethod(new YCMediaRequest.YCSwitchCamera(mCameraType));
            }
        });

        //扬声器/听筒切换
        mBtnLoudspeakerSwitch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (mIsLoudspeaker) {
                    YCMedia.getInstance().setLoudspeakerStatus(false);
                    mBtnLoudspeakerSwitch.setImageDrawable(getResources().getDrawable(R.drawable.ic_call_speaker_off));
                    mIsLoudspeaker = false;
                } else {
                    YCMedia.getInstance().setLoudspeakerStatus(true);
                    mBtnLoudspeakerSwitch.setImageDrawable(getResources().getDrawable(R.drawable.ic_call_speaker_on));
                    mIsLoudspeaker = true;
                }
            }
        });

        mBtnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String msg = mEditText.getText().toString();
                if (msg == null || msg.equals(""))
                    return;

                //发送文本消息
                YCMediaRequest.YCSendChatText sendText = new YCMediaRequest.YCSendChatText(msg, 0, 0);
                YCMedia.getInstance().requestMethod(sendText);

                //显示到本地列表
                ChatEntity chatEntity = new ChatEntity();
                chatEntity.setSenderName(mUid + "(自己)");
                chatEntity.setContext(msg);
                mChatList.add(chatEntity);
                mChatAdpter.notifyDataSetChanged();
                mEditText.setText("");
            }
        });
    }

    private void handleCameraPreviewReady(YCVideoPreview v) {

        Log.d(TAG, "preview update");

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
            Log.i(TAG, "start server record mode: " + mode + " businessId: " + businessId + " uidSetCount: " + recordUidSet.size());
            YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStartServerRecord(mode, businessId, recordUidSet));
            mIsVideoPublished = true;
        }
    }

    private void handlePreviewStoped() {
        Log.d(TAG, "handlePreviewStoped");
        mCameraPreview.removeAllViews();
    }

    private void handleAudioSpeaker(int state) {

        if (state == AudioSpeakerInfo.Start && !mIsAudioMute) {
            YCMedia.getInstance().requestMethod(new YCMediaRequest.YCMuteAudio(false));
        }
    }

    @Override
    public void onPreviewCreated(YCVideoPreview preview) {
        Log.d(TAG, "callback onPreviewCreated");
        Message msg = mMediaHandler.obtainMessage(Operation.MSG_CAMERA_PREVIEW_CREATED, preview);
        mMediaHandler.sendMessage(msg);
    }

    @Override
    public void onPreviewStartSuccess() {
        Log.d(TAG, "CameraActivity callback onPreviewStartSuccess");
    }

    @Override
    public void onPreviewStartFailed() {
        Log.d(TAG, "CameraActivity callback onPreviewStartFailed");
    }

    @Override
    public void onPreviewStopped() {
        Log.d(TAG, "CameraActivity callback onPreviewStopped");
        mVideoPreview = null;
        Message msg = mMediaHandler.obtainMessage();
        msg.what = Operation.MSG_CAMERA_PREVIEW_STOP;
        mMediaHandler.sendMessage(msg);
    }

    @Override
    public void onOpenCameraFailed(FailReason failReason, String reasonText) {
        Log.d(TAG, "CameraActivity callback onOpenCameraFailed");
    }

    @Override
    public void onVideoRecordStarted() {
        Log.d(TAG, "CameraActivity callback onVideoRecordStarted");
    }

    @Override
    public void onVideoRecordStopped() {
        Log.d(TAG, "CameraActivity callback onVideoRecordStopped");
    }

    private void handleMediaInnerCmd(int command) {

        //服务器强制命令
        if (command == MediaInnerCommandInfo.kCommandStopVideo) {
            if (mIsCameraStarted) {
                Log.d(TAG, "stop camera by command");

                //停止预览
                YCMedia.getInstance().requestMethod(new YCMediaRequest.YCStopCamera());

                mBtnVideoSwitch.setImageDrawable(getResources().getDrawable(R.drawable.btn_video_off));
                //mCameraPreview.removeAllViews();
                //mCameraPreview.setVisibility(View.INVISIBLE);
                mIsCameraStarted = false;
                mIsVideoPublished = false;
            }
        } else if (command == MediaInnerCommandInfo.kCommandStopAudio) {
            if (mIsOpenMic) {
                Log.d(TAG, "close mic by inner command.");
                mBtnMicSwitch.setImageDrawable(getResources().getDrawable(R.drawable.mic_disabled));
                mIsOpenMic = false;

            }
        }
    }

    /**
     * 登录结果事件通知 protoEvent.EventType.PROTO_EVENT_LOGIN_RES {@link #onLoginRes(byte[])}
     */
    private void signalLogin(final int uid) {
        new Thread() {
            @Override
            public void run() {
                //由于本demo获取token是通过http方式, 所以通过线程处理, YCLogin本身不需要单独线程执行
                httpToken = TokenEngine.genToken(uid, mSid, 0x3);
                if (null == httpToken) {
                    Message msg = mMediaHandler.obtainMessage();
                    msg.what = Operation.MSG_GET_TOKEN_FAILED;
                    mMediaHandler.sendMessage(msg);
                    return;
                }
                mTimerHandler.postDelayed(mTimerRunnable, (TokenEngine.TOKEN_EXPIRED_TIME - 5) * 1000);

                String tokenHex = "";
                for (int i = 0; i < httpToken.length; i++) {
                    tokenHex = String.format("%s%02x ", tokenHex, httpToken[i]);
                }

                ProtoReq.LoginByUidReq req = new ProtoReq.LoginByUidReq(uid, tokenHex);
                int ret = IProtoMgr.instance().sendRequest(req.getBytes());
                if (ret != SDKParam.RequestRes.REQ_SUCCESS) {
                    Message msg = mMediaHandler.obtainMessage();
                    msg.what = Operation.MSG_SEND_SINGAL_FAILED;
                    mMediaHandler.sendMessage(msg);
                    return;
                }
            }

        }.start();
    }

    private void signalLogout() {
        ProtoReq.LoginoutReq req = new ProtoReq.LoginoutReq();
        IProtoMgr.instance().sendRequest(req.getBytes());
    }

    private void loginMedia() {
        YCMedia.getInstance().requestMethod(new YCMediaRequest.YCLogin(YConfig.mAppKey, mSid, mUid, mWanIp, mWanIsp, mAreaType, httpToken));
    }

    private boolean updateToken() {
        byte[] token = TokenEngine.genToken(mUid, mSid, 3);
        if (token == null) {
            Log.d(TAG, "update token get failed!");
            Toast.makeText(getApplicationContext(), "get token failed",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        YCMedia.getInstance().requestMethod(new YCMediaRequest.YCUpdateToken(token));
        mTimerHandler.postDelayed(mTimerRunnable, (TokenEngine.TOKEN_EXPIRED_TIME - 5) * 1000);
        return true;
    }

    private void leave() {

        mTimerHandler.removeCallbacks(mTimerRunnable);
        IProtoMgr.instance().removeHandlerWatcher(mSignalHandler);
        signalLogout();//信令登出

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

    private void onLoginRes(byte[] data) {
        ProtoEvtLoginRes res = new ProtoEvtLoginRes();
        res.unmarshal(data);

        if (res.res != ProtoEvtLoginRes.LOGIN_SUCESS) {
            Log.i(TAG, "[applogin] login failed Res:" + res.res);

            signalLogout();
            Toast.makeText(getApplicationContext(), String.format("Signal Login failed %d", res.res),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mWanIp = res.uClientIp;
        mWanIsp = res.uClientIsp;
        mAreaType = res.uClientAreaType;

        Log.i(TAG, "[applogin] login successed, innerUid:" + res.uid + "wanIp:" + res.uClientIp + " mWanIsp:" + res.uClientIsp + " mAreaType:" + res.uClientAreaType);
        loginMedia();
    }

}
